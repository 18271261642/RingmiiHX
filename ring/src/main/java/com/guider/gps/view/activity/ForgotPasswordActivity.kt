package com.guider.gps.view.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.guider.baselib.base.BaseActivity
import com.guider.baselib.utils.*
import com.guider.baselib.widget.dialog.DialogHolder
import com.guider.gps.R
import com.guider.gps.adapter.CountryCodeDialogAdapter
import com.guider.gps.viewModel.ForgotPasswordViewModel
import com.guider.health.apilib.ApiCallBack
import com.guider.health.apilib.ApiUtil
import com.guider.health.apilib.JsonApi
import com.guider.health.apilib.bean.AreCodeBean
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.include_password_edit.*
import kotlinx.android.synthetic.main.include_phone_edit_layout.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import java.util.*

/**
 * @Package: com.guider.gps.view.activity
 * @ClassName: ForgotPasswordActivity
 * @Description: 忘记密码
 * @Author: hjr
 * @CreateDate: 2020/11/23 10:40
 * Copyright (C), 1998-2020, GuiderTechnology
 */
class ForgotPasswordActivity : BaseActivity() {

    private lateinit var viewModel: ForgotPasswordViewModel
    private var codeDialog: DialogHolder? = null
    private var phoneValue = ""

    //国家区号
    private var countryCodeValue = ""

    //国家英文代号
    private var countryCodeKey = DEFAULT_COUNTRY_CODE
    private var sendCodeStatus = "send"
    private var seconds = 60

    override val contentViewResId: Int
        get() = R.layout.activity_forgot_password

    override fun initImmersion() {
        setTitle(mContext?.resources?.getString(R.string.app_forgot_password) ?: "忘記密碼")
        showBackButton(R.drawable.ic_back_white)
        if (intent != null) {
            if (StringUtil.isNotBlankAndEmpty(intent.getStringExtra("phone"))) {
                phoneValue = intent.getStringExtra("phone")!!
            }
            if (StringUtil.isNotBlankAndEmpty(intent.getStringExtra("country"))) {
                countryCodeValue = intent.getStringExtra("country")!!
            }
            if (StringUtil.isNotBlankAndEmpty(intent.getStringExtra("countryCode"))) {
                countryCodeKey = intent.getStringExtra("countryCode")!!
            }
        }
    }

    override fun initView() {
        initViewModel()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[ForgotPasswordViewModel::class.java]
        viewModel.isNextStepAvailable.observe(this, {
            nextStep.isClickable = it
            if (it) {
                nextStep.setTextColor(CommonUtils.getColor(mContext!!, R.color.color8F5725))
                nextStep.setBackgroundResource(R.drawable.rounded_ffe29d_22_bg)
            } else {
                nextStep.setTextColor(CommonUtils.getColor(mContext!!, R.color.white))
                nextStep.setBackgroundResource(R.drawable.rounded_ddddd_22_bg)
            }
        })
        viewModel.isSendCodeAvailable.observe(this, {
            sendCodeTv.isClickable = it
            if (it) {
                sendCodeTv.setTextColor(CommonUtils.getColor(mContext!!, R.color.white))
                sendCodeTv.setBackgroundResource(R.drawable.rounded_f28a38_14_bg)
            } else {
                sendCodeTv.setTextColor(CommonUtils.getColor(mContext!!, R.color.white))
                sendCodeTv.setBackgroundResource(R.drawable.rounded_ddddd_22_bg)
            }
        })
        viewModel.loading.observe(this, {
            if (it) showDialog()
            else dismissDialog()
        })
        viewModel.sendCodeResult.observe(this, {
            if (it) {
                toastShort(mContext!!.resources.getString(R.string.app_main_send_success))
            } else {
                toastShort(mContext!!.resources.getString(R.string.app_main_send_fail))
            }
        })
        viewModel.nextDataResult.observe(this, {
            if (it) {
                val intent = Intent(mContext, ResetPasswordActivity::class.java)
                intent.putExtra("phone", phoneEdit.text.toString())
                intent.putExtra("country",
                        countryTv.text.toString().replace("+", ""))
                intent.putExtra("title",
                        mContext!!.resources.getString(R.string.app_forgot_password))
                seconds = 0
                startActivityForResult(intent, RESET_PASSWORD)
            }
        })
    }

    @SuppressLint("SetTextI18n")
    override fun initLogic() {
        if (StringUtil.isNotBlankAndEmpty(phoneValue)) {
            phoneEdit.setText(phoneValue)
        }
        if (StringUtil.isNotBlankAndEmpty(countryCodeValue)) {
            countryTv.text = countryCodeValue
        }
        countryTv.tag = countryCodeKey
        val tag =
                if (countryTv.tag is String) {
                    countryTv.tag as String
                } else DEFAULT_COUNTRY_CODE
        if (StringUtil.isNotBlankAndEmpty(phoneEdit.text.toString())
                && StringUtil.isMobileNumber(phoneEdit.text.toString(), tag)) {
            viewModel.setSendCodeAvailable(true)
        } else {
            viewModel.setSendCodeAvailable(false)
        }
        phoneEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                phoneEditEvent(s)
            }

        })
        lineCodeEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                lineCodeEditEvent(s)
            }

        })
        countryCodeLayout.setOnClickListener(this)
        sendCodeTv.setOnClickListener(this)
        nextStep.setOnClickListener(this)
    }

    private fun lineCodeEditEvent(s: Editable?) {
        val tag =
                if (countryTv.tag is String) {
                    countryTv.tag as String
                } else DEFAULT_COUNTRY_CODE
        if (s != null && StringUtil.isNotBlankAndEmpty(s.toString())
                && StringUtil.isNotBlankAndEmpty(phoneEdit.text.toString())
                && StringUtil.isMobileNumber(phoneEdit.text.toString(), tag)
        ) {
            viewModel.setNextStepAvailable(true)
        } else {
            viewModel.setNextStepAvailable(false)
        }
    }

    private fun phoneEditEvent(s: Editable?) {
        if (s != null && StringUtil.isNotBlankAndEmpty(s.toString())) {
            val tag =
                    if (countryTv.tag is String) {
                        countryTv.tag as String
                    } else DEFAULT_COUNTRY_CODE
            if (StringUtil.isMobileNumber(s.toString(), tag)) {
                viewModel.setSendCodeAvailable(true)
                if (StringUtil.isNotBlankAndEmpty(lineCodeEdit.text.toString())) {
                    viewModel.setNextStepAvailable(true)
                } else viewModel.setNextStepAvailable(false)
            } else {
                viewModel.setSendCodeAvailable(false)
                viewModel.setNextStepAvailable(false)
            }
        } else {
            viewModel.setSendCodeAvailable(false)
            viewModel.setNextStepAvailable(false)
        }
    }

    override fun onNoDoubleClick(v: View) {
        when (v) {
            //获取国家区号
            countryCodeLayout -> {
                getCountryCode()
            }
            sendCodeTv -> {
                sendCodeEvent()
            }
            nextStep -> {
                phoneValue = phoneEdit.text.toString()
                if (StringUtil.isEmpty(phoneValue)) {
                    toastShort(mContext!!.resources.getString(R.string.app_login_phone_empty))
                    return
                }
                val tag =
                        if (countryTv.tag is String) {
                            countryTv.tag as String
                        } else DEFAULT_COUNTRY_CODE
                if (!StringUtil.isMobileNumber(phoneValue, tag)) {
                    toastShort(mContext!!.resources.getString(R.string.app_phone_illegal))
                    return
                }
                if (StringUtil.isEmpty(lineCodeEdit.text.toString())) {
                    toastShort(mContext!!.resources.getString(R.string.app_verification_code_empty))
                    return
                }
                hideKeyboard(v)
                viewModel.entryNext(
                        countryTv.text.toString().replace("+", ""),
                        phoneValue, lineCodeEdit.text.toString()
                )
            }
        }
    }

    private fun hideKeyboard(view: View) {
        try {
            if (phoneEdit.isFocused) phoneEdit.clearFocus()
            if (lineCodeEdit.isFocused) lineCodeEdit.clearFocus()
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

    private fun sendCodeEvent() {
        phoneValue = phoneEdit.text.toString()
        val tag =
                if (countryTv.tag is String) {
                    countryTv.tag as String
                } else DEFAULT_COUNTRY_CODE
        if (!StringUtil.isMobileNumber(phoneValue, tag)) {
            toastShort(mContext!!.resources.getString(R.string.app_phone_illegal))
            return
        }
        if (sendCodeStatus == "send") {
            viewModel.sendCode(phoneValue)
            seconds = 60
            sendCodeStatus = "receive"
            sendCodeTv.setTextColor(CommonUtils.getColor(mContext!!, R.color.colorF28A38))
            sendCodeTv.setBackgroundResource(R.drawable.circle_f28a38_14_bg)
            sendCodeTv.isClickable = false
            lifecycleScope.launch {
                while (true) {
                    if (seconds > 0) {
                        val codeTvShow = String.format(
                                resources.getString(R.string.app_retry_send),
                                seconds)
                        seconds--
                        sendCodeTv.text = codeTvShow
                        delay(1_000)
                    } else {
                        sendCodeStatus = "send"
                        sendCodeTv.isClickable = true
                        sendCodeTv.setTextColor(CommonUtils.getColor(mContext!!, R.color.white))
                        sendCodeTv.setBackgroundResource(R.drawable.rounded_f28a38_14_bg)
                        sendCodeTv.text = mContext!!.resources.getString(
                                R.string.app_send_verify_code)
                        return@launch
                    }
                }
            }
        }
    }

    private fun getCountryCode() {
        showDialog()
        ApiUtil.createRingApi(JsonApi::class.java)
                .countryCode
                .enqueue(object : ApiCallBack<List<AreCodeBean>>(mContext) {
                    override fun onApiResponse(call: Call<List<AreCodeBean>>?,
                                               response: Response<List<AreCodeBean>>?) {
                        if (response != null) {
                            if (response.body() != null) {
                                getCountryCodeDialogShow(response.body())
                            }
                        }
                    }

                    override fun onRequestFinish() {
                        dismissDialog()
                    }
                })
    }

    private fun getCountryCodeDialogShow(areaList: List<AreCodeBean>?) {
        if (areaList.isNullOrEmpty()) {
            return
        }
        val newList = areaList as ArrayList
        codeDialog = object : DialogHolder(this,
                R.layout.dialog_country_code, Gravity.CENTER) {
            override fun bindView(dialogView: View) {
                val adapter = CountryCodeDialogAdapter(mContext!!, newList)
                val countryRv = dialogView.findViewById<RecyclerView>(R.id.countryRv)
                adapter.setListener(object : AdapterOnItemClickListener {
                    @SuppressLint("SetTextI18n")
                    override fun onClickItem(position: Int) {
                        val code = newList[position].phoneCode.toString()
                        countryTv.tag = newList[position].phoneAreCode
                        countryTv.text = "+$code"
                        dialog?.dismiss()
                        codeDialog = null
                    }

                })
                countryRv.layoutManager = LinearLayoutManager(mContext)
                countryRv.adapter = adapter
            }
        }
        codeDialog?.initView()
        codeDialog?.show(true)
    }

    override fun onDestroy() {
        if (codeDialog != null) {
            codeDialog?.closeDialog()
            codeDialog = null
        }
        seconds = 0
        super.onDestroy()
    }

    override fun showToolBar(): Boolean {
        return true
    }
}