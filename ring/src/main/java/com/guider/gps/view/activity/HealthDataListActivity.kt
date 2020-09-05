package com.guider.gps.view.activity

import android.graphics.Color
import android.view.Gravity
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import cn.addapp.pickers.picker.DatePicker
import cn.addapp.pickers.picker.DatePicker.OnYearMonthDayPickListener
import com.guider.baselib.base.BaseActivity
import com.guider.baselib.utils.CommonUtils
import com.guider.baselib.utils.DateUtilKotlin
import com.guider.baselib.utils.StringUtil
import com.guider.baselib.utils.toastShort
import com.guider.feifeia3.utils.ToastUtil
import com.guider.gps.R
import com.guider.gps.adapter.HealthDataAdapter
import kotlinx.android.synthetic.main.activity_health_data_list.*

class HealthDataListActivity : BaseActivity() {

    override val contentViewResId: Int
        get() = R.layout.activity_health_data_list

    private var type = ""
    private var dataList = arrayListOf<String>()
    private var startTime: String? = ""
    private var endTime: String? = ""
    private lateinit var adapter: HealthDataAdapter

    override fun openEventBus(): Boolean {
        return false
    }

    override fun initImmersion() {
        showBackButton(R.drawable.ic_back_white)
        if (intent != null) {
            if (StringUtil.isNotBlankAndEmpty(intent.getStringExtra("type"))) {
                type = intent.getStringExtra("type")!!
                setTitle(type)
            }
        }
    }

    override fun initView() {
        healthDataListRv.layoutManager = LinearLayoutManager(this)
        startTimeTv.text = CommonUtils.calTimeFrontDate(CommonUtils.getCurrentDate(), 30)
        endTimeTv.text = CommonUtils.getCurrentDate()
    }

    override fun initLogic() {
        dataList.add("2020-08-13")
        dataList.add("2020-08-12")
        adapter = HealthDataAdapter(mContext!!, dataList)
        healthDataListRv.adapter = adapter
        startTimeTv.setOnClickListener(this)
        endTimeTv.setOnClickListener(this)
        searchLayout.setOnClickListener(this)
    }

    override fun onNoDoubleClick(v: View) {
        when (v) {
            startTimeTv -> {
                onYearMonthDayPicker("start")
            }
            endTimeTv -> {
                onYearMonthDayPicker("end")
            }
            searchLayout -> {

            }
        }

    }

    /*
    * 年月日选择
    * */
    private fun onYearMonthDayPicker(type: String) {
        val picker = DatePicker(this)
        picker.setGravity(Gravity.BOTTOM)
        picker.setTopPadding(15)
        picker.setRangeStart(2000, 1, 1)
        picker.setRangeEnd(2100, 12, 31)
        val timeValue = if (type == "start") {
            startTimeTv.text.toString()
        } else {
            endTimeTv.text.toString()
        }
        val dayInt = timeValue.substring(
                timeValue.lastIndexOf('-') + 1).toInt()
        val yearInt = timeValue.substring(0, timeValue.indexOf('-')).toInt()
        val monthInt = timeValue.substring(
                timeValue.indexOf('-') + 1,
                timeValue.lastIndexOf('-')).toInt()
        picker.setSelectedItem(yearInt, monthInt, dayInt)
        picker.setTitleText("$yearInt-$monthInt-$dayInt")
        picker.setWeightEnable(true)
        picker.setLineColor(Color.BLACK)
        picker.setOnDatePickListener(OnYearMonthDayPickListener { year, month, day ->
            run {
                timeCalAndShow(year.toInt(), month.toInt(), day.toInt(), type)
            }
        })
        picker.setOnWheelListener(object : DatePicker.OnWheelListener {
            override fun onYearWheeled(index: Int, year: String) {
                picker.setTitleText(year + "-" + picker.selectedMonth + "-" + picker.selectedDay)
            }

            override fun onMonthWheeled(index: Int, month: String) {
                picker.setTitleText(picker.selectedYear + "-" + month + "-" + picker.selectedDay)
            }

            override fun onDayWheeled(index: Int, day: String) {
                picker.setTitleText(picker.selectedYear + "-" + picker.selectedMonth + "-" + day)
            }
        })
        picker.show()
    }

    /**
     * 计算显示时间选择的值和刷新数据显示
     */
    private fun timeCalAndShow(year: Int, month: Int, day: Int, type: String) {
        val selectDate = year.toString().plus("-")
                .plus(if (month < 10) {
                    "0$month"
                } else month)
                .plus("-").plus(if (day < 10) {
                    "0$day"
                } else day)
        if (type == "start") {
            //选择开始时间
            //结束时间比开始时间必须大，且结束时间不能比开始时间长30天
            if (StringUtil.isNotBlankAndEmpty(endTimeTv.text.toString()) &&
                    (CommonUtils.calTimeDateCompareNew(
                            selectDate, endTimeTv.text.toString()))
            ) {
                //如果成立说明开始时间大于结束时间，不符合要求
                toastShort("选择的时间不符合要求")
            } else {
                startTimeTv.text = selectDate
                val startTimeValue = selectDate.plus(" 00:00:00")
                val endTimeValue = endTimeTv.text.toString().plus(" 00:00:00")
                startTime = DateUtilKotlin.localToUTC(startTimeValue)
                endTime = DateUtilKotlin.localToUTC(endTimeValue)
            }
        } else {
            //选择结束时间
            if (StringUtil.isNotBlankAndEmpty(startTimeTv.text.toString()) &&
                    (CommonUtils.calTimeDateCompareNew(
                            startTimeTv.text.toString(), selectDate))
            ) {
                ToastUtil.show(mContext!!, "选择的时间不符合要求")
            } else {
                endTimeTv.text = selectDate
                val endTimeValue = selectDate.plus(" 00:00:00")
                val startTimeValue = startTimeTv.text.toString().plus(" 00:00:00")
                startTime = DateUtilKotlin.localToUTC(startTimeValue)
                endTime = DateUtilKotlin.localToUTC(endTimeValue)
            }
        }
    }

    override fun showToolBar(): Boolean {
        return true
    }
}