package com.guider.health.apilib.model.hd;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 心率入库
 */
public class Heart12Measure extends BaseMeasure {

    private int leadNumber = 12;
    private int breathRate = 0;
    private String curveDescription = "I,II,III,avR,avL,avF,v1,v2,v3,v4,v5,v6";
    private int gain = 1;
    private List<int[]> ecgData;
    private String imgUrl;
    private int baseLineValue = 0;
    private int samplingFrequency = 500;
    private int avm = 1000;
    private String heartRate;
    private String analysisResults;
    private String analysisList;
    private String paxis;
    private String prInterval;
    private String qrsAxis;
    private String qrsDuration;
    private String qtc;
    private String qtd;
    private String rrInterval;
    private String rv5;
    private String sv1;
    private String taxis;
    private int mask = 65536;

    private String customAnalysis;

    private int customType;

    public String getCustomAnalysis() {
        return customAnalysis;
    }

    public Heart12Measure setCustomAnalysis(String customAnalysis) {
        this.customAnalysis = customAnalysis;
        return this;
    }

    public int getCustomType() {
        return customType;
    }

    public Heart12Measure setCustomType(int customType) {
        this.customType = customType;
        return this;
    }


    public String getAnalysisList() {
        return analysisList;
    }

    public Heart12Measure setAnalysisList(Set<String> list) {
        if (list == null || list.size() <= 0) {
            return this;
        }
        StringBuffer re = new StringBuffer();
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            if (iterator.hasNext()) {
                re.append(next).append(",");
            } else {
                re.append(next);
            }
        }
        analysisList = re.toString();
        return this;
    }

    public int getLeadNumber() {
        return leadNumber;
    }

    public Heart12Measure setLeadNumber(int leadNumber) {
        this.leadNumber = leadNumber;
        return this;
    }

    public int getBreathRate() {
        return breathRate;
    }

    public Heart12Measure setBreathRate(int breathRate) {
        this.breathRate = breathRate;
        return this;
    }

    public String getCurveDescription() {
        return curveDescription;
    }

    public Heart12Measure setCurveDescription(String curveDescription) {
        this.curveDescription = curveDescription;
        return this;
    }

    public int getGain() {
        return gain;
    }

    public Heart12Measure setGain(int gain) {
        this.gain = gain;
        return this;
    }

    public List<int[]> getEcgData() {
        return ecgData;
    }

    public Heart12Measure setEcgData(List<int[]> ecgData) {
        this.ecgData = ecgData;
        return this;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public Heart12Measure setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
        return this;
    }

    public int getBaseLineValue() {
        return baseLineValue;
    }

    public Heart12Measure setBaseLineValue(int baseLineValue) {
        this.baseLineValue = baseLineValue;
        return this;
    }

    public int getSamplingFrequency() {
        return samplingFrequency;
    }

    public Heart12Measure setSamplingFrequency(int samplingFrequency) {
        this.samplingFrequency = samplingFrequency;
        return this;
    }

    public int getAvm() {
        return avm;
    }

    public Heart12Measure setAvm(int avm) {
        this.avm = avm;
        return this;
    }

    public String getHeartRate() {
        return heartRate;
    }

    public Heart12Measure setHeartRate(String heartRate) {
        this.heartRate = heartRate;
        return this;
    }

    public String getAnalysisResults() {
        return analysisResults;
    }

    public Heart12Measure setAnalysisResults(String analysisResults) {
        this.analysisResults = analysisResults;
        return this;
    }

    public String getPaxis() {
        return paxis;
    }

    public Heart12Measure setPaxis(String paxis) {
        this.paxis = paxis;
        return this;
    }

    public String getPrInterval() {
        return prInterval;
    }

    public Heart12Measure setPrInterval(String prInterval) {
        this.prInterval = prInterval;
        return this;
    }

    public String getQrsAxis() {
        return qrsAxis;
    }

    public Heart12Measure setQrsAxis(String qrsAxis) {
        this.qrsAxis = qrsAxis;
        return this;
    }

    public String getQrsDuration() {
        return qrsDuration;
    }

    public Heart12Measure setQrsDuration(String qrsDuration) {
        this.qrsDuration = qrsDuration;
        return this;
    }

    public String getQtc() {
        return qtc;
    }

    public Heart12Measure setQtc(String qtc) {
        this.qtc = qtc;
        return this;
    }

    public String getQtd() {
        return qtd;
    }

    public Heart12Measure setQtd(String qtd) {
        this.qtd = qtd;
        return this;
    }

    public String getRrInterval() {
        return rrInterval;
    }

    public Heart12Measure setRrInterval(String rrInterval) {
        this.rrInterval = rrInterval;
        return this;
    }

    public String getRv5() {
        return rv5;
    }

    public Heart12Measure setRv5(String rv5) {
        this.rv5 = rv5;
        return this;
    }

    public String getSv1() {
        return sv1;
    }

    public Heart12Measure setSv1(String sv1) {
        this.sv1 = sv1;
        return this;
    }

    public String getTaxis() {
        return taxis;
    }

    public Heart12Measure setTaxis(String taxis) {
        this.taxis = taxis;
        return this;
    }

    public int getMask() {
        return mask;
    }

    public Heart12Measure setMask(int mask) {
        this.mask = mask;
        return this;
    }
}
