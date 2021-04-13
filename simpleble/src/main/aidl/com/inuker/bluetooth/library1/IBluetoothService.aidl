// IBluetoothManager.aidl
package com.inuker.bluetooth.library1;

// Declare any non-default types here with import statements

import com.inuker.bluetooth.library1.IResponse;

interface IBluetoothService {
    void callBluetoothApi(int code, inout Bundle args, IResponse response);
}
