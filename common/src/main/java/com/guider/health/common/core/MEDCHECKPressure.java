package com.guider.health.common.core;

import android.os.Parcel;

// TODO 新设备MEDCHECK血压数据实体
public class MEDCHECKPressure extends HeartPressBp {
    private static final MEDCHECKPressure mMEDCheckPressure = new MEDCHECKPressure();

    private MEDCHECKPressure() {
    }

    protected MEDCHECKPressure(Parcel in) {
        super(in);
    }

    public static MEDCHECKPressure getMEDCHECKPressureInstance() {
        return mMEDCheckPressure;
    }
}
