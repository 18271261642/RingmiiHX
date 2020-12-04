package com.guider.gps.view.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.text.Editable
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
import com.guider.health.apilib.bean.CheckBindDeviceBean
import com.guider.health.apilib.bean.UserInfo
import kotlinx.android.synthetic.main.activity_device_bind_add_member.*
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import retrofit2.Call
import retrofit2.Response
import java.util.*

/**
 * @Package: com.guider.gps.view.activity
 * @ClassName: DeviceBindAddMemberActivity
 * @Description: 设备绑定添加成员页面
 * @Author: hjr
 * @CreateDate: 2020/9/4 16:54
 * Copyright (C), 1998-2020, GuiderTechnology
 */
class DeviceBindAddMemberActivity : BaseActivity() {

    private var userGroupId = ""
    private var code = ""
    private var areaCode = DEFAULT_COUNTRY_CODE
    private var accountId = ""

    override val contentViewResId: Int
        get() = R.layout.activity_device_bind_add_member

    override fun openEventBus(): Boolean {
        return true
    }

    override fun initImmersion() {
        showBackButton(R.drawable.ic_back_white)
        setTitle(mContext!!.resources.getString(R.string.app_device_bind))
        if (intent != null) {
            if (StringUtil.isNotBlankAndEmpty(intent.getStringExtra("codeValue"))) {
                code = intent.getStringExtra("codeValue")!!
            }
            if (StringUtil.isNotBlankAndEmpty(intent.getStringExtra("areaCode"))) {
                areaCode = intent.getStringExtra("areaCode")!!
            }
            if (StringUtil.isNotBlankAndEmpty(intent.getStringExtra("userGroupId"))) {
                userGroupId = intent.getStringExtra("userGroupId")!!
            }
            if (StringUtil.isNotBlankAndEmpty(intent.getStringExtra("accountId"))) {
                accountId = intent.getStringExtra("accountId")!!
            }
        }
    }

    override fun initView() {
        countryTv.tag = areaCode
        if (StringUtil.isNotBlankAndEmpty(accountId)) {
            bindPhoneLayout.visibility = View.GONE
        } else {
            phoneEdit.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    if (StringUtil.isNotBlankAndEmpty(phoneEdit.text.toString())) {
                        val phoneValue = phoneEdit.text.toString()
                        val tag =
                                if (countryTv.tag is String) {
                                    countryTv.tag as String
                                } else DEFAULT_COUNTRY_CODE
                        if (!StringUtil.isMobileNumber(phoneValue, tag)) {
                            ToastUtil.showCenter(mContext!!,
                                    mContext!!.resources.getString(R.string.app_incorrect_format))
                        }
                    }
                }
            }
        }
    }

    override fun initLogic() {
        countryCodeLayout.setOnClickListener(this)
        phoneEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.length != 0) {
                    phoneDelete.visibility = View.VISIBLE
                } else phoneDelete.visibility = View.GONE
            }

        })
        phoneDelete.setOnClickListener(this)
        deviceNameEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.length != 0) {
                    deviceNameDelete.visibility = View.VISIBLE
                } else deviceNameDelete.visibility = View.GONE
            }

        })
        deviceNameDelete.setOnClickListener(this)
        enterTv.setOnClickListener(this)
    }

    override fun onNoDoubleClick(v: View) {
        when (v) {
            countryCodeLayout -> {
                getCountryCode()
            }
            phoneDelete -> {
                phoneEdit.setText("")
            }
            deviceNameDelete -> {
                deviceNameEdit.setText("")
            }
            enterTv -> {
                if (StringUtil.isEmpty(deviceNameEdit.text.toString())) {
                    toastShort(mContext!!.resources.getString(R.string.app_device_nickName_empty))
                    return
                }

                if (StringUtil.isEmpty(accountId)) {
                    val phoneValue = phoneEdit.text.toString()
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
                    bindNewMemberCheck(phoneValue, deviceNameEdit.text.toString())
                } else {
                    addMemberToGroup()
                }
            }
        }
    }

    private fun bindNewMemberCheck(phoneValue: String, deviceName: String) {
        val groupId = userGroupId.substring(0, userGroupId.lastIndexOf(".")).toInt()
        lifecycleScope.launch {
            ApiCoroutinesCallBack.resultParse(mContext!!, onStart = {
                showDialog()
            }, block = {
                val resultBean = GuiderApiUtil.getApiService()
                        .devicePhoneVerify(
                                groupId, code, phoneValue, deviceName, countryTv.text.toString())
                if (resultBean is String && resultBean == "null") {
                    //返回null手机号未注册
                    toastShort("手机号未注册")
                    val intent = Intent(mContext, RegisterActivity::class.java)
                    intent.putExtra("pageEnterType", "bindAddMember")
                    intent.putExtra("country", countryTv.text.toString())
                    intent.putExtra("phone", phoneEdit.text.toString())
                    startActivity(intent)
                } else {
                    //成功返回家人列表
                    val list = ParseJsonData.parseJsonDataList<UserInfo>(resultBean,
                            UserInfo::class.java)
                    if (!list.isNullOrEmpty()) {
                        val bean = CheckBindDeviceBean()
                        bean.userGroupId = userGroupId.toInt()
                        bean.userInfos = list
                        EventBusUtils.sendEvent(EventBusEvent(
                                EventBusAction.REFRESH_DEVICE_MEMBER_LIST, bean))
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                }
            }, onRequestFinish = {
                dismissDialog()
            })
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun addNewMemberEvent(event: EventBusEvent<Int>) {
        if (event.code == EventBusAction.DEVICE_ADD_MEMBER_INFO) {
            if (event.data != 0) {
                //为设备添加新用户成功
                accountId = event.data.toString()
                val phoneValue = phoneEdit.text.toString()
                bindNewMemberCheck(phoneValue, deviceNameEdit.text.toString())
            }
        }
    }

    private fun addMemberToGroup() {
        val groupId = userGroupId.substring(0, userGroupId.lastIndexOf(".")).toInt()
        lifecycleScope.launch {
            ApiCoroutinesCallBack.resultParse(mContext!!, onStart = {
                showDialog()
            }, block = {
                val resultBean = GuiderApiUtil.getApiService()
                        .groupAddMemberDevice(
                                groupId, accountId.toInt(), deviceNameEdit.text.toString())
                if (!resultBean.isNullOrEmpty()) {
                    val bean = CheckBindDeviceBean()
                    bean.userGroupId = userGroupId.toInt()
                    bean.userInfos = resultBean
                    EventBusUtils.sendEvent(EventBusEvent(
                            EventBusAction.REFRESH_DEVICE_MEMBER_LIST, bean))
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }, onRequestFinish = {
                dismissDialog()
            })
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