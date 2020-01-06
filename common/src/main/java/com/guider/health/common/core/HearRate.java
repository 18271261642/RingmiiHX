package com.guider.health.common.core;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.guider.health.apilib.model.hd.standard.StandardRequestBean;
import com.guider.health.apilib.model.hd.standard.StandardResultBean;
import com.guider.health.common.device.standard.Constant;
import com.guider.health.common.device.standard.StandardCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by haix on 2019/6/25.
 */

public class HearRate extends BaseDataSave implements Parcelable {

    private static HearRate hearRate = new HearRate();
    private HearRate(){

    }

    public static HearRate getInstance(){
        return hearRate;
    }

    private String HeartRate;//平均心率
    private String HeartRateLight;//心律燈號 green yellow red
    private String HealthLight;//健康燈號 green yellow red
    private String HealthLightOriginal; //健康燈號 green yellow red
    private String DiaDescribe; //診斷描述，依據 HeartRateLight 和 HealthLight 燈號回傳不同描述文字
    private String SDNN = "32.756"; //压力指数 正常 R-R 間距標準差 單位:ms
    private String LFHF = "1.696";//疲劳指数  LF 除以 HF，LF 低頻自律神經整體活 性、HF 高頻自律神經活性。若 HF 為 0 或 Null，LFHF 值回傳 0。
    private String NN50;//心電圖中所有每對相鄰正 常心跳時間間隔，差距超過 50 毫秒的數 目。
    private String PNN50 = "0.04762";//心率健康 NN50 計數除以 NN 間距總數
    private String PredictedSymptoms;//偵測到的症狀:AF 心律不整、Bigeminy 二聯率、Trigeminy 三聯率、四聯率 Quadrigeminy、Coupet paired PVC 成 對 PVC。

    private String pervousSystemBalanceLight;
    private String stressLight;

    private String deviceAddress;

    private String str_PNN50 = "良好"; // 心率健康
    private String str_SDNN = "良好";  // 压力指数
    private String str_LFHF = "良好";  // 疲劳指数

    private int _HeartRate;//平均心率

    public int get_HeartRate() {
        return _HeartRate;
    }

    public HearRate set_HeartRate(int _HeartRate) {
        this._HeartRate = _HeartRate;
        return this;
    }

    public String getStr_PNN50() {
        return str_PNN50;
    }

    public void setStr_PNN50(String str_PNN50) {
        this.str_PNN50 = str_PNN50;
    }

    public String getStr_SDNN() {
        return str_SDNN;
    }

    public void setStr_SDNN(String str_SDNN) {
        this.str_SDNN = str_SDNN;
    }

    public String getStr_LFHF() {
        return str_LFHF;
    }

    public void setStr_LFHF(String str_LFHF) {
        this.str_LFHF = str_LFHF;
    }

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

    public String getPervousSystemBalanceLight() {
        return pervousSystemBalanceLight;
    }

    public void setPervousSystemBalanceLight(String pervousSystemBalanceLight) {
        if (pervousSystemBalanceLight == null){
            pervousSystemBalanceLight = "";
        }
        this.pervousSystemBalanceLight = pervousSystemBalanceLight;
    }

    public String getStressLight() {
        return stressLight;
    }

    public void setStressLight(String stressLight) {
        if (stressLight == null){
            stressLight = "";
        }
        this.stressLight = stressLight;
    }

    public String getHeartRate() {
        return HeartRate;
    }

    public void setHeartRate(String heartRate) {
        if (heartRate == null){
            heartRate = "";
        }

        float f = Float.parseFloat(heartRate);
        int fInt = Math.round(f);
//        if (heartRate.contains("\\.")) {
//            HeartRate = heartRate.split("\\.")[0];
//        }
        HeartRate = fInt+"";
    }

    public String getHeartRateLight() {
        return HeartRateLight;
    }

    public void setHeartRateLight(String heartRateLight) {
        if (heartRateLight == null){
            heartRateLight = "";
        }
        HeartRateLight = heartRateLight;
    }

    public String getHealthLight() {
        return HealthLight;
    }

    public void setHealthLight(String healthLight) {
        if (healthLight == null){
            healthLight = "";
        }
        HealthLight = healthLight;
    }

    public String getHealthLightOriginal() {
        return HealthLightOriginal;
    }

    public void setHealthLightOriginal(String healthLightOriginal) {
        if (healthLightOriginal == null){
            healthLightOriginal = "";
        }
        HealthLightOriginal = healthLightOriginal;
    }

    public String getDiaDescribe() {
        return DiaDescribe;
    }

    public void setDiaDescribe(String diaDescribe) {
        if (diaDescribe == null){
            diaDescribe = "";
        }
        DiaDescribe = diaDescribe;
    }

    public String getSDNN() {
        return SDNN;
    }

    public void setSDNN(String SDNN) {
        if (TextUtils.isEmpty(SDNN)){
            return;
        }
        this.SDNN = SDNN;
    }

    public String getLFHF() {
        return LFHF;
    }

    public void setLFHF(String LFHF) {
        if (TextUtils.isEmpty(LFHF)){
            return;
        }
        this.LFHF = LFHF;
    }

    public String getNN50() {
        return NN50;
    }

    public void setNN50(String NN50) {
        if (NN50 == null){
            NN50 = "";
        }
        this.NN50 = NN50;
    }

    public String getPNN50() {
        return PNN50;
    }

    public void setPNN50(String PNN50) {
        if (TextUtils.isEmpty(PNN50)){
            return;
        }
        this.PNN50 = PNN50;
    }

    public String getPredictedSymptoms() {
        return PredictedSymptoms;
    }

    public void setPredictedSymptoms(String predictedSymptoms) {
        if (predictedSymptoms == null){
            predictedSymptoms = "";
        }
        PredictedSymptoms = predictedSymptoms;
    }

    @Override
    protected void onStandardFinish(List<StandardResultBean> data) {
        for (int i = 0; i < data.size(); i++) {
            StandardResultBean bean = data.get(i);
            switch (bean.getType()) {
                case Constant.XINLV: // 心率
                    _HeartRate = getArrow(bean.getAnlysis2());
                    cardShowStr = bean.getAnlysis();
                    break;
                case Constant.YALIZHISHU: // 压力指数
                    str_SDNN = bean.getAnlysis();
                    break;
                case Constant.PILAOZHISHU: // 疲劳指数
                    str_LFHF = bean.getAnlysis();
                    break;
                case Constant.XINLVJIANKANG: // 心率健康
                    str_PNN50 = bean.getAnlysis();
                    break;
            }
        }
    }

    @Override
    public void startStandardRun(StandardCallback callback) {
        ArrayList<StandardRequestBean> standardRequestBeans = new ArrayList<>();
        int accountId = UserManager.getInstance().getAccountId();
        standardRequestBeans.add(new StandardRequestBean(accountId,Constant.XINLV, new Object[]{Double.valueOf(HeartRate)}));
        standardRequestBeans.add(new StandardRequestBean(accountId,Constant.YALIZHISHU, new Object[]{Double.valueOf(SDNN)}));
        standardRequestBeans.add(new StandardRequestBean(accountId,Constant.PILAOZHISHU, new Object[]{Double.valueOf(LFHF)}));
        standardRequestBeans.add(new StandardRequestBean(accountId,Constant.XINLVJIANKANG, new Object[]{Double.valueOf(PNN50),PredictedSymptoms}));
        standardFromServer("心电",standardRequestBeans, callback);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.HeartRate);
        dest.writeString(this.HeartRateLight);
        dest.writeString(this.HealthLight);
        dest.writeString(this.HealthLightOriginal);
        dest.writeString(this.DiaDescribe);
        dest.writeString(this.SDNN);
        dest.writeString(this.LFHF);
        dest.writeString(this.NN50);
        dest.writeString(this.PNN50);
        dest.writeString(this.PredictedSymptoms);
        dest.writeString(this.pervousSystemBalanceLight);
        dest.writeString(this.stressLight);
        dest.writeString(this.deviceAddress);
        dest.writeString(this.str_PNN50);
        dest.writeString(this.str_SDNN);
        dest.writeString(this.str_LFHF);
        dest.writeInt(this._HeartRate);
        dest.writeByte(this.tag ? (byte) 1 : (byte) 0);
    }

    protected HearRate(Parcel in) {
        this.HeartRate = in.readString();
        this.HeartRateLight = in.readString();
        this.HealthLight = in.readString();
        this.HealthLightOriginal = in.readString();
        this.DiaDescribe = in.readString();
        this.SDNN = in.readString();
        this.LFHF = in.readString();
        this.NN50 = in.readString();
        this.PNN50 = in.readString();
        this.PredictedSymptoms = in.readString();
        this.pervousSystemBalanceLight = in.readString();
        this.stressLight = in.readString();
        this.deviceAddress = in.readString();
        this.str_PNN50 = in.readString();
        this.str_SDNN = in.readString();
        this.str_LFHF = in.readString();
        this._HeartRate = in.readInt();
        this.tag = in.readByte() != 0;
    }

    public static final Creator<HearRate> CREATOR = new Creator<HearRate>() {
        @Override
        public HearRate createFromParcel(Parcel source) {
            return new HearRate(source);
        }

        @Override
        public HearRate[] newArray(int size) {
            return new HearRate[size];
        }
    };
}
