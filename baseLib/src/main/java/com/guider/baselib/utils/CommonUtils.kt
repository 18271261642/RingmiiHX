package com.guider.baselib.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Environment
import android.util.Log
import androidx.core.content.ContextCompat
import com.luck.picture.lib.language.LanguageConfig
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object CommonUtils {
    /**
     * 得到drawable
     */
    fun getDrawable(context: Context, id: Int): Drawable {
        return ContextCompat.getDrawable(context, id)!!
    }

    /**
     * 得到color
     */
    fun getColor(context: Context, id: Int): Int {
        return ContextCompat.getColor(context, id)
    }


    fun getCurrentDate(format: String = "yyyy-MM-dd"): String {
        @SuppressLint("SimpleDateFormat")
        val sdf = SimpleDateFormat(format)
        val today = Date()
        return sdf.format(today)
    }

    //计算当前日期前时间
    @SuppressLint("WrongConstant", "SimpleDateFormat")
    fun calTimeFrontDate(date: String, days: Int, format: String = "yyyy-MM-dd"): String {
        val sdf = SimpleDateFormat(format)
        val theCa: Calendar = Calendar.getInstance()
        theCa.time = sdf.parse(date)
        theCa.add(5, -days)
        val needDate = theCa.time
        return sdf.format(needDate)
    }

    //计算当前日期前几年的时间
    @SuppressLint("WrongConstant", "SimpleDateFormat")
    fun calTimeFrontYear(date: String, years: Int,
                         format: String = DEFAULT_TIME_FORMAT_PATTERN): String {
        val sdf = SimpleDateFormat(format)
        val rightNow = Calendar.getInstance()
        rightNow.time = sdf.parse(date)
        rightNow.add(Calendar.YEAR, -years)
        val dt1 = rightNow.time
        return sdf.format(dt1)
    }

    //计算当前日期前小时的时间
    @SuppressLint("WrongConstant", "SimpleDateFormat")
    fun calTimeFrontHour(date: String, hours: Int): String {
        val sdf = SimpleDateFormat(DEFAULT_TIME_FORMAT_PATTERN)
        val rightNow = Calendar.getInstance()
        rightNow.time = sdf.parse(date)
        rightNow.add(Calendar.HOUR, -hours)
        val dt1 = rightNow.time
        return sdf.format(dt1)
    }

    //计算日期是否符合要求 日期前后
    fun calTimeDateCompareNew(date: String, dateCompare: String): Boolean {
        val format = "yyyy-MM-dd"

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
    fun differentDays(date1: Date?, date2: Date?): Int {
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

    fun getPKgVersionName(ctx: Context): String? {
        return try {
            if (getPackageInfo(ctx) != null) {
                val pi: PackageInfo = getPackageInfo(ctx)!!
                pi.versionName
            } else ""
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * 得到当前应用的版本号
     */
    private fun getPackageInfo(ctx: Context): PackageInfo? {
        val pm = ctx.packageManager
        var pi: PackageInfo? = null
        try {
            pi = pm.getPackageInfo(ctx.packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return pi
    }

    /**
     * 判断是否安装了微信
     */
    fun isHaveWeChat(mContext: Context): Boolean {
        val packageManager = mContext.packageManager// 获取packagemanager
        val pinfo = packageManager
                .getInstalledPackages(0)// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (i in pinfo.indices) {
                val pn = pinfo[i].packageName
                if (pn.equals("com.tencent.mm", ignoreCase = true)) {
                    return true
                }
            }
        }
        return false
    }

    fun logOutClearMMKV() {
        var phone = ""
        if (MMKVUtil.containKey(USER.PHONE) &&
                StringUtil.isNotBlankAndEmpty(MMKVUtil.getString(USER.PHONE))) {
            phone = MMKVUtil.getString(USER.PHONE)
        }
        var countryCode = ""
        if (MMKVUtil.containKey(USER.COUNTRY_CODE) &&
                StringUtil.isNotBlankAndEmpty(MMKVUtil.getString(USER.COUNTRY_CODE))) {
            countryCode = MMKVUtil.getString(USER.COUNTRY_CODE)
        }
        MMKVUtil.clearAll()
        if (StringUtil.isNotBlankAndEmpty(phone))
            MMKVUtil.saveString(USER.PHONE, phone)
        if (StringUtil.isNotBlankAndEmpty(countryCode))
            MMKVUtil.saveString(USER.COUNTRY_CODE, countryCode)
        MMKVUtil.saveBoolean(IS_FIRST_START, true)
    }

    /**
     * 自定义压缩存储地址
     * @return
     */
    fun getCompressPath(): String {
        val path = Environment.getExternalStorageDirectory().path + CROP_PATH_NEW
        val file = File(path)
        return if (file.mkdirs()) {
            path
        } else path
    }

    /**
     * 图片挑选库获得当前系统语言
     */
    fun getCurrentLanguage(context: Context): Int {
        val language = Locale.getDefault().language.toLowerCase(Locale.ROOT)
        return if (language.contains("hk") || language.contains("tw"))
            LanguageConfig.TRADITIONAL_CHINESE
        else if (language.contains("cn")) LanguageConfig.CHINESE
        else LanguageConfig.ENGLISH
    }
}