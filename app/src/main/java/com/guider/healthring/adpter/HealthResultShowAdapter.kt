package com.guider.healthring.adpter

import android.content.Context
import com.guider.health.apilib.model.HeartMeasureResultListBean
import com.guider.health.common.device.Unit
import com.guider.health.common.utils.UnitUtil
import com.guider.healthring.R
import com.guider.healthring.util.DateUtilKotlin
import com.guider.healthring.widget.recyclerview.ViewHolder
import com.guider.healthring.widget.recyclerview.adapter.CommonAdapter

class HealthResultShowAdapter(context: Context, dataList: ArrayList<HeartMeasureResultListBean>)
    : CommonAdapter<HeartMeasureResultListBean>(context, dataList, R.layout.item_health_result) {

    private var typeValue = 0
    fun setTypeValue(typeValue: Int) {
        this.typeValue = typeValue
    }

    override fun bindData(holder: ViewHolder, data: HeartMeasureResultListBean, position: Int) {
        val temp = DateUtilKotlin.uTCToLocal(data.testTime, "yyyy-MM/dd HH:mm")
        val testTimeValue = temp?.substring(temp.indexOf("-") + 1)
        holder.setText(R.id.itemTimeText, testTimeValue)
        val unit = Unit()
        val unitValue = when (typeValue) {
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
        when (typeValue) {
            0 -> {
                val iUnit = UnitUtil.getIUnit()
                val value = iUnit.getGluShowValue(data.bs, 2)
                holder.setText(R.id.itemValueText, "${value}${unitValue}")
            }
            1 -> {
                holder.setText(R.id.itemValueText, "${data.bo}${unitValue}")
            }
            2 -> {
                holder.setText(R.id.itemValueText, "${data.bloodSpeed}${unitValue}")
            }
            3 -> {
                holder.setText(R.id.itemValueText, "${data.hb}${unitValue}")
            }
            4 -> {
                holder.setText(R.id.itemValueText, "${data.hemoglobin}${unitValue}")
            }
        }
    }

}