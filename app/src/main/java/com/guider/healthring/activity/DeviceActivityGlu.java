package com.guider.healthring.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.guider.glu_phone.view.AttentionInfo;
import com.guider.health.ChooseDeviceFragment;
import com.guider.health.apilib.ApiUtil;
import com.guider.health.bluetooth.core.BleBluetooth;
import com.guider.health.common.core.BaseActivity;
import com.guider.health.common.core.MyUtils;
import com.guider.health.common.core.UserManager;
import com.guider.healthring.BuildConfig;
import com.guider.healthring.R;

public class DeviceActivityGlu extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_glu);

        BleBluetooth.getInstance().init(this);

        MyUtils.setMacAddress(BuildConfig.MAC);
        loadRootFragment(R.id.main_content, new AttentionInfo());

        /*
        View view = findViewById(R.id.head_back);
        if (view == null) return;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
         */

    }

    public static void startGlu(Activity activity, int userId) {
        // 初始化工具类
        MyUtils.application = activity.getApplication();
        // ApiUtil.init(activity.getApplication(), MyUtils.getMacAddress());
        // DeviceInit.getInstance().init();
        // 初始化用户信息
        UserManager.getInstance().setAccountId(userId);
        UserManager.getInstance().getUserInfoOnServer(activity);
        // 开启M系列入口Activity
        activity.startActivity(new Intent(activity, DeviceActivityGlu.class));
    }
}
