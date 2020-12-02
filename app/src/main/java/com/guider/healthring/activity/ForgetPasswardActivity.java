package com.guider.healthring.activity;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.guider.health.apilib.ApiCallBack;
import com.guider.health.apilib.ApiUtil;
import com.guider.health.apilib.IGuiderApi;
import com.guider.health.apilib.model.ChangeWithBanPasswordBean;
import com.guider.healthring.R;
import com.guider.healthring.b30.bean.CodeBean;
import com.guider.healthring.base.BaseActivity;
import com.guider.healthring.bean.AreCodeBean;
import com.guider.healthring.siswatch.utils.WatchUtils;
import com.guider.healthring.util.Md5Util;
import com.guider.healthring.util.ToastUtil;
import com.guider.healthring.view.PhoneAreaCodeView;
import com.guider.healthring.w30s.utils.httputils.RequestPressent;
import com.guider.healthring.w30s.utils.httputils.RequestView;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.smssdk.SMSSDK;
import retrofit2.Call;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Created by admin on 2017/4/21.
 * 忘记密码
 */

public class ForgetPasswardActivity extends BaseActivity
        implements RequestView, View.OnClickListener {

    TextView tvTitle;
    //区号
    TextView tv_phone_head;
    ListView lv_forget;
    EditText username;//用户名
    EditText password;//密码
    Button sendBtn;//发送按钮
    EditText codeEdit;//验证码
    TextInputLayout textInputLayoutname;
    TextView forget_pwd_user_text;
    TextView forget_pwd_email_text;
    View forget_pwd_user_line;
    View forget_pwd_email_line;

    private Subscriber subscriber;

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

    //选择区号
    private PhoneAreaCodeView phoneAreaCodeView;
    RequestPressent requestPressent;


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
        initViewIds();
        tvTitle.setText(R.string.forget_password);
        initData();

        requestPressent = new RequestPressent();
        requestPressent.attach(this);

    }

    private void initViewIds() {
        tvTitle = findViewById(R.id.tv_title);
        tv_phone_head = findViewById(R.id.tv_phone_head);
        lv_forget = findViewById(R.id.lv_forget);
        username = findViewById(R.id.username_forget);
        password = findViewById(R.id.password_forget);
        sendBtn = findViewById(R.id.send_btn_forget);
        codeEdit = findViewById(R.id.code_et_forget);
        textInputLayoutname = findViewById(R.id.username_input_forget);
        forget_pwd_user_text = findViewById(R.id.forget_pwd_user_text);
        forget_pwd_email_text = findViewById(R.id.forget_pwd_email_text);
        forget_pwd_user_line = findViewById(R.id.forget_pwd_user_line);
        forget_pwd_email_line = findViewById(R.id.forget_pwd_email_line);
        findViewById(R.id.login_btn__forget).setOnClickListener(this);
        sendBtn.setOnClickListener(this);
        findViewById(R.id.forget_pwd_user).setOnClickListener(this);
        findViewById(R.id.forget_pwd_email).setOnClickListener(this);
        tv_phone_head.setOnClickListener(this);
    }

    private void initData() {
        colorBlue = ContextCompat.getColor(this, R.color.new_colorAccent);
        colorBlack = ContextCompat.getColor(this, R.color.black_9);
        leftDrawable = ContextCompat.getDrawable(this, R.mipmap.yonghuming_dianji);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send_btn_forget:  //获取验证码
                String phoneStr = username.getText().toString();
                if (WatchUtils.isEmpty(phoneStr))
                    return;
                getPhoneVerCode(phoneStr);
                break;
            case R.id.login_btn__forget:    //提交
                String phoneStr2 = username.getText().toString();    //手机号
                String pwdStr = password.getText().toString();  //密码
                String verCodeStr = codeEdit.getText().toString();   //验证码
                if (WatchUtils.isEmpty(phoneStr2) || WatchUtils.isEmpty(pwdStr) || WatchUtils.isEmpty(verCodeStr))
                    return;
                if (pwdStr.length() < 6) {
                    ToastUtil.showToast(ForgetPasswardActivity.this, getResources().getString(R.string.not_b_less));
                    return;
                }
                submitUpdatePwd(phoneStr2, pwdStr, verCodeStr);

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

    //提交
    private void submitUpdatePwd(String phoneStr2, String pwdStr, String verCodeStr) {
//        //提交
//        if (requestPressent != null) {
//            String subUrl = Commont.FRIEND_BASE_URL + URLs.SUB_GET_BACK_PWD_URL;
//            Map<String, String> maps = new HashMap<>();
//            maps.put("phone", phoneStr2);
//            maps.put("code", verCodeStr);
//            maps.put("pwd", Md5Util.Md532(pwdStr));
//            requestPressent.getRequestJSONObject(0x02, subUrl,
//                    ForgetPasswardActivity.this, new Gson().toJson(maps), 2);
//        }
        showLoadingDialog("");
        String pCode = tv_phone_head.getText().toString();
        String replace = pCode.replace("+", "");
        ApiUtil.createApi(IGuiderApi.class, false)
                .verifyLineCode(replace, phoneStr2, verCodeStr)
                .enqueue(new ApiCallBack<String>() {
                    @Override
                    public void onApiResponse(Call<String> call, Response<String> response) {
                        if (response.body() != null && response.body().equals("true")) {
                            forgotPassword(phoneStr2, replace, pwdStr);
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        super.onFailure(call, t);
                        closeLoadingDialog();
                    }
                });
    }

    private void forgotPassword(String phoneStr2, String countryCode, String pwdStr) {
        String pass = Md5Util.Md532(pwdStr);
        ApiUtil.createApi(IGuiderApi.class, false)
                .changeWithBackupPassword(countryCode, phoneStr2, pass, "")
                .enqueue(new ApiCallBack<ChangeWithBanPasswordBean>() {
                    @Override
                    public void onApiResponse(Call<ChangeWithBanPasswordBean> call,
                                              Response<ChangeWithBanPasswordBean> response) {
                        if (response.body() != null) {
                            ToastUtil.showToast(ForgetPasswardActivity.this,
                                    ForgetPasswardActivity.this.getResources()
                                            .getString(R.string.change_password));
                            finish();
                        }
                    }

                    @Override
                    public void onRequestFinish() {
                        super.onRequestFinish();
                        closeLoadingDialog();
                    }
                });
    }


    //获取验证码
    private void getPhoneVerCode(String phoneStr) {
//        String pCode = tv_phone_head.getText().toString();
//        if(requestPressent != null){
//            String url = Commont.FRIEND_BASE_URL + URLs.GET_BACK_PWD_PHOE_CODE_URL;
//            Map<String,String> mps = new HashMap<>();
//            mps.put("phone",WatchUtils.removeStr(phoneStr));
//            mps.put("code",StringUtils.substringAfter(pCode,"+"));
//            requestPressent.getRequestJSONObject(0x01,url,ForgetPasswardActivity.this,new Gson().toJson(mps),1);
//        }
        showLoadingDialog("");
        ApiUtil.createApi(IGuiderApi.class, false)
                .sendLineCode(phoneStr)
                .enqueue(new ApiCallBack<String>() {
                    @Override
                    public void onApiResponse(Call<String> call, Response<String> response) {
                        if (response.body() != null && response.body().equals("true")) {
                            initTime();
                        }
                    }

                    @Override
                    public void onRequestFinish() {
                        super.onRequestFinish();
                        closeLoadingDialog();
                    }
                });
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

//    private void registerRemote() {
//        Gson gson = new Gson();
//        HashMap<String, Object> map = new HashMap<>();
//        map.put("phone", phoneTxt);
//        map.put("pwd", Md5Util.Md532(pwdText));
//        map.put("code", code);
//        String mapjson = gson.toJson(map);
//        Log.i("msg", "-mapjson-" + mapjson);
//        dialogSubscriber = new DialogSubscriber(subscriberOnNextListener, ForgetPasswardActivity.this);
//        OkHttpObservable.getInstance().getData(dialogSubscriber, URLs.HTTPs + URLs.xiugaimima, mapjson);
////        OkHttpObservable.getInstance().getData(dialogSubscriber, URLs.HTTPs + URLs.SUB_GET_BACK_PWD_URL, mapjson);
//        /*Intent intent = new Intent(ForgetPasswardActivity.this, MainActivity.class);
//        startActivity(intent);*/
//    }

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
                    //registerRemote();
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
                        ToastUtil.showLong(ForgetPasswardActivity.this, getResources().getString(R.string.yonghuzdffhej));
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
        if (requestPressent != null)
            requestPressent.detach();
    }

    @Override
    public void showLoadDialog(int what) {

    }

    @Override
    public void successData(int what, Object object, int daystag) {
        if (object == null)
            return;
        try {
            JSONObject jsonObject = new JSONObject(object.toString());
            int code = jsonObject.getInt("code");
            if (what == 0x01) {   //获取手机号验证码
                ToastUtil.showToast(ForgetPasswardActivity.this, jsonObject.getString("data"));
            }
            if (what == 0x02) {
                if (code == 200) {   //提交成功
                    ToastUtil.showToast(ForgetPasswardActivity.this, getResources().getString(R.string.change_password));
                    finish();
                } else {
                    ToastUtil.showToast(ForgetPasswardActivity.this, jsonObject.getString("msg"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void failedData(int what, Throwable e) {

    }

    @Override
    public void closeLoadDialog(int what) {

    }
}
