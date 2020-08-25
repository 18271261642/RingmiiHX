package com.guider.gps.adapter

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.guider.baselib.widget.recyclerview.ViewHolder
import com.guider.baselib.widget.recyclerview.adapter.CommonAdapter
import com.guider.gps.R

class AbnormalMsgListAdapter(context: Context, dataList: ArrayList<String>)
    : CommonAdapter<String>(context, dataList, R.layout.item_abnormal_msg) {

    override fun bindData(holder: ViewHolder, data: String, position: Int) {
        val msgDataDetailRv = holder.getView<RecyclerView>(R.id.msgDataDetailRv)
        msgDataDetailRv.isNestedScrollingEnabled = true
        msgDataDetailRv.layoutManager = LinearLayoutManager(mContext)
        when (data) {
            "11:05" -> {
                holder.setImageResource(R.id.msgTypeIv, R.drawable.icon_home_blood)
                holder.setText(R.id.msgTypeTv,
                        String.format(mContext.resources.getString(
                                R.string.app_msg_item_title), mContext.resources.getString(
                                R.string.app_main_health_blood_pressure)))
                val list = arrayListOf(mContext.resources.getString(
                        R.string.app_msg_item_collect_compressive),
                        (mContext.resources.getString(
                                R.string.app_msg_item_shu_zhang_pressure)), "心率1")
                val adapter = AbnormalMsgContentDetailAdapter(mContext, list)
                msgDataDetailRv.adapter = adapter
            }
            "10:16" -> {
                holder.setImageResource(R.id.msgTypeIv, R.drawable.icon_home_heart)
                holder.setText(R.id.msgTypeTv, String.format(mContext.resources.getString(
                        R.string.app_msg_item_title), mContext.resources.getString(
                        R.string.app_main_health_heart_rate)))
                val list = arrayListOf("心率2")
                val adapter = AbnormalMsgContentDetailAdapter(mContext, list)
                msgDataDetailRv.adapter = adapter
            }
        }
    }
}