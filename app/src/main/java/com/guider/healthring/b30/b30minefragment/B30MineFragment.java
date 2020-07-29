package com.guider.healthring.b30.b30minefragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.aigestudio.wheelpicker.widgets.ProfessionPick;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.guider.healthring.BuildConfig;
import com.guider.healthring.Commont;
import com.guider.healthring.MyApp;
import com.guider.healthring.R;
import com.guider.healthring.activity.LoginActivity;
import com.guider.healthring.activity.MyPersonalActivity;
import com.guider.healthring.b30.B30DeviceActivity;
import com.guider.healthring.b30.B30SysSettingActivity;
import com.guider.healthring.b30.bean.BaseResultVoNew;
import com.guider.healthring.b31.BemoSwitchActivity;
import com.guider.healthring.b31.bpoxy.ReadHRVSoDataForDevices;
import com.guider.healthring.bean.GuiderUserInfo;
import com.guider.healthring.bleutil.MyCommandManager;
import com.guider.healthring.siswatch.LazyFragment;
import com.guider.healthring.siswatch.NewSearchActivity;
import com.guider.healthring.siswatch.utils.WatchUtils;
import com.guider.healthring.util.LocalizeTool;
import com.guider.healthring.util.MaterialDialogUtil;
import com.guider.healthring.util.OkHttpTool;
import com.guider.healthring.util.SharedPreferencesUtils;
import com.guider.healthring.util.ToastUtil;
import com.guider.healthring.w30s.utils.httputils.RequestPressent;
import com.guider.healthring.w30s.utils.httputils.RequestView;
import com.guider.libbase.activity.WebviewActivity;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.ICustomSettingDataListener;
import com.veepoo.protocol.model.enums.EFunctionStatus;
import com.veepoo.protocol.model.settings.CustomSetting;
import com.veepoo.protocol.model.settings.CustomSettingData;

import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by Administrator on 2018/7/20.
 */

/**
 * B30 我的界面
 */
public class B30MineFragment extends LazyFragment implements RequestView,View.OnClickListener {
    private static final String TAG = "B30MineFragment";
    View b30MineView;

    ImageView b30UserImageHead;
    TextView b30UserNameTv;
    TextView commentB30TitleTv;
    TextView b30MineDeviceTv;
    /**
     * 公制|英制
     */
    TextView b30MineUnitTv;
    TextView b30MineSportGoalTv;
    TextView b30MineSleepGoalTv;

    private RequestPressent requestPressent;

    private AlertDialog.Builder builder;
    //运动目标
    ArrayList<String> sportGoalList;
    //睡眠目标
    ArrayList<String> sleepGoalList;

    /**
     * 本地化工具
     */
    private LocalizeTool mLocalTool;
    /**
     * 是否修改了个人中心的用户资料
     */
    private boolean updatePersonalInfo;

    private Gson gson = new Gson();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        b30MineView = inflater.inflate(R.layout.fragment_b30_mine_layout, container, false);
        initViewIds();
        initViews();

        initData();

        return b30MineView;
    }

    private void initViewIds() {
        b30UserImageHead = b30MineView.findViewById(R.id.b30userImageHead);
        b30UserNameTv = b30MineView.findViewById(R.id.b30UserNameTv);
        commentB30TitleTv = b30MineView.findViewById(R.id.commentB30TitleTv);
        b30MineDeviceTv = b30MineView.findViewById(R.id.b30MineDeviceTv);
        b30MineUnitTv = b30MineView.findViewById(R.id.b30MineUnitTv);
        b30MineSportGoalTv = b30MineView.findViewById(R.id.b30MineSportGoalTv);
        b30MineSleepGoalTv = b30MineView.findViewById(R.id.b30MineSleepGoalTv);
        b30UserImageHead.setOnClickListener(this);
        b30MineView.findViewById(R.id.b30MineDeviceRel).setOnClickListener(this);
        b30MineView.findViewById(R.id.b30MineSportRel).setOnClickListener(this);
        b30MineView.findViewById(R.id.b30MineSleepRel).setOnClickListener(this);
        b30MineView.findViewById(R.id.b30MineUnitRel).setOnClickListener(this);
        b30MineView.findViewById(R.id.b30MineAboutRel).setOnClickListener(this);
        b30MineView.findViewById(R.id.b30LogoutBtn).setOnClickListener(this);
        b30MineView.findViewById(R.id.bemoRel).setOnClickListener(this);
        b30MineView.findViewById(R.id.rl_family).setOnClickListener(this);
        b30MineView.findViewById(R.id.rl_sos).setOnClickListener(this);
    }

    private void initData() {
        if (getActivity() != null) {
            mLocalTool = new LocalizeTool(getActivity());
//            readCustomSetting();// 读取手环的自定义配置(是否打开公制)

            requestPressent = new RequestPressent();
            requestPressent.attach(this);


            sportGoalList = new ArrayList<>();
            sleepGoalList = new ArrayList<>();
            for (int i = 1000; i <= 64000; i += 1000) {
                sportGoalList.add(i + "");
            }

            for (int i = 1; i < 48; i++) {
                sleepGoalList.add(WatchUtils.mul(Double.valueOf(i), 0.5) + "");
            }

            //显示运动目标和睡眠目标
            int b30SportGoal = (int) SharedPreferencesUtils.getParam(getActivity(), "b30Goal", 0);
            b30MineSportGoalTv.setText(b30SportGoal + "");
            //睡眠
            String b30SleepGoal = (String) SharedPreferencesUtils.getParam(MyApp.getContext(), "b30SleepGoal", "");
            if (!WatchUtils.isEmpty(b30SleepGoal)) {
                b30MineSleepGoalTv.setText(b30SleepGoal + "");
            }
        }
    }

    //获取盖德的用户信息
    private void getGadiDeUserInfoData() {
        try {
            long accountId = (long) SharedPreferencesUtils.getParam(getContext(),"accountIdGD",0L);
            if(accountId == 0)
                return;
            // http://api.guiderhealth.com/
            String guiderUrl = BuildConfig.APIURL + "api/v1/userinfo?accountId="+accountId;
            if(requestPressent != null){
                requestPressent.getRequestJSONObjectGet(11,guiderUrl,getContext(),11);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private void initViews() {
        commentB30TitleTv.setText(getResources().getString(R.string.menu_settings));

        //非国际版隐藏家人选项
        if (!BuildConfig.GOOGLEPLAY) {
            b30MineView.findViewById(R.id.ll_family).setVisibility(View.GONE);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        if (isVisible) {
            Log.e(TAG, "onFragmentVisibleChange----------执行");
            readDevicesSwithStute();// 读取手环的自定义配置(是否打开公制)

            if (getActivity() != null && !getActivity().isFinishing()) {
                if (MyCommandManager.DEVICENAME != null) {
                    String bleMac = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), Commont.BLEMAC);
                    b30MineDeviceTv.setText(MyCommandManager.DEVICENAME + " " + bleMac);
                } else {
                    b30MineDeviceTv.setText(getResources().getString(R.string.string_not_coon));//"未连接"
                }
            }
            if (updatePersonalInfo) {
                updatePersonalInfo = false;
            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getGadiDeUserInfoData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (requestPressent != null) {
            requestPressent.detach();
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b30userImageHead: //头像
                startActivity(new Intent(getActivity(), MyPersonalActivity.class));
                break;
            case R.id.b30MineDeviceRel: //设备
                if (!getActivity().isFinishing()) {
                    if (MyCommandManager.DEVICENAME != null) {
                        startActivity(new Intent(getActivity(), B30DeviceActivity.class));
                    } else {
                        startActivity(new Intent(getActivity(), NewSearchActivity.class));
                        getActivity().finish();
                    }
                }

                break;
            case R.id.b30MineSportRel:  //运动目标
                if (!getActivity().isFinishing()) {
                    if (MyCommandManager.DEVICENAME != null) {
                        setSportGoal(); //设置运动目标
                    } else {
                        startActivity(new Intent(getActivity(), NewSearchActivity.class));
                        getActivity().finish();
                    }
                }
                break;
            case R.id.b30MineSleepRel:  //睡眠目标
                if (!getActivity().isFinishing()) {
                    if (MyCommandManager.DEVICENAME != null) {
                        setSleepGoal(); //设置睡眠目标
                    } else {
                        startActivity(new Intent(getActivity(), NewSearchActivity.class));
                        getActivity().finish();
                    }
                }
                break;
            case R.id.b30MineUnitRel:   //单位设置
                if (!getActivity().isFinishing()) {
                    if (MyCommandManager.DEVICENAME != null) {
                        showUnitDialog();
                    } else {
                        startActivity(new Intent(getActivity(), NewSearchActivity.class));
                        getActivity().finish();
                    }
                }
                break;
            case R.id.b30MineAboutRel:  //关于
                startActivity(new Intent(getActivity(), B30SysSettingActivity.class));
                break;
            case R.id.b30LogoutBtn: //退出登录
                longOutApp();
                break;
            case R.id.bemoRel:
                startActivity(new Intent(getActivity(),BemoSwitchActivity.class));
            case R.id.rl_family: // 我的家人 https://app.guiderhealth.com/
                startWebviewActivity(BuildConfig.WEBDOMAIN + "#/family");
                break;
            case R.id.rl_sos: // https://app.guiderhealth.com/
                startWebviewActivity(BuildConfig.WEBDOMAIN + "#/friend/sos");
                break;
        }
    }


    private void startWebviewActivity(String url) {
        long accountId = (long) SharedPreferencesUtils.getParam(getContext(), "accountIdGD", 0L);
        String token = (String) SharedPreferencesUtils.getParam(getContext(), "tokenGD", "1");
        Intent intent = new Intent(getContext(), WebviewActivity.class);
        intent.putExtra("url", String.format("%s?accountId=%d&token=%s", url, accountId, token));
        Log.i(TAG, "startWebviewActivity url :" + url);
        startActivity(intent);
    }


    //运动过量提醒 B31不支持
    EFunctionStatus isOpenSportRemain = EFunctionStatus.UNSUPPORT;
    //血压/心率播报 B31不支持
    EFunctionStatus isOpenVoiceBpHeart = EFunctionStatus.UNSUPPORT;
    //查找手表  B31不支持
    EFunctionStatus isOpenFindPhoneUI = EFunctionStatus.UNSUPPORT;
    //秒表功能  支持
    EFunctionStatus isOpenStopWatch;
    //低压报警 支持
    EFunctionStatus isOpenSpo2hLowRemind = EFunctionStatus.SUPPORT_OPEN;
    //肤色功能 支持
    EFunctionStatus isOpenWearDetectSkin = EFunctionStatus.SUPPORT_OPEN;

    //自动接听来电 不支持
    EFunctionStatus isOpenAutoInCall = EFunctionStatus.UNSUPPORT;
    //自动检测HRV 支持
    EFunctionStatus isOpenAutoHRV = EFunctionStatus.SUPPORT_OPEN;
    //断连提醒 支持
    EFunctionStatus isOpenDisconnectRemind;
    //SOS  不支持
    EFunctionStatus isOpenSOS = EFunctionStatus.UNSUPPORT;

    //开关设置
    private void setSwitchCheck() {
        if(MyCommandManager.DEVICENAME == null)
            return;
        showLoadingDialog("loading...");

        //保存的状态
        boolean isSystem = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISSystem, true);//是否为公制
        boolean is24Hour = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.IS24Hour, true);//是否为24小时制
        boolean isAutomaticHeart = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISAutoHeart, true);//自动测量心率
        boolean isAutomaticBoold = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISAutoBp, true);//自动测量血压
        boolean isSecondwatch = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISSecondwatch, false);//秒表
        boolean isWearCheck = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISWearcheck, true);//佩戴
        boolean isFindPhone = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISFindPhone, false);//查找手机
        boolean CallPhone = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISCallPhone, true);//来电
        boolean isDisconn = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISDisAlert, false);//断开连接提醒
        boolean isSos = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISHelpe, false);//sos


        //秒表功能
        if (isSecondwatch) {
            isOpenStopWatch = EFunctionStatus.SUPPORT_OPEN;
        } else {
            isOpenStopWatch = EFunctionStatus.SUPPORT_CLOSE;
        }
        //佩戴
        if (isWearCheck) {
            isOpenWearDetectSkin = EFunctionStatus.SUPPORT_OPEN;
        } else {
            isOpenWearDetectSkin = EFunctionStatus.SUPPORT_CLOSE;
        }
        //断连提醒
        if (isDisconn) {
            isOpenDisconnectRemind = EFunctionStatus.SUPPORT_OPEN;
        } else {
            isOpenDisconnectRemind = EFunctionStatus.SUPPORT_CLOSE;
        }

        Log.e(TAG, "----- SOSa啊 " + isSos);
        //SOS
        if (isSos) {
            isOpenSOS = EFunctionStatus.SUPPORT_OPEN;
        } else {
            isOpenSOS = EFunctionStatus.SUPPORT_CLOSE;
        }

        Log.i("bbbbbbbbo" , "B31MineFragment");
        CustomSetting customSetting = new CustomSetting(true, isSystem, is24Hour, isAutomaticHeart,
                isAutomaticBoold, isOpenSportRemain, isOpenVoiceBpHeart, isOpenFindPhoneUI, isOpenStopWatch, isOpenSpo2hLowRemind,
                isOpenWearDetectSkin, isOpenAutoInCall, isOpenAutoHRV, isOpenDisconnectRemind, isOpenSOS);
        Log.e(TAG, "-----新设置的值啊---customSetting=" + customSetting.toString());


        MyApp.getInstance().getVpOperateManager().changeCustomSetting(iBleWriteResponse, new ICustomSettingDataListener() {
            @Override
            public void OnSettingDataChange(CustomSettingData customSettingData) {
                closeLoadingDialog();
                Log.e(TAG, "--新设置的值结果--customSettingData=" + customSettingData.toString());

                if (getActivity() != null && !getActivity().isFinishing()) {
                    if (customSettingData.getMetricSystem() == EFunctionStatus.SUPPORT_OPEN) {
                        SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISSystem, true);//是否为公制
                        b30MineUnitTv.setText(R.string.setkm);
                        mLocalTool.putMetricSystem(true);
                    } else {
                        SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISSystem, false);//是否为公制
                        b30MineUnitTv.setText(R.string.setmi);
                        mLocalTool.putMetricSystem(false);
                    }

                    Intent intent = new Intent();
                    intent.setAction("com.example.bozhilun.android.siswatch.run.UNIT");
                    getActivity().sendBroadcast(intent);
                }
            }
        }, customSetting);


    }


    private IBleWriteResponse iBleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int i) {

        }
    };


    private void longOutApp() {
        MaterialDialogUtil.INSTANCE.showDialog(this, R.string.prompt,
                R.string.exit_login, R.string.confirm, R.string.cancle,
                () -> {
                    unbindDevices();
                    if (MyCommandManager.DEVICENAME != null) {
                        MyApp.getInstance().getVpOperateManager().disconnectWatch(new IBleWriteResponse() {
                            @Override
                            public void onResponse(int state) {
                                if (state == -1) {
                                    SharedPreferencesUtils.saveObject(MyApp.getContext(), "mylanya", "");
                                    SharedPreferencesUtils.saveObject(MyApp.getContext(), Commont.BLEMAC, "");
                                    MyApp.getInstance().setMacAddress(null);// 清空全局的
                                    SharedPreferencesUtils.saveObject(MyApp.getContext(), "userId", null);
                                    SharedPreferencesUtils.saveObject(MyApp.getContext(), "userInfo", "");
                                    SharedPreferencesUtils.setParam(MyApp.getContext(), "isFirst", "");
                                    SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.DEVICESCODE, "0000");
                                    startActivity(new Intent(getActivity(), LoginActivity.class));
                                    getActivity().finish();
                                }
                            }
                        });
                    }
                    else {
                        MyCommandManager.DEVICENAME = null;
                        MyCommandManager.ADDRESS = null;
                        SharedPreferencesUtils.saveObject(MyApp.getContext(), "mylanya", "");
                        SharedPreferencesUtils.saveObject(MyApp.getContext(), Commont.BLEMAC, "");
                        MyApp.getInstance().setMacAddress(null);// 清空全局的
                        SharedPreferencesUtils.saveObject(MyApp.getContext(), "userId", null);
                        SharedPreferencesUtils.saveObject(MyApp.getContext(), "userInfo", "");
                        SharedPreferencesUtils.setParam(MyApp.getContext(), "isFirst", "");
                        SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.DEVICESCODE, "0000");
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                    }

                    MyCommandManager.DEVICENAME = null;
                    MyCommandManager.ADDRESS = null;
                    SharedPreferencesUtils.saveObject(MyApp.getContext(), "mylanya", "");
                    SharedPreferencesUtils.saveObject(MyApp.getContext(), Commont.BLEMAC, "");
                    MyApp.getInstance().setMacAddress(null);// 清空全局的
                    SharedPreferencesUtils.saveObject(MyApp.getContext(), "userId", null);
                    SharedPreferencesUtils.saveObject(MyApp.getContext(), "userInfo", "");
                    SharedPreferencesUtils.setParam(MyApp.getContext(), "isFirst", "");
                    SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.DEVICESCODE, "0000");
                    //  startActivity(new Intent(getActivity(), LoginActivity.class));

//                        if (BemoServiceConnection.getInstance()!=null){
//                            getActivity().unbindService(BemoServiceConnection.getInstance());
//                        }
                    //SharedPreferences_status.save_IMEI(MyApplication.context, "");
                    SharedPreferencesUtils.setParam(getContext(), Commont.BATTERNUMBER, 0);//电池
                    SharedPreferencesUtils.setParam(getContext(), "personContent", "");//SOS 内容
                    SharedPreferencesUtils.setParam(getContext(), "personOne", "");//SOS 联系人  名字 1
                    SharedPreferencesUtils.setParam(getContext(), "personTwo", "");//SOS 联系人  名字 2
                    SharedPreferencesUtils.setParam(getContext(), "personThree", "");//SOS 联系人 名字  3
                    SharedPreferencesUtils.setParam(getContext(), "NameOne", "");//SOS 联系人 电话  1
                    SharedPreferencesUtils.setParam(getContext(), "NameTwo", "");//SOS 联系人 电话  2
                    SharedPreferencesUtils.setParam(getContext(), "NameThree", "");//SOS 联系人 电话  3
                    new LocalizeTool(MyApp.getContext()).putUpdateDate(WatchUtils
                            .obtainFormatDate(1));// 同时把数据更新时间清楚更新最后更新数据的时间
                    return null;
                }
        );
    }


    /**
     * 解绑设备 http://47.92.218.150:8082/
     */
    private final String Base_Url = BuildConfig.APIURL + "api/v1/";

    void unbindDevices() {
        Long l;
        Integer i;
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
                        Log.e(TAG, "=======解绑该设备= " + result);
                    }
                }
            }
        });
    }


    //设置运动目标
    private void setSportGoal() {
        ProfessionPick dailyumberofstepsPopWin = new ProfessionPick.Builder(getActivity(),
                new ProfessionPick.OnProCityPickedListener() {
                    @Override
                    public void onProCityPickCompleted(String profession) {
                        b30MineSportGoalTv.setText(profession);
                        SharedPreferencesUtils.setParam(getActivity(), "b30Goal", Integer.valueOf(profession.trim()));
                    }
                }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                .btnTextSize(16) // button text size
                .viewTextSize(25) // pick view text size
                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                .setProvinceList(sportGoalList) //min year in loop
                .dateChose("8000") // date chose when init popwindow
                .build();
        dailyumberofstepsPopWin.showPopWin(getActivity());
    }

    //设置睡眠目标
    private void setSleepGoal() {
        ProfessionPick dailyumberofstepsPopWin = new ProfessionPick.Builder(getActivity(),
                new ProfessionPick.OnProCityPickedListener() {
                    @Override
                    public void onProCityPickCompleted(String profession) {
                        b30MineSleepGoalTv.setText(profession);
                        SharedPreferencesUtils.setParam(getActivity(), "b30SleepGoal", profession.trim());
                    }
                }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                .btnTextSize(16) // button text size
                .viewTextSize(25) // pick view text size
                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                .setProvinceList(sleepGoalList) //min year in loop
                .dateChose("8.0") // date chose when init popwindow
                .build();
        dailyumberofstepsPopWin.showPopWin(getActivity());
    }


    //展示公英制
    private void showUnitDialog() {

        String runTypeString[] = new String[]{getResources().getString(R.string.setkm),
                getResources().getString(R.string.setmi)};
        builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.select_running_mode))
                .setItems(runTypeString, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        if (i == 0) {
                            SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISSystem, true);//是否为公制
                        } else {
                            SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISSystem, false);//是否为公制
                        }

                        setSwitchCheck();
//                        changeCustomSetting(i == 0);
                    }
                }).setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }

    /**
     * 读取设备开关
     */
    void readDevicesSwithStute() {
        Log.e(TAG, "HRV-----Spo同步状态 " + ReadHRVSoDataForDevices.isUpdataHRV() + " == " + ReadHRVSoDataForDevices.isUpdataO2O());
        if(MyCommandManager.DEVICENAME == null)
            return;
        if (!WatchUtils.isEmpty(MyCommandManager.DEVICENAME)
                && MyCommandManager.DEVICENAME.equals("500S")
                && (ReadHRVSoDataForDevices.isUpdataHRV() || ReadHRVSoDataForDevices.isUpdataO2O())) {
            ToastUtil.showShort(getContext(), "HRV 同步中");
            return;
        }
        MyApp.getInstance().getVpOperateManager().readCustomSetting(new IBleWriteResponse() {
            @Override
            public void onResponse(int i) {

            }
        }, new ICustomSettingDataListener() {
            @Override
            public void OnSettingDataChange(CustomSettingData customSettingData) {
//                    Log.e(TAG, "---自定义设置--=" + customSettingData.toString());
                //查找手机
                if (customSettingData.getFindPhoneUi() == EFunctionStatus.SUPPORT_OPEN) {
                    SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISFindPhone, true);
                } else {
                    SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISFindPhone, false);
                }
                //公英制
                if (customSettingData.getMetricSystem() == EFunctionStatus.SUPPORT_OPEN) {
                    SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISSystem, true);//是否为公制
                    b30MineUnitTv.setText(R.string.setkm);
                    mLocalTool.putMetricSystem(true);
                } else {
                    SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISSystem, false);//是否为公制
                    b30MineUnitTv.setText(R.string.setmi);
                    mLocalTool.putMetricSystem(false);
                }
                //秒表
                if (customSettingData.getSecondsWatch() == EFunctionStatus.SUPPORT_OPEN) {
                    SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISSecondwatch, true);
                } else {
                    SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISSecondwatch, false);
                }
                //读取心率自动检测功能是否开启
                if (customSettingData.getAutoHeartDetect() == EFunctionStatus.SUPPORT_OPEN) {
                    SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISAutoHeart, true);
                } else {
                    SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISAutoHeart, false);
                }
                //读取血压自动检测功能是否开启
                if (customSettingData.getAutoBpDetect() == EFunctionStatus.SUPPORT_OPEN) {
                    SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISAutoBp, true);
                } else {
                    SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISAutoBp, false);
                }
                //读取设备是否为24小时制
                if (customSettingData.isIs24Hour()) {
                    SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.IS24Hour, true);
                } else {
                    SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.IS24Hour, false);
                }
                //是否开启断连提醒
                if (customSettingData.getDisconnectRemind() == EFunctionStatus.SUPPORT_OPEN) {
                    SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISDisAlert, true);
                } else {
                    SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISDisAlert, false);
                }
                //判断是否开启SOS
                if (customSettingData.getSOS() == EFunctionStatus.SUPPORT_OPEN) {
                    SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISHelpe, true);
                } else {
                    SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISHelpe, false);
                }
                //佩戴检测
                if (customSettingData.getSkin() == EFunctionStatus.SUPPORT_OPEN) {
                    SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISWearcheck, true);
                } else {
                    SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISWearcheck, false);
                }

//                    if(customSettingData.getFindPhoneUi() == EFunctionStatus.SUPPORT_OPEN){//判断是否开启查找手机UI
//                        b30SwitchFindPhoneToggleBtn.setChecked(true);
//                    }else {
//                        b30SwitchFindPhoneToggleBtn.setChecked(false);
//                    }

            }
        });
    }

    @Override
    public void showLoadDialog(int what) {

    }

    @Override
    public void successData(int what, Object object, int daystag) {
        if(object == null)
            return;
        if(what == 11){
            showGaideUserInfo(object.toString());
        }
    }

    @Override
    public void failedData(int what, Throwable e) {

    }

    @Override
    public void closeLoadDialog(int what) {

    }

    private void showGaideUserInfo(String userStr){
        if(WatchUtils.isNetRequestSuccess(userStr,0)){
            try {
                JSONObject jsonObject = new JSONObject(userStr);
                String dataStr = jsonObject.getString("data");
                GuiderUserInfo guiderUserInfo = gson.fromJson(dataStr, GuiderUserInfo.class);
                if(guiderUserInfo == null)
                    return;

                //头像
                //mineLogoIv
                String hearUrl = guiderUserInfo.getHeadUrl();
                if(hearUrl != null){
                    RequestOptions mRequestOptions = RequestOptions.circleCropTransform().diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true);
                    Glide.with(this).load(hearUrl).apply(mRequestOptions).into(b30UserImageHead);//头像
                }
                // 用户名
                String name = guiderUserInfo.getName();
                b30UserNameTv.setText(name);

            }catch (Exception e){
                e.printStackTrace();
            }
        }


    }


}
