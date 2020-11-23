package com.guider.gps.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

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
    var nextDataResult = MutableLiveData<String>()

    fun setNextStepAvailable(flag: Boolean) {
        isNextStepAvailable.postValue(flag)
    }

    fun setSendCodeAvailable(flag: Boolean) {
        isSendCodeAvailable.postValue(flag)
    }

    fun entryNext() {

    }

}