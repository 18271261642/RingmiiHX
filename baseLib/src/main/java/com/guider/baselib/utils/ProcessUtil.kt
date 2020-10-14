package com.guider.baselib.utils

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Process
import android.text.TextUtils
import androidx.annotation.Nullable

/**
 * @Package: com.guider.baselib.utils
 * @ClassName: ProcessUtil
 * @Description: 进程获取工具类
 * @Author: hjr
 * @CreateDate: 2020/9/28 15:43
 * Copyright (C), 1998-2020, GuiderTechnology
 */
object ProcessUtil {
    private var currentProcessName: String? = null

    /**
     * @return 当前进程名
     */
    @Nullable
    fun getCurrentProcessName(context: Context): String? {
        if (!TextUtils.isEmpty(currentProcessName)) {
            return currentProcessName
        }

        //1)通过Application的API获取当前进程名
        currentProcessName = getCurrentProcessNameByApplication()
        if (!TextUtils.isEmpty(currentProcessName)) {
            return currentProcessName
        }

//        //2)通过反射ActivityThread获取当前进程名
//        currentProcessName = getCurrentProcessNameByActivityThread()
//        if (!TextUtils.isEmpty(currentProcessName)) {
//            return currentProcessName
//        }

        //3)通过ActivityManager获取当前进程名
        currentProcessName = getCurrentProcessNameByActivityManager(context)
        return currentProcessName
    }

    /**
     * 通过Application新的API获取进程名，无需反射，无需IPC，效率最高。
     */
    private fun getCurrentProcessNameByApplication(): String? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Application.getProcessName()
        } else null
    }

//    /**
//     * 通过反射ActivityThread获取进程名，避免了ipc
//     */
//    fun getCurrentProcessNameByActivityThread(): String? {
//        var processName: String? = null
//        try {
//            val declaredMethod: Method = Class.forName("android.app.ActivityThread", false, Application::class.java.getClassLoader())
//                    .getDeclaredMethod("currentProcessName", *arrayOfNulls<Class<*>?>(0))
//            declaredMethod.setAccessible(true)
//            val invoke: Any = declaredMethod.invoke(null, arrayOfNulls<Any>(0))
//            if (invoke is String) {
//                processName = invoke
//            }
//        } catch (e: Throwable) {
//            e.printStackTrace()
//        }
//        return processName
//    }

    /**
     * 通过ActivityManager 获取进程名，需要IPC通信
     */
    private fun getCurrentProcessNameByActivityManager(context: Context): String? {
        val pid: Int = Process.myPid()
        val am: ActivityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningAppList: List<ActivityManager.RunningAppProcessInfo> = am.runningAppProcesses
        for (processInfo in runningAppList) {
            if (processInfo.pid == pid) {
                return processInfo.processName
            }
        }
        return null
    }
}