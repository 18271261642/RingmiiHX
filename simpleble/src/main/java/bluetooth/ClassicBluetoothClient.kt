package bluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import ble.BleClient.Companion.instance
import ble.SimpleDevice
import ble.callback.IConnectCallback
import ble.callback.IDisConnectCallback
import com.inuker.bluetooth.library.BluetoothClient
import com.inuker.bluetooth.library.R
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.util.*

class ClassicBluetoothClient(private val mContext: Context, // 连接工具类
                             var client: BluetoothClient) {
    // 断开连接监听
    private val disConnectCallback: IDisConnectCallback? = null
    var socket: BluetoothSocket? = null
        private set
    private var `is`: DataInputStream? = null
    private var os: DataOutputStream? = null
    var isConnecting = false
    private fun getString(id: Int): String {
        return mContext.resources.getString(id)
    }

    @Synchronized
    fun connect(device: SimpleDevice, callback: IConnectCallback?) {
        if (isConnecting || socket != null) {
            callback?.onConnectFail(getString(R.string.simpleble_bt_connectting))
            return
        }
        Thread(object : Runnable {
            override fun run() {
                isConnecting = true
                instance()!!.recordDevice(device)
                callback?.onConnectStart(device)
                try {
                    val d = device.deviceInfo.device
                    socket = d.createRfcommSocketToServiceRecord(uuid)
                    socket!!.connect()
                    if (callback != null) {
                        if (socket!!.outputStream != null && socket!!.inputStream != null) {
                            callback.onConnectSuccess(device)
                        } else {
                            if (socket != null) {
                                socket!!.close()
                            }
                            callback.onConnectFail(getString(R.string.simpleble_bt_conn_error)) // "蓝牙连接失败");
                        }
                    }
                } catch (e: IOException) {
                    try {
                        socket = device.javaClass.getMethod("createRfcommSocket",
                                Int::class.javaPrimitiveType).invoke(device, 1) as BluetoothSocket
                        socket!!.connect()
                    } catch (e1: Exception) {
                        e1.printStackTrace()
                        callback!!.onConnectFail(getString(R.string.simpleble_bt_conn_error)) // "蓝牙连接失败");
                    }
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                    callback!!.onConnectFail(getString(R.string.simpleble_bt_conn_error)) // "蓝牙连接失败");
                } finally {
                    isConnecting = false
                }
            }
        }).start()
    }

    fun write(bytes: ByteArray?) {
        try {
            if (os == null && socket != null) {
                os = DataOutputStream(socket!!.outputStream)
            }
            if (os != null) {
                os!!.write(bytes)
                os!!.flush()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun read(): ByteArray? {
        try {
            if (`is` == null && socket != null) {
                `is` = DataInputStream(socket!!.inputStream)
            }
            if (`is` != null) {
                val length = `is`!!.available()
                if (length <= 0) return null
                val bytes = ByteArray(length)
                `is`!!.read(bytes)
                return bytes
            }
            return ByteArray(0)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    fun close() {
        try {
            if (socket != null) {
                if (`is` != null) {
                    `is`!!.close()
                }
                if (os != null) {
                    os!!.close()
                }
                socket!!.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            `is` = null
            os = null
            socket = null
            isConnecting = false
        }
    }

    companion object {
        // 蓝牙2.0通用UUID
        private val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

        @JvmStatic
        fun boud(context: Context?, device: BluetoothDevice) {
            try {
                ClsUtils.createBond(device.javaClass, device)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        @JvmStatic
        fun boudOfPin(context: Context, device: BluetoothDevice, pin: String?) {
            val intentFilter = IntentFilter()
            intentFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST)
            // 注册广播接收器，接收并处理搜索结果
            context.registerReceiver(object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    val action = intent.action
                    if (BluetoothDevice.ACTION_PAIRING_REQUEST == action) {
                        try {
                            //如果不结束广播接收，配对界面会闪出
                            abortBroadcast()
                            //顺序一定要这样，否则会出问题
                            pin?.let {
                                ClsUtils.setPin(device.javaClass, device, it)
                            }
                            //这行代码会在控制台报错
                            //ClsUtils.setPairingConfirmation(bluetoothDevice.getClass(), bluetoothDevice,true);
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }, intentFilter)
            try {
                ClsUtils.createBond(device.javaClass, device)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}