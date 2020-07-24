package com.guider.health.apilib.model;

import java.io.Serializable;
import java.util.Date;

public class Stethoscope extends TEntityNoUpdate implements Serializable {
    /**
     * 用户账号id
     */
    long accountId;
    /**
     * 测量时间
     */
    Date testTime;
    /**
     * 测量模式
     */
    String measureMode;
    /**
     * 测量部位
     */
    String measurePart;
    /**
     * 测量点位
     */
    String measurePoint;
    /**
     * 前后部位
     */
    String aboutPart;
    /**
     * 音频文件路径
     */
    String audioUrl;

    /**
     * 设备mac地址
     */
    String deviceCode;

    public Stethoscope(){

    }

    public Stethoscope(long accountId, Date testTime, String measureMode, String measurePart,
                       String measurePoint, String aboutPart, String audioUrl, String deviceCode) {
        this.accountId = accountId;
        this.testTime = testTime;
        this.measureMode = measureMode;
        this.measurePart = measurePart;
        this.measurePoint = measurePoint;
        this.aboutPart = aboutPart;
        this.audioUrl = audioUrl;
        this.deviceCode = deviceCode;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public Date getTestTime() {
        return testTime;
    }

    public void setTestTime(Date testTime) {
        this.testTime = testTime;
    }

    public String getMeasureMode() {
        return measureMode;
    }

    public void setMeasureMode(String measureMode) {
        this.measureMode = measureMode;
    }

    public String getMeasurePart() {
        return measurePart;
    }

    public void setMeasurePart(String measurePart) {
        this.measurePart = measurePart;
    }

    public String getMeasurePoint() {
        return measurePoint;
    }

    public void setMeasurePoint(String measurePoint) {
        this.measurePoint = measurePoint;
    }

    public String getAboutPart() {
        return aboutPart;
    }

    public void setAboutPart(String aboutPart) {
        this.aboutPart = aboutPart;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }
}
