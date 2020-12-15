package com.guider.gps.adapter

import android.content.Context
import com.guider.baselib.utils.AdapterOnItemClickListener
import com.guider.baselib.utils.DateUtilKotlin
import com.guider.baselib.utils.StringUtil
import com.guider.baselib.widget.recyclerview.ViewHolder
import com.guider.baselib.widget.recyclerview.adapter.CommonAdapter
import com.guider.gps.R
import com.guider.health.apilib.bean.CareMsgListBean

class HealthCareMsgListAdapter(context: Context, dataList: ArrayList<CareMsgListBean>)
    : CommonAdapter<CareMsgListBean>(context, dataList, R.layout.item_health_care_msg) {

    private var listener: AdapterOnItemClickListener? = null

    fun setListener(listener: AdapterOnItemClickListener) {
        this.listener = listener
    }

    override fun bindData(holder: ViewHolder, data: CareMsgListBean, position: Int) {
        with(holder) {
            data.run {
                setText(R.id.careMsgContent, adviceContent)
                if (StringUtil.isNotBlankAndEmpty(sendTime))
                    setText(
                            R.id.timeTv, DateUtilKotlin.getDateWithWeekWithTime(mContext, sendTime))
                setOnItemClickListener {
                    listener?.onClickItem(adapterPosition)
                }
            }
        }

    }
}