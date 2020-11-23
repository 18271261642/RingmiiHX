package com.guider.health.apilib

import android.os.Looper
import android.util.Log
import com.guider.health.apilib.utils.ApiLibUtil
import com.guider.health.apilib.utils.ApiLibUtil.getCurrentLanguage
import com.guider.health.apilib.utils.ApiLibUtil.getCurrentToken
import com.guider.health.apilib.utils.ApiLibUtil.getTokenCache
import com.guider.health.apilib.utils.ApiLibUtil.setTokenCache
import com.guider.health.apilib.utils.MMKVUtil
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.*


class RequestHead : Interceptor {
    var acceptLanguage = "en"
    val TAG = this.javaClass.simpleName
    var appid = "--"
    val headers: Map<String, String>
        get() {
            val headers: MutableMap<String, String> = HashMap()
            headers["mac"] = ApiUtil.mac
            headers["appid"] = appid
            headers["Accept-Language"] = acceptLanguage
            return headers
        }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
//        val requestUrl = original.url.toString()
//        if (ApiLibUtil.verifyIsRequestWithToken(requestUrl)) {
//            Log.i(TAG, "需要带token请求")
//            val time = System.currentTimeMillis()
//            val token = if (!isTokenExpired()) {
//                /**token失效，发起同步请求*/
//                Log.i(TAG, "token失效，发起同步请求")
//                getNewToken()
//            } else {
//                /**token有效*/
//                Log.i(TAG, "token有效")
//                getCurrentToken()
//            }
//            Log.i(TAG, "取到token为${token}，耗时：${System.currentTimeMillis() - time} " +
//                    ",继续执行原请求：$requestUrl")
//        } else {
//            Log.i(TAG, "不需要带token请求")
//        }
        val requestBuilder = original.newBuilder()
                .addHeader("mac", ApiUtil.mac)
                .addHeader("appid", appid)
                .addHeader("Accept-Language", acceptLanguage)
        val request = requestBuilder.build()
        return chain.proceed(request)
    }

    @Synchronized
    private fun getNewToken(): String? {
        return if (!isTokenExpired()) {
            /**同步方法中二次确认token失效*/
            val call = GuiderApiUtil.getApiService().refreshTokenCall(
                    getCurrentToken(), MMKVUtil.getString(ApiLibUtil.REFRESH_TOKEN))
            val tokenBean = call?.execute()?.body()
            if (tokenBean != null) {
                setTokenCache(tokenBean)
                getCurrentToken()
            } else {
                if (!BuildConfig.DEBUG) {
                    ""
                } else {
                    throw IOException("no token result exception!!!")
                }
            }
        } else {
            /**同步方法中二次确认token有效*/
            getCurrentToken()
        }
    }

    fun isMainThread(): Boolean {
        return Looper.getMainLooper() == Looper.myLooper()
    }

    init {
        appid = "b3c327c04d8b9f9s"
        val language = getCurrentLanguage()
        acceptLanguage = when (language) {
            "tw" -> "tw"
            "cn" -> "zh"
            else -> "en"
        }
        Log.i(TAG, language + "lan " + acceptLanguage)
    }

    private fun isTokenExpired(): Boolean {
        val tokenBean = getTokenCache()
        if (tokenBean != null) {
            Log.i(TAG, "当前token info:==>$tokenBean<==")
            /**token过期提前30分钟*/
            var expireTime = (tokenBean.expired - 1800) * 1000

            /**token过期测试使用，30s过期*/
            expireTime = 3 * 1000
            val sinceUpdate = System.currentTimeMillis() - tokenBean.updateTime
            return sinceUpdate < expireTime
        } else {
            Log.i(TAG, "当前不存在token info")
        }
        return false
    }
}