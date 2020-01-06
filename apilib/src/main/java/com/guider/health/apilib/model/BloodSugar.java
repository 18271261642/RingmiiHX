package com.guider.health.apilib.model;

import com.guider.health.apilib.enums.BSTime;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 血糖实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@RequiredArgsConstructor
public class BloodSugar extends TEntityHealth implements Serializable {
    /**
     * 用户id
     */
    @NonNull
    long accountId;
    /**
     * 血糖测量时间段
     */
    @NonNull
    BSTime bsTime;
    /**
     * 血值
     */
    @NonNull
    float bs;
    @NonNull
    Date testTime;
    @NonNull
    String deviceCode;
    /**
     * 状态
     */
    String state;

    /**
     * 肱动脉血流速度
     */
    int bloodSpeed;
    /**
     * 血红蛋白
     */
    float hemoglobin;
}
