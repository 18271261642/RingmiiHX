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
        with(data) {
            holder.run {
                setText(R.id.dataTitle, type)
                if (StringUtil.isNotBlankAndEmpty(state)) {
                    when (state) {
                        "偏低" -> {
                            setViewVisibility(R.id.dataStatusIv, View.VISIBLE)
                            setImageResource(R.id.dataStatusIv, R.drawable.icon_arrow_low)
                        }
                        "偏高" -> {
                            setViewVisibility(R.id.dataStatusIv, View.VISIBLE)
                            setImageResource(R.id.dataStatusIv, R.drawable.icon_arrow_high)
                        }
                        else -> {
                            setViewVisibility(R.id.dataStatusIv, View.GONE)
                        }
                    }
                } else setViewVisibility(R.id.dataStatusIv, View.GONE)
                //心率值复杂一些
                if (type == mContext.resources.getString(
                                R.string.app_main_health_heart_rate)) {
                    if (StringUtil.isNotBlankAndEmpty(vaule) && vaule != "0")
                        setText(R.id.dataContentTv, vaule)
                    else setText(R.id.dataContentTv, "-")

                } else {
                    setText(R.id.dataContentTv, vaule)
                }
            }
        }
    }
}