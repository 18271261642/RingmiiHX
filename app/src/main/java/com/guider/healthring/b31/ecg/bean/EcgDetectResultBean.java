package com.guider.healthring.b31.ecg.bean;

import com.veepoo.protocol.model.datas.TimeData;
import com.veepoo.protocol.model.enums.EECGResultType;

/**
 * Created by Admin
 * Date 2021/5/20
 */
public class EcgDetectResultBean {

    private boolean isSuccess;
    private EECGResultType type;
    private TimeData timeBean;
    private int frequency;
    private int drawfrequency;
    private int duration;
    private int leadSign;
    private int[] originSign;
    private int[] filterSignals;
    private int[] result8;
    private int[] diseaseResult;
    private int aveHeart;
    private int aveResRate;
    private int aveHrv;
    private int aveQT;
    private int progress;

    public EcgDetectResultBean() {
    }

    public EcgDetectResultBean(boolean isSuccess, EECGResultType type, TimeData timeBean, int frequency,
                               int drawfrequency, int duration, int leadSign, int[] originSign, int[] filterSignals,
                               int[] result8, int[] diseaseResult, int aveHeart, int aveResRate, int aveHrv, int aveQT,
                               int progress) {
        this.isSuccess = isSuccess;
        this.type = type;
        this.timeBean = timeBean;
        this.frequency = frequency;
        this.drawfrequency = drawfrequency;
        this.duration = duration;
        this.leadSign = leadSign;
        this.originSign = originSign;
        this.filterSignals = filterSignals;
        this.result8 = result8;
        this.diseaseResult = diseaseResult;
        this.aveHeart = aveHeart;
        this.aveResRate = aveResRate;
        this.aveHrv = aveHrv;
        this.aveQT = aveQT;
        this.progress = progress;
    }


    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public EECGResultType getType() {
        return type;
    }

    public void setType(EECGResultType type) {
        this.type = type;
    }

    public TimeData getTimeBean() {
        return timeBean;
    }

    public void setTimeBean(TimeData timeBean) {
        this.timeBean = timeBean;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getDrawfrequency() {
        return drawfrequency;
    }

    public void setDrawfrequency(int drawfrequency) {
        this.drawfrequency = drawfrequency;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getLeadSign() {
        return leadSign;
    }

    public void setLeadSign(int leadSign) {
        this.leadSign = leadSign;
    }

    public int[] getOriginSign() {
        return originSign;
    }

    public void setOriginSign(int[] originSign) {
        this.originSign = originSign;
    }

    public int[] getFilterSignals() {
        return filterSignals;
    }

    public void setFilterSignals(int[] filterSignals) {
        this.filterSignals = filterSignals;
    }

    public int[] getResult8() {
        return result8;
    }

    public void setResult8(int[] result8) {
        this.result8 = result8;
    }

    public int[] getDiseaseResult() {
        return diseaseResult;
    }

    public void setDiseaseResult(int[] diseaseResult) {
        this.diseaseResult = diseaseResult;
    }

    public int getAveHeart() {
        return aveHeart;
    }

    public void setAveHeart(int aveHeart) {
        this.aveHeart = aveHeart;
    }

    public int getAveResRate() {
        return aveResRate;
    }

    public void setAveResRate(int aveResRate) {
        this.aveResRate = aveResRate;
    }

    public int getAveHrv() {
        return aveHrv;
    }

    public void setAveHrv(int aveHrv) {
        this.aveHrv = aveHrv;
    }

    public int getAveQT() {
        return aveQT;
    }

    public void setAveQT(int aveQT) {
        this.aveQT = aveQT;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
