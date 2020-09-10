package com.guider.baselib.utils

import com.google.gson.GsonBuilder
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

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
    fun <T> parseJsonDataList(data: Any,clazz: Class<*>): List<T> {
        val gson = GsonBuilder().enableComplexMapKeySerialization().create()
        val jsonString = gson.toJson(data)
        val type = ParameterizedTypeImpl(clazz)
        return gson.fromJson(jsonString,type)
    }

    /**
     * 反射解析list内部的bean类
     */
    class ParameterizedTypeImpl(var clazz: Class<*>) : ParameterizedType {

        override fun getActualTypeArguments(): Array<Type> {
            return arrayOf(clazz)
        }

        override fun getRawType(): Type {
            return MutableList::class.java
        }

        override fun getOwnerType(): Type? {
            return null
        }

    }
}