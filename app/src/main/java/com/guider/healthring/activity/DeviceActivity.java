package com.guider.healthring.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.guider.health.ChooseDeviceFragment;
import com.guider.health.bluetooth.core.BleBluetooth;
import com.guider.health.common.core.BaseActivity;
import com.guider.health.common.core.MyUtils;
import com.guider.health.common.core.UserManager;
import com.guider.healthring.R;

public class DeviceActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        BleBluetooth.getInstance().init(this);
        loadRootFragment(R.id.main_content, new ChooseDeviceFragment());
    }


    public static void start(Activity activity, int userId) {
        // 初始化工具类
        MyUtils.application = activity.getApplication();
        // 初始化用户信息
        UserManager.getInstance().setAccountId(userId);
        UserManager.getInstance().getUserInfoOnServer(activity);
        // 开启M系列入口Activity
        activity.startActivity(new Intent(activity , DeviceActivity.class));
    }
}
