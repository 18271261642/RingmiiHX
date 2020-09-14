package com.guider.gps.view.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.guider.baselib.base.BaseFragment
import com.guider.baselib.device.IUnit
import com.guider.baselib.utils.*
import com.guider.baselib.widget.dialog.DialogProgress
import com.guider.gps.R
import com.guider.gps.view.activity.DoctorAnswerActivity
import com.guider.health.apilib.ApiCallBack
import com.guider.health.apilib.ApiUtil
import com.guider.health.apilib.IUserHDApi
import com.guider.health.apilib.bean.BloodListBeann
import com.guider.health.apilib.bean.BloodOxygenListBean
import com.guider.health.apilib.bean.BloodSugarListBean
import kotlinx.android.synthetic.main.fragment_medicine_data.*
import lecho.lib.hellocharts.model.*
import lecho.lib.hellocharts.view.LineChartView
import retrofit2.Call
import retrofit2.Response

/**
 * 医疗数据2级页面
 */
class MedicineDataFragment : BaseFragment() {

    private val fragmentType = "text"
    private var medicineType = ""
    private var medicineDataBgResIds = arrayListOf(
            R.drawable.icon_medicine_high,
            R.drawable.icon_medicine_normal,
            R.drawable.icon_medicine_below)
    private var medicineDataTextValues = arrayListOf<String>()
    private var yMaxValue = 0f
    private var heartYMaxValue = 0f
    private var bloodYMaxValue = 0f

    override val layoutRes: Int
        get() = R.layout.fragment_medicine_data

    companion object {
        fun newInstance(text: String) = MedicineDataFragment().apply {
            arguments = Bundle().apply { putString(fragmentType, text) }
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
        arguments?.takeIf { it.containsKey(fragmentType) }?.apply {
            val string = getString(fragmentType)
            medicineType = string!!
        }
        chartTitleTv.text = String.format(
                resources.getString(R.string.app_main_medicine_chart_title), medicineType)
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
        when (medicineType) {
            resources.getString(R.string.app_main_health_blood_sugar) -> {
                getBloodSugarData()
            }
            resources.getString(R.string.app_main_health_blood_pressure) -> {
                getBloodData()
            }
            resources.getString(R.string.app_main_health_blood_oxygen) -> {
                getBloodOxygenData()
            }
        }
        initDataChartShow(arrayListOf())
    }

    @SuppressLint("SetTextI18n")
    private fun getBloodData() {
        val mDialog = DialogProgress(mActivity, null)
        mDialog.showDialog()
        val accountId = MMKVUtil.getInt(USER.USERID)
        val startTimeValue = DateUtilKotlin.localToUTC(CommonUtils.calTimeFrontYear(
                CommonUtils.getCurrentDate(DEFAULT_TIME_FORMAT_PATTERN), 2))!!
        val endTimeValue = DateUtilKotlin.localToUTC(
                CommonUtils.getCurrentDate(DEFAULT_TIME_FORMAT_PATTERN))!!
        ApiUtil.createHDApi(IUserHDApi::class.java)
                .getHealthBloodChartData(accountId, 1, 7,
                        startTimeValue, endTimeValue)
                .enqueue(object : ApiCallBack<List<BloodListBeann>>() {
                    override fun onApiResponse(call: Call<List<BloodListBeann>>?,
                                               response: Response<List<BloodListBeann>>?) {
                        if (!response?.body().isNullOrEmpty()) {
                            val belowBloodAxisPoints = getBelowBloodAxisPoints(response?.body()!!)
                            val highBloodAxisPoints = getHighBloodAxisPoints(response.body()!!)
                            initBloodLineChart(belowBloodAxisPoints, highBloodAxisPoints)
                            val state1 = response.body()!![0].state2.substring(0,
                                    response.body()!![0].state2.indexOf(","))
                            val state2 = response.body()!![0].state2.substring(
                                    response.body()!![0].state2.indexOf(",") + 1)
                            when {
                                state1 == "偏低" || (state1 == "正常" && state2 == "偏低") -> {
                                    dataLatestLayout.setBackgroundResource(medicineDataBgResIds[2])
                                    suggestTitleTv.text = String.format(
                                            resources.getString(
                                                    R.string.app_main_medicine_suggest_hint),
                                            medicineType, medicineDataTextValues[2]
                                    )
                                    suggestContentTv.text = resources.getString(
                                            R.string.app_main_medicine_suggest_blood_pre_low)
                                }
                                state1 == "正常" && state2 == "正常" -> {
                                    dataLatestLayout.setBackgroundResource(medicineDataBgResIds[1])
                                    suggestTitleTv.text = String.format(
                                            resources.getString(
                                                    R.string.app_main_medicine_suggest_hint),
                                            medicineType, medicineDataTextValues[1]
                                    )
                                    suggestContentTv.text = resources.getString(
                                            R.string.app_main_medicine_suggest_blood_pre_normal)
                                }
                                state1 == "偏高" || (state1 == "正常" && state2 == "偏高") -> {
                                    dataLatestLayout.setBackgroundResource(medicineDataBgResIds[0])
                                    suggestTitleTv.text = String.format(
                                            resources.getString(
                                                    R.string.app_main_medicine_suggest_hint),
                                            medicineType, medicineDataTextValues[0]
                                    )
                                    suggestContentTv.text = resources.getString(
                                            R.string.app_main_medicine_suggest_blood_pre_high)
                                }
                            }
                            dataTagTv.text = response.body()!![0].state
                            dataValueTv.text = "${response.body()!![0].sbp}/" +
                                    "${response.body()!![0].dbp}"
                            measureTime.text = DateUtilKotlin.uTCToLocal(
                                    response.body()!![0].testTime, TIME_FORMAT_PATTERN1)
                        }
                    }

                    override fun onRequestFinish() {
                        mDialog.hideDialog()
                    }
                })
    }

    private fun initBloodLineChart(belowBloodAxisPoints: ArrayList<PointValue>,
                                   highBloodAxisPoints: ArrayList<PointValue>) {
        //折线的颜色
        val lines = arrayListOf<Line>()
        val line1 = Line(belowBloodAxisPoints)
                .setColor(ContextCompat.getColor(mActivity, R.color.color46E8FD))
        val line2 = Line(highBloodAxisPoints)
                .setColor(ContextCompat.getColor(mActivity, R.color.color4897FF))
        commonLineSetInit(line1, lines)
        commonLineSetInit(line2, lines)
        val data = LineChartData()
        data.lines = lines
        data.baseValue = Float.NEGATIVE_INFINITY  //设置基准数(大概是数据范围)
        //坐标轴
        setAxisXShow(data)
        setAxisYShow(data)
        dataChart.lineChartData = data
        resetViewport(dataChart, bloodYMaxValue)
        //创建一个图标视图 大小为控件的最大大小
    }

    private fun getHighBloodAxisPoints(list: List<BloodListBeann>): ArrayList<PointValue> {
        val pointValues = arrayListOf<PointValue>()
        val pointYValues = arrayListOf<Float>()
        list.forEach {
            pointYValues.add(it.sbp.toFloat())
        }
        for (i in 0 until pointYValues.size) {
            val colorInt =
                    when (list[i].state2.substring(0,
                            list[i].state2.indexOf(","))) {
                        "偏低" -> {
                            CommonUtils.getColor(mActivity, R.color.colorF18937)
                        }
                        "偏高" -> {
                            CommonUtils.getColor(mActivity, R.color.colorE2402B)
                        }
                        else -> CommonUtils.getColor(mActivity, R.color.color59d15f)
                    }
            pointValues.add(PointValue(i.toFloat(), pointYValues[i], colorInt))
            if (bloodYMaxValue < pointYValues[i]) bloodYMaxValue = pointYValues[i]
        }
        bloodYMaxValue += 10
        return pointValues
    }

    private fun getBelowBloodAxisPoints(list: List<BloodListBeann>): ArrayList<PointValue> {
        val pointValues = arrayListOf<PointValue>()
        val pointYValues = arrayListOf<Float>()
        list.forEach {
            pointYValues.add(it.dbp.toFloat())
        }
        for (i in 0 until pointYValues.size) {
            val colorInt =
                    when (list[i].state2.substring(
                            list[i].state2.indexOf(",") + 1)) {
                        "偏低" -> {
                            CommonUtils.getColor(mActivity, R.color.colorF18937)
                        }
                        "偏高" -> {
                            CommonUtils.getColor(mActivity, R.color.colorE2402B)
                        }
                        else -> CommonUtils.getColor(mActivity, R.color.color59d15f)
                    }
            pointValues.add(PointValue(i.toFloat(), pointYValues[i], colorInt))
        }
        return pointValues
    }

    private fun getBloodOxygenData() {
        val mDialog = DialogProgress(mActivity, null)
        mDialog.showDialog()
        val accountId = MMKVUtil.getInt(USER.USERID)
        val startTimeValue = DateUtilKotlin.localToUTC(CommonUtils.calTimeFrontYear(
                CommonUtils.getCurrentDate(DEFAULT_TIME_FORMAT_PATTERN), 2))!!
        val endTimeValue = DateUtilKotlin.localToUTC(
                CommonUtils.getCurrentDate(DEFAULT_TIME_FORMAT_PATTERN))!!
        ApiUtil.createHDApi(IUserHDApi::class.java)
                .getHealthBloodOxygenData(accountId, 1, 7,
                        startTimeValue, endTimeValue)
                .enqueue(object : ApiCallBack<List<BloodOxygenListBean>>() {
                    override fun onApiResponse(call: Call<List<BloodOxygenListBean>>?,
                                               response: Response<List<BloodOxygenListBean>>?) {
                        if (!response?.body().isNullOrEmpty()) {
                            val bloodOxygenAxisPoints = getBloodOxygenAxisPoints(response?.body()!!)
                            initBloodOxygenLineChart(bloodOxygenAxisPoints)
                            when (response.body()!![0].state2) {
                                "偏低" -> {
                                    dataLatestLayout.setBackgroundResource(medicineDataBgResIds[2])
                                    suggestTitleTv.text = String.format(
                                            resources.getString(
                                                    R.string.app_main_medicine_suggest_hint),
                                            medicineType, medicineDataTextValues[2]
                                    )
                                    suggestContentTv.text = resources.getString(
                                            R.string.app_main_medicine_suggest_blood_oxygen_low)
                                }
                                else -> {
                                    dataLatestLayout.setBackgroundResource(medicineDataBgResIds[1])
                                    suggestTitleTv.text = String.format(
                                            resources.getString(
                                                    R.string.app_main_medicine_suggest_hint),
                                            medicineType, medicineDataTextValues[1]
                                    )
                                    suggestContentTv.text = resources.getString(
                                            R.string.app_main_medicine_suggest_blood_oxygen_normal)
                                }
                            }
                            dataValueTv.text = response.body()!![0].bo.toString()
                            dataTagTv.text = response.body()!![0].state
                            measureTime.text = DateUtilKotlin.uTCToLocal(
                                    response.body()!![0].testTime, TIME_FORMAT_PATTERN1)
                        }
                    }

                    override fun onRequestFinish() {
                        mDialog.hideDialog()
                    }
                })
    }

    private fun getBloodOxygenAxisPoints(list: List<BloodOxygenListBean>): ArrayList<PointValue> {
        val pointValues = arrayListOf<PointValue>()
        val pointYValues = arrayListOf<Float>()
        list.forEach {
            pointYValues.add(it.bo.toFloat())
        }
        heartYMaxValue = 0f
        for (i in 0 until pointYValues.size) {
            val colorInt =
                    when (list[i].state2) {
                        "偏低" -> {
                            CommonUtils.getColor(mActivity, R.color.colorF18937)
                        }
                        else -> CommonUtils.getColor(mActivity, R.color.color59d15f)
                    }
            pointValues.add(PointValue(i.toFloat(), pointYValues[i], colorInt))
            if (heartYMaxValue < pointYValues[i]) heartYMaxValue = pointYValues[i]
        }
        heartYMaxValue += 10
        return pointValues
    }

    private fun initBloodOxygenLineChart(bloodOxygenAxisPoints: java.util.ArrayList<PointValue>) {
        //折线的颜色
        val lines = arrayListOf<Line>()
        val line1 = Line(bloodOxygenAxisPoints)
                .setColor(ContextCompat.getColor(mActivity, R.color.color59d15f))
        commonLineSetInit(line1, lines)
        val data = LineChartData()
        data.lines = lines
        data.baseValue = Float.NEGATIVE_INFINITY  //设置基准数(大概是数据范围)
        //坐标轴
        setAxisXShow(data)
        setAxisYShow(data)
        dataChart.lineChartData = data
        resetViewport(dataChart, heartYMaxValue)
    }

    private fun getBloodSugarData() {
        val mDialog = DialogProgress(mActivity, null)
        mDialog.showDialog()
        val accountId = MMKVUtil.getInt(USER.USERID)
        val startTimeValue = DateUtilKotlin.localToUTC(CommonUtils.calTimeFrontYear(
                CommonUtils.getCurrentDate(DEFAULT_TIME_FORMAT_PATTERN), 2))!!
        val endTimeValue = DateUtilKotlin.localToUTC(
                CommonUtils.getCurrentDate(DEFAULT_TIME_FORMAT_PATTERN))!!
        ApiUtil.createHDApi(IUserHDApi::class.java)
                .getHealthBloodSugarChartData(accountId, 1, 7,
                        startTimeValue, endTimeValue)
                .enqueue(object : ApiCallBack<List<BloodSugarListBean>>() {
                    override fun onApiResponse(call: Call<List<BloodSugarListBean>>?,
                                               response: Response<List<BloodSugarListBean>>?) {
                        if (!response?.body().isNullOrEmpty()) {
                            //返回的数据类中state2的第一个字段为血糖状态
                            when (response?.body()!![0].state2.substring(0,
                                    response.body()!![0].state2.indexOf(","))) {
                                "偏低" -> {
                                    dataLatestLayout.setBackgroundResource(medicineDataBgResIds[2])
                                    suggestTitleTv.text = String.format(
                                            resources.getString(
                                                    R.string.app_main_medicine_suggest_hint),
                                            medicineType, medicineDataTextValues[2]
                                    )
                                    suggestContentTv.text = resources.getString(
                                            R.string.app_main_medicine_suggest_sugar_low)
                                }
                                "偏高" -> {
                                    dataLatestLayout.setBackgroundResource(medicineDataBgResIds[0])
                                    suggestTitleTv.text = String.format(
                                            resources.getString(
                                                    R.string.app_main_medicine_suggest_hint),
                                            medicineType, medicineDataTextValues[0]
                                    )
                                    suggestContentTv.text = resources.getString(
                                            R.string.app_main_medicine_suggest_sugar_high)
                                }
                                else -> {
                                    dataLatestLayout.setBackgroundResource(medicineDataBgResIds[1])
                                    suggestTitleTv.text = String.format(
                                            resources.getString(
                                                    R.string.app_main_medicine_suggest_hint),
                                            medicineType, medicineDataTextValues[1]
                                    )
                                    suggestContentTv.text = resources.getString(
                                            R.string.app_main_medicine_suggest_sugar_normal)
                                }
                            }
                            val iUnit: IUnit = UnitUtil.getIUnit(mActivity)
                            val value: Double = iUnit.getGluShowValue(
                                    response.body()!![0].bs, 2)
                            dataValueTv.text = value.toString()
                            dataTagTv.text = response.body()!![0].state
                            measureTime.text = DateUtilKotlin.uTCToLocal(
                                    response.body()!![0].testTime, TIME_FORMAT_PATTERN1)
                            val bloodSugarAxisPoints = getBloodSugarAxisPoints(response.body()!!)
                            initDataChartShow(bloodSugarAxisPoints)
                        }
                    }

                    override fun onRequestFinish() {
                        mDialog.hideDialog()
                    }
                })
    }

    private fun initDataChartShow(bloodSugarAxisPoints: ArrayList<PointValue>) {
        //折线的颜色
        val lines = arrayListOf<Line>()
        val line1 = Line(bloodSugarAxisPoints)
                .setColor(ContextCompat.getColor(mActivity, R.color.color59d15f))
        commonLineSetInit(line1, lines)
        val data = LineChartData()
        data.lines = lines
        data.baseValue = Float.NEGATIVE_INFINITY  //设置基准数(大概是数据范围)
        //坐标轴
        setAxisXShow(data)
        setAxisYShow(data)
        dataChart.lineChartData = data
        resetViewport(dataChart, yMaxValue)
    }

    private fun resetViewport(view: LineChartView, yMaxValue: Float) {
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

    private fun setAxisXShow(data: LineChartData) {
        //X轴
        val axisX = Axis()//X轴
        //对x轴，数据和属性的设置
        axisX.textSize = 9 //设置字体的大小
        axisX.setHasTiltedLabels(false) //x坐标轴字体是斜的显示还是直的，true表示斜的
        axisX.textColor = Color.GRAY //X轴灰色
        axisX.values = getAxisLabel() //设置x轴各个坐标点名称
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

    private fun getBloodSugarAxisPoints(list: List<BloodSugarListBean>): ArrayList<PointValue> {
        val pointValues = arrayListOf<PointValue>()
        val pointYValues = arrayListOf<Float>()
        val iUnit: IUnit = UnitUtil.getIUnit(mActivity)
        list.forEach {
            val value = iUnit.getGluShowValue(it.bs, 2).toFloat()
            pointYValues.add(value)
        }
        for (i in 0 until pointYValues.size) {
            val colorInt =
                    when (list[i].state2.substring(0, list[i].state2.indexOf(","))) {
                        "偏低" -> {
                            CommonUtils.getColor(mActivity, R.color.colorF18937)
                        }
                        "偏高" -> {
                            CommonUtils.getColor(mActivity, R.color.colorE2402B)
                        }
                        else -> CommonUtils.getColor(mActivity, R.color.color59d15f)
                    }
            pointValues.add(PointValue(i.toFloat(), pointYValues[i], colorInt))
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