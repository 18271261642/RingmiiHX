package com.guider.healthring.util

import android.annotation.SuppressLint
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

object CommonUtil {

    fun getCurrentDate(format: String = "yyyy/MM/dd"): String {
        @SuppressLint("SimpleDateFormat")
        val sdf = SimpleDateFormat(format)
        val today = Date()
        return sdf.format(today)
    }

    //计算当前日期前时间
    @SuppressLint("WrongConstant")
    fun calTimeFrontDate(date: String, days: Int, format: String = "yyyy/MM/dd"): String {
        val sdf = SimpleDateFormat(format)
        val theCa: Calendar = Calendar.getInstance()
        theCa.time = sdf.parse(date)
        theCa.add(5, -days)
        val needDate = theCa.time
        return sdf.format(needDate)
    }

    //计算日期是否符合要求 日期前后
    fun calTimeDateCompareNew(date: String, dateCompare: String): Boolean {
        val format = "yyyy/MM/dd"

        @SuppressLint("SimpleDateFormat")
        val sdf = SimpleDateFormat(format)
        val dayParse = sdf.parse(date)
        val dateCompareParse = sdf.parse(dateCompare)
        return if (dayParse.time > dateCompareParse.time) {
            Log.i("DateCompare", "dt1 在dt2前")
            true
        } else {
            false
        }
    }

    /**
     * date2比date1多的天数
     * @param date1
     * @param date2
     * @return
     */
    open fun differentDays(date1: Date?, date2: Date?): Int {
        val cal1: Calendar = Calendar.getInstance()
        cal1.time = date1
        val cal2: Calendar = Calendar.getInstance()
        cal2.time = date2
        val day1: Int = cal1.get(Calendar.DAY_OF_YEAR)
        val day2: Int = cal2.get(Calendar.DAY_OF_YEAR)
        val year1: Int = cal1.get(Calendar.YEAR)
        val year2: Int = cal2.get(Calendar.YEAR)
        return if (year1 != year2) //不同一年
        {
            var timeDistance = 0
            for (i in year1 until year2) {
                timeDistance += if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) //闰年
                {
                    366
                } else  //不是闰年
                {
                    365
                }
            }
            timeDistance + (day2 - day1)
        } else  //同一年
        {
            day2 - day1
        }
    }
}