package ble;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import com.inuker.bluetooth.library1.old.BluetoothClient;
import com.inuker.bluetooth.library1.old.Code;
import com.inuker.bluetooth.library1.old.Constants;
import com.inuker.bluetooth.library1.old.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library1.old.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library1.old.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library1.old.model.BleGattProfile;
import com.inuker.bluetooth.library1.old.search.SearchRequest;
import com.inuker.bluetooth.library1.old.search.SearchResult;
import com.inuker.bluetooth.library1.old.search.response.SearchResponse;

import java.util.HashMap;

import ble.callback.IConnectCallback;
import ble.callback.IDisConnectCallback;
import ble.callback.ISearchCallback;
import bluetooth.ClassicBluetoothClient;

public class BleClient implements SearchResponse, BleConnectResponse {

    // 当前的工作状态
    private int STATUS = STATUS_NORMAL;
    public static final int STATUS_NORMAL = 0;
    public static final int STATUS_SEARCHING = 1;
    public static final int STATUS_CONNECTING = 2;

    Context context;
    // 蓝牙核心工具类
    private BluetoothClient client;
    // 扫描结果过滤器 , 用于排重
    private SearchFilter searchFilter;
    // 扫描回调
    private ISearchCallback searchCallback;
    // 连接回调
    private IConnectCallback connectCallback;
    // 断开连接监听
    private IDisConnectCallback disConnectCallback;
    // 当前正在连接的设备
    private SimpleDevice stagingDevice;
    // 设备管理器
    private HashMap<String, SimpleDevice> connectDevices;

    private static final String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};


    // 单例 ------------------------------
    private volatile static BleClient instance;

    private BleClient(Activity context) {
        this.context = context;
        client = new BluetoothClient(context);
        connectDevices = new HashMap<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int i = context.checkSelfPermission(permissions[0]);
            if (i != PackageManager.PERMISSION_GRANTED) {
                context.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 12);
            }
        }
    }

    public static void init(Activity context) {
        if (instance != null) {
            return;
        }
        if (instance == null) {
            synchronized (BleClient.class) {
                if (instance == null) {
                    instance = new BleClient(context);
                }
            }
        }
    }

    public static BleClient instance() {
        return instance;
    }
    // 单例 ------------------------------

    /**
     * 打开蓝牙
     */
    public void openBluetooth() {
        client.openBluetooth();
    }

    /**
     * 关闭蓝牙
     */
    public void closeBluetooth() {
        client.closeBluetooth();
    }

    /**
     * 开始扫描设备
     */
    public void searchDevice(ISearchCallback callback) {
        if (checkStatus()) {
            STATUS = STATUS_SEARCHING;
            searchCallback = callback;
            client.search(new SearchRequest.Builder()
                    .searchBluetoothLeDevice(20 * 1000)
                    .build(), this);
        }
    }

    /**
     * 开始扫描设备
     */
    public void searchDevice(int duration , ISearchCallback callback) {
        if (checkStatus()) {
            STATUS = STATUS_SEARCHING;
            searchCallback = callback;
            client.search(new SearchRequest.Builder()
                    .searchBluetoothLeDevice(duration)
                    .build(), this);
        }
    }

    /**
     * 开始扫描设备
     */
    public void searchClassicDevice(ISearchCallback callback) {
        if (checkStatus()) {
            STATUS = STATUS_SEARCHING;
            searchCallback = callback;
            client.search(new SearchRequest.Builder()
                    .searchBluetoothClassicDevice(20 * 1000)
                    .build(), this);
        }
    }

    private boolean checkStatus() {
        return STATUS == STATUS_NORMAL;
    }

    /**
     * 停止搜索
     */
    public void stopSearching() {
        client.stopSearch();
    }

    /**
     * 连接
     *
     * @param device
     * @param callback
     */
    public void connect(SimpleDevice device, IConnectCallback callback) {
        Log.i("Simbluetoot", "开始连接 检查工具类状态" + checkStatus());
        if (checkStatus()) {
            STATUS = STATUS_CONNECTING;
            connectCallback = callback;
            stagingDevice = device;
            BleConnectOptions options = new BleConnectOptions.Builder()
                    .setConnectRetry(1)   // 连接如果失败重试1次
                    .setConnectTimeout(10000)   // 连接超时6s
                    .setServiceDiscoverRetry(2)  // 发现服务如果失败重试2次
                    .setServiceDiscoverTimeout(1000 * 5)  // 发现服务超时5s
                    .build();
            if (callback != null) {
                callback.onConnectStart(device);
            }
            client.connect(device.mac, options, this);
        }


//        BluetoothDevice dev = device.deviceInfo.device;
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            dev.connectGatt(context, true, gattCal, BluetoothDevice.TRANSPORT_LE);
//        } else {
//            dev.connectGatt(context, false, gattCal);
//        }
    }

//    BluetoothGattCallback gattCal = new BluetoothGattCallback() {
//        @Override
//        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
//            super.onCharacteristicChanged(gatt, characteristic);
//            Log.i("WWWWWWE", "onCharacteristicChanged");
//        }
//
//        @Override
//        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
//            super.onConnectionStateChange(gatt, status, newState);
//            Log.i("WWWWWWE", "连接中 onConnectionStateChange =  " + newState);
//            if (newState == BluetoothProfile.STATE_CONNECTED) {
//                // 连接成功了
//                boolean b = gatt.discoverServices();
//                Log.i("WWWWWWE", "发现服务 , 开始发现服务 " + b);
//            }
//        }
//        @Override
//        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
//            super.onServicesDiscovered(gatt, status);
//            Log.i("WWWWWWE", "发现服务了");
//        }
//    };


    public void recordDevice(SimpleDevice device) {
        stagingDevice = device;
    }

    public SimpleDevice popStagingDevice() {
        return stagingDevice;
    }

    /**
     * 断开连接
     *
     * @param device
     */
    public void disconnect(SimpleDevice device) {
        if (device == null) {
            return;
        }
        if (device.isBLE) {
            client.disconnect(device.getMac());
        } else {
            getClassicClient().close();
        }
    }

    /**
     * 断开连接的回调
     */
    public void setConnectStatusListener(IDisConnectCallback disConnectCallback) {
        this.disConnectCallback = disConnectCallback;
    }

    /**
     * 判断是否连接
     * @param device
     * @return
     */
    public boolean isConnect(SimpleDevice device) {
        if (device == null) {
            return false;
        }
        return connectDevices.containsKey(device.getMac());
    }

    /* 获取Ble工具类 */
    public BluetoothClient getClient() {
        return client;
    }

    /* 获取当前状态 */
    public int getWorkStatus() {
        return STATUS;
    }

    // 扫描回调 ----------------------------------------------
    @Override
    public void onSearchStarted() {
        searchFilter = new SearchFilter();
        if (searchCallback != null) {
            searchCallback.onSearchStarted();
        }
    }

    @Override
    public void onDeviceFounded(SearchResult device) {
        SimpleDevice d = searchFilter.getDevice(device);
        if (d != null) {
            if (searchCallback != null) {
                searchCallback.onDeviceFounded(d);
            }
        }
    }

    @Override
    public void onSearchStopped() {
        STATUS = STATUS_NORMAL;
        searchFilter = null;
        if (searchCallback != null) {
            searchCallback.onSearchStopped();
            searchCallback = null;
        }
    }

    @Override
    public void onSearchCanceled() {
        STATUS = STATUS_NORMAL;
        searchFilter = null;
        if (searchCallback != null) {
            searchCallback.onSearchCanceled();
            searchCallback = null;
        }
    }

    // 连接回调 ----------------------------------------------

    @Override
    public void onResponse(int code, BleGattProfile data) {
        if (code == Code.REQUEST_SUCCESS) {
            stagingDevice.gattProfile = data;
            client.registerConnectStatusListener(stagingDevice.getMac(), connectStatusListener);
            connectDevices.put(stagingDevice.getMac(), stagingDevice);
            if (connectCallback != null) {
                connectCallback.onConnectSuccess(stagingDevice);
            }
        } else {
            if (connectCallback != null) {
                connectCallback.onConnectFail(Code.toString(code));
            }
        }
        STATUS = STATUS_NORMAL;
        connectCallback = null;
    }

    // 连接状态回调 ----------------------------------------------

    BleConnectStatusListener connectStatusListener = new BleConnectStatusListener() {
        @Override
        public void onConnectStatusChanged(String mac, int status) {
            switch (status) {
                case Constants.STATUS_DISCONNECTED:
                    SimpleDevice device = connectDevices.remove(mac);
                    if (disConnectCallback != null) {
                        disConnectCallback.onDisconnect(device);
                    }
                    break;
            }
        }
    };

    static ClassicBluetoothClient classicBluetoothClient;
    public ClassicBluetoothClient getClassicClient() {
        if (classicBluetoothClient != null) {
            return classicBluetoothClient;
        }
        if (classicBluetoothClient == null) {
            synchronized (BleClient.class) {
                if (classicBluetoothClient == null) {
                    classicBluetoothClient = new ClassicBluetoothClient(client);
                }
            }
        }
        return classicBluetoothClient;
    }
}
