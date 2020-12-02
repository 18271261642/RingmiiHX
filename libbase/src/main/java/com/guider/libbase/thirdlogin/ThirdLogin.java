package com.guider.libbase.thirdlogin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.widget.Toast;

import com.guider.health.apilib.ApiCallBack;
import com.guider.health.apilib.ApiUtil;
import com.guider.health.apilib.IGuiderApi;
import com.guider.health.apilib.model.BeanOfWecaht;
import com.guider.health.common.utils.SharedPreferencesUtils;
import com.guider.health.common.utils.StringUtil;
import com.guider.libbase.thirdlogin.line.ILineLogin;
import com.linecorp.linesdk.LineApiResponseCode;
import com.linecorp.linesdk.LoginListener;
import com.linecorp.linesdk.Scope;
import com.linecorp.linesdk.auth.LineAuthenticationParams;
import com.linecorp.linesdk.auth.LineLoginResult;
import com.linecorp.linesdk.widget.LoginButton;
import com.mob.MobSDK;
import com.mob.OperationCallback;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;


import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;

import cn.sharesdk.wechat.friends.Wechat;

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

    public static ThirdLoginCallback mIThirdLoginCallback;

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


    public void lineLogin(final String appId, final ThirdLoginCallback thirdLoginCallback, Runnable action) {
        if (action != null)
            mCompelet = action;
        thidLogin("Line", appId, false, thirdLoginCallback, new HandleOriginUserInfo() {
            @Override
            public HashMap<String, Object> handle(HashMap<String, Object> hashMap) {
                return handleFields(hashMap, appId);
            }
        });
    }

    public synchronized void lineOfficeLogin(Context context, final LoginButton loginButton,
                                             final String appId, Fragment fragment,
                                             final ThirdLoginCallback thirdLoginCallback,
                                             Runnable action) {
        if (action != null)
            mCompelet = action;
        // final LoginButton loginButton = new LoginButton(context);
        // if the button is inside a Fragment, this function should be called.
        if (fragment != null)
            loginButton.setFragment(fragment);

        // replace the string to your own channel id.
        loginButton.setChannelId(appId);

        // configure whether login process should be done by LINE App, or by WebView.
        loginButton.enableLineAppAuthentication(true);

        // set up required scopes.
        // aggressive 模式相当于关注微信公众号
        //normal是正常登陆模式
        loginButton.setAuthenticationParams(new LineAuthenticationParams.Builder()
                .scopes(Arrays.asList(Scope.PROFILE, Scope.OPENID_CONNECT))
                .botPrompt(LineAuthenticationParams.BotPrompt.aggressive)
                .build()
        );

        // A delegate for delegating the login result to the internal login handler.
        // LoginDelegate loginDelegate = LoginDelegate.Factory.create();
        if (fragment != null && fragment instanceof ILineLogin) {
            loginButton.setLoginDelegate(((ILineLogin) fragment).getLoginDelegate());
        } else if (context instanceof ILineLogin) {
            loginButton.setLoginDelegate(((ILineLogin) context).getLoginDelegate());
        }

        loginButton.addLoginListener(new LoginListener() {
            @Override
            public void onLoginSuccess(@NonNull LineLoginResult result) {
                // Toast.makeText(mContext, "Login success", Toast.LENGTH_SHORT).show();
                if (thirdLoginCallback != null) {
                    final HashMap<String, Object> ret = new HashMap<>();
                    ret.put("userId", result.getLineProfile().getUserId());
                    if (result.getLineProfile().getPictureUrl() != null
                            && StringUtil.isEmpty(result.getLineProfile().getPictureUrl().getPath()))
                        ret.put("pictureUrl", result.getLineProfile().getPictureUrl().getPath());
                    else
                        ret.put("pictureUrl", "");
                    ret.put("displayName", result.getLineProfile().getDisplayName());

                    onUserInfo(appId, ret, thirdLoginCallback, new HandleOriginUserInfo() {
                        @Override
                        public HashMap<String, Object> handle(HashMap<String, Object> hashMap) {
                            return handleFields(hashMap, appId
                            );
                        }
                    });
                }
            }

            @Override
            public void onLoginFailure(@Nullable LineLoginResult result) {
                Log.e(TAG, "onLoginFailure : "
                        + (result != null ? result.getErrorData().getMessage() : "fail"));
                if (result.getResponseCode() == LineApiResponseCode.CANCEL) {
                    showToast("Login cancel");
                } else {
                    showToast("Login Fail");
                }
            }
        });

        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loginButton.performClick();
            }
        });
    }

    private void showToast(final String text) {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void thidLogin(String name, final String appId, final boolean customCallBack,
                          final ThirdLoginCallback thirdLoginCallback,
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

        // 移除授权状态和本地缓存，下次授权会重新授权
        plat.removeAccount(true);
        // SSO授权，传false默认是客户端授权
        plat.SSOSetting(false);
        // 授权回调监听，监听oncomplete，onerror，oncancel三种状态
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

        // 自定义
        /*
        HashMap<String, Object> param = new HashMap<>();
        param.put("response_type", "code");
        param.put("client_id", appId);
        param.put("redirect_uri", "https://www.mob.com/");
        param.put("scope", "profile");
        param.put("nonce", "11111111");
        param.put("state", "12345abcde");
        param.put("bot_prompt", "normal");
        plat.customerProtocol("https://access.line.me/oauth2/v2.1/authorize/consent", "GET",
                (short) Line.ACTION_AUTHORIZING, param, null);
        */
        // 抖音登录适配安卓9.0
        ShareSDK.setActivity((Activity) mContext);
        // 要数据不要功能，主要体现在不会重复出现授权界面
        plat.showUser(null);
    }

    private void onUserInfo(final String appId, final HashMap<String, Object> hashMap,
                            final ThirdLoginCallback thirdLoginCallback,
                            final HandleOriginUserInfo handleOriginUserInfo) {
        final HashMap<String, Object> map = handleOriginUserInfo ==
                null ? hashMap : handleOriginUserInfo.handle(hashMap);
        final String openId = map.get("openId").toString();
        mIGuiderApi.verifyThirdAccount(appId, openId, 0, 0)
                .enqueue(new ApiCallBack<BeanOfWecaht>() {
                    @Override
                    public void onResponse(Call<BeanOfWecaht> call, Response<BeanOfWecaht> response) {
                        if (response.body() == null) return;
                        BeanOfWecaht info = response.body();
                        if (info.getTokenInfo() == null) { // 需要跳转到手机号绑定页面
                            Intent intent = new Intent(mContext, BindPhoneV2Activity.class);
                            String nickName = map.get("nickName").toString();
                            String headUrl = map.get("headUrl").toString();
                            intent.putExtra("appId", appId);
                            intent.putExtra("openId", openId);
                            intent.putExtra("nickName", nickName);
                            intent.putExtra("headUrl", headUrl);

                            mIThirdLoginCallback = thirdLoginCallback;
                            mContext.startActivity(intent);
                        } else { // 直接跳转，注意这里需要第三方操作，特别是对于类似手环
                            if (thirdLoginCallback != null)
                                thirdLoginCallback.onUserInfo(map);
                            SharedPreferencesUtils.setParam(mContext,
                                    "accountIdGD", (long) info.getTokenInfo().getAccountId());
                            SharedPreferencesUtils.setParam(mContext,
                                    "tokenGD", info.getTokenInfo().getToken());
                            mCompelet.run();
                        }
                    }
                });
    }

    private HashMap<String, Object> handleFields(HashMap<String, Object> hashMap,
                                                 String appId) {
        final HashMap<String, Object> map = new HashMap<>();
        map.put("appId", appId);
        final String openId = hashMap.get("userId").toString();
        String nickName = hashMap.get("displayName").toString();
        String headUrl = hashMap.containsKey("pictureUrl") ? hashMap.get("pictureUrl").toString() : "";
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
