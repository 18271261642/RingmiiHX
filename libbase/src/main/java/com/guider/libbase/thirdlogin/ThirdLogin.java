package com.guider.libbase.thirdlogin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import java.util.Arrays;
import java.util.HashMap;
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
    }

    public void wechatLogin(final ThirdLoginCallback thirdLoginCallback) {
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
