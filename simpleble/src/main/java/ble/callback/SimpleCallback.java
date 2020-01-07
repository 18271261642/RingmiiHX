package ble.callback;

import android.util.Log;

import ble.BleClient;
import ble.SimpleDevice;

public abstract class SimpleCallback implements ISearchCallback, IConnectCallback {

    private SimpleDevice device;

    @Override
    public void onSearchStarted() {
        device = null;
        Log.i("Simbluetoot", "开始扫描");
    }

    @Override
    public void onDeviceFounded(SimpleDevice device) {
        if (this.device != null) {
            return;
        }
        Log.i("Simbluetoot", "搜索到设备" + device.getName());
        this.device = onFindDevice(device);
        if (this.device != null) {
            Log.i("Simbluetoot", "自动连接" + device.getName());
            BleClient.instance().stopSearching();
        }
    }

    @Override
    public void onSearchStopped() {
        Log.i("Simbluetoot", "扫描停止" + device);
        onSearchFinish();
    }

    @Override
    public void onSearchCanceled() {
        Log.i("Simbluetoot", "扫描取消" + device);
        if (device != null) {
            if (device.isBLE) {
                BleClient.instance().connect(device, this);
            } else {
                BleClient.instance().getClassicClient().connect(device, this);
            }
            device = null;
        }
    }

    /**
     * 当搜索到设备的时候回进行该回调
     *
     * @param device 搜索到的设备
     * @return true  代表你找到了想连接的设备并且停止查找
     * false 表示没有找到并且继续查找
     */
    protected abstract SimpleDevice onFindDevice(SimpleDevice device);

    /**
     * 停止搜索
     * 主动还是被动停止 都会调用该方法
     */
    protected abstract void onSearchFinish();

    @Override
    public void onConnectStart(SimpleDevice device) {
        Log.i("Simbluetoot", "开始连接" + device.getName());
    }

    @Override
    public void onConnectSuccess(SimpleDevice device) {
        Log.i("Simbluetoot", "连接成功" + device.getName());
    }

    @Override
    public void onConnectFail(String msg) {
        Log.i("Simbluetoot", "连接失败" + msg);
    }

}
