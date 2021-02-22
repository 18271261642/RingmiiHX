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
import com.guider.health.apilib.utils.MMKVUtil
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
        with(holder) {
            data.run {
                if (isSelected == 1) {
                    setViewVisibility(R.id.msgSelectIv, View.VISIBLE)
                    itemView.rootLayout.setBackgroundColor(
                            CommonUtils.getColor(mContext, R.color.colorFFFBF3))
                } else {
                    setViewVisibility(R.id.msgSelectIv, View.INVISIBLE)
                    itemView.rootLayout.setBackgroundColor(
                            CommonUtils.getColor(mContext, R.color.white))
                }
                setText(R.id.msgPersonName, relationShip ?: name ?: "")
                val batteryView = getView<BatteryView>(R.id.batteryLayout)
                val deviceCodeTv = getView<TextView>(R.id.deviceCodeTv)
                if (StringUtil.isNotBlankAndEmpty(deviceCode)) {
                    deviceCodeTv.visibility = View.VISIBLE
                    val spanny = Spanny().append("IMEI：",
                            ForegroundColorSpan(
                                    CommonUtils.getColor(mContext, R.color.color333333)))
                            .append(deviceCode, ForegroundColorSpan(
                                    CommonUtils.getColor(mContext, R.color.color999999)))
                    deviceCodeTv.text = spanny
                } else deviceCodeTv.visibility = View.GONE
                if (StringUtil.isNotBlankAndEmpty(deviceState)) {
                    setViewVisibility(R.id.msgRingStatusIv, View.VISIBLE)
                    setViewVisibility(R.id.msgRingStatusTv, View.VISIBLE)
                    when (deviceState) {
                        "ONLINE" -> {
                            setImageResource(R.id.msgRingStatusIv, R.drawable.oval_green_59d15f)
                            setText(R.id.msgRingStatusTv,
                                    mContext.resources.getString(
                                            R.string.app_main_left_msg_device_status_online))
                            setViewVisibility(R.id.batteryLayout, View.VISIBLE)
                            setViewVisibility(R.id.rssiIv, View.VISIBLE)
                            when (rssi) {
                                in 0..10 -> {
                                    setImageResource(R.id.rssiIv, R.drawable.icon_device_rssi_1)
                                }
                                in 11..30 -> {
                                    setImageResource(R.id.rssiIv, R.drawable.icon_device_rssi_2)
                                }
                                in 31..60 -> {
                                    setImageResource(R.id.rssiIv, R.drawable.icon_device_rssi_3)
                                }
                                else -> {
                                    setImageResource(R.id.rssiIv, R.drawable.icon_device_rssi_4)
                                }
                            }
                            batteryView.power = electricity
                        }
                        "OFFLINE" -> {
                            setImageResource(R.id.msgRingStatusIv, R.drawable.oval_red_d76155)
                            setText(R.id.msgRingStatusTv, mContext.resources.getString(
                                    R.string.app_main_left_msg_device_status_offline))
                            setViewVisibility(R.id.batteryLayout, View.GONE)
                            setViewVisibility(R.id.rssiIv, View.GONE)
                        }
                    }
                } else {
                    setViewVisibility(R.id.msgRingStatusIv, View.GONE)
                    setViewVisibility(R.id.msgRingStatusTv, View.GONE)
                }
                if (StringUtil.isNotBlankAndEmpty(headUrl)) {
                    setImagePath(R.id.msgHeadIv,
                            object : ViewHolder.HolderImageLoader(headUrl!!) {
                                override fun loadImage(iv: ImageView, path: String) {
                                    ImageLoaderUtils.loadImage(mContext, iv, path)
                                }
                            })
                } else {
                    setImageResource(R.id.msgHeadIv, R.drawable.icon_default_user)
                }
                setOnItemClickListener {
                    listener?.onClickItem(adapterPosition)
                }
                setOnItemLongClickListener {
                    longClickListener?.onClickItem(adapterPosition)
                    true
                }
                if (MMKVUtil.getInt(USER.USERID) != 0 &&
                        MMKVUtil.getInt(USER.USERID) == accountId) {
                    setViewVisibility(R.id.msgPersonNameIv, View.GONE)
                } else {
                    setViewVisibility(R.id.msgPersonNameIv, View.VISIBLE)
                    itemView.nameLayout.setOnClickListener {
                        editClickListener?.onEditItem(adapterPosition)
                    }
                }
                if (deviceMode == PositionType.FINDER.name) {
                    itemView.searchPersonBgView.visibility = View.VISIBLE
                    itemView.searchPersonFlag.visibility = View.VISIBLE
                    itemView.distanceView.visibility = View.VISIBLE
                } else {
                    itemView.searchPersonBgView.visibility = View.GONE
                    itemView.searchPersonFlag.visibility = View.GONE
                    itemView.distanceView.visibility = View.GONE
                }
            }
        }
    }

    interface AdapterOnItemLongClickListener {
        fun onClickItem(position: Int)
    }

    interface AdapterEditClickListener {
        fun onEditItem(position: Int)
    }
}