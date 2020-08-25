package com.guider.gps.adapter

import android.content.Context
import android.view.View
import com.guider.baselib.utils.AdapterOnItemClickListener
import com.guider.baselib.widget.BatteryView
import com.guider.baselib.widget.recyclerview.ViewHolder
import com.guider.baselib.widget.recyclerview.adapter.CommonAdapter
import com.guider.gps.R
import com.guider.gps.bean.WithSelectBaseBean

/**
 * 首页左侧抽屉的adapter
 */
class HomeLeftDrawMsgAdapter(context: Context, dataList: ArrayList<WithSelectBaseBean>)
    : CommonAdapter<WithSelectBaseBean>(context, dataList, R.layout.item_home_draw_msg) {

    private var listener: AdapterOnItemClickListener? = null

    fun setListener(listener: AdapterOnItemClickListener) {
        this.listener = listener
    }

    private var longClickListener: AdapterOnItemLongClickListener? = null

    fun setLongClickListener(longClickListener: AdapterOnItemLongClickListener) {
        this.longClickListener = longClickListener
    }

    override fun bindData(holder: ViewHolder, data: WithSelectBaseBean, position: Int) {
        if (data.isSelect) {
            holder.setViewVisibility(R.id.msgSelectIv, View.VISIBLE)
        } else holder.setViewVisibility(R.id.msgSelectIv, View.GONE)
        holder.setText(R.id.msgPersonName, data.name)
        val batteryView = holder.getView<BatteryView>(R.id.batteryLayout)
        when (data.name) {
            mContext.resources.getString(R.string.app_main_left_msg_device_name1) -> {
                holder.setImageResource(R.id.msgRingStatusIv, R.drawable.oval_green_59d15f)
                holder.setText(R.id.msgRingStatusTv,
                        mContext.resources.getString(
                                R.string.app_main_left_msg_device_status_online))
                holder.setViewVisibility(R.id.batteryLayout, View.VISIBLE)
                holder.setViewVisibility(R.id.batteryStatusTv, View.VISIBLE)
                batteryView.power = 100
                holder.setText(R.id.batteryStatusTv, "100%")
            }
            mContext.resources.getString(R.string.app_main_left_msg_device_name2) -> {
                holder.setImageResource(R.id.msgRingStatusIv, R.drawable.oval_red_d76155)
                holder.setText(R.id.msgRingStatusTv, mContext.resources.getString(
                        R.string.app_main_left_msg_device_status_offline))
            }
            mContext.resources.getString(R.string.app_main_left_msg_device_name3) -> {
                holder.setImageResource(R.id.msgRingStatusIv, R.drawable.oval_green_59d15f)
                holder.setText(R.id.msgRingStatusTv, mContext.resources.getString(
                        R.string.app_main_left_msg_device_status_online))
                holder.setViewVisibility(R.id.batteryLayout, View.VISIBLE)
                holder.setViewVisibility(R.id.batteryStatusTv, View.VISIBLE)
                batteryView.power = 54
                holder.setText(R.id.batteryStatusTv, "54%")
            }
        }
        holder.setOnItemClickListener {
            listener?.onClickItem(holder.adapterPosition)
        }
        holder.setOnItemLongClickListener {
            longClickListener?.onClickItem(holder.adapterPosition)
            true
        }
    }

    interface AdapterOnItemLongClickListener {
        fun onClickItem(position: Int)
    }
}