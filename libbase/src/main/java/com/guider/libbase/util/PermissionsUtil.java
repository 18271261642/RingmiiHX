package com.guider.libbase.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.guider.libbase.activity.PermissionsActivity;
import com.guider.libbase.other.impl.BaseOnDialogClick;


/**
 * Created by Administrator on 2016/9/13 0013.
 * 权限工具类
 */
public class PermissionsUtil
{
    private static final String PACKAGE_URL_SCHEME = "package:"; // 方案
    private static final String TAG = "PermissionsUtil";

    // 所需的全部权限
    static final String[] PERMISSIONS = new String[]{
            "android.permission.READ_CALENDAR",
            "android.permission.CAMERA",
            "android.permission.READ_CONTACTS",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.RECORD_AUDIO",
            "android.permission.READ_PHONE_STATE",
            "android.permission.SEND_SMS",
            "android.permission.READ_EXTERNAL_STORAGE"
    };

    /**
     * 显示权限请求设置
     * @param context
     * @param activity
     */
    public static void showMissingPermissionDialog(final Context context, final Activity activity)
    {
       DialogUtil.showConfirmDialog(context, (ViewGroup) null, "当前应用缺少必要权限。\n请点击\"设置\"-\"权限\"打开所需权限。\n", new BaseOnDialogClick()
       {
           @Override
           public void onConfirm(DialogInterface dialogInterface, View view)
           {
               Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
               intent.setData(Uri.parse(PACKAGE_URL_SCHEME + context.getPackageName()));
               context.startActivity(intent);
               super.onConfirm(dialogInterface, view);
           }

           @Override
           public void onCancle(DialogInterface dialogInterface, View view)
           {
               activity.setResult(PermissionsActivity.PERMISSIONS_DENIED);
               activity.finish();
               super.onCancle(dialogInterface, view);
           }
       }, true);
    }

    /**
     * 请求权限
     * @param context 上下文
     * @param requestCode 请求码
     */
    public static void requestPermissions(Activity context, int requestCode) {
        requestPermissions(context, requestCode, PERMISSIONS);
    }

    /**
     * 请求权限
     * @param context 上下文环境
     * @param requestCode 请求码
     * @param args 请求的权限数组
     */
    public static void requestPermissions(Activity context, int requestCode, String ... args) {
        if (23 > Build.VERSION.SDK_INT || !lacksPermissions(context, args))
            return;

        // Intent intent = new Intent(context, PermissionsActivity.class);
        // intent.putExtra(PermissionsActivity.EXTRA_PERMISSIONS, args);
        PermissionsActivity.startActivityForResult(context, requestCode, args);
        // context.startActivityForResult(intent, requestCode);
    }

    /**
     * 判断权限集合
     * @param context
     * @param permissions
     * @return
     */
    public static boolean lacksPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            Log.d(TAG, permission);
            if (lacksPermission(context, permission)) {
                return true;
            }
        }
        return false;
    }

    // 判断是否缺少权限
    private static boolean lacksPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) ==
                PackageManager.PERMISSION_DENIED;
    }
}
