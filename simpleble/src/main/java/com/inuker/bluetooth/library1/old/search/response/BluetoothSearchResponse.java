package com.inuker.bluetooth.library1.old.search.response;

import com.inuker.bluetooth.library1.old.search.SearchResult;

public interface BluetoothSearchResponse {
    void onSearchStarted();

    void onDeviceFounded(SearchResult device);

    void onSearchStopped();

    void onSearchCanceled();
}
