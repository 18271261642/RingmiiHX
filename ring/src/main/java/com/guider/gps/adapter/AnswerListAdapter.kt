package com.guider.gps.adapter

import android.content.Context
import android.view.View
import com.guider.baselib.utils.BIND_DEVICE_ACCOUNT_ID
import com.guider.baselib.utils.DateUtilKotlin
import com.guider.baselib.utils.MMKVUtil
import com.guider.baselib.widget.image.ImageLoaderUtils
import com.guider.baselib.widget.recyclerview.ViewHolder
import com.guider.baselib.widget.recyclerview.adapter.CommonAdapter
import com.guider.gps.R
import com.guider.health.apilib.bean.AnswerListBean
import com.guider.health.apilib.enums.AnswerMsgType
import kotlinx.android.synthetic.main.include_answer_msg_receive.view.*
import kotlinx.android.synthetic.main.include_answer_msg_send.view.*

class AnswerListAdapter(context: Context, dataList: ArrayList<AnswerListBean>)
    : CommonAdapter<AnswerListBean>(context, dataList, R.layout.item_answer_list) {

    override fun bindData(holder: ViewHolder, data: AnswerListBean, position: Int) {
        //判断是否是发送方
        val isSendUser = data.accountId == MMKVUtil.getInt(BIND_DEVICE_ACCOUNT_ID, 0)
        holder.setText(R.id.timeTv, DateUtilKotlin.getDateWithWeekWithTime(mContext, data.createTime))
        val receiveLayout = holder.itemView.receiveLayout
        val sendLayout = holder.itemView.sendLayout
        val receiveContentTv = holder.itemView.receiveContentTv
        val receiveContentIv = holder.itemView.receiveContentIv
        val sendContentTv = holder.itemView.sendContentTv
        val sendContentIv = holder.itemView.sendContentIv
        if (isSendUser) {
            sendLayout.visibility = View.VISIBLE
            receiveLayout.visibility = View.GONE
            when (data.type) {
                AnswerMsgType.STRING -> {
                    sendContentTv.visibility = View.VISIBLE
                    sendContentIv.visibility = View.GONE
                    holder.setText(R.id.sendContentTv, data.content)
                }
                AnswerMsgType.IMAGE -> {
                    sendContentTv.visibility = View.GONE
                    sendContentIv.visibility = View.VISIBLE
                    ImageLoaderUtils.loadImage(mContext, holder.getView(R.id.sendContentIv), data.content)
                }
                else -> {
                    sendContentTv.visibility = View.VISIBLE
                    sendContentIv.visibility = View.GONE
                    holder.setText(R.id.sendContentTv, data.content)
                }
            }
        } else {
            sendLayout.visibility = View.GONE
            receiveLayout.visibility = View.VISIBLE
            when (data.type) {
                AnswerMsgType.STRING -> {
                    receiveContentTv.visibility = View.VISIBLE
                    receiveContentIv.visibility = View.GONE
                    holder.setText(R.id.receiveContentTv, data.content)
                }
                AnswerMsgType.IMAGE -> {
                    receiveContentTv.visibility = View.GONE
                    receiveContentIv.visibility = View.VISIBLE
                    ImageLoaderUtils.loadImage(mContext, holder.getView(R.id.sendContentIv), data.content)
                }
                else -> {
                    receiveContentTv.visibility = View.VISIBLE
                    receiveContentIv.visibility = View.GONE
                    holder.setText(R.id.receiveContentTv, data.content)
                }
            }
        }
    }
}