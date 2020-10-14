package com.guider.health.apilib

import com.guider.health.apilib.bean.AreCodeBean
import retrofit2.Call
import retrofit2.http.GET

interface JsonCoroutinesApi {
    /**
     * 获取国家区号
     */
    @get:GET("areaCode.json")
    val countryCode: Call<List<AreCodeBean>>
}