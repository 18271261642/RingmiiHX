package com.guider.health.apilib.model;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Stethoscope extends TEntityNoUpdate implements Serializable {
    /**
     * 用户账号id
     */
    @NonNull
    long accountId;
    /**
     * 测量时间
     */
    Date testTime;
    /**
     * 测量模式
     */
    String measureMode;
    /**
     * 测量部位
     */
    String measurePart;
    /**
     * 测量点位
     */
    String measurePoint;
    /**
     * 前后部位
     */
    String aboutPart;
    /**
     * 音频文件路径
     */
    String audioUrl;

    /**
     * 设备mac地址
     */
    String deviceCode;
}
