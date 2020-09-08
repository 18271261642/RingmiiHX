package com.guider.baselib.bean;

/**
 * Created by haix on 2019/7/25.
 */

public class ParamHealthRangeAnalysis {

    public final static int BMI = 1;
    public final static int HEARTBEAT = 2;
    public final static int BLOODSUGAR = 3;
    public final static int BLOODOXYGEN = 4;
    public final static int BLOODPRESSURE = 5;
    public final static int DMYH = 6;
    public final static int LLZS = 7;
    public final static int YLZS = 8;
    public final static int JKZS = 9;
    public final static int AVI = 10;
    public final static int API = 11;


    public final static int TWOHPPG = 2;//饭后两小时
    public final static int FPG = 0;//空腹

    private int type;
    private Object value1;//Sbp 高压
    private Object value2;//Dbp 低压
    private int year = 0;
    private int bsTime;

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Object getValue2() {
        return value2;
    }

    public void setValue2(Object value2) {
        this.value2 = value2;
    }


    public void setType(int type){
        this.type = type;
    }
    public int getType(){
        return type;
    }

    public Object getValue1() {
        return value1;
    }

    public void setValue1(Object value1) {
        this.value1 = value1;
    }

    public int getBsTime(){
        return bsTime;
    }

    public void setBsTime(int bsTime){
        this.bsTime = bsTime;
    }
}
