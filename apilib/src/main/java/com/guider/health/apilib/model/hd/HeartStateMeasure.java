package com.guider.health.apilib.model.hd;

/**
 * 心率入库
 */
public class HeartStateMeasure extends BaseMeasure {

    private String diaDescribes;

    private String healthLight;

    private String healthLightOriginal;

    private String heartRate;

    private String heartRateLight;

    private String lfhf;

    private String nn50;

    private String pervousSystemBalanceLight;

    private String nervousSystemBalanceLight;

    private String pnn50;

    private String predictedSymptoms;

    private String stressLight;

    private String sdnn;

    private String ecgImageUrl;

    public String getEcgImageUrl() {
        return ecgImageUrl;
    }

    public HeartStateMeasure setEcgImageUrl(String ecgImageUrl) {
        this.ecgImageUrl = ecgImageUrl;
        return this;
    }

    public String getDiaDescribes() {
        return diaDescribes;
    }

    public HeartStateMeasure setDiaDescribes(String diaDescribes) {
        this.diaDescribes = diaDescribes;
        return this;
    }

    public String getHealthLight() {
        return healthLight;
    }

    public HeartStateMeasure setHealthLight(String healthLight) {
        this.healthLight = healthLight;
        return this;
    }

    public String getHealthLightOriginal() {
        return healthLightOriginal;
    }

    public HeartStateMeasure setHealthLightOriginal(String healthLightOriginal) {
        this.healthLightOriginal = healthLightOriginal;
        return this;
    }

    public String getHeartRate() {
        return heartRate;
    }

    public HeartStateMeasure setHeartRate(String heartRate) {
        this.heartRate = heartRate;
        return this;
    }

    public String getHeartRateLight() {
        return heartRateLight;
    }

    public HeartStateMeasure setHeartRateLight(String heartRateLight) {
        this.heartRateLight = heartRateLight;
        return this;
    }

    public String getLfhf() {
        return lfhf;
    }

    public HeartStateMeasure setLfhf(String lfhf) {
        this.lfhf = lfhf;
        return this;
    }

    public String getNn50() {
        return nn50;
    }

    public HeartStateMeasure setNn50(String nn50) {
        this.nn50 = nn50;
        return this;
    }

    public String getPervousSystemBalanceLight() {
        return pervousSystemBalanceLight;
    }

    public HeartStateMeasure setPervousSystemBalanceLight(String pervousSystemBalanceLight) {
        this.pervousSystemBalanceLight = pervousSystemBalanceLight;
        return this;
    }

    public String getNervousSystemBalanceLight() {
        return nervousSystemBalanceLight;
    }

    public HeartStateMeasure setNervousSystemBalanceLight(String nervousSystemBalanceLight) {
        this.nervousSystemBalanceLight = nervousSystemBalanceLight;
        return this;
    }

    public String getPnn50() {
        return pnn50;
    }

    public HeartStateMeasure setPnn50(String pnn50) {
        this.pnn50 = pnn50;
        return this;
    }

    public String getPredictedSymptoms() {
        return predictedSymptoms;
    }

    public HeartStateMeasure setPredictedSymptoms(String predictedSymptoms) {
        this.predictedSymptoms = predictedSymptoms;
        return this;
    }

    public String getStressLight() {
        return stressLight;
    }

    public HeartStateMeasure setStressLight(String stressLight) {
        this.stressLight = stressLight;
        return this;
    }

    public String getSdnn() {
        return sdnn;
    }

    public HeartStateMeasure setSdnn(String sdnn) {
        this.sdnn = sdnn;
        return this;
    }
}
