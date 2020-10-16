package com.guider.baselib.utils

import android.annotation.SuppressLint
import android.content.Context
import com.guider.baselib.R
import java.text.ParseException
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
     * Description: UTC时间转化为北京时间
     * @param utcTime 本地时间字符串
     * @return utc时间字符串
     */
    @SuppressLint("SimpleDateFormat")
    fun uTCToLocal(utcTime: String?, format: String): String? {
        val utcTimeValue = utcTime?.replace("T", " ")
                ?.replace("Z", "")
        val sdf = SimpleDateFormat(DEFAULT_TIME_FORMAT_PATTERN)
        val date = sdf.parse(utcTimeValue!!)
        val calendar: Calendar = Calendar.getInstance()
        calendar.time = date!!
        calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) + 8)
        return DateUtil.dateToString(calendar.time, format)
    }

    //获取当前完整的日期和时间
    @SuppressLint("SimpleDateFormat")
    fun getNowDateTime(): String? {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return sdf.format(Date())
    }

    //获取当前日期
    @SuppressLint("SimpleDateFormat")
    fun getNowDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        return sdf.format(Date())
    }

    //前一天
    @SuppressLint("SimpleDateFormat")
    fun getYesterday(date: Date?): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val theCa: Calendar = Calendar.getInstance()
        theCa.time = date!!
        theCa.add(5, -1)
        val needDate = theCa.time
        return sdf.format(needDate)
    }

    //后一天
    @SuppressLint("SimpleDateFormat")
    fun getTomorrow(date: Date?): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val theCa: Calendar = Calendar.getInstance()
        theCa.time = date!!
        theCa.add(5, +1)
        val needDate = theCa.time
        return sdf.format(needDate)
    }

    //获取当前时间
    @SuppressLint("SimpleDateFormat")
    fun getNowTime(): String? {
        val sdf = SimpleDateFormat("HH:mm:ss")
        return sdf.format(Date())
    }

    //获取当前日期(精确到毫秒)
    @SuppressLint("SimpleDateFormat")
    fun getNowTimeDetail(): String? {
        val sdf = SimpleDateFormat("HH:mm:ss.SSS")
        return sdf.format(Date())
    }

    //获取今天是星期几
    @SuppressLint("SimpleDateFormat")
    fun getWeekOfDate(date: Date?): String? {
        val weekDays = arrayOf("星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六")
        val cal = Calendar.getInstance()
        cal.time = date
        var w = cal[Calendar.DAY_OF_WEEK] - 1
        if (w < 0) w = 0
        return weekDays[w]
    }

    //计算星期几
    @SuppressLint("SimpleDateFormat")
    private fun getDayOfWeek(dateTime: String): Int {
        val cal = Calendar.getInstance()
        if (dateTime == "") {
            cal.time = Date(System.currentTimeMillis())
        } else {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            var date: Date?
            try {
                date = sdf.parse(dateTime)
            } catch (e: ParseException) {
                date = null
                e.printStackTrace()
            }
            if (date != null) {
                cal.time = Date(date.time)
            }
        }
        return cal[Calendar.DAY_OF_WEEK]
    }

    //根据年月日计算是星期几并与当前日期判断  非昨天、今天、明天 则以星期显示
    fun Week(dateTime: String): String? {
        var week = ""
        var yesterday = ""
        var today = ""
        var tomorrow = ""
        yesterday = getYesterday(Date())
        today = getNowDate()
        tomorrow = getTomorrow(Date())
        when (dateTime) {
            yesterday -> {
                week = "昨天"
            }
            today -> {
                week = "今天"
            }
            tomorrow -> {
                week = "明天"
            }
            else -> {
                when (getDayOfWeek(dateTime)) {
                    1 -> week = "星期日"
                    2 -> week = "星期一"
                    3 -> week = "星期二"
                    4 -> week = "星期三"
                    5 -> week = "星期四"
                    6 -> week = "星期五"
                    7 -> week = "星期六"
                }
            }
        }
        return week
    }

    //今天的国际化时间的话不显示
    private fun internationalizationTime(context: Context, dateTime: String): String {
        return when (dateTime) {
            "昨天" -> {
                context.resources.getString(R.string.app_yesterday)
            }
            "明天" -> {
                context.resources.getString(R.string.app_tomorrow)
            }
            "星期日" -> {
                context.resources.getString(R.string.app_sunday)
            }
            "星期一" -> {
                context.resources.getString(R.string.app_monday)
            }
            "星期二" -> {
                context.resources.getString(R.string.app_tuesday)
            }
            "星期三" -> {
                context.resources.getString(R.string.app_wednesday)
            }
            "星期四" -> {
                context.resources.getString(R.string.app_thursday)
            }
            "星期五" -> {
                context.resources.getString(R.string.app_friday)
            }
            "星期六" -> {
                context.resources.getString(R.string.app_saturday)
            }
            else -> ""
        }
    }

    private fun getDateWithWeek(context: Context, dateTime: String): String? {
        //得到是否今天，昨天，及星期的显示
        val week = internationalizationTime(context,Week(dateTime)!!)
        //得到是否显示日期
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = sdf.parse(dateTime)
        val differentDays = CommonUtils.differentDays(date, Date())
        //得到当前日期在当前周的个数
        val cal = Calendar.getInstance()
        cal.time = Date()
        val w = cal[Calendar.DAY_OF_WEEK]
        val year = dateTime.substring(0, dateTime.indexOf("-"))
        val monthDay = dateTime.substring(dateTime.indexOf("-") + 1)
        val currentDate = sdf.format(Date())
        val currentYear = currentDate.substring(0, currentDate.indexOf("-"))
        val showDate = if (year < currentYear) "$year $monthDay" else monthDay
        if (differentDays > w) return "$showDate $week"
        return week
    }

    fun getDateWithWeekWithTime(context: Context,dateTime: String): String? {
        val localTime = uTCToLocal(dateTime, TIME_FORMAT_PATTERN1)
        val yearMonthDay = localTime?.substring(0, localTime.indexOf(" "))
        val dateWithWeek = getDateWithWeek(context,yearMonthDay!!)
        val hourMinute = localTime.substring(localTime.indexOf(" ") + 1)
        return "$dateWithWeek $hourMinute"
    }

    //将时间戳转化为对应的时间(10位或者13位都可以)
    @SuppressLint("SimpleDateFormat")
    fun formatTime(time: Long): String? {
        var times: String? = null
        times = if (time.toString().length > 10) { // 10位的秒级别的时间戳
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date(time * 1000))
        } else { // 13位的秒级别的时间戳
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time)
        }
        return times
    }

    //将时间字符串转为时间戳字符串
    @SuppressLint("SimpleDateFormat")
    fun getStringTimestamp(time: String?): String? {
        var timestamp: String? = null
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val longTime = sdf.parse(time).time / 1000
            timestamp = longTime.toString()
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return timestamp
    }
}