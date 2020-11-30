package com.guider.gps.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guider.baselib.utils.ApiCoroutinesCallBack
import com.guider.baselib.utils.ParseJsonData
import com.guider.health.apilib.GuiderApiUtil
import com.guider.health.apilib.enums.ThirdAccountType
import kotlinx.coroutines.launch

/**
 * @Package: com.guider.gps.viewModel
 * @ClassName: ThirdAccountViewModel
 * @Description: 第三方账户viewModel
 * @Author: hjr
 * @CreateDate: 2020/11/30 16:33
 * Copyright (C), 1998-2020, GuiderTechnology
 */
class ThirdAccountViewModel : ViewModel() {

    var loading = MutableLiveData<Boolean>()
    var checkStatusResult = MutableLiveData<List<ThirdAccountType>>()

    fun getThirdStatusData(accountId: Int) {
        viewModelScope.launch {
            ApiCoroutinesCallBack.resultParse(block = {
                val result = GuiderApiUtil.getApiService().getThirdStatusData(accountId)
                if (result != "null") {
                    val thirdAccountList = ParseJsonData.parseJsonDataList<ThirdAccountType>(
                            result, ThirdAccountType::class.java)
                    checkStatusResult.postValue(thirdAccountList)
                } else checkStatusResult.postValue(arrayListOf())
            }, onError = {
                checkStatusResult.postValue(arrayListOf())
            })
        }
    }
}