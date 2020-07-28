package com.guider.healthring.b30;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.guider.health.apilib.BuildConfig;
import com.guider.healthring.Commont;
import com.guider.healthring.MyApp;
import com.guider.healthring.R;
import com.guider.healthring.b30.bean.BaseResultVoNew;
import com.guider.healthring.b30.view.B30DeviceAlarmActivity;
import com.guider.healthring.b31.B31SwitchActivity;
import com.guider.healthring.bleutil.MyCommandManager;
import com.guider.healthring.siswatch.NewSearchActivity;
import com.guider.healthring.siswatch.WatchBaseActivity;
import com.guider.healthring.siswatch.utils.WatchUtils;
import com.guider.healthring.util.LocalizeTool;
import com.guider.healthring.util.OkHttpTool;
import com.guider.healthring.util.SharedPreferencesUtils;
import com.guider.healthring.w30s.carema.W30sCameraActivity;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IBPSettingDataListener;
import com.veepoo.protocol.listener.data.ILongSeatDataListener;
import com.veepoo.protocol.listener.data.INightTurnWristeDataListener;
import com.veepoo.protocol.model.datas.BpSettingData;
import com.veepoo.protocol.model.datas.LongSeatData;
import com.veepoo.protocol.model.datas.NightTurnWristeData;
import com.veepoo.protocol.model.enums.EBPDetectModel;
import com.veepoo.protocol.model.settings.BpSetting;
import com.veepoo.protocol.model.settings.LongSeatSetting;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.Setting;
import java.util.List;


/**
 * Created by Administrator on 2018/8/4.
 */

/**
 * 设备页面
 */
public class B30DeviceActivity extends WatchBaseActivity
        implements Rationale<List<String>> ,View.OnClickListener{

    private static final String TAG = "B30DeviceActivity";

    ImageView commentB30BackImg;
    TextView commentB30TitleTv;
    ToggleButton longSitToggleBtn;
    ToggleButton turnWristToggleBtn;
    ToggleButton privateBloadToggleBtn;
    RelativeLayout b30DeviceStyleRelR;
    RelativeLayout b30DevicePrivateBloadRelR;


    View view_daojishi;
    RelativeLayout b31DeviceCounDownRel;
    View view_sty;
    View view_bp;

    /**
     * 私人血压数据
     */
    private BpSettingData bpData;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b30_device_layout);
        initViewIds();

        initViews();

        initData();

    }

    private void initViewIds() {
        commentB30BackImg = findViewById(R.id.commentB30BackImg);
        commentB30TitleTv = findViewById(R.id.commentB30TitleTv);
        longSitToggleBtn = findViewById(R.id.longSitToggleBtn);
        turnWristToggleBtn = findViewById(R.id.turnWristToggleBtn);
        privateBloadToggleBtn = findViewById(R.id.privateBloadToggleBtn);
        b30DeviceStyleRelR = findViewById(R.id.b30DeviceStyleRel);
        b30DevicePrivateBloadRelR = findViewById(R.id.b30DevicePrivateBloadRel);
        view_daojishi = findViewById(R.id.view_daojishi);
        b31DeviceCounDownRel = findViewById(R.id.b31DeviceCounDownRel);
        view_sty = findViewById(R.id.view_sty);
        view_bp = findViewById(R.id.view_bp);
        commentB30BackImg.setOnClickListener(this);
        findViewById(R.id.b30DeviceMsgRel).setOnClickListener(this);
        findViewById(R.id.b30DeviceAlarmRel).setOnClickListener(this);
        findViewById(R.id.b30DeviceLongSitRel).setOnClickListener(this);
        findViewById(R.id.b30DeviceWristRel).setOnClickListener(this);
        findViewById(R.id.b30DevicePrivateBloadRel).setOnClickListener(this);
        findViewById(R.id.b30DeviceSwitchRel).setOnClickListener(this);
        findViewById(R.id.b30DevicePtoRel).setOnClickListener(this);
        findViewById(R.id.b30DeviceResetRel).setOnClickListener(this);
        findViewById(R.id.b30DeviceStyleRel).setOnClickListener(this);
        findViewById(R.id.b30DevicedfuRel).setOnClickListener(this);
        findViewById(R.id.b30DeviceClearDataRel).setOnClickListener(this);
        findViewById(R.id.b30DisConnBtn).setOnClickListener(this);
        findViewById(R.id.b31DeviceCounDownRel).setOnClickListener(this);

    }

    private void initData() {
        //读取是否转腕亮屏
        if (MyCommandManager.DEVICENAME != null) {
//            //读取久坐提醒
//            MyApp.getVpOperateManager().readLongSeat(iBleWriteResponse, new ILongSeatDataListener() {
//                @Override
//                public void onLongSeatDataChange(LongSeatData longSeatData) {
//                    if (Commont.isDebug)Log.e(TAG,"----久坐提醒="+longSeatData.toString());
//                    longSitToggleBtn.setChecked(longSeatData.isOpen());
//                }
//            });
            //读取转腕亮屏
//            MyApp.getVpOperateManager().readNightTurnWriste(iBleWriteResponse, new INightTurnWristeDataListener() {
//                @Override
//                public void onNightTurnWristeDataChange(NightTurnWristeData nightTurnWristeData) {
//                    if (Commont.isDebug)Log.e(TAG,"----转腕亮屏="+nightTurnWristeData.toString());
//                    turnWristToggleBtn.setChecked(nightTurnWristeData.isNightTureWirsteStatusOpen());
//                }
//            });


            if (Commont.isDebug)Log.e(TAG, "这个是那个设备？？？   " + MyCommandManager.DEVICENAME);
            if (MyCommandManager.DEVICENAME.equals("Ringmii") || MyCommandManager.DEVICENAME.equals("B30")) {
                b30DeviceStyleRelR.setVisibility(View.GONE);
                view_sty.setVisibility(View.GONE);
                view_daojishi.setVisibility(View.GONE);
                b31DeviceCounDownRel.setVisibility(View.GONE);
            } else {
                if (!WatchUtils.isEmpty(MyCommandManager.DEVICENAME)
                        && MyCommandManager.DEVICENAME.equals("B31")) {
                    b30DevicePrivateBloadRelR.setVisibility(View.GONE);
                    view_bp.setVisibility(View.GONE);
                }
                b30DeviceStyleRelR.setVisibility(View.VISIBLE);
                view_sty.setVisibility(View.VISIBLE);
            }

            if (WatchUtils.isEmpty(MyCommandManager.DEVICENAME)
                    || MyCommandManager.DEVICENAME.equals("B31")) {
                return;
            }
            MyApp.getInstance().getVpOperateManager().readDetectBP(iBleWriteResponse, new IBPSettingDataListener() {
                @Override
                public void onDataChange(BpSettingData bpSettingData) {
                    readDetectBp(bpSettingData);
                }
            });
        }


    }

    private void initViews() {
        commentB30TitleTv.setText(getResources().getString(R.string.device));
        commentB30BackImg.setVisibility(View.VISIBLE);

        longSitToggleBtn.setOnCheckedChangeListener(new ToggleClickListener());
        turnWristToggleBtn.setOnCheckedChangeListener(new ToggleClickListener());
        privateBloadToggleBtn.setOnCheckedChangeListener(new ToggleClickListener());
    }

    /**
     * 读取手环私人血压模式是否打开
     */
    private void readDetectBp(BpSettingData bpSettingData) {
        bpData = bpSettingData;
        boolean privateBlood = bpSettingData.getModel() == EBPDetectModel.DETECT_MODEL_PRIVATE;
        if (Commont.isDebug)Log.e(TAG, "----私人血压模式= " + MyCommandManager.DEVICENAME
                + " == " + bpData
                + " == " + privateBlood);
        privateBloadToggleBtn.setChecked(privateBlood);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:    //返回
                finish();
                break;
            case R.id.b30DeviceMsgRel:  //消息提醒
                startActivity(B30MessAlertActivity.class);
                break;
            case R.id.b30DeviceAlarmRel:    //闹钟设置
                startActivity(B30DeviceAlarmActivity.class);
                break;
            case R.id.b30DeviceLongSitRel:  //久坐提醒
                startActivity(B30LongSitSetActivity.class);
                break;
            case R.id.b30DeviceWristRel:    //翻腕亮屏
                startActivity(B30TrunWristSetActivity.class);
                break;
            case R.id.b30DevicePrivateBloadRel: //血压私人模式
                startActivity(PrivateBloadActivity.class);
                break;
            case R.id.b30DeviceSwitchRel:   //开关设置
                /**
                 * 这里判断类型  B30  ?   B31
                 */
                if (Commont.isDebug)Log.e(TAG, "这个是那个设备？？？   " + MyCommandManager.DEVICENAME);
                if (MyCommandManager.DEVICENAME != null) {
                    if (MyCommandManager.DEVICENAME.equals("Ringmii") || MyCommandManager.DEVICENAME.equals("B30")) {
                        startActivity(B30SwitchSetActivity.class);
                    } else {
                        startActivity(B31SwitchActivity.class);
                    }
                }


                break;
            case R.id.b30DevicePtoRel:  //拍照

                AndPermission.with(this)
                        .runtime()
                        .permission(Permission.Group.CAMERA)
                        .rationale(this)//添加拒绝权限回调
                        .onGranted(new Action<List<String>>() {
                            @Override
                            public void onAction(List<String> data) {
                                // data.get(0);
//                                startActivity(CameraActivity.class);
                                startActivity(W30sCameraActivity.class);
//                                startActivity(new Intent(getActivity(),W30sCameraActivity.class));
                            }
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
                                    showSettingDialog(MyApp.getContext(), data);
                                }
                            }
                        }).start();

//                if (AndPermission.hasPermissions(B30DeviceActivity.this, Manifest.permission.CAMERA)) {
//                    startActivity(W30sCameraActivity.class);
//                    if (Commont.isDebug)Log.d("-----权限已打开-1--","拍照要权限");
//                } else {
//                    if (Commont.isDebug)Log.d("-----权限未打开---","拍照要权限");
//                    AndPermission.with(B30DeviceActivity.this)
//                            .runtime()
//                            .permission(Manifest.permission.CAMERA)
//                            .onGranted(new Action<List<String>>() {
//                                @Override
//                                public void onAction(List<String> data) {
//                                    if (Commont.isDebug)Log.d("-----权限已打开-2--","拍照要权限");
//                                    startActivity(W30sCameraActivity.class);
//                                }
//                            })
//                            .rationale(new Rationale<List<String>>() {
//                                @Override
//                                public void showRationale(Context context, List<String> data, RequestExecutor executor) {
//                                    if (Commont.isDebug)Log.d("-----权限未打开-2--","拍照要权限");
//                                }
//                            }).start();
//                }

                break;
//            case R.id.b30DeviceCounDownRel: //倒计时
//                startActivity(B30CounDownActivity.class);
//                break;
            case R.id.b30DeviceResetRel:    //重置设备密码
                startActivity(B30ResetActivity.class);
                break;
            case R.id.b30DeviceStyleRel:    //主题风格
                startActivity(B30ScreenStyleActivity.class);
                break;
            case R.id.b30DevicedfuRel:  //固件升级
                startActivity(B30DufActivity.class);
                break;
            case R.id.b30DeviceClearDataRel:    //清除数据
                new MaterialDialog.Builder(this)
                        .title(getResources().getString(R.string.prompt))
                        .content(getResources().getString(R.string.string_is_clear_data))
                        .positiveText(getResources().getString(R.string.confirm))
                        .negativeText(getResources().getString(R.string.cancle))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                MyApp.getInstance().getVpOperateManager().clearDeviceData(iBleWriteResponse);
                            }
                        }).show();
                break;
            case R.id.b30DisConnBtn:    //断开连接
                disB30Conn();
                break;
            case R.id.b31DeviceCounDownRel://倒计时
                startActivity(B30CounDownActivity.class);
                break;
        }
    }


    /**
     * 解绑设备 http://47.92.218.150:8082/
     */
    private final String Base_Url = BuildConfig.APIURL + "api/v1/";
    void unbindDevices() {
        try {
            long accountIdGD = (long) SharedPreferencesUtils.getParam(MyApp.getInstance(), "accountIdGD", 0L);
            if (accountIdGD == 0L) return;
            String deviceCode = (String) SharedPreferencesUtils.readObject(MyApp.getInstance(), Commont.BLEMAC);
            String upStepPatch = Base_Url + "user/" + accountIdGD + "/device/unbind?deviceCode=" + deviceCode;
            //{accountId}/deviceandcompany/bind?deviceCode=
            OkHttpTool.getInstance().doDelete(upStepPatch, "", this, new OkHttpTool.HttpResult() {
                @Override
                public void onResult(String result) {
                    if (!WatchUtils.isEmpty(result)) {
                        BaseResultVoNew baseResultVoNew = new Gson().fromJson(result, BaseResultVoNew.class);
                        if (baseResultVoNew.getCode() == 0 || baseResultVoNew.getMsg().equals("您已经绑定该设备")) {
                            if (Commont.isDebug)Log.e(TAG, "=======解绑该设备= " + result);
                        }
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    //断开连接
    private void disB30Conn() {
        new MaterialDialog.Builder(this)
                .title(getResources().getString(R.string.prompt))
                .content(getResources().getString(R.string.string_devices_is_disconnected))
                .positiveText(getResources().getString(R.string.confirm))
                .negativeText(getResources().getString(R.string.cancle))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        unbindDevices();

                        if (MyCommandManager.DEVICENAME != null) {
//                            MyCommandManager.DEVICENAME = null;
//                            MyCommandManager.ADDRESS = null;
//                            MyApp.getVpOperateManager().disconnectWatch(new IBleWriteResponse() {
//                                @Override
//                                public void onResponse(int state) {
//                                    if (Commont.isDebug)Log.e(TAG, "----state=" + state);
//                                    if (state == -1) {
//                                        SharedPreferencesUtils.saveObject(B30DeviceActivity.this, "mylanya", "");
//                                        SharedPreferencesUtils.saveObject(B30DeviceActivity.this, Commont.BLEMAC, "");
//                                        SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.DEVICESCODE, "0000");
//                                        MyApp.getInstance().setMacAddress(null);// 清空全局
//                                        startActivity(NewSearchActivity.class);
//                                        finish();
//                                        return;
//                                    }
//                                }
//                            });
                            MyApp.getInstance().getVpOperateManager().disconnectWatch(new IBleWriteResponse() {
                                @Override
                                public void onResponse(int state) {
                                    if (Commont.isDebug)Log.e(TAG, "----state=" + state);
                                }
                            });
                        }
//                        else {
//                            MyCommandManager.DEVICENAME = null;
//                            MyCommandManager.ADDRESS = null;
//                            SharedPreferencesUtils.saveObject(B30DeviceActivity.this, "mylanya", "");
//                            SharedPreferencesUtils.saveObject(B30DeviceActivity.this, Commont.BLEMAC, "");
//                            SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.DEVICESCODE, "0000");
//                            MyApp.getInstance().setMacAddress(null);// 清空全局
//                            startActivity(NewSearchActivity.class);
//                            finish();
//                        }
////                        if (BemoServiceConnection.getInstance()!=null){
////                           unbindService(BemoServiceConnection.getInstance());
////                        }
//                        SharedPreferences_status.save_IMEI(MyApplication.context, "");
//                        SharedPreferencesUtils.setParam(B30DeviceActivity.this, "personContent", "");
//                        SharedPreferencesUtils.setParam(B30DeviceActivity.this, "personOne", "");
//                        SharedPreferencesUtils.setParam(B30DeviceActivity.this, "personTwo", "");
//                        SharedPreferencesUtils.setParam(B30DeviceActivity.this, "personThree", "");
//                        SharedPreferencesUtils.setParam(B30DeviceActivity.this, "NameOne", "");
//                        SharedPreferencesUtils.setParam(B30DeviceActivity.this, "NameTwo", "");
//                        SharedPreferencesUtils.setParam(B30DeviceActivity.this, "NameThree", "");


                        MyCommandManager.DEVICENAME = null;
                        MyCommandManager.ADDRESS = null;
                        SharedPreferencesUtils.saveObject(MyApp.getContext(), "mylanya", "");
                        SharedPreferencesUtils.saveObject(MyApp.getContext(), Commont.BLEMAC, "");
                        MyApp.getInstance().setMacAddress(null);// 清空全局的
                        SharedPreferencesUtils.saveObject(MyApp.getContext(), "userId", null);
                        SharedPreferencesUtils.saveObject(MyApp.getContext(), "userInfo", "");
                        SharedPreferencesUtils.setParam(MyApp.getContext(), "isFirst", "");
                        SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.DEVICESCODE, "0000");
                        //                        if (BemoServiceConnection.getInstance()!=null){
//                            B30DeviceActivity.this.unbindService(BemoServiceConnection.getInstance());
//                        }
                        //SharedPreferences_status.save_IMEI(MyApplication.context, "");
                        SharedPreferencesUtils.setParam(B30DeviceActivity.this, Commont.BATTERNUMBER, 0);//电池
                        SharedPreferencesUtils.setParam(B30DeviceActivity.this, "personContent", "");//SOS 内容
                        SharedPreferencesUtils.setParam(B30DeviceActivity.this, "personOne", "");//SOS 联系人  名字 1
                        SharedPreferencesUtils.setParam(B30DeviceActivity.this, "personTwo", "");//SOS 联系人  名字 2
                        SharedPreferencesUtils.setParam(B30DeviceActivity.this, "personThree", "");//SOS 联系人 名字  3
                        SharedPreferencesUtils.setParam(B30DeviceActivity.this, "NameOne", "");//SOS 联系人 电话  1
                        SharedPreferencesUtils.setParam(B30DeviceActivity.this, "NameTwo", "");//SOS 联系人 电话  2
                        SharedPreferencesUtils.setParam(B30DeviceActivity.this, "NameThree", "");//SOS 联系人 电话  3
                        new LocalizeTool(MyApp.getContext()).putUpdateDate(WatchUtils
                                .obtainFormatDate(1));// 同时把数据更新时间清楚更新最后更新数据的时间
                        startActivity(NewSearchActivity.class);
                        finish();

                    }
                }).show();
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


    //开关按钮点击监听
    private class ToggleClickListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (Commont.isDebug)Log.e("===onCheckedChanged===", buttonView.isPressed() + "");
            if (!buttonView.isPressed())
                return;
            if (MyCommandManager.DEVICENAME != null) {
                switch (buttonView.getId()) {
                    case R.id.longSitToggleBtn: //久坐设置
                        setLongSit(isChecked);
                        break;
                    case R.id.turnWristToggleBtn:   //转腕亮屏
                        setNightTurnWriste(isChecked);
                        break;
                    case R.id.privateBloadToggleBtn:    // 血压私人模式
                        int high = bpData == null ? 120 : bpData.getHighPressure();// 判断一下,防空
                        int low = bpData == null ? 80 : bpData.getLowPressure();// 给个默认值
                        BpSetting bpSetting = new BpSetting(isChecked, high, low);
                        MyApp.getInstance().getVpOperateManager().settingDetectBP(iBleWriteResponse, new IBPSettingDataListener() {
                            @Override
                            public void onDataChange(BpSettingData bpSettingData) {
                                readDetectBp(bpSettingData);
                            }
                        }, bpSetting);

                        break;

                }
            }

        }
    }

    //设置转腕亮屏
    private void setNightTurnWriste(boolean isOn) {
        if (MyCommandManager.DEVICENAME != null) {
            MyApp.getInstance().getVpOperateManager().settingNightTurnWriste(iBleWriteResponse, new INightTurnWristeDataListener() {
                @Override
                public void onNightTurnWristeDataChange(NightTurnWristeData nightTurnWristeData) {

                }
            }, isOn);
        }
    }

    //设置久坐提醒
    private void setLongSit(boolean isOpen) {
//        if (Commont.isDebug)Log.e(TAG, "-----isOpen=" + isOpen);
        MyApp.getInstance().getVpOperateManager().settingLongSeat(iBleWriteResponse,
                new LongSeatSetting(8, 0, 19, 0, 60, isOpen),
                new ILongSeatDataListener() {
                    @Override
                    public void onLongSeatDataChange(LongSeatData longSeatData) {
//                        if (Commont.isDebug)Log.d(TAG, "-----设置久坐=" + longSeatData.toString());
                    }
                });
    }

    private IBleWriteResponse iBleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int i) {

        }
    };

}
