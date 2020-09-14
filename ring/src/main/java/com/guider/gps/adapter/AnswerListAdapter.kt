package com.guider.gps.adapter

import android.content.Context
import android.view.View
import com.guider.baselib.utils.CommonUtils
import com.guider.baselib.utils.DEFAULT_TIME_FORMAT_PATTERN
import com.guider.baselib.utils.DateUtilKotlin
import com.guider.baselib.utils.TIME_FORMAT_PATTERN10
import com.guider.baselib.widget.image.ImageLoaderUtils
import com.guider.baselib.widget.recyclerview.ViewHolder
import com.guider.baselib.widget.recyclerview.adapter.CommonAdapter
import com.guider.gps.R
import com.guider.health.apilib.bean.AnswerListBean
import com.guider.health.apilib.enums.AnswerMsgType

class AnswerListAdapter(context: Context, dataList: ArrayList<AnswerListBean>)
    : CommonAdapter<AnswerListBean>(context, dataList, R.layout.item_answer_list) {

    override fun bindData(holder: ViewHolder, data: AnswerListBean, position: Int) {
        val uTCToLocal = DateUtilKotlin.uTCToLocal(data.createTime,
                DEFAULT_TIME_FORMAT_PATTERN)!!
        var dateTime = uTCToLocal.substring(uTCToLocal.indexOf("-") + 1)
        dateTime = dateTime.substring(0, dateTime.lastIndexOf(":"))
        val date = dateTime.substring(0, 5)
        val currentDate = CommonUtils.getCurrentDate(TIME_FORMAT_PATTERN10)
        if (date == currentDate) {
            dateTime = dateTime.substring(dateTime.lastIndexOf(":") - 2)
        }
        holder.setText(R.id.timeTv, dateTime)
        when (data.type) {
            AnswerMsgType.STRING -> {
                holder.setViewVisibility(R.id.contentTv, View.VISIBLE)
                holder.setViewVisibility(R.id.contentIv, View.GONE)
                holder.setText(R.id.contentTv, data.content)
            }
            AnswerMsgType.IMAGE -> {
                holder.setViewVisibility(R.id.contentTv, View.GONE)
                holder.setViewVisibility(R.id.contentIv, View.VISIBLE)
                ImageLoaderUtils.loadImage(mContext, holder.getView(R.id.contentIv), data.content)
            }
        }
    }
}