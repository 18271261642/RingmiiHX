package com.guider.gps.adapter

import android.content.Context
import com.guider.baselib.utils.DateUtilKotlin
import com.guider.baselib.utils.StringUtil
import com.guider.baselib.widget.recyclerview.ViewHolder
import com.guider.baselib.widget.recyclerview.adapter.CommonAdapter
import com.guider.gps.R
import com.guider.health.apilib.bean.SystemMsgBean
import com.guider.health.apilib.enums.SystemMsgType

class SystemMsgListAdapter(context: Context, dataList: ArrayList<SystemMsgBean>)
    : CommonAdapter<SystemMsgBean>(context, dataList, R.layout.item_system_msg) {

    override fun bindData(holder: ViewHolder, data: SystemMsgBean, position: Int) {
        if (StringUtil.isNotBlankAndEmpty(data.createTime))
            holder.setText(
                    R.id.timeTv, DateUtilKotlin.getDateWithWeekWithTime(mContext, data.createTime!!))
        when (data.type) {
            //手环电量
            SystemMsgType.OPELECTRICITY -> {
                holder.setText(R.id.titleTv, mContext.resources
                        .getString(R.string.app_bracelet_power_reminder))
            }
            //关机
            SystemMsgType.CLOSE -> {
                holder.setText(R.id.titleTv, mContext.resources
                        .getString(R.string.app_shutdown_to_remind))
            }
            // 擦除
            SystemMsgType.TAKEOFF -> {
                holder.setText(R.id.titleTv, mContext.resources
                        .getString(R.string.app_Erase_remind))
            }
            //sos报警
            SystemMsgType.SOS -> {
                holder.setText(R.id.titleTv, mContext.resources
                        .getString(R.string.app_sos_alarm))
            }
            //电子围栏报警
            SystemMsgType.FENCE -> {
                holder.setText(R.id.titleTv, mContext.resources
                        .getString(R.string.app_electronic_fence_alarm))
            }
            else -> {
                holder.setText(R.id.titleTv, mContext.resources
                        .getString(R.string.app_electronic_fence_alarm))
            }
        }
        holder.setText(R.id.contentTv, "${data.name},${data.content}")
    }
}