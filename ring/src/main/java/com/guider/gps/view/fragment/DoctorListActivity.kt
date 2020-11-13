package com.guider.gps.view.fragment

import android.content.Intent
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.guider.baselib.base.BaseActivity
import com.guider.baselib.utils.AdapterOnItemClickListener
import com.guider.baselib.utils.BIND_DEVICE_ACCOUNT_ID
import com.guider.baselib.utils.MMKVUtil
import com.guider.gps.R
import com.guider.gps.adapter.DoctorListAdapter
import com.guider.gps.view.activity.DoctorAnswerActivity
import com.guider.gps.viewModel.DoctorListViewModel
import com.guider.health.apilib.bean.DoctorListBean
import kotlinx.android.synthetic.main.activity_doctor_list.*

/**
 * @Package: com.guider.gps.view.fragment
 * @ClassName: DoctorListActivity
 * @Description: 医生咨询列表页
 * @Author: hjr
 * @CreateDate: 2020/11/10 13:51
 * Copyright (C), 1998-2020, GuiderTechnology
 */
class DoctorListActivity : BaseActivity() {

    private lateinit var viewModel: DoctorListViewModel
    private lateinit var adapter: DoctorListAdapter
    private var doctorList = arrayListOf<DoctorListBean>()

    override val contentViewResId: Int
        get() = R.layout.activity_doctor_list

    override fun initImmersion() {
        setTitle(resources.getString(R.string.app_main_medicine_doctors_consultation))
        showBackButton(R.drawable.ic_back_white)
    }

    override fun initView() {
        doctorRv.layoutManager = LinearLayoutManager(this)
        initViewModel()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[DoctorListViewModel::class.java]
        viewModel.also {
            it.doctorList.observe(this, { list ->
                kotlin.run {
                    if (list.isNullOrEmpty()) {
                        emptyData.visibility = View.VISIBLE
                    } else {
                        emptyData.visibility = View.GONE
                        doctorList = list as ArrayList<DoctorListBean>
                        adapter.setSourceList(doctorList)
                    }
                }
            })
            it.loading.observe(this, { isShow ->
                if (isShow) {
                    showDialog()
                } else {
                    dismissDialog()
                }
            })
        }
    }

    override fun initLogic() {
        adapter = DoctorListAdapter(mContext!!, doctorList)
        adapter.setListener(object :AdapterOnItemClickListener{
            override fun onClickItem(position: Int) {
                val intent = Intent(mContext, DoctorAnswerActivity::class.java)
                intent.putExtra("title",adapter.mData[position].name)
                intent.putExtra("accountId",adapter.mData[position].accountId)
                startActivity(intent)
            }

        })
        doctorRv.adapter = adapter
        refreshLayout.setOnRefreshListener {
            getDoctorListData()
        }
        getDoctorListData()
    }

    private fun getDoctorListData() {
        val accountId = MMKVUtil.getInt(BIND_DEVICE_ACCOUNT_ID,0)
        viewModel.getDoctorList(accountId)
    }

    override fun showToolBar(): Boolean {
        return true
    }
}