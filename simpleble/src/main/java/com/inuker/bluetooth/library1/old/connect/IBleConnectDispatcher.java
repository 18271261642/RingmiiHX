package com.inuker.bluetooth.library1.old.connect;

import com.inuker.bluetooth.library1.old.connect.request.BleRequest;

public interface IBleConnectDispatcher {

    void onRequestCompleted(BleRequest request);
}
