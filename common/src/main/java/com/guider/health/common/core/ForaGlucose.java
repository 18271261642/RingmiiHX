package com.guider.health.common.core;

// TODO 新设备福尔血糖数据实体
public class ForaGlucose extends Glucose {
    private static Glucose mForaGlucose = new Glucose();

    public static Glucose getForaGluInstance() {
        return mForaGlucose;
    }
}
