package com.guider.baselib.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import com.guider.baselib.base.BaseApplication
import com.guider.baselib.utils.permissions.RxPermissions
import com.guider.feifeia3.utils.ToastUtil

/**
 * Description 权限Util
 * Author HJR36
 * Date 2018/6/23 17:10
 */
object PermissionUtils {

    private const val SETTINGS_REQ_CODE = 16061

    /**
     * activity中请求权限
     * @param activity 当前activity
     * @param perms 权限数组
     * @param onSuccess 成功回调
     * @param hint 权限名称
     * @param onError 失败回调
     */
    @SuppressLint("CheckResult")
    fun requestPermissionActivity(activity: Activity, perms: Array<String>, hint: String,
                                  onSuccess: () -> Unit,
                                  onError: () -> Unit) {

        if (checkPermissions(activity, perms)) {
            onSuccess.invoke()
        } else {
            val rxPermissions = RxPermissions(activity) // where this is an Activity
            rxPermissions.requestEachCombined(*perms)
                    .subscribe { permission ->
                        run {
                            when {
                                permission.granted -> // 用户已经同意该权限 !
                                    onSuccess.invoke()
                                permission.shouldShowRequestPermissionRationale -> {
                                    // At least one denied permission without ask never again
                                    // 用户拒绝了该权限，没有选中『不再询问』
                                    // （Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                                    onError.invoke()
                                }
                                else -> {
                                    // At least one denied permission with ask never again
                                    // 用户拒绝了该权限，并且选中『不再询问』
                                    // Need to go to the settings
                                    ToastUtil.show(BaseApplication.guiderHealthContext,
                                            "您已经拒绝${hint}权限，要使用需要去设置中开启")
                                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                    val uri = Uri.fromParts("package",
                                            activity.packageName, null)
                                    intent.data = uri
                                    startAppSettingsScreen(activity, intent)
                                }
                            }
                        }
                    }
        }
    }

    /**
     * fragment中请求权限
     * @param fragment fragment
     * @param perms 权限数组
     * @param onSuccess 成功回调
     * @param hint 权限名称
     * @param onError 失败回调
     */
    @SuppressLint("CheckResult")
    fun requestPermissionFragment(fragment: Fragment, perms: Array<String>, hint: String,
                                  onSuccess: () -> Unit,
                                  onError: () -> Unit) {
        if (checkPermissions(fragment.activity!!, perms)) {
            onSuccess.invoke()
        } else {
            val rxPermissions = RxPermissions(fragment.activity!!) // where this is an Fragment instance
            rxPermissions.requestEachCombined(*perms)
                    .subscribe { permission ->
                        run {
                            when {
                                permission.granted -> // 用户已经同意该权限 !
                                    onSuccess.invoke()
                                permission.shouldShowRequestPermissionRationale -> {
                                    // At least one denied permission without ask never again
                                    // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,
                                    // 那么下次再次启动时，还会提示请求权限的对话框
                                    onError.invoke()
                                }
                                else -> {
                                    // At least one denied permission with ask never again
                                    // 用户拒绝了该权限，并且选中『不再询问』
                                    // Need to go to the settings
                                    ToastUtil.show(
                                            BaseApplication.guiderHealthContext,
                                            "您已经拒绝${hint}权限，继续使用需要去设置中开启")
                                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                    val uri = Uri.fromParts("package",
                                            fragment.activity!!.packageName, null)
                                    intent.data = uri
                                    startAppSettingsScreen(fragment.activity!!, intent)
                                }
                            }
                        }
                    }
        }
    }

    private fun startAppSettingsScreen(`object`: Any, intent: Intent) {
        when (`object`) {
            is Activity -> `object`.startActivityForResult(intent, SETTINGS_REQ_CODE)
            is Fragment -> `object`.startActivityForResult(intent, SETTINGS_REQ_CODE)
        }
    }

    fun checkPermissions(context: Context, @NonNull permissions: Array<String>?): Boolean {
        if (Build.VERSION.SDK_INT < 23) {
            return true
        } else if (permissions != null && permissions.isNotEmpty()) {
            val var3 = permissions.size
            for (var4 in 0 until var3) {
                val permission = permissions[var4]
                if (context.checkCallingOrSelfPermission(permission) != PERMISSION_GRANTED) {
                    return false
                }
            }
            return true
        } else {
            return true
        }
    }


}