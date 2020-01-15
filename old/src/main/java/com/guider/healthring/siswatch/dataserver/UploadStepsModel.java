package com.guider.healthring.siswatch.dataserver;

import android.content.Context;
import com.guider.healthring.net.OkHttpObservable;
import com.guider.healthring.rxandroid.CommonSubscriber;
import com.guider.healthring.rxandroid.SubscriberOnNextListener;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Map;

/**
 * Created by Administrator on 2018/1/10.
 */

public class UploadStepsModel {

    public void modelUpdate(Context mContext,String url, Map<String, Object> objectMap,SubscriberOnNextListener<String> subscriberOnNextListener){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId", objectMap.get("userId"));
            jsonObject.put("deviceCode",objectMap.get("deviceCode"));
            jsonObject.put("stepNumber",objectMap.get("stepNums"));
            jsonObject.put("distance",objectMap.get("distance"));
            jsonObject.put("calories",objectMap.get("calories"));
            jsonObject.put("timeLen","0");
            jsonObject.put("date",objectMap.get("currentDate"));
            jsonObject.put("status",objectMap.get("targetStatus"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        CommonSubscriber commonSubscriber = new CommonSubscriber(subscriberOnNextListener, mContext);
        OkHttpObservable.getInstance().getData(commonSubscriber,url,jsonObject.toString());
    }


}
