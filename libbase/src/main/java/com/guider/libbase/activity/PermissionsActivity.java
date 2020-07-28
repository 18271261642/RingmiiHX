package com.guider.libbase.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.guider.libbase.R;
import com.guider.libbase.util.PermissionsUtil;

/**
 * 请求权限的Activity
 */
public class PermissionsActivity extends Activity
{
    public static final int PERMISSIONS_GRANTED = 0; // 权限授权
    public static final int PERMISSIONS_DENIED = 1; // 权限拒绝

    public static final int PERMISSION_REQUEST_CODE = 1000; // 系统权限管理页面的参数
    public static final String EXTRA_PERMISSIONS =
            "com.jhbs.extra_permission"; // 权限参数
    public static final String PACKAGE_URL_SCHEME = "package:"; // 方案

    private boolean isRequireCheck; // 是否需要系统权限检测

    // 启动当前权限页面的公开接口
    public static void startActivityForResult(Activity activity, int requestCode, String... permissions)
    {
        Intent intent = new Intent(activity, PermissionsActivity.class);
        intent.putExtra(EXTRA_PERMISSIONS, permissions);
        ActivityCompat.startActivityForResult(activity, intent, requestCode, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getIntent() == null || !getIntent().hasExtra(EXTRA_PERMISSIONS))
        {
            throw new RuntimeException("PermissionsActivity需要使用静态startActivityForResult方法启动!");
        }
        setContentView(R.layout.activity_permissions);

        isRequireCheck = true;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (isRequireCheck)
        {
            String[] permissions = getPermissions();
            if (PermissionsUtil.lacksPermissions(this, permissions))
            {
                requestPermissions(permissions); // 请求权限
            }
            else
            {
                allPermissionsGranted(); // 全部权限都已获取
            }
        }
        else
        {
            isRequireCheck = true;
        }
    }

    // 返回传递的权限参数
    private String[] getPermissions()
    {
        return getIntent().getStringArrayExtra(EXTRA_PERMISSIONS);
    }

    // 请求权限兼容低版本
    private void requestPermissions(String... permissions)
    {
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
    }

    // 全部权限均已获取
    private void allPermissionsGranted()
    {
        setResult(PERMISSIONS_GRANTED);
        finish();
    }

    /**
     * 用户权限处理,
     * 如果全部获取, 则直接过.
     * 如果权限缺失, 则提示Dialog.
     * @param requestCode  请求码
     * @param permissions  权限
     * @param grantResults 结果
     */
    @TargetApi(19)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (requestCode == PERMISSION_REQUEST_CODE && hasAllPermissionsGranted(grantResults))
        {
            isRequireCheck = true;
            allPermissionsGranted();
        }
        else
        {
            isRequireCheck = false;
            showMissingPermissionDialog();
        }
    }

    // 含有全部的权限
    private boolean hasAllPermissionsGranted(@NonNull int[] grantResults)
    {
        for (int grantResult : grantResults)
        {
            if (grantResult == PackageManager.PERMISSION_DENIED)
            {
                return false;
            }
        }
        return true;
    }

    // 显示缺失权限提示
    private void showMissingPermissionDialog()
    {
        PermissionsUtil.showMissingPermissionDialog(this, this);
    }
}
