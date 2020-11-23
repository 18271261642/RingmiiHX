package com.guider.gps.view.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.view.Gravity
import android.view.View
import androidx.lifecycle.lifecycleScope
import cn.addapp.pickers.picker.TimePicker
import com.guider.baselib.base.BaseActivity
import com.guider.baselib.utils.*
import com.guider.gps.R
import com.guider.health.apilib.GuiderApiUtil
import com.guider.health.apilib.utils.MMKVUtil
import kotlinx.android.synthetic.main.activity_alarm_set.*
import kotlinx.coroutines.launch
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
            if (hours > 0)
                timeIntervalTv.text = "$hours" +
                        mContext!!.resources.getString(R.string.app_hour) +
                        "$minute" +
                        mContext!!.resources.getString(R.string.app_minute)
            else {
                timeIntervalTv.text = "$minute" +
                        mContext!!.resources.getString(R.string.app_minute)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun initLogic() {
        timeIntervalLayout.setOnClickListener(this)
        switchSet1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                timeIntervalLayout.visibility = View.VISIBLE
                onTimePicker(timeIntervalTv.text.toString()) { hour, minute ->
                    if (hour == "00") {
                        timeIntervalTv.text = "${minute.toInt()}" +
                                mContext!!.resources.getString(R.string.app_minute)
                    } else timeIntervalTv.text = "${hour.toInt()}" +
                            mContext!!.resources.getString(R.string.app_hour) +
                            "${minute.toInt()}" +
                            mContext!!.resources.getString(R.string.app_minute)
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
                timeIntervalValue = if (timeIntervalTv.text.toString().contains(
                                mContext!!.resources.getString(R.string.app_hour))) {
                    val timeReplace = timeIntervalTv.text.toString()
                            .replace(mContext!!.resources.getString(R.string.app_hour),
                                    ":")
                            .replace(mContext!!.resources.getString(R.string.app_minute),
                                    "")
                    val selectHour = timeReplace.substring(0,
                            timeReplace.indexOf(":")).toInt()
                    val selectMinute = timeReplace.substring(
                            timeReplace.indexOf(":") + 1).toInt()
                    selectHour * 60 + selectMinute
                } else {
                    val timeReplace = timeIntervalTv.text.toString()
                            .replace(mContext!!.resources.getString(R.string.app_minute),
                                    "")
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
                    if (hour == "00") {
                        timeIntervalTv.text = "${minute.toInt()}" +
                                mContext!!.resources.getString(R.string.app_minute)
                    } else timeIntervalTv.text = "${hour.toInt()}" +
                            mContext!!.resources.getString(R.string.app_hour) +
                            "${minute.toInt()}" +
                            mContext!!.resources.getString(R.string.app_minute)
                }
            }
        }
    }

    private fun commitTempAlarmSet(checked: Boolean) {
        val accountId = MMKVUtil.getInt(USER.USERID)
        lifecycleScope.launch {
            ApiCoroutinesCallBack.resultParse(mContext!!,onStart = {
                showDialog()
            },block = {
                val resultBean = GuiderApiUtil.getApiService()
                        .setBodyTempAlarm(accountId, timeIntervalValue, checked)
                if (resultBean != null) {
                    MMKVUtil.saveBoolean(BT_CHECK, checked)
                    MMKVUtil.saveInt(BT_INTERVAL, timeIntervalValue)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            },onRequestFinish = {
                dismissDialog()
            })
        }
    }

    private fun commitHeartAlarmSet(checked: Boolean) {
        val accountId = MMKVUtil.getInt(USER.USERID)
        lifecycleScope.launch {
            ApiCoroutinesCallBack.resultParse(mContext!!, onStart = {
                showDialog()
            }, block = {
                val resultBean = GuiderApiUtil.getApiService()
                        .setHeartRateAlarm(accountId, timeIntervalValue, checked)
                if (resultBean != null) {
                    MMKVUtil.saveBoolean(HR_CHECK, checked)
                    MMKVUtil.saveInt(HR_INTERVAL, timeIntervalValue)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }, onRequestFinish = {
                dismissDialog()
            })
        }
    }

    private fun onTimePicker(time: String, onSelectTime: (hour: String, minute: String) -> Unit) {
        val picker = TimePicker(this, TimePicker.HOUR_24)
        picker.setRangeStart(0, 0)
        picker.setRangeEnd(23, 59)
        if (StringUtil.isNotBlankAndEmpty(time)) {
            val selectHour: Int
            val selectMinute: Int
            if (time.contains(mContext!!.resources.getString(R.string.app_hour))) {
                val timeReplace = time.replace(mContext!!.resources.getString(R.string.app_hour),
                        ":")
                        .replace(mContext!!.resources.getString(R.string.app_minute), "")
                selectHour = timeReplace.substring(0, timeReplace.indexOf(":")).toInt()
                selectMinute = timeReplace.substring(
                        timeReplace.indexOf(":") + 1).toInt()
            } else {
                val timeReplace = time.replace(mContext!!.resources.getString(R.string.app_minute),
                        "")
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