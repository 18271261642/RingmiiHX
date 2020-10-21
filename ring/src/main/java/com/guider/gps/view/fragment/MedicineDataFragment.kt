package com.guider.gps.view.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.guider.baselib.base.BaseFragment
import com.guider.baselib.device.IUnit
import com.guider.baselib.device.Unit
import com.guider.baselib.utils.*
import com.guider.baselib.widget.dialog.DialogProgress
import com.guider.gps.R
import com.guider.gps.view.activity.DoctorAnswerActivity
import com.guider.health.apilib.GuiderApiUtil
import com.guider.health.apilib.bean.BloodListBeann
import com.guider.health.apilib.bean.BloodOxygenListBean
import com.guider.health.apilib.bean.BloodSugarListBean
import com.guider.health.apilib.enums.SortType
import kotlinx.android.synthetic.main.fragment_medicine_data.*
import kotlinx.coroutines.launch
import lecho.lib.hellocharts.model.*
import lecho.lib.hellocharts.view.LineChartView
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

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
    private var isRefresh = false
    private var mDialog1: DialogProgress? = null
    private var mDialog2: DialogProgress? = null
    private var mDialog3: DialogProgress? = null
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
        suggestTitleTv.text = mActivity.resources.getString(R.string.app_health_suggest)
        chartTitleTv.text = mActivity.resources.getString(R.string.app_main_medicine_chart_title)
        initDataChart()
        answerLayout.setOnClickListener(this)
        measure.setOnClickListener(this)
        refreshLayout.setEnableAutoLoadMore(false)
        refreshLayout.setEnableRefresh(true)
        refreshLayout.setOnRefreshListener {
            isRefresh = true
            initDataChart()
        }
    }

    override fun onNoDoubleClick(v: View) {
        when (v) {
            answerLayout -> {
                val intent = Intent(mActivity, DoctorAnswerActivity::class.java)
                startActivity(intent)
            }
            measure -> {
                showToast("相关功能开发中，敬请期待...")
            }
        }
    }

    override fun openEventBus(): Boolean {
        return true
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshHealthData(event: EventBusEvent<String>) {
        if (event.code == EventBusAction.REFRESH_HEALTH_DATA) {
            initDataChart()
        }
    }

    private fun initDataChart() {
        heartYMaxValue = 0f
        bloodYMaxValue = 0f
        when (medicineType) {
            resources.getString(R.string.app_main_health_blood_sugar) -> {
                dataUnitTv.text = mActivity.resources.getString(R.string.glu_unit_food_2)
                dataChartUnit.text = mActivity.resources.getString(R.string.glu_unit_food_2)
                getBloodSugarData()
            }
            resources.getString(R.string.app_main_health_blood_pressure) -> {
                dataUnitTv.text = Unit().bp
                dataChartUnit.text = Unit().bp
                getBloodData()
            }
            resources.getString(R.string.app_main_health_blood_oxygen) -> {
                dataUnitTv.text = Unit().bloodO2
                dataChartUnit.text = Unit().bloodO2
                getBloodOxygenData()
            }
        }
        initDataChartShow(arrayListOf(), arrayListOf())
    }

    private fun getBloodData() {
        mDialog1 = DialogProgress(mActivity, null)

        val accountId = MMKVUtil.getInt(BIND_DEVICE_ACCOUNT_ID)
        lifecycleScope.launch {
            ApiCoroutinesCallBack.resultParse(mActivity, onStart = {
                if (!isRefresh) mDialog1?.showDialog()
            }, block = {
                val resultBean = GuiderApiUtil.getHDApiService()
                        .getHealthBloodChartData(accountId, 1, 7, sort = SortType.DESC)

                if (!resultBean.isNullOrEmpty()) {
                    dealBloodSuccessList(resultBean)
                } else {
                    dealBloodEmptyList()
                }
            }, onRequestFinish = {
                if (!isRefresh) mDialog1?.hideDialog()
                if (isRefresh) {
                    refreshLayout.finishRefresh()
                }
                isRefresh = false
            })
        }
    }

    @SuppressLint("SetTextI18n")
    private fun dealBloodEmptyList() {
        if (isRefresh) {
            refreshLayout.finishRefresh()
        }
        noDataTv.visibility = View.VISIBLE
        initBloodLineChart(arrayListOf(), arrayListOf(), arrayListOf())
        dataTagTv.text = ""
        dataValueTv.text = mActivity.resources.getString(R.string.app_no_data)
        measureTime.text = ""
        suggestContentTv.text = mActivity.resources.getString(R.string.app_no_suggest)
    }

    @SuppressLint("SetTextI18n")
    private fun dealBloodSuccessList(resultBean: List<BloodListBeann>) {
        if (isRefresh) refreshLayout.finishRefresh(500)
        noDataTv.visibility = View.GONE
        val belowBloodAxisPoints = getBelowBloodAxisPoints(resultBean)
        val highBloodAxisPoints = getHighBloodAxisPoints(resultBean)
        initBloodLineChart(belowBloodAxisPoints, highBloodAxisPoints, resultBean)
        val state1 = resultBean[0].state2.substring(0,
                resultBean[0].state2.indexOf(","))
        val state2 = resultBean[0].state2.substring(
                resultBean[0].state2.indexOf(",") + 1)
        when {
            state1 == "偏低" || (state1 == "正常" && state2 == "偏低") -> {
                dataLatestLayout.setBackgroundResource(medicineDataBgResIds[2])
                val title = String.format(
                        resources.getString(
                                R.string.app_main_medicine_suggest_hint), medicineDataTextValues[2]
                )
                val content = resources.getString(
                        R.string.app_main_medicine_suggest_blood_pre_low)
                suggestContentTv.text = "${title}${content}"
            }
            state1 == "正常" && state2 == "正常" -> {
                dataLatestLayout.setBackgroundResource(medicineDataBgResIds[1])
                val title = String.format(
                        resources.getString(
                                R.string.app_main_medicine_suggest_hint), medicineDataTextValues[1]
                )
                val content = resources.getString(
                        R.string.app_main_medicine_suggest_blood_pre_normal)
                suggestContentTv.text = "${title}${content}"
            }
            state1 == "偏高" || (state1 == "正常" && state2 == "偏高") -> {
                dataLatestLayout.setBackgroundResource(medicineDataBgResIds[0])
                val title = String.format(
                        resources.getString(
                                R.string.app_main_medicine_suggest_hint), medicineDataTextValues[0]
                )
                val content = resources.getString(
                        R.string.app_main_medicine_suggest_blood_pre_high)
                suggestContentTv.text = "${title}${content}"
            }
        }
        dataTagTv.text = resultBean[0].state
        dataValueTv.text = "${resultBean[0].sbp}/" +
                "${resultBean[0].dbp}"
        measureTime.text = DateUtilKotlin.uTCToLocal(
                resultBean[0].testTime, TIME_FORMAT_PATTERN1)
    }

    private fun initBloodLineChart(belowBloodAxisPoints: ArrayList<PointValue>,
                                   highBloodAxisPoints: ArrayList<PointValue>,
                                   list: List<BloodListBeann>) {
        //折线的颜色
        val lines = arrayListOf<Line>()
        val line1 = Line(belowBloodAxisPoints)
                .setColor(ContextCompat.getColor(mActivity, R.color.color46E8FD))
        val line2 = Line(highBloodAxisPoints)
                .setColor(ContextCompat.getColor(mActivity, R.color.color4897FF))
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
        setAxisXShow(data)
        setAxisYShow(data)
        dataChart.lineChartData = data
        resetViewport(dataChart, bloodYMaxValue)
        //创建一个图标视图 大小为控件的最大大小
    }

    private fun getHighBloodAxisPoints(list: List<BloodListBeann>): ArrayList<PointValue> {
        val pointValues = arrayListOf<PointValue>()
        val pointYValues = arrayListOf<Float>()
        if (list.size == 1) {
            pointYValues.add(0f)
        }
        list.forEach {
            pointYValues.add(it.sbp.toFloat())
        }

        if (list.size == 1) {
            for (i in 0 until pointYValues.size) {
                if (i == 0) {
                    pointValues.add(PointValue(i.toFloat(), pointYValues[i]))
                } else {
                    val colorInt =
                            when (list[i - 1].state2.substring(0,
                                    list[i - 1].state2.indexOf(","))) {
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
                if (bloodYMaxValue < pointYValues[i]) bloodYMaxValue = pointYValues[i]
            }
        } else {
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
                if (bloodYMaxValue < pointYValues[i]) bloodYMaxValue = pointYValues[i]
            }
        }
        bloodYMaxValue += 10
        return pointValues
    }

    private fun getBelowBloodAxisPoints(list: List<BloodListBeann>): ArrayList<PointValue> {
        val pointValues = arrayListOf<PointValue>()
        val pointYValues = arrayListOf<Float>()
        if (list.size == 1) {
            pointYValues.add(0f)
        }
        list.forEach {
            pointYValues.add(it.dbp.toFloat())
        }
        //1条数据的问题
        if (list.size == 1) {
            for (i in 0 until pointYValues.size) {
                if (i == 0) {
                    pointValues.add(PointValue(i.toFloat(), pointYValues[i]))
                } else {
                    val colorInt =
                            when (list[i - 1].state2.substring(
                                    list[i - 1].state2.indexOf(",") + 1)) {
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
            }
        } else {
            for (i in 0 until pointYValues.size) {
                val substring = list[i].state2.substring(
                        list[i].state2.indexOf(",") + 1)
                val colorInt =
                        when (substring) {
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
        }

        return pointValues
    }

    private fun getBloodOxygenData() {
        mDialog2 = DialogProgress(mActivity, null)
        val accountId = MMKVUtil.getInt(BIND_DEVICE_ACCOUNT_ID)
        lifecycleScope.launch {
            ApiCoroutinesCallBack.resultParse(mActivity, onStart = {
                if (!isRefresh) mDialog2?.showDialog()
            }, block = {
                val resultBean = GuiderApiUtil.getHDApiService()
                        .getHealthBloodOxygenData(accountId, 1, 7, sort = SortType.DESC)
                if (resultBean is String && resultBean == "null") {
                    dealBloodOxygenEmptyList()
                } else {
                    if (resultBean != null) {
                        dealBloodOxygenSuccessList(resultBean)
                    } else {
                        dealBloodOxygenEmptyList()
                    }

                }
            }, onRequestFinish = {
                if (!isRefresh) mDialog2?.hideDialog()
                isRefresh = false
            })
        }
    }

    @SuppressLint("SetTextI18n")
    private fun dealBloodOxygenEmptyList() {
        if (isRefresh) {
            refreshLayout.finishRefresh()
        }
        noDataTv.visibility = View.VISIBLE
        initBloodOxygenLineChart(arrayListOf(), arrayListOf())
        dataTagTv.text = ""
        dataValueTv.text = mActivity.resources.getString(R.string.app_no_data)
        measureTime.text = ""
        suggestContentTv.text = mActivity.resources.getString(R.string.app_no_suggest)
    }

    @SuppressLint("SetTextI18n")
    private fun dealBloodOxygenSuccessList(resultBean: Any) {
        if (isRefresh) refreshLayout.finishRefresh(500)
        noDataTv.visibility = View.GONE
        val tempList = ParseJsonData.parseJsonDataList<BloodOxygenListBean>(
                resultBean, BloodOxygenListBean::class.java
        )
        val bloodOxygenAxisPoints = getBloodOxygenAxisPoints(tempList)
        initBloodOxygenLineChart(bloodOxygenAxisPoints, tempList)
        when (tempList[0].state2) {
            "偏低" -> {
                dataLatestLayout.setBackgroundResource(medicineDataBgResIds[2])
                val title = String.format(
                        resources.getString(
                                R.string.app_main_medicine_suggest_hint), medicineDataTextValues[2]
                )
                val content = resources.getString(
                        R.string.app_main_medicine_suggest_blood_oxygen_low)
                suggestContentTv.text = "${title}${content}"
            }
            else -> {
                dataLatestLayout.setBackgroundResource(medicineDataBgResIds[1])
                val title = String.format(
                        resources.getString(
                                R.string.app_main_medicine_suggest_hint), medicineDataTextValues[1]
                )
                val content = resources.getString(
                        R.string.app_main_medicine_suggest_blood_oxygen_normal)
                suggestContentTv.text = "${title}${content}"
            }
        }
        dataValueTv.text = tempList[0].bo.toString()
        dataTagTv.text = tempList[0].state
        measureTime.text = DateUtilKotlin.uTCToLocal(
                tempList[0].testTime, TIME_FORMAT_PATTERN1)
    }

    private fun getBloodOxygenAxisPoints(list: List<BloodOxygenListBean>): ArrayList<PointValue> {
        val pointValues = arrayListOf<PointValue>()
        val pointYValues = arrayListOf<Float>()
        if (list.size == 1) {
            pointYValues.add(0f)
        }
        list.forEach {
            pointYValues.add(it.bo.toFloat())
        }
        heartYMaxValue = 0f

        if (list.size == 1) {
            for (i in 0 until pointYValues.size) {
                if (i == 0) {
                    pointValues.add(PointValue(i.toFloat(), pointYValues[i]))
                } else {
                    val colorInt =
                            when (list[i - 1].state2) {
                                "偏低" -> {
                                    CommonUtils.getColor(mActivity, R.color.colorF18937)
                                }
                                else -> CommonUtils.getColor(mActivity, R.color.color59d15f)
                            }
                    pointValues.add(PointValue(i.toFloat(), pointYValues[i], colorInt))
                }
                if (heartYMaxValue < pointYValues[i]) heartYMaxValue = pointYValues[i]
            }
        } else {
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
        }
        heartYMaxValue += 10
        return pointValues
    }

    private fun initBloodOxygenLineChart(bloodOxygenAxisPoints: ArrayList<PointValue>,
                                         list: List<BloodOxygenListBean>) {
        //折线的颜色
        val lines = arrayListOf<Line>()
        val line1 = Line(bloodOxygenAxisPoints)
                .setColor(ContextCompat.getColor(mActivity, R.color.color59d15f))
        if (list.size == 1) {
            line1.setHasLines(false) //是否用直线显示。如果为false 则没有曲线只有点显示
        } else {
            line1.setHasLines(true) //是否用直线显示。如果为false 则没有曲线只有点显示
        }
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
        mDialog3 = DialogProgress(mActivity, null)
        if (!isRefresh) mDialog3?.showDialog()
        val accountId = MMKVUtil.getInt(BIND_DEVICE_ACCOUNT_ID)
        lifecycleScope.launch {
            ApiCoroutinesCallBack.resultParse(mActivity, block = {
                val resultBean = GuiderApiUtil.getHDApiService()
                        .getHealthBloodSugarChartData(accountId, 1, 7, sort = SortType.DESC)
                if (!resultBean.isNullOrEmpty()) {
                    dealBloodSugarSuccessList(resultBean)
                } else {
                    dealBloodSugarEmptyList()
                }
            }, onRequestFinish = {
                if (!isRefresh) mDialog3?.hideDialog()
                if (isRefresh) {
                    refreshLayout.finishRefresh()
                }
                isRefresh = false
            })
        }
    }

    @SuppressLint("SetTextI18n")
    private fun dealBloodSugarEmptyList() {
        if (isRefresh) {
            refreshLayout.finishRefresh()
        }
        noDataTv.visibility = View.VISIBLE
        initDataChartShow(arrayListOf(), arrayListOf())
        dataTagTv.text = ""
        dataValueTv.text = mActivity.resources.getString(R.string.app_no_data)
        measureTime.text = ""
        suggestContentTv.text = mActivity.resources.getString(R.string.app_no_suggest)
    }

    @SuppressLint("SetTextI18n")
    private fun dealBloodSugarSuccessList(resultBean: List<BloodSugarListBean>) {
        noDataTv.visibility = View.GONE
        if (isRefresh) refreshLayout.finishRefresh(500)
        //返回的数据类中state2的第一个字段为血糖状态
        when (resultBean[0].state2.substring(0,
                resultBean[0].state2.indexOf(","))) {
            "偏低" -> {
                dataLatestLayout.setBackgroundResource(medicineDataBgResIds[2])
                val title = String.format(
                        resources.getString(
                                R.string.app_main_medicine_suggest_hint), medicineDataTextValues[2]
                )
                val content = resources.getString(
                        R.string.app_main_medicine_suggest_sugar_low)
                suggestContentTv.text = "${title}${content}"
            }
            "偏高" -> {
                dataLatestLayout.setBackgroundResource(medicineDataBgResIds[0])
                val title = String.format(
                        resources.getString(
                                R.string.app_main_medicine_suggest_hint), medicineDataTextValues[0]
                )
                val content = resources.getString(
                        R.string.app_main_medicine_suggest_sugar_high)
                suggestContentTv.text = "${title}${content}"
            }
            else -> {
                dataLatestLayout.setBackgroundResource(medicineDataBgResIds[1])
                val title = String.format(
                        resources.getString(
                                R.string.app_main_medicine_suggest_hint), medicineDataTextValues[1]
                )
                val content = resources.getString(
                        R.string.app_main_medicine_suggest_sugar_normal)
                suggestContentTv.text = "${title}${content}"
            }
        }
        val iUnit: IUnit = UnitUtil.getIUnit(mActivity)
        val value: Double = iUnit.getGluShowValue(
                resultBean[0].bs, 2)
        dataValueTv.text = value.toString()
        dataTagTv.text = resultBean[0].state
        measureTime.text = DateUtilKotlin.uTCToLocal(
                resultBean[0].testTime, TIME_FORMAT_PATTERN1)
        val bloodSugarAxisPoints = getBloodSugarAxisPoints(resultBean)
        initDataChartShow(bloodSugarAxisPoints, resultBean)
    }

    private fun initDataChartShow(bloodSugarAxisPoints: ArrayList<PointValue>,
                                  list: List<BloodSugarListBean>) {
        //折线的颜色
        val lines = arrayListOf<Line>()
        val line1 = Line(bloodSugarAxisPoints)
                .setColor(ContextCompat.getColor(mActivity, R.color.color59d15f))
        if (list.size == 1) {
            line1.setHasLines(false) //是否用直线显示。如果为false 则没有曲线只有点显示
        } else {
            line1.setHasLines(true) //是否用直线显示。如果为false 则没有曲线只有点显示
        }
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
//        line.setHasLines(true) //是否用直线显示。如果为false 则没有曲线只有点显示
        line.setHasPoints(true) //是否显示圆点 如果为false 则没有原点只有点显示
        lines.add(line)
    }

    private fun getBloodSugarAxisPoints(list: List<BloodSugarListBean>): ArrayList<PointValue> {
        val pointValues = arrayListOf<PointValue>()
        val pointYValues = arrayListOf<Float>()
        val iUnit: IUnit = UnitUtil.getIUnit(mActivity)
        if (list.size == 1) {
            pointYValues.add(0f)
        }
        list.forEach {
            val value = iUnit.getGluShowValue(it.bs, 2).toFloat()
            pointYValues.add(value)
        }
        if (list.size == 1) {
            for (i in 0 until pointYValues.size) {
                if (i == 0) {
                    pointValues.add(PointValue(i.toFloat(), pointYValues[i]))
                } else {
                    val colorInt =
                            when (list[i - 1].state2.substring(0,
                                    list[i - 1].state2.indexOf(","))) {
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
                if (yMaxValue < pointYValues[i]) yMaxValue = pointYValues[i]
            }
        } else {
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
                if (yMaxValue < pointYValues[i]) yMaxValue = pointYValues[i]
            }
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