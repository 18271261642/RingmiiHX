package com.guider.libbuglycn;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

// import com.guider.health.common.cache.MeasureDataUploader;
// import com.guider.health.common.device.standard.Constant;
import com.tencent.bugly.beta.Beta;

import java.util.Timer;
import java.util.TimerTask;

// import com.tencent.bugly.beta.Beta;

/**
 * 检查缓存数据上传的服务
 * 在程序启动时启动
 * 30秒检查并重新提交之前提交失败的缓存数据 , 在所有测量结束后会主动发起一次缓存检查上传
 * 缓存数据重新提交10次则删除 , 不再重试
 * 每次开始测量的时候打赏开始测量标记, 结束的时候打赏结束测量标记 , 在测量过程中不会尝试提交缓存
 * 缓存数据以URL为匹配项 , 在URL发生改变的时候则直接删除该缓存
 * 最大缓存数为100条 , 超过则删除所有缓存数据
 *
 * 另新增了检查更新业务  在非测试过程中的情况下 , 检查更新
 */
public class UploaderService extends Service{

    Timer timer;

    /**
     * 检查缓存的时间间隔
     */
    private int CheckTime = 1000 * 30;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                checkAndReuploadFailData();
                checkAppNewVersion();
            }
        }, 1000, CheckTime);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    /**
     * 检查和重新上传失败的数据
     */
    private void checkAndReuploadFailData() {
        // MeasureDataUploader.getInstance(this).checkAndReuploadFaillData();
    }

    boolean hasPrompt = false;
    /**
     * 检查新版本
     * 不在测试过程中的情况下 , 检查更新
     */
    private void checkAppNewVersion() {
        /*
        if (!MeasureDataUploader.getInstance(this).isWorking) {
                Beta.checkUpgrade(false , false);
        }

         */
    }
}
