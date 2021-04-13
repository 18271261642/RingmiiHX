package com.guider.health.apilib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;

import com.google.gson.Gson;

import java.io.File;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiUtil {
    private static OkHttpClient mOkHttpClient;
    private static Context context;
    static String mac;
    private static Map<String, String> mHeaders;

    static {
        // 访问域名处理
        // ApiConsts.API_HOST = BuildConfig.APIURL;
        // ApiConsts.API_HOST_HD = BuildConfig.APIHDURL;
    }

    public static OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    public static Map<String, String> getHeaders() {
        return mHeaders;
    }

    public static void init(Context context, String mac, String apiUtl, String apiHdUrl) {
        ApiConsts.API_HOST = apiUtl;
        ApiConsts.API_HOST_HD = apiHdUrl;

        ApiUtil.context = context.getApplicationContext();
        ApiUtil.mac = mac;
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES)
                .writeTimeout(2, TimeUnit.MINUTES);
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(loggingInterceptor);
        }
        RequestHead requestHead = new RequestHead(context);
        mHeaders = requestHead.getHeaders();
        builder.addInterceptor(requestHead);
        mOkHttpClient = builder.build();
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
//        X509TrustManager manager;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//            manager = new X509ExtendedTrustManager() {
//                @Override
//                public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket) {
//
//                }
//
//                @Override
//                public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket) {
//
//                }
//
//                @Override
//                public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine) {
//
//                }
//
//                @Override
//                public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine) {
//
//                }
//
//                public X509Certificate[] getAcceptedIssuers() {
//                    return new X509Certificate[0];
//                }
//
//                @Override
//                public void checkClientTrusted(X509Certificate[] certs, String authType) {
//                }
//
//                @Override
//                public void checkServerTrusted(X509Certificate[] certs, String authType) {
//                }
//            };
//        } else {
//            manager = new X509TrustManager() {
//                public X509Certificate[] getAcceptedIssuers() {
//                    return new X509Certificate[0];
//                }
//
//                @Override
//                public void checkClientTrusted(X509Certificate[] certs, String authType) {
//                }
//
//                @Override
//                public void checkServerTrusted(X509Certificate[] certs, String authType) {
//                }
//            };
//
//        }
//        TrustManager[] trustAllCerts = new TrustManager[]{manager};
//        SSLContext sc;
//        try {
//            sc = SSLContext.getInstance("TLS");
//            // trustAllCerts信任所有的证书
//            sc.init(null, trustAllCerts, new SecureRandom());
//            //忽略ssl证书,android10及以上的版本就不用了
////            if (Build.VERSION.SDK_INT < 29) {
//                builder.sslSocketFactory(sc.getSocketFactory(),manager);
////            }
//            builder.hostnameVerifier(new HostnameVerifier() {
//                @SuppressLint("BadHostnameVerifier")
//                @Override
//                public boolean verify(String hostname, SSLSession session) {
//                    return true;
//                }
//            });
//        } catch (NoSuchAlgorithmException | KeyManagementException e) {
//            e.printStackTrace();
//        }
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
            Gson gson = new Gson()
                    .newBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                    .registerTypeAdapter(Date.class, new DateTypeAdapter())
                    .create();
            ResultConverterFactory rcf = ResultConverterFactory.create(gson);
            return new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(rcf)
                    .client(builder.build())
                    .build()
                    .create(clz);
        } else {
            return new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(ResultConverterFactory.create(
                            new Gson().newBuilder()
                                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                                    .registerTypeAdapter(Date.class, new DateTypeAdapterNormal())
                                    .create()))
                    .client(builder.build())
                    .build()
                    .create(clz);
        }
    }

    private static <I> I createApi(String url, Class<I> clz, boolean needTimeZone,
                                   boolean needFormat, int timmeout) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(loggingInterceptor);
        }
        builder.readTimeout(timmeout * 2, TimeUnit.SECONDS)
                .connectTimeout(timmeout, TimeUnit.SECONDS)
                .writeTimeout(timmeout * 2, TimeUnit.SECONDS);
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
            Gson gson = new Gson()
                    .newBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                    .registerTypeAdapter(Date.class, new DateTypeAdapter())
                    .create();
            ResultConverterFactory rcf = ResultConverterFactory.create(gson);
            return new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(rcf)
                    .client(builder.build())
                    .build()
                    .create(clz);
        } else {
            return new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(ResultConverterFactory.create(
                            new Gson().newBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                                    .registerTypeAdapter(Date.class,
                                            new DateTypeAdapterNormal()).create()))
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
        return createApi(ApiConsts.API_HOST_HD, clz, needTimeZone);
    }

    public static <I> I createApi(Class<I> clz, boolean needTimeZone) {
        return createApi(ApiConsts.API_HOST, clz, needTimeZone);
    }

    public static <I> I createRingApi(Class<I> clz) {
        return createApi(ApiConsts.API_RING, clz, false, false);
    }

    public static Context getContext() {
        return context;
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
        RequestBody requestFile = RequestBody.create(MediaType.parse("application/otcet-stream"),
                file);
        MultipartBody.Part body = MultipartBody.Part.createFormData(
                "file", file.getName(), requestFile);
        createApi(IGuiderApi.class).uploadFile(body).enqueue(callBack);
    }
}
