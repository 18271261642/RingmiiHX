package com.guider.health.bluetooth.core;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by haix on 2019/6/6.
 * <p>
 * BLE的特征一次读写最大长度20字节
 * Android 作为中心设备，最多只能同时连接 6 个 BLE 外围设备
 * todo 这个单例一次只能和一个设备对接, 再次连接时, 会先关闭上次扫描和连接
 */

public class BleBluetooth {


    BluetoothAdapter bluetoothAdapter;
    public BluetoothGatt mBluetoothGatt;
    Context mContext;
    private boolean isConnected = false;

    private final static BleBluetooth bleBluetooth = new BleBluetooth();


    public static BleBluetooth getInstance() {
        return bleBluetooth;
    }


    /**
     * 用户打开蓝牙后，显示已绑定的设备列表
     */
    public void getBondDevices(List deviceList) {
        deviceList.clear();
        Set<BluetoothDevice> tmp = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice d : tmp) {
            deviceList.add(d);
        }
    }


    public void openBluetooth() {
        // 蓝牙未打开，询问打开
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }
    }

    public void closeBluetooth(){
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.disable();
        }
    }


    private static final String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

    /**
     * 进行初始化
     *
     * @param context
     * @return
     */
    public BleBluetooth init(Activity context) {

        if (mContext == null) {
            mContext = context.getApplicationContext();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                int i = context.checkSelfPermission(permissions[0]);
                if (i != PackageManager.PERMISSION_GRANTED) {
                    context.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Params.MY_PERMISSION_REQUEST_CONSTANT);
                }
            }

            BluetoothManager mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);

            bluetoothAdapter = mBluetoothManager.getAdapter();
            if (null == bluetoothAdapter) {
                Log.i("haix", "蓝牙初始化失败! ");
            }

        }

        return this;

    }


    //所有的蓝牙操作使用 Handler 固定在一条线程操作
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    //默认扫描时间：5s
    private static final int SCAN_TIME = 5000;
    private ScanCallback mScanCallBack;

    /**
     * 扫描设备   放在子线程中执行
     * 当传入的time值为0以下时默认扫描时间为5秒
     */
    public void scanBle(int time, ScanCallback scanCallback) {

        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }

        //如果正在扫描
        if (mScanCallBack != null) {
            //断开连接
            nowStopScan();
            try {
                //2秒后才能再次扫描
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //断开扫描
        disConnection();

        Log.i("haix", "开始扫描...");

        mScanCallBack = scanCallback;

        mHandler.postDelayed(runnable, time == 0 ? SCAN_TIME : time);

//        startLeScan(uuid[] ,mLeScanCallback);
        //目前这种方式是最稳定的, 就是耗电,  新api bluetoothAdapter.getBluetoothLeScanncer().startLeScan不是很稳定
        bluetoothAdapter.startLeScan(leScanCallback);

    }

    private final BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {

            if (mScanCallBack != null) {
                mScanCallBack.scanBle(device, rssi, scanRecord);
            }
        }
    };

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            scanFailed();
        }
    };

    public void scanFailed(){
        if (mScanCallBack != null) {
            bluetoothAdapter.stopLeScan(leScanCallback);
            mScanCallBack.scanBleStop();
            mScanCallBack = null;

            Log.i("haix", "扫描失败!");
        }
    }

    public void nowStopScan() {

        if (mScanCallBack != null) {
            mHandler.removeCallbacksAndMessages(null);
            bluetoothAdapter.stopLeScan(leScanCallback);
            mScanCallBack = null;

            Log.i("haix", "停止扫描!");
        }
    }


    // BluetoothDevice.connectGatt() ,
// BluetoothGatt.connectBle() ,
// BluetoothGatt.disconnect() 等操作最好都放在主线程中
    //对  BluetoothGatt 的连接和断开请求，都通过发送消息到 Android 的主线程中，让主线程来执行具体的操作

    private DeviceUUID deviceUUID;

    public void connectBle(DeviceUUID deviceUUID, final BluetoothDevice device, final BluetoothGattStateListener bgl) {

        if (deviceUUID == null) {
            return;
        }

        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }

        this.deviceUUID = deviceUUID;

        nowStopScan();

        //保证是在UI线程中执行连接操作
        runOnMainThread(new Runnable() {
            @Override
            public void run() {

                //重新连接
                disConnection();

                bluetoothGattStateListener = bgl;


                if (device == null) {
                    return;
                }

                Log.i("haix", "开始连接: " + device.getName());
                mBluetoothGatt = device.connectGatt(mContext, false, bluetoothGattCallback);
                //delayConnectResponse(0);

            }
        });


        //delayConnectResponse(0);
    }


    //是否是用户手动断开
    private boolean isBreakByMyself = false;

    /**
     * 手动断开Ble连接
     */
    public void nowCloseBleConn() {
        isBreakByMyself = true;
        disConnection();

//        gattCharacteristic = null;
//        mBluetoothManager = null;
    }

    /**
     * 断开连接
     */
    private void disConnection() {
        if (mBluetoothGatt != null) {
            //会回调 onConnectionStateChange
            mBluetoothGatt.disconnect();
            //mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
        reset();
        Log.i("haix", "断开连接");
    }

    //默认连接超时时间:6s
    private static final int CONNECTION_TIME_OUT = 20000;

    /**
     * 如果连接connectionTimeOut时间后还没有响应,手动关掉连接.
     *
     * @param connectionTimeOut
     */
    private void delayConnectResponse(int connectionTimeOut) {
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isConnected && !isBreakByMyself) {
                    Log.i("haix", "连接超时");
                    disConnection();
                    bluetoothGattStateListener.connectFailed(-100);

                } else {
                    isBreakByMyself = false;
                }
            }
        }, connectionTimeOut <= 0 ? CONNECTION_TIME_OUT : connectionTimeOut);
    }


    private HashMap<String, HashMap<String, BluetoothGattCharacteristic>> servicesMap = new HashMap<>();


    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {

        //当连接状态发生改变
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {


            if (status != BluetoothGatt.GATT_SUCCESS) {
                //String err = "不能连接设备 Cannot connectBle device with error status: " + status;
                // 当尝试连接失败的时候调用 disconnect 方法是不会引起这个方法回调的，所以这里
                //   直接回调就可以了。
                gatt.close();
                reset();

                bluetoothGattStateListener.connectFailed(status);
                return;
            }

            //连接成功
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                //搜索服务 异步执行 回调onServicesDiscovered
                gatt.discoverServices();
                isBreakByMyself = false;

                //断开连接
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {

                //Log.i("haix", "断开连接回调");
                //mBluetoothGatt.connectBle();//重连接
                //gatt.disconnect();
                gatt.close();
                reset();

                //如果不是手动断开的
                if (!isBreakByMyself) {
                    runOnMainThread(new Runnable() {
                        @Override
                        public void run() {

                            //重新连接
                        }
                    });
                }

            }
        }


        //调用 mBluetoothGatt.discoverServices();
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {

            //服务连接成功
            if (status == BluetoothGatt.GATT_SUCCESS) {

                //手机设备和蓝牙设备才算是真正建立了可通信的连接
                //接下来就可以执行相应的蓝牙通信操作了

//                if (bluetoothAdapter == null || mBluetoothGatt == null) {
//                    return;
//                }

                if (deviceUUID.getSERVICE_UUID() != null && deviceUUID.getCHARACTER_UUID() != null && gatt != null) {
                    BluetoothGattService service = gatt.getService(deviceUUID.getSERVICE_UUID());
                    if (service == null){
                        return;
                    }
                    BluetoothGattCharacteristic characteristic = service.getCharacteristic(deviceUUID.getCHARACTER_UUID());
                    gatt.setCharacteristicNotification(characteristic, true);
                    //拿到服务特征
                    BluetoothGattDescriptor descriptor = characteristic.getDescriptor(deviceUUID.getUUID_FINAL());
                    //设置特征为ENABLE_NOTIFICATION_VALUE
                    //设置成功后可以在该特征值上发送数据到ble设备和接收ble设备的数据
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    boolean b = gatt.writeDescriptor(descriptor);
                } else {
                    Log.i("haix", "UUID没有设置");
                }


                if (deviceUUID.getSERVICE_UUID() != null && deviceUUID.getDATA_LINE_UUID() != null) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mBluetoothGatt == null){
                                return;
                            }
                            BluetoothGattService service1 = mBluetoothGatt.getService(deviceUUID.getSERVICE_UUID());
                            BluetoothGattCharacteristic characteristic1 = service1.getCharacteristic(deviceUUID.getDATA_LINE_UUID());
                            mBluetoothGatt.setCharacteristicNotification(characteristic1, true);
                            //拿到服务特征
                            BluetoothGattDescriptor descriptor1 = characteristic1.getDescriptor(deviceUUID.getUUID_FINAL());
                            //设置特征为ENABLE_NOTIFICATION_VALUE
                            //设置成功后可以在该特征值上发送数据到ble设备和接收ble设备的数据
                            descriptor1.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            mBluetoothGatt.writeDescriptor(descriptor1);
                        }
                    }, 2000);
                }

                //1  getServices()
//                List<BluetoothGattService> services = gatt.getServices();
//                int serviceSize = services.size();
//                for (int i = 0; i < serviceSize; i++) {
//                    HashMap<String, BluetoothGattCharacteristic> charMap = new HashMap<>();
//                    BluetoothGattService bluetoothGattService = services.get(i);
//                    //一个uuid  key
//                    String serviceUuid = bluetoothGattService.getUuid().toString();
//                    //Log.i("haix", "总uuid: "+serviceUuid);
//                    //2  getCharacteristics()
//                    List<BluetoothGattCharacteristic> characteristics = bluetoothGattService.getCharacteristics();
//                    int characteristicSize = characteristics.size();
//                    for (int j = 0; j < characteristicSize; j++) {
//                        //Log.i("haix", "次uuid: "+characteristics.get(j).getUuid().toString());
//                        charMap.put(characteristics.get(j).getUuid().toString(), characteristics.get(j));
//
//                        //拿到服务
//                        BluetoothGattCharacteristic bgc = characteristics.get(j);
//                        if (bgc.getUuid().toString().equals(mUUID2.toString())) {
//
//
//                            Log.i("haix", "暂时连接成功! "+ serviceUuid);
//                            //设置监听  characteristic 变化的通知
//                            //开启 Android 端接收通知的开关
//                            gatt.setCharacteristicNotification(bgc, true);
//                            //拿到服务特征
//                            BluetoothGattDescriptor descriptor = bgc.getDescriptor(mUUID_FINAL);
//                            //设置特征为ENABLE_NOTIFICATION_VALUE
//                            //设置成功后可以在该特征值上发送数据到ble设备和接收ble设备的数据
//                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//                            boolean b = gatt.writeDescriptor(descriptor);
//
//                            //完成回调onDescriptorWrite
//                            if (!b){
//                                runOnMainThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//
//                                        //重新连接
//                                    }
//                                });
//                            }
//
//                        }
//                    }
//                  servicesMap.put(serviceUuid, charMap);
//                }

            } else {
                bluetoothGattStateListener.connectFailed(status);
            }
        }

        //描述符被写了
        @Override
        public void onDescriptorWrite(final BluetoothGatt gatt,
                                      final BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);

            //设置成功
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //todo 设置成功后就开始发送数据了。

                Log.i("haix", "设备连接成功!");

                isConnected = true;

                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {

                        bluetoothGattStateListener.onWrite(gatt, descriptor);
                    }
                });


            }

        }


        // 读取数据
//        BluetoothGattService service = gatt.getService(SERVICE_UUID);
//        BluetoothGattCharacteristic characteristic = gatt.getCharacteristic(CHARACTER_UUID);
//        gatt.readCharacteristic();

        //BluetoothGattCharactristic#readCharacteristic
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //BluetoothGattCharacteristic#getValue
                //Log.i("haix", "读取到数据 ");
            }
        }

        //收到数据
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            // broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);

            byte[] value = characteristic.getValue();
            bluetoothGattStateListener.onRead(gatt, characteristic);

        }


        //发送数据结果  发送数据成功回调
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status) {
            // TODO Auto-generated method stub
            //super.onCharacteristicWrite(gatt, characteristic, status);

            //检查发送的数据是不是要发送的
//            if (!characteristic.getValue().equals(bytes)) {
//                Log.i("haix", "发送错误: " + characteristic.getValue());
//                // 执行重发策略
//                gatt.writeCharacteristic(characteristic);
//            } else {
//                //发送数据成功了!
//
//            }

            bluetoothGattStateListener.onWriteSuccess(characteristic);


        }


    };

    private byte[] bytes;

    //发送数据要等到上一条数据发送成功后再发下一条数据

    //往蓝牙数据通道的写入数据
//    BluetoothGattService service = gattt.getService(SERVICE_UUID);
//    BluetoothGattCharacteristic characteristic = gatt.getCharacteristic(CHARACTER_UUID);
//    characteristic.setValue(sendValue);
//    gatt.writeCharacteristic(characteristic);

    //收发数据就是监听这些 Charateristic 中的 Value 字段有没有变化

    //写入数据和读取数据是不能同时进行的

    /**
     * 发送数据
     *
     * @param buf
     */
    public void writeBuffer(byte[] buf) {
        if (!bluetoothAdapter.isEnabled()) {
            return;
        }

        //找服务
//        Map<String, BluetoothGattCharacteristic> bluetoothGattCharacteristicMap = servicesMap.get(SERVICE_UUID);
//        if (null == bluetoothGattCharacteristicMap) {
//            return;
//        }
//
//        //找特征
//        Set<Map.Entry<String, BluetoothGattCharacteristic>> entries = bluetoothGattCharacteristicMap.entrySet();
//        BluetoothGattCharacteristic gattCharacteristic = null;
//        for (Map.Entry<String, BluetoothGattCharacteristic> entry : entries) {
//            if (mUUID2.equals(entry.getKey())) {
//                gattCharacteristic = entry.getValue();
//                break;
//            }
//        }


        if(mBluetoothGatt == null){
            return;
        }
        BluetoothGattService service = mBluetoothGatt.getService(deviceUUID.getSERVICE_UUID());
        BluetoothGattCharacteristic characteristic = null;
        if (deviceUUID.getDATA_LINE_UUID() == null) {
            if (service == null){
                return;
            }
            characteristic = service.getCharacteristic(deviceUUID.getCHARACTER_UUID());
        } else {

            if (service == null){
                return;
            }
            characteristic = service.getCharacteristic(deviceUUID.getDATA_LINE_UUID());
        }


        bytes = buf;

        //最多单次1支持 20 个字节数据的传输
        //设置数组进去
        characteristic.setValue(buf);
        //发送  通知系统异步写数据
        if(mBluetoothGatt == null){
            return;
        }
        boolean b = mBluetoothGatt.writeCharacteristic(characteristic);

        Log.i("haix", "发送数据的结果: "+b);

    }

    private void runOnMainThread(Runnable runnable) {
        if (isMainThread()) {
            runnable.run();
        } else {
            if (mHandler != null) {
                mHandler.post(runnable);
            }
        }
    }

    private boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }


    public static class BleDeviceScanCallback implements BluetoothAdapter.LeScanCallback {
        private ScanCallback mScanCallback;

        public BleDeviceScanCallback(ScanCallback scanCallback) {
            this.mScanCallback = scanCallback;
        }

        public void onLeScanStop() {
            if (null != mScanCallback) {
                mScanCallback.scanBleStop();
            }
        }

        //rssi 表示的则是设备距离的远近，信号强弱值
        //scanRecord ServiceUuids
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (null != mScanCallback) {
                //每次扫描到设备会回调此方法,应该在此回调方法中去除重复并加入集合，用于列表展示

                if (device != null && device.getName() != null) {
                    //不要做太多的事
                    //可以把扫描到的设备，扔到另外一个线程中去处理，让  onLeScan() 尽快返回
                    mScanCallback.scanBle(device, rssi, scanRecord);
                }
            }
        }
    }

    /**
     * 复位
     */
    private void reset() {
        isConnected = false;
        servicesMap.clear();
    }


    public interface ScanCallback {


        /**
         * 扫描过程中,每扫描到一个设备回调一次
         *
         * @param device     扫描到的设备
         * @param rssi       设备的信息强度
         * @param scanRecord
         */
        void scanBle(final BluetoothDevice device, int rssi, byte[] scanRecord);

        void scanBleStop();
    }


    BluetoothGattStateListener bluetoothGattStateListener;

    public interface BluetoothGattStateListener {

        void onWrite(BluetoothGatt gatt,
                     BluetoothGattDescriptor characteristic);

        void onWriteSuccess(BluetoothGattCharacteristic characteristic);

        void onRead(BluetoothGatt gatt,
                    BluetoothGattCharacteristic characteristic);

        void connectFailed(int status);

    }


    public void sss() {
//        try {
//            Method localMethod = mBluetoothGatt.getClass().getMethod("refresh");
//            if (localMethod != null) {
//                return (Boolean) localMethod.invoke(mBluetoothGatt);
//            }
//        } catch (Exception localException) {
//            Log.e("refreshServices()", "An exception occured while refreshing device");
//        }
    }


    public String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (byte aSrc : src) {
            int v = aSrc & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv.toUpperCase()).append(" ");
        }
        return stringBuilder.toString();
    }


}

