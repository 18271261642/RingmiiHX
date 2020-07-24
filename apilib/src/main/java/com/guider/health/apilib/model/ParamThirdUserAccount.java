package com.guider.health.apilib.model;

import com.guider.health.apilib.enums.Gender;

import java.util.Date;

public class ParamThirdUserAccount {
    private String appId;
    /**
     * 头像
     */
    private String headimgurl;
    /**
     * 昵称
     */
    private String nickname;
    private String openid;
    /**
     * 性别
     */
    private Gender gender;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 国家号，例如中国为86
     */
    private String areaCode;
    /**
     * 生日
     */
    private Date birthday;
    /**
     * 群组id
     */
    private long groupId;
    /**
     * 医生id
     */
    private long doctorAccountId;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public long getDoctorAccountId() {
        return doctorAccountId;
    }

    public void setDoctorAccountId(long doctorAccountId) {
        this.doctorAccountId = doctorAccountId;
    }
}
