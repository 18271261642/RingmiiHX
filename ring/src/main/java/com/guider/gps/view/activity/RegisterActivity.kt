package com.guider.gps.view.activity

import android.app.Activity
import android.content.Intent
import android.view.View
import com.guider.baselib.base.BaseActivity
import com.guider.baselib.utils.StringUtils
import com.guider.baselib.utils.toastShort
import com.guider.gps.R
import kotlinx.android.synthetic.main.activity_register.*
import me.jessyan.autosize.internal.CustomAdapt

class RegisterActivity : BaseActivity(), CustomAdapt {

    override val contentViewResId: Int
        get() = R.layout.activity_register

    override fun openEventBus(): Boolean {
        return false
    }

    override fun initImmersion() {
        showBackButton(R.drawable.ic_back_white)
        setTitle(mContext!!.resources.getString(R.string.app_register))
        setStatusBarBackground(R.drawable.bg_register_top_bar)
    }

    override fun initView() {

    }

    override fun getSizeInDp(): Float {
        return 640f
    }

    override fun isBaseOnWidth(): Boolean {
        return false
    }

    override fun initLogic() {
        registerTv.setOnClickListener(this)
    }

    override fun onNoDoubleClick(v: View) {
        when (v) {
            registerTv -> {
                if (StringUtils.isEmpty(phoneEdit.text.toString())) {
                    toastShort(mContext!!.resources.getString(R.string.app_login_phone_empty))
                    return
                }
                if (StringUtils.isEmpty(passwordEdit.text.toString())) {
                    toastShort(mContext!!.resources.getString(R.string.app_login_password_empty))
                    return
                }
                if (StringUtils.isEmpty(againPasswordEdit.text.toString())) {
                    toastShort(mContext!!.resources.getString(R.string.app_login_password_empty))
                    return
                }
                if (againPasswordEdit.text.toString() != passwordEdit.text.toString()) {
                    toastShort(mContext!!.resources.getString(
                            R.string.app_register_password_different))
                    return
                }
                val intent = Intent()
                intent.putExtra("register", "success")
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    override fun showToolBar(): Boolean {
        return true
    }
}