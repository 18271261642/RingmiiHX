package com.guider.gps.view.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.guider.baselib.base.BaseActivity
import com.guider.baselib.utils.*
import com.guider.baselib.widget.dialog.DialogHolder
import com.guider.feifeia3.utils.ToastUtil
import com.guider.gps.R
import com.guider.gps.adapter.CountryCodeDialogAdapter
import com.guider.health.apilib.ApiCallBack
import com.guider.health.apilib.ApiUtil
import com.guider.health.apilib.GuiderApiUtil
import com.guider.health.apilib.JsonApi
import com.guider.health.apilib.bean.AreCodeBean
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.include_password_edit.*
import kotlinx.android.synthetic.main.include_phone_edit_layout.*
import kotlinx.coroutines.launch
import me.jessyan.autosize.internal.CustomAdapt
import retrofit2.Call
import retrofit2.Response

class RegisterActivity : BaseActivity(), CustomAdapt {

    private var phoneValue = ""

    //国家区号
    private var countryCodeValue = ""

    //国家英文代号
    private var countryCodeKey = "TW"

    //判断是否是从注册页进入还是从绑定设备添加新账号
    private var pageEnterType = ""

    override val contentViewResId: Int
        get() = R.layout.activity_register

    override fun openEventBus(): Boolean {
        return false
    }

    override fun initImmersion() {
        showBackButton(R.drawable.ic_back_white)
        setTitle(mContext!!.resources.getString(R.string.app_register))
        setStatusBarBackground(R.drawable.bg_register_top_bar)
        if (intent != null) {
            if (StringUtil.isNotBlankAndEmpty(intent.getStringExtra("phone"))) {
                phoneValue = intent.getStringExtra("phone")!!
            }
            if (StringUtil.isNotBlankAndEmpty(intent.getStringExtra("country"))) {
                countryCodeValue = intent.getStringExtra("country")!!
            }
            if (StringUtil.isNotBlankAndEmpty(intent.getStringExtra("pageEnterType"))) {
                pageEnterType = intent.getStringExtra("pageEnterType")!!
            }
            if (StringUtil.isNotBlankAndEmpty(intent.getStringExtra("countryCode"))) {
                countryCodeKey = intent.getStringExtra("countryCode")!!
            }
        }
    }

    override fun initView() {
        if (StringUtil.isEmpty(pageEnterType)) {
            if (StringUtil.isNotBlankAndEmpty(phoneValue)) {
                phoneEdit.setText(phoneValue)
            }
            if (StringUtil.isNotBlankAndEmpty(countryCodeValue)) {
                countryTv.text = countryCodeValue
            }
            phoneEdit.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    if (StringUtil.isNotBlankAndEmpty(phoneEdit.text.toString())) {
                        phoneValue = phoneEdit.text.toString()
                        val tag =
                                if (countryTv.tag is String) {
                                    countryTv.tag as String
                                } else "TW"
                        val countryCode = countryTv.text.toString().replace(
                                "+", "")
                        if (!StringUtil.isMobileNumber(phoneValue, tag)) {
                            formatError()
                        } else {
                            //符合要求的手机号去判断是否是已注册的手机号
                            checkPhone(countryCode, phoneEdit.text.toString(), false,tag)
                        }
                    }
                }
            }
        } else {
            editLayout.visibility = View.GONE
        }
        countryTv.tag = countryCodeKey
    }

    private fun formatError() {
        ToastUtil.showCenter(mContext!!,
                mContext!!.resources.getString(R.string.app_incorrect_format))
        policyTextTv.visibility = View.VISIBLE
        policyTextTv.text = mContext!!.resources.getString(
                R.string.app_incorrect_format)
        policyTextTv.postDelayed({
            policyTextTv.visibility = View.GONE
        }, 1000)
    }

    private fun checkPhone(countryCode: String, phone: String, isExit: Boolean, areaCode: String) {
        //如果符合要求弹出提示
        lifecycleScope.launch {
            ApiCoroutinesCallBack.resultParse(mContext!!, block = {
                val resultBean = GuiderApiUtil.getApiService()
                        .checkPhoneIsRegister(countryCode, phone)
                //true可以用 false不可用
                if (resultBean == "false") {
                    ToastUtil.showCenter(mContext!!,
                            mContext!!.resources.getString(R.string.app_phone_register))
                    policyTextTv.visibility = View.VISIBLE
                    policyTextTv.text = mContext!!.resources.getString(R.string.app_phone_register)
                    policyTextTv.postDelayed({
                        policyTextTv.visibility = View.GONE
                    }, 1000)
                } else {
                    if (isExit) {
                        val intent = Intent(mContext!!, CompleteInfoActivity::class.java)
                        val passwordValue = MyUtils.md5(passwordEdit.text.toString())
                        intent.putExtra("passwd", passwordValue)
                        intent.putExtra("phone", phoneValue)
                        intent.putExtra("telAreaCode", countryCode)
                        intent.putExtra("areaCode", areaCode)
                        intent.putExtra("pageEnterType", pageEnterType)
                        startActivityForResult(intent, COMPLETE_INFO)
                    }
                }
            })
        }
    }

    override fun getSizeInDp(): Float {
        return 640f
    }

    override fun isBaseOnWidth(): Boolean {
        return false
    }

    override fun initLogic() {
        registerTv.setOnClickListener(this)
        countryCodeLayout.setOnClickListener(this)
        passwordShowIv.setOnClickListener(this)
        againPasswordShowIv.setOnClickListener(this)
        passwordEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s?.length != 0) {
                    passwordShowIv.visibility = View.VISIBLE
                } else passwordShowIv.visibility = View.GONE
            }

        })
        againPasswordEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s?.length != 0) {
                    againPasswordShowIv.visibility = View.VISIBLE
                } else againPasswordShowIv.visibility = View.GONE
            }

        })
    }

    override fun onNoDoubleClick(v: View) {
        when (v) {
            registerTv -> {
                if (StringUtil.isEmpty(pageEnterType)) {
                    phoneValue = phoneEdit.text.toString()
                }
                val countryCode = if (StringUtil.isEmpty(pageEnterType)) {
                    countryTv.text.toString().replace("+", "")
                } else countryCodeValue.replace("+", "")
                if (StringUtil.isEmpty(phoneValue)) {
                    toastShort(mContext!!.resources.getString(R.string.app_login_phone_empty))
                    return
                }
                val tag =
                        if (countryTv.tag is String) {
                            countryTv.tag as String
                        } else "TW"
                if (!StringUtil.isMobileNumber(phoneValue,tag)) {
                    toastShort(mContext!!.resources.getString(R.string.app_phone_illegal))
                    return
                }
                if (StringUtil.isEmpty(passwordEdit.text.toString())) {
                    toastShort(mContext!!.resources.getString(R.string.app_login_password_empty))
                    return
                }
                if (StringUtil.isEmpty(againPasswordEdit.text.toString())) {
                    toastShort(mContext!!.resources.getString(R.string.app_login_password_empty))
                    return
                }
                if (passwordEdit.text.toString().length<6){
                    toastShort(mContext!!.resources.getString(R.string.app_password_format_error))
                    return
                }
                if (againPasswordEdit.text.toString().length<6){
                    toastShort(mContext!!.resources.getString(R.string.app_password_format_error))
                    return
                }
                if (againPasswordEdit.text.toString() != passwordEdit.text.toString()) {
                    toastShort(mContext!!.resources.getString(
                            R.string.app_register_password_different))
                    return
                }
                //如果是绑定其他设备的新注册用户由于不能输入手机号也就不需要再去判断是否注册过
                if (StringUtil.isEmpty(pageEnterType))
                    checkPhone(countryCode, phoneValue, true, tag)
                else {
                    val intent = Intent(mContext!!, CompleteInfoActivity::class.java)
                    val passwordValue = MyUtils.md5(passwordEdit.text.toString())
                    intent.putExtra("passwd", passwordValue)
                    intent.putExtra("phone", phoneValue)
                    intent.putExtra("telAreaCode", countryCode)
                    intent.putExtra("areaCode", tag)
                    intent.putExtra("pageEnterType", pageEnterType)
                    startActivityForResult(intent, COMPLETE_INFO)
                }
            }
            //获取国家区号
            countryCodeLayout -> {
                getCountryCode()
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
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == COMPLETE_INFO) {
                intent.putExtra("phone", phoneEdit.text.toString())
                setResult(Activity.RESULT_OK, intent)
                finish()
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
        val dialog = object : DialogHolder(this,
                R.layout.dialog_country_code, Gravity.CENTER) {
            override fun bindView(dialogView: View) {
                val adapter = CountryCodeDialogAdapter(mContext!!, newList)
                val countryRv = dialogView.findViewById<RecyclerView>(R.id.countryRv)
                adapter.setListener(object : AdapterOnItemClickListener {
                    @SuppressLint("SetTextI18n")
                    override fun onClickItem(position: Int) {
                        val code = newList[position].phoneCode.toString()
                        countryTv.text = "+$code"
                        countryTv.tag = newList[position].phoneAreCode
                        dialog?.dismiss()
                    }

                })
                countryRv.layoutManager = LinearLayoutManager(mContext)
                countryRv.adapter = adapter
            }
        }
        dialog.initView()
        dialog.show(true)
    }

    override fun showToolBar(): Boolean {
        return true
    }
}