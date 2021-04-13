package com.guider.healthring.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.guider.health.apilib.BuildConfig;
import com.guider.healthring.R;
import com.guider.healthring.siswatch.WatchBaseActivity;
import com.guider.healthring.siswatch.utils.WatchUtils;
import com.guider.healthring.util.Md5Util;
import com.guider.healthring.util.OkHttpTool;
import com.guider.healthring.util.SharedPreferencesUtils;
import com.guider.healthring.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.guider.healthring.BuildConfig.APIURL;

/**
 * Created by thinkpad on 2017/3/9.
 * 修改密码
 */

public class ModifyPasswordActivity extends WatchBaseActivity implements View.OnClickListener {
    TextView tvTitle;
    EditText oldPassword;
    EditText newPassword;
    EditText confrimPassword;
    TextView right_commit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);
        initViewIds();
        tvTitle.setText(mContext.getResources().getString(R.string.modify_password));
    }

    private void initViewIds() {
        tvTitle = findViewById(R.id.bar_titles);
        oldPassword = findViewById(R.id.old_password);
        newPassword = findViewById(R.id.new_password);
        confrimPassword = findViewById(R.id.confrim_password);
        right_commit = findViewById(R.id.right_commit);
        right_commit.setOnClickListener(this);
    }

    private void modifyPersonData(String oldePwd, String newPwd) {
        HashMap<String, String> map = new HashMap<>();
        long accountId = (long) SharedPreferencesUtils.getParam(
                ModifyPasswordActivity.this, "accountIdGD", 0L);
        if (accountId == 0)
            return;
        map.put("accountId", accountId + "");
        map.put("oldPwd", Md5Util.Md532(oldePwd));
        map.put("confirmPwd", Md5Util.Md532(newPwd));
        map.put("newPwd", Md5Util.Md532(newPwd));
        String editPassUrl = APIURL + "api/v1/account/pwd";
        showLoadingDialog("");
        OkHttpTool.getInstance().doRequest(editPassUrl, map, "1", result -> {
            Log.e("ModifyPasswordActivity", "-------修改密码=" + result);
            runOnUiThread(() -> {
                if (WatchUtils.isNetRequestSuccess(result, 0)) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (!jsonObject.has("code")) {
                            hideLoadingDialog();
                            return;
                        }
                        if (jsonObject.getInt("code") == 0
                                && jsonObject.getBoolean("data")) {
                            ToastUtil.showShort(ModifyPasswordActivity.this,
                                    getString(R.string.modify_success));
                            finish();
                        } else {
                            ToastUtil.showToast(ModifyPasswordActivity.this,
                                    jsonObject.getString("msg"));
                        }
                        hideLoadingDialog();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        hideLoadingDialog();
                    }
                } else hideLoadingDialog();
            });
        }, false);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideLoadingDialog();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.right_commit) {
            String oldPass = oldPassword.getText().toString().trim();
            String newPass = newPassword.getText().toString().trim();
            String confrimPass = confrimPassword.getText().toString().trim();
            if (TextUtils.isEmpty(oldPass)) {
                ToastUtil.showShort(this, getString(R.string.input_old_password));
            } else if (TextUtils.isEmpty(newPass)) {
                ToastUtil.showShort(this, getString(R.string.string_writ_new_device_password));
            } else if (TextUtils.isEmpty(confrimPass)) {
                ToastUtil.showShort(this, getString(R.string.input_confirm_password));
            } else if (!newPass.equals(confrimPass)) {
                ToastUtil.showShort(this, getString(R.string.new_dif_confirm));
            } else if (oldPass.length() < 6 || newPass.length() < 6) {
                ToastUtil.showShort(this, getResources().getString(R.string.not_b_less));
            } else {
                modifyPersonData(oldPass, confrimPass);
            }
        }
    }
}
