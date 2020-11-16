package com.guider.health.adapter;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.guider.health.all.R;
import com.guider.health.common.core.DateUtil;
import com.guider.health.common.core.Glucose;
import com.guider.health.common.core.MEDCHECKGlucose;
import com.guider.health.common.core.NetIp;
import com.guider.health.common.core.UserManager;
import com.guider.health.common.device.IUnit;
import com.guider.health.common.net.app.Httper;
import com.guider.health.common.utils.UnitUtil;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * MEDCheck血糖 TODO 添加新设备面板
 */
public class ViewHolderOfMEDCheckGlu extends BaseResultViewHolder {

    TextView tvName;

    public ViewHolderOfMEDCheckGlu(ViewGroup viewGroup) {
        super(viewGroup);
        tvName = view.findViewById(R.id.name);
        tvName.setText(getName());
        view.setBackgroundResource(R.mipmap.long_icon1);
    }

    @Override
    protected int getRequestNum() {
        return 1;
    }

    @Override
    protected int getLayout() {
        return R.layout.item_fora_glu;
    }

    @Override
    protected String getName() {
        return getContext().getResources().getString(R.string.blood_sugar);
    }

    @Override
    void setResult(Object result) {
        if (hasData()) {
            Glucose instance = MEDCHECKGlucose.getMEDCHECKGluInstance();
            IUnit iUnit = UnitUtil.INSTANCE.getIUnit();
            TooLazyToWrite.setTextView(view, R.id.fora_glu, iUnit.getGluShowValue(
                    instance.getGlucose(), 2) +  iUnit.getGluUnit());

            TooLazyToWrite.setTextView(view, R.id.status, instance.getCardShowStr());
        }
    }

    @Override
    public void request(final RequestCallback callback) {
        if (requestStatus == REQUEST_STATUS_OK) {
            return;
        }
        try {
            String dateString = DateUtil.dateToString(DateUtil.utcNow(),
                    "yyyy-MM-dd'T'HH:mm:ss'Z'");
            // 血糖
            float glu = (Double.valueOf(Glucose.getInstance().getGlucose())).floatValue();
            Log.i("haix", "饭后时间: " + Glucose.getInstance().getFoodTime());
            String bsTime = "RANDOM";
            if (Glucose.getInstance().getFoodTime() == 0) {
                //空腹
                bsTime = "FPG";
            }
            if (Glucose.getInstance().getFoodTime() >= 1) {
                bsTime = "TWOHPPG";
            }
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("accountId", UserManager.getInstance().getAccountId());
            jsonObject.put("bs", glu);
            jsonObject.put("hemoglobin", 0);
            jsonObject.put("bloodSpeed", 0);
            jsonObject.put("bsTime", bsTime);
            jsonObject.put("deviceCode", Glucose.getInstance().getDeviceAddress());
            jsonObject.put("state", "0");
            jsonObject.put("testTime", dateString);
            jsonArray.add(jsonObject);
            final RequestBody requestBody2 =
                    RequestBody.create(
                            MediaType.parse("application/json; charset=utf-8"),
                            jsonArray.toJSONString());

            Httper.getInstance().post(NetIp.BASE_URL_apihd,
                    "api/v1/bloodsugar", requestBody2, new Callback<ResponseBody>() {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasData() {
        return MEDCHECKGlucose.getMEDCHECKGluInstance().isTag();
    }
}
