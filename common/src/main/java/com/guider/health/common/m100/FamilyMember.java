package com.guider.health.common.m100;

import com.guider.health.common.core.Glucose;
import com.guider.health.common.core.HeartPressBp;

/**
 * Created by haix on 2019/8/1.
 */

public class FamilyMember {

    private String headUrl;
    private String name = "";
    private String gender ="";
    private String birthday ="";
    private String tag="";
    private Glucose glucose;
    private HeartPressBp heartPressBp;
    private String phone;
    private int code;
    private String token;
    private int accountId;
    private int id;
    private int weight;
    private int height;
    private String createTime;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        if (headUrl == null){
            headUrl = "";
        }
        this.headUrl = headUrl;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        if (token == null){
            token = "";
        }
        this.token = token;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null){
            name = "";
        }
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        if ("MAN".equals(gender)){
            gender = "男";
        } else if("WOMAN".equals(gender)) {
            gender = "女";
        }

        if (gender == null){
            gender = "";
        }
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        if (birthday != null && birthday.length() > 10){
            birthday = birthday.substring(0, 10);
        }

        if (birthday == null){
            birthday = "";
        }
        this.birthday = birthday;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        if (tag == null){
            tag = "";
        }
        this.tag = tag;
    }

    public Glucose getGlucose() {
        return glucose;
    }

    public void setGlucose(Glucose glucose) {
        this.glucose = glucose;
    }

    public HeartPressBp getHeartPressBp() {
        return heartPressBp;
    }

    public void setHeartPressBp(HeartPressBp heartPressBp) {
        this.heartPressBp = heartPressBp;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        if (phone == null){
            phone = "";
        }
        this.phone = phone;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
