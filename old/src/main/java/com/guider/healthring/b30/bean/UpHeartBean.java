package com.guider.healthring.b30.bean;

public class UpHeartBean {

    /**
     * 用户ID
     */
    private String userId;
    /**
     * 手环MAC地址
     */
    private String deviceCode;
    /**
     * 心率
     */
    private String heartRate;
    /**
     * 血氧
     */
    private String bloodOxygen;
    /**
     * 低压
     */
    private String systolic;
    /**
     * 高压
     */
    private String diastolic;
    /**
     * 是否合格,1
     */
    private String status;
    /**
     * yyyy-MM-dd HH:mm
     */
    private String date;

    /**
     * 步数
     */
    private String stepNumber;

    public String getBloodOxygen() {
        return bloodOxygen;
    }

    public void setBloodOxygen(String bloodOxygen) {
        this.bloodOxygen = bloodOxygen;
    }

    public String getDiastolic() {
        return diastolic;
    }

    public void setDiastolic(String diastolic) {
        this.diastolic = diastolic;
    }

    public UpHeartBean(String userId, String deviceCode,
                       String systolic, String stepNumber,
                       String date, String heartRate,
                       String status,
                       String diastolic,
                       String bloodOxygen) {
        this.userId = userId;
        this.deviceCode = deviceCode;
        this.systolic = systolic;
        this.stepNumber = stepNumber;
        this.date = date;
        this.heartRate = heartRate;
        this.status = status;
        this.bloodOxygen = bloodOxygen;
        this.diastolic = diastolic;
    }

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

    public String getSystolic() {
        return systolic;
    }

    public void setSystolic(String systolic) {
        this.systolic = systolic;
    }

    public String getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(String stepNumber) {
        this.stepNumber = stepNumber;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(String heartRate) {
        this.heartRate = heartRate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "UpHeartBean{" +
                "userId='" + userId + '\'' +
                ", deviceCode='" + deviceCode + '\'' +
                ", heartRate='" + heartRate + '\'' +
                ", bloodOxygen='" + bloodOxygen + '\'' +
                ", systolic='" + systolic + '\'' +
                ", diastolic='" + diastolic + '\'' +
                ", status='" + status + '\'' +
                ", date='" + date + '\'' +
                ", stepNumber='" + stepNumber + '\'' +
                '}';
    }
}
