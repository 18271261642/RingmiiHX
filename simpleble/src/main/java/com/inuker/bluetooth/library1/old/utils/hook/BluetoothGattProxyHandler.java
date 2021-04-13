package com.inuker.bluetooth.library1.old.utils.hook;

import com.inuker.bluetooth.library1.old.utils.BluetoothLog;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by dingjikerbo on 16/9/2.
 */
public class BluetoothGattProxyHandler implements InvocationHandler {

    private Object bluetoothGatt;

    BluetoothGattProxyHandler(Object bluetoothGatt) {
        this.bluetoothGatt = bluetoothGatt;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        BluetoothLog.v(String.format("IBluetoothGatt method: %s", method.getName()));
        return method.invoke(bluetoothGatt, args);
    }
}