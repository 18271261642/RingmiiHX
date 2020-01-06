package com.guider.health.apilib.model;


import android.os.Parcel;
import android.os.Parcelable;

public class BeanOfWecaht implements Parcelable {


    /**
     * flag : false
     * WechatInfo : null
     * UserInfo : null
     */

    private boolean flag;
    private boolean devFlag;
    private WechatInfo WechatInfo;
    private UserInfo UserInfo;
    private TokenInfo TokenInfo;

    public boolean isDevFlag() {
        return devFlag;
    }

    public BeanOfWecaht setDevFlag(boolean devFlag) {
        this.devFlag = devFlag;
        return this;
    }

    public com.guider.health.apilib.model.TokenInfo getTokenInfo() {
        return TokenInfo;
    }

    public BeanOfWecaht setTokenInfo(com.guider.health.apilib.model.TokenInfo tokenInfo) {
        TokenInfo = tokenInfo;
        return this;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public WechatInfo getWechatInfo() {
        return WechatInfo;
    }

    public void setWechatInfo(WechatInfo WechatInfo) {
        this.WechatInfo = WechatInfo;
    }

    public UserInfo getUserInfo() {
        return UserInfo;
    }

    public void setUserInfo(UserInfo UserInfo) {
        this.UserInfo = UserInfo;
    }


    public BeanOfWecaht() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.flag ? (byte) 1 : (byte) 0);
        dest.writeByte(this.devFlag ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.WechatInfo, flags);
        dest.writeParcelable(this.UserInfo, flags);
        dest.writeParcelable(this.TokenInfo, flags);
    }

    protected BeanOfWecaht(Parcel in) {
        this.flag = in.readByte() != 0;
        this.devFlag = in.readByte() != 0;
        this.WechatInfo = in.readParcelable(com.guider.health.apilib.model.WechatInfo.class.getClassLoader());
        this.UserInfo = in.readParcelable(com.guider.health.apilib.model.UserInfo.class.getClassLoader());
        this.TokenInfo = in.readParcelable(com.guider.health.apilib.model.TokenInfo.class.getClassLoader());
    }

    public static final Creator<BeanOfWecaht> CREATOR = new Creator<BeanOfWecaht>() {
        @Override
        public BeanOfWecaht createFromParcel(Parcel source) {
            return new BeanOfWecaht(source);
        }

        @Override
        public BeanOfWecaht[] newArray(int size) {
            return new BeanOfWecaht[size];
        }
    };
}
