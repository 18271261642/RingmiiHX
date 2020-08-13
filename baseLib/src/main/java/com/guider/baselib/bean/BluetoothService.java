package com.guider.baselib.bean;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import androidx.annotation.Nullable;

/**
 * Created by haix on 2019/7/3.
 */

public class BluetoothService extends Service{

    public class MyBinder extends Binder{

        public BluetoothService getService(){
            return BluetoothService.this;
        }

    }

    private MyBinder binder = new MyBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        //Log.i("DemoLog", "TestService -> onUnbind, from:" + intent.getStringExtra("from"));
        return false;
    }


}
