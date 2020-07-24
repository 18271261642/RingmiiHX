package com.guider.healthring.activity;

import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.guider.healthring.Commont;
import com.guider.healthring.R;
import com.guider.healthring.base.BaseActivity;
import com.guider.healthring.net.OkHttpObservable;
import com.guider.healthring.rxandroid.DialogSubscriber;
import com.guider.healthring.rxandroid.SubscriberOnNextListener;
import com.guider.healthring.util.Common;
import com.guider.healthring.util.Md5Util;
import com.guider.healthring.util.SharedPreferencesUtils;
import com.guider.healthring.util.ToastUtil;
import com.guider.healthring.util.URLs;
import com.google.gson.Gson;

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

    private DialogSubscriber dialogSubscriber;
    private SubscriberOnNextListener<String> subscriberOnNextListener;

    @Override
    protected void initViews() {
        initViewIds();
        tvTitle.setText(R.string.modify_password);
        subscriberOnNextListener = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if(!jsonObject.has("code"))
                        return;
                    if(jsonObject.getInt("code") == 200){
                        ToastUtil.showShort(ModifyPasswordActivity.this, getString(R.string.modify_success));
                        finish();
                    }else{
                        ToastUtil.showToast(ModifyPasswordActivity.this,jsonObject.getString("msg"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
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
        } else if (oldPass.length() < 6||newPass.length() < 6) {
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
        Gson gson = new Gson();
        HashMap<String, Object> map = new HashMap<>();
        String userId = (String) SharedPreferencesUtils.readObject(ModifyPasswordActivity.this, Commont.USER_ID_DATA);
        map.put("userId", userId);
        map.put("oldePwd", Md5Util.Md532(oldePwd));
        map.put("newPwd", Md5Util.Md532(newPwd));
        String mapjson = gson.toJson(map);
        dialogSubscriber = new DialogSubscriber(subscriberOnNextListener, ModifyPasswordActivity.this);
        OkHttpObservable.getInstance().getData(dialogSubscriber, Commont.FRIEND_BASE_URL + URLs.xiugaipassword, mapjson);
    }

}
