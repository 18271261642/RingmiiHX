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
 * Created by haix on 2019/6/25.
 */

public class HeartPressYf extends BaseDataSave implements Parcelable {


    private HeartPressYf() {

    }

    private static HeartPressYf heartPress = new HeartPressYf();

    public static HeartPressYf getInstance() {
        return heartPress;
    }

    private String dbp;//低, 舒张
    private String sbp;//值高 搜说圧
    private String heart; // 心率

    private String ASI;    // 动脉硬化指数
    private String MAP;    // 平均压
    private String PP;    // 脉压
    private String BMI;    // 体重指数
    private String C;    // 血管顺应性

    private int _dbp;//低, 舒张
    private int _sbp;//值高 搜说圧
    private int _heart; // 心率
    private int _ASI;    // 动脉硬化指数
    private int _MAP;    // 平均压
    private int _PP;    // 脉压
    private int _BMI;    // 体重指数
    private int _C;    // 血管顺应性

    private String str_dbp;//低, 舒张
    private String str_sbp;//值高 搜说圧
    private String str_heart; // 心率
    private String str_ASI;    // 动脉硬化指数
    private String str_MAP;    // 平均压
    private String str_PP;    // 脉压
    private String str_BMI;    // 体重指数
    private String str_C;    // 血管顺应性

    public String getStr_dbp() {
        return str_dbp;
    }

    public HeartPressYf setStr_dbp(String str_dbp) {
        this.str_dbp = str_dbp;
        return this;
    }

    public String getStr_sbp() {
        return str_sbp;
    }

    public HeartPressYf setStr_sbp(String str_sbp) {
        this.str_sbp = str_sbp;
        return this;
    }

    public String getStr_heart() {
        return str_heart;
    }

    public HeartPressYf setStr_heart(String str_heart) {
        this.str_heart = str_heart;
        return this;
    }

    public String getStr_ASI() {
        return str_ASI;
    }

    public HeartPressYf setStr_ASI(String str_ASI) {
        this.str_ASI = str_ASI;
        return this;
    }

    public String getStr_MAP() {
        return str_MAP;
    }

    public HeartPressYf setStr_MAP(String str_MAP) {
        this.str_MAP = str_MAP;
        return this;
    }

    public String getStr_PP() {
        return str_PP;
    }

    public HeartPressYf setStr_PP(String str_PP) {
        this.str_PP = str_PP;
        return this;
    }

    public String getStr_BMI() {
        return str_BMI;
    }

    public HeartPressYf setStr_BMI(String str_BMI) {
        this.str_BMI = str_BMI;
        return this;
    }

    public String getStr_C() {
        return str_C;
    }

    public HeartPressYf setStr_C(String str_C) {
        this.str_C = str_C;
        return this;
    }

    public int get_dbp() {
        return _dbp;
    }

    public HeartPressYf set_dbp(int _dbp) {
        this._dbp = _dbp;
        return this;
    }

    public int get_sbp() {
        return _sbp;
    }

    public HeartPressYf set_sbp(int _sbp) {
        this._sbp = _sbp;
        return this;
    }

    public int get_heart() {
        return _heart;
    }

    public HeartPressYf set_heart(int _heart) {
        this._heart = _heart;
        return this;
    }

    public int get_ASI() {
        return _ASI;
    }

    public HeartPressYf set_ASI(int _ASI) {
        this._ASI = _ASI;
        return this;
    }

    public int get_MAP() {
        return _MAP;
    }

    public HeartPressYf set_MAP(int _MAP) {
        this._MAP = _MAP;
        return this;
    }

    public int get_PP() {
        return _PP;
    }

    public HeartPressYf set_PP(int _PP) {
        this._PP = _PP;
        return this;
    }

    public int get_BMI() {
        return _BMI;
    }

    public HeartPressYf set_BMI(int _BMI) {
        this._BMI = _BMI;
        return this;
    }

    public int get_C() {
        return _C;
    }

    public HeartPressYf set_C(int _C) {
        this._C = _C;
        return this;
    }

    private String deviceAddress;

    public static Creator<HeartPressYf> getCREATOR() {
        return CREATOR;
    }

    public static HeartPressYf getHeartPress() {
        return heartPress;
    }

    public static void setHeartPress(HeartPressYf heartPress) {
        HeartPressYf.heartPress = heartPress;
    }

    public String getASI() {
        return ASI;
    }

    public HeartPressYf setASI(String ASI) {
        this.ASI = ASI;
        return this;
    }

    public String getMAP() {
        return MAP;
    }

    public HeartPressYf setMAP(String MAP) {
        this.MAP = MAP;
        return this;
    }

    public String getPP() {
        return PP;
    }

    public HeartPressYf setPP(String PP) {
        this.PP = PP;
        return this;
    }

    public String getBMI() {
        return BMI;
    }

    public HeartPressYf setBMI(String BMI) {
        float f = Float.valueOf(BMI);
        this.BMI = (int) f + "";
        return this;
    }

    public String getC() {
        return C;
    }

    public HeartPressYf setC(String c) {
        C = c;
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
                case Constant.BMI: // 身体质量
                    _BMI = getArrow(bean.getAnlysis2());
                    str_BMI = bean.getAnlysis();
                    break;
                case Constant.ASI: // 动脉硬化
                    cardShowStr = bean.getAnlysis();
                    _ASI = getArrow(bean.getAnlysis2());
                    str_ASI = bean.getAnlysis();
                    break;
            }
        }
    }

    @Override
    public void startStandardRun(StandardCallback callback) {
        ArrayList<StandardRequestBean> standardRequestBeans = new ArrayList<>();
        int accountId = UserManager.getInstance().getAccountId();
        standardRequestBeans.add(new StandardRequestBean(accountId,Constant.XINLV, new Object[]{Double.valueOf(heart)}));
        standardRequestBeans.add(new StandardRequestBean(accountId,Constant.XUEYA, new Object[]{Double.valueOf(sbp), Double.valueOf(dbp)}));
        standardRequestBeans.add(new StandardRequestBean(accountId,Constant.BMI, new Object[]{Double.valueOf(BMI)}));
        standardRequestBeans.add(new StandardRequestBean(accountId,Constant.ASI, new Object[]{Double.valueOf(ASI)}));
        standardFromServer("YF", standardRequestBeans, callback);
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
        dest.writeString(this.ASI);
        dest.writeString(this.MAP);
        dest.writeString(this.PP);
        dest.writeString(this.BMI);
        dest.writeString(this.C);
        dest.writeInt(this._dbp);
        dest.writeInt(this._sbp);
        dest.writeInt(this._heart);
        dest.writeInt(this._ASI);
        dest.writeInt(this._MAP);
        dest.writeInt(this._PP);
        dest.writeInt(this._BMI);
        dest.writeInt(this._C);
        dest.writeString(this.str_dbp);
        dest.writeString(this.str_sbp);
        dest.writeString(this.str_heart);
        dest.writeString(this.str_ASI);
        dest.writeString(this.str_MAP);
        dest.writeString(this.str_PP);
        dest.writeString(this.str_BMI);
        dest.writeString(this.str_C);
        dest.writeString(this.deviceAddress);
        dest.writeByte(this.tag ? (byte) 1 : (byte) 0);
    }

    protected HeartPressYf(Parcel in) {
        this.dbp = in.readString();
        this.sbp = in.readString();
        this.heart = in.readString();
        this.ASI = in.readString();
        this.MAP = in.readString();
        this.PP = in.readString();
        this.BMI = in.readString();
        this.C = in.readString();
        this._dbp = in.readInt();
        this._sbp = in.readInt();
        this._heart = in.readInt();
        this._ASI = in.readInt();
        this._MAP = in.readInt();
        this._PP = in.readInt();
        this._BMI = in.readInt();
        this._C = in.readInt();
        this.str_dbp = in.readString();
        this.str_sbp = in.readString();
        this.str_heart = in.readString();
        this.str_ASI = in.readString();
        this.str_MAP = in.readString();
        this.str_PP = in.readString();
        this.str_BMI = in.readString();
        this.str_C = in.readString();
        this.deviceAddress = in.readString();
        this.tag = in.readByte() != 0;
    }

    public static final Creator<HeartPressYf> CREATOR = new Creator<HeartPressYf>() {
        @Override
        public HeartPressYf createFromParcel(Parcel source) {
            return new HeartPressYf(source);
        }

        @Override
        public HeartPressYf[] newArray(int size) {
            return new HeartPressYf[size];
        }
    };
}
