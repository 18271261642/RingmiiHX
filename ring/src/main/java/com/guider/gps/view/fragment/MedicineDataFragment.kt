package com.guider.gps.view.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.guider.baselib.base.BaseFragment
import com.guider.baselib.bean.Judgement
import com.guider.baselib.bean.ParamHealthRangeAnalysis
import com.guider.baselib.utils.*
import com.guider.baselib.widget.dialog.DialogProgress
import com.guider.gps.R
import com.guider.gps.view.activity.DoctorAnswerActivity
import com.guider.health.apilib.ApiCallBack
import com.guider.health.apilib.ApiUtil
import com.guider.health.apilib.IUserHDApi
import com.guider.health.apilib.bean.BloodListBeann
import com.guider.health.apilib.bean.BloodSugarListBean
import com.guider.health.apilib.bean.HeartListBean
import kotlinx.android.synthetic.main.fragment_medicine_data.*
import lecho.lib.hellocharts.model.*
import lecho.lib.hellocharts.view.LineChartView
import retrofit2.Call
import retrofit2.Response

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
    private var heartYMaxValue = 0f
    private var bloodYMaxValue = 0f

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
                chartLayout.setBackgroundResource(R.drawable.rounded_f6f6f6_7_bg)
                dataLatestLayout.setBackgroundResource(medicineDataBgResIds[0])
                dataTagTv.text = medicineDataTextValues[0]
                dataValueTv.text = "8.1"
                suggestTitleTv.text = String.format(
                        resources.getString(R.string.app_main_medicine_suggest_hint),
                        medicineType, medicineDataTextValues[0]
                )
            }
            resources.getString(R.string.app_main_health_blood_pressure) -> {
                chartLayout.setBackgroundResource(R.drawable.rounded_f6f6f6_8_bg)
                dataLatestLayout.setBackgroundResource(medicineDataBgResIds[1])
                dataTagTv.text = medicineDataTextValues[1]
                dataValueTv.text = "6.2"
                suggestTitleTv.text = String.format(
                        resources.getString(R.string.app_main_medicine_suggest_hint),
                        medicineType, medicineDataTextValues[1]
                )
            }
            resources.getString(R.string.app_main_health_ecg) -> {
                chartLayout.setBackgroundResource(R.drawable.rounded_6ecdff_8_bg)
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
        when (medicineType) {
            resources.getString(R.string.app_main_health_blood_sugar) -> {
                getBloodSugarData()
            }
            resources.getString(R.string.app_main_health_blood_pressure) -> {
                getBloodData()
            }
            resources.getString(R.string.app_main_health_ecg) -> {
                getHeartData()
            }
        }
        initDataChartShow(arrayListOf())
    }

    private fun getBloodData() {
        val mDialog = DialogProgress(mActivity, null)
        mDialog.showDialog()
        val accountId = MMKVUtil.getInt(USER.USERID)
        val startTimeValue = DateUtilKotlin.localToUTC(CommonUtils.calTimeFrontYear(
                CommonUtils.getCurrentDate(DEFAULT_TIME_FORMAT_PATTERN), 1))!!
        val endTimeValue = DateUtilKotlin.localToUTC(
                CommonUtils.getCurrentDate(DEFAULT_TIME_FORMAT_PATTERN))!!
        ApiUtil.createHDApi(IUserHDApi::class.java)
                .getHealthBloodChartData(2197, 1, 7,
                        startTimeValue, endTimeValue)
                .enqueue(object : ApiCallBack<List<BloodListBeann>>() {
                    override fun onApiResponse(call: Call<List<BloodListBeann>>?,
                                               response: Response<List<BloodListBeann>>?) {
                        if (!response?.body().isNullOrEmpty()) {
                            val belowBloodAxisPoints = getBelowBloodAxisPoints(response?.body()!!)
                            val highBloodAxisPoints = getHighBloodAxisPoints(response.body()!!)
                            initBloodLineChart(belowBloodAxisPoints, highBloodAxisPoints)
                            dealBloodStatusShow(response)
                        }
                    }

                    override fun onRequestFinish() {
                        mDialog.hideDialog()
                    }
                })
    }

    private fun dealBloodStatusShow(response: Response<List<BloodListBeann>>) {
        val list = arrayListOf<ParamHealthRangeAnalysis>()
        val paramHealthRangeAnalysis = ParamHealthRangeAnalysis()
        paramHealthRangeAnalysis.type = ParamHealthRangeAnalysis.BLOODPRESSURE
        paramHealthRangeAnalysis.value1 = response.body()!![0].sbp.toString()
        paramHealthRangeAnalysis.value2 = response.body()!![0].dbp.toString()
        var birth = "1970-01-01"
        if (StringUtil.isNotBlankAndEmpty(MMKVUtil.getString(USER.BIRTHDAY))) {
            birth = MMKVUtil.getString(USER.BIRTHDAY)
        }
        val stringBuilder = StringBuilder()
        stringBuilder.append(birth.substring(0, 4))
        stringBuilder.append(birth.substring(5, 7))
        stringBuilder.append(birth.substring(8, 10))
        paramHealthRangeAnalysis.year = stringBuilder.toString().toInt()
        list.add(paramHealthRangeAnalysis)
        val results: List<String> = Judgement.healthDataAnlysis(list)
        if (results.isNotEmpty()) {
            when (results[0]) {
                "血压偏低" -> {
                    dataLatestLayout.setBackgroundResource(medicineDataBgResIds[2])
                    dataTagTv.text = medicineDataTextValues[2]
                    suggestTitleTv.text = String.format(
                            resources.getString(
                                    R.string.app_main_medicine_suggest_hint),
                            medicineType, medicineDataTextValues[2]
                    )
                }
                "理想血压" -> {
                    dataLatestLayout.setBackgroundResource(medicineDataBgResIds[1])
                    dataTagTv.text = medicineDataTextValues[1]
                    suggestTitleTv.text = String.format(
                            resources.getString(
                                    R.string.app_main_medicine_suggest_hint),
                            medicineType, medicineDataTextValues[1]
                    )
                }
                "疑似高血压" -> {
                    dataLatestLayout.setBackgroundResource(medicineDataBgResIds[0])
                    dataTagTv.text = medicineDataTextValues[0]
                    suggestTitleTv.text = String.format(
                            resources.getString(
                                    R.string.app_main_medicine_suggest_hint),
                            medicineType, medicineDataTextValues[0]
                    )
                }
            }
        }
        dataValueTv.text = response.body()!![0].sbp.toString()
        measureTime.text = DateUtilKotlin.uTCToLocal(
                response.body()!![0].testTime, TIME_FORMAT_PATTERN1)
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
            pointValues.add(PointValue(i.toFloat(), pointYValues[i]))
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
            pointValues.add(PointValue(i.toFloat(), pointYValues[i]))
        }
        return pointValues
    }

    private fun getHeartData() {
        val mDialog = DialogProgress(mActivity, null)
        mDialog.showDialog()
        val accountId = MMKVUtil.getInt(USER.USERID)
        val startTimeValue = DateUtilKotlin.localToUTC(CommonUtils.calTimeFrontYear(
                CommonUtils.getCurrentDate(DEFAULT_TIME_FORMAT_PATTERN), 1))!!
        val endTimeValue = DateUtilKotlin.localToUTC(
                CommonUtils.getCurrentDate(DEFAULT_TIME_FORMAT_PATTERN))!!
        ApiUtil.createHDApi(IUserHDApi::class.java)
                .getHealthHeartChartData(2197, 1, 7,
                        startTimeValue, endTimeValue)
                .enqueue(object : ApiCallBack<List<HeartListBean>>() {
                    override fun onApiResponse(call: Call<List<HeartListBean>>?,
                                               response: Response<List<HeartListBean>>?) {
                        if (!response?.body().isNullOrEmpty()) {
                            val heartAxisPoints = getHeartAxisPoints(response?.body()!!)
                            initHeartLineChart(heartAxisPoints)
                            dealHeartStatusShow(response.body()!![0])
                        }
                    }

                    override fun onRequestFinish() {
                        mDialog.hideDialog()
                    }
                })
    }

    private fun dealHeartStatusShow(heartListBean: HeartListBean) {
        val list: List<ParamHealthRangeAnalysis> = java.util.ArrayList<ParamHealthRangeAnalysis>()
        val paramHealthRangeAnalysis = ParamHealthRangeAnalysis()
        paramHealthRangeAnalysis.type = ParamHealthRangeAnalysis.HEARTBEAT
        paramHealthRangeAnalysis.value1 = heartListBean.hb
        dataValueTv.text = heartListBean.hb.toString()
        measureTime.text = DateUtilKotlin.uTCToLocal(
                heartListBean.testTime, TIME_FORMAT_PATTERN1)

    }

    private fun getHeartAxisPoints(list: List<HeartListBean>): ArrayList<PointValue> {
        val pointValues = arrayListOf<PointValue>()
        val pointYValues = arrayListOf<Float>()
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
        dataChart.lineChartData = data
        resetViewport(dataChart, heartYMaxValue)
    }

    private fun getBloodSugarData() {
        val mDialog = DialogProgress(mActivity, null)
        mDialog.showDialog()
        val accountId = MMKVUtil.getInt(USER.USERID)
        val startTimeValue = DateUtilKotlin.localToUTC(CommonUtils.calTimeFrontYear(
                CommonUtils.getCurrentDate(DEFAULT_TIME_FORMAT_PATTERN), 1))!!
        val endTimeValue = DateUtilKotlin.localToUTC(
                CommonUtils.getCurrentDate(DEFAULT_TIME_FORMAT_PATTERN))!!
        ApiUtil.createHDApi(IUserHDApi::class.java)
                .getHealthBloodSugarChartData(2197, 1, 7,
                        startTimeValue, endTimeValue)
                .enqueue(object : ApiCallBack<List<BloodSugarListBean>>() {
                    override fun onApiResponse(call: Call<List<BloodSugarListBean>>?,
                                               response: Response<List<BloodSugarListBean>>?) {
                        if (!response?.body().isNullOrEmpty()) {
                            when {
                                response?.body()!![0].bs < 6 -> {
                                    dataLatestLayout.setBackgroundResource(medicineDataBgResIds[2])
                                    dataTagTv.text = medicineDataTextValues[2]
                                    dataValueTv.text = response.body()!![0].bs.toString()
                                    suggestTitleTv.text = String.format(
                                            resources.getString(
                                                    R.string.app_main_medicine_suggest_hint),
                                            medicineType, medicineDataTextValues[2]
                                    )
                                }
                                response.body()!![0].bs > 7 -> {
                                    dataLatestLayout.setBackgroundResource(medicineDataBgResIds[0])
                                    dataTagTv.text = medicineDataTextValues[0]
                                    suggestTitleTv.text = String.format(
                                            resources.getString(
                                                    R.string.app_main_medicine_suggest_hint),
                                            medicineType, medicineDataTextValues[0]
                                    )
                                }
                                else -> {
                                    dataLatestLayout.setBackgroundResource(medicineDataBgResIds[1])
                                    dataTagTv.text = medicineDataTextValues[1]
                                    suggestTitleTv.text = String.format(
                                            resources.getString(
                                                    R.string.app_main_medicine_suggest_hint),
                                            medicineType, medicineDataTextValues[1]
                                    )
                                }
                            }
                            dataValueTv.text = response.body()!![0].bs.toString()
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
        list.forEach {
            pointYValues.add(it.bs)
        }
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