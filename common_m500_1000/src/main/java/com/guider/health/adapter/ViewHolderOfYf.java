package com.guider.health.adapter;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.guider.health.all.R;
import com.guider.health.common.core.DateUtil;
import com.guider.health.common.core.HeartPressYf;
import com.guider.health.common.core.NetIp;
import com.guider.health.common.core.UserManager;
import com.guider.health.common.device.Unit;
import com.guider.health.common.net.app.Httper;

import java.text.SimpleDateFormat;
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
public class ViewHolderOfYf extends BaseResultViewHolder {

    TextView tvName;

    public ViewHolderOfYf(ViewGroup viewGroup) {
        super(viewGroup);
        tvName = view.findViewById(R.id.name);
        tvName.setText(getName());
        view.setBackgroundResource(R.mipmap.long_icon2);
        //        View rootView = view.findViewById(R.id.root);
    }

    @Override
    protected int getRequestNum() {
        return 2;
    }

    @Override
    protected int getLayout() {
        return R.layout.item_yf;
    }

    @Override
    protected String getName() {
        return "动脉硬化指数";
    }

    @Override
    void setResult(Object result) {
        if (hasData()) {
            Unit unit = new Unit();
            HeartPressYf instance = HeartPressYf.getInstance();
            TooLazyToWrite.setTextView(view, R.id.shousuoya, instance.getSbp() + unit.bp );
            TooLazyToWrite.setTextView(view, R.id.shuzhangya, instance.getDbp() + unit.bp );
            TooLazyToWrite.setTextView(view, R.id.dongmai, instance.getASI());
            TooLazyToWrite.setTextView(view, R.id.bmi, instance.getBMI());
            TooLazyToWrite.setTextView(view, R.id.maibo, instance.getHeart() +  unit.heart );
            TooLazyToWrite.setTextView(view, R.id.status, instance.getCardShowStr());
        }
    }

    @Override
    public void request(final RequestCallback callback) {
        if (requestStatus == REQUEST_STATUS_OK) {
            return;
        }
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            format.setTimeZone(TimeZone.getTimeZone("GMT+0"));
            String dateString = DateUtil.dateToString(DateUtil.utcNow(), "yyyy-MM-dd'T'HH:mm:ss'Z'");


            Log.i("haix", "获取到时间: " + dateString);

            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("accountId", UserManager.getInstance().getAccountId());
            jsonObject.put("createTime", dateString);
            jsonObject.put("deviceCode", HeartPressYf.getInstance().getDeviceAddress());
            jsonObject.put("heartBeat", Integer.parseInt(HeartPressYf.getInstance().getHeart()));
            jsonObject.put("dbp", Integer.parseInt(HeartPressYf.getInstance().getDbp()));
            jsonObject.put("sbp", Integer.parseInt(HeartPressYf.getInstance().getSbp()));
            jsonObject.put("state", "0");
            jsonObject.put("testTime", dateString);
            jsonArray.add(jsonObject);

            final RequestBody requestBody =
                    RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonArray.toJSONString());

            Httper.getInstance().post(NetIp.BASE_URL_apihd, "api/v1/bloodpressure", requestBody, new Callback<ResponseBody>() {
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

            jsonArray = new JSONArray();
            jsonObject = new JSONObject();
            jsonObject.put("accountId", UserManager.getInstance().getAccountId());
            jsonObject.put("createTime", dateString);
            jsonObject.put("hr", Integer.parseInt(HeartPressYf.getInstance().getHeart()));
            jsonObject.put("deviceCode", HeartPressYf.getInstance().getDeviceAddress());
            jsonObject.put("dbp", Integer.parseInt(HeartPressYf.getInstance().getDbp()));
            jsonObject.put("sbp", Integer.parseInt(HeartPressYf.getInstance().getSbp()));
            jsonObject.put("asi", Integer.parseInt(HeartPressYf.getInstance().getASI()));
            jsonObject.put("avi", "0");
            jsonObject.put("api", "0");
            jsonObject.put("testTime", dateString);
            jsonArray.add(jsonObject);

            final RequestBody requestBody2 =
                    RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonArray.toJSONString());

            Httper.getInstance().post(NetIp.BASE_URL_apihd, "api/v1/arteriosclerosis", requestBody2, new Callback<ResponseBody>() {
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
                        if (code >= 0) {
                            callback(0, callback);
                        } else {
                            callback(-1, callback);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    callback(-1, callback);
                }
            });
        } catch (Exception e) {

        }
    }

    @Override
    public boolean hasData() {
        return HeartPressYf.getInstance().isTag();
    }
}
