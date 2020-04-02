package com.guider.health.adapter;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.guider.health.all.R;
import com.guider.health.common.core.DateUtil;
import com.guider.health.common.core.HearRate;
import com.guider.health.common.core.NetIp;
import com.guider.health.common.core.UserManager;
import com.guider.health.common.device.Unit;
import com.guider.health.common.net.app.Httper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 移动版血压仪
 */
public class ViewHolderOfEcg6 extends BaseResultViewHolder {

    TextView tvName;

    public ViewHolderOfEcg6(ViewGroup viewGroup) {
        super(viewGroup);
        tvName = view.findViewById(R.id.name);
        tvName.setText(getName());
        view.setBackgroundResource(R.mipmap.long_icon4);
        //        View rootView = view.findViewById(R.id.root);
    }

    @Override
    protected int getRequestNum() {
        return 1;
    }

    @Override
    protected int getLayout() {
        return R.layout.item_ecg;
    }

    @Override
    protected String getName() {
        return getContext().getResources().getString(R.string.ecg);
    }

    @Override
    void setResult(Object result) {
        if (hasData()) {
            Unit unit = new Unit();
            HearRate instance = HearRate.getInstance();
            TooLazyToWrite.setTextView(view, R.id.xinlv, instance.getHeartRate() + unit.heart);
            TooLazyToWrite.setTextView(view, R.id.xinlvjiankang, instance.getStr_PNN50());
            TooLazyToWrite.setTextView(view, R.id.yali, instance.getStr_SDNN());
            TooLazyToWrite.setTextView(view, R.id.pilao, instance.getStr_LFHF());
        }
    }

    @Override
    public void request(final RequestCallback callback) {
        if (requestStatus == REQUEST_STATUS_OK) {
            return;
        }
        try {
            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            format.setTimeZone(TimeZone.getTimeZone("GMT+0"));
            String dateString = DateUtil.dateToString(DateUtil.utcNow(), "yyyy-MM-dd'T'HH:mm:ss'Z'");
            Log.i("haix", "获取到时间: " + dateString);
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("accountId", UserManager.getInstance().getAccountId());
            jsonObject.put("createTime", dateString);
            jsonObject.put("deviceCode", HearRate.getInstance().getDeviceAddress());
            jsonObject.put("testTime", dateString);
            jsonObject.put("diaDescribes", HearRate.getInstance().getDiaDescribe());
            jsonObject.put("healthLight", HearRate.getInstance().getHealthLight());
            jsonObject.put("healthLightOriginal", HearRate.getInstance().getHealthLightOriginal());
            jsonObject.put("heartRate", HearRate.getInstance().getHeartRate());
            jsonObject.put("heartRateLight", HearRate.getInstance().getHeartRateLight());
            jsonObject.put("lfhf", HearRate.getInstance().getLFHF());
            jsonObject.put("nn50", HearRate.getInstance().getNN50());
            jsonObject.put("pervousSystemBalanceLight", HearRate.getInstance().getPervousSystemBalanceLight());
            jsonObject.put("nervousSystemBalanceLight", HearRate.getInstance().getStr_SDNN());
            jsonObject.put("pnn50", HearRate.getInstance().getPNN50());
            jsonObject.put("predictedSymptoms", HearRate.getInstance().getPredictedSymptoms());
            jsonObject.put("stressLight", HearRate.getInstance().getStressLight());
            jsonObject.put("sdnn", HearRate.getInstance().getSDNN());
            jsonObject.put("state", "");
            jsonArray.add(jsonObject);

            final RequestBody requestBody =
                    RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonArray.toJSONString());
            Httper.getInstance().post(NetIp.BASE_URL_apihd, "api/v1/heartstate", requestBody, new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response.body() == null) {
                            callback(-1, callback);
                            return;
                        }
                        String result = response.body().string();
                        Log.i("haix", "|_____ecg请求: " + result);
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasData() {
        return HearRate.getInstance().isTag();
    }
}
