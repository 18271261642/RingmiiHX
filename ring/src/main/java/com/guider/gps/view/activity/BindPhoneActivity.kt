package com.guider.gps.view.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.Gravity
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.guider.baselib.base.BaseActivity
import com.guider.baselib.utils.*
import com.guider.baselib.widget.dialog.DialogHolder
import com.guider.gps.R
import com.guider.gps.adapter.CountryCodeDialogAdapter
import com.guider.health.apilib.ApiCallBack
import com.guider.health.apilib.ApiUtil
import com.guider.health.apilib.IGuiderApi
import com.guider.health.apilib.JsonApi
import com.guider.health.apilib.bean.AreCodeBean
import com.guider.health.apilib.bean.ThirdBindPhoneBean
import com.guider.health.apilib.bean.TokenInfo
import kotlinx.android.synthetic.main.activity_bind_phone.*
import kotlinx.android.synthetic.main.include_phone_edit_layout.*
import retrofit2.Call
import retrofit2.Response

/**
 * @Package: com.guider.gps.view.activity
 * @ClassName: BindPhoneActivity
 * @Description: 绑定手机号页面
 * @Author: hjr
 * @CreateDate: 2020/8/28 14:51
 * Copyright (C), 1998-2020, GuiderTechnology
 */
class BindPhoneActivity : BaseActivity() {

    private var openId = ""
    private var appId = ""
    private var header = ""
    private var name = ""
    private var phoneValue = ""

    override val contentViewResId: Int
        get() = R.layout.activity_bind_phone

    override fun openEventBus(): Boolean {
        return false
    }

    override fun initImmersion() {
        showBackButton(R.drawable.ic_back_white)
        setTitle(mContext!!.resources.getString(R.string.app_bind_phone_number))
        //  intent.putExtra("openId",openId)
        //                                intent.putExtra("appId",appId)
        //                                intent.putExtra("header",header)
        //                                intent.putExtra("name",name)
        if (intent != null) {
            if (StringUtil.isNotBlankAndEmpty(intent.getStringExtra("openId"))) {
                openId = intent.getStringExtra("openId")!!
            }
            if (StringUtil.isNotBlankAndEmpty(intent.getStringExtra("appId"))) {
                appId = intent.getStringExtra("appId")!!
            }
            if (StringUtil.isNotBlankAndEmpty(intent.getStringExtra("header"))) {
                header = intent.getStringExtra("header")!!
            }
            if (StringUtil.isNotBlankAndEmpty(intent.getStringExtra("name"))) {
                name = intent.getStringExtra("name")!!
            }
        }
    }

    override fun initView() {

    }

    override fun initLogic() {
        countryCodeLayout.setOnClickListener(this)
        bindTv.setOnClickListener(this)
    }

    override fun showToolBar(): Boolean {
        return true
    }

    override fun onNoDoubleClick(v: View) {
        when (v) {
            countryCodeLayout -> {
                getCountryCode()
            }
            bindTv -> {
                bindEvent()
            }
        }
    }

    private fun bindEvent() {
        phoneValue = phoneEdit.text.toString()
        if (StringUtil.isEmpty(phoneValue)) {
            toastShort(mContext!!.resources.getString(R.string.app_login_phone_empty))
            return
        }
        val countryCode = countryTv.text.toString().replace("+", "")
        if (!StringUtil.isMobileNumber(countryCode, phoneValue)) {
            toastShort(mContext!!.resources.getString(R.string.app_phone_illegal))
            return
        }
        showDialog()
        val map = hashMapOf<String, Any?>()
        map["appId"] = appId
        map["areaCode"] = countryCode
        map["birthday"] = null
        map["doctorAccountId"] = -1
        map["gender"] = "MAN"
        map["groupId"] = -1
        map["headimgurl"] = if (StringUtil.isNotBlankAndEmpty(header)) header else null
        map["nickname"] = name
        map["openid"] = openId
        map["phone"] = phoneValue
        ApiUtil.createApi(IGuiderApi::class.java, false)
                .lineBindLogin(map)
                ?.enqueue(object : ApiCallBack<ThirdBindPhoneBean>(mContext) {
                    override fun onApiResponseNull(call: Call<ThirdBindPhoneBean>?,
                                                   response: Response<ThirdBindPhoneBean>?) {
                        if (response?.body() != null) {
                            val bean = response.body()!!
                            loginSuccessEvent(bean.TokenInfo)
                        }
                    }

                    override fun onRequestFinish() {
                        dismissDialog()
                    }
                })
    }

    private fun loginSuccessEvent(bean: TokenInfo) {
        MMKVUtil.saveString(USER.TOKEN, bean.token!!)
        MMKVUtil.saveInt(USER.USERID, bean.accountId)
        MMKVUtil.saveString(USER.COUNTRY_CODE, countryTv.text.toString())
        MMKVUtil.saveString(USER.PHONE, phoneValue)
        MMKVUtil.saveString(REFRESH_TOKEN, bean.refreshToken!!)
        MMKVUtil.saveInt(EXPIRED_TIME, bean.expired)
        toastShort("登录成功")
        enterMainPage()
    }

    private fun enterMainPage() {
        intent.putExtra("bind", "success")
        setResult(Activity.RESULT_OK, intent)
        val intent = Intent(mContext, MainActivity::class.java)
        startActivity(intent)
        finish()
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
}