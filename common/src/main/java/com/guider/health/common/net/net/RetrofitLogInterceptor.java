package com.guider.health.common.net.net;

import android.util.Log;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;

/**
 * Created by haix on 2019/6/21.
 */

public class RetrofitLogInterceptor implements Interceptor {
    public static String TAG = "haix";

    @Override
    public synchronized okhttp3.Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        long startTime = System.currentTimeMillis();
        okhttp3.Response response = chain.proceed(chain.request());
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        okhttp3.MediaType mediaType = response.body().contentType();
        String content = response.body().string();


//        Log.i(TAG, "请求地址：| " + request.url().host());
        printParams(request.body());
        Log.i(TAG, "请求体返回：| Response:" + content);
        Log.i(TAG, "----------请求耗时:" + duration + "毫秒----------");
        return response.newBuilder().body(okhttp3.ResponseBody.create(mediaType, content)).build();
    }


    private void printParams(RequestBody body) {
        Buffer buffer = new Buffer();
        try {
            if (body == null){
                return;
            }
            body.writeTo(buffer);
            Charset charset = Charset.forName("UTF-8");
            MediaType contentType = body.contentType();
//            if (contentType != null) {
//                charset = contentType.charset(UTF_8);
//            }
            String params = buffer.readString(charset);
            Log.i(TAG, "请求参数： | " + params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

