package com.guider.gps.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guider.baselib.utils.APP_ID_LINE
import com.guider.baselib.utils.ApiCoroutinesCallBack
import com.guider.baselib.utils.ParseJsonData
import com.guider.baselib.utils.USER
import com.guider.health.apilib.GuiderApiUtil
import com.guider.health.apilib.enums.ThirdAccountType
import com.guider.health.apilib.utils.MMKVUtil
import kotlinx.coroutines.launch

/**
 * @Package: com.guider.gps.viewModel
 * @ClassName: ThirdAccountViewModel
 * @Description: 第三方账户viewModel
 * @Author: hjr
 * @CreateDate: 2020/11/30 16:33
 * Copyright (C), 1998-2020, GuiderTechnology
 */
class AccountViewModel : ViewModel() {

    var loading = MutableLiveData<Boolean>()
    var checkStatusResult = MutableLiveData<List<ThirdAccountType>>()
    var lineAccountResult = MutableLiveData<Boolean>()
    var unBindLineResult = MutableLiveData<Boolean>()

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

    fun checkLineAccountIsNewAccount(appId: String, openId: String) {
        viewModelScope.launch {
            ApiCoroutinesCallBack.resultParse(onStart = {
                loading.postValue(true)
            }, block = {
                val result = GuiderApiUtil.getApiService().checkLineAccountIsNewAccount(
                        appId, openId)
                lineAccountResult.postValue(result == "true")
            }, onRequestFinish = {
                loading.postValue(false)
            })
        }
    }

    fun unBindLine() {
        val accountId = MMKVUtil.getInt(USER.USERID)
        if (accountId == 0) return
        viewModelScope.launch {
            ApiCoroutinesCallBack.resultParse(block = {
                val result = GuiderApiUtil.getApiService().unBindLine(APP_ID_LINE, accountId)
                unBindLineResult.postValue(result == "true")
            })
        }
    }
}