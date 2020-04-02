package com.guider.health.common.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenShotUtils {
    private static long TIME_OFF = 12 * 60 * 60 * 1000;

    public static boolean shotScreen(Activity activity, String fileName, boolean override, long timeExpired) {
        String storePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "screenshot";
        File appDir = new File(storePath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        fileName = StringUtil.isEmpty(fileName) ? System.currentTimeMillis() + ".jpg" : fileName + ".jpg";
        File file = new File(appDir, fileName);
        if ((!override && file.exists())
                || (override && !isFileExpired(file, timeExpired)))
            return true;

        Log.i("ScreenShotUtils", "shotScreen");
        // 获取屏幕截图
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        try {
            file.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(file);
            // 通过io流的方式来压缩保存图片
            boolean isSuccess = bitmap.compress(Bitmap.CompressFormat.PNG, 80, fos);
            fos.flush();
            fos.close();

            // 保存图片后发送广播通知更新数据库
            Uri uri = Uri.fromFile(file);
            activity.getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));

            if (isSuccess) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isFileExpired(File file, long time) {
        long last = file.lastModified();
        if (last <= 0)
            return true;
        long diff = new Date().getTime() - last - TIME_OFF;
        Log.i("ScreenShotUtils", "last : " + last + ", time : " + time + ", new Date().getTime() - last - TIME_OFF = " + diff);
        return diff > time;
    }
}
