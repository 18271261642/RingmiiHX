package com.guider.glu.presenter;

import android.app.Activity;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.guider.glu.R;
import com.guider.glu.model.BodyIndex;
import com.guider.glu.model.GLUMeasureModel;
import com.guider.glu.model.PersonalInfo;
import com.guider.glu.view.GLUViewInterface;
import com.guider.health.apilib.ApiUtil;
import com.guider.health.bluetooth.core.BleBluetooth;
import com.guider.health.common.core.Glucose;
import com.guider.health.common.core.NetIp;
import com.guider.health.common.core.UserManager;
import com.guider.health.common.net.NetStateController;
import com.guider.health.common.net.net.RestService;
import com.guider.health.common.net.net.RetrofitLogInterceptor;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by haix on 2019/6/10.
 */

public class GLUServiceManager {

    private static GLUServiceManager serviceManager = new GLUServiceManager();
    private GLUServiceManager(){
    }

    public static GLUServiceManager getInstance(){
        return serviceManager;
    }

    private GLUMeasureModel measureModel = new GLUMeasureModel();


    private WeakReference<GLUViewInterface> fView;

    public void setBodyIndex(String timeMeal, String diabetesType, String glucose, String weight, String height) {
        BodyIndex.getInstance().setTimeMeal(timeMeal);
        BodyIndex.getInstance().setDiabetesType(diabetesType);
        BodyIndex.getInstance().setGlucose(glucose);
        BodyIndex.getInstance().setWeigh(weight);
        BodyIndex.getInstance().setHeight(height);
    }

    public void setViewObject(GLUViewInterface view) {
        this.fView = new WeakReference<>(view);
    }

    //只有phone版本才会设置
    private boolean isPhone = false;
    public void setIsPhone(boolean p){
        isPhone = p;
    }

    public boolean getIsPhone(){
        return isPhone;
    }

    public void generateData(BluetoothGatt gatt,
                             BluetoothGattCharacteristic characteristic) {
        synchronized (this) {
            if (characteristic == null || gatt == null) {
                Log.i("haix", "-------------GLU第一个请求发出去了--------------");
                measureModel.sendDataToDevice();
            } else {
                Log.i("haix", "-------------GLUread后的请求--------------");
                if (characteristic.getUuid().equals(GLUDeviceUUID.SERVICE_UUID)) {
                    measureModel.characteristicUUID(gatt, characteristic);

                } else if (characteristic.getUuid().equals(GLUDeviceUUID.DATA_LINE_UUID)) {
                    measureModel.dataLineUUID(characteristic.getValue());
                }
            }
        }
    }

    public void uploadHeightAndWeight(final int height, final int weight){

    }
    /*
    private static final int TIME_OUT = 60;
    private OkHttpClient OK_HTTP_CLIENT = new OkHttpClient.Builder()
            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
            .addInterceptor(new RetrofitLogInterceptor())
            .build();
    */

    public double getGlucoseResult(){
        return Glucose.getInstance().getGlucose();
    }

    public void startMeassure(){
        measureModel.makeInputData(BodyIndex.getInstance().getTimeMeal(),
                BodyIndex.getInstance().getDiabetesType(),
                BodyIndex.getInstance().getGlucose(),
                BodyIndex.getInstance().getWeigh(),
                BodyIndex.getInstance().getHeight());

        setMeasureListener();
        generateData(null, null);

        startInit();
    }

    public void connectFailed(int status) {
        Log.i("haix", "连接失败1");
        if (fView.get() != null) {
            fView.get().connectFailed(status);
        }
    }

    public void connSuccess() {
        if (fView.get() != null) {
            fView.get().connectSuccess();
        }
    }

    public void startConnect() {
        if (fView.get() != null) {
            fView.get().startConnect();
        }
    }

    CountDownTimer insertFingerTimerCounter;
    public void startInit() {
        if (insertFingerTimerCounter != null) {
            insertFingerTimerCounter.cancel();
        }
        insertFingerTimerCounter = new CountDownTimer(5000, 1000) {
            public void onTick(long millisUntilFinished) {
                if (fView.get() != null) {
                    fView.get().startInitTick(millisUntilFinished / 1000 + "");
                }
            }

            public void onFinish() {
                initTimeOut();
            }
        };
        insertFingerTimerCounter.start();
    }

    public void stopInit() {
        if (insertFingerTimerCounter != null) {
            insertFingerTimerCounter.cancel();
            insertFingerTimerCounter = null;
            if (fView.get() != null) {
                fView.get().stopInit();
            }
        }
    }

    public void initTimeOut() {
        if (fView.get() != null) {
            fView.get().initTimeOut();
        }
    }

    public void connSuccess_phone() {
        startSendByteToMeassure();
    }

    public void toMeasureTime(String time) {
        if (fView.get() != null) {
            fView.get().showMeasureTime(time);
        }
    }

    public void toMeasureRemind(String remind) {
        if (fView.get() != null) {
            fView.get().showMeasureRemind(remind);
        }
    }

    public void reSetList(){
        measureModel.reSetList();
    }

    public void startSendByteToMeassure(){
        measureModel.makeInputData();
        setMeasureListener();
        generateData(null, null);
    }


    private ExecutorService executor = Executors.newFixedThreadPool(3);

    public void startDeviceConnect(Activity activity) {
        measureModel.reSetList();

        BleBluetooth.getInstance().init(activity);

//        new Thread(new GLUScanAndConnectBluetooth(this));
        executor.execute(new GLUScanAndConnectBluetooth(this));
    }

    public void stopDeviceConnect() {

        BleBluetooth.getInstance().nowStopScan();
        BleBluetooth.getInstance().nowCloseBleConn();
    }

    public void setNonbsSet(WeakReference<Activity> activityWeakReference){

        if (activityWeakReference!=null && activityWeakReference.get() != null) {
            if (!NetStateController.isNetworkConnected(activityWeakReference.get())) {

                Toast.makeText(activityWeakReference.get(), activityWeakReference.get().getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
                return;
            }
        }
//
//        "abnormal": false,
//                "accountId": 0,
//                "createTime": "2019-08-12T07:12:07.592Z",
//                "enzyme": false,
//                "id": 0,
//                "phmb": false,
//                "unTake": false,
//                "updateTime": "2019-08-12T07:12:07.592Z",
//                "urea": false,
//                "value": 0


        JSONObject jsonObject = new JSONObject();
        jsonObject.put("abnormal", !BodyIndex.Normal.equals(BodyIndex.getInstance().getDiabetesType()));
        jsonObject.put("accountId", UserManager.getInstance().getAccountId());
        jsonObject.put("createTime", BodyIndex.getInstance().getCreateTime());
        jsonObject.put("enzyme", "1".equals(BodyIndex.getInstance().getGlucosedesesSate()));
        jsonObject.put("id", BodyIndex.getInstance().getId());
        jsonObject.put("phmb", "1".equals(BodyIndex.getInstance().getBiguanidesState()));
        jsonObject.put("unTake", !BodyIndex.getInstance().isEatmedicine());
        jsonObject.put("updateTime", BodyIndex.getInstance().getUpdateTime());
        jsonObject.put("urea", "1".equals(BodyIndex.getInstance().getSulphonylureasState()));
        jsonObject.put("value", BodyIndex.getInstance().getValue());

        final RequestBody requestBody =
                RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toJSONString());

        Retrofit retrofit = new Retrofit.Builder().baseUrl(NetIp.BASE_URL_apihd).client(ApiUtil.getOkHttpClient()).build();
        RestService restService = retrofit.create(RestService.class);
        Call<ResponseBody> call = restService.postOnlyBody("api/v1/nonbs/set", requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    String result = response.body().string();
                    JSONObject jsonObject = JSON.parseObject(result);
                    int code = jsonObject.getInteger("code");
                    if (code >= 0) {
                    } else {
                        //失败
                        String msg = jsonObject.getString("msg");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //operatorCallBack.result(1, "异常");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //operatorCallBack.result(1, "异常");
            }
        });
    }


    public interface CallBack{
        void result(int code, String re);
    }

    public void getNonbsSet(WeakReference<Activity> activityWeakReference, final CallBack operatorCallBack) {
        if (activityWeakReference!=null && activityWeakReference.get() != null) {
            if (!NetStateController.isNetworkConnected(activityWeakReference.get())) {
                Toast.makeText(activityWeakReference.get(), activityWeakReference.get().getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        //00:00:46:79:E5:5A
        HashMap params = new HashMap();
        params.put("accountId", UserManager.getInstance().getAccountId());

        try {
            Retrofit retrofit = new Retrofit.Builder().baseUrl(NetIp.BASE_URL_apihd).client(ApiUtil.getOkHttpClient()).build();
            RestService restService = retrofit.create(RestService.class);
            Call<ResponseBody> call = restService.get("api/v1/nonbs/set", params);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        String result = response.body().string();

                        JSONObject jsonObject = JSON.parseObject(result);

                        int code = jsonObject.getInteger("code");


//                        "abnormal": false,
//                                "accountId": 0,
//                                "createTime": "2019-08-12T05:35:42.439Z",
//                                "enzyme": false,
//                                "id": 0,
//                                "phmb": false,
//                                "unTake": false,
//                                "updateTime": "2019-08-12T05:35:42.439Z",
//                                "urea": false,
//                                "value": 0

                        if (code >= 0) {

                            JSONObject data = jsonObject.getJSONObject("data");

                            //unTake == false 是吃药了
                            BodyIndex.getInstance().setEatmedicine(!data.getBoolean("unTake"));
                            BodyIndex.getInstance().setGlucosedesesSate(!data.getBoolean("enzyme") ? "0" : "1");
                            BodyIndex.getInstance().setBiguanidesState(!data.getBoolean("phmb") ? "0" : "1");
                            BodyIndex.getInstance().setSulphonylureasState(!data.getBoolean("urea") ? "0" : "1");
                            //abnormal == flase 为展示页面
                            BodyIndex.getInstance().setDiabetesType(data.getBoolean("abnormal") ? "" : BodyIndex.Normal);
                            BodyIndex.getInstance().setCreateTime(data.getString("createTime"));
                            BodyIndex.getInstance().setUpdateTime(data.getString("updateTime"));
                            BodyIndex.getInstance().setValue(data.getFloat("value"));
                            BodyIndex.getInstance().setId(data.getInteger("id"));
                            operatorCallBack.result(0, "成功");

                        } else {
                            //失败
                            String msg = jsonObject.getString("msg");
                            operatorCallBack.result(code, msg);
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                        operatorCallBack.result(1, "异常: " + e);
                    }
                }

                @Override
                public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {

                    operatorCallBack.result(1, "失败");
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }


    public void setMeasureListener() {
        measureModel.setMeasureResultListener(new GLUMeasureModel.MeasureResultListener() {
            @Override
            public void measureTime(String time) {
                toMeasureTime(time);
            }

            @Override
            public void measureRemind(String remind) {
                toMeasureRemind(remind);
            }

            @Override
            public void measureFinish() {
                if (fView.get() != null) {
                    fView.get().measureFinish();
                }
            }

            @Override
            public void closeBluetoothConnect() {
                stopDeviceConnect();
                if (fView.get() != null) {
                    fView.get().measureErrorAndCloseBlueConnect();
                }
            }

            @Override
            public void insertFinger() {
                if (fView.get() != null) {
                    fView.get().goMeasure();
                }

            }

            @Override
            public void startTime60Measure() {
                if (fView.get() != null) {
                    fView.get().time60Begin();
                }
            }

            @Override
            public void error(String er) {
                if (fView.get() != null) {
                    fView.get().look_error(er);
                }
            }

            @Override
            public void fingerTime(int time) {
                stopInit();
                if (fView.get() != null) {
                    fView.get().finger_time(time);
                }
            }

        });
    }
}
