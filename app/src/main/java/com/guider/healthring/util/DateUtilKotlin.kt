package com.guider.healthring.util

import android.annotation.SuppressLint
import com.guider.health.common.core.DateUtil
import java.text.SimpleDateFormat
import java.util.*

object DateUtilKotlin {

    /**
     * Description: 本地时间转化为UTC时间
     * @param localTime 本地时间字符串
     * @return utc时间字符串
     */
    @SuppressLint("SimpleDateFormat")
    fun localToUTC(localTime: String?): String? {
        return DateUtil.dateToString(
                DateUtil.localToUtc(DateUtil.stringToDate(localTime)),
                "yyyy-MM-dd'T'HH:mm:ss'Z'")
    }

    /**
     * Description: 本地时间转化为UTC时间
     * @param utcTime 本地时间字符串
     * @return utc时间字符串
     */
    @SuppressLint("SimpleDateFormat")
    fun uTCToLocal(utcTime: String?, format: String): String? {
        val utcTimeValue = utcTime?.replace("T", " ")
                ?.replace("Z", "")
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date = sdf.parse(utcTimeValue!!)
        val calendar: Calendar = Calendar.getInstance()
        calendar.time = date!!
        calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) + 8)
        return DateUtil.dateToString(calendar.time, format)
    }
}