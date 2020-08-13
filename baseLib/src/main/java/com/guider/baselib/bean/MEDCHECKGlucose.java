package com.guider.baselib.bean;

// TODO 新设备MEDCHECK血糖数据实体
public class MEDCHECKGlucose extends Glucose {
    private static Glucose mForaGlucose = new Glucose();

    public static Glucose getMEDCHECKGluInstance() {
        return mForaGlucose;
    }
}
