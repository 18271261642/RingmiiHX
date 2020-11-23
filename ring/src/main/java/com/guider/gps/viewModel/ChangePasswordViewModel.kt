package com.guider.gps.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * @Package: com.guider.gps.viewModel
 * @ClassName: ChangePasswordViewModel
 * @Description: 修改密码的viewModel
 * @Author: hjr
 * @CreateDate: 2020/11/23 13:08
 * Copyright (C), 1998-2020, GuiderTechnology
 */
class ChangePasswordViewModel :ViewModel() {

    var isConfirmAvailable = MutableLiveData<Boolean>()

    fun setConfirmAvailable(flag: Boolean) {
        isConfirmAvailable.postValue(flag)
    }
}