package ble;

import com.inuker.bluetooth.library1.old.search.SearchResult;

import java.util.TreeSet;

/**
 * 扫描过滤器
 */
public class SearchFilter {

    private TreeSet<String> deviceAddress;
    public SearchFilter() {
        deviceAddress = new TreeSet<>();
    }

    public SimpleDevice getDevice(SearchResult searchResult) {
        String address = searchResult.getAddress();
        boolean isNew = deviceAddress.add(address);
        if (isNew) {
            return new SimpleDevice(searchResult);
        }
        return null;
    }
}
