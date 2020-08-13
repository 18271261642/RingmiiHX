package com.guider.baselib.bean;

import android.os.Parcel;

// TODO 新设备福尔耳温数据实体
public class ForaET extends Temp {
    private static ForaET mForaET = new ForaET();

    public ForaET() {
    }

    protected ForaET(Parcel in) {
        super(in);
    }

    public static ForaET getForaETInstance() {
        return mForaET;
    }
}
