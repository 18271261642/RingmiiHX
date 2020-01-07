package ble.callback;

import ble.SimpleDevice;

public interface IConnectCallback {

    void onConnectStart(SimpleDevice device);

    void onConnectSuccess(SimpleDevice device);

    void onConnectFail(String msg);
}
