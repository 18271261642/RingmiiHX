package com.guider.healthring.b30.service;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;


import com.guider.healthring.Commont;
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


public class UpdataThree extends AsyncTask<String, Void, Boolean> {
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
     * 该方法是运行在主线程中的
     * 该方法在task真正执行前运行，我们通常可以在该方法中显示一个进度条，从而告知用户后台任务即将开始
     */
    @SuppressLint("CheckResult")
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    /**
     * 该方法是运行在单独的工作线程中的，而不是运行在主线程中
     * doInBackground会在onPreExecute()方法执行完成后立即执行
     * 该方法用于在工作线程中执行耗时任务，我们可以在该方法中编写我们需要在后台线程中运行的逻辑代码
     * </p>
     * 由于doInBackgroud是抽象方法，我们在使用AsyncTask时必须重写该方法。
     * 在doInBackground中执行的任务可能要分解为好多步骤，
     * 每完成一步我们就可以通过调用AsyncTask的publishProgress(Progress…)将阶段性的处理结果发布出去，
     * 阶段性处理结果是Progress泛型类型。当调用了publishProgress方法后，处理结果会被传递到UI线程中，
     * 并在UI线程中回调onProgressUpdate方法，下面会详细介绍。根据我们的具体需要，
     * 我们可以在doInBackground中不调用publishProgress方法，当然也可以在该方法中多次调用publishProgress方法。
     * doInBackgroud方法的返回值表示后台线程完成任务之后的结果。
     *
     * @param strings
     * @return
     */
    @Override
    protected Boolean doInBackground(String... strings) {
        registerOrLogin();
        return null;
    }

    String deviceCode;
    String userId;
    private void registerOrLogin() {

        deviceCode = (String) SharedPreferencesUtils.readObject(MyApp.getInstance(), Commont.BLEMAC);
        userId = (String) SharedPreferencesUtils.readObject(MyApp.getInstance(), "userId");
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
        final List<HalfHourSportData> dataList = new Gson().fromJson(originData, new TypeToken<List<HalfHourSportData>>() {
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
//                    LogTestUtil.e(TAG, "sport- 步数数据" + dataList.size() + "===" + date + " " + dates + "-00" + "-----" + all);
                    /**
                     * 判断为0不上传
                     */
                    if (all > 0) {
                        intoNubmerSport++;

//                        LogTestUtil.e(TAG,
//                                "sport- 有效步数的数据------有效值长度：  " + numberCountSport +
//                                        "   有效值累计" + intoNubmerSport +
//                                        "== 步数" + all + "====时间" + dates.replace(":", "-"));
                        if (intoNubmerSport < numberCountSport){
                            Map<String, Object> params = new HashMap<>();
                            params.put("WALKTIME", "0");
                            params.put("CARDID", deviceCode);
                            params.put("STEPS", all);
                            params.put("DISTANCE", 0);
                            params.put("CALORIE", 0);
                            params.put("CDATE", date + " " + dates + "-00");
//                            LogTestUtil.e(TAG, "sport- 步数时间对比：" + dataList.get(i).getDate() + "====" + date);
//                            LogTestUtil.e(TAG, "sport- 上传步数参数:" + params.toString());

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
        SleepData sleepData = new Gson().fromJson(originData, SleepData.class);
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

    private void setUpHraet(final String date, String originData, final boolean isLast) {
        final List<HalfHourRateData> dataList = new Gson().fromJson(originData, new TypeToken<List<HalfHourRateData>>() {
        }.getType());


        if (dataList != null) {
//            LogTestUtil.e(TAG, "heart- 时间: " + date + " 的数据一共有  " + dataList.size() + " 条");
            numberCountHeart = 0;
            intoNubmerHeart = 0;
            for (int i = 0; i < dataList.size(); i++) {
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
//                            LogTestUtil.e(TAG, "heart- 有效心率的数据------有效值长度：  " + numberCountHeart + "   有效值累计" + intoNubmerHeart + "==" + rateValue + "====" + date + "====" + dates.replace(":", "-"));
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
        final List<HalfHourBpData> dataList = new Gson().fromJson(originData, new TypeToken<List<HalfHourBpData>>() {
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
    

    /**
     * 当我们在doInBackground中调用publishProgress(Progress…)方法后，就会在UI线程上回调onProgressUpdate方法
     *
     * @param values
     */
    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    /**
     * 该方法是在主线程中被调用的。当doInBackgroud方法执行完毕后，就表示任务完成了，
     * doInBackgroud方法的返回值就会作为参数在主线程中传入到onPostExecute方法中，
     * 这样就可以在主线程中根据任务的执行结果更新UI。
     *
     * @param aBoolean
     */
    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
    }



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
            resultVo = new Gson().fromJson(result, ResultVo.class);
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
