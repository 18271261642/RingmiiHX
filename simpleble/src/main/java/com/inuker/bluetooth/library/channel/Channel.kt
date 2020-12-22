package com.inuker.bluetooth.library.channel

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.SparseArray
import com.inuker.bluetooth.library.BluetoothContext
import com.inuker.bluetooth.library.channel.IChannelStateHandler
import com.inuker.bluetooth.library.channel.Timer.TimerCallback
import com.inuker.bluetooth.library.channel.packet.ACKPacket
import com.inuker.bluetooth.library.channel.packet.CTRPacket
import com.inuker.bluetooth.library.channel.packet.DataPacket
import com.inuker.bluetooth.library.channel.packet.Packet
import com.inuker.bluetooth.library.utils.BluetoothLog
import com.inuker.bluetooth.library.utils.ByteUtils
import com.inuker.bluetooth.library.utils.proxy.ProxyBulk
import com.inuker.bluetooth.library.utils.proxy.ProxyInterceptor
import com.inuker.bluetooth.library.utils.proxy.ProxyUtils
import java.lang.reflect.Method
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.TimeoutException

/**
 * Created by dingjikerbo on 17/4/14.
 */
/**
 * 要保证所有操作都在同一个线程
 */
abstract class Channel : IChannel, ProxyInterceptor {
    private var mCurrentState = ChannelState.IDLE
    private var mBytesToWrite: ByteArray? = null

    /**
     * 收到的包
     */
    private val mPacketRecv: SparseArray<Packet?> = SparseArray()
    private var mCurrentSync = 0

    /**
     * 发端要记录总字节数
     */
    private var mTotalBytes = 0

    /**
     * 收端要记录总帧数
     */
    private var mFrameCount = 0
    private var mChannelCallback: ChannelCallback? = null
    private val mWorkerHandler: Handler
    private val mChannel: IChannel
    private var mLastSync = 0

    override fun onRead(bytes: ByteArray?) {
        bytes?.let {
            mChannel.onRead(it)
        }
    }

    override fun send(value: ByteArray?, callback: ChannelCallback?) {
        if (value == null || callback == null) return
        BluetoothLog.e(String.format(">>> send %s", String(value)))
        mChannel.send(value, callback)
    }

    private val mSyncPacketHandler = IChannelStateHandler { args ->
        assertRuntime(false)
        val dataPacket = args[0] as DataPacket
        if (dataPacket.seq != mCurrentSync) {
            BluetoothLog.w(String.format("sync packet not matched!!"))
            return@IChannelStateHandler
        }
        if (!onDataPacketRecvd(dataPacket)) {
            BluetoothLog.w(String.format("sync packet repeated!!"))
            return@IChannelStateHandler
        }
        mLastSync = mCurrentSync
        mCurrentSync = 0
        startSyncPacket()
    }

    /**
     * 收到数据包的回调
     */
    private val mRecvDataHandler = IChannelStateHandler { args ->
        assertRuntime(false)
        val dataPacket = args[0] as DataPacket
        if (!onDataPacketRecvd(dataPacket)) {
            BluetoothLog.w(String.format("dataPacket repeated!!"))
            return@IChannelStateHandler
        }
        if (dataPacket.seq == mFrameCount) {
            // 如果最后一帧收到了，说明对端发送完毕了
            startSyncPacket()
        } else {
            startTimer(TIMEOUT, object : TimerCallback("WaitData") {
                override fun onTimerCallback() {
                    startSyncPacket()
                }
            })
        }
    }

    /**
     * 收到流控包的回调
     */
    private val mRecvCTRHandler = IChannelStateHandler { args ->
        assertRuntime(false)
        val ctrPacket = args[0] as CTRPacket
        mFrameCount = ctrPacket.frameCount
        val ackPacket = ACKPacket(ACKPacket.READY)
        setCurrentState(ChannelState.READY)
        performWrite(ackPacket) { code ->
            assertRuntime(false)
            if (code == Code.SUCCESS) {
                setCurrentState(ChannelState.READING)
                startTimer()
            } else {
                resetChannelStatus()
            }
        }
    }
    private val mWaitStartACKHandler = IChannelStateHandler {
        assertRuntime(false)
        setCurrentState(ChannelState.WAIT_START_ACK)
        startTimer()
    }
    private val mTimeoutHandler: TimerCallback = object : TimerCallback(javaClass.simpleName) {
        override fun onTimerCallback() {
            assertRuntime(false)
            onSendCallback(Code.TIMEOUT)
            resetChannelStatus()
        }
    }

    /**
     * 收到ACK包的回调
     */
    private val mRecvACKHandler = IChannelStateHandler { args ->
        assertRuntime(false)
        val ackPacket = args[0] as ACKPacket
        when (ackPacket.status) {
            ACKPacket.READY -> {
                stopTimer()
                setCurrentState(ChannelState.WRITING)
                sendDataPacket(0, true)
            }
            ACKPacket.SYNC -> {
                val index = ackPacket.seq
                if (index >= 1 && index <= mFrameCount) {
                    sendDataPacket(index - 1, false)
                    startTimer()
                }
            }
            ACKPacket.SUCCESS -> {
                onSendCallback(Code.SUCCESS)
                resetChannelStatus()
            }
            else -> {
                onSendCallback(Code.FAIL)
                resetChannelStatus()
            }
        }
    }
    private val STATE_MACHINE = arrayOf(
            ChannelStateBlock(ChannelState.READY, ChannelEvent.SEND_CTR, mWaitStartACKHandler),
            ChannelStateBlock(ChannelState.WAIT_START_ACK, ChannelEvent.RECV_ACK, mRecvACKHandler),
            ChannelStateBlock(ChannelState.SYNC, ChannelEvent.RECV_ACK, mRecvACKHandler),
            ChannelStateBlock(ChannelState.IDLE, ChannelEvent.RECV_CTR, mRecvCTRHandler),
            ChannelStateBlock(ChannelState.READING, ChannelEvent.RECV_DATA, mRecvDataHandler),
            ChannelStateBlock(ChannelState.SYNC_ACK, ChannelEvent.RECV_DATA, mSyncPacketHandler))

    /**
     * 这个函数主要是为了记录写出去的所有包
     * 执行写要放在UI线程
     */
    private fun performWrite(packet: Packet, callback: ChannelCallback?) {
        assertRuntime(false)
        if (callback == null) {
            throw NullPointerException("callback can't be null")
        }

        // 此处为防止底层写没回调，故抛异常提示
        if (!isTimerOn) {
            startExceptionTimer()
        }
        val bytes = packet.toBytes()
        BluetoothLog.w(String.format("%s: %s", logTag, packet))
        BluetoothContext.post { write(bytes, WriteCallback(callback)) }
    }

    private inner class WriteCallback internal constructor(var callback: ChannelCallback) : ChannelCallback {
        override fun onCallback(code: Int) {
            if (isExceptionTimerOn) {
                stopTimer()
            }
            mWorkerHandler.obtainMessage(MSG_WRITE_CALLBACK, code, 0, callback).sendToTarget()
        }
    }

    private fun sendStartFlowPacket() {
        assertRuntime(false)
        val flowPacket = CTRPacket(mFrameCount)
        performWrite(flowPacket) { code ->
            assertRuntime(false)
            if (code == Code.SUCCESS) {
                onPostState(ChannelEvent.SEND_CTR)
            } else {
                onSendCallback(Code.FAIL)
                resetChannelStatus()
            }
        }
    }

    private fun onSendCallback(code: Int) {
        assertRuntime(false)
        BluetoothLog.v(String.format("%s: code = %d", logTag, code))
        if (mChannelCallback != null) {
            mChannelCallback!!.onCallback(code)
        }
    }

    private fun onDataPacketRecvd(packet: DataPacket): Boolean {
        assertRuntime(false)

        // 如果对端包发重复了，则直接忽略
        if (mPacketRecv[packet.seq] != null) {
            return false
        }
        if (packet.seq == mFrameCount) {
            packet.setLastFrame()
        }
        mPacketRecv.put(packet.seq, packet)
        mTotalBytes += packet.bytes.dataLength
        stopTimer()
        return true
    }

    /**
     * 认为对端发送完毕了，可以开始同步了
     */
    private fun startSyncPacket() {
        assertRuntime(false)
        BluetoothLog.v(logTag)
        startTimer()
        setCurrentState(ChannelState.SYNC)
        if (!syncLostPacket()) {
            // 所有包都同步完了
            val bytes = totalRecvdBytes
            if (!ByteUtils.isEmpty(bytes)) {
                val ackPacket = ACKPacket(ACKPacket.SUCCESS)
                performWrite(ackPacket) { code ->
                    assertRuntime(false)
                    resetChannelStatus()
                    if (code == Code.SUCCESS) {
                        dispatchOnReceive(bytes)
                    }
                }
            } else {
                resetChannelStatus()
            }
        } else {
            // 什么都不做
        }
    }

    private fun dispatchOnReceive(bytes: ByteArray) {
        BluetoothLog.e(String.format(">>> receive: %s", String(bytes)))
        BluetoothContext.post(RecvCallback(bytes))
    }

    private inner class RecvCallback internal constructor(private val bytes: ByteArray) : Runnable {
        override fun run() {
            onRecv(bytes)
        }
    }

    private val totalRecvdBytes: ByteArray
        private get() {
            assertRuntime(false)
            check(mPacketRecv.size() == mFrameCount)
            BluetoothLog.v(String.format("%s: totalBytes = %d", logTag, mTotalBytes))
            val buffer = ByteBuffer.allocate(mTotalBytes)
            for (i in 1..mFrameCount) {
                val packet = mPacketRecv[i] as DataPacket?
                packet!!.fillByteBuffer(buffer)
                if (i == mFrameCount) {
                    if (!checkCRC(buffer.array(), packet.crc)) {
                        BluetoothLog.e(String.format("check crc failed!!"))
                        return ByteUtils.EMPTY_BYTES
                    }
                }
            }
            return buffer.array()
        }

    private fun syncLostPacket(): Boolean {
        assertRuntime(false)
        BluetoothLog.v(logTag)
        var i: Int
        i = mLastSync + 1
        while (i <= mFrameCount) {
            if (mPacketRecv[i] == null) {
                break
            }
            i++
        }
        if (i <= mFrameCount) {
            mCurrentSync = i
            val ackPacket = ACKPacket(ACKPacket.SYNC, i)
            performWrite(ackPacket) { code ->
                assertRuntime(false)
                if (code == Code.SUCCESS) {
                    setCurrentState(ChannelState.SYNC_ACK)
                    startTimer()
                } else {
                    resetChannelStatus()
                }
            }
            return true
        }
        return false
    }

    private fun resetChannelStatus() {
        assertRuntime(false)
        BluetoothLog.v(logTag)
        stopTimer()
        setCurrentState(ChannelState.IDLE)
        mBytesToWrite = null
        mFrameCount = 0
        mChannelCallback = null
        mPacketRecv.clear()
        mCurrentSync = 0
        mLastSync = 0
        mTotalBytes = 0
    }

    /**
     * @param index  包的索引，从0开始
     * @param looped 是否要循环发送下一个包
     */
    private fun sendDataPacket(index: Int, looped: Boolean) {
        assertRuntime(false)
        if (index >= mFrameCount) {
            BluetoothLog.v(String.format("%s: all packets sended!!", logTag))
            setCurrentState(ChannelState.SYNC)
            startTimer(TIMEOUT * 3)
            return
        }
        BluetoothLog.v(String.format("%s: index = %d, looped = %b", logTag, index + 1, looped))
        val start = index * 18
        val end = Math.min(mBytesToWrite!!.size, (index + 1) * 18) // 开区间
        val dataPacket = DataPacket(index + 1, mBytesToWrite, start, end)
        performWrite(dataPacket) { code ->
            assertRuntime(false)
            if (code != Code.SUCCESS) {
                BluetoothLog.w(String.format(">>> packet %d write failed", index))
            }
            if (looped) {
                sendDataPacket(index + 1, looped)
            }
        }
    }

    private fun setCurrentState(state: ChannelState) {
        assertRuntime(false)
        BluetoothLog.v(String.format("%s: state = %s", logTag, state))
        mCurrentState = state
    }

    private fun onPostState(event: ChannelEvent, vararg args: Any) {
        assertRuntime(false)
        BluetoothLog.v(String.format("%s: state = %s, event = %s",
                logTag, mCurrentState, event))
        for (block in STATE_MACHINE) {
            if (block.state == mCurrentState && block.event == event) {
                block.handler.handleState(*args)
                break
            }
        }
    }

    private fun assertRuntime(sync: Boolean) {
        val target = if (sync) Looper.getMainLooper() else mWorkerHandler.looper
        if (Looper.myLooper() != target) {
            throw RuntimeException()
        }
    }

    private fun performOnRead(bytes: ByteArray) {
        assertRuntime(false)
        val packet = Packet.getPacket(bytes)
        BluetoothLog.w(String.format("%s: %s", logTag, packet))
        when (packet.name) {
            Packet.ACK -> onPostState(ChannelEvent.RECV_ACK, packet)
            Packet.DATA -> onPostState(ChannelEvent.RECV_DATA, packet)
            Packet.CTR -> onPostState(ChannelEvent.RECV_CTR, packet)
            else -> {
            }
        }
    }

    private val mChannelImpl: IChannel = object : IChannel {

        override fun write(bytes: ByteArray?, callback: ChannelCallback?) {
            throw UnsupportedOperationException()
        }

        override fun onRead(bytes: ByteArray?) {
            bytes?.let {
                performOnRead(it)
            }
        }

        override fun onRecv(bytes: ByteArray?) {
            throw UnsupportedOperationException()
        }

        override fun send(value: ByteArray?, callback: ChannelCallback?) {
            if (value == null || callback == null) return
            performSend(value, callback)
        }
    }

    private fun performSend(value: ByteArray, callback: ChannelCallback) {
        assertRuntime(false)
        if (mCurrentState != ChannelState.IDLE) {
            callback.onCallback(Code.BUSY)
            return
        }
        mCurrentState = ChannelState.READY
        mChannelCallback = ProxyUtils.getUIProxy(callback)
        mTotalBytes = value.size
        mFrameCount = getFrameCount(mTotalBytes)
        BluetoothLog.v(String.format("%s: totalBytes = %d, frameCount = %d",
                logTag, mTotalBytes, mFrameCount))
        mBytesToWrite = Arrays.copyOf(value, value.size + 2)
        val crc = CRC16.get(value)
        System.arraycopy(crc, 0, mBytesToWrite, value.size, 2)
        sendStartFlowPacket()
    }

    override fun onIntercept(`object`: Any, method: Method, args: Array<Any>): Boolean {
        mWorkerHandler.obtainMessage(0, ProxyBulk(`object`, method, args)).sendToTarget()
        return true
    }

    private val mCallback = Handler.Callback { msg ->
        when (msg.what) {
            MSG_WRITE_CALLBACK -> {
                val callback = msg.obj as ChannelCallback
                callback.onCallback(msg.arg1)
            }
            else -> ProxyBulk.safeInvoke(msg.obj)
        }
        false
    }
    private val logTag: String
        private get() = String.format("%s.%s", javaClass.simpleName,
                BluetoothContext.getCurrentMethodName())

    /**
     * 末尾追加两个字节的crc，每包发18个字节
     *
     * @return 分包数
     */
    private fun getFrameCount(totalBytes: Int): Int {
        val total = totalBytes + 2
        return 1 + (total - 1) / 18
    }

    private fun startExceptionTimer() {
        startTimer(TIMEOUT, object : TimerCallback(TIMER_EXCEPTION) {
            @Throws(TimeoutException::class)
            override fun onTimerCallback() {
                throw TimeoutException()
            }
        })
    }

    private fun startTimer(duration: Long = TIMEOUT, callback: TimerCallback = mTimeoutHandler) {
        BluetoothLog.v(String.format("%s: duration = %d", logTag, duration))
        Timer.start(callback, duration)
    }

    private fun stopTimer() {
        BluetoothLog.v(logTag)
        Timer.stop()
    }

    private val isTimerOn: Boolean
        private get() = Timer.isRunning()
    private val isExceptionTimerOn: Boolean
        private get() = TIMER_EXCEPTION == Timer.getName()

    private fun checkCRC(bytes: ByteArray, crc0: ByteArray): Boolean {
        return ByteUtils.equals(crc0, CRC16.get(bytes))
    }

    companion object {
        private const val TIMEOUT: Long = 5000
        private const val MSG_WRITE_CALLBACK = 1
        private const val TIMER_EXCEPTION = "exception"
    }

    init {
        mChannel = ProxyUtils.getProxy(mChannelImpl, this)
        val thread = HandlerThread(javaClass.simpleName)
        thread.start()
        mWorkerHandler = Handler(thread.looper, mCallback)
    }
}