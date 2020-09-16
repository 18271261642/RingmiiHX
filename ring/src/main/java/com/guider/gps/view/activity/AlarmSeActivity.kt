package com.guider.gps.view.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.view.Gravity
import android.view.View
import cn.addapp.pickers.picker.TimePicker
import com.guider.baselib.base.BaseActivity
import com.guider.baselib.utils.*
import com.guider.gps.R
import com.guider.health.apilib.ApiCallBack
import com.guider.health.apilib.ApiUtil
import com.guider.health.apilib.IGuiderApi
import kotlinx.android.synthetic.main.activity_alarm_set.*
import retrofit2.Call
import retrofit2.Response
import kotlin.math.floor

/**
 * @Package: com.guider.gps.view.activity
 * @ClassName: AlarmSeActivity
 * @Description: 监测周期设置共用页面
 * @Author: hjr
 * @CreateDate: 2020/9/16 16:39
 * Copyright (C), 1998-2020, GuiderTechnology
 */
class AlarmSeActivity : BaseActivity() {

    private var enterType = ""

    override val contentViewResId: Int
        get() = R.layout.activity_alarm_set

    private var timeIntervalValue = 0

    override fun openEventBus(): Boolean {
        return false
    }

    override fun initImmersion() {
        showBackButton(R.drawable.ic_back_white)
        setRightImage(R.drawable.icon_top_right_confirm, this)
        if (intent != null) {
            if (StringUtil.isNotBlankAndEmpty(intent.getStringExtra("enterType"))) {
                enterType = intent.getStringExtra("enterType")!!
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        if (enterType == "heart") {
            setTitle(mContext!!.resources.getString(R.string.app_main_mine_heart_rate_set))
            val hrCheck = MMKVUtil.getBoolean(HR_CHECK, false)
            timeIntervalValue = MMKVUtil.getInt(HR_INTERVAL, 0)
            switchSet1.setCheckedNoEvent(hrCheck)
            if (hrCheck) {
                timeIntervalLayout.visibility = View.VISIBLE
            } else {
                timeIntervalLayout.visibility = View.GONE
            }
        } else {
            setTitle(mContext!!.resources.getString(R.string.app_main_mine_temp_set))
            val btCheck = MMKVUtil.getBoolean(BT_CHECK, false)
            timeIntervalValue = MMKVUtil.getInt(BT_INTERVAL, 0)
            switchSet1.setCheckedNoEvent(btCheck)
            if (btCheck) {
                timeIntervalLayout.visibility = View.VISIBLE
            } else {
                timeIntervalLayout.visibility = View.GONE
            }
        }
        if (timeIntervalValue != 0) {
            val hours = floor((timeIntervalValue / 60).toDouble()).toInt()
            val minute: Int = timeIntervalValue % 60
            timeIntervalTv.text = "${hours}h${minute}m"
        }
    }

    @SuppressLint("SetTextI18n")
    override fun initLogic() {
        timeIntervalLayout.setOnClickListener(this)
        switchSet1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                timeIntervalLayout.visibility = View.VISIBLE
                onTimePicker(timeIntervalTv.text.toString()) { hour, minute ->
                    if (hour == "0") {
                        timeIntervalTv.text = "${minute.toInt()}m"
                    } else timeIntervalTv.text = "${hour.toInt()}h${minute.toInt()}m"
                }
            } else {
                timeIntervalLayout.visibility = View.GONE
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onNoDoubleClick(v: View) {
        when (v) {
            iv_toolbar_right -> {
                timeIntervalValue = if (timeIntervalTv.text.toString().contains("h")) {
                    val timeReplace = timeIntervalTv.text.toString()
                            .replace("h", ":")
                            .replace("m", "")
                    val selectHour = timeReplace.substring(0,
                            timeReplace.indexOf(":")).toInt()
                    val selectMinute = timeReplace.substring(
                            timeReplace.indexOf(":") + 1).toInt()
                    selectHour * 60 + selectMinute
                } else {
                    val timeReplace = timeIntervalTv.text.toString()
                            .replace("m", "")
                    timeReplace.toInt()
                }
                val checked = switchSet1.isChecked
                if (enterType == "heart") {
                    commitHeartAlarmSet(checked)
                } else {
                    commitTempAlarmSet(checked)
                }
            }
            timeIntervalLayout -> {
                onTimePicker(timeIntervalTv.text.toString()) { hour, minute ->
                    if (hour == "0") {
                        timeIntervalTv.text = "${minute.toInt()}m"
                    } else timeIntervalTv.text = "${hour.toInt()}h${minute.toInt()}m"
                }
            }
        }
    }

    private fun commitTempAlarmSet(checked: Boolean) {
        showDialog()
        val accountId = MMKVUtil.getInt(USER.USERID)
        ApiUtil.createApi(IGuiderApi::class.java, false)
                .setBodyTempAlarm(accountId, timeIntervalValue, checked)
                .enqueue(object : ApiCallBack<Any>(mContext) {
                    override fun onApiResponse(call: Call<Any>?,
                                               response: Response<Any>?) {
                        if (response?.body() != null) {
                            MMKVUtil.saveBoolean(BT_CHECK, checked)
                            MMKVUtil.saveInt(BT_INTERVAL, timeIntervalValue)
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        }
                    }

                    override fun onRequestFinish() {
                        super.onRequestFinish()
                        dismissDialog()
                    }
                })
    }

    private fun commitHeartAlarmSet(checked: Boolean) {
        showDialog()
        val accountId = MMKVUtil.getInt(USER.USERID)
        ApiUtil.createApi(IGuiderApi::class.java, false)
                .setHeartRateAlarm(accountId, timeIntervalValue, checked)
                .enqueue(object : ApiCallBack<Any>(mContext) {
                    override fun onApiResponse(call: Call<Any>?,
                                               response: Response<Any>?) {
                        if (response?.body() != null) {
                            MMKVUtil.saveBoolean(HR_CHECK, checked)
                            MMKVUtil.saveInt(HR_INTERVAL, timeIntervalValue)
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        }
                    }

                    override fun onRequestFinish() {
                        super.onRequestFinish()
                        dismissDialog()
                    }
                })
    }

    private fun onTimePicker(time: String, onSelectTime: (hour: String, minute: String) -> Unit) {
        val picker = TimePicker(this, TimePicker.HOUR_24)
        picker.setRangeStart(0, 0)
        picker.setRangeEnd(23, 59)
        if (StringUtil.isNotBlankAndEmpty(time)) {
            val selectHour: Int
            val selectMinute: Int
            if (time.contains("h")) {
                val timeReplace = time.replace("h", ":")
                        .replace("m", "")
                selectHour = timeReplace.substring(0, timeReplace.indexOf(":")).toInt()
                selectMinute = timeReplace.substring(
                        timeReplace.indexOf(":") + 1).toInt()
            } else {
                val timeReplace = time.replace("m", "")
                selectHour = 0
                selectMinute = timeReplace.toInt()
            }
            picker.setSelectedItem(selectHour, selectMinute)
        }
        picker.setTopLineVisible(false)
        picker.setLineVisible(false)
        picker.setGravity(Gravity.BOTTOM)
        picker.setOnTimePickListener { hour, minute ->
            run {
                onSelectTime.invoke(hour, minute)
            }
        }
        picker.show()
    }

    override fun showToolBar(): Boolean {
        return true
    }
}