package com.guider.health.common.net.app;

import com.guider.health.common.net.net.RestService;
import com.guider.health.common.net.net.RetrofitLogInterceptor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class Httper {

    private static final int TIME_OUT = 60;
    private OkHttpClient OK_HTTP_CLIENT = new OkHttpClient.Builder()
            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
            .addInterceptor(new RetrofitLogInterceptor())
            .build();

    protected Httper() {}
    private volatile static Httper instance;
    public static Httper getInstance() {
        if (instance != null) {
            return instance;
        }
        if (instance == null) {
            synchronized (Httper.class) {
                if (instance == null) {
                    instance = new Httper();
                }
            }
        }
        return instance;
    }

    public void post(String baseUrl , String url , RequestBody body , Callback<ResponseBody> callback) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl).client(OK_HTTP_CLIENT).build();
        RestService restService = retrofit.create(RestService.class);
        Call<ResponseBody> call = restService.postOnlyBody(url, body);
        call.enqueue(callback);
    }

    public void post(String baseUrl , String url , HashMap body , Callback<ResponseBody> callback) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl).client(OK_HTTP_CLIENT).build();
        RestService restService = retrofit.create(RestService.class);
        Call<ResponseBody> call = restService.post(url, body);
        call.enqueue(callback);
    }

    public void get(String baseUrl , String url , Map<String, Object> params , Callback<ResponseBody> callback) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl).client(OK_HTTP_CLIENT).build();
        RestService restService = retrofit.create(RestService.class);
        Call<ResponseBody> call = restService.get(url, params);
        call.enqueue(callback);
    }

    public void put(String baseUrl , String url , Map<String, Object> params , Callback<ResponseBody> callback) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl).client(OK_HTTP_CLIENT).build();
        RestService restService = retrofit.create(RestService.class);
        Call<ResponseBody> call = restService.put(url, params);
        call.enqueue(callback);
    }
    public void put(String baseUrl , String url , RequestBody params , Callback<ResponseBody> callback) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl).client(OK_HTTP_CLIENT).build();
        RestService restService = retrofit.create(RestService.class);
        Call<ResponseBody> call = restService.put(url, params);
        call.enqueue(callback);
    }


}
