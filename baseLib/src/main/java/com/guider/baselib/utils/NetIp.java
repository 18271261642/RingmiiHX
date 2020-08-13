package com.guider.baselib.utils;

import com.guider.health.apilib.ApiConsts;

/**
 * Created by haix on 2019/6/25.
 */

public class NetIp {

    public final static String BASE_URL = ApiConsts.API_HOST;
    public final static String BASE_URL_HEALTH = ApiConsts.API_HOST_HD;

    public final static String LIGUOWEI = ApiConsts.API_HOST;

    public final static String BASE_URL_apihd = ApiConsts.API_HOST_HD.substring(0 , ApiConsts.API_HOST_HD.length()-1);
    public final static String BASE_URL_bind = ApiConsts.API_HOST.substring(0 , ApiConsts.API_HOST.length()-1);

}
