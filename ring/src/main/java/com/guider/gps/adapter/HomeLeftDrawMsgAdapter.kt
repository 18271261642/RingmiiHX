package com.guider.gps.adapter

import android.content.Context
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.guider.baselib.utils.*
import com.guider.baselib.widget.BatteryView
import com.guider.baselib.widget.image.ImageLoaderUtils
import com.guider.baselib.widget.recyclerview.ViewHolder
import com.guider.baselib.widget.recyclerview.adapter.CommonAdapter
import com.guider.gps.R
import com.guider.health.apilib.bean.UserInfo
import com.guider.health.apilib.enums.PositionType
import kotlinx.android.synthetic.main.item_home_draw_msg.view.*
import java.util.*

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

    private var editClickListener: AdapterEditClickListener? = null

    fun setEditClickListener(editClickListener: AdapterEditClickListener) {
        this.editClickListener = editClickListener
    }

    override fun bindData(holder: ViewHolder, data: UserInfo, position: Int) {
        if (data.isSelected == 1) {
            holder.setViewVisibility(R.id.msgSelectIv, View.VISIBLE)
            holder.itemView.rootLayout.setBackgroundColor(
                    CommonUtils.getColor(mContext, R.color.colorFFFBF3))
        } else {
            holder.setViewVisibility(R.id.msgSelectIv, View.INVISIBLE)
            holder.itemView.rootLayout.setBackgroundColor(
                    CommonUtils.getColor(mContext, R.color.white))
        }
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
                    holder.setViewVisibility(R.id.rssiIv, View.VISIBLE)
                    when (data.rssi) {
                        in 0..10 -> {
                            holder.setImageResource(R.id.rssiIv, R.drawable.icon_device_rssi_1)
                        }
                        in 11..30 -> {
                            holder.setImageResource(R.id.rssiIv, R.drawable.icon_device_rssi_2)
                        }
                        in 31..60 -> {
                            holder.setImageResource(R.id.rssiIv, R.drawable.icon_device_rssi_3)
                        }
                        else -> {
                            holder.setImageResource(R.id.rssiIv, R.drawable.icon_device_rssi_4)
                        }
                    }
                    batteryView.power = data.electricity
                }
                "OFFLINE" -> {
                    holder.setImageResource(R.id.msgRingStatusIv, R.drawable.oval_red_d76155)
                    holder.setText(R.id.msgRingStatusTv, mContext.resources.getString(
                            R.string.app_main_left_msg_device_status_offline))
                    holder.setViewVisibility(R.id.batteryLayout, View.GONE)
                    holder.setViewVisibility(R.id.rssiIv, View.GONE)
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
        if (MMKVUtil.getInt(USER.USERID) != 0 && MMKVUtil.getInt(USER.USERID) == data.accountId) {
            holder.setViewVisibility(R.id.msgPersonNameIv, View.GONE)
        } else {
            holder.setViewVisibility(R.id.msgPersonNameIv, View.VISIBLE)
            holder.itemView.nameLayout.setOnClickListener {
                editClickListener?.onEditItem(holder.adapterPosition)
            }
        }
        if (data.deviceMode == PositionType.FINDER.name) {
            holder.itemView.searchPersonBgView.visibility = View.VISIBLE
            holder.itemView.searchPersonFlag.visibility = View.VISIBLE
            holder.itemView.distanceView.visibility = View.VISIBLE
        } else {
            holder.itemView.searchPersonBgView.visibility = View.GONE
            holder.itemView.searchPersonFlag.visibility = View.GONE
            holder.itemView.distanceView.visibility = View.GONE
        }
    }

    interface AdapterOnItemLongClickListener {
        fun onClickItem(position: Int)
    }

    interface AdapterEditClickListener {
        fun onEditItem(position: Int)
    }
}