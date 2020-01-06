package com.guider.health.apilib.model.hd;

public class NonbsBean {

    /**
     * abnormal : false
     * accountId : 0
     * createTime : 2019-12-10T02:46:38.222Z
     * enzyme : false
     * id : 0
     * phmb : false
     * unTake : false
     * updateTime : 2019-12-10T02:46:38.222Z
     * urea : false
     * value : 0
     */

    private boolean abnormal;
    private int accountId;
    private String createTime;
    private boolean enzyme;
    private int id;
    private boolean phmb;
    private boolean unTake;
    private String updateTime;
    private boolean urea;
    private float value;

    public boolean isAbnormal() {
        return abnormal;
    }

    public void setAbnormal(boolean abnormal) {
        this.abnormal = abnormal;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public boolean isEnzyme() {
        return enzyme;
    }

    public void setEnzyme(boolean enzyme) {
        this.enzyme = enzyme;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isPhmb() {
        return phmb;
    }

    public void setPhmb(boolean phmb) {
        this.phmb = phmb;
    }

    public boolean isUnTake() {
        return unTake;
    }

    public void setUnTake(boolean unTake) {
        this.unTake = unTake;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public boolean isUrea() {
        return urea;
    }

    public void setUrea(boolean urea) {
        this.urea = urea;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
