package com.guider.gps.view.activity

import android.content.Intent
import android.view.View
import com.guider.baselib.base.BaseActivity
import com.guider.baselib.utils.StringUtils
import com.guider.baselib.utils.MMKVUtil
import com.guider.baselib.utils.USER
import com.guider.gps.R

/**
 * @Package:        com.guider.gps.view.activity
 * @ClassName:      SplashActivity
 * @Description:    启动闪屏页
 * @Author:         hjr
 * @CreateDate:     2020/8/27 15:06
 * Copyright (C), 1998-2020, GuiderTechnology
 */
class SplashActivity : BaseActivity() {

    override val contentViewResId: Int
        get() = R.layout.activity_splash

    override fun beforeContentViewSet() {
        this.window.decorView.background = null
        if (StringUtils.isNotBlankAndEmpty(MMKVUtil.getString(USER.USERID))) {
            //已经登录过
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish()
    }

    override fun openEventBus(): Boolean {
        return false
    }

    override fun initImmersion() {
        whiteStatusBarBlackFont()
        commonToolBarId?.visibility = View.GONE
    }

    override fun initView() {

    }

    override fun initLogic() {

    }

    override fun showToolBar(): Boolean {
        return true
    }
}