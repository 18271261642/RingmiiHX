package com.guider.health.apilib.model.hd;

public class BloodsugarMeasure extends BaseMeasure{

    private float bs; // 血糖
    private float hemoglobin; // 血红蛋白
    private float bloodSpeed; // 血流速
    private String bsTime;    // 测量时机 空腹 / 饭后


    public float getBs() {
        return bs;
    }

    public BloodsugarMeasure setBs(float bs) {
        this.bs = bs;
        return this;
    }

    public float getHemoglobin() {
        return hemoglobin;
    }

    public BloodsugarMeasure setHemoglobin(float hemoglobin) {
        this.hemoglobin = hemoglobin;
        return this;
    }

    public float getBloodSpeed() {
        return bloodSpeed;
    }

    public BloodsugarMeasure setBloodSpeed(float bloodSpeed) {
        this.bloodSpeed = bloodSpeed;
        return this;
    }

    public String getBsTime() {
        return bsTime;
    }

    public BloodsugarMeasure setBsTime(String bsTime) {
        this.bsTime = bsTime;
        return this;
    }
}
