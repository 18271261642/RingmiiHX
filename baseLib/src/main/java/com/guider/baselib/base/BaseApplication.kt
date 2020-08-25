package com.guider.baselib.base

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Process
import androidx.multidex.MultiDex
import android.text.TextUtils
import android.util.Log
import com.guider.baselib.utils.MMKV_ROOT
import com.guider.baselib.utils.MyUtils
import com.guider.health.apilib.ApiUtil
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.mmkv.MMKV
import me.jessyan.autosize.AutoSize
import me.jessyan.autosize.AutoSizeConfig
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException

abstract class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        guiderHealthContext = applicationContext
        // 获取当前包名
        val packageName = applicationContext.packageName
        // 获取当前进程名
        val processName = getProcessName(Process.myPid())
        if (processName != packageName) {
            Log.i("Application","不是主进程，不再执行初始化application")
            return
        }
        //MMKV初始化
        MMKV_ROOT = MMKV.initialize(guiderHealthContext)
        MyUtils.setMacAddress("11:11:11:11:11:16")
        ApiUtil.init(guiderHealthContext,MyUtils.getMacAddress())
        initAutoSize()
        MyUtils.application = this
        init()
        initBugly()
    }

    /**
     * 各自可以实现sdk的初始化操作
     */
    abstract fun init()

    private fun initBugly() {
        val context = applicationContext
        // 获取当前包名
        val packageName = context.packageName
        // 获取当前进程名
        val processName = getProcessName(Process.myPid())
        // 设置是否为上报进程
        val strategy = CrashReport.UserStrategy(context)
        strategy.isUploadProcess = processName == null || processName == packageName
        // 初始化bugly 建议在测试阶段建议设置成true，发布时设置为false
        CrashReport.initCrashReport(applicationContext, "78e0c0fa77", true, strategy)
    }

    private fun initAutoSize() {
        AutoSizeConfig.getInstance().isCustomFragment = true
        //多进程
        AutoSize.initCompatMultiProcess(this)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    companion object{
        // 获取到主线程的上下文
        @SuppressLint("StaticFieldLeak")
        lateinit var guiderHealthContext: Context
    }

    private fun getProcessName(pid: Int): String? {
        var reader: BufferedReader? = null
        try {
            reader = BufferedReader(FileReader("/proc/$pid/cmdline"))
            var processName = reader.readLine()
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim { it <= ' ' }
            }
            return processName
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        } finally {
            try {
                reader?.close()
            } catch (exception: IOException) {
                exception.printStackTrace()
            }

        }
        return null
    }
}