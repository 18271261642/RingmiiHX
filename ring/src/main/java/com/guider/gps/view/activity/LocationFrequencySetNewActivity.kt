package com.guider.gps.view.activity

import android.app.Activity
import android.view.View
import com.guider.baselib.base.BaseActivity
import com.guider.baselib.utils.BIND_DEVICE_ACCOUNT_ID
import com.guider.baselib.utils.BIND_DEVICE_CODE
import com.guider.baselib.utils.MMKVUtil
import com.guider.baselib.utils.StringUtil
import com.guider.gps.R
import com.guider.health.apilib.ApiCallBack
import com.guider.health.apilib.ApiUtil
import com.guider.health.apilib.IGuiderApi
import com.guider.health.apilib.bean.FrequencySetBean
import kotlinx.android.synthetic.main.activity_location_frequency_set_new.*
import retrofit2.Call
import retrofit2.Response

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
        ApiUtil.createApi(IGuiderApi::class.java, false)
                .locationFrequencySet(accountId)
                .enqueue(object : ApiCallBack<List<FrequencySetBean>>(mContext) {
                    override fun onApiResponse(call: Call<List<FrequencySetBean>>?,
                                               response: Response<List<FrequencySetBean>>?) {
                        if (!response?.body().isNullOrEmpty()) {
                            val frequencySetList = response?.body() as ArrayList
                            showFrequencySet(frequencySetList)
                        }
                    }

                    override fun onRequestFinish() {
                        dismissDialog()
                    }
                })
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
        ApiUtil.createApi(IGuiderApi::class.java, false)
                .setLocationFrequency(hashMap)
                .enqueue(object : ApiCallBack<Any>(mContext) {
                    override fun onApiResponse(call: Call<Any>?,
                                               response: Response<Any>?) {
                        if (response?.body() != null) {
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        }
                    }

                    override fun onRequestFinish() {
                        dismissDialog()
                    }
                })
    }

    override fun showToolBar(): Boolean {
        return true
    }
}