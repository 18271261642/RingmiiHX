package com.inuker.bluetooth.library.old.connect.listener;

import com.inuker.bluetooth.library.old.model.BleGattProfile;

/**
 * Created by dingjikerbo on 2016/8/25.
 */
public interface ServiceDiscoverListener extends GattResponseListener {
    void onServicesDiscovered(int status, BleGattProfile profile);
}
