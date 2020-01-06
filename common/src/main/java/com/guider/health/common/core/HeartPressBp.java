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

public class HeartPressBp extends BaseDataSave implements Parcelable {


    public HeartPressBp() {

    }

    private static HeartPressBp heartPress = new HeartPressBp();

    public static HeartPressBp getInstance() {
        return heartPress;
    }

    private String dbp;//低, 舒张
    private String sbp;//值高 搜说圧
    private String heart; // 心率
    private String measureTime = "";

    private int _dbp;
    private int _sbp;
    private int _heart;

    private String str_dbp;
    private String str_sbp;
    private String str_heart;

    public String getStr_dbp() {
        return str_dbp;
    }

    public HeartPressBp setStr_dbp(String str_dbp) {
        this.str_dbp = str_dbp;
        return this;
    }

    public String getStr_sbp() {
        return str_sbp;
    }

    public HeartPressBp setStr_sbp(String str_sbp) {
        this.str_sbp = str_sbp;
        return this;
    }

    public String getStr_heart() {
        return str_heart;
    }

    public HeartPressBp setStr_heart(String str_heart) {
        this.str_heart = str_heart;
        return this;
    }

    public int get_dbp() {
        return _dbp;
    }

    public HeartPressBp set_dbp(int _dbp) {
        this._dbp = _dbp;
        return this;
    }

    public int get_sbp() {
        return _sbp;
    }

    public HeartPressBp set_sbp(int _sbp) {
        this._sbp = _sbp;
        return this;
    }

    public int get_heart() {
        return _heart;
    }

    public HeartPressBp set_heart(int _heart) {
        this._heart = _heart;
        return this;
    }


    private String deviceAddress;

    public static Creator<HeartPressBp> getCREATOR() {
        return CREATOR;
    }

    public static HeartPressBp getHeartPress() {
        return heartPress;
    }

    public static void setHeartPress(HeartPressBp heartPress) {
        HeartPressBp.heartPress = heartPress;
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
                case Constant.XINLV: // 心率
                    str_heart = bean.getAnlysis();
                    _heart = getArrow(bean.getAnlysis2());
                    break;
                case Constant.XUEYA: // 血压
                    cardShowStr = bean.getAnlysis();
                    String[] split = bean.getAnlysis2().split(",");
                    if (split.length == 2) {
                        _sbp = getArrow(split[0]);
                        _dbp = getArrow(split[1]);
                    }
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
        standardFromServer("BP",standardRequestBeans, callback);
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
        dest.writeInt(this._dbp);
        dest.writeInt(this._sbp);
        dest.writeInt(this._heart);
        dest.writeString(this.str_dbp);
        dest.writeString(this.str_sbp);
        dest.writeString(this.str_heart);
        dest.writeString(this.deviceAddress);
        dest.writeByte(this.tag ? (byte) 1 : (byte) 0);
    }

    protected HeartPressBp(Parcel in) {
        this.dbp = in.readString();
        this.sbp = in.readString();
        this.heart = in.readString();
        this._dbp = in.readInt();
        this._sbp = in.readInt();
        this._heart = in.readInt();
        this.str_dbp = in.readString();
        this.str_sbp = in.readString();
        this.str_heart = in.readString();
        this.deviceAddress = in.readString();
        this.tag = in.readByte() != 0;
    }

    public static final Creator<HeartPressBp> CREATOR = new Creator<HeartPressBp>() {
        @Override
        public HeartPressBp createFromParcel(Parcel source) {
            return new HeartPressBp(source);
        }

        @Override
        public HeartPressBp[] newArray(int size) {
            return new HeartPressBp[size];
        }
    };
}
