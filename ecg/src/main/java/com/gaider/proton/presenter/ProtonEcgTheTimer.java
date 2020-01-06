package com.gaider.proton.presenter;

import android.os.Handler;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

public class ProtonEcgTheTimer implements Handler.Callback{

    private int currentTime , countDownTime;
    private OnTimingCallback onFinishCallback;
    private boolean isTiming = false;
    private Timer mTimer;
    private Handler handler;

    public ProtonEcgTheTimer(int downTime , OnTimingCallback onFinishCallback) {
        this.currentTime = downTime;
        this.countDownTime = downTime;
        this.onFinishCallback = onFinishCallback;
        handler = new Handler(this);
    }

    public void start() {
        if (isTiming) {
            return;
        }
        isTiming = true;
        if (mTimer == null) {
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (isTiming) {
                        currentTime--;
                        handler.sendEmptyMessage(0);
                    }
                }
            } , 1000 , 1000);
        }
    }

    public void stop() {
        isTiming = false;
    }

    public void reset() {
        clean();
        currentTime = countDownTime;
    }

    public void clean() {
        isTiming = false;
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (onFinishCallback != null) {
            if (currentTime < 0) {
                onFinishCallback.onFinish();
                reset();
            } else {
                onFinishCallback.onTick(currentTime);
            }
        }
        return false;
    }


    public static class OnTimingCallback {
        void onFinish(){}
        void onTick(int time){}
    }

}
