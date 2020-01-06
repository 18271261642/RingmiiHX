package com.guider.health.apilib.model.hd;

public class BpMeasure extends BaseMeasure {

    String dbp;

    String sbp;

    String heartBeat;

    public String getHeartBeat() {
        return heartBeat;
    }

    public BpMeasure setHeartBeat(String heartBeat) {
        this.heartBeat = heartBeat;
        return this;
    }

    public String getDbp() {
        return dbp;
    }

    public BpMeasure setDbp(String dbp) {
        this.dbp = dbp;
        return this;
    }

    public String getSbp() {
        return sbp;
    }

    public BpMeasure setSbp(String sbp) {
        this.sbp = sbp;
        return this;
    }
}
