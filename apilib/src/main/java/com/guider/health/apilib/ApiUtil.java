package com.guider.health.apilib;

import android.content.Context;

import com.google.gson.Gson;

import java.io.File;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiUtil {

    private static Context context;
    static String mac;
    public static void init(Context context , String mac) {
        ApiUtil.context = context.getApplicationContext();
        ApiUtil.mac = mac;
    }

    private static <I> I createApi(String url, Class<I> clz, boolean needTimeZone) {
        return createApi(url, clz, needTimeZone, true);
    }
    private static <I> I createApi(String url, Class<I> clz, boolean needTimeZone, boolean needFormat) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(loggingInterceptor);
        }
        builder.readTimeout(5, TimeUnit.MINUTES)
            .connectTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES);
        if (needFormat)
            builder.addInterceptor(new RequestHead(context));
        if (!needFormat) {
             return new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(builder.build())
                    .build()
                    .create(clz);
        } else if (needTimeZone) {
            return new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(ResultConverterFactory.create(
                            new Gson().newBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").registerTypeAdapter(Date.class, new DateTypeAdapter()
                            ).create()))
                    .client(builder.build())
                    .build()
                    .create(clz);
        } else {
            return new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(ResultConverterFactory.create(new Gson().newBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").registerTypeAdapter(Date.class, new DateTypeAdapterNormal()).create()))
                    .client(builder.build())
                    .build()
                    .create(clz);
        }
    }

    public static <I> I createHDApi(Class<I> clz) {
        return createHDApi(clz, true);
    }

    public static <I> I createApi(Class<I> clz) {
        return createApi(clz, true);
    }

    public static <I> I createHDApi(Class<I> clz, boolean needTimeZone) {
        return createApi(Consts.API_HOST_HD, clz, needTimeZone);
    }
    public static <I> I createApi(Class<I> clz, boolean needTimeZone) {
        return createApi(Consts.API_HOST, clz, needTimeZone);
    }

    public static <I> I createRingApi(Class<I> clz) {
        return createApi(Consts.API_RING, clz, false, false);
    }

    /**
     * 上传文件
     *
     * @param context
     * @param filePath 文件路径
     * @param callBack
     */
    public static void uploadFile(Context context, String filePath, ApiCallBack<String> callBack) {
        File file = new File(filePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("application/otcet-stream"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        createApi(IGuiderApi.class).uploadFile(body).enqueue(callBack);
    }
}
