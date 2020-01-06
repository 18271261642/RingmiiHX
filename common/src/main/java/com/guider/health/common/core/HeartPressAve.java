package com.guider.health.common.core;

import android.os.Parcel;
import android.os.Parcelable;

import com.guider.health.apilib.model.hd.standard.StandardRequestBean;
import com.guider.health.apilib.model.hd.standard.StandardResultBean;
import com.guider.health.common.device.standard.Constant;
import com.guider.health.common.device.standard.StandardCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by haix on 2019/6/25.
 */

public class HeartPressAve extends BaseDataSave implements Parcelable {


    private HeartPressAve(){

    }

    private static HeartPressAve heartPress = new HeartPressAve();
    public static HeartPressAve getInstance(){
        return heartPress;
    }

    private String dbp;//低, 舒张
    private String sbp;//值高 搜说圧
    private String heart; // 心率
    private String deviceAddress;

    private String avi;
    private String api;

    private int _dbp;//低, 舒张
    private int _sbp;//值高 搜说圧
    private int _heart; // 心率
    private int _avi;
    private int _api;

    private String str_dbp;//低, 舒张
    private String str_sbp;//值高 搜说圧
    private String str_heart; // 心率
    private String str_avi;
    private String str_api;

    public String getStr_dbp() {
        return str_dbp;
    }

    public HeartPressAve setStr_dbp(String str_dbp) {
        this.str_dbp = str_dbp;
        return this;
    }

    public String getStr_sbp() {
        return str_sbp;
    }

    public HeartPressAve setStr_sbp(String str_sbp) {
        this.str_sbp = str_sbp;
        return this;
    }

    public String getStr_heart() {
        return str_heart;
    }

    public HeartPressAve setStr_heart(String str_heart) {
        this.str_heart = str_heart;
        return this;
    }

    public String getStr_avi() {
        return str_avi;
    }

    public HeartPressAve setStr_avi(String str_avi) {
        this.str_avi = str_avi;
        return this;
    }

    public String getStr_api() {
        return str_api;
    }

    public HeartPressAve setStr_api(String str_api) {
        this.str_api = str_api;
        return this;
    }

    public int get_dbp() {
        return _dbp;
    }

    public HeartPressAve set_dbp(int _dbp) {
        this._dbp = _dbp;
        return this;
    }

    public int get_sbp() {
        return _sbp;
    }

    public HeartPressAve set_sbp(int _sbp) {
        this._sbp = _sbp;
        return this;
    }

    public int get_heart() {
        return _heart;
    }

    public HeartPressAve set_heart(int _heart) {
        this._heart = _heart;
        return this;
    }

    public int get_avi() {
        return _avi;
    }

    public HeartPressAve set_avi(int _avi) {
        this._avi = _avi;
        return this;
    }

    public int get_api() {
        return _api;
    }

    public HeartPressAve set_api(int _api) {
        this._api = _api;
        return this;
    }

    public static Creator<HeartPressAve> getCREATOR() {
        return CREATOR;
    }

    public String getAvi() {
        return avi;
    }

    public HeartPressAve setAvi(String avi) {
        this.avi = avi;
        return this;
    }

    public String getApi() {
        return api;
    }

    public HeartPressAve setApi(String api) {
        this.api = api;
        return this;
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

    public String getDbp() {
        return dbp;
    }

    public void setDbp(String dbp) {
        this.dbp = dbp;
    }

    public String getSbp() {
        return sbp;
    }

    public void setSbp(String sbp) {
        this.sbp = sbp;
    }

    public String getHeart() {
        return heart;
    }

    public void setHeart(String heart) {
        this.heart = heart;
    }

    @Override
    protected void onStandardFinish(List<StandardResultBean> data) {
        for (int i = 0; i < data.size(); i++) {
            StandardResultBean bean = data.get(i);
            switch (bean.getType()) {
                case Constant.XINLV: // 心率
                    str_heart = bean.getAnlysis();
                    _heart = getArrow(bean.getAnlysis2());
                    break;
                case Constant.XUEYA: // 血压
                    String[] split = bean.getAnlysis2().split(",");
                    if (split.length == 2) {
                        _sbp = getArrow(split[0]);
                        _dbp = getArrow(split[1]);
                        str_sbp = split[0];
                        str_dbp = split[1];
                    }
                    break;
                case Constant.AVI: // AVI
                    cardShowStr = bean.getAnlysis();
                    _avi = getArrow(bean.getAnlysis2());
                    str_avi = bean.getAnlysis();
                    break;
            }
        }
    }

    @Override
    public void startStandardRun(StandardCallback callback) {
        ArrayList<StandardRequestBean> standardRequestBeans = new ArrayList<>();
        int accountId = UserManager.getInstance().getAccountId();
        standardRequestBeans.add(new StandardRequestBean(accountId,Constant.XINLV, new Object[]{Double.valueOf(heart)}));
        standardRequestBeans.add(new StandardRequestBean(accountId,Constant.XUEYA, new Object[]{Double.valueOf(sbp) , Double.valueOf(dbp)}));
        standardRequestBeans.add(new StandardRequestBean(accountId,Constant.AVI, new Object[]{Double.valueOf(avi)}));
        standardFromServer("AVE",standardRequestBeans, callback);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.dbp);
        dest.writeString(this.sbp);
        dest.writeString(this.heart);
        dest.writeString(this.deviceAddress);
        dest.writeString(this.avi);
        dest.writeString(this.api);
        dest.writeInt(this._dbp);
        dest.writeInt(this._sbp);
        dest.writeInt(this._heart);
        dest.writeInt(this._avi);
        dest.writeInt(this._api);
        dest.writeString(this.str_dbp);
        dest.writeString(this.str_sbp);
        dest.writeString(this.str_heart);
        dest.writeString(this.str_avi);
        dest.writeString(this.str_api);
        dest.writeByte(this.tag ? (byte) 1 : (byte) 0);
    }

    protected HeartPressAve(Parcel in) {
        this.dbp = in.readString();
        this.sbp = in.readString();
        this.heart = in.readString();
        this.deviceAddress = in.readString();
        this.avi = in.readString();
        this.api = in.readString();
        this._dbp = in.readInt();
        this._sbp = in.readInt();
        this._heart = in.readInt();
        this._avi = in.readInt();
        this._api = in.readInt();
        this.str_dbp = in.readString();
        this.str_sbp = in.readString();
        this.str_heart = in.readString();
        this.str_avi = in.readString();
        this.str_api = in.readString();
        this.tag = in.readByte() != 0;
    }

    public static final Creator<HeartPressAve> CREATOR = new Creator<HeartPressAve>() {
        @Override
        public HeartPressAve createFromParcel(Parcel source) {
            return new HeartPressAve(source);
        }

        @Override
        public HeartPressAve[] newArray(int size) {
            return new HeartPressAve[size];
        }
    };
}
