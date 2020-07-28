package com.guider.health.ecg.view;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.guider.health.apilib.ApiUtil;
import com.guider.health.common.core.Config;
import com.guider.health.common.core.DateUtil;
import com.guider.health.common.core.RouterPathManager;
import com.guider.health.common.core.HearRate;
import com.guider.health.common.core.MyUtils;
import com.guider.health.common.core.NetIp;
import com.guider.health.common.core.UserManager;
import com.guider.health.common.net.net.RestService;
import com.guider.health.ecg.R;
import com.guider.health.ecg.presenter.ECGServiceManager;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by haix on 2019/6/12.
 */


///qiyong
public class ECGDeviceResultAnalysis extends ECGFragment {


    private RotateAnimation myAlphaAnimation;
    private View view;
    public TextView textView;
    public TextView text;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.ecg_measure_analysis, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHomeEvent(view.findViewById(R.id.home), Config.HOME_DEVICE);
        ((TextView) view.findViewById(R.id.title)).setText("心美特测量");

        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               getFragmentManager().popBackStack(RouterPathManager.ECG_PATH, 0);
            }
        });


        view.findViewById(R.id.analysis_go).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ECGServiceManager.getInstance().startResultAnalysis();
                View view = LayoutInflater.from(_mActivity).inflate(R.layout.ecg_dialog, null);
                textView = view.findViewById(R.id.ecg_loading_pic);
                text = view.findViewById(R.id.ecg_loading_text);
                showDialog(view);

                animation();
                myAlphaAnimation.startNow();
            }
        });
    }



    @Override
    public void onAnalysisEnd() {


        Log.i("haix",  "读取文档数据完成");


    }
/*
    private static final int TIME_OUT = 60;
    private OkHttpClient OK_HTTP_CLIENT = new OkHttpClient.Builder()
            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
            .addInterceptor(new RetrofitLogInterceptor())
            .build();
*/
    @Override
    public void onAnalysisResult(String result) {

        if (result == null) {

            return;
        }
        try {
            Date date = new Date(System.currentTimeMillis());

            SimpleDateFormat format = new SimpleDateFormat("2019-09-01'T'10:00:00'Z'", Locale.CHINA);
            format.setTimeZone(TimeZone.getTimeZone("GMT+0"));
            String dateString = DateUtil.dateToString(DateUtil.utcNow(), "yyyy-MM-dd'T'HH:mm:ss'Z'");

            Log.i("haix", "获取到时间: " + dateString);

            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("accountId", UserManager.getInstance().getAccountId());
            jsonObject.put("createTime", dateString);
            jsonObject.put("deviceCode", MyUtils.getMacAddress());
            jsonObject.put("testTime", dateString);
            jsonObject.put("diaDescribes", HearRate.getInstance().getDiaDescribe());
            jsonObject.put("healthLight", HearRate.getInstance().getHealthLight());
            jsonObject.put("healthLightOriginal", HearRate.getInstance().getHealthLightOriginal());
            jsonObject.put("heartRate", HearRate.getInstance().getHeartRate());
            jsonObject.put("heartRateLight", HearRate.getInstance().getHeartRateLight());
            jsonObject.put("lfhf", HearRate.getInstance().getLFHF());
            jsonObject.put("nn50", HearRate.getInstance().getNN50());
            jsonObject.put("pervousSystemBalanceLight", HearRate.getInstance().getPervousSystemBalanceLight());
            jsonObject.put("pnn50", HearRate.getInstance().getPNN50());
            jsonObject.put("predictedSymptoms", HearRate.getInstance().getPredictedSymptoms());
            jsonObject.put("stressLight", HearRate.getInstance().getStressLight());
            jsonObject.put("sdnn", HearRate.getInstance().getSDNN());
            jsonObject.put("state", "");
            jsonArray.add(jsonObject);

            final RequestBody requestBody =
                    RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonArray.toJSONString());

            Retrofit retrofit = new Retrofit.Builder().baseUrl(NetIp.BASE_URL).client(ApiUtil.getOkHttpClient()).build();
            RestService restService = retrofit.create(RestService.class);
            Call<ResponseBody> call = restService.postOnlyBody("api/v1/heartstate", requestBody);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        String result = response.body().string();

                        JSONObject jo = JSON.parseObject(result);
                        int code = jo.getInteger("code");


                        if (code >= 0) {

                            getFragmentManager().popBackStack(Config.HOME_DEVICE, 0);
                            getFragmentManager().beginTransaction().replace(R.id.main_content, new ECGDeviceMeasureResult()).commit();

                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void animation() {
        myAlphaAnimation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);//设置图片动画属性，各参数说明可参照api
        myAlphaAnimation.setRepeatCount(-1);//设置旋转重复次数，即转几圈
        myAlphaAnimation.setDuration(500);//设置持续时间，注意这里是每一圈的持续时间，如果上面设置的圈数为3，持续时间设置1000，则图片一共旋转3秒钟
        myAlphaAnimation.setInterpolator(new LinearInterpolator());//设置动画匀速改变。相应的还有AccelerateInterpolator、DecelerateInterpolator、CycleInterpolator等
        textView.setAnimation(myAlphaAnimation);//设置imageview的动画，也可以myImageView.startAnimation(myAlphaAnimation)
        myAlphaAnimation.setAnimationListener(new Animation.AnimationListener() { //设置动画监听事件
            @Override
            public void onAnimationStart(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                // TODO Auto-generated method stub

            }

            //图片旋转结束后触发事件，这里启动新的activity
            @Override
            public void onAnimationEnd(Animation arg0) {
                // TODO Auto-generated method stub
//                Intent i2 = new Intent(StartActivity.this, ECGMainActivity.class);
//                startActivity(i2);
            }
        });
    }
}
