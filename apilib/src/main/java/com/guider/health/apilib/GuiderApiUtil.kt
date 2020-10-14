package com.guider.health.apilib

import android.content.Context
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

/**
 * @Package: com.guider.health.apilib
 * @ClassName: GuiderApiUtil
 * @Description: 接口调用工具类
 * @Author: hjr
 * @CreateDate: 2020/10/10 9:58
 * Copyright (C), 1998-2020, GuiderTechnology
 */
object GuiderApiUtil {

    fun setContextAndMac(mContext: Context?, mac: String?) {
        RetrofitClient.single_Instance.setContextAndMac(mContext, mac)
    }

    fun getApiService(): IGuiderCoroutinesApi {
        return RetrofitClient.single_Instance.getCoroutinesRetrofit().create(
                IGuiderCoroutinesApi::class.java)
    }

    fun getHDApiService(): IGuiderCoroutinesHDApi {
        return RetrofitClient.single_Instance.getCoroutinesRetrofit().create(
                IGuiderCoroutinesHDApi::class.java)
    }

    fun uploadFile(filePath: String):MultipartBody.Part?{
        val file = File(filePath)
        val requestFile = file.asRequestBody("application/otcet-stream".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(
                "file", file.name, requestFile)
    }
}