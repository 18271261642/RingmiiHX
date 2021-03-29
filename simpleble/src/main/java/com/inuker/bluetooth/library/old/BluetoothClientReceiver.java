package com.inuker.bluetooth.library.old;

import com.inuker.bluetooth.library.old.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.old.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.old.receiver.listener.BluetoothBondListener;
import com.inuker.bluetooth.library.old.connect.listener.BluetoothStateListener;

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
