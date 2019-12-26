package com.guider.healthring.b30;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

public class BemoServiceConnection implements ServiceConnection {

    //将同步内容下方到if内部，提高了执行的效率，不必每次获取对象时都进行同步，只有第一次才同步，创建了以后就没必要了。
    private static volatile BemoServiceConnection instance = null;

    private BemoServiceConnection() {

    }

    public static BemoServiceConnection getInstance() {
        if (instance == null) {
            synchronized (BemoServiceConnection.class) {
                if (instance == null) {
                    instance = new BemoServiceConnection();
                }
            }
        }
        return instance;
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }
}
