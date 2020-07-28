package com.guider.healthring.b30;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.guider.healthring.Commont;
import com.guider.healthring.MyApp;
import com.guider.healthring.R;
import com.guider.healthring.bleutil.MyCommandManager;
import com.guider.healthring.siswatch.WatchBaseActivity;
import com.guider.healthring.util.SharedPreferencesUtils;
import com.guider.healthring.util.ToastUtil;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IPwdDataListener;
import com.veepoo.protocol.model.datas.PwdData;
import com.veepoo.protocol.model.enums.EPwdStatus;

/**
 * 重置设备密码
 */
public class B30ResetActivity extends WatchBaseActivity implements View.OnClickListener{


    ImageView commentB30BackImg;
    TextView commentB30TitleTv;
    EditText b30OldPwdEdit;
    EditText b30NewPwdEdit;
    EditText b30AgainNewPwdEdit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b30_reset);
        initViewIds();
        initViews();


    }

    private void initViewIds() {
        commentB30BackImg =findViewById(R.id.commentB30BackImg);
        commentB30TitleTv =findViewById(R.id.commentB30TitleTv);
        b30OldPwdEdit =findViewById(R.id.b30OldPwdEdit);
        b30NewPwdEdit =findViewById(R.id.b30NewPwdEdit);
        b30AgainNewPwdEdit =findViewById(R.id.b30AgainNewPwdEdit);
        commentB30BackImg.setOnClickListener(this);
        findViewById(R.id.b30ResetPwdBtn).setOnClickListener(this);
    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(getResources().getString(R.string.string_reset_device_password));

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:
                finish();
                break;
            case R.id.b30ResetPwdBtn:
                resetBlePwd();
                break;
        }
    }

    //重置设备密码
    private void resetBlePwd() {
        String oldPwd = b30OldPwdEdit.getText().toString().trim();
        final String newPwd = b30NewPwdEdit.getText().toString().trim();
        String againNewPwd = b30AgainNewPwdEdit.getText().toString().trim();
        //密码
        String b30Pwd = (String) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.DEVICESCODE, "0000");
        String newb30pwd = (String) SharedPreferencesUtils.getParam(MyApp.getContext(), "newb30pwd", "0000");

        if (TextUtils.isEmpty(oldPwd)) {
            ToastUtil.showShort(B30ResetActivity.this, getResources().getString(R.string.string_writ_old_password));
            return;
        }
        if (TextUtils.isEmpty(newPwd)) {
            ToastUtil.showShort(B30ResetActivity.this, getResources().getString(R.string.string_writ_new_device_password));
            return;
        }
        if (TextUtils.isEmpty(againNewPwd)) {
            ToastUtil.showShort(B30ResetActivity.this, getResources().getString(R.string.string_writ_new_device_password));
            return;
        }

        if (!newPwd.equals(againNewPwd)) {
            ToastUtil.showShort(B30ResetActivity.this, getResources().getString(R.string.string_two_passwords_are_different));
            return;
        }

        if (!oldPwd.equals(b30Pwd) && !oldPwd.equals(newb30pwd)) {
            ToastUtil.showShort(B30ResetActivity.this, getResources().getString(R.string.string_old_password_incorrect));
            return;
        }

        if (MyCommandManager.DEVICENAME != null) {
            if (againNewPwd.length() != 4) {
                ToastUtil.showShort(B30ResetActivity.this, getResources().getString(R.string.string_pass_4));
                return;
            }
            MyApp.getInstance().getVpOperateManager().modifyDevicePwd(iBleWriteResponse, new IPwdDataListener() {
                @Override
                public void onPwdDataChange(PwdData pwdData) {
                    Log.e("密码", "-----pwdData=" + pwdData.toString());
                    if (pwdData.getmStatus() == EPwdStatus.SETTING_SUCCESS) {
                        SharedPreferencesUtils.setParam(MyApp.getContext(), "newb30pwd", newPwd);
                        ToastUtil.showShort(B30ResetActivity.this, getResources().getString(R.string.string_reset_password_successfully));
                        finish();
                    }
                }
            }, againNewPwd);
        }

    }

    private IBleWriteResponse iBleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int i) {

        }
    };
}
