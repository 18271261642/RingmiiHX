package com.guider.health.foraglu;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

public interface IBleServiceManager {
    void scanAndconnectFailed();
    void generateData(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic);
}
