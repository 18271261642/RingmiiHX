package com.joinutech.ddbeslibrary.bean

import java.io.Serializable

/**
 * @name LoginApi
 * @class name：com.vincent.cloud.entity
 * @class describe
 * @anthor Vincent
 * @time 2017/7/19 12:37
 * @change
 * @chang time
 * @class describe
 */

class WXUserInfo(var openid: String? = null,
                 var nickname: String? = null,
                 var sex: Int = 0,
                 var language: String? = null,
                 var city: String? = null,
                 var province: String? = null,
                 var country: String? = null,
                 var headimgurl: String? = null,
                 var unionid: String? = null,
                 var privilege: List<*>? = null) : Serializable
