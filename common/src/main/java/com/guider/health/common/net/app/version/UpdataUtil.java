package com.guider.health.common.net.app.version;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.guider.health.common.core.DateUtil;
import com.guider.health.common.core.MyUtils;
import com.guider.health.common.core.NetIp;
import com.guider.health.common.net.app.Httper;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        getVersionFromLocal(activity);
        apkName = appName + ".apk";
        apkFilePath = Environment.getExternalStoragePublicDirectory("Download").getPath();
        this.clientType = clientType;
    }

    public void checkVersion() {
        getVersionFromServer();
    }

    /**
     * 获取本地版本号
     *
     * @param context
     */
    private void getVersionFromLocal(Context context) {
        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (info == null) {
            return;
        }
        versionName = info.versionName;
        packageName = info.packageName;
        int labelRes = info.applicationInfo.labelRes;
        appName = context.getResources().getString(labelRes);
    }

    /**
     * 获取服务器版本号
     */
    private void getVersionFromServer() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        String dateString = DateUtil.dateToString(DateUtil.utcNow(), "yyyy-MM-dd'T'HH:mm:ss'Z'");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("appName", appName);
        jsonObject.put("packageName", packageName);
        jsonObject.put("clientType", clientType);
        jsonObject.put("createTime", dateString);
        jsonObject.put("majorCode", versionName.split("\\.")[0]);
        jsonObject.put("subCode", versionName.split("\\.")[1]);
        jsonObject.put("patchCode", versionName.split("\\.")[2]);
        final RequestBody requestBody =
                RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toJSONString());

        Httper.getInstance().post(NetIp.BASE_URL_bind, "api/v1/version/news", requestBody, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String result = null;
                try {
                    result = response.body().string();
                    Log.i("haix", "------App更新: " + result);
                    JSONObject jb = JSON.parseObject(result);
                    if (jb == null) {
                        return;
                    }
                    int code = jb.getInteger("code");
                    if (code >= 0) {
                        final JSONObject jo = jb.getJSONObject("data");
                        if (jo == null) {
                            return;
                        }
                        String serverVersion = jo.getString("majorCode") + "." +
                                jo.getString("patchCode") + "." +
                                jo.getString("subCode");
                        Log.i("haix", "------服务版本: " + serverVersion);
//                        if (isCurrentVersionLatestOne(serverVersion, versionName) && !TextUtils.isEmpty(jo.getString("urls"))) {
//                            DownloadReceiver receiver = new DownloadReceiver();
//                            IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
//                            activity.registerReceiver(receiver, intentFilter);
//                            DownloadManagerUtil util = new DownloadManagerUtil(activity);
//                            util.download(jo.getString("urls"), appName, appName + "正在下载中 , 请耐心等待...");
//                        }

                        if (!TextUtils.isEmpty(jo.getString("urls"))) {

                            MyUtils.onlyPositiveDialog("有新版本, 请确认下载", activity, new MyUtils.deleteCallBack() {

                                @Override
                                public void doCallBack(boolean confirm) {

                                    if (confirm) {

                                        DownloadReceiver receiver = new DownloadReceiver();
                                        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
                                        activity.registerReceiver(receiver, intentFilter);
                                        DownloadManagerUtil util = new DownloadManagerUtil(activity);
                                        util.download(jo.getString("urls"), appName, appName + "正在下载中 , 请耐心等待...");

                                        Log.i("haix", "下载-------");

                                    }
                                }
                            });

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }

    /**
     * 当前版本是否最新
     *
     * @param serverVersion 服务器版本
     * @param localVersion  本地版本
     * @return true 是最新的   false 不是最新的
     */
    private boolean isCurrentVersionLatestOne(String serverVersion, String localVersion) {
        String[] serverV = serverVersion.split(".");
        String[] localV = localVersion.split(".");
        int length = serverV.length > localV.length ? serverV.length : localV.length;
        for (int i = 0; i < length; i++) {
            String sv = i >= serverV.length ? "0" : serverV[i];
            String lv = i >= localV.length ? "0" : localV[i];
            if (Integer.valueOf(sv) > Integer.valueOf(lv)) {
                return false;
            } else if (Integer.valueOf(sv) == Integer.valueOf(lv)) {
                continue;
            } else {
                return true;
            }
        }
        return true;
    }

//    /**
//     * 调起安装
//     *
//     * @param file
//     */
//    private void installApp(File file) {
//        if (file == null || !file.getPath().endsWith(".apk")) {
//            return;
//        }
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//
//        //判读版本是否在7.0以上
//        if (Build.VERSION.SDK_INT >= 24) {
//            Uri apkUri = FileProvider.getUriForFile(activity, "你的包名.fileprovider", file);
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
//        } else {
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
//        }
//        activity.startActivity(intent);
//    }
}
