package com.guider.baselib.utils

import android.content.Context
import android.util.Log
import com.guider.baselib.R
import com.guider.baselib.base.BaseApplication
import com.guider.feifeia3.utils.ToastUtil

/**
 * @Package: com.guider.health.apilib
 * @ClassName: ApiCorutinesCallBack
 * @Description: api协程统一回调
 * @Author: hjr
 * @CreateDate: 2020/10/20 9:24
 * Copyright (C), 1998-2020, GuiderTechnology
 */
object ApiCoroutinesCallBack {

    suspend fun resultParse(context: Context?=null, onStart: (() -> Unit)? = null,
                            block: suspend () -> Unit,
                            onError: ((Throwable) -> Unit)? = null,
                            onRequestFinish: (() -> Unit)? = null) {
        try {
            onStart?.invoke()
            block()
        } catch (e: Exception) {
            onError?.invoke(e)
            if (e is RuntimeException){
                if (e.message == "customErrorMsg") {
                    ToastUtil.show(BaseApplication.guiderHealthContext,
                            BaseApplication.guiderHealthContext
                                    .resources.getString(R.string.app_data_request_error))
                } else {
                    ToastUtil.show(BaseApplication.guiderHealthContext, e.message.toString())
                }
            }else {
                ToastUtil.show(BaseApplication.guiderHealthContext,
                        BaseApplication.guiderHealthContext
                                .resources.getString(R.string.app_data_request_error))
            }
            Log.e("GuiderGpsApiError", e.message.toString())
        } finally {
            onRequestFinish?.invoke()
        }
    }

}