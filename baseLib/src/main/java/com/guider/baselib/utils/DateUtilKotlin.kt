package com.guider.baselib.utils

import android.annotation.SuppressLint

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
        return DateUtil.dateToString(
                DateUtil.utcToLocal(DateUtil.stringToDate(utcTimeValue)), format)
    }
}