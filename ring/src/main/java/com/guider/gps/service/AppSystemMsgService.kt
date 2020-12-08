package com.guider.gps.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.guider.baselib.base.BaseActivity
import com.guider.baselib.base.BaseApplication
import com.guider.baselib.utils.*
import com.guider.gps.BuildConfig
import com.guider.gps.R
import com.guider.health.apilib.GuiderApiUtil
import com.guider.health.apilib.bean.SystemMsgBean
import com.guider.health.apilib.enums.PushMsgType
import com.guider.health.apilib.utils.MMKVUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

/**
 * @Package: com.guider.gps.service
 * @ClassName: AppSystemMsgService
 * @Description: 轮询获取系统消息的service
 * @Author: hjr
 * @CreateDate: 2020/11/2 9:53
 * Copyright (C), 1998-2020, GuiderTechnology
 */
class AppSystemMsgService : LifecycleService() {

    private var latestSystemMsgTime = ""
    private var TAG = javaClass.simpleName

    override fun onCreate() {
        super.onCreate()
        latestSystemMsgTime = DateUtilKotlin.localToUTC(
                DateUtil.localNowStringByPattern(DEFAULT_TIME_FORMAT_PATTERN))!!
        Log.i(TAG, "最新的时间为${latestSystemMsgTime}")
        createChannel()
        val notification = NotificationCompat.Builder(this, BuildConfig.APPLICATION_ID)
                .setSmallIcon(R.mipmap.icon)
                .setLargeIcon((BitmapFactory.decodeResource(
                        BaseApplication.guiderHealthContext.resources,
                        R.mipmap.icon)))
                .setContentTitle(
                        BaseApplication.guiderHealthContext.resources.getString(R.string.app_name))
                .setContentText(
                        BaseApplication.guiderHealthContext.resources.getString(
                                R.string.app_get_latest_news))
                .build()
        startForeground(1, notification)
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "guiderGps"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(BuildConfig.APPLICATION_ID, name, importance)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return MyBinder(WeakReference(this))
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "AppSystemMsgService销毁了")
    }


    class MyBinder(ref: WeakReference<AppSystemMsgService>) : Binder() {

        private var service: AppSystemMsgService? = ref.get()

        //定时器
        val DELAY_TIME: Long = 1_000 * 10

        init {
//            loopGetLatestSystemMsg()
        }

        private fun loopGetLatestSystemMsg() {
            if (service == null) return
            val accountId = MMKVUtil.getInt(USER.USERID, 0)
            if (accountId == 0) return
            var num = 1
            service?.lifecycleScope?.launch {
                while (true) {
                    delay(DELAY_TIME)
                    Log.i(service?.TAG, "轮询方法执行了${num++}次")
                    getLatestSystemMsg(accountId)
                }
            }
        }

        private suspend fun getLatestSystemMsg(accountId: Int) {
            if (service == null) return
            ApiCoroutinesCallBack.resultParse(service, block = {
                Log.i(service?.TAG, "最新的时间为${service?.latestSystemMsgTime}")
                val resultBean = GuiderApiUtil.getApiService().getSystemMsgLatest(
                        accountId, service!!.latestSystemMsgTime)
                if (!(resultBean is String && resultBean == "null") && resultBean != null) {
                    val bean = ParseJsonData.parseJsonAny<SystemMsgBean>(resultBean)
                    if (StringUtil.isNotBlankAndEmpty(bean.createTime))
                        service?.latestSystemMsgTime = bean.createTime!!
                    ((BaseActivity.getForegroundActivity()) as BaseActivity)
                            .showSystemMsgDialog("${bean.name},${bean.content}",
                                    PushMsgType.SOS, bean.id.toString())
                }
            }, onError = {
                Log.i(service?.TAG, it.message!!)
            })

        }
    }
}