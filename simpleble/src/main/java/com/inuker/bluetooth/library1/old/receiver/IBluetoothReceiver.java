package com.inuker.bluetooth.library1.old.receiver;

import com.inuker.bluetooth.library1.old.receiver.listener.BluetoothReceiverListener;

/**
 * Created by dingjikerbo on 2016/11/25.
 */

public interface IBluetoothReceiver {

    void register(BluetoothReceiverListener listener);
}
