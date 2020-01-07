package ble.callback;

import ble.SimpleDevice;

public interface IDisConnectCallback {

    void onDisconnect(SimpleDevice device);
}
