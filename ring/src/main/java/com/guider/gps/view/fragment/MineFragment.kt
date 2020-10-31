package com.guider.gps.view.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.guider.baselib.base.BaseFragment
import com.guider.baselib.utils.*
import com.guider.baselib.utils.CommonUtils.logOutClearMMKV
import com.guider.baselib.utils.EventBusAction.REFRESH_TARGET_STEP
import com.guider.baselib.widget.dialog.DialogHolder
import com.guider.baselib.widget.image.ImageLoaderUtils
import com.guider.gps.R
import com.guider.gps.view.activity.*
import com.guider.health.apilib.GuiderApiUtil
import kotlinx.android.synthetic.main.fragment_mine.*
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.math.floor

class MineFragment : BaseFragment() {

    override val layoutRes: Int
        get() = R.layout.fragment_mine

    private var sportValueInt = 0

    override fun initView(rootView: View) {
    }

    override fun initLogic() {
        refreshHeaderAndName()
        ringSetShow()
        versionValue.text = CommonUtils.getPKgVersionName(mActivity)
        bindLayout.setOnClickListener(this)
        topLayout.setOnClickListener(this)
        sportLayout.setOnClickListener(this)
        loginOutLayout.setOnClickListener(this)
    }

    private fun ringSetShow() {
        sportValueInt = if (MMKVUtil.getInt(TARGET_STEP, 0) != 0) {
            MMKVUtil.getInt(TARGET_STEP, 0)
        } else {
            8000
        }
        if (MMKVUtil.getInt(USER.OWN_BIND_DEVICE_CODE) != 0) {
            //说明有绑定的设备 是解绑
            bindTv.text = mActivity.resources.getString(R.string.app_main_mine_remove_bind)
        } else {
            //说明是没有绑定的设备 是绑定
            bindTv.text = mActivity.resources.getString(R.string.app_device_bind)
        }
        sportValue.text = sportValueInt.toString()
        setTempAndHeartShow()
        heartLayout.setOnClickListener(this)
        tempSetLayout.setOnClickListener(this)
    }

    @SuppressLint("SetTextI18n")
    /**
     * 设置心率和体温周期设置
     * @param type 0全部 1心率 2体温
     */
    private fun setTempAndHeartShow(type: Int = 0) {
        if (type == 0 || type == 1) {
            val hrCheck = MMKVUtil.getBoolean(HR_CHECK, false)
            if (hrCheck) {
                val timeIntervalValue = MMKVUtil.getInt(HR_INTERVAL, 0)
                if (timeIntervalValue != 0) {
                    val hours = floor((timeIntervalValue / 60).toDouble()).toInt()
                    val minute: Int = timeIntervalValue % 60
                    if (hours > 0)
                        heartSetTv.text = "$hours" +
                                mActivity.resources.getString(R.string.app_hour_simple) +
                                "$minute" +
                                mActivity.resources.getString(R.string.app_minute_simple)
                    else heartSetTv.text = "$minute" +
                            mActivity.resources.getString(R.string.app_minute_simple)
                }
            } else {
                heartSetTv.text = mActivity.resources.getString(R.string.app_no_open)
            }
        }
        if (type == 0 || type == 2) {
            val btCheck = MMKVUtil.getBoolean(BT_CHECK, false)
            if (btCheck) {
                val timeIntervalValue = MMKVUtil.getInt(BT_INTERVAL, 0)
                if (timeIntervalValue != 0) {
                    val hours = floor((timeIntervalValue / 60).toDouble()).toInt()
                    val minute: Int = timeIntervalValue % 60
                    if (hours > 0)
                        tempSetTv.text = "$hours" +
                                mActivity.resources.getString(R.string.app_hour_simple) +
                                "$minute" +
                                mActivity.resources.getString(R.string.app_minute_simple)
                    else tempSetTv.text = "$minute" +
                            mActivity.resources.getString(R.string.app_minute_simple)
                }
            } else {
                tempSetTv.text = mActivity.resources.getString(R.string.app_no_open)
            }
        }
    }

    override fun openEventBus(): Boolean {
        return true
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshRightRedPoint(event: EventBusEvent<Boolean>) {
        if (event.code == EventBusAction.REFRESH_MINE_FRAGMENT_UNBIND_SHOW) {
            if (event.data!!) {
                bindTv.text = mActivity.resources.getString(R.string.app_main_mine_remove_bind)
            } else {
                bindTv.text = mActivity.resources.getString(R.string.app_device_bind)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshTargetStep(event: EventBusEvent<Int>) {
        if (event.code == REFRESH_TARGET_STEP) {
            if (event.data != 0) {
                sportValueInt = event.data!!
                sportValue.text = sportValueInt.toString()
            }
        }
    }

    override fun onNoDoubleClick(v: View) {
        when (v) {
            bindLayout -> {
                if (MMKVUtil.getInt(USER.OWN_BIND_DEVICE_CODE) != 0) {
                    //说明有绑定的设备 是解绑
                    unBindDialogShow()
                } else {
                    //说明是没有绑定的设备 是绑定
                    val intent = Intent(mActivity, AddNewDeviceActivity::class.java)
                    intent.putExtra("type", "unBindAndBindNew")
                    startActivityForResult(intent, ADD_NEW_DEVICE)
                }
            }
            topLayout -> {
                val intent = Intent(mActivity, PersonInfoActivity::class.java)
                startActivityForResult(intent, PERSON_INFO)
            }
            sportLayout -> {
                val intent = Intent(mActivity, SingleLineEditActivity::class.java)
                intent.putExtra("type",
                        resources.getString(R.string.app_main_mine_sport_target))
                if (sportValueInt != 0)
                    intent.putExtra("inputValue", sportValueInt.toString())
                startActivityForResult(intent, SPORT_STEP_INFO)
            }
            loginOutLayout -> {
                logOutClearMMKV()
                val intent = Intent(mActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or
                        Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                mActivity.finish()
            }
            heartLayout -> {
                val intent = Intent(mActivity, AlarmSeActivity::class.java)
                intent.putExtra("enterType", "heart")
                startActivityForResult(intent, ALARM_SET)
            }
            tempSetLayout -> {
                val intent = Intent(mActivity, AlarmSeActivity::class.java)
                intent.putExtra("enterType", "temp")
                startActivityForResult(intent, ALARM_SET)
            }
        }
    }

    private fun unBindDialogShow() {
        val dialog = object : DialogHolder(mActivity,
                R.layout.dialog_common_with_title, Gravity.CENTER) {
            @SuppressLint("SetTextI18n")
            override fun bindView(dialogView: View) {
                val unBindContentTv = dialogView.findViewById<TextView>(R.id.unBindContentTv)
                val unbindValue = String.format(
                        resources.getString(R.string.app_main_unbind_device),
                        mActivity.resources.getString(R.string.app_own_string))
                unBindContentTv.text = unbindValue
                val cancel = dialogView.findViewById<ConstraintLayout>(R.id.cancelLayout)
                cancel.setOnClickListener {
                    dialog?.dismiss()
                }
                val confirm = dialogView.findViewById<ConstraintLayout>(R.id.confirmLayout)
                confirm.setOnClickListener {
                    dialog?.dismiss()
                    unBindEvent()
                }
            }
        }
        dialog.initView()
        dialog.show(true)
    }

    private fun unBindEvent() {
        val accountId = MMKVUtil.getInt(USER.USERID)
        (mActivity as MainActivity).unbindDeviceFromMineFragment(accountId)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                SPORT_STEP_INFO -> {
                    if (StringUtil.isNotBlankAndEmpty(data.getStringExtra("inputResult"))) {
                        sportValueInt = data.getStringExtra("inputResult")!!.toInt()
                        setTargetStepData(sportValueInt)
                    }
                }
                PERSON_INFO -> {
                    if (data.getBooleanExtra("isChange", false))
                        refreshHeaderAndName(false)
                }
                ALARM_SET -> {
                    if (StringUtil.isNotBlankAndEmpty(data.getStringExtra("enterType"))) {
                        val enterType = data.getStringExtra("enterType")!!
                        if (enterType == "heart") {
                            setTempAndHeartShow(type = 1)
                        } else {
                            setTempAndHeartShow(type = 2)
                        }
                    }
                }
            }
        }
    }

    private fun setTargetStepData(sportValueInt: Int) {
        val accountId = MMKVUtil.getInt(USER.USERID)
        lifecycleScope.launch {
            ApiCoroutinesCallBack.resultParse(mActivity, onStart = {
                mActivity.showDialog()
            }, block = {
                val resultBean = GuiderApiUtil.getApiService()
                        .setWalkTarget(accountId, sportValueInt)
                if (resultBean != null) {
                    sportValue.text = sportValueInt.toString()
                    MMKVUtil.saveInt(TARGET_STEP, sportValueInt)
                    EventBusUtils.sendEvent(EventBusEvent(
                            REFRESH_TARGET_STEP, sportValueInt))
                    showToast(mActivity.resources.getString(R.string.app_set_success))
                }
            }, onRequestFinish = {
                mActivity.dismissDialog()
            })
        }
    }

    private fun refreshHeaderAndName(isFirst: Boolean = true) {
        if (MMKVUtil.containKey(USER.HEADER) &&
                StringUtil.isNotBlankAndEmpty(MMKVUtil.getString(USER.HEADER))) {
            ImageLoaderUtils.loadImage(mActivity, headerIv, MMKVUtil.getString(USER.HEADER))
        } else {
            headerIv.setImageResource(R.drawable.icon_default_user)
        }
        if (MMKVUtil.containKey(USER.NAME) &&
                StringUtil.isNotBlankAndEmpty(MMKVUtil.getString(USER.NAME))) {
            nameTv.text = MMKVUtil.getString(USER.NAME)
        }
        if (!isFirst) EventBusUtils.sendEvent(
                EventBusEvent(EventBusAction.REFRESH_LATEST_GROUP_DATA, true))
    }
}