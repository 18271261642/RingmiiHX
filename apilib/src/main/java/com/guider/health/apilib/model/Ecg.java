package com.guider.health.apilib.model;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 心电实体类
 */
@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@RequiredArgsConstructor
public class Ecg extends TEntityHealth implements Serializable {
    /**
     * 用户id
     */
    @NonNull
    long accountId;
    @NonNull
    Date testTime;
    String deviceCode;
    /**
     * 心率
     */
    int heartRate;
    /**
     * 呼吸率
     */
    int breathRate;
    /**
     * PR间期
     */
    int prInterval;
    /**
     * RR间期
     */
    int rrInterval;
    /**
     * QRS间期
     */
    int qrsDuration;
    /**
     * QT间期
     */
    int qtd;
    /**
     * QTC间期
     */
    int qtc;
    /**
     * RV5幅值
     */
    double rv5;
    /**
     * SV1幅值
     */
    double sv1;
    /**
     * p轴
     */
    int pAxis;
    /**
     * t轴
     */
    int tAxis;
    /**
     * QRS轴
     */
    int qrsAxis;
    /**
     * 分析结果
     */
    String analysisResults;
    /**
     * 导联数
     */
    int leadNumber;
    /**
     * 导联名称描述
     */
    String curveDescription;
    /**
     * 采样频率
     */
    int samplingFrequency;
    /**
     * 幅值单位
     */
    int avm;
    /**
     * 基准值
     */
    int baseLineValue;
    /**
     * 增益
     */
    double gain;
    /**
     * 心电图单点纵坐标掩码（默认值：Ox0FFF）
     */
    int mask;
    /**
     * 图片路径
     */
    String imgUrl;
}
