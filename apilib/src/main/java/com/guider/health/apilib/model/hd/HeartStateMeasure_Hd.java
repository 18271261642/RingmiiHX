package com.guider.health.apilib.model.hd;

/**
 * 心率入库
 */
public class HeartStateMeasure_Hd{

    private int HeartRate;//平均心率
    private int emotionIndex;
    private int emotionScore;
    private int stressIndex;
    private int stressScore;
    private int alcholRiskIndex;
    private int alcholRiskScore;
    private double bodyFatRatio;
    private int bodyFatIndex;
    private int prematureBeat;
    private int atrialFibrillation;
    private double RRpercent;

    public int getHeartRate() {
        return HeartRate;
    }

    public HeartStateMeasure_Hd setHeartRate(int heartRate) {
        HeartRate = heartRate;
        return this;
    }

    public int getEmotionIndex() {
        return emotionIndex;
    }

    public HeartStateMeasure_Hd setEmotionIndex(int emotionIndex) {
        this.emotionIndex = emotionIndex;
        return this;
    }

    public int getEmotionScore() {
        return emotionScore;
    }

    public HeartStateMeasure_Hd setEmotionScore(int emotionScore) {
        this.emotionScore = emotionScore;
        return this;
    }

    public int getStressIndex() {
        return stressIndex;
    }

    public HeartStateMeasure_Hd setStressIndex(int stressIndex) {
        this.stressIndex = stressIndex;
        return this;
    }

    public int getStressScore() {
        return stressScore;
    }

    public HeartStateMeasure_Hd setStressScore(int stressScore) {
        this.stressScore = stressScore;
        return this;
    }

    public int getAlcholRiskIndex() {
        return alcholRiskIndex;
    }

    public HeartStateMeasure_Hd setAlcholRiskIndex(int alcholRiskIndex) {
        this.alcholRiskIndex = alcholRiskIndex;
        return this;
    }

    public int getAlcholRiskScore() {
        return alcholRiskScore;
    }

    public HeartStateMeasure_Hd setAlcholRiskScore(int alcholRiskScore) {
        this.alcholRiskScore = alcholRiskScore;
        return this;
    }

    public double getBodyFatRatio() {
        return bodyFatRatio;
    }

    public HeartStateMeasure_Hd setBodyFatRatio(double bodyFatRatio) {
        this.bodyFatRatio = bodyFatRatio;
        return this;
    }

    public int getBodyFatIndex() {
        return bodyFatIndex;
    }

    public HeartStateMeasure_Hd setBodyFatIndex(int bodyFatIndex) {
        this.bodyFatIndex = bodyFatIndex;
        return this;
    }

    public int getPrematureBeat() {
        return prematureBeat;
    }

    public HeartStateMeasure_Hd setPrematureBeat(int prematureBeat) {
        this.prematureBeat = prematureBeat;
        return this;
    }

    public int getAtrialFibrillation() {
        return atrialFibrillation;
    }

    public HeartStateMeasure_Hd setAtrialFibrillation(int atrialFibrillation) {
        this.atrialFibrillation = atrialFibrillation;
        return this;
    }

    public double getRRpercent() {
        return RRpercent;
    }

    public HeartStateMeasure_Hd setRRpercent(double RRpercent) {
        this.RRpercent = RRpercent;
        return this;
    }
}
