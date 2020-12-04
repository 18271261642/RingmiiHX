package com.guider.gps.view.activity

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.guider.baselib.base.BaseActivity
import com.guider.baselib.utils.*
import com.guider.gps.R
import com.guider.gps.view.line.ILineLogin
import com.guider.gps.view.line.LineLoginEvent
import com.guider.gps.viewModel.AccountViewModel
import com.guider.health.apilib.enums.ThirdAccountType
import com.guider.health.apilib.utils.MMKVUtil
import com.linecorp.linesdk.LoginDelegate
import kotlinx.android.synthetic.main.acitivity_account_bind.*
import java.util.*

/**
 * @Package: com.guider.gps.view.activity
 * @ClassName: ThirdAccountBindActivity
 * @Description: 账号绑定页面
 * @Author: hjr
 * @CreateDate: 2020/11/30 15:59
 * Copyright (C), 1998-2020, GuiderTechnology
 */
class AccountBindActivity : BaseActivity(), ILineLogin {

    override val contentViewResId: Int
        get() = R.layout.acitivity_account_bind

    private var lineBindFlag = false
    private var isLineBindFirst = false
    private lateinit var viewModel: AccountViewModel
    private var lineOpenId = ""
    private var lineHeader = ""
    private var lineName = ""

    /**
     * Line第三方登陆相关
     */
    private val loginDelegate = LoginDelegate.Factory.create()
    private var isTouristsLogin = false

    override fun initImmersion() {
        setTitle(mContext!!.resources.getString(R.string.app_account_bind))
        showBackButton(R.drawable.ic_back_white, this)
    }

    override fun initView() {
        lineBindStatusShow()
        initViewModel()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[AccountViewModel::class.java]
        viewModel.loading.observe(this, {
            if (it) showDialog()
            else dismissDialog()
        })
        viewModel.checkStatusResult.observe(this, {
            lineBindFlag = if (it.isNullOrEmpty()) {
                false
            } else {
                it.contains(ThirdAccountType.LINE)
            }
            lineBindStatusShow()
        })
        viewModel.lineAccountResult.observe(this, {
            if (it) {
                val intent = Intent(mContext!!, BindPhoneActivity::class.java)
                intent.putExtra("openId", lineOpenId)
                intent.putExtra("appId", APP_ID_LINE)
                intent.putExtra("header", lineHeader)
                intent.putExtra("name", lineName)
                if (MMKVUtil.containKey(TOURISTS_MODE) && MMKVUtil.getBoolean(TOURISTS_MODE)) {
                    intent.putExtra("bindType", "touristsLINE")
                } else intent.putExtra("bindType", "bindLINE")
                startActivityForResult(intent, BIND_PHONE)
            } else {
                toastShort(mContext!!.resources.getString(R.string.app_line_account_error_hint))
            }
        })
        viewModel.unBindLineResult.observe(this, {
            if (it) {
                lineBindFlag = false
                isLineBindFirst = false
                lineBindStatusShow()
            }
        })
    }

    override fun initLogic() {
        showBindStatus()
        phoneLayout.setOnClickListener(this)
        lienLayout.setOnClickListener(this)
    }

    private fun showBindStatus() {
        if (MMKVUtil.containKey(TOURISTS_MODE) && MMKVUtil.getBoolean(TOURISTS_MODE)) {
            phoneBindTv.text = mContext!!.resources.getString(R.string.app_bind)
            lineBindTv.text = mContext!!.resources.getString(R.string.app_bind)
        } else {
            if (StringUtil.isNotBlankAndEmpty(MMKVUtil.getString(USER.PHONE))) {
                val phone = MMKVUtil.getString(USER.PHONE)
                phoneBindTv.text = StringUtil.showHidePhoneNumber(phone)
            } else phoneBindTv.text = mContext!!.resources.getString(R.string.app_bind)
            checkThirdAccountStatus()
        }
    }

    private fun checkThirdAccountStatus() {
        val accountId = MMKVUtil.getInt(USER.USERID)
        if (accountId == 0) return
        viewModel.getThirdStatusData(accountId)
    }

    private fun lineBindStatusShow() {
        if (!lineBindFlag) {
            lineBindTv.text = mContext!!.resources.getString(R.string.app_bind)
            lineBindIv.visibility = View.VISIBLE
        } else {
            lineBindTv.text = mContext!!.resources.getString(R.string.app_unbind)
            lineBindIv.visibility = View.GONE
        }
    }

    override fun onNoDoubleClick(v: View) {
        super.onNoDoubleClick(v)
        when (v) {
            phoneLayout -> {
                if (MMKVUtil.containKey(TOURISTS_MODE) && MMKVUtil.getBoolean(TOURISTS_MODE)) {
                    //进入绑定手机号页面绑定游客身份
                    val intent = Intent(mContext!!, BindPhoneActivity::class.java)
                    intent.putExtra("bindType", "touristsBind")
                    startActivityForResult(intent, BIND_PHONE)
                } else {
                    if (StringUtil.isNotBlankAndEmpty(MMKVUtil.getString(USER.PHONE))) {
                        //修改手机号
                    } else return
                }
            }
            lienLayout -> {
                if (lineBindFlag) {
                    viewModel.unBindLine()
                } else {
                    LineLoginEvent.lineOfficeLogin(this, loginButton, APP_ID_LINE,
                            null
                    ) {
                        if (!isLineBindFirst) {
                            isLineBindFirst = true
                            dealLineInfo(it)
                        }
                    }
                }
            }
            iv_left -> {
                onBackPressed()
            }
        }
    }

    override fun onBackPressed() {
        if (isTouristsLogin) {
            val intent = Intent()
            intent.putExtra("tourists", isTouristsLogin)
            setResult(Activity.RESULT_OK, intent)
        }
        super.onBackPressed()
    }

    private fun dealLineInfo(hashMap: HashMap<String, String?>) {
        if (StringUtil.isNotBlankAndEmpty(hashMap["pictureUrl"])) {
            lineHeader = hashMap["pictureUrl"]!!
        }
        if (StringUtil.isNotBlankAndEmpty(hashMap["displayName"])) {
            lineName = hashMap["displayName"]!!
        }
        if (StringUtil.isNotBlankAndEmpty(hashMap["userId"])) {
            lineOpenId = hashMap["userId"]!!
            lineBind()
        }
    }

    private fun lineBind() {
        viewModel.checkLineAccountIsNewAccount(APP_ID_LINE, lineOpenId)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //LINE登录的回调
        if (loginDelegate.onActivityResult(requestCode, resultCode, data)) {
            // Login result is consumed.
            return
        }
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                BIND_PHONE -> {
                    if (StringUtil.isNotBlankAndEmpty(data.getStringExtra("bindPhone"))) {
                        if (MMKVUtil.containKey(TOURISTS_MODE)
                                && MMKVUtil.getBoolean(TOURISTS_MODE)) {
                            MMKVUtil.clearByKey(TOURISTS_MODE)
                            isTouristsLogin = true
                            showBindStatus()
                        }
                    }
                    if (StringUtil.isNotBlankAndEmpty(data.getStringExtra("bindLine"))) {
                        if (MMKVUtil.containKey(TOURISTS_MODE)
                                && MMKVUtil.getBoolean(TOURISTS_MODE)) {
                            MMKVUtil.clearByKey(TOURISTS_MODE)
                            isTouristsLogin = true
                            if (StringUtil.isNotBlankAndEmpty(MMKVUtil.getString(USER.PHONE))) {
                                val phone = MMKVUtil.getString(USER.PHONE)
                                phoneBindTv.text = StringUtil.showHidePhoneNumber(phone)
                            }
                        }
                        lineBindFlag = true
                        lineBindStatusShow()
                    }
                }
            }
        }
    }

    override fun getLoginDelegate(): LoginDelegate {
        return loginDelegate
    }

    override fun showToolBar(): Boolean {
        return true
    }
}