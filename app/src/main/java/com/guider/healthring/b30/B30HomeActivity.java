package com.guider.healthring.b30;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

import com.guider.healthring.Commont;
import com.guider.healthring.MyApp;
import com.guider.healthring.R;
import com.guider.healthring.adpter.FragmentAdapter;
import com.guider.healthring.b30.b30homefragment.B30HomeFragment;
import com.guider.healthring.b30.b30minefragment.B30MineFragment;
import com.guider.healthring.b30.service.UpDataToGDServices;
import com.guider.healthring.b30.service.UpDataToGDServicesNew;
import com.guider.healthring.b30.service.UpHrvDataToGDServices;
import com.guider.healthring.bleutil.MyCommandManager;
// import com.guider.healthring.bzlmaps.sos.GPSGaoDeUtils;
// import com.guider.healthring.bzlmaps.sos.GPSGoogleUtils;
import com.guider.healthring.bzlmaps.sos.SendSMSBroadCast;
import com.guider.healthring.commdbserver.CommDataFragment;
import com.guider.healthring.siswatch.WatchBaseActivity;
import com.guider.healthring.siswatch.run.B30RunFragment;
import com.guider.healthring.siswatch.utils.WatchUtils;
import com.guider.healthring.util.SharedPreferencesUtils;
import com.guider.healthring.util.VerifyUtil;
import com.guider.healthring.widget.NoScrollViewPager;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.Setting;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/7/20.
 */

public class B30HomeActivity extends WatchBaseActivity implements Rationale<List<String>> {


    @BindView(R.id.b30View_pager)
    NoScrollViewPager b30ViewPager;
    @BindView(R.id.b30BottomBar)
    BottomBar b30BottomBar;

    private List<Fragment> b30FragmentList = new ArrayList<>();


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1001:
//                    if (MyApp.getB30ConnStateService() != null) {
//                        String bm = (String) SharedPreferencesUtils.readObject(B30HomeActivity.this, Commont.BLEMAC);//设备mac
//                        if (!TextUtils.isEmpty(bm))
//                            MyApp.getB30ConnStateService().connB30ConnBle(bm);
//                    }
                    if (MyApp.getInstance().getB30ConnStateService() != null) {
                        String bm = (String) SharedPreferencesUtils.readObject(B30HomeActivity.this, Commont.BLEMAC);//设备mac
                        if (!TextUtils.isEmpty(bm))
                            MyApp.getInstance().getB30ConnStateService().connectAutoConn(true);
                    }
                    break;
                case 0x01:
                    handler.removeMessages(0x01);
                    String stringpersonOne = (String) SharedPreferencesUtils.getParam(B30HomeActivity.this, "personOne", "");
                    String stringpersonTwo = (String) SharedPreferencesUtils.getParam(B30HomeActivity.this, "personTwo", "");
                    String stringpersonThree = (String) SharedPreferencesUtils.getParam(B30HomeActivity.this, "personThree", "");

                    if (!TextUtils.isEmpty(stringpersonOne)) {
                        call(stringpersonOne);
                    } else {
                        if (!TextUtils.isEmpty(stringpersonTwo)) {
                            call(stringpersonTwo);
                        } else {
                            if (!TextUtils.isEmpty(stringpersonThree)) {
                                call(stringpersonThree);
                            }
                        }
                    }
                    break;
            }
        }
    };

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b30_home);
        ButterKnife.bind(this);
        initViews();
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);



        //过滤器
        IntentFilter mIntentFilter = new IntentFilter("com.guider.ringmiihx.bzlmaps.sos.SENDSMS");
        mIntentFilter.addAction("com.example.bozhilun.android.b30.service.UploadServiceGD");
        //创建广播接收者的对象
        sendSMSBroadCast = new SendSMSBroadCast();
        //注册广播接收者的对象
        this.registerReceiver(sendSMSBroadCast, mIntentFilter);

//        String isimei = (String) SharedPreferencesUtils.getParam(B30HomeActivity.this, "ISIMEI", "");
//        if (WatchUtils.isEmpty(isimei)){
//            String imei = SystemUtil.getIMEI(B30HomeActivity.this);
//            if (!WatchUtils.isEmpty(imei)){
//                if (Commont.isDebug)Log.e("====IEMI==", imei);
//                SharedPreferencesUtils.setParam(B30HomeActivity.this, "ISIMEI", imei);
//                Intent intent = new Intent(B30HomeActivity.this, MainActivity.class);
//                intent.setFlags(101);//傳送設備碼
//                intent.putExtra("data", imei);
//                startActivity(intent);
//                startActivityForResult(intent, 1); //REQUESTCODE--->1
//            }
//        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        //getGps();
    }

    SendSMSBroadCast sendSMSBroadCast = null;
    private Vibrator vibrator;

    public void vibrate() {
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(3000); //参数标识  震动持续多少毫秒
        }
    }


    private void initViews() {
        b30FragmentList.add(new B30HomeFragment());
//        b30FragmentList.add(new B30DataFragment());
        b30FragmentList.add(new CommDataFragment());
//        b30FragmentList.add(new W30sNewRunFragment());
        b30FragmentList.add(new B30RunFragment());//跑步
        b30FragmentList.add(new B30MineFragment());
        FragmentStatePagerAdapter fragmentPagerAdapter = new FragmentAdapter(getSupportFragmentManager(), b30FragmentList);
        if (b30ViewPager != null) {
            b30ViewPager.setAdapter(fragmentPagerAdapter);
            b30ViewPager.setOffscreenPageLimit(0);
        }
        if (b30BottomBar != null) b30BottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(int tabId) {
                if (b30ViewPager == null) return;
                switch (tabId) {
                    case R.id.b30_tab_home: //首页
                        b30ViewPager.setCurrentItem(0, false);
                        break;
                    case R.id.b30_tab_data: //数据
                        b30ViewPager.setCurrentItem(1, false);
                        break;
                    case R.id.b30_tab_set:  //开跑
                        b30ViewPager.setCurrentItem(2, false);
                        break;
                    case R.id.b30_tab_my:   //我的
                        b30ViewPager.setCurrentItem(3, false);
                        break;
                }
            }
        });
    }

    /**
     * 重新连接设备B30
     */
    public void reconnectDevice() {
        if (MyCommandManager.ADDRESS == null) {    //未连接
            if (MyApp.getInstance().getB30ConnStateService() != null) {
                String bm = (String) SharedPreferencesUtils.readObject(B30HomeActivity.this, Commont.BLEMAC);//设备mac
//                if (Commont.isDebug)Log.d("----去自动链接-", "go go go3" + bm);
                if (!WatchUtils.isEmpty(bm)) {
//                    if (Commont.isDebug)Log.d("----去自动链接-", "go go go4" + bm);

                    //MyApp.getB30ConnStateService().connB30ConnBle(bm.trim());
                    MyApp.getInstance().getB30ConnStateService().connectAutoConn(true);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        super.onBackPressed();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 过滤按键动作
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);

        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            moveTaskToBack(true);
        } else if (keyCode == KeyEvent.KEYCODE_HOME) {
            moveTaskToBack(true);
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 启动上传数据的服务
     */
    public void startUploadDate() {
        boolean uploading = MyApp.getInstance().isUploadDate();
        // 判断一下是否正在上传数据
        if (!uploading) {
           // startService(new Intent(this, DateUploadService.class));
        }

        if (!MyApp.getInstance().isUploadDateGDNew()){
            //上传到新的额服务器
            if (upDataToGDServicesNew != null && upDataToGDServicesNew.getStatus() == AsyncTask.Status.RUNNING) {
                upDataToGDServicesNew.cancel(true); // 如果Task还在运行，则先取消它
                upDataToGDServicesNew = null;
                upDataToGDServicesNew = new UpDataToGDServicesNew();
            } else {
                upDataToGDServicesNew = new UpDataToGDServicesNew();
            }
            upDataToGDServicesNew.execute();
        }
        if (!MyApp.getInstance().isUploadDateGDHrv()){
            //上传到新的服务器---hrv
            if (upHrvDataToGDServices != null && upHrvDataToGDServices.getStatus() == AsyncTask.Status.RUNNING) {
                upHrvDataToGDServices.cancel(true); // 如果Task还在运行，则先取消它
                upHrvDataToGDServices = null;
                upHrvDataToGDServices = new UpHrvDataToGDServices();
            } else {
                upHrvDataToGDServices = new UpHrvDataToGDServices();
            }
            upHrvDataToGDServices.execute();
        }


        boolean uploadingGD = MyApp.getInstance().isUploadDateGD();
        if (Commont.isDebug)Log.d("========", "数据同步中  " + uploadingGD);
        if (!uploadingGD) {
//            UpdataThree downloadTask = new UpdataThree();
//            downloadTask.execute("");

//            if (upDataToGDServices != null) {
//                upDataToGDServices.cancel(true);
//                upDataToGDServices = null;
//            }
            try {
                //上传到旧的服务器
                if (upDataToGDServices != null && upDataToGDServices.getStatus() == AsyncTask.Status.RUNNING) {
                    upDataToGDServices.cancel(true); // 如果Task还在运行，则先取消它
                    upDataToGDServices = null;
                    upDataToGDServices = new UpDataToGDServices();
                } else {
                    upDataToGDServices = new UpDataToGDServices();
                }
                upDataToGDServices.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }

//            startService(new Intent(this, DateUploadServiceGD.class));
        }

    }


    UpDataToGDServices upDataToGDServices = null;
    UpDataToGDServicesNew upDataToGDServicesNew = null;
    UpHrvDataToGDServices upHrvDataToGDServices = null;



    // GPSGoogleUtils instance;
/*
    void getGpsGoogle() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean b = instance.startLocationUpdates(MyApp.getInstance());
                if (!b) {
                  //  getGpsGoogle();
                }
            }
        }, 3000);
    }
*/
    /**
     * 打电话
     *
     * @param tel
     */
    //点击事件调用的类
    protected void call(final String tel) {
        try {
            if(AndPermission.hasPermissions(B30HomeActivity.this,Manifest.permission.CALL_PHONE)){
                Uri uri = Uri.parse("tel:" + tel);
                Intent intent = new Intent(Intent.ACTION_CALL, uri);
                startActivity(intent);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sendSMSBroadCast != null) unregisterReceiver(sendSMSBroadCast);
    }

    @Override
    public void showRationale(Context context, List<String> data, final RequestExecutor executor) {
        List<String> permissionNames = Permission.transformText(context, data);
        String message = getResources().getString(R.string.string_get_permission) + "\n" + permissionNames;

        new android.app.AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle(getResources().getString(R.string.prompt))
                .setMessage(message)
                .setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        executor.execute();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancle), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        executor.cancel();
                    }
                })
                .show();
    }

    /**
     * Display setting dialog.
     */
    public void showSettingDialog(Context context, final List<String> permissions) {
        List<String> permissionNames = Permission.transformText(context, permissions);
        String message = getResources().getString(R.string.string_get_permission) + "\n" + permissionNames;
//                context.getString("Please give us permission in the settings:\\n\\n%1$s", TextUtils.join("\n", permissionNames));

        new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle(getResources().getString(R.string.prompt))
                .setMessage(message)
                .setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setPermission();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancle), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    /**
     * Set permissions.
     */
    private void setPermission() {
        AndPermission.with(this)
                .runtime()
                .setting()
                .onComeback(new Setting.Action() {
                    @Override
                    public void onAction() {
                        //Toast.makeText(MyApp.getContext(),"用户从设置页面返回。", Toast.LENGTH_SHORT).show();
                    }
                })
                .start();
    }

}
