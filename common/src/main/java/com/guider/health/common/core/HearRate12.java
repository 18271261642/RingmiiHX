package com.guider.health.common.core;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.List;
import java.util.Set;

/**
 * Created by haix on 2019/6/25.
 */

public class HearRate12 implements Parcelable {

    private static HearRate12 hearRate = new HearRate12();
    private HearRate12(){
    }

    public static HearRate12 getInstance(){
        return hearRate;
    }

    private boolean tag;
    private String deviceAddress;
    private String HeartRate;           // 心率 analyses.put("HeartRate", String.valueOf(hr));
    private String analysisResults;     // 诊断结论
    private Set<String> analysisList;   // 症状
    private String createTime;
    private String testTime;
    private String avm;
    private String baseLineValue;
    private String breathRate;
    private String curveDescription;
    private String gain;
    private String heartRate;
    private String imgUrl;
    private String leadNumber;
    private String mask;
    private String paxis;                  // analyses.put("PAxis", String.valueOf(axis[0]));
    private String prInterval;             // analyses.put("PRInterval", String.valueOf(pr));
    private String qrsAxis;                // analyses.put("QRSAxis", String.valueOf(axis[1]));
    private String qrsDuration;            // analyses.put("QRSDuration", String.valueOf(qrs));
    private String qtc;                    // analyses.put("QTC", String.valueOf(qtc));
    private String qtd;                    // analyses.put("QTD", String.valueOf(qt));
    private String rrInterval;             // analyses.put("RRInterval", String.valueOf(rr));
    private String rv5;                    // analyses.put("RV5", String.valueOf(rv5));
    private String samplingFrequency;
    private String sv1;                    // analyses.put("SV1", String.valueOf(sv1));
    private String taxis;                  // analyses.put("TAxis", String.valueOf(axis[2]));
    private String indexHealth;
    private String indexPress;
    private String indexTired;
    private String normal;                  // 是否正常

    private int _HeartRate;
    /**
     *
     *
     *
     *
     *
     *
     *
     * 			analyses.put("RV5SV1", String.valueOf((Math.abs(rv5)+Math.abs(sv1))));
     *
     *
     *
     */


    public boolean isTag() {
        return tag;
    }

    public HearRate12 setTag(boolean tag) {
        this.tag = tag;
        return this;
    }

    public int get_HeartRate() {
        return _HeartRate;
    }

    public HearRate12 set_HeartRate(int _HeartRate) {
        this._HeartRate = _HeartRate;
        return this;
    }

    public String getNormal() {
        return normal;
    }

    public HearRate12 setNormal(String normal) {
        this.normal = normal;
        return this;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public HearRate12 setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
        return this;
    }

    public String getHeartRate() {
        return HeartRate;
    }

    public HearRate12 setHeartRate(String heartRate) {
        HeartRate = heartRate;
        return this;
    }

    public Set<String> getAnalysisList() {
        return analysisList;
    }

    public HearRate12 setAnalysisList(Set<String> analysisList) {
        this.analysisList = analysisList;
        return this;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public HearRate12 setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
        return this;
    }

    public String getLeadNumber() {
        return leadNumber;
    }

    public HearRate12 setLeadNumber(String leadNumber) {
        this.leadNumber = leadNumber;
        return this;
    }

    public String getIndexHealth() {
        return indexHealth;
    }

    public HearRate12 setIndexHealth(String indexHealth) {
        this.indexHealth = indexHealth;
        return this;
    }

    public String getIndexPress() {
        return indexPress;
    }

    public HearRate12 setIndexPress(String indexPress) {
        this.indexPress = indexPress;
        return this;
    }

    public String getIndexTired() {
        return indexTired;
    }

    public HearRate12 setIndexTired(String indexTired) {
        this.indexTired = indexTired;
        return this;
    }

    public String getMask() {
        return mask;
    }

    public HearRate12 setMask(String mask) {
        this.mask = mask;
        return this;
    }

    public String getPaxis() {
        return paxis;
    }

    public HearRate12 setPaxis(String paxis) {
        this.paxis = paxis;
        return this;
    }

    public String getPrInterval() {
        return prInterval;
    }

    public HearRate12 setPrInterval(String prInterval) {
        this.prInterval = prInterval;
        return this;
    }

    public String getQrsAxis() {
        return qrsAxis;
    }

    public HearRate12 setQrsAxis(String qrsAxis) {
        this.qrsAxis = qrsAxis;
        return this;
    }

    public String getQrsDuration() {
        return qrsDuration;
    }

    public HearRate12 setQrsDuration(String qrsDuration) {
        this.qrsDuration = qrsDuration;
        return this;
    }

    public String getQtc() {
        return qtc;
    }

    public HearRate12 setQtc(String qtc) {
        this.qtc = qtc;
        return this;
    }

    public String getQtd() {
        return qtd;
    }

    public HearRate12 setQtd(String qtd) {
        this.qtd = qtd;
        return this;
    }

    public String getRrInterval() {
        return rrInterval;
    }

    public HearRate12 setRrInterval(String rrInterval) {
        this.rrInterval = rrInterval;
        return this;
    }

    public String getRv5() {
        return rv5;
    }

    public HearRate12 setRv5(String rv5) {
        this.rv5 = rv5;
        return this;
    }

    public String getSamplingFrequency() {
        return samplingFrequency;
    }

    public HearRate12 setSamplingFrequency(String samplingFrequency) {
        this.samplingFrequency = samplingFrequency;
        return this;
    }

    public String getSv1() {
        return sv1;
    }

    public HearRate12 setSv1(String sv1) {
        this.sv1 = sv1;
        return this;
    }

    public String getTaxis() {
        return taxis;
    }

    public HearRate12 setTaxis(String taxis) {
        this.taxis = taxis;
        return this;
    }

    public String getAnalysisResults() {
        return analysisResults;
    }

    public HearRate12 setAnalysisResults(String analysisResults) {
        this.analysisResults = analysisResults;
        if (!TextUtils.isEmpty(this.analysisResults)) {
            this.analysisResults = this.analysisResults.replace("*", "");
        }
        return this;
    }

    public String getCreateTime() {
        return createTime;
    }

    public HearRate12 setCreateTime(String createTime) {
        this.createTime = createTime;
        return this;
    }

    public String getTestTime() {
        return testTime;
    }

    public HearRate12 setTestTime(String testTime) {
        this.testTime = testTime;
        return this;
    }

    public String getAvm() {
        return avm;
    }

    public HearRate12 setAvm(String avm) {
        this.avm = avm;
        return this;
    }

    public String getBaseLineValue() {
        return baseLineValue;
    }

    public HearRate12 setBaseLineValue(String baseLineValue) {
        this.baseLineValue = baseLineValue;
        return this;
    }

    public String getBreathRate() {
        return breathRate;
    }

    public HearRate12 setBreathRate(String breathRate) {
        this.breathRate = breathRate;
        return this;
    }

    public String getCurveDescription() {
        return curveDescription;
    }

    public HearRate12 setCurveDescription(String curveDescription) {
        this.curveDescription = curveDescription;
        return this;
    }

    public String getGain() {
        return gain;
    }

    public HearRate12 setGain(String gain) {
        this.gain = gain;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.tag ? (byte) 1 : (byte) 0);
        dest.writeString(this.deviceAddress);
        dest.writeString(this.HeartRate);
        dest.writeString(this.analysisResults);
        dest.writeString(this.createTime);
        dest.writeString(this.testTime);
        dest.writeString(this.avm);
        dest.writeString(this.baseLineValue);
        dest.writeString(this.breathRate);
        dest.writeString(this.curveDescription);
        dest.writeString(this.gain);
        dest.writeString(this.heartRate);
        dest.writeString(this.imgUrl);
        dest.writeString(this.leadNumber);
        dest.writeString(this.mask);
        dest.writeString(this.paxis);
        dest.writeString(this.prInterval);
        dest.writeString(this.qrsAxis);
        dest.writeString(this.qrsDuration);
        dest.writeString(this.qtc);
        dest.writeString(this.qtd);
        dest.writeString(this.rrInterval);
        dest.writeString(this.rv5);
        dest.writeString(this.samplingFrequency);
        dest.writeString(this.sv1);
        dest.writeString(this.taxis);
        dest.writeString(this.indexHealth);
        dest.writeString(this.indexPress);
        dest.writeString(this.indexTired);
        dest.writeString(this.normal);
    }

    protected HearRate12(Parcel in) {
        this.tag = in.readByte() != 0;
        this.deviceAddress = in.readString();
        this.HeartRate = in.readString();
        this.analysisResults = in.readString();
        this.createTime = in.readString();
        this.testTime = in.readString();
        this.avm = in.readString();
        this.baseLineValue = in.readString();
        this.breathRate = in.readString();
        this.curveDescription = in.readString();
        this.gain = in.readString();
        this.heartRate = in.readString();
        this.imgUrl = in.readString();
        this.leadNumber = in.readString();
        this.mask = in.readString();
        this.paxis = in.readString();
        this.prInterval = in.readString();
        this.qrsAxis = in.readString();
        this.qrsDuration = in.readString();
        this.qtc = in.readString();
        this.qtd = in.readString();
        this.rrInterval = in.readString();
        this.rv5 = in.readString();
        this.samplingFrequency = in.readString();
        this.sv1 = in.readString();
        this.taxis = in.readString();
        this.indexHealth = in.readString();
        this.indexPress = in.readString();
        this.indexTired = in.readString();
        this.normal = in.readString();
    }

    public static final Creator<HearRate12> CREATOR = new Creator<HearRate12>() {
        @Override
        public HearRate12 createFromParcel(Parcel source) {
            return new HearRate12(source);
        }

        @Override
        public HearRate12[] newArray(int size) {
            return new HearRate12[size];
        }
    };

    List<int[]> ecgData;
    public void setEcgData(List<int[]> ecgDataBuffer) {
        ecgData = ecgDataBuffer;
    }

    public List<int[]> getEcgData() {
        return ecgData;
    }
}
