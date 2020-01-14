package com.guider.healthring.b30.bean;

public class GDHeartUpBean {
    private String CARDID;//卡號
    private String PU;//心率
    private String CDATE;//上傳時間

    public String getCARDID() {
        return CARDID;
    }

    public void setCARDID(String CARDID) {
        this.CARDID = CARDID;
    }

    public String getPU() {
        return PU;
    }

    public void setPU(String PU) {
        this.PU = PU;
    }

    public String getCDATE() {
        return CDATE;
    }

    public void setCDATE(String CDATE) {
        this.CDATE = CDATE;
    }
}
