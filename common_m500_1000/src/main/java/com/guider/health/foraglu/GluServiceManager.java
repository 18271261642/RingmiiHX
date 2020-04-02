package com.guider.health.foraglu;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.guider.health.bluetooth.core.BleBluetooth;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by haix on 2019/6/10.
 */

public class GluServiceManager {
    private BleScanAndConnectBluetooth bpScanAndConnectBluetooth;
    private ForaGluModel foraGluModel = new ForaGluModel();
    private WeakReference<BleVIewInterface> bpViewData;
    private ExecutorService executor = Executors.newFixedThreadPool(10);

    private GluServiceManager() {
    }

    private static GluServiceManager serviceManager = new GluServiceManager();

    public static GluServiceManager getInstance() {
        return serviceManager;
    }

    public void setViewObject(BleVIewInterface bpView) {
        bpViewData = new WeakReference<BleVIewInterface>(bpView);
    }

    public void startMeasure() {
        if (bpScanAndConnectBluetooth == null) {
            bpScanAndConnectBluetooth = new BleScanAndConnectBluetooth(this);
        }
        bpScanAndConnectBluetooth.run();
    }

    public void scanAndconnectFailed() {
        if (bpViewData.get() != null) {
            bpViewData.get().connectNotSuccess();
        }
    }

    public void stopDeviceConnect() {
        if (bpScanAndConnectBluetooth != null)
            bpScanAndConnectBluetooth.stop();
    }

    public String getSbp(int sbp){
        if (130 >= sbp && sbp >= 90){
            return "正常";
        }else if (sbp > 130){
            return "偏高";
        }else if (sbp < 90){
            return "偏低";
        }

        return "";
    }

    public String getDbp(int dbp){
        if (90 >= dbp && dbp >= 60){
            return "正常";
        }else if (dbp > 90){
            return "偏高";
        }else if (dbp < 60){
            return "偏低";
        }

        return "";
    }


    public void generateData(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        synchronized (this) {
            if (gatt == null || characteristic == null) {
                foraGluModel.writeReadDeviceSerial();
                return;
            }
            ArrayList<Byte> characteristicValues = new ArrayList<Byte>();
            // Mark 裡面是量測值
            for(int i = 0 ; i < characteristic.getValue().length ; i++){
                if (characteristic.getValue()[i] != 0){
                    characteristicValues.add(characteristic.getValue()[i]);
                    System.out.println("Mark " + intParse(characteristic, i));
                }
            }

            // Mark 過程
            switch (characteristic.getValue()[1]) {
                case 39:
                    foraGluModel.deviceSerialNumber1DateFormat(characteristicValues, characteristic, gatt);
                    break;
                case 40:
                    foraGluModel.deviceSerialNumber2DateFormat(characteristicValues, characteristic, gatt);
                    break;
                case 51:
                    foraGluModel.writeClockDateTime(characteristicValues, characteristic, gatt);
                    break;
                case 35:
                    foraGluModel.readClockTimeDateFormat(characteristicValues, characteristic, gatt);
                    break;
                case 38:
                    foraGluModel.measureDataFormat(characteristicValues, characteristic, gatt);
                    break;
                case 80:
                    foraGluModel.trunOffDevice(characteristicValues, characteristic, gatt);
                    if (bpViewData.get() != null) {
                        // bpViewData.get().startUploadData();
                        bpViewData.get().connectAndMessureIsOK();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public int intParse(BluetoothGattCharacteristic characteristic, int position) {
        return (characteristic.getValue()[position] <= 0 ? byteToInt(characteristic.getValue()[position]) :
                characteristic.getValue()[position]);
    }

    public int byteToInt(byte b) {
        return b & 0xFF;
    }
}
