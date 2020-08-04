package com.guider.health.foraet;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;
import androidx.annotation.RequiresApi;

import com.guider.health.bluetooth.core.BleBluetooth;
import com.guider.health.foraglu.BleScanAndConnectBluetooth;
import com.guider.health.foraglu.BleVIewInterface;
import com.guider.health.foraglu.IBleServiceManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by haix on 2019/6/10.
 */
public class ForaETServiceManager implements IBleServiceManager {
    private static final String BT_NAME = "FORA IR21";
    private BleScanAndConnectBluetooth bpScanAndConnectBluetooth;
    private ForaETModel foraETModel = new ForaETModel();
    private WeakReference<BleVIewInterface> bpViewData;
    private ExecutorService executor = Executors.newFixedThreadPool(10);

    private ForaETServiceManager() {
    }

    private static ForaETServiceManager serviceManager = new ForaETServiceManager();

    public static ForaETServiceManager getInstance() {
        return serviceManager;
    }

    public void setViewObject(BleVIewInterface bpView) {
        bpViewData = new WeakReference<BleVIewInterface>(bpView);
    }

    public void write(String command) {
        BleBluetooth.getInstance().writeBuffer(foraETModel.getHexBytes(command));
    }

    public void startMeasure() {
        if (bpScanAndConnectBluetooth == null) {
            bpScanAndConnectBluetooth = new BleScanAndConnectBluetooth(this, new ForaETDeviceUUID(), BT_NAME);
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

    public void generateData(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        synchronized (this) {
            if (gatt == null || characteristic ==null) {
                foraETModel.writeReadDeviceSerial();
                return;
            }
            ArrayList<Byte> characteristicValues = new ArrayList<Byte>();
            for (int i = 0; i < characteristic.getValue().length; i++) {
                if (characteristic.getValue()[i] != 0){
                    characteristicValues.add(characteristic.getValue()[i]);
                    // System.out.println("Mark"+intParse(characteristic, i));
                }
            }
            switch (characteristic.getValue()[1]) {
                case 39:
                    foraETModel.deviceSerialNumber1DateFormat(characteristicValues, characteristic, gatt);
                    break;
                case 40:
                    foraETModel.deviceSerialNumber2DateFormat(characteristicValues, characteristic, gatt);
                    break;
                case 51:
                    foraETModel.writeClockDateTime(characteristicValues, characteristic, gatt);
                    break;
                case 35:
                    foraETModel.readClockTimeDateFormat(characteristicValues, characteristic, gatt);
                    break;
                case 38:
                    foraETModel.measureDataFormat(characteristicValues, characteristic, gatt);
                    break;
                case 80:
                    foraETModel.trunOffDevice(characteristicValues, characteristic, gatt);
                    if (bpViewData.get() != null) {
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
