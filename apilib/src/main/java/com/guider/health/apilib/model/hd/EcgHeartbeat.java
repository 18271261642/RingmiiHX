package com.guider.health.apilib.model.hd;

import java.util.Date;

public class EcgHeartbeat {

    /**
     * id : 1314220
     * createTime : 2019-09-11T03:25:04Z
     * normal : true
     * accountId : 859
     * hb : 76
     * testTime : 2019-09-11T03:25:03Z
     * deviceCode : 24:FB:65:31:7C:6A
     * state : 正常
     * state2 : 正常
     */

    private int id;
    private String createTime;
    private boolean normal;
    private int accountId;
    private int hb;
    private Date testTime;
    private String deviceCode;
    private String state;
    private String state2;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public boolean isNormal() {
        return normal;
    }

    public void setNormal(boolean normal) {
        this.normal = normal;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getHb() {
        return hb;
    }

    public void setHb(int hb) {
        this.hb = hb;
    }

    public Date getTestTime() {
        return testTime;
    }

    public void setTestTime(Date testTime) {
        this.testTime = testTime;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState2() {
        return state2;
    }

    public void setState2(String state2) {
        this.state2 = state2;
    }
}
