package com.guider.gps.view.activity

import android.annotation.SuppressLint
import android.os.Build
import android.view.View
import android.webkit.*
import android.widget.ProgressBar
import com.guider.baselib.base.BaseActivity
import com.guider.baselib.utils.AndroidBug5497Workaround
import com.guider.baselib.utils.StringUtil
import com.guider.gps.R
import java.lang.ref.WeakReference

/**
 * @Package: com.guider.gps.view.activity
 * @ClassName: SimpleCustomWebActivity
 * @Description: 通用web页面
 * @Author: hjr
 * @CreateDate: 2020/8/28 14:01
 * Copyright (C), 1998-2020, GuiderTechnology
 */
class SimpleCustomWebActivity : BaseActivity() {

    private var pageTitle = "web页面"
    private var webUrl = ""
    private var webView: WebView? = null
    private var progressBar: ProgressBar? = null

    // 页面是否加载错误
    private var isError = false

    // 页面是否加载成功
    private var isSuccess = true

    override val contentViewResId: Int
        get() = R.layout.activity_simple_custom_web

    override fun openEventBus(): Boolean {
        return false
    }

    override fun initImmersion() {
        showBackButton(R.drawable.ic_back_white)
        if (intent != null) {
            if (StringUtil.isNotBlankAndEmpty(intent.getStringExtra("pageTitle"))) {
                pageTitle = intent.getStringExtra("pageTitle")!!
            }
            if (StringUtil.isNotBlankAndEmpty(intent.getStringExtra("webUrl"))) {
                webUrl = intent.getStringExtra("webUrl")!!
            }
        }
        setTitle(pageTitle)
    }

    override fun initView() {
        AndroidBug5497Workaround.assistActivity(this)
        webView = findViewById(R.id.web_simple)
        progressBar = findViewById(R.id.pb_web_load_progress)
    }

    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    override fun initLogic() {
        val webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if (newProgress < 100) {
                    progressBar!!.progress = newProgress
                    if (progressBar!!.visibility == View.GONE) {
                        progressBar!!.visibility = View.VISIBLE //显示
                    }
                } else {
                    progressBar!!.visibility = View.GONE
                    progressBar!!.progress = 0
                }
                super.onProgressChanged(view, newProgress)
            }
        }
        webView?.webChromeClient = webChromeClient
        // 设置与Js交互的权限
        val webSettings: WebSettings = webView!!.settings
        // 设置与Js交互的权限
        webSettings.javaScriptEnabled = true
        // 设置允许JS弹窗
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        //Android Lollipop(5.0)开始 webview默认不允许混合模式，https当中不能加载http资源，
        // 如果要加载，需单独设置开启。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        webView?.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (!isError) {
                    // 在访问失败的时候会首先回调onReceivedError，然后再回调onPageFinished
                    isSuccess = true
                }
                // 还原变量
                isError = false
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?,
                                         error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                isError = true
                isSuccess = false
            }
        }
        webView?.addJavascriptInterface(CustomJsInterface(WeakReference(this)),
                "GuiderGps")
        webView?.loadUrl(webUrl)
    }

    class CustomJsInterface(ref: WeakReference<SimpleCustomWebActivity>) {

        private var activity: SimpleCustomWebActivity? = ref.get()

        @JavascriptInterface
        fun goBack() {
            activity?.finish()
        }
    }

    override fun onBackPressed() {
        if (webView != null && webView!!.canGoBack()) {
            webView!!.goBack()
        }
        super.onBackPressed()
    }

    override fun showToolBar(): Boolean {
        return true
    }


}