package com.guider.gps.adapter

import android.content.Context
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.guider.baselib.device.IUnit
import com.guider.baselib.device.Unit.bloodO2
import com.guider.baselib.device.Unit.bodyTemp
import com.guider.baselib.device.Unit.bp
import com.guider.baselib.device.Unit.heart
import com.guider.baselib.utils.*
import com.guider.baselib.widget.recyclerview.ViewHolder
import com.guider.baselib.widget.recyclerview.adapter.CommonAdapter
import com.guider.gps.R
import com.guider.gps.bean.SimpleWithTypeBean
import com.guider.health.apilib.bean.AbnormalRingMsgListBean
import com.guider.health.apilib.enums.EnumHealthDataStateKey
import com.guider.health.apilib.enums.HealthWarnDataType
import com.guider.health.apilib.utils.MMKVUtil

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
                HealthWarnDataType.BLOODSUGAR -> {
                    holder.setImageResource(R.id.msgTypeIv,
                            R.drawable.icon_health_warn_msg_blood_glucose)
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
                                            R.string.app_health_msg_blood_sugar_state),
                                    data.dataBean!!.state)
                    )
                    val adapter = AbnormalMsgContentDetailAdapter(mContext, list)
                    msgDataDetailRv.adapter = adapter
                    when (data.dataBean?.stateKey) {
                        EnumHealthDataStateKey.FbsLow.key,
                        EnumHealthDataStateKey.PbsLow.key -> {
                            suggestTv.text = mContext.resources.getString(
                                    R.string.app_main_medicine_suggest_sugar_low)
                        }
                        EnumHealthDataStateKey.FbsHigh.key,
                        EnumHealthDataStateKey.PbsHigh.key -> {
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
                HealthWarnDataType.BLOODPRESSUREDYNAMIC -> {
                    holder.setImageResource(R.id.msgTypeIv,
                            R.drawable.icon_health_warn_msg_blood_pressure)
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
                            data.dataBean!!.sbp.toString().plus(bp), state1),
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
                                            R.string.app_health_msg_blood_pressure_state),
                                    data.dataBean!!.state)
                    )
                    val adapter = AbnormalMsgContentDetailAdapter(mContext, list)
                    msgDataDetailRv.adapter = adapter
                    when (data.dataBean?.stateKey) {
                        EnumHealthDataStateKey.BpLow.key -> {
                            suggestTv.text = mContext.resources.getString(
                                    R.string.app_main_medicine_suggest_blood_pre_low)
                        }
                        EnumHealthDataStateKey.BpHigh.key -> {
                            suggestTv.text = mContext.resources.getString(
                                    R.string.app_main_medicine_suggest_blood_pre_high)
                        }
                        EnumHealthDataStateKey.BpHyp.key -> {
                            suggestTv.text = mContext.resources.getString(
                                    R.string.app_main_medicine_suggest_hypertension)
                        }
                        else -> {
                            suggestTv.text = mContext.resources.getString(
                                    R.string.app_main_medicine_suggest_blood_pre_normal)
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
                HealthWarnDataType.BLOODOXYGEN -> {
                    holder.setImageResource(R.id.msgTypeIv,
                            R.drawable.icon_health_warn_msg_blood_oxygen)
                    holder.setText(R.id.msgTypeTv, String.format(mContext.resources.getString(
                            R.string.app_msg_item_title), mContext.resources.getString(
                            R.string.app_main_health_blood_oxygen)))
                    val list = arrayListOf(SimpleWithTypeBean(
                            mContext.resources.getString(
                                    R.string.app_main_health_blood_oxygen_title),
                            data.dataBean!!.bo.toString().plus(bloodO2),
                            data.dataBean!!.state2
                    ), SimpleWithTypeBean(
                            mContext.resources.getString(
                                    R.string.app_health_msg_blood_oxygen_state),
                            data.dataBean!!.state)
                    )
                    val adapter = AbnormalMsgContentDetailAdapter(mContext, list)
                    msgDataDetailRv.adapter = adapter
                    when (data.dataBean?.stateKey) {
                        EnumHealthDataStateKey.Low.key -> {
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
                //体温
                HealthWarnDataType.BODYTEMP -> {
                    holder.setImageResource(R.id.msgTypeIv,
                            R.drawable.icon_health_warn_msg_body_temp)
                    holder.setText(R.id.msgTypeTv, String.format(mContext.resources.getString(
                            R.string.app_msg_item_title), mContext.resources.getString(
                            R.string.app_main_health_body_temp)))
                    val list = arrayListOf(SimpleWithTypeBean(
                            mContext.resources.getString(
                                    R.string.app_main_health_body_temp),
                            data.dataBean!!.bt.toString().plus(bodyTemp),
                            data.dataBean!!.state2
                    ), SimpleWithTypeBean(
                            mContext.resources.getString(
                                    R.string.app_health_msg_body_temp_state),
                            data.dataBean!!.state)
                    )
                    val adapter = AbnormalMsgContentDetailAdapter(mContext, list)
                    msgDataDetailRv.adapter = adapter
                    when (data.dataBean?.stateKey) {
                        EnumHealthDataStateKey.Low.key -> {
                            suggestTv.text = mContext.resources.getString(
                                    R.string.app_health_warn_temp_low)
                        }
                        EnumHealthDataStateKey.LowFever.key -> {
                            suggestTv.text = mContext.resources.getString(
                                    R.string.app_health_warn_temp_low_fever)
                        }
                        EnumHealthDataStateKey.MediumFever.key -> {
                            suggestTv.text = mContext.resources.getString(
                                    R.string.app_health_warn_temp_medium_fever)
                        }
                        EnumHealthDataStateKey.HighlyFever.key -> {
                            suggestTv.text = mContext.resources.getString(
                                    R.string.app_health_warn_temp_highly_fever)
                        }
                        EnumHealthDataStateKey.Hyperthermia.key -> {
                            suggestTv.text = mContext.resources.getString(
                                    R.string.app_health_warn_temp_hyperthermia)
                        }
                        else -> {
                            suggestTv.text = mContext.resources.getString(
                                    R.string.app_health_warn_temp_normal)
                        }
                    }
                    if (data.accountId == accountId) {
                        msgContent.text = mContext.resources.getString(
                                R.string.app_abnormal_msg_tile_model_own,
                                mContext.resources.getString(
                                        R.string.app_main_health_body_temp))
                    } else {
                        msgContent.text = mContext.resources.getString(
                                R.string.app_abnormal_msg_tile_model,
                                data.relationShip, mContext.resources.getString(
                                R.string.app_main_health_body_temp))
                    }
                }
                //心率
                else -> {
                    holder.setImageResource(R.id.msgTypeIv,
                            R.drawable.icon_health_warn_msg_heart_rate)
                    holder.setText(R.id.msgTypeTv, String.format(mContext.resources.getString(
                            R.string.app_msg_item_title), mContext.resources.getString(
                            R.string.app_main_health_heart_rate)))
                    val list = arrayListOf(SimpleWithTypeBean(
                            mContext.resources.getString(
                                    R.string.app_main_health_heart_rate),
                            data.dataBean!!.hb.toString().plus(heart),
                            data.dataBean!!.state2
                    ), SimpleWithTypeBean(
                            mContext.resources.getString(
                                    R.string.app_health_msg_heart_rate_state),
                            data.dataBean!!.state)
                    )
                    val adapter = AbnormalMsgContentDetailAdapter(mContext, list)
                    msgDataDetailRv.adapter = adapter
                    when (data.dataBean?.stateKey) {
                        EnumHealthDataStateKey.Low.key, EnumHealthDataStateKey.High.key -> {
                            suggestTv.text = mContext.resources.getString(
                                    R.string.app_main_medicine_suggest_heart_rate_abnormal)
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