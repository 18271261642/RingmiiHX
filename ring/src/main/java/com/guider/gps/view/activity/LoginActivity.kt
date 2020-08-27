package com.guider.gps.view.activity

import android.app.Activity
import android.content.Intent
import android.view.View
import com.guider.baselib.base.BaseActivity
import com.guider.baselib.utils.*
import com.guider.feifeia3.utils.MMKVUtil
import com.guider.feifeia3.utils.ToastUtil
import com.guider.gps.R
import com.guider.gps.view.line.ILineLogin
import com.guider.gps.view.line.LineLoginEvent
import com.linecorp.linesdk.LoginDelegate
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPI
import kotlinx.android.synthetic.main.activity_login.*
import me.jessyan.autosize.internal.CustomAdapt
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.HashMap


class LoginActivity : BaseActivity(), CustomAdapt, ILineLogin {

    private lateinit var api: IWXAPI

    /**
     * Line第三方登陆相关
     */
    private val loginDelegate = LoginDelegate.Factory.create()

    override val contentViewResId: Int
        get() = R.layout.activity_login

    override fun openEventBus(): Boolean {
        return true
    }

    override fun initImmersion() {
        mImmersionBar!!.statusBarDarkFont(false).init()
        commonToolBarId?.visibility = View.GONE
    }

    override fun getSizeInDp(): Float {
        return 640f
    }

    override fun isBaseOnWidth(): Boolean {
        return false
    }

    override fun initView() {
        //通过WXAPIFactory工厂获取IWXApI的示例
//        api = WXAPIFactory.createWXAPI(this, APP_ID_WX, true)
//        //将应用的appid注册到微信
//        api.registerApp(APP_ID_WX)
    }

    override fun initLogic() {
        registerTv.setOnClickListener(this)
        noAccountTv.setOnClickListener(this)
        loginTv.setOnClickListener(this)
//        weChatIv.setOnClickListener(this)
        lineIv.setOnClickListener(this)
    }

    override fun onNoDoubleClick(v: View) {
        when (v) {
            registerTv, noAccountTv -> {
                val intent = Intent(mContext, RegisterActivity::class.java)
                if (StringUtils.isNotBlankAndEmpty(phoneEdit.text.toString())) {
                    intent.putExtra("phone", phoneEdit.text.toString())
                }
                startActivityForResult(intent, REGISTER)
            }
            loginTv -> {
                if (StringUtils.isEmpty(phoneEdit.text.toString())) {
                    toastShort(mContext!!.resources.getString(R.string.app_login_phone_empty))
                    return
                }
                if (StringUtils.isEmpty(passwordEdit.text.toString())) {
                    toastShort(mContext!!.resources.getString(R.string.app_login_password_empty))
                    return
                }
                toastShort("登录成功，进入首页")
                enterMainPage()
            }
            weChatIv -> {
                if (CommonUtils.isHaveWeChat(mContext!!)) {
                    //有微信
                    val req = SendAuth.Req()
                    req.scope = "snsapi_userinfo"
                    req.state = "wechat_sdk_微信登录"
                    MMKVUtil.saveString("wxInfo", "wxInfo")
                    api.sendReq(req)
                } else {
                    ToastUtil.show(mContext!!, "请先安装微信")
                }
            }
            lineIv -> {
                LineLoginEvent.lineOfficeLogin(this, loginButton, APP_ID_LINE,
                        null
                ) {
                    dealLineInfo(it)
                }
            }
        }
    }

    private fun dealLineInfo(hashMap: HashMap<String, String?>) {
        var userId = ""
        if (StringUtils.isNotBlankAndEmpty(hashMap["userId"])) {
            userId = hashMap["userId"]!!
        }
        var pictureUrl = ""
        if (StringUtils.isNotBlankAndEmpty(hashMap["pictureUrl"])) {
            pictureUrl = hashMap["pictureUrl"]!!
        }
        var displayName = ""
        if (StringUtils.isNotBlankAndEmpty(hashMap["displayName"])) {
            displayName = hashMap["displayName"]!!
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun weChatLogin(event: EventBusEvent<String>) {
        if (event.code == EventBusAction.WECHAT_LOGIN) {
            val data: String = event.data as String
            if (StringUtils.isNotBlankAndEmpty(data) &&
                    data.contains(":")) {
                val dataList = data.split(":")
                if (dataList.isNotEmpty()) {
                    var openId = ""
                    var unionid = ""
                    var wxName = ""
                    if (dataList.size == 3) {
                        openId = dataList[0]
                        unionid = dataList[1]
                        wxName = dataList[2]
                    } else {
                        openId = dataList[0]
                        unionid = dataList[1]
                    }
                }
            } else {
                ToastUtil.show(mContext!!, "微信登录失败")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                REGISTER -> {
                    toastShort("注册成功，进入首页")
                    enterMainPage()
                }
            }
        }
    }

    private fun enterMainPage() {
        val intent = Intent(mContext, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun showToolBar(): Boolean {
        return true
    }

    override fun getLoginDelegate(): LoginDelegate {
        return loginDelegate
    }
}