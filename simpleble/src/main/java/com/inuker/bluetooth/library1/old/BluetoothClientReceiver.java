package com.inuker.bluetooth.library1.old;

import com.inuker.bluetooth.library1.old.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library1.old.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library1.old.receiver.listener.BluetoothBondListener;
import com.inuker.bluetooth.library1.old.connect.listener.BluetoothStateListener;

import java.util.HashMap;
import java.util.List;

/**
 * Created by liwentian on 2017/1/13.
 */

public class BluetoothClientReceiver {

    private HashMap<String, HashMap<String, List<BleNotifyResponse>>> mNotifyResponses;
    private HashMap<String, List<BleConnectStatusListener>> mConnectStatusListeners;
    private List<BluetoothStateListener> mBluetoothStateListeners;
    private List<BluetoothBondListener> mBluetoothBondListeners;
}
