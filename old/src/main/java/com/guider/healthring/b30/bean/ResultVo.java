package com.guider.healthring.b30.bean;

public class ResultVo {

    /**
     * "STATUS":"200"
     * CARDID : D6:64:CB:24:7E:74
     * USERNAME : 使用者
     * MESSAGE : INSERT SUCCESSFUL
     */
    String STATUS;
    private String CARDID;
    private String USERNAME;
    private String MESSAGE;

    public String getSTATUS() {
        return STATUS;
    }

    public void setSTATUS(String STATUS) {
        this.STATUS = STATUS;
    }


    public String getCARDID() {
        return CARDID;
    }

    public void setCARDID(String CARDID) {
        this.CARDID = CARDID;
    }

    public String getUSERNAME() {
        return USERNAME;
    }

    public void setUSERNAME(String USERNAME) {
        this.USERNAME = USERNAME;
    }

    public String getMESSAGE() {
        return MESSAGE;
    }

    public void setMESSAGE(String MESSAGE) {
        this.MESSAGE = MESSAGE;
    }
}