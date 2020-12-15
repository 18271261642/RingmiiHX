package com.guider.gps.adapter

import android.content.Context
import com.guider.baselib.utils.AdapterOnItemClickListener
import com.guider.baselib.utils.StringUtil
import com.guider.baselib.widget.image.ImageLoaderUtils
import com.guider.baselib.widget.recyclerview.ViewHolder
import com.guider.baselib.widget.recyclerview.adapter.CommonAdapter
import com.guider.gps.R
import com.guider.health.apilib.bean.DoctorListBean

/**
 * @Package: com.guider.gps.adapter
 * @ClassName: DoctorListAdapter
 * @Description: 医生咨询列表的adapter
 * @Author: hjr
 * @CreateDate: 2020/11/10 14:02
 * Copyright (C), 1998-2020, GuiderTechnology
 */
class DoctorListAdapter(context: Context, dataList: ArrayList<DoctorListBean>)
    : CommonAdapter<DoctorListBean>(context, dataList, R.layout.item_doctor_list) {

    private var listener: AdapterOnItemClickListener? = null

    fun setListener(listener: AdapterOnItemClickListener) {
        this.listener = listener
    }

    override fun bindData(holder: ViewHolder, data: DoctorListBean, position: Int) {
        with(holder) {
            data.run {
                if (StringUtil.isEmpty(headUrl)) {
                    setImageResource(R.id.headerIv, R.drawable.icon_default_user)
                } else {
                    ImageLoaderUtils.loadImage(mContext, holder.getView(R.id.headerIv), headUrl!!)
                }
                setText(R.id.nameTv, name)
                setText(R.id.positionTv, professionalTitle)
                setOnItemClickListener {
                    listener?.onClickItem(position)
                }
            }
        }
    }
}