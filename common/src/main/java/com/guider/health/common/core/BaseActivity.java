package com.guider.health.common.core;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

/**
 * Created by haix on 2019/6/22.
 */

public class BaseActivity extends MySupportActivity{

    public Fragment getCurrentFragment() {
        return currentFragment;
    }

    public void setCurrentFragment(Fragment currentFragment) {
        this.currentFragment = currentFragment;
    }

    private Fragment currentFragment;

    @Override
    protected void onResume() {
        super.onResume();
        hideBottomUIMenu();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        MyTimerTask.getInstance().stopTimerTask();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startTimer();

        SoftKeyBoardListener.setListener(this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {

            @Override
            public void keyBoardShow(int height) {
                hideBottomUIMenu();
            }

            @Override
            public void keyBoardHide(int height) {
                hideBottomUIMenu();
            }
        });
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

    long firstTime = 0;// 上一次按回退键的时间

    /**
     * home 返回
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            //被fragment截获事件
            BaseFragment fragment = (BaseFragment) getCurrentFragment();
            if (fragment != null && fragment.backPressed()) {

                return true;
            }

            if (getFragmentManager().getBackStackEntryCount() > 1) {

                // 退出应用
                //getApplication().ForceExitProcess();
                pop();

            } else {
                if (System.currentTimeMillis() - firstTime >= 2000) {
                    Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    firstTime = System.currentTimeMillis();
                } else {
                    finish();// 销毁当前activity
                    System.exit(0);// 完全退出应用
                }
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // TODO Auto-generated method stub
        if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager.isActive()) {
                inputMethodManager.hideSoftInputFromWindow(this
                        .getCurrentFocus().getWindowToken(), 0);
            }
            return true;
        }
        // if (event.getAction() == event.ACTION_DOWN) {
        // InputMethodManager manager = (InputMethodManager)
        // getSystemService(Context.INPUT_METHOD_SERVICE);
        // if (getCurrentFocus() != null
        // && getCurrentFocus().getWindowToken() != null) {
        // manager.hideSoftInputFromWindow(getCurrentFocus()
        // .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        // }
        // }
        return super.dispatchKeyEvent(event);
    }


    /**
     * APP字体大小，不随系统的字体大小变化而变化
     *
     * @return
     */
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }


    // 开始计时
    private void startTimer() {
        MyTimerTask.getInstance().setRetentionTimeCallBack(new MyTimerTask.RetentionTimeCallBack() {
            @Override
            public void stopTimer() {
                try {
                    popTo(Class.forName(Config.HOME_LOGIN), false);
                    DevicePolicyManager mDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
                    mDevicePolicyManager.lockNow();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        long mLastActionTime = System.currentTimeMillis();
        MyTimerTask.getInstance().initTimerTask(mLastActionTime);

    }


    // 每当用户接触了屏幕，都会执行此方法
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        long mLastActionTime = System.currentTimeMillis();
        MyTimerTask.getInstance().updataStartRetentionTime(mLastActionTime);

        return super.dispatchTouchEvent(ev);
    }



}
