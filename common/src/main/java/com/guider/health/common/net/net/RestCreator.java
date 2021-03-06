package com.guider.health.common.net.net;




import com.guider.health.common.net.app.ConfigKeys;
import com.guider.health.common.net.app.ProjectInit;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
//import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by jett on 2018/6/6.
 */

public final class RestCreator {
    /**
     * 产生一个全局的Retrofit客户端
     */
    private static final class RetrofitHolder{
        private static final String BASE_URL= ProjectInit.getConfiguration(ConfigKeys.API_HOST);
        private static final Retrofit RETROFIT_CLIENT=new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(OKHttpHolder.OK_HTTP_CLIENT)
                .build();
//        .addConverterFactory(GsonConverterFactory.create())

    }
    //为了扩展用, 在RETROFIT_CLIENT的 .client方法中绑定了这个OKHttpHolder
    //todo 猜想是OKHttp的配置会覆盖默认的Retrofit的配置
    private static final class OKHttpHolder{
        private static final int TIME_OUT=60;
        private static final OkHttpClient OK_HTTP_CLIENT=new OkHttpClient.Builder()
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                .addInterceptor(new RetrofitLogInterceptor())
                .build();


    }

    //提供接口让调用者得到retrofit对象
    private static final class RestServiceHolder{
        private static final RestService REST_SERVICE=RetrofitHolder.RETROFIT_CLIENT.create(RestService.class);
    }
    /**
     * 获取对象
     */
    public static RestService getRestService(){
        return RestServiceHolder.REST_SERVICE;
    }

}








