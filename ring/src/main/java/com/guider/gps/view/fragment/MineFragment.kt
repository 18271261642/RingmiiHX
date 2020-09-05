package com.guider.gps.view.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.guider.baselib.base.BaseFragment
import com.guider.baselib.utils.*
import com.guider.baselib.utils.CommonUtils.logOutClearMMKV
import com.guider.baselib.widget.dialog.DialogHolder
import com.guider.baselib.widget.image.ImageLoaderUtils
import com.guider.gps.R
import com.guider.gps.view.activity.*
import com.guider.health.apilib.ApiCallBack
import com.guider.health.apilib.ApiUtil
import com.guider.health.apilib.IGuiderApi
import kotlinx.android.synthetic.main.fragment_mine.*
import retrofit2.Call
import retrofit2.Response

class MineFragment : BaseFragment() {

    override val layoutRes: Int
        get() = R.layout.fragment_mine

    private var sportValueInt = 0

    override fun initView(rootView: View) {
    }

    override fun initLogic() {
        refreshHeaderAndName()
        sportValueInt = if (MMKVUtil.getInt(TARGET_STEP, 0) != 0) {
            MMKVUtil.getInt(TARGET_STEP, 0)
        } else {
            8000
        }
        sportValue.text = sportValueInt.toString()
        versionValue.text = CommonUtils.getPKgVersionName(mActivity)
        bindLayout.setOnClickListener(this)
        topLayout.setOnClickListener(this)
        sportLayout.setOnClickListener(this)
        loginOutLayout.setOnClickListener(this)
    }

    override fun onNoDoubleClick(v: View) {
        when (v) {
            bindLayout -> {
                unBindDialogShow()
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
                    intent.putExtra("inputValue", sportValueInt)
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
        }
    }

    private fun unBindDialogShow() {
        val dialog = object : DialogHolder(mActivity,
                R.layout.dialog_mine_unbind, Gravity.CENTER) {
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
        mActivity.showDialog()
        val accountId = MMKVUtil.getString(BIND_DEVICE_ACCOUNT_ID).toInt()
        ApiUtil.createApi(IGuiderApi::class.java, false)
                .unBindDevice(accountId, "")
                .enqueue(object : ApiCallBack<Any?>(mActivity) {
                    override fun onApiResponse(call: Call<Any?>?,
                                               response: Response<Any?>?) {
                        if (response?.body() != null) {
                            (mActivity as MainActivity)
                                    .unbindDeviceFromMineFragment(
                                            mActivity.resources.getString(R.string.app_own_string))
                            showToast(resources.getString(R.string.app_main_unbind_success))
                            //当前账户必须要有一个设备绑定，所以解绑后要重新到绑定页面
                            MMKVUtil.clearByKey(BIND_DEVICE_ACCOUNT_ID)
                            val intent = Intent(mActivity, AddNewDeviceActivity::class.java)
                            intent.putExtra("type", "mine")
                            startActivity(intent)
                        }
                    }

                    override fun onRequestFinish() {
                        mActivity.dismissDialog()
                    }
                })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                SPORT_STEP_INFO -> {
                    if (StringUtil.isNotBlankAndEmpty(data.getStringExtra("inputResult"))) {
                        sportValueInt = data.getStringExtra("inputResult")!!.toInt()
                        sportValue.text = sportValueInt.toString()
                        MMKVUtil.saveInt(TARGET_STEP, sportValueInt)
                    }
                }
                PERSON_INFO -> {
                    if (data.getBooleanExtra("isChange", false))
                        refreshHeaderAndName()
                }
            }
        }
    }

    private fun refreshHeaderAndName() {
        if (MMKVUtil.containKey(USER.HEADER) &&
                StringUtil.isNotBlankAndEmpty(MMKVUtil.getString(USER.HEADER))) {
            ImageLoaderUtils.loadImage(mActivity, headerIv, MMKVUtil.getString(USER.HEADER))
        }
        if (MMKVUtil.containKey(USER.NAME) &&
                StringUtil.isNotBlankAndEmpty(MMKVUtil.getString(USER.NAME))) {
            nameTv.text = MMKVUtil.getString(USER.NAME)
        }
    }
}