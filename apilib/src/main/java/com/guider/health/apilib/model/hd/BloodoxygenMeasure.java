package com.guider.health.apilib.model.hd;

/**
 * 血氧
 */
public class BloodoxygenMeasure extends BaseMeasure {

    private int bo;
    private int heartBeat;

    public int getBo() {
        return bo;
    }

    public BloodoxygenMeasure setBo(int bo) {
        this.bo = bo;
        return this;
    }

    public int getHeartBeat() {
        return heartBeat;
    }

    public BloodoxygenMeasure setHeartBeat(int heartBeat) {
        this.heartBeat = heartBeat;
        return this;
    }
}
