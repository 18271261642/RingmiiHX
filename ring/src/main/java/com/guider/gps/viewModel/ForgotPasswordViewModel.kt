package com.guider.gps.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guider.baselib.utils.ApiCoroutinesCallBack
import com.guider.health.apilib.GuiderApiUtil
import kotlinx.coroutines.launch

/**
 * @Package: com.guider.gps.viewModel
 * @ClassName: ForgotPasswordViewModel
 * @Description: 忘记密码的viewModel
 * @Author: hjr
 * @CreateDate: 2020/11/23 11:05
 * Copyright (C), 1998-2020, GuiderTechnology
 */
class ForgotPasswordViewModel : ViewModel() {

    var isNextStepAvailable = MutableLiveData<Boolean>()
    var isSendCodeAvailable = MutableLiveData<Boolean>()
    var nextDataResult = MutableLiveData<Boolean>()
    var sendCodeResult = MutableLiveData<Boolean>()
    var loading = MutableLiveData<Boolean>()

    fun setNextStepAvailable(flag: Boolean) {
        isNextStepAvailable.postValue(flag)
    }

    fun setSendCodeAvailable(flag: Boolean) {
        isSendCodeAvailable.postValue(flag)
    }

    fun sendCode(phone: String) {
        viewModelScope.launch {
            ApiCoroutinesCallBack.resultParse(onStart = {
                loading.postValue(true)
            }, block = {
                val result = GuiderApiUtil.getApiService().sendLineCode(phone)
                sendCodeResult.postValue(result == "true")
            }, onRequestFinish = {
                loading.postValue(false)
            })
        }
    }

    fun entryNext(telAreaCode: String, phoneNum: String, code: String) {
        viewModelScope.launch {
            ApiCoroutinesCallBack.resultParse(onStart = {
                loading.postValue(true)
            }, block = {
                val result = GuiderApiUtil.getApiService().verifyCode(telAreaCode, phoneNum, code)
                nextDataResult.postValue(result == "true")
            }, onRequestFinish = {
                loading.postValue(false)
            })
        }
    }

}