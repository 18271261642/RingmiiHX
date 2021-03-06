package com.guider.healthring.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.guider.healthring.Commont;
import com.guider.healthring.MyApp;
import com.guider.healthring.R;
import com.guider.healthring.b30.B30HomeActivity;
import com.guider.healthring.b31.B31HomeActivity;
import com.guider.healthring.siswatch.NewSearchActivity;
import com.guider.healthring.siswatch.WatchBaseActivity;
import com.guider.healthring.siswatch.utils.WatchUtils;
import com.guider.healthring.util.SharedPreferencesUtils;


/**
 * 启动页
 */
public class LaunchActivity extends WatchBaseActivity {

    private static final String TAG = "LaunchActivity";

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            boolean isGuid = (boolean) msg.obj;
//            Log.e(TAG,"---isGuid="+isGuid);

            if (isGuid) {
                switchLoginUser();
            } else {
                startActivity(NewGuidActivity.class);
                finish();
            }
        }
    };

    String defaultPackName = null;


    @SuppressLint("WrongConstant")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_layout);
        initData();

//        String currPack = getPackageName();
//
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH){
//            defaultPackName = Telephony.Sms.getDefaultSmsPackage(this);//获取手机当前设置的默认短信应用的包名
//        }
//
//        if (!defaultPackName.equals(currPack)) {
//            Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
//            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, currPack);
//            startActivity(intent);
//        }


        final boolean isGuide = (boolean) SharedPreferencesUtils.getParam(LaunchActivity.this, "isGuide", false);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Message message = handler.obtainMessage();
                message.what = 1001;
                message.obj = isGuide;
                handler.sendMessage(message);
            }
        }, 3 * 1000);

    }


    private void initData() {

        //B30目标步数 默认8000
        int goalStep = (int) SharedPreferencesUtils.getParam(MyApp.getContext(), "b30Goal", 0);
        if (goalStep == 0) {
            SharedPreferencesUtils.setParam(MyApp.getContext(), "b30Goal", 8000);
        }
        String b30SleepGoal = (String) SharedPreferencesUtils.getParam(MyApp.getContext(), "b30SleepGoal", "");
        if (WatchUtils.isEmpty(b30SleepGoal)) {
            SharedPreferencesUtils.setParam(MyApp.getContext(), "b30SleepGoal", "8.0");
        }
        //B30的默认密码
        String b30Pwd = (String) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.DEVICESCODE, "");
        if (WatchUtils.isEmpty(b30Pwd)) {
            SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.DEVICESCODE, "0000");
        }

        com.suchengkeji.android.w30sblelibrary.utils.SharedPreferenceUtil.put(LaunchActivity.this, "w30sunit", true);
        //H8手表初始化消息提醒开关
        String isFirst = (String) SharedPreferencesUtils.getParam(LaunchActivity.this, "msgfirst", "");
        if (WatchUtils.isEmpty(isFirst)) {
            SharedPreferencesUtils.saveObject(this, "weixinmsg", "1");
            SharedPreferencesUtils.saveObject(this, "msg", "1");
            SharedPreferencesUtils.saveObject(this, "qqmsg", "1");
            SharedPreferencesUtils.saveObject(this, "Viber", "1");
            SharedPreferencesUtils.saveObject(this, "Twitteraa", "1");
            SharedPreferencesUtils.saveObject(this, "facebook", "1");
            SharedPreferencesUtils.saveObject(this, "Whatsapp", "1");
            SharedPreferencesUtils.saveObject(this, "Instagrambutton", "1");
            SharedPreferencesUtils.saveObject(this, "laidian", "0");
            SharedPreferencesUtils.setParam(MyApp.getApplication(), "laidianphone", "off");
        } else {
            //初始化H8消息提醒功能
            SharedPreferencesUtils.setParam(this, "msgfirst", "isfirst");
        }


    }

    //判断进入的页面
    private void switchLoginUser() {
        String userId = (String) SharedPreferencesUtils.readObject(LaunchActivity.this, "userId");
        Log.e(TAG,"----userId="+userId);
        //判断有没有登录
        if (!WatchUtils.isEmpty(userId)) {
            String btooth = (String) SharedPreferencesUtils.readObject(LaunchActivity.this, "mylanya");
            if (!WatchUtils.isEmpty(btooth) ) {
                if ("B30".equals(btooth)) {
                    startActivity(new Intent(this, B30HomeActivity.class));
                } else if ("Ringmii".equals(btooth)) {
                    startActivity(new Intent(this, B30HomeActivity.class));
                } else if ("500S".equals(btooth)||"B31".equals(btooth)) {
                    startActivity(new Intent(this, B31HomeActivity.class));
                } else {
                    startActivity(new Intent(this, NewSearchActivity.class));
                }
            } else {
                startActivity(new Intent(this, NewSearchActivity.class));
            }
            finish();
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}
