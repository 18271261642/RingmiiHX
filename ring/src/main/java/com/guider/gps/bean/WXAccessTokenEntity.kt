package com.joinutech.ddbeslibrary.bean

import java.io.Serializable

/**
 * description ：
 * project name：CCloud
 * author : Vincent
 * creation date: 2017/6/10 11:20
 *
 * @version 1.0
 */

data class WXAccessTokenEntity (var access_token: String?,
                                var expires_in: Int = 0,
                                var refresh_token: String? ,
                                var openid: String?,
                                var scope: String? ,
                                var unionid: String): Serializable

