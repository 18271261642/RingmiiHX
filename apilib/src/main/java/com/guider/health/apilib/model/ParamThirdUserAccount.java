package com.guider.health.apilib.model;

import com.guider.health.apilib.enums.Gender;

import lombok.Data;

@Data
public class ParamThirdUserAccount {
    private String appId;
    /**
     * 头像
     */
    private String headimgurl;
    /**
     * 昵称
     */
    private String nickname;
    private String openid;
    /**
     * 性别
     */
    private Gender gender;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 国家号，例如中国为86
     */
    private String areaCode;
}
