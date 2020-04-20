package com.guider.health.common.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by haix on 2019/7/3.
 */

class MyTimerTask extends TimerTask {

    private final static int RETENTION_TIME = 600000;
    private Timer mTimer; // 计时器，每1秒执行一次任务
    private long mLastActionTime; // 上一次操作时间

    private MyTimerTask() {
    }

    private static MyTimerTask myTimerTask = new MyTimerTask();

    public static MyTimerTask getInstance(){
        return myTimerTask;
    }

    public void initTimerTask(long time){
        mLastActionTime = time;
        if (mTimer == null){
            mTimer = new Timer();
            mTimer.schedule(this, 0, 1000);
        }
    }

    public void updataStartRetentionTime(long time){
        mLastActionTime = time;

    }

    public void stopTimerTask(){
        if (mTimer != null){
            mTimer.cancel();
        }
    }

    public void setRetentionTimeCallBack(RetentionTimeCallBack callBack){
        this.callBack = callBack;
    }

    private RetentionTimeCallBack callBack;
    public interface RetentionTimeCallBack{
        void stopTimer();
    }

    @Override
    public void run() {
        // 5s未操作
        if (System.currentTimeMillis() - mLastActionTime > RETENTION_TIME) {
            if (callBack != null){
                callBack.stopTimer();
            }
        }
    }
}