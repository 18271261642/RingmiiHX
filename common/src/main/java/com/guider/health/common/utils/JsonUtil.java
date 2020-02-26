package com.guider.health.common.utils;

import com.alibaba.fastjson.JSON;

public class JsonUtil {
    public static String toStr(Object object) {
        return JSON.toJSONString(object);
    }

    public static <T> T fromStr(String str, Class<T> clz) {
        return JSON.parseObject(str, clz);
    }
}
