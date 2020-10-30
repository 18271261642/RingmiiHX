package com.guider.gps.adapter

import android.content.Context
import android.view.View
import com.guider.baselib.utils.AdapterOnItemClickListener
import com.guider.baselib.utils.OnNoDoubleClickListener
import com.guider.baselib.widget.recyclerview.ViewHolder
import com.guider.baselib.widget.recyclerview.adapter.CommonAdapter
import com.guider.gps.R
import com.guider.health.apilib.bean.ElectronicFenceListBean
import kotlinx.android.synthetic.main.item_electronic_fence_list.view.*

/**
 * @Package: com.guider.gps.adapter
 * @ClassName: ElectronicFenceListAdapter
 * @Description: 电子围栏列表的adapter
 * @Author: hjr
 * @CreateDate: 2020/10/29 9:24
 * Copyright (C), 1998-2020, GuiderTechnology
 */
class ElectronicFenceListAdapter(context: Context, dataList: ArrayList<ElectronicFenceListBean>)
    : CommonAdapter<ElectronicFenceListBean>(context, dataList, R.layout.item_electronic_fence_list) {

    private var listener: AdapterOnItemClickListener? = null

    fun setListener(listener: AdapterOnItemClickListener) {
        this.listener = listener
    }

    private var eventListener: AdapterEventListener? = null

    fun setSwitchListener(switchListener: AdapterEventListener) {
        this.eventListener = switchListener
    }

    override fun bindData(holder: ViewHolder, data: ElectronicFenceListBean, position: Int) {
        val fenceSwitch = holder.itemView.fenceSwitch
        fenceSwitch.setCheckedNoEvent(data.open)
        holder.itemView.nameTv.text = data.title
        holder.setOnItemClickListener {
            listener?.onClickItem(holder.adapterPosition)
        }
        fenceSwitch.setOnCheckedChangeListener { _, isChecked ->
            eventListener?.switchChange(holder.adapterPosition, isChecked)
        }
        holder.itemView.deleteIv.setOnClickListener(object : OnNoDoubleClickListener {
            override fun onNoDoubleClick(v: View) {
                eventListener?.deleteItem(holder.adapterPosition)
            }

        })
    }

    interface AdapterEventListener {
        fun switchChange(position: Int, isChecked: Boolean)
        fun deleteItem(position: Int)
    }
}