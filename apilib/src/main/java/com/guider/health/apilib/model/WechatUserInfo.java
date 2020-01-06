package com.guider.health.apilib.model;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Data;

@Data
public class WechatUserInfo implements Parcelable {


    /**
     * openid : oeUVb08mdhM1G8JnHS1px9x2VqtI
     * nickname : 张三
     * sex : 1
     * headimgurl : http://thirdwx.qlogo.cn/mmopen/DHDrCukOdm9RYP9XicfQYA4bZhLgcsKZtricmH0U2JrzvZDJBd5dAticOR19yJ5CZzpn3PrPNcwS1icbSicCibYPSMycCeGAyXox0t/132
     * unionid : oaVtW50oPgqcS96ev0KTaJrv9REM
     */

    private String openId;
    private String nickname;
    private int gender; // 2是男 , 1是女
    private int sex; // 2是男 , 1是女
    private String unionId;
    private String headUrl;
    private String birthday;
    private int wechatAccountId = 1;


    public WechatUserInfo(String openId, String nickname, int gender, String unionId, String headUrl ,String birthday) {
        this.openId = openId;
        this.nickname = nickname;
        this.gender = gender;
        this.sex = gender;
        this.unionId = unionId;
        this.headUrl = headUrl;
        this.birthday = birthday;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.openId);
        dest.writeString(this.nickname);
        dest.writeInt(this.gender);
        dest.writeString(this.unionId);
        dest.writeString(this.headUrl);
    }

    protected WechatUserInfo(Parcel in) {
        this.openId = in.readString();
        this.nickname = in.readString();
        this.gender = in.readInt();
        this.unionId = in.readString();
        this.headUrl = in.readString();
    }

    public static final Parcelable.Creator<WechatUserInfo> CREATOR = new Parcelable.Creator<WechatUserInfo>() {
        @Override
        public WechatUserInfo createFromParcel(Parcel source) {
            return new WechatUserInfo(source);
        }

        @Override
        public WechatUserInfo[] newArray(int size) {
            return new WechatUserInfo[size];
        }
    };
}
