package com.guider.health.apilib.model;

import android.os.Parcel;
import android.os.Parcelable;

public class WechatInfo implements Parcelable {


    /**
     * openid : oeUVb08mdhM1G8JnHS1px9x2VqtI
     * nickname : 张三
     * sex : 1
     * headimgurl : http://thirdwx.qlogo.cn/mmopen/DHDrCukOdm9RYP9XicfQYA4bZhLgcsKZtricmH0U2JrzvZDJBd5dAticOR19yJ5CZzpn3PrPNcwS1icbSicCibYPSMycCeGAyXox0t/132
     * unionid : oaVtW50oPgqcS96ev0KTaJrv9REM
     */

    private String appId;
    private String openid;
    private String nickname;
    private int sex;
    private String headimgurl;
    private String unionid;

    public String getAppId() {
        return appId;
    }

    public WechatInfo setAppId(String appId) {
        this.appId = appId;
        return this;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public WechatInfo() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.appId);
        dest.writeString(this.openid);
        dest.writeString(this.nickname);
        dest.writeInt(this.sex);
        dest.writeString(this.headimgurl);
        dest.writeString(this.unionid);
    }

    protected WechatInfo(Parcel in) {
        this.appId = in.readString();
        this.openid = in.readString();
        this.nickname = in.readString();
        this.sex = in.readInt();
        this.headimgurl = in.readString();
        this.unionid = in.readString();
    }

    public static final Creator<WechatInfo> CREATOR = new Creator<WechatInfo>() {
        @Override
        public WechatInfo createFromParcel(Parcel source) {
            return new WechatInfo(source);
        }

        @Override
        public WechatInfo[] newArray(int size) {
            return new WechatInfo[size];
        }
    };
}
