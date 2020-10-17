package com.guider.gps.view.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayout
import com.guider.baselib.base.BaseFragment
import com.guider.baselib.utils.*
import com.guider.baselib.widget.aAInfographicsLib.aAChartCreator.*
import com.guider.baselib.widget.aAInfographicsLib.aAOptionsModel.AALabels
import com.guider.baselib.widget.aAInfographicsLib.aAOptionsModel.AAOptions
import com.guider.baselib.widget.aAInfographicsLib.aAOptionsModel.AAStyle
import com.guider.baselib.widget.aAInfographicsLib.aATools.AAColor
import com.guider.baselib.widget.dialog.DialogProgress
import com.guider.gps.R
import com.guider.gps.view.activity.HealthDataListActivity
import com.guider.gps.view.activity.MainActivity
import com.guider.health.apilib.GuiderApiUtil
import com.guider.health.apilib.bean.*
import kotlinx.android.synthetic.main.fragment_health_data.*
import kotlinx.android.synthetic.main.fragment_home_health.*
import kotlinx.coroutines.launch
import lecho.lib.hellocharts.model.*
import lecho.lib.hellocharts.view.AbstractChartView
import lecho.lib.hellocharts.view.LineChartView
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.math.abs


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
    private val mAxisValues = arrayListOf<AxisValue>()
    private lateinit var bloodChart: LineChartView
    private lateinit var heartChart: LineChartView
    private var isFirstLoadData = true
    private var isRefresh = false
    private var mDialog: DialogProgress? = null

    override fun initView(rootView: View) {
        bloodLayout = rootView.findViewById(R.id.bloodLayout)
        tempLayout = rootView.findViewById(R.id.tempLayout)
        heartLayout = rootView.findViewById(R.id.heartLayout)
        sleepLayout = rootView.findViewById(R.id.sleepLayout)
        sportLayout = rootView.findViewById(R.id.sportLayout)
        bloodChart = rootView.findViewById(R.id.bloodChart)
        heartChart = rootView.findViewById(R.id.heartChart)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initLogic() {
        tabInit()
        tabChangeEvent()
        bloodLayout.setOnClickListener(this)
        tempLayout.setOnClickListener(this)
        heartLayout.setOnClickListener(this)
        sleepLayout.setOnClickListener(this)
        sportLayout.setOnClickListener(this)
        getAxisLabel()
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
        val targetSteps = if (MMKVUtil.getInt(TARGET_STEP, 0) != 0) {
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
    }

    override fun openEventBus(): Boolean {
        return true
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshTargetStep(event: EventBusEvent<Int>) {
        if (event.code == EventBusAction.REFRESH_TARGET_STEP) {
            targetStepTv.text = event.data.toString()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshHealthData(event: EventBusEvent<String>) {
        if (event.code == EventBusAction.REFRESH_HEALTH_DATA) {
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
    }

    private fun getSportData() {
        val accountId = MMKVUtil.getInt(BIND_DEVICE_ACCOUNT_ID)
        lifecycleScope.launch {
            try {
                val resultBean = GuiderApiUtil.getHDApiService()
                        .getHealthSportChartData(accountId, -1, 100,
                                startTimeValue, endTimeValue)
                if (!resultBean.isNullOrEmpty()) {
                    sportNoDataTv.visibility = View.GONE
                    val sportSubColumnValue = getSportSubColumnValue(resultBean)
                    initSportColumn(sportSubColumnValue, resultBean)
                    //总步数为一天时间段的步数总和
                    var stepTotal = 0
                    resultBean.forEach {
                        stepTotal += it.step
                    }
                    stepNumTv.text = stepTotal.toString()
//                    val targetSteps = if (MMKVUtil.getInt(TARGET_STEP,
//                                    0) != 0) {
//                        MMKVUtil.getInt(TARGET_STEP, 0)
//                    } else 8000
//                    val str: String = String.format("%.2f",
//                            (4927 * 1.00f / targetSteps))
//                    val progress: Int = (str.toFloat() * 100).toInt()
                } else {
                    initSportChart()
                    stepNumTv.text = "0"
                    sportNoDataTv.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                showToast(e.message!!)
            }
        }
    }

    private fun getSleepData() {
        val accountId = MMKVUtil.getInt(BIND_DEVICE_ACCOUNT_ID)
        lifecycleScope.launch {
            try {
                val resultBean = GuiderApiUtil.getHDApiService()
                        .getHealthSleepChartData(accountId, startTimeValue)
                if (!resultBean.isNullOrEmpty()) {
                    sleepNoDataTv.visibility = View.GONE
                    val sleepSubColumnValue = getSleepSubColumnValue(resultBean)
                    showSleepTime(resultBean)
                    initSleepColumn(sleepSubColumnValue, resultBean)
                } else {
                    sleepTimeTv.text = ""
                    initSleepChart()
                    sleepNoDataTv.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                showToast(e.message!!)
            }
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
                    list[list.size - 1].startTime, TIME_FORMAT_PATTERN8)
            sleepTimeTv.text = mActivity.resources.getString(
                    R.string.app_main_health_time_sleep, startTime, endTime)
        }
    }

    private fun getHeartData() {
        val accountId = MMKVUtil.getInt(BIND_DEVICE_ACCOUNT_ID)
        lifecycleScope.launch {
            try {
                val resultBean = GuiderApiUtil.getHDApiService()
                        .getHealthHeartChartData(accountId, -1, 100,
                                startTimeValue, endTimeValue)
                if (!resultBean.isNullOrEmpty()) {
                    heartNoDataTv.visibility = View.GONE
                    val heartAxisPoints = getHeartAxisPoints(resultBean)
                    initHeartLineChart(heartAxisPoints, resultBean)
                } else {
                    initHeartChart()
                    heartNoDataTv.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                showToast(e.message!!)
            }
        }
    }

    private fun getBodyTempData() {
        val accountId = MMKVUtil.getInt(BIND_DEVICE_ACCOUNT_ID)
        lifecycleScope.launch {
            try {
                val resultBean = GuiderApiUtil.getHDApiService()
                        .getHealthTempChartData(accountId,
                                -1, 9, startTimeValue, endTimeValue)
                if (!resultBean.isNullOrEmpty()) {
                    tempNoDataTv.visibility = View.GONE
                    val options = getTempXAxisLabels(resultBean)
                    val yAxisLabels = getTempYAxisLabels()
                    val tempDataList = resultBean.map { it.bodyTemp }
                    val dataArray: Array<Any> = tempDataList.toTypedArray()
                    val aaOptions = initTempLineChart(options, dataArray)
                    aaOptions.yAxis!!.labels(yAxisLabels)
                    Log.i(TAG, "体温数据的个数为${tempDataList.size}")
                    val requestPage: Int
                    if (tempDataList.size > 40) {
                        requestPage = tempDataList.size / 40
                        Log.e(TAG, "需要展示的页数为${requestPage}")
                        tempChart.contentWidth =
                                (ScreenUtils.widthPixels(mActivity) * requestPage).toFloat()
                    } else if (tempDataList.size in 31..40) {
                        requestPage = tempDataList.size / 30
                        Log.e(TAG, "需要展示的页数为${requestPage}")
                        tempChart.contentWidth =
                                (ScreenUtils.widthPixels(mActivity) * requestPage).toFloat()
                    }
                    tempChart.aa_drawChartWithChartOptions(aaOptions)
                } else {
                    initTempChart()
                    tempNoDataTv.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                showToast(e.message!!)
            }
        }
    }

    private fun getBloodData() {
        mDialog = DialogProgress(mActivity, null, true)
        if (!isFirstLoadData && !isRefresh) mDialog?.showDialog()
        val accountId = MMKVUtil.getInt(BIND_DEVICE_ACCOUNT_ID)
        lifecycleScope.launch {
            try {
                val resultBean = GuiderApiUtil.getHDApiService()
                        .getHealthBloodChartData(
                                accountId, -1, 100, startTimeValue, endTimeValue)
                if (!isFirstLoadData && !isRefresh) mDialog?.hideDialog()
                if (!resultBean.isNullOrEmpty()) {
                    bloodPressureNoDataTv.visibility = View.GONE
                    if (isRefresh) refreshLayout.finishRefresh(500)
                    val belowBloodAxisPoints = getBelowBloodAxisPoints(resultBean)
                    val highBloodAxisPoints = getHighBloodAxisPoints(resultBean)
                    initBloodLineChart(belowBloodAxisPoints, highBloodAxisPoints, resultBean)
                } else {
                    if (isRefresh) {
                        refreshLayout.finishRefresh()
                    }
                    initBloodChart()
                    bloodPressureNoDataTv.visibility = View.VISIBLE
                }
                isRefresh = false
            } catch (e: Exception) {
                if (!isFirstLoadData && !isRefresh) mDialog?.hideDialog()
                isRefresh = false
                if (isRefresh) refreshLayout.finishRefresh()
                showToast(e.message!!)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mDialog != null) {
            mDialog?.hideDialog()
            mDialog = null
        }
    }

    private fun initSportChart() {
        initSportColumn(arrayListOf(), arrayListOf())
    }

    private fun initSportColumn(columns: ArrayList<Column>, list: List<SportListBean>) {
        val mColumnChartData = ColumnChartData(columns) //设置数据
        mColumnChartData.isStacked = false //设置是否堆叠
        mColumnChartData.fillRatio = 0.2f//设置柱子宽度 0-1之间
        //坐标轴
        setSportAxisXShowColumn(mColumnChartData, list)
        setAxisYShowColumn(mColumnChartData)
        sportChart.columnChartData = mColumnChartData
        resetViewport(sportYMaxValue, sportChart, list)
    }

    private fun initSleepChart() {
        initSleepColumn(arrayListOf(), arrayListOf())
    }

    private fun initSleepColumn(columns: ArrayList<Column>, list: List<SleepDataListBean>) {
        val mColumnChartData = ColumnChartData(columns) //设置数据
        mColumnChartData.isStacked = false //设置是否堆叠
        mColumnChartData.fillRatio = 0.5f//设置柱子宽度 0-1之间
        //坐标轴
        setSleepAxisXShowColumn(mColumnChartData, list)
        setAxisYShowColumn(mColumnChartData)
        sleepChart.maxZoom = (list.size / 6 + 66).toFloat()//按照柱体数量增加缩放次数
        sleepChart.columnChartData = mColumnChartData
        resetViewport(sleepYMaxValue, sleepChart, list)
    }

    private fun setSleepAxisXShowColumn(data: ColumnChartData, list: List<SleepDataListBean>) {
        //X轴
        val axisX = Axis()//X轴
        //对x轴，数据和属性的设置
        axisX.textSize = 11 //设置字体的大小
        axisX.setHasTiltedLabels(false) //x坐标轴字体是斜的显示还是直的，true表示斜的
        axisX.textColor = Color.WHITE //X轴灰色
        val mAxisValues = arrayListOf<AxisValue>()
        for (i in list.indices) {
            val timeFormat = DateUtilKotlin.timeFormat(list[i].startTime, TIME_FORMAT_PATTERN8)!!
            val label = AxisValue((i).toFloat()).setLabel(timeFormat)
            mAxisValues.add(label)
        }
        axisX.values = mAxisValues //设置x轴各个坐标点名称
        data.axisXBottom = axisX //x 轴在底部
    }

    private fun setSportAxisXShowColumn(data: ColumnChartData, list: List<SportListBean>) {
        //X轴
        val axisX = Axis()//X轴
        //对x轴，数据和属性的设置
        axisX.textSize = 11 //设置字体的大小
        axisX.setHasTiltedLabels(false) //x坐标轴字体是斜的显示还是直的，true表示斜的
        axisX.textColor = Color.WHITE //X轴白色
        val mAxisValues = arrayListOf<AxisValue>()
        for (i in list.indices) {
            val label = AxisValue((i).toFloat()).setLabel(
                    DateUtilKotlin.uTCToLocal(list[i].testTime, TIME_FORMAT_PATTERN8)!!)
            mAxisValues.add(label)
        }
        axisX.values = mAxisValues //设置x轴各个坐标点名称
        data.axisXBottom = axisX //x 轴在底部
    }

    private fun setAxisYShowColumn(data: ColumnChartData) {
        //Y轴
        val axisY = Axis().setHasLines(true)
        axisY.textSize = 11//设置字体大小
        axisY.textColor = Color.WHITE //Y轴灰色
        axisY.maxLabelChars = 4
        data.axisYLeft = axisY //设置Y轴位置 左边
    }

    private fun initTempChart() {
        val options = getTempXAxisLabels(arrayListOf())
        val yAxisLabels = getTempYAxisLabels()
        val aaOptions = initTempLineChart(options, arrayOf())
        aaOptions.yAxis?.labels(yAxisLabels)
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
                .zoomType(AAChartZoomType.X)
                .series(
                        if (bodyTempList.isNotEmpty())
                            arrayOf(
                                    AASeriesElement()
                                            .name("体温")
                                            .data(bodyTempList)
                                            .fillOpacity(0.5f)
                                            .lineWidth(1f)
                                            .zones(zonesArr)
                            )
                        else arrayOf()
                )

        return AAOptionsConstructor.configureChartOptions(aaChartModel)
    }

    private fun initHeartChart() {
        initHeartLineChart(arrayListOf(), arrayListOf())
    }

    private fun initHeartLineChart(heartAxisPoints: ArrayList<PointValue>,
                                   body: List<HeartRateListBean>) {
        //折线的颜色
        val lines = arrayListOf<Line>()
        val line1 = Line(heartAxisPoints)
                .setColor(ContextCompat.getColor(mActivity, R.color.white))
        if (body.size == 1) {
            line1.setHasLines(false) //是否用直线显示。如果为false 则没有曲线只有点显示
        } else {
            line1.setHasLines(true) //是否用直线显示。如果为false 则没有曲线只有点显示
        }
        commonLineSetInit(line1, lines)
        val data = LineChartData()
        data.lines = lines
        data.baseValue = Float.NEGATIVE_INFINITY  //设置基准数(大概是数据范围)
        //坐标轴
        setHeartAxisXShow(data, body)
        setAxisYShow(data)
        heartChart.lineChartData = data
        resetViewport(heartYMaxValue, heartChart, heartAxisPoints)
    }

    private fun initBloodChart() {
        initBloodLineChart(arrayListOf(), arrayListOf(), arrayListOf())
    }

    private fun initBloodLineChart(belowBloodAxisPoints: ArrayList<PointValue>,
                                   highBloodAxisPoints: ArrayList<PointValue>,
                                   list: List<BloodListBeann>) {
        //折线的颜色
        val lines = arrayListOf<Line>()
        val line1 = Line(belowBloodAxisPoints)
                .setColor(ContextCompat.getColor(mActivity, R.color.color5E95FF))
        val line2 = Line(highBloodAxisPoints)
                .setColor(ContextCompat.getColor(mActivity, R.color.white))
        if (list.size == 1) {
            line1.setHasLines(false) //是否用直线显示。如果为false 则没有曲线只有点显示
            line2.setHasLines(false) //是否用直线显示。如果为false 则没有曲线只有点显示
        } else {
            line1.setHasLines(true) //是否用直线显示。如果为false 则没有曲线只有点显示
            line2.setHasLines(true) //是否用直线显示。如果为false 则没有曲线只有点显示
        }
        commonLineSetInit(line1, lines)
        commonLineSetInit(line2, lines)
        val data = LineChartData()
        data.lines = lines
        data.baseValue = Float.NEGATIVE_INFINITY  //设置基准数(大概是数据范围)
        //坐标轴
        setBloodAxisXShow(data, list)
        setAxisYShow(data)
        bloodChart.lineChartData = data
        resetViewport(bloodYMaxValue, bloodChart, belowBloodAxisPoints)
        //创建一个图标视图 大小为控件的最大大小
    }

    private fun setBloodAxisXShow(data: LineChartData, list: List<BloodListBeann>) {
        //X轴
        val axisX = Axis()//X轴
        //对x轴，数据和属性的设置
        axisX.textSize = 11 //设置字体的大小
        axisX.setHasTiltedLabels(false) //x坐标轴字体是斜的显示还是直的，true表示斜的
        axisX.textColor = CommonUtils.getColor(mActivity, R.color.white)
        val mAxisValues = arrayListOf<AxisValue>()
        if (list.size == 1) {
            mAxisValues.add(AxisValue(0f).setLabel("0"))
            for (i in list.indices) {
                val label = AxisValue((i + 1).toFloat()).setLabel(
                        DateUtilKotlin.uTCToLocal(list[i].testTime, TIME_FORMAT_PATTERN8)!!)
                mAxisValues.add(label)
            }
        } else
            for (i in list.indices) {
                val label = AxisValue((i).toFloat()).setLabel(
                        DateUtilKotlin.uTCToLocal(list[i].testTime, TIME_FORMAT_PATTERN8)!!)
                mAxisValues.add(label)
            }
        axisX.values = mAxisValues //设置x轴各个坐标点名称
        data.axisXBottom = axisX //x 轴在底部
    }

    /**
     * 重点方法，计算绘制图表
     */
    private fun resetViewport(yMaxValue: Float, view: AbstractChartView, list: List<Any>) {
        //创建一个图标视图 大小为控件的最大大小
        val v = Viewport(view.maximumViewport)
        v.top = yMaxValue
        v.bottom = 0f
        view.maximumViewport = v //给最大的视图设置 相当于原图
        v.left = -0.2f
        v.right = if (list.size <= 8) list.size.toFloat() else 8f
        view.currentViewport = v //给当前的视图设置 相当于当前展示的图
        view.postInvalidate()
    }

    private fun setAxisYShow(data: LineChartData) {
        //Y轴
        val axisY = Axis().setHasLines(true)
        axisY.textSize = 11//设置字体大小
        axisY.textColor = Color.WHITE //X轴白色
        axisY.maxLabelChars = 4
        data.axisYLeft = axisY //设置Y轴位置 左边
    }

    private fun setHeartAxisXShow(data: LineChartData, list: List<HeartRateListBean>) {
        //X轴
        val axisX = Axis()//X轴
        //对x轴，数据和属性的设置
        axisX.textSize = 11 //设置字体的大小
        axisX.setHasTiltedLabels(false) //x坐标轴字体是斜的显示还是直的，true表示斜的
//        if (StringUtil.isNotBlankAndEmpty(type))
//            axisX.textColor = CommonUtils.getColor(mActivity, R.color.colorF18937)
//        //X轴灰色
//        else
        axisX.textColor = Color.WHITE //X轴白色
        val mAxisValues = arrayListOf<AxisValue>()
        if (list.size == 1) {
            mAxisValues.add(AxisValue(0f).setLabel("0"))
            for (i in list.indices) {
                val label = AxisValue((i + 1).toFloat()).setLabel(
                        DateUtilKotlin.uTCToLocal(list[i].testTime, TIME_FORMAT_PATTERN8)!!)
                mAxisValues.add(label)
            }
        } else
            for (i in list.indices) {
                val label = AxisValue((i).toFloat()).setLabel(
                        DateUtilKotlin.uTCToLocal(list[i].testTime, TIME_FORMAT_PATTERN8)!!)
                mAxisValues.add(label)
            }
        axisX.values = mAxisValues //设置x轴各个坐标点名称
        data.axisXBottom = axisX //x 轴在底部
    }

    private fun commonLineSetInit(line: Line, lines: ArrayList<Line>) {
        line.shape = ValueShape.CIRCLE
        //折线图上每个数据点的形状
        // 这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.SQUARE）
        line.isCubic = true //曲线是否平滑
        line.isFilled = false //是否填充曲线的面积
        line.pointRadius = 2
        line.strokeWidth = 1
        line.setHasLabels(false) //曲线的数据坐标是否加上备注
        line.setHasLabelsOnlyForSelected(false)
        //点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
        line.setHasPoints(true) //是否显示圆点 如果为false 则没有原点只有点显示
        lines.add(line)
    }

    private fun getHighBloodAxisPoints(list: List<BloodListBeann>): ArrayList<PointValue> {
        if (list.isNullOrEmpty()) return arrayListOf()
        val pointValues = arrayListOf<PointValue>()
        val pointYValues = arrayListOf<Float>()
        if (list.size == 1) {
            pointYValues.add(0f)
        }
        list.forEach {
            pointYValues.add(it.sbp.toFloat())
        }
        for (i in 0 until pointYValues.size) {
            pointValues.add(PointValue(i.toFloat(), pointYValues[i]))
            if (bloodYMaxValue < pointYValues[i]) bloodYMaxValue = pointYValues[i]
        }
        bloodYMaxValue += 10
        return pointValues
    }

    private fun getBelowBloodAxisPoints(list: List<BloodListBeann>): ArrayList<PointValue> {
        if (list.isNullOrEmpty()) return arrayListOf()
        val pointValues = arrayListOf<PointValue>()
        val pointYValues = arrayListOf<Float>()
        if (list.size == 1) {
            pointYValues.add(0f)
        }
        list.forEach {
            pointYValues.add(it.dbp.toFloat())
        }
        for (i in 0 until pointYValues.size) {
            pointValues.add(PointValue(i.toFloat(), pointYValues[i]))
        }
        return pointValues
    }

    private fun getHeartAxisPoints(list: List<HeartRateListBean>): ArrayList<PointValue> {
        if (list.isNullOrEmpty()) return arrayListOf()
        val pointValues = arrayListOf<PointValue>()
        val pointYValues = arrayListOf<Float>()
        if (list.size == 1) {
            pointYValues.add(0f)
        }
        list.forEach {
            pointYValues.add(it.hb.toFloat())
        }
        heartYMaxValue = 0f
        for (i in 0 until pointYValues.size) {
            pointValues.add(PointValue(i.toFloat(), pointYValues[i]))
            if (heartYMaxValue < pointYValues[i]) heartYMaxValue = pointYValues[i]
        }
        heartYMaxValue += 10
        return pointValues
    }

    private fun getTempYAxisLabels(): AALabels {
        return AALabels()
                .style(
                        AAStyle()
                                .fontSize(8f)
                                .color(AAColor.whiteColor())
                )
                .formatter(
                        """
                    function () {
                            var yValue = this.value;
                            if (yValue >= 40) {
                                return "40";
                            } else if (yValue >= 39 && yValue < 40) {
                                return "39";
                            } else if (yValue >= 38 && yValue < 39) {
                                return "38";
                            } else if (yValue >= 37 && yValue < 38) {
                                return "37";
                            } else if (yValue >= 36 && yValue < 37) {
                                return "36";
                            }else {
                                return "0";
                            }
                        }
                """.trimIndent()
                )
    }

    private fun getTempXAxisLabels(list: List<BodyTempListBean>): Array<String> {
        if (list.isNullOrEmpty()) return arrayOf()
        val category = arrayListOf<String>()
//        for (i in 0..9) {
//            when (i) {
//                0 -> {
//                    category.add("00:00")
//                }
//                1, 2, 3 -> {
//                    category.add("0${i * 3}:00")
//                }
//                4, 5, 6, 7 -> {
//                    category.add("${i * 3}:00")
//                }
//                8 -> {
//                    category.add("23:59")
//                }
//            }
//        }
        for (i in list.indices) {
            category.add(
                    DateUtilKotlin.uTCToLocal(list[i].testTime, TIME_FORMAT_PATTERN8)!!)
        }
        return category.toTypedArray()
    }

    private fun getSleepSubColumnValue(subColumnValueData: List<SleepDataListBean>)
            : ArrayList<Column> {
        val columns = arrayListOf<Column>()
        for (i in subColumnValueData.indices) {
            //根据1；深度睡眠，2；浅度睡眠，3；醒来时长
            val colorIntValue = when (subColumnValueData[i].sleepType) {
                1 -> {
                    CommonUtils.getColor(mActivity, R.color.color5838F0)
                }
                2 -> {
                    CommonUtils.getColor(mActivity, R.color.colorB6A7FF)
                }
                else -> {
                    CommonUtils.getColor(mActivity, R.color.colorFFAD29)
                }
            }
            /*===== 柱状图相关设置 =====*/
            val column = Column(
                    arrayListOf(SubcolumnValue(subColumnValueData[i].minute.toFloat(), colorIntValue)))
            column.setHasLabels(true) //没有标签
            column.setHasLabelsOnlyForSelected(false)//固定显示，而非在得到点击后才进行显示
            columns.add(column)
            if (sleepYMaxValue < subColumnValueData[i].minute.toFloat()) sleepYMaxValue =
                    subColumnValueData[i].minute.toFloat()
        }
        sleepYMaxValue += 10
        return columns
    }

    private fun getSportSubColumnValue(list: List<SportListBean>): ArrayList<Column> {
        val columns = arrayListOf<Column>()
        val subColumnValueData = arrayListOf<Float>()
        list.forEach {
            subColumnValueData.add(it.step.toFloat())
        }
        for (i in 0 until subColumnValueData.size) {
            val colorIntValue = CommonUtils.getColor(mActivity, R.color.white)
            /*===== 柱状图相关设置 =====*/
            val column = Column(
                    arrayListOf(SubcolumnValue(subColumnValueData[i], colorIntValue)))
            column.setHasLabels(true) //没有标签
            column.setHasLabelsOnlyForSelected(true) //点击只放大
            columns.add(column)
            if (sportYMaxValue < subColumnValueData[i]) sportYMaxValue = subColumnValueData[i]
        }
        sportYMaxValue += 10
        return columns
    }

    private fun getAxisLabel() {
        for (i in 0..9) {
            when (i) {
                0 -> {
                    mAxisValues.add(AxisValue(i.toFloat()).setLabel("00:00"))
                }
                1, 2, 3 -> {
                    mAxisValues.add(AxisValue(i.toFloat()).setLabel("0${i * 3}:00"))
                }
                4, 5, 6, 7 -> {
                    mAxisValues.add(AxisValue(i.toFloat()).setLabel("${i * 3}:00"))
                }
                8 -> {
                    mAxisValues.add(AxisValue(i.toFloat()).setLabel("23:59"))
                }
            }
        }
    }

    override fun onNoDoubleClick(v: View) {
        when (v) {
            bloodLayout -> {
                val intent = Intent(mActivity, HealthDataListActivity::class.java)
                intent.putExtra("type",
                        resources.getString(R.string.app_main_health_blood_pressure))
                startActivity(intent)
            }
            heartLayout -> {
                val intent = Intent(mActivity, HealthDataListActivity::class.java)
                intent.putExtra("type",
                        resources.getString(R.string.app_main_health_heart_rate))
                startActivity(intent)
            }
            tempLayout -> {
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
            sportLayout -> {
                val intent = Intent(mActivity, HealthDataListActivity::class.java)
                intent.putExtra("type",
                        resources.getString(R.string.app_main_health_sport))
                startActivity(intent)
            }
        }
    }

    var touchListener: View.OnTouchListener = object : View.OnTouchListener {
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