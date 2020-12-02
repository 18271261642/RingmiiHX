package com.guider.libbase.other;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Handler;
import android.os.Message;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.webkit.ConsoleMessage;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.guider.health.common.core.DateUtil;
import com.guider.libbase.R;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.AgentWebConfig;
import com.just.agentweb.DefaultWebClient;
import com.just.agentweb.WebViewClient;

public class WebviewAgent implements IWebviewAgent {
    private static String TAG = "WebviewAgent";
    private Activity mActivity;
    private Fragment mFragment;
    protected AgentWeb mAgentWeb;
    private ViewGroup mRootView;

    // webview加载进度条
    private Dialog mProgressDialog;
    // webview加载动画
    private RotateAnimation mProgressAnimation;

    private boolean mIsShowing;
    private WebInterface mWebInterface;
    protected BaseAndroidInterface mBaseAndroidInterface;

    private String mUrl;

    private Handler mEventHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    initWebView(mUrl, getAndroidInterface());
                    break;
            }
        }
    };

    public WebviewAgent(Activity activity, String url, ViewGroup rootView, BaseAndroidInterface baseAndroidInterface) {
        mActivity = activity;
        mRootView = rootView;
        mUrl = url;
        mBaseAndroidInterface = baseAndroidInterface;

        initWebView(url, baseAndroidInterface);
    }

    public WebviewAgent(Fragment fragment, String url, ViewGroup rootView, BaseAndroidInterface baseAndroidInterface) {
        mFragment = fragment;
        mRootView = rootView;
        mUrl = url;
        mBaseAndroidInterface = baseAndroidInterface;

        // mEventHandler.sendEmptyMessage(1);
        initWebView(url, baseAndroidInterface);
    }

    public void onWebviewShow() {
        if (mWebInterface != null)
            mWebInterface.webShow();
        if (mAgentWeb != null)
            mAgentWeb.getWebLifeCycle().onResume();//恢复
    }

    public void onWebviewHide() {
        if (mWebInterface != null)
            mWebInterface.webHide();
        if (mAgentWeb != null)
            mAgentWeb.getWebLifeCycle().onPause();
    }

    public void onWeviewDestroy() {
        if (mAgentWeb != null)
            mAgentWeb.getWebLifeCycle().onDestroy();
    }

    public boolean onBack() {
        return mAgentWeb.back();
    }

    protected void initWebView(String url, BaseAndroidInterface baseAndroidInterface) {
        AgentWeb.AgentBuilder builder;
        if (mActivity != null)
            builder = AgentWeb.with(mActivity);
        else if (mFragment != null && mFragment.getActivity() != null)
            builder = AgentWeb.with(mFragment.getActivity());
        else return;

        mAgentWeb = builder
                .setAgentWebParent(mRootView, new ViewGroup.LayoutParams(-1, -1))
                // new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))// 传入AgentWeb的父控件。
                .useDefaultIndicator(-1, 3) // 设置进度条颜色与高度，-1为默认值，高度为2，单位为dp。
                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK) // 严格模式 Android 4.2.2 以下会放弃注入对象 ，使用AgentWebView没影响。
                .setMainFrameErrorView(R.layout.agentweb_error_page, -1) // 参数1是错误显示的布局，参数2点击刷新控件ID -1表示点击整个布局都刷新， AgentWeb 3.0.0 加入。
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.DISALLOW) // 打开其他页面时，弹窗质询用户前往其他应用 AgentWeb 3.0.0 加入。
                .interceptUnkownUrl() // 拦截找不到相关页面的Url AgentWeb 3.0.0 加入。
                .setWebChromeClient(new com.just.agentweb.WebChromeClient() {
                    @Override
                    public void onConsoleMessage(String message, int lineNumber, String sourceID) {
                        super.onConsoleMessage(message, lineNumber, sourceID);
                        Log.i("console", message + "(" + sourceID + ":" + lineNumber + ")");
                    }

                    @Override
                    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                        Log.i("console", "[" + consoleMessage.messageLevel() + "] "
                                + consoleMessage.message() + "(" + consoleMessage.sourceId() + ":" + consoleMessage.lineNumber() + ")");
                        return true;
                    }

                    @Override
                    public void onProgressChanged(WebView view, int newProgress) {
                        super.onProgressChanged(view, newProgress);
                        Log.i(TAG, "onProgressChanged " + newProgress);
                        if (newProgress < 100)
                            showLoading(true);
                        else
                            showLoading(false);
                    }
                })
                .setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                        Log.i(TAG, "onPageStarted");
                        Log.i(TAG, "time : onPageStarted " + DateUtil.localNowString());
                        mIsShowing = false;
                        showLoading(true);
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        Log.i(TAG, "onPageFinished");
                        showLoading(false);
                    }

                    @Override
                    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                        super.onReceivedHttpError(view, request, errorResponse);
                        if (errorResponse != null && errorResponse.getData() != null)
                            Log.e(TAG, "onReceivedHttpError" + errorResponse.getData().toString());
                        showLoading(false);
                    }

                    @Override
                    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                        super.onReceivedSslError(view, handler, error);
                        Log.e(TAG, "onReceivedSslError" + error.toString());
                        showLoading(false);
                    }
                })
                .createAgentWeb() // 创建AgentWeb。
                .ready() // 设置 WebSettings。
                .go(url); // WebView载入该url地址的页面并显示。
        mAgentWeb.getWebCreator().getWebView().setOverScrollMode(WebView.OVER_SCROLL_NEVER);

        mAgentWeb.getWebCreator().getWebView().getSettings().setJavaScriptEnabled(true);
        // 优先使用网络
        mAgentWeb.getWebCreator().getWebView().getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        // 将图片调整到适合webview的大小
        mAgentWeb.getWebCreator().getWebView().getSettings().setUseWideViewPort(true);
        // 支持内容重新布局
        mAgentWeb.getWebCreator().getWebView().getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        // 支持自动加载图片
        mAgentWeb.getWebCreator().getWebView().getSettings().setLoadsImagesAutomatically(true);
        // 当webview调用requestFocus时为webview设置节点
        mAgentWeb.getWebCreator().getWebView().getSettings().setNeedInitialFocus(true);
        // 自适应屏幕
        mAgentWeb.getWebCreator().getWebView().getSettings().setUseWideViewPort(true);
        mAgentWeb.getWebCreator().getWebView().getSettings().setLoadWithOverviewMode(true);
        // 开启DOM storage API功能（HTML5 提供的一种标准的接口，主要将键值对存储在本地，在页面加载完毕后可以通过 javascript 来操作这些数据。）
        mAgentWeb.getWebCreator().getWebView().getSettings().setDomStorageEnabled(true);
        // 支持缩放
        mAgentWeb.getWebCreator().getWebView().getSettings().setBuiltInZoomControls(true);
        mAgentWeb.getWebCreator().getWebView().getSettings().setSupportZoom(true);

        // 允许webview对文件的操作
        mAgentWeb.getWebCreator().getWebView().getSettings().setAllowFileAccess(true);
        mAgentWeb.getWebCreator().getWebView().getSettings().setAllowFileAccessFromFileURLs(true);
        mAgentWeb.getWebCreator().getWebView().getSettings().setAllowUniversalAccessFromFileURLs(true);

        mAgentWeb.getWebCreator().getWebView().getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        if (baseAndroidInterface == null) {
            baseAndroidInterface = new BaseAndroidInterface(getContext(), this);
        }
        mAgentWeb.getJsInterfaceHolder().addJavaObject("Android", baseAndroidInterface);

        String ua = mAgentWeb.getWebCreator().getWebView().getSettings().getUserAgentString();

        mAgentWeb.getWebCreator().getWebView().getSettings().setUserAgentString(ua + " guider-android/1.0.0");

        mWebInterface = new WebInterface(mAgentWeb);

        AgentWebConfig.debug();
    }

    protected Context getContext() {
        if (mActivity != null)
            return mActivity;

        return mFragment.getContext();
    }

    protected BaseAndroidInterface getAndroidInterface() {
        return null;
    }

    private void showLoading(boolean show) {
        Log.i(TAG, "showLoading " + show + mIsShowing);
        if (mIsShowing == show) {
            return;
        }
        mIsShowing = show;
        if (show) {
            showDialog("正在加载");
        } else {
            hideDialog();
        }
    }

    protected void showDialog(String msg) {
        Dialog dialog = mProgressDialog;
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        dialog = mProgressDialog = new Dialog(getContext());
        // 去掉标题线
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.common_dialog);
        dialog.setCanceledOnTouchOutside(false);
        View baseTextview = dialog.findViewById(R.id.loading);
        dialog.setCancelable(false);
        TextView tvMsg = dialog.findViewById(R.id.msg);
        if (msg == null) {
            tvMsg.setVisibility(View.GONE);
        } else {
            tvMsg.setVisibility(View.VISIBLE);
            tvMsg.setText(msg);
        }
        // 背景透明
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        // 初始化动画
        initAnimation(baseTextview);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.CENTER; // 居中位置
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        window.setWindowAnimations(com.guider.health.common.R.style.mystyle);  //添加动画
    }

    protected void hideDialog() {
        if (mProgressDialog == null || !mProgressDialog.isShowing())
            return;
        mProgressDialog.dismiss();
        mProgressDialog = null;

        if (mProgressAnimation != null) {
            mProgressAnimation.cancel();
        }
    }

    protected void initAnimation(View textView) {
        mProgressAnimation = new RotateAnimation(0f, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);//设置图片动画属性，各参数说明可参照api
        mProgressAnimation.setRepeatCount(-1);// 设置旋转重复次数，即转几圈
        mProgressAnimation.setDuration(1000);// 设置持续时间，注意这里是每一圈的持续时间，如果上面设置的圈数为3，持续时间设置1000，则图片一共旋转3秒钟
        mProgressAnimation.setInterpolator(new LinearInterpolator());// 设置动画匀速改变。相应的还有AccelerateInterpolator、DecelerateInterpolator、CycleInterpolator等
        textView.setAnimation(mProgressAnimation);// 设置imageview的动画，也可以myImageView.startAnimation(myAlphaAnimation)

        mProgressAnimation.startNow();
    }

    @Override
    public void callJs(String method, String value) {
        Log.i("cmateH_webCall", method + ": " + value);
        mAgentWeb.getJsAccessEntrace().quickCallJs(method, value);
    }

    /**
     * 处理页面展示和隐藏
     */
    public class WebInterface {
        private AgentWeb mAgentWeb;

        public WebInterface(AgentWeb web) {
            mAgentWeb = web;
        }

        public void webShow() {
            mAgentWeb.getJsAccessEntrace().quickCallJs("webShow");
        }

        public void webHide() {
            mAgentWeb.getJsAccessEntrace().quickCallJs("webHide");
        }
    }
}
