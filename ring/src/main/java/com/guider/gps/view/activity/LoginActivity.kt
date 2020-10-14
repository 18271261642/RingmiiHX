package com.guider.gps.view.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.guider.baselib.base.BaseActivity
import com.guider.baselib.utils.*
import com.guider.baselib.utils.USER.BIRTHDAY
import com.guider.baselib.utils.USER.COUNTRY_CODE
import com.guider.baselib.utils.USER.HEADER
import com.guider.baselib.utils.USER.NAME
import com.guider.baselib.utils.USER.PHONE
import com.guider.baselib.utils.USER.TOKEN
import com.guider.baselib.utils.USER.USERID
import com.guider.baselib.widget.dialog.DialogHolder
import com.guider.feifeia3.utils.ToastUtil
import com.guider.gps.R
import com.guider.gps.adapter.CountryCodeDialogAdapter
import com.guider.gps.view.line.ILineLogin
import com.guider.gps.view.line.LineLoginEvent
import com.guider.health.apilib.ApiCallBack
import com.guider.health.apilib.ApiUtil
import com.guider.health.apilib.GuiderApiUtil
import com.guider.health.apilib.JsonApi
import com.guider.health.apilib.bean.AreCodeBean
import com.guider.health.apilib.bean.CheckBindDeviceBean
import com.guider.health.apilib.bean.TokenInfo
import com.guider.health.apilib.bean.WeChatInfo
import com.linecorp.linesdk.LoginDelegate
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPI
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.include_phone_edit_layout.*
import kotlinx.coroutines.launch
import me.jessyan.autosize.internal.CustomAdapt
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import retrofit2.Call
import retrofit2.Response
import java.util.*


class LoginActivity : BaseActivity(), CustomAdapt, ILineLogin {

    private lateinit var api: IWXAPI

    private var transY = 0
    private var isAnim = false
    private var phoneValue = ""
    private var isLineLoginFirst = false

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
        transY = ScreenUtils.dip2px(this, 60f)
        setListenerToRootView()
        if (MMKVUtil.containKey(PHONE) &&
                StringUtil.isNotBlankAndEmpty(MMKVUtil.getString(PHONE))) {
            phoneEdit.setText(MMKVUtil.getString(PHONE))
        }
        if (MMKVUtil.containKey(COUNTRY_CODE) &&
                StringUtil.isNotBlankAndEmpty(MMKVUtil.getString(COUNTRY_CODE))) {
            countryTv.text = MMKVUtil.getString(COUNTRY_CODE)
        }
    }

    override fun initLogic() {
        registerTv.setOnClickListener(this)
        noAccountTv.setOnClickListener(this)
        loginTv.setOnClickListener(this)
//        weChatIv.setOnClickListener(this)
        lineIv.setOnClickListener(this)
        textTv.setOnClickListener(this)
        val spanny = Spanny()
                .append(mContext!!.resources.getString(R.string.app_login_to_sing_agree),
                        ForegroundColorSpan(
                                CommonUtils.getColor(mContext!!, R.color.color999999))
                )
                .append(mContext!!.resources.getString(R.string.app_login_user_service_agreement),
                        ForegroundColorSpan(
                                CommonUtils.getColor(mContext!!, R.color.colorF18A2E)))
                .append(mContext!!.resources.getString(R.string.app_login_and),
                        ForegroundColorSpan(
                                CommonUtils.getColor(mContext!!, R.color.color999999))
                )
                .append(mContext!!.resources.getString(R.string.app_login_privacy_policy_clause),
                        ForegroundColorSpan(
                                CommonUtils.getColor(mContext!!, R.color.colorF18A2E)))
        textTv.text = spanny
        countryCodeLayout.setOnClickListener(this)
        passwordEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.length != 0) {
                    passwordShowIv.visibility = View.VISIBLE
                } else passwordShowIv.visibility = View.GONE
            }

        })
        passwordShowIv.setOnClickListener(this)
        phoneEdit.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (StringUtil.isNotBlankAndEmpty(phoneEdit.text.toString())) {
                    phoneValue = phoneEdit.text.toString()
                    val countryCode = countryTv.text.toString().replace(
                            "+", "")
                    if (!StringUtil.isMobileNumber(countryCode, phoneValue)) {
                        ToastUtil.showCenter(mContext!!,
                                mContext!!.resources.getString(R.string.app_incorrect_format))
                    }
                }
            }
        }
    }

    /**
     * 通过view树监听键盘变化
     */
    private fun setListenerToRootView() {
        val rootView = window.decorView.findViewById<View>(android.R.id.content)
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val mKeyboardUp = isKeyboardShown(rootView)
            if (mKeyboardUp) {
                //键盘弹出
                editLayout.translationY = -(transY.toFloat())
                isAnim = true
            } else {
                //键盘收起
                if (isAnim) {
                    editLayout.translationY = 0f
                    isAnim = false
                }
            }
        }
    }

    private fun isKeyboardShown(rootView: View): Boolean {
        val softKeyboardHeight = 100
        val r = Rect()
        rootView.getWindowVisibleDisplayFrame(r)
        val dm = rootView.resources.displayMetrics
        val heightDiff = rootView.bottom - r.bottom
        Log.e("diff", heightDiff.toString())
        //同时判断键盘高度和是否是密码键盘获取到焦点才去移动布局
        return heightDiff > softKeyboardHeight * dm.density && passwordEdit.isFocused
    }

    override fun onNoDoubleClick(v: View) {
        when (v) {
            registerTv, noAccountTv -> {
                val intent = Intent(mContext, RegisterActivity::class.java)
                if (StringUtil.isNotBlankAndEmpty(phoneEdit.text.toString())) {
                    intent.putExtra("phone", phoneEdit.text.toString())
                }
                intent.putExtra("country", countryTv.text.toString())
                startActivityForResult(intent, REGISTER)
            }
            loginTv -> {
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
                if (StringUtil.isEmpty(passwordEdit.text.toString())) {
                    toastShort(mContext!!.resources.getString(R.string.app_login_password_empty))
                    return
                }
                loginEvent()
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
                    if (!isLineLoginFirst) {
                        isLineLoginFirst = true
                        dealLineInfo(it)
                    }
                }
            }
            textTv -> {
                val intent = Intent(mContext, SimpleCustomWebActivity::class.java)
                intent.putExtra("pageTitle",
                        mContext?.resources?.getString(R.string.app_login_privacy_policy_clause))
                intent.putExtra("webUrl",
                        "http://cmate.guidertech.com/#/open?navbartitle=1")
                startActivity(intent)
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
        }
    }

    private fun loginEvent() {
        val countryCode = countryTv.text.toString().replace("+", "")
        val passwordValue = MyUtils.md5(passwordEdit.text.toString())
        showDialog()
        lifecycleScope.launch {
            try {
                val tokenInfo = GuiderApiUtil.getApiService().loginWithPassword(
                        countryCode, phoneValue, passwordValue)
                if (tokenInfo != null) {
                    loginSuccessEvent(tokenInfo)
                }
            } catch (e: Exception) {
                dismissDialog()
                toastShort(e.message!!)
            }
        }
    }

    private fun loginSuccessEvent(bean: TokenInfo) {
        MMKVUtil.saveString(TOKEN, bean.token!!)
        MMKVUtil.saveInt(USERID, bean.accountId)
        MMKVUtil.saveString(COUNTRY_CODE, countryTv.text.toString())
        MMKVUtil.saveString(PHONE, phoneValue)
        MMKVUtil.saveString(REFRESH_TOKEN, bean.refreshToken!!)
        MMKVUtil.saveInt(EXPIRED_TIME, bean.expired)
        //拿到登录的token后去拿个人信息
        getUserInfo(bean.accountId)
    }

    private fun getUserInfo(accountId: Int) {
        lifecycleScope.launch {
            try {
                val resultBean = GuiderApiUtil.getApiService().getUserInfo(
                        accountId)
                if (resultBean != null) {
                    if (StringUtil.isNotBlankAndEmpty(resultBean.headUrl))
                        MMKVUtil.saveString(HEADER, resultBean.headUrl!!)
                    if (StringUtil.isNotBlankAndEmpty(resultBean.name))
                        MMKVUtil.saveString(NAME, resultBean.name!!)
                    var birthday = "1970-01-01"
                    if (StringUtil.isNotBlankAndEmpty(resultBean.birthday))
                        birthday = resultBean.birthday!!.replace(
                                "T00:00:00Z", "")
                    MMKVUtil.saveString(BIRTHDAY, birthday)
                    //拿到个人信息并保存后去验证是否绑定设备
                    verifyIsBindDevice(accountId)
                }
            } catch (e: Exception) {
                dismissDialog()
                toastShort(e.message!!)
            }
        }
    }

    private fun verifyIsBindDevice(accountId: Int) {
        lifecycleScope.launch {
            try {
                val resultBean = GuiderApiUtil.getApiService().checkIsBindDevice(
                        accountId)
                dismissDialog()
                if (resultBean is String && resultBean == "null") {
                    //未绑定帐号返回null
                    val intent = Intent(mContext!!, AddNewDeviceActivity::class.java)
                    intent.putExtra("type", "mine")
                    startActivity(intent)
                    finish()
                } else {
                    val bean = ParseJsonData.parseJsonAny<CheckBindDeviceBean>(
                            resultBean)
                    val intent = Intent(mContext!!, MainActivity::class.java)
                    MMKVUtil.saveInt(BIND_DEVICE_ACCOUNT_ID, accountId)
                    kotlin.run breaking@{
                        bean.userInfos?.forEach {
                            if (it.accountId == accountId) {
                                it.relationShip = it.name
                                MMKVUtil.saveString(BIND_DEVICE_NAME, it.name!!)
                                if (StringUtil.isNotBlankAndEmpty(it.headUrl))
                                    MMKVUtil.saveString(BIND_DEVICE_ACCOUNT_HEADER, it.headUrl!!)
                                if (StringUtil.isNotBlankAndEmpty(it.deviceCode)) {
                                    MMKVUtil.saveString(BIND_DEVICE_CODE, it.deviceCode!!)
                                    MMKVUtil.saveString(USER.OWN_BIND_DEVICE_CODE,
                                            it.deviceCode!!)
                                }
                                return@breaking
                            }
                        }
                    }
                    intent.putExtra("bindListBean", bean)
                    startActivity(intent)
                    finish()
                }
            } catch (e: Exception) {
                dismissDialog()
                toastShort(e.message!!)
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
            lineLogin(openId, pictureUrl, displayName)
        }
    }

    private fun lineLogin(openId: String, header: String, name: String) {
        showDialog()
        lifecycleScope.launch {
            try {
                val resultBean = GuiderApiUtil.getApiService()
                        .lineVerifyLogin(APP_ID_LINE, openId, -1, -1)
                dismissDialog()
                if (resultBean != null) {
                    if (resultBean.TokenInfo == null) {
                        //说明没有绑定用户
                        val intent = Intent(mContext!!, BindPhoneActivity::class.java)
                        intent.putExtra("openId", openId)
                        intent.putExtra("appId", APP_ID_LINE)
                        intent.putExtra("header", header)
                        intent.putExtra("name", name)
                        startActivityForResult(intent, BIND_PHONE)
                    } else {
                        //绑定了用户成功登录
                        loginSuccessEvent(resultBean.TokenInfo!!)
                    }
                }
            } catch (e: Exception) {
                dismissDialog()
                isLineLoginFirst = false
                toastShort(e.message!!)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun weChatLogin(event: EventBusEvent<String>) {
        if (event.code == EventBusAction.WECHAT_LOGIN) {
            val data: String = event.data as String
            if (StringUtil.isNotBlankAndEmpty(data) &&
                    data.contains(":")) {
                val dataList = data.split(":")
                if (dataList.isNotEmpty()) {
                    dealWeChatInfo(dataList)
                }
            } else {
                ToastUtil.show(mContext!!, "微信登录失败")
            }
        }
    }

    private fun dealWeChatInfo(dataList: List<String>) {
        val openId: String
        var wxName = ""
        var header = ""
        var sex = 0
        val unionid: String
        if (dataList.size == 5) {
            openId = dataList[0]
            unionid = dataList[1]
            sex = dataList[2].toInt()
            wxName = dataList[3]
            header = dataList[4]
        } else {
            openId = dataList[0]
            unionid = dataList[1]
        }
        val weChatInfo = WeChatInfo()
        weChatInfo.setAppId(APP_ID_WX)
        weChatInfo.openid = openId
        weChatInfo.nickname = wxName
        weChatInfo.headimgurl =
                if (StringUtil.isNotBlankAndEmpty(header)) header else null
        weChatInfo.unionid = unionid
        weChatInfo.sex = sex
        showDialog()
        lifecycleScope.launch {
            try {
                val resultBean = GuiderApiUtil.getApiService()
                        .weChatLoginToken(weChatInfo)
                dismissDialog()
                if (resultBean != null) {
                    //如果返回flag==false，调用第二步绑定手机号
                    if (!resultBean.isFlag) {
                        //说明没有绑定用户
                        val intent = Intent(mContext!!, BindPhoneActivity::class.java)
                        intent.putExtra("weChatInfo", weChatInfo)
                        intent.putExtra("type", "weChat")
                        startActivityForResult(intent, BIND_PHONE)
                    } else {
                        //绑定了用户成功登录
                        loginSuccessEvent(resultBean.tokenInfo!!)
                    }
                }
            } catch (e: Exception) {
                dismissDialog()
                toastShort(e.message!!)
            }
        }
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
                REGISTER -> {
                    Log.i("登录页", "注册成功，判断账号和设备的绑定")
                    val accountId = MMKVUtil.getInt(USERID)
                    verifyIsBindDevice(accountId)
                }
                BIND_PHONE -> {
                    Log.i("第三方登录", "第三方绑定成功，判断账号和设备的绑定")
                    val accountId = MMKVUtil.getInt(USERID)
                    verifyIsBindDevice(accountId)
                }
            }
        }
    }

    override fun showToolBar(): Boolean {
        return true
    }

    override fun getLoginDelegate(): LoginDelegate {
        return loginDelegate
    }
}