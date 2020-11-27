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
 * @ClassName: ChangePasswordViewModel
 * @Description: 修改密码的viewModel
 * @Author: hjr
 * @CreateDate: 2020/11/23 13:08
 * Copyright (C), 1998-2020, GuiderTechnology
 */
class ChangePasswordViewModel : ViewModel() {

    var isConfirmAvailable = MutableLiveData<Boolean>()
    var nextDataResult = MutableLiveData<Boolean>()
    var loading = MutableLiveData<Boolean>()

    fun setConfirmAvailable(flag: Boolean) {
        isConfirmAvailable.postValue(flag)
    }

    fun entryNext(pwd: String, accountId: Int) {
        viewModelScope.launch {
            ApiCoroutinesCallBack.resultParse(onStart = {
                loading.postValue(true)
            }, block = {
                val result = GuiderApiUtil.getApiService().verifyOldPassword(
                        MyUtils.md5(pwd), accountId)
                nextDataResult.postValue(result == "true")
            }, onRequestFinish = {
                loading.postValue(false)
            })
        }
    }
}