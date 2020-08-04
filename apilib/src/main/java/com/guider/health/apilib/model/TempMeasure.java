package com.guider.health.apilib.model;

import com.guider.health.apilib.model.hd.BaseMeasure;

/**
 * 体温
 */
public class TempMeasure extends BaseMeasure {
    private float temp;

    public float getTemp() {
        return temp;
    }

    public TempMeasure setTemp(float temp) {
        this.temp = temp;
        return this;
    }
}
