package com.guider.health.apilib.model;

import com.guider.health.apilib.enums.BSTime;

import java.io.Serializable;
import java.util.Date;

/**
 * 血糖实体类
 */
public class BloodSugar extends TEntityHealth implements Serializable {
    /**
     * 用户id
     */
    long accountId;
    /**
     * 血糖测量时间段
     */
    BSTime bsTime;
    /**
     * 血值
     */
    float bs;
    Date testTime;
    String deviceCode;
    /**
     * 状态
     */
    String state;

    /**
     * 肱动脉血流速度
     */
    int bloodSpeed;
    /**
     * 血红蛋白
     */
    float hemoglobin;

    public BloodSugar(long accountId, BSTime bsTime, float bs, Date testTime,
                      String deviceCode, String state, int bloodSpeed, float hemoglobin) {
        this.accountId = accountId;
        this.bsTime = bsTime;
        this.bs = bs;
        this.testTime = testTime;
        this.deviceCode = deviceCode;
        this.state = state;
        this.bloodSpeed = bloodSpeed;
        this.hemoglobin = hemoglobin;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public BSTime getBsTime() {
        return bsTime;
    }

    public void setBsTime(BSTime bsTime) {
        this.bsTime = bsTime;
    }

    public float getBs() {
        return bs;
    }

    public void setBs(float bs) {
        this.bs = bs;
    }

    public Date getTestTime() {
        return testTime;
    }

    public void setTestTime(Date testTime) {
        this.testTime = testTime;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getBloodSpeed() {
        return bloodSpeed;
    }

    public void setBloodSpeed(int bloodSpeed) {
        this.bloodSpeed = bloodSpeed;
    }

    public float getHemoglobin() {
        return hemoglobin;
    }

    public void setHemoglobin(float hemoglobin) {
        this.hemoglobin = hemoglobin;
    }
}
