package com.guider.healthring.b30.service;

import android.content.ContentValues;
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
import com.guider.health.common.utils.StringUtil;
import com.guider.healthring.B18I.b18iutils.B18iUtils;
import com.guider.healthring.Commont;
import com.guider.healthring.MyApp;
import com.guider.healthring.b30.bean.B30HalfHourDB;
import com.guider.healthring.b30.bean.B30HalfHourDao;
import com.guider.healthring.b30.bean.BaseResultVoNew;
import com.guider.healthring.b30.bean.ResultVoNew;
import com.guider.healthring.b31.model.B31HRVBean;
import com.guider.healthring.b31.model.B31Spo2hBean;
import com.guider.healthring.bean.BlueUser;
import com.guider.healthring.bean.TypeUserDatas;
import com.guider.healthring.bean.User;
import com.guider.healthring.bean.WXUserBean;
import com.guider.healthring.siswatch.utils.WatchUtils;
import com.guider.healthring.util.GetJsonDataUtil;
import com.guider.healthring.util.OkHttpTool;
import com.guider.healthring.util.SharedPreferencesUtils;
import com.guider.healthring.w30s.utils.W30BasicUtils;
import com.veepoo.protocol.model.datas.HRVOriginData;
import com.veepoo.protocol.model.datas.HalfHourBpData;
import com.veepoo.protocol.model.datas.HalfHourRateData;
import com.veepoo.protocol.model.datas.HalfHourSportData;
import com.veepoo.protocol.model.datas.SleepData;
import com.veepoo.protocol.model.datas.Spo2hOriginData;
import com.veepoo.protocol.model.datas.SportData;
import com.veepoo.protocol.model.datas.TimeData;
import com.veepoo.protocol.util.HrvScoreUtil;
import com.veepoo.protocol.util.Spo2hOriginUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.LitePal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import static com.veepoo.protocol.model.enums.ESpo2hDataType.TYPE_BREATH;
import static com.veepoo.protocol.model.enums.ESpo2hDataType.TYPE_HEART;
import static com.veepoo.protocol.model.enums.ESpo2hDataType.TYPE_LOWSPO2H;
import static com.veepoo.protocol.model.enums.ESpo2hDataType.TYPE_SLEEP;
import static com.veepoo.protocol.model.enums.ESpo2hDataType.TYPE_SPO2H;


public class UpNewDataToGDServices extends AsyncTask<Void, Void, Void> {
    private final String TAG = "UpNewDataToGDServices";
    private final String Base_Url = BuildConfig.APIURL + "api/v1/";//http://api.guiderhealth.com/


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
     * 上传是否完成的状态: 血压数据
     */
    private final int STATE_HVR = 5;

    /**
     * 上传是否完成的状态: 血压数据
     */
    private final int STATE_SPO2 = 6;


    private String deviceCode;


    private Gson gson = new Gson();

    long accountId = 0;
    //    private String phone, wechartJson;
    private TypeUserDatas typeUserDatas;

    private String userId = (String) SharedPreferencesUtils.readObject(
            MyApp.getContext(), Commont.USER_ID_DATA);

    //血氧的集合
    private List<Spo2hOriginData> spo2hOriginDataList = new ArrayList<>();
    //血氧的map
    List<Map<String, String>> oxyMapList = new ArrayList<>();

    //hrv的集合
    private List<HRVOriginData> hrvOriginDataList = new ArrayList<>();
    //hrv的map
    List<Map<String, Object>> hrvMapList = new ArrayList<>();


    // 方法1：onPreExecute（）
    // 作用：执行 线程任务前的操作
    // 注：根据需求复写
    @Override
    protected void onPreExecute() {
        deviceCode = (String) SharedPreferencesUtils.readObject(MyApp.getInstance(), Commont.BLEMAC);
        Log.e(TAG, "-------onPreExecute--mac=" + deviceCode);
        if (deviceCode == null)
            return;
        String userDetailedData = (String) SharedPreferencesUtils.readObject(MyApp.getContext(),
                "UserDetailedData");
        if (!WatchUtils.isEmpty(userDetailedData)) {
            typeUserDatas = new Gson().fromJson(userDetailedData, TypeUserDatas.class);
            Log.e(TAG, "-------onPreExecute--typeUserDatas=" + typeUserDatas);
        }

//        phone = "" + (String) SharedPreferencesUtils.readObject(MyApp.getContext(), "phoneNumber");
//        wechartJson = "" + (String) SharedPreferencesUtils.readObject(MyApp.getContext(), "wechartJson");
    }

    // 方法2：doInBackground（）
    // 作用：接收输入参数、执行任务中的耗时操作、返回 线程任务执行的结果
    // 注：必须复写，从而自定义线程任务
    @Override
    protected Void doInBackground(Void... voids) {
        Log.e(TAG, "--------doInBackground=" + isCancelled());
        // Task被取消了，马上退出循环
        if (isCancelled()) return null;

        if (Commont.isDebug) Log.e(TAG, "-------上传开始啦");
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
     * 需要上传的HRV数据
     */
    private List<B31HRVBean> hrvList;

    /**
     * 需要上传的血氧数据
     */
    private List<B31Spo2hBean> spo2List;


    /**
     * 查找本地数据所有没上传的数据
     */
    private void findNotUploadData() {

        try {
            B30HalfHourDao instance = B30HalfHourDao.getInstance();
            if (instance != null) {
                //sportData = instance.findNotUploadDataGD(deviceCode, B30HalfHourDao.TYPE_SPORT);

                sportData = instance.findGDTodayData(deviceCode, B30HalfHourDao.TYPE_SPORT);
                //sleepData = instance.findNotUploadDataGD(deviceCode, B30HalfHourDao.TYPE_SLEEP);

                sleepData = instance.findGDThreeDaysData(deviceCode, B30HalfHourDao.TYPE_SLEEP);
                //rateData = instance.findNotUploadDataGD(deviceCode, B30HalfHourDao.TYPE_RATE);

                rateData = instance.findGDThreeDaysData(deviceCode, B30HalfHourDao.TYPE_RATE);

                //bpData = instance.findNotUploadDataGD(deviceCode, B30HalfHourDao.TYPE_BP);

                bpData = instance.findGDThreeDaysData(deviceCode, B30HalfHourDao.TYPE_BP);

                String where = "bleMac = ? and dateStr = ?";
                //HRV
                hrvList = LitePal.where(where, deviceCode, WatchUtils.obtainFormatDate(0))
                        .find(B31HRVBean.class);

                //Log.e(TAG,"-------hrvList.Size="+hrvList.size());

                //血氧
                spo2List = LitePal.where(where, deviceCode, WatchUtils.obtainFormatDate(0))
                        .find(B31Spo2hBean.class);

                if (sportData != null && !sportData.isEmpty())
                    if (Commont.isDebug) Log.e(TAG, "未上传数据条数 运动: " + sportData.size());

                if (sleepData != null && !sleepData.isEmpty())
                    if (Commont.isDebug) Log.e(TAG, "未上传数据条数 睡眠: " + sleepData.size());

                if (rateData != null && !rateData.isEmpty())
                    if (Commont.isDebug) Log.e(TAG, "未上传数据条数 心率: " + rateData.size());

                if (bpData != null && !bpData.isEmpty())
                    if (Commont.isDebug) Log.e(TAG, "未上传数据条数 血压: " + bpData.size());


                if (hrvList != null && !hrvList.isEmpty())
                    if (Commont.isDebug) Log.e(TAG, "未上传数据条数 HRV: " + hrvList.size());


                if (spo2List != null && !spo2List.isEmpty())
                    if (Commont.isDebug) Log.e(TAG, "未上传数据条数 SPO2: " + spo2List.size());

                if ((sportData != null && !sportData.isEmpty())
                        || (sleepData != null && !sleepData.isEmpty())
                        || (rateData != null && !rateData.isEmpty())
                        || (bpData != null && !bpData.isEmpty())

                        || (hrvList != null && !hrvList.isEmpty())
                        || (spo2List != null && !spo2List.isEmpty())) {
                    if (Commont.isDebug)
                        Log.e(TAG,
                                "数据库中存在数据------开始登陆账户-----去上传==" +
                                        "（运动->睡眠->心率->血压->HRV->SPO2）");

                    //登陆到盖得后台

                    long guiderId = (long) SharedPreferencesUtils.getParam(MyApp.getContext(),
                            "accountIdGD", 0L);
                    if (guiderId != 0) {
                        accountId = guiderId;
                        bindDevices(guiderId);
                    } else {
                        loginGdServices();
                    }

                } else {
                    //结束上传
                    onCancelled();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        String typeData = typeUserDatas.getTypeData() + "--str=" + typeUserDatas.getDataJson();
        Log.e(TAG, "----typeData=" + typeData + typeData.equals("LOGION_PHONE"));

        String phoneType = "LOGION_PHONE";


        if (typeUserDatas.getTypeData().equals(phoneType)) {
            String dataJson = typeUserDatas.getDataJson();
            BlueUser blueUser = new Gson().fromJson(dataJson, BlueUser.class);
            String phone = blueUser.getPhone();
            Log.e(TAG, "-----phone=" + phone);
            // params.put("phone", phone);
            if (Commont.isDebug) Log.e(TAG, "游客注册或者登陆参数：" + params.toString());
            String loginUrl = Base_Url + "login/onlyphone?phone=" + phone;
            Log.e(TAG, "-------手机号登录的url=" + loginUrl);
            OkHttpTool.getInstance().doRequest(loginUrl, null, "1", loginHttpResult, false);
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
            Log.e(TAG, "-------dataJson=" + dataJson);
            WXUserBean wxUserBean = new Gson().fromJson(dataJson, WXUserBean.class);
            Log.e(TAG, "------wxUserBean=" + wxUserBean.toString());
            params.put("appId", wxUserBean.getOpenid() + "");
            params.put("headimgurl", wxUserBean.getHeadimgurl() + "");
            params.put("nickname", wxUserBean.getNickname() + "");
            params.put("openid", wxUserBean.getOpenid());
            params.put("sex", (wxUserBean.getSex().equals("M") ? 1 : 0) + "");
            params.put("unionid", wxUserBean.getUnionid() + "");
            if (Commont.isDebug) Log.e(TAG, "222游客注册或者登陆参数：" + params.toString());
            JSONObject json = new JSONObject(params);

            if (Commont.isDebug) Log.e(TAG, "====  json  " + json.toString());
            OkHttpTool.getInstance().doRequest(Base_Url + "/login/wachat",
                    json.toString(), this, loginHttpResult);
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
                if (Commont.isDebug) Log.e(TAG, "游客注册或者登陆上传返回" + result);

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

                            SharedPreferencesUtils.setParam(MyApp.getInstance(), "accountIdGD", (long) accountId);
                            String token = dataBean.getToken();
                            SharedPreferencesUtils.setParam(MyApp.getInstance(),
                                    "tokenGD", token);
                            if (Commont.isDebug) Log.e(TAG, "游客注册或者登陆成功：开始上传步数");
                            MyApp.isLogin = true;
                            SharedPreferencesUtils.setParam(MyApp.getInstance(), "UpGdServices", B18iUtils.getSystemDataStart());

                            //并发一起上传
                            //findUnUpdataToservices();


                            //登陆成功之后----同步用户信息
                            //sycyToServices_GD(accountId);
                            bindDevices(accountId);

                        } else {
                            MyApp.getInstance().setUploadDateGDNew(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行

                            if (Commont.isDebug) Log.e(TAG, "游客注册或者登陆失败---上传返回" + result);
                            Intent intent = new Intent("com.example.bozhilun.android.b30.service.UploadServiceGD");
                            MyApp.getInstance().sendBroadcast(intent);
                            onCancelled();
                        }


                    } else {
                        MyApp.getInstance().setUploadDateGDNew(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行

                        if (Commont.isDebug) Log.e(TAG, "游客注册或者登陆失败---上传返回" + result);
                        Intent intent = new Intent("com.example.bozhilun.android.b30.service.UploadServiceGD");
                        MyApp.getInstance().sendBroadcast(intent);
                        onCancelled();
                    }
                } else {
                    MyApp.getInstance().setUploadDateGDNew(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行

                    if (Commont.isDebug) Log.e(TAG, "游客注册或者登陆失败---上传返回" + result);
                    Intent intent = new Intent("com.example.bozhilun.android.b30.service.UploadServiceGD");
                    MyApp.getInstance().sendBroadcast(intent);
                    onCancelled();
                }

            } else {
                MyApp.getInstance().setUploadDateGDNew(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行

                if (Commont.isDebug) Log.e(TAG, "游客注册或者登陆失败---上传返回" + result);
                Intent intent = new Intent("com.example.bozhilun.android.b30.service.UploadServiceGD");
                MyApp.getInstance().sendBroadcast(intent);
                onCancelled();
            }

        }
    };


    /**
     * 同步用户信息
     *
     * @param accountId
     */
    void sycyToServices_GD(final long accountId) {
        if (typeUserDatas == null) {
            String userDetailedData = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), "UserDetailedData");
            if (!WatchUtils.isEmpty(userDetailedData)) {
                typeUserDatas = new Gson().fromJson(userDetailedData, TypeUserDatas.class);
            }
        }
        if (typeUserDatas == null) return;
        String typeData = typeUserDatas.getTypeData();
        Map<String, Object> params = new HashMap<>();
        if (typeData.equals("LOGION_PHONE")) {
            /**
             * {
             *   "birthday": "1995-06-15",
             *   "image": "http:\/\/47.90.83.197\/image\/2018\/11\/24\/1543019974622.jpg",
             *   "nickName": "hello RaceFitPro",
             *   "sex": "M",
             *   "weight": "60 kg",
             *   "equipment": "B30",
             *   "userId": "0d56716e5629475882d4f4bfc7c51420",
             *   "mac": "E7:A7:0F:11:BE:B5",
             *   "phone": "14791685830",
             *   "height": "170 cm"
             * }
             */
            String dataJson = typeUserDatas.getDataJson();
            User blueUser = new Gson().fromJson(dataJson, User.class);
            String birthday = blueUser.getBirthday();
            String image = blueUser.getImage();
            String nickName = blueUser.getNickName() == null ? blueUser.getNickname() : blueUser.getNickName();
            String sex = blueUser.getSex();
            String weight = blueUser.getWeight();
            String phone = blueUser.getPhone();
            String height = blueUser.getHeight();
            if (Commont.isDebug)
                Log.d(TAG, "=======WX= " + accountId + " == " + blueUser.toString());
            params.put("accountId", accountId);
            params.put("addr", "");
//            params.put("birthday", "1988-12-02T00:00:00Z");//生日
            params.put("birthday", WatchUtils.isEmpty(birthday) ? "" : (birthday + "T00:00:00Z"));//生日
            params.put("cardId", "");//身份证号
            if ("M".equals(sex)) {
                params.put("gender", "MAN");//性别
            } else if ("F".equals(sex)) {
                params.put("gender", "WOMAN");//性别
            }
            params.put("headUrl", image + "");//头像链接

            if (height.trim().contains("cm")) {
                params.put("height", Integer.valueOf(height.trim().replace(" ", "").substring(0, height.trim().replace(" ", "").length() - 2)));//体重
            } else {
                params.put("height", Integer.valueOf(height.trim().replace(" ", "")));//身高
            }
            params.put("name", WatchUtils.isEmpty(nickName) ? phone + "" : nickName + "");//姓名
            params.put("phone", phone + "");//电话号码
            params.put("userState", "ACTIVE");//默认
            if (weight.trim().contains("kg")) {
                params.put("weight", Integer.valueOf(weight.trim().replace(" ", "").substring(0, weight.trim().replace(" ", "").length() - 2)));//体重
            } else {
                params.put("weight", Integer.valueOf(weight.trim().replace(" ", "")));//体重
            }
        } else {
            /**
             * openid : onxAK59awxJ17IjZnaYslUiOshEE
             * nickname : 丶
             * sex : 1
             * language : zh_CN
             * city : Shenzhen
             * province : Guangdong
             * country : CN
             * headimgurl : http://thirdwx.qlogo.cn/mmopen/vi_32/qndNbICrNwia9HQHMq8Bu4CAJ6KCfum9RQe408vq76KibSYiaicibbQXOuhlJzibEL8PrX1E3l6iaQH4UMjllrM6icVhIQ/132
             * privilege : []
             * unionid : oaVtW5_Yp-o9NPhbmlqFfUn-5He0
             */
            String dataJson = typeUserDatas.getDataJson();
            WXUserBean wxUserBean = new Gson().fromJson(dataJson, WXUserBean.class);
            String sex = wxUserBean.getSex();
            String headimgurl = wxUserBean.getHeadimgurl();
            String nickname = wxUserBean.getNickname();

            if (Commont.isDebug)
                Log.d(TAG, "=======WX= " + accountId + " == " + wxUserBean.toString());

            params.put("accountId", accountId);
            params.put("addr", "");
            params.put("birthday", "");//生日
            //params.put("birthday", birthday + "T00:00:00Z");//生日
            params.put("cardId", "");//身份证号
            if (sex.equals("M") || sex.equals("1")) {
                params.put("gender", "MAN");//性别
            } else {
                params.put("gender", "WOMAN");//性别
            }
            params.put("headUrl", headimgurl + "");//头像链接
            params.put("height", 0);//身高

            params.put("name", nickname + "");//姓名
            params.put("phone", "");//电话号码
            params.put("userState", "ACTIVE");//默认
            params.put("weight", 0);//体重
        }
        if (params.isEmpty()) return;
        JSONObject json = new JSONObject(params);
        String upStepPatch = Base_Url + "usersimpleinfo";
        OkHttpTool.getInstance().doPut(upStepPatch, json.toString(), this, new OkHttpTool.HttpResult() {
            @Override
            public void onResult(String result) {
                if (!WatchUtils.isEmpty(result)) {
                    BaseResultVoNew baseResultVoNew = new Gson().fromJson(result, BaseResultVoNew.class);
                    if (baseResultVoNew.getCode() == 0) {
                        if (Commont.isDebug) Log.d(TAG, "=======同步用户信息= " + result);
                        bindDevices(accountId);
                    }
                }
            }
        });

    }


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
        String upStepPatch = Base_Url + "user/" + accountId +
                "/deviceandcompany/bind?deviceCode=" + deviceCode;
        //{accountId}/deviceandcompany/bind?deviceCode=
        OkHttpTool.getInstance().doRequest(upStepPatch, null, this, result -> {
            try {
                if (!WatchUtils.isEmpty(result)) {
                    Log.e(TAG, "------bindDevices--=" + result);
                    if (result.contains("Unable"))
                        return;
                    BaseResultVoNew baseResultVoNew = new Gson().fromJson(result,
                            BaseResultVoNew.class);
                    if (Commont.isDebug) Log.d(TAG, "=======绑定该设备A= " + result);
                    if (baseResultVoNew.getCode() == 0 ||
                            baseResultVoNew.getMsg().equals("您已经绑定该设备")) {
                        if (Commont.isDebug) Log.d(TAG, "=======绑定该设备= " + result);
                        //并发一起上传
                        findUnUpdataToservices();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }, false);
    }


    private boolean upSport = false;
    private boolean upSleep = false;
    private boolean upHeart = false;
    private boolean upBool = false;

    private boolean upHrv = false;
    private boolean upSpo2 = false;

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 0x01:
                    if (upSport && upSleep && upHeart && upBool
                            && upHrv && upSpo2) {

                        changeUpload(STATE_SPORT);
                        changeUpload(STATE_SLEEP);
                        changeUpload(STATE_RATE);
                        changeUpload(STATE_BP);
                        changeUpload(STATE_HVR);
                        changeUpload(STATE_SPO2);

                        handler.sendEmptyMessageDelayed(0x02, 1000);
                    } else {
                        handler.sendEmptyMessageDelayed(0x01, 5 * 1000);
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
        /**
         * 如果 accountId==0 那么就是没登陆成功
         */
        if (accountId == 0) {
            //loginGdServices();
            return;
        }
        uploadGDSleepData();    //睡眠
        //getHeartUpToServices();
        uploadGDHeartData();    //心率
        //getBpUpToServices();
        uploadGDBloodData();    //血压
        getHrvToServices();
        getSpo2ToServices();
        uploadRingData();
    }


    //上传血压数据
    private void uploadGDBloodData() {
        if (bpData == null || bpData.isEmpty())
            return;
        Log.e(TAG, "------上传的血压天数=" + bpData.size() + "天");
        List<Map<String, Object>> bloodListMap = new ArrayList<>();
        for (B30HalfHourDB bloodDb : bpData) {
            if (bloodDb.getDate().equals(WatchUtils.getCurrentDate()) || bloodDb.getUpload() != 1) {
                String bloodStr = bloodDb.getOriginData();
                List<HalfHourBpData> hourBpDataList = gson.fromJson(bloodStr, new TypeToken<List<HalfHourBpData>>() {
                }.getType());
                for (HalfHourBpData bpDB : hourBpDataList) {
                    TimeData timeData = bpDB.getTime();
                    Date dateStart = W30BasicUtils.stringToDate(timeData.getDateAndClockForSleepSecond(), "yyyy-MM-dd HH:mm:ss");
                    Map<String, Object> bpMap = new HashMap<>();
                    bpMap.put("accountId", accountId);
                    bpMap.put("sbp", bpDB.getHighValue());
                    bpMap.put("dbp", bpDB.getLowValue());//
                    bpMap.put("testTime", WatchUtils.getISO8601Timestamp(dateStart));
                    bpMap.put("deviceCode", deviceCode);
                    bloodListMap.add(bpMap);
                }
            }
        }

        if (bloodListMap.isEmpty())
            return;
        String bloodParams = gson.toJson(bloodListMap);
        //Log.e(TAG,"--------上传血压的参数="+bloodParams);

//        new GetJsonDataUtil().writeTxtToFile("血压参数:"+WatchUtils.getCurrentDate1()+bloodParams,
//                Environment.getExternalStorageDirectory()+"/DCIM/","heartBloodParams.json");


        String bloodUrl = Commont.BLOOD_URL;
        OkHttpTool.getInstance().doRequest(bloodUrl, bloodParams, "", new OkHttpTool.HttpResult() {
            @Override
            public void onResult(String result) {
                Log.e(TAG, "------血压上传返回=" + result);
                if (WatchUtils.isEmpty(result))
                    return;
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (!jsonObject.has("code"))
                        return;
                    if (jsonObject.getInt("code") == 0) {
                        updateBloodStatus(bpData);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    //修改血压
    private void updateBloodStatus(List<B30HalfHourDB> bpDatas) {
        for (int i = 0; i < bpDatas.size(); i++) {
            String dateStr = bpDatas.get(i).getDate();
            if (!dateStr.equals(WatchUtils.getCurrentDate())) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("upload", 1);
                int bloodCode = LitePal.updateAll(B30HalfHourDB.class, contentValues, "address = ? and type = ? and date = ?",
                        deviceCode, B30HalfHourDao.TYPE_BP, dateStr);
                Log.e(TAG, "-----血压修改=" + bloodCode);
            }
        }
    }


    /**
     * [
     * {"accountId":accountId,
     * "hb":80,
     * "testTime":"2019-09-19:00:00"
     * },
     * <p>
     * {
     * "accountId":accountId,
     * "hb":80,
     * "testTime":"2019-09-19:00:30"
     * }，
     * {
     * "accountId":accountId,
     * "hb":80,
     * "testTime":"2019-09-18 00:00"
     * <p>
     * },
     * {
     * "accountId":accountId,
     * "hb":80,
     * "testTime":"2019-09-18 00:30"
     * }
     * ]
     */

    //上传心率
    private void uploadGDHeartData() {
        if (rateData == null || rateData.isEmpty())
            return;
        Log.e(TAG, "------心率的天数=" + rateData.size() + "天");
        List<Map<String, Object>> heartListMap = new ArrayList<>();
        for (B30HalfHourDB heartdb : rateData) {
            if (heartdb.getDate().equals(WatchUtils.getCurrentDate()) || heartdb.getUpload() != 1) {

                String heartStr = heartdb.getOriginData();
                //Log.e(TAG,"-----心率="+heartdb.toString());

                List<HalfHourRateData> hourRateDataList = gson.fromJson(heartStr,
                        new TypeToken<List<HalfHourRateData>>() {
                }.getType());
                for (HalfHourRateData hb : hourRateDataList) {
                    Map<String, Object> heartMap = new HashMap<>();
                    TimeData timeData = hb.getTime();
                    Date dateStart = W30BasicUtils.stringToDate(timeData.getDateAndClockForSleepSecond(), "yyyy-MM-dd HH:mm:ss");
                    heartMap.put("accountId", accountId);
                    heartMap.put("hb", hb.getRateValue());
                    heartMap.put("testTime", WatchUtils.getISO8601Timestamp(dateStart));
                    heartMap.put("deviceCode", deviceCode);
                    heartListMap.add(heartMap);
                }

            }

        }

        if (heartListMap.isEmpty())
            return;
        String heartParams = gson.toJson(heartListMap);
        // Log.e(TAG,"-------心率参数="+heartParams);

//        new GetJsonDataUtil().writeTxtToFile("心率参数:"+WatchUtils.getCurrentDate1()+heartParams,
//                Environment.getExternalStorageDirectory()+"/DCIM/","heartBloodParams.json");
        String heartUrl = Commont.HEART_URL;

        OkHttpTool.getInstance().doRequest(heartUrl, heartParams, "", result -> {
            Log.e(TAG, "--------心率上传返回=" + result);
//                new GetJsonDataUtil().writeTxtToFile("心率上传返回:"+WatchUtils.getCurrentDate1()+result,
//                        Environment.getExternalStorageDirectory()+"/DCIM/","heartBloodParams.json");
            if (WatchUtils.isEmpty(result))
                return;
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (!jsonObject.has("code"))
                    return;
                if (jsonObject.getInt("code") == 0) {
                    updateHeartStatus(rateData);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    //修改去除当天的血压标识，已上传
    private void updateHeartStatus(List<B30HalfHourDB> rateDatas) {
        for (int i = 0; i < rateDatas.size(); i++) {
            String dateStr = rateDatas.get(i).getDate();
            if (!dateStr.equals(WatchUtils.getCurrentDate())) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("upload", 1);
                int heartCode = LitePal.updateAll(B30HalfHourDB.class, contentValues, "address = ? and type = ? and date = ?",
                        deviceCode, B30HalfHourDao.TYPE_RATE, dateStr);
                Log.e(TAG, "-----心率修改=" + heartCode);
            }
        }

    }


    //上传睡眠
    private void uploadGDSleepData() {
        if (sleepData == null || sleepData.isEmpty())
            return;
        Log.e(TAG, "------上传睡眠的天数=" + sleepData.size() + "天");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        try {
            List<Map<String, Object>> sleepListMap = new ArrayList<>();
            for (B30HalfHourDB sleepDb : sleepData) {
                if (sleepDb.getDate().equals(WatchUtils.getCurrentDate()) || sleepDb.getUpload() != 1) {
                    SleepData sleepDataStr = gson.fromJson(sleepDb.getOriginData(), SleepData.class);
                    Map<String, Object> sleepMap = new HashMap<>();
                    sleepMap.put("accountId", accountId);
                    Date wakeDate = sdf.parse(sleepDataStr.getSleepUp().getDateAndClockForSleepSecond());
                    sleepMap.put("testTime", WatchUtils.getISO8601Timestamp(new Date(wakeDate.getTime())));
                    sleepMap.put("sleepTime", sleepDataStr.getAllSleepTime());
                    sleepMap.put("wakeUpNum", sleepDataStr.getWakeCount());
                    Date dateStr = sdf.parse(sleepDataStr.getSleepDown().getDateAndClockForSleepSecond());
                    sleepMap.put("goSleepTime", WatchUtils.getISO8601Timestamp(new Date(dateStr.getTime())));

                    sleepMap.put("wakeUpTime", WatchUtils.getISO8601Timestamp(new Date(wakeDate.getTime())));
                    sleepMap.put("deepSleepTime", sleepDataStr.getDeepSleepTime());
                    sleepMap.put("lowSleepTime", sleepDataStr.getLowSleepTime());
                    sleepMap.put("state", String.valueOf(sleepDataStr.getSleepQulity()));
                    sleepMap.put("deviceCode", deviceCode);
                    sleepListMap.add(sleepMap);
                }

            }
            String sleepUrl = Commont.SLEEP_QUALITY_URL;
            if (sleepListMap.isEmpty())
                return;
            //Log.e(TAG,"--------睡眠参数="+gson.toJson(sleepListMap));
            OkHttpTool.getInstance().doRequest(sleepUrl, gson.toJson(sleepListMap), "",
                    result -> {
                        Log.e(TAG, "--------睡眠上传=" + result);
                        if (WatchUtils.isEmpty(result))
                            return;
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            if (!jsonObject.has("code"))
                                return;
                            if (jsonObject.getInt("code") == 0) {
                                updateSleepStatus(sleepData);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //修改睡眠
    private void updateSleepStatus(List<B30HalfHourDB> sleepDatas) {
        for (int i = 0; i < sleepDatas.size(); i++) {
            String dateStr = sleepDatas.get(i).getDate();
            if (!dateStr.equals(WatchUtils.getCurrentDate())) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("upload", 1);
                int sleepCode = LitePal.updateAll(B30HalfHourDB.class, contentValues,
                        "address = ? and type = ? and date = ?",
                        deviceCode, B30HalfHourDao.TYPE_SLEEP, dateStr);
                Log.e(TAG, "-----睡眠修改=" + sleepCode);
            }
        }


    }


    //统一上传接口
    private void uploadRingData() {
        if (hrvOriginDataList.isEmpty() && hrvMapList.isEmpty())
            return;
        //总的结果
        Map<String, Object> allResultMap = new HashMap<>();
        allResultMap.put("accountId", accountId);
        allResultMap.put("deviceCode", deviceCode);
        allResultMap.put("testTime", WatchUtils.getCurrentDate());
        //运动
        Map<String, Object> sportDataMap = new HashMap<>();

        List<B30HalfHourDB> sportGDList = B30HalfHourDao.getInstance()
                .findGDTodayData(deviceCode, B30HalfHourDao.TYPE_SPORT);
        if (sportGDList == null || sportGDList.isEmpty()) return;
        String orginStr = sportGDList.get(sportGDList.size() - 1).getOriginData();

        // Log.e(TAG,"--------运动="+orginStr);
        List<HalfHourSportData> halfHourSportData = new Gson().fromJson(orginStr,
                new TypeToken<List<HalfHourSportData>>() {
                }.getType());

        List<Map<String, Object>> sportList = new ArrayList<>();
        for (HalfHourSportData hSport : halfHourSportData) {
            if (hSport.getDate().equals(WatchUtils.getCurrentDate())) {
                Map<String, Object> sMap = new HashMap<>();

                TimeData timeData = hSport.getTime();

                String spo2Dates = timeData.getDateAndClockForSleepSecond();

                Date dateStart = W30BasicUtils.stringToDate(
                        spo2Dates.replace(":", "-"),
                        "yyyy-MM-dd HH-mm-ss");
                String iso8601Timestamp = WatchUtils.getISO8601Timestamp(dateStart);

                sMap.put("stepValue", hSport.getStepValue());
                sMap.put("disValue", hSport.getDisValue());
                sMap.put("calValue", hSport.getCalValue());
                sMap.put("testTime", iso8601Timestamp);
                sportList.add(sMap);
            }


        }

        sportDataMap.put("data", sportList);
        String step_detail = B30HalfHourDao.getInstance().findOriginData(deviceCode,
                WatchUtils.getCurrentDate(), B30HalfHourDao.TYPE_STEP_DETAIL);
        if (WatchUtils.isEmpty(step_detail)) {
            sportDataMap.put("totalStep", 0);
            sportDataMap.put("totalDis", 0.0);
            sportDataMap.put("totalCal", 0.0);
        } else {
            SportData sportData = gson.fromJson(step_detail, SportData.class);
            sportDataMap.put("totalStep", sportData.getStep());
            sportDataMap.put("totalDis", sportData.getDis());
            sportDataMap.put("totalCal", sportData.getKcal());

        }


        allResultMap.put("sport", sportDataMap);


        //Log.e(TAG,"---------allMap="+allResultMap.get("sport").toString());

        //hrv
        if (!hrvOriginDataList.isEmpty() && !hrvMapList.isEmpty()) {
            //心脏健康指数
            HrvScoreUtil hrvScoreUtil = new HrvScoreUtil();
            int heartSocre = hrvScoreUtil.getSocre(hrvOriginDataList);

            Map<String, Object> hrvTempMap = new HashMap<>();
            hrvTempMap.put("data", hrvMapList);

            hrvTempMap.put("hrvIndex", heartSocre);

            allResultMap.put("hrv", hrvTempMap);
        } else {
            allResultMap.put("hrv", "");
        }

        //spo2
        if (!spo2hOriginDataList.isEmpty() && !oxyMapList.isEmpty()) {
            try {
                Map<String, Object> spo2TempMap = new HashMap<>();
                Spo2hOriginUtil spo2hOriginUtil = new Spo2hOriginUtil(spo2hOriginDataList);
                //获取低氧数据[最大，最小，平均]       *参考：取低氧最大值，大于20，则显示偏高，其他显示正常
                int[] onedayDataArrLowSpo2h = spo2hOriginUtil.getOnedayDataArr(TYPE_LOWSPO2H);
                //获取呼吸率数据[最大，最小，平均]     *参考：取呼吸率最大值，低于18，则显示偏低，其他显示正常
                int[] onedayDataArrLowBreath = spo2hOriginUtil.getOnedayDataArr(TYPE_BREATH);
                //获取睡眠数据[最大，最小，平均]       *参考：取睡眠活动荷最大值，大于70，则显示偏高，其他显示正常
                int[] onedayDataArrSleep = spo2hOriginUtil.getOnedayDataArr(TYPE_SLEEP);
                //获取心脏负荷数据[最大，最小，平均]   *参考：取心脏负荷最大值，大于40，则显示偏高，其他显示正常
                int[] onedayDataArrHeart = spo2hOriginUtil.getOnedayDataArr(TYPE_HEART);
                //获取血氧数据[最大，最小，平均]       *参考：取血氧最小值，低于95，则显示偏低，其他显示正常
                int[] onedayDataArrSpo2h = spo2hOriginUtil.getOnedayDataArr(TYPE_SPO2H);


                //呼吸暂停
                List<Map<String, Float>> rateStopList = spo2hOriginUtil.getApneaList();
                DecimalFormat decimalFormat = new DecimalFormat("#");    //保留两位小数


                for (Map<String, Float> stopMap : rateStopList) {
                    Log.e(TAG, "------stopMap=" + stopMap.toString());
                }

                List<Map<String, Object>> rateMapList = new ArrayList<>();

                for (Map<String, Float> mp : rateStopList) {
                    float timeStr = mp.get("time");
                    float valueStr = mp.get("value");
                    Log.e(TAG, "-=----timeStr=" + timeStr + "--=" + valueStr + "--=" + (timeStr != 0));
                    if (valueStr != 0) {
                        int timeInt = (int) timeStr;

                        //小时
                        int hour = (int) Math.floor(timeInt / 60);
                        //分钟
                        int mine = timeInt % 60;

                        Map<String, Object> timeMap = new HashMap<>();
                        timeMap.put("time", "0" + hour + ":" + (mine == 0 ? "0" + mine : mine));
                        timeMap.put("value", valueStr + "");
                        Log.e(TAG, "-----timeMap=" + timeMap.toString());
                        rateMapList.add(timeMap);
                    }
                }


                int osahs = spo2hOriginUtil.getIsHypoxia();
                Log.e(TAG, "---osahs=" + osahs);
                spo2TempMap.put("OSAHS", osahs);
                spo2TempMap.put("onedayDataArrLowSpo2h", onedayDataArrLowSpo2h);
                spo2TempMap.put("onedayDataArrLowBreath", onedayDataArrLowBreath);
                spo2TempMap.put("onedayDataArrSleep", onedayDataArrSleep);
                spo2TempMap.put("onedayDataArrHeart", onedayDataArrHeart);
                spo2TempMap.put("onedayDataArrSpo2h", onedayDataArrSpo2h);
                spo2TempMap.put("tmpLt", rateMapList);
                spo2TempMap.put("data", oxyMapList);//oxyMapList

                allResultMap.put("spo2", spo2TempMap);
            } catch (Exception e) {
                e.printStackTrace();
                if (!StringUtil.isEmpty(e.getMessage()))
                    Log.e(TAG, "查询报错----" + e.getMessage());
            }
        } else {
            allResultMap.put("spo2", "");
        }
        String allParmas = gson.toJson(allResultMap);

        String filePath = Environment.getExternalStorageDirectory() + "/DCIM/";
        new GetJsonDataUtil().writeTxtToFile(
                WatchUtils.getCurrentDate1() + allParmas, filePath, "ringData.json");


        //String allUrl = "http://apihd.guiderhealth.com/api/v1/ringdata";
        String allUrl = Commont.GAI_DE_BASE_URL + "ringdata";
        OkHttpTool.getInstance().doRequest(allUrl, allParmas, "", new OkHttpTool.HttpResult() {
            @Override
            public void onResult(String result) {
                Log.e(TAG, "--------所有上传返回=" + result);
            }
        });


    }


    /**
     * 读取步数 上传步数到后台
     */
    private void getStepUpToServices() {

        if (sportData != null && !sportData.isEmpty()) {
            if (Commont.isDebug) Log.e(TAG, "一共有 " + sportData.size() + " 天运动数据");
            for (int i = (sportData.size() - 1); i >= 0; i--) {
                if (Commont.isDebug)
                    Log.e(TAG, "第 " + i + " 天前的运动数据   日期是：" + sportData.get(i).getDate());
                String date = sportData.get(i).getDate();
                String originData = sportData.get(i).getOriginData();
                if (!WatchUtils.isEmpty(originData) && !WatchUtils.isEmpty(date)) {
                    //为倒序，i == 0  最后一个为今天，今天的数据要做处理
                    setUpSport(date, originData, i);
                } else {
                    if (i == 0) {
                        if (Commont.isDebug) Log.e(TAG, "运动数据异常");
                        upSport = true;
                    }
                }
            }
        } else {
            if (Commont.isDebug) Log.e(TAG, "没有运动数据");
            upSport = true;
        }
    }


    /**
     * 读取睡眠 --- 上传到盖得后台
     */
    private void getSleepUpToServices() {
        if (sleepData != null && !sleepData.isEmpty()) {
            if (Commont.isDebug) Log.e(TAG, "一共有 " + sleepData.size() + " 天睡眠数据");
            for (int i = (sleepData.size() - 1); i >= 0; i--) {
                String date = sleepData.get(i).getDate();
                String originData = sleepData.get(i).getOriginData();
                if (Commont.isDebug)
                    Log.e(TAG, "第 " + i + " 天前的睡眠数据   日期是：" + sleepData.get(i).getDate());
                if (!WatchUtils.isEmpty(originData) && !WatchUtils.isEmpty(date)) {
                    //为倒序，i == 0  最后一个为今天，今天的数据要做处理
                    setUpSleep(date, originData, i);
                } else {
                    if (i == 0) {
                        if (Commont.isDebug) Log.e(TAG, "睡眠数据异常");
                    }
                }
            }
        } else {
            if (Commont.isDebug) Log.e(TAG, "没有睡眠数据");
        }

//        if (sleepData != null && !sleepData.isEmpty()) {
//             if (Commont.isDebug)Log.e(TAG, "一共有 " + sleepData.size() + " 天睡眠数据");
//            for (int i = (sleepData.size() - 1); i >= 0; i--) {
//                String date = sleepData.get(i).getDate();
//                String originData = sleepData.get(i).getOriginData();
//                 if (Commont.isDebug)Log.e(TAG, "第 " + i + " 天前的睡眠数据   日期是：" + sleepData.get(i).getDate());
//                if (!WatchUtils.isEmpty(originData) && !WatchUtils.isEmpty(date)) {
//                    //为倒序，i == 0  最后一个为今天，今天的数据要做处理
//                    setUpSleep(date, originData, i);
//                } else {
//                    if (i == 0) {
//                         if (Commont.isDebug)Log.e(TAG, "睡眠数据异常");
//                    }
//                }
//            }
//        } else {
//             if (Commont.isDebug)Log.e(TAG, "没有睡眠数据");
//        }
    }


    /**
     * 读取心率
     */
    private void getHeartUpToServices() {

        if (rateData != null && !rateData.isEmpty()) {
            if (Commont.isDebug) Log.e(TAG, "一共有 " + rateData.size() + " 天心率数据");
            for (int i = (rateData.size() - 1); i >= 0; i--) {
                String date = rateData.get(i).getDate();
                String originData = rateData.get(i).getOriginData();
                if (Commont.isDebug)
                    Log.e(TAG, "第 " + i + " 天前的心率数据   日期是：" + rateData.get(i).getDate());
                if (!WatchUtils.isEmpty(originData) && !WatchUtils.isEmpty(date)) {
                    //为倒序，i == 0  最后一个为今天，今天的数据要做处理
                    if (i == 0) setUpHeart(date, originData, true);
                    else setUpHeart(date, originData, false);
                } else {
                    if (i == 0) {
                        upHeart = true;
                        if (Commont.isDebug) Log.e(TAG, "心率数据异常");
                    }
                }
            }
        } else {
            upHeart = true;
            if (Commont.isDebug) Log.e(TAG, "没有心率数据");
        }
    }


    /**
     * 读取血压
     */
    private void getBpUpToServices() {

        if (bpData != null && !bpData.isEmpty()) {
            if (Commont.isDebug) Log.e(TAG, "一共有 " + bpData.size() + " 天血压数据");
            for (int i = (bpData.size() - 1); i >= 0; i--) {
                String date = bpData.get(i).getDate();
                String originData = bpData.get(i).getOriginData();
                if (Commont.isDebug)
                    Log.e(TAG, "第 " + i + " 天前的血压数据   日期是：" + bpData.get(i).getDate());
                if (!WatchUtils.isEmpty(originData) && !WatchUtils.isEmpty(date)) {
                    //为倒序，i == 0  最后一个为今天，今天的数据要做处理
                    setUpBp(date, originData, i);
                } else {
                    if (i == 0) {
                        upBool = true;
                        if (Commont.isDebug) Log.e(TAG, "血压数据异常");
                    }
                }
            }
        } else {
            if (Commont.isDebug) Log.e(TAG, "血压没有数据1");
            upBool = true;
        }
    }


    /**
     * 读取HRV
     */
    private void getHrvToServices() {
        hrvOriginDataList.clear();
        hrvMapList.clear();
        if (hrvList == null || hrvList.isEmpty())
            return;
        Log.e(TAG, "----------hrv大小=" + hrvList.size());
        Log.e(TAG, "----------hrv第一个=" + hrvList.get(0).toString());
        Log.e(TAG, "----------hrv最后一个=" + hrvList.get(hrvList.size() - 1).toString());
        List<Map<String, Object>> list = new ArrayList<>();
        for (B31HRVBean b31HRVBean : hrvList) {
            String hrvDataStr = b31HRVBean.getHrvDataStr();
            HRVOriginData hrvOriginData = new Gson().fromJson(hrvDataStr, HRVOriginData.class);
            if (hrvOriginData.getDate().equals(WatchUtils.getCurrentDate())) {
                hrvOriginDataList.add(hrvOriginData);
            }

            TimeData timeData = hrvOriginData.getmTime();
            String hrvDates = timeData.getDateAndClockForSleepSecond();//obtainDate(timeData);

            Date dateStart = W30BasicUtils.stringToDate(hrvDates.replace(
                    ":", "-"), "yyyy-MM-dd HH-mm-ss");
            String iso8601Timestamp = WatchUtils.getISO8601Timestamp(dateStart);
            // Log.e(TAG,"-------testTime="+iso8601Timestamp);

            Map<String, Object> hrMap = new HashMap<>();
            hrMap.put("hrvValue", hrvOriginData.getHrvValue());
            hrMap.put("testTime", iso8601Timestamp);
            hrvMapList.add(hrMap);

            //Log.e(TAG,"---b31Hrv="+b31HRVBean.toString());
            if (!b31HRVBean.isUpdata()) {
                int hrvValue = hrvOriginData.getHrvValue();
                String rate = hrvOriginData.getRate();
                if (WatchUtils.isEmpty(rate)) return;
                int[] hrvDatas = null;
                if (rate.length() - 2 != 0) {
                    hrvDatas = new int[rate.length() - 2];
                    for (int i = 1; i < rate.length() - 1; i++) {
                        char ch = rate.charAt(i);
                        hrvDatas[i - 1] = ch;
                    }
                }

                Map<String, Object> params = new HashMap<>();
                params.put("accountId", accountId);
                params.put("deviceCode", deviceCode);
                params.put("hrvData", hrvDatas);
                params.put("hrvIndex", hrvValue);
                params.put("state", "");
                params.put("createTime", "");
                //testTime  =   2019-06-20 17-00-00
                //2019-06-20T08:40:41Z

                params.put("testTime", iso8601Timestamp);
                // JSONObject json = new JSONObject(params);
                list.add(params);

            }


        }

        if (list.isEmpty())
            return;
        // Log.e(TAG,"--------hrv参数="+new Gson().toJson(list));
        //String upPatch = "http://apihd.guiderhealth.com/api/v1/hrv";
        String upPatch = Commont.HRV_URL;
        OkHttpTool.getInstance().doRequest(upPatch, new Gson().toJson(list), this, result -> {
            if (!WatchUtils.isEmpty(result)) {
                if (Commont.isDebug) Log.e(TAG, "最后HRV传返回啊: " + result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.has("code")) {
                        if (jsonObject.getInt("code") == 0) {
                            //上传成功，修改状态
                            //LitePal.updateAsync()
                            updateLoadStatus(hrvList);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                upHrv = true;
            } else {
                MyApp.getInstance().setUploadDateGDNew(false);
                // 正在上传数据,写到全局,保证同时只有一个本服务在运行
            }
        });

    }

    //修改上传状态
    private void updateLoadStatus(List<B31HRVBean> hrvLists) {

//        ContentValues contentValues = new ContentValues();
//        contentValues.put("isUpdata",true);
//        int code = LitePal.updateAll(B31HRVBean.class,contentValues,"bleMac = ? and dateStr = ?",
//                hrvBean.getBleMac(),hrvBean.getDateStr());
//        Log.e(TAG,"-------hrv修改="+code);

        for (B31HRVBean hrvBean : hrvLists) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("isUpdata", true);
            int code = LitePal.updateAll(B31HRVBean.class, contentValues,
                    "bleMac = ? and dateStr = ?",
                    hrvBean.getBleMac(), hrvBean.getDateStr());
//            Log.e(TAG,"-------hrv修改="+code);
        }
    }


    /**
     * 读取SPO2
     */
    private void getSpo2ToServices() {
        spo2hOriginDataList.clear();
        oxyMapList.clear();
        if (spo2List == null || spo2List.isEmpty()) return;
        Log.e(TAG, "------spo2的大小=" + spo2List.size());

        //血氧的集合
        List<Map<String, String>> oxygenValueList = new ArrayList<>();
        //呼吸暂停的集合
        List<Map<String, String>> apneaResultList = new ArrayList<>();
        //呼吸率的集合
        List<Map<String, String>> respirationRateList = new ArrayList<>();
        //心脏负荷的集合
        List<Map<String, String>> cardiacLoadList = new ArrayList<>();

        //睡眠活动
        List<Map<String, String>> sleepActivityList = new ArrayList<>();


        for (B31Spo2hBean b31Spo2hBean : spo2List) {
//            if(b31Spo2hBean.isUpdata())
//                return;
            //Log.e(TAG,"------spo2="+b31Spo2hBean.toString());

            Spo2hOriginData spo2hOriginData = new Gson().fromJson(b31Spo2hBean.getSpo2hOriginData(), Spo2hOriginData.class);
            if (spo2hOriginData.getDate().equals(WatchUtils.getCurrentDate())) {
                spo2hOriginDataList.add(spo2hOriginData);
            }

            TimeData timeData = spo2hOriginData.getmTime();
            String spo2Dates = timeData.getDateAndClockForSleepSecond();

            Date dateStart = W30BasicUtils.stringToDate(spo2Dates.replace(":",
                    "-"), "yyyy-MM-dd HH-mm-ss");
            String iso8601Timestamp = WatchUtils.getISO8601Timestamp(dateStart);

            //血氧
            Map<String, String> oxyMap = new HashMap<>();
            oxyMap.put("accountId", accountId + "");
            oxyMap.put("bo", spo2hOriginData.getOxygenValue() + "");
            oxyMap.put("testTime", iso8601Timestamp);
            oxyMap.put("deviceCode", deviceCode);
            oxygenValueList.add(oxyMap);


            Map<String, String> tmpoxyMap = new HashMap<>();
            tmpoxyMap.put("testTime", iso8601Timestamp);
            tmpoxyMap.put("oxygenValue", spo2hOriginData.getOxygenValue() + "");
            oxyMapList.add(tmpoxyMap);


            //呼吸暂停
            Map<String, String> apneaResultMap = new HashMap<>();
            apneaResultMap.put("accountId", accountId + "");
            apneaResultMap.put("breathpause", spo2hOriginData.getApneaResult() + "");
            apneaResultMap.put("avg", "0");
            apneaResultMap.put("testTime", iso8601Timestamp);
            apneaResultMap.put("deviceCode", deviceCode);
            apneaResultList.add(apneaResultMap);


            //呼吸率
            Map<String, String> respirationRateMap = new HashMap<>();
            respirationRateMap.put("accountId", accountId + "");
            respirationRateMap.put("value", spo2hOriginData.getRespirationRate() + "");
            respirationRateMap.put("testTime", iso8601Timestamp);
            respirationRateMap.put("deviceCode", deviceCode);
            respirationRateList.add(respirationRateMap);


            //心脏负荷
            Map<String, String> cardiacLoadMap = new HashMap<>();
            cardiacLoadMap.put("accountId", accountId + "");
            cardiacLoadMap.put("value", "0");
            cardiacLoadMap.put("testTime", iso8601Timestamp);
            cardiacLoadMap.put("deviceCode", deviceCode);
            cardiacLoadList.add(cardiacLoadMap);


            //睡眠活动
            Map<String, String> sleepActivityMap = new HashMap<>();
            sleepActivityMap.put("accountId", accountId + "");
            sleepActivityMap.put("bo", spo2hOriginData.getOxygenValue() + "");
            sleepActivityMap.put("heartBeat", spo2hOriginData.getHeartValue() + "");
            sleepActivityMap.put("breathPause", spo2hOriginData.getApneaResult() + "");
            sleepActivityMap.put("hypoxiaTime", spo2hOriginData.getHypoxiaTime() + "");
            sleepActivityMap.put("heartLoad", spo2hOriginData.getCardiacLoad() + "");
            sleepActivityMap.put("sleepActivity", spo2hOriginData.getSportValue() + "");
            sleepActivityMap.put("respirationRate", spo2hOriginData.getRespirationRate() + "");
            sleepActivityMap.put("deviceCode", deviceCode);
            sleepActivityMap.put("osahs", "normal");
            sleepActivityMap.put("testTime", iso8601Timestamp);
            sleepActivityList.add(sleepActivityMap);


        }


        //血氧
        String oxyUrl = Commont.GAI_DE_BASE_URL + "bloodoxygen";
        //String oxyUrl = Commont.BLOOD_OXY_URL;

        OkHttpTool.getInstance().doRequest(oxyUrl, new Gson().toJson(oxygenValueList),
                "", result -> Log.e(TAG, "-------血氧上传=" + result));

        if (apneaResultList.isEmpty())
            return;
        //呼吸暂停
        String apneaResultUrl = Commont.GAI_DE_BASE_URL + "breathpause";

        String apneStr = new Gson().toJson(apneaResultList);

//        String filePath = Environment.getExternalStorageDirectory()+"/DCIM/";
//        new GetJsonDataUtil().writeTxtToFile(WatchUtils.getCurrentDate1()+apneStr,filePath,"apnea.json");

        OkHttpTool.getInstance().doRequest(apneaResultUrl, apneStr, "", new OkHttpTool.HttpResult() {
            @Override
            public void onResult(String result) {
                Log.e(TAG, "--------呼吸暂停=" + result);
            }
        });

        if (respirationRateList.isEmpty())
            return;
        //呼吸率
        final String respirationRateUrl = Commont.GAI_DE_BASE_URL + "breathe";
        OkHttpTool.getInstance().doRequest(respirationRateUrl,
                new Gson().toJson(respirationRateList),
                "", result -> Log.e(TAG, "-------呼吸率=" + result));

        if (cardiacLoadList.isEmpty())
            return;
        //心脏负荷
        String cardiacLoadUrl = Commont.GAI_DE_BASE_URL + "heartload";
        OkHttpTool.getInstance().doRequest(cardiacLoadUrl, new Gson().toJson(cardiacLoadList),
                "", result -> Log.e(TAG, "------心脏负荷=" + result));

        if (sleepActivityList.isEmpty())
            return;
        //睡眠活动
        String sleepActivityUrl = Commont.GAI_DE_BASE_URL + "sleepactivity";


        OkHttpTool.getInstance().doRequest(sleepActivityUrl, new Gson().toJson(sleepActivityList),
                "", result -> {
                    Log.e(TAG, "-------睡眠活动=" + result);
                    if (WatchUtils.isEmpty(result))
                        return;
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (!jsonObject.has("code"))
                            return;
                        if (jsonObject.getInt("code") == 0) {
                            updateSpo2Data(spo2List);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });


//
//        for(B31Spo2hBean b31Spo2hBean : spo2List) {
//            Spo2hOriginData spo2hOriginData1 = new Gson().fromJson(b31Spo2hBean.getSpo2hOriginData(), Spo2hOriginData.class);
//            TimeData timeData = spo2hOriginData1.getmTime();
//            String spo2Dates = timeData.getDateAndClockForSleepSecond();//obtainDate(timeData);
//            int oxygenValue = spo2hOriginData1.getOxygenValue();
//
//            if (oxygenValue > 0) {
//                Map<String, Object> params = new HashMap<>();
//                params.put("accountId", accountId);
//                params.put("deviceCode", deviceCode);
//                params.put("state", "");
//                params.put("bo", oxygenValue);
//                params.put("createTime", WatchUtils.getISO8601Timestamp(new Date()));
//                //testTime  =   2019-06-20 17-00-00
//                //2019-06-20T08:40:41Z
//                Date dateStart = W30BasicUtils.stringToDate(spo2Dates.replace(":", "-"), "yyyy-MM-dd HH-mm-ss");
//                String iso8601Timestamp = WatchUtils.getISO8601Timestamp(dateStart);
//                params.put("testTime", iso8601Timestamp);
//                list.add(params);
//
//            }
//        }
//        //Log.e(TAG,"-----spo2参数="+new Gson().toJson(list));
//            //String upPatch = Base_Url + "bloodoxygen";
//            String upPatch = "http://apihd.guiderhealth.com/api/v1/breathpause";
//        OkHttpTool.getInstance().doRequest(upPatch, list.toString(), this, new OkHttpTool.HttpResult() {
//            @Override
//            public void onResult(String result) {
//                if (!WatchUtils.isEmpty(result)) {
//                    if (Commont.isDebug)Log.e(TAG, "最后SPO2上传返回啊: " + result);
//                    upSpo2 = true;
//                } else {
//                    MyApp.getInstance().setUploadDateGDNew(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行
//                }
//            }
//        });


//
//        if (spo2List != null && !spo2List.isEmpty()) {
//            if (Commont.isDebug)Log.e(TAG, "一共有 " + spo2List.size() + " 条SPO2数据");
//            for (int i = (spo2List.size() - 1); i >= 0; i--) {
//                /**
//                 * {dateStr='2019-06-21', bleMac='C4:F3:AF:CB:D9:12'
//                 */
//                B31Spo2hBean b31Spo2hBean = spo2List.get(i);
//                if (b31Spo2hBean != null) {
//                    String date = b31Spo2hBean.getDateStr();
//                    String spo2hOriginData = b31Spo2hBean.getSpo2hOriginData();
//                    if (Commont.isDebug)Log.e(TAG, "第 " + i + " 条的Spo2数据   日期是：" + b31Spo2hBean.getDateStr());
//                    if (!WatchUtils.isEmpty(spo2hOriginData) && !WatchUtils.isEmpty(date)) {
//                        //为倒序，i == 0  最后一个为今天，今天的数据要做处理
//                        if (i == 0) setUpSpo2(spo2hOriginData, true);
//                        else setUpSpo2(spo2hOriginData, false);
//                    } else {
//                        if (i == 0) {
//                            upSpo2 = true;
//                            if (Commont.isDebug)Log.e(TAG, "SPO2数据异常");
//                        }
//                    }
//                }
//
//            }
//        } else {
//            if (Commont.isDebug)Log.e(TAG, "没有SPO2数据");
//            upSpo2 = true;
//        }
    }

    private void updateSpo2Data(List<B31Spo2hBean> spo2Lists) {
        for (B31Spo2hBean b31Spo2hBean : spo2Lists) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("isUpdata", true);
            LitePal.updateAll(B31Spo2hBean.class, contentValues,
                    "bleMac = ? and dateStr = ?", b31Spo2hBean.getBleMac(),
                    b31Spo2hBean.getDateStr());

        }
    }


    /**
     * 上传步数
     *
     * @param date
     * @param originData
     * @param postion    为倒序，最后一个为今天，今天的数据要做处理
     */
    private void setUpSport(String date, String originData, final int postion) {
        /**
         * 原来的格式
         * [
         *   {
         *     "calValue": 2.5,
         *     "date": "2019-06-24",
         *     "disValue": 0.038,
         *     "sportValue": 236,
         *     "stepValue": 49,
         *     "time": {
         *       "day": 24,
         *       "hour": 0,
         *       "minute": 0,
         *       "month": 6,
         *       "second": 0,
         *       "weekDay": 0,
         *       "year": 2019
         *     }
         *   },
         *   ...
         * ]
         */
        //LogTestUtil.e(TAG, "======步数数据  " + originData);

        /**
         * 需要上传的格式
         * [
         *   {
         *     "accountId": 0,
         *     "createTime": "2019-06-24T06:10:15.759Z",
         *     "deviceCode": "string",
         *     "id": 0,
         *     "step": 0,
         *     "testTime": "2019-06-24T06:10:15.759Z"
         *   }
         * ]
         */
        final List<HalfHourSportData> dataList = new Gson().fromJson(originData,
                new TypeToken<List<HalfHourSportData>>() {
        }.getType());

        List<Map<String, Object>> mapList = new ArrayList<>();
        if (dataList != null && !dataList.isEmpty()) {

            for (int i = 0; i < dataList.size(); i++) {

                HalfHourSportData halfHourSportData = dataList.get(i);
                if (halfHourSportData != null) {
                    int stepValue = halfHourSportData.getStepValue();
                    TimeData time = halfHourSportData.getTime();
                    //返回时分
                    String stepDate = getTimeStr(time);
                    Date dateStart = W30BasicUtils.stringToDate(
                            time.getDateAndClockForSleepSecond().replace(
                                    ":", "-"), "yyyy-MM-dd HH-mm-ss");
                    String iso8601Timestamp = WatchUtils.getISO8601Timestamp(dateStart);
                    //Log.e(TAG,"-------时间="+time.getDateAndClockForSleepSecond().replace(":","-")+"----转换="+iso8601Timestamp);
                    /**
                     * [
                     *   {
                     *     "accountId": 0,
                     *     "createTime": "2019-06-25T03:07:52.007Z",
                     *     "deviceCode": "string",
                     *     "id": 0,
                     *     "step": 0,
                     *     "testTime": "2019-06-25T03:07:52.007Z"
                     *   }
                     * ]
                     */
                    if (stepValue > 0) {
                        Map<String, Object> params = new HashMap<>();
                        params.put("accountId", accountId);
                        params.put("createTime", WatchUtils.getISO8601Timestamp(new Date()));
                        params.put("deviceCode", deviceCode);
                        params.put("id", "");
                        params.put("step", stepValue);
                        params.put("testTime", iso8601Timestamp);
                        mapList.add(params);
                    }

                }
            }
        }
        if (!mapList.isEmpty()) {
            JSONArray jsonArray = new JSONArray(mapList);
//             if (Commont.isDebug)Log.e(TAG, "====STEP===" + jsonArray.toString());
            String upStepPatch = Base_Url + "walkrecord";

            //String upStepPatch = "http://apihd.guiderhealth.com/api/v1/walkrecord";


            Log.e(TAG, "-----步数上传数组=" + jsonArray.toString());

            if (postion == 0) {//这里天数是按照倒序排的，0 = 今天
                OkHttpTool.getInstance().doRequest(upStepPatch, jsonArray.toString(), this,
                        result -> {
                    if (!WatchUtils.isEmpty(result)) {
                        if (Commont.isDebug) Log.e(TAG, "sport- 最后步数上传返回啊: " + result);
                        //是否是 最后一天最后第二条
                        upSport = true;
                    } else {
                        MyApp.getInstance().setUploadDateGDNew(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行
                    }
                });
            } else {
                OkHttpTool.getInstance().doRequest(upStepPatch, jsonArray.toString(),
                        this, result -> {
                    if (Commont.isDebug) Log.e(TAG, "sport- 步数上传返回: " + result);
                    if (!WatchUtils.isEmpty(result)) {
                        if (Commont.isDebug) Log.e(TAG, "sport- 步数上传返回: " + result);
                    } else {
                        MyApp.getInstance().setUploadDateGDNew(false);
                        // 正在上传数据,写到全局,保证同时只有一个本服务在运行
                    }
                });
            }
        } else {
            upSport = true;
        }


//        if (dataList != null && !dataList.isEmpty()) {
//            int SportCount = 0;//总的有效数据长度
//            int SportCountAdd = 0;//总的有效数据长度
//            for (int i = 0; i < dataList.size(); i++) {
//                if (dataList.get(i).getStepValue() > 0) {
//                    SportCount++;
//                }
//            }
//             if (Commont.isDebug)Log.e(TAG, date + "  运动数据总共长度为：" + dataList.size() + "  有效长度为：" + SportCount);
//            for (int i = 0; i < dataList.size(); i++) {
//                int allStep = dataList.get(i).getStepValue();
//                TimeData time = dataList.get(i).getTime();
//                String stringHour = "00";
//                String stringMinute = "00";
//                int hour = time.getHour();
//                int minute = time.getMinute();
//                if (hour >= 10) {
//                    stringHour = "" + hour;
//                } else {
//                    stringHour = "0" + hour;
//                }
//                if (minute >= 10) {
//                    stringMinute = "" + minute;
//                } else {
//                    stringMinute = "0" + minute;
//                }
//                String stepDate = stringHour + "-" + stringMinute;
//
//
//                if (allStep > 0) {
//                    SportCountAdd++;
//                    if (SportCountAdd < SportCount) {
//                        Map<String, Object> params = new HashMap<>();
//                        params.put("accountId", accountId);
//                        params.put("deviceCode", deviceCode);
//                        params.put("step", allStep);
//                        params.put("id", 0);
//                        params.put("createTime", WatchUtils.getISO8601Timestamp(new Date()));
//                        //testTime  =   2019-06-20 17-00-00
//                        //2019-06-20T08:40:41Z
//                        Date dateStart = W30BasicUtils.stringToDate(date + " " + stepDate + "-00", "yyyy-MM-dd HH-mm-ss");
//                        String iso8601Timestamp = WatchUtils.getISO8601Timestamp(dateStart);
//                        params.put("testTime", iso8601Timestamp);
////                        params.put("WALKTIME", "0");
////                        params.put("CARDID", deviceCode);
////                        params.put("STEPS", allStep);
////                        params.put("DISTANCE", 0);
////                        params.put("CALORIE", 0);
////                        params.put("CDATE", date + " " + stepDate + "-00");
//                        String upStepPatch = Base_Url + "walkstep";
//
//                        JSONObject json = new JSONObject(params);
//                        List<JSONObject> list = new ArrayList<>();
//                        list.add(json);
//                         if (Commont.isDebug)Log.e(TAG, "sport- 上传步数参数:" + list.toString());
//
//                        if (postion == 0) {//这里天数是按照倒序排的，0 = 今天
//                            OkHttpTool.getInstance().doRequest(upStepPatch, list.toString(), this, new OkHttpTool.HttpResult() {
//                                @Override
//                                public void onResult(String result) {
//                                    if (!WatchUtils.isEmpty(result)) {
//                                         if (Commont.isDebug)Log.e(TAG, "sport- 最后步数上传返回啊: " + result);
//                                        //是否是 最后一天最后第二条
//                                        upSport = true;
//                                    } else {
//                                        MyApp.getInstance().setUploadDateGDNew(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行
//                                    }
//                                }
//                            });
//                        } else {
//                            OkHttpTool.getInstance().doRequest(upStepPatch, list.toString(), this, new OkHttpTool.HttpResult() {
//                                @Override
//                                public void onResult(String result) {
//                                     if (Commont.isDebug)Log.e(TAG, "sport- 步数上传返回: " + result);
//                                    if (!WatchUtils.isEmpty(result)) {
//                                         if (Commont.isDebug)Log.e(TAG, "sport- 步数上传返回: " + result);
//                                    } else {
//                                        MyApp.getInstance().setUploadDateGDNew(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行
//                                    }
//                                }
//                            });
//                        }
//                    } else {
//                        if (i == (dataList.size() - 1) && postion == 0) {
//                             if (Commont.isDebug)Log.e(TAG, "sport- 步数数据越界: ");
//                            upSport = true;
//                        }
//                    }
//
//                } else {
//                    if (i == (dataList.size() - 1) && postion == 0) {
//                         if (Commont.isDebug)Log.e(TAG, "sport- 步数数据小于等于0啦: ");
//                        upSport = true;
//                    }
//                }
//            }
//        } else {
//             if (Commont.isDebug)Log.e(TAG, "运动数据异常为空");
//            upSport = true;
//        }
    }

    /**
     * 上传睡眠数据
     *
     * @param date
     * @param originData
     * @param postion
     */
    private void setUpSleep(String date, String originData, final int postion) {
        /**
         * 原来的数据类型
         * {
         *   "Date": "2019-06-22",
         *   "allSleepTime": 370,
         *   "cali_flag": 0,
         *   "deepSleepTime": 100,
         *   "lowSleepTime": 270,
         *   "sleepDown": {
         *     "day": 23,
         *     "hour": 1,
         *     "minute": 30,
         *     "month": 6,
         *     "second": 0,
         *     "weekDay": 0,
         *     "year": 2019
         *   },
         *   "sleepLine": "00000000000010100011000100111110000002222000000100020002011101100100011210",
         *   "sleepQulity": 2,
         *   "sleepUp": {
         *     "day": 23,
         *     "hour": 7,
         *     "minute": 40,
         *     "month": 6,
         *     "second": 0,
         *     "weekDay": 0,
         *     "year": 2019
         *   },
         *   "wakeCount": 4
         * }
         */
        //LogTestUtil.e(TAG, "======睡眠数据   " + originData);
        SleepData sleepData = new Gson().fromJson(originData, SleepData.class);

        //入睡时间
        TimeData sleepDown = sleepData.getSleepDown();
        String timeStr_goSleepTime = getTimeStr(sleepDown);//返回时分
        Date dateStart_goSleepTime = W30BasicUtils.stringToDate(
                date + " " + timeStr_goSleepTime + "-00", "yyyy-MM-dd HH-mm-ss");
        String goSleepTime = WatchUtils.getISO8601Timestamp(dateStart_goSleepTime);

        //起床时间
        TimeData sleepUp = sleepData.getSleepUp();
        String timeStr_wakeUpTime = getTimeStr(sleepUp);
        Date dateStart_wakeUpTime = W30BasicUtils.stringToDate(
                date + " " + timeStr_wakeUpTime + "-00", "yyyy-MM-dd HH-mm-ss");
        String wakeUpTime = WatchUtils.getISO8601Timestamp(dateStart_wakeUpTime);

        //睡眠质量
        int sleepQulity = sleepData.getSleepQulity();

        //潜水时间
        int lowSleepTime = sleepData.getLowSleepTime();

        //深水时间
        int deepSleepTime = sleepData.getDeepSleepTime();

        //所有睡眠时间
        int allSleepTime = sleepData.getAllSleepTime();


        String sleepLine = sleepData.getSleepLine();


        //清醒次数
        int wakeCount = 0;
        for (int i = 0; i < sleepLine.length(); i++) {
            char chars = sleepLine.charAt(i);
            if (chars == '0') {
                wakeCount++;
            }
        }


        /**
         * 上传是需要的数据类型
         * [
         *   {
         *     "accountId": 0,
         *     "createTime": "2019-06-24T06:42:05.550Z",
         *     "deepSleepTime": "2019-06-24T06:42:05.550Z",
         *     "deviceCode": "string",
         *     "goSleepTime": "2019-06-24T06:42:05.550Z",
         *     "id": 0,
         *     "lowSleepTime": "2019-06-24T06:42:05.550Z",
         *     "sleepTime": "2019-06-24T06:42:05.550Z",
         *     "state": "string",
         *     "testTime": "2019-06-24T06:42:05.550Z",
         *     "wakeUpNum": 0,
         *     "wakeUpTime": "2019-06-24T06:42:05.550Z"
         *   }
         * ]
         */
        Map<String, Object> params = new HashMap<>();
        List<Map<String, Object>> mapList = new ArrayList<>();
        params.put("accountId", accountId);
        params.put("createTime", WatchUtils.getISO8601Timestamp(new Date()));
        params.put("deepSleepTime", deepSleepTime);
        params.put("deviceCode", deviceCode);
        params.put("goSleepTime", goSleepTime);
        params.put("lowSleepTime", lowSleepTime);
        params.put("sleepTime", allSleepTime);
        params.put("testTime", WatchUtils.getISO8601Timestamp(new Date()));
        params.put("id", 0);
        params.put("state", +sleepQulity);
        params.put("wakeUpNum", wakeCount);
        params.put("wakeUpTime", wakeUpTime);
        mapList.add(params);

        if (!mapList.isEmpty()) {
            String usSleepPath = Commont.SLEEP_QUALITY_URL;

            //String usSleepPath = "http://api.guiderhealth.com/api/v1/sleepquality";
            JSONArray jsonArray = new JSONArray(mapList);
//             if (Commont.isDebug)Log.e(TAG, "====SLEEP===" + jsonArray.toString());
            OkHttpTool.getInstance().doRequest(usSleepPath, jsonArray.toString(), this,
                    result -> {
                if (!WatchUtils.isEmpty(result)) {
                    //最后一天最后一条
                    if (postion == 0) {
                        if (Commont.isDebug) Log.e(TAG, "sleep- 最后睡眠上传返回" + result);
                        upSleep = true;
                    }
                } else {
                    MyApp.getInstance().setUploadDateGDNew(false);
                    // 正在上传数据,写到全局,保证同时只有一个本服务在运行
                }
            });
        } else {
            upSleep = true;
        }

//
//        int deepSleepTime = sleepData.getDeepSleepTime();
//        int lowSleepTime = sleepData.getLowSleepTime();
//
//        Map<String, Object> params = new HashMap<>();
//        int allTimes = deepSleepTime + lowSleepTime;
//        String times = remainTimeStr(allTimes);
//        params.put("accountId", accountId);
//        params.put("deviceCode", deviceCode);
//        params.put("id", 0);
//        params.put("state", "");
//        params.put("value", times);
//        params.put("createTime", WatchUtils.getISO8601Timestamp(new Date()));
//        //testTime  =   2019-06-20 00-00-00
//        //2019-06-20T08:40:41Z
//        Date dateStart = W30BasicUtils.stringToDate(date + " 00-00-00", "yyyy-MM-dd HH-mm-ss");
//        String iso8601Timestamp = WatchUtils.getISO8601Timestamp(dateStart);
//        params.put("testTime", iso8601Timestamp);
//
//        JSONObject json = new JSONObject(params);
//        List<JSONObject> list = new ArrayList<>();
//        list.add(json);
////        params.put("SLEEPTIME", times);
////        params.put("CARDID", deviceCode);
////        params.put("CDATE", date + " 00-00-00");
//
////        MyLogUtil.e(TAG, "sleep- 上传睡眠参数：" + params.toString());
//        String usSleepPath = Base_Url + "sleepactivity";
//        OkHttpTool.getInstance().doRequest(usSleepPath, list.toString(), this, new OkHttpTool.HttpResult() {
//            @Override
//            public void onResult(String result) {
//                if (!WatchUtils.isEmpty(result)) {
//                    //最后一天最后一条
//                    if (postion == 0) {
////                         if (Commont.isDebug)Log.e(TAG, "sleep- 最后睡眠上传返回" + result);
//                        upSleep = true;
//                    }
//
//                } else {
//                    MyApp.getInstance().setUploadDateGDNew(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行
//                }
//            }
//        });
    }


    /**
     * 上传心率数据
     *
     * @param date
     * @param originData
     * @param postion
     */
    private void setUpHeart(String date, String originData, final boolean postion) {

        /**
         * 原始数据类型
         * [
         *   {
         *     "date": "2019-06-21",
         *     "rateValue": 72,
         *     "time": {
         *       "day": 21,
         *       "hour": 0,
         *       "minute": 0,
         *       "month": 6,
         *       "second": 0,
         *       "weekDay": 0,
         *       "year": 2019
         *     }
         *   },
         *   ...
         * ]
         */
        //LogTestUtil.e(TAG, "======心率数据  " + originData);
        final List<HalfHourRateData> dataList = new Gson().fromJson(originData,
                new TypeToken<List<HalfHourRateData>>() {
        }.getType());

        List<Map<String, Object>> mapList = new ArrayList<>();
        if (dataList != null && !dataList.isEmpty()) {
            Map<String, Object> params = new HashMap<>();
            for (int i = 0; i < dataList.size(); i++) {
                HalfHourRateData hourRateData = dataList.get(i);
                if (hourRateData != null) {
                    int rateValue = hourRateData.getRateValue();
                    TimeData time = hourRateData.getTime();
                    //返回时分
                    String stepDate = getTimeStr(time);
                    Date dateStart = W30BasicUtils.stringToDate(
                            date + " " + stepDate + "-00", "yyyy-MM-dd HH-mm-ss");
                    String iso8601Timestamp = WatchUtils.getISO8601Timestamp(dateStart);

                    /**
                     * 上传时的数据类型
                     * [
                     *   {
                     *     "accountId": 0,
                     *     "createTime": "2019-06-24T07:24:09.003Z",
                     *     "deviceCode": "string",
                     *     "hb": 0,
                     *     "id": 0,
                     *     "state": "string",
                     *     "testTime": "2019-06-24T07:24:09.003Z"
                     *   }
                     * ]
                     */
                    if (rateValue > 0) {
                        params.put("accountId", accountId);
//                        params.put("createTime", WatchUtils.getISO8601Timestamp(new Date()));
//                        params.put("deviceCode", deviceCode);
//                        params.put("id", "");
//                        params.put("state", "");
                        params.put("hb", rateValue);
                        params.put("testTime", iso8601Timestamp);
                        mapList.add(params);
                    }

                }
            }


            if (!mapList.isEmpty()) {
                JSONArray jsonArray = new JSONArray(mapList);
//                 if (Commont.isDebug)Log.e(TAG, "====HEART===" + jsonArray.toString());

                String path = Commont.GAI_DE_BASE_URL + "heartbeat";
                //String path = "http://apihd.guiderhealth.com/api/v1/heartbeat";


                if (postion) {//这里天数是按照倒序排的，0 = 今天
                    OkHttpTool.getInstance().doRequest(path, jsonArray.toString(),
                            this, result -> {
                        if (!WatchUtils.isEmpty(result)) {
                            if (Commont.isDebug) Log.e(TAG, "最后心率上传返回啊: " + result);
                            upHeart = true;
                        } else {
                            MyApp.getInstance().setUploadDateGDNew(false);
                            // 正在上传数据,写到全局,保证同时只有一个本服务在运行
                        }
                    });
                } else {
                    OkHttpTool.getInstance().doRequest(path, jsonArray.toString(),
                            this, result -> {
                        if (!WatchUtils.isEmpty(result)) {
                            if (Commont.isDebug) Log.e(TAG, "心率上传返回: " + result);
                        } else {
                            MyApp.getInstance().setUploadDateGDNew(false);
                            // 正在上传数据,写到全局,保证同时只有一个本服务在运行
                        }
                    });
                }
            } else {
                upHeart = true;
            }
        }


//        if (dataList != null && !dataList.isEmpty()) {
//            int HeartCount = 0;//总的有效数据长度
//            int HeartCountAdd = 0;
//            for (int i = 0; i < dataList.size(); i++) {
//                if (dataList.get(i).getRateValue() > 0) {
//                    HeartCount++;
//                }
//            }
////             if (Commont.isDebug)Log.e(TAG, date + "  心率数据总共长度为：" + dataList.size() + "  有效长度为：" + HeartCount);
//
//            for (int i = 0; i < dataList.size(); i++) {
//                String heartDates = obtainDate(dataList.get(i).getTime());
//                int rateValue = dataList.get(i).getRateValue();
//
//                if (rateValue > 0) {
//                    HeartCountAdd++;
//                    if (HeartCountAdd < HeartCount) {
//
//                        Map<String, Object> params = new HashMap<>();
//                        params.put("accountId", accountId);
//                        params.put("deviceCode", deviceCode);
//                        params.put("hb", rateValue);
//                        params.put("id", 0);
//                        params.put("state", "");
//                        params.put("createTime", WatchUtils.getISO8601Timestamp(new Date()));
//                        //testTime  =   2019-06-20 00-00-00
//                        //2019-06-20T08:40:41Z
//                        // if (Commont.isDebug)Log.e(TAG,"---xl- "+heartDates.replace(":", "-"));
//                        Date dateStart = W30BasicUtils.stringToDate(heartDates.replace(":", "-"), "yyyy-MM-dd HH-mm-ss");
//                        String iso8601Timestamp = WatchUtils.getISO8601Timestamp(dateStart);
//                        params.put("testTime", iso8601Timestamp);
//                        JSONObject json = new JSONObject(params);
//                        List<JSONObject> list = new ArrayList<>();
//                        list.add(json);
//
//                        String path = Base_Url + "heartbeat";
////                        MyLogUtil.e(TAG, "heart- 心率上传参数" + params.toString());
//
//                        if (postion) {//这里天数是按照倒序排的，0 = 今天
//                            OkHttpTool.getInstance().doRequest(path, list.toString(), this, new OkHttpTool.HttpResult() {
//                                @Override
//                                public void onResult(String result) {
//                                    if (!WatchUtils.isEmpty(result)) {
////                                         if (Commont.isDebug)Log.e(TAG, "最后心率上传返回啊: " + result);
//                                        upHeart = true;
//                                    } else {
//                                        MyApp.getInstance().setUploadDateGDNew(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行
//                                    }
//                                }
//                            });
//                        } else {
//                            OkHttpTool.getInstance().doRequest(path, list.toString(), this, new OkHttpTool.HttpResult() {
//                                @Override
//                                public void onResult(String result) {
//                                    if (!WatchUtils.isEmpty(result)) {
////                                         if (Commont.isDebug)Log.e(TAG, "心率上传返回: " + result);
//                                    } else {
//                                        MyApp.getInstance().setUploadDateGDNew(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行
//                                    }
//                                }
//                            });
//                        }
//
//                    } else {
//                        if (i == (dataList.size() - 1) && postion) {
//                            upHeart = true;
////                             if (Commont.isDebug)Log.e(TAG, " 心率数据越界了: " + result);
//                        }
//                    }
//                } else {
//                    if (i == (dataList.size() - 1) && postion) {
//                        upHeart = true;
////                         if (Commont.isDebug)Log.e(TAG, " 心率异常为小于等于0啦: " + result);
//                    }
//                }
//            }
//        }
    }


    /**
     * 上传血压
     *
     * @param date
     * @param originData
     * @param postion
     */
    private void setUpBp(String date, String originData, int postion) {
        /**
         * [
         *   {
         *     "date": "2019-06-21",
         *     "highValue": 115,
         *     "lowValue": 84,
         *     "time": {
         *       "day": 21,
         *       "hour": 0,
         *       "minute": 5,
         *       "month": 6,
         *       "second": 0,
         *       "weekDay": 0,
         *       "year": 2019
         *     }
         *   },
         *  ...
         * ]
         */
        //LogTestUtil.e(TAG, "======血压数据  " + originData);
        final List<HalfHourBpData> dataList = new Gson().fromJson(originData,
                new TypeToken<List<HalfHourBpData>>() {
        }.getType());
        List<Map<String, Object>> mapList = new ArrayList<>();
        if (dataList != null && !dataList.isEmpty()) {
            Map<String, Object> params = new HashMap<>();
            for (int i = 0; i < dataList.size(); i++) {
                HalfHourBpData halfHourBpData = dataList.get(i);
                if (halfHourBpData != null) {
                    TimeData time = halfHourBpData.getTime();
                    int highValue = halfHourBpData.getHighValue();
                    int lowValue = halfHourBpData.getLowValue();
                    //返回时分
                    String stepDate = getTimeStr(time);
                    Date dateStart = W30BasicUtils.stringToDate(
                            date + " " + stepDate + "-00", "yyyy-MM-dd HH-mm-ss");
                    String iso8601Timestamp = WatchUtils.getISO8601Timestamp(dateStart);

                    /**
                     * [
                     *   {
                     *     "accountId": 0,
                     *     "createTime": "2019-06-24T07:49:48.511Z",
                     *     "dbp": 0,
                     *     "deviceCode": "string",
                     *     "id": 0,
                     *     "sbp": 0,
                     *     "state": "string",
                     *     "testTime": "2019-06-24T07:49:48.511Z"
                     *   }
                     * ]
                     */
                    if (lowValue > 0 && highValue > 0) {
                        params.put("accountId", accountId);
                        params.put("createTime", WatchUtils.getISO8601Timestamp(new Date()));
                        params.put("deviceCode", deviceCode);
                        params.put("id", "");
                        params.put("state", "");
                        params.put("dbp", lowValue);
                        params.put("sbp", highValue);
                        params.put("testTime", iso8601Timestamp);
                        mapList.add(params);
                    }

                }
            }

            if (!mapList.isEmpty()) {
                JSONArray jsonArray = new JSONArray(mapList);
//                 if (Commont.isDebug)Log.e(TAG, "====BLOOP===" + jsonArray.toString());

                String path = Commont.BLOOD_URL;
                ///String path = "http://apihd.guiderhealth.com/api/v1/bloodpressure";

                if (postion == 0) {//这里天数是按照倒序排的，0 = 今天
                    OkHttpTool.getInstance().doRequest(path, jsonArray.toString(), this,
                            result -> {
                        if (!WatchUtils.isEmpty(result)) {
                            upBool = true;
                            if (Commont.isDebug) Log.e(TAG, " 最后血压上传返回啊: " + result);
                        }
                    });
                } else {
                    OkHttpTool.getInstance().doRequest(path, jsonArray.toString(),
                            this, result -> {
                        if (!WatchUtils.isEmpty(result)) {
                            if (Commont.isDebug) Log.e(TAG, "血压上传返回: " + result);
                        }
                    });
                }
            } else {
                upBool = true;
            }
        }

//        if (dataList != null && !dataList.isEmpty()) {
//            int BpCount = 0;//总的有效数据长度
//            int BpCountAdd = 0;
//            for (int i = 0; i < dataList.size(); i++) {
//                if (dataList.get(i).getLowValue() > 0 && dataList.get(i).getHighValue() > 0) {
//                    BpCount++;
//                }
//            }
////            if (Commont.isDebug)Log.e(TAG, date + "  血压数据总共长度为：" + dataList.size() + "  有效长度为：" + BpCount);
//            for (int i = 0; i < dataList.size(); i++) {
//                String bpDates = obtainDate(dataList.get(i).getTime());
//                int highValue = dataList.get(i).getHighValue();
//                int lowValue = dataList.get(i).getLowValue();
//                if (highValue > 0 && lowValue > 0) {
//                    BpCountAdd++;
//                    if (BpCountAdd < BpCount) {
//                        Map<String, Object> params = new HashMap<>();
//                        params.put("accountId", accountId);
//                        params.put("dbp", lowValue);
//                        params.put("sbp", highValue);
//                        params.put("state", "");
//                        params.put("id", 0);
//                        params.put("deviceCode", deviceCode);
//                        params.put("createTime", WatchUtils.getISO8601Timestamp(new Date()));
//                        //testTime  =   2019-06-20 00-00-00
//                        //2019-06-20T08:40:41Z
//                        //if (Commont.isDebug)Log.e(TAG,"---xy- "+bpDates.replace(":", "-"));
//                        Date dateStart = W30BasicUtils.stringToDate(bpDates.replace(":", "-"), "yyyy-MM-dd HH-mm-ss");
//                        String iso8601Timestamp = WatchUtils.getISO8601Timestamp(dateStart);
//                        params.put("testTime", iso8601Timestamp);
//                        JSONObject json = new JSONObject(params);
//                        List<JSONObject> list = new ArrayList<>();
//                        list.add(json);
//
//                        String path = Base_Url + "bloodPressures ";
////                        MyLogUtil.e(TAG, "bp- 血压上传参数" + params.toString());
//
//                        if (postion == 0) {//这里天数是按照倒序排的，0 = 今天
//                            OkHttpTool.getInstance().doRequest(path, list.toString(), this, new OkHttpTool.HttpResult() {
//                                @Override
//                                public void onResult(String result) {
//                                    if (!WatchUtils.isEmpty(result)) {
//                                        upBool = true;
////                                        if (Commont.isDebug)Log.e(TAG, " 最后血压上传返回啊: " + result);
//                                    }
//                                }
//                            });
//                        } else {
//                            OkHttpTool.getInstance().doRequest(path, list.toString(), this, new OkHttpTool.HttpResult() {
//                                @Override
//                                public void onResult(String result) {
//                                    if (!WatchUtils.isEmpty(result)) {
////                                        if (Commont.isDebug)Log.e(TAG, "血压上传返回: " + result);
//                                    }
//                                }
//                            });
//                        }
//                    } else {
//                        if (i == (dataList.size() - 1) && postion == 0) {
//                            upBool = true;
////                            if (Commont.isDebug)Log.e(TAG, "血压异常越界了: " + result);
//                        }
//                    }
//                } else {
//                    if (i == (dataList.size() - 1) && postion == 0) {
//                        upBool = true;
////                        if (Commont.isDebug)Log.e(TAG, "血压异常为0啦: " + result);
//                    }
//                }
//            }
//        }
    }


    /**
     * 上传HRV到后台
     *
     * @param hrvDataStr
     * @param isToday
     */
    private void setUpHrv(String hrvDataStr, boolean isToday) {

//        if (Commont.isDebug)Log.e(TAG, "======= HRV   " + hrvDataStr);

        HRVOriginData hrvOriginData = new Gson().fromJson(hrvDataStr, HRVOriginData.class);
        TimeData timeData = hrvOriginData.getmTime();
        String hrvDates = obtainDate(timeData);
        int hrvValue = hrvOriginData.getHrvValue();
        String rate = hrvOriginData.getRate();
        if (WatchUtils.isEmpty(rate)) return;
        int[] hrvDatas = null;
        if (rate.length() - 2 != 0) {
            hrvDatas = new int[rate.length() - 2];
            for (int i = 1; i < rate.length() - 1; i++) {
                char ch = rate.charAt(i);
                hrvDatas[i - 1] = ch;
            }
        }

        Map<String, Object> params = new HashMap<>();
        params.put("accountId", accountId);
        params.put("deviceCode", deviceCode);
        params.put("hrvData", hrvDatas);
        params.put("hrvIndex", hrvValue);
        params.put("state", "");
        params.put("createTime", WatchUtils.getISO8601Timestamp(new Date()));
        //testTime  =   2019-06-20 17-00-00
        //2019-06-20T08:40:41Z
        Date dateStart = W30BasicUtils.stringToDate(hrvDates.replace(
                ":", "-"), "yyyy-MM-dd HH-mm-ss");
        String iso8601Timestamp = WatchUtils.getISO8601Timestamp(dateStart);
        params.put("testTime", iso8601Timestamp);
        JSONObject json = new JSONObject(params);
        List<JSONObject> list = new ArrayList<>();
        list.add(json);

        String upPatch = Commont.GAI_DE_BASE_URL + "hrv";
        //String upPatch = "http://apihd.guiderhealth.com/api/v1/hrv";

        if (isToday) {
            OkHttpTool.getInstance().doRequest(upPatch, list.toString(), this, result -> {
                if (!WatchUtils.isEmpty(result)) {
                    if (Commont.isDebug) Log.e(TAG, "最后心HRV传返回啊: " + result);
                    upHrv = true;
                } else {
                    MyApp.getInstance().setUploadDateGDNew(false);
                    // 正在上传数据,写到全局,保证同时只有一个本服务在运行
                }
            });
        } else {
            OkHttpTool.getInstance().doRequest(upPatch, list.toString(), this, result -> {
                if (!WatchUtils.isEmpty(result)) {
                    if (Commont.isDebug) Log.e(TAG, "HRV上传返回啊: " + result);
                } else {
                    MyApp.getInstance().setUploadDateGDNew(false);
                    // 正在上传数据,写到全局,保证同时只有一个本服务在运行
                }
            });
        }

    }


    /**
     * 上传SPO2到后台
     *
     * @param spo2hOriginData
     * @param isToday
     */
    private void setUpSpo2(String spo2hOriginData, boolean isToday) {

//        if (Commont.isDebug)Log.e(TAG, "======= SPO2   " + spo2hOriginData);

        Spo2hOriginData spo2hOriginData1 = new Gson().fromJson(spo2hOriginData,
                Spo2hOriginData.class);
        TimeData timeData = spo2hOriginData1.getmTime();
        String spo2Dates = obtainDate(timeData);
        int oxygenValue = spo2hOriginData1.getOxygenValue();

        if (oxygenValue > 0) {
            Map<String, Object> params = new HashMap<>();
            params.put("accountId", accountId);
            params.put("deviceCode", deviceCode);
            params.put("state", "");
            params.put("bo", oxygenValue);
            params.put("createTime", WatchUtils.getISO8601Timestamp(new Date()));
            //testTime  =   2019-06-20 17-00-00
            //2019-06-20T08:40:41Z
            Date dateStart = W30BasicUtils.stringToDate(spo2Dates.replace(
                    ":", "-"), "yyyy-MM-dd HH-mm-ss");
            String iso8601Timestamp = WatchUtils.getISO8601Timestamp(dateStart);
            params.put("testTime", iso8601Timestamp);
            JSONObject json = new JSONObject(params);
            List<JSONObject> list = new ArrayList<>();
            list.add(json);

            //String upPatch = Base_Url + "bloodoxygen";
            String upPatch = Commont.BLOOD_OXY_URL;


            if (isToday) {
                OkHttpTool.getInstance().doRequest(upPatch, list.toString(), this, result -> {
                    if (!WatchUtils.isEmpty(result)) {
                        if (Commont.isDebug) Log.e(TAG, "最后SPO2上传返回啊: " + result);
                        upSpo2 = true;
                    } else {
                        MyApp.getInstance().setUploadDateGDNew(false);
                        // 正在上传数据,写到全局,保证同时只有一个本服务在运行
                    }
                });
            } else {
                OkHttpTool.getInstance().doRequest(upPatch, list.toString(), this, result -> {
                    if (!WatchUtils.isEmpty(result)) {
                        if (Commont.isDebug) Log.e(TAG, "SPO2上传返回啊: " + result);
                    } else {
                        MyApp.getInstance().setUploadDateGDNew(false);
                        // 正在上传数据,写到全局,保证同时只有一个本服务在运行
                    }
                });
            }
        }
    }


    /**
     * 改变本地数据库上传数据状态
     */
    private void changeUpload(int type) {
        B30HalfHourDB dbData = null;
        String currentDate = WatchUtils.getCurrentDate();
        switch (type) {
//            case STATE_SPORT:
//                for (int i = 0; i < sportData.size(); i++) {
//                    dbData = sportData.get(i);
////                    if (Commont.isDebug)Log.e(TAG, "时间对比--A-----" + currentDate + "=====" + dbData.getDate());
//                    if (!dbData.getDate().equals(currentDate)) {
//                        dbData.setUploadGD(1);// 只要不是当天的数据,都把本地状态设为"已上传"
//                        dbData.save();//因为当天数据还有可能不停的更新和上传
//                    }
//
//                }
//                break;
            case STATE_SLEEP:
                for (int i = 0; i < sleepData.size(); i++) {
                    dbData = sleepData.get(i);
//                    if (Commont.isDebug)Log.e(TAG, "时间对比--B-----" + currentDate + "=====" + dbData.getDate());
                    if (!dbData.getDate().equals(currentDate)) {
                        dbData.setUploadGD(1);// 只要不是当天的数据,都把本地状态设为"已上传"
                        dbData.save();//因为当天数据还有可能不停的更新和上传
                    }

                }
                break;
            case STATE_RATE:
                for (int i = 0; i < rateData.size(); i++) {
                    dbData = rateData.get(i);
//                    if (Commont.isDebug)Log.e(TAG, "时间对比--C-----" + currentDate + "=====" + dbData.getDate());
                    if (!dbData.getDate().equals(currentDate)) {
                        dbData.setUploadGD(1);// 只要不是当天的数据,都把本地状态设为"已上传"
                        dbData.save();//因为当天数据还有可能不停的更新和上传
                    }

                }
                break;
            case STATE_BP:
                for (int i = 0; i < bpData.size(); i++) {
                    dbData = bpData.get(i);
//                    if (Commont.isDebug)Log.e(TAG, "时间对比--D-----" + currentDate + "=====" + dbData.getDate());
                    if (!dbData.getDate().equals(currentDate)) {
                        dbData.setUploadGD(1);// 只要不是当天的数据,都把本地状态设为"已上传"
                        dbData.save();//因为当天数据还有可能不停的更新和上传
                    }
                }
                break;
            case STATE_HVR:
                for (int i = 0; i < hrvList.size(); i++) {
                    B31HRVBean b31HRVBean = hrvList.get(i);
                    if (!b31HRVBean.getDateStr().equals(currentDate)) {
                        b31HRVBean.setUpdata(true);
                        b31HRVBean.save();
                    }
                }
                break;
            case STATE_SPO2:
                for (int i = 0; i < spo2List.size(); i++) {
                    B31Spo2hBean b31Spo2hBean = spo2List.get(i);
                    if (!b31Spo2hBean.getDateStr().equals(currentDate)) {
                        b31Spo2hBean.setUpdata(true);
                        b31Spo2hBean.save();
                    }
                }
                break;
        }
    }


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
        return (hour > 9 ? hour : "0" + hour) + ":" + (minute > 9 ? minute : "0" + minute)
                + ":" + (seconds > 9 ? seconds : "0" + seconds);
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
//        if (Commont.isDebug)Log.e(TAG,"-------上传结束啦");
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

    /**
     * TimeData 拼接成 时分
     *
     * @param time
     * @return
     */
    String getTimeStr(TimeData time) {
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
        return stringHour + "-" + stringMinute;
    }

}
