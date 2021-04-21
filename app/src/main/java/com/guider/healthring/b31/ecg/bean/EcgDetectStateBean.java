package com.guider.healthring.b31.ecg.bean;

import com.veepoo.protocol.model.enums.EDeviceStatus;

/**
 *
 * 承接com.veepoo.protocol.model.datas.EcgDetectState
 * Created by Admin
 * Date 2021/4/20
 */
public class EcgDetectStateBean {

    private int ecgType;
    private int con;
    private int dataType;
    private EDeviceStatus deviceState;
    private int hr1;
    private int hr2;
    private int hrv;
    private int rr1;

    private int rr2;
    private int br1;
    private int br2;
    private int wear;
    private int mid;
    private int qtc;
    private int progress;


    public EcgDetectStateBean(int ecgType, int con, int dataType, EDeviceStatus deviceState,
                              int hr1, int hr2, int hrv, int rr1, int rr2, int br1, int br2,
                              int wear, int mid, int qtc, int progress) {
        this.ecgType = ecgType;
        this.con = con;
        this.dataType = dataType;
        this.deviceState = deviceState;
        this.hr1 = hr1;
        this.hr2 = hr2;
        this.hrv = hrv;
        this.rr1 = rr1;
        this.rr2 = rr2;
        this.br1 = br1;
        this.br2 = br2;
        this.wear = wear;
        this.mid = mid;
        this.qtc = qtc;
        this.progress = progress;
    }

    public int getEcgType() {
        return ecgType;
    }

    public void setEcgType(int ecgType) {
        this.ecgType = ecgType;
    }

    public int getCon() {
        return con;
    }

    public void setCon(int con) {
        this.con = con;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public EDeviceStatus getDeviceState() {
        return deviceState;
    }

    public void setDeviceState(EDeviceStatus deviceState) {
        this.deviceState = deviceState;
    }

    public int getHr1() {
        return hr1;
    }

    public void setHr1(int hr1) {
        this.hr1 = hr1;
    }

    public int getHr2() {
        return hr2;
    }

    public void setHr2(int hr2) {
        this.hr2 = hr2;
    }

    public int getHrv() {
        return hrv;
    }

    public void setHrv(int hrv) {
        this.hrv = hrv;
    }

    public int getRr1() {
        return rr1;
    }

    public void setRr1(int rr1) {
        this.rr1 = rr1;
    }

    public int getRr2() {
        return rr2;
    }

    public void setRr2(int rr2) {
        this.rr2 = rr2;
    }

    public int getBr1() {
        return br1;
    }

    public void setBr1(int br1) {
        this.br1 = br1;
    }

    public int getBr2() {
        return br2;
    }

    public void setBr2(int br2) {
        this.br2 = br2;
    }

    public int getWear() {
        return wear;
    }

    public void setWear(int wear) {
        this.wear = wear;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public int getQtc() {
        return qtc;
    }

    public void setQtc(int qtc) {
        this.qtc = qtc;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    @Override
    public String toString() {
        return "EcgDetectStateBean{" +
                "ecgType=" + ecgType +
                ", con=" + con +
                ", dataType=" + dataType +
                ", deviceState=" + deviceState +
                ", hr1=" + hr1 +
                ", hr2=" + hr2 +
                ", hrv=" + hrv +
                ", rr1=" + rr1 +
                ", rr2=" + rr2 +
                ", br1=" + br1 +
                ", br2=" + br2 +
                ", wear=" + wear +
                ", mid=" + mid +
                ", qtc=" + qtc +
                ", progress=" + progress +
                '}';
    }
}
