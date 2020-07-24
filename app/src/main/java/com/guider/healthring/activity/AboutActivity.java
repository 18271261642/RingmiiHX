package com.guider.healthring.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.TextView;
import com.guider.healthring.base.BaseActivity;
import com.guider.healthring.R;

/**
 * Created by thinkpad on 2017/3/9.
 * 关于
 */

public class AboutActivity extends BaseActivity {

    TextView tvTitle;
    TextView versionTv;

    @Override
    protected void initViews() {
        initViewIds();
        tvTitle.setText(R.string.abour);
        try {
            versionTv.setText(getVersionName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        checkUpdates();
    }

    private void initViewIds(){
        tvTitle = findViewById(R.id.tv_title);
        versionTv = findViewById(R.id.version_tv);
        findViewById(R.id.app_version_relayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUpdates();
            }


        });
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_about;
    }


    public void checkUpdates(){
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
