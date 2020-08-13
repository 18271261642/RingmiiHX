package com.guider.baselib.bean;

import com.guider.baselib.device.standard.StandardCallback;
import com.guider.baselib.utils.MyUtils;
import com.guider.health.apilib.model.hd.standard.StandardResultBean;

import java.util.List;

/**
 * Created by haix on 2019/6/25.
 */

public class HearRateHd extends BaseDataSave {

    private static HearRateHd hearRate = new HearRateHd();

    private HearRateHd() {

    }

    public static HearRateHd getInstance() {
        return hearRate;
    }

    private String HeartRate;//平均心率
    private int emotionIndex;
    private int emotionScore;
    private int stressIndex;
    private int stressScore;
    private int alcholRiskIndex;
    private int alcholRiskScore;
    private int bodyFatRatio;
    private int bodyFatIndex;
    private int prematureBeat;
    private int atrialFibrillation;
    private double RRpercent;

    private String deviceAddress;


    public String getDeviceAddress() {
        return MyUtils.getMacAddress();
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }


    private boolean tag = false;

    public boolean isTag() {
        return tag;
    }

    public void setTag(boolean tag) {
        this.tag = tag;
    }


    public String getHeartRate() {
        return HeartRate;
    }

    public void setHeartRate(String heartRate) {
        if (heartRate == null) {
            heartRate = "";
        }

        float f = Float.parseFloat(heartRate);
        int fInt = Math.round(f);
        HeartRate = fInt + "";
    }

    public static HearRateHd getHearRate() {
        return hearRate;
    }

    public static void setHearRate(HearRateHd hearRate) {
        HearRateHd.hearRate = hearRate;
    }

    public int getEmotionIndex() {
        return emotionIndex;
    }

    public HearRateHd setEmotionIndex(int emotionIndex) {
        this.emotionIndex = emotionIndex;
        return this;
    }

    public int getEmotionScore() {
        return emotionScore;
    }

    public HearRateHd setEmotionScore(int emotionScore) {
        this.emotionScore = emotionScore;
        return this;
    }

    public int getStressIndex() {
        return stressIndex;
    }

    public HearRateHd setStressIndex(int stressIndex) {
        this.stressIndex = stressIndex;
        return this;
    }

    public int getStressScore() {
        return stressScore;
    }

    public HearRateHd setStressScore(int stressScore) {
        this.stressScore = stressScore;
        return this;
    }

    public int getAlcholRiskIndex() {
        return alcholRiskIndex;
    }

    public HearRateHd setAlcholRiskIndex(int alcholRiskIndex) {
        this.alcholRiskIndex = alcholRiskIndex;
        return this;
    }

    public int getAlcholRiskScore() {
        return alcholRiskScore;
    }

    public HearRateHd setAlcholRiskScore(int alcholRiskScore) {
        this.alcholRiskScore = alcholRiskScore;
        return this;
    }

    public int getBodyFatRatio() {
        return bodyFatRatio;
    }

    public HearRateHd setBodyFatRatio(int bodyFatRatio) {
        this.bodyFatRatio = bodyFatRatio;
        return this;
    }

    public int getBodyFatIndex() {
        return bodyFatIndex;
    }

    public HearRateHd setBodyFatIndex(int bodyFatIndex) {
        this.bodyFatIndex = bodyFatIndex;
        return this;
    }

    public int getPrematureBeat() {
        return prematureBeat;
    }

    public HearRateHd setPrematureBeat(int prematureBeat) {
        this.prematureBeat = prematureBeat;
        return this;
    }

    public int getAtrialFibrillation() {
        return atrialFibrillation;
    }

    public HearRateHd setAtrialFibrillation(int atrialFibrillation) {
        this.atrialFibrillation = atrialFibrillation;
        return this;
    }

    public double getRRpercent() {
        return RRpercent;
    }

    public HearRateHd setRRpercent(double RRpercent) {
        this.RRpercent = RRpercent;
        return this;
    }

    @Override
    public void startStandardRun(StandardCallback callback) {
//        ArrayList<StandardRequestBean> standardRequestBeans = new ArrayList<>();
//        int accountId = UserManager.getInstance().getAccountId();
//        standardRequestBeans.add(new StandardRequestBean(accountId, Constant.XINLV, new Object[]{Double.valueOf(HeartRate)}));
//        standardRequestBeans.add(new StandardRequestBean(accountId, Constant.YALIZHISHU, new Object[]{Double.valueOf(SDNN)}));
//        standardRequestBeans.add(new StandardRequestBean(accountId, Constant.PILAOZHISHU, new Object[]{Double.valueOf(LFHF)}));
//        standardRequestBeans.add(new StandardRequestBean(accountId, Constant.XINLVJIANKANG, new Object[]{Double.valueOf(PNN50), PredictedSymptoms}));
//        standardFromServer("心电", standardRequestBeans, callback);
    }

    @Override
    protected void onStandardFinish(List<StandardResultBean> data) {
//        for (int i = 0; i < data.size(); i++) {
//            StandardResultBean bean = data.get(i);
//            switch (bean.getType()) {
//                case Constant.XINLV: // 心率
//                    _HeartRate = getArrow(bean.getAnlysis2());
//                    cardShowStr = bean.getAnlysis();
//                    break;
//                case Constant.YALIZHISHU: // 压力指数
//                    str_SDNN = bean.getAnlysis();
//                    break;
//                case Constant.PILAOZHISHU: // 疲劳指数
//                    str_LFHF = bean.getAnlysis();
//                    break;
//                case Constant.XINLVJIANKANG: // 心率健康
//                    str_PNN50 = bean.getAnlysis();
//                    break;
//            }
//        }
    }

}
