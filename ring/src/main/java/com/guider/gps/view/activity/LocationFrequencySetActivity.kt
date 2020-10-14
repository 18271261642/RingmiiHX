package com.guider.gps.view.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.view.Gravity
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import cn.addapp.pickers.picker.TimePicker
import com.guider.baselib.base.BaseActivity
import com.guider.baselib.utils.*
import com.guider.gps.R
import com.guider.health.apilib.GuiderApiUtil
import com.guider.health.apilib.bean.FrequencySetBean
import kotlinx.android.synthetic.main.activity_location_frequency_set.*
import kotlinx.coroutines.launch

/**
 * 定位频率设置
 */
class LocationFrequencySetActivity : BaseActivity() {

    override val contentViewResId: Int
        get() = R.layout.activity_location_frequency_set

    private var frequencySetList = arrayListOf<FrequencySetBean>()

    override fun openEventBus(): Boolean {
        return false
    }

    override fun initImmersion() {
        setTitle(resources.getString(R.string.app_main_map_positioning_frequency_setting))
        showBackButton(R.drawable.ic_back_white)
        setRightImage(R.drawable.icon_top_right_confirm, this)
    }

    override fun initView() {
        switchSet1.setCheckedNoEvent(false)
        switchSet2.setCheckedNoEvent(true)
        switchSet3.setCheckedNoEvent(false)
        timeIntervalEdit1.addTextChangedListener {
            if (StringUtil.isEmpty(it.toString())) {
                return@addTextChangedListener
            }
            if (it.toString().toInt() < 1 || it.toString().toInt() > 1440) {
                toastShort("输入时间有误，请重新输入")
                timeIntervalEdit1.setText("1")
            }
        }
        timeIntervalEdit2.addTextChangedListener {
            if (StringUtil.isEmpty(it.toString())) {
                return@addTextChangedListener
            }
            if (it.toString().toInt() < 1 || it.toString().toInt() > 1440) {
                toastShort("输入时间有误，请重新输入")
                timeIntervalEdit2.setText("1")
            }
        }
        timeIntervalEdit3.addTextChangedListener {
            if (StringUtil.isEmpty(it.toString())) {
                return@addTextChangedListener
            }
            if (it.toString().toInt() < 1 || it.toString().toInt() > 1440) {
                toastShort("输入时间有误，请重新输入")
                timeIntervalEdit3.setText("1")
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onNoDoubleClick(v: View) {
        when (v) {
            iv_toolbar_right -> {
                commitSet()
            }
            setLayout1StartTimeLayout -> {
                onTimePicker(startTimeTv1.text.toString()) { hour, minute ->
                    startTimeTv1.text = "${hour}:${minute}"
                }
            }
            setLayout1EndTimeLayout -> {
                onTimePicker(endTimeTv1.text.toString()) { hour, minute ->
                    endTimeTv1.text = "${hour}:${minute}"
                }
            }
            setLayout2StartTimeLayout -> {
                onTimePicker(startTimeTv2.text.toString()) { hour, minute ->
                    startTimeTv2.text = "${hour}:${minute}"
                }
            }
            setLayout2EndTimeLayout -> {
                onTimePicker(endTimeTv2.text.toString()) { hour, minute ->
                    endTimeTv2.text = "${hour}:${minute}"
                }
            }
            setLayout3StartTimeLayout -> {
                onTimePicker(startTimeTv3.text.toString()) { hour, minute ->
                    startTimeTv3.text = "${hour}:${minute}"
                }
            }
            setLayout3EndTimeLayout -> {
                onTimePicker(endTimeTv3.text.toString()) { hour, minute ->
                    endTimeTv3.text = "${hour}:${minute}"
                }
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
        val setBean1 = FrequencySetBean(
                endTimeTv1.text.toString(), switchSet1.isChecked,
                if (StringUtil.isNotBlankAndEmpty(timeIntervalEdit1.text.toString())) {
                    timeIntervalEdit1.text.toString().toInt()
                } else {
                    0
                },
                startTimeTv1.text.toString(),
        )
        val setBean2 = FrequencySetBean(
                endTimeTv2.text.toString(), switchSet2.isChecked,
                if (StringUtil.isNotBlankAndEmpty(timeIntervalEdit2.text.toString())) {
                    timeIntervalEdit2.text.toString().toInt()
                } else {
                    0
                },
                startTimeTv2.text.toString(),
        )
        val setBean3 = FrequencySetBean(
                endTimeTv3.text.toString(), switchSet3.isChecked,
                if (StringUtil.isNotBlankAndEmpty(timeIntervalEdit3.text.toString())) {
                    timeIntervalEdit3.text.toString().toInt()
                } else {
                    0
                },
                startTimeTv3.text.toString(),
        )
        val accountId = MMKVUtil.getInt(BIND_DEVICE_ACCOUNT_ID)
        hashMap["accountId"] = accountId
        hashMap["rates"] = arrayListOf(setBean1, setBean2, setBean3)
        showDialog()
        lifecycleScope.launch {
            try {
                val resultBean = GuiderApiUtil.getApiService().setLocationFrequency(hashMap)
                if (resultBean != null) {
                    dismissDialog()
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            } catch (e: Exception) {
                dismissDialog()
                toastShort(e.message!!)
            }
        }
    }

    private fun onTimePicker(time: String, onSelectTime: (hour: String, minute: String) -> Unit) {
        val picker = TimePicker(this, TimePicker.HOUR_24)
        picker.setRangeStart(0, 0)
        picker.setRangeEnd(23, 59)
        if (StringUtil.isNotBlankAndEmpty(time) && time.length == 5 && time.contains(":")) {
            val selectHour = time.substring(0, 2)
            val selectMinute = time.substring(3)
            picker.setSelectedItem(selectHour.toInt(), selectMinute.toInt())
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

    override fun initLogic() {
        setLayout1StartTimeLayout.setOnClickListener(this)
        setLayout1EndTimeLayout.setOnClickListener(this)
        setLayout2StartTimeLayout.setOnClickListener(this)
        setLayout2EndTimeLayout.setOnClickListener(this)
        setLayout3StartTimeLayout.setOnClickListener(this)
        setLayout3EndTimeLayout.setOnClickListener(this)
        getLocationFrequencySet()
        switchSet1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                setLayout1ContentLayout.visibility = View.VISIBLE
            } else {
                setLayout1ContentLayout.visibility = View.GONE
            }
            if (switchSet2.isChecked) {
                switchSet2.setCheckedNoEvent(false)
                setLayout2ContentLayout.visibility = View.GONE
            }
            if (switchSet3.isChecked) {
                switchSet3.setCheckedNoEvent(false)
                setLayout3ContentLayout.visibility = View.GONE
            }
        }
        switchSet2.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                setLayout2ContentLayout.visibility = View.VISIBLE
            } else {
                setLayout2ContentLayout.visibility = View.GONE
            }
            if (switchSet1.isChecked) {
                switchSet1.setCheckedNoEvent(false)
                setLayout1ContentLayout.visibility = View.GONE
            }
            if (switchSet3.isChecked) {
                switchSet3.setCheckedNoEvent(false)
                setLayout3ContentLayout.visibility = View.GONE
            }
        }
        switchSet3.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                setLayout3ContentLayout.visibility = View.VISIBLE
            } else {
                setLayout3ContentLayout.visibility = View.GONE
            }
            if (switchSet1.isChecked) {
                switchSet1.setCheckedNoEvent(false)
                setLayout1ContentLayout.visibility = View.GONE
            }
            if (switchSet2.isChecked) {
                switchSet2.setCheckedNoEvent(false)
                setLayout2ContentLayout.visibility = View.GONE
            }
        }
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
                    frequencySetList = resultBean as ArrayList
                    showFrequencySet(frequencySetList)
                }
            } catch (e: Exception) {
                dismissDialog()
                toastShort(e.message!!)
            }
        }
    }

    private fun showFrequencySet(frequencySetList: ArrayList<FrequencySetBean>) {
        if (frequencySetList.size == 3) {
            switchSet1.setCheckedNoEvent(frequencySetList[0].open)
            if (frequencySetList[0].open) {
                setLayout1ContentLayout.visibility = View.VISIBLE
            } else {
                setLayout1ContentLayout.visibility = View.GONE
            }
            startTimeTv1.text = frequencySetList[0].start
            endTimeTv1.text = frequencySetList[0].end
            if (frequencySetList[0].rate != 0)
                timeIntervalEdit1.setText(frequencySetList[0].rate.toString())
            switchSet2.setCheckedNoEvent(frequencySetList[1].open)
            if (frequencySetList[1].open) {
                setLayout2ContentLayout.visibility = View.VISIBLE
            } else {
                setLayout2ContentLayout.visibility = View.GONE
            }
            startTimeTv2.text = frequencySetList[1].start
            endTimeTv2.text = frequencySetList[1].end
            if (frequencySetList[1].rate != 0)
                timeIntervalEdit2.setText(frequencySetList[1].rate.toString())
            switchSet3.setCheckedNoEvent(frequencySetList[2].open)
            if (frequencySetList[2].open) {
                setLayout3ContentLayout.visibility = View.VISIBLE
            } else {
                setLayout3ContentLayout.visibility = View.GONE
            }
            startTimeTv3.text = frequencySetList[2].start
            endTimeTv3.text = frequencySetList[2].end
            if (frequencySetList[2].rate != 0)
                timeIntervalEdit3.setText(frequencySetList[2].rate.toString())
        }
    }

    override fun showToolBar(): Boolean {
        return true
    }
}