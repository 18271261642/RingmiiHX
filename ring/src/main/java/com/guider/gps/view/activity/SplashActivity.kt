package com.guider.gps.view.activity

import android.content.Intent
import com.guider.baselib.base.BaseActivity
import com.guider.gps.R
import kotlinx.android.synthetic.main.activity_splash.*

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

    override fun openEventBus(): Boolean {
        return false
    }

    override fun initImmersion() {
        whiteStatusBarBlackFont()
    }

    override fun initView() {
        setTheme(R.style.Splash)
        companyInfo.postDelayed({
            startActivity(Intent(this, GuideActivity::class.java))
            finish()
        }, 300)
    }

    override fun initLogic() {

    }

    override fun showToolBar(): Boolean {
        return false
    }
}