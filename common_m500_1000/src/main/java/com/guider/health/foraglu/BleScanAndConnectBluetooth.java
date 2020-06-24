package com.guider.health.foraglu;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.os.Handler;

import com.guider.health.bluetooth.core.BleBluetooth;
import com.guider.health.bluetooth.core.DeviceUUID;

import ble.BleClient;
import ble.SimpleDevice;
import ble.callback.SimpleCallback;


/**
 * Created by haix on 2019/6/8.
 */

public class BleScanAndConnectBluetooth {

    private BluetoothDevice device;
    private static String BT_NAME = "FORA GD40";

    private GluServiceManager measure;
    private DeviceUUID deviceUUID;

    public BleScanAndConnectBluetooth(GluServiceManager m, DeviceUUID deviceUUID, String btName){
        this.measure = m;
        this.deviceUUID = deviceUUID;
        this.BT_NAME = btName;
    }

    public void run() {
        BleClient.instance().searchDevice(1000 * 90, new SimpleCallback() {
            @Override
            protected SimpleDevice onFindDevice(SimpleDevice device) {
                if (BT_NAME.equals(device.getName())) {
                    BleScanAndConnectBluetooth.this.device = device.deviceInfo.device;

                    BleClient.instance().stopSearching();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            toConnect();
                        }
                    }, 500);
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
    }

    public void stop() {
        BleClient.instance().stopSearching();
        BleBluetooth.getInstance().nowCloseBleConn();
    }

    public void toConnect() {
        BleBluetooth.getInstance().connectBle(deviceUUID, device, new BleBluetooth.BluetoothGattStateListener() {
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
