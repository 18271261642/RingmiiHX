package com.guider.gps.view.activity

import android.app.Activity
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.guider.baselib.base.BaseActivity
import com.guider.baselib.utils.*
import com.guider.gps.R
import com.guider.health.apilib.GuiderApiUtil
import com.guider.health.apilib.bean.FrequencySetBean
import kotlinx.android.synthetic.main.activity_location_frequency_set_new.*
import kotlinx.coroutines.launch

/**
 * @Package: com.guider.gps.view.activity
 * @ClassName: LocationFrequencySetNewActivity
 * @Description:定位频率设置页面
 * @Author: hjr
 * @CreateDate: 2020/9/21 12:56
 * Copyright (C), 1998-2020, GuiderTechnology
 */
class LocationFrequencySetNewActivity : BaseActivity() {

    override val contentViewResId: Int
        get() = R.layout.activity_location_frequency_set_new

    private var selectKind = 0

    override fun initImmersion() {
        setTitle(resources.getString(R.string.app_main_map_positioning_frequency_setting))
        showBackButton(R.drawable.ic_back_white)
        setRightImage(R.drawable.icon_top_right_confirm, this)
    }

    override fun initView() {

    }

    override fun initLogic() {
        getLocationFrequencySet()
        normalKindLayout.setOnClickListener(this)
        searchKindLayout.setOnClickListener(this)
    }

    private fun getLocationFrequencySet() {
        val accountId = MMKVUtil.getInt(BIND_DEVICE_ACCOUNT_ID)
        if (accountId == 0) {
            return
        }
        showDialog()
        lifecycleScope.launch {
            try {
                val resultBean = GuiderApiUtil.getApiService().locationFrequencySet(accountId)
                dismissDialog()
                if (!resultBean.isNullOrEmpty()) {
                    val frequencySetList = resultBean as ArrayList
                    showFrequencySet(frequencySetList)
                }
            } catch (e: Exception) {
                dismissDialog()
                toastShort(e.message!!)
            }
        }
    }

    private fun showFrequencySet(frequencySetList: ArrayList<FrequencySetBean>) {
        if (frequencySetList.size == 1) {
            val rate = frequencySetList[0].rate
            selectKind = if (rate == 5) {
                1
            } else {
                0
            }
            if (selectKind != 0) {
                normalSelectIv.visibility = View.INVISIBLE
                searchSelectIv.visibility = View.VISIBLE
            } else {
                normalSelectIv.visibility = View.VISIBLE
                searchSelectIv.visibility = View.INVISIBLE
            }
        }
    }

    override fun onNoDoubleClick(v: View) {
        when (v) {
            normalKindLayout -> {
                if (selectKind != 0) {
                    selectKind = 0
                    normalSelectIv.visibility = View.VISIBLE
                    searchSelectIv.visibility = View.INVISIBLE
                }
            }
            searchKindLayout -> {
                if (selectKind != 1) {
                    selectKind = 1
                    normalSelectIv.visibility = View.INVISIBLE
                    searchSelectIv.visibility = View.VISIBLE
                }
            }
            iv_toolbar_right -> {
                commitSet()
            }
        }
    }

    private fun commitSet() {
        val deviceCode = MMKVUtil.getString(BIND_DEVICE_CODE)
        if (StringUtil.isEmpty(deviceCode)) {
            return
        }
        val hashMap = hashMapOf<String, Any>()
        hashMap["deviceCode"] = deviceCode
        val accountId = MMKVUtil.getInt(BIND_DEVICE_ACCOUNT_ID)
        hashMap["accountId"] = accountId
        val setBean1 = FrequencySetBean("24:00", true,
                if (selectKind == 0) {
                    60
                } else {
                    5
                }, "00:00"
        )
        hashMap["rates"] = arrayListOf(setBean1)
        showDialog()
        lifecycleScope.launch {
            try {
                val resultBean = GuiderApiUtil.getApiService().setLocationFrequency(hashMap)
                dismissDialog()
                if (resultBean != null) {
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            } catch (e: Exception) {
                dismissDialog()
                toastShort(e.message!!)
            }
        }
    }

    override fun showToolBar(): Boolean {
        return true
    }
}