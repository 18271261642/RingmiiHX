package com.guider.health.apilib.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 心电实体类
 */
public class Ecg extends TEntityHealth implements Serializable {
    /**
     * 用户id
     */
    long accountId;
    Date testTime;
    String deviceCode;
    /**
     * 心率
     */
    int heartRate;
    /**
     * 呼吸率
     */
    int breathRate;
    /**
     * PR间期
     */
    int prInterval;
    /**
     * RR间期
     */
    int rrInterval;
    /**
     * QRS间期
     */
    int qrsDuration;
    /**
     * QT间期
     */
    int qtd;
    /**
     * QTC间期
     */
    int qtc;
    /**
     * RV5幅值
     */
    double rv5;
    /**
     * SV1幅值
     */
    double sv1;
    /**
     * p轴
     */
    int pAxis;
    /**
     * t轴
     */
    int tAxis;
    /**
     * QRS轴
     */
    int qrsAxis;
    /**
     * 分析结果
     */
    String analysisResults;
    /**
     * 导联数
     */
    int leadNumber;
    /**
     * 导联名称描述
     */
    String curveDescription;
    /**
     * 采样频率
     */
    int samplingFrequency;
    /**
     * 幅值单位
     */
    int avm;
    /**
     * 基准值
     */
    int baseLineValue;
    /**
     * 增益
     */
    double gain;
    /**
     * 心电图单点纵坐标掩码（默认值：Ox0FFF）
     */
    int mask;
    /**
     * 图片路径
     */
    String imgUrl;

    public Ecg(long accountId, Date testTime, String deviceCode, int heartRate, int breathRate,
               int prInterval, int rrInterval, int qrsDuration, int qtd, int qtc, double rv5,
               double sv1, int pAxis, int tAxis, int qrsAxis, String analysisResults,
               int leadNumber, String curveDescription, int samplingFrequency, int avm,
               int baseLineValue, double gain, int mask, String imgUrl) {
        this.accountId = accountId;
        this.testTime = testTime;
        this.deviceCode = deviceCode;
        this.heartRate = heartRate;
        this.breathRate = breathRate;
        this.prInterval = prInterval;
        this.rrInterval = rrInterval;
        this.qrsDuration = qrsDuration;
        this.qtd = qtd;
        this.qtc = qtc;
        this.rv5 = rv5;
        this.sv1 = sv1;
        this.pAxis = pAxis;
        this.tAxis = tAxis;
        this.qrsAxis = qrsAxis;
        this.analysisResults = analysisResults;
        this.leadNumber = leadNumber;
        this.curveDescription = curveDescription;
        this.samplingFrequency = samplingFrequency;
        this.avm = avm;
        this.baseLineValue = baseLineValue;
        this.gain = gain;
        this.mask = mask;
        this.imgUrl = imgUrl;
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

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public int getBreathRate() {
        return breathRate;
    }

    public void setBreathRate(int breathRate) {
        this.breathRate = breathRate;
    }

    public int getPrInterval() {
        return prInterval;
    }

    public void setPrInterval(int prInterval) {
        this.prInterval = prInterval;
    }

    public int getRrInterval() {
        return rrInterval;
    }

    public void setRrInterval(int rrInterval) {
        this.rrInterval = rrInterval;
    }

    public int getQrsDuration() {
        return qrsDuration;
    }

    public void setQrsDuration(int qrsDuration) {
        this.qrsDuration = qrsDuration;
    }

    public int getQtd() {
        return qtd;
    }

    public void setQtd(int qtd) {
        this.qtd = qtd;
    }

    public int getQtc() {
        return qtc;
    }

    public void setQtc(int qtc) {
        this.qtc = qtc;
    }

    public double getRv5() {
        return rv5;
    }

    public void setRv5(double rv5) {
        this.rv5 = rv5;
    }

    public double getSv1() {
        return sv1;
    }

    public void setSv1(double sv1) {
        this.sv1 = sv1;
    }

    public int getpAxis() {
        return pAxis;
    }

    public void setpAxis(int pAxis) {
        this.pAxis = pAxis;
    }

    public int gettAxis() {
        return tAxis;
    }

    public void settAxis(int tAxis) {
        this.tAxis = tAxis;
    }

    public int getQrsAxis() {
        return qrsAxis;
    }

    public void setQrsAxis(int qrsAxis) {
        this.qrsAxis = qrsAxis;
    }

    public String getAnalysisResults() {
        return analysisResults;
    }

    public void setAnalysisResults(String analysisResults) {
        this.analysisResults = analysisResults;
    }

    public int getLeadNumber() {
        return leadNumber;
    }

    public void setLeadNumber(int leadNumber) {
        this.leadNumber = leadNumber;
    }

    public String getCurveDescription() {
        return curveDescription;
    }

    public void setCurveDescription(String curveDescription) {
        this.curveDescription = curveDescription;
    }

    public int getSamplingFrequency() {
        return samplingFrequency;
    }

    public void setSamplingFrequency(int samplingFrequency) {
        this.samplingFrequency = samplingFrequency;
    }

    public int getAvm() {
        return avm;
    }

    public void setAvm(int avm) {
        this.avm = avm;
    }

    public int getBaseLineValue() {
        return baseLineValue;
    }

    public void setBaseLineValue(int baseLineValue) {
        this.baseLineValue = baseLineValue;
    }

    public double getGain() {
        return gain;
    }

    public void setGain(double gain) {
        this.gain = gain;
    }

    public int getMask() {
        return mask;
    }

    public void setMask(int mask) {
        this.mask = mask;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
