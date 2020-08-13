package com.guider.baselib.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.guider.baselib.cache.UserManager;
import com.guider.baselib.device.standard.Constant;
import com.guider.baselib.device.standard.StandardCallback;
import com.guider.baselib.utils.MyUtils;
import com.guider.health.apilib.model.hd.standard.StandardRequestBean;
import com.guider.health.apilib.model.hd.standard.StandardResultBean;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by haix on 2019/6/24.
 */

public class Glucose extends BaseDataSave implements Parcelable {
    public Glucose() {
        Logger.i("构造函数");
    }

    private static Glucose mGlucose = new Glucose();

    public static Glucose getInstance() {
        return mGlucose;
    }

    public static void deassign(Glucose glucose) {
        mGlucose = glucose;
    }

    private double glucose = 0;//血糖
    private String oxygenSaturation;//血氧
    private String hemoglobin;//血紅蛋白
    private String speed;//血流速度
    private String pulse;//心率
    private String batteryLevel;//电量
    private boolean tag = false;
    private String deviceAddress;
    private String measureTime = "";

    private String indexOxygen;
    private String indexGlucose;

    private int foodTime = 0;//0为饭前,  1为饭后2小时

    private int _glucose = 0;//血糖
    private int _oxygenSaturation;//血氧
    private int _hemoglobin;//血紅蛋白
    private int _speed;//血流速度
    private int _pulse;//心率

    private String str_glucose;//血糖
    private String str_oxygenSaturation;//血氧
    private String str_hemoglobin;//血紅蛋白
    private String str_speed;//血流速度
    private String str_pulse;//心率

    private String fingerTemperature = "";//指尖温度
    private String fingerHumidity = "";//指尖湿度
    private String environmentTemperature = "";//环境温度
    private String environmentHumidity = "";//环境湿度

    public String getFingerTemperature() {
        if (!TextUtils.isEmpty(fingerTemperature)) {
            fingerTemperature = Double.valueOf(fingerTemperature) / 10 + "";
        }
        return fingerTemperature;
    }

    public void setFingerTemperature(String fingerTemperature) {
        this.fingerTemperature = fingerTemperature;
    }

    public String getFingerHumidity() {
        if (!TextUtils.isEmpty(fingerHumidity)) {
            fingerHumidity = Double.valueOf(fingerHumidity) / 10 + "";
        }
        return fingerHumidity;
    }

    public void setFingerHumidity(String fingerHumidity) {
        this.fingerHumidity = fingerHumidity;
    }

    public String getEnvironmentTemperature() {
        if (!TextUtils.isEmpty(environmentTemperature)) {
            environmentTemperature = Double.valueOf(environmentTemperature) / 10 + "";
        }
        return environmentTemperature;
    }

    public void setEnvironmentTemperature(String environmentTemperature) {
        this.environmentTemperature = environmentTemperature;
    }

    public String getEnvironmentHumidity() {
        if (!TextUtils.isEmpty(environmentHumidity)) {
            environmentHumidity = Double.valueOf(environmentHumidity) / 10 + "";
        }
        return environmentHumidity;
    }

    public void setEnvironmentHumidity(String environmentHumidity) {
        this.environmentHumidity = environmentHumidity;
    }

    public String getStr_glucose() {
        return str_glucose;
    }

    public Glucose setStr_glucose(String str_glucose) {
        this.str_glucose = str_glucose;
        return this;
    }

    public String getStr_oxygenSaturation() {
        return str_oxygenSaturation;
    }

    public Glucose setStr_oxygenSaturation(String str_oxygenSaturation) {
        this.str_oxygenSaturation = str_oxygenSaturation;
        return this;
    }

    public String getStr_hemoglobin() {
        return str_hemoglobin;
    }

    public Glucose setStr_hemoglobin(String str_hemoglobin) {
        this.str_hemoglobin = str_hemoglobin;
        return this;
    }

    public String getStr_speed() {
        return str_speed;
    }

    public Glucose setStr_speed(String str_speed) {
        this.str_speed = str_speed;
        return this;
    }

    public String getStr_pulse() {
        return str_pulse;
    }

    public Glucose setStr_pulse(String str_pulse) {
        this.str_pulse = str_pulse;
        return this;
    }

    public int get_glucose() {
        return _glucose;
    }

    public Glucose set_glucose(int _glucose) {
        this._glucose = _glucose;
        return this;
    }

    public int get_oxygenSaturation() {
        return _oxygenSaturation;
    }

    public Glucose set_oxygenSaturation(int _oxygenSaturation) {
        this._oxygenSaturation = _oxygenSaturation;
        return this;
    }

    public int get_hemoglobin() {
        return _hemoglobin;
    }

    public Glucose set_hemoglobin(int _hemoglobin) {
        this._hemoglobin = _hemoglobin;
        return this;
    }

    public int get_speed() {
        return _speed;
    }

    public Glucose set_speed(int _speed) {
        this._speed = _speed;
        return this;
    }

    public int get_pulse() {
        return _pulse;
    }

    public Glucose set_pulse(int _pulse) {
        this._pulse = _pulse;
        return this;
    }

    public int getFoodTime() {
        return foodTime;
    }

    public void setFoodTime(int foodTime) {
        this.foodTime = foodTime;
    }

    public String getIndexOxygen() {
        return indexOxygen;
    }

    public void setIndexOxygen(String indexOxygen) {
        this.indexOxygen = indexOxygen;
    }

    public String getIndexGlucose() {
        return indexGlucose;
    }

    public void setIndexGlucose(String indexGlucose) {
        this.indexGlucose = indexGlucose;
    }

    public String getDeviceAddress() {
        return MyUtils.getMacAddress();
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public boolean isTag() {
        return tag;
    }

    public void setTag(boolean tag) {
        Logger.i("oldTag =   " + this.tag  + " -- newTag = " + tag);
        this.tag = tag;
    }

    public String getPulse() {
        return pulse;
    }

    public void setPulse(String pulse) {
        this.pulse = pulse;
    }

    public String getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(String batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public double getGlucose() {
        return glucose;
    }

    public void setGlucose(double glucose) {
        this.glucose = glucose;
    }

    public String getOxygenSaturation() {
        return oxygenSaturation;
    }

    public void setOxygenSaturation(String oxygenSaturation) {
        this.oxygenSaturation = oxygenSaturation;
    }

    public String getHemoglobin() {
        return hemoglobin;
    }

    public void setHemoglobin(String hemoglobin) {
        if (hemoglobin != null && hemoglobin.length() >= 3) {
            this.hemoglobin = hemoglobin.substring(0, 3);
        } else {
            this.hemoglobin = hemoglobin;
        }
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
//        if (Integer.valueOf(speed) > 100) {
//            this.speed = Integer.valueOf(speed) / 10 + "";
//        }
    }

    public String getMeasureTime() {
        return measureTime;
    }

    public void setMeasureTime(String measureTime) {
        this.measureTime = measureTime;
    }
    @Override
    protected void onStandardFinish(List<StandardResultBean> data) {
        for (int i = 0; i < data.size(); i++) {
            StandardResultBean bean = data.get(i);
            switch (bean.getType()) {
                case Constant.XUETANG: // 血糖
                    _glucose = getArrow(bean.getAnlysis2());
                    str_glucose = bean.getAnlysis();
                    cardShowStr = bean.getAnlysis();
                    break;
                case Constant.XUEYANG: // 血氧
                    _oxygenSaturation = getArrow(bean.getAnlysis2());
                    str_oxygenSaturation = bean.getAnlysis();
                    break;
                case Constant.XUEHONGDANBAI: // 血红蛋白
                    _hemoglobin = getArrow(bean.getAnlysis2());
                    str_hemoglobin = bean.getAnlysis();
                    break;
                case Constant.XUELIUSU: // 血流速
                    _speed = getArrow(bean.getAnlysis2());
                    str_speed = bean.getAnlysis();
                    break;
                case Constant.XINLV: // 心率
                    _pulse = getArrow(bean.getAnlysis2());
                    str_pulse = bean.getAnlysis();
                    break;

            }
        }
    }

    @Override
    public void startStandardRun(StandardCallback callback) {
        ArrayList<StandardRequestBean> standardRequestBeans = new ArrayList<>();
        int accountId = UserManager.getInstance().getAccountId();
        standardRequestBeans.add(new StandardRequestBean(accountId,foodTime == 0 ? Constant.FPG : Constant.TWOHPPG , Constant.XUETANG, new Object[]{Double.valueOf(glucose)}));
        standardRequestBeans.add(new StandardRequestBean(accountId,Constant.XUEYANG, new Object[]{Double.valueOf(oxygenSaturation)}));
        standardRequestBeans.add(new StandardRequestBean(accountId,Constant.XUEHONGDANBAI, new Object[]{Double.valueOf(hemoglobin)}));
        standardRequestBeans.add(new StandardRequestBean(accountId,Constant.XUELIUSU, new Object[]{Double.valueOf(speed)}));
        standardRequestBeans.add(new StandardRequestBean(accountId,Constant.XINLV, new Object[]{Double.valueOf(pulse)}));
        standardFromServer("血糖" , standardRequestBeans, callback);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.glucose);
        dest.writeString(this.oxygenSaturation);
        dest.writeString(this.hemoglobin);
        dest.writeString(this.speed);
        dest.writeString(this.pulse);
        dest.writeString(this.batteryLevel);
        dest.writeByte(this.tag ? (byte) 1 : (byte) 0);
        dest.writeString(this.deviceAddress);
        dest.writeString(this.indexOxygen);
        dest.writeString(this.indexGlucose);
        dest.writeInt(this.foodTime);
        dest.writeInt(this._glucose);
        dest.writeInt(this._oxygenSaturation);
        dest.writeInt(this._hemoglobin);
        dest.writeInt(this._speed);
        dest.writeInt(this._pulse);
        dest.writeString(this.str_glucose);
        dest.writeString(this.str_oxygenSaturation);
        dest.writeString(this.str_hemoglobin);
        dest.writeString(this.str_speed);
        dest.writeString(this.str_pulse);
    }

    protected Glucose(Parcel in) {
        this.glucose = in.readDouble();
        this.oxygenSaturation = in.readString();
        this.hemoglobin = in.readString();
        this.speed = in.readString();
        this.pulse = in.readString();
        this.batteryLevel = in.readString();
        this.tag = in.readByte() != 0;
        this.deviceAddress = in.readString();
        this.indexOxygen = in.readString();
        this.indexGlucose = in.readString();
        this.foodTime = in.readInt();
        this._glucose = in.readInt();
        this._oxygenSaturation = in.readInt();
        this._hemoglobin = in.readInt();
        this._speed = in.readInt();
        this._pulse = in.readInt();
        this.str_glucose = in.readString();
        this.str_oxygenSaturation = in.readString();
        this.str_hemoglobin = in.readString();
        this.str_speed = in.readString();
        this.str_pulse = in.readString();
    }

    public static final Creator<Glucose> CREATOR = new Creator<Glucose>() {
        @Override
        public Glucose createFromParcel(Parcel source) {
            return new Glucose(source);
        }

        @Override
        public Glucose[] newArray(int size) {
            return new Glucose[size];
        }
    };
}
