package com.guider.healthring.bean;

/**
 * 盖德用户信息
 * Created by Admin
 * Date 2019/9/19
 */
public class GuiderUserInfo {


    /**
     * accountId : 1
     * addr : string
     * birthday : 1988-12-02T00:00:00Z
     * cardId : string
     * gender : MAN
     * headUrl : string
     * height : 0
     * name : string
     * phone : string
     * userState : ACTIVE
     * weight : 0
     */

    private int accountId;
    private String addr = null;
    private String birthday;
    private String cardId = null;
    private String gender;
    private String headUrl;
    private int height;
    private String name;
    private String phone;
    private String userState;
    private int weight;


    public GuiderUserInfo() {
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getAddr() {
        return addr == null ? null : addr;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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
    public String toString() {
        return "GuiderUserInfo{" +
                "accountId=" + accountId +
                ", addr='" + addr + '\'' +
                ", birthday='" + birthday + '\'' +
                ", cardId='" + cardId + '\'' +
                ", gender='" + gender + '\'' +
                ", headUrl='" + headUrl + '\'' +
                ", height=" + height +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", userState='" + userState + '\'' +
                ", weight=" + weight +
                '}';
    }
}
