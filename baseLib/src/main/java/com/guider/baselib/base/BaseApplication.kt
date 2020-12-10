package com.guider.baselib.base

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.multidex.MultiDex
import cat.ereza.customactivityoncrash.CustomActivityOnCrash
import com.alibaba.android.arouter.launcher.ARouter
import com.guider.baselib.utils.MMKV_ROOT
import com.guider.baselib.utils.MyUtils
import com.guider.baselib.utils.ProcessUtil
import com.guider.health.apilib.ApiUtil
import com.guider.health.apilib.BuildConfig
import com.guider.health.apilib.GuiderApiUtil
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import me.jessyan.autosize.AutoSize
import me.jessyan.autosize.AutoSizeConfig

@Suppress("JAVA_CLASS_ON_COMPANION")
abstract class BaseApplication : Application() {

    val TAG = BaseApplication.javaClass.simpleName

    @SuppressLint("RestrictedApi")
    override fun onCreate() {
        super.onCreate()
        guiderHealthContext = applicationContext
        //判断当前进程是否为主进程，那么初始化主进程
        if (!isMainProcess()) {
            Log.i("Application", "不是主进程，不再执行初始化application")
            return
        }
        MMKV_ROOT = MMKV.initialize(guiderHealthContext)
        asyncInitSdk()
    }

    private fun asyncInitSdk() {
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            initSdk()
        }
    }

    @SuppressLint("RestrictedApi")
    private fun initSdk() {
        //MMKV初始化
        MyUtils.setMacAddress("11:11:11:11:11:26")
        ApiUtil.init(guiderHealthContext, MyUtils.getMacAddress())
        GuiderApiUtil.setContextAndMac(guiderHealthContext, MyUtils.getMacAddress())
        initAutoSize()
        MyUtils.application = this
        init()
        if (BuildConfig.DEBUG) {
            CustomActivityOnCrash.install(guiderHealthContext)
        } else
            initBugly()
        //ARouter 初始化
        ARouter.init(this) // 尽可能早，推荐在Application中初始化
    }

    override fun onTerminate() {
        super.onTerminate()
        ARouter.getInstance().destroy()
    }

    /**
     * 各自可以实现sdk的初始化操作
     */
    abstract fun init()

    private fun initBugly() {
        val context = applicationContext
        // 设置是否为上报进程
        val strategy = CrashReport.UserStrategy(context)
        strategy.isUploadProcess = isMainProcess()
        // 初始化bugly 建议在测试阶段建议设置成true，发布时设置为false
        CrashReport.initCrashReport(applicationContext, "78e0c0fa77", BuildConfig.DEBUG, strategy)
    }

    private fun initAutoSize() {
        AutoSizeConfig.getInstance().isCustomFragment = true
        //多进程
        AutoSize.initCompatMultiProcess(this)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        applicationTime = System.currentTimeMillis()
        MultiDex.install(this)
    }

    companion object {
        // 获取到主线程的上下文
        @SuppressLint("StaticFieldLeak")
        lateinit var guiderHealthContext: Context
        var applicationTime = 0L
    }

    private fun isMainProcess(): Boolean {
        //获取当前进程名，并与主进程对比，来判断是否为主进程
        val processName = ProcessUtil.getCurrentProcessName(this)
        Log.e(TAG, "isMainProcess processName=$processName")
        // 获取当前包名
        val packageName = applicationContext.packageName
        Log.e(TAG, "packageName=$packageName")
        return packageName == processName
    }
}