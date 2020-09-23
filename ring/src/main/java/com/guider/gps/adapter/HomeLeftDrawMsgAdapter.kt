package com.guider.gps.adapter

import android.content.Context
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.guider.baselib.utils.AdapterOnItemClickListener
import com.guider.baselib.utils.CommonUtils
import com.guider.baselib.utils.Spanny
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
        } else holder.setViewVisibility(R.id.msgSelectIv, View.INVISIBLE)
        holder.setText(R.id.msgPersonName, data.relationShip)
        val batteryView = holder.getView<BatteryView>(R.id.batteryLayout)
        val deviceCodeTv = holder.getView<TextView>(R.id.deviceCodeTv)
        if (StringUtil.isNotBlankAndEmpty(data.deviceCode)) {
            deviceCodeTv.visibility = View.VISIBLE
            val spanny = Spanny().append("IMEI：",
                    ForegroundColorSpan(
                            CommonUtils.getColor(mContext, R.color.color333333)))
                    .append(data.deviceCode, ForegroundColorSpan(
                            CommonUtils.getColor(mContext, R.color.color999999)))
            deviceCodeTv.text = spanny
        } else deviceCodeTv.visibility = View.GONE
        if (StringUtil.isNotBlankAndEmpty(data.deviceState)) {
            holder.setViewVisibility(R.id.msgRingStatusIv, View.VISIBLE)
            holder.setViewVisibility(R.id.msgRingStatusTv, View.VISIBLE)
            when (data.deviceState) {
                "ONLINE" -> {
                    holder.setImageResource(R.id.msgRingStatusIv, R.drawable.oval_green_59d15f)
                    holder.setText(R.id.msgRingStatusTv,
                            mContext.resources.getString(
                                    R.string.app_main_left_msg_device_status_online))
                    holder.setViewVisibility(R.id.batteryLayout, View.VISIBLE)
                    holder.setViewVisibility(R.id.batteryStatusTv, View.VISIBLE)
                    batteryView.power = if (data.electricity == null) {
                        0
                    } else data.electricity!!
                    holder.setText(R.id.batteryStatusTv, "${
                        if (data.electricity == null) {
                            0
                        } else data.electricity!!
                    }%")
                }
                "OFFLINE" -> {
                    holder.setImageResource(R.id.msgRingStatusIv, R.drawable.oval_red_d76155)
                    holder.setText(R.id.msgRingStatusTv, mContext.resources.getString(
                            R.string.app_main_left_msg_device_status_offline))
                }
            }
        } else {
            holder.setViewVisibility(R.id.msgRingStatusIv, View.GONE)
            holder.setViewVisibility(R.id.msgRingStatusTv, View.GONE)

        }
        if (StringUtil.isNotBlankAndEmpty(data.headUrl)) {
            holder.setImagePath(R.id.msgHeadIv,
                    object : ViewHolder.HolderImageLoader(data.headUrl!!) {
                        override fun loadImage(iv: ImageView, path: String) {
                            ImageLoaderUtils.loadImage(mContext, iv, path)
                        }
                    })
        } else {
            holder.setImageResource(R.id.msgHeadIv, R.drawable.icon_default_user)
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