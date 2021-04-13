package com.guider.glu_phone;

import android.app.Application;

import com.guider.health.apilib.ApiUtil;
import com.guider.health.common.core.MyUtils;
import com.guider.health.common.device.standard.Constant;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by haix on 2019/8/13.
 */

public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        MyUtils.application = this;

        if (!Constant.notInitBugly) {
            Bugly.init(getApplicationContext(), "e23a30c1bb", false);
            CrashReport.putUserData(this, "Mac", MyUtils.getMacAddress() + "");
        }
        /*
        UMConfigure.init(this, "5d3a98154ca357cfde000a87"
                , "umeng", UMConfigure.DEVICE_TYPE_PHONE, "");
        UMConfigure.setLogEnabled(true);
        PlatformConfig.setWeixin("wxfc96fa4b92b43c9a", "a49af0d9adb38421150fd0edef3a3a1e");
        */
    }

}
