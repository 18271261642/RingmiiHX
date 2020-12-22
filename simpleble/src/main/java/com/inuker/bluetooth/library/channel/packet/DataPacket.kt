package com.inuker.bluetooth.library.channel.packet

import com.inuker.bluetooth.library.utils.ByteUtils
import java.nio.ByteBuffer
import java.util.*

/**
 * Created by dingjikerbo on 17/4/16.
 */
class DataPacket(val seq: Int, val bytes: Bytes) : Packet() {
    // only last frame has crc
    lateinit var crc: ByteArray
        private set

    constructor(seq: Int, value: ByteArray?, start: Int, end: Int)
            : this(seq, Bytes(value!!, start, end))

    fun setLastFrame() {
        bytes.end -= 2
        crc = ByteUtils.get(bytes.value, bytes.end, 2)
    }

    override val name: String
        get() = DATA

    override fun toBytes(): ByteArray? {
        val buffer: ByteBuffer
        val packetSize: Int = bytes.dataLength + 2
        buffer = if (packetSize == BUFFER_SIZE) {
            Arrays.fill(BUFFER, 0.toByte())
            ByteBuffer.wrap(BUFFER)
        } else {
            ByteBuffer.allocate(packetSize)
        }
        buffer.putShort(seq.toShort())
        fillByteBuffer(buffer)
        return buffer.array()
    }

    fun fillByteBuffer(buffer: ByteBuffer) {
        buffer.put(bytes.value, bytes.start, bytes.dataLength)
    }

    override fun toString(): String {
        return "DataPacket{" +
                "seq=" + seq +
                ", size=" + bytes.dataLength +  //				", value=0x" + ByteUtils.byteToString(value) +
                '}'
    }
}