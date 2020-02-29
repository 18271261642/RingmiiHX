package com.guider.glu_phone.view;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.guider.health.apilib.ApiCallBack;
import com.guider.health.apilib.ApiUtil;
import com.guider.health.apilib.IGuiderApi;
import com.guider.health.apilib.model.BeanOfWecaht;
import com.guider.health.apilib.model.WechatInfo;
import com.guider.health.common.core.SPUtils;
import com.guider.health.common.core.UserManager;
import com.just.agentweb.AgentWeb;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by haix on 2019/8/11.
 */
public class AndroidInterface{

    private final String TAG = AndroidInterface.class.getSimpleName();
    private AgentWeb web;
    private AgentWebFragment webFragment;
    private Activity _activity;
    private UMShareAPI mUMShareAPI;

    public volatile static boolean measureDid = false;



    public AndroidInterface(AgentWeb agentWeb, AgentWebFragment fragment) {
        this.web = agentWeb;
        this.webFragment = fragment;
        _activity = fragment.getActivity();

    }


    @JavascriptInterface
    public void getUserInfo(String method) {
        Log.i("haix", "getUserInfo: " + method);
        int accountId = (int) SPUtils.get(_activity, "accountId", 0);
        String token = (String) SPUtils.get(_activity, "token", "");
        UserManager.getInstance().setAccountId(accountId);
        UserManager.getInstance().setToken(token);

        if ("".equals(token)) {
            token = null;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("accountId", accountId);
        jsonObject.put("token", token);
        if (web != null) {
            Log.i("haix", "getUserInfo2: " + jsonObject.toJSONString());
            web.getJsAccessEntrace().quickCallJs(method, jsonObject.toJSONString());

        }

    }

    @JavascriptInterface
    public void testResult(String re) {
        if (web != null) {
            synchronized (AndroidInterface.class) {

                Object ob = JSON.toJSON(measureDid);
                web.getJsAccessEntrace().quickCallJs(re, ob.toString());

                measureDid = false;

            }
        }
    }


    @JavascriptInterface
    public void sugarTest() {
        Log.i("haix", "========sugarTest: ");
        if (webFragment != null) {
            webFragment.toGlu();
        }

    }

    @JavascriptInterface
    public void openCamera(String re) {
        Log.i("haix", "========openCamera: " + re);
        //{‘callback’:‘func’,‘count’:‘1’,‘size’:‘2000’}
//        {"callback":"uploadFileImage","count":1,"size":2000}
        JSONObject jsonObject = JSONObject.parseObject(re);
        final String methodName = jsonObject.getString("callback");

        if (!TextUtils.isEmpty(methodName) && webFragment != null) {


            webFragment.setCameraCallBack(new AgentWebFragment.CameraCallBack() {

                @Override
                public void getCameraBase64String(String str) {
                    if (web != null) {


                        str = "data:image/jpeg;base64," + str;
                        String[] s = {str};

                        Object jb = JSONArray.toJSON(s);
                        Log.i("haix", "图排: " + str);
                        web.getJsAccessEntrace().quickCallJs(methodName, jb.toString());
                    }
                }
            });

            webFragment.goPhotoAlbum();
        }

    }

    @JavascriptInterface
    public void logout() {
        Log.i("haix", "========logout退出登陆: ");
        UserManager.getInstance().setAccountId(0);
        UserManager.getInstance().setToken("");
        SPUtils.put(_activity, "accountId", 0);
        SPUtils.put(_activity, "token", "");
    }


    @JavascriptInterface
    public void login(String re) {
        Log.i("haix", "========login登陆: " + re);
        //{'accountId':'122','token':'xxx','refreshToken':'xxx','callback':'JS回调方法'}
        JSONObject jsonObject = JSONObject.parseObject(re);
        if(jsonObject == null){
            return;
        }
        Log.i("haix", "========login登陆2: " + jsonObject);
        final int accountId = jsonObject.getInteger("accountId");
        final String token = jsonObject.getString("token");

        if (!TextUtils.isEmpty(token)) {
            UserManager.getInstance().setAccountId(accountId);
            UserManager.getInstance().setToken(token);
            SPUtils.put(_activity, "accountId", accountId);
            SPUtils.put(_activity, "token", token);
        }
    }


    @JavascriptInterface
    public void wechat(final String re) {
        Log.i("haix", "++++++++++++++++wechat: " + re);
        mUMShareAPI = UMShareAPI.get(_activity);
        mUMShareAPI.getPlatformInfo(_activity, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA platform) {
                Log.i(TAG, "onStart");
            }

            @Override
            public void onComplete(SHARE_MEDIA platform, int action, final Map<String, String> data) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        WechatInfo info = new WechatInfo();
                        info.setAppId("wx1971c8a28a09ad4b");
                        info.setHeadimgurl(data.get("iconurl"));
                        info.setNickname(data.get("name"));
                        info.setOpenid(data.get("openid"));
                        int gender = data.get("gender").equals("男") ? 0 : 1;
                        info.setSex(gender);
                        info.setUnionid(data.get("unionid"));
                        ApiUtil.createApi(IGuiderApi.class).wechatLoginToken(info).enqueue(new ApiCallBack<BeanOfWecaht>() {
                            @Override
                            public void onApiResponse(Call<BeanOfWecaht> call, Response<BeanOfWecaht> response) {
                                super.onApiResponse(call, response);
                                web.getJsAccessEntrace().quickCallJs(re, new Gson().toJson(response.body()));
                            }

                            @Override
                            public void onApiResponseNull(Call<BeanOfWecaht> call, Response<BeanOfWecaht> response) {
                                super.onApiResponseNull(call, response);
                                Toast.makeText(_activity, "为获取到有效数据", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Call<BeanOfWecaht> call, Throwable t) {
                                super.onFailure(call, t);
                                Toast.makeText(_activity, t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


//                        Log.i("haix", "-----微信回调的data: " + data);
//
//                        JSONObject jsonObject = new JSONObject();
//                        //HashMap<String, Object> request = new HashMap<>();
//                        jsonObject.put("appId", "wxfc96fa4b92b43c9a");
//                        jsonObject.put("headimgurl", data.get("iconurl"));
//                        jsonObject.put("nickname", data.get("name"));
//                        jsonObject.put("openid", data.get("openid"));
//
//                        int gender = data.get("gender").equals("男") ? 0 : 1;
//                        jsonObject.put("sex", gender);
//                        jsonObject.put("unionid", data.get("unionid"));
//
//                        //wechatAppId *  code
//
//
//                        NetRequest.getInstance().weixLogin(jsonObject, new WeakReference<Activity>(_activity), new NetRequest.NetCallBack() {
//
//                            @Override
//                            public void result(int code, String result) {
//                                if (code == 0) {
////                                    SPUtils.put(_activity, "token", UserManager.getInstance().getToken());
////                                    SPUtils.put(_activity, "accountId", UserManager.getInstance().getAccountId());
//
//                                    web.getJsAccessEntrace().quickCallJs(re, result);
//                                } else {
//                                    Toast.makeText(_activity, result, Toast.LENGTH_SHORT).show();
//                                }
//
//
//
//                            }
//                        });


                    }
                }).start();
            }

            @Override
            public void onError(SHARE_MEDIA platform, int action, Throwable t) {
                Toast.makeText(_activity, "失败：" + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.i("MainActivity onError", t.getMessage());
            }

            @Override
            public void onCancel(SHARE_MEDIA platform, int action) {
                // Toast.makeText(MainActivity.this, "取消了", Toast.LENGTH_LONG).show();
            }
        });
    }




}
