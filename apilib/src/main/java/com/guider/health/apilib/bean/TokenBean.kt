package com.guider.health.apilib.bean

/**
 * @Package: com.guider.health.apilib.bean
 * @ClassName: TokenBean
 * @Description: token的实体类
 * @Author: hjr
 * @CreateDate: 2020/11/20 10:53
 * Copyright (C), 1998-2020, GuiderTechnology
 */
data class TokenBean (
        var token: String? = null,
        var refreshToken: String? = null,
        var accountId:Int = 0,
        /**过期秒数*/
        var expired:Int = 0,
        /**更新时间，第一次为存储时间*/
        var updateTime: Long
)
