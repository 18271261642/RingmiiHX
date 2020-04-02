package com.guider.health.common.core;

public class ForaGlucose extends Glucose {
    private static Glucose mForaGlucose = new Glucose();

    public static Glucose getForaGluInstance() {
        return mForaGlucose;
    }
}
