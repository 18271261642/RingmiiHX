package com.guider.health.adapter;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.guider.health.all.R;
import com.guider.health.common.core.DateUtil;
import com.guider.health.common.core.HearRate12;
import com.guider.health.common.core.NetIp;
import com.guider.health.common.core.UserManager;
import com.guider.health.common.device.Unit;
import com.guider.health.common.net.app.Httper;
import com.orhanobut.logger.Logger;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Set;
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
public class ViewHolderOfEcg12 extends BaseResultViewHolder {

    TextView tvName;

    public ViewHolderOfEcg12(ViewGroup viewGroup) {
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
        return R.layout.item_ecg_12;
    }

    @Override
    protected String getName() {
        return "心电";
    }

    @Override
    void setResult(Object result) {
        if (hasData()) {
            Unit unit = new Unit();
            HearRate12 instance = HearRate12.getInstance();
            TooLazyToWrite.setTextView(view , R.id.xinlv , instance.getHeartRate()+ unit.heart );
            Set<String> analysisList = instance.getAnalysisList();
            if (analysisList != null && analysisList.size()>0) {
                Iterator<String> iterator = analysisList.iterator();
                String jielun = "";
                while (iterator.hasNext()) {
                    jielun += iterator.next() + "\n";
                }
                TooLazyToWrite.setTextView(view , R.id.jielun ,jielun);
            }
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
            jsonObject.put("deviceCode", HearRate12.getInstance().getDeviceAddress());
            jsonObject.put("testTime", dateString);

            /**
             * 幅值单位 ?
             * 基准值  ?
             */
            jsonObject.put("leadNumber", 12);
            jsonObject.put("breathRate", 0);
            jsonObject.put("curveDescription", "I,II,III,avR,avL,avF,v1,v2,v3,v4,v5,v6");
            jsonObject.put("gain", 1);
            jsonObject.put("ecgData", HearRate12.getInstance().getEcgData());
            jsonObject.put("imgUrl", "");
            jsonObject.put("baseLineValue", 0);
            jsonObject.put("samplingFrequency", 500);
            jsonObject.put("avm", 1000);

            jsonObject.put("heartRate", HearRate12.getInstance().getHeartRate());
            jsonObject.put("analysisResults", HearRate12.getInstance().getAnalysisResults());
            jsonObject.put("paxis", HearRate12.getInstance().getPaxis());
            jsonObject.put("prInterval", HearRate12.getInstance().getPrInterval());
            jsonObject.put("qrsAxis", HearRate12.getInstance().getQrsAxis());
            jsonObject.put("qrsDuration", HearRate12.getInstance().getQrsDuration());
            jsonObject.put("qtc", HearRate12.getInstance().getQtc());
            jsonObject.put("qtd", HearRate12.getInstance().getQtd());
            jsonObject.put("rrInterval", HearRate12.getInstance().getRrInterval());
            jsonObject.put("rv5", HearRate12.getInstance().getRv5());
            jsonObject.put("sv1", HearRate12.getInstance().getSv1());
            jsonObject.put("taxis", HearRate12.getInstance().getTaxis());
            jsonObject.put("mask", 65536);
            jsonArray.add(jsonObject);

            Log.i("daooooooo", jsonArray.toJSONString());
            final RequestBody requestBody =
                    RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonArray.toJSONString());
            Httper.getInstance().post(NetIp.BASE_URL_apihd , "api/v1/ecg" , requestBody , new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response.body() == null) {
                            callback(-1, callback);
                            return;
                        }
                        String result = response.body().string();
                        Logger.i(result);
                        Log.i("haix", "|_____ecg请求: " + result);
                        JSONObject jo = JSON.parseObject(result);
                        int code = jo.getInteger("code");
                        callback(code , callback);
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback(-1 , callback);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Logger.i(t.getMessage());
                    callback(-1 , callback);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasData() {
        return HearRate12.getInstance().isTag();
    }
}
