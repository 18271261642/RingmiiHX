package com.guider.ring.view

import com.guider.baselib.base.BaseActivity
import com.guider.ring.R

class MainActivity : BaseActivity() {

    override val contentViewResId: Int
        get() = R.layout.activity_main

    override fun initImmersion() {
        setTitle("首页")
    }

    override fun initView() {
       whiteStatusBarBlackFont()
    }

    override fun initLogic() {

    }

    override fun showToolBar(): Boolean {
       return true
    }


}