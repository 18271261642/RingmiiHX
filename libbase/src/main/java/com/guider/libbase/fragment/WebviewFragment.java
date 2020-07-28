package com.guider.libbase.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.guider.libbase.R;
import com.guider.libbase.other.WebviewAgent;


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
