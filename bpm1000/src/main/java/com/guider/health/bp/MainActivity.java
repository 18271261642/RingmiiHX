package com.guider.health.bp;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.guider.health.bluetooth.core.BleBluetooth;
import com.guider.health.common.core.BaseActivity;

public class MainActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BleBluetooth.getInstance().init(this);





    }

    @Override
    protected void onResume() {
        super.onResume();
        BleBluetooth.getInstance().openBluetooth();
        hideBottomUIMenu();
    }

    //全屏
    protected void hideBottomUIMenu() {

        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

}
