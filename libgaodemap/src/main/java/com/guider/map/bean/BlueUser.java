package com.guider.map.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

/**
 * Created by Administrator
 * 用户资料实体类
 */
@Entity
public class BlueUser implements Parcelable {
    /**
     * {"userInfo":
     * {"birthday":"1995-06-15",
     * "image":"http://47.90.83.197/image/2018/11/24/1543019974622.jpg",
     * "nickName":"hello RaceFitPro",
     * "sex":"M",
     * "weight":"60 kg",
     * "equipment":"B30",
     * "userId":"0d56716e5629475882d4f4bfc7c51420",
     * "mac":"E7:A7:0F:11:BE:B5",
     * "phone":"14791685830",
     * "height":"170 cm"},
     * "resultCode":"001"}
     */
    @Id
    @Index
    private String userId;
    private String email;
    private String nickName;
    private String password;
    private String sex;
    private String image;
    private String height;
    private String weight;
    private String birthday;
    private String deviceCode;
    private int type;
    private String phone;

    protected BlueUser(Parcel in) {
        userId = in.readString();
        email = in.readString();
        nickName = in.readString();
        password = in.readString();
        sex = in.readString();
        image = in.readString();
        height = in.readString();
        weight = in.readString();
        birthday = in.readString();
        deviceCode = in.readString();
        type = in.readInt();
        phone = in.readString();
    }

    @Generated(hash = 735548404)
    public BlueUser(String userId, String email, String nickName, String password, String sex,
            String image, String height, String weight, String birthday, String deviceCode,
            int type, String phone) {
        this.userId = userId;
        this.email = email;
        this.nickName = nickName;
        this.password = password;
        this.sex = sex;
        this.image = image;
        this.height = height;
        this.weight = weight;
        this.birthday = birthday;
        this.deviceCode = deviceCode;
        this.type = type;
        this.phone = phone;
    }

    @Generated(hash = 1687602255)
    public BlueUser() {
    }

    public static final Creator<BlueUser> CREATOR = new Creator<BlueUser>() {
        @Override
        public BlueUser createFromParcel(Parcel in) {
            return new BlueUser(in);
        }

        @Override
        public BlueUser[] newArray(int size) {
            return new BlueUser[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(userId);
        parcel.writeString(email);
        parcel.writeString(nickName);
        parcel.writeString(password);
        parcel.writeString(sex);
        parcel.writeString(image);
        parcel.writeString(height);
        parcel.writeString(weight);
        parcel.writeString(birthday);
        parcel.writeString(deviceCode);
        parcel.writeInt(type);
        parcel.writeString(phone);
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickName() {
        return this.nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSex() {
        return this.sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getHeight() {
        return this.height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return this.weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getBirthday() {
        return this.birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getDeviceCode() {
        return this.deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
