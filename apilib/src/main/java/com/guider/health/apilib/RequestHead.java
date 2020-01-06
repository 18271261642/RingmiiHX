package com.guider.health.apilib;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RequestHead implements Interceptor {

    String appid = "--";
    String mac;

    public RequestHead(Context context) {
        if (context == null) {
            return;
        }
        ApplicationInfo info = context.getApplicationInfo();
        String packageName = info.packageName;
        switch (packageName) {
            case "com.guider.health.m1000":
                appid = "9yTIUXvpNgjVAjec";
                break;
            case "com.guider.health.m500":
                appid = "4AQAax2u3FZUHTZM";
                break;
            case "com.guider.health.m100":
                appid = "KzBIa6vCu9KIKt0f";
                break;
            case "com.guider.glu_phone":
                appid = "izwWwFtN8myYBUBq";
                break;
        }
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder requestBuilder = original.newBuilder()
                .addHeader("mac" , ApiUtil.mac)
                .addHeader("appid", appid);
        Request request = requestBuilder.build();
        return chain.proceed(request);
    }
}
