package com.guider.healthring.b30.service;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import com.guider.healthring.Commont;
import com.guider.healthring.MyApp;
import com.guider.healthring.b30.bean.B30HalfHourDB;
import com.guider.healthring.b30.bean.B30HalfHourDao;
import com.guider.healthring.bleutil.MyCommandManager;
import com.guider.healthring.commdbserver.CommCalUtils;
import com.guider.healthring.commdbserver.CommDBManager;
import com.guider.healthring.siswatch.utils.WatchUtils;
import com.guider.healthring.util.LocalizeTool;
import com.guider.healthring.util.SharedPreferencesUtils;
import com.google.gson.Gson;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IAlarm2DataListListener;
import com.veepoo.protocol.listener.data.IAllHealthDataListener;
import com.veepoo.protocol.listener.data.IBatteryDataListener;
import com.veepoo.protocol.listener.data.ICustomSettingDataListener;
import com.veepoo.protocol.listener.data.IDeviceFuctionDataListener;
import com.veepoo.protocol.listener.data.ILanguageDataListener;
import com.veepoo.protocol.listener.data.IPersonInfoDataListener;
import com.veepoo.protocol.listener.data.IPwdDataListener;
import com.veepoo.protocol.listener.data.ISocialMsgDataListener;
import com.veepoo.protocol.listener.data.ISportDataListener;
import com.veepoo.protocol.model.datas.AlarmData2;
import com.veepoo.protocol.model.datas.BatteryData;
import com.veepoo.protocol.model.datas.FunctionDeviceSupportData;
import com.veepoo.protocol.model.datas.FunctionSocailMsgData;
import com.veepoo.protocol.model.datas.HRVOriginData;
import com.veepoo.protocol.model.datas.HalfHourBpData;
import com.veepoo.protocol.model.datas.HalfHourRateData;
import com.veepoo.protocol.model.datas.HalfHourSportData;
import com.veepoo.protocol.model.datas.LanguageData;
import com.veepoo.protocol.model.datas.OriginData;
import com.veepoo.protocol.model.datas.OriginHalfHourData;
import com.veepoo.protocol.model.datas.PersonInfoData;
import com.veepoo.protocol.model.datas.PwdData;
import com.veepoo.protocol.model.datas.SleepData;
import com.veepoo.protocol.model.datas.SportData;
import com.veepoo.protocol.model.enums.EFunctionStatus;
import com.veepoo.protocol.model.enums.ELanguage;
import com.veepoo.protocol.model.enums.EOprateStauts;
import com.veepoo.protocol.model.enums.EPwdStatus;
import com.veepoo.protocol.model.settings.CustomSetting;
import com.veepoo.protocol.model.settings.CustomSettingData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Administrator on 2018/7/26.
 */

public class NewConnBleHelpService {

    private static final String TAG = "ConnBleHelpService";
    private int count;

    //验证密码
    private ConnBleHelpListener connBleHelpListener;

    //设备数据
    private ConnBleMsgDataListener connBleMsgDataListener;
    //HRV数据
    private ConnHRVBackDataListener connHRVBackDataListener;


    private List<Map<String, String>> listMap = new ArrayList<>();
    private HashMap<String, List<Map<String, String>>> countMap = new HashMap<>();


    //HRV返回的数据保存的集合
    private List<HRVOriginData> backHRVList = new ArrayList<>();

    //睡眠处理map
    private Map<String, SleepData> sleepMap = new HashMap<>();
    private List<SleepData> sleepDataList = new ArrayList<>();

    /**
     * 转换工具
     */
    private Gson gson = new Gson();

    private static volatile NewConnBleHelpService connBleHelpService;



    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (sleepMap != null && !sleepMap.isEmpty()) {
                for (Map.Entry<String, SleepData> mp : sleepMap.entrySet()) {
                    B30HalfHourDB db = new B30HalfHourDB();
                    db.setAddress(MyApp.getInstance().getMacAddress());
                    db.setDate(WatchUtils.obtainAroundDate(mp.getValue().getDate(), false));
                    db.setType(B30HalfHourDao.TYPE_SLEEP);
                    db.setOriginData(gson.toJson(mp.getValue()));
                    db.setUpload(0);
                    db.setUploadGD(0);
                    B30HalfHourDao.getInstance().saveOriginData(db);



                    String bleName = "B31";
                    if (!WatchUtils.isEmpty(MyCommandManager.DEVICENAME))
                        bleName = MyCommandManager.DEVICENAME;
                    //保存睡眠数据
                    SleepData sleepData = mp.getValue();
                    //清醒时长=总的睡眠时长-深睡时长-清醒时长
                    int soberlen = sleepData.getAllSleepTime() - sleepData.getDeepSleepTime() - sleepData.getLowSleepTime();
                    CommDBManager.getCommDBManager().saveCommSleepDbData(bleName, WatchUtils.getSherpBleMac(MyApp.getContext()), sleepData.getDate(),
                            sleepData.getDeepSleepTime(), sleepData.getLowSleepTime(), soberlen, sleepData.getAllSleepTime(),
                            sleepData.getSleepDown().getDateAndClockForSleepSecond(), sleepData.getSleepUp().getDateAndClockForSleepSecond(),
                            sleepData.getWakeCount());


                }
            }
        }
    };



    private NewConnBleHelpService() {

    }

    public static NewConnBleHelpService getConnBleHelpService() {
        if (connBleHelpService == null) {
            synchronized (NewConnBleHelpService.class) {
                if (connBleHelpService == null) {
                    connBleHelpService = new NewConnBleHelpService();
                }
            }
        }
        return connBleHelpService;
    }


    //    private Dialog dialog;
    //发送广播提示输入密码
    private void showLoadingDialog2(final String b30Mac) {
        Intent intent = new Intent();
        intent.setAction(WatchUtils.CHANGEPASS);
        intent.putExtra("b30ID", b30Mac);
        MyApp.getContext().sendBroadcast(intent);
    }


    public void doConnOperater(final String bMac) {
        String b30Pwd = (String) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.DEVICESCODE, "0000");
//        Log.e(TAG, "--A-s--pwdData=" + b30Pwd);
        boolean is24Hour = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.IS24Hour, true);//是否为24小时制


        VPOperateManager.getMangerInstance(MyApp.getContext()).confirmDevicePwd(new IBleWriteResponse() {
            @Override
            public void onResponse(int i) {
            }
        }, new IPwdDataListener() {
            @Override
            public void onPwdDataChange(PwdData pwdData) {
//                Log.e(TAG, "---111--pwdData=" + pwdData.getmStatus() + "=" + pwdData.toString());
                //默认密码不正确，提醒用户输入密码
                if (pwdData.getmStatus() == EPwdStatus.CHECK_FAIL) {
                    showLoadingDialog2(bMac);
                }

            }
        }, new IDeviceFuctionDataListener() {   //设置支持的功能
            @Override
            public void onFunctionSupportDataChange(FunctionDeviceSupportData functionDeviceSupportData) {
//                Log.e(TAG, "--111---functionDeviceSupportData--=" + functionDeviceSupportData.toString());
//                Log.e(TAG, "-----contactMsgLength=" + functionDeviceSupportData.getContactMsgLength() + "--all=" + functionDeviceSupportData.getAllMsgLength());
            }
        }, new ISocialMsgDataListener() {   //消息提醒的开关状态
            @Override
            public void onSocialMsgSupportDataChange(FunctionSocailMsgData functionSocailMsgData) {
//                Log.e(TAG, "-----functionSocailMsgData-=" + functionSocailMsgData);
            }
        }, new ICustomSettingDataListener() {   //自定义设置的开关状态
            @Override
            public void OnSettingDataChange(CustomSettingData customSettingData) {
//                Log.e(TAG, "----111-CustomSettingData-=" + customSettingData.toString());


            }
        }, b30Pwd, is24Hour);

//        //设置语言，根据系统的语言设置
//        ELanguage languageData;
//        String localelLanguage = Locale.getDefault().getLanguage();
//        if (WatchUtils.isEmpty(localelLanguage) && localelLanguage.equals("zh")) {    //中文
//            languageData = ELanguage.CHINA;
//        } else {
//            languageData = ELanguage.ENGLISH;
//        }
//        MyApp.getInstance().getVpOperateManager().settingDeviceLanguage(bleWriteResponse, new ILanguageDataListener() {
//            @Override
//            public void onLanguageDataChange(LanguageData languageData) {
//
//            }
//        }, languageData);


        SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.BATTERNUMBER, 0);//每次连接清空电量
        //同步用户信息，设置目标步数
        setDeviceUserData();
    }

    /**
     * 读取设备开关
     */
    void readDevicesSwithStute() {
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
                } else {
                    SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISSystem, false);//是否为公制
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
//                //判断是否支持断连提醒
//                if (customSettingData.getDisconnectRemind() == EFunctionStatus.UNSUPPORT) {
//                } else {
//                }
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

    //验证设备密码
    public void doConnOperater(final String blePwd, final VerB30PwdListener verB30PwdListener) {
        boolean is24Hour = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.IS24Hour, true);//是否为24小时制
        VPOperateManager.getMangerInstance(MyApp.getContext()).confirmDevicePwd(new IBleWriteResponse() {
            @Override
            public void onResponse(int i) {

            }
        }, new IPwdDataListener() {
            @Override
            public void onPwdDataChange(PwdData pwdData) {
                //showLoadingDialog2(mac);
//                Log.e(TAG, "-----pwdData=" + pwdData.toString());
                //此方法调用 ，密码不正确
                if (pwdData.getmStatus() == EPwdStatus.CHECK_FAIL) {
                    if (verB30PwdListener != null)
                        verB30PwdListener.verPwdFailed();
                }

                //验证密码成功
                if (pwdData.getmStatus() == EPwdStatus.CHECK_AND_TIME_SUCCESS) {
//                    Log.e(TAG, "-----pwdData=" + pwdData.toString());
                    SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.DEVICESCODE, blePwd);
                    if (verB30PwdListener != null)
                        verB30PwdListener.verPwdSucc();
                }

            }
        }, new IDeviceFuctionDataListener() {
            @Override
            public void onFunctionSupportDataChange(FunctionDeviceSupportData functionDeviceSupportData) {
//                Log.e(TAG, "-----functionDeviceSupportData--=" + functionDeviceSupportData.toString());
//                Log.e(TAG, "-----contactMsgLength=" + functionDeviceSupportData.getContactMsgLength() + "--all=" + functionDeviceSupportData.getAllMsgLength());
            }
        }, new ISocialMsgDataListener() {
            @Override
            public void onSocialMsgSupportDataChange(FunctionSocailMsgData functionSocailMsgData) {
                //Log.e(TAG, "-----functionSocailMsgData-=" + functionSocailMsgData);
            }
        }, new ICustomSettingDataListener() {
            @Override
            public void OnSettingDataChange(CustomSettingData customSettingData) {
//                Log.e(TAG, "---2222--OnSettingDataChange-=" + customSettingData.toString());


            }
        }, blePwd, is24Hour);


//        //设置语言，根据系统的语言设置
//        ELanguage languageData;
//        String localelLanguage = Locale.getDefault().getLanguage();
//        if (WatchUtils.isEmpty(localelLanguage) && localelLanguage.equals("zh")) {    //中文
//            languageData = ELanguage.CHINA;
//        } else {
//            languageData = ELanguage.ENGLISH;
//        }
//        MyApp.getInstance().getVpOperateManager().settingDeviceLanguage(bleWriteResponse, new ILanguageDataListener() {
//            @Override
//            public void onLanguageDataChange(LanguageData languageData) {
//
//            }
//        }, languageData);


        SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.BATTERNUMBER, 0);//每次连接清空电量
        //同步用户信息，设置目标步数
        setDeviceUserData();


    }

    /**
     * 同步用户信息设置设备的目标步数，
     */
    public void setDeviceUserData() {
        //目标步数
        int sportGoal = (int) SharedPreferencesUtils.getParam(MyApp.getContext(), "b30Goal", 0);
        PersonInfoData personInfoData = WatchUtils.getUserPerson(sportGoal);

        if (personInfoData != null){
            MyApp.getInstance().getVpOperateManager().syncPersonInfo(new IBleWriteResponse() {
                @Override
                public void onResponse(int i) {
                }
            }, new IPersonInfoDataListener() {
                @Override
                public void OnPersoninfoDataChange(EOprateStauts eOprateStauts) {
                    //同步用户信息成功
//                if (eOprateStauts == EOprateStauts.OPRATE_SUCCESS) {
//                    if (connBleHelpListener != null) {
//                        connBleHelpListener.connSuccState();
//                    }
//                }
                }
            }, personInfoData);
        }



        //设置语言，根据系统的语言设置
        ELanguage languageData ;
        String localelLanguage = Locale.getDefault().getLanguage();
        Log.e(TAG,"----------localelLanguage="+localelLanguage);
        if(!WatchUtils.isEmpty(localelLanguage) && localelLanguage.equals("zh")){    //中文
            Locale locales = MyApp.getInstance().getApplicationContext().getResources().getConfiguration().locale;
            String localCountry = locales.getCountry();
            if(localCountry.equals("TW")){  //繁体
//                languageData = ELanguage.CHINA_TRADITIONAL;
                languageData = ELanguage.ENGLISH;
            }else{
                languageData = ELanguage.CHINA;
            }

        }else{
            languageData = ELanguage.ENGLISH;
        }
        MyApp.getInstance().getVpOperateManager().settingDeviceLanguage(bleWriteResponse, new ILanguageDataListener() {
            @Override
            public void onLanguageDataChange(LanguageData languageData) {
                if (connBleHelpListener != null) {
                    connBleHelpListener.connSuccState();
                }
            }
        }, languageData);

//        //设置主界面默认风格为1
//        MyApp.getInstance().getVpOperateManager().settingScreenStyle(bleWriteResponse, new IScreenStyleListener() {
//            @Override
//            public void onScreenStyleDataChange(ScreenStyleData screenStyleData) {
//
//            }
//        }, 1);

        //设置消息提醒的开关状态
//        WatchUtils.setCommSocailMsgSetting(MyApp.getContext());

        //设置开关，佩戴检测、自动心率 血氧测量打开
//        WatchUtils.setSwitchCheck();

        setSwitchCheck();

    }


    /**
     * 获取手环基本数据
     */
    public void getDeviceMsgData1() {
        // 获取步数
        MyApp.getInstance().getVpOperateManager().readSportStep(bleWriteResponse, new ISportDataListener() {
            @Override
            public void onSportDataChange(SportData sportData) {
//                Log.e(TAG, "----------总步数=" + sportData.toString());
                if (connBleMsgDataListener != null) {
                    connBleMsgDataListener.getBleSportData(sportData.getStep());
                }

            }
        });
        //电量
        MyApp.getInstance().getVpOperateManager().readBattery(bleWriteResponse, new IBatteryDataListener() {
            @Override
            public void onDataChange(BatteryData batteryData) {
                if (connBleMsgDataListener != null) {
                    connBleMsgDataListener.getBleBatteryData(batteryData.getBatteryLevel());
                }
            }
        });

        //连接成功后读取一下闹钟
        MyApp.getInstance().getVpOperateManager().readAlarm2(bleWriteResponse, new IAlarm2DataListListener() {
            @Override
            public void onAlarmDataChangeListListener(AlarmData2 alarmData2) {

            }
        });

    }

    /**
     * 获取手环基本数据
     */
    public void getDeviceMsgData() {
        //设置语言，根据系统的语言设置
        ELanguage languageData;
        String localelLanguage = Locale.getDefault().getLanguage();
        if (!WatchUtils.isEmpty(localelLanguage) && localelLanguage.equals("zh")) {    //中文
//            Locale locales = MyApp.getInstance().getApplicationContext().getResources().getConfiguration().locale;
//            String localCountry = locales.getCountry();
//            if(localCountry.equals("TW")){  //繁体
//                languageData = ELanguage.CHINA_TRADITIONAL;
//            }else{
//                languageData = ELanguage.CHINA;
//            }
            languageData = ELanguage.CHINA;
        } else {
            languageData = ELanguage.ENGLISH;
        }

        MyApp.getInstance().getVpOperateManager().settingDeviceLanguage(bleWriteResponse, new ILanguageDataListener() {
            @Override
            public void onLanguageDataChange(LanguageData languageData) {

            }
        }, languageData);

        // 获取步数
        MyApp.getInstance().getVpOperateManager().readSportStep(bleWriteResponse, new ISportDataListener() {
            @Override
            public void onSportDataChange(SportData sportData) {
                B30HalfHourDao b30HalfHourDao = B30HalfHourDao.getInstance();
                B30HalfHourDB b30HalfHourDB = new B30HalfHourDB();
                b30HalfHourDB.setDate(WatchUtils.getCurrentDate());
                b30HalfHourDB.setAddress(MyApp.getInstance().getMacAddress());
                b30HalfHourDB.setType(B30HalfHourDao.TYPE_STEP_DETAIL);
                b30HalfHourDB.setOriginData(gson.toJson(sportData));
                b30HalfHourDB.setUpload(0);
                b30HalfHourDB.setUploadGD(0);

                b30HalfHourDao.saveOriginData(b30HalfHourDB);


                if (connBleMsgDataListener != null) {
                    connBleMsgDataListener.getBleSportData(sportData.getStep());
                }

                String bleName = "B31";
                if (!WatchUtils.isEmpty(MyCommandManager.DEVICENAME)) bleName = MyCommandManager.DEVICENAME;
                //保存总步数
                CommDBManager.getCommDBManager().saveCommCountStepDate(bleName, MyApp.getInstance().getMacAddress(), WatchUtils.getCurrentDate(), sportData.getStep());
            }
        });
        //电量
        MyApp.getInstance().getVpOperateManager().readBattery(bleWriteResponse, new IBatteryDataListener() {
            @Override
            public void onDataChange(BatteryData batteryData) {
                if (connBleMsgDataListener != null) {
                    connBleMsgDataListener.getBleBatteryData(batteryData.getBatteryLevel());
                }
            }
        });
    }

    /**
     * 读取所有的原始数据(包括睡眠数据)
     *
     * @param today true_只加载今天数据 false_加载三天
     */
    public void readAllHealthData(boolean today) {

//        Log.e(TAG, "====== 是否只加载今天 " + today + "====是否在刷新中 " + isGETDATAS);
        try {
            if (!isGETDATAS) {
                if (upDataToGDServices != null && upDataToGDServices.getStatus() == AsyncTask.Status.RUNNING) {
                    upDataToGDServices.cancel(true); // 如果Task还在运行，则先取消它
//                    Log.e("-------AAA--", "先取消异步，在去重新开始");
                }
                upDataToGDServices = new ReadDataForDevices(today);
                upDataToGDServices.execute();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    ReadDataForDevices upDataToGDServices = null;
    boolean isGETDATAS = false;


    public class ReadDataForDevices extends AsyncTask<Void, Void, Void> {
        VPOperateManager vpOperateManager = null;
        boolean today;

        public ReadDataForDevices(boolean today) {
            this.today = today;
        }

        // 方法1：onPreExecute（）
        // 作用：执行 线程任务前的操作
        // 注：根据需求复写
        @Override
        protected void onPreExecute() {
//            Log.e("-------AAA--", "异步初始化了 " + isGETDATAS);
            sleepMap.clear();
            vpOperateManager = MyApp.getInstance().getVpOperateManager();
        }

        // 方法2：doInBackground（）
        // 作用：接收输入参数、执行任务中的耗时操作、返回 线程任务执行的结果
        // 注：必须复写，从而自定义线程任务
        @Override
        protected Void doInBackground(Void... voids) {
            // Task被取消了，马上退出循环
            if (isCancelled() || isGETDATAS) return null;
//            Log.e("-------AAA--", "异步任务开始啦 " + isGETDATAS);
            // 可调用publishProgress（）显示进度, 之后将执行onProgressUpdate（）
//        publishProgress();
            try {
                isGETDATAS = true;

                MyApp.getInstance().getVpOperateManager().readAllHealthData(new IAllHealthDataListener() {
                    @Override
                    public void onProgress(float v) {

                    }

                    @Override
                    public void onSleepDataChange(SleepData sleepData) {
//                        Log.e(TAG, "---------睡眠原始返回数据=" + sleepData.toString());
                        // 睡眠数据返回,会有多条数据
                        saveSleepData(sleepData);
                    }

                    @Override
                    public void onReadSleepComplete() {
                        // 读取睡眠数据结束
//                        Log.e(TAG, "----------睡眠数据读取结束------=" + sleepMap.size());
                        handler.sendEmptyMessage(1001);
                    }

                    @Override
                    public void onOringinFiveMinuteDataChange(OriginData originData) {

                    }

                    @Override
                    public void onOringinHalfHourDataChange(OriginHalfHourData originHalfHourData) {
                        // 多少天就有多少条数据
                        // 30分钟原始数据的回调，来自5分钟的原始数据，只是在内部进行了数据处理
//                        MyLogUtil.d("----------", originHalfHourData.toString());
                        saveHalfHourData(originHalfHourData);
                    }

                    @Override
                    public void onReadOriginComplete() {
//                        Log.e(TAG,"----------读取运动数据结束--------="+System.currentTimeMillis()/1000+"---差值="+(System.currentTimeMillis()/1000-currTime));
                        // 读取取运动,心率,血压数据结束
                        new LocalizeTool(MyApp.getContext()).putUpdateDate(WatchUtils
                                .obtainFormatDate(0));// 更新最后更新数据的时间
                        if (connBleMsgDataListener != null) {
                            connBleMsgDataListener.onOriginData();
                        }
                    }
                }, today ? 2 : 4);
//                }, today ? 1 : 3);
            } catch (Error e) {
                isGETDATAS = false;
            }
            return null;
        }


        // 方法3：onProgressUpdate（）
        // 作用：在主线程 显示线程任务执行的进度
        // 注：根据需求复写
        @Override
        protected void onProgressUpdate(Void... values) {
            if (isCancelled()) return;
        }

        // 方法4：onPostExecute（）
        // 作用：接收线程任务执行结果、将执行结果显示到UI组件
        // 注：必须复写，从而自定义UI操作
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            isGETDATAS = false;
//            Log.e("-------AAA--", "异步任务读取结束啦 " + isGETDATAS);
        }

        // 方法5：onCancelled()
        // 作用：将异步任务设置为：取消状态
        @Override
        protected void onCancelled() {
            //如果异步任务不为空 并且状态是 运行时  ，就把他取消这个加载任务
            if (getStatus() == AsyncTask.Status.RUNNING) {
                cancel(true);
            }
            // 正在读取数据,写到全局,保证同时只有一个本服务在运行
            isGETDATAS = false;
//            Log.e("-------AAA--", "异步任务取消了 " + isGETDATAS);
        }


        /**
         * 保存手环返回的睡眠数据到本地数据库
         *
         * @param sleepData 睡眠数据
         */
        private void saveSleepData(SleepData sleepData) {
            if (sleepData == null) return;
//            Log.e("-------设备睡眠数据---", gson.toJson(sleepData) + "");
            if (sleepMap.get(sleepData.getDate()) == null) {
                sleepMap.put(sleepData.getDate(), sleepData);
            } else {
                //sleepMap.put(sleepData.getDate(),sleepData);
//                Log.e(TAG, "---------sleepMap=" + sleepMap.toString());
                if (sleepMap.get(sleepData.getDate()).getDate().equals(sleepData.getDate())) {  //同一天的
                    if (!sleepMap.get(sleepData.getDate()).getSleepLine().equals(sleepData.getSleepLine())) {
                        //map 中已经保存的
                        SleepData tempSleepData = sleepMap.get(sleepData.getDate());
                        SleepData resultSlee = new SleepData();
                        resultSlee.setDate(sleepData.getDate());    //日期
                        resultSlee.setCali_flag(0);
                        //睡眠质量，取最大值
                        resultSlee.setSleepQulity(tempSleepData.getSleepQulity() >= sleepData.getSleepQulity() ? tempSleepData.getSleepQulity() : sleepData.getSleepQulity());
                        //睡醒次数
                        resultSlee.setWakeCount(tempSleepData.getWakeCount() + sleepData.getWakeCount() + 1);
                        //深睡时间
                        resultSlee.setDeepSleepTime(tempSleepData.getDeepSleepTime() + sleepData.getDeepSleepTime());
                        //浅睡时间
                        resultSlee.setLowSleepTime(tempSleepData.getLowSleepTime() + sleepData.getLowSleepTime());
                        //入睡时间 比较时间大小
                        String time1 = tempSleepData.getSleepDown().getDateAndClockForSleepSecond();
                        String time2 = sleepData.getSleepDown().getDateAndClockForSleepSecond();
                        resultSlee.setSleepDown(WatchUtils.comPariDateDetail(time2, time1) ? sleepData.getSleepDown() : tempSleepData.getSleepDown());
                        //清醒时间
                        String sleepUpStr1 = tempSleepData.getSleepUp().getDateAndClockForSleepSecond();
                        String sleepUpStr2 = sleepData.getSleepUp().getDateAndClockForSleepSecond();
                        resultSlee.setSleepUp(WatchUtils.comPariDateDetail(sleepUpStr2, sleepUpStr1) ? tempSleepData.getSleepUp() : sleepData.getSleepUp());
                        //计算两段时间间隔，第二段的入睡时间-第一段的清醒时间
                        int sleepLen = WatchUtils.intervalTimeStr(sleepUpStr1, time2);
                        int sleepStatus = sleepLen / 5;
                        StringBuilder stringBuffer = new StringBuilder();
                        for (int i = 1; i <= sleepStatus; i++) {
                            stringBuffer.append("2");
                        }
                        //所有睡眠时间
                        resultSlee.setAllSleepTime(Integer.valueOf(tempSleepData.getAllSleepTime()) + Integer.valueOf(sleepData.getAllSleepTime()) + sleepStatus * 5);
                        resultSlee.setSleepLine(WatchUtils.comPariDateDetail(time1, time2) ?
                                (tempSleepData.getSleepLine() + stringBuffer + "" + sleepData.getSleepLine()) :
                                (sleepData.getSleepLine() + stringBuffer + "" + tempSleepData.getSleepLine()));
//                        Log.e(TAG, "----------结果睡眠---=" + resultSlee.toString());
                        sleepMap.put(sleepData.getDate(), resultSlee);
                    }

                }


            }
        }

        /**
         * 保存手环返回的健康数据(步数,运动,心率,血压)到本地数据库
         *
         * @param data 30分钟的数据源
         */
        private void saveHalfHourData(OriginHalfHourData data) {
            if (data == null) return;
            String mac = MyApp.getInstance().getMacAddress();
            //MyLogUtil.e("------sport------" + data.getHalfHourSportDatas());
            String dateSport = saveSportData(mac, data.getHalfHourSportDatas());
            //MyLogUtil.e("------sport -time" + dateSport);
            saveStepData(mac, dateSport, data.getAllStep());
            saveRateData(mac, data.getHalfHourRateDatas());
//            Log.d("-------xue", data.getHalfHourBps().toString());
            saveBpData(mac, data.getHalfHourBps());
        }

        /**
         * 保存手环返回的运动数据到本地数据库
         *
         * @param mac       手环MAC地址
         * @param sportData 当天所有30分钟运动数据
         * @return 日期(保存步数时用, 有运动数据才会有步数)
         */
        private String saveSportData(String mac, List<HalfHourSportData> sportData) {
            if (sportData == null || sportData.isEmpty()) return null;
            String date = sportData.get(0).getDate();
            B30HalfHourDB db = new B30HalfHourDB();
            db.setAddress(mac);
            db.setDate(date);
            db.setType(B30HalfHourDao.TYPE_SPORT);
            db.setOriginData(gson.toJson(sportData));
            db.setUpload(0);
            db.setUploadGD(0);
            B30HalfHourDao.getInstance().saveOriginData(db);
            return date;
        }

        /**
         * 保存手环返回的心率数据到本地数据库
         *
         * @param mac      手环MAC地址
         * @param rateData 当天所有30分钟心率数据
         */
        private void saveRateData(String mac, List<HalfHourRateData> rateData) {
            if (rateData == null || rateData.isEmpty()) return;
            B30HalfHourDB db = new B30HalfHourDB();
            db.setAddress(mac);
            db.setDate(rateData.get(0).getDate());
            db.setType(B30HalfHourDao.TYPE_RATE);
//            MyLogUtil.d("-----心率数据---", gson.toJson(rateData));
            db.setOriginData(gson.toJson(rateData));
            db.setUpload(0);
            db.setUploadGD(0);
            B30HalfHourDao.getInstance().saveOriginData(db);



            String bleName = "B31";
            if (!WatchUtils.isEmpty(MyCommandManager.DEVICENAME)) bleName = MyCommandManager.DEVICENAME;
            //保存心率数据
            Integer[] heartStr = CommCalUtils.calHeartData(rateData);
            CommDBManager.getCommDBManager().saveCommHeartData(bleName, WatchUtils.getSherpBleMac(MyApp.getContext()),
                    rateData.get(0).date, heartStr[0], heartStr[1], heartStr[2]);


        }

        /**
         * 保存手环返回的血压数据到本地数据库
         *
         * @param mac    手环MAC地址
         * @param bpData 当天所有30分钟血压数据
         */
        private void saveBpData(String mac, List<HalfHourBpData> bpData) {
            if (bpData == null || bpData.isEmpty()) return;
            B30HalfHourDB db = new B30HalfHourDB();
            db.setAddress(mac);
            db.setDate(bpData.get(0).getDate());
            db.setType(B30HalfHourDao.TYPE_BP);
            db.setOriginData(gson.toJson(bpData));
            db.setUpload(0);
            db.setUploadGD(0);
            //Log.e("-------血压原始数据", gson.toJson(bpData));
            B30HalfHourDao.getInstance().saveOriginData(db);

            //保存血压的数据
            Integer[] bloodStr = CommCalUtils.calBloodData(bpData);
//            Log.e(TAG, "-------血压平均数据=" + Arrays.toString(bloodStr));
            CommDBManager.getCommDBManager().saveCommBloodDb(WatchUtils.getSherpBleMac(MyApp.getContext()), bpData.get(0).date,
                    bloodStr[0], bloodStr[1], bloodStr[2], bloodStr[3]);
        }

        /**
         * 保存手环返回的步数数据到本地数据库
         *
         * @param mac      手环MAC地址
         * @param date     日期(步数没有日期,用运动数据的日期)
         * @param stepCurr 当天全部步数
         */
        private void saveStepData(String mac, String date, int stepCurr) {
            // 当天步数数据要以首页单独获取到的步数为准,因为这里获取到的当天总步数,
            // 有时会大于首页获取的实时步数,所以当天的总步数这里不保存
//            MyLogUtil.e("---保存手环返回的步数数据到本地数据库" + mac + "\n" + date + "\n" + stepCurr);
            if (date == null || date.equals(WatchUtils.obtainFormatDate(0))) return;
            // 跟本地的对比一下,以防步数倒流
            String step = B30HalfHourDao.getInstance().findOriginData(mac, date, B30HalfHourDao.TYPE_STEP);
            int stepLocal = 0;
            try {
                if (TextUtils.isEmpty(step)) step = "0";
                stepLocal = Integer.parseInt(step);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            String bleName = "B31";
            if (!WatchUtils.isEmpty(MyCommandManager.DEVICENAME)) bleName = MyCommandManager.DEVICENAME;
//            Log.e(TAG, " B31  " + mac + "  " + date + "  " + stepCurr);
            //保存总步数
            CommDBManager.getCommDBManager().saveCommCountStepDate(bleName, mac, date, stepCurr);

            if (stepCurr > stepLocal) {
                B30HalfHourDB db = new B30HalfHourDB();
                db.setAddress(mac);
                db.setDate(date);
                db.setType(B30HalfHourDao.TYPE_STEP);
                db.setOriginData("" + stepCurr);
                db.setUpload(0);
                db.setUploadGD(0);
                B30HalfHourDao.getInstance().saveOriginData(db);
            }
        }


    }


    /**
     * ==============================================================
     */

    //设置密码回调
    public interface ConnBleHelpListener {
        void connSuccState();
    }

    //步数，电量回调
    public interface ConnBleMsgDataListener {
        /**
         * 电量
         */
        void getBleBatteryData(int batteryLevel);

        /**
         * 步数数据
         */
        void getBleSportData(int step);

        /**
         * 原始数据有更新到数据库的通知
         */
        void onOriginData();
    }


    public interface ConnHRVBackDataListener {
        void backHRVListData(List<HRVOriginData> dataList);
    }

    public void setConnHRVBackDataListener(ConnHRVBackDataListener connHRVBackDataListener) {
        this.connHRVBackDataListener = connHRVBackDataListener;
    }

    public void setConnBleMsgDataListener(ConnBleMsgDataListener connBleMsgDataListener) {
        this.connBleMsgDataListener = connBleMsgDataListener;
    }

    public void setConnBleHelpListener(ConnBleHelpListener connBleHelpListener) {
        this.connBleHelpListener = connBleHelpListener;
    }

    private IBleWriteResponse bleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int i) {
//            Log.e(TAG, "------bleWriteResponse=" + i);
        }
    };




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
    boolean isOnclickSOS = false;

    //开关设置
    private void setSwitchCheck() {
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


        /***********************/
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
        //查找手机
        if (isFindPhone) {
            isOpenFindPhoneUI = EFunctionStatus.SUPPORT_OPEN;
        } else {
            isOpenFindPhoneUI = EFunctionStatus.SUPPORT_CLOSE;
        }
        //断连提醒
        if (isDisconn) {
            isOpenDisconnectRemind = EFunctionStatus.SUPPORT_OPEN;
        } else {
            isOpenDisconnectRemind = EFunctionStatus.SUPPORT_CLOSE;
        }

        //Log.e(TAG, "----- SOSa啊 " + isSos);
        //SOS
        if (isSos) {
            isOpenSOS = EFunctionStatus.SUPPORT_OPEN;
        } else {
            isOpenSOS = EFunctionStatus.SUPPORT_CLOSE;
        }

        Log.i("bbbbbbbbo" , "NewConnBleHelpService = " + isAutomaticBoold);
        CustomSetting customSetting = new CustomSetting(true, isSystem, is24Hour, isAutomaticHeart,
                isAutomaticBoold, isOpenSportRemain, isOpenVoiceBpHeart, isOpenFindPhoneUI, isOpenStopWatch, isOpenSpo2hLowRemind,
                isOpenWearDetectSkin, isOpenAutoInCall, isOpenAutoHRV, isOpenDisconnectRemind, isOpenSOS);
        //Log.e(TAG, "-----新设置的值啊---customSetting=" + customSetting.toString());

        MyApp.getInstance().getVpOperateManager().changeCustomSetting(bleWriteResponse, new ICustomSettingDataListener() {
            @Override
            public void OnSettingDataChange(CustomSettingData customSettingData) {

            }
        }, customSetting);
    }
}
