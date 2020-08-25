package com.guider.gps.adapter

import android.content.Context
import com.guider.baselib.widget.recyclerview.ViewHolder
import com.guider.baselib.widget.recyclerview.adapter.CommonAdapter
import com.guider.gps.R

class HistoryRecordListAdapter(context: Context, dataList: ArrayList<String>)
    : CommonAdapter<String>(context, dataList, R.layout.item_history_record) {

    override fun bindData(holder: ViewHolder, data: String, position: Int) {
        holder.setText(R.id.timeTv, data)
    }
}