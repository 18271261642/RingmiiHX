package com.guider.gps.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import com.guider.baselib.utils.ParseJsonData
import com.guider.baselib.utils.USER
import com.guider.health.apilib.GuiderApiUtil
import com.guider.health.apilib.bean.AbnormalRingMsgListBean
import com.guider.health.apilib.bean.HealthDataSimpleBean
import com.guider.health.apilib.utils.MMKVUtil

/**
 * @Package: com.guider.gps.viewModel
 * @ClassName: PagingViewModel
 * @Description: page的viewModel
 * @Author: hjr
 * @CreateDate: 2020/12/17 16:00
 * Copyright (C), 1998-2020, GuiderTechnology
 */
class PagingViewModel : ViewModel() {

    var loading = MutableLiveData<Boolean>()
    var emptyData = MutableLiveData<Boolean>()

    val listData =
            Pager(PagingConfig(20)) {
                object : PagingSource<Int, AbnormalRingMsgListBean>() {
                    override suspend fun load(params: LoadParams<Int>): LoadResult<Int,
                            AbnormalRingMsgListBean> {
                        return try {
                            var key = params.key ?: 1
                            val accountId = MMKVUtil.getInt(USER.USERID,0)
                            val data = GuiderApiUtil.getHDApiService()
                                    .getAbnormalMsgList(accountId, -1, key, 20)
                            val dataList = data.map {
                                val dataBean = ParseJsonData.parseJsonAny<HealthDataSimpleBean>(
                                        it.warningContent)
                                val state = dataBean.state
                                val warningContent = dataBean.warningContent
                                dataBean.state = warningContent
                                dataBean.state2 = state
                                it.dataBean = dataBean
                                it
                            }
                            if (key == 1 && data.isNullOrEmpty()) {
                                emptyData.postValue(true)
                            } else emptyData.postValue(false)
                            LoadResult.Page(dataList, null,
                                    if (data.isNullOrEmpty()) null else ++key)
                        } catch (e: Exception) {
                            LoadResult.Error(e)
                        }
                    }
                }
            }
                    .flow
                    .cachedIn(viewModelScope)
                    .asLiveData(viewModelScope.coroutineContext)

}