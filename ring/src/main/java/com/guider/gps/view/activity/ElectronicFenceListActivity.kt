package com.guider.gps.view.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.guider.baselib.base.BaseActivity
import com.guider.baselib.utils.*
import com.guider.baselib.widget.dialog.DialogHolder
import com.guider.gps.R
import com.guider.gps.adapter.ElectronicFenceListAdapter
import com.guider.health.apilib.GuiderApiUtil
import com.guider.health.apilib.utils.MMKVUtil
import com.guider.health.apilib.bean.ElectronicFenceListBean
import kotlinx.android.synthetic.main.activity_electronic_fence_list.*
import kotlinx.coroutines.launch

/**
 * @Package: com.guider.gps.view.activity
 * @ClassName: ElectronicFenceList
 * @Description: 电子围栏列表页
 * @Author: hjr
 * @CreateDate: 2020/10/28 18:20
 * Copyright (C), 1998-2020, GuiderTechnology
 */
class ElectronicFenceListActivity : BaseActivity() {

    override val contentViewResId: Int
        get() = R.layout.activity_electronic_fence_list

    private var electronicFenceList = arrayListOf<ElectronicFenceListBean>()
    private lateinit var adapter: ElectronicFenceListAdapter
    private var deleteBackShowFencePosition = ""

    override fun initImmersion() {
        setTitle(mContext!!.resources.getString(R.string.app_electronic_fence_list))
        showBackButton(R.drawable.ic_back_white, this)
        setRightImage(R.drawable.icon_white_top_add, this)
    }

    override fun initView() {
        electronicListRv.layoutManager = LinearLayoutManager(this)
    }

    override fun initLogic() {
        adapter = ElectronicFenceListAdapter(mContext!!, electronicFenceList)
        adapter.setListener(object : AdapterOnItemClickListener {
            override fun onClickItem(position: Int) {
                intent.putExtra("info", adapter.mData[position])
                setResult(Activity.RESULT_OK, intent)
                finish()
            }

        })
        adapter.setSwitchListener(object : ElectronicFenceListAdapter.AdapterEventListener {
            override fun switchChange(position: Int, isChecked: Boolean) {
                switchStatus(position, isChecked)
            }

            override fun deleteItem(position: Int) {
                if (position > electronicFenceList.size - 1) return
                deleteElectronicFence(position)
            }

        })
        electronicListRv.adapter = adapter
        getElectronicFenceListData()
    }

    private fun deleteElectronicFence(position: Int) {
        val dialog = object : DialogHolder(this,
                R.layout.dialog_common_with_title, Gravity.CENTER) {
            @SuppressLint("SetTextI18n")
            override fun bindView(dialogView: View) {
                val cancel = dialogView.findViewById<ConstraintLayout>(R.id.cancelLayout)
                cancel.setOnClickListener {
                    dialog?.dismiss()
                }
                val unBindContentTv = dialogView.findViewById<TextView>(R.id.unBindContentTv)
                unBindContentTv.text = mContext!!.resources.getString(
                        R.string.app_main_delete_fence)
                val selectDateTitle = dialogView.findViewById<TextView>(R.id.selectDateTitle)
                selectDateTitle.text = mContext!!.resources.getString(
                        R.string.app_main_delete_electronic_fence_area)
                val confirm = dialogView.findViewById<ConstraintLayout>(R.id.confirmLayout)
                confirm.setOnClickListener {
                    lifecycleScope.launch {
                        ApiCoroutinesCallBack.resultParse(mContext!!, onStart = {
                            showDialog()
                        }, block = {
                            val resultBean = GuiderApiUtil.getApiService()
                                    .deleteFence(electronicFenceList[position].id)
                            if (resultBean == "true") {
                                deleteBackShowFencePosition = position.toString()
                                electronicFenceList.removeAt(position)
                                adapter.notifyItemRemoved(position)
                                if (electronicFenceList.isNullOrEmpty()) {
                                    emptyData.visibility = View.VISIBLE
                                }
                            }
                        }, onRequestFinish = {
                            dismissDialog()
                            dialog?.dismiss()
                        })
                    }
                }
            }
        }
        dialog.initView()
        dialog.show(true)
    }

    override fun onBackPressed() {
        if (StringUtil.isNotBlankAndEmpty(deleteBackShowFencePosition)) {
            val fenceBean =
                    if (electronicFenceList.isNullOrEmpty()) {
                        val deviceCode = MMKVUtil.getString(BIND_DEVICE_CODE)
                        val accountId = MMKVUtil.getInt(BIND_DEVICE_ACCOUNT_ID)
                        ElectronicFenceListBean(accountId, deviceCode, 0, true,
                                arrayListOf(), "")

                    } else {
                        electronicFenceList[deleteBackShowFencePosition.toInt()]
                    }
            intent.putExtra("info", fenceBean)
            setResult(Activity.RESULT_OK, intent)
        }
        finish()
    }

    private fun switchStatus(position: Int, isChecked: Boolean) {
        lifecycleScope.launch {
            ApiCoroutinesCallBack.resultParse(mContext!!, onStart = {
                showDialog()
            }, block = {
                val resultBean = GuiderApiUtil.getApiService()
                        .fenceSwitchSet(electronicFenceList[position].id, isChecked)
                if (resultBean != "true") {
                    electronicFenceList[position].open = !isChecked
                } else electronicFenceList[position].open = isChecked
            }, onError = {
                electronicFenceList[position].open = !isChecked
            }, onRequestFinish = {
                if (electronicFenceList[position].open != isChecked)
                    adapter.notifyItemChanged(position)
                dismissDialog()
            })
        }
    }

    private fun getElectronicFenceListData() {
        val accountId = MMKVUtil.getInt(BIND_DEVICE_ACCOUNT_ID)
        if (accountId == 0) {
            return
        }
        lifecycleScope.launch {
            ApiCoroutinesCallBack.resultParse(mContext!!, onStart = {
                showDialog()
            }, block = {
                val resultBean = GuiderApiUtil.getApiService()
                        .getElectronicFence(accountId, -1, false)
                if (resultBean is String && resultBean == "null") {
                    emptyData.visibility = View.VISIBLE
                } else {
                    val fenceList = ParseJsonData.parseJsonDataList<ElectronicFenceListBean>(
                            resultBean, ElectronicFenceListBean::class.java)
                    if (fenceList.isNullOrEmpty()) {
                        emptyData.visibility = View.VISIBLE
                    } else {
                        emptyData.visibility = View.GONE
                        electronicFenceList = fenceList as ArrayList<ElectronicFenceListBean>
                        adapter.setSourceList(electronicFenceList)
                    }
                }
            }, onRequestFinish = {
                dismissDialog()
            })
        }
    }

    override fun showToolBar(): Boolean {
        return true
    }

    override fun onNoDoubleClick(v: View) {
        when (v) {
            iv_toolbar_right -> {
                val deviceCode = MMKVUtil.getString(BIND_DEVICE_CODE)
                val accountId = MMKVUtil.getInt(BIND_DEVICE_ACCOUNT_ID)
                val fenceBean = ElectronicFenceListBean(accountId, deviceCode, 0, true,
                        arrayListOf(), "")
                intent.putExtra("info", fenceBean)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
            iv_left -> {
                onBackPressed()
            }
        }
    }
}