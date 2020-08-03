package com.aliyun.rtcdemo;

import androidx.multidex.MultiDexApplication;

/**
 * 程序入口
 */
public class AliRtcApplication extends MultiDexApplication {

    private static AliRtcApplication sInstance;

    public static AliRtcApplication getInstance(){
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }
}
