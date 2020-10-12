package com.guider.libbase.sms;

import android.app.Activity;
import android.content.Context;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.guider.health.common.utils.StringUtil;
import com.guider.health.common.utils.ToastUtil;
import com.guider.libbase.R;

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

        eventHandler = new EventHandler() {
            @Override
            public void afterEvent(final int event, final int result, final Object data) {
                // 此处不可直接处理UI线程，处理后续操作需传到主线程中操作
                if (result == SMSSDK.RESULT_COMPLETE) { // 回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
                        runInUIThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!onError(data)) {
                                    if (mSmsCodeVerifyListener != null)
                                        mSmsCodeVerifyListener.onResult(0);
                                }
                            }
                        });
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {// 获取验证码成功
                        runInUIThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, mContext.getResources().getString(R.string.phone_code_send_success), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {//返回支持发送验证码的国家列表
                    }
                } else {
                    runInUIThread(new Runnable() {
                        @Override
                        public void run() {
                            onError(data);
                        }
                    });

                    ((Throwable) data).printStackTrace();
                }
            }
        };
        //注册一个事件回调监听，用于处理SMSSDK接口请求的结果
        SMSSDK.registerEventHandler(eventHandler);
    }

    private static boolean onError(Object data) {
        if (!StringUtil.isEmpty(data.toString()) && data.toString().contains("java.lang.Throwable:")) {
            String res = data.toString().replace("java.lang.Throwable:", "").trim();
            if (!StringUtil.isEmpty(res)) {
                CodeBean codeBean = new Gson().fromJson(res, CodeBean.class);
                String msg;
                if (codeBean != null) {
                    int status = codeBean.status;
                    if (status == 603) {//手机号错
                        msg = mContext.getResources().getString(R.string.string_phone_er);
                    } else if (status == 468) {//验证码错
                        msg = mContext.getResources().getString(R.string.yonghuzdffhej);
                    } else if (status == 457) {//手机号格式不对
                        msg = mContext.getResources().getString(R.string.format_is_wrong);
                    } else {
                        msg = codeBean.error;
                    }
                    if (mSmsCodeVerifyListener != null)
                        mSmsCodeVerifyListener.onResult(status);
                    ToastUtil.showLong(mContext, msg);
                }
            }
            return true;
        }
        return false;
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

    public static String getTempCode(String country) {
        if (country.trim().startsWith("+"))
            country = country.trim().replace("+", "");
        String tempCode = "4004692";
        if (country.equals("86") || Locale.getDefault().getLanguage().startsWith("zh")) {
            tempCode = "16492964";
        }
        return tempCode;
    }

    private static void runInUIThread(Runnable action) {
        ((Activity) mContext).runOnUiThread(action);
    }

    public interface SmsCodeVerifyListener {
        void onResult(int result);
    }

    class CodeBean {
        /**
         * httpStatus : 400
         * error : China phone to send SMS when the foreign area code.
         * status : 457
         */

        public int httpStatus;
        public String error;
        public int status;
    }
}
