package com.guider.healthring.B18I.b18isystemic;

import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.guider.healthring.B18I.b18imonitor.B18iResultCallBack;
import com.guider.healthring.B18I.evententity.B18iEventBus;
import com.guider.healthring.MyApp;
import com.guider.healthring.R;
import com.guider.healthring.bleutil.MyCommandManager;
import com.guider.healthring.h9.settingactivity.BloodPressureActivity;
import com.guider.healthring.h9.settingactivity.CorrectionTimeActivity;
import com.guider.healthring.h9.settingactivity.IsUnitActivity;
import com.guider.healthring.siswatch.NewSearchActivity;
import com.guider.healthring.siswatch.WatchBaseActivity;
import com.guider.healthring.util.SharedPreferencesUtils;
import com.sdk.bluetooth.config.BluetoothConfig;
import com.sdk.bluetooth.manage.AppsBluetoothManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import cn.appscomm.bluetooth.app.BluetoothSDK;

/**
 * @aboutContent: 功能设置界面
 * @author： 安
 * @crateTime: 2017/9/5 16:29
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */
public class B18ISettingActivity extends WatchBaseActivity implements View.OnClickListener {
    private final String TAG = "----->>>" + this.getClass();
    ImageView imageBack;
    TextView barTitles;
    LinearLayout setNotifi;
    LinearLayout setHeart;
    LinearLayout setShock;
    LinearLayout setClock;
    LinearLayout setTimeType;
    LinearLayout setSettings;
    LinearLayout setBlood;
    View bloodView;
    LinearLayout setUnit;
    View unitView;
    LinearLayout setAdjust;
    View adjustView;
    LinearLayout targetSetting;
    LinearLayout setUnbind;
    String bluName;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1001:
                    handler.removeMessages(1001);
                    closeLoadingDialog();
                    SharedPreferencesUtils.saveObject(MyApp.getContext(), "mylanya", "");
                    SharedPreferencesUtils.saveObject(MyApp.getContext(), "mylanyamac", "");
                    MyCommandManager.DEVICENAME = null;
                    startActivity(NewSearchActivity.class);
                    finish();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b18i_setting_layout);
        initViewIds();
        bluName = (String) SharedPreferencesUtils.readObject(B18ISettingActivity.this, "mylanya");
        imageBack.setOnClickListener(this);
        barTitles.setText(getResources().getString(R.string.function_str));
        whichDevice();//判断是B18i还是H9
        setNotifi.setOnClickListener(this);
        setHeart.setOnClickListener(this);
        setShock.setOnClickListener(this);
        setClock.setOnClickListener(this);
        setTimeType.setOnClickListener(this);
        setSettings.setOnClickListener(this);
        setBlood.setOnClickListener(this);
        setUnit.setOnClickListener(this);
        setAdjust.setOnClickListener(this);
        targetSetting.setOnClickListener(this);
        setUnbind.setOnClickListener(this);
    }

    private void initViewIds() {
        imageBack = findViewById(R.id.image_back);
        barTitles = findViewById(R.id.bar_titles);
        setNotifi = findViewById(R.id.set_notifi);
        setHeart = findViewById(R.id.set_heart);
        setShock = findViewById(R.id.set_shock);
        setClock = findViewById(R.id.set_clock);
        setTimeType = findViewById(R.id.set_timeType);
        setSettings = findViewById(R.id.set_settings);
        setBlood = findViewById(R.id.set_blood);
        bloodView = findViewById(R.id.blood_view);
        setUnit = findViewById(R.id.set_unit);
        unitView = findViewById(R.id.unit_view);
        setAdjust = findViewById(R.id.set_adjust);
        adjustView = findViewById(R.id.adjust_view);
        targetSetting = findViewById(R.id.targetSetting);
        setUnbind = findViewById(R.id.set_unbind);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onB18iEventBus(B18iEventBus event) {
        switch (event.getName()) {
            case "STATE_ON":
                startActivity(NewSearchActivity.class);
                finish();
                break;
            case "STATE_TURNING_ON":
                break;
            case "STATE_OFF":
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                enableBtIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(enableBtIntent);
                break;
            case "STATE_TURNING_OFF":
                Toast.makeText(this, getResources().getString(R.string.bluetooth_disconnected), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        whichDevice();//判断是B18i还是H9
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private String is18i;

    //判断是B18i还是H9
    private void whichDevice() {
        is18i = getIntent().getStringExtra("is18i");
        if (TextUtils.isEmpty(is18i)) finish();
        //在这里分别请求数据
        switch (is18i){
            case "B18i":
                setBlood.setVisibility(View.GONE);
                bloodView.setVisibility(View.GONE);
                setAdjust.setVisibility(View.GONE);
                adjustView.setVisibility(View.GONE);
                break;
            case "H9":
                setBlood.setVisibility(View.GONE);
                bloodView.setVisibility(View.GONE);
                break;
            case "B15P":
                setBlood.setVisibility(View.GONE);
                bloodView.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.image_back:   //返回
                finish();
                break;
            case R.id.set_notifi://通知
                //加入提示框
                startActivity(B18IIntelligentReminderActivity.class, new String[]{"is18i"}, new String[]{is18i});
                break;
            case R.id.set_heart://心率
                //************************************************
                startActivity(new Intent(B18ISettingActivity.this, HeartRateActivity.class).putExtra("is18i", is18i));
                break;
            //H9有血压，38i无血压
            case R.id.set_blood:
                if (!is18i.equals("is18i")) {
                    startActivity(new Intent(B18ISettingActivity.this, BloodPressureActivity.class));//跳转到血压
                }
                break;
            //单位：公制、英制
            case R.id.set_unit:
                startActivity(new Intent(B18ISettingActivity.this, IsUnitActivity.class).putExtra("is18i", is18i));//跳转到单位
                break;
            case R.id.set_shock://震动
                //************************************************
                startActivity(new Intent(B18ISettingActivity.this, ShockActivity.class).putExtra("is18i", is18i));
                break;
            case R.id.set_clock://闹钟
                //************************************************
                startActivity(new Intent(B18ISettingActivity.this, AlarmClockRemindActivity.class).putExtra("is18i", is18i));
                break;
            case R.id.set_timeType://时间格式
                //************************************************
                startActivity(new Intent(B18ISettingActivity.this, TimeFormatActivity.class).putExtra("is18i", is18i));
                break;
            case R.id.set_settings://高级设置
                //************************************************
                startActivity(new Intent(B18ISettingActivity.this, AdvancedSettingsActivity.class).putExtra("is18i", is18i));
                break;
            case R.id.set_adjust://校时
                startActivity(new Intent(B18ISettingActivity.this, CorrectionTimeActivity.class));
                break;
            case R.id.targetSetting://目标
                startActivity(new Intent(B18ISettingActivity.this, B18ITargetSettingActivity.class).putExtra("is18i", is18i));
                break;
            case R.id.set_unbind://解绑
                switch (bluName){
                    case "B18i":
                        new MaterialDialog.Builder(this)
                                .title(getResources().getString(R.string.prompt))
                                .content(getResources().getString(R.string.confirm_unbind_strap))
                                .positiveText(getResources().getString(R.string.confirm))
                                .negativeText(getResources().getString(R.string.cancle))
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        BluetoothSDK.disConnect(B18iResultCallBack.getB18iResultCallBack());
                                    }
                                }).show();
                        break;
                    case "W06X":
                        new MaterialDialog.Builder(this)
                                .title(getResources().getString(R.string.prompt))
                                .content(getResources().getString(R.string.confirm_unbind_strap))
                                .positiveText(getResources().getString(R.string.confirm))
                                .negativeText(getResources().getString(R.string.cancle))
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        Log.d("--SDK中的--mac---", BluetoothConfig.getDefaultMac(MyApp.getContext()));
                                        BluetoothConfig.setDefaultMac(MyApp.getContext(), "");
                                        String sss = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), "mylanyamac");
                                        Log.d("--SDK中的--mac--111111-", BluetoothConfig.getDefaultMac(MyApp.getContext()));
                                        AppsBluetoothManager.getInstance(MyApp.getContext()).doUnbindDevice(sss);
                                        AppsBluetoothManager.getInstance(MyApp.getContext()).clearBluetoothManagerDeviceConnectListeners();
                                        BluetoothConfig.setDefaultMac(MyApp.getContext(), "");
                                        showLoadingDialog(getResources().getString(R.string.dlog));
                                        handler.sendEmptyMessageDelayed(1001, 2000);
//                                    SharedPreferencesUtils.saveObject(MyApp.getContext(),"mylanya","");
//                                    SharedPreferencesUtils.saveObject(MyApp.getContext(),"mylanyamac","");
//                            startActivity(SearchDeviceActivity.class);
//                            startActivity(NewSearchActivity.class);
//                            finish();
                                    }
                                }).show();
                        break;
//                    case "B15P":
//                        new MaterialDialog.Builder(this)
//                                .title(getResources().getString(R.string.prompt))
//                                .content(getResources().getString(R.string.confirm_unbind_strap))
//                                .positiveText(getResources().getString(R.string.confirm))
//                                .negativeText(getResources().getString(R.string.cancle))
//                                .onPositive(new MaterialDialog.SingleButtonCallback() {
//                                    @Override
//                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                        MyApp.getVpOperateManager().disconnectWatch(new IBleWriteResponse() {
//                                            @Override
//                                            public void onResponse(int i) {
//                                                if (i == -1){
//                                                    showLoadingDialog(getResources().getString(R.string.dlog));
//                                                    handler.sendEmptyMessageDelayed(1001, 1000);
//                                                }
//                                            }
//                                        });
//                                    }
//                                }).show();
//                        break;
                }

                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "BBB" + "------request----" + requestCode + "--" + resultCode);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 判断是否打开了通知监听使用权限
     *
     * @return
     */
    private boolean notificationListenerEnable() {
        String pkgName = getPackageName();
        final String flat =
                Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
