package com.guider.health.apilib;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RequestHead implements Interceptor {
    String acceptLanguage = "en";
    String appid = "--";
    String mac;

    public RequestHead(Context context) {
        if (context == null) {
            return;
        }
        appid = "b3c327c04d8b0471";

        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getCountry().toLowerCase();
        if (language.contains("hk"))
            acceptLanguage = "hk";
        else if (language.contains("tw"))
            acceptLanguage = "tw";
        else if (language.contains("cn"))
            acceptLanguage = "zh";
        else
            acceptLanguage = "en";
        Log.i("RequestHead", language + "lan " + acceptLanguage);
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder requestBuilder = original.newBuilder()
                .addHeader("mac" , ApiUtil.mac)
                .addHeader("appid", appid)
                .addHeader("Accept-Language", acceptLanguage);
        Request request = requestBuilder.build();
        return chain.proceed(request);
    }
}
