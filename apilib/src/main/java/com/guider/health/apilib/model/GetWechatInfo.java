package com.guider.health.apilib.model;

public class GetWechatInfo {

    long doctorAccountId;
    String deviceCode;
    String sence;

    public long getDoctorAccountId() {
        return doctorAccountId;
    }

    public void setDoctorAccountId(long doctorAccountId) {
        this.doctorAccountId = doctorAccountId;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getSence() {
        return sence;
    }

    public void setSence(String sence) {
        this.sence = sence;
    }
}
