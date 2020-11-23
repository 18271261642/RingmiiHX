package com.guider.gps.adapter

import android.content.Context
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.guider.baselib.device.IUnit
import com.guider.baselib.device.Unit
import com.guider.baselib.utils.*
import com.guider.baselib.widget.recyclerview.ViewHolder
import com.guider.baselib.widget.recyclerview.adapter.CommonAdapter
import com.guider.gps.R
import com.guider.gps.bean.SimpleWithTypeBean
import com.guider.health.apilib.utils.MMKVUtil
import com.guider.health.apilib.bean.AbnormalRingMsgListBean

class AbnormalMsgListAdapter(context: Context, dataList: ArrayList<AbnormalRingMsgListBean>)
    : CommonAdapter<AbnormalRingMsgListBean>(context, dataList, R.layout.item_abnormal_msg) {

    private var listener: AdapterOnItemClickListener? = null

    fun setListener(listener: AdapterOnItemClickListener) {
        this.listener = listener
    }

    override fun bindData(holder: ViewHolder, data: AbnormalRingMsgListBean, position: Int) {
        val msgDataDetailRv = holder.getView<RecyclerView>(R.id.msgDataDetailRv)
        msgDataDetailRv.isNestedScrollingEnabled = true
        msgDataDetailRv.layoutManager = LinearLayoutManager(mContext)
        if (StringUtil.isNotBlankAndEmpty(data.testTime))
            holder.setText(
                    R.id.timeTv, DateUtilKotlin.getDateWithWeekWithTime(mContext, data.testTime))
        holder.setText(R.id.testTimeTv, DateUtilKotlin.uTCToLocal(
                data.testTime, TIME_FORMAT_PATTERN1))
        val suggestTv = holder.getView<TextView>(R.id.suggestTv)
        val accountId = MMKVUtil.getInt(USER.USERID)
        val msgContent = holder.getView<TextView>(R.id.msgContent)
        if (data.dataBean != null) {
            when (data.dataType) {
                //血糖
                "BLOODSUGAR" -> {
                    holder.setImageResource(R.id.msgTypeIv, R.drawable.icon_home_blood)
                    holder.setText(R.id.msgTypeTv,
                            String.format(mContext.resources.getString(
                                    R.string.app_msg_item_title), mContext.resources.getString(
                                    R.string.app_main_health_blood_sugar)))
                    val iUnit: IUnit = UnitUtil.getIUnit()
                    val value: Double = iUnit.getGluShowValue(data.dataBean!!.bs, 2)
                    val list = arrayListOf(SimpleWithTypeBean(
                            mContext.resources.getString(R.string.app_main_health_blood_sugar),
                            "${value}${mContext.resources.getString(R.string.glu_unit_food_2)}",
                            data.dataBean!!.state.substring(0, data.dataBean!!
                                    .state2.indexOf(","))
                    ),
                            SimpleWithTypeBean(
                                    mContext.resources.getString(
                                            R.string.app_health_msg_blood_sugar_level),
                                    data.dataBean!!.state)
                    )
                    val adapter = AbnormalMsgContentDetailAdapter(mContext, list)
                    msgDataDetailRv.adapter = adapter
                    when (data.dataBean!!.state2.substring(0,
                            data.dataBean!!.state2.indexOf(","))) {
                        "偏低" -> {
                            suggestTv.text = mContext.resources.getString(
                                    R.string.app_main_medicine_suggest_sugar_low)
                        }
                        "偏高" -> {
                            suggestTv.text = mContext.resources.getString(
                                    R.string.app_main_medicine_suggest_sugar_high)
                        }
                        else -> {
                            suggestTv.text = mContext.resources.getString(
                                    R.string.app_main_medicine_suggest_sugar_normal)
                        }
                    }
                    if (data.accountId == accountId) {
                        msgContent.text = mContext.resources.getString(
                                R.string.app_abnormal_msg_tile_model_own,
                                mContext.resources.getString(
                                        R.string.app_main_health_blood_sugar))
                    } else {
                        msgContent.text = mContext.resources.getString(
                                R.string.app_abnormal_msg_tile_model,
                                data.relationShip, mContext.resources.getString(
                                R.string.app_main_health_blood_sugar))
                    }
                }
                //血压
                "BLOODPRESSUREDYNAMIC" -> {
                    holder.setImageResource(R.id.msgTypeIv, R.drawable.icon_home_blood_sugar)
                    holder.setText(R.id.msgTypeTv,
                            String.format(mContext.resources.getString(
                                    R.string.app_msg_item_title), mContext.resources.getString(
                                    R.string.app_main_health_blood_pressure)))
                    val state1 = data.dataBean!!.state2.substring(0,
                            data.dataBean!!.state2.indexOf(","))
                    val state2 = data.dataBean!!.state2.substring(
                            data.dataBean!!.state2.indexOf(",") + 1)
                    val list = arrayListOf(SimpleWithTypeBean(
                            mContext.resources.getString(R.string.app_msg_item_collect_compressive),
                            data.dataBean!!.sbp.toString().plus(Unit().bp), state1),
                            SimpleWithTypeBean(
                                    mContext.resources.getString(
                                            R.string.app_msg_item_shu_zhang_pressure),
                                    data.dataBean!!.dbp.toString(), state2),
                            SimpleWithTypeBean(
                                    mContext.resources.getString(
                                            R.string.app_main_health_heart_rate),
                                    data.dataBean!!.hb.toString()),
                            SimpleWithTypeBean(
                                    mContext.resources.getString(
                                            R.string.app_health_msg_blood_pre_level),
                                    data.dataBean!!.state)
                    )
                    val adapter = AbnormalMsgContentDetailAdapter(mContext, list)
                    msgDataDetailRv.adapter = adapter
                    when {
                        state1 == "偏低" || (state1 == "正常" && state2 == "偏低") -> {
                            suggestTv.text = mContext.resources.getString(
                                    R.string.app_main_medicine_suggest_blood_pre_low)
                        }
                        state1 == "正常" && state2 == "正常" -> {
                            suggestTv.text = mContext.resources.getString(
                                    R.string.app_main_medicine_suggest_blood_pre_normal)
                        }
                        state1 == "偏高" || (state1 == "正常" && state2 == "偏高") -> {
                            suggestTv.text = mContext.resources.getString(
                                    R.string.app_main_medicine_suggest_blood_pre_high)
                        }
                    }
                    if (data.accountId == accountId) {
                        msgContent.text = mContext.resources.getString(
                                R.string.app_abnormal_msg_tile_model_own,
                                mContext.resources.getString(
                                        R.string.app_main_health_blood_pressure))
                    } else {
                        msgContent.text = mContext.resources.getString(
                                R.string.app_abnormal_msg_tile_model,
                                data.relationShip, mContext.resources.getString(
                                R.string.app_main_health_blood_pressure))
                    }
                }
                //血氧
                "BLOODOXYGEN" -> {
                    holder.setImageResource(R.id.msgTypeIv, R.drawable.icon_home_blood_oxygen)
                    holder.setText(R.id.msgTypeTv, String.format(mContext.resources.getString(
                            R.string.app_msg_item_title), mContext.resources.getString(
                            R.string.app_main_health_blood_oxygen)))
                    val list = arrayListOf(SimpleWithTypeBean(
                            mContext.resources.getString(
                                    R.string.app_main_health_blood_oxygen_title),
                            data.dataBean!!.bo.toString().plus(Unit().bloodO2),
                            data.dataBean!!.state2
                    ))
                    val adapter = AbnormalMsgContentDetailAdapter(mContext, list)
                    msgDataDetailRv.adapter = adapter
                    when (data.dataBean!!.state2) {
                        "偏低" -> {
                            suggestTv.text = mContext.resources.getString(
                                    R.string.app_main_medicine_suggest_blood_oxygen_low)
                        }
                        else -> {
                            suggestTv.text = mContext.resources.getString(
                                    R.string.app_main_medicine_suggest_blood_oxygen_normal)
                        }
                    }
                    if (data.accountId == accountId) {
                        msgContent.text = mContext.resources.getString(
                                R.string.app_abnormal_msg_tile_model_own,
                                mContext.resources.getString(
                                        R.string.app_main_health_blood_oxygen))
                    } else {
                        msgContent.text = mContext.resources.getString(
                                R.string.app_abnormal_msg_tile_model,
                                data.relationShip, mContext.resources.getString(
                                R.string.app_main_health_blood_oxygen))
                    }
                }
                //心率
                else -> {
                    holder.setImageResource(R.id.msgTypeIv, R.drawable.icon_home_heart)
                    holder.setText(R.id.msgTypeTv, String.format(mContext.resources.getString(
                            R.string.app_msg_item_title), mContext.resources.getString(
                            R.string.app_main_health_heart_rate)))
                    val list = arrayListOf(SimpleWithTypeBean(
                            mContext.resources.getString(
                                    R.string.app_main_health_heart_rate),
                            data.dataBean!!.hb.toString().plus(Unit().heart),
                            data.dataBean!!.state2
                    ))
                    val adapter = AbnormalMsgContentDetailAdapter(mContext, list)
                    msgDataDetailRv.adapter = adapter
                    when (data.dataBean!!.state2) {
                        "偏高", "偏低" -> {
                            suggestTv.text = mContext.resources.getString(
                                    R.string.app_main_medicine_suggest_heart_rate_high)
                        }
                        else -> {
                            suggestTv.text = mContext.resources.getString(
                                    R.string.app_main_medicine_suggest_heart_rate_normal)
                        }
                    }
                    if (data.accountId == accountId) {
                        msgContent.text = mContext.resources.getString(
                                R.string.app_abnormal_msg_tile_model_own,
                                mContext.resources.getString(
                                        R.string.app_main_health_heart_rate))
                    } else {
                        msgContent.text = mContext.resources.getString(
                                R.string.app_abnormal_msg_tile_model,
                                data.relationShip, mContext.resources.getString(
                                R.string.app_main_health_heart_rate))
                    }
                }
            }
        }

        holder.setOnItemClickListener {
            listener?.onClickItem(holder.adapterPosition)
        }
    }
}