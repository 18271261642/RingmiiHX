package com.guider.health.common.net.app.version;

import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import androidx.core.app.ActivityCompat;

import java.io.File;

public class DownloadManagerUtil {
    private Context mContext;

    public DownloadManagerUtil(Context context) {
        mContext = context;
    }

    /**
     * 比较实用的升级版下载功能
     *
     * @param url   下载地址
     * @param title 文件名字
     * @param desc  文件路径
     */
    public long download(String url, String title, String desc) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                return -1;
            }
        }
        File dowloadFile = new File(UpdataUtil.apkFilePath + UpdataUtil.apkName);
        if (dowloadFile.exists()) {
            dowloadFile.delete();
        }
        DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        long ID;

        //以下两行代码可以让下载的apk文件被直接安装而不用使用Fileprovider,系统7.0或者以上才启动。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder localBuilder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(localBuilder.build());
        }

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        // 仅允许在WIFI连接情况下下载
//        request.setAllowedNetworkTypes(Request.NETWORK_WIFI);
        // 通知栏中将出现的内容
        request.setTitle(title); 
        request.setDescription(desc);

//        //7.0以上的系统适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            request.setRequiresDeviceIdle(false);
            request.setRequiresCharging(false);
        }

        //制定下载的文件类型为APK
        request.setMimeType("application/vnd.android.package-archive");

        // 下载过程和下载完成后通知栏有通知消息。
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE | DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        // 指定下载文件地址，使用这个指定地址可不需要WRITE_EXTERNAL_STORAGE权限。
        File file = new File(UpdataUtil.apkFilePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        File apkFile = new File(UpdataUtil.apkFilePath + UpdataUtil.apkName);
        request.setDestinationUri(Uri.fromFile(apkFile));
//        request.setDestinationInExternalPublicDir("Download", UpdataUtil.apkName);

        //大于11版本手机允许扫描
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            //表示允许MediaScanner扫描到这个文件，默认不允许。
            request.allowScanningByMediaScanner();
        }

//        Activity activity = UpdataUtil.activity;
//        if (activity != null) {
//            activity.startActivity(new android.content.Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));//启动系统下载界面
//        }
        ID = downloadManager.enqueue(request);
        return ID;
    }

    /**
     * 下载前先移除前一个任务，防止重复下载
     *
     * @param downloadId
     */
    public void clearCurrentTask(long downloadId) {
        DownloadManager dm = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        try {
            dm.remove(downloadId);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }
}
