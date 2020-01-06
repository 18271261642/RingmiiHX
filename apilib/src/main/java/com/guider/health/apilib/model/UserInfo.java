package com.guider.health.apilib.model;

import android.os.Parcel;
import android.os.Parcelable;

public class UserInfo implements Parcelable {
    /**
     * accountId : 0
     * addr : string
     * birthday : 2019-08-07T14:59:46.422Z
     * cardId : string
     * city : 0
     * countie : 0
     * createTime : 2019-08-07T14:59:46.422Z
     * descDetail : string
     * emergencyContact : string
     * emergencyContactPhone : string
     * gender : MAN
     * headUrl : string
     * height : 0
     * id : 0
     * mail : string
     * name : string
     * nation : 0
     * phone : string
     * province : 0
     * remark : string
     * updateTime : 2019-08-07T14:59:46.422Z
     * userState : ACTIVE
     * weight : 0
     */

    private int accountId;
    private String addr;
    private String birthday;
    private String cardId;
    private int city;
    private int countie;
    private String createTime;
    private String descDetail;
    private String emergencyContact;
    private String emergencyContactPhone;
    private String gender;
    private String headUrl;
    private int height;
    private int id;
    private String mail;
    private String name;
    private int nation;
    private String phone;
    private int province;
    private String remark;
    private String updateTime;
    private String userState;
    private int weight;

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public int getCity() {
        return city;
    }

    public void setCity(int city) {
        this.city = city;
    }

    public int getCountie() {
        return countie;
    }

    public void setCountie(int countie) {
        this.countie = countie;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getDescDetail() {
        return descDetail;
    }

    public void setDescDetail(String descDetail) {
        this.descDetail = descDetail;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getEmergencyContactPhone() {
        return emergencyContactPhone;
    }

    public void setEmergencyContactPhone(String emergencyContactPhone) {
        this.emergencyContactPhone = emergencyContactPhone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNation() {
        return nation;
    }

    public void setNation(int nation) {
        this.nation = nation;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getProvince() {
        return province;
    }

    public void setProvince(int province) {
        this.province = province;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getUserState() {
        return userState;
    }

    public void setUserState(String userState) {
        this.userState = userState;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.accountId);
        dest.writeString(this.addr);
        dest.writeString(this.birthday);
        dest.writeString(this.cardId);
        dest.writeInt(this.city);
        dest.writeInt(this.countie);
        dest.writeString(this.createTime);
        dest.writeString(this.descDetail);
        dest.writeString(this.emergencyContact);
        dest.writeString(this.emergencyContactPhone);
        dest.writeString(this.gender);
        dest.writeString(this.headUrl);
        dest.writeInt(this.height);
        dest.writeInt(this.id);
        dest.writeString(this.mail);
        dest.writeString(this.name);
        dest.writeInt(this.nation);
        dest.writeString(this.phone);
        dest.writeInt(this.province);
        dest.writeString(this.remark);
        dest.writeString(this.updateTime);
        dest.writeString(this.userState);
        dest.writeInt(this.weight);
    }

    public UserInfo() {
    }

    protected UserInfo(Parcel in) {
        this.accountId = in.readInt();
        this.addr = in.readString();
        this.birthday = in.readString();
        this.cardId = in.readString();
        this.city = in.readInt();
        this.countie = in.readInt();
        this.createTime = in.readString();
        this.descDetail = in.readString();
        this.emergencyContact = in.readString();
        this.emergencyContactPhone = in.readString();
        this.gender = in.readString();
        this.headUrl = in.readString();
        this.height = in.readInt();
        this.id = in.readInt();
        this.mail = in.readString();
        this.name = in.readString();
        this.nation = in.readInt();
        this.phone = in.readString();
        this.province = in.readInt();
        this.remark = in.readString();
        this.updateTime = in.readString();
        this.userState = in.readString();
        this.weight = in.readInt();
    }

    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel source) {
            return new UserInfo(source);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };
}
