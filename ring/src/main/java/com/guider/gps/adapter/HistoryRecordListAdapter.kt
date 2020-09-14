package com.guider.gps.adapter

import android.content.Context
import com.guider.baselib.utils.DateUtilKotlin
import com.guider.baselib.utils.TIME_FORMAT_PATTERN1
import com.guider.baselib.widget.recyclerview.ViewHolder
import com.guider.baselib.widget.recyclerview.adapter.CommonAdapter
import com.guider.gps.R
import com.guider.health.apilib.bean.UserPositionListBean

class HistoryRecordListAdapter(context: Context, dataList: ArrayList<UserPositionListBean>)
    : CommonAdapter<UserPositionListBean>(context, dataList, R.layout.item_history_record) {

    override fun bindData(holder: ViewHolder, data: UserPositionListBean, position: Int) {
        val localTime = DateUtilKotlin.uTCToLocal(data.testTime, TIME_FORMAT_PATTERN1)
        holder.setText(R.id.timeTv, localTime)
        holder.setText(R.id.historyLocation,data.addr)
    }
}