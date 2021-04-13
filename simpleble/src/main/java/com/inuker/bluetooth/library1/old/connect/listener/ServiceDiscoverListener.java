package com.inuker.bluetooth.library1.old.connect.listener;

import com.inuker.bluetooth.library1.old.model.BleGattProfile;

/**
 * Created by dingjikerbo on 2016/8/25.
 */
public interface ServiceDiscoverListener extends GattResponseListener {
    void onServicesDiscovered(int status, BleGattProfile profile);
}
