package com.guider.healthring.w30s.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import android.util.Log;

import com.guider.healthring.activity.wylactivity.wyl_util.service.AlertService;
import com.guider.healthring.R;
import com.guider.healthring.util.AppUtils;
import com.suchengkeji.android.w30sblelibrary.W30SBLEManage;

/**
 * 正常的系统前台进程，会在系统通知栏显示一个Notification通知图标
 * Intent whiteIntent = new Intent(getApplicationContext(), WhiteService.class);
 * startService(whiteIntent);
 *
 * @author clock
 * @since 2016-04-12
 */
public class WhiteService extends Service {

    private final static String TAG = WhiteService.class.getSimpleName();

    private final static int FOREGROUND_ID = 1000;

    @Override
    public void onCreate() {
        Log.i(TAG, "WhiteService->onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "WhiteService->onStartCommand");
        createNotification();
        toggleNotificationListenerService(true);
        return super.onStartCommand(intent, flags, startId);
    }


    Notification notification;

    /**
     * Notification
     */
    public void createNotification() {
        String appName = AppUtils.getAppName(getApplicationContext());
        Bitmap appIcon = AppUtils.getBitmap(getApplicationContext());
        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "1002";
            NotificationChannel channel = new NotificationChannel(channelId,
                    "guider_hx_WhiteService", NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = (NotificationManager) getSystemService(
                    Context.NOTIFICATION_SERVICE);
            if (manager == null)
                return;
            manager.createNotificationChannel(channel);
            builder = new NotificationCompat.Builder(this,
                    channelId);
        } else {
            builder = new NotificationCompat.Builder(this);
        }
        notification = builder
                .setContentTitle(appName)
                .setContentText("WhiteService")
                .setLargeIcon(appIcon)
                .setAutoCancel(false)
                .build();
        //设置为前台服务
        startForeground(FOREGROUND_ID, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "WhiteService->onBind");
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "WhiteService->onDestroy");
        stopForeground(true);
        if (notification != null) {
            notification = null;
        }
        toggleNotificationListenerService(false);
        super.onDestroy();
    }

    /**
     * 被杀后再次启动时，监听不生效的问题
     */
    private void toggleNotificationListenerService(boolean isStatue) {

        if (isStatue) {
            Intent sevice = new Intent(this, AlertService.class);
            sevice.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startService(sevice);
        } else {
            W30SBLEManage.mW30SBLEServices.close();
        }

    }
}