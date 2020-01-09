package com.guider.health.common.net.app.version;

import android.app.Activity;
import android.os.Environment;

public class UpdataUtil {

    public static String apkFilePath;   // apk安装包路径
    public static String apkName;       // 安装包名称
    public static Activity activity;    // 传入的Activity

    String packageName;                 // 包名
    public String versionName;                 // 版本号
    String appName;                     // app名称
    String clientType;                  // 客户端类型

    public UpdataUtil(Activity activity, String clientType) {
        this.activity = activity;
        apkName = appName + ".apk";
        apkFilePath = Environment.getExternalStoragePublicDirectory("Download").getPath();
        this.clientType = clientType;
    }

}
