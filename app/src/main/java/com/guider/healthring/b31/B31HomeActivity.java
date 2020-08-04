package com.guider.healthring.b31;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.guider.healthring.BuildConfig;
import com.guider.healthring.Commont;
import com.guider.healthring.MyApp;
import com.guider.healthring.R;
import com.guider.healthring.activity.DeviceActivity;
import com.guider.healthring.activity.DeviceActivityGlu;
import com.guider.healthring.adpter.FragmentAdapter;
import com.guider.healthring.b30.b30minefragment.B30MineFragment;
import com.guider.healthring.b30.b30run.B36RunFragment;
import com.guider.healthring.b30.service.UpNewDataToGDServices;
import com.guider.healthring.b30.service.VerB30PwdListener;
import com.guider.healthring.b31.record.B31RecordFragment;
import com.guider.healthring.bleutil.MyCommandManager;
import com.guider.healthring.bzlmaps.sos.SendSMSBroadCast;
import com.guider.healthring.commdbserver.CommDataFragment;
import com.guider.healthring.siswatch.WatchBaseActivity;
import com.guider.healthring.siswatch.utils.WatchUtils;
import com.guider.healthring.util.SharedPreferencesUtils;
import com.guider.healthring.util.ToastUtil;
import com.guider.healthring.view.CusInputDialogView;
import com.guider.healthring.widget.BottomSelectView;
import com.guider.healthring.widget.NoScrollViewPager;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.Setting;
import java.util.ArrayList;
import java.util.List;

/**
 * B31的主activity
 * Created by Admin
 * Date 2018/12/17
 */
public class B31HomeActivity extends WatchBaseActivity implements  Rationale<List<String>> {

    private static final String TAG = "B31HomeActivity";

    NoScrollViewPager b31ViewPager;
    BottomSelectView b31BottomBar;


    private List<Fragment> fragmentList = new ArrayList<>();

    //列设备验证密码提示框
    private CusInputDialogView cusInputDialogView;
    private SendSMSBroadCast sendSMSBroadCast = null;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1001:
                    if (MyApp.getInstance().getB30ConnStateService() != null) {
                        String bm = (String) SharedPreferencesUtils.readObject(B31HomeActivity.this, Commont.BLEMAC);//设备mac
                        if (!WatchUtils.isEmpty(bm))
                            MyApp.getInstance().getB30ConnStateService().connectAutoConn(true);
                    }
                    break;
                case 0x01:
                    handler.removeMessages(0x01);
                    String stringpersonOne = (String) SharedPreferencesUtils.getParam(B31HomeActivity.this, "personOne", "");
                    String stringpersonTwo = (String) SharedPreferencesUtils.getParam(B31HomeActivity.this, "personTwo", "");
                    String stringpersonThree = (String) SharedPreferencesUtils.getParam(B31HomeActivity.this, "personThree", "");

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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b31_home);
        initViewIds();
        initViews();
        registerReceiver(broadcastReceiver, new IntentFilter(WatchUtils.CHANGEPASS));
        //过滤器
        IntentFilter mIntentFilter = new IntentFilter("com.guider.ringmiihx.bzlmaps.sos.SENDSMS");
        //创建广播接收者的对象
        sendSMSBroadCast = new SendSMSBroadCast();
        //注册广播接收者的对象
        this.registerReceiver(sendSMSBroadCast, mIntentFilter);
        MyApp.getInstance().getVpOperateManager().settingDeviceControlPhone(MyApp.phoneSosOrDisPhone);
    }

    private void initViewIds() {
        b31ViewPager = findViewById(R.id.b31View_pager);
        b31BottomBar = findViewById(R.id.b31BottomBar);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (MyCommandManager.DEVICENAME == null || MyCommandManager.ADDRESS == null) {    //未连接
            reconnectDevice();// 重新连接
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void initViews() {
        fragmentList.add(new B31RecordFragment());
        fragmentList.add(new CommDataFragment());
        fragmentList.add(new B36RunFragment());
        fragmentList.add(new B30MineFragment());//WatchMineFragment
        FragmentStatePagerAdapter fragmentPagerAdapter = new FragmentAdapter(getSupportFragmentManager(), fragmentList);
        if (b31ViewPager != null) {
            b31ViewPager.setAdapter(fragmentPagerAdapter);
            b31ViewPager.setOffscreenPageLimit(0);
        }

        if (BuildConfig.HEALTH != 0) {
            TextView tv = findViewById(R.id.t3_text);
            tv.setText(R.string.btn_health);
        }

        b31BottomBar.setOnTabSelectListener(new BottomSelectView.OnTabSelectListener() {
            @Override
            public void onTabSelected(int tabId) {
                switch (tabId) {
                    case R.id.t1: //首页
                        b31ViewPager.setCurrentItem(0, false);
                        break;
                    case R.id.t2: //数据
                        b31ViewPager.setCurrentItem(1, false);
                        break;
                    case R.id.t3:  //开跑
                        switch (BuildConfig.HEALTH) {
                            case 0 : // 运动
                                b31ViewPager.setCurrentItem(2, false);
                                break;
                            case 1: // 横板健康
                                long accountId = (long) SharedPreferencesUtils.getParam(MyApp.getContext(), "accountIdGD", 0L);
                                DeviceActivity.start(B31HomeActivity.this, (int) accountId);
                                break;
                            case 2: // 竖版无创
                                long accountIdV = (long) SharedPreferencesUtils.getParam(MyApp.getContext(), "accountIdGD", 0L);
                                DeviceActivityGlu.startGlu(B31HomeActivity.this, (int) accountIdV);
                                break;
                            default:
                                b31ViewPager.setCurrentItem(2, false);
                                break;
                        }
                    case R.id.t4:   //我的
                        b31ViewPager.setCurrentItem(3, false);
                        break;
                    case R.id.t5:   //开始测试
                        // TODO 跳转到设备选择列表
//                        long accountId = (long) SharedPreferencesUtils.getParam(MyApp.getContext(),"accountIdGD",0L);
//                        DeviceActivity.start(B31HomeActivity.this , (int) accountId);
                        break;
                }
            }
        });
    }


    /**
     * 重新连接设备
     */
    public void reconnectDevice() {
        if (MyCommandManager.ADDRESS == null) {    //未连接
            if (MyApp.getInstance().getB30ConnStateService() != null) {
                String bm = (String) SharedPreferencesUtils.readObject(B31HomeActivity.this, Commont.BLEMAC);//设备mac
                if (!WatchUtils.isEmpty(bm)) {
                    MyApp.getInstance().getB30ConnStateService().connectAutoConn(true);
                }
            } else {
                handler.sendEmptyMessageDelayed(1001, 3 * 1000);
            }
        }
    }


    /**
     * 启动上传数据的服务
     */
    public void startUploadDate() {
        Log.e(TAG,"----------开始上传B31的服务-----");
//        if (!MyApp.getInstance().isUploadDateGDNew()) {
//            //上传到新的额服务器
//            if (upDataToGDServicesNew != null && upDataToGDServicesNew.getStatus() == AsyncTask.Status.RUNNING) {
//                upDataToGDServicesNew.cancel(true); // 如果Task还在运行，则先取消它
//                upDataToGDServicesNew = null;
//                upDataToGDServicesNew = new UpNewDataToGDServices();
//            } else {
//                upDataToGDServicesNew = new UpNewDataToGDServices();
//            }
//            upDataToGDServicesNew.execute();
//        }
        if (upDataToGDServicesNew != null && upDataToGDServicesNew.getStatus() == AsyncTask.Status.RUNNING) {
            upDataToGDServicesNew.cancel(true); // 如果Task还在运行，则先取消它
            upDataToGDServicesNew = null;
            upDataToGDServicesNew = new UpNewDataToGDServices();
        } else {
            upDataToGDServicesNew = new UpNewDataToGDServices();
        }
        upDataToGDServicesNew.execute();
    }

    //UpDataToGDServices upDataToGDServices = null;
    UpNewDataToGDServices upDataToGDServicesNew = null;
//    UpHrvDataToGDServices upHrvDataToGDServices = null;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null)
            unregisterReceiver(broadcastReceiver);
        if (cusInputDialogView != null)
            cusInputDialogView.cancel();
        if (sendSMSBroadCast != null) unregisterReceiver(sendSMSBroadCast);
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


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!WatchUtils.isEmpty(action)) {
                if (action.equals(WatchUtils.CHANGEPASS)) {
                    showB30InputPwd();  //弹出输入密码的提示框
                }
            }

        }
    };

    //提示输入密码
    private void showB30InputPwd() {
        if (cusInputDialogView == null) {
            cusInputDialogView = new CusInputDialogView(B31HomeActivity.this);
        }
        cusInputDialogView.show();
        cusInputDialogView.setCancelable(false);
        cusInputDialogView.setCusInputDialogListener(new CusInputDialogView.CusInputDialogListener() {
            @Override
            public void cusDialogCancle() {
                cusInputDialogView.dismiss();
                //断开连接
                MyApp.getInstance().getVpOperateManager().disconnectWatch(new IBleWriteResponse() {
                    @Override
                    public void onResponse(int i) {

                    }
                });
                //刷新搜索界面
                //handler.sendEmptyMessage(777);
            }

            @Override
            public void cusDialogSureData(String data) {
                MyApp.getInstance().getB30ConnStateService().continuteConn(data, new VerB30PwdListener() {
                    @Override
                    public void verPwdFailed() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showShort(B31HomeActivity.this, getResources().getString(R.string.miamacuo));
                            }
                        });

                        //ToastUtil.showLong(B31HomeActivity.this, getResources().getString(R.string.miamacuo));
                    }

                    @Override
                    public void verPwdSucc() {
                        cusInputDialogView.dismiss();
                    }
                });
            }
        });

    }



    //获取Do not disturb权限,才可进行音量操作
    private void getDoNotDisturb(){
        NotificationManager notificationManager =
                (NotificationManager) MyApp.getInstance().
                        getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if(notificationManager == null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                && !notificationManager.isNotificationPolicyAccessGranted()) {

            Intent intent = new Intent(
                    android.provider.Settings
                            .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

            MyApp.getInstance().getApplicationContext().startActivity(intent);
        }

    }



    // GPSGoogleUtils instance;


    /**
     * 打电话
     *
     * @param tel
     */
    //点击事件调用的类
    protected void call(final String tel) {

        AndPermission.with(B31HomeActivity.this)
                .runtime()
                .permission(
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.READ_CALL_LOG,
                        Manifest.permission.USE_SIP
                )
                .rationale(this)
                .rationale(this)//添加拒绝权限回调
                .onGranted(data -> {
                    //Toast.makeText(B30HomeActivity.this,"SOS 执行了 拨打电话 "+tel,Toast.LENGTH_SHORT).show();
                    //直接拨打
//                        Log.d("GPS", "call:" + tel);
                    Uri uri = Uri.parse("tel:" + tel);
                    Intent intent = new Intent(Intent.ACTION_CALL, uri);
                    if (ActivityCompat.checkSelfPermission(B31HomeActivity.this,
                            Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    startActivity(intent);
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        /**
                         * 当用户没有允许该权限时，回调该方法
                         */
                        Toast.makeText(MyApp.getContext(), getString(R.string.string_no_permission), Toast.LENGTH_SHORT).show();
                        /**
                         * 判断用户是否点击了禁止后不再询问，AndPermission.hasAlwaysDeniedPermission(MainActivity.this, data)
                         */
                        if (AndPermission.hasAlwaysDeniedPermission(MyApp.getContext(), data)) {
                            //true，弹窗再次向用户索取权限
                            //showSettingDialog(B31HomeActivity.this, data);
                        }
                    }
                }).start();
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

    @Override
    public void showRationale(Context context, List<String> data, final RequestExecutor executor) {
        List<String> permissionNames = Permission.transformText(context, data);
        String message = getResources().getString(R.string.string_get_permission) + "\n" + permissionNames;

        new AlertDialog.Builder(context)
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


}
