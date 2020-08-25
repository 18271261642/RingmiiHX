package com.guider.gps.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import com.guider.baselib.utils.CommonUtils
import com.guider.baselib.widget.recyclerview.ViewHolder
import com.guider.baselib.widget.recyclerview.adapter.CommonAdapter
import com.guider.gps.R

class HealthDetailListAdapter (context: Context, dataList: ArrayList<String>)
    : CommonAdapter<String>(context, dataList, R.layout.item_health_data_detail) {

    @SuppressLint("SetTextI18n")
    override fun bindData(holder: ViewHolder, data: String, position: Int) {
        holder.setText(R.id.timeTv,data)
        val dataBackValue = holder.getView<TextView>(R.id.dataBackValue)
        val dataFrontValue = holder.getView<TextView>(R.id.dataFrontValue)

        when(data){
            "08:30" ->{
                dataBackValue.setTextColor(CommonUtils.getColor(mContext,R.color.colorFFd76155))
                dataBackValue.text = "84"
                dataFrontValue.text = "114/"
                holder.setImageResource(R.id.dataStatusIv,R.drawable.icon_arrow_high)
            }
            "09:30" ->{
                dataBackValue.setTextColor(CommonUtils.getColor(mContext,R.color.color333333))
                dataBackValue.text = "66"
                dataFrontValue.text = "114/"
            }
            "10:30" ->{
                dataBackValue.setTextColor(CommonUtils.getColor(mContext,R.color.colorFFd76155))
                dataBackValue.text = "56"
                dataFrontValue.text = "114/"
                holder.setImageResource(R.id.dataStatusIv,R.drawable.icon_arrow_low)
            }
        }
    }
}