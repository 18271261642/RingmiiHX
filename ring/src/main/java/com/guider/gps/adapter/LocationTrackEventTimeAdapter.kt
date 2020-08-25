package com.guider.gps.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import com.guider.baselib.utils.AdapterOnItemClickListener
import com.guider.baselib.utils.CommonUtils
import com.guider.baselib.widget.recyclerview.ViewHolder
import com.guider.baselib.widget.recyclerview.adapter.CommonAdapter
import com.guider.gps.R
import com.guider.gps.bean.WithSelectBaseBean

class LocationTrackEventTimeAdapter(context: Context, dataList: ArrayList<WithSelectBaseBean>)
    : CommonAdapter<WithSelectBaseBean>(context, dataList, R.layout.item_location_track_event_time) {

    private var listener: AdapterOnItemClickListener? = null

    fun setListener(listener: AdapterOnItemClickListener) {
        this.listener = listener
    }

    @SuppressLint("SetTextI18n")
    override fun bindData(holder: ViewHolder, data: WithSelectBaseBean, position: Int) {
        val timeTv = holder.getView<TextView>(R.id.timeTv)
        timeTv.text = String.format(
                mContext.resources.getString(R.string.app_map_track_time_hour), data.name.toInt())
        if (data.isSelect) {
            timeTv.setTextColor(CommonUtils.getColor(mContext, R.color.colorF18937))
            timeTv.setBackgroundResource(R.drawable.circle_f18937_21_bg)
        } else {
            timeTv.setTextColor(CommonUtils.getColor(mContext, R.color.colorCCCCCC))
            timeTv.setBackgroundResource(R.drawable.circle_cccccc_21_bg)
        }
        holder.setOnItemClickListener {
            listener?.onClickItem(holder.adapterPosition)
        }
    }
}