package com.guider.gps.view.activity

import android.app.Activity
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.guider.baselib.base.BaseActivity
import com.guider.baselib.utils.CommonUtils
import com.guider.baselib.utils.RESET_PASSWORD
import com.guider.baselib.utils.StringUtil
import com.guider.baselib.utils.toastShort
import com.guider.gps.R
import com.guider.gps.viewModel.ChangePasswordViewModel
import kotlinx.android.synthetic.main.activity_change_password.*
import kotlinx.android.synthetic.main.include_password_edit.*

/**
 * @Package: com.guider.gps.view.activity
 * @ClassName: ChangePasswordActivity
 * @Description:修改密码
 * @Author: hjr
 * @CreateDate: 2020/11/23 13:04
 * Copyright (C), 1998-2020, GuiderTechnology
 */
class ChangePasswordActivity : BaseActivity() {

    override val contentViewResId: Int
        get() = R.layout.activity_change_password

    private lateinit var viewModel: ChangePasswordViewModel

    override fun initImmersion() {
        setTitle(mContext?.resources?.getString(R.string.app_main_mine_change_password) ?: "修改密碼")
        showBackButton(R.drawable.ic_back_white)
    }

    override fun initView() {
        initViewModel()
        passwordEdit.hint = mContext!!.resources.getString(R.string.app_old_password_hint)
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[ChangePasswordViewModel::class.java]
        viewModel.isConfirmAvailable.observe(this, {
            nextStep.isClickable = it
            if (it) {
                nextStep.setBackgroundResource(R.drawable.rounded_ffe29d_22_bg)
                nextStep.setTextColor(CommonUtils.getColor(mContext!!, R.color.color8F5725))
            } else {
                nextStep.setBackgroundResource(R.drawable.rounded_ddddd_22_bg)
                nextStep.setTextColor(CommonUtils.getColor(mContext!!, R.color.white))
            }
        })
    }

    override fun initLogic() {
        passwordShowIv.setOnClickListener(this)
        passwordEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s?.length != 0) {
                    passwordShowIv.visibility = View.VISIBLE
                } else passwordShowIv.visibility = View.GONE
                viewModel.setConfirmAvailable(checkPasswordAvailable())
            }

        })
        nextStep.setOnClickListener(this)
    }

    override fun onNoDoubleClick(v: View) {
        when (v) {
            nextStep -> {
                if (!checkPasswordAvailable(true)) return
                if (passwordEdit.text.toString() == "123456") {
                    toastShort("验证成功")
                    val intent = Intent(mContext, ResetPasswordActivity::class.java)
                    intent.putExtra("title",
                            mContext!!.resources.getString(R.string.app_main_mine_change_password))
                    startActivityForResult(intent, RESET_PASSWORD)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == RESET_PASSWORD) {
                finish()
            }
        }
    }

    private fun checkPasswordAvailable(isShowError: Boolean = false): Boolean {
        if (StringUtil.isEmpty(passwordEdit.text.toString())) {
            if (isShowError) toastShort(
                    mContext!!.resources.getString(R.string.app_login_password_empty))
            return false
        }
        if (passwordEdit.text.toString().length < 6) {
            if (isShowError) toastShort(
                    mContext!!.resources.getString(R.string.app_password_format_error))
            return false
        }
        return true
    }

    override fun showToolBar(): Boolean {
        return true
    }
}