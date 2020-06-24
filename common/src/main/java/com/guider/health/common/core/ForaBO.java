package com.guider.health.common.core;

import android.os.Parcel;

// TODO 新设备福尔血氧数据实体
public class ForaBO extends BloodOxygen {
    private static ForaBO mForaBO = new ForaBO();

    public ForaBO() {
    }

    protected ForaBO(Parcel in) {
        super(in);
    }

    public static ForaBO getForaBOInstance() {
        return mForaBO;
    }
}
