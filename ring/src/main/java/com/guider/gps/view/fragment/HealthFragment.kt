package com.guider.gps.view.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayout
import com.guider.baselib.base.BaseFragment
import com.guider.baselib.utils.*
import com.guider.baselib.widget.aAInfographicsLib.aAChartCreator.*
import com.guider.baselib.widget.aAInfographicsLib.aAOptionsModel.AAOptions
import com.guider.baselib.widget.aAInfographicsLib.aATools.AAColor
import com.guider.baselib.widget.dialog.DialogProgress
import com.guider.gps.R
import com.guider.gps.view.activity.HealthDataListActivity
import com.guider.gps.view.activity.MainActivity
import com.guider.health.apilib.GuiderApiUtil
import com.guider.health.apilib.utils.MMKVUtil
import com.guider.health.apilib.bean.*
import kotlinx.android.synthetic.main.fragment_health_data.*
import kotlinx.android.synthetic.main.fragment_home_health.*
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import kotlin.math.abs
import kotlin.math.ceil


class HealthFragment : BaseFragment() {

    override val layoutRes: Int
        get() = R.layout.fragment_home_health

    private var tabTitleList = arrayListOf<String>()

    private var startTimeValue = ""
    private var endTimeValue = ""
    private lateinit var bloodLayout: ConstraintLayout
    private lateinit var tempLayout: ConstraintLayout
    private lateinit var heartLayout: ConstraintLayout
    private lateinit var sleepLayout: ConstraintLayout
    private lateinit var sportLayout: ConstraintLayout
    private var bloodYMaxValue = 0f
    private var heartYMaxValue = 0f
    private var sleepYMaxValue = 0f
    private var sportYMaxValue = 0f

    //日期类型分为今天，昨天，前天
    private var dateType = 0
    private var isFirstLoadData = true
    private var isRefresh = false
    private var mDialog: DialogProgress? = null
    private var selectDate = ""

    override fun initView(rootView: View) {
        bloodLayout = rootView.findViewById(R.id.bloodLayout)
        tempLayout = rootView.findViewById(R.id.tempLayout)
        heartLayout = rootView.findViewById(R.id.heartLayout)
        sleepLayout = rootView.findViewById(R.id.sleepLayout)
        sportLayout = rootView.findViewById(R.id.sportLayout)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initLogic() {
        tabInit()
        tabChangeEvent()
        bloodWatchMore.setOnClickListener(this)
        tempWatchMore.setOnClickListener(this)
        heartWatchMore.setOnClickListener(this)
        sleepLayout.setOnClickListener(this)
        sportWatchMore.setOnClickListener(this)
        initBloodChart()
        initHeartChart()
        initTempChart()
        initSleepChart()
        initSportChart()
        getHealthData()
        refreshLayout.setEnableAutoLoadMore(false)
        refreshLayout.setEnableRefresh(true)
        refreshLayout.setOnRefreshListener {
            isRefresh = true
            (mActivity as MainActivity).getLatestGroupData(false)
            getHealthData()
        }
        bloodChart.setOnTouchListener(touchListener)
        heartChart.setOnTouchListener(touchListener)
        tempChart.setOnTouchListener(touchListener)
        sleepChart.setOnTouchListener(touchListener)
        sportChart.setOnTouchListener(touchListener)
    }

    private fun tabInit() {
        tabTitleList = arrayListOf(
                resources.getString(R.string.app_main_health_today),
                resources.getString(R.string.app_main_health_yesterday),
                resources.getString(R.string.app_main_health_before_yesterday))
        val accountId = MMKVUtil.getInt(BIND_DEVICE_ACCOUNT_ID)
        val userId = MMKVUtil.getInt(USER.USERID)
        val targetSteps = if (MMKVUtil.getInt(TARGET_STEP, 0) != 0
                && accountId == userId) {
            String.format(
                    resources.getString(R.string.app_main_health_target_step),
                    MMKVUtil.getInt(TARGET_STEP, 0))
        } else String.format(
                resources.getString(R.string.app_main_health_target_step),
                8000)
        targetStepTv.text = targetSteps
        healthTabLayout.tabMode = TabLayout.MODE_FIXED
        // 设置选中下划线颜色
        healthTabLayout.setSelectedTabIndicatorColor(
                CommonUtils.getColor(mActivity, R.color.colorF18937))
        // 设置文本字体颜色[未选中颜色、选中颜色]
        healthTabLayout.setTabTextColors(CommonUtils.getColor(mActivity, R.color.color999999),
                CommonUtils.getColor(mActivity, R.color.color333333))
        // 设置下划线跟文本宽度一致
        healthTabLayout.isTabIndicatorFullWidth = true
        startTimeValue = DateUtilKotlin.localToUTC(
                "${CommonUtils.getCurrentDate()} 00:00:00")!!
        endTimeValue = DateUtilKotlin.localToUTC(
                "${CommonUtils.getCurrentDate()} 24:00:00")!!
        selectDate = CommonUtils.getCurrentDate()
    }

    override fun openEventBus(): Boolean {
        return true
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshTargetStep(event: EventBusEvent<Int>) {
        if (event.code == EventBusAction.REFRESH_TARGET_STEP) {
            val accountId = MMKVUtil.getInt(BIND_DEVICE_ACCOUNT_ID)
            val userId = MMKVUtil.getInt(USER.USERID)
            if (event.data != 0 && accountId == userId)
                targetStepTv.text = event.data.toString()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshHealthData(event: EventBusEvent<String>) {
        if (event.code == EventBusAction.REFRESH_USER_DATA) {
            getHealthData()
        }
    }

    private fun tabChangeEvent() {
        if (healthTabLayout.tabCount == 0) {
            tabTitleList.forEach {
                healthTabLayout.addTab(healthTabLayout.newTab().setText(it))
            }
            healthTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when (tab?.text.toString()) {
                        tabTitleList[0] -> {
                            dateType = 0
                            startTimeValue = DateUtilKotlin.localToUTC(
                                    "${CommonUtils.getCurrentDate()} 00:00:00")!!
                            endTimeValue = DateUtilKotlin.localToUTC(
                                    "${CommonUtils.getCurrentDate()} 24:00:00")!!
                            isFirstLoadData = false
                            getHealthData()
                        }
                        tabTitleList[1] -> {
                            dateType = 1
                            startTimeValue = DateUtilKotlin.localToUTC(
                                    "${
                                        CommonUtils.calTimeFrontDay(
                                                CommonUtils.getCurrentDate(), 1)
                                    } 00:00:00")!!
                            endTimeValue = DateUtilKotlin.localToUTC(
                                    "${
                                        CommonUtils.calTimeFrontDay(
                                                CommonUtils.getCurrentDate(), 1)
                                    } 24:00:00")!!
                            isFirstLoadData = false
                            getHealthData()
                        }
                        tabTitleList[2] -> {
                            dateType = 2
                            startTimeValue = DateUtilKotlin.localToUTC(
                                    "${
                                        CommonUtils.calTimeFrontDay(
                                                CommonUtils.getCurrentDate(), 2)
                                    } 00:00:00")!!
                            endTimeValue = DateUtilKotlin.localToUTC(
                                    "${
                                        CommonUtils.calTimeFrontDay(
                                                CommonUtils.getCurrentDate(), 2)
                                    } 24:00:00")!!
                            isFirstLoadData = false
                            getHealthData()
                        }
                    }
                    Log.i(TAG, "获取的是${tab?.text.toString()}的数据")
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {

                }

                override fun onTabReselected(tab: TabLayout.Tab?) {

                }

            })
        }
    }

    private fun getHealthData() {
        bloodYMaxValue = 0f
        heartYMaxValue = 0f
        sleepYMaxValue = 0f
        sportYMaxValue = 0f
        getBloodData()
        getBodyTempData()
        getHeartData()
        getSleepData()
        getSportData()
        getSportStepAndTargetStepData()
    }

    @SuppressLint("SetTextI18n")
    private fun getSportStepAndTargetStepData() {
        val accountId = MMKVUtil.getInt(BIND_DEVICE_ACCOUNT_ID)
        val userId = MMKVUtil.getInt(USER.USERID)
        lifecycleScope.launch {
            ApiCoroutinesCallBack.resultParse(mActivity, block = {
                val resultBean = GuiderApiUtil.getHDApiService()
                        .sportStepAndTargetStep(accountId, startTimeValue)
                if (resultBean != null) {
                    //总步数为一天时间段的步数总和
                    stepNumTv.text = resultBean.step.toString()
                    targetStepTv.text = String.format(
                            resources.getString(R.string.app_main_health_target_step),
                            if (resultBean.walkTarget != 0) {
                                resultBean.walkTarget
                            } else {
                                8000
                            })
                    if (accountId == userId) {
                        MMKVUtil.saveInt(TARGET_STEP, resultBean.walkTarget)
                    }
                } else {
                    initSportChart()
                    stepNumTv.text = "0"
                    targetStepTv.text = String.format(
                            resources.getString(R.string.app_main_health_target_step),
                            8000)
                }
            })
        }
    }

    private fun getSportData() {
        val accountId = MMKVUtil.getInt(BIND_DEVICE_ACCOUNT_ID)
        lifecycleScope.launch {
            ApiCoroutinesCallBack.resultParse(mActivity, block = {
                var resultBean = GuiderApiUtil.getHDApiService()
                        .getHealthSportChartData(accountId, -1, 100,
                                startTimeValue, endTimeValue)
                var maximum = 24
                if (dateType == 0) {
                    maximum = DateUtilKotlin.getNowHourTime() ?: 24
                }
                if (resultBean.size > 24) {
                    val tempList = arrayListOf<SportListBean>()
                    val indexList = getSamplingIndexList(resultBean, maximum)
                    for (i in indexList.indices) {
                        tempList.add(resultBean[indexList[i]])
                    }
                    resultBean = tempList
                }
                if (!resultBean.isNullOrEmpty()) {
                    sportNoDataTv.visibility = View.GONE
                    val options = getSportXAxisLabels(resultBean)
                    val sportDataList = resultBean.map { it.step }
                    val max = Collections.max(sportDataList)
                    val mean = ceil((max + 10) / 4.0)
                    val array = arrayListOf<Int>()
                    for (i in 0 until 5) {
                        array.add((i * mean).toInt())
                    }
                    Log.i(TAG, "运动的最大步数${max}")
                    Log.i(TAG, "运动的y轴数组为$array")
                    val aaOptions = initSportColumn(options,
                            sportDataList.toTypedArray(), array.toTypedArray())
                    sportChart.aa_drawChartWithChartOptions(aaOptions)
//                    val targetSteps = if (MMKVUtil.getInt(TARGET_STEP,
//                                    0) != 0) {
//                        MMKVUtil.getInt(TARGET_STEP, 0)
//                    } else 8000
//                    val str: String = String.format("%.2f",
//                            (4927 * 1.00f / targetSteps))
//                    val progress: Int = (str.toFloat() * 100).toInt()
                } else {
                    initSportChart()
                    sportNoDataTv.visibility = View.VISIBLE
                }
            })
        }
    }

    private fun getSleepData() {
        val accountId = MMKVUtil.getInt(BIND_DEVICE_ACCOUNT_ID)
        lifecycleScope.launch {
            ApiCoroutinesCallBack.resultParse(mActivity, block = {
                val resultBean = GuiderApiUtil.getHDApiService()
                        .getHealthSleepChartData(accountId, startTimeValue)
                if (!resultBean.isNullOrEmpty()) {
                    sleepNoDataTv.visibility = View.GONE
                    showSleepTime(resultBean)
                    val tempList = arrayListOf<SleepDataListBean>()
                    tempList.add(SleepDataListBean(
                            223, startTime = resultBean[0].startTime))
                    tempList.addAll(resultBean)
                    val options = getSleepXAxisLabels(tempList)
                    val colors = getSleepColorArray(tempList)
                    val sleepDataList = tempList.map { it.minute }
                    val aaOptions = initSleepColumn(options,
                            sleepDataList.toTypedArray(), colors)
                    sleepChart.aa_drawChartWithChartOptions(aaOptions)
                } else {
                    sleepTimeTv.text = ""
                    initSleepChart()
                    sleepNoDataTv.visibility = View.VISIBLE
                }
            })
        }
    }

    private fun showSleepTime(list: List<SleepDataListBean>) {
        if (list.size == 1) {
            val startTime = DateUtilKotlin.timeFormat(list[0].startTime, TIME_FORMAT_PATTERN8)
            val endTime = DateUtilKotlin.timeFormat(list[0].endTime, TIME_FORMAT_PATTERN8)
            sleepTimeTv.text = mActivity.resources.getString(
                    R.string.app_main_health_time_sleep, startTime, endTime)
        } else {
            val startTime = DateUtilKotlin.timeFormat(list[0].startTime, TIME_FORMAT_PATTERN8)
            val endTime = DateUtilKotlin.timeFormat(
                    list[list.size - 1].endTime, TIME_FORMAT_PATTERN8)
            sleepTimeTv.text = mActivity.resources.getString(
                    R.string.app_main_health_time_sleep, startTime, endTime)
        }
    }

    private fun getHeartData() {
        val accountId = MMKVUtil.getInt(BIND_DEVICE_ACCOUNT_ID)
        lifecycleScope.launch {
            ApiCoroutinesCallBack.resultParse(mActivity, block = {
                var resultBean = GuiderApiUtil.getHDApiService()
                        .getHealthHeartChartData(accountId, -1, 100,
                                startTimeValue, endTimeValue)
                var maximum = 24
                if (dateType == 0) {
                    maximum = DateUtilKotlin.getNowHourTime() ?: 24
                }
                if (resultBean.size > 24) {
                    val tempList = arrayListOf<HeartRateListBean>()
                    val indexList = getSamplingIndexList(resultBean, maximum)
                    for (i in indexList.indices) {
                        tempList.add(resultBean[indexList[i]])
                    }
                    resultBean = tempList
                }
                if (!resultBean.isNullOrEmpty()) {
                    heartNoDataTv.visibility = View.GONE
                    val options = getHeartXAxisLabels(resultBean)
                    val heartDataList = resultBean.map { it.hb }
                    val dataArray: Array<Any> = heartDataList.toTypedArray()
                    val aaOptions = initHeartLineChart(options, dataArray)
                    heartChart.aa_drawChartWithChartOptions(aaOptions)
                } else {
                    initHeartChart()
                    heartNoDataTv.visibility = View.VISIBLE
                }
            })
        }
    }

    private fun getBodyTempData() {
        val accountId = MMKVUtil.getInt(BIND_DEVICE_ACCOUNT_ID)
        lifecycleScope.launch {
            ApiCoroutinesCallBack.resultParse(mActivity, block = {
                var resultBean = GuiderApiUtil.getHDApiService()
                        .getHealthTempChartData(accountId,
                                -1, 9, startTimeValue, endTimeValue)
                var maximum = 24
                if (dateType == 0) {
                    maximum = DateUtilKotlin.getNowHourTime() ?: 24
                }
                if (resultBean.size > 24) {
                    val tempList = arrayListOf<BodyTempListBean>()
                    val indexList = getSamplingIndexList(resultBean, maximum)
                    for (i in indexList.indices) {
                        tempList.add(resultBean[indexList[i]])
                    }
                    resultBean = tempList
                }
                if (!resultBean.isNullOrEmpty()) {
                    tempNoDataTv.visibility = View.GONE
                    val options = getTempXAxisLabels(resultBean)
                    val tempDataList = resultBean.map { it.bodyTemp }
                    val dataArray: Array<Any> = tempDataList.toTypedArray()
                    val max = Collections.max(tempDataList)
                    val min = Collections.min(tempDataList)
                    Log.i("temp", "$min----$max")
                    val aaOptions = initTempLineChart(options, dataArray)
                    tempChart.aa_drawChartWithChartOptions(aaOptions)
                } else {
                    initTempChart()
                    tempNoDataTv.visibility = View.VISIBLE
                }
            })
        }
    }

    private fun getBloodData() {
        mDialog = DialogProgress(mActivity, null, true)
        if (!isFirstLoadData && !isRefresh) mDialog?.showDialog()
        val accountId = MMKVUtil.getInt(BIND_DEVICE_ACCOUNT_ID)
        lifecycleScope.launch {
            ApiCoroutinesCallBack.resultParse(mActivity, {
                if (!isFirstLoadData && !isRefresh) mDialog?.showDialog()
            }, block = {
                var resultBean = GuiderApiUtil.getHDApiService()
                        .getHealthBloodChartData(
                                accountId, -1, 100, startTimeValue, endTimeValue)
                var maximum = 24
                if (dateType == 0) {
                    maximum = DateUtilKotlin.getNowHourTime() ?: 24
                }
                if (resultBean.size > 24) {
                    val tempList = arrayListOf<BloodListBeann>()
                    val indexList = getSamplingIndexList(resultBean, maximum)
                    for (i in indexList.indices) {
                        tempList.add(resultBean[indexList[i]])
                    }
                    resultBean = tempList
                }
                if (!resultBean.isNullOrEmpty()) {
                    bloodPressureNoDataTv.visibility = View.GONE
                    if (isRefresh) refreshLayout.finishRefresh(500)
                    val options = getBloodXAxisLabels(resultBean)
                    val highDataList = resultBean.map { it.sbp }
                    val highArray: Array<Any> = highDataList.toTypedArray()
                    val belowDataList = resultBean.map { it.dbp }
                    val belowArray: Array<Any> = belowDataList.toTypedArray()
                    val aaOptions = initBloodLineChart(options, highArray, belowArray)
                    bloodChart.aa_drawChartWithChartOptions(aaOptions)
                } else {
                    if (isRefresh) {
                        refreshLayout.finishRefresh()
                    }
                    initBloodChart()
                    bloodPressureNoDataTv.visibility = View.VISIBLE
                }
            }, onRequestFinish = {
                if (isRefresh) refreshLayout.finishRefresh()
                if (!isFirstLoadData && !isRefresh) mDialog?.hideDialog()
                isRefresh = false
            })
        }
    }

    /**
     * 取均值的index数组
     */
    private fun getSamplingIndexList(resultBean: List<Any>, maximum: Int): ArrayList<Int> {
        val ceil = resultBean.size / maximum
        var num = (resultBean.size / ceil)
        if (num > maximum) num = maximum
        Log.i(TAG, "集合的大小为${resultBean.size}")
        Log.i(TAG, "数据采样的数字间隔为$ceil")
        Log.i(TAG, "数据采样的最大数为$maximum")
        val indexList = arrayListOf<Int>()
        for (i in 0 until num) {
            val tempIndex = if (i == num - 1 && i * ceil < resultBean.size - 1) {
                resultBean.size - 1
            } else i * ceil
            indexList.add(tempIndex)
        }
        Log.i(TAG, "取值的index数组为$indexList")
        Log.i(TAG, "取值的index数组的大小为${indexList.size}")
        return indexList
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mDialog != null) {
            mDialog?.hideDialog()
            mDialog = null
        }
    }

    private fun initSportChart() {
        val options = getSportXAxisLabels(arrayListOf())
        val aaOptions = initSportColumn(options, arrayOf(), arrayOf())
        sportChart.aa_drawChartWithChartOptions(aaOptions)
    }

    private fun getSportXAxisLabels(list: List<SportListBean>): Array<String> {
        if (list.isNullOrEmpty()) return arrayOf()
        val category = arrayListOf<String>()
        for (i in list.indices) {
            category.add(
                    DateUtilKotlin.uTCToLocal(list[i].testTime, TIME_FORMAT_PATTERN8)!!)
        }
        return category.toTypedArray()
    }

    private fun initSportColumn(category: Array<String>, sportList: Array<Any>,
                                array: Array<Any>): AAOptions {
        val aaChartModel = AAChartModel()
                .chartType(AAChartType.Column)//图形类型
                .dataLabelsEnabled(false)
                .categories(category)
                .legendEnabled(false)
                .yAxisTitle("")
                .colorsTheme(arrayOf("#ffffff"))
                .backgroundColor("#71d19b")
                .axesTextColor(AAColor.whiteColor())
                .axesTextSize(8f)
                .tooltipValueSuffix(mActivity.resources.getString(
                        R.string.app_main_health_step_unit))
                .zoomType(AAChartZoomType.X)
                .series(
                        if (sportList.isNotEmpty())
                            arrayOf(
                                    AASeriesElement()
                                            .name(mActivity.resources.getString(
                                                    R.string.app_main_health_sport))
                                            .data(sportList)
                            )
                        else arrayOf()
                )
        val options = AAOptionsConstructor.configureChartOptions(aaChartModel)
        options.plotOptions?.column?.groupPadding = 0.2f
        options.yAxis!!.gridLineColor("#FFFFFF4D").tickPositions(array)
        options.xAxis!!.lineColor("#FFFFFF4D")
        return options
    }

    private fun initSleepChart() {
        val options = getSleepXAxisLabels(arrayListOf())
        val colors = getSleepColorArray(arrayListOf())
        val aaOptions = initSleepColumn(options, arrayOf(), colors)
        sleepChart.aa_drawChartWithChartOptions(aaOptions)
    }

    private fun getSleepXAxisLabels(list: List<SleepDataListBean>): Array<String> {
        if (list.isNullOrEmpty()) return arrayOf()
        val category = arrayListOf<String>()
        for (i in list.indices) {
            if (i == 0) {
                category.add(
                        DateUtilKotlin.timeFormat(list[i].startTime, TIME_FORMAT_PATTERN8)!!)
            } else
                category.add(
                        DateUtilKotlin.timeFormat(list[i].endTime, TIME_FORMAT_PATTERN8)!!)
        }
        return category.toTypedArray()
    }

    private fun initSleepColumn(category: Array<String>, sleepList: Array<Any>,
                                colors: Array<Any>): AAOptions {
        val aaChartModel = AAChartModel()
                .chartType(AAChartType.Column)//图形类型
                .dataLabelsEnabled(true)
                .categories(category)
                .legendEnabled(false)
                .yAxisTitle("")
                .colorsTheme(colors)
                .backgroundColor("#8e7aee")
                .axesTextColor(AAColor.whiteColor())
                .axesTextSize(8f)
                .tooltipValueSuffix(mActivity.resources.getString(
                        R.string.app_main_health_sleep_minute))
                .series(
                        if (sleepList.isNotEmpty())
                            arrayOf(
                                    AASeriesElement()
                                            .name(mActivity.resources.getString(
                                                    R.string.app_main_health_sleep))
                                            .data(sleepList)
                                            .colorByPoint(true)

                            )
                        else arrayOf()
                )
        val options = AAOptionsConstructor.configureChartOptions(aaChartModel)
        options.plotOptions?.column?.groupPadding = 0.2f
        options.yAxis!!.gridLineColor("#FFFFFF4D")
        options.xAxis!!.lineColor("#FFFFFF4D")
//        options.yAxis!!.lineWidth = 0.5
        return options
    }

    private fun getSleepColorArray(list: List<SleepDataListBean>): Array<Any> {
        val colors = arrayListOf<String>()
        if (list.isNullOrEmpty()) {
            colors.add("#ffffff")
        } else {
            for (i in list.indices) {
                //根据1；深度睡眠，2；浅度睡眠，3；醒来时长
                val colorIntValue = when (list[i].sleepType) {
                    1 -> {
                        "#5838F0"
                    }
                    2 -> {
                        "#B6A7FF"
                    }
                    else -> {
                        "#FFAD29"
                    }
                }
                colors.add(colorIntValue)
            }
        }
        return colors.toTypedArray()
    }

    private fun initTempChart() {
        val options = getTempXAxisLabels(arrayListOf())
        val aaOptions = initTempLineChart(options, arrayOf())
        tempChart.aa_drawChartWithChartOptions(aaOptions)
    }

    private fun initTempLineChart(category: Array<String>, bodyTempList: Array<Any>)
            : AAOptions {
        val zonesArr: Array<Any> = arrayOf(
                mapOf(
                        "value" to 37.5,
                        "color" to "#ffffff"
                ),
                mapOf(
                        "color" to "#E2402B"
                )
        )
        val aaChartModel = AAChartModel()
                .chartType(AAChartType.Spline)//图形类型
                .dataLabelsEnabled(false)
                .categories(category)
                .legendEnabled(false)
                .markerRadius(2f)
                .yAxisTitle("")
                .backgroundColor("#5E95FF")
                .axesTextColor("#ffffff")
                .axesTextSize(8f)
                .tooltipValueSuffix("℃")
                .zoomType(AAChartZoomType.X)
                .series(
                        if (bodyTempList.isNotEmpty())
                            arrayOf(
                                    AASeriesElement()
                                            .name(mActivity.resources.getString(R.string.app_main_health_body_temp))
                                            .data(bodyTempList)
                                            .fillOpacity(0.5f)
                                            .lineWidth(1f)
                                            .zones(zonesArr)
                            )
                        else arrayOf()
                )
        val aaOptions = AAOptionsConstructor.configureChartOptions(aaChartModel)
        aaOptions.yAxis!!.gridLineColor("#FFFFFF4D").tickPositions(getTempYAxisLabels())
        aaOptions.xAxis!!.lineColor("#FFFFFF4D")
        return aaOptions
    }

    private fun initHeartChart() {
        val options = getBloodXAxisLabels(arrayListOf())
        val aaOptions = initHeartLineChart(options, arrayOf())
        heartChart.aa_drawChartWithChartOptions(aaOptions)
    }

    private fun getHeartXAxisLabels(list: List<HeartRateListBean>): Array<String> {
        if (list.isNullOrEmpty()) return arrayOf()
        val category = arrayListOf<String>()
        for (i in list.indices) {
            category.add(
                    DateUtilKotlin.uTCToLocal(list[i].testTime, TIME_FORMAT_PATTERN8)!!)
        }
        return category.toTypedArray()
    }

    private fun getHeartYAxisLabels(): Array<Any> {
        return arrayOf(0, 40, 80, 120, 160)
    }

    private fun initHeartLineChart(category: Array<String>, heartList: Array<Any>): AAOptions {
        val aaChartModel = AAChartModel()
                .chartType(AAChartType.Spline)//图形类型
                .dataLabelsEnabled(false)
                .categories(category)
                .legendEnabled(false)
                .markerRadius(2f)
                .yAxisTitle("")
                .colorsTheme(arrayOf(AAColor.whiteColor()))
                .backgroundColor("#6ecdff")
                .axesTextColor(AAColor.whiteColor())
                .axesTextSize(8f)
                .tooltipValueSuffix("bpm")
                .zoomType(AAChartZoomType.X)
                .series(
                        if (heartList.isNotEmpty())
                            arrayOf(
                                    AASeriesElement()
                                            .name(mActivity.resources.getString(
                                                    R.string.app_main_health_heart_rate))
                                            .data(heartList)
                                            .fillOpacity(0.5f)
                                            .lineWidth(1f)
                            )
                        else arrayOf()
                )
        val aaOptions = AAOptionsConstructor.configureChartOptions(aaChartModel)
        aaOptions.yAxis!!.gridLineColor("#FFFFFF4D").tickPositions(getHeartYAxisLabels())
        aaOptions.xAxis!!.lineColor("#FFFFFF4D")
        return aaOptions
    }

    private fun initBloodChart() {
        val options = getHeartXAxisLabels(arrayListOf())
        val aaOptions = initBloodLineChart(options, arrayOf(), arrayOf())
        bloodChart.aa_drawChartWithChartOptions(aaOptions)
    }

    private fun getBloodXAxisLabels(list: List<BloodListBeann>): Array<String> {
        if (list.isNullOrEmpty()) return arrayOf()
        val category = arrayListOf<String>()
        for (i in list.indices) {
            category.add(DateUtilKotlin.uTCToLocal(list[i].testTime, TIME_FORMAT_PATTERN8)!!)
        }
        return category.toTypedArray()
    }

    private fun getBloodYAxisLabels(): Array<Any> {
        return arrayOf(0, 60, 120, 180, 240)
    }

    private fun initBloodLineChart(category: Array<String>, bloodHighList: Array<Any>,
                                   bloodBelowList: Array<Any>): AAOptions {
        val aaChartModel = AAChartModel()
                .chartType(AAChartType.Spline)//图形类型
                .dataLabelsEnabled(false)
                .categories(category)
                .legendEnabled(false)
                .markerRadius(2f)
                .yAxisTitle("")
                .colorsTheme(arrayOf(
                        AAColor.whiteColor(),
                        "#5E95FF"
                ))
                .backgroundColor("#FF8147")
                .axesTextColor(AAColor.whiteColor())
                .axesTextSize(8f)
                .zoomType(AAChartZoomType.X)
                .tooltipValueSuffix("mmHg")
                .series(
                        if (bloodHighList.isNotEmpty())
                            arrayOf(
                                    AASeriesElement()
                                            .name(mActivity.resources.getString(
                                                    R.string.app_main_health_high_pressure))
                                            .data(bloodHighList)
                                            .fillOpacity(0.5f)
                                            .lineWidth(1f),
                                    AASeriesElement()
                                            .name(mActivity.resources.getString(
                                                    R.string.app_main_health_low_pressure))
                                            .data(bloodBelowList)
                                            .fillOpacity(0.5f)
                                            .lineWidth(1f)
                            )
                        else arrayOf()
                )
        val aaOptions = AAOptionsConstructor.configureChartOptions(aaChartModel)
        aaOptions.yAxis!!.gridLineColor("#FFFFFF4D").tickPositions(getBloodYAxisLabels())
        aaOptions.xAxis!!.lineColor("#FFFFFF4D")
//        var ceil = ceil((category.size / 9).toDouble())
//        if (ceil != 1.0 && category.size > 18) {
//            ceil += 1
//        }
//        aaOptions.xAxis!!.tickInterval = ceil.toInt()
        return aaOptions
    }

    private fun getTempYAxisLabels(): Array<Any> {
        return arrayOf(33, 35, 37, 39, 41)
    }

    private fun getTempXAxisLabels(list: List<BodyTempListBean>): Array<String> {
        if (list.isNullOrEmpty()) return arrayOf()
        val category = arrayListOf<String>()
        for (i in list.indices) {
            category.add(
                    DateUtilKotlin.uTCToLocal(list[i].testTime, TIME_FORMAT_PATTERN8)!!)
        }
        return category.toTypedArray()
    }

    override fun onNoDoubleClick(v: View) {
        when (v) {
            bloodWatchMore -> {
                val intent = Intent(mActivity, HealthDataListActivity::class.java)
                intent.putExtra("type",
                        resources.getString(R.string.app_main_health_blood_pressure))
                startActivity(intent)
            }
            heartWatchMore -> {
                val intent = Intent(mActivity, HealthDataListActivity::class.java)
                intent.putExtra("type",
                        resources.getString(R.string.app_main_health_heart_rate))
                startActivity(intent)
            }
            tempWatchMore -> {
                val intent = Intent(mActivity, HealthDataListActivity::class.java)
                intent.putExtra("type",
                        resources.getString(R.string.app_main_health_body_temp))
                startActivity(intent)
            }
            sleepLayout -> {
//                val intent = Intent(mActivity, HealthDataListActivity::class.java)
//                intent.putExtra("type",
//                        resources.getString(R.string.app_main_health_sleep))
//                startActivity(intent)
            }
            sportWatchMore -> {
                val intent = Intent(mActivity, HealthDataListActivity::class.java)
                intent.putExtra("type",
                        resources.getString(R.string.app_main_health_sport))
                startActivity(intent)
            }
        }
    }

    private var touchListener: View.OnTouchListener = object : View.OnTouchListener {
        var ratio = 1.8f //水平和竖直方向滑动的灵敏度,偏大是水平方向灵敏
        var x0 = 0f
        var y0 = 0f
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    x0 = event.x
                    y0 = event.y
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = abs(event.x - x0)
                    val dy = abs(event.y - y0)
                    x0 = event.x
                    y0 = event.y
                    contentScrollview.requestDisallowInterceptTouchEvent(
                            dx * ratio > dy)
                }
                MotionEvent.ACTION_UP -> {
                    v.performClick()
                }
            }
            return false
        }
    }
}