package com.guider.gps.adapter

import android.content.Context
import android.view.View
import com.guider.baselib.utils.BIND_DEVICE_ACCOUNT_ID
import com.guider.baselib.utils.DateUtilKotlin
import com.guider.baselib.widget.image.ImageLoaderUtils
import com.guider.baselib.widget.recyclerview.ViewHolder
import com.guider.baselib.widget.recyclerview.adapter.CommonAdapter
import com.guider.gps.R
import com.guider.health.apilib.bean.AnswerListBean
import com.guider.health.apilib.enums.AnswerMsgType
import com.guider.health.apilib.utils.MMKVUtil
import kotlinx.android.synthetic.main.include_answer_msg_receive.view.*
import kotlinx.android.synthetic.main.include_answer_msg_send.view.*

class AnswerListAdapter(context: Context, dataList: ArrayList<AnswerListBean>)
    : CommonAdapter<AnswerListBean>(context, dataList, R.layout.item_answer_list) {

    override fun bindData(holder: ViewHolder, data: AnswerListBean, position: Int) {
        with(data) {
            holder.run {
                //判断是否是发送方
                val isSendUser = accountId == MMKVUtil.getInt(BIND_DEVICE_ACCOUNT_ID, 0)
                setText(R.id.timeTv, DateUtilKotlin.getDateWithWeekWithTime(mContext, createTime))
                val receiveLayout = itemView.receiveLayout
                val sendLayout = itemView.sendLayout
                val receiveContentTv = itemView.receiveContentTv
                val receiveContentIv = itemView.receiveContentIv
                val sendContentTv = itemView.sendContentTv
                val sendContentIv = itemView.sendContentIv
                if (isSendUser) {
                    sendLayout.visibility = View.VISIBLE
                    receiveLayout.visibility = View.GONE
                    when (type) {
                        AnswerMsgType.STRING -> {
                            sendContentTv.visibility = View.VISIBLE
                            sendContentIv.visibility = View.GONE
                            setText(R.id.sendContentTv, content)
                        }
                        AnswerMsgType.IMAGE -> {
                            sendContentTv.visibility = View.GONE
                            sendContentIv.visibility = View.VISIBLE
                            ImageLoaderUtils.loadImage(mContext,
                                    getView(R.id.sendContentIv), content)
                        }
                        else -> {
                            sendContentTv.visibility = View.VISIBLE
                            sendContentIv.visibility = View.GONE
                            setText(R.id.sendContentTv, content)
                        }
                    }
                } else {
                    sendLayout.visibility = View.GONE
                    receiveLayout.visibility = View.VISIBLE
                    when (data.type) {
                        AnswerMsgType.STRING -> {
                            receiveContentTv.visibility = View.VISIBLE
                            receiveContentIv.visibility = View.GONE
                            setText(R.id.receiveContentTv, content)
                        }
                        AnswerMsgType.IMAGE -> {
                            receiveContentTv.visibility = View.GONE
                            receiveContentIv.visibility = View.VISIBLE
                            ImageLoaderUtils.loadImage(mContext, getView(R.id.sendContentIv),
                                    content)
                        }
                        else -> {
                            receiveContentTv.visibility = View.VISIBLE
                            receiveContentIv.visibility = View.GONE
                            setText(R.id.receiveContentTv, content)
                        }
                    }
                }
            }
        }
    }
}