package ble

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import ble.callback.IConnectCallback
import ble.callback.IDisConnectCallback
import ble.callback.ISearchCallback
import bluetooth.ClassicBluetoothClient
import com.inuker.bluetooth.library.BluetoothClient
import com.inuker.bluetooth.library.Code
import com.inuker.bluetooth.library.Constants
import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener
import com.inuker.bluetooth.library.connect.options.BleConnectOptions
import com.inuker.bluetooth.library.connect.response.BleConnectResponse
import com.inuker.bluetooth.library.model.BleGattProfile
import com.inuker.bluetooth.library.search.SearchRequest
import com.inuker.bluetooth.library.search.SearchResult
import com.inuker.bluetooth.library.search.response.SearchResponse
import java.util.*

class BleClient private constructor(context: Activity) : SearchResponse, BleConnectResponse {
    /* 获取当前状态 */
    // 当前的工作状态
    var workStatus = STATUS_NORMAL
        private set
    var context: Context

    /* 获取Ble工具类 */
    // 蓝牙核心工具类
    val client: BluetoothClient

    // 扫描结果过滤器 , 用于排重
    private var searchFilter: SearchFilter? = null

    // 扫描回调
    private var searchCallback: ISearchCallback? = null

    // 连接回调
    private var connectCallback: IConnectCallback? = null

    // 断开连接监听
    private var disConnectCallback: IDisConnectCallback? = null

    // 当前正在连接的设备
    private var stagingDevice: SimpleDevice? = null

    // 设备管理器
    private val connectDevices: HashMap<String, SimpleDevice?> = HashMap()
    // 单例 ------------------------------
    /**
     * 打开蓝牙
     */
    fun openBluetooth() {
        client.openBluetooth()
    }

    /**
     * 关闭蓝牙
     */
    fun closeBluetooth() {
        client.closeBluetooth()
    }

    /**
     * 开始扫描设备
     */
    fun searchDevice(callback: ISearchCallback?) {
        if (checkStatus()) {
            workStatus = STATUS_SEARCHING
            searchCallback = callback
            client.search(SearchRequest.Builder()
                    .searchBluetoothLeDevice(20 * 1000)
                    .build(), this)
        }
    }

    /**
     * 开始扫描设备
     */
    fun searchDevice(duration: Int, callback: ISearchCallback?) {
        if (checkStatus()) {
            workStatus = STATUS_SEARCHING
            searchCallback = callback
            client.search(SearchRequest.Builder()
                    .searchBluetoothLeDevice(duration)
                    .build(), this)
        }
    }

    /**
     * 开始扫描设备
     */
    fun searchClassicDevice(callback: ISearchCallback?) {
        if (checkStatus()) {
            workStatus = STATUS_SEARCHING
            searchCallback = callback
            client.search(SearchRequest.Builder()
                    .searchBluetoothClassicDevice(20 * 1000)
                    .build(), this)
        }
    }

    private fun checkStatus(): Boolean {
        return workStatus == STATUS_NORMAL
    }

    /**
     * 停止搜索
     */
    fun stopSearching() {
        client.stopSearch()
    }

    /**
     * 连接
     *
     * @param device
     * @param callback
     */
    fun connect(device: SimpleDevice, callback: IConnectCallback?) {
        Log.i("Simbluetoot", "开始连接 检查工具类状态" + checkStatus())
        if (checkStatus()) {
            workStatus = STATUS_CONNECTING
            connectCallback = callback
            stagingDevice = device
            val options = BleConnectOptions.Builder()
                    .setConnectRetry(1) // 连接如果失败重试1次
                    .setConnectTimeout(10000) // 连接超时6s
                    .setServiceDiscoverRetry(2) // 发现服务如果失败重试2次
                    .setServiceDiscoverTimeout(1000 * 5) // 发现服务超时5s
                    .build()
            callback?.onConnectStart(device)
            client.connect(device.mac, options, this)
        }
    }

    fun recordDevice(device: SimpleDevice?) {
        stagingDevice = device
    }

    fun popStagingDevice(): SimpleDevice? {
        return stagingDevice
    }

    /**
     * 断开连接
     *
     * @param device
     */
    fun disconnect(device: SimpleDevice?) {
        if (device == null) {
            return
        }
        if (device.isBLE) {
            client.disconnect(device.getMac())
        } else {
            classicClient!!.close()
        }
    }

    /**
     * 断开连接的回调
     */
    fun setConnectStatusListener(disConnectCallback: IDisConnectCallback?) {
        this.disConnectCallback = disConnectCallback
    }

    /**
     * 判断是否连接
     *
     * @param device
     * @return
     */
    fun isConnect(device: SimpleDevice?): Boolean {
        return if (device == null) {
            false
        } else connectDevices?.containsKey(device.getMac())
    }

    // 扫描回调 ----------------------------------------------
    override fun onSearchStarted() {
        searchFilter = SearchFilter()
        if (searchCallback != null) {
            searchCallback!!.onSearchStarted()
        }
    }

    override fun onDeviceFounded(device: SearchResult) {
        val d = searchFilter!!.getDevice(device)
        if (d != null) {
            if (searchCallback != null) {
                searchCallback!!.onDeviceFounded(d)
            }
        }
    }

    override fun onSearchStopped() {
        workStatus = STATUS_NORMAL
        searchFilter = null
        if (searchCallback != null) {
            searchCallback!!.onSearchStopped()
            searchCallback = null
        }
    }

    override fun onSearchCanceled() {
        workStatus = STATUS_NORMAL
        searchFilter = null
        if (searchCallback != null) {
            searchCallback!!.onSearchCanceled()
            searchCallback = null
        }
    }

    // 连接回调 ----------------------------------------------
    override fun onResponse(code: Int, data: BleGattProfile) {
        if (code == Code.REQUEST_SUCCESS) {
            stagingDevice!!.gattProfile = data
            client.registerConnectStatusListener(stagingDevice!!.getMac(), connectStatusListener)
            connectDevices[stagingDevice!!.getMac()] = stagingDevice
            if (connectCallback != null) {
                connectCallback!!.onConnectSuccess(stagingDevice)
            }
        } else {
            if (connectCallback != null) {
                connectCallback!!.onConnectFail(Code.toString(code))
            }
        }
        workStatus = STATUS_NORMAL
        connectCallback = null
    }

    // 连接状态回调 ----------------------------------------------
    var connectStatusListener: BleConnectStatusListener = object : BleConnectStatusListener() {
        override fun onConnectStatusChanged(mac: String, status: Int) {
            when (status) {
                Constants.STATUS_DISCONNECTED -> {
                    val device = connectDevices.remove(mac)
                    if (disConnectCallback != null) {
                        disConnectCallback!!.onDisconnect(device)
                    }
                }
            }
        }
    }
    val classicClient: ClassicBluetoothClient?
        get() {
            if (classicBluetoothClient != null) {
                return classicBluetoothClient
            }
            if (classicBluetoothClient == null) {
                synchronized(BleClient::class.java) {
                    if (classicBluetoothClient == null) {
                        classicBluetoothClient = ClassicBluetoothClient(context, client)
                    }
                }
            }
            return classicBluetoothClient
        }

    companion object {
        const val STATUS_NORMAL = 0
        const val STATUS_SEARCHING = 1
        const val STATUS_CONNECTING = 2
        private val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

        // 单例 ------------------------------
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: BleClient? = null

        @JvmStatic
        fun init(context: Activity) {
            if (instance != null) {
                return
            }
            if (instance == null) {
                synchronized(BleClient::class.java) {
                    if (instance == null) {
                        instance = BleClient(context)
                    }
                }
            }
        }

        @JvmStatic
        fun instance(): BleClient? {
            return instance
        }

        var classicBluetoothClient: ClassicBluetoothClient? = null
    }

    init {
        this.context = context
        client = BluetoothClient(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val i = context.checkSelfPermission(permissions[0])
            if (i != PackageManager.PERMISSION_GRANTED) {
                context.requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        12)
            }
        }
    }
}