package com.guider.health.apilib.model;

import lombok.Data;

@Data
public class GetWechatInfo {

    long doctorAccountId;
    String deviceCode;
    String sence;

}
