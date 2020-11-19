package com.guider.baselib.utils

import com.guider.baselib.device.IUnit
import com.guider.baselib.device.UnitCN
import com.guider.baselib.device.UnitImpl
import com.guider.baselib.device.UnitTW
import com.guider.health.apilib.LanguageUtil.getCurrentLanguage

object UnitUtil {
    fun getIUnit(): IUnit {
        val language = getCurrentLanguage()
        return if (language == "tw") {
            UnitImpl(UnitTW())
        } else {
            UnitImpl(UnitCN())
        }
    }
}