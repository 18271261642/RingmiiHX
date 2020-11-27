package com.guider.gps.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guider.baselib.utils.ApiCoroutinesCallBack
import com.guider.baselib.utils.MyUtils
import com.guider.health.apilib.GuiderApiUtil
import kotlinx.coroutines.launch

/**
 * @Package: com.guider.gps.viewModel
 * @ClassName: ResetPasswordViewModel
 * @Description: 重置密码的viewModel
 * @Author: hjr
 * @CreateDate: 2020/11/23 12:46
 * Copyright (C), 1998-2020, GuiderTechnology
 */
class ResetPasswordViewModel : ViewModel() {

    var isConfirmAvailable = MutableLiveData<Boolean>()
    var nextDataResult = MutableLiveData<Boolean>()
    var loading = MutableLiveData<Boolean>()

    fun setConfirmAvailable(flag: Boolean) {
        isConfirmAvailable.postValue(flag)
    }

    fun changePassword(oldPwd: String, confirmPwd: String, newPwd: String, accountId: Int) {
        viewModelScope.launch {
            ApiCoroutinesCallBack.resultParse(onStart = {
                loading.postValue(true)
            }, block = {
                val map = hashMapOf<String, Any>()
                map["accountId"] = accountId
                map["confirmPwd"] = MyUtils.md5(confirmPwd)
                map["newPwd"] = MyUtils.md5(newPwd)
                map["oldPwd"] = MyUtils.md5(oldPwd)
                val result = GuiderApiUtil.getApiService().changePassword(map)
                nextDataResult.postValue(result == "true")
            }, onRequestFinish = {
                loading.postValue(false)
            })
        }
    }

    fun forgotPassword(telAreaCode: String, phoneNum: String, pwd: String) {
        viewModelScope.launch {
            ApiCoroutinesCallBack.resultParse(onStart = {
                loading.postValue(true)
            }, block = {
                val result = GuiderApiUtil.getApiService().forgotPassword(
                        telAreaCode, phoneNum, MyUtils.md5(pwd), "")
                nextDataResult.postValue(result != "null")
            }, onRequestFinish = {
                loading.postValue(false)
            })
        }
    }
}