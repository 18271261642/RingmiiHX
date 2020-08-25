package com.guider.gps.adapter

import android.content.Context
import android.view.View
import com.guider.baselib.widget.recyclerview.ViewHolder
import com.guider.baselib.widget.recyclerview.adapter.CommonAdapter
import com.guider.gps.R

/**
 * 消息内容内部数据的adapter
 */
class AbnormalMsgContentDetailAdapter(context: Context, dataList: ArrayList<String>)
    : CommonAdapter<String>(context, dataList, R.layout.item_annormal_msg_content) {

    override fun bindData(holder: ViewHolder, data: String, position: Int) {
        holder.setText(R.id.dataTitle, data)
        when (data) {
            mContext.resources.getString(
                    R.string.app_msg_item_collect_compressive) -> {
                holder.setText(R.id.dataContentTv, "139 mmHg")
                holder.setViewVisibility(R.id.dataStatusIv, View.VISIBLE)
                holder.setImageResource(R.id.dataStatusIv, R.drawable.icon_arrow_high)
            }
            (mContext.resources.getString(
                    R.string.app_msg_item_shu_zhang_pressure)) -> {
                holder.setText(R.id.dataContentTv, "69mmHg")
                holder.setViewVisibility(R.id.dataStatusIv, View.GONE)
            }
            "心率1" -> {
                holder.setText(R.id.dataTitle, (mContext.resources.getString(
                        R.string.app_main_health_heart_rate)))
                holder.setText(R.id.dataContentTv, "-")
                holder.setViewVisibility(R.id.dataStatusIv, View.GONE)
            }
            "心率2" -> {
                holder.setText(R.id.dataTitle, (mContext.resources.getString(
                        R.string.app_main_health_heart_rate)))
                holder.setText(R.id.dataContentTv, "162 次/分")
                holder.setViewVisibility(R.id.dataStatusIv, View.GONE)
            }
        }
    }
}