package com.guider.gps.view.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.guider.baselib.base.BaseFragment
import com.guider.baselib.utils.CommonUtils
import com.guider.gps.R
import com.guider.gps.view.activity.DoctorAnswerActivity
import kotlinx.android.synthetic.main.fragment_medicine_data.*
import lecho.lib.hellocharts.model.*
import lecho.lib.hellocharts.view.LineChartView

/**
 * 医疗数据2级页面
 */
class MedicineDataFragment : BaseFragment() {

    private val ARG_STR = "text"
    private var medicineType = ""
    private var medicineDataBgResIds = arrayListOf(
            R.drawable.icon_medicine_high,
            R.drawable.icon_medicine_normal,
            R.drawable.icon_medicine_below)
    private var medicineDataTextValues = arrayListOf<String>()
    private var yMaxValue = 0f

    override val layoutRes: Int
        get() = R.layout.fragment_medicine_data

    companion object {
        fun newInstance(text: String) = MedicineDataFragment().apply {
            arguments = Bundle().apply { putString(ARG_STR, text) }
            TAG = text
        }
    }

    override fun initView(rootView: View) {

    }

    @SuppressLint("SetTextI18n")
    override fun initLogic() {
        medicineDataTextValues = arrayListOf(
                resources.getString(R.string.app_main_medicine_high_side),
                resources.getString(R.string.app_main_health_normal),
                resources.getString(R.string.app_main_medicine_low_side))
        arguments?.takeIf { it.containsKey(ARG_STR) }?.apply {
            val string = getString(ARG_STR)
            medicineType = string!!
        }
        chartTitleTv.text = String.format(
                resources.getString(R.string.app_main_medicine_chart_title), medicineType)
        when (medicineType) {
            resources.getString(R.string.app_main_health_blood_sugar) -> {
                dataLatestLayout.setBackgroundResource(medicineDataBgResIds[0])
                dataTagTv.text = medicineDataTextValues[0]
                dataValueTv.text = "8.1"
                suggestTitleTv.text = String.format(
                        resources.getString(R.string.app_main_medicine_suggest_hint),
                        medicineType, medicineDataTextValues[0]
                )
            }
            resources.getString(R.string.app_main_health_blood_pressure) -> {
                dataLatestLayout.setBackgroundResource(medicineDataBgResIds[1])
                dataTagTv.text = medicineDataTextValues[1]
                dataValueTv.text = "6.2"
                suggestTitleTv.text = String.format(
                        resources.getString(R.string.app_main_medicine_suggest_hint),
                        medicineType, medicineDataTextValues[1]
                )
            }
            resources.getString(R.string.app_main_health_ecg) -> {
                dataLatestLayout.setBackgroundResource(medicineDataBgResIds[2])
                dataTagTv.text = medicineDataTextValues[2]
                dataValueTv.text = "5.2"
                suggestTitleTv.text = String.format(
                        resources.getString(R.string.app_main_medicine_suggest_hint),
                        medicineType, medicineDataTextValues[2]
                )
            }
        }
        initDataChart()
        answerLayout.setOnClickListener(this)
    }

    override fun onNoDoubleClick(v: View) {
        when (v) {
            answerLayout -> {
                val intent = Intent(mActivity, DoctorAnswerActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun initDataChart() {
        val mAxisValues = getAxisLabel()
        val bloodSugarAxisPoints = getBloodSugarAxisPoints()
        initDataChartShow(bloodSugarAxisPoints, mAxisValues)
    }

    private fun initDataChartShow(bloodSugarAxisPoints: ArrayList<PointValue>,
                                  mAxisValues: ArrayList<AxisValue>) {
        //折线的颜色
        val lines = arrayListOf<Line>()
        val line1 = Line(bloodSugarAxisPoints)
                .setColor(ContextCompat.getColor(mActivity, R.color.color59d15f))
        commonLineSetInit(line1, lines)
        val data = LineChartData()
        data.lines = lines
        data.baseValue = Float.NEGATIVE_INFINITY  //设置基准数(大概是数据范围)
        //坐标轴
        setAxisXShow(data, mAxisValues)
        setAxisYShow(data)
        dataChart.lineChartData = data
        resetViewport(dataChart)
    }

    private fun resetViewport(view: LineChartView) {
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

    private fun setAxisYShow(data: LineChartData) {
        //Y轴
        val axisY = Axis().setHasLines(true)
        axisY.textSize = 8//设置字体大小
        axisY.textColor = Color.GRAY //X轴灰色
        axisY.maxLabelChars = 4
        data.axisYLeft = axisY //设置Y轴位置 左边
    }

    private fun setAxisXShow(data: LineChartData, mAxisValues: ArrayList<AxisValue>) {
        //X轴
        val axisX = Axis()//X轴
        //对x轴，数据和属性的设置
        axisX.textSize = 9 //设置字体的大小
        axisX.setHasTiltedLabels(false) //x坐标轴字体是斜的显示还是直的，true表示斜的
        axisX.textColor = Color.GRAY //X轴灰色
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

    private fun getBloodSugarAxisPoints(): ArrayList<PointValue> {
        val pointValues = arrayListOf<PointValue>()
        val pointYValues = arrayListOf(5.2f, 6.5f, 6f, 8f, 6f, 7f, 8.1f)
        for (i in 0 until pointYValues.size) {
            val colorInt =
                    when {
                        pointYValues[i] < 6 -> {
                            CommonUtils.getColor(mActivity, R.color.colorF18937)
                        }
                        pointYValues[i] > 7 -> {
                            CommonUtils.getColor(mActivity, R.color.colorE2402B)
                        }
                        else -> CommonUtils.getColor(mActivity, R.color.color59d15f)
                    }
            pointValues.add(PointValue(i.toFloat(), pointYValues[i],colorInt))
            if (yMaxValue < pointYValues[i]) yMaxValue = pointYValues[i]
        }
        yMaxValue += 2
        return pointValues
    }

    private fun getAxisLabel(): ArrayList<AxisValue> {
        val mAxisValues = arrayListOf<AxisValue>()
        for (i in 0..7) {
            mAxisValues.add(AxisValue(i.toFloat()).setLabel(i.toString()))
        }
        return mAxisValues
    }
}