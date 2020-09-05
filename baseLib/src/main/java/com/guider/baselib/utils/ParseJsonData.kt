package com.guider.baselib.utils

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

/**
 * 解决json返回data为LinkTreeMap的问题
 */
object ParseJsonData {

    /**
     * 解决json解析kotlin的Any问题
     * @param data 数据Any
     * @param T 数据bean类
     */
    inline fun <reified T> parseJsonAny(data: Any): T {
        val gson = GsonBuilder().enableComplexMapKeySerialization().create()
        val jsonString = gson.toJson(data)
        return gson.fromJson(jsonString, T::class.java)
    }

    /**
     * 解决json解析kotlin中集合问题
     * @param data 数据Any
     * @param T 数据bean类
     */
    fun <T> parseJsonDataList(data: Any): List<T> {
        val gson = GsonBuilder().enableComplexMapKeySerialization().create()
        val jsonString = gson.toJson(data)
        return gson.fromJson(jsonString,
                object : TypeToken<List<T>>() {}.type)
    }
}