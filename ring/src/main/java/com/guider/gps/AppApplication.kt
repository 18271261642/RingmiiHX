package com.guider.gps

import android.annotation.SuppressLint
import android.os.Build
import com.guider.baselib.base.BaseApplication

class AppApplication : BaseApplication() {

    override fun init() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P){
            closeAndroidPDialog()
        }
    }

    @SuppressLint("PrivateApi", "DiscouragedPrivateApi")
    private fun closeAndroidPDialog() {
//        try {
//            val aClass = Class.forName("android.content.pm.PackageParser${}")
//            val declaredConstructor = aClass.getDeclaredConstructor(String::class.java)
//            declaredConstructor.isAccessible = true
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }

        try {
            val cls = Class.forName("android.app.ActivityThread")
            val declaredMethod = cls.getDeclaredMethod("currentActivityThread")
            declaredMethod.isAccessible = true
            val activityThread = declaredMethod.invoke(null)
            val declaredFields = cls.declaredFields
            val mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown")
            if (declaredFields.contains(mHiddenApiWarningShown)) {
                mHiddenApiWarningShown.isAccessible = true
                mHiddenApiWarningShown.setBoolean(activityThread, true)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}