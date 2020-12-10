package com.guider.baselib.utils

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Process
import android.util.Log
import com.guider.baselib.utils.StringUtil.Companion.isNotBlankAndEmpty
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException

/**
 * @Package: com.guider.gps.util
 * @ClassName: ProcessUtil
 * @Description: 进程获取工具类
 * @Author: hjr
 * @CreateDate: 2020/12/10 10:12
 * Copyright (C), 1998-2020, GuiderTechnology
 */
object ProcessUtil {
    private const val TAG = "ProcessUtil"
    private var sProcessName: String? = ""
    fun getCurrentProcessName(context: Context): String? {
        if (isNotBlankAndEmpty(sProcessName)) {
            return sProcessName
        }
        try {
            sProcessName = getCurrentProcessNameByActivityManager(context)
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
        if (isNotBlankAndEmpty(sProcessName)) {
            return sProcessName
        }
        try {
            sProcessName = currentProcessNameByApplication
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
        if (isNotBlankAndEmpty(sProcessName)) {
            return sProcessName
        }
        try {
            sProcessName = processNameByCmd
        } catch (e: Exception) {
            Log.e(TAG, e.toString(), e)
        }
        if (isNotBlankAndEmpty(sProcessName)) {
            return sProcessName
        }
        try {
            sProcessName = currentProcessNameByActivityThread
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
        return sProcessName
    }

    private fun getCurrentProcessNameByActivityManager(context: Context): String? {
        val pid = Process.myPid()
        val mActivityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (appProcess in mActivityManager.runningAppProcesses) {
            if (appProcess.pid == pid) {
                return appProcess.processName
            }
        }
        return null
    }

    private val currentProcessNameByApplication: String?
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Application.getProcessName()
        } else null
    private val processNameByCmd: String?
        get() {
            var reader: BufferedReader? = null
            try {
                reader = BufferedReader(FileReader("/proc/" + Process.myPid() + "/cmdline"))
                var processName = reader.readLine()
                if (isNotBlankAndEmpty(processName)) {
                    processName = processName.trim { it <= ' ' }
                }
                return processName
            } catch (e: Exception) {
                Log.e(TAG, "getProcessName read is fail. exception=$e")
            } finally {
                try {
                    reader?.close()
                } catch (e: IOException) {
                    Log.e(TAG, "getProcessName close is fail. exception=$e")
                }
            }
            return null
        }

    @get:SuppressLint("DiscouragedPrivateApi", "PrivateApi")
    val currentProcessNameByActivityThread: String?
        get() {
            var processName: String? = null
            try {
                val declaredMethod = Class.forName("android.app.ActivityThread",
                        false, Application::class.java.classLoader)
                        .getDeclaredMethod("currentProcessName", *arrayOfNulls<Class<*>?>(0))
                declaredMethod.isAccessible = true
                val invoke = declaredMethod.invoke(null, *arrayOfNulls(0))
                if (invoke is String) {
                    processName = invoke
                }
            } catch (e: Throwable) {
                Log.e(TAG, e.toString())
            }
            return processName
        }
}