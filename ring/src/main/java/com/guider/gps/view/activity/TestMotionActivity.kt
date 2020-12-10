package com.guider.gps.view.activity

import com.guider.baselib.base.BaseActivity
import com.guider.gps.R

/**
 * @Package: com.guider.gps.view.activity
 * @ClassName: TestMotionActivity
 * @Description: 测试MotionLayout
 * @Author: hjr
 * @CreateDate: 2020/12/9 11:19
 * Copyright (C), 1998-2020, GuiderTechnology
 */
class TestMotionActivity :BaseActivity(){

    override val contentViewResId: Int
        get() = R.layout.activity_test_motion

    override fun initImmersion() {
        setTitle("测试的motion")
    }

    override fun initView() {

    }

    override fun initLogic() {

    }

    override fun showToolBar(): Boolean {
        return true
    }
}