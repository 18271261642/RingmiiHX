package com.guider.gps.view.activity

import android.content.Intent
import com.guider.baselib.base.BaseActivity
import com.guider.baselib.utils.StringUtil
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

    private var isHaveNewMsg = false

    override val contentViewResId: Int
        get() = R.layout.activity_splash

    override fun openEventBus(): Boolean {
        return false
    }

    override fun initImmersion() {
        if (intent != null) {
            if (StringUtil.isNotBlankAndEmpty(intent.getStringExtra("key"))) {
                isHaveNewMsg = true
            }
        }
    }


    override fun initView() {
        val intent = Intent(this, GuideActivity::class.java)
        if (isHaveNewMsg) intent.putExtra("news", isHaveNewMsg)
        startActivity(intent)
        finish()
    }

    override fun initLogic() {

    }

    override fun showToolBar(): Boolean {
        return false
    }
}