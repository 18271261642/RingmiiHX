package com.guider.baselib.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.guider.baselib.cache.UserManager;
import com.guider.baselib.device.standard.Constant;
import com.guider.baselib.device.standard.StandardCallback;
import com.guider.baselib.utils.MyUtils;
import com.guider.health.apilib.model.hd.standard.StandardRequestBean;
import com.guider.health.apilib.model.hd.standard.StandardResultBean;
import java.util.ArrayList;
import java.util.List;

/**
 * 体温 // TODO 新设备数据实体
 */
public class Temp extends BaseDataSave implements Parcelable {
    private float value; //体温
    private String deviceAddress;

    private int _value;

    private boolean tag = false;

    public Temp() {
    }

    public boolean isTag() {
        return tag;
    }

    public void setTag(boolean tag) {
        this.tag = tag;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public float getValue() {
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
                case Constant.TEMP: // 心率
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
        standardRequestBeans.add(new StandardRequestBean(accountId,Constant.XINLV,
                new Object[]{Float.valueOf(value)}));
        standardFromServer("体温", standardRequestBeans, callback);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.value);
        dest.writeInt(this._value);
        dest.writeByte(this.tag ? (byte) 1 : (byte) 0);
    }

    protected Temp(Parcel in) {
        this.value = in.readFloat();
        this._value = in.readInt();
        this.deviceAddress = in.readString();
        this.tag = in.readByte() != 0;
    }

    public static final Creator<Temp> CREATOR = new Creator<Temp>() {
        @Override
        public Temp createFromParcel(Parcel source) {
            return new Temp(source);
        }

        @Override
        public Temp[] newArray(int size) {
            return new Temp[size];
        }
    };
}
