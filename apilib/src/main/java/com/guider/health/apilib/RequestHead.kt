package com.guider.health.apilib

import android.util.Log
import com.guider.health.apilib.LanguageUtil.getCurrentLanguage
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.*

class RequestHead : Interceptor {
    var acceptLanguage = "en"
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
        val requestBuilder = original.newBuilder()
                .addHeader("mac", ApiUtil.mac)
                .addHeader("appid", appid)
                .addHeader("Accept-Language", acceptLanguage)
        val request = requestBuilder.build()
        return chain.proceed(request)
    }

    init {
        appid = "b3c327c04d8b9f9s"
        val language = getCurrentLanguage()
        acceptLanguage = when (language) {
            "tw" -> "tw"
            "cn" -> "zh"
            else -> "en"
        }
        Log.i("RequestHead", language + "lan " + acceptLanguage)
    }
}