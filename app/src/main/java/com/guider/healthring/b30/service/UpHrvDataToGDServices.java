package com.guider.healthring.b30.service;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.guider.healthring.B18I.b18iutils.B18iUtils;
import com.guider.healthring.Commont;
import com.guider.healthring.MyApp;
import com.guider.healthring.b30.bean.B30HalfHourDao;
import com.guider.healthring.b30.bean.ResultVoNew;
import com.guider.healthring.b31.model.B31HRVBean;
import com.guider.healthring.b31.model.B31Spo2hBean;
import com.guider.healthring.bean.WXUserBean;
import com.guider.healthring.h9.utils.H9TimeUtil;
import com.guider.healthring.siswatch.utils.WatchUtils;
import com.guider.healthring.util.OkHttpTool;
import com.guider.healthring.util.SharedPreferencesUtils;
import com.guider.healthring.w30s.utils.W30BasicUtils;
import com.veepoo.protocol.model.datas.HRVOriginData;
import com.veepoo.protocol.model.datas.Spo2hOriginData;
import com.veepoo.protocol.model.datas.TimeData;

import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpHrvDataToGDServices extends AsyncTask<Void, Void, Void> {
    private final String TAG = "UpHrvDataToGDServices";
    private final String Base_Url = "http://47.92.218.150:8082/api/v1/";

    private final int STATE_HRV = 1;
    private final int STATE_SPO2 = 2;
    private String deviceCode;
    private String userId;
    private String upDataSysTime;
    private String timeDifference;
    private String phone, wechartJson;
    int accountId = 0;

    // 方法1：onPreExecute（）
    // 作用：执行 线程任务前的操作
    // 注：根据需求复写
    @Override
    protected void onPreExecute() {
        deviceCode = (String) SharedPreferencesUtils.readObject(MyApp.getInstance(), Commont.BLEMAC);
        userId = (String) SharedPreferencesUtils.readObject(MyApp.getInstance(), "userId");
        phone = "" + (String) SharedPreferencesUtils.readObject(MyApp.getContext(), "phoneNumber");
        wechartJson = "" + (String) SharedPreferencesUtils.readObject(MyApp.getContext(), "wechartJson");


        upDataSysTime = (String) SharedPreferencesUtils.getParam(MyApp.getInstance(), "UpGdServices", "2017-11-02 15:00:00");
        timeDifference = H9TimeUtil.getTimeDifferencesec(upDataSysTime, B18iUtils.getSystemDataStart());

        upDataSysTime = (String) SharedPreferencesUtils.getParam(MyApp.getInstance(), "UpGdServices", "2017-11-02 15:00:00");
        timeDifference = H9TimeUtil.getTimeDifferencesec(upDataSysTime, B18iUtils.getSystemDataStart());
    }

    // 方法2：doInBackground（）
    // 作用：接收输入参数、执行任务中的耗时操作、返回 线程任务执行的结果
    // 注：必须复写，从而自定义线程任务
    @Override
    protected Void doInBackground(Void... voids) {
        // Task被取消了，马上退出循环
        if (isCancelled()) return null;
        Log.e(TAG, "-------上传开始啦");
        try {
            findNotUploadData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



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

                String where = "bleMac = ? and dateStr = ?";
                //HRV
                hrvList = LitePal.where(where, deviceCode, WatchUtils.obtainFormatDate(0)).find(B31HRVBean.class);

                //血氧
                spo2List = LitePal.where(where, deviceCode, WatchUtils.obtainFormatDate(0)).find(B31Spo2hBean.class);

                if (hrvList != null && !hrvList.isEmpty())
                    Log.e(TAG, "未上传数据条数 HRV: " + hrvList.size());
                if (spo2List != null && !spo2List.isEmpty())
                    Log.e(TAG, "未上传数据条数 SPO2: " + spo2List.size());

                if ((hrvList != null && !hrvList.isEmpty())
                        || (spo2List != null && !spo2List.isEmpty())) {
                    Log.e(TAG, "数据库中存在数据------开始登陆账户-----去上传==（HRV-->SPO2）");

                    //登陆到盖得后台
                    loginGdServices();
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
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 登陆到盖得后台
     */
    private void loginGdServices() {
//        Map<String, String> params = new HashMap<>();
//        params.put("", "");
//        Log.e(TAG, "游客注册或者登陆参数：" + params.toString());
//        OkHttpTool.getInstance().doRequest(Base_Url + "login/onlyphone?phone=" + phone,
//                null, this, loginHttpResult, false);
//

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
            MyApp.getInstance().setUploadDateGDHrv(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行

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
                            MyApp.getInstance().setUploadDateGDHrv(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行

                            Log.e(TAG, "游客注册或者登陆失败---上传返回" + result);
                            Intent intent = new Intent("com.example.bozhilun.android.b30.service.UploadServiceGD");
                            MyApp.getInstance().sendBroadcast(intent);
                            onCancelled();
                        }


                    } else {
                        MyApp.getInstance().setUploadDateGDHrv(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行

                        Log.e(TAG, "游客注册或者登陆失败---上传返回" + result);
                        Intent intent = new Intent("com.example.bozhilun.android.b30.service.UploadServiceGD");
                        MyApp.getInstance().sendBroadcast(intent);
                        onCancelled();
                    }
                } else {
                    MyApp.getInstance().setUploadDateGDHrv(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行

                    Log.e(TAG, "游客注册或者登陆失败---上传返回" + result);
                    Intent intent = new Intent("com.example.bozhilun.android.b30.service.UploadServiceGD");
                    MyApp.getInstance().sendBroadcast(intent);
                    onCancelled();
                }

            } else {
                MyApp.getInstance().setUploadDateGDHrv(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行

                Log.e(TAG, "游客注册或者登陆失败---上传返回" + result);
                Intent intent = new Intent("com.example.bozhilun.android.b30.service.UploadServiceGD");
                MyApp.getInstance().sendBroadcast(intent);
                onCancelled();
            }


        }
    };


    private boolean upHrv = false;
    private boolean upSpo2 = false;

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 0x01:
                    if (upHrv && upSpo2) {

                        changeUpload(STATE_HRV);
                        changeUpload(STATE_SPO2);

                        handler.sendEmptyMessageDelayed(0x02, 1000);
                    } else {
                        handler.sendEmptyMessageDelayed(0x01, 10000);
                    }
                    break;
                case 0x02:
                    MyApp.getInstance().setUploadDateGDHrv(false);
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
        boolean uploadingGD = MyApp.getInstance().isUploadDateGDHrv();
        if (!uploadingGD) {
            MyApp.getInstance().setUploadDateGDHrv(true);// 正在上传数据,写到全局,保证同时只有一个本服务在运行
            upHrv = false;
            upSpo2 = false;


            getHrvToServices();

            getSpo2ToServices();

            if (handler != null) handler.sendEmptyMessageDelayed(0x01, 20000);
        }
    }

    private void getSpo2ToServices() {
        if (spo2List != null && !spo2List.isEmpty()) {
            Log.e(TAG, "一共有 " + spo2List.size() + " 天SPO2数据");
            for (int i = (spo2List.size() - 1); i >= 0; i--) {
                /**
                 * {dateStr='2019-06-21', bleMac='C4:F3:AF:CB:D9:12'
                 */
                B31Spo2hBean b31Spo2hBean = spo2List.get(i);
                if (b31Spo2hBean != null) {
                    String date = b31Spo2hBean.getDateStr();
                    String spo2hOriginData = b31Spo2hBean.getSpo2hOriginData();
                    Log.e(TAG, "第 " + i + " 天前的Spo2数据   日期是：" + b31Spo2hBean.getDateStr());
                    if (!WatchUtils.isEmpty(spo2hOriginData) && !WatchUtils.isEmpty(date)) {
                        //为倒序，i == 0  最后一个为今天，今天的数据要做处理
                        if (i == 0) setUpSpo2(spo2hOriginData, true);
                        else setUpSpo2(spo2hOriginData, false);
                    } else {
                        if (i == 0) {
                            upSpo2 = true;
                            Log.e(TAG, "SPO2数据异常");
                        }
                    }
                }

            }
        } else {
            Log.e(TAG, "没有SPO2数据");
            upSpo2 = true;
        }
    }


    private void getHrvToServices() {

        if (hrvList != null && !hrvList.isEmpty()) {
            Log.e(TAG, "一共有 " + hrvList.size() + " 天HRV数据");
            for (int i = (hrvList.size() - 1); i >= 0; i--) {

                /**
                 * {dateStr='2019-06-21', bleMac='C4:F3:AF:CB:D9:12'
                 */
                B31HRVBean b31HRVBean = hrvList.get(i);
                if (b31HRVBean != null) {
                    String dateStr = b31HRVBean.getDateStr();
                    /**
                     * [HRVOriginData
                     * {date='2019-06-21',
                     * mTime=TimeData [2019-06-21 00:00:00],
                     * currentPackNumber=1,
                     * allCurrentPackNumber=420,
                     * rate='66,67,60,63,62,84,87,72,72,0',
                     * hrvValue=50,
                     * tempOne=9}
                     */
                    String hrvDataStr = b31HRVBean.getHrvDataStr();
                    Log.e(TAG, "第 " + i + " 天前的HRV数据   日期是：" + b31HRVBean.getDateStr());
                    if (!WatchUtils.isEmpty(hrvDataStr) && !WatchUtils.isEmpty(dateStr)) {

                        if (i == 0) setUpHrv(hrvDataStr, true);
                        else setUpHrv(hrvDataStr, false);
                    } else {
                        if (i == 0) {
                            upHrv = true;
                            Log.e(TAG, "HRV数据异常");
                        }
                    }
                }

            }
        } else {
            Log.e(TAG, "没有HRV数据");
            upHrv = true;
        }
    }


    /**
     * 上传HRV到后台
     *
     * @param hrvDataStr
     * @param isToday
     */
    private void setUpHrv(String hrvDataStr, boolean isToday) {

        if (!WatchUtils.isEmpty(hrvDataStr)) {
            HRVOriginData hrvOriginData = new Gson().fromJson(hrvDataStr, HRVOriginData.class);
            TimeData timeData = hrvOriginData.getmTime();
            String hrvDates = obtainDate(timeData);
            int hrvValue = hrvOriginData.getHrvValue();
            String rate = hrvOriginData.getRate();
            if (WatchUtils.isEmpty(rate)) return;
            Log.e(TAG, "==   " + rate);
            if (rate.length() - 2 != 0) {
                int[] hrvDatas = new int[rate.length() - 2];
                for (int i = 1; i < rate.length() - 1; i++) {
                    char ch = rate.charAt(i);
                    hrvDatas[i - 1] = ch;
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
                Date dateStart = W30BasicUtils.stringToDate(hrvDates.replace(":", "-"), "yyyy-MM-dd HH-mm-ss");
                String iso8601Timestamp = WatchUtils.getISO8601Timestamp(dateStart);
                params.put("testTime", iso8601Timestamp);
                JSONObject json = new JSONObject(params);
                List<JSONObject> list = new ArrayList<>();
                list.add(json);

                String upPatch = Base_Url + "hrv";
                if (isToday) {
                    OkHttpTool.getInstance().doRequest(upPatch, list.toString(), this, new OkHttpTool.HttpResult() {
                        @Override
                        public void onResult(String result) {
                            if (!WatchUtils.isEmpty(result)) {
//                                        Log.e(TAG, "最后心率上传返回啊: " + result);
                                upHrv = true;
                            } else {
                                MyApp.getInstance().setUploadDateGDHrv(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行
                            }
                        }
                    });
                } else {
                    OkHttpTool.getInstance().doRequest(upPatch, list.toString(), this, new OkHttpTool.HttpResult() {
                        @Override
                        public void onResult(String result) {
                            if (!WatchUtils.isEmpty(result)) {
//                                        Log.e(TAG, "最后心率上传返回啊: " + result);
                            } else {
                                MyApp.getInstance().setUploadDateGDHrv(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行
                            }
                        }
                    });
                }
            } else {
                Log.e(TAG, "没有HRV数据");
                upHrv = true;
            }


        } else {
            if (isToday) {
                upHrv = true;
//                        Log.e(TAG, " 心率异常为小于等于0啦: " + result);
            }
        }


    }


    /**
     * 上传SPO2到后台
     *
     * @param spo2hOriginData
     * @param isToday
     */
    private void setUpSpo2(String spo2hOriginData, boolean isToday) {

        /**
         * Spo2hOriginData
         * {allPackNumner='420'
         * currentPackNumber='1
         * 'date='2019-06-21',
         * mTime=TimeData [2019-06-21 00:00:00],
         * heartValue=90,
         * sportValue=0,
         * oxygenValue=98,
         * apneaResult=0,
         * isHypoxia=0,
         * hypoxiaTime=0,
         * hypopnea=0,
         * cardiacLoad=32,
         * hRVariation=50,
         * stepValue=0,
         * respirationRate=16,
         * temp1=12},
         */
        if (!WatchUtils.isEmpty(spo2hOriginData)) {
            Spo2hOriginData spo2hOriginData1 = new Gson().fromJson(spo2hOriginData, Spo2hOriginData.class);

            TimeData timeData = spo2hOriginData1.getmTime();
            String spo2Dates = obtainDate(timeData);
            int oxygenValue = spo2hOriginData1.getOxygenValue();

            Map<String, Object> params = new HashMap<>();
            params.put("accountId", accountId);
            params.put("deviceCode", deviceCode);
            params.put("state", "");
            params.put("bo", oxygenValue);
            params.put("createTime", WatchUtils.getISO8601Timestamp(new Date()));
            //testTime  =   2019-06-20 17-00-00
            //2019-06-20T08:40:41Z
            Date dateStart = W30BasicUtils.stringToDate(spo2Dates.replace(":", "-"), "yyyy-MM-dd HH-mm-ss");
            String iso8601Timestamp = WatchUtils.getISO8601Timestamp(dateStart);
            params.put("testTime", iso8601Timestamp);
            JSONObject json = new JSONObject(params);
            List<JSONObject> list = new ArrayList<>();
            list.add(json);

            String upPatch = Base_Url + "bloodoxygen";

            if (isToday) {
                OkHttpTool.getInstance().doRequest(upPatch, list.toString(), this, new OkHttpTool.HttpResult() {
                    @Override
                    public void onResult(String result) {
                        if (!WatchUtils.isEmpty(result)) {
//                                        Log.e(TAG, "最后心率上传返回啊: " + result);
                            upSpo2 = true;
                        } else {
                            MyApp.getInstance().setUploadDateGDHrv(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行
                        }
                    }
                });
            } else {
                OkHttpTool.getInstance().doRequest(upPatch, list.toString(), this, new OkHttpTool.HttpResult() {
                    @Override
                    public void onResult(String result) {
                        if (!WatchUtils.isEmpty(result)) {
//                                        Log.e(TAG, "最后心率上传返回啊: " + result);
                        } else {
                            MyApp.getInstance().setUploadDateGDHrv(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行
                        }
                    }
                });
            }

        } else {
            if (isToday) {
                upSpo2 = true;
//                        Log.e(TAG, " 心率异常为小于等于0啦: " + result);
            }
        }


    }


    /**
     * 改变本地数据库上传数据状态
     */
    private void changeUpload(int type) {
        String currentDate = WatchUtils.getCurrentDate();
        switch (type) {
            case STATE_HRV:
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
        MyApp.getInstance().setUploadDateGDHrv(false);// 正在上传数据,写到全局,保证同时只有一个本服务在运行
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
}
