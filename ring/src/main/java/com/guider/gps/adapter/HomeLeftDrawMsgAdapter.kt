package com.guider.gps.adapter

import android.content.Context
import android.view.View
import android.widget.ImageView
import com.guider.baselib.utils.AdapterOnItemClickListener
import com.guider.baselib.utils.StringUtil
import com.guider.baselib.widget.BatteryView
import com.guider.baselib.widget.image.ImageLoaderUtils
import com.guider.baselib.widget.recyclerview.ViewHolder
import com.guider.baselib.widget.recyclerview.adapter.CommonAdapter
import com.guider.gps.R
import com.guider.health.apilib.bean.UserInfo

/**
 * 首页左侧抽屉的adapter
 */
class HomeLeftDrawMsgAdapter(context: Context, dataList: ArrayList<UserInfo>)
    : CommonAdapter<UserInfo>(context, dataList, R.layout.item_home_draw_msg) {

    private var listener: AdapterOnItemClickListener? = null

    fun setListener(listener: AdapterOnItemClickListener) {
        this.listener = listener
    }

    private var longClickListener: AdapterOnItemLongClickListener? = null

    fun setLongClickListener(longClickListener: AdapterOnItemLongClickListener) {
        this.longClickListener = longClickListener
    }

    override fun bindData(holder: ViewHolder, data: UserInfo, position: Int) {
        if (data.isSelected == 1) {
            holder.setViewVisibility(R.id.msgSelectIv, View.VISIBLE)
        } else holder.setViewVisibility(R.id.msgSelectIv, View.GONE)
        holder.setText(R.id.msgPersonName, data.relationShip)
        val batteryView = holder.getView<BatteryView>(R.id.batteryLayout)
        when (data.relationShip) {
            mContext.resources.getString(R.string.app_own_string) -> {
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
        }
        if (StringUtil.isNotBlankAndEmpty(data.headUrl)) {
            holder.setImagePath(R.id.msgHeadIv,
                    object : ViewHolder.HolderImageLoader(data.headUrl!!) {
                        override fun loadImage(iv: ImageView, path: String) {
                            ImageLoaderUtils.loadImage(mContext, iv, path)
                        }
                    })
        } else {
            holder.setImageResource(R.id.msgHeadIv, R.drawable.bg_image_default)
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