package com.guider.healthring.net;

import android.text.TextUtils;

import com.guider.healthring.MyApp;
import com.guider.healthring.R;
import com.guider.healthring.util.MyLogUtil;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by thinkpad on 2016/6/27.
 */
public class OkHttpObservable {

    private static OkHttpClient client = new OkHttpClient();

    private static class SingletonHolder {
        // private static String jsonParam;
        private static final OkHttpObservable okHttpObservable = new OkHttpObservable();
    }

    //获取单例
    public static OkHttpObservable getInstance() {
        return SingletonHolder.okHttpObservable;
    }

    public void getData(Subscriber<String> subscriber, final String url, final String jsonParam) {
         MyLogUtil.i("--url-jsonParam->"+url+jsonParam);
        Observable observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {
                try {
                    RequestBody formBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonParam);
                    client.newBuilder().connectTimeout(80, TimeUnit.SECONDS).readTimeout(60,TimeUnit.SECONDS)
                    .writeTimeout(60,TimeUnit.SECONDS);
                    Request request = new Request.Builder()
                            .url(url)
                            .post(formBody)
                            .addHeader("appid","b3c327c04d8b0471")
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            subscriber.onError(e);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String responseUrl = response.body().string();
                            MyLogUtil.i("msg", "--responseUrl-" + responseUrl);
                            if (!TextUtils.isEmpty(responseUrl)) {
                                try {
                                 //   JSONObject jsonObject = new JSONObject(responseUrl);
                                    subscriber.onNext(responseUrl);
                                    subscriber.onCompleted();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    //subscriber.onError(new Exception(MyApp.getApplication().getResources().getString(R.string.fuwuqi)));
                                }
                            } else {
                                //subscriber.onError(new Exception(MyApp.getApplication().getResources().getString(R.string.fuwuqi)));
                            }
                        }
                    });
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
        toSubscribe(observable, subscriber);
    }


    public void getPutData(Subscriber<String> subscriber, final String url, final String jsonParam) {
        MyLogUtil.i("--url-jsonParam->"+url+jsonParam);
        Observable observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {
                try {
                    RequestBody formBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonParam);
                    client.newBuilder().connectTimeout(60, TimeUnit.SECONDS).readTimeout(40,TimeUnit.SECONDS)
                            .writeTimeout(40,TimeUnit.SECONDS);
                    Request request = new Request.Builder()
                            .url(url)
                            //.post(formBody)
                            .put(formBody)
                            .addHeader("appid","b3c327c04d8b0471")
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            subscriber.onError(e);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String responseUrl = response.body().string();
                            MyLogUtil.i("msg", "--responseUrl-" + responseUrl);
                            if (!TextUtils.isEmpty(responseUrl)) {
                                try {
                                    //   JSONObject jsonObject = new JSONObject(responseUrl);
                                    subscriber.onNext(responseUrl);
                                    subscriber.onCompleted();
                                } catch (Exception e) {
                                    e.printStackTrace();

                                }
                            }
                        }
                    });
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
        toSubscribe(observable, subscriber);
    }









    public void getNoParamData(Subscriber<String> subscriber, final String url) {
        Observable observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {
                try {
                    client.newBuilder().connectTimeout(60, TimeUnit.SECONDS).readTimeout(40,TimeUnit.SECONDS)
                            .writeTimeout(40,TimeUnit.SECONDS);
                    Request request = new Request.Builder()
                            .addHeader("appid","b3c327c04d8b0471")
                            .url(url)
                            .get()
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            subscriber.onError(e);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String responseUrl = response.body().string();
                            if (!TextUtils.isEmpty(responseUrl)) {
                                subscriber.onNext(responseUrl);
                                subscriber.onCompleted();
                            } else {
                                subscriber.onError(new Exception(MyApp.getApplication().getResources().getString(R.string.fuwuqi)));
                            }
                        }
                    });
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
        toSubscribe(observable, subscriber);
    }

    private <T> void toSubscribe(Observable<T> o, Subscriber<T> s) {
        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
    }
}
