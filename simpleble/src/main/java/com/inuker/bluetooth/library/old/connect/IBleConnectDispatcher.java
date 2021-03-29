package com.inuker.bluetooth.library.old.connect;

import com.inuker.bluetooth.library.old.connect.request.BleRequest;

public interface IBleConnectDispatcher {

    void onRequestCompleted(BleRequest request);
}
