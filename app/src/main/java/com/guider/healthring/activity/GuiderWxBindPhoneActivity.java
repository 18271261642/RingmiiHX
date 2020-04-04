package com.guider.healthring.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.guider.healthring.MyApp;
import com.guider.healthring.R;
import com.guider.healthring.b30.bean.CodeBean;
import com.guider.healthring.bean.AreCodeBean;
import com.guider.healthring.siswatch.NewSearchActivity;
import com.guider.healthring.siswatch.WatchBaseActivity;
import com.guider.healthring.siswatch.utils.WatchUtils;
import com.guider.healthring.util.SharedPreferencesUtils;
import com.guider.healthring.util.ToastUtil;
import com.guider.healthring.util.WxScanUtil;
import com.guider.healthring.view.PhoneAreaCodeView;
import com.guider.healthring.w30s.utils.httputils.RequestPressent;
import com.guider.healthring.w30s.utils.httputils.RequestView;
import com.guider.libbase.sms.SmsMob;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * 盖德微信绑定手机号，获取手机号验证码
 * Created by Admin
 * Date 2019/9/24
 */
public class GuiderWxBindPhoneActivity extends WatchBaseActivity implements RequestView {

    private static final String TAG = "GuiderWxBindPhoneActivi";

    @BindView(R.id.wxBindPhoneVerCodeEdit)
    EditText wxBindPhoneVerCodeEdit;
    @BindView(R.id.wxBindGetVerCodeBtn)
    Button wxBindGetVerCodeBtn;



    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.wxBindPhoneCodeTv)
    TextView wxBindPhoneCodeTv;
    @BindView(R.id.wxBindPhoneCodeEdit)
    EditText wxBindPhoneCodeEdit;
    @BindView(R.id.wxBindSubBtn)
    Button wxBindSubBtn;

    //倒计时
    MyCountDownTimerUtils countTimeUtils;


    private PhoneAreaCodeView phoneAreaCodeView;
    private RequestPressent requestPressent;

    private String wxStr = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guider_bind_wx_layout);
        ButterKnife.bind(this);


        initViews();

        wxStr = getIntent().getStringExtra("wxStr");

        initData();


    }

    private void initData() {
        requestPressent = new RequestPressent();
        requestPressent.attach(this);
    }

    private void initViews() {
        commentB30TitleTv.setText(getResources().getString(R.string.bind_phone));
        countTimeUtils = new MyCountDownTimerUtils(60 * 1000, 1000);

    }

    @OnClick({R.id.wxBindPhoneCodeTv, R.id.wxBindGetVerCodeBtn, R.id.wxBindSubBtn,})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.wxBindPhoneCodeTv:    //选择区号
                choosePhoneAreCode();
                break;
            case R.id.wxBindGetVerCodeBtn:  //获取验证码
                //手机号
                String phoeCode = wxBindPhoneCodeEdit.getText().toString();
                if (WatchUtils.isEmpty(phoeCode)) {
                    ToastUtil.showToast(this, getResources().getString(R.string.input_name));
                    return;
                }
                getPhoneVerCode(phoeCode);
                break;
            case R.id.wxBindSubBtn:     //提交
                //手机号
                String phoeCodes = wxBindPhoneCodeEdit.getText().toString();
                //验证码
                String phoeVerCode = wxBindPhoneVerCodeEdit.getText().toString();
                if (WatchUtils.isEmpty(phoeCodes)) {
                    ToastUtil.showToast(this, getResources().getString(R.string.input_name));
                    return;
                }
                if (WatchUtils.isEmpty(phoeVerCode)) {
                    ToastUtil.showToast(this, getResources().getString(R.string.input_code));
                    return;
                }

                subWxBindData(phoeCodes, phoeVerCode);
                break;
        }
    }


    //提交绑定信息
    private void subWxBindData(String phoeCodes, String phoeVerCode) {
        // 提交验证码，其中的code表示验证码，如“1357”
        SMSSDK.submitVerificationCode(StringUtils.substringAfter(wxBindPhoneCodeTv.getText().toString(),"+").trim(), phoeCodes, phoeVerCode);

    }

    //获取验证码
    private void getPhoneVerCode(final String phoeCode) {
        //requestPressent.getRequestJSONObjectGet(1001, GET_PHONE_CODE + phoeCode, this, 1);
        //区号
        String areaCode = wxBindPhoneCodeTv.getText().toString();
        // 注册一个事件回调，用于处理SMSSDK接口请求的结果
        SMSSDK.registerEventHandler(eventHandler);
        // 请求验证码，其中country表示国家代码，如“86”；phone表示手机号码，如“13800138000”
        // SMSSDK.getVerificationCode(StringUtils.substringAfter(areaCode,"+").trim(), phoeCode);
        SMSSDK.getVerificationCode(areaCode, phoeCode, SmsMob.getTempCode(areaCode), null);
        if (countTimeUtils == null)
            countTimeUtils = new MyCountDownTimerUtils(60 * 1000, 1000);
        countTimeUtils.start();

    }


    EventHandler eventHandler = new EventHandler() {
        public void afterEvent(int event, int result, Object data) {
            // afterEvent会在子线程被调用，因此如果后续有UI相关操作，需要将数据发送到UI线程
            Message msg = new Message();
            msg.arg1 = event;
            msg.arg2 = result;
            msg.obj = data;
            new Handler(Looper.getMainLooper(), new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    int event = msg.arg1;
                    int result = msg.arg2;
                    Object data = msg.obj;

                    Log.e(TAG,"-------event="+event+"---result="+result+"---data="+data.toString());


                    if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            // TODO 处理成功得到验证码的结果
                            // 请注意，此时只是完成了发送验证码的请求，验证码短信还需要几秒钟之后才送达
//                                                Log.e("===验证码", "完成了发送验证码的请求，验证码短信还需要几秒钟之后才送达  " + data.toString());

                        } else {
                            if (!WatchUtils.isEmpty(data.toString()) && data.toString().contains("java.lang.Throwable:")) {
                                String res = data.toString().replace("java.lang.Throwable:", "").trim();
//                                                    Log.e("===验证码", "处理错误的结果  " + res);
                                if (!WatchUtils.isEmpty(res)) {
                                    CodeBean codeBean = new Gson().fromJson(res, CodeBean.class);
                                    if (codeBean != null) {
                                        int status = codeBean.getStatus();
                                        if (status == 603) {//手机号错
                                            ToastUtil.showLong(GuiderWxBindPhoneActivity.this, getResources().getString(R.string.string_phone_er));
                                        } else if (status == 468) {//验证码错
                                            ToastUtil.showLong(GuiderWxBindPhoneActivity.this, getResources().getString(R.string.yonghuzdffhej));
                                        } else if (status == 457) {//手机号格式不对
                                            //username.setText("");
                                            wxBindGetVerCodeBtn.setText(getResources().getString(R.string.resend));
                                            wxBindGetVerCodeBtn.setClickable(true);
                                            ToastUtil.showShort(GuiderWxBindPhoneActivity.this, getResources().getString(R.string.format_is_wrong));
                                        } else {
                                            ToastUtil.showLong(GuiderWxBindPhoneActivity.this, codeBean.getError());
                                        }
                                    }
                                }
                            }
                            // TODO 处理错误的结果
                            ((Throwable) data).printStackTrace();
                        }
                    } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            // TODO 处理验证码验证通过的结果
                            Log.e("===验证码", "处理验证码验证通过的结果  " + data.toString());
                            //手机号
                            String phoeCodes = wxBindPhoneCodeEdit.getText().toString();

                            String bindUrl = "http://api.guiderhealth.com/api/v2/wechat/bind/phone/token?phone=" + phoeCodes + "&code=" ;
                            WxBindStr wxBindStr = new Gson().fromJson(wxStr, WxBindStr.class);
                            //Log.e(TAG, "--------wx=" + wxBindStr.toString());
                            requestPressent.getRequestJSONObject(1002, bindUrl, GuiderWxBindPhoneActivity.this, new Gson().toJson(wxBindStr), 2);

                        } else {
                            if (!WatchUtils.isEmpty(data.toString()) && data.toString().contains("java.lang.Throwable:")) {
                                String res = data.toString().replace("java.lang.Throwable:", "").trim();
//                                                    Log.e("===验证码", "处理错误的结果2  " + res);
                                if (!WatchUtils.isEmpty(res)) {
                                    CodeBean codeBean = new Gson().fromJson(res, CodeBean.class);
                                    if (codeBean != null) {
                                        int status = codeBean.getStatus();
//                                                            Log.e("===验证码", "处理错误的结果2  " + status);
                                        if (status == 468) {//验证码错
                                            ToastUtil.showLong(GuiderWxBindPhoneActivity.this, getResources().getString(R.string.yonghuzdffhej));
                                        } else if (status == 457) {//手机号格式不对
                                            // username.setText("");
                                            wxBindGetVerCodeBtn.setText(getResources().getString(R.string.resend));
                                            wxBindGetVerCodeBtn.setClickable(true);
                                            ToastUtil.showShort(GuiderWxBindPhoneActivity.this, getResources().getString(R.string.format_is_wrong));
                                        } else {
                                            ToastUtil.showLong(GuiderWxBindPhoneActivity.this, codeBean.getError());
                                        }
                                    }
                                }
                            }
                            // TODO 处理错误的结果
                            ((Throwable) data).printStackTrace();
                        }
                    }
//                                        Log.e("===验证码", "其他接口的返回结果也类似  " + event);
                    // TODO 其他接口的返回结果也类似，根据event判断当前数据属于哪个接口
                    return false;
                }
            }).sendMessage(msg);
        }
    };
















    //选择区号
    private void choosePhoneAreCode() {
        if (phoneAreaCodeView == null)
            phoneAreaCodeView = new PhoneAreaCodeView(GuiderWxBindPhoneActivity.this);
        phoneAreaCodeView.show();
        phoneAreaCodeView.setPhoneAreaClickListener(new PhoneAreaCodeView.PhoneAreaClickListener() {
            @Override
            public void chooseAreaCode(AreCodeBean areCodeBean) {
                wxBindPhoneCodeTv.setText("+" + areCodeBean.getPhoneCode());
                phoneAreaCodeView.dismiss();
            }
        });
    }


    @Override
    public void showLoadDialog(int what) {
        showLoadingDialog("Loading...");
    }

    @Override
    public void successData(int what, Object object, int daystag) {
        closeLoadingDialog();
        if (object == null)
            return;
        Log.e(TAG, "------what=" + what + "--obj=" + object.toString());
        switch (what) {
            case 1001:  //获取验证码
                analysiPoneVerCode(object.toString());
                break;
            case 1002:  //绑定返回
                analysisWbBindData(object.toString());
                break;

        }
    }

    private void analysiPoneVerCode(String data) {
        try {
           JSONObject jsonObject = new JSONObject(data);
           if(jsonObject.getInt("code") != 0){
               ToastUtil.showToast(this,jsonObject.getString("msg"));
           }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void analysisWbBindData(String str) {
            try {
                JSONObject jsonObject = new JSONObject(str);
                if(jsonObject.getInt("code") != 0){
                    ToastUtil.showToast(GuiderWxBindPhoneActivity.this,jsonObject.getString("msg"));
                    return;
                }
                JSONObject dataJson = jsonObject.getJSONObject("data");
                long accountId = dataJson.getLong("accountId");
                SharedPreferencesUtils.setParam(this, "accountIdGD", accountId);
                String token = dataJson.getString("token");
                SharedPreferencesUtils.setParam(this, "tokenGD", accountId);
                ToastUtil.showToast(this, getResources().getString(R.string.bind_phone_success));


                WxScanUtil.handle(GuiderWxBindPhoneActivity.this, accountId, new WxScanUtil.IWxScan() {
                    @Override
                    public void onError() {
                        startActivity(NewSearchActivity.class);
                        finish();
                    }
                    @Override
                    public void onOk() {
                        Intent intent = new Intent(GuiderWxBindPhoneActivity.this, WxScanActivity.class);
                        intent.putExtra("accountId", accountId);
                        startActivity(intent);
                        finish();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    @Override
    public void failedData(int what, Throwable e) {
        closeLoadingDialog();
        ToastUtil.showToast(this, e.getMessage());
    }

    @Override
    public void closeLoadDialog(int what) {
        closeLoadingDialog();
    }


    class WxBindStr {
        private String openId;
        private String unionId;
        private String headUrl;
        private int sex;
        private String nickname;

        public String getOpenId() {
            return openId;
        }

        public void setOpenId(String openId) {
            this.openId = openId;
        }

        public String getUnionId() {
            return unionId;
        }

        public void setUnionId(String unionId) {
            this.unionId = unionId;
        }

        public String getHeadUrl() {
            return headUrl;
        }

        public void setHeadUrl(String headUrl) {
            this.headUrl = headUrl;
        }

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        @Override
        public String toString() {
            return "WxBindStr{" +
                    "openId='" + openId + '\'' +
                    ", unionId='" + unionId + '\'' +
                    ", headUrl='" + headUrl + '\'' +
                    ", sex=" + sex +
                    ", nickname='" + nickname + '\'' +
                    '}';
        }
    }


    private class MyCountDownTimerUtils extends CountDownTimer {


        MyCountDownTimerUtils(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            wxBindGetVerCodeBtn.setClickable(false);
            wxBindGetVerCodeBtn.setText(millisUntilFinished / 1000 + "s");
        }

        @Override
        public void onFinish() {

            wxBindGetVerCodeBtn.setClickable(true);
            wxBindGetVerCodeBtn.setText(getResources().getString(R.string.send_code));


        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (requestPressent != null)
            requestPressent.detach();
    }
}
