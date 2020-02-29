package com.guider.glu.presenter;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.util.Log;

import com.guider.health.bluetooth.core.BleBluetooth;
import com.guider.health.common.core.Glucose;


/**
 * Created by haix on 2019/6/8.
 */

public class GLUScanAndConnectBluetooth implements Runnable {

    private BluetoothDevice device;
    private final static String BDE_WEIXIN_TTM = "BDE_WEIXIN_TTM";
    GLUServiceManager serviceManager;

    public GLUScanAndConnectBluetooth(GLUServiceManager m) {

        serviceManager = m;
    }

    @Override
    public void run() {

//        BleClient.instance().searchDevice(1000 * 10, new SimpleCallback() {
//            @Override
//            protected SimpleDevice onFindDevice(SimpleDevice btDevice) {
//                Log.i("AAAAAAAAA", "device: " + btDevice.getName());
//
//                if (BDE_WEIXIN_TTM.equals(btDevice.getName())) {
//                    if (device == null) {
//
//                        Log.i("haix", "发现指定设备BDE_WEIXIN_TTM");
//                        device = btDevice.deviceInfo.device;
//                        BleClient.instance().stopSearching();
//                        Glucose.getInstance().setDeviceAddress(device.getAddress());
//
//                        toConnect();
//                    }
//                }
//                return null;
//            }
//
//            @Override
//            protected void onSearchFinish() {
//                if (device == null) {
//
//                    serviceManager.connectFailed(-101);
//
//                }
//            }
//        });
        BleBluetooth.getInstance().scanBle(20000, new BleBluetooth.ScanCallback() {

            @Override
            public void scanBle(BluetoothDevice btDevice, int rssi, byte[] scanRecord) {


                Log.i("AAAAAAAAA", "device: " + btDevice.getName());

                if (BDE_WEIXIN_TTM.equals(btDevice.getName())) {
                    if (device == null) {

                        Log.i("haix", "发现指定设备BDE_WEIXIN_TTM");
                        device = btDevice;
                        Glucose.getInstance().setDeviceAddress(device.getAddress());

                        toConnect();
                    }
                }
            }

            @Override
            public void scanBleStop() {

                if (device == null) {

                    serviceManager.connectFailed(-101);

                }

            }
        });

    }

    public void toConnect() {
        final GLUDeviceUUID gluDeviceUUID = new GLUDeviceUUID();

        serviceManager.startConnect();

        BleBluetooth.getInstance().connectBle(gluDeviceUUID, device, new BleBluetooth.BluetoothGattStateListener() {


            @Override
            public void onWrite(BluetoothGatt gatt,
                                BluetoothGattDescriptor descriptor) {

                //byte[] blePackage = {(byte)0x43, (byte)0x43, (byte)0x44, (byte)0x4C, (byte)0x4A, (byte)0x43, (byte)0x24};

                if (descriptor.getCharacteristic().getUuid().equals(gluDeviceUUID.getDATA_LINE_UUID())) {

                    Log.i("haix", "glu正常发送数据");
                    if (serviceManager.getIsPhone()){
                        Log.i("haix", "运行了glu手机版流程");
                        serviceManager.connSuccess_phone();
                    }else{
                        serviceManager.connSuccess();
                    }







                }


            }

            @Override
            public void onWriteSuccess(BluetoothGattCharacteristic characteristic) {


            }

            @Override
            public void onRead(BluetoothGatt gatt,
                               BluetoothGattCharacteristic characteristic) {



                serviceManager.generateData(gatt, characteristic);


            }

            @Override
            public void connectFailed(int status) {

                serviceManager.connectFailed(status);

            }
        });

    }


}
