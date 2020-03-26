package com.guider.glu_phone.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.guider.glu_phone.R;
import com.guider.health.common.core.MyUtils;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.DefaultWebClient;

import java.io.File;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by haix on 2019/8/7.
 */

public class AgentWebFragment extends GlocoseFragment implements EasyPermissions.PermissionCallbacks{
    public AgentWeb mAgentWeb;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);


    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {

            mAgentWeb.getJsAccessEntrace().quickCallJs("webShow");

        }else{

            mAgentWeb.getJsAccessEntrace().quickCallJs("webHide");



        }

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_agent_webview, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String url = "http://nons.guiderhealth.com/#/login";
        // String url = "http://192.168.1.182:8080/#/login";

        // forceLocale(Locale.ENGLISH, getContext());

        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent((LinearLayout) view, -1, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))//传入AgentWeb的父控件。
                .useDefaultIndicator(-1, 3)//设置进度条颜色与高度，-1为默认值，高度为2，单位为dp。
                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK) //严格模式 Android 4.2.2 以下会放弃注入对象 ，使用AgentWebView没影响。
                .setMainFrameErrorView(R.layout.agentweb_error_page, -1) //参数1是错误显示的布局，参数2点击刷新控件ID -1表示点击整个布局都刷新， AgentWeb 3.0.0 加入。
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.DISALLOW)//打开其他页面时，弹窗质询用户前往其他应用 AgentWeb 3.0.0 加入。
                .interceptUnkownUrl() //拦截找不到相关页面的Url AgentWeb 3.0.0 加入。
                .createAgentWeb()//创建AgentWeb。
                .ready()//设置 WebSettings。
                .go(url); //WebView载入该url地址的页面并显示。


        //AgentWebConfig.debug();

        // AgentWeb 4.0 开始，删除该类以及删除相关的API
//        DefaultMsgConfig.DownloadMsgConfig mDownloadMsgConfig = mAgentWeb.getDefaultMsgConfig().getDownloadMsgConfig();
        //  mDownloadMsgConfig.setCancel("放弃");  // 修改下载提示信息，这里可以语言切换

        // AgentWeb 没有把WebView的功能全面覆盖 ，所以某些设置 AgentWeb 没有提供 ， 请从WebView方面入手设置。
        mAgentWeb.getWebCreator().getWebView().setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        //mAgentWeb.getWebCreator().getWebView()  获取WebView .

//		mAgentWeb.getWebCreator().getWebView().setOnLongClickListener();


        mAgentWeb.getWebCreator().getWebView().getSettings().setJavaScriptEnabled(true);
//        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        //优先使用网络
        mAgentWeb.getWebCreator().getWebView().getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //将图片调整到适合webview的大小
        mAgentWeb.getWebCreator().getWebView().getSettings().setUseWideViewPort(true);
        //支持内容重新布局
        mAgentWeb.getWebCreator().getWebView().getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        //支持自动加载图片
        mAgentWeb.getWebCreator().getWebView().getSettings().setLoadsImagesAutomatically(true);
        //当webview调用requestFocus时为webview设置节点
        mAgentWeb.getWebCreator().getWebView().getSettings().setNeedInitialFocus(true);
        //自适应屏幕
        mAgentWeb.getWebCreator().getWebView().getSettings().setUseWideViewPort(true);
        mAgentWeb.getWebCreator().getWebView().getSettings().setLoadWithOverviewMode(true);
        //开启DOM storage API功能（HTML5 提供的一种标准的接口，主要将键值对存储在本地，在页面加载完毕后可以通过 javascript 来操作这些数据。）
        mAgentWeb.getWebCreator().getWebView().getSettings().setDomStorageEnabled(true);
        //支持缩放
        mAgentWeb.getWebCreator().getWebView().getSettings().setBuiltInZoomControls(true);
        mAgentWeb.getWebCreator().getWebView().getSettings().setSupportZoom(true);

        //允许webview对文件的操作
        mAgentWeb.getWebCreator().getWebView().getSettings().setAllowFileAccess(true);
        mAgentWeb.getWebCreator().getWebView().getSettings().setAllowFileAccessFromFileURLs(true);
        mAgentWeb.getWebCreator().getWebView().getSettings().setAllowUniversalAccessFromFileURLs(true);

        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(false);
        }
//        mAgentWeb.getWebCreator().getWebView().setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (event.getAction() == KeyEvent.ACTION_DOWN) {
//                    if (keyCode == KeyEvent.KEYCODE_BACK && mAgentWeb.getWebCreator().getWebView().canGoBack()) { // 表示按返回键时的操作
//                        mAgentWeb.getWebCreator().getWebView().goBack(); // 后退
//                        // webview.goForward();//前进
//                        return true; // 已处理
//                    } else if (keyCode == KeyEvent.KEYCODE_BACK) {
//                        getActivity().moveTaskToBack(true);
//                    }
//                }
//                return false;
//            }
//        });



//        mAgentWeb.getWebCreator().getWebView().setWebChromeClient(new WebChromeClient() {
//
//            public void onProgressChanged(WebView view, int newProgress) {
//
//            }
//
//            public boolean onConsoleMessage(ConsoleMessage cm) {
//                Log.d("haix", "++++++: "+cm.message() + " line: "+cm.lineNumber()+ " scource: "+ cm.sourceId());
//                return true;
//            }
//
//        });

        mAgentWeb.getJsInterfaceHolder().addJavaObject("Android",new AndroidInterface(mAgentWeb,this));
        String ua = mAgentWeb.getWebCreator().getWebView().getSettings().getUserAgentString();

        mAgentWeb.getWebCreator().getWebView().getSettings().setUserAgentString(ua+" guider-android/1.0.0");
        String ua1 = mAgentWeb.getWebCreator().getWebView().getSettings().getUserAgentString();
        Log.i("haix", "=======UserAgent:"+ ua1);



    }

    @Override
    public void onResume() {
        if (mAgentWeb != null) {
            mAgentWeb.getWebLifeCycle().onResume();//恢复
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        if (mAgentWeb != null){
            mAgentWeb.getWebLifeCycle().onPause(); //暂停应用内所有WebView ， 调用mWebView.resumeTimers();/mAgentWeb.getWebLifeCycle().onResume(); 恢复。
        }
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        if (mAgentWeb != null) {
            mAgentWeb.getWebLifeCycle().onDestroy();
        }
        super.onDestroyView();
    }


    public void toGlu(){
        start(new AttentionInfo());
    }





    ////todo 相册
    private String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    //获取权限
    private void getPermission() {
        if (EasyPermissions.hasPermissions(_mActivity, permissions)) {
            //已经打开权限
            Toast.makeText(_mActivity, "已经申请相关权限", Toast.LENGTH_SHORT).show();
        } else {
            //没有打开相关权限、申请权限
            EasyPermissions.requestPermissions(_mActivity, "需要获取您的相册、照相使用权限", 1, permissions);
        }

    }

    private File cameraSavePath;//拍照照片路径
    private Uri uri;
    //激活相机操作
    public void goCamera() {
        cameraSavePath = new File(Environment.getExternalStorageDirectory().getPath() + "/" + System.currentTimeMillis() + ".jpg");

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(_mActivity, "com.example.hxd.pictest.fileprovider", cameraSavePath);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(cameraSavePath);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, 1);
    }

    //激活相册操作
    public void goPhotoAlbum() {
        if(cameraCallBack == null){
            Log.i("haix","回调为null");
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 2);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //框架要求必须这么写
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    //成功打开权限
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

        Toast.makeText(_mActivity, "相关权限获取成功", Toast.LENGTH_SHORT).show();
    }
    //用户未同意权限
    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(_mActivity, "请同意相关权限，否则功能无法使用", Toast.LENGTH_SHORT).show();
    }


    Bitmap myBitmap = null;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        final String photoPath;
        if (requestCode == 1 && resultCode == RESULT_OK) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                photoPath = String.valueOf(cameraSavePath);
            } else {
                photoPath = uri.getEncodedPath();
            }
            Log.d("拍照返回图片路径:", photoPath);

            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        myBitmap = Glide.with(_mActivity)
                                .load(photoPath)
                                .asBitmap()
                                .centerCrop()
                                .into(100, 100)
                                .get();

                        String bitmapStr = MyUtils.Bitmap2StrByBase64(myBitmap);
                        cameraCallBack.getCameraBase64String(bitmapStr);
//                        _mActivity.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                image_head.setImageBitmap(myBitmap);
//                            }
//                        });
//                        MemberManager.getInstance().uploadPic(myBitmap, new MemberManager.OperatorCallBack() {
//                            @Override
//                            public void operator(int code, String result) {
//                                Log.i("hai", "++++++++结果: "+result);
//                                Config100.MEMBERS.get(index).setHeadUrl(result);
//                            }
//                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();



            //Glide.with(_mActivity).load(photoPath).into(image_head);
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            photoPath = getPhotoFromPhotoAlbum.getRealPathFromUri(_mActivity, data.getData());

            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        myBitmap = Glide.with(_mActivity)
                                .load(photoPath)
                                .asBitmap()
                                .centerCrop()
                                .into(100, 100)
                                .get();

                        String bitmapStr = MyUtils.Bitmap2StrByBase64(myBitmap);
                        cameraCallBack.getCameraBase64String(bitmapStr);
//                        _mActivity.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                image_head.setImageBitmap(myBitmap);
//                            }
//                        });

//                        MemberManager.getInstance().uploadPic(myBitmap, new MemberManager.OperatorCallBack() {
//                            @Override
//                            public void operator(int code, String result) {
//                                Log.i("hai", "++++++++结果: "+result);
//                                Config100.MEMBERS.get(index).setHeadUrl(result);
//                            }
//                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            //Glide.with(_mActivity).load(photoPath).into(image_head);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    private CameraCallBack cameraCallBack;

    public void setCameraCallBack(CameraCallBack callBack){
        cameraCallBack = callBack;
    }
    public interface CameraCallBack{
        void getCameraBase64String(String str);
    }

    public void forceLocale(Locale locale, Context context) {
        Configuration conf = context.getResources().getConfiguration();
        updateConfiguration(conf, locale);
        context.getResources().updateConfiguration(conf, context.getResources().getDisplayMetrics());

        Configuration systemConf = Resources.getSystem().getConfiguration();
        updateConfiguration(systemConf, locale);
        Resources.getSystem().updateConfiguration(conf, context.getResources().getDisplayMetrics());

        Locale.setDefault(locale);
    }

    public void updateConfiguration(Configuration conf, Locale locale) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
            conf.setLocale(locale);
        }else {
            //noinspection deprecation
            conf.locale = locale;
        }
    }

}

