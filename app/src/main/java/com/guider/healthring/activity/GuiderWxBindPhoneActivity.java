package com.guider.healthring.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.guider.health.common.utils.StringUtil;
import com.guider.healthring.BuildConfig;
import com.guider.healthring.R;
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

import org.json.JSONObject;

import static com.guider.healthring.BuildConfig.APIURL;

/**
 * 盖德微信绑定手机号，获取手机号验证码
 * Created by Admin
 * Date 2019/9/24
 */
public class GuiderWxBindPhoneActivity extends WatchBaseActivity
        implements RequestView, View.OnClickListener {

    private static final String TAG = "GuiderWxBindPhoneActivi";

    EditText wxBindPhoneVerCodeEdit;
    Button wxBindGetVerCodeBtn;
    TextView commentB30TitleTv;
    TextView wxBindPhoneCodeTv;
    EditText wxBindPhoneCodeEdit;
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
        initViewIds();
        initViews();

        wxStr = getIntent().getStringExtra("wxStr");

        initData();


    }

    private void initViewIds() {
        wxBindPhoneVerCodeEdit = findViewById(R.id.wxBindPhoneVerCodeEdit);
        wxBindGetVerCodeBtn = findViewById(R.id.wxBindGetVerCodeBtn);
        commentB30TitleTv = findViewById(R.id.commentB30TitleTv);
        wxBindPhoneCodeTv = findViewById(R.id.wxBindPhoneCodeTv);
        wxBindPhoneCodeEdit = findViewById(R.id.wxBindPhoneCodeEdit);
        wxBindSubBtn = findViewById(R.id.wxBindSubBtn);
        wxBindPhoneCodeTv.setOnClickListener(this);
        wxBindGetVerCodeBtn.setOnClickListener(this);
        wxBindSubBtn.setOnClickListener(this);
    }

    private void initData() {
        requestPressent = new RequestPressent();
        requestPressent.attach(this);
    }

    private void initViews() {
        commentB30TitleTv.setText(getResources().getString(R.string.bind_phone));
        countTimeUtils = new MyCountDownTimerUtils(60 * 1000, 1000);

    }

    @Override
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
//                String phoeVerCode = wxBindPhoneVerCodeEdit.getText().toString();
                if (WatchUtils.isEmpty(phoeCodes)) {
                    ToastUtil.showToast(this, getResources().getString(R.string.input_name));
                    return;
                }
//                if (WatchUtils.isEmpty(phoeVerCode)) {
//                    ToastUtil.showToast(this, getResources().getString(R.string.input_code));
//                    return;
//                }

                subWxBindData(phoeCodes);
                break;
        }
    }


    //提交绑定信息
    private void subWxBindData(String phoeCodes) {
        // 提交验证码，其中的code表示验证码，如“1357”
//        SMSSDK.submitVerificationCode(
//                StringUtils.substringAfter(
//                        wxBindPhoneCodeTv.getText().toString(), "+").trim(),
//                phoeCodes, phoeVerCode);
        String bindUrl = APIURL +
                "api/v2/wechat/bind/phone/token?phone=" + phoeCodes + "&code=";
        // http://api.guiderhealth.com/
        if (!StringUtil.isEmpty(wxStr)) {
            WxBindStr wxBindStr = new Gson().fromJson(wxStr, WxBindStr.class);
            //Log.e(TAG, "--------wx=" + wxBindStr.toString());
            requestPressent.getRequestJSONObject(1002, bindUrl,
                    GuiderWxBindPhoneActivity.this,
                    new Gson().toJson(wxBindStr), 2);
        }
    }

    //获取验证码
    private void getPhoneVerCode(final String phoeCode) {
    }


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
            if (jsonObject.getInt("code") != 0) {
                ToastUtil.showToast(this, jsonObject.getString("msg"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void analysisWbBindData(String str) {
        try {
            JSONObject jsonObject = new JSONObject(str);
            if (jsonObject.getInt("code") != 0) {
                ToastUtil.showToast(GuiderWxBindPhoneActivity.this, jsonObject.getString("msg"));
                return;
            }
            JSONObject dataJson = jsonObject.getJSONObject("data");
            long accountId = dataJson.getLong("accountId");
            SharedPreferencesUtils.setParam(this, "accountIdGD", accountId);
            String token = dataJson.getString("token");
            SharedPreferencesUtils.setParam(this, "tokenGD", token);
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

        @SuppressLint("SetTextI18n")
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
