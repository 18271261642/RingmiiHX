package com.guider.health.forabo;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;

import com.guider.health.foraglu.BleScanAndConnectBluetooth;
import com.guider.health.foraglu.BleVIewInterface;
import com.guider.health.foraglu.IBleServiceManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by haix on 2019/6/10.
 */
public class ForaBOServiceManager implements IBleServiceManager {
    private static final String TAG = "ForaBOServiceManager";
    private static final String BT_NAME = "TAIDOC TD8201";
    private BleScanAndConnectBluetooth bpScanAndConnectBluetooth;
    private ForaBOModel foraBOModel = new ForaBOModel();
    private WeakReference<BleVIewInterface> bpViewData;
    private ExecutorService executor = Executors.newFixedThreadPool(10);

    private ForaBOServiceManager() { }

    private static ForaBOServiceManager serviceManager = new ForaBOServiceManager();

    public static ForaBOServiceManager getInstance() {
        return serviceManager;
    }

    public void setViewObject(BleVIewInterface bpView) {
        bpViewData = new WeakReference<>(bpView);
    }

    public void startMeasure() {
        if (bpScanAndConnectBluetooth == null) {
            bpScanAndConnectBluetooth = new BleScanAndConnectBluetooth(this, new ForaBODeviceUUID(), BT_NAME);
        }
        bpScanAndConnectBluetooth.run();
    }

    public void scanAndconnectFailed() {
        Log.i(TAG, "链接错误");
        if (bpViewData.get() != null) {
            bpViewData.get().connectNotSuccess();
        }
    }

    public void stopDeviceConnect() {
        if (bpScanAndConnectBluetooth != null)
            bpScanAndConnectBluetooth.stop();
    }

    public void generateData(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        synchronized (this) {
            if (gatt == null || characteristic == null) {
                foraBOModel.writeReadDeviceSerial();
                return;
            }
            System.out.println("characteristic的值 " + Arrays.toString(characteristic.getValue()));
            ArrayList<Byte> characteristicValues = new ArrayList<Byte>();
            // Mark 裡面是量測值
            for(int i = 0 ; i < characteristic.getValue().length ; i++){
                if (characteristic.getValue()[i] != 0){
                    characteristicValues.add(characteristic.getValue()[i]);
                     System.out.println("血氧结果Mark " + characteristic.getValue()[i]);
                }
            }

            // Mark 過程
            switch (characteristic.getValue()[1]) {
                case 39:
                    foraBOModel.deviceSerialNumber1DateFormat(characteristicValues, characteristic, gatt);
                    break;
                case 40:
                    foraBOModel.deviceSerialNumber2DateFormat(characteristicValues, characteristic, gatt);
                    break;
                case 51:
                    foraBOModel.writeClockDateTime(characteristicValues, characteristic, gatt);
                    break;
                case 35:
                    foraBOModel.readClockTimeDateFormat(characteristicValues, characteristic, gatt);
                    break;
                case 38:
                    foraBOModel.measureDataFormat(characteristicValues, characteristic, gatt);
                    //目前发现血氧仪只支持写入一次指令，再次写入系统报蓝牙写入数据失败，
                    // 所以获得数据后直接回调成功
                    Log.i(TAG, "关闭链接");
                    if (bpViewData.get() != null) {
                        // bpViewData.get().startUploadData();
                        bpViewData.get().connectAndMessureIsOK();
                    }
                    break;
                case 80:
                    foraBOModel.trunOffDevice(characteristicValues, characteristic, gatt);
                    break;
                default:
                    break;
            }
        }
    }
}
