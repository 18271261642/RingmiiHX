package com.guider.healthring.adpter

import android.content.Context
import android.view.View
import com.guider.healthring.R
import com.guider.healthring.bean.ListTitleSelectBean
import com.guider.healthring.widget.recyclerview.ViewHolder
import com.guider.healthring.widget.recyclerview.adapter.CommonAdapter
import kotlinx.android.synthetic.main.item_health_result_list_title.view.*

class HealthResultListTitleAdapter(context: Context, dataList: ArrayList<ListTitleSelectBean>)
    : CommonAdapter<ListTitleSelectBean>(context, dataList,
        R.layout.item_health_result_list_title) {
    private var listener: OnClickItemListener? = null

    fun setListener(listener: OnClickItemListener) {
        this.listener = listener
    }

    override fun bindData(holder: ViewHolder, data: ListTitleSelectBean, position: Int) {
        holder.itemView.titleTv.text = data.title
        holder.itemView.titleTv.isSelected = data.isSelected
        if (data.isSelected) {
            holder.itemView.tagLine.visibility = View.VISIBLE
        } else holder.itemView.tagLine.visibility = View.GONE
        holder.setOnItemClickListener {
            listener?.onClick(holder.adapterPosition)
        }
    }

    interface OnClickItemListener {
        fun onClick(position: Int)
    }
}