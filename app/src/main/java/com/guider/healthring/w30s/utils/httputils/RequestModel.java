package com.guider.healthring.w30s.utils.httputils;

import android.content.Context;

import com.guider.healthring.net.OkHttpObservable;
import com.guider.healthring.rxandroid.CommonSubscriber;
import com.guider.healthring.rxandroid.SubscriberOnNextListener;

/**
 * Created by Administrator on 2018/4/3.
 */

public class RequestModel{

    public void getJSONObjectModelData(String url, Context mContext, String jsonObject,
                                       final SubscriberOnNextListener<String> subscriberOnNextListener,
                                       CustumListener custumListener){
        CommonSubscriber subscriber = new CommonSubscriber(subscriberOnNextListener,mContext);
        subscriber.setCustumListener(custumListener);
        OkHttpObservable.getInstance().getData(subscriber,url,jsonObject);
    }

    public void getPutJSONObjectModelData(String url, Context mContext, String jsonObject,
                                          final SubscriberOnNextListener<String> subscriberOnNextListener,CustumListener custumListener){
        CommonSubscriber subscriber = new CommonSubscriber(subscriberOnNextListener,mContext);
        subscriber.setCustumListener(custumListener);
        OkHttpObservable.getInstance().getPutData(subscriber,url,jsonObject);
    }


    public void getJSONObjectModelDataGet(String url, Context mContext, final SubscriberOnNextListener<String> subscriberOnNextListener,CustumListener custumListener){
        CommonSubscriber subscriber = new CommonSubscriber(subscriberOnNextListener,mContext);
        subscriber.setCustumListener(custumListener);
        OkHttpObservable.getInstance().getNoParamData(subscriber,url);
    }

    public void getJSONObjectModelData(String url, Context mContext, final SubscriberOnNextListener<String> subscriberOnNextListener,CustumListener custumListener){
        CommonSubscriber subscriber = new CommonSubscriber(subscriberOnNextListener,mContext);
        subscriber.setCustumListener(custumListener);
        OkHttpObservable.getInstance().getNoParamData(subscriber,url);
    }


}
