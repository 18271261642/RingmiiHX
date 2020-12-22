package com.aliyun.rtcdemo.base;

import android.content.Intent;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.aliyun.rtcdemo.R;
import com.aliyun.rtcdemo.utils.PermissionUtils;
import com.guider.health.common.core.BaseActivity;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * 权限基类
 */
public class AliBaseActivity extends BaseActivity {


    protected CompositeSubscription mCompositeSubscription;


    /**
     * 请求权限
     */
    public void setUpSplash() {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        Subscription splash = Observable.timer(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> requestPermission());
        mCompositeSubscription.add(splash);
    }



    private PermissionUtils.PermissionGrant mGrant = new PermissionUtils.PermissionGrant() {
        @Override
        public void onPermissionGranted(int requestCode) {
            Log.i("chentao","requestCode:"+requestCode);

        }

        @Override
        public void onPermissionCancel() {

            Toast.makeText(AliBaseActivity.this.getApplicationContext(), getString(R.string.rtc_permission), Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    public void requestPermission() {
        try{
            PermissionUtils.requestMultiPermissions(this,
                    new String[]{
                            PermissionUtils.PERMISSION_CAMERA,
                            PermissionUtils.PERMISSION_WRITE_EXTERNAL_STORAGE,
                            PermissionUtils.PERMISSION_RECORD_AUDIO,
                            PermissionUtils.PERMISSION_READ_EXTERNAL_STORAGE}, mGrant);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionUtils.CODE_MULTI_PERMISSION) {
            PermissionUtils.requestPermissionsResult(this, requestCode, permissions, grantResults, mGrant);
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PermissionUtils.REQUEST_CODE_SETTING) {
            new Handler().postDelayed(this::requestPermission, 500);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
        }
    }
}
