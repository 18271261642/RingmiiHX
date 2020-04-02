package com.guider.health.adapter;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.guider.health.all.R;
import com.guider.health.apilib.ApiCallBack;
import com.guider.health.apilib.ApiUtil;
import com.guider.health.apilib.IUserHDApi;
import com.guider.health.apilib.model.hd.HeartBpmMeasure;
import com.guider.health.common.core.DateUtil;
import com.guider.health.common.core.Glucose;
import com.guider.health.common.core.MyUtils;
import com.guider.health.common.core.NetIp;
import com.guider.health.common.core.UserManager;
import com.guider.health.common.device.Unit;
import com.guider.health.common.net.app.Httper;

import java.util.Arrays;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 血糖
 */
public class ViewHolderOfGlu extends BaseResultViewHolder {

    TextView tvName;

    public ViewHolderOfGlu(ViewGroup viewGroup) {
        super(viewGroup);
        tvName = view.findViewById(R.id.name);
        tvName.setText(getName());
        view.setBackgroundResource(R.mipmap.long_icon1);
        //        View rootView = view.findViewById(R.id.root);
    }

    @Override
    protected int getRequestNum() {
        return 3;
    }

    @Override
    protected int getLayout() {
        return R.layout.item_glu;
    }

    @Override
    protected String getName() {
        return getContext().getResources().getString(R.string.blood_sugar);
    }

    @Override
    void setResult(Object result) {
        if (hasData()) {
            Glucose instance = Glucose.getInstance();
            Unit unit = new Unit();
            TooLazyToWrite.setTextView(view, R.id.xuetang, instance.getGlucose() +  unit.bloodSugar);
            TooLazyToWrite.setTextView(view, R.id.xueliusu, instance.getSpeed() +  unit.bloodFlow);
            TooLazyToWrite.setTextView(view, R.id.xuehongdanbai, instance.getHemoglobin() +  unit.hemoglobin);
            TooLazyToWrite.setTextView(view, R.id.xueyang, instance.getOxygenSaturation() +  unit.bloodO2);
            TooLazyToWrite.setTextView(view, R.id.status, instance.getCardShowStr());
        }
    }

    @Override
    public void request(final RequestCallback callback) {
        if (requestStatus == REQUEST_STATUS_OK) {
            return;
        }
        try {
            String dateString = DateUtil.dateToString(DateUtil.utcNow(), "yyyy-MM-dd'T'HH:mm:ss'Z'");
            Log.i("haix", "double值: " + Glucose.getInstance().getGlucose());
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("accountId", UserManager.getInstance().getAccountId());
            jsonObject.put("bo", Integer.parseInt(Glucose.getInstance().getOxygenSaturation()));
            jsonObject.put("createTime", dateString);
            jsonObject.put("deviceCode", Glucose.getInstance().getDeviceAddress());
            jsonObject.put("heartBeat", Integer.parseInt(Glucose.getInstance().getPulse()));
            jsonObject.put("state", "0");
            jsonObject.put("testTime", dateString);
            jsonArray.add(jsonObject);
            final RequestBody requestBody =
                    RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonArray.toJSONString());
            Httper.getInstance().post(NetIp.BASE_URL_apihd, "api/v1/bloodoxygen", requestBody, new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response.body() == null) {
                            callback(-1, callback);
                            return;
                        }
                        String result = response.body().string();
                        Log.i("haix", "|_____bp请求: " + result);
                        JSONObject jo = JSON.parseObject(result);
                        int code = jo.getInteger("code");
                        callback(code, callback);
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback(-1, callback);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    callback(-1, callback);
                }
            });


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
            if (Glucose.getInstance().getFoodTime() >= 1) {
                bsTime = "TWOHPPG";
            }
            jsonArray = new JSONArray();
            jsonObject = new JSONObject();
            jsonObject.put("accountId", UserManager.getInstance().getAccountId());
            jsonObject.put("createTime", dateString);
            jsonObject.put("bs", glu);
            jsonObject.put("hemoglobin", hemoglobin);
            jsonObject.put("bloodSpeed", bloodSpeed);
            jsonObject.put("bsTime", bsTime);
            jsonObject.put("deviceCode", Glucose.getInstance().getDeviceAddress());
            jsonObject.put("state", "0");
            jsonObject.put("testTime", dateString);
            jsonArray.add(jsonObject);


            final RequestBody requestBody2 =
                    RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonArray.toJSONString());

            Httper.getInstance().post(NetIp.BASE_URL_apihd, "api/v1/bloodsugar", requestBody2, new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        String result = response.body().string();
                        Log.i("haix", "|_____glu请求: " + result);
                        JSONObject jo = JSON.parseObject(result);
                        int code = jo.getInteger("code");
                        callback(code, callback);
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback(-1, callback);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    callback(-1, callback);
                }
            });

            HeartBpmMeasure bpm = new HeartBpmMeasure();
            bpm.setAccountId(UserManager.getInstance().getAccountId());
            bpm.setTestTime(new Date());
            bpm.setDeviceCode(MyUtils.getMacAddress());
            bpm.setHb(Integer.valueOf(Glucose.getInstance().getPulse()));
            ApiUtil.createHDApi(IUserHDApi.class).sendHeartBpm(Arrays.asList(bpm)).enqueue(new ApiCallBack<String>(view.getContext()){
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    super.onResponse(call, response);
                    callback(response.code(), callback);
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    super.onFailure(call, t);
                    callback(-1, callback);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasData() {
        return Glucose.getInstance().isTag();
    }
}
