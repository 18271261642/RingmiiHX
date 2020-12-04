package com.guider.baselib.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.util.Log
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import com.guider.baselib.R
import com.guider.baselib.base.BaseActivity
import com.guider.baselib.base.BaseApplication
import com.permissionx.guolindev.PermissionX

/**
 * Description 权限Util
 * Author HJR36
 * Date 2018/6/23 17:10
 */
object PermissionUtils {

    /**
     * activity中请求权限
     * @param activity 当前activity
     * @param perms 权限数组
     * @param onSuccess 成功回调
     * @param hint 权限名称
     * @param onError 失败回调
     */
    @SuppressLint("CheckResult")
    fun requestPermissionActivity(activity: BaseActivity, perms: Array<String>, hint: String,
                                  requestHint: String = "",
                                  onSuccess: () -> Unit,
                                  onError: () -> Unit, isRequestGiveReason: Boolean = true) {

        if (checkPermissions(activity, perms)) {
            onSuccess.invoke()
        } else {
            val permissions = PermissionX.init(activity)
                    .permissions(*perms)
            if (isRequestGiveReason) {
                permissions.explainReasonBeforeRequest()
                // 给出用户请求的原因
                permissions.onExplainRequestReason { scope, deniedList ->
                    scope.showRequestReasonDialog(deniedList, requestHint,
                            BaseApplication.guiderHealthContext.resources
                            .getString(R.string.app_allow),
                            BaseApplication.guiderHealthContext.resources
                            .getString(R.string.app_deny))
                }
            }
            // 用户拒绝了该权限，并且选中『不再询问』
            permissions.onForwardToSettings { scope, deniedList ->
                val refuseHints = String.format(
                        BaseApplication.guiderHealthContext.resources
                                .getString(R.string.app_refuse_hint_permission),
                        hint)
                scope.showForwardToSettingsDialog(deniedList,
                        refuseHints,
                        BaseApplication.guiderHealthContext.resources
                                .getString(R.string.app_confirm),
                        BaseApplication.guiderHealthContext.resources
                                .getString(R.string.app_cancel))
            }
            permissions.request { allGranted, _, deniedList ->
                if (allGranted) {
                    // 用户已经同意该权限 !
                    onSuccess.invoke()
                } else {
                    // 用户拒绝了该权限，没有选中『不再询问』
                    Log.i(PermissionUtils.javaClass.simpleName,
                            "用户拒绝的权限为$deniedList")
                    onError.invoke()
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
                                  requestHint: String,
                                  onSuccess: () -> Unit,
                                  onError: () -> Unit) {
        if (checkPermissions(fragment.activity!!, perms)) {
            onSuccess.invoke()
        } else {
            PermissionX.init(fragment)
                    .permissions(*perms)
                    .explainReasonBeforeRequest()
                    // 给出用户请求的原因
                    .onExplainRequestReason { scope, deniedList ->
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            val customDialog = CustomPermissionDialogFragment(requestHint, deniedList)
                            scope.showRequestReasonDialog(customDialog)
                        }
                    }
                    // 用户拒绝了该权限，并且选中『不再询问』
                    .onForwardToSettings { scope, deniedList ->
                        val refuseHints = String.format(
                                BaseApplication.guiderHealthContext.resources
                                        .getString(R.string.app_refuse_hint_permission),
                                hint)
                        scope.showForwardToSettingsDialog(deniedList,
                                refuseHints,
                                BaseApplication.guiderHealthContext.resources
                                        .getString(R.string.app_confirm),
                                BaseApplication.guiderHealthContext.resources
                                        .getString(R.string.app_cancel))
                    }
                    .request { allGranted, _, deniedList ->
                        if (allGranted) {
                            // 用户已经同意该权限 !
                            onSuccess.invoke()
                        } else {
                            // 用户拒绝了该权限，没有选中『不再询问』
                            Log.i(PermissionUtils.javaClass.simpleName,
                                    "用户拒绝的权限为$deniedList")
                            onError.invoke()
                        }
                    }
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