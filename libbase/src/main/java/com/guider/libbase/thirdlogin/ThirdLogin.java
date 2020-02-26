package com.guider.libbase.thirdlogin;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.guider.health.common.utils.JsonUtil;
import com.mob.MobSDK;
import com.mob.OperationCallback;

import java.util.HashMap;
import java.util.Map;

import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.google.GooglePlus;
import cn.sharesdk.line.Line;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.whatsapp.WhatsApp;

public class ThirdLogin {
    private static String TAG = "ThirdLogin";
    // private UMShareAPI mUMShareAPI;
    private Context mContext;

    public ThirdLogin(Context context) {
        mContext = context;
        MobSDK.submitPolicyGrantResult(true, new OperationCallback<Void>() {
            @Override
            public void onComplete(Void data) {
                Log.d(TAG, "隐私协议授权结果提交：成功");
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG, "隐私协议授权结果提交：失败");
            }
        });
    }

    public void wechatLogin() {
        thidLogin(Wechat.NAME, null);
    }

    public void googleLogin() {
        thidLogin(GooglePlus.NAME, null);
    }

    public void facebookLogin() {
        thidLogin(Facebook.NAME, null);
    }

    public void lineLogin(String appId, ThirdLoginCallback thirdLoginCallback) {
        thidLogin(Line.NAME, new ThirdLoginCallback() {
            @Override
            public void onUserInfo(HashMap<String, Object> hashMap) {
                // 通过结果判断账号是否存在
                // 如果不存在转到手机号补充界面
                // 如果存在直接登陆
                Map<String, Object> userInfo;
            }
        });
    }

    public void whatsAppLogin() {
        thidLogin(WhatsApp.NAME, null);
    }

    public void thidLogin(String name, final ThirdLoginCallback thirdLoginCallback) {
        Platform plat = ShareSDK.getPlatform(name);
        //移除授权状态和本地缓存，下次授权会重新授权
        plat.removeAccount(true);
        //SSO授权，传false默认是客户端授权
        plat.SSOSetting(false);
        //授权回调监听，监听oncomplete，onerror，oncancel三种状态
        plat.setPlatformActionListener(new PlatformActionListenerImpl(mContext) {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                super.onComplete(platform, i, hashMap);
                if (thirdLoginCallback != null)
                    thirdLoginCallback.onUserInfo(hashMap);
            }
        });
        //抖音登录适配安卓9.0
        ShareSDK.setActivity((Activity) mContext);
        //要数据不要功能，主要体现在不会重复出现授权界面
        plat.showUser(null);
    }

    public interface ThirdLoginCallback {
        void onUserInfo(HashMap<String, Object> hashMap);
    }
}
