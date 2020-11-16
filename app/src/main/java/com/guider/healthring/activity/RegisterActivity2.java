package com.guider.healthring.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputLayout;

import androidx.appcompat.widget.Toolbar;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.guider.health.apilib.BuildConfig;
import com.guider.healthring.Commont;
import com.guider.healthring.MyApp;
import com.guider.healthring.R;
import com.guider.healthring.activity.wylactivity.wyl_util.service.ConnectManages;
import com.guider.healthring.adpter.PhoneAdapter;
import com.guider.healthring.bean.AreCodeBean;
import com.guider.healthring.bean.BlueUser;
import com.guider.healthring.bean.UserInfoBean;
import com.guider.healthring.siswatch.WatchBaseActivity;
import com.guider.healthring.siswatch.utils.WatchUtils;
import com.guider.healthring.util.Common;
import com.guider.healthring.util.Md5Util;
import com.guider.healthring.util.SharedPreferencesUtils;
import com.guider.healthring.util.ToastUtil;
import com.guider.healthring.util.URLs;
import com.guider.healthring.view.PhoneAreaCodeView;
import com.guider.healthring.view.PrivacyActivity;
import com.guider.healthring.w30s.utils.httputils.RequestPressent;
import com.guider.healthring.w30s.utils.httputils.RequestView;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Created by thinkpad on 2017/3/4.
 * 注册页面
 */

public class RegisterActivity2 extends WatchBaseActivity implements RequestView, View.OnClickListener {

    private static final String TAG = "RegisterActivity2";

    TextView tvTitle;
    TextView tv_phone_head;
    TextView registerAgreement;
    TextInputLayout usernameInput;
    TextInputLayout textinputPassword;
    EditText codeEt;
    EditText username;
    EditText password;
    Button sendBtn;
    TextInputLayout textinput_code;
    Toolbar toolbar;

    private Subscription subscribe;


    private List<Integer> phoneHeadList;
    private PhoneAdapter phoneAdapter;

//    //倒计时
//    MyCountDownTimerUtils countTimeUtils;

    private PhoneAreaCodeView phoneAreaCodeView;


    private RequestPressent requestPressent;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regsiter2);
        initViewIds();
        initViews();

        requestPressent = new RequestPressent();
        requestPressent.attach(this);

    }

    private void initViewIds() {
        tvTitle = findViewById(R.id.tv_title);
        tv_phone_head = findViewById(R.id.tv_phone_head);
        registerAgreement = findViewById(R.id.register_agreement_my);
        usernameInput = findViewById(R.id.username_input);
        textinputPassword = findViewById(R.id.textinput_password_regster);
        codeEt = findViewById(R.id.code_et_regieg);
        username = findViewById(R.id.username_regsiter);
        password = findViewById(R.id.password_logonregigter);
        sendBtn = findViewById(R.id.send_btn);
        textinput_code = findViewById(R.id.textinput_code);
        toolbar = findViewById(R.id.toolbar);
        findViewById(R.id.login_btn_reger).setOnClickListener(this);
        sendBtn.setOnClickListener(this);
        findViewById(R.id.login_btn_emil_reger).setOnClickListener(this);
        tv_phone_head.setOnClickListener(this);
    }


    private void initViews() {
        tvTitle.setText(R.string.user_regsiter);
        usernameInput.setHint(getResources().getString(R.string.input_name));
        tv_phone_head.setText("+86");
        codeEt.setHintTextColor(getResources().getColor(R.color.white));
        if (com.guider.healthring.BuildConfig.GOOGLEPLAY) {
            RelativeLayout codeLayout = findViewById(R.id.codeLayout);
            codeLayout.setVisibility(View.GONE);
        }
        //倒计时
        //countTimeUtils = new MyCountDownTimerUtils(60 * 1000, 1000);

        //初始化底部声明
        String INSURANCE_STATEMENT = getResources().getString(R.string.register_agreement);
        SpannableString spanStatement = new SpannableString(INSURANCE_STATEMENT);
        ClickableSpan clickStatement = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                //跳转到协议页面
                startActivity(new Intent(
                        RegisterActivity2.this, PrivacyActivity.class));
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(false);
            }
        };
        spanStatement.setSpan(clickStatement, 0, INSURANCE_STATEMENT.length(),
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spanStatement.setSpan(new ForegroundColorSpan(
                        getResources().getColor(R.color.new_colorAccent)), 0,
                INSURANCE_STATEMENT.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        registerAgreement.setText(R.string.agree_agreement);
        registerAgreement.append(spanStatement);
        registerAgreement.setMovementMethod(LinkMovementMethod.getInstance());
        toolbar.setNavigationIcon(R.mipmap.backs);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    @SuppressLint("SetTextI18n")
    private void initTime() {
        final int countTime = 60;
        sendBtn.setText(getResources().getString(R.string.resend) + "(" + countTime + "s)");
        sendBtn.setClickable(false);
        Subscriber subscriber = new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }


            @Override
            public void onNext(Integer integer) {
                if (integer == 0) {
                    //isTime = false;
                    sendBtn.setText(getResources().getString(R.string.resend));
                    sendBtn.setClickable(true);
                } else {
                    sendBtn.setText(getResources().getString(R.string.resend) + "(" + integer + "s)");
                }
            }
        };
        Subscription subscribe = Observable.interval(0, 1, TimeUnit.SECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(increaseTime -> countTime - increaseTime.intValue())
                .take(countTime + 1)
                .subscribe(subscriber);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_btn_emil_reger://跳转到邮箱注册
                startActivity(new Intent(
                        RegisterActivity2.this, RegisterActivity.class));
                break;

            case R.id.tv_phone_head:    //选择区号
                choosePhoneArea();
                break;
            case R.id.send_btn:     //发送验证码
                gendPhoneCode();
                break;
            case R.id.login_btn_reger:  //注册
                //中文状态为手机号
                String phoneStr = username.getText().toString().trim(); //手机号
                String phoneCode = tv_phone_head.getText().toString();  //验证码
                String phonePwd = password.getText().toString();        //密码
                if (com.guider.healthring.BuildConfig.GOOGLEPLAY) {
                    if (WatchUtils.isEmpty(phoneStr)
                            || WatchUtils.isEmpty(phonePwd))
                        return;
                    registerForNoCode(phoneStr, phonePwd);
                } else {
                    if (WatchUtils.isEmpty(phoneStr)
                            || WatchUtils.isEmpty(phoneCode)
                            || WatchUtils.isEmpty(phonePwd))
                        return;
                    registerRemote(phoneStr, phoneCode, phonePwd);
                }
                break;
        }
    }

    /**
     * 手机号+密码注册，不需要验证码
     *
     * @param phoneStr 手机号
     * @param phonePwd 密码
     */
    private void registerForNoCode(String phoneStr, String phonePwd) {
        try {
            Gson gson = new Gson();
            HashMap<String, Object> map = new HashMap<>();
            map.put("phone", phoneStr);
            map.put("pwd", Md5Util.Md532(phonePwd));
            map.put("status", "0");
            map.put("type", "0");
            String mapjson = gson.toJson(map);
            Log.e("msg", "-mapjson-" + mapjson);
            if (requestPressent != null) {
                requestPressent.getRequestJSONObject(0x02,
                        Commont.FRIEND_BASE_URL + URLs.myHTTPs,
                        RegisterActivity2.this, mapjson, 2);
                //注册盖德账号
                registerGuiderAccount();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //选择区号
    @SuppressLint("SetTextI18n")
    private void choosePhoneArea() {
        phoneAreaCodeView = new PhoneAreaCodeView(RegisterActivity2.this);
        phoneAreaCodeView.show();
        phoneAreaCodeView.setPhoneAreaClickListener(areCodeBean -> {
            phoneAreaCodeView.dismiss();
            tv_phone_head.setText("+" + areCodeBean.getPhoneCode());
        });
    }


    //提交注册信息
    private void registerRemote(String phoneStr, String verCode, String pwd) {
        Gson gson = new Gson();
        HashMap<String, Object> map = new HashMap<>();
        map.put("phone", phoneStr);
        map.put("pwd", Md5Util.Md532(pwd));
        map.put("code", verCode);
        map.put("status", "0");
        map.put("type", "0");
        String mapjson = gson.toJson(map);
        Log.e("msg", "-mapjson-" + mapjson);
        if (requestPressent != null) {
            requestPressent.getRequestJSONObject(0x02,
                    Commont.FRIEND_BASE_URL + URLs.myHTTPs,
                    RegisterActivity2.this, mapjson, 2);
            registerGuiderAccount();
        }

    }

    //获取手机号验证码 盖德
    private void gendPhoneCode() {
        if (!Common.isFastClick())
            return;
        if (!ConnectManages.isNetworkAvailable(RegisterActivity2.this)) {
            ToastUtil.showShort(RegisterActivity2.this,
                    getResources().getString(R.string.string_not_net));
            return;
        }

        //输入的手机号码
        String phoneStr = username.getText().toString();
        //选择的区号
        String areaCodeStr = tv_phone_head.getText().toString();
        if (WatchUtils.isEmpty(phoneStr) || WatchUtils.isEmpty(areaCodeStr))
            return;
        initTime();
        snedPhoneNumToServer(phoneStr, areaCodeStr);
    }


    /**
     * 获取手机号验证码
     *
     * @param number 手机号
     * @param pCode  国标码
     */
    private void snedPhoneNumToServer(String number, String pCode) {
        Gson gson = new Gson();
        HashMap<String, Object> map = new HashMap<>();
        map.put("phone", WatchUtils.removeStr(number));
        map.put("code", StringUtils.substringAfter(pCode, "+"));
        String mapjson = gson.toJson(map);
        Log.e("msg", "-mapjson-" + mapjson);
        if (requestPressent != null) {
            requestPressent.getRequestJSONObject(0x01,
                    Commont.FRIEND_BASE_URL + URLs.GET_PHONE_VERCODE_URL,
                    RegisterActivity2.this, mapjson, 1);
        }
    }


    //注册盖德的账号
    private void registerGuiderAccount() {
        String phoneStr = username.getText().toString();
        if (requestPressent != null && WatchUtils.isEmpty(phoneStr)) {
            String loginUrl = BuildConfig.APIURL + "api/v1/login/onlyphone?phone="
                    + phoneStr; // http://api.guiderhealth.com/
            requestPressent.getRequestJSONObject(1001, loginUrl,
                    RegisterActivity2.this, 1);
            //http://api.guiderhealth.com/api/v1/phonecode?phone=
        }

    }


    @Override
    public void showLoadDialog(int what) {

    }

    @Override
    public void successData(int what, Object object, int daystag) {
        Log.e(TAG, "----------what=" + what + "--=obj=" + object.toString());
        if (object == null)
            return;
        if (object.toString().contains("<html>"))
            return;
        try {
            JSONObject jsonObject = new JSONObject(object.toString());
            switch (what) {
                case 0x01:  //获取验证码返回
                    ToastUtil.showToast(RegisterActivity2.this,
                            jsonObject.getString("data")
                                    + jsonObject.getString("msg"));
                    break;
                case 0x02:  //注册返回
                    analysisRegiData(jsonObject);
                    break;
                case 1001:  //注册盖德返回
                    if (WatchUtils.isNetRequestSuccess(object.toString(), 0)) {
                        try {
                            if (jsonObject.has("data")) {
                                JSONObject dataJsonObject = jsonObject.getJSONObject("data");
                                long accountId = dataJsonObject.getLong("accountId");
                                SharedPreferencesUtils.setParam(MyApp.getInstance(),
                                        "accountIdGD", accountId);
                                String token = dataJsonObject.getString("token");
                                SharedPreferencesUtils.setParam(MyApp.getInstance(),
                                        "tokenGD", accountId);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


//        switch (what){
//            case 0x01:
//                analysisRegiInfo(object.toString());
//                break;
//            case 1001:  //登录盖德后台返回
//                if(WatchUtils.isNetRequestSuccess(object.toString(),0)){
//                    try {
//                        JSONObject jsonObject = new JSONObject(object.toString());
//                        if(jsonObject.has("data")){
//                            JSONObject dataJsonObject = jsonObject.getJSONObject("data");
//                            long accountId = dataJsonObject.getLong("accountId");
//                            SharedPreferencesUtils.setParam(MyApp.getInstance(), "accountIdGD", accountId);
//                        }
//
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }
//                }
//                break;
//            case 1002:  //获取手机验证码
//                try {
//                    JSONObject jsonObject = new JSONObject(object.toString());
//                    ToastUtil.showToast(RegisterActivity2.this, jsonObject.getString("data")+jsonObject.getString("msg"));
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//
//                break;
//        }

    }

    @Override
    public void failedData(int what, Throwable e) {
        ToastUtil.showToast(RegisterActivity2.this, e.getMessage() + "");
    }

    @Override
    public void closeLoadDialog(int what) {

    }


    //提交注册返回
    private void analysisRegiData(JSONObject jsonObject) {
        try {
            if (!jsonObject.has("code"))
                return;
            if (jsonObject.getInt("code") == 200) {
                String data = jsonObject.getString("data");
                UserInfoBean userInfoBean = new Gson().fromJson(data, UserInfoBean.class);
                if (userInfoBean != null) {
                    Common.customer_id = userInfoBean.getUserid();
                    SharedPreferencesUtils.saveObject(
                            RegisterActivity2.this,
                            Commont.USER_ID_DATA, userInfoBean.getUserid());
                    String phone =  username.getText().toString().trim(); //手机号
                    Intent intent = new Intent(
                            RegisterActivity2.this, PersonDataActivity.class);
                    intent.putExtra("phone",phone);
                    startActivity(intent);
                    finish();
                }
            } else {
                String msg = jsonObject.getString("msg");
                if (msg.equals("用户已被注册") && jsonObject.getInt("code") == 5000) {
                    msg = "用户已注册,请您直接登录即可";
                    tvTitle.postDelayed(this::finish, 1000);
                }
                ToastUtil.showToast(RegisterActivity2.this, msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (requestPressent != null)
            requestPressent.detach();
        if (subscribe != null && subscribe.isUnsubscribed()) {
            subscribe.unsubscribe();
        }
    }


    private void analysisRegiInfo(String str) {
        try {
            JSONObject jsonObject = new JSONObject(str.toString());
            if (!jsonObject.has("resultCode"))
                ToastUtil.showToast(RegisterActivity2.this, str);
            if (jsonObject.getString("resultCode").equals("001")) {
                BlueUser userInfo = new Gson().fromJson(
                        jsonObject.getString("userInfo"), BlueUser.class);
                Common.userInfo = userInfo;
                Common.customer_id = userInfo.getUserId();

                SharedPreferencesUtils.setParam(RegisterActivity2.this,
                        SharedPreferencesUtils.CUSTOMER_ID, Common.customer_id);
                startActivity(new Intent(RegisterActivity2.this,
                        PersonDataActivity.class));
                finish();
            } else {
                WatchUtils.verServerCode(RegisterActivity2.this,
                        jsonObject.getString("resultCode"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
