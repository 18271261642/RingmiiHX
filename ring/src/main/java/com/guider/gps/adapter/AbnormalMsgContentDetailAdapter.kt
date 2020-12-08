package com.guider.gps.adapter

import android.content.Context
import android.view.View
import com.guider.baselib.utils.StringUtil
import com.guider.baselib.widget.recyclerview.ViewHolder
import com.guider.baselib.widget.recyclerview.adapter.CommonAdapter
import com.guider.gps.R
import com.guider.gps.bean.SimpleWithTypeBean

/**
 * 消息内容内部数据的adapter
 */
class AbnormalMsgContentDetailAdapter(context: Context, dataList: ArrayList<SimpleWithTypeBean>)
    : CommonAdapter<SimpleWithTypeBean>(context, dataList, R.layout.item_annormal_msg_content) {

    override fun bindData(holder: ViewHolder, data: SimpleWithTypeBean, position: Int) {
        holder.setText(R.id.dataTitle, data.type)
        if (StringUtil.isNotBlankAndEmpty(data.state)) {
            when (data.state) {
                "偏低" -> {
                    holder.setViewVisibility(R.id.dataStatusIv, View.VISIBLE)
                    holder.setImageResource(R.id.dataStatusIv, R.drawable.icon_arrow_low)
                }
                "偏高" -> {
                    holder.setViewVisibility(R.id.dataStatusIv, View.VISIBLE)
                    holder.setImageResource(R.id.dataStatusIv, R.drawable.icon_arrow_high)
                }
                else -> {
                    holder.setViewVisibility(R.id.dataStatusIv, View.GONE)
                }
            }
        } else holder.setViewVisibility(R.id.dataStatusIv, View.GONE)
        //心率值复杂一些
        if (data.type == mContext.resources.getString(
                        R.string.app_main_health_heart_rate)) {
            if (StringUtil.isNotBlankAndEmpty(data.vaule) && data.vaule != "0")
                holder.setText(R.id.dataContentTv, data.vaule)
            else holder.setText(R.id.dataContentTv, "-")

        } else {
            holder.setText(R.id.dataContentTv, data.vaule)
        }
    }
}