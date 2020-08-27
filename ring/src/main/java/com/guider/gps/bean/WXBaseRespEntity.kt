package com.joinutech.ddbeslibrary.bean

import java.io.Serializable

/**
 * description ：
 * project name：CCloud
 * author : Vincent
 * creation date: 2017/6/10 9:39
 *
 * @version 1.0
 */

class WXBaseRespEntity (var code: String? = null,
                        var country: String? = null,
                        var errCode: Int = 0,
                        var lang: String? = null,
                        var state: String? = null,
                        var type: Int = 0,
                        var url: String? = null): Serializable