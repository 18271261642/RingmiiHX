package com.guider.health.common.core;

import android.graphics.Bitmap;

/**
 * Created by haix on 2019/6/25.
 */

public class CardInfos {


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getIdcode() {
        return idcode;
    }

    public void setIdcode(String idcode) {
        this.idcode = idcode;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getBeginData() {
        return beginData;
    }

    public void setBeginData(String beginData) {
        this.beginData = beginData;
    }

    public String getEndData() {
        return endData;
    }

    public void setEndData(String endData) {
        this.endData = endData;
    }

    private static CardInfos cardInfos = new CardInfos();
    public static CardInfos getInstance(){
        return cardInfos;
    }

    private CardInfos(){

    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    private Bitmap bitmap;
    private String name;
    private String sex;
    private String nation;
    private String birth;
    private String addr;
    private String idcode;
    private String issue;
    private String beginData;
    private String endData;
    private boolean isCardLogin;
    private String headUrl;

    public String getHeadUrl() {
        return headUrl;
    }

    public CardInfos setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
        return this;
    }

    public boolean isCardLogin() {
        return isCardLogin;
    }

    public void setCardLogin(boolean cardLogin) {
        isCardLogin = cardLogin;
    }

}
