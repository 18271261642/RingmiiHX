package com.guider.health.apilib.utils

import android.content.res.Resources
import android.os.Build
import android.util.Log
import com.guider.health.apilib.bean.TokenBean
import java.util.*

/**
 * @Package: com.guider.health.apilib
 * @ClassName: LanguageUtil
 * @Description: ApiLib的相关使用的工具类
 * @Author: hjr
 * @CreateDate: 2020/11/19 17:56
 * Copyright (C), 1998-2020, GuiderTechnology
 */
object ApiLibUtil {
    const val TOKEN = "token"//用户token
    const val REFRESH_TOKEN = "refresh_token"//刷新token
    const val TOKEN_CACHE = "token_cache"

    /**
     * 获取当前系统语言
     */
    fun getCurrentLanguage(): String {
        val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Resources.getSystem().configuration.locales.get(0)
        } else {
            Resources.getSystem().configuration.locale
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val toLanguageTag = locale.toLanguageTag()
            Log.i("LanguageUtil", toLanguageTag)
            if (toLanguageTag.contains("-")) {
                val substring = toLanguageTag.substring(
                        toLanguageTag.indexOf("-") + 1)
                if (substring.contains("-")) {
                    //有语言有地区
                    val substring1 = substring.substring(0, substring.indexOf("-"))
                    return when {
                        substring1.contains("Hant") -> {
                            Log.i("LanguageUtil", "tw")
                            "tw"
                        }
                        substring1.contains("Hans") -> {
                            Log.i("LanguageUtil", "cn")
                            "cn"
                        }
                        else -> {
                            Log.i("LanguageUtil", "en")
                            "en"
                        }
                    }
                } else {
                    //只有语言
                    val lowerCase = substring.toLowerCase(Locale.ROOT)
                    return when {
                        lowerCase.contains("tw") -> {
                            Log.i("LanguageUtil", "tw")
                            "tw"
                        }
                        lowerCase.contains("cn") -> {
                            Log.i("LanguageUtil", "cn")
                            "cn"
                        }
                        else -> {
                            Log.i("LanguageUtil", "en")
                            "en"
                        }
                    }
                }
            } else {
                Log.i("LanguageUtil", "cn")
                "cn"
            }
        } else {
            Log.i("LanguageUtil", "cn")
            "cn"
        }
    }

    /**
     * 验证是否需要带token请求
     * @param url 请求的url
     * @return 返回true需要带，false不需要带
     */
    fun verifyIsRequestWithToken(url: String): Boolean {
        return when {
            /*用户名密码登录*/
            url.contains("api/v1/login/phonewithpasswd")
                    /*注册*/
                    || url.contains("api/v2/register/phonewithpasswd")
                    /*第三方登录前检测是否绑定手机号, 返回数据带UserInfo*/
                    || url.contains("api/v1/accountthird/verify/login")
                    /*第三方账号登录并绑定手机号*/
                    || url.contains("api/v1/accountthird/phone/login")
                    /*微信登录*/
                    || url.contains("api/v2/third/login/wachat/tokeninfo")
                    /*手机号注册验证*/
                    || url.contains("api/v1/register/phone/check")
                    /*线程内刷新token*/
                    || url.contains("api/v1/refreshtoken")
            -> {
                false
            }
            else -> true
        }
    }

    fun isNotBlankAndEmpty(str: String?): Boolean {
        return str != null && str.isNotBlank() && str != "" && str != "null" && str != "NULL"
    }

    fun setTokenCache(tokenBean: TokenBean?) {
        if (tokenBean != null && isNotBlankAndEmpty(tokenBean.token)) {
            MMKVUtil.saveString(TOKEN, tokenBean.token ?: "")
            MMKVUtil.saveString(REFRESH_TOKEN,tokenBean.refreshToken?:"")
            /**存储token获取时间，用于计算当前token有效期是否过期*/
            tokenBean.updateTime = System.currentTimeMillis()
            MMKVUtil.saveObject(TOKEN_CACHE, tokenBean)
        } else {
            MMKVUtil.clearByKey(TOKEN)
            MMKVUtil.clearByKey(REFRESH_TOKEN)
            MMKVUtil.clearByKey(TOKEN_CACHE)
        }
    }

    fun getCurrentToken(): String {
        return MMKVUtil.getString(TOKEN)
    }

    fun getTokenCache(): TokenBean? {
        val bean = MMKVUtil.getObject(TOKEN_CACHE, TokenBean::class.java)
        if (bean != null) {
            return bean
        }
        return null
    }
}