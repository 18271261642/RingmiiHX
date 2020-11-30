package com.guider.gps.view.activity

import android.content.Intent
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.guider.baselib.base.BaseActivity
import com.guider.baselib.utils.APP_ID_LINE
import com.guider.baselib.utils.StringUtil
import com.guider.baselib.utils.USER
import com.guider.gps.R
import com.guider.gps.view.line.ILineLogin
import com.guider.gps.view.line.LineLoginEvent
import com.guider.gps.viewModel.ThirdAccountViewModel
import com.guider.health.apilib.enums.ThirdAccountType
import com.guider.health.apilib.utils.MMKVUtil
import com.linecorp.linesdk.LoginDelegate
import kotlinx.android.synthetic.main.acitivity_third_account_bind.*
import java.util.*

/**
 * @Package: com.guider.gps.view.activity
 * @ClassName: ThirdAccountBindActivity
 * @Description: 第三方账号绑定页面
 * @Author: hjr
 * @CreateDate: 2020/11/30 15:59
 * Copyright (C), 1998-2020, GuiderTechnology
 */
class ThirdAccountBindActivity : BaseActivity(), ILineLogin {

    override val contentViewResId: Int
        get() = R.layout.acitivity_third_account_bind

    private var lineBindFlag = false
    private var isLineLoginFirst = false
    private lateinit var viewModel: ThirdAccountViewModel

    /**
     * Line第三方登陆相关
     */
    private val loginDelegate = LoginDelegate.Factory.create()

    override fun initImmersion() {
        setTitle(mContext!!.resources.getString(R.string.app_account_bind))
        showBackButton(R.drawable.ic_back_white)
    }

    override fun initView() {
        lineBindStatusShow()
        initViewModel()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[ThirdAccountViewModel::class.java]
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
    }

    override fun initLogic() {
        checkThirdAccountStatus()
        lienLayout.setOnClickListener(this)
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
            lienLayout -> {
                if (lineBindFlag) {
                    lineBindFlag = false
                    lineBindStatusShow()
                } else {
                    LineLoginEvent.lineOfficeLogin(this, loginButton, APP_ID_LINE,
                            null
                    ) {
                        if (!isLineLoginFirst) {
                            isLineLoginFirst = true
                            dealLineInfo(it)
                        }
                    }
                }
            }
        }
    }

    private fun dealLineInfo(hashMap: HashMap<String, String?>) {
        var pictureUrl = ""
        if (StringUtil.isNotBlankAndEmpty(hashMap["pictureUrl"])) {
            pictureUrl = hashMap["pictureUrl"]!!
        }
        var displayName = ""
        if (StringUtil.isNotBlankAndEmpty(hashMap["displayName"])) {
            displayName = hashMap["displayName"]!!
        }
        if (StringUtil.isNotBlankAndEmpty(hashMap["userId"])) {
            val openId = hashMap["userId"]!!
            lineBind(openId, pictureUrl, displayName)
        }
    }

    private fun lineBind(openId: String, header: String, name: String) {
        MMKVUtil.saveString("LINEOPENID", openId)
        lineBindFlag = true
        lineBindStatusShow()
//        lifecycleScope.launch {
//            ApiCoroutinesCallBack.resultParse(mContext!!, onStart = {
//                showDialog()
//            }, block = {
//                val resultBean = GuiderApiUtil.getApiService()
//                        .lineVerifyLogin(APP_ID_LINE, openId, -1, -1)
//            }, onError = {
//                isLineLoginFirst = false
//            }, onRequestFinish = {
//                dismissDialog()
//            })
//        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //LINE登录的回调
        if (loginDelegate.onActivityResult(requestCode, resultCode, data)) {
            // Login result is consumed.
            return
        }
    }

    override fun getLoginDelegate(): LoginDelegate {
        return loginDelegate
    }

    override fun showToolBar(): Boolean {
        return true
    }
}