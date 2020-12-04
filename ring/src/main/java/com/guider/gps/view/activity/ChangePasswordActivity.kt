package com.guider.gps.view.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProvider
import com.guider.baselib.base.BaseActivity
import com.guider.baselib.utils.*
import com.guider.gps.R
import com.guider.gps.viewModel.ChangePasswordViewModel
import com.guider.health.apilib.utils.MMKVUtil
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
        viewModel.loading.observe(this, {
            if (it) showDialog()
            else dismissDialog()
        })
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
        viewModel.nextDataResult.observe(this, {
            if (it) {
                val intent = Intent(mContext, ResetPasswordActivity::class.java)
                intent.putExtra("title",
                        mContext!!.resources.getString(R.string.app_main_mine_change_password))
                intent.putExtra("pwd", passwordEdit.text.toString())
                startActivityForResult(intent, RESET_PASSWORD)
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
                hideKeyboard(v)
                viewModel.entryNext(passwordEdit.text.toString(),
                        MMKVUtil.getInt(USER.USERID))
            }
            passwordShowIv -> {
                if (passwordShowIv.isSelected) {
                    passwordShowIv.isSelected = false
                    passwordShowIv.setImageResource(R.drawable.icon_password_show_close)
                    passwordEdit.inputType =
                            InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
                    passwordEdit.setSelection(passwordEdit.text.length)
                } else {
                    passwordShowIv.isSelected = true
                    passwordShowIv.setImageResource(R.drawable.icon_password_show_open)
                    passwordEdit.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    passwordEdit.setSelection(passwordEdit.text.length)
                }
            }
        }
    }

    private fun hideKeyboard(view: View) {
        try {
            if (passwordEdit.isFocused) passwordEdit.clearFocus()
            val imm: InputMethodManager = view.context
                    .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        } catch (e: Exception) {
            e.printStackTrace()
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