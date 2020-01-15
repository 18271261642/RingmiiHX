package com.guider.healthring.b30;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.guider.healthring.MyApp;
import com.guider.healthring.R;
import com.guider.healthring.activity.FeedbackActivity;
import com.guider.healthring.activity.ModifyPasswordActivity;
import com.guider.healthring.siswatch.WatchBaseActivity;
import com.guider.healthring.siswatch.utils.WatchUtils;
import com.guider.healthring.util.SharedPreferencesUtils;
import com.guider.healthring.util.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * B30手环系统设置页面
 */
public class B30SysSettingActivity extends WatchBaseActivity {


    @BindView(R.id.bar_titles)
    TextView barTitles;
    @BindView(R.id.version_tv)
    TextView versionTv;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b30_syssetting);
        ButterKnife.bind(this);


        initViews();

        initData();
    }

    private void initData() {

        try {
            versionTv.setText(packageName(MyApp.getContext()) + "");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String packageName(Context context) {
        PackageManager manager = context.getPackageManager();
        String name = null;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            name = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return name;
    }


    private void initViews() {
        barTitles.setText(getResources().getString(R.string.system_settings));

    }

    @OnClick({R.id.image_back, R.id.updatePwdLin, R.id.feebackLin, R.id.aboutLin})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_back:   //返回
                finish();
                break;
            case R.id.updatePwdLin:  //修改密码
                SharedPreferences share = getSharedPreferences("Login_id", 0);
                String userId = (String) SharedPreferencesUtils.readObject(B30SysSettingActivity.this, "userId");
                if (!WatchUtils.isEmpty(userId) && userId.equals("9278cc399ab147d0ad3ef164ca156bf0")) {
                    ToastUtil.showToast(B30SysSettingActivity.this, getResources().getString(R.string.noright));
                } else {
                    int isoff = share.getInt("id", 0);
                    if (isoff == 0) {
                        startActivity(ModifyPasswordActivity.class);
                    } else {
                        ToastUtil.showToast(B30SysSettingActivity.this, getResources().getString(R.string.string_third_login_changepass));
                    }
                }
                break;
            case R.id.feebackLin:    //意见反馈
                startActivity(FeedbackActivity.class);
                break;
            case R.id.aboutLin:

                break;
        }
    }

    public String getVersionName() throws Exception {
        // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
        String version = packInfo.versionName;
        return version;
    }
}
