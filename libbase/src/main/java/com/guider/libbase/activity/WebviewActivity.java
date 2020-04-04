package com.guider.libbase.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebSettings;
import android.widget.LinearLayout;

import com.guider.health.common.core.DateUtil;
import com.guider.health.common.utils.StringUtil;
import com.guider.libbase.R;
import com.guider.libbase.other.BaseAndroidInterface;
import com.guider.libbase.other.WebviewAgent;

import java.util.List;

public class WebviewActivity extends AppCompatActivity {
    private static String TAG = "WebviewActivity";

    private LinearLayout mRootView;
    private boolean mIsShowing;

    private WebviewAgent mWebviewAgent;

    private long mBackClickTime;

    public static final int REQUEST_CODE_PHOTO = 1001;

    private String mSelectPhotoCallback;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        mRootView = findViewById(R.id.root_view);
        if (getWebiviewBackground() != null)
            mRootView.setBackground(getWebiviewBackground());

        Log.i(TAG, "time : " + DateUtil.localNowString());

        mWebviewAgent = new WebviewAgent(this, getIntent().getStringExtra("url"), mRootView, null);
    }

    protected Drawable getWebiviewBackground() {
        return null;
    }

    protected BaseAndroidInterface getAndroidInterface() {
        return null;
    }

    protected int getWebiviewCache() {
        return WebSettings.LOAD_DEFAULT;
    }

    @Override
    public void onResume() {
        mWebviewAgent.onWebviewShow();
        super.onResume();
    }

    @Override
    public void onPause() {
        mWebviewAgent.onWebviewHide();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebviewAgent.onWeviewDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mWebviewAgent.onBack())
            return;
       finish();
    }

    /*
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            boolean back = mAgentWeb.back();
            if (!back) {
                long currentTimeMillis = System.currentTimeMillis();
                if (currentTimeMillis - mBackClickTime >= 2000) {
                    Toast.makeText(this, "再按一次退出App", Toast.LENGTH_SHORT).show();
                    mBackClickTime = currentTimeMillis;
                    return true;
                } else {
                    System.exit(0);
                }
            } else {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PermissionsActivity.PERMISSION_REQUEST_CODE :
                if (resultCode == PermissionsActivity.PERMISSIONS_DENIED)
                    finish();
                break;
            case REQUEST_CODE_PHOTO :
                if (null == data || StringUtil.isEmpty(mSelectPhotoCallback))
                    return;
                // 获取返回的图片列表,这里只返回一张图
                List<String> path = data.getStringArrayListExtra(PictureSelectActivity.EXTRA_RESULT);
                // 处理你自己的逻辑 ....
                if (null == path || 0 >= path.size() || StringUtil.isEmpty(mSelectPhotoCallback))
                    return;

                // mAgentWeb.getJsAccessEntrace().quickCallJs(mSelectPhotoCallback, JsonUtil.toStr(path));
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }

    }

    public void setSelectPhoto(String callback) {
        mSelectPhotoCallback = callback;
    }


}
