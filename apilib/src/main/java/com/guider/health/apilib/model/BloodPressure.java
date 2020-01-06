package com.guider.health.apilib.model;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * 血压实体类
 */
@Data
@NoArgsConstructor
public class BloodPressure extends TEntityHealth implements Serializable {
    /**
     * 用户id
     */
    @NonNull
    long accountId;
    /**
     * 收缩压, 高压
     */
    @NonNull
    int sbp;
    /**
     * 舒张压,低压
     */
    @NonNull
    int dbp;
    @NonNull
    Date testTime;
    String deviceCode;

    /**
     * 脉搏跳动次数, 等于心率.
     */
    int heartBeat;
    
    /**
     * 状态
     */
    String state;
}
