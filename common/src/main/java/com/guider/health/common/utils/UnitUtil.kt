package com.guider.health.common.utils

import android.content.res.Resources
import android.os.Build
import android.util.Log
import com.guider.health.common.device.IUnit
import com.guider.health.common.device.UnitCN
import com.guider.health.common.device.UnitImpl
import com.guider.health.common.device.UnitTW
import java.util.*

object UnitUtil {
    
    fun getIUnit(): IUnit {
        val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Resources.getSystem().configuration.locales.get(0)
        } else {
            Resources.getSystem().configuration.locale
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val toLanguageTag = locale.toLanguageTag()
            Log.i("UnitUtil", toLanguageTag)
            if (toLanguageTag.contains("-")) {
                val substring = toLanguageTag.substring(
                        toLanguageTag.indexOf("-") + 1)
                if (substring.contains("-")) {
                    //有语言有地区
                    val substring1 = substring.substring(0, substring.indexOf("-"))
                    if (substring1.contains("Hant")) {
                        Log.i("UnitUtil", "tw")
                        UnitImpl(UnitTW())
                    } else {
                        Log.i("UnitUtil", "cn")
                        UnitImpl(UnitCN())
                    }
                } else {
                    //只有语言
                    val lowerCase = substring.toLowerCase(Locale.ROOT)
                    if (lowerCase.contains("tw")) {
                        Log.i("UnitUtil", "tw")
                        UnitImpl(UnitTW())
                    } else {
                        Log.i("UnitUtil", "cn")
                        UnitImpl(UnitCN())
                    }
                }
            } else UnitImpl(UnitCN())
        } else UnitImpl(UnitCN())
    }
}