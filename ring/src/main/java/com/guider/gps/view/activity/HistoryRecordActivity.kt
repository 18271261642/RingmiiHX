package com.guider.gps.view.activity

import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.guider.baselib.base.BaseActivity
import com.guider.baselib.utils.BIND_DEVICE_ACCOUNT_ID
import com.guider.baselib.utils.MMKVUtil
import com.guider.baselib.utils.StringUtil
import com.guider.baselib.utils.toastShort
import com.guider.gps.R
import com.guider.gps.adapter.HistoryRecordListAdapter
import com.guider.health.apilib.GuiderApiUtil
import com.guider.health.apilib.bean.UserPositionListBean
import kotlinx.android.synthetic.main.activity_history_record.*
import kotlinx.coroutines.launch

/**
 * 历史记录
 */
class HistoryRecordActivity : BaseActivity() {

    override val contentViewResId: Int
        get() = R.layout.activity_history_record

    private lateinit var adapter: HistoryRecordListAdapter
    private var historyRecordList = arrayListOf<UserPositionListBean>()
    private var page = 1
    private var isRefresh = false
    private var isLoadMore = false
    private var entryType = ""

    override fun openEventBus(): Boolean {
        return false
    }

    override fun initImmersion() {
        setTitle(resources.getString(R.string.app_history_list))
        showBackButton(R.drawable.ic_back_white)
        if (intent != null) {
            if (StringUtil.isNotBlankAndEmpty(intent.getStringExtra("entryType"))) {
                entryType = intent.getStringExtra("entryType")!!
            }
        }
    }

    override fun initView() {
        historyRecordRv.layoutManager = LinearLayoutManager(this)
    }

    override fun initLogic() {
        adapter = HistoryRecordListAdapter(mContext!!, historyRecordList)
        historyRecordRv.adapter = adapter
        refreshLayout.setOnRefreshListener {
            page = 1
            isRefresh = true
            getUserLocationHistoryData(false)
        }
        refreshLayout.setOnLoadMoreListener {
            page++
            isLoadMore = true
            getUserLocationHistoryData()
        }
        getUserLocationHistoryData()
    }

    /**
     * 得到用户的定位的历史记录
     */
    private fun getUserLocationHistoryData(isShowLoading: Boolean = true) {
        if (isShowLoading)
            showDialog()
        val accountId = MMKVUtil.getInt(BIND_DEVICE_ACCOUNT_ID)
        lifecycleScope.launch {
            try {
                val resultBean = if (entryType == "locationHistory") {
                    GuiderApiUtil.getApiService()
                            .userPosition(accountId, page, 20, "", "")
                } else {
                    GuiderApiUtil.getApiService()
                            .getProactivelyAddressingList(accountId, page, 20)
                }
                if (isShowLoading) dismissDialog()
                isRefresh = false
                isLoadMore = false
                if (!resultBean.isNullOrEmpty()) {
                    if (isRefresh) refreshLayout.finishRefresh(500)
                    if (isLoadMore) refreshLayout.finishLoadMore(500)
                    if (resultBean.size < 20) {
                        refreshLayout.setEnableLoadMore(false)
                    } else refreshLayout.setEnableLoadMore(true)
                    if (isLoadMore) {
                        historyRecordList.addAll(resultBean)
                    } else {
                        historyRecordList = resultBean as ArrayList<UserPositionListBean>
                    }
                    adapter.setSourceList(historyRecordList)
                } else {
                    if (isRefresh) {
                        refreshLayout.finishRefresh()
                    }
                    if (isLoadMore) {
                        refreshLayout.finishLoadMore()
                        refreshLayout.setEnableLoadMore(false)
                    }
                    if (!isLoadMore) {
                        historyRecordList.clear()
                        adapter.setSourceList(historyRecordList)
                    }
                }
            } catch (e: Exception) {
                if (isShowLoading) dismissDialog()
                isRefresh = false
                isLoadMore = false
                if (isRefresh) refreshLayout.finishRefresh()
                if (isLoadMore) {
                    refreshLayout.finishLoadMore()
                    page--
                }
                toastShort(e.message!!)
            }
        }
    }

    override fun showToolBar(): Boolean {
        return true
    }
}