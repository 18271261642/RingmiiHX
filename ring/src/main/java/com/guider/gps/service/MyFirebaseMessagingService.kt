package com.guider.gps.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.guider.baselib.base.BaseActivity
import com.guider.baselib.base.BaseApplication
import com.guider.baselib.utils.CommonUtils
import com.guider.baselib.utils.EventBusAction.REFRESH_NEW_PUSH_TOKEN
import com.guider.baselib.utils.EventBusEvent
import com.guider.baselib.utils.EventBusUtils
import com.guider.gps.BuildConfig
import com.guider.gps.R
import com.guider.gps.bean.PushMessageBean


class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = this.javaClass.simpleName
    private val CHANNEL_ID = BuildConfig.APPLICATION_ID

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.i(TAG, "Refreshed token: $token")
        super.onNewToken(token)
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        EventBusUtils.sendEvent(EventBusEvent(REFRESH_NEW_PUSH_TOKEN, token))
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        var pushMessageBean: PushMessageBean? = null
        // Check if message contains a data payload.
        if (!remoteMessage.data.isNullOrEmpty()) {
            Log.i(TAG, "Message data payload: ${remoteMessage.data}")
            if (CommonUtils.toBean<PushMessageBean>(remoteMessage.data) != null) {
                pushMessageBean = CommonUtils.toBean<PushMessageBean>(remoteMessage.data)
            }
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.i(TAG, "Message Notification Body: ${it.body}")
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createChannel(notificationManager)
            }
            val notificationId = pushMessageBean?.key?.hashCode() ?: 0x1234
            val context = (BaseActivity.getForegroundActivity() as BaseActivity).mContext!!
            val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder.setOnlyAlertOnce(true)
            } else builder.setDefaults(Notification.DEFAULT_ALL)
            builder.setSmallIcon(R.mipmap.icon)
                    .setLargeIcon((BitmapFactory.decodeResource(
                            BaseApplication.guiderHealthContext.resources,
                            R.mipmap.icon)))
                    .setContentTitle(
                            BaseApplication.guiderHealthContext.resources.getString(
                                    R.string.app_name))
                    .setContentText(it.body)
                    .setAutoCancel(true)
                    .setWhen(System.currentTimeMillis())
            builder.setContentIntent(clickInstallIntent(context, notificationId))
            val notification = builder.build()
            notificationManager.notify(notificationId, notification)
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(notificationManager: NotificationManager) {
        val name = "guiderGps"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
        //默认全部
        mChannel.enableVibration(true)
        mChannel.vibrationPattern = longArrayOf(100, 200, 300)
        mChannel.enableLights(true)
        mChannel.setShowBadge(true) //是否显示角标
        notificationManager.createNotificationChannel(mChannel)
    }

    /**
     * 点击通知
     */
    private fun clickInstallIntent(context: Context, notificationId: Int)
            : PendingIntent? {
        val intent = Intent("notification_clicked")
        intent.putExtra("notificationId", notificationId.toString())
        intent.setPackage(BaseApplication.guiderHealthContext.packageName)
        intent.putExtra("id", notificationId)
        return PendingIntent.getBroadcast(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}