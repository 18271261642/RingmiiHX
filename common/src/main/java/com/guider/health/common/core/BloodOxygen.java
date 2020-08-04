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
 * 血氧 // TODO 新设备数据实体
 */
public class BloodOxygen extends BaseDataSave implements Parcelable {
    private int value; // 血氧
    private String deviceAddress;
    private int heartBeat;

    public int getHeartBeat() {
        return heartBeat;
    }

    public void setHeartBeat(int heartBeat) {
        this.heartBeat = heartBeat;
    }

    private int _value;

    private boolean tag = false;

    public BloodOxygen() {
    }

    public boolean isTag() {
        return tag;
    }

    public void setTag(boolean tag) {
        this.tag = tag;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public String getDeviceAddress() {
        return MyUtils.getMacAddress();
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public void set_value(int value) {
        this._value = value;
    }

    public int get_value() {
        return this._value;
    }

    @Override
    protected void onStandardFinish(List<StandardResultBean> data) {
        for (int i = 0; i < data.size(); i++) {
            StandardResultBean bean = data.get(i);
            switch (bean.getType()) {
                case Constant.XUEYANG: // 血氧
                    _value = getArrow(bean.getAnlysis2());
                    cardShowStr = bean.getAnlysis();
                    break;
            }
        }
    }

    @Override
    public void startStandardRun(StandardCallback callback) {
        ArrayList<StandardRequestBean> standardRequestBeans = new ArrayList<>();
        int accountId = UserManager.getInstance().getAccountId();
        standardRequestBeans.add(new StandardRequestBean(accountId,Constant.XINLV, new Object[]{Float.valueOf(value)}));
        standardFromServer("血氧", standardRequestBeans, callback);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.value);
        dest.writeInt(this._value);
        dest.writeByte(this.tag ? (byte) 1 : (byte) 0);
    }

    protected BloodOxygen(Parcel in) {
        this.value = in.readInt();
        this._value = in.readInt();
        this.deviceAddress = in.readString();
        this.tag = in.readByte() != 0;
    }

    public static final Creator<BloodOxygen> CREATOR = new Creator<BloodOxygen>() {
        @Override
        public BloodOxygen createFromParcel(Parcel source) {
            return new BloodOxygen(source);
        }

        @Override
        public BloodOxygen[] newArray(int size) {
            return new BloodOxygen[size];
        }
    };
}
