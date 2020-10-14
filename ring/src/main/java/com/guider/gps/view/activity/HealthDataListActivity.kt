package com.guider.gps.view.activity

import android.graphics.Color
import android.view.Gravity
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import cn.addapp.pickers.picker.DatePicker
import cn.addapp.pickers.picker.DatePicker.OnYearMonthDayPickListener
import com.guider.baselib.base.BaseActivity
import com.guider.baselib.utils.*
import com.guider.feifeia3.utils.ToastUtil
import com.guider.gps.R
import com.guider.gps.adapter.HealthDetailListAdapter
import com.guider.health.apilib.GuiderApiUtil
import com.guider.health.apilib.bean.HealthDataSimpleBean
import kotlinx.android.synthetic.main.activity_health_data_list.*
import kotlinx.coroutines.launch

class HealthDataListActivity : BaseActivity() {

    override val contentViewResId: Int
        get() = R.layout.activity_health_data_list

    private var type = ""
    private var dataList = arrayListOf<HealthDataSimpleBean>()
    private var startTime: String? = ""
    private var endTime: String? = ""
    private lateinit var adapter: HealthDetailListAdapter
    private var page = 1
    private var isRefresh = false
    private var isLoadMore = false

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
        startTime = DateUtilKotlin.localToUTC(CommonUtils.calTimeFrontDay(
                CommonUtils.getCurrentDate(DEFAULT_TIME_FORMAT_PATTERN),
                7, DEFAULT_TIME_FORMAT_PATTERN))!!
        startTimeTv.text = CommonUtils.calTimeFrontDay(
                CommonUtils.getCurrentDate(DEFAULT_TIME_FORMAT_PATTERN),
                7, TIME_FORMAT_PATTERN4)
        endTime = DateUtilKotlin.localToUTC(
                CommonUtils.getCurrentDate(DEFAULT_TIME_FORMAT_PATTERN))!!
        endTimeTv.text = CommonUtils.getCurrentDate(TIME_FORMAT_PATTERN4)
    }

    override fun initLogic() {
        adapter = HealthDetailListAdapter(mContext!!, dataList, type)
        healthDataListRv.adapter = adapter
        refreshLayout.setOnRefreshListener {
            page = 1
            isRefresh = true
            getHealthData(false)
        }
        refreshLayout.setOnLoadMoreListener {
            page++
            isLoadMore = true
            getHealthData(false)
        }
        startTimeTv.setOnClickListener(this)
        endTimeTv.setOnClickListener(this)
        searchLayout.setOnClickListener(this)
        getHealthData()
    }

    private fun getHealthData(isShowLoading: Boolean = true) {
        if (isShowLoading)
            showDialog()
        val accountId = MMKVUtil.getInt(BIND_DEVICE_ACCOUNT_ID)
        when (type) {
            resources.getString(R.string.app_main_health_blood_pressure) -> {
                getBloodPressureData(accountId, isShowLoading)
            }
            resources.getString(R.string.app_main_health_heart_rate) -> {
                getHeartData(accountId, isShowLoading)
            }
            resources.getString(R.string.app_main_health_body_temp) -> {
                getBodyTempData(accountId, isShowLoading)
            }
            resources.getString(R.string.app_main_health_sleep) -> {
                getSleepData(accountId, isShowLoading)
            }
            resources.getString(R.string.app_main_health_sport) -> {
                getSportData(accountId, isShowLoading)
            }
        }
    }

    private fun getBloodPressureData(accountId: Int, isShowLoading: Boolean) {
        lifecycleScope.launch {
            try {
                val resultBean = GuiderApiUtil.getHDApiService()
                        .getHealthBloodChartData(accountId, page, 20, startTime!!, endTime!!)
                if (isShowLoading) dismissDialog()
                if (!resultBean.isNullOrEmpty()) {
                    noDataTv.visibility = View.GONE
                    if (isRefresh) refreshLayout.finishRefresh(500)
                    if (isLoadMore) refreshLayout.finishLoadMore(500)
                    if (resultBean.size < 20) {
                        refreshLayout.setEnableLoadMore(false)
                    } else refreshLayout.setEnableLoadMore(true)
                    val resultList = resultBean.map {
                        HealthDataSimpleBean(
                                it.id, it.testTime, sbp = it.sbp, dbp = it.dbp,
                                state2 = it.state2)
                    }
//                            val timeResultList = arrayListOf<HealthDataDetailListBean>()
//                            timeResultList.add(HealthDataDetailListBean(resultList[0].testTime,
//                                    resultList))
                    if (isLoadMore) {
                        dataList.addAll(resultList)
                    } else {
                        dataList = resultList as ArrayList
                    }
                    adapter.type = resources.getString(
                            R.string.app_main_health_blood_pressure)
                    adapter.setSourceList(dataList)
                } else {
                    noDataTv.visibility = View.VISIBLE
                    if (isRefresh) {
                        refreshLayout.finishRefresh()
                    }
                    if (isLoadMore) {
                        refreshLayout.finishLoadMore()
                        refreshLayout.setEnableLoadMore(false)
                    }
                    if (!isLoadMore) {
                        dataList.clear()
                        adapter.setSourceList(dataList)
                    }
                }
                isRefresh = false
                isLoadMore = false
            } catch (e: Exception) {
                if (isShowLoading) dismissDialog()
                if (isRefresh) refreshLayout.finishRefresh()
                if (isLoadMore) {
                    refreshLayout.finishLoadMore()
                    page--
                }
                isRefresh = false
                isLoadMore = false
                toastShort(e.message!!)
            }
        }
    }

    private fun getHeartData(accountId: Int, isShowLoading: Boolean) {
        lifecycleScope.launch {
            try {
                val resultBean = GuiderApiUtil.getHDApiService()
                        .getHealthHeartChartData(accountId, page, 20, startTime!!, endTime!!)
                if (isShowLoading) dismissDialog()
                if (!resultBean.isNullOrEmpty()) {
                    noDataTv.visibility = View.GONE
                    if (isRefresh) refreshLayout.finishRefresh(500)
                    if (isLoadMore) refreshLayout.finishLoadMore(500)
                    if (resultBean.size < 20) {
                        refreshLayout.setEnableLoadMore(false)
                    } else refreshLayout.setEnableLoadMore(true)
                    val resultList = resultBean.map {
                        HealthDataSimpleBean(it.id, it.testTime, hb = it.hb,
                                state2 = it.state2)
                    }
//                            val timeResultList = arrayListOf<HealthDataDetailListBean>()
//                            timeResultList.add(HealthDataDetailListBean(resultList[0].testTime,
//                                    resultList))
                    if (isLoadMore) {
                        dataList.addAll(resultList)
                    } else {
                        dataList = resultList as ArrayList
                    }
                    adapter.type = resources.getString(
                            R.string.app_main_health_heart_rate)
                    adapter.setSourceList(dataList)
                } else {
                    noDataTv.visibility = View.VISIBLE
                    if (isRefresh) {
                        refreshLayout.finishRefresh()
                    }
                    if (isLoadMore) {
                        refreshLayout.finishLoadMore()
                        refreshLayout.setEnableLoadMore(false)
                    }
                    if (!isLoadMore) {
                        dataList.clear()
                        adapter.setSourceList(dataList)
                    }
                }
                isRefresh = false
                isLoadMore = false
            } catch (e: Exception) {
                if (isShowLoading) dismissDialog()
                if (isRefresh) refreshLayout.finishRefresh()
                if (isLoadMore) {
                    refreshLayout.finishLoadMore()
                    page--
                }
                isRefresh = false
                isLoadMore = false
                toastShort(e.message!!)
            }
        }
    }

    private fun getBodyTempData(accountId: Int, isShowLoading: Boolean) {
        lifecycleScope.launch {
            try {
                val resultBean = GuiderApiUtil.getHDApiService()
                        .getHealthTempChartData(accountId, page, 20, startTime!!, endTime!!)
                if (isShowLoading) dismissDialog()
                if (!resultBean.isNullOrEmpty()) {
                    noDataTv.visibility = View.GONE
                    if (isRefresh) refreshLayout.finishRefresh(500)
                    if (isLoadMore) refreshLayout.finishLoadMore(500)
                    if (resultBean.size < 20) {
                        refreshLayout.setEnableLoadMore(false)
                    } else refreshLayout.setEnableLoadMore(true)
                    val resultList = resultBean.map {
                        HealthDataSimpleBean(it.id, it.testTime, bodyTemp = it.bodyTemp,
                                state2 = it.state2)
                    }
//                            val timeResultList = arrayListOf<HealthDataDetailListBean>()
//                            timeResultList.add(HealthDataDetailListBean(resultList[0].testTime,
//                                    resultList))
                    if (isLoadMore) {
                        dataList.addAll(resultList)
                    } else {
                        dataList = resultList as ArrayList
                    }
                    adapter.type = resources.getString(
                            R.string.app_main_health_body_temp)
                    adapter.setSourceList(dataList)
                } else {
                    noDataTv.visibility = View.VISIBLE
                    if (isRefresh) {
                        refreshLayout.finishRefresh()
                    }
                    if (isLoadMore) {
                        refreshLayout.finishLoadMore()
                        refreshLayout.setEnableLoadMore(false)
                    }
                    if (!isLoadMore) {
                        dataList.clear()
                        adapter.setSourceList(dataList)
                    }
                }
                isRefresh = false
                isLoadMore = false
            } catch (e: Exception) {
                if (isShowLoading) dismissDialog()
                isRefresh = false
                isLoadMore = false
                if (isRefresh) refreshLayout.finishRefresh()
                if (isLoadMore) {
                    refreshLayout.finishLoadMore()
                    page--
                }
                toastShort(e.message!!)
            }
        }
    }

    private fun getSleepData(accountId: Int, isShowLoading: Boolean) {
        lifecycleScope.launch {
            try {
                val resultBean = GuiderApiUtil.getHDApiService()
                        .getHealthSleepChartData(accountId,startTime!!)
                if (isShowLoading) dismissDialog()
                if (!resultBean.isNullOrEmpty()) {
                    noDataTv.visibility = View.GONE
                    if (isRefresh) refreshLayout.finishRefresh(500)
                    if (isLoadMore) refreshLayout.finishLoadMore(500)
                    if (resultBean.size < 20) {
                        refreshLayout.setEnableLoadMore(false)
                    } else refreshLayout.setEnableLoadMore(true)
                    val resultList = resultBean.map {
                        HealthDataSimpleBean(it.id, it.testTime, minute = it.minute)
                    }
//                            val timeResultList = arrayListOf<HealthDataDetailListBean>()
//                            timeResultList.add(HealthDataDetailListBean(resultList[0].testTime,
//                                    resultList))
                    if (isLoadMore) {
                        dataList.addAll(resultList)
                    } else {
                        dataList = resultList as ArrayList
                    }
                    adapter.type = resources.getString(
                            R.string.app_main_health_sleep)
                    adapter.setSourceList(dataList)
                } else {
                    noDataTv.visibility = View.VISIBLE
                    if (isRefresh) {
                        refreshLayout.finishRefresh()
                    }
                    if (isLoadMore) {
                        refreshLayout.finishLoadMore()
                        refreshLayout.setEnableLoadMore(false)
                    }
                    if (!isLoadMore) {
                        dataList.clear()
                        adapter.setSourceList(dataList)
                    }
                }
                isRefresh = false
                isLoadMore = false
            } catch (e: Exception) {
                if (isShowLoading) dismissDialog()
                isRefresh = false
                isLoadMore = false
                if (isRefresh) refreshLayout.finishRefresh()
                if (isLoadMore) {
                    refreshLayout.finishLoadMore()
                    page--
                }
                toastShort(e.message!!)
            }
        }
    }

    private fun getSportData(accountId: Int, isShowLoading: Boolean) {
        lifecycleScope.launch {
            try {
                val resultBean = GuiderApiUtil.getHDApiService()
                        .getHealthSportChartData(accountId, page, 20, startTime!!, endTime!!)
                if (isShowLoading) dismissDialog()
                if (!resultBean.isNullOrEmpty()) {
                    noDataTv.visibility = View.GONE
                    if (isRefresh) refreshLayout.finishRefresh(500)
                    if (isLoadMore) refreshLayout.finishLoadMore(500)
                    if (resultBean.size < 20) {
                        refreshLayout.setEnableLoadMore(false)
                    } else refreshLayout.setEnableLoadMore(true)
                    val resultList = resultBean.map {
                        HealthDataSimpleBean(it.id, it.testTime, step = it.step)
                    }
//                            val timeResultList = arrayListOf<HealthDataDetailListBean>()
//                            timeResultList.add(HealthDataDetailListBean(resultList[0].testTime,
//                                    resultList))
                    if (isLoadMore) {
                        dataList.addAll(resultList)
                    } else {
                        dataList = resultList as ArrayList<HealthDataSimpleBean>
                    }
                    adapter.type = resources.getString(
                            R.string.app_main_health_sport)
                    adapter.setSourceList(dataList)
                } else {
                    noDataTv.visibility = View.VISIBLE
                    if (isRefresh) {
                        refreshLayout.finishRefresh()
                    }
                    if (isLoadMore) {
                        refreshLayout.finishLoadMore()
                        refreshLayout.setEnableLoadMore(false)
                    }
                    if (!isLoadMore) {
                        dataList.clear()
                        adapter.setSourceList(dataList)
                    }
                }
                isRefresh = false
                isLoadMore = false
            } catch (e: Exception) {
                if (isShowLoading) dismissDialog()
                isRefresh = false
                isLoadMore = false
                if (isRefresh) refreshLayout.finishRefresh()
                if (isLoadMore) {
                    refreshLayout.finishLoadMore()
                    page--
                }
                toastShort(e.message!!)
            }
        }
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
                getHealthData()
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