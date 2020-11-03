package com.guider.gps.view.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.guider.baselib.base.BaseFragment
import com.guider.baselib.utils.*
import com.guider.gps.R
import com.guider.gps.adapter.AbnormalMsgListAdapter
import com.guider.gps.adapter.HealthCareMsgListAdapter
import com.guider.gps.adapter.SystemMsgListAdapter
import com.guider.gps.view.activity.HealthCareMsgDetailActivity
import com.guider.gps.view.activity.RingMsgListActivity
import com.guider.health.apilib.GuiderApiUtil
import com.guider.health.apilib.bean.AbnormalRingMsgListBean
import com.guider.health.apilib.bean.CareMsgListBean
import com.guider.health.apilib.bean.HealthDataSimpleBean
import com.guider.health.apilib.bean.SystemMsgBean
import kotlinx.android.synthetic.main.fragment_ring_msg_list.*
import kotlinx.coroutines.launch

class RingMsgListFragment : BaseFragment() {

    private val ARG_STR = "type"

    companion object {
        fun newInstance(text: String) = RingMsgListFragment().apply {
            arguments = Bundle().apply { putString(ARG_STR, text) }
            TAG = text
        }
    }

    private var abnormalMsgList = arrayListOf<AbnormalRingMsgListBean>()
    private var careMsgList = arrayListOf<CareMsgListBean>()
    private var systemMsgList = arrayListOf<SystemMsgBean>()
    private lateinit var abnormalAdapter: AbnormalMsgListAdapter
    private lateinit var careAdapter: HealthCareMsgListAdapter
    private lateinit var systemAdapter: SystemMsgListAdapter
    private var page = 1
    private var isRefresh = false
    private var isLoadMore = false
    private var msgListType = ""
    private var isInitComplete = false

    override val layoutRes: Int
        get() = R.layout.fragment_ring_msg_list

    override fun initView(rootView: View) {
    }

    override fun initLogic() {
        isInitComplete = true
        msgListRv.layoutManager = LinearLayoutManager(mActivity)
        arguments?.takeIf { it.containsKey(ARG_STR) }?.apply {
            msgListType = getString(ARG_STR)!!
            initData(this, this@RingMsgListFragment)
        }
        refreshLayout.setOnRefreshListener {
            page = 1
            isRefresh = true
            getMsgListData(false)
            //刷新数据时刷新未读数和红点
            when (msgListType) {
                resources.getString(R.string.app_msg_health_notify) -> {
                    (mActivity as RingMsgListActivity).getAbnormalMsgUndoNum()
                }
                resources.getString(R.string.app_msg_care_info) -> {
                    (mActivity as RingMsgListActivity).getCareMsgUndoNum()
                }
            }
        }
        refreshLayout.setOnLoadMoreListener {
            page++
            isLoadMore = true
            getMsgListData(false)
        }
        getMsgListData()
    }

    private fun getMsgListData(isShowLoading: Boolean = true) {
        val accountId = MMKVUtil.getInt(USER.USERID)
        when (msgListType) {
            resources.getString(R.string.app_msg_health_notify) -> {
                getAbnormalMsgListData(accountId, isShowLoading)
            }
            resources.getString(R.string.app_msg_care_info) -> {
                getCareMsgListData(accountId, isShowLoading)
                (mActivity as RingMsgListActivity).resetCareNum()
            }
            resources.getString(R.string.app_msg_system_info) -> {
                getSystemMsgListData(accountId, isShowLoading)
            }
        }

    }

    private fun getAbnormalMsgListData(accountId: Int, isShowLoading: Boolean) {
        lifecycleScope.launch {
            ApiCoroutinesCallBack.resultParse(mActivity, onStart = {
                if (isShowLoading)
                    mActivity.showDialog()
            }, block = {
                val resultBean = GuiderApiUtil.getHDApiService()
                        .getAbnormalMsgList(accountId, -1, page, 20)
                if (!resultBean.isNullOrEmpty()) {
                    emptyData.visibility = View.GONE
                    if (isRefresh) refreshLayout.finishRefresh(500)
                    if (isLoadMore) refreshLayout.finishLoadMore(500)
                    if (resultBean.size < 20) {
                        refreshLayout.setEnableLoadMore(false)
                    } else refreshLayout.setEnableLoadMore(true)
                    val dataList = resultBean.map {
                        val dataBean = ParseJsonData.parseJsonAny<HealthDataSimpleBean>(
                                it.warningContent)
                        val state = dataBean.state
                        val warningContent = dataBean.warningContent
                        dataBean.state = warningContent
                        dataBean.state2 = state
                        it.dataBean = dataBean
                        it
                    }
                    if (isLoadMore) {
                        abnormalMsgList.addAll(dataList)
                    } else {
                        abnormalMsgList = dataList as ArrayList
                    }
                    abnormalAdapter.setSourceList(abnormalMsgList)
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
                        abnormalMsgList.clear()
                        abnormalAdapter.setSourceList(abnormalMsgList)
                    }
                }
            }, onError = {
                if (isLoadMore) {
                    page--
                }
            }, onRequestFinish = {
                if (isShowLoading) mActivity.dismissDialog()
                if (isRefresh) refreshLayout.finishRefresh()
                if (isLoadMore) {
                    refreshLayout.finishLoadMore()
                }
                isRefresh = false
                isLoadMore = false
            })
        }
    }

    private fun getCareMsgListData(accountId: Int, isShowLoading: Boolean) {
        lifecycleScope.launch {
            ApiCoroutinesCallBack.resultParse(mActivity, onStart = {
                if (isShowLoading)
                    mActivity.showDialog()
            }, block = {
                val resultBean = GuiderApiUtil.getHDApiService()
                        .getCareMsgList(accountId, page, 20, -1)
                if (!resultBean.isNullOrEmpty()) {
                    emptyData.visibility = View.GONE
                    if (isRefresh) refreshLayout.finishRefresh(500)
                    if (isLoadMore) refreshLayout.finishLoadMore(500)
                    if (resultBean.size < 20) {
                        refreshLayout.setEnableLoadMore(false)
                    } else refreshLayout.setEnableLoadMore(true)
                    if (isLoadMore) {
                        careMsgList.addAll(resultBean)
                    } else {
                        careMsgList = resultBean as ArrayList
                    }
                    careAdapter.setSourceList(careMsgList)
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
                        careMsgList.clear()
                        careAdapter.setSourceList(careMsgList)
                    }
                }
            }, onError = {
                if (isLoadMore) {
                    page--
                }
            }, onRequestFinish = {
                if (isShowLoading) mActivity.dismissDialog()
                if (isRefresh) refreshLayout.finishRefresh()
                if (isLoadMore) {
                    refreshLayout.finishLoadMore()
                }
                isRefresh = false
                isLoadMore = false
            })
        }
    }

    fun getSystemMsgListData(accountId: Int, isShowLoading: Boolean) {
        if (!isInitComplete) return
        Log.i(TAG,"加载新数据")
        lifecycleScope.launch {
            ApiCoroutinesCallBack.resultParse(mActivity, onStart = {
                if (isShowLoading)
                    mActivity.showDialog()
            }, block = {
                val resultBean = GuiderApiUtil.getApiService().getSystemMsgList(
                        accountId, page, 20)
                if (!resultBean.isNullOrEmpty()) {
                    emptyData.visibility = View.GONE
                    if (isRefresh) refreshLayout.finishRefresh(500)
                    if (isLoadMore) refreshLayout.finishLoadMore(500)
                    if (resultBean.size < 20) {
                        refreshLayout.setEnableLoadMore(false)
                    } else refreshLayout.setEnableLoadMore(true)
                    if (isLoadMore) {
                        systemMsgList.addAll(resultBean)
                    } else {
                        systemMsgList = resultBean as ArrayList
                    }
                    systemAdapter.setSourceList(systemMsgList)
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
                        systemMsgList.clear()
                        systemAdapter.setSourceList(systemMsgList)
                    }
                }
            }, onError = {
                if (isLoadMore) {
                    page--
                }
            }, onRequestFinish = {
                if (isShowLoading) mActivity.dismissDialog()
                if (isRefresh) refreshLayout.finishRefresh()
                if (isLoadMore) {
                    refreshLayout.finishLoadMore()
                }
                isRefresh = false
                isLoadMore = false
            })
        }
    }

    private fun initData(bundle: Bundle, ringMsgListFragment: RingMsgListFragment) {
        when (bundle.getString(ringMsgListFragment.ARG_STR)) {
            resources.getString(R.string.app_msg_health_notify) -> {
                emptyData.visibility = View.VISIBLE
                abnormalAdapter = AbnormalMsgListAdapter(mActivity, abnormalMsgList)
                msgListRv.adapter = abnormalAdapter
            }
            resources.getString(R.string.app_msg_care_info) -> {
                emptyData.visibility = View.VISIBLE
                careAdapter = HealthCareMsgListAdapter(mActivity, careMsgList)
                msgListRv.adapter = careAdapter
                careAdapter.setListener(object : AdapterOnItemClickListener {
                    override fun onClickItem(position: Int) {
                        val intent = Intent(mActivity, HealthCareMsgDetailActivity::class.java)
                        intent.putExtra("content", careMsgList[position].adviceContent)
                        startActivity(intent)
                    }

                })
            }
            resources.getString(R.string.app_msg_system_info) -> {
                emptyData.visibility = View.VISIBLE
                systemAdapter = SystemMsgListAdapter(mActivity, systemMsgList)
                msgListRv.adapter = systemAdapter
            }
        }
    }
}