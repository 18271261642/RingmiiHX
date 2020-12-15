package com.guider.gps.adapter

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.guider.baselib.widget.recyclerview.ViewHolder
import com.guider.baselib.widget.recyclerview.adapter.CommonAdapter
import com.guider.gps.R
import com.guider.health.apilib.bean.HealthDataDetailListBean

class HealthDataAdapter(context: Context, dataList: ArrayList<HealthDataDetailListBean>,
                        val type: String)
    : CommonAdapter<HealthDataDetailListBean>(context, dataList, R.layout.item_health_data) {

    override fun bindData(holder: ViewHolder, data: HealthDataDetailListBean, position: Int) {
        with(holder) {
            data.run {
                setText(R.id.dateTv, time)
                val dataDetailListRv = getView<RecyclerView>(R.id.dataDetailListRv)
                dataDetailListRv.isNestedScrollingEnabled = true
                dataDetailListRv.layoutManager = LinearLayoutManager(mContext)
                val adapter = HealthDetailListAdapter(mContext, list as ArrayList, type)
                dataDetailListRv.adapter = adapter
            }
        }
    }
}