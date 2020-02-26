package com.guider.libbase.sms;

import android.app.Activity;
import android.content.Context;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.util.Locale;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class SmsMob {
    private static String TAG = "SmsMob";
    private static EventHandler eventHandler;
    private static Context mContext;
    private static SmsCodeVerifyListener mSmsCodeVerifyListener;

    public static void init(Context context) {
        mContext = context;

        eventHandler = new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {
                // 此处不可直接处理UI线程，处理后续操作需传到主线程中操作
                if (result == SMSSDK.RESULT_COMPLETE) { // 回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
                        runInUIThread(new Runnable() {
                            @Override
                            public void run() {
                               mSmsCodeVerifyListener.onResult(true);
                            }
                        });
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {// 获取验证码成功
                        runInUIThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "验证码发送成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {//返回支持发送验证码的国家列表
                    }
                } else {
                    ((Throwable) data).printStackTrace();
                }
            }
        };
        //注册一个事件回调监听，用于处理SMSSDK接口请求的结果
        SMSSDK.registerEventHandler(eventHandler);
    }

    public static void sendCode(String country, String phone) {
        String tempCode = "4004692";
        if (Locale.getDefault().getLanguage().startsWith("zh"))
            tempCode = "16492829";
        Log.i(TAG, "mob sms temo code ： " + tempCode);
        SMSSDK.getVerificationCode(tempCode, country, phone);
    }

    public static void submitVerifyCode(String country, String phone, String code, SmsCodeVerifyListener smsCodeVerifyListener) {
        mSmsCodeVerifyListener = smsCodeVerifyListener;
        SMSSDK.submitVerificationCode(country, phone, code);
    }

    public static void unInit() {
        SMSSDK.unregisterEventHandler(eventHandler);
    }

    private static void runInUIThread(Runnable action) {
        ((Activity)mContext).runOnUiThread(action);
    }

    public interface SmsCodeVerifyListener {
        void onResult(boolean result);
    }
}
