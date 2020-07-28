package com.guider.healthring.activity;

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
import android.widget.TextView;

import com.google.gson.Gson;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Created by thinkpad on 2017/3/4.
 * 注册页面
 */

public class RegisterActivity2 extends WatchBaseActivity implements RequestView {

    private static final String TAG = "RegisterActivity2";

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_phone_head)
    TextView tv_phone_head;

    @BindView(R.id.register_agreement_my)
    TextView registerAgreement;
    @BindView(R.id.username_input)
    TextInputLayout usernameInput;
    @BindView(R.id.textinput_password_regster)
    TextInputLayout textinputPassword;
    @BindView(R.id.code_et_regieg)
    EditText codeEt;
    @BindView(R.id.username_regsiter)
    EditText username;
    @BindView(R.id.password_logonregigter)
    EditText password;
    @BindView(R.id.send_btn)
    Button sendBtn;
    @BindView(R.id.textinput_code)
    TextInputLayout textinput_code;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private Subscriber subscriber;



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
        ButterKnife.bind(this);


        initViews();

        requestPressent = new RequestPressent();
        requestPressent.attach(this);

    }


    private void initViews() {
        tvTitle.setText(R.string.user_regsiter);
        usernameInput.setHint(getResources().getString(R.string.input_name));
        tv_phone_head.setText("+86");
        codeEt.setHintTextColor(getResources().getColor(R.color.white));
        //倒计时
        //countTimeUtils = new MyCountDownTimerUtils(60 * 1000, 1000);

        //初始化底部声明
        String INSURANCE_STATEMENT = getResources().getString(R.string.register_agreement);
        SpannableString spanStatement = new SpannableString(INSURANCE_STATEMENT);
        ClickableSpan clickStatement = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                //跳转到协议页面
                startActivity(new Intent(RegisterActivity2.this, PrivacyActivity.class));
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(false);
            }
        };
        spanStatement.setSpan(clickStatement, 0, INSURANCE_STATEMENT.length(),
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spanStatement.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.new_colorAccent)), 0,
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

    private void initTime() {
        final int countTime = 60;
        sendBtn.setText(getResources().getString(R.string.resend) + "(" + countTime + "s)");
        sendBtn.setClickable(false);
        subscriber = new Subscriber<Integer>() {
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
        Observable.interval(0, 1, TimeUnit.SECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<Long, Integer>() {
                    @Override
                    public Integer call(Long increaseTime) {
                        return countTime - increaseTime.intValue();
                    }
                })
                .take(countTime + 1)
                .subscribe(subscriber);
    }

    @OnClick({R.id.login_btn_reger, R.id.send_btn,
            R.id.login_btn_emil_reger, R.id.tv_phone_head})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_btn_emil_reger://跳转到邮箱注册
                startActivity(new Intent(RegisterActivity2.this, RegisterActivity.class));
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
                if(WatchUtils.isEmpty(phoneStr) || WatchUtils.isEmpty(phoneCode) || WatchUtils.isEmpty(phonePwd))
                    return;
                //registerRemote(phoneStr,phoneCode);
                registerRemote(phoneStr,phoneCode,phonePwd);









//                if (!WatchUtils.isEmpty(phoneTxt) && !WatchUtils.isEmpty(pwdText)) {
//                    if (pwdText.length() > 5) {
//                        if (!WatchUtils.isEmpty(code) && VerifyUtil.checkNumber(code)) {  //验证码
//                            // 提交验证码，其中的code表示验证码，如“1357”
//                            SMSSDK.submitVerificationCode(StringUtils.substringAfter(phoneCode,"+").trim(), phoneStr, code);
//                        } else {
//                            ToastUtil.showShort(RegisterActivity2.this, getResources().getString(R.string.string_code_null));
//                        }
//                    } else {
//                        ToastUtil.showShort(RegisterActivity2.this, getResources().getString(R.string.not_b_less));
//                    }
//                } else {
//                    ToastUtil.showShort(RegisterActivity2.this, getResources().getString(R.string.string_phone_pass_null));
//                }

                break;
        }
    }


    //选择区号
    private void choosePhoneArea() {
        phoneAreaCodeView = new PhoneAreaCodeView(RegisterActivity2.this);
        phoneAreaCodeView.show();
        phoneAreaCodeView.setPhoneAreaClickListener(new PhoneAreaCodeView.PhoneAreaClickListener() {
            @Override
            public void chooseAreaCode(AreCodeBean areCodeBean) {
                phoneAreaCodeView.dismiss();
                tv_phone_head.setText("+" + areCodeBean.getPhoneCode());
            }
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
            requestPressent.getRequestJSONObject(0x02, Commont.FRIEND_BASE_URL + URLs.myHTTPs, RegisterActivity2.this, mapjson, 2);
            registerGuiderAccount();
        }

    }



    //注册账号 盖德
    private void registerRemote(String phoneTxt,String phoneCode) {
        Gson gson = new Gson();
        HashMap<String, Object> map = new HashMap<>();
        map.put("phone", phoneTxt);
        map.put("phonecode", Md5Util.Md532(phoneCode));
        String mapjson = gson.toJson(map);
        String urls = "http://api.guiderhealth.com/api/v1/login/phone";
        if(requestPressent != null){
            requestPressent.getRequestJSONObject(0x01,urls,RegisterActivity2.this,mapjson,1);

        }






//        if (!WatchUtils.isEmpty(pwdText) && !WatchUtils.isEmpty(phoneTxt)) {
//            Gson gson = new Gson();
//            HashMap<String, Object> map = new HashMap<>();
//            map.put("phone", phoneTxt);
//            map.put("pwd", Md5Util.Md532(pwdText));
//            map.put("status", "0");
//            map.put("type", "0");
//            String mapjson = gson.toJson(map);
////        Log.e("msg", "-mapjson-" + mapjson);
////            dialogSubscriber = new DialogSubscriber(subscriberOnNextListener, RegisterActivity2.this);
////            OkHttpObservable.getInstance().getData(dialogSubscriber, URLs.HTTPs + URLs.myHTTPs, mapjson);
//            if(requestPressent != null){
//                requestPressent.getRequestJSONObject(0x01,URLs.HTTPs + URLs.myHTTPs,RegisterActivity2.this,mapjson,1);
//
//            }
//
//        }

    }



    //获取手机号验证码 盖德
    private void gendPhoneCode(){
        if(!Common.isFastClick())
            return;
        if(!ConnectManages.isNetworkAvailable(RegisterActivity2.this)){
            ToastUtil.showShort(RegisterActivity2.this, getResources().getString(R.string.string_not_net));
            return;
        }

//        EventHandler eventHandler = new EventHandler() {
//            public void afterEvent(int event, int result, Object data) {
//                // afterEvent会在子线程被调用，因此如果后续有UI相关操作，需要将数据发送到UI线程
//                Message msg = new Message();
//                msg.arg1 = event;
//                msg.arg2 = result;
//                msg.obj = data;
//                new Handler(Looper.getMainLooper(), new Handler.Callback() {
//                    @Override
//                    public boolean handleMessage(Message msg) {
//                        int event = msg.arg1;
//                        int result = msg.arg2;
//                        Object data = msg.obj;
//
//                        Log.e(TAG,"-------event="+event+"---result="+result+"---data="+data.toString());
//
//
//                        if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
//                            if (result == SMSSDK.RESULT_COMPLETE) {
//                                // TODO 处理成功得到验证码的结果
//                                // 请注意，此时只是完成了发送验证码的请求，验证码短信还需要几秒钟之后才送达
////                                                Log.e("===验证码", "完成了发送验证码的请求，验证码短信还需要几秒钟之后才送达  " + data.toString());
//
//                            } else {
//                                if (!WatchUtils.isEmpty(data.toString()) && data.toString().contains("java.lang.Throwable:")) {
//                                    String res = data.toString().replace("java.lang.Throwable:", "").trim();
////                                                    Log.e("===验证码", "处理错误的结果  " + res);
//                                    if (!WatchUtils.isEmpty(res)) {
//                                        CodeBean codeBean = new Gson().fromJson(res, CodeBean.class);
//                                        if (codeBean != null) {
//                                            int status = codeBean.getStatus();
//                                            if (status == 603) {//手机号错
//                                                ToastUtil.showLong(RegisterActivity2.this, getResources().getString(R.string.string_phone_er));
//                                            } else if (status == 468) {//验证码错
//                                                ToastUtil.showLong(RegisterActivity2.this, getResources().getString(R.string.yonghuzdffhej));
//                                            } else if (status == 457) {//手机号格式不对
//                                                username.setText("");
//                                                sendBtn.setText(getResources().getString(R.string.resend));
//                                                sendBtn.setClickable(true);
//                                                ToastUtil.showShort(RegisterActivity2.this, getResources().getString(R.string.format_is_wrong));
//                                            } else {
//                                                ToastUtil.showLong(RegisterActivity2.this, codeBean.getError());
//                                            }
//                                        }
//                                    }
//                                }
//                                // TODO 处理错误的结果
//                                ((Throwable) data).printStackTrace();
//                            }
//                        } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
//                            if (result == SMSSDK.RESULT_COMPLETE) {
//                                // TODO 处理验证码验证通过的结果
//                                Log.e("===验证码", "处理验证码验证通过的结果  " + data.toString());
//                                registerRemote();
//                                //注册盖德的账号
//                                registerGuiderAccount();
//
//                            } else {
//                                if (!WatchUtils.isEmpty(data.toString()) && data.toString().contains("java.lang.Throwable:")) {
//                                    String res = data.toString().replace("java.lang.Throwable:", "").trim();
////                                                    Log.e("===验证码", "处理错误的结果2  " + res);
//                                    if (!WatchUtils.isEmpty(res)) {
//                                        CodeBean codeBean = new Gson().fromJson(res, CodeBean.class);
//                                        if (codeBean != null) {
//                                            int status = codeBean.getStatus();
////                                                            Log.e("===验证码", "处理错误的结果2  " + status);
//                                            if (status == 468) {//验证码错
//                                                ToastUtil.showLong(RegisterActivity2.this, getResources().getString(R.string.yonghuzdffhej));
//                                            } else if (status == 457) {//手机号格式不对
//                                                username.setText("");
//                                                sendBtn.setText(getResources().getString(R.string.resend));
//                                                sendBtn.setClickable(true);
//                                                ToastUtil.showShort(RegisterActivity2.this, getResources().getString(R.string.format_is_wrong));
//                                            } else {
//                                                ToastUtil.showLong(RegisterActivity2.this, codeBean.getError());
//                                            }
//                                        }
//                                    }
//                                }
//                                // TODO 处理错误的结果
//                                ((Throwable) data).printStackTrace();
//                            }
//                        }
////                                        Log.e("===验证码", "其他接口的返回结果也类似  " + event);
//                        // TODO 其他接口的返回结果也类似，根据event判断当前数据属于哪个接口
//                        return false;
//                    }
//                }).sendMessage(msg);
//            }
//        };


        //输入的手机号码
        String phoneStr = username.getText().toString();
        //选择的区号
        String areaCodeStr = tv_phone_head.getText().toString();
        if(WatchUtils.isEmpty(phoneStr) || WatchUtils.isEmpty(areaCodeStr))
            return;
        initTime();
        snedPhoneNumToServer(phoneStr,areaCodeStr);







//        if (WatchUtils.isEmpty(phoneStr)) {
//            ToastUtil.showShort(RegisterActivity2.this, getResources().getString(R.string.format_is_wrong));
//        } else {
//            initTime();
//
//
//            String getPhoneCodeUrl = "http://api.guiderhealth.com/api/v1/phonecode?phone=";
//            requestPressent.getRequestJSONObjectGet(1002,getPhoneCodeUrl,RegisterActivity2.this,0);
//
//
//            //registerGuiderAccount();
//
////            // 注册一个事件回调，用于处理SMSSDK接口请求的结果
////            SMSSDK.registerEventHandler(eventHandler);
////            // 请求验证码，其中country表示国家代码，如“86”；phone表示手机号码，如“13800138000”
////            SMSSDK.getVerificationCode(StringUtils.substringAfter(areaCodeStr,"+").trim(), phoneStr);
//        }
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
            requestPressent.getRequestJSONObject(0x01, Commont.FRIEND_BASE_URL + URLs.GET_PHONE_VERCODE_URL, RegisterActivity2.this, mapjson, 1);
        }
    }








    //注册盖德的账号
    private void registerGuiderAccount() {
        String phoneStr = username.getText().toString();
        if(requestPressent != null && WatchUtils.isEmpty(phoneStr)){
            String loginUrl = "http://api.guiderhealth.com/api/v1/login/onlyphone?phone=" + phoneStr;
            requestPressent.getRequestJSONObject(1001,loginUrl,RegisterActivity2.this,1);
            //http://api.guiderhealth.com/api/v1/phonecode?phone=
        }

    }


    @Override
    public void showLoadDialog(int what) {

    }

    @Override
    public void successData(int what, Object object, int daystag) {
        Log.e(TAG,"----------what="+what+"--=obj="+object.toString());
        if(object == null)
            return;
        if (object.toString().contains("<html>"))
            return;
        try {
            JSONObject jsonObject = new JSONObject(object.toString());
            switch (what) {
                case 0x01:  //获取验证码返回
                    ToastUtil.showToast(RegisterActivity2.this, jsonObject.getString("data")+jsonObject.getString("msg"));
                    break;
                case 0x02:  //注册返回
                    analysisRegiData(jsonObject);
                    break;
                case 1001:  //注册盖德返回
                    if(WatchUtils.isNetRequestSuccess(object.toString(),0)){
                    try {
                        if(jsonObject.has("data")){
                            JSONObject dataJsonObject = jsonObject.getJSONObject("data");
                            long accountId = dataJsonObject.getLong("accountId");
                            SharedPreferencesUtils.setParam(MyApp.getInstance(), "accountIdGD", accountId);
                        }

                    }catch (Exception e){
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
        ToastUtil.showToast(RegisterActivity2.this,e.getMessage()+"");
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
                UserInfoBean userInfoBean = new Gson().fromJson(data,UserInfoBean.class);
                if(userInfoBean != null){
                    Common.customer_id = userInfoBean.getUserid();
                    SharedPreferencesUtils.saveObject(RegisterActivity2.this, Commont.USER_ID_DATA, userInfoBean.getUserid());
                    startActivity(new Intent(RegisterActivity2.this, PersonDataActivity.class));
                    finish();
                }
            } else {
                ToastUtil.showToast(RegisterActivity2.this, jsonObject.getString("msg") + jsonObject.getString("data"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }








    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(requestPressent != null)
            requestPressent.detach();
    }


    private void analysisRegiInfo(String str){
        try {
            JSONObject jsonObject = new JSONObject(str.toString());
            if(!jsonObject.has("resultCode"))
                ToastUtil.showToast(RegisterActivity2.this,str);
            if(jsonObject.getString("resultCode").equals("001")){
                BlueUser userInfo = new Gson().fromJson(jsonObject.getString("userInfo"), BlueUser.class);
                Common.userInfo = userInfo;
                Common.customer_id = userInfo.getUserId();

                SharedPreferencesUtils.setParam(RegisterActivity2.this, SharedPreferencesUtils.CUSTOMER_ID, Common.customer_id);
                startActivity(new Intent(RegisterActivity2.this, PersonDataActivity.class));
                finish();
            }else{
                WatchUtils.verServerCode(RegisterActivity2.this,jsonObject.getString("resultCode"));
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }



}
