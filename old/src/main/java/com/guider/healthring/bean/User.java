package com.guider.healthring.bean;

/**
 *
 */
public class User {

    /**
     * birthday : 1995-06-15
     * image : http://47.90.83.197/image/2018/11/24/1543019974622.jpg
     * nickName : hello RaceFitPro
     * sex : M
     * weight : 60 kg
     * equipment : B30
     * userId : 0d56716e5629475882d4f4bfc7c51420
     * mac : E7:A7:0F:11:BE:B5
     * phone : 14791685830
     * height : 170 cm
     */

    private String openid;
    private String birthday;
    private String image;
    private String nickName;
    private String sex;
    private String weight;
    private String equipment;
    private String userId;
    private String mac;
    private String phone;
    private String height;

    private String nickname;
    private String unionid;
    private String headimgurl;

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
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

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    @Override
    public String toString() {
        return "User{" +
                "openid='" + openid + '\'' +
                ", birthday='" + birthday + '\'' +
                ", image='" + image + '\'' +
                ", nickName='" + nickName + '\'' +
                ", sex='" + sex + '\'' +
                ", weight='" + weight + '\'' +
                ", equipment='" + equipment + '\'' +
                ", userId='" + userId + '\'' +
                ", mac='" + mac + '\'' +
                ", phone='" + phone + '\'' +
                ", height='" + height + '\'' +
                '}';
    }
}
