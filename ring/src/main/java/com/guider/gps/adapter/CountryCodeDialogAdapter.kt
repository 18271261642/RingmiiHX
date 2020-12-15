package com.guider.gps.adapter

import android.content.Context
import com.guider.baselib.utils.AdapterOnItemClickListener
import com.guider.baselib.widget.recyclerview.ViewHolder
import com.guider.baselib.widget.recyclerview.adapter.CommonAdapter
import com.guider.gps.R
import com.guider.health.apilib.bean.AreCodeBean

/**
 * @Package: com.guider.gps.adapter
 * @ClassName: CountryCodeDialogAdapter
 * @Description: 国家区号的对话框的adapter
 * @Author: hjr
 * @CreateDate: 2020/8/28 15:38
 * Copyright (C), 1998-2020, GuiderTechnology
 */
class CountryCodeDialogAdapter(context: Context, dataList: ArrayList<AreCodeBean>)
    : CommonAdapter<AreCodeBean>(context, dataList, R.layout.item_country_code_dialog) {

    private var listener: AdapterOnItemClickListener? = null

    fun setListener(listener: AdapterOnItemClickListener) {
        this.listener = listener
    }

    override fun bindData(holder: ViewHolder, data: AreCodeBean, position: Int) {
        with(holder) {
            data.run {
                setText(R.id.itemPhoneRegionsTv, phoneRegious)
                setText(R.id.itemPhoneCountryTv, phoneCountry)
                setText(R.id.itemPhoneCodeTv, phoneAreCode)
                setText(R.id.tv_phone_head, phoneCode)
                setOnItemClickListener {
                    listener?.onClickItem(adapterPosition)
                }
            }
        }
    }
}