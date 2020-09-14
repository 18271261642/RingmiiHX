package com.guider.gps.view.activity

import androidx.recyclerview.widget.LinearLayoutManager
import com.guider.baselib.base.BaseActivity
import com.guider.baselib.utils.BIND_DEVICE_ACCOUNT_ID
import com.guider.baselib.utils.MMKVUtil
import com.guider.gps.R
import com.guider.gps.adapter.HistoryRecordListAdapter
import com.guider.health.apilib.ApiCallBack
import com.guider.health.apilib.ApiUtil
import com.guider.health.apilib.IGuiderApi
import com.guider.health.apilib.bean.UserPositionListBean
import kotlinx.android.synthetic.main.activity_history_record.*
import retrofit2.Call
import retrofit2.Response

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

    override fun openEventBus(): Boolean {
        return false
    }

    override fun initImmersion() {
        setTitle(resources.getString(R.string.app_history_list))
        showBackButton(R.drawable.ic_back_white)
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
        getUserLocationHistoryData(false)
    }

    /**
     * 得到用户的定位的历史记录
     */
    private fun getUserLocationHistoryData(isShowLoading: Boolean = true) {
        if (isShowLoading)
            showDialog()
        val accountId = MMKVUtil.getInt(BIND_DEVICE_ACCOUNT_ID)
        ApiUtil.createApi(IGuiderApi::class.java, false)
                .userPosition(accountId, page, 20, "", "")
                .enqueue(object : ApiCallBack<List<UserPositionListBean>>(mContext) {
                    override fun onApiResponse(call: Call<List<UserPositionListBean>>?,
                                               response: Response<List<UserPositionListBean>>?) {
                        if (!response?.body().isNullOrEmpty()) {
                            if (isRefresh) refreshLayout.finishRefresh(500)
                            if (isLoadMore) refreshLayout.finishLoadMore(500)
                            if (response?.body()!!.size < 20) {
                                refreshLayout.setEnableLoadMore(false)
                            } else refreshLayout.setEnableLoadMore(true)
                            if (isLoadMore) {
                                historyRecordList.addAll(response.body()!!)
                            } else {
                                historyRecordList = response.body() as ArrayList<UserPositionListBean>
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
                    }

                    override fun onFailure(call: Call<List<UserPositionListBean>>, t: Throwable) {
                        super.onFailure(call, t)
                        if (isRefresh) refreshLayout.finishRefresh()
                        if (isLoadMore) {
                            refreshLayout.finishLoadMore()
                            page--
                        }
                    }

                    override fun onRequestFinish() {
                        if (isShowLoading) dismissDialog()
                        isRefresh = false
                        isLoadMore = false
                    }
                })
    }

    override fun showToolBar(): Boolean {
        return true
    }
}