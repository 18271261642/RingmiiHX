package ble.callback;

import ble.BleClient;
import ble.SimpleDevice;

public abstract class SimpleCallback implements ISearchCallback, IConnectCallback {

    private SimpleDevice device;

    @Override
    public void onSearchStarted() {
        device = null;

    }

    @Override
    public void onDeviceFounded(SimpleDevice device) {

        this.device = onFindDevice(device);
        if (this.device != null) {

            BleClient.instance().stopSearching();
        }
    }

    @Override
    public void onSearchStopped() {

        onSearchFinish();
    }

    @Override
    public void onSearchCanceled() {

        if (device != null) {
            if (device.isBLE) {
                BleClient.instance().connect(device, this);
            } else {
                BleClient.instance().getClassicClient().connect(device, this);
            }
            device = null;
        }
    }

    protected abstract SimpleDevice onFindDevice(SimpleDevice device);


    protected abstract void onSearchFinish();

    @Override
    public void onConnectStart(SimpleDevice device) {

    }

    @Override
    public void onConnectSuccess(SimpleDevice device) {

    }

    @Override
    public void onConnectFail(String msg) {

    }

}
