package com.guider.gps.adapter

import android.content.Context
import android.content.Intent
import com.guider.baselib.base.BaseActivity
import com.guider.baselib.widget.recyclerview.ViewHolder
import com.guider.baselib.widget.recyclerview.adapter.CommonAdapter
import com.guider.gps.R
import com.guider.gps.view.activity.HealthCareMsgDetailActivity

class HealthCareMsgListAdapter(context: Context, dataList: ArrayList<String>)
    : CommonAdapter<String>(context, dataList, R.layout.item_health_care_msg) {

    override fun bindData(holder: ViewHolder, data: String, position: Int) {
        val msgContent = "人们每天摄入的热量大部分都来自碳水化合物，如" +
                "面包，面条，大米，谷物和土豆。对于简单碳水化" +
                "合物，饮用牛奶和果汁，食用适量的水果是十分重" +
                "要的。但食用糖和其他甜味剂会提供大量体内不需" +
                "合物，饮用牛奶和果汁，食用适量的水果是十分重" +
                "要的。但食用糖和其他甜味剂会提供大量体内不需" +
                "要的热量对健康有害。对于复杂碳水化合物，…"
        holder.setText(R.id.careMsgContent, msgContent)
        holder.setOnItemClickListener {
            val intent = Intent(mContext, HealthCareMsgDetailActivity::class.java)
            (mContext as BaseActivity).startActivity(intent)
        }
    }
}