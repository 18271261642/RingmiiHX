package com.guider.glu_phone;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.KeyEvent;
import android.widget.Toast;

import com.guider.glu.presenter.GLUServiceManager;
import com.guider.glu_phone.net.NetRequest;
import com.guider.glu_phone.view.TurnOnOperation;
import com.guider.health.bluetooth.core.BleBluetooth;
import com.guider.health.common.core.BaseActivity;
import com.guider.health.common.core.BaseFragment;

import java.lang.ref.WeakReference;

public class GlucoseMeasureActivity extends BaseActivity {


    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
    //请求状态码
    private static int REQUEST_PERMISSION_CODE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.glucose_measure_layout);

        /*
        UMShareConfig config = new UMShareConfig();
        config.isNeedAuthOnGetUserInfo(true);
        UMShareAPI.get(GlucoseMeasureActivity.this).setShareConfig(config);
        UMShareAPI mShareAPI = UMShareAPI.get(GlucoseMeasureActivity.this);
        */
        /*
        BaseFragment fragment = findFragment(AgentWebFragment.class);

        if (fragment == null) {
            loadRootFragment(R.id.content, new AgentWebFragment());
        }

         */

        NetRequest.getInstance().getHealthRange(new WeakReference<Activity>(this));

        BleBluetooth.getInstance().init(this);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            }
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

            if (fragment.getClass().toString().contains("MeasureResult")){
                // AndroidInterface.measureDid = true;
                // popTo(AgentWebFragment.class, false);
            }else if (fragment.getClass().toString().contains("AgentWebFragment")){
                if (System.currentTimeMillis() - firstTime >= 2000) {
                    Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    firstTime = System.currentTimeMillis();
                } else {
                    finish();// 销毁当前activity
                    System.exit(0);// 完全退出应用
                }
            }else if (fragment.getClass().toString().contains("BluetoothConnect")){
                GLUServiceManager.getInstance().stopDeviceConnect();
                popTo(TurnOnOperation.class, false);
            }else if (fragment.getClass().toString().contains("InsertFinger")){
                GLUServiceManager.getInstance().stopDeviceConnect();
                popTo(TurnOnOperation.class, false);
            }else if (fragment.getClass().toString().contains("Measure")){
                GLUServiceManager.getInstance().stopDeviceConnect();
                popTo(TurnOnOperation.class, false);
            }else{
                pop();
            }

//            if (getFragmentManager().getBackStackEntryCount() > 0) {
//
//                // 退出应用
//                //getApplication().ForceExitProcess();
//                pop();
//
//            } else {
//                if (System.currentTimeMillis() - firstTime >= 2000) {
//                    Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
//                    firstTime = System.currentTimeMillis();
//                } else {
//                    finish();// 销毁当前activity
//                    System.exit(0);// 完全退出应用
//                }
//            }
            return true;
        }

        return super.onKeyDown(keyCode, event);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // UMShareAPI.get(MainActivity.this).deleteOauth(this, SHARE_MEDIA.WEIXIN, authListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // UMShareAPI.get(this).onActivityResult(requestCode,resultCode,data);
    }



}
