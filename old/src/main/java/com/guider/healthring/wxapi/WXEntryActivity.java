package com.guider.healthring.wxapi;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.google.gson.Gson;
import com.guider.healthring.Commont;
import com.guider.healthring.MyApp;
import com.guider.healthring.activity.GuiderWxBindPhoneActivity;
import com.guider.healthring.bean.BlueUser;
import com.guider.healthring.bean.GuiderWXUserInfoBean;
import com.guider.healthring.bean.WXUserBean;
import com.guider.healthring.siswatch.NewSearchActivity;
import com.guider.healthring.siswatch.WatchBaseActivity;
import com.guider.healthring.siswatch.utils.WatchUtils;
import com.guider.healthring.util.Common;
import com.guider.healthring.util.OkHttpTool;
import com.guider.healthring.util.SharedPreferencesUtils;
import com.guider.healthring.util.ToastUtil;
import com.guider.healthring.util.URLs;
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


/**
 * MicroMsg
 * Created by wyl on 2017/2/6.
 */

public class WXEntryActivity extends WatchBaseActivity implements IWXAPIEventHandler,RequestView {

    private static final String TAG = "WXEntryActivity";

    //盖德微信登录
    private String GUIDER_WX_LOGIN_URL = "http://api.guiderhealth.com/api/v2/third/login/wachat/tokeninfo";

    private IWXAPI iwxapi;
    /**
     * 国内用这个
     */
    private static final String APP_SECRET = Commont.WX_APP_SECRET;
    public static final String WEIXIN_APP_ID = Commont.WX_APP_ID;

    /**
     * Google Play用这个
     */
//    private static final String APP_SECRET = "8864cc82ce546270207e5ba1f3ef0054";
//    public static final String WEIXIN_APP_ID = "wx7e5e9e90ae4d8f51";

    private RequestPressent requestPressent ;


    private WXUserBean wxUserBean = null;



    @Override
    public void onReq(BaseReq baseReq) {
//        Log.e(TAG, "---baseReq=" + baseReq.toString());
    }

    @Override
    public void onResp(BaseResp resp) {

        if(requestPressent == null)
            requestPressent = new RequestPressent();
            requestPressent.attach(this);

        if (resp instanceof SendAuth.Resp) {
            SendAuth.Resp newResp = (SendAuth.Resp) resp;

            Log.e(TAG,"---------wx="+newResp.userName+"\n"+newResp.token+"\n"+newResp.state);

            //获取微信传回的code  e9f2b83c
            String code = newResp.token;

            String wxResponseUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="
                    + WEIXIN_APP_ID + "&secret=" + APP_SECRET + "&code=" + code + "&grant_type=authorization_code";
            Log.e(TAG,"-------url="+wxResponseUrl);
            requestPressent.getRequestJSONObject(1001,wxResponseUrl,WXEntryActivity.this,1);


//            Log.e(TAG, "------code--" + code);

            /**
             * 通过这个获取信息
             * https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
             * 获得opid和access_token
             *0da2a2a2c3e35d25460c8f6bb01cd876
             */

        }
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
        String url = URLs.HTTPs + URLs.disanfang;
        if (Commont.isDebug)Log.e(TAG, "====  json  " +  new Gson().toJson(params));
        OkHttpTool.getInstance().doRequest(url, new Gson().toJson(params), this, new OkHttpTool.HttpResult() {
                    @Override
                    public void onResult(String result) {
                        Log.e(TAG, "-------微信登录到bl=" + result);
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                if(jsonObject.has("resultCode")){
                                    if(jsonObject.getString("resultCode").equals("001")){
                                        String shuzhu = jsonObject.getString("userInfo");
                                        JSONObject jsonObjectStr = new JSONObject(shuzhu);
                                        String userId = jsonObjectStr.getString("userId");
                                        Gson gson = new Gson();
                                        BlueUser userInfo = gson.fromJson(shuzhu, BlueUser.class);
                                        Common.userInfo = userInfo;
                                        Common.customer_id = userId;
                                        //保存userid
                                        SharedPreferencesUtils.saveObject(WXEntryActivity.this, "userId", userInfo.getUserId());
                                    }

                                }
                            } catch (Exception E) {
                                E.printStackTrace();
                            }
                        }
                    });

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Log.e(TAG, "-----oncratewx---");
        //注册API
        iwxapi = WXAPIFactory.createWXAPI(this, WEIXIN_APP_ID, true);
        iwxapi.handleIntent(getIntent(), this);

        requestPressent = new RequestPressent();
        requestPressent.attach(this);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        iwxapi.handleIntent(intent, this);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(requestPressent != null)
            requestPressent.detach();
    }



    @Override
    public void showLoadDialog(int what) {

    }

    @Override
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



    @Override
    public void failedData(int what, Throwable e) {

    }

    @Override
    public void closeLoadDialog(int what) {

    }

    //解析微信的信息
    private void analysiWXMsg(String wxStr) {
        try {
            JSONObject jsonObject = new JSONObject(wxStr);
            if(jsonObject.has("access_token")){
                String wxAccessToken = jsonObject.getString("access_token");
                String wxOpenId = jsonObject.getString("openid");

                //获取用户的信息
                String userUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=" + wxAccessToken + "&openid=" + wxOpenId;
                requestPressent.getRequestJSONObject(1002,userUrl,WXEntryActivity.this,2);

            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }


    //解析用户信息，登录到盖德平台
    private void analysisWXUserInfo(String userStr) {
        wxUserBean = new Gson().fromJson(userStr, WXUserBean.class);
        WatchUtils.setIsWxLogin("LOGION_WX", new Gson().toJson(wxUserBean));

        loginBZLForWx(wxUserBean);
        Log.e(TAG,"------wxUsr="+wxUserBean.toString());
        Map<String,Object> maps = new HashMap<>();
        maps.put("appId","b3c327c04d8b0471");
        maps.put("headimgurl",wxUserBean.getHeadimgurl());
        maps.put("nickname",wxUserBean.getNickname());
        maps.put("openid",wxUserBean.getOpenid());
        maps.put("sex",Integer.valueOf(wxUserBean.getSex()));
        maps.put("unionid",wxUserBean.getUnionid());


        Log.e(TAG,"------ltMap="+new Gson().toJson(maps));
        requestPressent.getRequestJSONObject(1003,GUIDER_WX_LOGIN_URL,WXEntryActivity.this,new Gson().toJson(maps),3);

    }


    //解析登录到盖德返回信息，判断是否需要绑定手机号
    private void analysiWXToGuiderData(String str) {
        try {
            GuiderWXUserInfoBean guiderWXUserInfoBean = new Gson().fromJson(str,GuiderWXUserInfoBean.class);
            if(guiderWXUserInfoBean.getCode() != 0){
                ToastUtil.showToast(WXEntryActivity.this,guiderWXUserInfoBean.getMsg()+guiderWXUserInfoBean.getData());
                return;
            }
            boolean flag = guiderWXUserInfoBean.getData().isFlag();
            if(!flag){      //false不需要绑定手机号
                long accountId = guiderWXUserInfoBean.getData().getTokenInfo().getAccountId();
                SharedPreferencesUtils.setParam(MyApp.getInstance(), "accountIdGD", accountId);
                startActivity(NewSearchActivity.class);
                finish();
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

}


