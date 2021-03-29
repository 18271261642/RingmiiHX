package com.inuker.bluetooth.library.old.connect.request;

import com.inuker.bluetooth.library.old.connect.IBleConnectDispatcher;

/**
 * Created by dingjikerbo on 16/8/25.
 */
public interface IBleRequest {

    void process(IBleConnectDispatcher dispatcher);

    void cancel();
}
