package com.guider.healthring.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.guider.healthring.MyApp;
import com.guider.healthring.R;
import com.guider.healthring.activity.wylactivity.wyl_util.service.ConnectManages;
import com.guider.healthring.adpter.PhoneAdapter;
import com.guider.healthring.b30.bean.CodeBean;
import com.guider.healthring.base.BaseActivity;
import com.guider.healthring.bean.AreCodeBean;
import com.guider.healthring.net.OkHttpObservable;
import com.guider.healthring.rxandroid.DialogSubscriber;
import com.guider.healthring.rxandroid.SubscriberOnNextListener;
import com.guider.healthring.siswatch.utils.WatchUtils;
import com.guider.healthring.util.Md5Util;
import com.guider.healthring.util.TimerCount;
import com.guider.healthring.util.ToastUtil;
import com.guider.healthring.util.URLs;
import com.guider.healthring.util.VerifyUtil;
import com.google.gson.Gson;
import com.guider.healthring.view.PhoneAreaCodeView;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Created by admin on 2017/4/21.
 * 忘记密码
 */

public class ForgetPasswardActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_phone_head)
    TextView tv_phone_head;
    @BindView(R.id.lv_forget)
    ListView lv_forget;
    @BindView(R.id.username_forget)
    EditText username;//用户名
    @BindView(R.id.password_forget)
    EditText password;//密码
    @BindView(R.id.send_btn_forget)
    Button sendBtn;//发送按钮
    @BindView(R.id.code_et_forget)
    EditText yuanzhengma;//验证码
    @BindView(R.id.username_input_forget)
    TextInputLayout textInputLayoutname;
    @BindView(R.id.forget_pwd_user_text)
    TextView forget_pwd_user_text;
    @BindView(R.id.forget_pwd_email_text)
    TextView forget_pwd_email_text;
    @BindView(R.id.forget_pwd_user_line)
    View forget_pwd_user_line;
    @BindView(R.id.forget_pwd_email_line)
    View forget_pwd_email_line;

    private JSONObject jsonObject;
    private DialogSubscriber dialogSubscriber;
    private Subscriber subscriber;
    private SubscriberOnNextListener<String> subscriberOnNextListener;
    private String phoneTxt, pwdText;
    private String code;//验证码
    /**
     * true_邮箱找回 false_手机找回
     */
    private boolean isEmail;
    /**
     * 蓝和黑
     */
    private int colorBlue, colorBlack;
    /**
     * 用户名左边图片(邮箱用)
     */
    private Drawable leftDrawable;

    private List<Integer> phoneHeadList;
    private PhoneAdapter phoneAdapter;


    private PhoneAreaCodeView phoneAreaCodeView;



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 过滤按键动作
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            //moveTaskToBack(true);
            finish();

        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            moveTaskToBack(true);
        } else if (keyCode == KeyEvent.KEYCODE_HOME) {
            moveTaskToBack(true);
        }

        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void initViews() {
        tvTitle.setText(R.string.forget_password);
        initData();
//        SMSSDK.initSDK(this, "169d6eaccd2ac", "753aa57cf4a85122671fcc7d4c379ac5");
//        SMSSDK.initSDK(this, "27d747209c6db", "716ae323ee316f142777ebc73f89c90f");
//        boolean lauage = VerifyUtil.isZh(ForgetPasswardActivity.this);
//        if (lauage) {
//            textInputLayoutname.setHint(getResources().getString(R.string.input_name));
//        } else {
//            textInputLayoutname.setHint(getResources().getString(R.string.input_email));
//        }

        subscriberOnNextListener = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {
                //Loaddialog.getInstance().dissLoading();
                Gson gson = new Gson();
                try {
                    if (WatchUtils.isEmpty(result)) return;
                    Log.e("======", result);
                    JSONObject jsonObject = new JSONObject(result);
                    String loginResult = jsonObject.getString("resultCode");
                    if ("001".equals(loginResult)) {
                        Toast.makeText(ForgetPasswardActivity.this, getResources().getString(R.string.change_password), Toast.LENGTH_SHORT).show();
                  /*      BlueUser userInfo = gson.fromJson(jsonObject.getString("userInfo").toString(), BlueUser.class);
                        MyLogUtil.i("msg", "-userInfo-" + userInfo.toString());
                        B18iCommon.userInfo = userInfo;
                        B18iCommon.customer_id = userInfo.getUserId();
                        MobclickAgent.onProfileSignIn(B18iCommon.customer_id);
                        String pass = password.getText().toString();
                        String usernametxt = username.getText().toString();
                        userInfo.setPassword(Md5Util.Md532(pass));


                        MyApp.getApplication().getDaoSession().getBlueUserDao().insertOrReplace(userInfo);
                        SharedPreferencesUtils.setParam(ForgetPasswardActivity.this, SharedPreferencesUtils.CUSTOMER_ID, B18iCommon.customer_id);
                        SharedPreferencesUtils.setParam(ForgetPasswardActivity.this, SharedPreferencesUtils.CUSTOMER_PASSWORD, pass);*/
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private void initData() {
        colorBlue = ContextCompat.getColor(this, R.color.new_colorAccent);
        colorBlack = ContextCompat.getColor(this, R.color.black_9);
        leftDrawable = getResources().getDrawable(R.mipmap.yonghuming_dianji);
//        phoneHeadList = new ArrayList<>();
//        phoneHeadList.add(86);
//        phoneHeadList.add(852);
//        phoneHeadList.add(853);
//        phoneHeadList.add(886);
//        phoneHeadList.add(81);
//        phoneHeadList.add(82);
//        phoneHeadList.add(84);
//        phoneHeadList.add(49);
//        phoneHeadList.add(39);
//        phoneHeadList.add(7);
//        phoneHeadList.add(34);
//        phoneHeadList.add(351);

        //phoneAdapter = new PhoneAdapter(phoneHeadList, this);

//        lv_forget.setAdapter(phoneAdapter);
//
//        lv_forget.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                tv_phone_head.setText("+" + phoneHeadList.get(position));
//                lv_forget.setVisibility(View.GONE);
//            }
//        });
    }

    @OnClick({R.id.login_btn__forget, R.id.send_btn_forget,
            R.id.forget_pwd_user, R.id.forget_pwd_email
            , R.id.tv_phone_head})
    public void onClick(View view) {
        phoneTxt = username.getText().toString();
        switch (view.getId()) {
            case R.id.send_btn_forget:
                try {
                    //判断当前网络
                    boolean isbb = ConnectManages.isNetworkAvailable(ForgetPasswardActivity.this);
                    if (isbb) {
//                        boolean lauages = VerifyUtil.isZh(ForgetPasswardActivity.this);
                        if (!isEmail) {
                            if (TextUtils.isEmpty(phoneTxt)) {
                                ToastUtil.showShort(ForgetPasswardActivity.this, getResources().getString(R.string.format_is_wrong));
                            } else {
                                initTime();
                                String phoneCodeStr = tv_phone_head.getText().toString();
                                SMSSDK.getVerificationCode(StringUtils.substringAfter(phoneCodeStr,"+").trim(), phoneTxt);
                            }
                            EventHandler eventHandler = new EventHandler() {
                                @Override
                                public void afterEvent(int event, int result, Object data) {
                                    Message msg = new Message();
                                    msg.arg1 = event;
                                    msg.arg2 = result;
                                    msg.obj = data;
                                    handler.sendMessage(msg);
                                }
                            };
                            SMSSDK.registerEventHandler(eventHandler);

                        } else {
                            //邮箱
                            if (!TextUtils.isEmpty(username.getText().toString())) {
                                if (isEmail(username.getText().toString())) {
                                    sendBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.sms_verification));
                                    sendBtn.setTextColor(Color.parseColor("#2b2b2b"));
                                    TimerCount timer = new TimerCount(60000, 1000, sendBtn);
                                    timer.start();
                                    //判断网络是否连接
                                    boolean is = ConnectManages.isNetworkAvailable(ForgetPasswardActivity.this);
                                    if (is) {
                                        //  RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                                        try {
                                            jsonObject = new JSONObject();
                                            jsonObject.put("phone", username.getText().toString());

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        System.out.print("调用次数" + jsonObject.toString());
                                        JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Request.Method.POST, URLs.HTTPs + URLs.sendEmail, jsonObject,
                                                new Response.Listener<JSONObject>() {
                                                    @Override
                                                    public void onResponse(JSONObject response) {
//                                                        Log.d("dfg", response.toString());
                                                        if (response.optString("resultCode").equals("001")) {
                                                            code = response.optString("code");
                                                        }
                                                    }
                                                }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                ToastUtil.showShort(ForgetPasswardActivity.this, getResources().getString(R.string.wangluo));
                                            }
                                        }) {
                                            @Override
                                            public Map<String, String> getHeaders() {
                                                HashMap<String, String> headers = new HashMap<String, String>();
                                                headers.put("Accept", "application/json");
                                                headers.put("Content-Type", "application/json; charset=UTF-8");
                                                return headers;
                                            }
                                        };
                                        MyApp.getRequestQueue().add(jsonRequest);
                                    } else {
                                        ToastUtil.showShort(ForgetPasswardActivity.this, getResources().getString(R.string.wangluo));
                                    }
                                } else {
                                    ToastUtil.showShort(ForgetPasswardActivity.this, getResources().getString(R.string.tianxie));
                                }

                            } else {
                                ToastUtil.showShort(ForgetPasswardActivity.this, getResources().getString(R.string.write_nickname));
                            }
                        }
                    } else {
                        ToastUtil.showShort(ForgetPasswardActivity.this, getResources().getString(R.string.wangluo));
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case R.id.login_btn__forget:
                pwdText = password.getText().toString();
                String phoneAreStr = tv_phone_head.getText().toString().trim();
                try {
                    if (!isEmail) {
                        String yzm = yuanzhengma.getText().toString().trim();
                        if (WatchUtils.isEmpty(phoneTxt)) {
                            ToastUtil.showToast(ForgetPasswardActivity.this, getResources().getString(R.string.user_name_format));
                        } else if (TextUtils.isEmpty(pwdText) | pwdText.length() < 6) {
                            ToastUtil.showShort(ForgetPasswardActivity.this, getResources().getString(R.string.not_b_less));
                        } else if (TextUtils.isEmpty(yzm) | !VerifyUtil.checkNumber(yzm) | yzm.length() < 4) {
                            ToastUtil.showShort(ForgetPasswardActivity.this, getResources().getString(R.string.yonghuzdffhej));
                        } else {
                            SMSSDK.submitVerificationCode(StringUtils.substringAfter(phoneAreStr,"+") + "", phoneTxt, yzm);
                        }
                    }
                    //国外为邮箱
                    else {
                        if (TextUtils.isEmpty(phoneTxt) | !VerifyUtil.checkEmail(phoneTxt)) {
                            ToastUtil.showShort(this, getResources().getString(R.string.mailbox_format_error));
                        } else if (pwdText.length() < 6) {
                            ToastUtil.showShort(this, getResources().getString(R.string.not_b_less));
                        } else if (TextUtils.isEmpty(pwdText)) {
                            ToastUtil.showShort(this, getResources().getString(R.string.input_password));
                        } else if (yuanzhengma == null || TextUtils.isEmpty(yuanzhengma.getText().toString())) {
                            ToastUtil.showShort(this, getResources().getString(R.string.input_code));
                        } else if (yuanzhengma == null || !code.equals(yuanzhengma.getText().toString())) {
                            ToastUtil.showShort(this, getResources().getString(R.string.yonghuzdffhej));
                        } else {
                            registerRemote();
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tv_phone_head:    //选择区号
                choosePhoneCode();
                break;
            case R.id.forget_pwd_user:
                changeModel(false);
                break;
            case R.id.forget_pwd_email:
                changeModel(true);
                break;
        }
    }

    //选择区号
    private void choosePhoneCode() {
        phoneAreaCodeView = new PhoneAreaCodeView(ForgetPasswardActivity.this);
        phoneAreaCodeView.show();
        phoneAreaCodeView.setPhoneAreaClickListener(new PhoneAreaCodeView.PhoneAreaClickListener() {
            @Override
            public void chooseAreaCode(AreCodeBean areCodeBean) {
                phoneAreaCodeView.dismiss();
                tv_phone_head.setText("+" + areCodeBean.getPhoneCode());
            }
        });
    }

    /**
     * 切换找回方式
     *
     * @param email true_邮箱找回 false_手机找回
     */
    private void changeModel(boolean email) {
        if (email == isEmail) return;
        forget_pwd_user_text.setTextColor(email ? colorBlack : colorBlue);
        forget_pwd_email_text.setTextColor(email ? colorBlue : colorBlack);
        forget_pwd_user_line.setVisibility(email ? View.GONE : View.VISIBLE);
        forget_pwd_email_line.setVisibility(email ? View.VISIBLE : View.GONE);
        tv_phone_head.setVisibility(email ? View.GONE : View.VISIBLE);
        username.setCompoundDrawablesWithIntrinsicBounds(email ? leftDrawable : null, null, null, null);
        if (email) {
            textInputLayoutname.setHint(getResources().getString(R.string.input_email));
            lv_forget.setVisibility(View.GONE);
        } else {
            textInputLayoutname.setHint(getResources().getString(R.string.input_name));
        }
        isEmail = email;
    }


    /*
     * 是否email
     */
    public static boolean isEmail(String strEmail) {
        String strPattern = "^[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(strEmail);
        return m.matches();
    }

    private void registerRemote() {
        Gson gson = new Gson();
        HashMap<String, Object> map = new HashMap<>();
        map.put("phone", phoneTxt);
        map.put("pwd", Md5Util.Md532(pwdText));
        map.put("code", code);
        String mapjson = gson.toJson(map);
        Log.i("msg", "-mapjson-" + mapjson);
        dialogSubscriber = new DialogSubscriber(subscriberOnNextListener, ForgetPasswardActivity.this);
        OkHttpObservable.getInstance().getData(dialogSubscriber, URLs.HTTPs + URLs.xiugaimima, mapjson);
//        OkHttpObservable.getInstance().getData(dialogSubscriber, URLs.HTTPs + URLs.SUB_GET_BACK_PWD_URL, mapjson);
        /*Intent intent = new Intent(ForgetPasswardActivity.this, MainActivity.class);
        startActivity(intent);*/
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

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;
            if (result == SMSSDK.RESULT_COMPLETE) {
                // 短信注册成功后，返回MainActivity,然后提示新好友
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
                    registerRemote();
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    Toast.makeText(getApplicationContext(), R.string.yanzhengma,
                            Toast.LENGTH_SHORT).show();
                } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {// 返回支持发送验证码的国家列表
                    Toast.makeText(getApplicationContext(), R.string.guojia,
                            Toast.LENGTH_SHORT).show();
                }
            } else {
//                com.alibaba.fastjson.JSONObject jsStr =  com.alibaba.fastjson.JSONObject.parseObject();
                String s = data.toString().trim().substring(20, data.toString().trim().length());
                if (TextUtils.isEmpty(s)) return;
                CodeBean codeBean = new Gson().fromJson(s, CodeBean.class);
                if (codeBean != null) {
                    int status = codeBean.getStatus();
                    if (status == 603) {//手机号错
                        ToastUtil.showLong(ForgetPasswardActivity.this, getResources().getString(R.string.string_phone_er));
                    } else if (status == 468) {//验证码错
                        ToastUtil.showLong(ForgetPasswardActivity.this, getResources().getString(R.string.yonghuzdffhej));
                    } else {
                        ToastUtil.showLong(ForgetPasswardActivity.this, data.toString());
                    }
                }


            }
        }
    };


    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mymobiles) {
    /*
    移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
    联通：130、131、132、152、155、156、185、186
    电信：133、153、180、189、（1349卫通）
    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
    */
        String telRegex = "[1][358]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mymobiles)) return false;
        else return mymobiles.matches(telRegex);
    }


    @Override
    protected int getContentViewId() {
        return R.layout.activity_forgetpassward;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
