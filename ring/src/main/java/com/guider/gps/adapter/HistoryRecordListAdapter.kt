package com.guider.gps.adapter

import android.content.Context
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.guider.baselib.utils.*
import com.guider.baselib.widget.recyclerview.ViewHolder
import com.guider.baselib.widget.recyclerview.adapter.CommonAdapter
import com.guider.gps.R
import com.guider.health.apilib.bean.UserPositionListBean

class HistoryRecordListAdapter(context: Context, dataList: ArrayList<UserPositionListBean>)
    : CommonAdapter<UserPositionListBean>(context, dataList, R.layout.item_history_record) {


    private var listener: AdapterOnItemClickListener? = null

    fun setListener(listener: AdapterOnItemClickListener) {
        this.listener = listener
    }

    override fun bindData(holder: ViewHolder, data: UserPositionListBean, position: Int) {
        val localTime = DateUtilKotlin.uTCToLocal(data.testTime, TIME_FORMAT_PATTERN1)
        holder.setText(R.id.timeTv, localTime)
        if (StringUtil.isNotBlankAndEmpty(data.addr)){
            holder.setViewVisibility(R.id.historyLocation,View.VISIBLE)
            holder.setText(R.id.historyLocation, data.addr)
        }else {
            holder.setViewVisibility(R.id.historyLocation,View.GONE)
        }
        holder.setText(R.id.locationMethodTv, String.format(
                mContext.resources.getString(
                        R.string.app_map_location_method_content), data.signalType
        ))
        val detailLayout = holder.getView<ConstraintLayout>(R.id.detailLayout)
        detailLayout.setOnClickListener(object : OnNoDoubleClickListener {
            override fun onNoDoubleClick(v: View) {
                MapUtils.startGuide(mContext, data.lat, data.lng)
            }

        })
        holder.setOnItemClickListener {
            listener?.onClickItem(holder.adapterPosition)
        }
    }
}