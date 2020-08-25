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
import com.guider.baselib.widget.dialog.DialogHolder
import com.guider.feifeia3.utils.MMKVUtil
import com.guider.gps.R
import com.guider.gps.view.activity.MainActivity
import com.guider.gps.view.activity.PersonInfoActivity
import com.guider.gps.view.activity.SingleLineEditActivity
import kotlinx.android.synthetic.main.fragment_mine.*

class MineFragment : BaseFragment() {

    override val layoutRes: Int
        get() = R.layout.fragment_mine

    private var sportValueInt = 0


    override fun initView(rootView: View) {

    }

    override fun initLogic() {
        versionValue.text = CommonUtils.getPKgVersionName(mActivity)
        bindLayout.setOnClickListener(this)
        topLayout.setOnClickListener(this)
        sportLayout.setOnClickListener(this)
    }

    override fun onNoDoubleClick(v: View) {
        when (v) {
            bindLayout -> {
                if (StringUtils.isNotBlankAndEmpty(MMKVUtil.getString(CURRENT_DEVICE_NAME))) {
                    unBindDialogShow(MMKVUtil.getString(CURRENT_DEVICE_NAME))
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
                startActivityForResult(intent, PERSON_INFO)
            }
        }
    }

    private fun unBindDialogShow(name: String) {
        val dialog = object : DialogHolder(mActivity,
                R.layout.dialog_mine_unbind, Gravity.CENTER) {
            @SuppressLint("SetTextI18n")
            override fun bindView(dialogView: View) {
                val unBindContentTv = dialogView.findViewById<TextView>(R.id.unBindContentTv)
                val unbindValue = String.format(
                        resources.getString(R.string.app_main_unbind_device), name)
                unBindContentTv.text = unbindValue
                val cancel = dialogView.findViewById<ConstraintLayout>(R.id.cancelLayout)
                cancel.setOnClickListener {
                    dialog?.dismiss()
                }
                val confirm = dialogView.findViewById<ConstraintLayout>(R.id.confirmLayout)
                confirm.setOnClickListener {
                    dialog?.dismiss()
                    (mActivity as MainActivity).unbindDeviceFromMineFragment(name)
                    showToast(resources.getString(R.string.app_main_unbind_success))
                }
            }
        }
        dialog.initView()
        dialog.show(true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                SINGLE_LINE_EDIT -> {
                    if (StringUtils.isNotBlankAndEmpty(data.getStringExtra("inputResult"))) {
                        sportValueInt = data.getStringExtra("inputResult")!!.toInt()
                        MMKVUtil.saveInt(TARGET_STEP, sportValueInt)
                    }
                }
            }
        }
    }
}