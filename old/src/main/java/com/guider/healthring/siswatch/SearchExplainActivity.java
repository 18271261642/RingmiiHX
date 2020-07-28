package com.guider.healthring.siswatch;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
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

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/11/3.
 */

public class SearchExplainActivity extends WatchBaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.search_explainWV)
    WebView searchExplainWV;
    WebSettings webSettings;
    String url = "";
    @BindView(R.id.moreHelpTv)
    TextView moreHelpTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_explain);
        ButterKnife.bind(this);

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
