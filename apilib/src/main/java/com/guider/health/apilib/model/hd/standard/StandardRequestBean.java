package com.guider.health.apilib.model.hd.standard;


public class StandardRequestBean {

    /**
     * accountId : 205
     * bsTime : FPG
     * type : HRV
     * values : [12]
     */

    private int accountId;
    private String bsTime;
    private String type;
    private Object[] values;

    public static final String FPG = "FPG";         // 空腹
    public static final String TWOHPPG = "TWOHPPG"; // 饭后两小时

    public StandardRequestBean(int accountId , String type, Object[] values) {
        this(accountId , FPG , type , values);
    }

    public StandardRequestBean(int accountId ,String bsTime, String type, Object[] values) {
        this.accountId = accountId;
        this.bsTime = bsTime;
        this.type = type;
        this.values = values;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getBsTime() {
        return bsTime;
    }

    public void setBsTime(String bsTime) {
        this.bsTime = bsTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object[] getValues() {
        return values;
    }

    public void setValues(Object[] values) {
        this.values = values;
    }
}
