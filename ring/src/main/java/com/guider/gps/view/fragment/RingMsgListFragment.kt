package com.guider.gps.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.guider.baselib.base.BaseFragment
import com.guider.baselib.utils.AdapterOnItemClickListener
import com.guider.baselib.utils.MMKVUtil
import com.guider.baselib.utils.ParseJsonData
import com.guider.baselib.utils.USER
import com.guider.gps.R
import com.guider.gps.adapter.AbnormalMsgListAdapter
import com.guider.gps.adapter.HealthCareMsgListAdapter
import com.guider.gps.view.activity.HealthCareMsgDetailActivity
import com.guider.gps.view.activity.RingMsgListActivity
import com.guider.health.apilib.ApiCallBack
import com.guider.health.apilib.ApiUtil
import com.guider.health.apilib.IUserHDApi
import com.guider.health.apilib.bean.AbnormalRingMsgListBean
import com.guider.health.apilib.bean.CareMsgListBean
import com.guider.health.apilib.bean.HealthDataSimpleBean
import kotlinx.android.synthetic.main.fragment_ring_msg_list.*
import retrofit2.Call
import retrofit2.Response

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
    private lateinit var abnormalAdapter: AbnormalMsgListAdapter
    private lateinit var careAdapter: HealthCareMsgListAdapter
    private var page = 1
    private var isRefresh = false
    private var isLoadMore = false
    private var msgListType = ""

    override val layoutRes: Int
        get() = R.layout.fragment_ring_msg_list

    override fun initView(rootView: View) {
    }

    override fun initLogic() {
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
                resources.getString(R.string.app_msg_error_notify) -> {
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
            resources.getString(R.string.app_msg_error_notify) -> {
                if (isShowLoading)
                    mActivity.showDialog()
                getAbnormalMsgListData(accountId, isShowLoading)
            }
            resources.getString(R.string.app_msg_care_info) -> {
                if (isShowLoading)
                    mActivity.showDialog()
                getCareMsgListData(accountId, isShowLoading)
                (mActivity as RingMsgListActivity).resetCareNum()
            }
        }

    }

    private fun getAbnormalMsgListData(accountId: Int, isShowLoading: Boolean) {
        ApiUtil.createHDApi(IUserHDApi::class.java, false)
                .getAbnormalMsgList(accountId, -1, page, 20)
                .enqueue(object : ApiCallBack<List<AbnormalRingMsgListBean>>(mActivity) {
                    override fun onApiResponse(call: Call<List<AbnormalRingMsgListBean>>?,
                                               response: Response<List<AbnormalRingMsgListBean>>?) {
                        if (!response?.body().isNullOrEmpty()) {
                            emptyData.visibility = View.GONE
                            if (isRefresh) refreshLayout.finishRefresh(500)
                            if (isLoadMore) refreshLayout.finishLoadMore(500)
                            if (response?.body()!!.size < 20) {
                                refreshLayout.setEnableLoadMore(false)
                            } else refreshLayout.setEnableLoadMore(true)
                            val dataList = response.body()!!.map {
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
                    }

                    override fun onFailure(call: Call<List<AbnormalRingMsgListBean>>, t: Throwable) {
                        super.onFailure(call, t)
                        if (isRefresh) refreshLayout.finishRefresh()
                        if (isLoadMore) {
                            refreshLayout.finishLoadMore()
                            page--
                        }
                    }

                    override fun onRequestFinish() {
                        if (isShowLoading) mActivity.dismissDialog()
                        isRefresh = false
                        isLoadMore = false
                    }
                })
    }

    private fun getCareMsgListData(accountId: Int, isShowLoading: Boolean) {
        ApiUtil.createHDApi(IUserHDApi::class.java, false)
                .getCareMsgList(accountId, page, 20, -1)
                .enqueue(object : ApiCallBack<List<CareMsgListBean>>(mActivity) {
                    override fun onApiResponse(call: Call<List<CareMsgListBean>>?,
                                               response: Response<List<CareMsgListBean>>?) {
                        if (!response?.body().isNullOrEmpty()) {
                            emptyData.visibility = View.GONE
                            if (isRefresh) refreshLayout.finishRefresh(500)
                            if (isLoadMore) refreshLayout.finishLoadMore(500)
                            if (response?.body()!!.size < 20) {
                                refreshLayout.setEnableLoadMore(false)
                            } else refreshLayout.setEnableLoadMore(true)
                            if (isLoadMore) {
                                careMsgList.addAll(response.body()!!)
                            } else {
                                careMsgList = response.body() as ArrayList
                            }
                            careAdapter.setSourceList(careMsgList)
                        } else {
                            emptyData.visibility = View.VISIBLE
                            if (isRefresh) {
                                refreshLayout.finishRefresh()
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
                    }

                    override fun onFailure(call: Call<List<CareMsgListBean>>, t: Throwable) {
                        super.onFailure(call, t)
                        if (isRefresh) refreshLayout.finishRefresh()
                        if (isLoadMore) {
                            refreshLayout.finishLoadMore()
                            page--
                        }
                    }

                    override fun onRequestFinish() {
                        if (isShowLoading) mActivity.dismissDialog()
                        isRefresh = false
                        isLoadMore = false
                    }
                })
    }

    private fun initData(bundle: Bundle, ringMsgListFragment: RingMsgListFragment) {
        when (bundle.getString(ringMsgListFragment.ARG_STR)) {
            resources.getString(R.string.app_msg_error_notify) -> {
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
            }
        }
    }
}