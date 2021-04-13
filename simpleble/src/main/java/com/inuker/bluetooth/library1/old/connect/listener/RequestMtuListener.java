package com.inuker.bluetooth.library1.old.connect.listener;

/**
 * Created by fuhao on 2017/8/24.
 */

public interface RequestMtuListener extends GattResponseListener {
    void onMtuChanged(int mtu, int status);
}
