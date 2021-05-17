package com.guider.healthring.b30.service;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.guider.healthring.Commont;
import com.guider.healthring.MyApp;
import com.guider.healthring.b30.bean.B30HalfHourDB;
import com.guider.healthring.b30.bean.B30HalfHourDao;
import com.guider.healthring.b30.bean.CusVPSleepData;
import com.guider.healthring.b30.bean.CusVPSleepPrecisionData;
import com.guider.healthring.b30.bean.CusVPTimeData;
import com.guider.healthring.b31.bpoxy.NewReadHRVAnSpo2DatatService;
import com.guider.healthring.bleutil.MyCommandManager;
import com.guider.healthring.commdbserver.CommCalUtils;
import com.guider.healthring.commdbserver.CommDBManager;
import com.guider.healthring.siswatch.utils.DateTimeUtils;
import com.guider.healthring.siswatch.utils.WatchUtils;
import com.guider.healthring.util.LocalizeTool;
import com.guider.healthring.util.SharedPreferencesUtils;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IAlarm2DataListListener;
import com.veepoo.protocol.listener.data.IBatteryDataListener;
import com.veepoo.protocol.listener.data.ICustomSettingDataListener;
import com.veepoo.protocol.listener.data.IDeviceFuctionDataListener;
import com.veepoo.protocol.listener.data.ILanguageDataListener;
import com.veepoo.protocol.listener.data.IOriginData3Listener;
import com.veepoo.protocol.listener.data.IOriginDataListener;
import com.veepoo.protocol.listener.data.IPersonInfoDataListener;
import com.veepoo.protocol.listener.data.IPwdDataListener;
import com.veepoo.protocol.listener.data.ISleepDataListener;
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
import com.veepoo.protocol.model.datas.OriginData3;
import com.veepoo.protocol.model.datas.OriginHalfHourData;
import com.veepoo.protocol.model.datas.PersonInfoData;
import com.veepoo.protocol.model.datas.PwdData;
import com.veepoo.protocol.model.datas.SleepData;
import com.veepoo.protocol.model.datas.SleepPrecisionData;
import com.veepoo.protocol.model.datas.Spo2hOriginData;
import com.veepoo.protocol.model.datas.SportData;
import com.veepoo.protocol.model.datas.TimeData;
import com.veepoo.protocol.model.enums.EFunctionStatus;
import com.veepoo.protocol.model.enums.ELanguage;
import com.veepoo.protocol.model.enums.EOprateStauts;
import com.veepoo.protocol.model.enums.EPwdStatus;
import com.veepoo.protocol.model.settings.CustomSettingData;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * Created by Administrator on 2018/7/26.
 */

public class ConnBleHelpService {

    private static final String TAG = "ConnBleHelpService";

    //验证密码
    private ConnBleHelpListener connBleHelpListener;

    //设备数据
    private ConnBleMsgDataListener connBleMsgDataListener;


    //睡眠处理map
    private Map<String, SleepData> sleepMap = new HashMap<>();
    //精准睡眠处理的map
    private ConcurrentMap<String, CusVPSleepPrecisionData> precisionSleepMap = new ConcurrentHashMap<>();

    //是否支持血氧
    private boolean isSupportSpo2 = false;
    //是否支持精准睡眠
    private boolean isSleepPrecisionData = false;

    //保存spo2的task
    private B31SaveSpo2AsyncTask b31SaveSpo2AsyncTask;
    //保存hrv的task
    private B31sSaveHrvAsyncTask b31sSaveHrvAsyncTask;

    //协议版本 0为旧版，不支持精准睡眠
    private int originProtcolVersion;

    /**
     * 转换工具
     */
    private Gson gson = new Gson();

    private static volatile ConnBleHelpService connBleHelpService;

    private ConnBleHelpService() {

    }

    public static ConnBleHelpService getConnBleHelpService() {
        if (connBleHelpService == null) {
            synchronized (ConnBleHelpService.class) {
                if (connBleHelpService == null) {
                    connBleHelpService = new ConnBleHelpService();
                }
            }
        }
        return connBleHelpService;
    }


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1001) {
                handler.removeMessages(1001);
                //普通睡眠
                if (sleepMap != null && !sleepMap.isEmpty()) {
                    for (Map.Entry<String, SleepData> mp : sleepMap.entrySet()) {
                        Log.e(TAG,"-----普通睡眠入库="+mp.toString());
                        //保存详细数据
                        B30HalfHourDB db = new B30HalfHourDB();
                        db.setAddress(MyApp.getInstance().getMacAddress());
                        // db.setDate(WatchUtils.obtainAroundDate(mp.getValue().getDate(), false));
                        db.setDate(mp.getValue().getDate());
                        db.setType(B30HalfHourDao.TYPE_SLEEP);
                        db.setOriginData(gson.toJson(new CusVPSleepData(mp.getValue())));
                        db.setUpload(0);
                        B30HalfHourDao.getInstance().saveOriginData(db);

                        //保存汇总数据
                        String bleName = "B31";
                        if (!WatchUtils.isEmpty(MyCommandManager.DEVICENAME))
                            bleName = MyCommandManager.DEVICENAME;
                        //保存睡眠数据
                        SleepData sleepData = mp.getValue();
                        //清醒时长=总的睡眠时长-深睡时长-清醒时长
                        int soberlen = sleepData.getAllSleepTime() - sleepData.getDeepSleepTime() - sleepData.getLowSleepTime();
                        CommDBManager.getCommDBManager().saveCommSleepDbData(bleName, WatchUtils.getSherpBleMac(MyApp.getContext()), sleepData.getDate(),
                                sleepData.getDeepSleepTime(), sleepData.getLowSleepTime(), soberlen, sleepData.getAllSleepTime(),
                                sleepData.getSleepDown().getDateAndClockForSleep(), sleepData.getSleepUp().getDateAndClockForSleep(),
                                sleepData.getWakeCount());
                    }
                }

                //精准睡眠
                if (precisionSleepMap != null && !precisionSleepMap.isEmpty()) {
                    Log.e(TAG, "------精准睡眠的map=" + precisionSleepMap.size());
                    for (Map.Entry<String, CusVPSleepPrecisionData> mmp : precisionSleepMap.entrySet()) {
                        //保存详细数据 ，保存详细数据时日期会往后+ 一天
                        Log.e(TAG, "------保存精准睡眠=" + mmp.toString() + "--=" + mmp.getValue().getSleepLine());
                        B30HalfHourDB db = new B30HalfHourDB();
                        db.setAddress(MyApp.getInstance().getMacAddress());
                        db.setDate(mmp.getKey());
                        db.setType(B30HalfHourDao.TYPE_PRECISION_SLEEP);
                        db.setOriginData(gson.toJson(mmp.getValue()));
                        db.setUpload(0);
                        B30HalfHourDao.getInstance().saveOriginData(db);
                    }
                }
            }

            if (msg.what == 1001) {
                boolean isToday = (boolean) msg.obj;
                if (originProtcolVersion == 0) {
                    readOldVersionData(isToday);
                } else {
                    new ReadAllDataAsync().execute(isToday);
                }
            }
        }
    };


    //    private Dialog dialog;
    //发送广播提示输入密码
    private void showLoadingDialog2(final String b30Mac) {
        Intent intent = new Intent();
        intent.setAction("com.example.bozhilun.android.siswatch.CHANGEPASS");
        intent.putExtra("b30ID", b30Mac);
        MyApp.getContext().sendBroadcast(intent);
    }


    public void doConnOperater(final String bMac) {
        String b30Pwd = (String) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.DEVICESCODE, "0000");
        if (WatchUtils.isEmpty(b30Pwd))
            b30Pwd = "0000";
        final boolean is24Hour = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.IS24Hour, true);//是否为24小时制

        VPOperateManager.getMangerInstance(MyApp.getContext()).confirmDevicePwd(new IBleWriteResponse() {
            @Override
            public void onResponse(int i) {

            }
        }, new IPwdDataListener() {
            @Override
            public void onPwdDataChange(PwdData pwdData) {
                //默认密码不正确，提醒用户输入密码
                if (pwdData.getmStatus() == EPwdStatus.CHECK_FAIL) {
                    showLoadingDialog2(bMac);
                }

                //是否支持佩戴检测
                SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.IS_SUPPORT_CHECK_WEAR, pwdData.getWearDetectFunction() == EFunctionStatus.SUPPORT);
            }
        }, new IDeviceFuctionDataListener() {    //设备所支持的功能
            @Override
            public void onFunctionSupportDataChange(FunctionDeviceSupportData functionDeviceSupportData) {
                getCommSupportFunction(functionDeviceSupportData);
            }
        }, new ISocialMsgDataListener() {
            @Override
            public void onSocialMsgSupportDataChange(FunctionSocailMsgData functionSocailMsgData) {
                Log.e(TAG, "-----functionSocailMsgData-=" + functionSocailMsgData);
            }

            @Override
            public void onSocialMsgSupportDataChange2(FunctionSocailMsgData functionSocailMsgData) {

            }
        }, new ICustomSettingDataListener() {
            @Override
            public void OnSettingDataChange(CustomSettingData customSettingData) {
                Log.e(TAG, "----111-CustomSettingData-=" + customSettingData.toString());

            }
        }, b30Pwd.trim(), is24Hour);

        setCommDevice();
    }

    //验证设备密码
    public void doConnOperater(final String blePwd, final VerB30PwdListener verB30PwdListener) {
        // String b30Pwd = (String) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.DEVICESCODE, "0000");
        final boolean is24Hour = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.IS24Hour, true);//是否为24小时制
        VPOperateManager.getMangerInstance(MyApp.getContext()).confirmDevicePwd(new IBleWriteResponse() {
            @Override
            public void onResponse(int i) {

            }
        }, new IPwdDataListener() {
            @Override
            public void onPwdDataChange(PwdData pwdData) {
                //showLoadingDialog2(mac);
                Log.e(TAG, "-----pwdData=" + pwdData.toString());
                //此方法调用 ，密码不正确
                if (pwdData.getmStatus() == EPwdStatus.CHECK_FAIL) {
                    if (verB30PwdListener != null)
                        verB30PwdListener.verPwdFailed();
                }

                //验证密码成功
                if (pwdData.getmStatus() == EPwdStatus.CHECK_AND_TIME_SUCCESS) {
                    SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.DEVICESCODE, blePwd);
                    if (verB30PwdListener != null)
                        verB30PwdListener.verPwdSucc();
                }
                //是否支持佩戴检测
                SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.IS_SUPPORT_CHECK_WEAR, pwdData.getWearDetectFunction() == EFunctionStatus.SUPPORT);
            }
        }, functionDeviceSupportData -> getCommSupportFunction(functionDeviceSupportData), new ISocialMsgDataListener() {
            @Override
            public void onSocialMsgSupportDataChange(FunctionSocailMsgData functionSocailMsgData) {
                //Log.e(TAG, "-----functionSocailMsgData-=" + functionSocailMsgData);
            }

            @Override
            public void onSocialMsgSupportDataChange2(FunctionSocailMsgData functionSocailMsgData) {
            }
        }, customSettingData -> {

        }, blePwd, is24Hour);

        setCommDevice();
    }


    private void getCommSupportFunction(FunctionDeviceSupportData functionDeviceSupportData) {
        Context context = MyApp.getContext();
        Log.e(TAG, "--111---functionDeviceSupportData--=" + functionDeviceSupportData.toString());
        Log.i(TAG, "--手环存储数据的天数--=" + functionDeviceSupportData.getWathcDay());
        //版本协议
        originProtcolVersion = functionDeviceSupportData.getOriginProtcolVersion();
        SharedPreferencesUtils.setParam(context, Commont.VP_DEVICE_VERSION, originProtcolVersion);

        //设置支持的主题风格
        int deviceStyleCoount = functionDeviceSupportData.getScreenstyle();
        SharedPreferencesUtils.setParam(context, Commont.SP_DEVICE_STYLE_COUNT, deviceStyleCoount);

        //是否支持亮度调节
        SharedPreferencesUtils.setParam(context, Commont.IS_B31S_LIGHT_KEY, functionDeviceSupportData.getScreenLight() == EFunctionStatus.SUPPORT);
        //是否支持血压
        SharedPreferencesUtils.setParam(context, Commont.IS_B31_HAS_BP_KEY, functionDeviceSupportData.getBp() == EFunctionStatus.SUPPORT);
        //是否支持倒计时
        SharedPreferencesUtils.setParam(context, Commont.IS_SUPPORT_COUNT_DOWM, functionDeviceSupportData.getCountDown() == EFunctionStatus.SUPPORT);

        //是否支持血氧
        isSupportSpo2 = functionDeviceSupportData.getSpo2H() == EFunctionStatus.SUPPORT;
        SharedPreferencesUtils.setParam(context, Commont.IS_SUPPORT_SPO2, isSupportSpo2);

        //是否支持精准睡眠
        isSleepPrecisionData = functionDeviceSupportData.getPrecisionSleep() == EFunctionStatus.SUPPORT;
        SharedPreferencesUtils.setParam(context, Commont.IS_SUPPORT_precisionSleep, isSleepPrecisionData);
        Log.i(TAG, "是否支持精准睡眠 : " + isSleepPrecisionData);

        //是否疲劳度检测功能
        SharedPreferencesUtils.setParam(context, Commont.IS_B31S_FATIGUE_KEY, functionDeviceSupportData.getFatigue() == EFunctionStatus.SUPPORT);
        //B31是否支持呼吸率
        SharedPreferencesUtils.setParam(context, Commont.IS_B31_HEART, functionDeviceSupportData.getBeathFunction() == EFunctionStatus.SUPPORT);

        //是否支持心电
        SharedPreferencesUtils.setParam(context, Commont.IS_SUPPORT_ECG_KEY, functionDeviceSupportData.getEcg() == EFunctionStatus.SUPPORT);
    }


    //设置共同的属性
    private void setCommDevice() {
        SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.BATTERNUMBER, 0);//每次连接清空电量
        //同步用户信息，设置目标步数
        setDeviceUserData();

        //设置语言，根据系统的语言设置
        ELanguage languageData;
        String localelLanguage = Locale.getDefault().getLanguage();
        if (!WatchUtils.isEmpty(localelLanguage) && localelLanguage.equals("zh")) {    //中文
            Locale locales = MyApp.getInstance().getApplicationContext().getResources().getConfiguration().locale;
            String localCountry = locales.getCountry();
            if (localCountry.equals("TW")) {  //繁体
                languageData = ELanguage.CHINA_TRADITIONAL;
            } else {
                languageData = ELanguage.CHINA;
            }
        } else {
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
    }


    /**
     * 同步用户信息设置设备的目标步数，
     */
    public void setDeviceUserData() {
        //目标步数
        int sportGoal = (int) SharedPreferencesUtils.getParam(MyApp.getContext(), "b30Goal", 0);
        PersonInfoData personInfoData = WatchUtils.getUserPerson(sportGoal);
        if (personInfoData == null) {
            if (connBleHelpListener != null) {
                connBleHelpListener.connSuccState();
            }
            return;
        }

        MyApp.getInstance().getVpOperateManager().syncPersonInfo(new IBleWriteResponse() {
            @Override
            public void onResponse(int i) {
            }
        }, new IPersonInfoDataListener() {
            @Override
            public void OnPersoninfoDataChange(EOprateStauts eOprateStauts) {
                //同步用户信息成功
            }
        }, personInfoData);
    }


    /**
     * 获取手环基本数据
     */
    public void getDeviceMsgData() {
        // 获取步数
        Log.i(TAG, "获取步数");
        MyApp.getInstance().getVpOperateManager().readSportStep(bleWriteResponse, new ISportDataListener() {
            @Override
            public void onSportDataChange(SportData sportData) {
                //保存当天总步数
                B30HalfHourDB db = new B30HalfHourDB();
                db.setAddress(MyApp.getInstance().getMacAddress());
                db.setDate(WatchUtils.getCurrentDate());
                db.setType(B30HalfHourDao.TYPE_STEP);
                db.setOriginData("" + sportData.getStep());
                B30HalfHourDao.getInstance().saveOriginData(db);

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
                if (!WatchUtils.isEmpty(MyCommandManager.DEVICENAME))
                    bleName = MyCommandManager.DEVICENAME;
                // 保存总步数
                CommDBManager.getCommDBManager().saveCommCountStepDate(bleName, MyApp.getInstance().getMacAddress(),
                        WatchUtils.getCurrentDate(), sportData.getStep());


            }
        });
        //电量
        Log.i(TAG, "获取电量");
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
     * 读取所有的原始数据(包括睡眠数据)
     *
     * @param today true_只加载今天数据 false_加载三天
     */
    public void readAllHealthData(boolean today) {
        //读取健康数据，步数详情、心率、血压等
        readAllDeviceData(today);
    }

    //读取所有的健康数据，
    private void readAllDeviceData(boolean today) {
        if (sleepMap != null)
            sleepMap.clear();
        readOnlySleep(today);
    }


    //只读取睡眠
    private void readOnlySleep(boolean day) {
        if (sleepMap != null)
            sleepMap.clear();
        //单单读取睡眠的
        MyApp.getInstance().getVpOperateManager().readSleepData(bleWriteResponse, new ISleepDataListener() {
            @Override
            public void onSleepDataChange(SleepData sleepData) {
                if (sleepData == null)
                    return;
                //Log.e(TAG, "-----22----睡眠原始返回数据=" + sleepData.toString());
                if (sleepData instanceof SleepPrecisionData && isSleepPrecisionData) {
                    Log.i(TAG, "保存精准睡眠信息");
                    SleepPrecisionData sleepPrecisionData = (SleepPrecisionData) sleepData;
                    savePrecisionData(sleepPrecisionData);
                } else {
                    Log.i(TAG, "保存普通睡眠信息");
                    // 睡眠数据返回,会有多条数据
                    saveSleepData(sleepData);
                }
            }

            @Override
            public void onSleepProgress(float v) {
                //Log.e(TAG,"---------onSleepProgress="+v);
            }

            @Override
            public void onSleepProgressDetail(String s, int i) {
                //Log.e(TAG,"-------onSleepProgressDetail="+s+"--i="+i);
            }

            @Override
            public void onReadSleepComplete() {
                Message message = handler.obtainMessage();
                message.what = 1001;
                message.obj = day;
                handler.sendMessageDelayed(message, 3 * 1000);
            }
        }, day ? 2 : 3);
    }


    //保存精准睡眠
    private void savePrecisionData(SleepPrecisionData sleepPrecisionData) {
        try {
            if (sleepPrecisionData == null)
                return;
            Log.e(TAG, "--------精准睡眠原始数据=" + sleepPrecisionData.toString() + "=" + sleepPrecisionData.getSleepLine());
            TimeData downTimeData = sleepPrecisionData.getSleepDown();
            CusVPTimeData donwTime = new CusVPTimeData(downTimeData.getYear(), downTimeData.getMonth(),
                    downTimeData.getDay(), downTimeData.getHour(), downTimeData.getMinute(),
                    downTimeData.getSecond(), downTimeData.getWeekDay());

            TimeData upTimeData = sleepPrecisionData.getSleepUp();
            CusVPTimeData upTime = new CusVPTimeData(upTimeData.getYear(), upTimeData.getMonth(),
                    upTimeData.getDay(), upTimeData.getHour(), upTimeData.getMinute(),
                    upTimeData.getSecond(), upTimeData.getWeekDay());


            CusVPSleepPrecisionData cSleep = new CusVPSleepPrecisionData();

            cSleep.setDate(sleepPrecisionData.getDate());
            cSleep.setSleepDown(donwTime);
            cSleep.setSleepUp(upTime);
            cSleep.setAllSleepTime(sleepPrecisionData.getAllSleepTime());
            cSleep.setLowSleepTime(sleepPrecisionData.getLowSleepTime());
            cSleep.setDeepSleepTime(sleepPrecisionData.getDeepSleepTime());
            cSleep.setWakeCount(sleepPrecisionData.getWakeCount());
            cSleep.setSleepQulity(sleepPrecisionData.getSleepQulity());


            cSleep.setSleepTag(sleepPrecisionData.getSleepTag());
            cSleep.setGetUpScore(sleepPrecisionData.getGetUpScore());
            cSleep.setDeepScore(sleepPrecisionData.getDeepScore());
            cSleep.setSleepEfficiencyScore(sleepPrecisionData.getSleepEfficiencyScore());
            cSleep.setFallAsleepScore(sleepPrecisionData.getFallAsleepScore());
            cSleep.setSleepTimeScore(sleepPrecisionData.getSleepTimeScore());
            cSleep.setExitSleepMode(sleepPrecisionData.getExitSleepMode());
            cSleep.setDeepAndLightMode(sleepPrecisionData.getDeepAndLightMode());
            cSleep.setOtherDuration(sleepPrecisionData.getOtherDuration());
            cSleep.setFirstDeepDuration(sleepPrecisionData.getFirstDeepDuration());
            cSleep.setGetUpDuration(sleepPrecisionData.getGetUpDuration());
            cSleep.setGetUpToDeepAve(sleepPrecisionData.getGetUpToDeepAve());
            cSleep.setOnePointDuration(sleepPrecisionData.getOnePointDuration());
            cSleep.setAccurateType(sleepPrecisionData.getAccurateType());
            cSleep.setInsomniaTag(sleepPrecisionData.getInsomniaTag());
            cSleep.setInsomniaScore(sleepPrecisionData.getInsomniaScore());
            cSleep.setInsomniaTimes(sleepPrecisionData.getInsomniaTimes());
            cSleep.setInsomniaLength(sleepPrecisionData.getInsomniaLength());
            cSleep.setInsomniaBeanList(sleepPrecisionData.getInsomniaBeanList());
            cSleep.setStartInsomniaTime(sleepPrecisionData.getStartInsomniaTime());
            cSleep.setStopInsomniaTime(sleepPrecisionData.getStopInsomniaTime());
            cSleep.setInsomniaDuration(sleepPrecisionData.getInsomniaDuration());
            cSleep.setSleepSourceStr(sleepPrecisionData.getSleepSourceStr());
            cSleep.setLaster(sleepPrecisionData.getLaster());
            cSleep.setNext(sleepPrecisionData.getNext());
            cSleep.setSleepLine(sleepPrecisionData.getSleepLine());


            String dateStr = cSleep.getDate();
            Log.e(TAG, "---------dateStr=" + dateStr);
            if (precisionSleepMap.get(dateStr) == null) {
                precisionSleepMap.put(dateStr, cSleep);
            } else {
                //同一天的
                CusVPSleepPrecisionData tmpCusVpSleep = precisionSleepMap.get(dateStr);
                if (tmpCusVpSleep == null)
                    return;

                //入睡时间的分钟
                long tmpSleepDownTime = DateTimeUtils.formatDateToLong(tmpCusVpSleep.getSleepDown().getDateAndClockForSleepSecond());
                long cSleepDownTime = DateTimeUtils.formatDateToLong(cSleep.getSleepDown().getDateAndClockForSleepSecond());

                if (tmpSleepDownTime != cSleepDownTime) { //组合分段的睡眠数据
                    CusVPSleepPrecisionData resultSleepData = new CusVPSleepPrecisionData();
                    resultSleepData.setDate(dateStr);
                    //判断哪一个是最后的时间
                    //入睡时间
                    resultSleepData.setSleepDown(tmpSleepDownTime > cSleepDownTime ? cSleep.getSleepDown() : tmpCusVpSleep.getSleepDown());
                    //起床时间
                    resultSleepData.setSleepUp(tmpSleepDownTime > cSleepDownTime ? tmpCusVpSleep.getSleepUp() : cSleep.getSleepUp());
                    //所有睡眠时间
                    resultSleepData.setAllSleepTime(tmpSleepDownTime > cSleepDownTime ? tmpCusVpSleep.getAllSleepTime() : cSleep.getAllSleepTime());
                    //浅睡时间
                    resultSleepData.setLowSleepTime(tmpSleepDownTime > cSleepDownTime ? tmpCusVpSleep.getLowSleepTime() : cSleep.getLowSleepTime());
                    //深睡
                    resultSleepData.setDeepSleepTime(tmpSleepDownTime > cSleepDownTime ? tmpCusVpSleep.getDeepSleepTime() : cSleep.getDeepSleepTime());
                    //苏醒次数
                    resultSleepData.setWakeCount(tmpSleepDownTime > cSleepDownTime ? tmpCusVpSleep.getWakeCount() : cSleep.getWakeCount());
                    //睡眠质量
                    resultSleepData.setSleepQulity(Math.max(tmpCusVpSleep.getSleepQulity(), cSleep.getSleepQulity()));


                    resultSleepData.setSleepTag(tmpCusVpSleep.getSleepTag());
                    //起夜得分
                    resultSleepData.setGetUpScore(Math.max(tmpCusVpSleep.getGetUpScore(), cSleep.getGetUpScore()));
                    //深睡夜得分
                    resultSleepData.setDeepScore(Math.max(tmpCusVpSleep.getDeepScore(), cSleep.getDeepScore()));
                    //睡眠效率得分
                    resultSleepData.setSleepEfficiencyScore(Math.max(tmpCusVpSleep.getSleepEfficiencyScore(), cSleep.getSleepEfficiencyScore()));
                    //睡眠时长得分
                    resultSleepData.setSleepTimeScore(Math.max(tmpCusVpSleep.getSleepTimeScore(), cSleep.getSleepTimeScore()));
                    //设置退出睡眠模式
                    resultSleepData.setExitSleepMode(Math.max(tmpCusVpSleep.getExitSleepMode(), cSleep.getExitSleepMode()));
                    //入睡效率得分
                    resultSleepData.setFallAsleepScore(Math.max(tmpCusVpSleep.getFallAsleepScore(), cSleep.getFallAsleepScore()));
                    //获得深浅睡模式
                    resultSleepData.setDeepAndLightMode(tmpCusVpSleep.getDeepAndLightMode());
                    //其它睡眠时长
                    resultSleepData.setOtherDuration(Math.max(tmpCusVpSleep.getOtherDuration(), cSleep.getOtherDuration()));
                    resultSleepData.setGetUpToDeepAve(tmpCusVpSleep.getGetUpToDeepAve());
                    resultSleepData.setOnePointDuration(tmpCusVpSleep.getOnePointDuration());


                    resultSleepData.setAccurateType(tmpCusVpSleep.getAccurateType());
                    resultSleepData.setInsomniaTag(tmpCusVpSleep.getInsomniaTag());
                    resultSleepData.setInsomniaScore(tmpCusVpSleep.getInsomniaScore());
                    resultSleepData.setInsomniaTimes(tmpCusVpSleep.getInsomniaTimes());
                    resultSleepData.setInsomniaLength(tmpCusVpSleep.getInsomniaLength());
                    resultSleepData.setInsomniaBeanList(tmpCusVpSleep.getInsomniaBeanList());
                    resultSleepData.setStartInsomniaTime(tmpCusVpSleep.getStartInsomniaTime());
                    resultSleepData.setStopInsomniaTime(tmpCusVpSleep.getStopInsomniaTime());
                    resultSleepData.setInsomniaDuration(tmpCusVpSleep.getInsomniaDuration());
                    resultSleepData.setSleepSourceStr(tmpCusVpSleep.getSleepSourceStr());
                    resultSleepData.setLaster(tmpCusVpSleep.getLaster());
                    resultSleepData.setNext(tmpCusVpSleep.getNext());

                    String sleepLinStr1 = tmpCusVpSleep.getSleepLine();
                    String sleepLinStr2 = cSleep.getSleepLine();
                    resultSleepData.setSleepLine(tmpSleepDownTime > cSleepDownTime ? (sleepLinStr2 + sleepLinStr1) : (sleepLinStr1 + sleepLinStr2));
                    precisionSleepMap.put(dateStr, resultSleepData);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //版本协议为新版
    class ReadAllDataAsync extends AsyncTask<Boolean, Void, Void> {
        @Override
        protected Void doInBackground(Boolean... booleans) {
            if (!isSupportSpo2) {
                new LocalizeTool(MyApp.getContext()).putUpdateDate(WatchUtils
                        .obtainFormatDate(0));// 更新最后更新数据的时间
                if (connBleMsgDataListener != null) {
                    connBleMsgDataListener.onOriginData();
                }

                Intent intent = new Intent();
                intent.setAction(WatchUtils.B31_SPO2_COMPLETE);
                MyApp.getContext().sendBroadcast(intent);

                return null;
            }
            Boolean isToday = true;
            if (booleans.length != 0) {
                isToday = booleans[0];
            }
            if (isToday) {
                Log.i(TAG, "版本协议新版只读当日");
            } else Log.i(TAG, "版本协议新版读取3日数据");
            MyApp.getInstance().getVpOperateManager().readOriginData(bleWriteResponse,
                    new IOriginData3Listener() {
                        @Override
                        public void onOriginFiveMinuteListDataChange(List<OriginData3> list) {

                        }

                        @Override
                        public void onOriginHalfHourDataChange(OriginHalfHourData originHalfHourData) {
                            saveHalfHourData(originHalfHourData);
                        }

                        @Override
                        public void onOriginHRVOriginListDataChange(List<HRVOriginData> list) {
                            if (b31sSaveHrvAsyncTask != null &&
                                    b31sSaveHrvAsyncTask.getStatus() == Status.RUNNING) {
                                b31sSaveHrvAsyncTask.cancel(true);
                                b31sSaveHrvAsyncTask = null;
                            }
                            b31sSaveHrvAsyncTask = new B31sSaveHrvAsyncTask();
                            b31sSaveHrvAsyncTask.execute(list);
                        }

                        @Override
                        public void onOriginSpo2OriginListDataChange(List<Spo2hOriginData> list) {
                            if (b31SaveSpo2AsyncTask != null &&
                                    b31SaveSpo2AsyncTask.getStatus() == Status.RUNNING) {
                                b31SaveSpo2AsyncTask.cancel(true);
                                b31SaveSpo2AsyncTask = null;
                            }
                            b31SaveSpo2AsyncTask = new B31SaveSpo2AsyncTask();
                            b31SaveSpo2AsyncTask.execute(list);
                        }

                        @Override
                        public void onReadOriginProgressDetail(int i, String s, int i1, int i2) {

                        }

                        @Override
                        public void onReadOriginProgress(float v) {

                        }

                        @Override
                        public void onReadOriginComplete() {
                            new LocalizeTool(MyApp.getContext()).putUpdateDate(WatchUtils
                                    .obtainFormatDate(0));// 更新最后更新数据的时间
                            if (connBleMsgDataListener != null) {
                                connBleMsgDataListener.onOriginData();
                            }
                        }
                    }, isToday ? 1 : 3);
            return null;
        }
    }


    // 读取版本协议为0的数据
    private void readOldVersionData(boolean isToday) {
        Log.e(TAG, "------老版本读取健康数据=" + isToday);
        VPOperateManager.getMangerInstance(MyApp.getContext()).readOriginData(bleWriteResponse, new IOriginDataListener() {
            @Override
            public void onOringinFiveMinuteDataChange(OriginData originData) {

            }

            @Override
            public void onOringinHalfHourDataChange(OriginHalfHourData originHalfHourData) {
                Log.e(TAG, "------onOringinHalfHourDataChange=" + originHalfHourData.toString());
                saveHalfHourData(originHalfHourData);
            }

            @Override
            public void onReadOriginProgressDetail(int i, String s, int i1, int i2) {

            }

            @Override
            public void onReadOriginProgress(float v) {
                Log.i(TAG, "老版本健康数据读取进度 " + v);
            }

            @Override
            public void onReadOriginComplete() {
                Log.i(TAG, "老版本读取取运动,心率,血压数据结束");
                // 读取取运动,心率,血压数据结束
                new LocalizeTool(MyApp.getContext()).putUpdateDate(WatchUtils
                        .obtainFormatDate(0));// 更新最后更新数据的时间
                if (connBleMsgDataListener != null) {
                    Log.i(TAG, "老版本读取取运动,心率,血压数据结束.结束回调执行");
                    connBleMsgDataListener.onOriginData();
                }

                //开始读取血氧和HRV数据
                if (isSupportSpo2) {
                    try {
                        Log.i(TAG, "开始读取血氧和HRV数据");
                        Intent intent = new Intent(MyApp.getContext(), NewReadHRVAnSpo2DatatService.class);
                        MyApp.getInstance().getApplicationContext().startService(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, isToday ? 1 : 2);
    }


    /**
     * 保存手环返回的睡眠数据到本地数据库
     *
     * @param
     */
    private void saveSleepData(SleepData sleepData) {
        if (sleepData == null) return;
        Log.e("-------设备睡眠数据---", gson.toJson(sleepData) + "");
        if (sleepMap.get(sleepData.getDate()) == null) {
            sleepMap.put(sleepData.getDate(), sleepData);
        } else {
            //sleepMap.put(sleepData.getDate(),sleepData);
            Log.e(TAG, "---------sleepMap=" + sleepMap.toString());
            if (sleepMap.get(sleepData.getDate()).getDate().equals(sleepData.getDate())) {  //同一天的
                if (!sleepMap.get(sleepData.getDate()).getSleepLine().equals(sleepData.getSleepLine())) {
                    // map 中已经保存的
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
                    resultSlee.setAllSleepTime(tempSleepData.getAllSleepTime()
                            + sleepData.getAllSleepTime() + sleepStatus * 5);
                    resultSlee.setSleepLine(WatchUtils.comPariDateDetail(time1, time2) ?
                            (tempSleepData.getSleepLine() + stringBuffer + "" + sleepData.getSleepLine()) :
                            (sleepData.getSleepLine() + stringBuffer + "" + tempSleepData.getSleepLine()));
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
        if (WatchUtils.isEmpty(mac))
            return;
        String dateSport = saveSportData(mac, data.getHalfHourSportDatas());
        saveStepData(mac, dateSport, data.getAllStep());
        saveRateData(mac, data.getHalfHourRateDatas());
        Log.d(TAG, "------------心率=" + data.getHalfHourRateDatas().toString());
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
        Log.e(TAG, "-------运动数据=" + gson.toJson(sportData));

        String date = sportData.get(0).getDate();
        B30HalfHourDB db = new B30HalfHourDB();
        db.setAddress(mac);
        db.setDate(date);
        db.setType(B30HalfHourDao.TYPE_SPORT);
        db.setOriginData(gson.toJson(sportData));
        db.setUpload(0);
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
        db.setOriginData(gson.toJson(rateData));
        db.setUpload(0);
        B30HalfHourDao.getInstance().saveOriginData(db);

        String bleName = "B31";
        if (!WatchUtils.isEmpty(MyCommandManager.DEVICENAME)) bleName = MyCommandManager.DEVICENAME;
        //保存心率数据
        Integer[] heartStr = CommCalUtils.calHeartData(rateData);
        CommDBManager.getCommDBManager().saveCommHeartData(bleName, WatchUtils.getSherpBleMac(MyApp.getContext()),
                rateData.get(0).getDate(), heartStr[0], heartStr[1], heartStr[2]);
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
        //Log.e("-------血压数据", gson.toJson(bpData));
        B30HalfHourDao.getInstance().saveOriginData(db);

        //保存血压的数据
        Integer[] bloodStr = CommCalUtils.calBloodData(bpData);
        Log.e(TAG, "-------血压平均数据=" + Arrays.toString(bloodStr));
        CommDBManager.getCommDBManager().saveCommBloodDb(WatchUtils.getSherpBleMac(MyApp.getContext()), bpData.get(0).getDate(),
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
        if (date == null || date.equals(WatchUtils.getCurrentDate())) return;
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
        Log.e(TAG, " B31  " + mac + "  " + date + "  " + stepCurr);
        //当天的汇总步数不在此保存，这里是根据详细步数累加步数，和返回的有出入
        if (!WatchUtils.isEquesValue(date)) {
            //保存总步数
            CommDBManager.getCommDBManager().saveCommCountStepDate(bleName, mac, date, stepCurr);
            if (stepCurr > stepLocal) {
                B30HalfHourDB db = new B30HalfHourDB();
                db.setAddress(mac);
                db.setDate(date);
                db.setType(B30HalfHourDao.TYPE_STEP);
                db.setOriginData("" + stepCurr);
                db.setUpload(0);
                Log.e(TAG, "---------保存步数总数=" + db.toString());
                B30HalfHourDao.getInstance().saveOriginData(db);
            }
        }
    }

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
}
