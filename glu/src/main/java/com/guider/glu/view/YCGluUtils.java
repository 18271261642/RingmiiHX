package com.guider.glu.view;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import ble.BleClient;
import ble.SimpleDevice;
import ble.callback.SimpleCallback;

public class YCGluUtils {

    static String TAG = "YYYYYcUtil";
    Context activity;

    public YCGluUtils(Activity activity) {
        this.activity = activity;
        BleClient.init(activity);
    }

    public void start() {
        BleClient.instance().searchDevice(9999 * 1000 , new SimpleCallback() {
            @Override
            protected SimpleDevice onFindDevice(SimpleDevice device) {
                Log.i(TAG, device.getName());
                return null;
            }

            @Override
            protected void onSearchFinish() {

            }
        });
    }
    
    private Callback callback;
    public void setCallback(Callback listener) {
        this.callback = listener;
    }
    public interface Callback {
        void onConnect();

        void onDisconnect();

    }

}
