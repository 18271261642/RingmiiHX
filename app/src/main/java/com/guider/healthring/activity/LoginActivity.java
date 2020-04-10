package com.guider.healthring.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.guider.health.apilib.BuildConfig;
import com.guider.health.apilib.enums.Gender;
import com.guider.health.common.utils.JsonUtil;
import com.guider.healthring.Commont;
import com.guider.healthring.CustomMade;
import com.guider.healthring.MyApp;
import com.guider.healthring.R;
import com.guider.healthring.bean.BlueUser;
import com.guider.healthring.bean.UserInfoBean;
import com.guider.healthring.net.OkHttpObservable;
import com.guider.healthring.rxandroid.DialogSubscriber;
import com.guider.healthring.rxandroid.SubscriberOnNextListener;
import com.guider.healthring.siswatch.NewSearchActivity;
import com.guider.healthring.siswatch.WatchBaseActivity;
import com.guider.healthring.siswatch.utils.WatchUtils;
import com.guider.healthring.siswatch.view.LoginWaveView;
import com.guider.healthring.util.Common;
import com.guider.healthring.util.Md5Util;
import com.guider.healthring.util.OkHttpTool;
import com.guider.healthring.util.SharedPreferencesUtils;
import com.guider.healthring.util.ToastUtil;
import com.guider.healthring.util.URLs;
import com.guider.healthring.util.VerifyUtil;
import com.guider.healthring.util.WxScanUtil;
import com.guider.healthring.view.PrivacyActivity;
import com.guider.healthring.view.PromptDialog;
import com.guider.healthring.w30s.utils.httputils.RequestPressent;
import com.guider.healthring.w30s.utils.httputils.RequestView;
import com.guider.healthring.xinlangweibo.SinaUserInfo;
import com.guider.healthringx.wxapi.WXEntryActivity;
import com.guider.healthringx.wxapi.WXEntryActivityAdapter;
import com.guider.libbase.thirdlogin.ThirdLogin;
import com.guider.libbase.thirdlogin.line.ILineLogin;
import com.linecorp.linesdk.LoginDelegate;
import com.tencent.connect.common.Constants;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendAuth;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;
import com.yanzhenjie.permission.AndPermission;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by thinkpad on 2017/3/3.
 */

public class LoginActivity extends WatchBaseActivity implements Callback , RequestView, ILineLogin {//}, PlatformActionListener {

    /**
     * Line第三方登陆相关
     */
    private final LoginDelegate loginDelegate = LoginDelegate.Factory.create();

    @BindView(R.id.login_visitorTv)
    TextView loginVisitorTv;
    //波浪形曲线
    @BindView(R.id.login_waveView)
    LoginWaveView loginWaveView;

    @Override
    public boolean handleMessage(Message msg) {
        return false;
    }

    @BindView(R.id.ll_bottom_tabaa)
    LinearLayout guolei;//在国内


    @BindView(R.id.username)
    EditText username;
    @BindView(R.id.username_input_logon)
    TextInputLayout usernameInput;
    @BindView(R.id.password_logon)
    EditText password;
    @BindView(R.id.textinput_password)
    TextInputLayout textinputPassword;
    @BindView(R.id.xinlang_iv)
    RelativeLayout weiboIv;
    @BindView(R.id.qq_iv)
    RelativeLayout qqIv;
    @BindView(R.id.weixin_iv)
    RelativeLayout weixinIv;
    @BindView(R.id.line_iv)
    RelativeLayout lineIv;
    private static final String TAG = "LoginActivity";
    private DialogSubscriber dialogSubscriber;
    private SubscriberOnNextListener<String> subscriberOnNextListener;
    //qq
    //private Tencent mTencent;
    String openID;//唯一标识符 1105653402
    private final String QQAPP_ID = "101357650";// 测试时使用，真正发布的时候要换成自己的APP_ID
    public static String mAppid;

    private IUiListener loginListener;
    // private CallbackManager callbackManager;
    JSONObject jsonObject;

    private TextView userinfo_tv;
    private SinaUserInfo userInfo;


    private BluetoothAdapter bluetoothAdapter;

    private IWXAPI iwxapi;

    private RequestPressent requestPressent;


    private ThirdLogin mThirdLogin;
    private WXEntryActivityAdapter mWXEntryActivityAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        // 定制logo
        int id = CustomMade.getSmallLogo();
        if (id > 0) {
            ImageView ivLogo = findViewById(R.id.logo_img);
            ivLogo.setImageResource(id);
        }

        iwxapi = WXAPIFactory.createWXAPI(this, Commont.WX_APP_SECRET, true);
        iwxapi.registerApp(Commont.WX_APP_ID);
        mThirdLogin = new ThirdLogin(this);
        //Google Play用这个
//        iwxapi = WXAPIFactory.createWXAPI(this, "wx7e5e9e90ae4d8f51", true);
//        iwxapi.registerApp("wx7e5e9e90ae4d8f51");




        initViews();
    }


    @SuppressLint("MissingPermission")
    private void initViews() {
        loginWaveView.startMove();  //波浪线贝塞尔曲线

        requestPressent = new RequestPressent();
        requestPressent.attach(this);
        mWXEntryActivityAdapter = new WXEntryActivityAdapter(this, requestPressent);

        subscriberOnNextListener = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {
                //Loaddialog.getInstance().dissLoading();
                Log.e("LoainActivity", "-----loginresult---" + result);
                Gson gson = new Gson();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String loginResult = jsonObject.getString("resultCode");
                    if ("001".equals(loginResult)) {
                        BlueUser userInfo = gson.fromJson(jsonObject.getString("userInfo"), BlueUser.class);
//                        MyLogUtil.i("msg", "-userInfo-" + userInfo.toString());
                        Common.userInfo = userInfo;
                        Common.customer_id = userInfo.getUserId();

                        //保存userid
                        SharedPreferencesUtils.saveObject(LoginActivity.this, "userId", userInfo.getUserId());
                        SharedPreferencesUtils.saveObject(LoginActivity.this, "userInfo", jsonObject.getString("userInfo"));
                        Log.e("LoainActivity", "-----loginresult---" + userInfo.toString());
//                        WatchUtils.setIsWxLogin("",phone);
                        WatchUtils.setIsWxLogin("LOGION_PHONE",jsonObject.getString("userInfo"));

                        SharedPreferencesUtils.setParam(LoginActivity.this, SharedPreferencesUtils.CUSTOMER_ID, Common.customer_id);

                        //SharedPreferencesUtils.saveObject(LoginActivity.this, Commont.USER_INFO_DATA, userStr);

                        startActivity(new Intent(LoginActivity.this, NewSearchActivity.class));
                        finish();
                    } else if (loginResult.equals("003")) {
                        ToastUtil.showShort(LoginActivity.this, getString(R.string.yonghuzhej));
                    } else if (loginResult.equals("004")) {
                        ToastUtil.showShort(LoginActivity.this, getString(R.string.string_user_pass_error));
                    } else if (loginResult.equals("006")) {
                        ToastUtil.showShort(LoginActivity.this, getString(R.string.miamacuo));
                    } else {
                        ToastUtil.showShort(LoginActivity.this, getString(R.string.miamacuo));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };


        guolei.setVisibility(View.VISIBLE);

        usernameInput.setHint(getResources().getString(R.string.input_email));
        //请求读写SD卡的权限
        AndPermission.with(LoginActivity.this)
                .runtime()
                .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION)
                .start();

        //判断蓝牙是否开启
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            turnOnBlue();
        }

    }


    private void turnOnBlue() {
        // 请求打开 Bluetooth
        Intent requestBluetoothOn = new Intent(
                BluetoothAdapter.ACTION_REQUEST_ENABLE);
        // 设置 Bluetooth 设备可以被其它 Bluetooth 设备扫描到
        requestBluetoothOn
                .setAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        // 设置 Bluetooth 设备可见时间
        requestBluetoothOn.putExtra(
                BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
                1111);
        // 请求开启 Bluetooth
        this.startActivityForResult(requestBluetoothOn,
                1112);
    }



    @OnClick({R.id.register_btn, R.id.forget_tv,
            R.id.login_btn, R.id.xinlang_iv,
            R.id.qq_iv, R.id.weixin_iv,
            R.id.login_visitorTv,R.id.privacyTv, R.id.line_iv})
    public void onClick(View view) {
        Context context = view.getContext();
        switch (view.getId()) {

            case R.id.register_btn://注册
                startActivity(new Intent(LoginActivity.this, RegisterActivity2.class));
                break;
            case R.id.forget_tv://忘记密码
                startActivity(new Intent(LoginActivity.this, ForgetPasswardActivity.class));
                break;
            case R.id.login_btn:
                boolean lauage = VerifyUtil.isZh(LoginActivity.this);

                //登录时判断
                String pass = password.getText().toString();
                String usernametxt = username.getText().toString();
                if (!WatchUtils.isEmpty(usernametxt) && !WatchUtils.isEmpty(pass)) {
                    loginRemote(usernametxt, pass);
                } else {
                    ToastUtil.showToast(this, getResources().getString(R.string.string_login_toast));
                }


                break;
            case R.id.xinlang_iv://f新浪登录
//                authInfo = new AuthInfo(context, SWB_APP_ID, SWB_REDIRECT_URL, SWB_SCOPE);
//                ssoHandler = new SsoHandler(LoginActivity.this, authInfo);
//                ssoHandler.authorize(new AuthListener());
                break;
            case R.id.qq_iv://QQ登录
                loginListener = new IUiListener() {
                    @Override
                    public void onComplete(Object o) {
                        JSONObject jsonObject = (JSONObject) o;
                        Log.e(TAG, "-----QQ登录返回----" + o.toString());
                        try {
                            String accessToken = jsonObject.getString("access_token");
                            String expires = jsonObject.getString("expires_in");
                            openID = jsonObject.getString("openid");
//                            mTencent.setAccessToken(accessToken, expires);
//                            mTencent.setOpenId(openID);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(UiError uiError) {
                        Log.i("=====", "===失败");
                    }

                    @Override
                    public void onCancel() {
                        Log.i("=====", "===取消");
                    }
                };
                break;
            case R.id.weixin_iv://微信登录
                mThirdLogin.wechatLogin(hashMap -> {
                    String strUserInfo = JsonUtil.toStr(hashMap);
                    mWXEntryActivityAdapter.analysisWXUserInfo(strUserInfo);
                });
                break;
            case R.id.login_visitorTv:  //游客登录
                final PromptDialog pd = new PromptDialog(LoginActivity.this);
                pd.show();
                pd.setTitle(getResources().getString(R.string.prompt));
                pd.setContent(getResources().getString(R.string.login_alert));
                pd.setleftText(getResources().getString(R.string.cancle));
                pd.setrightText(getResources().getString(R.string.confirm));
                pd.setListener(new PromptDialog.OnPromptDialogListener() {
                    @Override
                    public void leftClick(int code) {
                        pd.dismiss();
                    }

                    @Override
                    public void rightClick(int code) {
                        pd.dismiss();
                        Gson gson = new Gson();
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("phone", "bozlun888@gmail.com");
                        map.put("pwd", Md5Util.Md532("e10adc3949ba59abbe56e057f20f883e"));
                        String mapjson = gson.toJson(map);
//                        Log.e("msg", "-mapjson-" + mapjson);
                        dialogSubscriber = new DialogSubscriber(subscriberOnNextListener, LoginActivity.this);
                        OkHttpObservable.getInstance().getData(dialogSubscriber, URLs.HTTPs + URLs.logon, mapjson);

                        SharedPreferences userSettings = getSharedPreferences("Login_id", 0);
                        SharedPreferences.Editor editor = userSettings.edit();
                        editor.putInt("id", 0);
                        editor.commit();

                    }
                });
                break;
            case R.id.privacyTv:    //隐私政策
                startActivity(PrivacyActivity.class);
                break;
            case R.id.line_iv: // LINE 登陆
                mThirdLogin.lineOfficeLogin(this, findViewById(R.id.lb_line), "1653887386", null, hashMap -> {
                    // 用户信息可以做自己的操作
                    registerRingUser(hashMap);
                }, () -> {
                    startActivity(NewSearchActivity.class);
                    finish();
                });
                break;
        }
    }

    // 注册到手环方平台
    private void registerRingUser(HashMap<String, Object> map) {
        Map<String,Object> params = new HashMap<>();
        if (map.containsKey("openId"))
            params.put("thirdId", map.get("openId").toString());
        params.put("thirdType",  "3");
        if (map.containsKey("headUrl"))
            params.put("image", map.get("headUrl").toString());
        if (map.containsKey("gender"))
            params.put("sex", ((Gender)map.get("Gender")) == Gender.MAN ? "M" : "F");
        if (map.containsKey("nickName"))
            params.put("nickName", map.get("nickName").toString());
        if (Commont.isDebug)Log.e(TAG, "3333游客注册或者登陆参数：" + params.toString());
        String url = Commont.FRIEND_BASE_URL + URLs.disanfang;
        if (Commont.isDebug)Log.e(TAG, "====  json  " +  new Gson().toJson(params));
        OkHttpTool.getInstance().doRequest(url, new Gson().toJson(params), this, new OkHttpTool.HttpResult() {
            @Override
            public void onResult(String result) {
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
                            SharedPreferencesUtils.saveObject(LoginActivity.this, Commont.USER_ID_DATA, userInfoBean.getUserid());
                            SharedPreferencesUtils.saveObject(LoginActivity.this, "userInfo", userStr);
                            SharedPreferencesUtils.saveObject(LoginActivity.this, Commont.USER_INFO_DATA, userStr);

                            // startActivity(new Intent(LoginActivity.this, NewSearchActivity.class));
                            // finish();
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    //用户手机登录
    private void loginRemote(String uName, String uPwd) {
        Gson gson = new Gson();
        HashMap<String, Object> map = new HashMap<>();
        map.put("phone", uName);
        map.put("pwd", Md5Util.Md532(uPwd));
        String mapjson = gson.toJson(map);
        Log.e("msg", "-mapjson-" + mapjson);
        if (requestPressent != null) {
            requestPressent.getRequestJSONObject(0x01, Commont.FRIEND_BASE_URL + URLs.logon, LoginActivity.this, mapjson, 1);
        }



        //登录到盖德后台
        String loginUrl = BuildConfig.APIURL + "api/v1/login/onlyphone?phone=" + uName; // http://api.guiderhealth.com/
        Log.e(TAG,"-------手机号登录的url="+loginUrl);
        OkHttpTool.getInstance().doRequest(loginUrl, null, "1", new OkHttpTool.HttpResult() {
            @Override
            public void onResult(String result) {
                Log.e(TAG,"-------手机号录到盖德="+result);
                if(WatchUtils.isNetRequestSuccess(result,0)){
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if(jsonObject.has("data")){
                            JSONObject dataJsonObject = jsonObject.getJSONObject("data");
                            long accountId = dataJsonObject.getLong("accountId");
                            SharedPreferencesUtils.setParam(MyApp.getInstance(), "accountIdGD", accountId);
                            String token = dataJsonObject.getString("token");
                            SharedPreferencesUtils.setParam(MyApp.getInstance(), "tokenGD", accountId);
                            WxScanUtil.handle(LoginActivity.this, accountId, new WxScanUtil.IWxScan() {
                                @Override
                                public void onError() {
                                    startActivity(NewSearchActivity.class);
                                    finish();
                                }
                                @Override
                                public void onOk() {
                                    Intent intent = new Intent(LoginActivity.this, WxScanActivity.class);
                                    intent.putExtra("accountId", accountId);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }, false);


    }

    public LoginDelegate getLoginDelegate() {
        return loginDelegate;
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Log.e(TAG, "----onActivityResult------" + requestCode + "---resultCode--" + resultCode);
//        Log.e(TAG, "----requestCode:" + requestCode);
//        Log.e(TAG, "----resultCode:" + resultCode);
        if (requestCode == Constants.REQUEST_LOGIN) {
            if (resultCode == -1) {

            }
        } else if (loginDelegate.onActivityResult(requestCode, resultCode, data)) {
            // Login result is consumed.
            return;
        }
    }



    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                //上传到服务器
                try {
                    //判断网络是否连接
//                    Boolean is=   ConnectManages.isNetworkAvailable(LoginActivity.this);
//                    if(is==true) {
                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    try {
                        jsonObject = new JSONObject();
                        jsonObject.put("thirdId", userInfo.getUid());
                        jsonObject.put("thirdType", 4);//微博
                        jsonObject.put("image", userInfo.getAvatarHd());//头像地址
                        if (userInfo.getSEX().equals("m")) {
                            jsonObject.put("sex", "M");
                        } else {
                            jsonObject.put("sex", "F");
                        }//性别
                        jsonObject.put("nickName", userInfo.getName()); //姓名
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Request.Method.POST, URLs.HTTPs + URLs.disanfang, jsonObject,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
//                                    Log.e(TAG, "--------微博登录返回-=" + response.toString());
//                                    Log.d("eeeee", response.toString());
                                    String shuzhu = response.optString("userInfo").toString();
                                    if (response.optString("resultCode").toString().equals("001")) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(shuzhu);
                                            String userId = jsonObject.getString("userId");
                                            Gson gson = new Gson();
                                            BlueUser userInfo = gson.fromJson(shuzhu, BlueUser.class);
                                            Common.userInfo = userInfo;
                                            Common.customer_id = userId;
                                            //保存userid
                                            SharedPreferencesUtils.saveObject(LoginActivity.this, "userId", userInfo.getUserId());
                                            SharedPreferencesUtils.saveObject(LoginActivity.this, "userInfo", userInfo);
                                            //startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                            startActivity(new Intent(LoginActivity.this, NewSearchActivity.class));
                                            SharedPreferences userSettings = getSharedPreferences("Login_id", 0);
                                            SharedPreferences.Editor editor = userSettings.edit();
                                            editor.putInt("id", 1);
                                            editor.commit();

                                            finish();
                                        } catch (Exception E) {
                                            E.printStackTrace();
                                        }
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
//                            Log.e("LoginActivity", "---sina--error--" + error.getMessage());
                            Toast.makeText(LoginActivity.this, R.string.wangluo, Toast.LENGTH_SHORT).show();
                        }
                    }) {
                        @Override
                        public Map<String, String> getHeaders() {
                            HashMap<String, String> headers = new HashMap<>();
                            headers.put("Accept", "application/json");
                            headers.put("Content-Type", "application/json; charset=UTF-8");
                            return headers;
                        }
                    };
                    requestQueue.add(jsonRequest);
                    //}
//                    else{
//                        Toast.makeText(LoginActivity.this,R.string.wangluo,Toast.LENGTH_SHORT).show();}
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            handler.removeCallbacksAndMessages(null);
        } catch (Exception E) {
            E.printStackTrace();
        }
        loginWaveView.stopMove();
    }

    public long exitTime; // 储存点击退出时间

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    ToastUtil.showToast(LoginActivity.this, "再按一次退出程序");
                    exitTime = System.currentTimeMillis();
                    return false;
                } else {
                    // 全局推出
                    //removeAllActivity();
                    MyApp.getInstance().removeALLActivity();
                    return true;
                }
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }




    @Override
    public void showLoadDialog(int what) {

    }

    @Override
    public void successData(int what, Object object, int daystag) {
        if (object == null)
            return;
        if (object.toString().contains("<html>"))
            return;
        if (what == 0x01) {  //手机号登录
            loginForUserPhone(object.toString(), daystag);
        } else if (mWXEntryActivityAdapter != null) { // 兼容老的微信登陆绑定手机号代码，暂时
            mWXEntryActivityAdapter.successData(what, object, daystag);
        }
    }

    @Override
    public void failedData(int what, Throwable e) {

    }

    @Override
    public void closeLoadDialog(int what) {

    }


    //用户输入账号登录
    private void loginForUserPhone(String result, int tag) {
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
                    SharedPreferencesUtils.saveObject(LoginActivity.this, Commont.USER_ID_DATA, userInfoBean.getUserid());
                    SharedPreferencesUtils.saveObject(LoginActivity.this, "userInfo", userStr);
                    SharedPreferencesUtils.saveObject(LoginActivity.this, Commont.USER_INFO_DATA, userStr);

                    // startActivity(new Intent(LoginActivity.this, NewSearchActivity.class));
                    // finish();
                }

            }else{
                ToastUtil.showToast(LoginActivity.this,jsonObject.getString("msg") + jsonObject.getString("data"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
