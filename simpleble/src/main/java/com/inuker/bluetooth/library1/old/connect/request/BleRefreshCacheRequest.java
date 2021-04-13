package com.inuker.bluetooth.library1.old.connect.request;

import com.inuker.bluetooth.library1.old.Code;
import com.inuker.bluetooth.library1.old.connect.response.BleGeneralResponse;

/**
 * Created by liwentian on 2017/2/15.
 */

public class BleRefreshCacheRequest extends BleRequest {

    public BleRefreshCacheRequest(BleGeneralResponse response) {
        super(response);
    }

    @Override
    public void processRequest() {
        refreshDeviceCache();

        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                onRequestCompleted(Code.REQUEST_SUCCESS);
            }
        }, 3000);
    }
}