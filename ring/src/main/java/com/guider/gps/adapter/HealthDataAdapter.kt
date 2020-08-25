package com.guider.gps.adapter

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.guider.baselib.widget.recyclerview.ViewHolder
import com.guider.baselib.widget.recyclerview.adapter.CommonAdapter
import com.guider.gps.R

class HealthDataAdapter(context: Context, dataList: ArrayList<String>)
    : CommonAdapter<String>(context, dataList, R.layout.item_health_data) {

    override fun bindData(holder: ViewHolder, data: String, position: Int) {
        holder.setText(R.id.dateTv, data)
        val dataDetailListRv = holder.getView<RecyclerView>(R.id.dataDetailListRv)
        dataDetailListRv.isNestedScrollingEnabled = true
        dataDetailListRv.layoutManager = LinearLayoutManager(mContext)
        val list = arrayListOf("08:30", "09:30", "10:30")
        val adapter = HealthDetailListAdapter(mContext, list)
        dataDetailListRv.adapter = adapter
    }
}