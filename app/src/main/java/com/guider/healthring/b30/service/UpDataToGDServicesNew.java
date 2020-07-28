package com.guider.healthring.b30.service;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.guider.health.apilib.BuildConfig;
import com.guider.healthring.B18I.b18iutils.B18iUtils;
import com.guider.healthring.Commont;
import com.guider.healthring.MyApp;
import com.guider.healthring.b30.bean.B30HalfHourDB;
import com.guider.healthring.b30.bean.B30HalfHourDao;
import com.guider.healthring.b30.bean.BaseResultVoNew;
import com.guider.healthring.b30.bean.ResultVoNew;
import com.guider.healthring.bean.BlueUser;
import com.guider.healthring.bean.TypeUserDatas;
import com.guider.healthring.bean.WXUserBean;
import com.guider.healthring.h9.utils.H9TimeUtil;
import com.guider.healthring.siswatch.utils.WatchUtils;
import com.guider.healthring.util.GetJsonDataUtil;
import com.guider.healthring.util.MyLogUtil;
import com.guider.healthring.util.OkHttpTool;
import com.guider.healthring.util.SharedPreferencesUtils;
import com.guider.healthring.w30s.utils.W30BasicUtils;
import com.veepoo.protocol.model.datas.HalfHourBpData;
import com.veepoo.protocol.model.datas.HalfHourRateData;
import com.veepoo.protocol.model.datas.HalfHourSportData;
import com.veepoo.protocol.model.datas.SleepData;
import com.veepoo.protocol.model.datas.SportData;
import com.veepoo.protocol.model.datas.TimeData;
import com.veepoo.protocol.util.HrvScoreUtil;
import com.veepoo.protocol.util.Spo2hOriginUtil;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.veepoo.protocol.model.enums.ESpo2hDataType.TYPE_BREATH;
import static com.veepoo.protocol.model.enums.ESpo2hDataType.TYPE_HEART;
import static com.veepoo.protocol.model.enums.ESpo2hDataType.TYPE_LOWSPO2H;
import static com.veepoo.protocol.model.enums.ESpo2hDataType.TYPE_SLEEP;
import static com.veepoo.protocol.model.enums.ESpo2hDataType.TYPE_SPO2H;

public class UpDataToGDServicesNew extends AsyncTask<Void, Void, Void> {
    private final String TAG = "UpDataToGDServicesNew";
    private final String Base_Url = BuildConfig.APIURL + "api/v1/"; // http://47.92.218.150:8082/
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
    private String deviceCode;
    private String userId;
    private String upDataSysTime;
    private String timeDifference;
    long accountId = 0;
    private String phone, wechartJson;

    private TypeUserDatas typeUserDatas;

    // 方法1：onPreExecute（）
    // 作用：执行 线程任务前的操作
    // 注：根据需求复写
    @Override
    protected void onPreExecute() {
        deviceCode = (String) SharedPreferencesUtils.readObject(MyApp.getInstance(), Commont.BLEMAC);

        String userDetailedData = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), "UserDetailedData");
        if (!WatchUtils.isEmpty(userDetailedData)) {
            typeUserDatas = new Gson().fromJson(userDetailedData, TypeUserDatas.class);
            Log.e(TAG,"-------onPreExecute--typeUserDatas="+typeUserDatas);
        }
        /*
        userId = (String) SharedPreferencesUtils.readObject(MyApp.getInstance(), "userId");

        phone = "" + (String) SharedPreferencesUtils.readObject(MyApp.getContext(), "phoneNumber");
        wechartJson = "" + (String) SharedPreferencesUtils.readObject(MyApp.getContext(), "wechartJson");

        upDataSysTime = (String) SharedPreferencesUtils.getParam(MyApp.getInstance(), "UpGdServices", "2017-11-02 15:00:00");
        timeDifference = H9TimeUtil.getTimeDifferencesec(upDataSysTime, B18iUtils.getSystemDataStart());
        */
    }

    // 方法2：doInBackground（）
    // 作用：接收输入参数、执行任务中的耗时操作、返回 线程任务执行的结果
    // 注：必须复写，从而自定义线程任务
    @Override
    protected Void doInBackground(Void... voids) {
        // Task被取消了，马上退出循环
        if (isCancelled()) return null;

        Log.e(TAG, "-------上传开始啦");

        // 可调用publishProgress（）显示进度, 之后将执行onProgressUpdate（）
//        publishProgress();
        try {
            findNotUploadData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
        B30HalfHourDao instance = B30HalfHourDao.getInstance();
        if (instance != null) {
            sportData = instance.findNotUploadDataGD(deviceCode, B30HalfHourDao.TYPE_SPORT);
            sleepData = instance.findNotUploadDataGD(deviceCode, B30HalfHourDao.TYPE_SLEEP);
            rateData = instance.findNotUploadDataGD(deviceCode, B30HalfHourDao.TYPE_RATE);
            bpData = instance.findNotUploadDataGD(deviceCode, B30HalfHourDao.TYPE_BP);
            if (sportData != null && !sportData.isEmpty()) {
                Log.e(TAG, "未上传数据条数 运动: " + sportData.size());
            }
            if (sleepData != null && !sleepData.isEmpty()) {
                Log.e(TAG, "未上传数据条数 睡眠: " + sleepData.size());
            }
            if (rateData != null && !rateData.isEmpty()) {
                Log.e(TAG, "未上传数据条数 心率: " + rateData.size());
            }
            if (bpData != null && !bpData.isEmpty()) {
                Log.e(TAG, "未上传数据条数 血压: " + bpData.size());
            }
            if ((sportData != null && !sportData.isEmpty())
                    || (sleepData != null && !sleepData.isEmpty())
                    || (rateData != null && !rateData.isEmpty())
                    || (bpData != null && !bpData.isEmpty())) {
                Log.e(TAG, "数据库中存在数据------开始登陆账户-----去上传==（运动->睡眠->心率->血压）");

                //登陆到盖得后台
                long guiderId = (long) SharedPreferencesUtils.getParam(MyApp.getContext(),"accountIdGD",0L);
                if(guiderId != 0){
                    accountId = guiderId;
                    bindDevices(guiderId);
                } else {
                    loginGdServices();
                }
//                if (!WatchUtils.isEmpty(timeDifference)) {
//                    int number = Integer.valueOf(timeDifference.trim());
//                    if (!MyApp.isLogin || Math.abs(number) >= 13 * 60) {//十五分钟可以设置一次
//                        //登陆到盖得后台
//                        loginGdServices();
//                    } else {
//                        //并发一起上传
//                        findUnUpdataToservices();
//                    }
//                }
            } else {
                //结束上传
                onCancelled();
            }
        }
    }

    /**
     * 登陆到盖得后台
     */
    private void loginGdServices() {
        Map<String, String> params = new HashMap<>();
        params.clear();

        if (typeUserDatas == null) {
            String userDetailedData = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), "UserDetailedData");
            if (!WatchUtils.isEmpty(userDetailedData)) {
                typeUserDatas = new Gson().fromJson(userDetailedData, TypeUserDatas.class);
            }
        }
        if (typeUserDatas == null) return;
        String typeData = typeUserDatas.getTypeData()+"--str="+typeUserDatas.getDataJson();
        Log.e(TAG,"----typeData="+typeData+typeData.equals("LOGION_PHONE"));

        String phoneType = "LOGION_PHONE";


        if (typeUserDatas.getTypeData().equals(phoneType)) {
            String dataJson = typeUserDatas.getDataJson();
            BlueUser blueUser = new Gson().fromJson(dataJson, BlueUser.class);
            String phone = blueUser.getPhone();
            Log.e(TAG,"-----phone="+phone);
            // params.put("phone", phone);
            if (Commont.isDebug)Log.e(TAG, "游客注册或者登陆参数：" + params.toString());
            String loginUrl = Base_Url + "login/onlyphone?phone=" + phone;
            Log.e(TAG,"-------手机号登录的url="+loginUrl);
            OkHttpTool.getInstance().doRequest(loginUrl,null, "1", loginHttpResult,false);
        } else {
            /**
             * {
             *   "appId": "onxAK59awxJ17IjZnaYslUiOshEE",
             *   "headimgurl": "http://thirdwx.qlogo.cn/mmopen/vi_32/qndNbICrNwia9HQHMq8Bu4CAJ6KCfum9RQe408vq76KibSYiaicibbQXOuhlJzibEL8PrX1E3l6iaQH4UMjllrM6icVhIQ/132",
             *   "nickname": "丶",
             *   "openid": "onxAK59awxJ17IjZnaYslUiOshEE",
             *   "sex": 1,
             *   "unionid": "oaVtW5_Yp-o9NPhbmlqFfUn-5He0"
             * }
             */

            String dataJson = typeUserDatas.getDataJson();
            Log.e(TAG,"-------dataJson="+dataJson);
            WXUserBean wxUserBean = new Gson().fromJson(dataJson, WXUserBean.class);
            Log.e(TAG,"------wxUserBean="+wxUserBean.toString());
            params.put("appId", wxUserBean.getOpenid() + "");
            params.put("headimgurl", wxUserBean.getHeadimgurl() + "");
            params.put("nickname", wxUserBean.getNickname() + "");
            params.put("openid", wxUserBean.getOpenid());
            params.put("sex", (wxUserBean.getSex().equals("M")?1:0)+"");
            params.put("unionid", wxUserBean.getUnionid() + "");
            if (Commont.isDebug)Log.e(TAG, "222游客注册或者登陆参数：" + params.toString());
            JSONObject json = new JSONObject(params);

            if (Commont.isDebug)Log.e(TAG, "====  json  " + json.toString());
            OkHttpTool.getInstance().doRequest(Base_Url + "/login/wachat",
                    json.toString(), this, loginHttpResult);
        }
    }

    /**
     * 登陆到盖得后台
     */
    private void loginGdServicesOld() {
        Map<String, String> params = new HashMap<>();
        params.clear();

        if (!WatchUtils.isEmpty(phone)) {
            params.put("", "");
            Log.e(TAG, "游客注册或者登陆参数：" + params.toString());
            OkHttpTool.getInstance().doRequest(Base_Url + "login/onlyphone?phone=" + phone,
                    null, this, loginHttpResult, false);
        } else {
            /**
             * {
             *   "appId": "onxAK59awxJ17IjZnaYslUiOshEE",
             *   "headimgurl": "http://thirdwx.qlogo.cn/mmopen/vi_32/qndNbICrNwia9HQHMq8Bu4CAJ6KCfum9RQe408vq76KibSYiaicibbQXOuhlJzibEL8PrX1E3l6iaQH4UMjllrM6icVhIQ/132",
             *   "nickname": "丶",
             *   "openid": "onxAK59awxJ17IjZnaYslUiOshEE",
             *   "sex": 1,
             *   "unionid": "oaVtW5_Yp-o9NPhbmlqFfUn-5He0"
             * }
             */
            if (!WatchUtils.isEmpty(wechartJson)) {
                WXUserBean wxUserBean = new Gson().fromJson(wechartJson, WXUserBean.class);

                params.put("appId", wxUserBean.getOpenid() + "");
                params.put("headimgurl", wxUserBean.getHeadimgurl() + "");
                params.put("nickname", wxUserBean.getNickname() + "");
                params.put("openid", wxUserBean.getOpenid() + "");
                params.put("sex", wxUserBean.getSex() + "");
                params.put("unionid", wxUserBean.getUnionid() + "");
                Log.e(TAG, "222游客注册或者登陆参数：" + params.toString());
                JSONObject json = new JSONObject(params);

                Log.e(TAG, "====  json  " + json.toString());
                OkHttpTool.getInstance().doRequest(Base_Url + "/login/wachat",
                        json.toString(), this, loginHttpResult);
            }

        }


    }

    /**
     * 登陆返回
     */
    OkHttpTool.HttpResult loginHttpResult = new OkHttpTool.HttpResult() {
        @Override
        public void onResult(String result) {
            MyApp.getInstance().setUploadDateGDNew(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行

            if (!WatchUtils.isEmpty(result)) {
                if (Commont.isDebug)Log.e(TAG, "游客注册或者登陆上传返回" + result);

                ResultVoNew resultVo = null;
                try {
                    resultVo = new Gson().fromJson(result, ResultVoNew.class);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
                //{"STATUS":"500","CARDID":"E7:A7:0F:11:BE:B5","USERNAME":"使用者","MESSAGE":"Duplicate entry 'E7:A7:0F:11:BE:B5-2019-01-07 00:00:00' for key 'PRIMARY'"}
                //{"STATUS":"500","CARDID":"D6:64:CB:24:7E:74","USERNAME":"使用者", "MESSAGE":"Duplicate entry 'D6:64:CB:24:7E:74-2018-12-07 03:30:00' for key 'PRIMARY'"},position:1
                //LogTestUtil.e(TAG, "=====返回" + (resultVo != null && resultVo.MESSAGE.contains("PRIMARY")));
                final int RESULT_CODE = 0;// 上传数据成功结果码
                if (resultVo != null) {
                    if (resultVo.getCode() == RESULT_CODE) {

                        ResultVoNew.DataBean dataBean = resultVo.getData();
                        if (dataBean != null) {
                            accountId = dataBean.getAccountId();

                            SharedPreferencesUtils.setParam(MyApp.getInstance(), "accountIdGD", (long)accountId);
                            String token = dataBean.getToken();
                            SharedPreferencesUtils.setParam(MyApp.getInstance(), "tokenGD", accountId);
                            if (Commont.isDebug)Log.e(TAG, "游客注册或者登陆成功：开始上传步数");
                            MyApp.isLogin = true;
                            SharedPreferencesUtils.setParam(MyApp.getInstance(), "UpGdServices", B18iUtils.getSystemDataStart());

                            //并发一起上传
                            //findUnUpdataToservices();


                            //登陆成功之后----同步用户信息
                            //sycyToServices_GD(accountId);
                            bindDevices(accountId);

                        } else {
                            MyApp.getInstance().setUploadDateGDNew(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行

                            if (Commont.isDebug)Log.e(TAG, "游客注册或者登陆失败---上传返回" + result);
                            Intent intent = new Intent("com.example.bozhilun.android.b30.service.UploadServiceGD");
                            MyApp.getInstance().sendBroadcast(intent);
                            onCancelled();
                        }


                    } else {
                        MyApp.getInstance().setUploadDateGDNew(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行

                        if (Commont.isDebug)Log.e(TAG, "游客注册或者登陆失败---上传返回" + result);
                        Intent intent = new Intent("com.example.bozhilun.android.b30.service.UploadServiceGD");
                        MyApp.getInstance().sendBroadcast(intent);
                        onCancelled();
                    }
                } else {
                    MyApp.getInstance().setUploadDateGDNew(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行

                    if (Commont.isDebug)Log.e(TAG, "游客注册或者登陆失败---上传返回" + result);
                    Intent intent = new Intent("com.example.bozhilun.android.b30.service.UploadServiceGD");
                    MyApp.getInstance().sendBroadcast(intent);
                    onCancelled();
                }

            } else {
                MyApp.getInstance().setUploadDateGDNew(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行

                if (Commont.isDebug)Log.e(TAG, "游客注册或者登陆失败---上传返回" + result);
                Intent intent = new Intent("com.example.bozhilun.android.b30.service.UploadServiceGD");
                MyApp.getInstance().sendBroadcast(intent);
                onCancelled();
            }

        }
    };

    /**
     * 绑定设备
     *
     * @param accountId
     */
    void bindDevices(long accountId) {

        Map<String, Object> params = new HashMap<>();
//        params.put("accountId", accountId);//accountId
//        params.put("deviceCode", deviceCode);//deviceCode
//        JSONObject json = new JSONObject(params);
        String upStepPatch = Base_Url + "user/" + accountId + "/deviceandcompany/bind?deviceCode=" + deviceCode;
        //{accountId}/deviceandcompany/bind?deviceCode=
        OkHttpTool.getInstance().doRequest(upStepPatch, null, this, new OkHttpTool.HttpResult() {
            @Override
            public void onResult(String result) {
                try {
                    if (!WatchUtils.isEmpty(result)) {
                        Log.e(TAG,"------bindDevices--="+result);
                        if(result.contains("Unable"))
                            return;
                        BaseResultVoNew baseResultVoNew = new Gson().fromJson(result, BaseResultVoNew.class);
                        if (Commont.isDebug)Log.d(TAG, "=======绑定该设备A= " + result);
                        if (baseResultVoNew.getCode() == 0 || baseResultVoNew.getMsg().equals("您已经绑定该设备")) {
                            if (Commont.isDebug)Log.d(TAG, "=======绑定该设备= " + result);
                            //并发一起上传
                            findUnUpdataToservices();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        },false);
    }

    /**
     * 登陆返回
     */
    OkHttpTool.HttpResult loginHttpResultOld = new OkHttpTool.HttpResult() {
        @Override
        public void onResult(String result) {
            MyApp.getInstance().setUploadDateGDNew(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行

            if (!WatchUtils.isEmpty(result)) {
                Log.e(TAG, "游客注册或者登陆上传返回" + result);

                ResultVoNew resultVo = null;
                try {
                    resultVo = new Gson().fromJson(result, ResultVoNew.class);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
                //{"STATUS":"500","CARDID":"E7:A7:0F:11:BE:B5","USERNAME":"使用者","MESSAGE":"Duplicate entry 'E7:A7:0F:11:BE:B5-2019-01-07 00:00:00' for key 'PRIMARY'"}
                //{"STATUS":"500","CARDID":"D6:64:CB:24:7E:74","USERNAME":"使用者", "MESSAGE":"Duplicate entry 'D6:64:CB:24:7E:74-2018-12-07 03:30:00' for key 'PRIMARY'"},position:1
                //LogTestUtil.e(TAG, "=====返回" + (resultVo != null && resultVo.MESSAGE.contains("PRIMARY")));
                final int RESULT_CODE = 0;// 上传数据成功结果码
                if (resultVo != null) {
                    if (resultVo.getCode() == RESULT_CODE) {

                        ResultVoNew.DataBean dataBean = resultVo.getData();
                        if (dataBean != null) {
                            accountId = dataBean.getAccountId();

                            Log.e(TAG, "游客注册或者登陆成功：开始上传步数");
                            MyApp.isLogin = true;
                            SharedPreferencesUtils.setParam(MyApp.getInstance(), "UpGdServices", B18iUtils.getSystemDataStart());

                            //并发一起上传
                            findUnUpdataToservices();
                        } else {
                            MyApp.getInstance().setUploadDateGDNew(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行

                            Log.e(TAG, "游客注册或者登陆失败---上传返回" + result);
                            Intent intent = new Intent("com.example.bozhilun.android.b30.service.UploadServiceGD");
                            MyApp.getInstance().sendBroadcast(intent);
                            onCancelled();
                        }


                    } else {
                        MyApp.getInstance().setUploadDateGDNew(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行

                        Log.e(TAG, "游客注册或者登陆失败---上传返回" + result);
                        Intent intent = new Intent("com.example.bozhilun.android.b30.service.UploadServiceGD");
                        MyApp.getInstance().sendBroadcast(intent);
                        onCancelled();
                    }
                } else {
                    MyApp.getInstance().setUploadDateGDNew(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行

                    Log.e(TAG, "游客注册或者登陆失败---上传返回" + result);
                    Intent intent = new Intent("com.example.bozhilun.android.b30.service.UploadServiceGD");
                    MyApp.getInstance().sendBroadcast(intent);
                    onCancelled();
                }

            } else {
                MyApp.getInstance().setUploadDateGDNew(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行

                Log.e(TAG, "游客注册或者登陆失败---上传返回" + result);
                Intent intent = new Intent("com.example.bozhilun.android.b30.service.UploadServiceGD");
                MyApp.getInstance().sendBroadcast(intent);
                onCancelled();
            }

//            if (!WatchUtils.isEmpty(result)) {
//
//                if (resultSuccess(result)) {
//                    Log.e(TAG, "游客注册或者登陆成功：开始上传步数");
//                    MyApp.isLogin = true;
//                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "UpGdServices", B18iUtils.getSystemDataStart());
//
//                    //并发一起上传
//                    findUnUpdataToservices();
//                } else {
//                    Log.e(TAG, "游客注册或者登陆失败---上传返回" + result);
//                    Intent intent = new Intent("com.example.bozhilun.android.b30.service.UploadServiceGD");
//                    MyApp.getInstance().sendBroadcast(intent);
//                    onCancelled();
//                }
//            }

        }
    };


    private boolean upSport = false;
    private boolean upSleep = false;
    private boolean upHeart = false;
    private boolean upBool = false;

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 0x01:
                    if (upSport && upSleep && upHeart && upBool) {

                        changeUpload(STATE_SPORT);
                        changeUpload(STATE_SLEEP);
                        changeUpload(STATE_RATE);
                        changeUpload(STATE_BP);

                        handler.sendEmptyMessageDelayed(0x02, 1000);
                    } else {
                        handler.sendEmptyMessageDelayed(0x01, 10000);
                    }
                    break;
                case 0x02:
                    MyApp.getInstance().setUploadDateGDNew(false);
                    onCancelled();
                    break;
            }
            return false;
        }
    });

    /**
     * 并行上传
     */
    private synchronized void findUnUpdataToservices() {
        boolean uploadingGD = MyApp.getInstance().isUploadDateGDNew();
        if (!uploadingGD) {
            MyApp.getInstance().setUploadDateGDNew(true);// 正在上传数据,写到全局,保证同时只有一个本服务在运行
            upSport = false;
            upSleep = false;
            upHeart = false;
            upBool = false;

            // 上传数据的顺序为  步数---睡眠----心率----血压
            //STATE_SPORT     STATE_SLEEP   STATE_RATE   STATE_BP
            // handlerNewResult(STATE_SPORT);
            // 开始 读取上传运动数据
            getStepUpToServices();
            getSleepUpToServices();
            getHeartUpToServices();
            getBpUpToServices();
            uploadRingData();
            if (handler != null) handler.sendEmptyMessageDelayed(0x01, 20000);
        }
    }

    //统一上传接口
    private void uploadRingData() {
        //总的结果
        Map<String,Object> allResultMap = new HashMap<>();
        allResultMap.put("accountId",accountId);
        allResultMap.put("deviceCode",deviceCode);
        allResultMap.put("testTime",WatchUtils.getCurrentDate());
        //运动
        Map<String,Object> sportDataMap = new HashMap<>();

        List<B30HalfHourDB> sportGDList = B30HalfHourDao.getInstance().findGDTodayData(deviceCode,B30HalfHourDao.TYPE_SPORT);
        if(sportGDList == null || sportGDList.isEmpty())return;
        String orginStr = sportGDList.get(sportGDList.size()-1).getOriginData();

        // Log.e(TAG,"--------运动="+orginStr);
        List<HalfHourSportData> halfHourSportData = new Gson().fromJson(orginStr,
                new TypeToken<List<HalfHourSportData>>(){}.getType());

        List<Map<String,Object>> sportList = new ArrayList<>();
        for(HalfHourSportData hSport : halfHourSportData){
            if(hSport.getDate().equals(WatchUtils.getCurrentDate())){
                Map<String,Object> sMap = new HashMap<>();

                TimeData timeData = hSport.getTime();

                String spo2Dates = timeData.getDateAndClockForSleepSecond();

                Date dateStart = W30BasicUtils.stringToDate(spo2Dates.replace(":", "-"), "yyyy-MM-dd HH-mm-ss");
                String iso8601Timestamp = WatchUtils.getISO8601Timestamp(dateStart);

                sMap.put("stepValue",hSport.getStepValue());
                sMap.put("disValue",hSport.getDisValue());
                sMap.put("calValue",hSport.getCalValue());
                sMap.put("testTime",iso8601Timestamp);
                sportList.add(sMap);
            }
        }

        sportDataMap.put("data",sportList);
        String step_detail = B30HalfHourDao.getInstance().findOriginData(deviceCode,WatchUtils.getCurrentDate(),B30HalfHourDao.TYPE_STEP_DETAIL);
        if(WatchUtils.isEmpty(step_detail)){
            sportDataMap.put("totalStep",0);
            sportDataMap.put("totalDis",0.0);
            sportDataMap.put("totalCal",0.0);
        }else{
            SportData sportData = new Gson().fromJson(step_detail,SportData.class);
            sportDataMap.put("totalStep",sportData.getStep());
            sportDataMap.put("totalDis",sportData.getDis());
            sportDataMap.put("totalCal",sportData.getKcal());
        }

        allResultMap.put("sport",sportDataMap);

        //hrv
        Map<String,Object> hrvTempMap = new HashMap<>();
        hrvTempMap.put("data", new ArrayList<Map<String,Object>>());
        hrvTempMap.put("hrvIndex", 0);
        allResultMap.put("hrv",hrvTempMap);

        //spo2
        ParamNewDayData pndd = new ParamNewDayData();
        allResultMap.put("spo2", pndd); // "{ \"onedayDataArrLowSpo2h\" : [0,0,0 ],\"data\" : [],\"onedayDataArrLowBreath\" : [0,0,0],\"onedayDataArrSleep\" : [0,0,0],\"onedayDataArrHeart\" : [0,0,0],\"onedayDataArrSpo2h\" : [0,0,0],\"tmpLt\" : [],\"OSAHS\" : -1}");
        String allParmas = new Gson().toJson(allResultMap);

        String filePath = Environment.getExternalStorageDirectory()+"/DCIM/";
        new GetJsonDataUtil().writeTxtToFile(WatchUtils.getCurrentDate1()+allParmas,filePath,"ringData.json");

        //String allUrl = "http://apihd.guiderhealth.com/api/v1/ringdata";
        String allUrl = Commont.GAI_DE_BASE_URL + "ringdata";
        OkHttpTool.getInstance().doRequest(allUrl, allParmas, "", new OkHttpTool.HttpResult() {
            @Override
            public void onResult(String result) {
                Log.e(TAG,"--------所有上传返回="+result);
            }
        });
    }

    public class ParamNewDayData {
        /**
         * 分析结果
         */
        String OSAHS = "-1";
        /**
         * 呼吸暂停[{time:3322,value:1.0}]
         * time秒为单位转换成分秒就是对应的时间
         */
        List<Map<String, Object>> tmpLt = new ArrayList<>();
        /**
         * 低氧数据[最大值，最新值，平均值]
         */
        int[] onedayDataArrLowSpo2h = {0, 0, 0};
        /**
         * 呼吸率[最大值，最新值，平均值]
         */
        int[] onedayDataArrLowBreath = {0, 0, 0};
        /**
         * 睡眠活动[最大值，最新值，平均值]
         */
        int[] onedayDataArrSleep = {0, 0, 0};
        /**
         * 心脏负荷[最大值，最新值，平均值]
         */
        int[] onedayDataArrHeart = {0, 0, 0};
        /**
         * 血氧[最大值，最新值，平均值]
         */
        int[] onedayDataArrSpo2h = {0, 0, 0};
        /**
         * 血氧历史数据
         */
        List<Map<String, Object>> data = new ArrayList<>();
    }

    /**
     * 读取步数 上传步数到后台
     */
    private void getStepUpToServices() {
        if (sportData != null && !sportData.isEmpty()) {
            Log.e(TAG, "一共有 " + sportData.size() + " 天运动数据");
            for (int i = (sportData.size() - 1); i >= 0; i--) {
                Log.e(TAG, "第 " + i + " 天前的运动数据   日期是：" + sportData.get(i).getDate());
                String date = sportData.get(i).getDate();
                String originData = sportData.get(i).getOriginData();
                if (!WatchUtils.isEmpty(originData) && !WatchUtils.isEmpty(date)) {
                    //为倒序，i == 0  最后一个为今天，今天的数据要做处理
                    setUpSport(date, originData, i);
                } else {
                    if (i == 0) {
                        Log.e(TAG, "运动数据异常");
                        upSport = true;
                    }
                }
            }
        } else {
            Log.e(TAG, "没有运动数据");
            upSport = true;
        }
    }


    /**
     * 读取睡眠 --- 上传到盖得后台
     */
    private void getSleepUpToServices() {
        if (sleepData != null && !sleepData.isEmpty()) {
            Log.e(TAG, "一共有 " + sleepData.size() + " 天睡眠数据");
            for (int i = (sleepData.size() - 1); i >= 0; i--) {
                String date = sleepData.get(i).getDate();
                String originData = sleepData.get(i).getOriginData();
                Log.e(TAG, "第 " + i + " 天前的睡眠数据   日期是：" + sleepData.get(i).getDate());
                if (!WatchUtils.isEmpty(originData) && !WatchUtils.isEmpty(date)) {
                    //为倒序，i == 0  最后一个为今天，今天的数据要做处理
                    setUpSleep(date, originData, i);
                } else {
                    if (i == 0) {
                        Log.e(TAG, "睡眠数据异常");
                    }
                }
            }
        } else {
            Log.e(TAG, "没有睡眠数据");
        }
    }


    /**
     * 读取心率
     */
    private void getHeartUpToServices() {

        if (rateData != null && !rateData.isEmpty()) {
            Log.e(TAG, "一共有 " + rateData.size() + " 天心率数据");
            for (int i = (rateData.size() - 1); i >= 0; i--) {
                String date = rateData.get(i).getDate();
                String originData = rateData.get(i).getOriginData();
                Log.e(TAG, "第 " + i + " 天前的心率数据   日期是：" + rateData.get(i).getDate());
                if (!WatchUtils.isEmpty(originData) && !WatchUtils.isEmpty(date)) {
                    //为倒序，i == 0  最后一个为今天，今天的数据要做处理
                    if (i == 0) setUpHeart(date, originData, true);
                    else setUpHeart(date, originData, false);
                } else {
                    if (i == 0) {
                        upHeart = true;
                        Log.e(TAG, "心率数据异常");
                    }
                }
            }
        } else {
            upHeart = true;
            Log.e(TAG, "没有心率数据");
        }
    }


    /**
     * 读取血压
     */
    private void getBpUpToServices() {

        if (bpData != null && !bpData.isEmpty()) {
            Log.e(TAG, "一共有 " + bpData.size() + " 天血压数据");
            for (int i = (bpData.size() - 1); i >= 0; i--) {
                String date = bpData.get(i).getDate();
                String originData = bpData.get(i).getOriginData();
                Log.e(TAG, "第 " + i + " 天前的血压数据   日期是：" + bpData.get(i).getDate());
                if (!WatchUtils.isEmpty(originData) && !WatchUtils.isEmpty(date)) {
                    //为倒序，i == 0  最后一个为今天，今天的数据要做处理
                    setUpBp(date, originData, i);
                } else {
                    if (i == 0) {
                        upBool = true;
                        Log.e(TAG, "血压数据异常");
                    }
                }
            }
        } else {
            Log.e(TAG, "血压没有数据1");
            upBool = true;
        }
    }


    //private String result = "{\"STATUS\":\"500\",\"CARDID\":\"D6:64:CB:24:7E:74\",\"USERNAME\":\"开发者\", \"MESSAGE\":\"Duplicate entry 'D6:64:CB:24:7E:74-0000-00-00 00:00:00' for key 'PRIMARY'\"}";

    /**
     * 上传步数
     *
     * @param date
     * @param originData
     * @param postion    为倒序，最后一个为今天，今天的数据要做处理
     */
    private void setUpSport(String date, String originData, final int postion) {
        final List<HalfHourSportData> dataList = new Gson().fromJson(originData, new TypeToken<List<HalfHourSportData>>() {
        }.getType());
        if (dataList != null && !dataList.isEmpty()) {
            int SportCount = 0;//总的有效数据长度
            int SportCountAdd = 0;//总的有效数据长度
            for (int i = 0; i < dataList.size(); i++) {
                if (dataList.get(i).getStepValue() > 0) {
                    SportCount++;
                }
            }
            Log.e(TAG, date + "  运动数据总共长度为：" + dataList.size() + "  有效长度为：" + SportCount);
            for (int i = 0; i < dataList.size(); i++) {
                int allStep = dataList.get(i).getStepValue();
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
                String stepDate = stringHour + "-" + stringMinute;


                if (allStep > 0) {
                    SportCountAdd++;
                    if (SportCountAdd < SportCount) {
                        Map<String, Object> params = new HashMap<>();
                        params.put("accountId", accountId);
                        params.put("deviceCode", deviceCode);
                        params.put("step", allStep);
                        params.put("id", 0);
                        params.put("createTime", WatchUtils.getISO8601Timestamp(new Date()));
                        //testTime  =   2019-06-20 17-00-00
                        //2019-06-20T08:40:41Z
                        Date dateStart = W30BasicUtils.stringToDate(date + " " + stepDate + "-00", "yyyy-MM-dd HH-mm-ss");
                        String iso8601Timestamp = WatchUtils.getISO8601Timestamp(dateStart);
                        params.put("testTime", iso8601Timestamp);
//                        params.put("WALKTIME", "0");
//                        params.put("CARDID", deviceCode);
//                        params.put("STEPS", allStep);
//                        params.put("DISTANCE", 0);
//                        params.put("CALORIE", 0);
//                        params.put("CDATE", date + " " + stepDate + "-00");
                       // String upStepPatch = Base_Url + "walkstep";
                        String upStepPatch = BuildConfig.APIHDURL + "api/v1/walkrecord"; // http://apihd.guiderhealth.com/
                        JSONObject json = new JSONObject(params);
                        List<JSONObject> list = new ArrayList<>();
                        list.add(json);
                        Log.e(TAG, "sport- 上传步数参数:" + list.toString());

                        if (postion == 0) {//这里天数是按照倒序排的，0 = 今天
                            OkHttpTool.getInstance().doRequest(upStepPatch, list.toString(), this, new OkHttpTool.HttpResult() {
                                @Override
                                public void onResult(String result) {
                                    if (!WatchUtils.isEmpty(result)) {
                                        Log.e(TAG, "sport- 最后步数上传返回啊: " + result);
                                        //是否是 最后一天最后第二条
                                        upSport = true;
                                    } else {
                                        MyApp.getInstance().setUploadDateGDNew(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行
                                    }
                                }
                            });
                        } else {
                            OkHttpTool.getInstance().doRequest(upStepPatch, list.toString(), this, new OkHttpTool.HttpResult() {
                                @Override
                                public void onResult(String result) {
                                    Log.e(TAG, "sport- 步数上传返回: " + result);
                                    if (!WatchUtils.isEmpty(result)) {
                                        Log.e(TAG, "sport- 步数上传返回: " + result);
                                    } else {
                                        MyApp.getInstance().setUploadDateGDNew(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行
                                    }
                                }
                            });
                        }
                    } else {
                        if (i == (dataList.size() - 1) && postion == 0) {
                            Log.e(TAG, "sport- 步数数据越界: ");
                            upSport = true;
                        }
                    }

                } else {
                    if (i == (dataList.size() - 1) && postion == 0) {
                        Log.e(TAG, "sport- 步数数据小于等于0啦: ");
                        upSport = true;
                    }
                }
            }
        } else {
            Log.e(TAG, "运动数据异常为空");
            upSport = true;
        }
    }


    /**
     * 上传睡眠数据
     *
     * @param date
     * @param originData
     * @param postion
     */
    private void setUpSleep(String date, String originData, final int postion) {
        SleepData sleepData = new Gson().fromJson(originData, SleepData.class);
        int deepSleepTime = sleepData.getDeepSleepTime();
        int lowSleepTime = sleepData.getLowSleepTime();

        Map<String, Object> params = new HashMap<>();
        int allTimes = deepSleepTime + lowSleepTime;
        String times = remainTimeStr(allTimes);
        params.put("accountId", accountId);
        params.put("deviceCode", deviceCode);
        params.put("id", 0);
        params.put("state", "");
        params.put("value", times);
        params.put("createTime", WatchUtils.getISO8601Timestamp(new Date()));
        //testTime  =   2019-06-20 00-00-00
        //2019-06-20T08:40:41Z
        Date dateStart = W30BasicUtils.stringToDate(date + " 00-00-00", "yyyy-MM-dd HH-mm-ss");
        String iso8601Timestamp = WatchUtils.getISO8601Timestamp(dateStart);
        params.put("testTime", iso8601Timestamp);

        JSONObject json = new JSONObject(params);
        List<JSONObject> list = new ArrayList<>();
        list.add(json);

        String usSleepPath = BuildConfig.APIHDURL + "api/v1/sleepquality"; // http://api.guiderhealth.com/
        OkHttpTool.getInstance().doRequest(usSleepPath, list.toString(), this, new OkHttpTool.HttpResult() {
            @Override
            public void onResult(String result) {
                if (!WatchUtils.isEmpty(result)) {
                    //最后一天最后一条
                    if (postion == 0) {
//                        Log.e(TAG, "sleep- 最后睡眠上传返回" + result);
                        upSleep = true;
                    }

                } else {
                    MyApp.getInstance().setUploadDateGDNew(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行
                }
            }
        });
    }


    /**
     * 上传心率数据
     *
     * @param date
     * @param originData
     * @param postion
     */
    private void setUpHeart(String date, String originData, final boolean postion) {
        final List<HalfHourRateData> dataList = new Gson().fromJson(originData, new TypeToken<List<HalfHourRateData>>() {
        }.getType());
        if (dataList != null && !dataList.isEmpty()) {
            int HeartCount = 0;//总的有效数据长度
            int HeartCountAdd = 0;
            for (int i = 0; i < dataList.size(); i++) {
                if (dataList.get(i).getRateValue() > 0) {
                    HeartCount++;
                }
            }
//            Log.e(TAG, date + "  心率数据总共长度为：" + dataList.size() + "  有效长度为：" + HeartCount);

            for (int i = 0; i < dataList.size(); i++) {
                String heartDates = obtainDate(dataList.get(i).getTime());
                int rateValue = dataList.get(i).getRateValue();

                if (rateValue > 0) {
                    HeartCountAdd++;
                    if (HeartCountAdd < HeartCount) {

                        Map<String, Object> params = new HashMap<>();
                        params.put("accountId", accountId);
                        params.put("deviceCode", deviceCode);
                        params.put("hb", rateValue);
                        params.put("id", 0);
                        params.put("state", "");
                        params.put("createTime", WatchUtils.getISO8601Timestamp(new Date()));
                        //testTime  =   2019-06-20 00-00-00
                        //2019-06-20T08:40:41Z
                        //Log.e(TAG,"---xl- "+heartDates.replace(":", "-"));
                        Date dateStart = W30BasicUtils.stringToDate(heartDates.replace(":", "-"), "yyyy-MM-dd HH-mm-ss");
                        String iso8601Timestamp = WatchUtils.getISO8601Timestamp(dateStart);
                        params.put("testTime", iso8601Timestamp);
                        JSONObject json = new JSONObject(params);
                        List<JSONObject> list = new ArrayList<>();
                        list.add(json);

                        //String path = Base_Url + "heartbeat";
                        String path = BuildConfig.APIHDURL + "api/v1/heartbeat"; // http://apihd.guiderhealth.com/

//                        MyLogUtil.e(TAG, "heart- 心率上传参数" + params.toString());

                        if (postion) {//这里天数是按照倒序排的，0 = 今天
                            OkHttpTool.getInstance().doRequest(path, list.toString(), this, new OkHttpTool.HttpResult() {
                                @Override
                                public void onResult(String result) {
                                    if (!WatchUtils.isEmpty(result)) {
//                                        Log.e(TAG, "最后心率上传返回啊: " + result);
                                        upHeart = true;
                                    } else {
                                        MyApp.getInstance().setUploadDateGDNew(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行
                                    }
                                }
                            });
                        } else {
                            OkHttpTool.getInstance().doRequest(path, list.toString(), this, new OkHttpTool.HttpResult() {
                                @Override
                                public void onResult(String result) {
                                    if (!WatchUtils.isEmpty(result)) {
//                                        Log.e(TAG, "心率上传返回: " + result);
                                    } else {
                                        MyApp.getInstance().setUploadDateGDNew(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行
                                    }
                                }
                            });
                        }

                    } else {
                        if (i == (dataList.size() - 1) && postion) {
                            upHeart = true;
//                            Log.e(TAG, " 心率数据越界了: " + result);
                        }
                    }
                } else {
                    if (i == (dataList.size() - 1) && postion) {
                        upHeart = true;
//                        Log.e(TAG, " 心率异常为小于等于0啦: " + result);
                    }
                }
            }
        }
    }


    /**
     * 上传血压
     *
     * @param date
     * @param originData
     * @param postion
     */
    private void setUpBp(String date, String originData, int postion) {
        final List<HalfHourBpData> dataList = new Gson().fromJson(originData, new TypeToken<List<HalfHourBpData>>() {
        }.getType());
        if (dataList != null && !dataList.isEmpty()) {
            int BpCount = 0;//总的有效数据长度
            int BpCountAdd = 0;
            for (int i = 0; i < dataList.size(); i++) {
                if (dataList.get(i).getLowValue() > 0 && dataList.get(i).getHighValue() > 0) {
                    BpCount++;
                }
            }
//            Log.e(TAG, date + "  血压数据总共长度为：" + dataList.size() + "  有效长度为：" + BpCount);
            for (int i = 0; i < dataList.size(); i++) {
                String bpDates = obtainDate(dataList.get(i).getTime());
                int highValue = dataList.get(i).getHighValue();
                int lowValue = dataList.get(i).getLowValue();
                if (highValue > 0 && lowValue > 0) {
                    BpCountAdd++;
                    if (BpCountAdd < BpCount) {
                        Map<String, Object> params = new HashMap<>();
                        params.put("accountId", accountId);
                        params.put("dbp", lowValue);
                        params.put("sbp", highValue);
                        params.put("state", "");
                        params.put("id", 0);
                        params.put("deviceCode", deviceCode);
                        params.put("createTime", WatchUtils.getISO8601Timestamp(new Date()));
                        //testTime  =   2019-06-20 00-00-00
                        //2019-06-20T08:40:41Z
                        //Log.e(TAG,"---xy- "+bpDates.replace(":", "-"));
                        Date dateStart = W30BasicUtils.stringToDate(bpDates.replace(":", "-"), "yyyy-MM-dd HH-mm-ss");
                        String iso8601Timestamp = WatchUtils.getISO8601Timestamp(dateStart);
                        params.put("testTime", iso8601Timestamp);
                        JSONObject json = new JSONObject(params);
                        List<JSONObject> list = new ArrayList<>();
                        list.add(json);

                        //String path = Base_Url + "bloodPressures ";

                        String path = BuildConfig.APIHDURL + "api/v1/bloodpressure"; // http://apihd.guiderhealth.com/

                        MyLogUtil.e(TAG, "bp- 血压上传参数" + params.toString());

                        if (postion == 0) {//这里天数是按照倒序排的，0 = 今天
                            OkHttpTool.getInstance().doRequest(path, list.toString(), this, new OkHttpTool.HttpResult() {
                                @Override
                                public void onResult(String result) {
                                    if (!WatchUtils.isEmpty(result)) {
                                        upBool = true;
//                                        Log.e(TAG, " 最后血压上传返回啊: " + result);
                                    }
                                }
                            });
                        } else {
                            OkHttpTool.getInstance().doRequest(path, list.toString(), this, new OkHttpTool.HttpResult() {
                                @Override
                                public void onResult(String result) {
                                    if (!WatchUtils.isEmpty(result)) {
//                                        Log.e(TAG, "血压上传返回: " + result);
                                    }
                                }
                            });
                        }
                    } else {
                        if (i == (dataList.size() - 1) && postion == 0) {
                            upBool = true;
//                            Log.e(TAG, "血压异常越界了: " + result);
                        }
                    }
                } else {
                    if (i == (dataList.size() - 1) && postion == 0) {
                        upBool = true;
//                        Log.e(TAG, "血压异常为0啦: " + result);
                    }
                }
            }
        }
    }


//    /**
//     * 上传顺序   步数---睡眠---心率---血压
//     * <p>
//     * 处理请求结果
//     */
//    private void handlerNewResult(int type) {
//        switch (type) {
//            case STATE_SPORT:
//                changeUpload(STATE_SPORT);
//                break;
//            case STATE_SLEEP:
//                changeUpload(STATE_SLEEP);
//                break;
//            case STATE_RATE:
//                changeUpload(STATE_RATE);
//                break;
//            case STATE_BP:
//                changeUpload(STATE_BP);
//                break;
//            //if (upSport && upSleep && upHeart && upBool)
//        }
////        if (resultSuccess(result)) changeUpload(type);
//        if (type != STATE_SPORT
//                && type != STATE_SLEEP
//                && type != STATE_RATE
//                && type != STATE_BP) {
//            MyApp.getInstance().setUploadDateGDNew(false);
//            onCancelled();
//            return;
//        }
//        switch (type) {
//            case STATE_SPORT:
//                getStepUpToServices();
//                break;
//            case STATE_SLEEP:
//                getSleepUpToServices();
//                break;
//            case STATE_RATE:
//                getHeartUpToServices();
//                break;
//            case STATE_BP:
//                getBpUpToServices();
//                break;
//        }
//
//    }

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
//                    Log.e(TAG, "时间对比--A-----" + currentDate + "=====" + dbData.getDate());
                    if (!dbData.getDate().equals(currentDate)) {
                        dbData.setUploadGD(1);// 只要不是当天的数据,都把本地状态设为"已上传"
                        dbData.save();//因为当天数据还有可能不停的更新和上传
                    }

                }
                break;
            case STATE_SLEEP:
                for (int i = 0; i < sleepData.size(); i++) {
                    dbData = sleepData.get(i);
//                    Log.e(TAG, "时间对比--B-----" + currentDate + "=====" + dbData.getDate());
                    if (!dbData.getDate().equals(currentDate)) {
                        dbData.setUploadGD(1);// 只要不是当天的数据,都把本地状态设为"已上传"
                        dbData.save();//因为当天数据还有可能不停的更新和上传
                    }

                }
                break;
            case STATE_RATE:
                for (int i = 0; i < rateData.size(); i++) {
                    dbData = rateData.get(i);
//                    Log.e(TAG, "时间对比--C-----" + currentDate + "=====" + dbData.getDate());
                    if (!dbData.getDate().equals(currentDate)) {
                        dbData.setUploadGD(1);// 只要不是当天的数据,都把本地状态设为"已上传"
                        dbData.save();//因为当天数据还有可能不停的更新和上传
                    }

                }
                break;
            case STATE_BP:
                for (int i = 0; i < bpData.size(); i++) {
                    dbData = bpData.get(i);
//                    Log.e(TAG, "时间对比--D-----" + currentDate + "=====" + dbData.getDate());
                    if (!dbData.getDate().equals(currentDate)) {
                        dbData.setUploadGD(1);// 只要不是当天的数据,都把本地状态设为"已上传"
                        dbData.save();//因为当天数据还有可能不停的更新和上传
                    }
                }
                break;
        }
    }


//    /**
//     * 请求结果是否是成功的
//     *
//     * @param result 请求结果
//     * @return 是否成功
//     */
//    private boolean resultSuccess(String result) {
//        if (WatchUtils.isEmpty(result)) {
//            MyApp.getInstance().setUploadDateGDNew(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行
//            return false;
//        }
//        ResultVo resultVo = null;
//        try {
//            resultVo = new Gson().fromJson(result, ResultVo.class);
//        } catch (JsonSyntaxException e) {
//            e.printStackTrace();
//        }
//        //{"STATUS":"500","CARDID":"E7:A7:0F:11:BE:B5","USERNAME":"使用者","MESSAGE":"Duplicate entry 'E7:A7:0F:11:BE:B5-2019-01-07 00:00:00' for key 'PRIMARY'"}
//        //{"STATUS":"500","CARDID":"D6:64:CB:24:7E:74","USERNAME":"使用者", "MESSAGE":"Duplicate entry 'D6:64:CB:24:7E:74-2018-12-07 03:30:00' for key 'PRIMARY'"},position:1
//        //Log.e(TAG, "=====返回" + (resultVo != null && resultVo.MESSAGE.contains("PRIMARY")));
//        final String RESULT_CODE = "200";// 上传数据成功结果码
//        if (resultVo != null) {
//            if (resultVo.getSTATUS().equals(RESULT_CODE)) {
//                return resultVo.getSTATUS().equals(RESULT_CODE);
//            } else if (resultVo.getMESSAGE().contains("PRIMARY") || resultVo.getSTATUS().equals("500")) {
//                return true;
//            } else {
//                MyApp.getInstance().setUploadDateGDNew(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行
//                return false;
//            }
//        } else {
//            MyApp.getInstance().setUploadDateGDNew(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行
//            return false;
//        }
//    }

    /**
     * 请求结果是否是成功的
     *
     * @param result 请求结果
     * @return 是否成功
     */
    private boolean resultSuccess(String result) {
        if (WatchUtils.isEmpty(result)) {
            MyApp.getInstance().setUploadDateGDNew(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行
            return false;
        }
        ResultVoNew resultVo = null;
        try {
            resultVo = new Gson().fromJson(result, ResultVoNew.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        //{"STATUS":"500","CARDID":"E7:A7:0F:11:BE:B5","USERNAME":"使用者","MESSAGE":"Duplicate entry 'E7:A7:0F:11:BE:B5-2019-01-07 00:00:00' for key 'PRIMARY'"}
        //{"STATUS":"500","CARDID":"D6:64:CB:24:7E:74","USERNAME":"使用者", "MESSAGE":"Duplicate entry 'D6:64:CB:24:7E:74-2018-12-07 03:30:00' for key 'PRIMARY'"},position:1
        //LogTestUtil.e(TAG, "=====返回" + (resultVo != null && resultVo.MESSAGE.contains("PRIMARY")));
        final int RESULT_CODE = 0;// 上传数据成功结果码
        if (resultVo != null) {
            if (resultVo.getCode() == RESULT_CODE) {
                return resultVo.getCode() == RESULT_CODE;
            }
//            else if (resultVo.MESSAGE.contains("PRIMARY") || resultVo.STATUS.equals("500")) {
//                return true;
//            }
            else {
                MyApp.getInstance().setUploadDateGDNew(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行
                return false;
            }
        } else {
            MyApp.getInstance().setUploadDateGDNew(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行
            return false;
        }
        //return resultVo != null && resultVo.STATUS.equals(RESULT_CODE);
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
//        Log.e(TAG,"-------上传结束啦");
    }

    // 方法5：onCancelled()
    // 作用：将异步任务设置为：取消状态
    @Override
    protected void onCancelled() {
        //如果异步任务不为空 并且状态是 运行时  ，就把他取消这个加载任务
        if (getStatus() == Status.RUNNING) {
            cancel(true);
        }
        MyApp.getInstance().setUploadDateGDNew(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行
    }
}
