package com.guider.health.bp.presenter;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.os.Handler;

import com.guider.health.bluetooth.core.BleBluetooth;

import ble.BleClient;
import ble.SimpleDevice;
import ble.callback.SimpleCallback;


/**
 * Created by haix on 2019/6/8.
 */

public class BPScanAndConnectBluetooth{

    private BluetoothDevice device;
    private final static String FORA_P30 = "FORA P30 PLUS";

    private BPServiceManager measure;
    public BPScanAndConnectBluetooth(BPServiceManager m){
        this.measure = m;
    }

    public void run() {
        BleClient.instance().searchDevice(1000 * 90, new SimpleCallback() {
            @Override
            protected SimpleDevice onFindDevice(SimpleDevice device) {
                if (FORA_P30.equals(device.getName())) {
                    BPScanAndConnectBluetooth.this.device = device.deviceInfo.device;

                    BleClient.instance().stopSearching();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            toConnect();
                        }
                    }, 500);
//                    return device;
                }
                return null;
            }

            @Override
            protected void onSearchFinish() {
                if (device == null){
                    measure.scanAndconnectFailed();
                }
            }

            @Override
            public void onConnectSuccess(SimpleDevice device) {
                super.onConnectSuccess(device);
//                toConnect();
            }
        });
//        BleBluetooth.getInstance().scanBle(60000, new BleBluetooth.ScanCallback() {
//
//            @Override
//            public void scanBle(BluetoothDevice btDevice, int rssi, byte[] scanRecord) {
//
//
//                Log.i("haix", "device: "+btDevice.getName()+ " 地址: "+ btDevice.getAddress());
//
//                if (FORA_P30.equals(btDevice.getName())) {
//                    if (device == null){
//                        device = btDevice;
//                        HeartPressBp.getInstance().setDeviceAddress(device.getAddress());
//
//                        toConnect();
//                    }
//                }
//            }
//
//            @Override
//            public void scanBleStop() {
//
//
//                if (device == null){
//                    measure.scanAndconnectFailed();
//                }
//            }
//        });

    }

    public void toConnect() {

        BleBluetooth.getInstance().connectBle(new BPDeviceUUID(), device, new BleBluetooth.BluetoothGattStateListener() {


            @Override
            public void onWrite(BluetoothGatt gatt, BluetoothGattDescriptor characteristic) {
                    measure.generateData(null, null);
            }

            @Override
            public void onWriteSuccess(BluetoothGattCharacteristic characteristic) {

            }

            @Override
            public void onRead(BluetoothGatt gatt,
                               BluetoothGattCharacteristic characteristic) {

                    measure.generateData(gatt, characteristic);

            }

            @Override
            public void connectFailed(int status) {

                if (device == null){
                    measure.scanAndconnectFailed();
                }

            }
        });

    }



}
