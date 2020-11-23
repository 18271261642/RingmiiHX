package com.guider.health.apilib.utils

import com.tencent.mmkv.MMKV
import java.lang.reflect.Type

/**
 * MMKV存储工具类
 */
object MMKVUtil {

    //初始化
    fun getMMKv(): MMKV {
        val kv by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            MMKV.defaultMMKV()
        }
        return kv
    }

    fun containKey(key: String): Boolean {
        return getMMKv().containsKey(key)
    }

    fun saveInt(key: String, value: Int) {
        getMMKv().encode(key, value)
    }

    fun saveLong(key: String, value: Long) {
        getMMKv().encode(key, value)
    }

    fun saveDouble(key: String, value: Double) {
        getMMKv().encode(key, value)
    }

    fun saveString(key: String, value: String) {
        getMMKv().encode(key, value)
    }

    fun saveObject(key: String, value: Any) {
        getMMKv().encode(key, GsonUtil.toJson(value))
    }

    fun saveStringSet(key: String, value: Set<String>) {
        getMMKv().encode(key, value)
    }

    fun getInt(key: String, defaultValue: Int = 0): Int {
        return getMMKv().decodeInt(key, defaultValue)
    }

    fun getLong(key: String, defaultValue: Long = 0L): Long {
        return getMMKv().decodeLong(key, defaultValue)
    }

    fun getDouble(key: String, defaultValue: Double = 0.0): Double {
        return getMMKv().decodeDouble(key, defaultValue)
    }

    fun getString(key: String, defaultValue: String = ""): String {
        return getMMKv().decodeString(key, defaultValue)
    }

    fun <T> getObject(key: String, clazz: Class<T>): T? {
        val json = getString(key)
        return GsonUtil.fromJson(json, clazz)
    }

    fun saveList(key: String, list: List<Any>) {
        val toJson = GsonUtil.toJson(list)
        getMMKv().encode(key, toJson)
    }

    fun <T> getList(key: String, type: Type): List<T>? {
        val json = getString(key)
        return GsonUtil.fromJson(json, type)
    }

    fun getStringSet(key: String, defaultValue: Set<String> = hashSetOf()): Set<String> {
        if (getMMKv().containsKey(key)) {
            return getMMKv().decodeStringSet(key, defaultValue)
        }
        return defaultValue
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return getMMKv().getBoolean(key, defaultValue)
    }

    fun saveBoolean(key: String, value: Boolean) {
        getMMKv().encode(key, value)
    }

    fun clearByKey(key: String) {
        if (getMMKv().containsKey(key)) {
            getMMKv().removeValueForKey(key)
        }
    }

    fun clearAll() {
        getMMKv().clearAll()
    }
}