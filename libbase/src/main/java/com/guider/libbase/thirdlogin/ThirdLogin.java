package com.guider.libbase.thirdlogin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.annimon.stream.function.FunctionalInterface;
import com.guider.health.apilib.ApiCallBack;
import com.guider.health.apilib.ApiUtil;
import com.guider.health.apilib.IGuiderApi;
import com.guider.health.apilib.model.BeanOfWecaht;
import com.guider.health.common.utils.JsonUtil;
import com.guider.health.common.utils.SharedPreferencesUtils;
import com.mob.MobSDK;
import com.mob.OperationCallback;

import java.util.HashMap;

import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.google.GooglePlus;
import cn.sharesdk.line.Line;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.whatsapp.WhatsApp;
import retrofit2.Call;
import retrofit2.Response;

public class ThirdLogin {
    private static String TAG = "ThirdLogin";
    // private UMShareAPI mUMShareAPI;
    private Context mContext;
    private IGuiderApi mIGuiderApi = ApiUtil.createApi(IGuiderApi.class);
    public static Runnable mCompelet = new Runnable() {
        @Override
        public void run() {

        }
    };

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

    public void wechatLogin(final ThirdLoginCallback thirdLoginCallback) {
        //  {
        //      "country":"博茨瓦纳","unionid":"oaVtW52ne8N1zIuQVrECjZ1WMAf8","province":"","city":"",
        //      "openid":"oDW5y1MRxDdgD44AXaVx8Hkc2xMA","sex":1,"nickname":"董刚",
        //      "headimgurl":"http://thirdwx.qlogo.cn/mmopen/vi_32/mrJwOdcr1xuzq6uCPls2ia20feAIVf1ykrOJGXwGRVC9QTryKqTww1JAvm5rpSNf9oiamGlvMhFW8d3o7vgJhvFA/132",
        //      "userTags":"","language":"en","privilege":[]
        //  }
        thidLogin(Wechat.NAME, null, true, thirdLoginCallback, null);
    }

    public void googleLogin() {
        thidLogin(GooglePlus.NAME, null, false,null, null);
    }

    public void facebookLogin(final String appId) {
        thidLogin(Facebook.NAME, appId, false, null, new HandleOriginUserInfo() {
            @Override
            public HashMap<String, Object> handle(HashMap<String, Object> hashMap) {
                return handleFields(hashMap, appId, "id", "name", "url");
            }
        });
    }

    public void lineLogin(final String appId, final ThirdLoginCallback thirdLoginCallback, Runnable action) {
        if (action != null)
            mCompelet = action;
        thidLogin(Line.NAME, appId, false, thirdLoginCallback, new HandleOriginUserInfo() {
            @Override
            public HashMap<String, Object> handle(HashMap<String, Object> hashMap) {
                return handleFields(hashMap, appId, "userId", "displayName", "pictureUrl");
            }
        });
    }

    public void whatsAppLogin() {
        thidLogin(WhatsApp.NAME, null, false,null, null);
    }

    public void thidLogin(String name, final String appId, final boolean customCallBack, final ThirdLoginCallback thirdLoginCallback,
                          final HandleOriginUserInfo handleOriginUserInfo) {
        /*
        if (thirdLoginCallback != null) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("displayName", "董刚");
            map.put("userId", "Udac45f0523083055c2d466ac5bbbaa2d");
            map.put("pictureUrl", "https://profile.line-scdn.net/0hsiz1D_sZLEZHSAR09eZTEXsNIiswZioOPykwIWEdInI4LGhCfHtgc2pIe39vfm0SLCpjIGVAInVq");
            thirdLoginCallback.onUserInfo(map);
            return;
        }
         */
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
                if (!customCallBack)
                    onUserInfo(appId, hashMap, thirdLoginCallback, handleOriginUserInfo);
                else if (thirdLoginCallback != null)
                    thirdLoginCallback.onUserInfo(hashMap);
            }
        });
        //抖音登录适配安卓9.0
        ShareSDK.setActivity((Activity) mContext);
        //要数据不要功能，主要体现在不会重复出现授权界面
        plat.showUser(null);
    }

    private void onUserInfo(final String appId, final HashMap<String, Object> hashMap,
                            final ThirdLoginCallback thirdLoginCallback,
                            final HandleOriginUserInfo handleOriginUserInfo) {
        final HashMap<String, Object> map = handleOriginUserInfo == null ? hashMap : handleOriginUserInfo.handle(hashMap);
        final String openId = map.get("openId").toString();
        mIGuiderApi.verifyThirdAccount(appId, openId).enqueue(new ApiCallBack<BeanOfWecaht>() {
            @Override
            public void onResponse(Call<BeanOfWecaht> call, Response<BeanOfWecaht> response) {
                BeanOfWecaht info = response.body();

                if (thirdLoginCallback != null)
                    thirdLoginCallback.onUserInfo(map);
                if (info.getTokenInfo() == null) { // 需要跳转到手机号绑定页面
                    Intent intent = new Intent(mContext, BindPhoneV2Activity.class);
                    String nickName = hashMap.get("nickName").toString();
                    String headUrl = hashMap.get("headUrl").toString();
                    intent.putExtra("appId", appId);
                    intent.putExtra("openId", openId);
                    intent.putExtra("nickName", nickName);
                    intent.putExtra("headUrl", headUrl);
                    mContext.startActivity(intent);
                } else { // 直接跳转，注意这里需要第三方操作，特别是对于类似手环
                    SharedPreferencesUtils.setParam(mContext, "accountIdGD", info.getTokenInfo().getAccountId());
                    mCompelet.run();
                }
            }
        });
    }

    private HashMap<String, Object> handleFields(HashMap<String, Object> hashMap, String appId, String openIdNameField,
                                                 String nickNameField, String headUrlField) {
        final HashMap<String, Object> map = new HashMap<>();
        map.put("appId", appId);
        final String openId = hashMap.get(openIdNameField).toString();
        String nickName = hashMap.get(nickNameField).toString();
        String headUrl = hashMap.get(headUrlField).toString();
        map.put("openId", openId);
        map.put("nickName", nickName);
        map.put("headUrl", headUrl);
        return map;
    }
    public interface ThirdLoginCallback {
        void onUserInfo(HashMap<String, Object> hashMap);
    }

    @FunctionalInterface
    private interface HandleOriginUserInfo {
        HashMap<String, Object> handle(HashMap<String, Object> map);
    }
}
