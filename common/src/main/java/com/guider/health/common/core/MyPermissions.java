package com.guider.health.common.core;

/**
 * Created by haix on 2019/7/12.
 */

import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


public class MyPermissions {
    WeakReference<Activity> activityWeakReference;
    List<String> mPermissionList = new ArrayList<>();
    private final int mRequestCode = 100;

    public MyPermissions(Activity context){
        activityWeakReference = new WeakReference<Activity>(context);
    }

    public void initPermission(String[] permissions) {
        if (activityWeakReference.get() != null) {
            mPermissionList.clear();//清空已经允许的没有通过的权限
            //逐个判断是否还有未通过的权限
            for (int i = 0; i < permissions.length; i++) {
                if (ContextCompat.checkSelfPermission(activityWeakReference.get(), permissions[i]) !=
                        PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(permissions[i]);//添加还未授予的权限到mPermissionList中
                }
            }
            //申请权限
            if (mPermissionList.size() > 0) {//有权限没有通过，需要申请
                ActivityCompat.requestPermissions(activityWeakReference.get(), permissions, mRequestCode);
            } else {
                Log.i("haix", "权限已经都通过了，可以将程序继续打开了");
                //权限已经都通过了，可以将程序继续打开了
                //init();
            }
        }
    }

    /**
     * 请求权限后回调的方法
     *
     * @param requestCode  是我们自己定义的权限请求码
     * @param permissions  是我们请求的权限名称数组
     * @param grantResults 是我们在弹出页面后是否允许权限的标识数组，数组的长度对应的是权限
     *                     名称数组的长度，数组的数据0表示允许权限，-1表示我们点击了禁止权限
     */

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        boolean hasPermissionDismiss = false;//有权限没有通过
        if (mRequestCode == requestCode) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == -1) {
                    hasPermissionDismiss = true;
                    break;
                }
            }
        }
        if (hasPermissionDismiss) {//如果有没有被允许的权限
            showPermissionDialog();
        } else {
            //权限已经都通过了，可以将程序继续打开了
            //init();
        }
    }

    android.app.AlertDialog mPermissionDialog;

    private void showPermissionDialog() {
        if (activityWeakReference.get() != null) {
            if (mPermissionDialog == null) {
                mPermissionDialog = new android.app.AlertDialog.Builder(activityWeakReference.get())
                        .setMessage("已禁用权限，请手动授予")
                        .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cancelPermissionDialog();



                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //关闭页面或者做其他操作
                                cancelPermissionDialog();


                            }
                        })
                        .create();
            }
            mPermissionDialog.show();
        }
    }

    private void cancelPermissionDialog() {
        mPermissionDialog.cancel();
    }
}
