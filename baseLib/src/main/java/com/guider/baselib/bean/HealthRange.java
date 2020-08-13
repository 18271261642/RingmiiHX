package com.guider.baselib.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by haix on 2019/7/25.
 */

public class HealthRange implements Parcelable {

    private static HealthRange healthRange = new HealthRange();

    private HealthRange(){

    }

    public static HealthRange getInstance(){
        return healthRange;
    }

    public HealthRange(Parcel in) {

        sbpIdealMin = in.readInt();
        sbpIdealMax = in.readInt();
        dbpIdealMin = in.readInt();
        dbpIdealMax = in.readInt();
        sbpHMin = in.readInt();
        dbpHMin = in.readInt();
        boMin = in.readInt();
        hrMin = in.readInt();
        hrMax = in.readInt();
        fbsMin = in.readDouble();
        fbsMax = in.readDouble();
        pbsMax = in.readDouble();
        pbsMin = in.readDouble();
        bmiMin = in.readDouble();
        bmiMax = in.readDouble();

    }

    //反序列化
    public static final Parcelable.Creator<HealthRange> CREATOR = new Parcelable.Creator<HealthRange>() {

        @Override
        public HealthRange createFromParcel(Parcel in) {
            return new HealthRange(in);
        }

        @Override
        public HealthRange[] newArray(int size) {
            return new HealthRange[size];
        }
    };

    //描述
    @Override
    public int describeContents() {
        return 0;
    }


    //序列化方法
    @Override
    public void writeToParcel(Parcel out, int flags) {

        out.writeInt(sbpIdealMin);
        out.writeInt(sbpIdealMax);
        out.writeInt(dbpIdealMin);
        out.writeInt(dbpIdealMax);

        out.writeInt(sbpHMin);
        out.writeInt(dbpHMin);
        out.writeInt(boMin);
        out.writeInt(hrMin);
        out.writeInt(hrMax);

        out.writeDouble(fbsMin);
        out.writeDouble(fbsMax);
        out.writeDouble(pbsMax);

        out.writeDouble(pbsMin);
        out.writeDouble(bmiMin);
        out.writeDouble(bmiMax);


    }


    @Override
    public String toString() {
        return "HealthRange{" +
                "sbpIdealMin=" + sbpIdealMin +
                ", sbpIdealMax=" + sbpIdealMax +
                ", dbpIdealMin=" + dbpIdealMin +
                ", dbpIdealMax=" + dbpIdealMax +
                ", sbpHMin=" + sbpHMin +
                ", dbpHMin=" + dbpHMin +
                ", fbsMin=" + fbsMin +
                ", fbsMax=" + fbsMax +
                ", pbsMax=" + pbsMax +
                ", pbsMin=" + pbsMin +
                ", bmiMin=" + bmiMin +
                ", bmiMax=" + bmiMax +
                ", boMin=" + boMin +
                ", hrMin=" + hrMin +
                ", hrMax=" + hrMax +
                '}';
    }


    public void toHealthRange(HealthRange healthRange){
        if (healthRange == null) {
            return;
        }
        HealthRange.getInstance().setSbpIdealMin(healthRange.getSbpIdealMin());
        HealthRange.getInstance().setSbpIdealMax(healthRange.getSbpIdealMax());
        HealthRange.getInstance().setDbpIdealMin(healthRange.getDbpIdealMin());
        HealthRange.getInstance().setDbpIdealMax(healthRange.getDbpIdealMax());
        HealthRange.getInstance().setSbpHMin(healthRange.getSbpHMin());
        HealthRange.getInstance().setDbpHMin(healthRange.getDbpHMin());
        HealthRange.getInstance().setFbsMin(healthRange.getFbsMin());
        HealthRange.getInstance().setFbsMax(healthRange.getFbsMax());
        HealthRange.getInstance().setPbsMax(healthRange.getPbsMax());
        HealthRange.getInstance().setPbsMin(healthRange.getPbsMin());
        HealthRange.getInstance().setBmiMin(healthRange.getBmiMin());
        HealthRange.getInstance().setBmiMax(healthRange.getBmiMax());
        HealthRange.getInstance().setBoMin(healthRange.getBoMin());
        HealthRange.getInstance().setHrMin(healthRange.getHrMin());
        HealthRange.getInstance().setHrMax(healthRange.getHrMax());

    }

    public void setSbpIdealMin(int sbpIdealMin) {
        this.sbpIdealMin = sbpIdealMin;
    }

    public void setSbpIdealMax(int sbpIdealMax) {
        this.sbpIdealMax = sbpIdealMax;
    }

    public void setDbpIdealMin(int dbpIdealMin) {
        this.dbpIdealMin = dbpIdealMin;
    }

    public void setDbpIdealMax(int dbpIdealMax) {
        this.dbpIdealMax = dbpIdealMax;
    }

    public void setSbpHMin(int sbpHMin) {
        this.sbpHMin = sbpHMin;
    }

    public void setDbpHMin(int dbpHMin) {
        this.dbpHMin = dbpHMin;
    }

    public void setFbsMin(double fbsMin) {
        this.fbsMin = fbsMin;
    }

    public void setFbsMax(double fbsMax) {
        this.fbsMax = fbsMax;
    }

    public void setPbsMax(double pbsMax) {
        this.pbsMax = pbsMax;
    }

    public void setBmiMin(double bmiMin) {
        this.bmiMin = bmiMin;
    }

    public void setBmiMax(double bmiMax) {
        this.bmiMax = bmiMax;
    }

    public void setBoMin(int boMin) {
        this.boMin = boMin;
    }

    public void setHrMin(int hrMin) {
        this.hrMin = hrMin;
    }

    public void setHrMax(int hrMax) {
        this.hrMax = hrMax;
    }

    public double getBmiMin() {
        return bmiMin;
    }

    public double getBmiMax() {
        return bmiMax;
    }

    public int getHrMin() {
        return hrMin;
    }

    public int getHrMax() {
        return hrMax;
    }

    public double getPbsMax() {
        return pbsMax;
    }

    public double getFbsMin() {
        return fbsMin;
    }

    public double getFbsMax() {
        return fbsMax;
    }

    public int getBoMin() {
        return boMin;
    }

    public int getSbpIdealMin() {
        return sbpIdealMin;
    }

    public int getDbpIdealMin() {
        return dbpIdealMin;
    }

    public int getSbpIdealMax() {
        return sbpIdealMax;
    }

    public int getDbpIdealMax() {
        return dbpIdealMax;
    }

    public int getSbpHMin() {
        return sbpHMin;
    }

    public int getDbpHMin() {
        return dbpHMin;
    }

    public double getPbsMin() {
        return pbsMin;
    }

    public void setPbsMin(double pbsMin) {
        this.pbsMin = pbsMin;
    }

    /**
     * 理想收缩压，最小值
     */
//    @ApiModelProperty("理想收缩压，最小值")
    int sbpIdealMin=90;
    /**
     * 理想收缩压，最大值
     */
//    @ApiModelProperty("理想收缩压，最大值")
    int sbpIdealMax=120;
    /**
     * 理想舒张压，最小值
     */
//    @ApiModelProperty("理想舒张压，最小值")
    int dbpIdealMin=60;
    /**
     * 理想舒张压，最大值
     */
//    @ApiModelProperty("理想舒张压，最大值")
    int dbpIdealMax=80;
    /**
     * 疑似高血压，收缩压，最小值
     */
//    @ApiModelProperty("疑似高血压，收缩压，最小值")
    int sbpHMin=139;
    /**
     * 疑似高血压，舒张压，最小值
     */
//    @ApiModelProperty("疑似高血压，舒张压，最小值")
    int dbpHMin=89;
    /**
     * 空腹血糖最小值
     */
//    @ApiModelProperty("空腹血糖最小值")
    double fbsMin=3.9;
    /**
     * 空腹血糖最大值
     */
//    @ApiModelProperty("空腹血糖最大值")
    double fbsMax=6.1;
    /**
     * 餐后2小时血糖最大值
     */
//    @ApiModelProperty("餐后2小时血糖最大值")
    double pbsMax=7.8;

    /**
     * 餐后2小时血糖最小值
     */
//    @ApiModelProperty("餐后2小时血糖最小值")
    double pbsMin=3.9;
    /**
     * 体脂最小值
     */
//    @ApiModelProperty("体脂最小值")
    double bmiMin=18.5;
    /**
     * 体脂最大值
     */
//    @ApiModelProperty("体脂最大值")
    double bmiMax=24.0;
    /**
     * 血氧最小值
     */
//    @ApiModelProperty("血氧最小值")
    int boMin=94;
    /**
     * 心率最小值
     */
//    @ApiModelProperty("心率最小值")
    int hrMin=60;
    /**
     * 心率最大值
     */
//    @ApiModelProperty("心率最大值")
    int hrMax=100;

    // ------------------------------- tod -------------------------------一下是还没有从服务器同步的字段

    // 血红蛋白
    public int getHemoglobinMin() {
        return 110;
    }
    public int getHemoglobinMax() {
        return 160;
    }

    // 血流速度
    public int getSpeedMin() {
        return 0;
    }
    public int getSpeedMax() {
        return 0;
    }

    // 动脉硬化
    public int getAsiMin() {
        return 0;
    }
    public int getAsiMax() {
        return 80;
    }
}
