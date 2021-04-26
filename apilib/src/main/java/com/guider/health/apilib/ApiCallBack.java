package com.guider.health.apilib;

import android.content.Context;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiCallBack<T> implements Callback<T> {
    private Context context;

    public ApiCallBack(Context context) {
        this.context = context;
    }

    public ApiCallBack() {
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (!response.isSuccessful() || response.body() == null) {
            onFailure(call, new Throwable(response.message()));
        } else if ("null".equals(response.body())) {
            onApiResponseNull(call , response);
        }else {
            onApiResponse(call, response);
        }
        onRequestFinish();
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        onRequestFinish();
        String msg = t.getMessage();
        if (msg == null || "".equals(msg))
            msg = "Unknown error!";
        if (context != null) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }

    public void onApiResponse(Call<T> call, Response<T> response) {
    }

    public void onApiResponseNull(Call<T> call, Response<T> response) {
        // todo 这里是调用成功了 , 但是没有数据
    }

    public void onRequestFinish() {
        // todo 不管成功与否 , 这个回调都会调用 , 表示请求结束了
    }
}
