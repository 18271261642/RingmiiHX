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
        with(holder) {
            data.run {
                val localTime = DateUtilKotlin.uTCToLocal(data.testTime, TIME_FORMAT_PATTERN1)
                setText(R.id.timeTv, localTime)
                if (StringUtil.isNotBlankAndEmpty(addr)) {
                    setViewVisibility(R.id.historyLocation, View.VISIBLE)
                    setText(R.id.historyLocation, addr)
                } else {
                    setViewVisibility(R.id.historyLocation, View.GONE)
                }
                setText(R.id.locationMethodTv, String.format(
                        mContext.resources.getString(
                                R.string.app_map_location_method_content), signalType
                ))
                val detailLayout = getView<ConstraintLayout>(R.id.detailLayout)
                detailLayout.setOnClickListener(object : OnNoDoubleClickListener {
                    override fun onNoDoubleClick(v: View) {
                        MapUtils.startGuide(mContext, lat, lng)
                    }

                })
                setOnItemClickListener {
                    listener?.onClickItem(adapterPosition)
                }
            }
        }
    }
}