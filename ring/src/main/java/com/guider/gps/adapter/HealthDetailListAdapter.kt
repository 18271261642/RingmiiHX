package com.guider.gps.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.TextView
import com.guider.baselib.device.Unit.bp
import com.guider.baselib.device.Unit.heart
import com.guider.baselib.utils.CommonUtils
import com.guider.baselib.utils.DateUtilKotlin
import com.guider.baselib.utils.TIME_FORMAT_PATTERN4
import com.guider.baselib.utils.TIME_FORMAT_PATTERN8
import com.guider.baselib.widget.recyclerview.ViewHolder
import com.guider.baselib.widget.recyclerview.adapter.CommonAdapter
import com.guider.gps.R
import com.guider.health.apilib.bean.HealthDataSimpleBean

class HealthDetailListAdapter(context: Context, dataList: ArrayList<HealthDataSimpleBean>,
                              var type: String)
    : CommonAdapter<HealthDataSimpleBean>(context, dataList, R.layout.item_health_data_detail) {


    @SuppressLint("SetTextI18n")
    override fun bindData(holder: ViewHolder, data: HealthDataSimpleBean, position: Int) {
        with(holder) {
            data.run {
                setText(R.id.dateTv, DateUtilKotlin.uTCToLocal(testTime, TIME_FORMAT_PATTERN4))
                setText(R.id.timeTv, DateUtilKotlin.uTCToLocal(testTime, TIME_FORMAT_PATTERN8))
                val dataBackValue = getView<TextView>(R.id.dataBackValue)
                val dataFrontValue = getView<TextView>(R.id.dataFrontValue)
                val unitTv = getView<TextView>(R.id.unitTv)
                when (type) {
                    mContext.resources.getString(R.string.app_main_health_blood_pressure) -> {
                        dataFrontValue.visibility = View.VISIBLE
                        dataFrontValue.text = "${sbp}/"
                        dataBackValue.text = dbp.toString()
                        val state1 = state2.substring(0, state2.indexOf(","))
                        val state2 = state2.substring(state2.indexOf(",") + 1)
                        showStatusColor(state1, dataFrontValue)
                        showStatusColor(state2, dataBackValue)
                        unitTv.text = bp
                    }
                    mContext.resources.getString(R.string.app_main_health_heart_rate) -> {
                        dataFrontValue.visibility = View.GONE
                        dataBackValue.text = hb.toString()
                        showStatusColor(state2, dataBackValue)
                        unitTv.text = heart
                    }
                    mContext.resources.getString(R.string.app_main_health_body_temp) -> {
                        dataFrontValue.visibility = View.GONE
                        dataBackValue.text = bt.toString()
                        showStatusColor(state2, dataBackValue)
                        unitTv.text = "°C"
                    }
                    mContext.resources.getString(R.string.app_main_health_sleep) -> {
                        dataFrontValue.visibility = View.GONE
                        dataBackValue.text = minute.toString()
                        dataBackValue.setTextColor(CommonUtils.getColor(
                                mContext, R.color.color333333))
                    }
                    mContext.resources.getString(R.string.app_main_health_sport) -> {
                        dataFrontValue.visibility = View.GONE
                        dataBackValue.text = step.toString()
                        dataBackValue.setTextColor(CommonUtils.getColor(
                                mContext, R.color.color333333))
                    }
                }
            }
        }
    }

    private fun showStatusColor(state: String, view: TextView) {
        when (state) {
            "偏低" -> {
                view.setTextColor(CommonUtils.getColor(
                        mContext, R.color.colorF18937))
            }
            "偏高" -> {
                view.setTextColor(CommonUtils.getColor(
                        mContext, R.color.colorFFd76155))
            }
            else -> {
                view.setTextColor(CommonUtils.getColor(
                        mContext, R.color.color333333))
            }
        }
    }
}