//package com.example.bozhilun.android.b30.service;
//
//import android.app.IntentService;
//import android.content.Intent;
//import android.support.annotation.Nullable;
//import android.text.TextUtils;
//
//import com.example.bozhilun.android.Commont;
//import com.example.bozhilun.android.LogTestUtil;
//import com.example.bozhilun.android.MyApp;
//import com.example.bozhilun.android.b30.bean.B30HalfHourDB;
//import com.example.bozhilun.android.b30.bean.B30HalfHourDao;
//import com.example.bozhilun.android.b30.bean.UpHeartBean;
//import com.example.bozhilun.android.siswatch.utils.WatchUtils;
//import com.example.bozhilun.android.util.MyLogUtil;
//import com.example.bozhilun.android.util.OkHttpTool;
//import com.example.bozhilun.android.util.SharedPreferencesUtils;
//import com.example.bozhilun.android.util.URLs;
//import com.example.bozhilun.android.util.VerifyUtil;
//import com.google.gson.Gson;
//import com.google.gson.JsonSyntaxException;
//import com.google.gson.reflect.TypeToken;
//import com.veepoo.protocol.listener.base.IBleWriteResponse;
//import com.veepoo.protocol.listener.data.ILanguageDataListener;
//import com.veepoo.protocol.model.datas.HalfHourBpData;
//import com.veepoo.protocol.model.datas.HalfHourRateData;
//import com.veepoo.protocol.model.datas.HalfHourSportData;
//import com.veepoo.protocol.model.datas.LanguageData;
//import com.veepoo.protocol.model.datas.SleepData;
//import com.veepoo.protocol.model.datas.TimeData;
//import com.veepoo.protocol.model.enums.ELanguage;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * 上传手环本地数据用的IntentService(上传完成自动销毁的服务)
// *
// * @author XuBo 2018-09-20
// */
//public class DateUploadServiceGD extends IntentService {
//
//    private final String TAG = "------AAA-GD";
//    /**
//     * 上传是否完成的状态: 运动数据
//     */
//    private final int STATE_SPORT = 1;
//    /**
//     * 上传是否完成的状态: 睡眠数据
//     */
//    private final int STATE_SLEEP = 2;
//    /**
//     * 上传是否完成的状态: 心率数据
//     */
//    private final int STATE_RATE = 3;
//    /**
//     * 上传是否完成的状态: 血压数据
//     */
//    private final int STATE_BP = 4;
//    /**
//     * 当天日期
//     */
//    private final String CURR_DATE = WatchUtils.getCurrentDate();
//    /**
//     * 需要上传的数据源: 运动数据
//     */
//    private List<B30HalfHourDB> sportData;
//    /**
//     * 需要上传的数据源: 睡眠数据
//     */
//    private List<B30HalfHourDB> sleepData;
//    /**
//     * 需要上传的数据源: 心率数据
//     */
//    private List<B30HalfHourDB> rateData;
//    /**
//     * 需要上传的数据源: 血压数据
//     */
//    private List<B30HalfHourDB> bpData;
//    /**
//     * 处理Json工具类
//     */
//    private Gson gson;
//    /**
//     * 手环MAC
//     */
//    private String deviceCode;
//    /**
//     * 用户ID
//     */
//    private String userId;
//
//    public DateUploadServiceGD() {
//        super("DateUploadService");
//    }
//
//    @Override
//    protected void onHandleIntent(@Nullable Intent intent) {
//        MyApp.getInstance().setUploadDateGD(true);// 正在上传数据,写到全局,保证同时只有一个本服务在运行
//        gson = new Gson();
//        deviceCode = (String) SharedPreferencesUtils.readObject(this, Commont.BLEMAC);
//        userId = (String) SharedPreferencesUtils.readObject(this, "userId");
//        if (TextUtils.isEmpty(deviceCode) || TextUtils.isEmpty(userId)) return;
//
//
//        registerOrLogin();
//    }
//
//
//    private void registerOrLogin() {
//        Map<String, String> params = new HashMap<>();
//        params.put("CARDID", deviceCode);
//        //LogTestUtil.e(TAG, "游客注册或者登陆" + params.toString());
//        OkHttpTool.getInstance().doRequest(URLs.GDHTTPS + "/SendCardId",
//                params, this, new OkHttpTool.HttpResult() {
//                    @Override
//                    public void onResult(String result) {
//                        if (!WatchUtils.isEmpty(result)) {
//                            LogTestUtil.e(TAG, "游客注册或者登陆上传返回" + result);
//                            if (resultSuccess(result)) {
//                                findNotUploadData();// 1.找出要上传的所有数据
//                            }
//                        }
//
//                    }
//                }, true);
//    }
//
//    /**
//     * 查找本地数据所有没上传的数据
//     */
//    private void findNotUploadData() {
//        sportData = B30HalfHourDao.getInstance().findNotUploadDataGD(deviceCode, B30HalfHourDao.TYPE_SPORT);
//        sleepData = B30HalfHourDao.getInstance().findNotUploadDataGD(deviceCode, B30HalfHourDao.TYPE_SLEEP);
//        rateData = B30HalfHourDao.getInstance().findNotUploadDataGD(deviceCode, B30HalfHourDao.TYPE_RATE);
//        bpData = B30HalfHourDao.getInstance().findNotUploadDataGD(deviceCode, B30HalfHourDao.TYPE_BP);
//        if (sportData != null) LogTestUtil.e(TAG, "未上传数据条数 运动: " + sportData.size());
//        if (sleepData != null) LogTestUtil.e(TAG, "未上传数据条数 睡眠: " + sleepData.size());
//        if (rateData != null) LogTestUtil.e(TAG, "未上传数据条数 心率: " + rateData.size());
//        if (bpData != null) LogTestUtil.e(TAG, "未上传数据条数 血压: " + bpData.size());
//        if ((sportData != null && !sportData.isEmpty())
//                || (sleepData != null && !sleepData.isEmpty())
//                || (rateData != null && !rateData.isEmpty())
//                || (bpData != null && !bpData.isEmpty())) {
//            //数据库中存在数据------开始上传
//            LogTestUtil.e(TAG, "数据库中存在数据------开始上传===========步数");
//            uploadSportData(0);// 2.按一个个类型上传: 运动->睡眠->心率->血压
//        }
//
//    }
//
//    /**
//     * 上传运动数据
//     */
//    private void uploadSportData(int position) {
//        if (sportData == null || sportData.isEmpty() || position >= sportData.size()) {
//            LogTestUtil.e(TAG, "步数上传完成，开始上传睡眠");
//            uploadSleepData(0);// 运动数据上传完了,换着上传睡眠数据
//            return;
//        }
//        B30HalfHourDB dbData = sportData.get(position);
//        String date = dbData.getDate();
//        String originData = dbData.getOriginData();
//        LogTestUtil.e(TAG, "读到的步数数据："+date+"---" + originData);
//        setStep(originData, position);
//    }
//
//    /**
//     * 转换数据格式上传数据-----------好友功能详细数据
//     *
//     * @param originData
//     * @param position
//     */
//    void setStep(String originData, final int position) {
//        List<HalfHourSportData> dataList = gson.fromJson(originData, new TypeToken<List<HalfHourSportData>>() {
//        }.getType());
//        if (dataList == null || dataList.isEmpty()) return;
//        String date = dataList.get(0).getDate();
//        LogTestUtil.e(TAG, "部署时间："+date);
//        if (!dataList.isEmpty()) {
//            int all = 0;
//            for (int i = 0; i < 48; i++) {
//                if (i < dataList.size()) {
//                    all = dataList.get(i).getStepValue();
//                    TimeData time = dataList.get(i).getTime();
//                    String stringHour = "00";
//                    String stringMinute = "00";
//                    int hour = time.getHour();
//                    int minute = time.getMinute();
//                    if (hour >= 10) {
//                        stringHour = "" + hour;
//                    } else {
//                        stringHour = "0" + hour;
//                    }
//                    if (minute >= 10) {
//                        stringMinute = "" + minute;
//                    } else {
//                        stringMinute = "0" + minute;
//                    }
//                    String dates = stringHour + "-" + stringMinute;
//                    /**
//                     * 判断为0不上传
//                     */
//                    if (all > 0) {
//                        Map<String, Object> params = new HashMap<>();
//                        params.put("WALKTIME", "0");
//                        params.put("CARDID", deviceCode);
//                        params.put("STEPS", all);
//                        params.put("DISTANCE", 0);
//                        params.put("CALORIE", 0);
//                        //LogTestUtil.e(TAG, "---------上传步数时间----" + date + " " + dates + "  ------- " + all);
//                        params.put("CDATE", date + " " + dates + "-00");
//                        LogTestUtil.e(TAG, "上传步数参数:" + params.toString());
//                        requestStep(URLs.GDHTTPS + "/SendWalkRecord", params, STATE_SPORT, position);
//                    } else {
//                        String result = "{\"STATUS\":\"500\",\"CARDID\":\"E7:A7:0F:11:BE:B5\",\"USERNAME\":\"步数为零\",\"MESSAGE\":\"Duplicate entry 'E7:A7:0F:11:BE:B5-2018-12-20 05:00:00' for key 'PRIMARY'\"}";
//                        if (i == dataList.size() - 1)
//                            handlerResult(result, STATE_SPORT, position);
//                    }
//                }
//            }
//        }
//    }
//
//    /**
//     * 分钟转换时分秒
//     *
//     * @param minutes
//     * @return
//     */
//    public String remainTimeStr(int minutes) {
//        int hour = minutes / 60;
//        int minute = minutes % 60;
//        int seconds = minutes * 60 % 60;
//        return (hour > 9 ? hour : "0" + hour) + ":" + (minute > 9 ? minute : "0" + minute) + ":" + (seconds > 9 ? seconds : "0" + seconds);
//    }
//
//
//    /**
//     * 上传睡眠数据
//     */
//    private void uploadSleepData(int position) {
//        if (sleepData == null || sleepData.isEmpty() || position >= sleepData.size()) {
//            LogTestUtil.e(TAG, "睡眠数据上传完成，开始上传心率数据");
//            uploadRateData(0);// 睡眠数据上传完了,换着上传心率数据
//            return;
//        }
//        B30HalfHourDB dbData = sleepData.get(position);
//        if (dbData != null) {
//            String originData = dbData.getOriginData();
//            if (TextUtils.isEmpty(originData)) return;
//            LogTestUtil.e(TAG, "读到的睡眠数据" + dbData.getDate() + "------" + originData);
//            SleepData sleepData = gson.fromJson(originData, SleepData.class);
//            submitSleepData(sleepData, position);
//        }
//    }
//
//    /**
//     * 请求提交睡眠数据到后台
//     *
//     * @param sleepData 手环的睡眠数据
//     */
//    private void submitSleepData(SleepData sleepData, final int position) {
//        int deepSleepTime = sleepData.getDeepSleepTime();
//        int lowSleepTime = sleepData.getLowSleepTime();
//        //LogTestUtil.e(TAG, "睡眠时长-  分钟" + (deepSleepTime + lowSleepTime) + "睡眠日期-" + sleepData.getDate());
//        Map<String, Object> params = new HashMap<>();
//        int allTimes = deepSleepTime + lowSleepTime;
//        String times = remainTimeStr(allTimes);
//        //LogTestUtil.e(TAG, "------睡眠时间是：" + times + "=======" + sleepData.getDate());
//        params.put("SLEEPTIME", times);
//        params.put("CARDID", deviceCode);
//        params.put("CDATE", sleepData.getDate() + " 09-00-00");
//        MyLogUtil.e(TAG, "睡眠上传参数：" + params.toString());
//        request(URLs.GDHTTPS + "/SendSleepRecord", params, STATE_SLEEP, position);
//    }
//
//
//    /**
//     * 上传心率数据
//     */
//    private void uploadRateData(int position) {
//        if (rateData == null || rateData.isEmpty() || position >= rateData.size()) {
//            LogTestUtil.e(TAG, "心率数据上传完成，开始上传血压数据");
//            uploadBpData(0);// 心率数据上传完了,换着上传血压数据
//            return;
//        }
//
//        B30HalfHourDB b30HalfHourDB = rateData.get(position);
//        if (b30HalfHourDB != null) {
//            String originData = b30HalfHourDB.getOriginData();
//            if (!TextUtils.isEmpty(originData)) {
//                List<HalfHourRateData> dataList = gson.fromJson(originData, new TypeToken<List<HalfHourRateData>>() {
//                }.getType());
//
//                for (int i = 0; i < dataList.size(); i++) {
//                    HalfHourRateData halfHourRateData = dataList.get(i);
//                    String date = halfHourRateData.getDate();
//                    int rateValue = halfHourRateData.getRateValue();
//                    LogTestUtil.e(TAG, "拿到的心率数据准备上传-----T:"+date+"===V:"+rateValue);
//                    if (i == dataList.size() - 1) {
//                        requestDD(rateValue+"", date.replace(":", "-"), STATE_RATE, position, true);
//                    } else {
//                        requestDD(rateValue+"", date.replace(":", "-"), STATE_RATE, position, false);
//                    }
//                }
//            }
//        }
//    }
//
//
//    private void requestDD(String heart, String time, final int type, final int position, final boolean isHandler) {
//        /**
//         * 判断为0不上传
//         */
//        if (Integer.valueOf(heart) > 0) {
//            Map<String, Object> params = new HashMap<>();
//            params.put("PU", heart);
//            params.put("CARDID", deviceCode);
//            params.put("CDATE", time);
//            MyLogUtil.e(TAG, "心率上传参数" + params.toString());
//            String path = URLs.GDHTTPS + "/SendPUInfo";
//            OkHttpTool.getInstance().doRequest(path, params, this, new OkHttpTool.HttpResult() {
//                @Override
//                public void onResult(String result) {
//                    if (WatchUtils.isEmpty(result))
//                        return;
//                    LogTestUtil.e(TAG, "心率上传返回" + result);
//                    if (isHandler) {
//                        handlerResult(result, type, position);
//                    }
//                }
//            });
//        } else {
//            String result = "{\"STATUS\":\"500\",\"CARDID\":\"D6:64:CB:24:7E:74\",\"USERNAME\":\"心率为零\", \"MESSAGE\":\"Duplicate entry 'D6:64:CB:24:7E:74-2018-12-07 03:30:00' for key 'PRIMARY'\"}";
//            if (isHandler) handlerResult(result, type, position);
//        }
//
//    }
//
//
//
//
//    IBleWriteResponse iBleWriteResponse = new IBleWriteResponse() {
//        @Override
//        public void onResponse(int i) {
//
//        }
//    };
//
//    ILanguageDataListener iLanguageDataListener = new ILanguageDataListener() {
//        @Override
//        public void onLanguageDataChange(LanguageData languageData) {
//            if (languageData != null && languageData.getLanguage() != null) {
//                LogTestUtil.e("-----", languageData.getLanguage().toString());
//            }
//        }
//    };
//
//    /**
//     * 上传血压数据
//     */
//    private void uploadBpData(int position) {
//        if (bpData == null || bpData.isEmpty() || position >= bpData.size()) {
//            // 血压数据上传完了,到此结束
//            //LogTestUtil.e(TAG, "全部数据上传完成,改变上传状态，没在上传数据------此处设置设备语言");
//            //数据上传完咯，改变设备语言
//            //判断设置语言
//            boolean zh = VerifyUtil.isZh(MyApp.getInstance());
//            if (zh) {
//                MyApp.getInstance().getVpOperateManager().settingDeviceLanguage(iBleWriteResponse, iLanguageDataListener, ELanguage.CHINA);
//            } else {
//                MyApp.getInstance().getVpOperateManager().settingDeviceLanguage(iBleWriteResponse, iLanguageDataListener, ELanguage.ENGLISH);
//            }
//            MyApp.getInstance().setUploadDate(false);
//            return;
//        }
//        B30HalfHourDB b30HalfHourDB = bpData.get(position);
//        if (b30HalfHourDB != null) {
//            String originData = b30HalfHourDB.getOriginData();
//            if (TextUtils.isEmpty(originData)) return;
//            List<HalfHourBpData> dataList = gson.fromJson(originData, new TypeToken<List<HalfHourBpData>>() {
//            }.getType());
//            for (int i = 0; i < dataList.size(); i++) {
//                HalfHourBpData halfHourBpData = dataList.get(i);
//                String date = obtainDate(halfHourBpData.getTime());
//                int highValue = halfHourBpData.getHighValue();
//                int lowValue = halfHourBpData.getLowValue();
//                LogTestUtil.e(TAG, "血压数据：" + lowValue + "===" + highValue + "===" + date.replace(":", "-"));
//                if (i == dataList.size() - 1) {
//                    requestBp(lowValue+"", highValue+"", date.replace(":", "-"), STATE_BP, position, true);
//                } else {
//                    requestBp(lowValue+"", highValue+"", date.replace(":", "-"), STATE_BP, position, false);
//                }
//            }
//        }
//
//    }
//
//
//
//    /**
//     * 将手环数据的日期转换为提交到后台的格式
//     */
//    private String obtainDate(TimeData data) {
//        String month = data.month < 10 ? "0" + data.month : "" + data.month;
//        String day = data.day < 10 ? "0" + data.day : "" + data.day;
//        String hour = data.hour < 10 ? "0" + data.hour : "" + data.hour;
//        String minute = data.minute < 10 ? "0" + data.minute : "" + data.minute;
//        String second = data.second < 10 ? "0" + data.second : "" + data.second;
//        return data.year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
//    }
//
//    /**
//     * 执行OkHttp请求操作,上传睡眠和步数
//     *
//     * @param path     地址
//     * @param params   参数
//     * @param type     类型
//     * @param position 数据源下标,用于上传数据成功后更新本地数据库
//     */
//    private void request(String path, Map<String, Object> params, final int type, final int
//            position) {
//        OkHttpTool.getInstance().doRequest(path, params, this, new OkHttpTool.HttpResult() {
//            @Override
//            public void onResult(String result) {
//                if (WatchUtils.isEmpty(result))
//                    return;
//                handlerResult(result, type, position);
//            }
//        });
//    }
//
//    /**
//     * 步数上传返回
//     *
//     * @param path
//     * @param params
//     * @param type
//     * @param position
//     */
//    private void requestStep(String path, Map<String, Object> params, final int type, final int
//            position) {
//        OkHttpTool.getInstance().doRequest(path, params, this, new OkHttpTool.HttpResult() {
//            @Override
//            public void onResult(String result) {
//                if (WatchUtils.isEmpty(result))
//                    return;
//                LogTestUtil.e(TAG, "步数上传返回" + result);
//                handlerResult(result, type, position);
//            }
//        });
//    }
//
//
//    private void requestBp(String dbp, String sbp, String time, final int type, final int position, final boolean ishandler) {
//
//        /**
//         * 判断为0不上传
//         */
//        if (Integer.valueOf(sbp) > 0 || Integer.valueOf(dbp) > 0) {
//            Map<String, Object> params = new HashMap<>();
//            params.put("DBP", dbp);
//            params.put("SBP", sbp);
//            params.put("HB", "0");
//            params.put("CARDID", deviceCode);
//            params.put("CDATE", time);
//            String path = URLs.GDHTTPS + "/SendBPInfo";
//            LogTestUtil.e(TAG, "------AAA-GD: 血压上传参数" + params.toString());
//            OkHttpTool.getInstance().doRequest(path, params, this, new OkHttpTool.HttpResult() {
//                @Override
//                public void onResult(String result) {
//                    if (!WatchUtils.isEmpty(result)){
//                        LogTestUtil.e(TAG, "------AAA-GD: 血压上传返回" + result);
//                        if (ishandler) handlerResult(result, type, position);
//                    }
//                }
//            });
//        } else {
//            String result = "{\"STATUS\":\"500\",\"CARDID\":\"D6:64:CB:24:7E:74\",\"USERNAME\":\"血压为零\", \"MESSAGE\":\"Duplicate entry 'D6:64:CB:24:7E:74-2018-12-07 03:30:00' for key 'PRIMARY'\"}";
//            if (ishandler) handlerResult(result, type, position);
//        }
//    }
//
//    /**
//     * 处理请求结果
//     */
//    private void handlerResult(String result, int type, int position) {
//
//        if (type == STATE_SPORT) {
//            LogTestUtil.e(TAG, type + ",最后一条上传步数数据结果:" + result + ",position:" + position);
//            if (resultSuccess(result)) changeUpload(type, position);
//        }else if (type == STATE_SLEEP){
//            LogTestUtil.e(TAG, type + ",上传睡眠数据结果:" + result + ",position:" + position);
//            if (resultSleepSuccess(result)) changeUpload(type, position);
//        }else if (type == STATE_RATE){
//            LogTestUtil.e(TAG, type + ",最后一条心率数据结果:" + result + ",position:" + position);
//            if (resultSuccess(result)) changeUpload(type, position);
//        }else if (type == STATE_BP){
//            LogTestUtil.e(TAG, type + ",最后一条上传血压数据结果:" + result + ",position:" + position);
//            if (resultSuccess(result)) changeUpload(type, position);
//        }
//
//        switch (type) {
//            case STATE_SPORT:
//                uploadSportData(++position);// 上传下一条运动数据
//                 LogTestUtil.e(TAG, "步数上传第" + position + "条");
//                break;
//            case STATE_SLEEP:
//                uploadSleepData(++position);// 上传下一条睡眠数据
//                 LogTestUtil.e(TAG, "睡眠上传第" + position + "条");
//                break;
//            case STATE_RATE:
//                uploadRateData(++position);// 上传下一条心率数据
//                LogTestUtil.e(TAG, "心率上传第" + position + "条");
//                break;
//            case STATE_BP:
//                uploadBpData(++position);// 上传下一条血压数据
//                LogTestUtil.e(TAG, "血压上传第" + position + "条");
//                break;
//        }
//    }
//
//    /**
//     * 请求结果是否是成功的
//     *
//     * @param result 请求结果
//     * @return 是否成功
//     */
//    private boolean resultSleepSuccess(String result) {
//        if (WatchUtils.isEmpty(result)) return false;
//        ResultVo resultVo = null;
//        try {
//            resultVo = gson.fromJson(result, ResultVo.class);
//        } catch (JsonSyntaxException e) {
//            e.printStackTrace();
//        }
//        final String RESULT_CODE = "200";// 上传数据成功结果码
//        if (resultVo != null) {
//            if (resultVo.STATUS.equals(RESULT_CODE)) {
//                return resultVo.STATUS.equals(RESULT_CODE);
//            } else {
//                return false;
//            }
//        } else {
//            return false;
//        }
//        //return resultVo != null && resultVo.STATUS.equals(RESULT_CODE);
//    }
//
//
//    /**
//     * 请求结果是否是成功的
//     *
//     * @param result 请求结果
//     * @return 是否成功
//     */
//    private boolean resultSuccess(String result) {
//        if (WatchUtils.isEmpty(result)) return false;
//        ResultVo resultVo = null;
//        try {
//            resultVo = gson.fromJson(result, ResultVo.class);
//        } catch (JsonSyntaxException e) {
//            e.printStackTrace();
//        }
//        //{"STATUS":"500","CARDID":"D6:64:CB:24:7E:74","USERNAME":"使用者", "MESSAGE":"Duplicate entry 'D6:64:CB:24:7E:74-2018-12-07 03:30:00' for key 'PRIMARY'"},position:1
//        //LogTestUtil.e(TAG, "=====返回" + (resultVo != null && resultVo.MESSAGE.contains("PRIMARY")));
//        final String RESULT_CODE = "200";// 上传数据成功结果码
//        if (resultVo != null) {
//            if (resultVo.STATUS.equals(RESULT_CODE)) {
//                return resultVo.STATUS.equals(RESULT_CODE);
//            } else if (resultVo.MESSAGE.contains("PRIMARY")) {
//                return true;
//            } else {
//                return false;
//            }
//        } else {
//            return false;
//        }
//        //return resultVo != null && resultVo.STATUS.equals(RESULT_CODE);
//    }
//
//    /**
//     * 改变本地数据库上传数据状态
//     */
//    private void changeUpload(int type, int position) {
//        B30HalfHourDB dbData = null;
//        switch (type) {
//            case STATE_SPORT:
//                dbData = sportData.get(position);
//                break;
//            case STATE_SLEEP:
//                dbData = sleepData.get(position);
//                break;
//            case STATE_RATE:
//                dbData = rateData.get(position);
//                break;
//            case STATE_BP:
//                dbData = bpData.get(position);
//                break;
//        }
//        if (dbData != null && !dbData.getDate().equals(CURR_DATE)) {
//            dbData.setUploadGD(1);// 只要不是当天的数据,都把本地状态设为"已上传"
//            dbData.save();//因为当天数据还有可能不停的更新和上传
//        }
//    }
//
//    /**
//     * 内部类,请求结果模型
//     */
//    private class ResultVo {
//
//        /**
//         * "STATUS":"200"
//         * CARDID : D6:64:CB:24:7E:74
//         * USERNAME : 使用者
//         * MESSAGE : INSERT SUCCESSFUL
//         */
//        String STATUS;
//        private String CARDID;
//        private String USERNAME;
//        private String MESSAGE;
//
//        public String getSTATUS() {
//            return STATUS;
//        }
//
//        public void setSTATUS(String STATUS) {
//            this.STATUS = STATUS;
//        }
//
//
//        public String getCARDID() {
//            return CARDID;
//        }
//
//        public void setCARDID(String CARDID) {
//            this.CARDID = CARDID;
//        }
//
//        public String getUSERNAME() {
//            return USERNAME;
//        }
//
//        public void setUSERNAME(String USERNAME) {
//            this.USERNAME = USERNAME;
//        }
//
//        public String getMESSAGE() {
//            return MESSAGE;
//        }
//
//        public void setMESSAGE(String MESSAGE) {
//            this.MESSAGE = MESSAGE;
//        }
//    }
//
//    /**
//     * 内部类,健康数据最终提交数据模型
//     */
//    private class HealthParamVo {
//        List<HealthVo> data;
//    }
//
//    /**
//     * 内部类,健康数据模型
//     */
//    private class HealthVo {
//        /**
//         * 用户ID
//         */
//        String userId;
//        /**
//         * 手环MAC地址
//         */
//        String deviceCode;
//        /**
//         * 心率
//         */
//        String heartRate;
//        /**
//         * 血氧
//         */
//        String bloodOxygen;
//        /**
//         * 低压
//         */
//        String systolic;
//        /**
//         * 高压
//         */
//        String diastolic;
//        /**
//         * 是否合格,1
//         */
//        String status;
//        /**
//         * yyyy-MM-dd HH:mm
//         */
//        String date;
//
//        public String getUserId() {
//            return userId;
//        }
//
//        public void setUserId(String userId) {
//            this.userId = userId;
//        }
//
//        public String getDeviceCode() {
//            return deviceCode;
//        }
//
//        public void setDeviceCode(String deviceCode) {
//            this.deviceCode = deviceCode;
//        }
//
//        public String getHeartRate() {
//            return heartRate;
//        }
//
//        public void setHeartRate(String heartRate) {
//            this.heartRate = heartRate;
//        }
//
//        public String getBloodOxygen() {
//            return bloodOxygen;
//        }
//
//        public void setBloodOxygen(String bloodOxygen) {
//            this.bloodOxygen = bloodOxygen;
//        }
//
//        public String getSystolic() {
//            return systolic;
//        }
//
//        public void setSystolic(String systolic) {
//            this.systolic = systolic;
//        }
//
//        public String getDiastolic() {
//            return diastolic;
//        }
//
//        public void setDiastolic(String diastolic) {
//            this.diastolic = diastolic;
//        }
//
//        public String getStatus() {
//            return status;
//        }
//
//        public void setStatus(String status) {
//            this.status = status;
//        }
//
//        public String getDate() {
//            return date;
//        }
//
//        public void setDate(String date) {
//            this.date = date;
//        }
//    }
//
//    /**
//     * 内部类,运动数据模型
//     */
//    private class SportVo {
//        /**
//         * yyyy-MM-dd
//         */
//        String date;
//        /**
//         * 步数
//         */
//        String step;
//        /**
//         * 卡路里
//         */
//        String kcal;
//        /**
//         * 距离
//         */
//        String dis;
//        /**
//         * 时长,0
//         */
//        String timeLen;
//        /**
//         * 是否合格,1
//         */
//        String status;
//
//        TimeData time;
//
//        public TimeData getTime() {
//            return time;
//        }
//
//        public void setTime(TimeData time) {
//            this.time = time;
//        }
//
//        public String getDate() {
//            return date;
//        }
//
//        public void setDate(String date) {
//            this.date = date;
//        }
//
//        public String getStep() {
//            return step;
//        }
//
//        public void setStep(String step) {
//            this.step = step;
//        }
//
//        public String getKcal() {
//            return kcal;
//        }
//
//        public void setKcal(String kcal) {
//            this.kcal = kcal;
//        }
//
//        public String getDis() {
//            return dis;
//        }
//
//        public void setDis(String dis) {
//            this.dis = dis;
//        }
//
//        public String getTimeLen() {
//            return timeLen;
//        }
//
//        public void setTimeLen(String timeLen) {
//            this.timeLen = timeLen;
//        }
//
//        public String getStatus() {
//            return status;
//        }
//
//        public void setStatus(String status) {
//            this.status = status;
//        }
//    }
//
//}
//
////    /**
////     * 改变本地数据库上传数据状态
////     */
////    private void changeUploadGD(int type, int position) {
////        B30HalfHourDB dbData = null;
////        switch (type) {
////            case STATE_SPORT:
////                dbData = sportData.get(position);
////                break;
////            case STATE_SLEEP:
////                dbData = sleepData.get(position);
////                break;
////            case STATE_RATE:
////                dbData = rateData.get(position);
////                break;
////            case STATE_BP:
////                dbData = bpData.get(position);
////                break;
////        }
////        if (dbData != null && !dbData.getDate().equals(CURR_DATE)) {
////            dbData.setUploadGD(1);// 只要不是当天的数据,都把本地状态设为"已上传"
////            dbData.save();//因为当天数据还有可能不停的更新和上传
////        }
////    }
////
////
////    /**
////     * 请求结果是否是成功的
////     *
////     * @param result 请求结果
////     * @return 是否成功
////     */
////    private boolean resultSuccessGD(String result) {
////        if (result == null) return false;
////        ResultVoGD resultVo = null;
////        try {
////            resultVo = gson.fromJson(result, ResultVoGD.class);
////        } catch (JsonSyntaxException e) {
////            e.printStackTrace();
////        }
////        final String RESULT_CODE = "200";// 上传数据成功结果码
////        return resultVo != null && resultVo.STATUS.equals(RESULT_CODE);
////    }
////
////    /**
////     * 内部类,请求结果模型
////     */
////    private class ResultVoGD {
////        /**
////         * 结果码
////         */
////        String STATUS;
////    }
////}
package com.guider.healthring.b30.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.guider.healthring.Commont;
import com.guider.healthring.LogTestUtil;
import com.guider.healthring.MyApp;
import com.guider.healthring.b30.bean.B30HalfHourDB;
import com.guider.healthring.b30.bean.B30HalfHourDao;
import com.guider.healthring.siswatch.utils.WatchUtils;
import com.guider.healthring.util.OkHttpTool;
import com.guider.healthring.util.SharedPreferencesUtils;
import com.guider.healthring.util.URLs;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.veepoo.protocol.model.datas.HalfHourBpData;
import com.veepoo.protocol.model.datas.HalfHourRateData;
import com.veepoo.protocol.model.datas.HalfHourSportData;
import com.veepoo.protocol.model.datas.SleepData;
import com.veepoo.protocol.model.datas.TimeData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 上传手环本地数据用的IntentService(上传完成自动销毁的服务)
 *
 * @author XuBo 2018-09-20
 */
public class DateUploadServiceGD extends IntentService {

    private final String TAG = "------AAA-GD";
    /**
     * 上传是否完成的状态: 运动数据
     */
    private final int STATE_SPORT = 1;
    /**
     * 上传是否完成的状态: 睡眠数据
     */
    private final int STATE_SLEEP = 2;
    /**
     * 上传是否完成的状态: 心率数据
     */
    private final int STATE_RATE = 3;
    /**
     * 上传是否完成的状态: 血压数据
     */
    private final int STATE_BP = 4;
    /**
     * 当天日期
     */
    private final String CURR_DATE = WatchUtils.getCurrentDate();
    /**
     * 需要上传的数据源: 运动数据
     */
    private List<B30HalfHourDB> sportData;
    /**
     * 需要上传的数据源: 睡眠数据
     */
    private List<B30HalfHourDB> sleepData;
    /**
     * 需要上传的数据源: 心率数据
     */
    private List<B30HalfHourDB> rateData;
    /**
     * 需要上传的数据源: 血压数据
     */
    private List<B30HalfHourDB> bpData;
    /**
     * 处理Json工具类
     */
    private Gson gson;
    /**
     * 手环MAC
     */
    private String deviceCode;
    /**
     * 用户ID
     */
    private String userId;

    public DateUploadServiceGD() {
        super("DateUploadService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        MyApp.getInstance().setUploadDateGD(true);// 正在上传数据,写到全局,保证同时只有一个本服务在运行
        gson = new Gson();
        deviceCode = (String) SharedPreferencesUtils.readObject(this, Commont.BLEMAC);
        userId = (String) SharedPreferencesUtils.readObject(this, "userId");
        if (TextUtils.isEmpty(deviceCode) || TextUtils.isEmpty(userId)) return;

        registerOrLogin();
    }


    private void registerOrLogin() {
        Map<String, String> params = new HashMap<>();
        params.put("CARDID", deviceCode);
        //LogTestUtil.e(TAG, "游客注册或者登陆" + params.toString());
        OkHttpTool.getInstance().doRequest(URLs.GDHTTPS + "/SendCardId",
                params, this, new OkHttpTool.HttpResult() {
                    @Override
                    public void onResult(String result) {
                        MyApp.getInstance().setUploadDateGD(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行
                        if (!WatchUtils.isEmpty(result)) {
//                            LogTestUtil.e(TAG, "游客注册或者登陆上传返回" + result);
                            if (resultSuccess(result)) {
                                findNotUploadData();// 1.找出要上传的所有数据
                            } else {
                                Intent intent = new Intent("com.example.bozhilun.android.b30.service.UploadServiceGD");
                                MyApp.getInstance().sendBroadcast(intent);
                            }
                        }

                    }
                }, true);
    }

    /**
     * 查找本地数据所有没上传的数据
     */
    private void findNotUploadData() {
        sportData = B30HalfHourDao.getInstance().findNotUploadDataGD(deviceCode, B30HalfHourDao.TYPE_SPORT);
        sleepData = B30HalfHourDao.getInstance().findNotUploadDataGD(deviceCode, B30HalfHourDao.TYPE_SLEEP);
        rateData = B30HalfHourDao.getInstance().findNotUploadDataGD(deviceCode, B30HalfHourDao.TYPE_RATE);
        bpData = B30HalfHourDao.getInstance().findNotUploadDataGD(deviceCode, B30HalfHourDao.TYPE_BP);
//        if (sportData != null) LogTestUtil.e(TAG, "未上传数据条数 运动: " + sportData.size());
//        if (sleepData != null) LogTestUtil.e(TAG, "未上传数据条数 睡眠: " + sleepData.size());
//        if (rateData != null) LogTestUtil.e(TAG, "未上传数据条数 心率: " + rateData.size());
//        if (bpData != null) LogTestUtil.e(TAG, "未上传数据条数 血压: " + bpData.size());
        if ((sportData != null && !sportData.isEmpty())
                || (sleepData != null && !sleepData.isEmpty())
                || (rateData != null && !rateData.isEmpty())
                || (bpData != null && !bpData.isEmpty())) {
            //数据库中存在数据------开始上传
            //LogTestUtil.e(TAG, "数据库中存在数据------开始上传");
            //uploadSportData(0);// 2.按一个个类型上传: 运动->睡眠->心率->血压


            updataStepData_GD();
        }

    }

    /**
     * 获取步数
     */
    private void updataStepData_GD() {
        //有运动数据
        if (sportData != null && !sportData.isEmpty()) {
//            LogTestUtil.e(TAG, "sport- 运动数据总条数：" + sportData.size()+"----"+sportData.toString());
//            for (int i = 0; i < sportData.size(); i++) {
            for (int i = (sportData.size() - 1); i >= 0; i--) {
                B30HalfHourDB dbData = sportData.get(i);
                if (dbData != null) {
                    String date = dbData.getDate();
                    String originData = dbData.getOriginData();
//                    LogTestUtil.e(TAG, "sport- 第  " + i + "  条运动数据" + "  时间: " + date + " 的数据");
                    if (i == 0) {
                        setUpStep(date, originData, true);
                    } else {
                        setUpStep(date, originData, false);
                    }

                }
            }
        }
        //没有运动数据
        else {
//            LogTestUtil.e(TAG, "sport- 没有运动数据了------------开始上传睡眠数据");
//            setUpSleep_GD();
            String result = "{\"STATUS\":\"500\",\"CARDID\":\"D6:64:CB:24:7E:74\",\"USERNAME\":\"开发者\", \"MESSAGE\":\"Duplicate entry 'D6:64:CB:24:7E:74-0000-00-00 00:00:00' for key 'PRIMARY'\"}";
            handlerNewResult(result, STATE_SPORT);
        }
    }


    int numberCountSport = 0;
    int intoNubmerSport = 0;

    int numberCountHeart = 0;
    int intoNubmerHeart = 0;

    /**
     * 上传步数
     *
     * @param date
     * @param originData
     * @param isLast     最后一天的数据
     */
    private void setUpStep(String date, String originData, final boolean isLast) {
        final List<HalfHourSportData> dataList = gson.fromJson(originData, new TypeToken<List<HalfHourSportData>>() {
        }.getType());


        if (dataList != null) {
//            LogTestUtil.e(TAG, "sport- 时间: " + date + " 的数据一共有  " + dataList.size() + " 条");
            numberCountSport = 0;
            intoNubmerSport = 0;
            for (int i = 0; i < 48; i++) {
                if (i < dataList.size()) {
                    if (dataList.get(i).getStepValue() > 0) {
                        numberCountSport++;
                    }
                }
            }
//            LogTestUtil.e(TAG, "sport- 实际有效的运动数据有  " + numberCountSport + " 条");
            for (int i = 0; i < 48; i++) {
                if (i < dataList.size()) {
                    int all = dataList.get(i).getStepValue();
                    TimeData time = dataList.get(i).getTime();
                    String stringHour = "00";
                    String stringMinute = "00";
                    int hour = time.getHour();
                    int minute = time.getMinute();


                    if (hour >= 10) {
                        stringHour = "" + hour;
                    } else {
                        stringHour = "0" + hour;
                    }
                    if (minute >= 10) {
                        stringMinute = "" + minute;
                    } else {
                        stringMinute = "0" + minute;
                    }
                    String dates = stringHour + "-" + stringMinute;
                    LogTestUtil.e(TAG, "sport- 步数数据" + dataList.size() + "===" + date + " " + dates + "-00" + "-----" + all);
                    /**
                     * 判断为0不上传
                     */
                    if (all > 0) {
                        intoNubmerSport++;

//                        LogTestUtil.e(TAG,
//                                "sport- 有效步数的数据------有效值长度：  " + numberCountSport +
//                                "   有效值累计" + intoNubmerSport +
//                                        "== 步数" + all + "====时间" + dates.replace(":", "-"));
                        if (intoNubmerSport < numberCountSport){
                            Map<String, Object> params = new HashMap<>();
                            params.put("WALKTIME", "0");
                            params.put("CARDID", deviceCode);
                            params.put("STEPS", all);
                            params.put("DISTANCE", 0);
                            params.put("CALORIE", 0);
                            params.put("CDATE", date + " " + dates + "-00");
                            LogTestUtil.e(TAG, "sport- 步数时间对比：" + dataList.get(i).getDate() + "====" + date);
                            LogTestUtil.e(TAG, "sport- 上传步数参数:" + params.toString());

                            String upStepPatch = URLs.GDHTTPS + "/SendWalkRecord";

                            if (numberCountSport == (intoNubmerSport-1) && isLast) {
                                OkHttpTool.getInstance().doRequest(upStepPatch, params, this, new OkHttpTool.HttpResult() {
                                    @Override
                                    public void onResult(String result) {
                                        if (!WatchUtils.isEmpty(result)) {
//                                            LogTestUtil.e(TAG, "sport- 最后步数上传返回: " + result);
                                            //是否是 最后一天最后第二条
                                            handlerNewResult(result, STATE_SPORT);
                                        } else {
                                            MyApp.getInstance().setUploadDateGD(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行
                                        }
                                    }
                                });
                            }else {
                                OkHttpTool.getInstance().doRequest(upStepPatch, params, this, new OkHttpTool.HttpResult() {
                                    @Override
                                    public void onResult(String result) {
                                        if (!WatchUtils.isEmpty(result)) {
//                                            LogTestUtil.e(TAG, "sport- 步数上传返回: " + result);
                                        } else {
                                            MyApp.getInstance().setUploadDateGD(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行
                                        }
                                    }
                                });
                            }
                        }

                    } else {
                        if (i == (dataList.size() - 1) && isLast) {
//                            LogTestUtil.e(TAG, "sport- 运动数据没有有效数据------------开始上传睡眠数据  " + i + "===" + (dataList.size() - 1));
                            String result = "{\"STATUS\":\"500\",\"CARDID\":\"D6:64:CB:24:7E:74\",\"USERNAME\":\"开发者\", \"MESSAGE\":\"Duplicate entry 'D6:64:CB:24:7E:74-0000-00-00 00:00:00' for key 'PRIMARY'\"}";
                            handlerNewResult(result, STATE_SPORT);
                        }
                    }

                }
            }

        }
    }


    /**
     * 获取睡眠
     */
    private void setUpSleep_GD() {
        if (sleepData != null && !sleepData.isEmpty()) {
            for (int i = (sleepData.size() - 1); i >= 0; i--) {
                B30HalfHourDB dbData = sleepData.get(i);
                if (dbData != null) {
                    String date = dbData.getDate();
                    String originData = dbData.getOriginData();
//                    LogTestUtil.e(TAG, "sleep- 睡眠数据第：" + i + " 条" + "===" + date + "===" + originData);
                    //setUpSleep(date, originData);

                    if (i == 0) {
                        setUpSleep(date, originData, true);
                    } else {
                        setUpSleep(date, originData, false);
                    }
                }
            }
        }
        //没有睡眠数据
        else {
//            LogTestUtil.e(TAG, "sleep- 没有睡眠数据了------------开始上传心率数据");

            //setUpHeart_GD();
            String result = "{\"STATUS\":\"500\",\"CARDID\":\"D6:64:CB:24:7E:74\",\"USERNAME\":\"开发者\", \"MESSAGE\":\"Duplicate entry 'D6:64:CB:24:7E:74-0000-00-00 00:00:00' for key 'PRIMARY'\"}";
            handlerNewResult(result, STATE_SLEEP);
        }
    }


    /**
     * 上传睡眠
     *
     * @param date
     * @param originData
     */
    private void setUpSleep(String date, String originData, final boolean isLast) {
        SleepData sleepData = gson.fromJson(originData, SleepData.class);
        int deepSleepTime = sleepData.getDeepSleepTime();
        int lowSleepTime = sleepData.getLowSleepTime();

        Map<String, Object> params = new HashMap<>();
        int allTimes = deepSleepTime + lowSleepTime;
        String times = remainTimeStr(allTimes);
        params.put("SLEEPTIME", times);
        params.put("CARDID", deviceCode);
        params.put("CDATE", date + " 00-00-00");
//        MyLogUtil.e(TAG, "sleep- 睡眠时间对比：" + sleepData.getDate() + "======" + date);
//        MyLogUtil.e(TAG, "sleep- 上传睡眠参数：" + params.toString());
        String usSleepPath = URLs.GDHTTPS + "/SendSleepRecord";
        OkHttpTool.getInstance().doRequest(usSleepPath, params, this, new OkHttpTool.HttpResult() {
            @Override
            public void onResult(String result) {
                if (!WatchUtils.isEmpty(result)) {
//                    LogTestUtil.e(TAG, "sleep- 睡眠上传返回" + result);
                    //最后一天最后一条
                    if (isLast)
                        handlerNewResult(result, STATE_SLEEP);
                } else {
                    MyApp.getInstance().setUploadDateGD(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行
                }

            }
        });
    }


    /**
     * 获取心率
     */
    private void setUpHeart_GD() {
        if (rateData != null && !rateData.isEmpty()) {
//            for (int i = 0; i < rateData.size(); i++) {
            for (int i = (rateData.size() - 1); i >= 0; i--) {
                B30HalfHourDB b30HalfHourDB = rateData.get(i);
                if (b30HalfHourDB != null) {
                    String date = b30HalfHourDB.getDate();
                    String originData = b30HalfHourDB.getOriginData();
//                    LogTestUtil.e(TAG, "heart- 心率数据第：" + i + " 条" + "  时间： " + date);
                    //setUpHraet(date, originData);
                    if (i == 0) {
                        setUpHraet(date, originData, true);
                    } else {
                        setUpHraet(date, originData, false);
                    }
                }
            }
        }
        //没有心率数据
        else {
//            LogTestUtil.e(TAG, "heart- 没有心率数据了------------开始上传血压数据");
            //setUpBp_GD();
            String result = "{\"STATUS\":\"500\",\"CARDID\":\"D6:64:CB:24:7E:74\",\"USERNAME\":\"开发者\", \"MESSAGE\":\"Duplicate entry 'D6:64:CB:24:7E:74-0000-00-00 00:00:00' for key 'PRIMARY'\"}";
            handlerNewResult(result, STATE_RATE);
        }
    }


    /**
     * 获取心率
     *
     * @param date
     * @param originData
     */
//    int numberCountSport = 0;
//    int intoNubmerSport = 0;
//    int numberCountHeart = 0;
//    int intoNubmerHeart = 0;

//    int numberCountBp = 0;
//    int intoNubmerBp = 0;
    private void setUpHraet(final String date, String originData, final boolean isLast) {
        final List<HalfHourRateData> dataList = gson.fromJson(originData, new TypeToken<List<HalfHourRateData>>() {
        }.getType());


        if (dataList != null) {
//            LogTestUtil.e(TAG, "heart- 时间: " + date + " 的数据一共有  " + dataList.size() + " 条");
            numberCountHeart = 0;
            intoNubmerHeart = 0;
            for (int i = 0; i < 48; i++) {
                if (i < dataList.size()) {
                    if (dataList.get(i).getRateValue() > 0) {
                        numberCountHeart++;
                    }
                }
            }
//            LogTestUtil.e(TAG, "heart- 实际有效的心率数据有  " + numberCountHeart + " 条");
            for (int i = 0; i < dataList.size(); i++) {

                HalfHourRateData halfHourRateData = dataList.get(i);
                if (halfHourRateData != null) {
                    String dates = obtainDate(halfHourRateData.getTime());
                    int rateValue = halfHourRateData.getRateValue();
                    if (rateValue > 0) {
                        intoNubmerHeart++;
                        if (intoNubmerHeart < numberCountHeart){
//                        LogTestUtil.e(TAG, "heart- 有效心率的数据------有效值长度：  " + numberCountHeart + "   有效值累计" + intoNubmerHeart + "==" + rateValue + "====" + date + "====" + dates.replace(":", "-"));
                            Map<String, Object> params = new HashMap<>();
                            params.put("PU", rateValue);
                            params.put("CARDID", deviceCode);
                            params.put("CDATE", dates.replace(":", "-"));
//                            LogTestUtil.e(TAG, "heart- 心率时间对比：" + dates + "====" + date);
//                            MyLogUtil.e(TAG, "heart- 心率上传参数" + params.toString());
                            String path = URLs.GDHTTPS + "/SendPUInfo";

                            if (numberCountHeart == (intoNubmerHeart-1) && isLast) {
                                OkHttpTool.getInstance().doRequest(path, params, this, new OkHttpTool.HttpResult() {
                                    @Override
                                    public void onResult(String result) {
                                        if (!WatchUtils.isEmpty(result)) {
//                                            LogTestUtil.e(TAG, "heart- 最后心率上传返回： " + result);
                                            handlerNewResult(result, STATE_RATE);
                                        } else {
                                            MyApp.getInstance().setUploadDateGD(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行
                                        }
                                    }
                                });
                            }else {
                                OkHttpTool.getInstance().doRequest(path, params, this, new OkHttpTool.HttpResult() {
                                    @Override
                                    public void onResult(String result) {
                                        if (!WatchUtils.isEmpty(result)) {
//                                            LogTestUtil.e(TAG, "heart- 心率上传返回： " + result);

                                        } else {
                                            MyApp.getInstance().setUploadDateGD(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行
                                        }

                                    }
                                });
                            }
                        }
                    } else {
                        if (i == (dataList.size() - 1) && isLast) {
//                            LogTestUtil.e(TAG, "heart- 心率数据没有有效数据------------开始上传血压数据  " + i + "===" + (dataList.size() - 1));
                            String result = "{\"STATUS\":\"500\",\"CARDID\":\"D6:64:CB:24:7E:74\",\"USERNAME\":\"开发者\", \"MESSAGE\":\"Duplicate entry 'D6:64:CB:24:7E:74-0000-00-00 00:00:00' for key 'PRIMARY'\"}";
                            handlerNewResult(result, STATE_RATE);
                        }
                    }
                }

            }

        }
    }


    /**
     * 获取血压
     */
    private void setUpBp_GD() {
        if (bpData != null && !bpData.isEmpty()) {
//            for (int i = 0; i < bpData.size(); i++) {
            for (int i = (bpData.size() - 1); i >= 0; i--) {
                B30HalfHourDB b30HalfHourDB = bpData.get(i);
                if (b30HalfHourDB != null) {
                    String date = b30HalfHourDB.getDate();
                    String originData = b30HalfHourDB.getOriginData();
//                    LogTestUtil.e(TAG, "bp-血压数据第：" + i + " 条" + "===" + date);
                    //setUpBp(date, originData);
                    if (i == 0) {
                        setUpBp(date, originData, true);
                    } else {
                        setUpBp(date, originData, false);
                    }
                }
            }
        } else {
//            LogTestUtil.e(TAG, "bp- 没有血压数据了------------该表上传状态");
//            MyApp.getInstance().setUploadDateGD(false);
            String result = "{\"STATUS\":\"500\",\"CARDID\":\"D6:64:CB:24:7E:74\",\"USERNAME\":\"开发者\", \"MESSAGE\":\"Duplicate entry 'D6:64:CB:24:7E:74-0000-00-00 00:00:00' for key 'PRIMARY'\"}";
            handlerNewResult(result, STATE_BP);

        }

    }

    /**
     * 上传血压数据
     *
     * @param date
     * @param originData
     */
    private void setUpBp(String date, String originData, final boolean islast) {
        final List<HalfHourBpData> dataList = gson.fromJson(originData, new TypeToken<List<HalfHourBpData>>() {
        }.getType());

        if (dataList != null) {
//            LogTestUtil.e(TAG, "bp- 时间: " + date + " 的数据一共有  " + dataList.size() + " 条");
//            numberCountBp = 0;
//            intoNubmerBp = 0;
//            for (int i = 0; i < dataList.size(); i++) {
//                HalfHourBpData halfHourBpData = dataList.get(i);
//                int highValue = halfHourBpData.getHighValue();
//                int lowValue = halfHourBpData.getLowValue();
//                if (lowValue > 0 && highValue > 0) {
//                    numberCountBp++;
//                }
//            }
//            LogTestUtil.e(TAG, "bp- 实际有效的血压数据有  " + numberCountBp + " 条");
            for (int i = 0; i < dataList.size(); i++) {
                HalfHourBpData halfHourBpData = dataList.get(i);
                if (halfHourBpData != null) {
                    String dates = obtainDate(halfHourBpData.getTime());
                    int highValue = halfHourBpData.getHighValue();
                    int lowValue = halfHourBpData.getLowValue();

                    if (lowValue > 0 && highValue > 0) {
//                        intoNubmerBp++;
//                        LogTestUtil.e(TAG, "bp- 有效血压的数据------有效值长度：  " + numberCountBp + "   有效值累计" + intoNubmerBp + "=h: " + highValue + " =l: " + lowValue + "====" + date + "====" + dates.replace(":", "-"));
//                        if (intoNubmerBp <= numberCountBp) {
                        Map<String, Object> params = new HashMap<>();
                        params.put("DBP", lowValue);
                        params.put("SBP", highValue);
                        params.put("HB", "0");
                        params.put("CARDID", deviceCode);
                        params.put("CDATE", dates.replace(":", "-"));
                        String path = URLs.GDHTTPS + "/SendBPInfo";
//                        LogTestUtil.e(TAG, "bp- 血压时间对比：" + dates + "====" + date);
//                        MyLogUtil.e(TAG, "bp- 血压上传参数" + params.toString());

                        OkHttpTool.getInstance().doRequest(path, params, this, new OkHttpTool.HttpResult() {
                            @Override
                            public void onResult(String result) {
                                if (!WatchUtils.isEmpty(result)) {
//                                    LogTestUtil.e(TAG, "bp- 血压上传返回： " + result);
                                    if (islast) {
//                                        LogTestUtil.e(TAG, "bp- 血压上传返回： " + result);
                                        handlerNewResult(result, STATE_BP);
                                    }
//                                        LogTestUtil.e(TAG, "bp- 血压最后一个" + intoNubmerBp + "===" + numberCountBp + "  血压上传返回： " + result);
//                                        if (intoNubmerBp == numberCountBp) {
//                                            handlerNewResult(result, STATE_BP);
//                                        }
                                } else {
                                    MyApp.getInstance().setUploadDateGD(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行
                                }
                            }
                        });
//                        }

                    } else {
                        if (islast) {
//                            LogTestUtil.e(TAG, "bp- 血压数据没有有效数据------------去改变上传状态  " + i + "===" + (dataList.size() - 1));
                            String result = "{\"STATUS\":\"500\",\"CARDID\":\"D6:64:CB:24:7E:74\",\"USERNAME\":\"开发者\", \"MESSAGE\":\"Duplicate entry 'D6:64:CB:24:7E:74-0000-00-00 00:00:00' for key 'PRIMARY'\"}";
                            handlerNewResult(result, STATE_BP);
                        }
                    }
                }
            }
        }

    }


    /**
     * 上传顺序   步数---睡眠---心率---血压
     * <p>
     * 处理请求结果
     */
    private void handlerNewResult(String result, int type) {
        if (resultSuccess(result)) changeUpload(type);
        switch (type) {
            case STATE_SPORT:
//                LogTestUtil.e(TAG, "步数上传结束-----睡眠上传开始");
                setUpSleep_GD();
                break;
            case STATE_SLEEP:
//                LogTestUtil.e(TAG, "睡眠上传结束-----心率上传开始");
                setUpHeart_GD();
                break;
            case STATE_RATE:
//                LogTestUtil.e(TAG, "心率上传结束-----血压上传开始");
                setUpBp_GD();
                break;
            case STATE_BP:
//                LogTestUtil.e(TAG, "血压上传结束-----上传数据结束");
                MyApp.getInstance().setUploadDateGD(false);
                break;
        }
    }


    /**
     * 改变本地数据库上传数据状态
     */
    private void changeUpload(int type) {
        B30HalfHourDB dbData = null;
        String currentDate = WatchUtils.getCurrentDate();
        switch (type) {
            case STATE_SPORT:
                for (int i = 0; i < sportData.size(); i++) {
                    dbData = sportData.get(i);
//                    LogTestUtil.e(TAG, "时间对比--A-----" + currentDate + "=====" + dbData.getDate());
                    if (!dbData.getDate().equals(currentDate)) {
                        dbData.setUploadGD(1);// 只要不是当天的数据,都把本地状态设为"已上传"
                        dbData.save();//因为当天数据还有可能不停的更新和上传
                    }

                }
                break;
            case STATE_SLEEP:
                for (int i = 0; i < sleepData.size(); i++) {
                    dbData = sleepData.get(i);
//                    LogTestUtil.e(TAG, "时间对比--B-----" + currentDate + "=====" + dbData.getDate());
                    if (!dbData.getDate().equals(currentDate)) {
                        dbData.setUploadGD(1);// 只要不是当天的数据,都把本地状态设为"已上传"
                        dbData.save();//因为当天数据还有可能不停的更新和上传
                    }

                }
                break;
            case STATE_RATE:
                for (int i = 0; i < rateData.size(); i++) {
                    dbData = rateData.get(i);
//                    LogTestUtil.e(TAG, "时间对比--C-----" + currentDate + "=====" + dbData.getDate());
                    if (!dbData.getDate().equals(currentDate)) {
                        dbData.setUploadGD(1);// 只要不是当天的数据,都把本地状态设为"已上传"
                        dbData.save();//因为当天数据还有可能不停的更新和上传
                    }

                }
                break;
            case STATE_BP:
                for (int i = 0; i < bpData.size(); i++) {
                    dbData = bpData.get(i);
//                    LogTestUtil.e(TAG, "时间对比--D-----" + currentDate + "=====" + dbData.getDate());
//                    if (!dbData.getDate().equals(currentDate)) {
                    dbData.setUploadGD(1);// 只要不是当天的数据,都把本地状态设为"已上传"
                    dbData.save();//因为当天数据还有可能不停的更新和上传
//                    }
                }
                break;
        }
    }

//
//    ////////////////////////////////////////////////
//
//    /**
//     * 上传运动数据
//     */
//    private void uploadSportData(int position) {
//        if (sportData == null || sportData.isEmpty() || position >= sportData.size()) {
//            //LogTestUtil.e(TAG, "步数上传完成，开始上传睡眠");
//            uploadSleepData(0);// 运动数据上传完了,换着上传睡眠数据
//            return;
//        }
//        B30HalfHourDB dbData = sportData.get(position);
//        String date = dbData.getDate();
//        String originData = dbData.getOriginData();
//        //LogTestUtil.e(TAG, "步数数据" + originData);
//        setStep(originData, position);
//
//
////        SportVo sportVo = totalSportByDay(date, originData);
////        submitSportData(sportVo, position);
//    }
//
//    /**
//     * 转换数据格式上传数据-----------好友功能详细数据
//     *
//     * @param originData
//     * @param position
//     */
//    void setStep(String originData, final int position) {
//        List<HalfHourSportData> dataList = gson.fromJson(originData, new TypeToken<List<HalfHourSportData>>() {
//        }.getType());
//        if (dataList == null || dataList.isEmpty()) return;
//        String date = dataList.get(0).getDate();
////        LogTestUtil.e(TAG, "步数长度" + dataList.size());
////        List<UpHeartBean> upHeartBeanList = new ArrayList<>();
////        List<Integer> list = new ArrayList<>();
//        if (!dataList.isEmpty()) {
//            int s = 0;
//            int s1 = 0;
//            int all = 0;
//            String times = "00-00";
//            for (int i = 0; i < 48; i++) {
//
//                if (i < dataList.size()) {
//                    all = dataList.get(i).getStepValue();
//
//                    TimeData time = dataList.get(i).getTime();
//                    String stringHour = "00";
//                    String stringMinute = "00";
//                    int hour = time.getHour();
//                    int minute = time.getMinute();
//                    if (hour >= 10) {
//                        stringHour = "" + hour;
//                    } else {
//                        stringHour = "0" + hour;
//                    }
//                    if (minute >= 10) {
//                        stringMinute = "" + minute;
//                    } else {
//                        stringMinute = "0" + minute;
//                    }
////                String dates = ((int)time.getHour() > 9 ? (int)time.getHour()+"" : "0" + (int)time.getHour())
////                        + ":" + ((int)time.getMinute() > 9 ? (int)time.getMinute()+"" : "0" + (int)time.getMinute());
//                    String dates = stringHour + "-" + stringMinute;
//                    //LogTestUtil.e(TAG, "步数时间--" + date + " " + dates);
//
//
////                    UpHeartBean upHeartBean = new UpHeartBean(userId, deviceCode,
////                            "00", all + "",
////                            date + " " + dates, "00", "0", "00", "00");
////                    upHeartBeanList.add(upHeartBean);
//                    /**
//                     * 判断为0不上传
//                     */
//                    if (all > 0) {
//                        Map<String, Object> params = new HashMap<>();
//                        params.put("WALKTIME", "0");
//                        params.put("CARDID", deviceCode);
//                        params.put("STEPS", all);
//                        params.put("DISTANCE", 0);
//                        params.put("CALORIE", 0);
//                        //LogTestUtil.e(TAG, "---------上传步数时间----" + date + " " + dates + "  ------- " + all);
//                        params.put("CDATE", date + " " + dates + "-00");
//                        LogTestUtil.e(TAG, "上传步数参数:" + params.toString());
//                        requestStep(URLs.GDHTTPS + "/SendWalkRecord", params, STATE_SPORT, position);
//                    } else {
//                        String result = "{\"STATUS\":\"500\",\"CARDID\":\"D6:64:CB:24:7E:74\",\"USERNAME\":\"开发者\", \"MESSAGE\":\"Duplicate entry 'D6:64:CB:24:7E:74-0000-00-00 00:00:00' for key 'PRIMARY'\"}";
//                        LogTestUtil.e(TAG, "---------上传步数时间----步数为0了不上传" + result);
//                        if (i == dataList.size() - 1)
//                            handlerResult(result, STATE_SPORT, position);
//                    }
//                }
////                //times = toTimeStr((i / 2) * 60 * 60 * 1000);
////                times = remainTimeStr2((i / 2) * 60);
////                //根据条件，将48个点转换为24个点，并且把半点的数据加到正点上
////
////
////                LogTestUtil.e(TAG, "步数时间--" + date + " " + times);
////                list.add(all);
////                UpHeartBean upHeartBean = new UpHeartBean(userId, deviceCode,
////                        "00", all + "",
////                        date + " " + times, "00", "0", "00", "00");
////                upHeartBeanList.add(upHeartBean);
//            }
//
////            JSONArray jsonArray1 = ProLogListJson(upHeartBeanList);
////            LogTestUtil.e(TAG, "步数处理数据长度" + upHeartBeanList.size() + "===" + jsonArray1);
//
////            JSONArray jsonArray1 = ProLogListJson(upHeartBeanList);
//////            UpDatasBase.upAllDataSteps(jsonArray1);
////            LogTestUtil.e(TAG, "步数处理数据长度" + list.size() + "===" + list.toString());
////            try {
////                JSONObject mapB = new JSONObject();
////                mapB.put("data", jsonArray1);
////                String mapjson = mapB.toString();
////                LogTestUtil.e("-------------步数上传", jsonArray1.toString());
////                SubscriberOnNextListener sb = new SubscriberOnNextListener<String>() {
////                    @Override
////                    public void onNext(String result) {
////                        LogTestUtil.e("-------------步数上传返回", result);
////                        handlerResult(result, STATE_SPORT, position);
////                    }
////                };
////                CommonSubscriber commonSubscriber = new CommonSubscriber(sb, MyApp.getInstance());
////                OkHttpObservable.getInstance().getData(commonSubscriber, URLs.HTTPs + URLs.upHeart, mapjson);
////            } catch (Exception E) {
////                E.printStackTrace();
////            }
//
////            LogTestUtil.e(TAG, "步数处理数据长度a" + upHeartBeanList.size() + "===" + upHeartBeanList.toString());
//        }
//    }
//
//    /**
//     * 将 list 转换 为 JSONArray
//     *
//     * @param list
//     * @return
//     */
//    public JSONArray ProLogListJson(List<UpHeartBean> list) {
//        JSONArray json = new JSONArray();
//        for (UpHeartBean pLog : list) {
//            JSONObject jo = new JSONObject();
//            try {
//                jo.put("userId", pLog.getUserId());
//                jo.put("deviceCode", pLog.getDeviceCode());
//                jo.put("diastolic", pLog.getDiastolic());
//                jo.put("systolic", pLog.getSystolic());
//                jo.put("stepNumber", pLog.getStepNumber());
//                jo.put("date", pLog.getDate());
//                jo.put("heartRate", pLog.getHeartRate());
//                jo.put("status", pLog.getStatus());
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            json.put(jo);
//        }
//        return json;
//    }
//

    /**
     * 分钟转换时分秒
     *
     * @param minutes
     * @return
     */
    public String remainTimeStr(int minutes) {
        int hour = minutes / 60;
        int minute = minutes % 60;
        int seconds = minutes * 60 % 60;
        return (hour > 9 ? hour : "0" + hour) + ":" + (minute > 9 ? minute : "0" + minute) + ":" + (seconds > 9 ? seconds : "0" + seconds);
    }

////    /**
////     * 分钟转换时分秒
////     *
////     * @param minutes
////     * @return
////     */
////    public String remainTimeStr2(int minutes) {
////        int hour = minutes / 60;
////        int minute = minutes % 60;
////        int seconds = minutes * 60 % 60;
////        return (hour > 9 ? hour : "0" + hour) + "-" + (minute > 9 ? minute : "0" + minute);
////    }
//
//
////    /**
////     * 汇总一天的原始运动数据
////     *
////     * @param originData 当天的运动数据源
////     * @return 汇总后的数据
////     */
////    private SportVo totalSportByDay(String date, String originData) {
////        //目标步数
////        int goalStep = (int) SharedPreferencesUtils.getParam(MyApp.getInstance().getApplicationContext(), "b30Goal", 0);
////        List<HalfHourSportData> dataList = gson.fromJson(originData, new TypeToken<List<HalfHourSportData>>() {
////        }.getType());
////        int stepValue = 0;
////        double calValue = 0;
////        double disValue = 0;
////        if (dataList != null) {
////            for (HalfHourSportData item : dataList) {
////                stepValue += item.getStepValue();
////                calValue += item.getCalValue();
////                disValue += item.getDisValue();
////            }
////        }
////        SportVo sportVo = new SportVo();
////        sportVo.setDate(date);
////        sportVo.setStep(stepValue + "");
////        sportVo.setKcal("" + calValue);
////        sportVo.setDis("" + disValue);
////        //判断是否达标 1 达标；0 不达标
////        int status;
////        if (stepValue - goalStep >= 0) {
////            status = 1;
////        } else {
////            status = 0;
////        }
////
////        sportVo.setStatus(status + "");
////        sportVo.setTimeLen("0");
////        return sportVo;
////    }
//
////    /**
////     * 请求提交运动步数数据到后台
////     *
////     * @param sportVo  整理好要提交的步数数据
////     * @param position 记录当前传的是哪个下标,以便上传完后更新数据库
////     */
////    private void submitSportData(SportVo sportVo, int position) {
////        Map<String, Object> params = new HashMap<>();
////        SimpleDateFormat format = new SimpleDateFormat("HH-mm-ss");
////        String timeLen = sportVo.getTime().toString();
////
////        String format1 = format.format(new Date());
////        int integer = Integer.valueOf(sportVo.getStep().trim());
////
////        /**
////         * 判断为0不上传
////         */
////        if (integer > 0) {
////            params.put("WALKTIME", "0");
////            params.put("CARDID", deviceCode);
////            params.put("STEPS", Integer.valueOf(sportVo.getStep().trim()));
////            params.put("DISTANCE", Float.valueOf(sportVo.getDis().trim()));
////            params.put("CALORIE", Float.valueOf(sportVo.getKcal().trim()));
//////        Date date = W30BasicUtils.stringToDate(sportVo.date + " " + format1, "yyyy-MM-dd HH-mm-ss");
////            LogTestUtil.e(TAG, "---------步数时间----" + sportVo.getDate() + " " + format1 + "========" + timeLen);
////            params.put("CDATE", sportVo.getDate() + " " + format1);
////            //net.sf.json.JSONObject 将Map转换为JSON方法
//////        JSONObject json = JSONObject.fromObject(map);
////            //org.json.JSONObject 将Map转换为JSON方法
////            LogTestUtil.e(TAG, "上传步数参数:" + params.toString());
////            requestStep(URLs.GDHTTPS + "/SendWalkRecord", params, STATE_SPORT, position);
////        } else {
////            String result = "{\"STATUS\":\"500\",\"CARDID\":\"D6:64:CB:24:7E:74\",\"USERNAME\":\"使用者\", \"MESSAGE\":\"Duplicate entry 'D6:64:CB:24:7E:74-2018-12-07 03:30:00' for key 'PRIMARY'\"}";
////            LogTestUtil.e(TAG, "步数为0了不上传" + result);
////            handlerResult(result, STATE_SPORT, position);
////        }
////    }
//
//    /**
//     * 上传睡眠数据
//     */
//    private void uploadSleepData(int position) {
//        if (sleepData == null || sleepData.isEmpty() || position >= sleepData.size()) {
//            //LogTestUtil.e(TAG, "睡眠数据上传完成，开始上传心率数据");
//            uploadRateData(0);// 睡眠数据上传完了,换着上传心率数据
//            return;
//        }
//        B30HalfHourDB dbData = sleepData.get(position);
//        if (dbData != null) {
//            String originData = dbData.getOriginData();
//            if (TextUtils.isEmpty(originData)) return;
////            LogTestUtil.e(TAG, "睡眠数据" + dbData.getDate() + "------" + originData);
//            SleepData sleepData = gson.fromJson(originData, SleepData.class);
//            submitSleepData(sleepData, position);
//        }
//    }
//
//    /**
//     * 请求提交睡眠数据到后台
//     *
//     * @param sleepData 手环的睡眠数据
//     */
//    private void submitSleepData(SleepData sleepData, final int position) {
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
////        Date date = W30BasicUtils.stringToDate(format.format(new Date()), "yyyy-MM-dd HH-mm-ss");
//        int deepSleepTime = sleepData.getDeepSleepTime();
//        int lowSleepTime = sleepData.getLowSleepTime();
//        //LogTestUtil.e(TAG, "睡眠时长-  分钟" + (deepSleepTime + lowSleepTime) + "睡眠日期-" + sleepData.getDate());
//        Map<String, Object> params = new HashMap<>();
//        int allTimes = deepSleepTime + lowSleepTime;
//        String times = remainTimeStr(allTimes);
//        //LogTestUtil.e(TAG, "------睡眠时间是：" + times + "=======" + sleepData.getDate());
//        params.put("SLEEPTIME", times);
//        params.put("CARDID", deviceCode);
//        params.put("CDATE", sleepData.getDate() + " 09-00-00");
//        MyLogUtil.e(TAG, "睡眠上传参数：" + params.toString());
//        request(URLs.GDHTTPS + "/SendSleepRecord", params, STATE_SLEEP, position);
//    }
//
//
//    /**
//     * 上传心率数据
//     */
//    private void uploadRateData(int position) {
//        if (rateData == null || rateData.isEmpty() || position >= rateData.size()) {
//            //LogTestUtil.e(TAG, "心率数据上传完成，开始上传血压数据");
//            uploadBpData(0);// 心率数据上传完了,换着上传血压数据
//            return;
//        }
//
//        B30HalfHourDB b30HalfHourDB = rateData.get(position);
//        if (b30HalfHourDB != null) {
//            String originData = b30HalfHourDB.getOriginData();
//            if (!TextUtils.isEmpty(originData)) {
//                List<HalfHourRateData> dataList = gson.fromJson(originData, new TypeToken<List<HalfHourRateData>>() {
//                }.getType());
////            HealthParamVo paramVo = new HealthParamVo();
////            paramVo.data = submitRateData(userId, deviceCode, dataList);
////            String rateData = gson.toJson(paramVo);
////            LogTestUtil.e("------AAA-", "心率数据:" + rateData);
////            request(rateData, STATE_RATE, position);
//                List<HealthVo> healthVos = submitRateData(userId, deviceCode, dataList);
//                for (int i = 0; i < healthVos.size(); i++) {
//                    String heart = healthVos.get(i).getHeartRate();
//                    String time = healthVos.get(i).getDate();
//                    //LogTestUtil.e(TAG, "心率数据：" + heart + "===" + time.replace(":", "-"));
//                    if (i == dataList.size() - 1) {
//                        requestDD(heart, time.replace(":", "-"), STATE_RATE, position, true);
//                    } else {
//                        requestDD(heart, time.replace(":", "-"), STATE_RATE, position, false);
//                    }
//                }
//            }
//        }
//    }
//
//
//    private void requestDD(String heart, String time, final int type, final int position, final boolean isHandler) {
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//        /**
//         * 判断为0不上传
//         */
//        if (Integer.valueOf(heart) > 0) {
//
//
//            Map<String, Object> params = new HashMap<>();
//            params.put("PU", heart);
//            params.put("CARDID", deviceCode);
////        params.put("CDATE", format.format(new Date()));
//            params.put("CDATE", time);
//            MyLogUtil.e(TAG, "心率上传参数" + params.toString());
//            String path = URLs.GDHTTPS + "/SendPUInfo";
//            OkHttpTool.getInstance().doRequest(path, params, this, new OkHttpTool.HttpResult() {
//                @Override
//                public void onResult(String result) {
//                    if (WatchUtils.isEmpty(result))
//                        return;
//                    LogTestUtil.e(TAG, "心率上传返回" + result);
//                    if (isHandler) {
//                        handlerResult(result, type, position);
//                    }
//
//                }
//            });
//        } else {
//            String result = "{\"STATUS\":\"500\",\"CARDID\":\"D6:64:CB:24:7E:74\",\"USERNAME\":\"开发者\", \"MESSAGE\":\"Duplicate entry 'D6:64:CB:24:7E:74-2018-12-07 03:30:00' for key 'PRIMARY'\"}";
//            LogTestUtil.e(TAG, "心率为0了不上传" + result);
//            if (isHandler) handlerResult(result, type, position);
//        }
//
//
////        OkHttpTool.getInstance().doRequest(path, params, this, new OkHttpTool.HttpResult() {
////            @Override
////            public void onResult(String result) {
////                if (WatchUtils.isEmpty(result))
////                    return;
////                LogTestUtil.e(TAG, "心率上传返回" + result);
////                handlerResult(result, type, position);
////            }
////        }, false);
//    }
//
//
//    /**
//     * 获取最后上传的心率数据
//     */
//    private List<HealthVo> submitRateData(String id, String code, List<HalfHourRateData> data) {
//        List<HealthVo> rateList = new ArrayList<>();// 组织心率提交的数据
//        for (HalfHourRateData item : data) {
//            HealthVo healthVo = new HealthVo();
//            healthVo.setUserId(id);
//            healthVo.setDeviceCode(code);
//            healthVo.setHeartRate(item.getRateValue() + "");
//            healthVo.setBloodOxygen("0");
//            healthVo.setSystolic("0");
//            healthVo.setDiastolic("0");
//            healthVo.setStatus("1");
//            healthVo.setDate(obtainDate(item.getTime()));
//            rateList.add(healthVo);
//        }
//        return rateList;
//    }
//
//
//    IBleWriteResponse iBleWriteResponse = new IBleWriteResponse() {
//        @Override
//        public void onResponse(int i) {
//
//        }
//    };
//
//    ILanguageDataListener iLanguageDataListener = new ILanguageDataListener() {
//        @Override
//        public void onLanguageDataChange(LanguageData languageData) {
//            if (languageData != null && languageData.getLanguage() != null) {
//                LogTestUtil.e("-----", languageData.getLanguage().toString());
//            }
//        }
//    };
//
//    /**
//     * 上传血压数据
//     */
//    private void uploadBpData(int position) {
//        if (bpData == null || bpData.isEmpty() || position >= bpData.size()) {
//            // 血压数据上传完了,到此结束
//            //LogTestUtil.e(TAG, "全部数据上传完成,改变上传状态，没在上传数据------此处设置设备语言");
//            //数据上传完咯，改变设备语言
//            //判断设置语言
//            boolean zh = VerifyUtil.isZh(MyApp.getInstance());
//            if (zh) {
//                MyApp.getInstance().getVpOperateManager().settingDeviceLanguage(iBleWriteResponse, iLanguageDataListener, ELanguage.CHINA);
//            } else {
//                MyApp.getInstance().getVpOperateManager().settingDeviceLanguage(iBleWriteResponse, iLanguageDataListener, ELanguage.ENGLISH);
//            }
//            MyApp.getInstance().setUploadDate(false);
//            return;
//        }
//        B30HalfHourDB b30HalfHourDB = bpData.get(position);
//        if (b30HalfHourDB != null) {
//            String originData = b30HalfHourDB.getOriginData();
//            if (TextUtils.isEmpty(originData)) return;
//            List<HalfHourBpData> dataList = gson.fromJson(originData, new TypeToken<List<HalfHourBpData>>() {
//            }.getType());
////            HealthParamVo paramVo = new HealthParamVo();
////            paramVo.data = submitBpData(userId, deviceCode, dataList);
////            String bpData = gson.toJson(paramVo);
////            LogTestUtil.e("-------血压数据----获取", bpData);
////            requestBp(bpData, STATE_BP, position);
//
//            List<HealthVo> healthVos = submitBpData(userId, deviceCode, dataList);
//            for (int i = 0; i < healthVos.size(); i++) {
//                String sbp = healthVos.get(i).getSystolic();
//                String dbp = healthVos.get(i).getDiastolic();
//                String time = healthVos.get(i).getDate();
//
//                //LogTestUtil.e(TAG, "血压数据：" + sbp + "===" + dbp + "===" + time.replace(":", "-"));
//                if (i == healthVos.size() - 1) {
//                    requestBp(dbp, sbp, time.replace(":", "-"), STATE_BP, position, true);
//                } else {
//                    requestBp(dbp, sbp, time.replace(":", "-"), STATE_BP, position, false);
//                }
//
//            }
//        }
//
//    }
//
//
//    /**
//     * 获取最后上传的血压数据
//     */
//    private List<HealthVo> submitBpData(String id, String code, List<HalfHourBpData> data) {
//        List<HealthVo> bpList = new ArrayList<>();// 组织血压提交的数据
//        for (HalfHourBpData item : data) {
//            HealthVo healthVo = new HealthVo();
//            healthVo.userId = id;
//            healthVo.deviceCode = code;
//            healthVo.heartRate = "0";
//            healthVo.bloodOxygen = "0";
//            healthVo.systolic = "" + item.highValue;
//            healthVo.diastolic = "" + item.lowValue;
//            healthVo.status = "0";
//            healthVo.date = obtainDate(item.getTime());
//            bpList.add(healthVo);
//        }
//        return bpList;
//    }
//

    /**
     * 将手环数据的日期转换为提交到后台的格式
     */
    private String obtainDate(TimeData data) {
        String month = data.month < 10 ? "0" + data.month : "" + data.month;
        String day = data.day < 10 ? "0" + data.day : "" + data.day;
        String hour = data.hour < 10 ? "0" + data.hour : "" + data.hour;
        String minute = data.minute < 10 ? "0" + data.minute : "" + data.minute;
        String second = data.second < 10 ? "0" + data.second : "" + data.second;
        return data.year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
    }
//
//    /**
//     * 执行OkHttp请求操作,上传睡眠和步数
//     *
//     * @param path     地址
//     * @param params   参数
//     * @param type     类型
//     * @param position 数据源下标,用于上传数据成功后更新本地数据库
//     */
//    private void request(String path, Map<String, Object> params, final int type, final int
//            position) {
//        OkHttpTool.getInstance().doRequest(path, params, this, new OkHttpTool.HttpResult() {
//            @Override
//            public void onResult(String result) {
//                if (WatchUtils.isEmpty(result))
//                    return;
//                LogTestUtil.e(TAG, "睡眠上传返回" + result);
//                handlerResult(result, type, position);
//            }
//        });
////        OkHttpTool.getInstance().doRequest(path, params, this, new OkHttpTool.HttpResult() {
////            @Override
////            public void onResult(String result) {
////                if (WatchUtils.isEmpty(result))
////                    return;
////                LogTestUtil.e(TAG, "睡眠上传返回" + result);
////                handlerResult(result, type, position);
////            }
////        }, false);
//    }
//
//    /**
//     * 步数上传返回
//     *
//     * @param path
//     * @param params
//     * @param type
//     * @param position
//     */
//    private void requestStep(String path, Map<String, Object> params, final int type, final int
//            position) {
//        OkHttpTool.getInstance().doRequest(path, params, this, new OkHttpTool.HttpResult() {
//            @Override
//            public void onResult(String result) {
//                if (WatchUtils.isEmpty(result))
//                    return;
//                LogTestUtil.e(TAG, "步数上传返回" + result);
//                handlerResult(result, type, position);
//            }
//        });
////        OkHttpTool.getInstance().doRequest(path, params, this, new OkHttpTool.HttpResult() {
////            @Override
////            public void onResult(String result) {
////                if (WatchUtils.isEmpty(result))
////                    return;
////                LogTestUtil.e(TAG, "步数上传返回" + result);
////                handlerResult(result, type, position);
////            }
////        }, false);
//    }
//
//
//    private void requestBp(String dbp, String sbp, String time, final int type, final int position, final boolean ishandler) {
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//
//        /**
//         * 判断为0不上传
//         */
//        if (Integer.valueOf(sbp) > 0 || Integer.valueOf(dbp) > 0) {
//            Map<String, Object> params = new HashMap<>();
//            params.put("DBP", dbp);
//            params.put("SBP", sbp);
//            params.put("HB", "0");
//            params.put("CARDID", deviceCode);
////        params.put("CDATE", format.format(new Date()));
//            params.put("CDATE", time);
//            String path = URLs.GDHTTPS + "/SendBPInfo";
//            LogTestUtil.e(TAG, "------AAA-GD: 血压上传参数" + params.toString());
//            OkHttpTool.getInstance().doRequest(path, params, this, new OkHttpTool.HttpResult() {
//                @Override
//                public void onResult(String result) {
//                    if (WatchUtils.isEmpty(result))
//                        return;
//                    LogTestUtil.e(TAG, "------AAA-GD: 血压上传返回" + result);
//                    if (ishandler) handlerResult(result, type, position);
//                }
//            });
//        } else {
//            String result = "{\"STATUS\":\"500\",\"CARDID\":\"D6:64:CB:24:7E:74\",\"USERNAME\":\"开发者\", \"MESSAGE\":\"Duplicate entry 'D6:64:CB:24:7E:74-2018-12-07 03:30:00' for key 'PRIMARY'\"}";
//            LogTestUtil.e(TAG, "血压为0了不上传" + result);
//            if (ishandler) handlerResult(result, type, position);
//        }
//
//
////        OkHttpTool.getInstance().doRequest(path, params, this, new OkHttpTool.HttpResult() {
////            @Override
////            public void onResult(String result) {
////                if (WatchUtils.isEmpty(result))
////                    return;
////                LogTestUtil.e(TAG, "血压上传返回" + result);
////                handlerResult(result, type, position);
////            }
////        }, false);
//
//
////        String path = URLs.HTTPs + URLs.upHeart;
////        OkHttpTool.getInstance().doRequest(path, json, this, new OkHttpTool.HttpResult() {
////            @Override
////            public void onResult(String result) {
////                if (WatchUtils.isEmpty(result))
////                    return;
////                LogTestUtil.e("------AAA-血压传返回", result);
////                handlerResult(result, type, position);
////            }
////        });
//    }
//
//    /**
//     * 处理请求结果
//     */
//    private void handlerResult(String result, int type, int position) {
//        //LogTestUtil.e(TAG, type + ",上传数据结果:" + result + ",position:" + position);
//        if (resultSuccess(result)) changeUpload(type, position);
//
//        switch (type) {
//            case STATE_SPORT:
//                uploadSportData(++position);// 上传下一条运动数据
//                // LogTestUtil.e(TAG, "步数上传第" + position + "条");
//                break;
//            case STATE_SLEEP:
//                uploadSleepData(++position);// 上传下一条睡眠数据
//                // LogTestUtil.e(TAG, "睡眠上传第" + position + "条");
//                break;
//            case STATE_RATE:
//                uploadRateData(++position);// 上传下一条心率数据
//                //LogTestUtil.e(TAG, "心率上传第" + position + "条");
//                break;
//            case STATE_BP:
//                uploadBpData(++position);// 上传下一条血压数据
//                //LogTestUtil.e(TAG, "血压上传第" + position + "条");
//                break;
//        }
//    }
//

    /**
     * 请求结果是否是成功的
     *
     * @param result 请求结果
     * @return 是否成功
     */
    private boolean resultSuccess(String result) {
        if (WatchUtils.isEmpty(result)) {
            MyApp.getInstance().setUploadDateGD(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行
            return false;
        }
        ResultVo resultVo = null;
        try {
            resultVo = gson.fromJson(result, ResultVo.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        //{"STATUS":"500","CARDID":"E7:A7:0F:11:BE:B5","USERNAME":"使用者","MESSAGE":"Duplicate entry 'E7:A7:0F:11:BE:B5-2019-01-07 00:00:00' for key 'PRIMARY'"}
        //{"STATUS":"500","CARDID":"D6:64:CB:24:7E:74","USERNAME":"使用者", "MESSAGE":"Duplicate entry 'D6:64:CB:24:7E:74-2018-12-07 03:30:00' for key 'PRIMARY'"},position:1
        //LogTestUtil.e(TAG, "=====返回" + (resultVo != null && resultVo.MESSAGE.contains("PRIMARY")));
        final String RESULT_CODE = "200";// 上传数据成功结果码
        if (resultVo != null) {
            if (resultVo.STATUS.equals(RESULT_CODE)) {
                return resultVo.STATUS.equals(RESULT_CODE);
            } else if (resultVo.MESSAGE.contains("PRIMARY") || resultVo.STATUS.equals("500")) {
                return true;
            } else {
                MyApp.getInstance().setUploadDateGD(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行
                return false;
            }
        } else {
            MyApp.getInstance().setUploadDateGD(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行
            return false;
        }
        //return resultVo != null && resultVo.STATUS.equals(RESULT_CODE);
    }
//
//    /**
//     * 改变本地数据库上传数据状态
//     */
//    private void changeUpload(int type, int position) {
//        B30HalfHourDB dbData = null;
//        switch (type) {
//            case STATE_SPORT:
//                dbData = sportData.get(position);
//                break;
//            case STATE_SLEEP:
//                dbData = sleepData.get(position);
//                break;
//            case STATE_RATE:
//                dbData = rateData.get(position);
//                break;
//            case STATE_BP:
//                dbData = bpData.get(position);
//                break;
//        }
//        if (dbData != null && !dbData.getDate().equals(CURR_DATE)) {
//            dbData.setUploadGD(1);// 只要不是当天的数据,都把本地状态设为"已上传"
//            dbData.save();//因为当天数据还有可能不停的更新和上传
//        }
//    }

    /**
     * 内部类,请求结果模型
     */
    private class ResultVo {

        /**
         * "STATUS":"200"
         * CARDID : D6:64:CB:24:7E:74
         * USERNAME : 使用者
         * MESSAGE : INSERT SUCCESSFUL
         */
        String STATUS;
        private String CARDID;
        private String USERNAME;
        private String MESSAGE;

        public String getSTATUS() {
            return STATUS;
        }

        public void setSTATUS(String STATUS) {
            this.STATUS = STATUS;
        }


        public String getCARDID() {
            return CARDID;
        }

        public void setCARDID(String CARDID) {
            this.CARDID = CARDID;
        }

        public String getUSERNAME() {
            return USERNAME;
        }

        public void setUSERNAME(String USERNAME) {
            this.USERNAME = USERNAME;
        }

        public String getMESSAGE() {
            return MESSAGE;
        }

        public void setMESSAGE(String MESSAGE) {
            this.MESSAGE = MESSAGE;
        }
    }

    /**
     * 内部类,健康数据最终提交数据模型
     */
    private class HealthParamVo {
        List<HealthVo> data;
    }

    /**
     * 内部类,健康数据模型
     */
    private class HealthVo {
        /**
         * 用户ID
         */
        String userId;
        /**
         * 手环MAC地址
         */
        String deviceCode;
        /**
         * 心率
         */
        String heartRate;
        /**
         * 血氧
         */
        String bloodOxygen;
        /**
         * 低压
         */
        String systolic;
        /**
         * 高压
         */
        String diastolic;
        /**
         * 是否合格,1
         */
        String status;
        /**
         * yyyy-MM-dd HH:mm
         */
        String date;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getDeviceCode() {
            return deviceCode;
        }

        public void setDeviceCode(String deviceCode) {
            this.deviceCode = deviceCode;
        }

        public String getHeartRate() {
            return heartRate;
        }

        public void setHeartRate(String heartRate) {
            this.heartRate = heartRate;
        }

        public String getBloodOxygen() {
            return bloodOxygen;
        }

        public void setBloodOxygen(String bloodOxygen) {
            this.bloodOxygen = bloodOxygen;
        }

        public String getSystolic() {
            return systolic;
        }

        public void setSystolic(String systolic) {
            this.systolic = systolic;
        }

        public String getDiastolic() {
            return diastolic;
        }

        public void setDiastolic(String diastolic) {
            this.diastolic = diastolic;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }

    /**
     * 内部类,运动数据模型
     */
    private class SportVo {
        /**
         * yyyy-MM-dd
         */
        String date;
        /**
         * 步数
         */
        String step;
        /**
         * 卡路里
         */
        String kcal;
        /**
         * 距离
         */
        String dis;
        /**
         * 时长,0
         */
        String timeLen;
        /**
         * 是否合格,1
         */
        String status;

        TimeData time;

        public TimeData getTime() {
            return time;
        }

        public void setTime(TimeData time) {
            this.time = time;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getStep() {
            return step;
        }

        public void setStep(String step) {
            this.step = step;
        }

        public String getKcal() {
            return kcal;
        }

        public void setKcal(String kcal) {
            this.kcal = kcal;
        }

        public String getDis() {
            return dis;
        }

        public void setDis(String dis) {
            this.dis = dis;
        }

        public String getTimeLen() {
            return timeLen;
        }

        public void setTimeLen(String timeLen) {
            this.timeLen = timeLen;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

}

//    /**
//     * 改变本地数据库上传数据状态
//     */
//    private void changeUploadGD(int type, int position) {
//        B30HalfHourDB dbData = null;
//        switch (type) {
//            case STATE_SPORT:
//                dbData = sportData.get(position);
//                break;
//            case STATE_SLEEP:
//                dbData = sleepData.get(position);
//                break;
//            case STATE_RATE:
//                dbData = rateData.get(position);
//                break;
//            case STATE_BP:
//                dbData = bpData.get(position);
//                break;
//        }
//        if (dbData != null && !dbData.getDate().equals(CURR_DATE)) {
//            dbData.setUploadGD(1);// 只要不是当天的数据,都把本地状态设为"已上传"
//            dbData.save();//因为当天数据还有可能不停的更新和上传
//        }
//    }
//
//
//    /**
//     * 请求结果是否是成功的
//     *
//     * @param result 请求结果
//     * @return 是否成功
//     */
//    private boolean resultSuccessGD(String result) {
//        if (result == null) return false;
//        ResultVoGD resultVo = null;
//        try {
//            resultVo = gson.fromJson(result, ResultVoGD.class);
//        } catch (JsonSyntaxException e) {
//            e.printStackTrace();
//        }
//        final String RESULT_CODE = "200";// 上传数据成功结果码
//        return resultVo != null && resultVo.STATUS.equals(RESULT_CODE);
//    }
//
//    /**
//     * 内部类,请求结果模型
//     */
//    private class ResultVoGD {
//        /**
//         * 结果码
//         */
//        String STATUS;
//    }
//}
