package com.guider.healthring.siswatch;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.guider.healthring.R;
import com.guider.healthring.b31.MessageHelpActivity;
import com.guider.healthring.siswatch.utils.WatchUtils;

import java.util.Locale;

/**
 * Created by Administrator on 2017/11/3.
 */

public class SearchExplainActivity extends WatchBaseActivity {

    TextView tvTitle;
    Toolbar toolbar;
    WebView searchExplainWV;
    WebSettings webSettings;
    String url = "";
    TextView moreHelpTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_explain);
        initViewIds();

        initViews();

        Locale locales = getResources().getConfiguration().locale;
        String country = locales.getCountry();
        if (!WatchUtils.isEmpty(country)) {
            if (country.equals("CN")) {
                url = "file:///android_asset/search_explain_zh.html";
            } else {
                url = "file:///android_asset/search_explain_en.html";
            }
        } else {
            url = "file:///android_asset/search_explain_en.html";
        }
        searchExplainWV.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

                return true;
            }
        });
        searchExplainWV.loadUrl(url);

    }

    private void initViewIds() {
        tvTitle = findViewById(R.id.tv_title);
        toolbar = findViewById(R.id.toolbar);
        searchExplainWV = findViewById(R.id.search_explainWV);
        moreHelpTv = findViewById(R.id.moreHelpTv);
    }

    private void initViews() {
        tvTitle.setText(getResources().getString(R.string.help));
        toolbar.setNavigationIcon(R.mipmap.backs);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        webSettings = searchExplainWV.getSettings();
        webSettings.setJavaScriptEnabled(false);
        webSettings.setSupportZoom(false);

        moreHelpTv.setText(">>"+getResources().getString(R.string.more));
        moreHelpTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(MessageHelpActivity.class);
            }
        });

    }
}
