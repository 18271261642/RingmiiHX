package com.guider.health.apilib

import android.content.res.Resources
import android.os.Build
import android.util.Log
import java.util.*

/**
 * @Package: com.guider.health.apilib
 * @ClassName: LanguageUtil
 * @Description: 语言工具类
 * @Author: hjr
 * @CreateDate: 2020/11/19 17:56
 * Copyright (C), 1998-2020, GuiderTechnology
 */
object LanguageUtil {

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
}