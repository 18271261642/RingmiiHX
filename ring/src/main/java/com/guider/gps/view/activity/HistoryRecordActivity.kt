package com.guider.gps.view.activity

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.guider.baselib.base.BaseActivity
import com.guider.baselib.utils.*
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
        emptyData.visibility = View.VISIBLE
    }

    override fun initLogic() {
        adapter = HistoryRecordListAdapter(mContext!!, historyRecordList)
        historyRecordRv.adapter = adapter
        adapter.setListener(object : AdapterOnItemClickListener {
            override fun onClickItem(position: Int) {
                val intent = Intent()
                intent.putExtra("clickLatLng", historyRecordList[position])
                setResult(Activity.RESULT_OK, intent)
                finish()
            }

        })
        refreshLayout.setOnRefreshListener {
            page = 1
            isRefresh = true
            getUserLocationHistoryData(false)
        }
        refreshLayout.setOnLoadMoreListener {
            page++
            isLoadMore = true
            getUserLocationHistoryData(false)
        }
        getUserLocationHistoryData()
    }

    /**
     * 得到用户的定位的历史记录
     */
    private fun getUserLocationHistoryData(isShowLoading: Boolean = true) {
        val accountId = MMKVUtil.getInt(BIND_DEVICE_ACCOUNT_ID)
        lifecycleScope.launch {
            ApiCoroutinesCallBack.resultParse(mContext!!, onStart = {
                if (isShowLoading)
                    showDialog()
            }, block = {
                val resultBean = if (entryType == "locationHistory") {
                    GuiderApiUtil.getApiService()
                            .userPosition(accountId, page, 20, "", "")
                } else {
                    GuiderApiUtil.getApiService()
                            .getProactivelyAddressingList(accountId, page, 20)
                }
                if (!resultBean.isNullOrEmpty()) {
                    emptyData.visibility = View.GONE
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
                        emptyData.visibility = View.VISIBLE
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
            }, onError = {
                if (isLoadMore) {
                    page--
                }
            }, onRequestFinish = {
                if (isShowLoading) dismissDialog()
                if (isRefresh) refreshLayout.finishRefresh()
                if (isLoadMore) {
                    refreshLayout.finishLoadMore()
                }
                isRefresh = false
                isLoadMore = false
            })
        }
    }

    override fun showToolBar(): Boolean {
        return true
    }
}