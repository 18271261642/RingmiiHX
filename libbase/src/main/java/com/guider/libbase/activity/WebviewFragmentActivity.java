package com.guider.libbase.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.guider.libbase.R;
import com.guider.libbase.fragment.WebviewFragment;

public class WebviewFragmentActivity extends AppCompatActivity {
    private WebviewFragment mWebviewFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        String url = getIntent().getStringExtra("url");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        mWebviewFragment = new WebviewFragment();
        mWebviewFragment.setUrl(url);
        final int commit = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.root_view, mWebviewFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (mWebviewFragment.onBackPressed())
            return;
        finish();
    }
}
