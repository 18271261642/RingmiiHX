package com.guider.health.apilib.model;

public class CardInfo {


    /**
     * addr : string
     * birthday : string
     * cardId : string
     * deviceCode : string
     * gender : MAN
     * headUrl : string
     * name : string
     */

    private String addr;
    private String birthday;
    private String cardId;
    private String deviceCode;
    private String gender;
    private String headUrl;
    private String name;

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
        if (birthday != null && birthday.length() == 8) {
            this.birthday = birthday.substring(0, 4) + "-" + birthday.substring(4, 6) + "-" + birthday.substring(6, 8);
        } else {
            this.birthday = birthday;
        }
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
