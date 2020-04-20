package com.guider.glu_phone.view;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.guider.glu_phone.R;
import com.guider.health.apilib.ApiUtil;
import com.guider.health.common.core.DateUtil;
import com.guider.health.common.core.Glucose;
import com.guider.health.common.core.NetIp;
import com.guider.health.common.core.UserManager;
import com.guider.health.common.device.IUnit;
import com.guider.health.common.device.standard.StandardCallback;
import com.guider.health.common.net.net.RestService;
import com.guider.health.common.net.net.RetrofitLogInterceptor;
import com.guider.health.common.utils.UnitUtil;

import java.io.IOException;
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
 * Created by haix on 2019/7/18.
 */

public class MeasureResult extends GlocoseFragment{


    private View view;
    private int requestCount = 2;


    @Override
    public void onPause() {
        super.onPause();
        try {
            if (mWakeLock != null) {
                mWakeLock.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.measure_result, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String dateString = DateUtil.dateToString(DateUtil.utcNow(), "yyyy-MM-dd HH:mm");

        ((TextView)view.findViewById(R.id.foot_time)).setText(dateString);

        if (Glucose.getInstance().getFoodTime() == 0){
            ((TextView)view.findViewById(R.id.foot_time)).setText(getResources().getText(R.string.measure_3_qingchen));
        }else if (Glucose.getInstance().getFoodTime() == 2){
            ((TextView)view.findViewById(R.id.foot_time)).setText(getResources().getText(R.string.measure_3_canhou));
        }


        Glucose.getInstance().startStandardRun(new StandardCallback() {
            @Override
            public void onResult(boolean isFinish) {

                if (MeasureResult.this.isDetached()) {
                    return;
                }

                if (!isFinish){
                    return;
                }

                if (Glucose.getInstance().get_glucose() == 0){
                    view.findViewById(R.id.glu_result_arrow).setVisibility(View.GONE);
                    ((TextView) view.findViewById(R.id.glu_result)).setTextColor(Color.parseColor("#FFFFFF"));
                    ((TextView) view.findViewById(R.id.glu_result_danwei)).setTextColor(Color.parseColor("#FFFFFF"));
                }else if (Glucose.getInstance().get_glucose() > 0){
                    ((TextView) view.findViewById(R.id.glu_result)).setTextColor(Color.parseColor("#FF0000"));
                    view.findViewById(R.id.glu_result_arrow).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.glu_result_arrow).setBackgroundResource(R.mipmap.up);
                    ((TextView) view.findViewById(R.id.glu_result_danwei)).setTextColor(Color.parseColor("#FF0000"));
                }else if (Glucose.getInstance().get_glucose() < 0){
                    ((TextView) view.findViewById(R.id.glu_result)).setTextColor(Color.parseColor("#FF0000"));
                    view.findViewById(R.id.glu_result_arrow).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.glu_result_arrow).setBackgroundResource(R.mipmap.down);
                    ((TextView) view.findViewById(R.id.glu_result_danwei)).setTextColor(Color.parseColor("#FF0000"));
                }


                if (Glucose.getInstance().get_pulse() == 0){
                    view.findViewById(R.id.pulse_result_arrow).setVisibility(View.INVISIBLE);
                    ((TextView) view.findViewById(R.id.pulse_result)).setTextColor(Color.parseColor("#FFFFFF"));
                    ((TextView) view.findViewById(R.id.pulse_result_danwei)).setTextColor(Color.parseColor("#FFFFFF"));
                }else if (Glucose.getInstance().get_glucose() > 0){
                    ((TextView) view.findViewById(R.id.pulse_result)).setTextColor(Color.parseColor("#FF0000"));
                    view.findViewById(R.id.pulse_result_arrow).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.pulse_result_arrow).setBackgroundResource(R.mipmap.up);
                    ((TextView) view.findViewById(R.id.pulse_result_danwei)).setTextColor(Color.parseColor("#FF0000"));
                }else if (Glucose.getInstance().get_glucose() < 0){
                    ((TextView) view.findViewById(R.id.pulse_result)).setTextColor(Color.parseColor("#FF0000"));
                    view.findViewById(R.id.pulse_result_arrow).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.pulse_result_arrow).setBackgroundResource(R.mipmap.down);
                    ((TextView) view.findViewById(R.id.pulse_result_danwei)).setTextColor(Color.parseColor("#FF0000"));
                }


                if (Glucose.getInstance().get_speed() == 0){
                    view.findViewById(R.id.speed_result_arrow).setVisibility(View.INVISIBLE);
                    ((TextView) view.findViewById(R.id.speed_result)).setTextColor(Color.parseColor("#FFFFFF"));
                    ((TextView) view.findViewById(R.id.speed_result_danwei)).setTextColor(Color.parseColor("#FFFFFF"));
                }else if (Glucose.getInstance().get_glucose() > 0){
                    ((TextView) view.findViewById(R.id.speed_result)).setTextColor(Color.parseColor("#FF0000"));
                    view.findViewById(R.id.speed_result_arrow).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.speed_result_arrow).setBackgroundResource(R.mipmap.up);
                    ((TextView) view.findViewById(R.id.speed_result_danwei)).setTextColor(Color.parseColor("#FF0000"));
                }else if (Glucose.getInstance().get_glucose() < 0){
                    ((TextView) view.findViewById(R.id.speed_result)).setTextColor(Color.parseColor("#FF0000"));
                    view.findViewById(R.id.speed_result_arrow).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.speed_result_arrow).setBackgroundResource(R.mipmap.down);
                    ((TextView) view.findViewById(R.id.speed_result_danwei)).setTextColor(Color.parseColor("#FF0000"));
                }


            }
        });

        try {
            ((TextView) view.findViewById(R.id.head_title)).setText(getResources().getText(R.string.measure_result));
            view.findViewById(R.id.head_back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AndroidInterface.measureDid = true;
                    popTo(AttentionInfo.class, false);
                    _mActivity.finish();
                }
            });

            // 根据国别处理血糖单位
            IUnit iUnit = UnitUtil.getIUnit(this._mActivity);
            double value = iUnit.getGluShowValue(Glucose.getInstance().getGlucose(), 2);
            ((TextView) view.findViewById(R.id.glu_result)).setText(value + ""); // 值
            ((TextView) view.findViewById(R.id.glu_result_danwei)).setText(iUnit.getGluUnit()); // 单位

            // ((TextView) view.findViewById(R.id.glu_result)).setText(Glucose.getInstance().getGlucose()+"");
            ((TextView) view.findViewById(R.id.pulse_result)).setText(Glucose.getInstance().getPulse());
            ((TextView) view.findViewById(R.id.hemoglobin_result)).setText(Glucose.getInstance().getHemoglobin());
            ((TextView) view.findViewById(R.id.oxygen_result)).setText(Glucose.getInstance().getOxygenSaturation());
            ((TextView) view.findViewById(R.id.speed_result)).setText(Glucose.getInstance().getSpeed());
            ((TextView) view.findViewById(R.id.result1)).setText(Glucose.getInstance().getFingerTemperature());
            ((TextView) view.findViewById(R.id.result2)).setText(Glucose.getInstance().getFingerHumidity());
            ((TextView) view.findViewById(R.id.result3)).setText(Glucose.getInstance().getEnvironmentTemperature());
            ((TextView) view.findViewById(R.id.result4)).setText(Glucose.getInstance().getEnvironmentHumidity());
            ((TextView) view.findViewById(R.id.result5)).setText(Glucose.getInstance().getBatteryLevel()+ " %");


            tryShowDialog();
            glu();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void tryShowDialog(){
        if(requestCount == 2){
            showDialog();
        }
    }

    private void tryHideDialog(){
        requestCount --;
        if (requestCount == 0){

            hideDialog();
        }
    }

    /*
    private static final int TIME_OUT = 60;
    private OkHttpClient OK_HTTP_CLIENT = new OkHttpClient.Builder()
            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
            .addInterceptor(new RetrofitLogInterceptor())
            .build();
    */
    public void glu() {

        bloodOxygens();
        try {

            String dateString = DateUtil.dateToString(DateUtil.utcNow(), "yyyy-MM-dd'T'HH:mm:ss'Z'");


// 血糖
            float glu = (new Double(Glucose.getInstance().getGlucose())).floatValue();
            // 血红蛋白
            float hemoglobin = (new Double(Glucose.getInstance().getHemoglobin())).floatValue();
            // 血流速度
            float bloodSpeed = (new Double(Glucose.getInstance().getSpeed())).floatValue();
            Log.i("haix", "饭后时间: " + Glucose.getInstance().getFoodTime());
            String bsTime = "RANDOM";
            if (Glucose.getInstance().getFoodTime() == 0) {
                //空腹
                bsTime = "FPG";
            }
            if (Glucose.getInstance().getFoodTime() == 2) {
                bsTime = "TWOHPPG";
            }

            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("accountId", UserManager.getInstance().getAccountId());
            jsonObject.put("createTime", dateString);
            jsonObject.put("bs", glu);
            jsonObject.put("hemoglobin", hemoglobin);
            jsonObject.put("bloodSpeed", bloodSpeed);
            jsonObject.put("bsTime", bsTime);
            jsonObject.put("deviceCode", "11:11:11:11:11:11");
            jsonObject.put("state", "0");
            jsonObject.put("testTime", dateString);
            jsonArray.add(jsonObject);




            final RequestBody requestBody =
                    RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonArray.toJSONString());


            Retrofit retrofit = new Retrofit.Builder().baseUrl(NetIp.BASE_URL_apihd).client(ApiUtil.getOkHttpClient()).build();
            RestService restService = retrofit.create(RestService.class);
            Call<ResponseBody> call = restService.postOnlyBody("api/v1/bloodsugar", requestBody);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {


                        String result = response.body().string();

                        Log.i("haix", "|_____glu请求: " + result);

                        JSONObject jo = JSON.parseObject(result);
                        int code = jo.getInteger("code");


                        if (code >= 0) {


                        }else{
                            _mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(_mActivity, "血糖数据没有上传成功", Toast.LENGTH_SHORT);
                                }
                            });
                        }





                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    tryHideDialog();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    _mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(_mActivity, "血糖数据没有上传成功", Toast.LENGTH_SHORT);
                        }
                    });

                    tryHideDialog();

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void bloodOxygens() {
        try {



            String dateString = DateUtil.dateToString(DateUtil.utcNow(), "yyyy-MM-dd'T'HH:mm:ss'Z'");

            Log.i("haix", "double值: " + Glucose.getInstance().getGlucose());



            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("accountId", UserManager.getInstance().getAccountId());
            jsonObject.put("bo", Integer.parseInt(Glucose.getInstance().getOxygenSaturation()));
            jsonObject.put("createTime", dateString);
            jsonObject.put("deviceCode", "11:11:11:11:11:11");
            jsonObject.put("heartBeat", Integer.parseInt(Glucose.getInstance().getPulse()));
            jsonObject.put("id", UserManager.getInstance().getId());
            jsonObject.put("state", "0");
            jsonObject.put("testTime", dateString);
            jsonArray.add(jsonObject);


            final RequestBody requestBody =
                    RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonArray.toJSONString());


            Retrofit retrofit = new Retrofit.Builder().baseUrl(NetIp.BASE_URL_apihd).client(ApiUtil.getOkHttpClient()).build();
            RestService restService = retrofit.create(RestService.class);
            Call<ResponseBody> call = restService.postOnlyBody("api/v1/bloodoxygen", requestBody);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {


                        String result = response.body().string();

                        Log.i("haix", "|_____bp请求: " + result);

                        JSONObject jo = JSON.parseObject(result);
                        int code = jo.getInteger("code");


                        if (code >= 0) {


                        }else{
                            _mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(_mActivity, "血氧数据没有上传成功", Toast.LENGTH_SHORT);
                                }
                            });
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    tryHideDialog();

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    _mActivity.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(_mActivity, "血氧数据没有上传成功", Toast.LENGTH_SHORT);

                        }
                    });
                    tryHideDialog();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onBackPressedSupport() {
        // return super.onBackPressedSupport();
        _mActivity.finish();
        return true;
    }
}
