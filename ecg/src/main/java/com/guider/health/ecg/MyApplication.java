package com.guider.health.ecg;

import android.app.Application;

import com.guider.health.common.core.NetIp;
import com.guider.health.common.net.app.ProjectInit;


/**
 * Created by jett on 2018/6/4.
 */

public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();


        ProjectInit.init(this)
                .withApiHost(NetIp.BASE_URL)
                .configure();

//        com.imediplus.phonomagics.app     ECGMainActivity

    }


}
