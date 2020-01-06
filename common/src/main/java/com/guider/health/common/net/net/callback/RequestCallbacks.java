package com.guider.health.common.net.net.callback;

import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by jett on 2018/6/6.
 */

public class RequestCallbacks implements Callback<ResponseBody>{
    private final IRequest REQUEST;
    private final ISuccess SUCCESS;
    private final IFailure FAILURE;
    private final IError ERROR;
    public RequestCallbacks(IRequest request, ISuccess success, IFailure failure, IError error) {
        this.REQUEST = request;
        this.SUCCESS = success;
        this.FAILURE = failure;
        this.ERROR = error;
    }

    //有响应的情况
    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

        try {
            if(response.isSuccessful()){
                if(call.isExecuted()){
                    if(SUCCESS!=null){
                        SUCCESS.onSuccess(response.body().string());
                    }
                }
            }else{
                if(ERROR!=null){
                    ERROR.onError(response.code(),response.message());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //服务器没有响应, 没有连接到服务器
    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {
        if(FAILURE!=null){
            FAILURE.onFailure();
        }
        if(REQUEST!=null){
            REQUEST.onRequestEnd();
        }
    }
}
