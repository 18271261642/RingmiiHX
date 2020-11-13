package com.guider.gps.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.guider.baselib.base.BaseActivity
import com.guider.baselib.base.BaseApplication
import com.guider.gps.BuildConfig
import com.guider.gps.R


class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = this.javaClass.simpleName
    private val CHANNEL_ID = BuildConfig.APPLICATION_ID

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        super.onNewToken(token)
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
//        sendRegistrationToServer(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // (developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
//        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        if (!remoteMessage.data.isNullOrEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")

//            if (/* Check if data needs to be processed by long running job */ true) {
//                // For long-running tasks (10 seconds or more) use WorkManager.
//                scheduleJob()
//            } else {
//                // Handle message within 10 seconds
//                handleNow()
//            }
//            handleMsg(remoteMessage.data)
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.i(TAG, "Message Notification Body: ${it.body}")
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            createChannel(notificationManager)
            val notificationId = 0x1234
            val context = (BaseActivity.getForegroundActivity() as BaseActivity).mContext!!
            val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            builder.setSmallIcon(R.mipmap.icon)
                    .setLargeIcon((BitmapFactory.decodeResource(
                            BaseApplication.guiderHealthContext.resources,
                            R.mipmap.icon)))
                    .setContentTitle(
                            BaseApplication.guiderHealthContext.resources.getString(
                                    R.string.app_name))
                    .setContentText(it.body)
                    .setAutoCancel(true)
            builder.setContentIntent(clickInstallIntent(context,notificationId))
            val notification = builder.build()
            notificationManager.notify(notificationId, notification)
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private fun createChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "guiderGps"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            notificationManager.createNotificationChannel(mChannel)
        }
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