package com.guider.health.apilib.model.hd;

import java.util.Date;

public class BaseMeasure {

    private int accountId;
    private String deviceCode;
    private Date testTime = new Date();

    public int getAccountId() {
        return accountId;
    }

    public BaseMeasure setAccountId(int accountId) {
        this.accountId = accountId;
        return this;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public BaseMeasure setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
        return this;
    }

    public Date getTestTime() {
        return testTime;
    }

    public BaseMeasure setTestTime(Date testTime) {
        this.testTime = testTime;
        return this;
    }
}
