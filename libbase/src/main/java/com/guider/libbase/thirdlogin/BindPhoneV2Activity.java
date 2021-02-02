package com.guider.libbase.thirdlogin;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.guider.health.apilib.ApiCallBack;
import com.guider.health.apilib.ApiUtil;
import com.guider.health.apilib.IGuiderApi;
import com.guider.health.apilib.enums.Gender;
import com.guider.health.apilib.model.AreaCode;
import com.guider.health.apilib.model.BeanOfWecaht;
import com.guider.health.apilib.model.ParamThirdUserAccount;
import com.guider.health.common.utils.SharedPreferencesUtils;
import com.guider.health.common.utils.StringUtil;
import com.guider.health.common.utils.ToastUtil;
import com.guider.libbase.R;
import com.guider.libbase.view.PhoneAreaCodeView;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Response;

public class BindPhoneV2Activity extends AppCompatActivity implements View.OnClickListener {
    // @BindView(R2.id.wxBindPhoneVerCodeEdit)
    EditText wxBindPhoneVerCodeEdit;
    // @BindView(R.id.wxBindGetVerCodeBtn)
    Button wxBindGetVerCodeBtn;

    // @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    // @BindView(R.id.wxBindPhoneCodeTv)
    TextView wxBindPhoneCodeTv;
    // @BindView(R.id.wxBindPhoneCodeEdit)
    EditText wxBindPhoneCodeEdit;
    // @BindView(R.id.wxBindSubBtn)
    Button wxBindSubBtn;

    private Dialog dialog;

    private PhoneAreaCodeView phoneAreaCodeView;

    // 倒计时
    MyCountDownTimerUtils countTimeUtils;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_phone_layout);

        wxBindPhoneVerCodeEdit = findViewById(R.id.wxBindPhoneVerCodeEdit);
        wxBindGetVerCodeBtn = findViewById(R.id.wxBindGetVerCodeBtn);
        commentB30TitleTv = findViewById(R.id.commentB30TitleTv);
        wxBindPhoneCodeTv = findViewById(R.id.wxBindPhoneCodeTv);
        wxBindPhoneCodeEdit = findViewById(R.id.wxBindPhoneCodeEdit);
        wxBindSubBtn = findViewById(R.id.wxBindSubBtn);

        wxBindPhoneCodeTv.setOnClickListener(this);
        wxBindGetVerCodeBtn.setOnClickListener(this);
        wxBindSubBtn.setOnClickListener(this);

        initViews();
    }

    private void initViews() {
        commentB30TitleTv.setText(getResources().getString(R.string.bind_phone));
        countTimeUtils = new MyCountDownTimerUtils(60 * 1000, 1000);
    }

    // @OnClick({R.id.wxBindPhoneCodeTv, R.id.wxBindGetVerCodeBtn, R.id.wxBindSubBtn,})
    public void onClick(View view) {
        // 区号
        String areaCode = wxBindPhoneCodeTv.getText().toString();
        areaCode = areaCode.startsWith("+") ? areaCode.substring(1) : areaCode;
        if (view.getId() == R.id.wxBindPhoneCodeTv) {   // 选择区号
            choosePhoneAreCode();
        } else if (view.getId() == R.id.wxBindGetVerCodeBtn) { // 获取验证码

        } else if (view.getId() == R.id.wxBindSubBtn) {     // 提交
            //手机号
            final String phoeCodes = wxBindPhoneCodeEdit.getText().toString();
            //验证码
//            String phoeVerCode = wxBindPhoneVerCodeEdit.getText().toString();

            if (StringUtil.isEmpty(phoeCodes) || StringUtil.isEmpty(areaCode)) {
                showToast(this, R.string.input_name);
                return;
            }
//            if (StringUtil.isEmpty(phoeVerCode)) {
//                showToast(this, R.string.input_code);
//                return;
//            }

//            final String finalAreaCode = areaCode;
//            SmsMob.submitVerifyCode(areaCode, phoeCodes, phoeVerCode, new SmsMob.SmsCodeVerifyListener() {
//                @Override
//                public void onResult(int result) {
//
//                    if (result != 0) {
//                        if (result == 457) {//手机号格式不对
//                            wxBindGetVerCodeBtn.setText(getResources().getString(R.string.resend));
//                            wxBindGetVerCodeBtn.setClickable(true);
//                            ToastUtil.showShort(BindPhoneV2Activity.this, getResources().getString(R.string.format_is_wrong));
//                        }
//                        return;
//                    }

            // 注册
            IGuiderApi iGuiderApi = ApiUtil.createApi(IGuiderApi.class);
            ParamThirdUserAccount param = new ParamThirdUserAccount();
            final String appId = getIntent().getStringExtra("appId");
            final String openId = getIntent().getStringExtra("openId");
            final String nickName = getIntent().getStringExtra("nickName");
            final String headUrl = getIntent().getStringExtra("headUrl");
            param.setAppId(appId);
            param.setOpenid(openId);
            param.setNickname(nickName);
            param.setHeadimgurl(headUrl);
            param.setGender(Gender.MAN);
            param.setPhone(phoeCodes);
            param.setAreaCode(areaCode);
            showLoadingDialog("");
            iGuiderApi.bindPhoneAndLogin(param).enqueue(new ApiCallBack<BeanOfWecaht>() {
                @Override
                public void onApiResponse(Call<BeanOfWecaht> call, Response<BeanOfWecaht> response) {
                    BeanOfWecaht info = response.body();
                    if (info != null) {
                        // 跳转
                        long accountId = info.getTokenInfo().getAccountId();
                        SharedPreferencesUtils.setParam(BindPhoneV2Activity.this,
                                "accountIdGD", accountId);
                        ToastUtil.showToast(BindPhoneV2Activity.this,
                                getResources().getString(R.string.bind_phone_success));

                        final HashMap<String, Object> map = new HashMap<>();
                        map.put("appId", appId);
                        map.put("openId", openId);
                        map.put("nickName", nickName);
                        map.put("headUrl", headUrl);
                        ThirdLogin.mIThirdLoginCallback.onUserInfo(map);
                        ThirdLogin.mCompelet.run();
                        finish();
                    }
                }

                @Override
                public void onRequestFinish() {
                    super.onRequestFinish();
                    hideLoadingDialog();
                }
            });
//                }
//            });
        }
    }

    // 选择区号
    private void choosePhoneAreCode() {
        if (phoneAreaCodeView == null)
            phoneAreaCodeView = new PhoneAreaCodeView(BindPhoneV2Activity.this);
        phoneAreaCodeView.show();
        phoneAreaCodeView.setPhoneAreaClickListener(new PhoneAreaCodeView.PhoneAreaClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void chooseAreaCode(AreaCode areCodeBean) {
                wxBindPhoneCodeTv.setText("+" + areCodeBean.getPhoneCode());
                phoneAreaCodeView.dismiss();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void showLoadingDialog(String msg) {
        if (dialog == null) {
            dialog = new Dialog(this, R.style.CustomProgressDialog);
            dialog.setContentView(R.layout.pro_dialog_layout_view);
            TextView tv = dialog.getWindow().findViewById(R.id.progress_tv);
            tv.setText(msg + "");
            dialog.setCancelable(true);
        } else {
            dialog.setContentView(R.layout.pro_dialog_layout_view);
            dialog.setCancelable(true);
            TextView tv = dialog.getWindow().findViewById(R.id.progress_tv);
            tv.setText(msg + "");
        }
        dialog.show();
        // mHandler.sendEmptyMessageDelayed(MSG_DISMISS_DIALOG, 30 * 1000);
    }

    public void hideLoadingDialog() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            dialog = null;
        }
    }

    private void showToast(Context context, int rcId) {
        showToast(context, getResources().getString(rcId));
    }

    private void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
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
}
