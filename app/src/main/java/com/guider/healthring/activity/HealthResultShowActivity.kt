package com.guider.healthring.activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.aigestudio.wheelpicker.widgets.DatePick
import com.aigestudio.wheelpicker.widgets.DatePick.OnDatePickedListener
import com.guider.health.apilib.ApiCallBack
import com.guider.health.apilib.ApiUtil
import com.guider.health.apilib.IUserHDApi
import com.guider.health.apilib.model.HeartMeasureResultListBean
import com.guider.health.common.core.DateUtil
import com.guider.health.common.device.Unit
import com.guider.health.common.utils.StringUtil
import com.guider.health.common.utils.UnitUtil
import com.guider.healthring.MyApp
import com.guider.healthring.R
import com.guider.healthring.adpter.HealthResultListTitleAdapter
import com.guider.healthring.adpter.HealthResultShowAdapter
import com.guider.healthring.bean.ListTitleSelectBean
import com.guider.healthring.siswatch.WatchBaseActivity
import com.guider.healthring.util.*
import kotlinx.android.synthetic.main.activity_health_result_show.*
import kotlinx.android.synthetic.main.b18i_titil_bar_white.*
import lecho.lib.hellocharts.gesture.ContainerScrollType
import lecho.lib.hellocharts.gesture.ZoomType
import lecho.lib.hellocharts.model.*
import retrofit2.Call
import retrofit2.Response


class HealthResultShowActivity : WatchBaseActivity(), OnNoDoubleClickListener {

    private lateinit var adapter: HealthResultShowAdapter
    private var gluList = arrayListOf<HeartMeasureResultListBean>()
    private lateinit var titleListAdapter: HealthResultListTitleAdapter
    private var titleList = arrayListOf<ListTitleSelectBean>()
    private var accountId = 0
    private var titleOldPosition = 0
    private var startTime: String? = ""
    private var endTime: String? = ""
    private val mPointValues = arrayListOf<PointValue>()
    private val mAxisValues = arrayListOf<AxisValue>()
    private var yMaxValue = 0f
    private val unit = Unit()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_result_show)
        initView()
        initData()
    }

    private fun initData() {
        accountId = (SharedPreferencesUtils.getParam(MyApp.getContext(),
                "accountIdGD", 0L) as Long).toInt()
        adapter = HealthResultShowAdapter(mContext, gluList)
        result_rv.adapter = adapter
        titleList.add(ListTitleSelectBean(mContext.getString(R.string.measure_glucose),
                true))
        titleList.add(ListTitleSelectBean(mContext.getString(R.string.measure_oxygen)))
        titleList.add(ListTitleSelectBean(mContext.getString(R.string.blood_flow_velocity)))
        titleList.add(ListTitleSelectBean(mContext.getString(R.string.heartbeats_per_minute)))
        titleList.add(ListTitleSelectBean(mContext.getString(R.string.measure_hemoglobin)))
        titleListAdapter = HealthResultListTitleAdapter(mContext, titleList)
        titleListAdapter.setListener(object : HealthResultListTitleAdapter.OnClickItemListener {
            override fun onClick(position: Int) {
                if (position != titleOldPosition) {
                    titleList[titleOldPosition].isSelected = false
                    titleList[position].isSelected = true
                    titleListAdapter.notifyItemChanged(titleOldPosition)
                    titleListAdapter.notifyItemChanged(position)
                    titleOldPosition = position
                    startTime = ""
                    endTime = ""
                    initDataShow()
                }
            }

        })
        listTitleRv.adapter = titleListAdapter
        startMeasure.setOnClickListener(this)
        image_back.setOnClickListener(this)
        startTimeTv.setOnClickListener(this)
        endTimeTv.setOnClickListener(this)
        initDataShow()
    }

    @SuppressLint("SetTextI18n")
    private fun initDataShow() {
        initUnitShow()
        if (titleOldPosition == 0 || titleOldPosition == 2 || titleOldPosition == 4) {
            //血糖，血流速度，血红蛋白都从血糖接口获取
            gluMeasureResult()
        } else if (titleOldPosition == 1) {
            //血氧
            boMeasureResult()
        } else {
            //心率
            hbMeasureResult()
        }

    }

    private fun hbMeasureResult() {
        showLoadingDialog("加载中...")
        ApiUtil.createHDApi(IUserHDApi::class.java)
                .getHbDayLatestMeasureListByTime(accountId, startTime, endTime).enqueue(
                        object : ApiCallBack<List<HeartMeasureResultListBean>?>() {

                            override fun onApiResponse(call: Call<List<HeartMeasureResultListBean>?>,
                                                       response: Response<List<HeartMeasureResultListBean>?>) {
                                response.let {
                                    if (!it.body().isNullOrEmpty()) {
                                        noDataLayout.visibility = View.GONE
                                        result_rv.visibility = View.VISIBLE
                                        latest_value.text = it.body()!![0].hb.toString()
                                        val tempList =
                                                it.body() as ArrayList<HeartMeasureResultListBean>
                                        gluList = tempList.filter { bean ->
                                            bean.hb != 0
                                        } as ArrayList<HeartMeasureResultListBean>
                                        adapter.setTypeValue(titleOldPosition)
                                        adapter.setSourceList(gluList)
                                        drawLineChart(gluList)
                                    }
                                }
                            }

                            override fun onApiResponseNull(call: Call<List<HeartMeasureResultListBean>?>,
                                                           response: Response<List<HeartMeasureResultListBean>?>) {
                                noDataLayout.visibility = View.VISIBLE
                                result_rv.visibility = View.GONE
                                latest_value.text = "0"
                                drawLineChart(arrayListOf())
                            }

                            override fun onRequestFinish() {
                                hideLoadingDialog()
                            }
                        }
                )
    }

    @SuppressLint("SetTextI18n")
    private fun boMeasureResult() {
        showLoadingDialog("加载中...")
        ApiUtil.createHDApi(IUserHDApi::class.java)
                .getBoDayLatestMeasureListByTime(accountId, startTime, endTime).enqueue(
                        object : ApiCallBack<List<HeartMeasureResultListBean>?>() {


                            override fun onApiResponse(call: Call<List<HeartMeasureResultListBean>?>,
                                                       response: Response<List<HeartMeasureResultListBean>?>) {
                                response.let {
                                    if (!it.body().isNullOrEmpty()) {
                                        noDataLayout.visibility = View.GONE
                                        result_rv.visibility = View.VISIBLE
                                        latest_value.text = it.body()!![0].bo.toString()
                                        val tempList =
                                                it.body() as ArrayList<HeartMeasureResultListBean>
                                        gluList = tempList.filter { bean ->
                                            bean.bo != 0
                                        } as ArrayList<HeartMeasureResultListBean>
                                        adapter.setTypeValue(titleOldPosition)
                                        adapter.setSourceList(gluList)
                                        drawLineChart(gluList)
                                    }
                                }
                            }

                            override fun onApiResponseNull(call: Call<List<HeartMeasureResultListBean>?>,
                                                           response: Response<List<HeartMeasureResultListBean>?>) {
                                noDataLayout.visibility = View.VISIBLE
                                result_rv.visibility = View.GONE
                                latest_value.text = "0"
                                drawLineChart(arrayListOf())
                            }

                            override fun onRequestFinish() {
                                hideLoadingDialog()
                            }
                        }
                )
    }

    private fun gluMeasureResult() {
        showLoadingDialog("加载中...")
        ApiUtil.createHDApi(IUserHDApi::class.java)
                .getBsDayLatestMeasureListByTime(accountId, startTime, endTime).enqueue(
                        object : ApiCallBack<List<HeartMeasureResultListBean>?>() {

                            override fun onApiResponse(call: Call<List<HeartMeasureResultListBean>?>?,
                                                       response: Response<List<HeartMeasureResultListBean>?>?) {
                                response.let {
                                    if (!it?.body().isNullOrEmpty()) {
                                        noDataLayout.visibility = View.GONE
                                        result_rv.visibility = View.VISIBLE
                                        val tempList =
                                                it?.body() as ArrayList<HeartMeasureResultListBean>
                                        when (titleOldPosition) {
                                            0 -> {
                                                // 根据国别处理血糖单位
                                                val iUnit = UnitUtil.getIUnit()
                                                val value = iUnit.getGluShowValue(
                                                        it.body()!![0].bs, 2)
                                                latest_value.text = value.toString()
                                                gluList = tempList.filter { bean ->
                                                    bean.bs != 0.0
                                                } as ArrayList<HeartMeasureResultListBean>
                                            }
                                            2 -> {
                                                latest_value.text = it.body()!![0].bloodSpeed.toString()
                                                gluList = tempList.filter { bean ->
                                                    bean.bloodSpeed != 0
                                                } as ArrayList<HeartMeasureResultListBean>
                                            }
                                            4 -> {
                                                latest_value.text = it.body()!![0].hemoglobin.toString()
                                                gluList = tempList.filter { bean ->
                                                    bean.hemoglobin != 0
                                                } as ArrayList<HeartMeasureResultListBean>
                                            }
                                        }
                                        adapter.setTypeValue(titleOldPosition)
                                        adapter.setSourceList(gluList)
                                        drawLineChart(gluList)
                                    }
                                }
                            }

                            override fun onApiResponseNull(call: Call<List<HeartMeasureResultListBean>?>,
                                                           response: Response<List<HeartMeasureResultListBean>?>) {
                                noDataLayout.visibility = View.VISIBLE
                                result_rv.visibility = View.GONE
                                latest_value.text = "0"
                                drawLineChart(arrayListOf())
                            }

                            override fun onRequestFinish() {
                                hideLoadingDialog()
                            }
                        }
                )
    }

    private fun drawLineChart(list: ArrayList<HeartMeasureResultListBean>) {
        val tempList = arrayListOf<HeartMeasureResultListBean>()
        if (!list.isNullOrEmpty()) {
            tempList.addAll(list)
            tempList.reverse()
        }
        getAxisLabel(tempList)
        getAxisPoints(tempList)
        initLineChart()
    }

    /**
     * X 轴的显示
     */
    private fun getAxisLabel(list: ArrayList<HeartMeasureResultListBean>) {
        if (!list.isNullOrEmpty()) {
            mAxisValues.clear()
            mAxisValues.add(AxisValue(0f).setLabel("0"))
            for (i in 0 until list.size) {
                val temp = DateUtilKotlin.uTCToLocal(list[i].testTime, "yyyy-MM-dd")
                val testTimeValue = temp?.substring(temp.indexOf("-") + 1)
                mAxisValues.add(AxisValue((i + 1).toFloat()).setLabel(testTimeValue))
            }
        }
    }

    /**
     * 图表的每个点的显示
     */
    private fun getAxisPoints(list: ArrayList<HeartMeasureResultListBean>) {
        mPointValues.clear()
        if (!list.isNullOrEmpty()) {
            mPointValues.add(PointValue(0f, 0f))
            yMaxValue = 0f
            for (i in 0 until list.size) {
                val temp = DateUtilKotlin.uTCToLocal(list[i].testTime, "yyyy-MM-dd")
                val testTimeValue = temp?.substring(temp.indexOf("-") + 1)
                //血糖  血氧  血流速度  心率  血红蛋白
                var pointValue = 0f
                when (titleOldPosition) {
                    0 -> {
                        pointValue = list[i].bs.toFloat()
                    }
                    1 -> {
                        pointValue = list[i].bo.toFloat()
                    }
                    2 -> {
                        pointValue = list[i].bloodSpeed.toFloat()
                    }
                    3 -> {
                        pointValue = list[i].hb.toFloat()
                    }
                    4 -> {
                        pointValue = list[i].hemoglobin.toFloat()
                    }
                }
                if (yMaxValue < pointValue) yMaxValue = pointValue
                if (pointValue > 0f)
                    mPointValues.add(PointValue((i + 1).toFloat(), pointValue))
                Log.d("LineChartData", "=========X,Y数据$testTimeValue===$pointValue")
            }
            yMaxValue += if (yMaxValue < 100) 2 else 10
        }
    }

    /**
     * 绘制折线图
     */
    private fun initLineChart() {
        //折线的颜色
        val lines = arrayListOf<Line>()
        val line = Line(mPointValues)
                .setColor(ContextCompat.getColor(mContext, R.color.colorF88B5E))
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
        line.setHasLines(true) //是否用直线显示。如果为false 则没有曲线只有点显示
        line.setHasPoints(true) //是否显示圆点 如果为false 则没有原点只有点显示
        lines.add(line)
        val data = LineChartData()
        data.lines = lines
        data.baseValue = Float.NEGATIVE_INFINITY  //设置基准数(大概是数据范围)
        //坐标轴
        //X轴
        val axisX = Axis() //X轴
        //对x轴，数据和属性的设置
        //对x轴，数据和属性的设置
        axisX.textSize = 9 //设置字体的大小
        axisX.setHasTiltedLabels(false) //x坐标轴字体是斜的显示还是直的，true表示斜的
        axisX.textColor = Color.GRAY //X轴灰色
        axisX.values = mAxisValues //设置x轴各个坐标点名称
        data.axisXBottom = axisX //x 轴在底部
        //Y轴
        val axisY = Axis().setHasLines(true)
        axisY.textColor = Color.GRAY //Y轴灰色
        data.axisYLeft = axisY //设置Y轴位置 左边
        //设置行为属性，支持缩放、滑动以及平移
        measureLineChart.isInteractive = true
        measureLineChart.zoomType = ZoomType.HORIZONTAL
        measureLineChart.maxZoom = 2f//最大方法比例
        measureLineChart.setContainerScrollEnabled(
                true, ContainerScrollType.HORIZONTAL)
        measureLineChart.lineChartData = data
        resetViewport()
    }

    /**
     * 重点方法，计算绘制图表
     */
    private fun resetViewport() {
        //创建一个图标视图 大小为控件的最大大小
        val v = Viewport(measureLineChart.maximumViewport)
        v.top = yMaxValue
        v.bottom = 0f
        measureLineChart.maximumViewport = v //给最大的视图设置 相当于原图
        v.left = -0.2f
        v.right = if (gluList.size > 5) {
            5f
        } else {
            gluList.size.toFloat()
        }
        measureLineChart.currentViewport = v //给当前的视图设置 相当于当前展示的图
        measureLineChart.postInvalidate()
    }

    private fun initView() {
        bar_titles.text = mContext.getString(R.string.btn_health)
        result_rv.layoutManager = LinearLayoutManager(this)
        result_rv.isNestedScrollingEnabled = true
        listTitleRv.layoutManager = LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false)
        listTitleRv.isNestedScrollingEnabled = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            measureLineChart.isNestedScrollingEnabled = true
        }
        endTimeTv.text = CommonUtil.getCurrentDate()
        startTimeTv.text = CommonUtil.calTimeFrontDate(CommonUtil.getCurrentDate(), 30)
        initUnitShow()
    }

    private fun onDateRangePicker(type: String) {
        val pickerPopWin = DatePick.Builder(
                mContext,
                OnDatePickedListener { year: Int, month: Int, day: Int, _: String ->
                    timeCalAndShow(year, month, day, type)
                }).textConfirm(resources.getString(R.string.confirm)) //text of confirm button
                .textCancel(resources.getString(R.string.cancle)) //text of cancel button
                .btnTextSize(16) // button text size
                .viewTextSize(25) // pick view text size
                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                .colorConfirm(Color.parseColor("#009900")) //color of confirm button
                .minYear(1900) //min year in loop
                .maxYear(2050) // max year in loop
                .dateChose(if (type == "start") {
                    //开始时间
                    startTimeTv.text.toString()
                            .replace('/', '-')
                            .replace('/', '-')
                } else {
                    //结束时间
                    endTimeTv.text.toString()
                            .replace('/', '-')
                            .replace('/', '-')
                }) //可以复现选择值
                .build()
        pickerPopWin.showPopWin(this)
    }

    /**
     * 计算显示时间选择的值和刷新数据显示
     */
    private fun timeCalAndShow(year: Int, month: Int, day: Int, type: String) {
        val selectDate = year.toString().plus("/")
                .plus(if (month < 10) {
                    "0$month"
                } else month)
                .plus("/").plus(if (day < 10) {
                    "0$day"
                } else day)
        if (type == "start") {
            //选择开始时间
            //结束时间比开始时间必须大，且结束时间不能比开始时间长30天
            if (!StringUtil.isEmpty(endTimeTv.text.toString()) &&
                    (CommonUtil.calTimeDateCompareNew(
                            selectDate, endTimeTv.text.toString())
                            || CommonUtil.differentDays(
                            DateUtil.stringToDate(selectDate, "yyyy/MM/dd"),
                            DateUtil.stringToDate(endTimeTv.text.toString(),
                                    "yyyy/MM/dd")
                    ) > 30)
            ) {
                //如果成立说明开始时间大于结束时间，不符合要求
                ToastUtil.showShort(mContext, "选择的时间不符合要求")
            } else {
                startTimeTv.text = selectDate
                val startTimeValue = selectDate.replace('/', '-')
                        .replace('/', '-').plus(" 00:00:00")
                val endTimeValue = endTimeTv.text
                        .toString().replace('/', '-')
                        .replace('/', '-').plus(" 00:00:00")
                startTime = DateUtilKotlin.localToUTC(startTimeValue)
                endTime = DateUtilKotlin.localToUTC(endTimeValue)
                initDataShow()
            }
        } else {
            //选择结束时间
            if (!StringUtil.isEmpty(startTimeTv.text.toString()) &&
                    (CommonUtil.calTimeDateCompareNew(
                            startTimeTv.text.toString(), selectDate)
                            || CommonUtil.differentDays(
                            DateUtil.stringToDate(startTimeTv.text.toString(),
                                    "yyyy/MM/dd"),
                            DateUtil.stringToDate(selectDate, "yyyy/MM/dd")
                    ) > 30)
            ) {
                ToastUtil.showShort(mContext, "选择的时间不符合要求")
            } else {
                endTimeTv.text = selectDate
                val endTimeValue = selectDate.replace('/', '-')
                        .replace('/', '-').plus(" 00:00:00")
                val startTimeValue = startTimeTv.text
                        .toString().replace('/', '-')
                        .replace('/', '-').plus(" 00:00:00")
                startTime = DateUtilKotlin.localToUTC(startTimeValue)
                endTime = DateUtilKotlin.localToUTC(endTimeValue)
                initDataShow()
            }
        }
    }

    private fun initUnitShow() {
        latest_value_unit.text = when (titleOldPosition) {
            0 -> {
                mContext.resources.getString(R.string.glu_unit_food_2)
            }
            1 -> {
                unit.bloodO2
            }
            2 -> {
                unit.bloodFlow
            }
            3 -> {
                unit.heart
            }
            else -> {
                unit.hemoglobin
            }
        }
    }

    override fun onNoDoubleClick(v: View) {
        when (v) {
            startMeasure -> {
                DeviceActivityGlu.startGlu(this@HealthResultShowActivity, accountId)
            }
            image_back -> {
                finish()
            }
            startTimeTv -> {
                onDateRangePicker("start")
            }
            endTimeTv -> {
                onDateRangePicker("end")
            }
        }
    }

}