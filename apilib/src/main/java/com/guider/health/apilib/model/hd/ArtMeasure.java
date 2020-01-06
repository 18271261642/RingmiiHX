package com.guider.health.apilib.model.hd;

/**
 * 动脉硬化
 */
public class ArtMeasure extends BaseMeasure{


    /**
     * hb : 67
     */

    private int hr;
    private int dbp;
    private int sbp;
    private int api;
    private int avi;
    private int asi;

    public int getHr() {
        return hr;
    }

    public ArtMeasure setHr(int hr) {
        this.hr = hr;
        return this;
    }

    public int getDbp() {
        return dbp;
    }

    public ArtMeasure setDbp(int dbp) {
        this.dbp = dbp;
        return this;
    }

    public int getSbp() {
        return sbp;
    }

    public ArtMeasure setSbp(int sbp) {
        this.sbp = sbp;
        return this;
    }

    public int getApi() {
        return api;
    }

    public ArtMeasure setApi(int api) {
        this.api = api;
        return this;
    }

    public int getAvi() {
        return avi;
    }

    public ArtMeasure setAvi(int avi) {
        this.avi = avi;
        return this;
    }

    public int getAsi() {
        return asi;
    }

    public ArtMeasure setAsi(int asi) {
        this.asi = asi;
        return this;
    }
}
