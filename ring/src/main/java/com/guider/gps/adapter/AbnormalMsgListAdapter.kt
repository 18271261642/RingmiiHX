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
        with(data) {
            holder.run {
                val msgDataDetailRv = getView<RecyclerView>(R.id.msgDataDetailRv)
                msgDataDetailRv.isNestedScrollingEnabled = true
                msgDataDetailRv.layoutManager = LinearLayoutManager(mContext)
                if (StringUtil.isNotBlankAndEmpty(testTime))
                    setText(
                            R.id.timeTv, DateUtilKotlin.getDateWithWeekWithTime(mContext, testTime))
                setText(R.id.testTimeTv, DateUtilKotlin.uTCToLocal(
                        testTime, TIME_FORMAT_PATTERN1))
                val suggestTv = getView<TextView>(R.id.suggestTv)
                val accountId = MMKVUtil.getInt(USER.USERID)
                val msgContent = getView<TextView>(R.id.msgContent)
                msgDifferentTypeEvent(this@with, this, msgDataDetailRv,
                        suggestTv, accountId, msgContent)
                setOnItemClickListener {
                    listener?.onClickItem(adapterPosition)
                }

            }
        }
    }

    private fun msgDifferentTypeEvent(data: AbnormalRingMsgListBean,
                                      holder: ViewHolder,
                                      msgDataDetailRv: RecyclerView,
                                      suggestTv: TextView,
                                      accountId: Int,
                                      msgContent: TextView) {
        with(data) {
            if (dataBean != null) {
                when (dataType) {
                    //血糖
                    HealthWarnDataType.BLOODSUGAR -> {
                        bloodGlucose(holder, this, msgDataDetailRv,
                                suggestTv, accountId, msgContent)
                    }
                    //血压
                    HealthWarnDataType.BLOODPRESSUREDYNAMIC -> {
                        bloodPressure(holder, this, msgDataDetailRv,
                                suggestTv, accountId, msgContent)
                    }
                    //血氧
                    HealthWarnDataType.BLOODOXYGEN -> {
                        bloodOxygen(holder, this, msgDataDetailRv,
                                suggestTv, accountId, msgContent)
                    }
                    //体温
                    HealthWarnDataType.BODYTEMP -> {
                        bodyTemp(holder, this, msgDataDetailRv,
                                suggestTv, accountId, msgContent)
                    }
                    //心率
                    else -> {
                        heartRate(holder, this, msgDataDetailRv,
                                suggestTv, accountId, msgContent)
                    }
                }
            }
        }
    }

    private fun heartRate(holder: ViewHolder, data: AbnormalRingMsgListBean,
                          msgDataDetailRv: RecyclerView, suggestTv: TextView,
                          accountId: Int, msgContent: TextView) {
        with(data) {
            holder.run {
                setImageResource(R.id.msgTypeIv,
                        R.drawable.icon_health_warn_msg_heart_rate)
                setText(R.id.msgTypeTv, String.format(mContext.resources.getString(
                        R.string.app_msg_item_title), mContext.resources.getString(
                        R.string.app_main_health_heart_rate)))
                val list = arrayListOf(SimpleWithTypeBean(
                        mContext.resources.getString(
                                R.string.app_main_health_heart_rate),
                        dataBean!!.hb.toString().plus(heart),
                        dataBean!!.state2
                ), SimpleWithTypeBean(
                        mContext.resources.getString(
                                R.string.app_health_msg_heart_rate_state),
                        dataBean!!.state)
                )
                val adapter = AbnormalMsgContentDetailAdapter(mContext, list)
                msgDataDetailRv.adapter = adapter
                when (dataBean?.stateKey) {
                    EnumHealthDataStateKey.Low.key, EnumHealthDataStateKey.High.key -> {
                        suggestTv.text = mContext.resources.getString(
                                R.string.app_main_medicine_suggest_heart_rate_abnormal)
                    }
                    else -> {
                        suggestTv.text = mContext.resources.getString(
                                R.string.app_main_medicine_suggest_heart_rate_normal)
                    }
                }
                if (accountId == accountId) {
                    msgContent.text = mContext.resources.getString(
                            R.string.app_abnormal_msg_tile_model_own,
                            mContext.resources.getString(
                                    R.string.app_main_health_heart_rate))
                } else {
                    msgContent.text = mContext.resources.getString(
                            R.string.app_abnormal_msg_tile_model,
                            relationShip, mContext.resources.getString(
                            R.string.app_main_health_heart_rate))
                }
            }
        }
    }

    private fun bodyTemp(holder: ViewHolder, data: AbnormalRingMsgListBean,
                         msgDataDetailRv: RecyclerView, suggestTv: TextView,
                         accountId: Int, msgContent: TextView) {
        with(holder) {
            data.run {
                setImageResource(R.id.msgTypeIv,
                        R.drawable.icon_health_warn_msg_body_temp)
                setText(R.id.msgTypeTv, String.format(mContext.resources.getString(
                        R.string.app_msg_item_title), mContext.resources.getString(
                        R.string.app_main_health_body_temp)))
                val list = arrayListOf(SimpleWithTypeBean(
                        mContext.resources.getString(
                                R.string.app_main_health_body_temp),
                        dataBean!!.bt.toString().plus(bodyTemp),
                        dataBean!!.state2
                ), SimpleWithTypeBean(
                        mContext.resources.getString(
                                R.string.app_health_msg_body_temp_state),
                        dataBean!!.state)
                )
                val adapter = AbnormalMsgContentDetailAdapter(mContext, list)
                msgDataDetailRv.adapter = adapter
                when (dataBean?.stateKey) {
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
                if (accountId == accountId) {
                    msgContent.text = mContext.resources.getString(
                            R.string.app_abnormal_msg_tile_model_own,
                            mContext.resources.getString(
                                    R.string.app_main_health_body_temp))
                } else {
                    msgContent.text = mContext.resources.getString(
                            R.string.app_abnormal_msg_tile_model,
                            relationShip, mContext.resources.getString(
                            R.string.app_main_health_body_temp))
                }
            }
        }
    }

    private fun bloodOxygen(holder: ViewHolder, data: AbnormalRingMsgListBean,
                            msgDataDetailRv: RecyclerView, suggestTv: TextView,
                            accountId: Int, msgContent: TextView) {
        with(data) {
            holder.run {
                setImageResource(R.id.msgTypeIv,
                        R.drawable.icon_health_warn_msg_blood_oxygen)
                setText(R.id.msgTypeTv, String.format(mContext.resources.getString(
                        R.string.app_msg_item_title), mContext.resources.getString(
                        R.string.app_main_health_blood_oxygen)))
                val list = arrayListOf(SimpleWithTypeBean(
                        mContext.resources.getString(
                                R.string.app_main_health_blood_oxygen_title),
                        dataBean!!.bo.toString().plus(bloodO2),
                        dataBean!!.state2
                ), SimpleWithTypeBean(
                        mContext.resources.getString(
                                R.string.app_health_msg_blood_oxygen_state),
                        dataBean!!.state)
                )
                val adapter = AbnormalMsgContentDetailAdapter(mContext, list)
                msgDataDetailRv.adapter = adapter
                when (dataBean?.stateKey) {
                    EnumHealthDataStateKey.Low.key -> {
                        suggestTv.text = mContext.resources.getString(
                                R.string.app_main_medicine_suggest_blood_oxygen_low)
                    }
                    else -> {
                        suggestTv.text = mContext.resources.getString(
                                R.string.app_main_medicine_suggest_blood_oxygen_normal)
                    }
                }
                if (accountId == accountId) {
                    msgContent.text = mContext.resources.getString(
                            R.string.app_abnormal_msg_tile_model_own,
                            mContext.resources.getString(
                                    R.string.app_main_health_blood_oxygen))
                } else {
                    msgContent.text = mContext.resources.getString(
                            R.string.app_abnormal_msg_tile_model,
                            relationShip, mContext.resources.getString(
                            R.string.app_main_health_blood_oxygen))
                }
            }
        }
    }

    private fun bloodPressure(holder: ViewHolder, data: AbnormalRingMsgListBean,
                              msgDataDetailRv: RecyclerView, suggestTv: TextView,
                              accountId: Int, msgContent: TextView) {
        with(holder) {
            data.run {
                setImageResource(R.id.msgTypeIv,
                        R.drawable.icon_health_warn_msg_blood_pressure)
                setText(R.id.msgTypeTv,
                        String.format(mContext.resources.getString(
                                R.string.app_msg_item_title), mContext.resources.getString(
                                R.string.app_main_health_blood_pressure)))
                val state1 = dataBean!!.state2.substring(0,
                        dataBean!!.state2.indexOf(","))
                val state2 = dataBean!!.state2.substring(
                        dataBean!!.state2.indexOf(",") + 1)
                val list = arrayListOf(SimpleWithTypeBean(
                        mContext.resources.getString(R.string.app_msg_item_collect_compressive),
                        dataBean!!.sbp.toString().plus(bp), state1),
                        SimpleWithTypeBean(
                                mContext.resources.getString(
                                        R.string.app_msg_item_shu_zhang_pressure),
                                dataBean!!.dbp.toString(), state2),
                        SimpleWithTypeBean(
                                mContext.resources.getString(
                                        R.string.app_main_health_heart_rate),
                                dataBean!!.hb.toString()),
                        SimpleWithTypeBean(
                                mContext.resources.getString(
                                        R.string.app_health_msg_blood_pressure_state),
                                dataBean!!.state)
                )
                val adapter = AbnormalMsgContentDetailAdapter(mContext, list)
                msgDataDetailRv.adapter = adapter
                when (dataBean?.stateKey) {
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
                if (accountId == accountId) {
                    msgContent.text = mContext.resources.getString(
                            R.string.app_abnormal_msg_tile_model_own,
                            mContext.resources.getString(
                                    R.string.app_main_health_blood_pressure))
                } else {
                    msgContent.text = mContext.resources.getString(
                            R.string.app_abnormal_msg_tile_model,
                            relationShip, mContext.resources.getString(
                            R.string.app_main_health_blood_pressure))
                }
            }
        }
    }

    private fun bloodGlucose(holder: ViewHolder, data: AbnormalRingMsgListBean,
                             msgDataDetailRv: RecyclerView, suggestTv: TextView,
                             accountId: Int, msgContent: TextView) {
        with(holder) {
            data.run {
                setImageResource(R.id.msgTypeIv,
                        R.drawable.icon_health_warn_msg_blood_glucose)
                setText(R.id.msgTypeTv,
                        String.format(mContext.resources.getString(
                                R.string.app_msg_item_title), mContext.resources.getString(
                                R.string.app_main_health_blood_sugar)))
                val iUnit: IUnit = UnitUtil.getIUnit()
                val value: Double = iUnit.getGluShowValue(dataBean!!.bs, 2)
                val list = arrayListOf(SimpleWithTypeBean(
                        mContext.resources.getString(R.string.app_main_health_blood_sugar),
                        "${value}${mContext.resources.getString(R.string.glu_unit_food_2)}",
                        dataBean!!.state.substring(0, dataBean!!
                                .state2.indexOf(","))
                ),
                        SimpleWithTypeBean(
                                mContext.resources.getString(
                                        R.string.app_health_msg_blood_sugar_state),
                                dataBean!!.state)
                )
                val adapter = AbnormalMsgContentDetailAdapter(mContext, list)
                msgDataDetailRv.adapter = adapter
                when (dataBean?.stateKey) {
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
                if (accountId == accountId) {
                    msgContent.text = mContext.resources.getString(
                            R.string.app_abnormal_msg_tile_model_own,
                            mContext.resources.getString(
                                    R.string.app_main_health_blood_sugar))
                } else {
                    msgContent.text = mContext.resources.getString(
                            R.string.app_abnormal_msg_tile_model,
                            relationShip, mContext.resources.getString(
                            R.string.app_main_health_blood_sugar))
                }
            }
        }
    }
}