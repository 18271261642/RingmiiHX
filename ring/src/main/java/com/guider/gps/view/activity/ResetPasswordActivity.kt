package com.guider.gps.view.activity

import android.app.Activity
import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProvider
import com.guider.baselib.base.BaseActivity
import com.guider.baselib.utils.CommonUtils
import com.guider.baselib.utils.StringUtil
import com.guider.baselib.utils.USER
import com.guider.baselib.utils.toastShort
import com.guider.gps.R
import com.guider.gps.viewModel.ResetPasswordViewModel
import com.guider.health.apilib.utils.MMKVUtil
import kotlinx.android.synthetic.main.activity_register.againPasswordEdit
import kotlinx.android.synthetic.main.activity_register.againPasswordShowIv
import kotlinx.android.synthetic.main.activity_reset_password.*
import kotlinx.android.synthetic.main.include_password_edit.*

/**
 * @Package: com.guider.gps.view.activity
 * @ClassName: ResetPasswordActivity
 * @Description: 重置密码
 * @Author: hjr
 * @CreateDate: 2020/11/23 12:06
 * Copyright (C), 1998-2020, GuiderTechnology
 */
class ResetPasswordActivity : BaseActivity() {

    private var phoneValue = ""

    //国家区号
    private var countryCodeValue = ""
    private var pwd = ""
    private lateinit var viewModel: ResetPasswordViewModel

    override val contentViewResId: Int
        get() = R.layout.activity_reset_password

    override fun initImmersion() {
        showBackButton(R.drawable.ic_back_white)
        if (intent != null) {
            if (StringUtil.isNotBlankAndEmpty(intent.getStringExtra("phone"))) {
                phoneValue = intent.getStringExtra("phone")!!
            }
            if (StringUtil.isNotBlankAndEmpty(intent.getStringExtra("country"))) {
                countryCodeValue = intent.getStringExtra("country")!!
            }
            if (StringUtil.isNotBlankAndEmpty(intent.getStringExtra("pwd"))) {
                pwd = intent.getStringExtra("pwd")!!
            }
            if (StringUtil.isNotBlankAndEmpty(intent.getStringExtra("title"))) {
                setTitle(intent.getStringExtra("title")!!)
            } else {
                setTitle(mContext?.resources?.getString(R.string.app_forgot_password) ?: "忘記密碼")
            }
        }
    }

    override fun initView() {
        initViewModel()
        //修改密码提示语变更
        passwordEdit.hint = mContext!!.resources.getString(
                R.string.app_please_entry_new_password)
        againPasswordEdit.hint = mContext!!.resources.getString(
                R.string.app_please_entry_new_password_again)
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[ResetPasswordViewModel::class.java]
        viewModel.isConfirmAvailable.observe(this, {
            confirmTv.isClickable = it
            if (it) {
                confirmTv.setBackgroundResource(R.drawable.rounded_ffe29d_22_bg)
                confirmTv.setTextColor(CommonUtils.getColor(mContext!!, R.color.color8F5725))
            } else {
                confirmTv.setBackgroundResource(R.drawable.rounded_ddddd_22_bg)
                confirmTv.setTextColor(CommonUtils.getColor(mContext!!, R.color.white))
            }
        })
        viewModel.loading.observe(this, {
            if (it) showDialog()
            else dismissDialog()
        })
        viewModel.nextDataResult.observe(this, {
            if (it) {
                //修改密码直接返回，忘记密码的话提示并延时返回
                if (StringUtil.isNotBlankAndEmpty(pwd)) {
                    toastShort(mContext!!.resources.getString(
                            R.string.app_person_info_change_success))
                } else {
                    toastShort(mContext!!.resources.getString(R.string.app_forgot_password_success))
                }
                confirmTv.postDelayed({
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }, 1500)
            }
        })
    }

    override fun initLogic() {
        passwordShowIv.setOnClickListener(this)
        againPasswordShowIv.setOnClickListener(this)
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
        againPasswordEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s?.length != 0) {
                    againPasswordShowIv.visibility = View.VISIBLE
                } else againPasswordShowIv.visibility = View.GONE
                viewModel.setConfirmAvailable(checkPasswordAvailable())
            }

        })
        confirmTv.setOnClickListener(this)
    }

    override fun onNoDoubleClick(v: View) {
        when (v) {
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
            againPasswordShowIv -> {
                if (againPasswordShowIv.isSelected) {
                    againPasswordShowIv.isSelected = false
                    againPasswordShowIv.setImageResource(R.drawable.icon_password_show_close)
                    againPasswordEdit.inputType =
                            InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
                    againPasswordEdit.setSelection(passwordEdit.text.length)
                } else {
                    againPasswordShowIv.isSelected = true
                    againPasswordShowIv.setImageResource(R.drawable.icon_password_show_open)
                    againPasswordEdit.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    againPasswordEdit.setSelection(passwordEdit.text.length)
                }
            }
            confirmTv -> {
                if (!checkPasswordAvailable(true)) return
                hideKeyboard(v)
                if (StringUtil.isNotBlankAndEmpty(pwd)) {
                    if (MMKVUtil.getInt(USER.USERID) == 0) return
                    //修改密码
                    viewModel.changePassword(
                            pwd,
                            againPasswordEdit.text.toString(),
                            passwordEdit.text.toString(),
                            MMKVUtil.getInt(USER.USERID))
                } else {
                    //忘记密码
                    viewModel.forgotPassword(countryCodeValue, phoneValue,
                            passwordEdit.text.toString())
                }
            }
        }
    }

    private fun hideKeyboard(view: View) {
        try {
            if (passwordEdit.isFocused) passwordEdit.clearFocus()
            if (againPasswordEdit.isFocused) againPasswordEdit.clearFocus()
            val imm: InputMethodManager = view.context
                    .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkPasswordAvailable(isShowError: Boolean = false): Boolean {
        if (StringUtil.isEmpty(passwordEdit.text.toString())) {
            if (isShowError) toastShort(
                    mContext!!.resources.getString(R.string.app_login_password_empty))
            return false
        }
        if (StringUtil.isEmpty(againPasswordEdit.text.toString())) {
            if (isShowError) toastShort(
                    mContext!!.resources.getString(R.string.app_login_password_empty))
            return false
        }
        if (passwordEdit.text.toString().length < 6) {
            if (isShowError) toastShort(
                    mContext!!.resources.getString(R.string.app_password_format_error))
            return false
        }
        if (againPasswordEdit.text.toString().length < 6) {
            if (isShowError) toastShort(
                    mContext!!.resources.getString(R.string.app_password_format_error))
            return false
        }
        if (againPasswordEdit.text.toString() != passwordEdit.text.toString()) {
            if (isShowError) toastShort(
                    mContext!!.resources.getString(
                            R.string.app_register_password_different))
            return false
        }
        return true
    }

    override fun showToolBar(): Boolean {
        return true
    }
}