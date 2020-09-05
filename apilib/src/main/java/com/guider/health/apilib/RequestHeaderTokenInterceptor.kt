package com.guider.health.apilib

import android.util.Log
import com.tencent.mmkv.MMKV
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * 请求头拦截器
 */
class RequestHeaderTokenInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originRequest = chain.request()
        val url = originRequest.url.toString()
        return if (verifyRequestHeader(url)){
            chain.proceed(originRequest)
        }else {
            val time = System.currentTimeMillis()
            Log.e("tokenInterceptor","需要校验token是否失效")
            val token = ""
//                    if (!isTokenExpired()) {
//                /**token失效，发起同步请求*/
//                Log.e("tokenInterceptor","token失效，发起同步请求")
//                getNewToken()
//            } else {
//                /**token有效*/
//                Log.e("tokenInterceptor","token有效")
//                MMKV.defaultMMKV().decodeString("token", "")
//            }
            Log.e("tokenInterceptor","取到token，耗时：" +
                    "${System.currentTimeMillis() - time} " +
                    ",继续执行原请求：$url")
            val builder = originRequest.newBuilder()
                    .header("tokenKey", token)
                    .method(originRequest.method, originRequest.body)
            val request = builder.build()
            chain.proceed(request)
        }
    }

//    @Synchronized
//    private fun getNewToken(): String {
//        return if (!isTokenExpired()) {
//            /**同步方法中二次确认token失效*/
//            val url = version_mode.tokenServer + "oauth/token"
//            val call = DdbesApiUtil.getTokenService().refreshTokenCall(
//                    DEFAULT_TOKEN, url, BaseApplication.CacheHolder.getCurrentUserRefreshToken())
//            val tokenBean = call.execute().body()
//            if (tokenBean != null) {
//                BaseApplication.CacheHolder.setTokenCache(tokenBean)
//                BaseApplication.CacheHolder.getCurrentUserToken()
//            } else {
//                if (local == ServerEnv.PRO) {
//                    ""
//                } else {
//                    throw IOException("no token result exception!!!")
//                }
//            }
//        } else {
//            /**同步方法中二次确认token有效*/
//            BaseApplication.CacheHolder.getCurrentUserToken()
//        }
//    }
//
//    private fun isTokenExpired(): Boolean {
//        val token = MMKV.defaultMMKV().decodeString("token", "")
//        val expiredTime = MMKV.defaultMMKV().decodeInt("expired_time", "")
//        if (token != null) {
//            Log.e("tokenInterceptor",
//                    "当前token info:==>${token}<==")
//            /**token过期提前30分钟*/
//            val expireTime = (expiredTime - 1800) * 1000
//            /**token过期测试使用，一分钟过期*/
////            expireTime = 60*000
//            val sinceUpdate = System.currentTimeMillis() - tokenBean.updateTime
//            return sinceUpdate < expireTime
//        } else {
//            Log.e("tokenInterceptor","当前不存在token info")
//        }
//        return false
//    }

    private fun verifyRequestHeader(url: String): Boolean {
        return when {
            /*注册接口*/
            url.contains("api/v2/register/phonewithpasswd") ||
                    /*登录接口*/
                    url.contains("api/v1/login/phonewithpasswd") -> {
                true
            }
            else -> false
        }
    }
}