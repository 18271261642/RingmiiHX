package com.guider.gps.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * @Package: com.guider.gps.viewModel
 * @ClassName: ResetPasswordViewModel
 * @Description: 重置密码的viewModel
 * @Author: hjr
 * @CreateDate: 2020/11/23 12:46
 * Copyright (C), 1998-2020, GuiderTechnology
 */
class ResetPasswordViewModel :ViewModel(){

    var isConfirmAvailable = MutableLiveData<Boolean>()

    fun setConfirmAvailable(flag: Boolean) {
        isConfirmAvailable.postValue(flag)
    }
}