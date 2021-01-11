package com.guider.gps.view.activity

import android.content.Intent
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LoadState
import com.guider.baselib.base.BaseActivity
import com.guider.gps.R
import com.guider.gps.adapter.PageAdapter
import com.guider.gps.viewModel.PagingViewModel
import com.scwang.smart.refresh.layout.constant.RefreshState
import kotlinx.android.synthetic.main.activity_paging_list.*

/**
 * @Package: com.guider.gps.view.activity
 * @ClassName: PagingListActivity
 * @Description: paging列表页
 * @Author: hjr
 * @CreateDate: 2020/12/17 15:50
 * Copyright (C), 1998-2020, GuiderTechnology
 */
class PagingListActivity : BaseActivity() {

    private lateinit var viewModel: PagingViewModel
    private val adapter by lazy { PageAdapter() }

    override val contentViewResId: Int
        get() = R.layout.activity_paging_list

    override fun initImmersion() {
        showBackButton(R.drawable.ic_back_white)
        setTitle("page列表")
    }

    override fun initView() {
        initViewModel()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[PagingViewModel::class.java]
        viewModel.loading.observe(this, {
            if (it) showDialog()
            else dismissDialog()
        })
        viewModel.emptyData.observe(this, {
            if (it) {
                emptyData.visibility = View.VISIBLE
            } else {
                emptyData.visibility = View.GONE
            }
        })
        viewModel.listData.observe(this, {
            adapter.submitData(this.lifecycle, it)
        })
    }

    override fun initLogic() {
        viewModel.loading.postValue(true)
        initRv()
    }

    private fun initRv() {
        msgListRv.adapter = adapter
        //设置下拉刷新
        refreshLayout.setOnRefreshListener {
            adapter.refresh()
        }
        //上拉加载更多
        refreshLayout.setOnLoadMoreListener {
            adapter.retry()
        }
        adapter.setOnItemClickListener { _, _, _ ->
            val intent = Intent(mContext, NavigationActivity::class.java)
            startActivity(intent)
        }
        //下拉刷新状态
        //因为刷新前也会调用LoadState.NotLoading，所以用一个外部变量判断是否是刷新后
        var hasRefreshing = false
        adapter.addLoadStateListener {
            when (it.refresh) {
                is LoadState.Loading -> {
                    hasRefreshing = true
                    //如果是手动下拉刷新，则不展示loading页
                    if (refreshLayout.state != RefreshState.Refreshing) {
                        viewModel.loading.postValue(true)
                    }
                }
                is LoadState.NotLoading -> {
                    if (hasRefreshing) {
                        hasRefreshing = false
                        viewModel.loading.postValue(false)
                        refreshLayout.finishRefresh(true)
                        //如果第一页数据就没有更多了，则展示没有更多了
                        if (it.source.append.endOfPaginationReached) {
                            //没有更多了(只能用source的append)
                            refreshLayout.finishLoadMoreWithNoMoreData()
                        }
                    }
                }
                is LoadState.Error -> {
                    viewModel.loading.postValue(false)
                    refreshLayout.finishRefresh(false)
                }
            }
        }
        //加载更多状态
        //因为刷新前也会调用LoadState.NotLoading，所以用一个外部变量判断是否是加载更多后
        var hasLoadingMore = false
        adapter.addLoadStateListener {
            when (it.append) {
                is LoadState.Loading -> {
                    hasLoadingMore = true
                    //重置上拉加载状态，显示加载loading
                    refreshLayout.resetNoMoreData()
                }
                is LoadState.NotLoading -> {
                    if (hasLoadingMore) {
                        hasLoadingMore = false
                        if (it.source.append.endOfPaginationReached) {
                            //没有更多了(只能用source的append)
                            refreshLayout.finishLoadMoreWithNoMoreData()
                        } else {
                            refreshLayout.finishLoadMore(true)
                        }
                    }
                }
                is LoadState.Error -> {
                    refreshLayout.finishLoadMore(false)
                }
            }
        }
    }

    override fun showToolBar(): Boolean {
        return true
    }
}