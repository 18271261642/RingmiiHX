package com.guider.health.apilib.model.hd;

import java.util.Date;

public class EcgRecorderData {


    /**
     * id : 686
     * createTime : null
     * normal : true
     * accountId : 258
     * heartRate : 84
     * heartRateLight : null
     * healthLight : null
     * healthLightOriginal : null
     * diaDescribes : null
     * stressLight : null
     * nervousSystemBalanceLight : null
     * predictedSymptoms : null
     * deviceCode : null
     * state : 正常,正常,尚可,良好
     * state2 : 正常,正常,偏高,正常
     * testTime : 2019-10-24T22:58:11Z
     * ecgImageUrl : null
     * deviceTypeName : M500
     * name : 刘军  中关村物联网产业联盟
     * nn50 : 0
     * sdnn : 0
     * lfhf : 0
     * pnn50 : 0
     */

    private int id;
    private String createTime;
    private boolean normal;
    private int accountId;
    private double heartRate;
    private String heartRateLight;
    private String healthLight;
    private String healthLightOriginal;
    private String diaDescribes;
    private String stressLight;
    private String nervousSystemBalanceLight;
    private String predictedSymptoms;
    private String deviceCode;
    private String state;
    private String state2;
    private Date testTime;
    private String ecgImageUrl;
    private String deviceTypeName;
    private String name;
    private int nn50;
    private int sdnn;
    private int lfhf;
    private int pnn50;

    public int getId() {
        return id;
    }

    public EcgRecorderData setId(int id) {
        this.id = id;
        return this;
    }

    public String getCreateTime() {
        return createTime;
    }

    public EcgRecorderData setCreateTime(String createTime) {
        this.createTime = createTime;
        return this;
    }

    public boolean isNormal() {
        return normal;
    }

    public EcgRecorderData setNormal(boolean normal) {
        this.normal = normal;
        return this;
    }

    public int getAccountId() {
        return accountId;
    }

    public EcgRecorderData setAccountId(int accountId) {
        this.accountId = accountId;
        return this;
    }

    public int getHeartRate() {
        return (int) heartRate;
    }

    public EcgRecorderData setHeartRate(int heartRate) {
        this.heartRate = heartRate;
        return this;
    }

    public String getHeartRateLight() {
        return heartRateLight;
    }

    public EcgRecorderData setHeartRateLight(String heartRateLight) {
        this.heartRateLight = heartRateLight;
        return this;
    }

    public String getHealthLight() {
        return healthLight;
    }

    public EcgRecorderData setHealthLight(String healthLight) {
        this.healthLight = healthLight;
        return this;
    }

    public String getHealthLightOriginal() {
        return healthLightOriginal;
    }

    public EcgRecorderData setHealthLightOriginal(String healthLightOriginal) {
        this.healthLightOriginal = healthLightOriginal;
        return this;
    }

    public String getDiaDescribes() {
        return diaDescribes;
    }

    public EcgRecorderData setDiaDescribes(String diaDescribes) {
        this.diaDescribes = diaDescribes;
        return this;
    }

    public String getStressLight() {
        return stressLight;
    }

    public EcgRecorderData setStressLight(String stressLight) {
        this.stressLight = stressLight;
        return this;
    }

    public String getNervousSystemBalanceLight() {
        return nervousSystemBalanceLight;
    }

    public EcgRecorderData setNervousSystemBalanceLight(String nervousSystemBalanceLight) {
        this.nervousSystemBalanceLight = nervousSystemBalanceLight;
        return this;
    }

    public String getPredictedSymptoms() {
        return predictedSymptoms;
    }

    public EcgRecorderData setPredictedSymptoms(String predictedSymptoms) {
        this.predictedSymptoms = predictedSymptoms;
        return this;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public EcgRecorderData setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
        return this;
    }

    public String getState() {
        return state;
    }

    public EcgRecorderData setState(String state) {
        this.state = state;
        return this;
    }

    public String getState2() {
        return state2;
    }

    public EcgRecorderData setState2(String state2) {
        this.state2 = state2;
        return this;
    }

    public Date getTestTime() {
        return testTime;
    }

    public EcgRecorderData setTestTime(Date testTime) {
        this.testTime = testTime;
        return this;
    }

    public String getEcgImageUrl() {
        return ecgImageUrl;
    }

    public EcgRecorderData setEcgImageUrl(String ecgImageUrl) {
        this.ecgImageUrl = ecgImageUrl;
        return this;
    }

    public String getDeviceTypeName() {
        return deviceTypeName;
    }

    public EcgRecorderData setDeviceTypeName(String deviceTypeName) {
        this.deviceTypeName = deviceTypeName;
        return this;
    }

    public String getName() {
        return name;
    }

    public EcgRecorderData setName(String name) {
        this.name = name;
        return this;
    }

    public int getNn50() {
        return nn50;
    }

    public EcgRecorderData setNn50(int nn50) {
        this.nn50 = nn50;
        return this;
    }

    public int getSdnn() {
        return sdnn;
    }

    public EcgRecorderData setSdnn(int sdnn) {
        this.sdnn = sdnn;
        return this;
    }

    public int getLfhf() {
        return lfhf;
    }

    public EcgRecorderData setLfhf(int lfhf) {
        this.lfhf = lfhf;
        return this;
    }

    public int getPnn50() {
        return pnn50;
    }

    public EcgRecorderData setPnn50(int pnn50) {
        this.pnn50 = pnn50;
        return this;
    }
}
