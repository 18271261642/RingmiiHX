package com.guider.gps.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guider.baselib.utils.ApiCoroutinesCallBack
import com.guider.health.apilib.GuiderApiUtil
import com.guider.health.apilib.bean.DoctorListBean
import kotlinx.coroutines.launch

/**
 * @Package: com.guider.gps.viewModel
 * @ClassName: DoctorListViewModel
 * @Description: 医生列表的viewModel
 * @Author: hjr
 * @CreateDate: 2020/11/10 13:58
 * Copyright (C), 1998-2020, GuiderTechnology
 */
class DoctorListViewModel : ViewModel() {

    var doctorList = MutableLiveData<List<DoctorListBean>>()
    var loading = MutableLiveData<Boolean>()

    fun getDoctorList(accountId: Int) {
        viewModelScope.launch {
            ApiCoroutinesCallBack.resultParse(onStart = {
                loading.postValue(true)
            }, block = {
                val result = GuiderApiUtil.getApiService().getDoctorListData(accountId)
                if (result.isNullOrEmpty()) {
                    doctorList.postValue(arrayListOf())
                } else {
                    doctorList.postValue(result)
                }
            }, onRequestFinish = {
                loading.postValue(false)
            })
        }
    }
}