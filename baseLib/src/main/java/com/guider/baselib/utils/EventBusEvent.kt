package com.guider.baselib.utils

/**
 * Description TODO
 * Author HJR36
 * Date 2018/4/13 16:50
 */
data class EventBusEvent<T>(var code: String, var data: T? = null)