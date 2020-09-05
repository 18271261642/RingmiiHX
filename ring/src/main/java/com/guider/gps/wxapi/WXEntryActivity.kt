package com.guider.gps.wxapi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.guider.baselib.base.BaseApplication
import com.guider.baselib.utils.*
import com.guider.feifeia3.utils.ToastUtil
import com.joinutech.ddbeslibrary.bean.WXAccessTokenEntity
import com.joinutech.ddbeslibrary.bean.WXBaseRespEntity
import com.joinutech.ddbeslibrary.bean.WXUserInfo
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.StringCallback
import okhttp3.Call

class WXEntryActivity : AppCompatActivity(), IWXAPIEventHandler {
    private lateinit var api: IWXAPI
    private val RETURN_MSG_TYPE_LOGIN = 1
    private val RETURN_MSG_TYPE_SHARE = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        api = WXAPIFactory.createWXAPI(this, APP_ID_WX, true)
        api.registerApp(APP_ID_WX)
        //注意：
        //第三方开发者如果使用透明界面来实现WXEntryActivity，需要判断handleIntent的返回值，
        // 如果返回值为false，则说明入参不合法未被SDK处理，应finish当前透明界面，
        // 避免外部通过传递非法参数的Intent导致停留在透明界面，引起用户的疑惑
        val result = api.handleIntent(intent, this)
        if (!result) {
            logShow("参数不合法，未被SDK处理，退出")
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        api.handleIntent(data, this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        api.handleIntent(intent, this)
        finish()
    }

    private fun logShow(text: String) {
        Log.d("WXLogin", text)
    }

    override fun onReq(baseReq: BaseReq?) {
        logShow("baseReq: ${Gson().toJson(baseReq)}")
    }

    override fun onResp(baseReq: BaseResp?) {
        val json = Gson().toJson(baseReq)
        logShow("baseReq: ${Gson().toJson(baseReq)}")
        logShow("baseResp--B:${baseReq!!.errStr},${baseReq.openId}," +
                "${baseReq.transaction},${baseReq.errCode}")
        val entity: WXBaseRespEntity = Gson().fromJson(json, WXBaseRespEntity::class.java)
        val result: String
        //根据errCode判断情况
        when (baseReq.errCode) {
            BaseResp.ErrCode.ERR_OK -> {
                //用户同意
                result = "发送成功"
                OkHttpUtils.get().url("https://api.weixin.qq.com/sns/oauth2/access_token")
                        .addParams("appid", APP_ID_WX)
                        .addParams("secret", APP_SECRET_WX)
                        .addParams("code", entity.code)
                        .addParams("grant_type", "authorization_code")
                        .build()
                        .execute(object : StringCallback() {
                            override fun onResponse(response: String?, id: Int) {
                                logShow("response:$response")
                                val accessTokenEntity: WXAccessTokenEntity? = Gson()
                                        .fromJson(response, WXAccessTokenEntity::class.java)
                                if (accessTokenEntity != null) {
                                    val openid = accessTokenEntity.openid
                                    val unionid = accessTokenEntity.unionid
                                    logShow("微信登录资料已获取，后续未完成")
                                    if (StringUtil.isNotBlankAndEmpty(MMKVUtil.getString("wxInfo"))) {
                                        //需要用户信息
                                        getUserInfo(accessTokenEntity)
                                    } else {
                                        //只是登录
                                        EventBusUtils.sendEvent(EventBusEvent(
                                                EventBusAction.WECHAT_LOGIN,
                                                "$openid:$unionid"))
                                        finish()
                                    }
                                } else {
                                    logShow("获取失败")
                                }
                            }

                            override fun onError(p0: Call?, p1: Exception?, p2: Int) {
                                logShow("请求错误")
                            }

                        })
            }
            BaseResp.ErrCode.ERR_USER_CANCEL -> {
                //用户取消
                result = "发送取消"
                if (baseReq.type == RETURN_MSG_TYPE_SHARE) {
                    ToastUtil.show(BaseApplication.guiderHealthContext, "微信分享取消")
                } else {
                    ToastUtil.show(BaseApplication.guiderHealthContext, "微信登录取消")
                }
                finish()
            }
            BaseResp.ErrCode.ERR_AUTH_DENIED -> {
                //用户拒绝授权
                result = "发送被拒绝"
                ToastUtil.show(BaseApplication.guiderHealthContext, "微信登录失败")
                finish()
            }
            BaseResp.ErrCode.ERR_BAN -> {
                //签名错误
                result = "签名错误"
                ToastUtil.show(BaseApplication.guiderHealthContext, "微信登录失败")
                finish()
            }
            else -> {
                result = "发送返回"
                ToastUtil.show(BaseApplication.guiderHealthContext, "微信登录取消")
                finish()
            }
        }
        logShow(result)
    }

    private fun getUserInfo(accessTokenEntity: WXAccessTokenEntity) {
        OkHttpUtils.get()
                .url("https://api.weixin.qq.com/sns/userinfo")
                .addParams("access_token", accessTokenEntity.access_token)
                .addParams("openid", accessTokenEntity.openid)//openid:授权用户唯一标识
                .build()
                .execute(object : StringCallback() {
                    override fun onResponse(response: String?, id: Int) {
                        logShow("response:$response")
                        val wxResponse: WXUserInfo? = Gson().fromJson(response,
                                WXUserInfo::class.java)
                        var nickName = ""
                        var headimgurl = ""
                        if (wxResponse != null) {
                            if (StringUtil.isNotBlankAndEmpty(wxResponse.nickname)) {
                                nickName = wxResponse.nickname!!
                            }
                            if (StringUtil.isNotBlankAndEmpty(wxResponse.nickname)) {
                                headimgurl = wxResponse.headimgurl!!
                            }
                            EventBusUtils.sendEvent(EventBusEvent(
                                    EventBusAction.WECHAT_LOGIN,
                                    "${wxResponse.openid}:" +
                                            "${wxResponse.unionid}:" +
                                            "${wxResponse.sex}:" +
                                            "$nickName:" +
                                            headimgurl))
                        }
                        finish()
                    }

                    override fun onError(p0: Call?, p1: Exception?, p2: Int) {
                        logShow("请求错误")
                    }

                })
    }

}