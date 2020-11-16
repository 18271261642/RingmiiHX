package com.guider.healthring.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.guider.healthring.BuildConfig;
import com.guider.healthring.R;
import com.guider.healthring.base.BaseActivity;
import com.guider.healthring.siswatch.utils.WatchUtils;
import com.guider.healthring.util.Md5Util;
import com.guider.healthring.util.OkHttpTool;
import com.guider.healthring.util.SharedPreferencesUtils;
import com.guider.healthring.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by thinkpad on 2017/3/9.
 * 修改密码
 */

public class ModifyPasswordActivity extends BaseActivity {
    TextView tvTitle;
    EditText oldPassword;
    EditText newPassword;
    EditText confrimPassword;
    private Dialog dialog;

    @Override
    protected void initViews() {
        initViewIds();
        tvTitle.setText(R.string.modify_password);
    }

    private void initViewIds() {
        tvTitle = findViewById(R.id.tv_title);
        oldPassword = findViewById(R.id.old_password);
        newPassword = findViewById(R.id.new_password);
        confrimPassword = findViewById(R.id.confrim_password);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_complete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_modify_password;
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
        String editPassUrl = BuildConfig.APIURL + "api/v1/account/pwd";
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

    @SuppressLint("SetTextI18n")
    public void showLoadingDialog(String msg) {
        if (dialog == null) {
            dialog = new Dialog(ModifyPasswordActivity.this, R.style.CustomProgressDialog);
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
    protected void onDestroy() {
        super.onDestroy();
        hideLoadingDialog();
    }
}
