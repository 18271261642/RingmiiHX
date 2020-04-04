package com.guider.libbase.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.widget.LinearLayout;

import com.guider.health.common.core.DateUtil;
import com.guider.health.common.utils.StringUtil;
import com.guider.libbase.R;
import com.guider.libbase.activity.PermissionsActivity;
import com.guider.libbase.activity.PictureSelectActivity;
import com.guider.libbase.other.BaseAndroidInterface;
import com.guider.libbase.other.WebviewAgent;

import java.util.List;


public class WebviewFragment extends Fragment {
    private WebviewAgent mWebviewAgent;
    private String mUrl;

    public void setUrl(String url) {
        mUrl = url;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_webview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mWebviewAgent = new WebviewAgent(WebviewFragment.this, mUrl, (ViewGroup) view, null);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mWebviewAgent != null)
            mWebviewAgent.onWebviewShow();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mWebviewAgent != null)
            mWebviewAgent.onWebviewHide();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mWebviewAgent != null)
            mWebviewAgent.onWeviewDestroy();
    }

    public boolean onBackPressed() {
        if (mWebviewAgent == null) return true;
        return mWebviewAgent.onBack();
    }

}
