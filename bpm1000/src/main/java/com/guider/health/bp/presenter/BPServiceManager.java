package com.guider.health.bp.presenter;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;

import com.guider.health.bluetooth.core.BleBluetooth;
import com.guider.health.bp.model.BPModel;
import com.guider.health.bp.view.BPVIewInterface;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by haix on 2019/6/10.
 */

public class BPServiceManager {

    private BPServiceManager() {
    }

    private static BPServiceManager serviceManager = new BPServiceManager();

    public static BPServiceManager getInstance() {
        return serviceManager;
    }

    private BPModel bpModel = new BPModel();
    private WeakReference<BPVIewInterface> bpViewData;

    public void setViewObject(BPVIewInterface bpView) {
        bpViewData = new WeakReference<BPVIewInterface>(bpView);
    }

    private ExecutorService executor = Executors.newFixedThreadPool(10);

    BPScanAndConnectBluetooth bpScanAndConnectBluetooth;
    public void startMeasure() {
        if (bpScanAndConnectBluetooth == null) {
            bpScanAndConnectBluetooth = new BPScanAndConnectBluetooth(this);
        }
        bpScanAndConnectBluetooth.run();
    }

    public void scanAndconnectFailed() {
        if (bpViewData.get() != null) {
            bpViewData.get().connectNotSuccess();
        }
    }

    public void stopDeviceConnect() {
        BleBluetooth.getInstance().nowStopScan();
        BleBluetooth.getInstance().nowCloseBleConn();
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
                bpModel.sendDataToDevice();
            } else {
                ArrayList<Byte> characteristicValues = new ArrayList<Byte>();
                // Mark 裡面是量測值
                for (int i = 0; i < characteristic.getValue().length; i++) {
                    if (characteristic.getValue()[i] != 0) {
                        characteristicValues.add(characteristic.getValue()[i]);
                    }
                }

                // Mark 過程
                Log.i("haix", "code: " + characteristic.getValue()[1]);
                switch (characteristic.getValue()[1]) {
                    case 39:
                        bpModel.deviceSerialNumber1DateFormat(characteristicValues, characteristic, gatt);
                        break;
                    case 40:
                        bpModel.deviceSerialNumber2DateFormat(characteristicValues, characteristic, gatt);
                        break;
                    case 51:
                        bpModel.writeClockDateTime(characteristicValues, characteristic, gatt);
                        break;
                    case 35:
                        bpModel.readClockTimeDateFormat(characteristicValues, characteristic, gatt);
                        break;
                    case 38:
                        bpModel.measureDataFormat(characteristicValues, characteristic, gatt);
                        break;
                    case 80:
                        bpModel.turnOffDevice(characteristicValues, characteristic, gatt);
                        // 關機了 開始做事
                        // System.out.println("Johnson 開始彈視窗");
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
    }
}
