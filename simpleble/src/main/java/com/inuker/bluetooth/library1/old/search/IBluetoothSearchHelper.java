package com.inuker.bluetooth.library1.old.search;

import com.inuker.bluetooth.library1.old.search.response.BluetoothSearchResponse;

/**
 * Created by dingjikerbo on 2016/8/28.
 */
public interface IBluetoothSearchHelper {

    void startSearch(BluetoothSearchRequest request, BluetoothSearchResponse response);

    void stopSearch();
}
