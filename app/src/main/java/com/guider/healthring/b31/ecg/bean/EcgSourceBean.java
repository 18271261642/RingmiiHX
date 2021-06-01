package com.guider.healthring.b31.ecg.bean;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Admin
 * Date 2021/4/20
 */
public class EcgSourceBean extends LitePalSupport implements Serializable {

    //mac地址
    private String bleMac;

    //测量日期 yyyy-MM-dd格式
    private String detectDate;

    //测量时间小时，分钟 HH:mm格式
    private String detectTime;

    //ECG 通过App测量的第二部分内容(测量数据以及测量进度) 已转化为字符串，再反序列化成对象
    //private EcgDetectStateBean ecgDetectStateBean;
    private String ecgDetectStateBeanStr;
    //ecg测量结果，对应SDK中com.veepoo.protocol.model.datas.EcgDetectResult
    private String ecgDetectResult;
    //所有的数据源，ecg数据
   // private List<int[]> ecgList;
    private String ecgListStr;


    public EcgSourceBean() {
    }

    public EcgSourceBean(String bleMac, String detectDate, String detectTime, String ecgDetectStateBeanStr, String ecgDetectResult, String ecgListStr) {
        this.bleMac = bleMac;
        this.detectDate = detectDate;
        this.detectTime = detectTime;
        this.ecgDetectStateBeanStr = ecgDetectStateBeanStr;
        this.ecgDetectResult = ecgDetectResult;
        this.ecgListStr = ecgListStr;
    }

    public String getBleMac() {
        return bleMac;
    }

    public void setBleMac(String bleMac) {
        this.bleMac = bleMac;
    }

    public String getDetectDate() {
        return detectDate;
    }

    public void setDetectDate(String detectDate) {
        this.detectDate = detectDate;
    }

    public String getDetectTime() {
        return detectTime;
    }

    public void setDetectTime(String detectTime) {
        this.detectTime = detectTime;
    }

    public String getEcgDetectStateBeanStr() {
        return ecgDetectStateBeanStr;
    }

    public void setEcgDetectStateBeanStr(String ecgDetectStateBeanStr) {
        this.ecgDetectStateBeanStr = ecgDetectStateBeanStr;
    }

    public String getEcgListStr() {
        return ecgListStr;
    }

    public void setEcgListStr(String ecgListStr) {
        this.ecgListStr = ecgListStr;
    }

    public String getEcgDetectResult() {
        return ecgDetectResult;
    }

    public void setEcgDetectResult(String ecgDetectResult) {
        this.ecgDetectResult = ecgDetectResult;
    }

    @Override
    public String toString() {
        return "EcgSourceBean{" +
                "bleMac='" + bleMac + '\'' +
                ", detectDate='" + detectDate + '\'' +
                ", detectTime='" + detectTime + '\'' +
                ", ecgDetectStateBeanStr='" + ecgDetectStateBeanStr + '\'' +
                ", ecgDetectResult='" + ecgDetectResult + '\'' +
                ", ecgListStr='" + ecgListStr + '\'' +
                '}';
    }
}
