package com.guider.healthringx.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.guider.health.apilib.BuildConfig;
import com.guider.health.common.utils.JsonUtil;
import com.guider.healthring.Commont;
import com.guider.healthring.MyApp;
import com.guider.healthring.activity.GuiderWxBindPhoneActivity;
import com.guider.healthring.activity.WxScanActivity;
import com.guider.healthring.bean.GuiderWXUserInfoBean;
import com.guider.healthring.bean.UserInfoBean;
import com.guider.healthring.bean.WXUserBean;
import com.guider.healthring.siswatch.NewSearchActivity;
import com.guider.healthring.siswatch.utils.WatchUtils;
import com.guider.healthring.util.Common;
import com.guider.healthring.util.OkHttpTool;
import com.guider.healthring.util.SharedPreferencesUtils;
import com.guider.healthring.util.ToastUtil;
import com.guider.healthring.util.URLs;
import com.guider.healthring.util.WxScanUtil;
import com.guider.healthring.w30s.utils.httputils.RequestPressent;
import com.guider.healthring.w30s.utils.httputils.RequestView;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendAuth;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.guider.healthring.BuildConfig.APIURL;


/**
 * MicroMsg
 * Created by wyl on 2017/2/6.
 */

public class WXEntryActivityAdapter {

    private static final String TAG = "WXEntryActivity";

    //盖德微信登录
    private String GUIDER_WX_LOGIN_URL = APIURL +  "api/v2/third/login/wachat/tokeninfo"; // http://api.guiderhealth.com/
    /**
     * 国内用这个
     */
    private static final String APP_SECRET = Commont.WX_APP_SECRET;
    public static final String WEIXIN_APP_ID = Commont.WX_APP_ID;

    private Activity mContext;
    private RequestPressent requestPressent ;

    private WXUserBean wxUserBean = null;

    public WXEntryActivityAdapter(Activity context, RequestPressent rp) {
        mContext = context;
        requestPressent = rp;
    }

    //注册到博之轮平台账号
    private void loginBZLForWx(WXUserBean wxUserBean) {
       // User wxUserBean = new Gson().fromJson(users, User.class);
        Log.e(TAG,"------wxUserBean="+wxUserBean.toString());
        Map<String,Object> params = new HashMap<>();
        params.put("thirdId", wxUserBean.getOpenid());
        params.put("thirdType",  "3");
        params.put("image", wxUserBean.getHeadimgurl());
        params.put("sex", (wxUserBean.getSex().equals("1")?"M":"F"));
        params.put("nickName", wxUserBean.getNickname());
        if (Commont.isDebug)Log.e(TAG, "3333游客注册或者登陆参数：" + params.toString());
        String url = Commont.FRIEND_BASE_URL + URLs.disanfang;
        if (Commont.isDebug)Log.e(TAG, "====  json  " +  new Gson().toJson(params));
        OkHttpTool.getInstance().doRequest(url, new Gson().toJson(params), this, result -> {
            Log.e(TAG, "-------微信登录到bl=" + result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (!jsonObject.has("code"))
                    return;
                if (jsonObject.getInt("code") == 200) {
                    String userStr = jsonObject.getString("data");
                    if (userStr != null) {
                        UserInfoBean userInfoBean = new Gson().fromJson(userStr, UserInfoBean.class);
                        Common.customer_id = userInfoBean.getUserid();
                        //保存userid
                        SharedPreferencesUtils.saveObject(mContext, Commont.USER_ID_DATA, userInfoBean.getUserid());
                        SharedPreferencesUtils.saveObject(mContext, "userInfo", userStr);
                        SharedPreferencesUtils.saveObject(mContext, Commont.USER_INFO_DATA, userStr);

                        // startActivity(NewSearchActivity.class);
                        // finish();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            });

    }


    // @Override
    public void successData(int what, Object object, int daystag) {
        if(object == null)
            return;
        Log.e(TAG,"--------Obj="+what+object.toString());
        switch (what){
            case 1001:  //获取微信的token
                analysiWXMsg(object.toString());
                break;
            case 1002:  //用户的信息
                analysisWXUserInfo(object.toString());
                break;
            case 1003:  //微信登录到盖德平台返回
                analysiWXToGuiderData(object.toString());
                break;
        }
    }


    // 解析微信的信息
    private void analysiWXMsg(String wxStr) {
        try {
            JSONObject jsonObject = new JSONObject(wxStr);
            if(jsonObject.has("access_token")){
                String wxAccessToken = jsonObject.getString("access_token");
                String wxOpenId = jsonObject.getString("openid");

                //获取用户的信息
                String userUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=" + wxAccessToken + "&openid=" + wxOpenId;
                requestPressent.getRequestJSONObject(1002,userUrl, mContext,2);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    // 解析用户信息，登录到盖德平台
    public void analysisWXUserInfo(String userStr) {
        wxUserBean = new Gson().fromJson(userStr, WXUserBean.class);
        WatchUtils.setIsWxLogin("LOGION_WX", new Gson().toJson(wxUserBean));

        loginBZLForWx(wxUserBean);
        Log.e(TAG,"------wxUsr="+wxUserBean.toString());
        Map<String,Object> maps = new HashMap<>();
        maps.put("appId", WEIXIN_APP_ID); // "b3c327c04d8b0471");
        maps.put("headimgurl",wxUserBean.getHeadimgurl());
        maps.put("nickname",wxUserBean.getNickname());
        maps.put("openid",wxUserBean.getOpenid());
        maps.put("sex",Integer.valueOf(wxUserBean.getSex()));
        maps.put("unionid",wxUserBean.getUnionid());


        Log.e(TAG,"------ltMap="+new Gson().toJson(maps));
        requestPressent.getRequestJSONObject(1003,GUIDER_WX_LOGIN_URL,
                mContext,new Gson().toJson(maps),3);
    }

    //解析登录到盖德返回信息，判断是否需要绑定手机号
    private void analysiWXToGuiderData(String str) {
        try {
            GuiderWXUserInfoBean guiderWXUserInfoBean = new Gson().fromJson(str,GuiderWXUserInfoBean.class);
            if(guiderWXUserInfoBean.getCode() != 0){
                ToastUtil.showToast(mContext,guiderWXUserInfoBean.getMsg()+guiderWXUserInfoBean.getData());
                return;
            }
            boolean flag = guiderWXUserInfoBean.getData().isFlag();
            if(!flag){      //false不需要绑定手机号
                long accountId = guiderWXUserInfoBean.getData().getTokenInfo().getAccountId();
                SharedPreferencesUtils.setParam(MyApp.getInstance(), "accountIdGD", accountId);
                String token = guiderWXUserInfoBean.getData().getTokenInfo().getToken();
                SharedPreferencesUtils.setParam(MyApp.getInstance(), "tokenGD", token);

                WxScanUtil.handle(mContext, accountId, new WxScanUtil.IWxScan() {
                    @Override
                    public void onError() {
                        startActivity(NewSearchActivity.class);
                    }
                    @Override
                    public void onOk() {
                        Intent intent = new Intent(mContext, WxScanActivity.class);
                        intent.putExtra("accountId", accountId);
                        mContext.startActivity(intent);
                    }
                });
            }else{
                if(wxUserBean == null)
                    return;

                Map<String,Object> param = new HashMap<>();
                param.put("openId",wxUserBean.getOpenid());
                param.put("unionId",wxUserBean.getUnionid());
                param.put("headUrl",wxUserBean.getHeadimgurl());
                param.put("sex",wxUserBean.getSex());
                param.put("nickname",wxUserBean.getNickname());
                String wxStr = new Gson().toJson(param);

                startActivity(GuiderWxBindPhoneActivity.class,new String[]{"wxStr"},new String[]{wxStr});
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void startActivity(Class<?> cls, String[] keys, String[] values) {
        Intent intent = new Intent(mContext, cls);
        int size = keys.length;
        for (int i = 0; i < size; i++) {
            intent.putExtra(keys[i], values[i]);
        }
        mContext.startActivity(intent);
    }

    public void startActivity(Class<?> cls) {
        Intent intent = new Intent(mContext, cls);
        mContext.startActivity(intent);
    }
}


