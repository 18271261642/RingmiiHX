package ble.callback;

import ble.SimpleDevice;

public interface ISearchCallback {

    void onSearchStarted();

    void onDeviceFounded(SimpleDevice device);

    void onSearchStopped();

    void onSearchCanceled();


}
