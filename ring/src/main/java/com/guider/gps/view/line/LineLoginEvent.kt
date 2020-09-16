package com.guider.gps.view.line

import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import com.guider.baselib.base.BaseActivity
import com.guider.baselib.utils.StringUtil
import com.guider.feifeia3.utils.ToastUtil
import com.linecorp.linesdk.LineApiResponseCode
import com.linecorp.linesdk.LoginListener
import com.linecorp.linesdk.Scope
import com.linecorp.linesdk.auth.LineAuthenticationParams
import com.linecorp.linesdk.auth.LineLoginResult
import com.linecorp.linesdk.widget.LoginButton
import java.util.*

object LineLoginEvent {

    var tag = "LineLoginEvent"

    @Synchronized
    fun lineOfficeLogin(context: Context?, loginButton: LoginButton,
                        appId: String?, fragment: Fragment?,
                        onSuccess: (hashMap: HashMap<String, String?>) -> Unit) {
        // final LoginButton loginButton = new LoginButton(context);
        // if the button is inside a Fragment, this function should be called.
        if (fragment != null) loginButton.setFragment(fragment)
        // replace the string to your own channel id.
        loginButton.setChannelId(appId!!)
        // configure whether login process should be done by LINE App, or by WebView.
        loginButton.enableLineAppAuthentication(true)
        // set up required scopes.
        // aggressive 模式相当于关注微信公众号
        //normal是正常登陆模式
        loginButton.setAuthenticationParams(LineAuthenticationParams.Builder()
                .scopes(listOf(Scope.PROFILE, Scope.OPENID_CONNECT))
                .botPrompt(LineAuthenticationParams.BotPrompt.aggressive)
                .build()
        )

        // A delegate for delegating the login result to the internal login handler.
        // LoginDelegate loginDelegate = LoginDelegate.Factory.create();
        if (fragment != null && fragment is ILineLogin) {
            loginButton.setLoginDelegate((fragment as ILineLogin).loginDelegate)
        } else if (context is ILineLogin) {
            loginButton.setLoginDelegate((context as ILineLogin).loginDelegate)
        }
        loginButton.addLoginListener(object : LoginListener {
            override fun onLoginSuccess(result: LineLoginResult) {
                // Toast.makeText(mContext, "Login success", Toast.LENGTH_SHORT).show();
                val ret = HashMap<String, String?>()
                ret["userId"] = result.lineProfile!!.userId
                if (result.lineProfile!!.pictureUrl != null
                        && StringUtil.isEmpty(result.lineProfile!!.pictureUrl!!.path))
                    ret["pictureUrl"] = result.lineProfile!!.pictureUrl!!.path
                else ret["pictureUrl"] = ""
                ret["displayName"] = result.lineProfile!!.displayName
                onSuccess.invoke(ret)
            }

            override fun onLoginFailure(result: LineLoginResult?) {
                Log.e(tag, "onLoginFailure : " + result.toString())
                if (result?.responseCode == LineApiResponseCode.CANCEL) {
                    ToastUtil.show(context!!, "Login cancel")
                } else {
                    ToastUtil.show(context!!, "Login failure")
                }
            }
        })
        (context as BaseActivity).runOnUiThread { loginButton.performClick() }
    }
}