package com.guider.gps.view.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.guider.baselib.base.BaseFragment
import com.guider.baselib.utils.StringUtils
import com.guider.baselib.widget.aAInfographicsLib.aAChartCreator.AAChartModel
import com.guider.baselib.widget.aAInfographicsLib.aAChartCreator.AAChartType
import com.guider.baselib.widget.aAInfographicsLib.aAChartCreator.AAOptionsConstructor
import com.guider.baselib.widget.aAInfographicsLib.aAChartCreator.AASeriesElement
import com.guider.baselib.widget.aAInfographicsLib.aAOptionsModel.AAOptions
import com.guider.gps.R
import com.guider.gps.view.activity.HealthDataListActivity
import kotlinx.android.synthetic.main.fragment_health_data.*
import lecho.lib.hellocharts.model.*
import lecho.lib.hellocharts.view.LineChartView


class HealthDataFragment : BaseFragment() {

    private val ARG_STR = "text"

    private lateinit var bloodLayout: ConstraintLayout
    private lateinit var tempLayout: ConstraintLayout
    private lateinit var heartLayout: ConstraintLayout
    private lateinit var sleepLayout: ConstraintLayout
    private lateinit var sportLayout: ConstraintLayout
    private var bloodYMaxValue = 0f
    private var heartYMaxValue = 0f
    private var tempYMaxValue = 0f

    //    private var sleepYMaxValue = 0f
    private val mAxisValues = arrayListOf<AxisValue>()
    private lateinit var bloodChart: LineChartView
    private lateinit var heartChart: LineChartView


    companion object {
        fun newInstance(text: String) = HealthDataFragment().apply {
            arguments = Bundle().apply { putString(ARG_STR, text) }
            TAG = text
        }
    }

    override val layoutRes: Int
        get() = R.layout.fragment_health_data


    override fun initView(rootView: View) {
        bloodLayout = rootView.findViewById(R.id.bloodLayout)
        tempLayout = rootView.findViewById(R.id.tempLayout)
        heartLayout = rootView.findViewById(R.id.heartLayout)
        sleepLayout = rootView.findViewById(R.id.sleepLayout)
        sportLayout = rootView.findViewById(R.id.sportLayout)
        bloodChart = rootView.findViewById(R.id.bloodChart)
        heartChart = rootView.findViewById(R.id.heartChart)
    }

    @SuppressLint("SetTextI18n")
    override fun initLogic() {
        arguments?.takeIf { it.containsKey(ARG_STR) }?.apply {
            val string = getString(ARG_STR)
        }
        tempStatusTv.text =
                "[37.4-38]${mActivity.resources.getString(R.string.app_main_health_mild)}   " +
                        "[36-37.3" +
                        "]${mActivity.resources.getString(R.string.app_main_health_normal)}" +
                        "    [>38]${mActivity.resources.getString(R.string.app_main_health_error)}"
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
    }

    private fun initSportChart() {
        val sportSubColumnValue = getSPortSubColumnValue();
        initSportColumn(sportSubColumnValue)
    }

    private fun initSportColumn(columns: java.util.ArrayList<Column>) {
        val mColumnChartData = ColumnChartData(columns) //设置数据
        mColumnChartData.isStacked = false //设置是否堆叠
        mColumnChartData.fillRatio = 0.2f//设置柱子宽度 0-1之间
        //坐标轴
        setAxisXShowColumn(mColumnChartData)
        setAxisYShowColumn(mColumnChartData)
        sportChart.columnChartData = mColumnChartData
    }

    private fun initSleepChart() {
        val sleepSubColumnValue = getSleepSubColumnValue()
        initSleepColumn(sleepSubColumnValue)
    }

    private fun initSleepColumn(columns: ArrayList<Column>) {
        val mColumnChartData = ColumnChartData(columns) //设置数据
        mColumnChartData.isStacked = false //设置是否堆叠
        mColumnChartData.fillRatio = 0.2f//设置柱子宽度 0-1之间
        //坐标轴
        setAxisXShowColumn(mColumnChartData)
        setAxisYShowColumn(mColumnChartData)
        sleepChart.columnChartData = mColumnChartData
    }

    private fun setAxisYShowColumn(data: ColumnChartData) {
        //X轴
        val axisX = Axis()//X轴
        //对x轴，数据和属性的设置
        axisX.textSize = 9 //设置字体的大小
        axisX.setHasTiltedLabels(false) //x坐标轴字体是斜的显示还是直的，true表示斜的
        axisX.textColor = Color.WHITE //X轴灰色
        axisX.values = mAxisValues //设置x轴各个坐标点名称
        data.axisXBottom = axisX //x 轴在底部
    }

    private fun setAxisXShowColumn(data: ColumnChartData) {
        //Y轴
        val axisY = Axis().setHasLines(true)
        axisY.textSize = 8//设置字体大小
        axisY.textColor = Color.WHITE //Y轴灰色
        axisY.maxLabelChars = 4
        data.axisYLeft = axisY //设置Y轴位置 左边
    }

    private fun initTempChart() {
        val options = getTempXAxisLabels()
        val aaOptions = initTempLineChart(options)
        tempChart.aa_drawChartWithChartOptions(aaOptions)
    }

    private fun initTempLineChart(category: Array<String>): AAOptions {
        val zonesArr: Array<Any> = arrayOf(
                mapOf(
                        "value" to 37.4,
                        "color" to "#ffffff"
                ),
                mapOf(
                        "value" to 38,
                        "color" to "#FFAD29"
                ),
                mapOf(
                        "color" to "#E2402B"
                )
        )
        val dataArray: Array<Any> =
                arrayOf(36.3f, 36.8f, 37.8f, 37.5f, 36.5f, 36.9f, 37f, 38.5f, 36.6f)
        val aaChartModel = AAChartModel()
                .chartType(AAChartType.Spline)//图形类型
                .dataLabelsEnabled(false)
                .categories(category)
                .legendEnabled(false)
                .markerRadius(2f)
                .yAxisTitle("")
                .backgroundColor("#5E95FF")
                .axesTextColor("#ffffff")
                .axesTextSize(10f)
                .series(
                        arrayOf(
                                AASeriesElement()
                                        .name("体温")
                                        .data(dataArray)
                                        .fillOpacity(0.5f)
                                        .lineWidth(1f)
                                        .zones(zonesArr)
                        )
                )

        return AAOptionsConstructor.configureChartOptions(aaChartModel)
    }

    private fun initHeartChart() {
        val heartAxisPoints = getHeartAxisPoints()
        initHeartLineChart(heartAxisPoints)
    }

    private fun initHeartLineChart(heartAxisPoints: java.util.ArrayList<PointValue>) {
        //折线的颜色
        val lines = arrayListOf<Line>()
        val line1 = Line(heartAxisPoints)
                .setColor(ContextCompat.getColor(mActivity, R.color.white))
        commonLineSetInit(line1, lines)
        val data = LineChartData()
        data.lines = lines
        data.baseValue = Float.NEGATIVE_INFINITY  //设置基准数(大概是数据范围)
        //坐标轴
        setAxisXShow(data)
        setAxisYShow(data)
        heartChart.lineChartData = data
        resetViewport(heartYMaxValue, heartChart)
    }

    private fun initBloodChart() {
        val belowBloodAxisPoints = getBelowBloodAxisPoints()
        val highBloodAxisPoints = getHighBloodAxisPoints()
        initBloodLineChart(belowBloodAxisPoints, highBloodAxisPoints)
    }

    private fun initBloodLineChart(belowBloodAxisPoints: ArrayList<PointValue>,
                                   highBloodAxisPoints: ArrayList<PointValue>) {
        //折线的颜色
        val lines = arrayListOf<Line>()
        val line1 = Line(belowBloodAxisPoints)
                .setColor(ContextCompat.getColor(mActivity, R.color.color5BC2FF))
        val line2 = Line(highBloodAxisPoints)
                .setColor(ContextCompat.getColor(mActivity, R.color.colorF18937))
        commonLineSetInit(line1, lines)
        commonLineSetInit(line2, lines)
        val data = LineChartData()
        data.lines = lines
        data.baseValue = Float.NEGATIVE_INFINITY  //设置基准数(大概是数据范围)
        //坐标轴
        setAxisXShow(data, "blood")
        setAxisYShow(data, "blood")
        bloodChart.lineChartData = data
        resetViewport(bloodYMaxValue, bloodChart)
        //创建一个图标视图 大小为控件的最大大小
    }

    /**
     * 重点方法，计算绘制图表
     */
    private fun resetViewport(yMaxValue: Float, view: LineChartView) {
        //创建一个图标视图 大小为控件的最大大小
        val v = Viewport(view.maximumViewport)
        v.top = yMaxValue
        v.bottom = 0f
        view.maximumViewport = v //给最大的视图设置 相当于原图
        v.left = -0.2f
        v.right = 10f
        view.currentViewport = v //给当前的视图设置 相当于当前展示的图
        view.postInvalidate()
    }

    private fun setAxisYShow(data: LineChartData, type: String = "") {
        //Y轴
        val axisY = Axis().setHasLines(true)
        axisY.textSize = 8//设置字体大小
        if (StringUtils.isNotBlankAndEmpty(type)) axisY.textColor = Color.GRAY //X轴灰色
        else axisY.textColor = Color.WHITE //X轴灰色
        axisY.maxLabelChars = 4
        data.axisYLeft = axisY //设置Y轴位置 左边
    }

    private fun setAxisXShow(data: LineChartData, type: String = "") {
        //X轴
        val axisX = Axis()//X轴
        //对x轴，数据和属性的设置
        axisX.textSize = 9 //设置字体的大小
        axisX.setHasTiltedLabels(false) //x坐标轴字体是斜的显示还是直的，true表示斜的
        if (StringUtils.isNotBlankAndEmpty(type)) axisX.textColor =
                mActivity.resources.getColor(R.color.colorF18937) //X轴灰色
        else axisX.textColor = Color.WHITE //X轴灰色
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
        line.setHasLines(true) //是否用直线显示。如果为false 则没有曲线只有点显示
        line.setHasPoints(true) //是否显示圆点 如果为false 则没有原点只有点显示
        lines.add(line)
    }

    private fun getHighBloodAxisPoints(): ArrayList<PointValue> {
        val pointValues = arrayListOf<PointValue>()
        val pointYValues = arrayListOf(100f, 121f, 105f, 150f, 120f, 110f, 170f, 130f, 100f)
        for (i in 0 until pointYValues.size) {
            pointValues.add(PointValue(i.toFloat(), pointYValues[i]))
            if (bloodYMaxValue < pointYValues[i]) bloodYMaxValue = pointYValues[i]
        }
        bloodYMaxValue += 10
        return pointValues
    }

    private fun getBelowBloodAxisPoints(): ArrayList<PointValue> {
        val pointValues = arrayListOf<PointValue>()
        val pointYValues = arrayListOf(58f, 70f, 60f, 90f, 68f, 61f, 81f, 110f, 65f)
        for (i in 0 until pointYValues.size) {
            pointValues.add(PointValue(i.toFloat(), pointYValues[i]))
        }
        return pointValues
    }

    private fun getHeartAxisPoints(): ArrayList<PointValue> {
        val pointValues = arrayListOf<PointValue>()
        val pointYValues = arrayListOf(45f, 80f, 100f, 90f, 70f, 85f, 125f, 78f, 95f)
        heartYMaxValue = 0f
        for (i in 0 until pointYValues.size) {
            pointValues.add(PointValue(i.toFloat(), pointYValues[i]))
            if (heartYMaxValue < pointYValues[i]) heartYMaxValue = pointYValues[i]
        }
        heartYMaxValue += 10
        return pointValues
    }

    private fun getTempXAxisLabels(): Array<String> {
        val category = arrayOfNulls<String>(9)
        for (i in 0..9) {
            when (i) {
                0 -> {
                    category[i] = "00:00"
                }
                1, 2, 3 -> {
                    category[i] = "0${i * 3}:00"
                }
                4, 5, 6, 7 -> {
                    category[i] = "${i * 3}:00"
                }
                8 -> {
                    category[i] = "23:59"
                }
            }
        }
        return category as Array<String>
    }

    private fun getSleepSubColumnValue(): ArrayList<Column> {
        val columns = arrayListOf<Column>()
        val subColumnValueData = arrayListOf(22f, 40f, 18f, 30f, 60f, 35f, 10f, 72f, 36f)
        for (i in 0 until subColumnValueData.size) {
            val colorIntValue = when {
                subColumnValueData[i] <= 20 -> {
                    mActivity.resources.getColor(R.color.colorF18937)
                }
                subColumnValueData[i] <= 40 -> {
                    mActivity.resources.getColor(R.color.white)
                }
                else -> {
                    mActivity.resources.getColor(R.color.colorE2402B)
                }
            }
            /*===== 柱状图相关设置 =====*/
            val column = Column(
                    arrayListOf(SubcolumnValue(subColumnValueData[i], colorIntValue)))
            column.setHasLabels(true) //没有标签
            column.setHasLabelsOnlyForSelected(true) //点击只放大
            columns.add(column)
        }
        return columns
    }

    private fun getSPortSubColumnValue(): ArrayList<Column> {
        val columns = arrayListOf<Column>()
        val subColumnValueData = arrayListOf(2600f, 5000f, 2400f, 3000f, 7500f, 4000f, 720f,
                9000f, 3800f)
        for (i in 0 until subColumnValueData.size) {
            val colorIntValue = mActivity.resources.getColor(R.color.white)
            /*===== 柱状图相关设置 =====*/
            val column = Column(
                    arrayListOf(SubcolumnValue(subColumnValueData[i], colorIntValue)))
            column.setHasLabels(true) //没有标签
            column.setHasLabelsOnlyForSelected(true) //点击只放大
            columns.add(column)
        }
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
            tempLayout -> {
                val intent = Intent(mActivity, HealthDataListActivity::class.java)
                intent.putExtra("type",
                        resources.getString(R.string.app_main_health_heart_rate))
                startActivity(intent)
            }
            heartLayout -> {
                val intent = Intent(mActivity, HealthDataListActivity::class.java)
                intent.putExtra("type",
                        resources.getString(R.string.app_main_health_body_temp))
                startActivity(intent)
            }
            sleepLayout -> {
                val intent = Intent(mActivity, HealthDataListActivity::class.java)
                intent.putExtra("type",
                        resources.getString(R.string.app_main_health_sleep))
                startActivity(intent)
            }
            sportLayout -> {
                val intent = Intent(mActivity, HealthDataListActivity::class.java)
                intent.putExtra("type",
                        resources.getString(R.string.app_main_health_sport))
                startActivity(intent)
            }
        }
    }

}