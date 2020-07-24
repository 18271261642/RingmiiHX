package com.guider.health.apilib.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 血压实体类
 */
public class BloodPressure extends TEntityHealth implements Serializable {
    /**
     * 用户id
     */
    long accountId;
    /**
     * 收缩压, 高压
     */
    int sbp;
    /**
     * 舒张压,低压
     */
    int dbp;
    Date testTime;
    String deviceCode;

    /**
     * 脉搏跳动次数, 等于心率.
     */
    int heartBeat;
    
    /**
     * 状态
     */
    String state;

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public int getSbp() {
        return sbp;
    }

    public void setSbp(int sbp) {
        this.sbp = sbp;
    }

    public int getDbp() {
        return dbp;
    }

    public void setDbp(int dbp) {
        this.dbp = dbp;
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

    public int getHeartBeat() {
        return heartBeat;
    }

    public void setHeartBeat(int heartBeat) {
        this.heartBeat = heartBeat;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
