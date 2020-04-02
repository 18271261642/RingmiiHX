package com.guider.health.common.net.util.file;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.guider.health.common.R;
import com.guider.health.common.core.BaseFragment;
import com.guider.health.common.core.SPUtils;
import com.guider.health.common.net.app.version.DownloadManagerUtil;
import com.guider.health.common.net.app.version.DownloadReceiver;

/**
 * 脏音听诊器的App管理工具
 */
public class CardiartAppUtil {

    private String packname = "com.imediplus.phonomagics.app.guider";
    private String apkUrl = "http://api.guiderhealth.com/app/cardiart-v1.0.1.apk";

    public boolean skipToApp(BaseFragment fragment) {
        if (!checkPackInfo(fragment.getActivity() , packname)) {
            return false;
        }
        Intent intent = new Intent();
        ComponentName comp = new ComponentName(packname, packname + ".MainActivity");
        intent.setComponent(comp);
        intent.putExtra("accountId", 205);
        intent.putExtra("token", "we");
        intent.setAction("android.intent.action.VIEW");
        fragment.startActivityForResult(intent, 992);
        return true;
    }

    public void checkAndLoadApk(Activity activity) {
        if (!checkPackInfo(activity , packname)) {
            // 没有安装
            long dowloadId = (long) SPUtils.get(activity, "cardiartApkDownloadID", -1l);
            DownloadReceiver receiver = new DownloadReceiver();
            IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            activity.registerReceiver(receiver, intentFilter);
            DownloadManagerUtil util = new DownloadManagerUtil(activity);
            if (dowloadId != -1) {
                util.clearCurrentTask(dowloadId);
            }
            long downloadID = util.download(apkUrl, "PhonoMagicsGuider", "PhonoMagicsGuider" + activity.getResources().getString(R.string.zy_download_tips));
            if (downloadID <= 0) {
                return;
            }
            SPUtils.put(activity, "cardiartApkDownloadID", downloadID);
        }
    }

    /**
     * 检查包是否存在
     *
     * @param packname
     * @return
     */
    private boolean checkPackInfo(Activity activity ,String packname) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = activity.getPackageManager().getPackageInfo(packname, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo != null;
    }
}
