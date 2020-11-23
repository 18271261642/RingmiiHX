package com.guider.health.apilib.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

object GsonUtil {
    fun getGson(): Gson {
        val gson by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            Gson()
        }
        return gson
    }

    fun toJson(obj: Any?): String = getGson().toJson(obj)

    fun <T> fromJson(json: String?, clazz: Class<T>): T? {
        if (json == null || json.isNullOrBlank()) return null
        return getGson().fromJson(json, clazz)
    }

    fun <T> fromJson(json: String?, type: Type): List<T>? {
        if (json == null || json.isNullOrBlank()) return null
        return getGson().fromJson(json, type)
    }

    fun <T> getList(json: String): List<T>? {
        if (json.isNullOrBlank()) return null
        return getGson().fromJson(json, object : TypeToken<List<T>>() {}.type)
    }

    inline fun <reified T> fromJson(jsonStr: String): T = getGson().fromJson(jsonStr, T::class.java)
}