package com.guider.gps.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.alibaba.android.arouter.launcher.ARouter
import com.guider.baselib.utils.msgList

/**
 * @Package: com.guider.gps.receiver
 * @ClassName: NotificationClickReceiver
 * @Description:通知栏点击广播
 * @Author: hjr
 * @CreateDate: 2020/11/11 16:18
 * Copyright (C), 1998-2020, GuiderTechnology
 */
class NotificationClickReceiver :BroadcastReceiver(){

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i(this.javaClass.simpleName,"收到了广播事件")
        //用这个方法实现点击notification后的事件
        val notificationManager = context?.getSystemService(
                Context.NOTIFICATION_SERVICE) as NotificationManager
        val targetId = intent?.getStringExtra("notificationId")
        val action = intent!!.action
        if (action.equals("notification_clicked")) {
            //处理点击事件
            ARouter.getInstance().build(msgList)
                    //进入页面需跳转到的指定列表项
                    .withInt("entryPageIndex", 2)
                    .navigation()
            //移除通知栏
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //关闭通知通道
                notificationManager.deleteNotificationChannel(targetId)
            } else {
                notificationManager.cancel(intent.getIntExtra("id",
                        -1))
            }
        }
    }
}