package com.guider.healthring.b30.bean;

public class HealthVo {
    /**
     * 用户ID
     */
    String userId;
    /**
     * 手环MAC地址
     */
    String deviceCode;
    /**
     * 心率
     */
    String heartRate;
    /**
     * 血氧
     */
    String bloodOxygen;
    /**
     * 低压
     */
    String systolic;
    /**
     * 高压
     */
    String diastolic;
    /**
     * 是否合格,1
     */
    String status;
    /**
     * yyyy-MM-dd HH:mm
     */
    String date;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(String heartRate) {
        this.heartRate = heartRate;
    }

    public String getBloodOxygen() {
        return bloodOxygen;
    }

    public void setBloodOxygen(String bloodOxygen) {
        this.bloodOxygen = bloodOxygen;
    }

    public String getSystolic() {
        return systolic;
    }

    public void setSystolic(String systolic) {
        this.systolic = systolic;
    }

    public String getDiastolic() {
        return diastolic;
    }

    public void setDiastolic(String diastolic) {
        this.diastolic = diastolic;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}