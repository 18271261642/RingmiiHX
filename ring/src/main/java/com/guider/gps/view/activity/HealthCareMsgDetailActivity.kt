package com.guider.gps.view.activity

import com.guider.baselib.base.BaseActivity
import com.guider.baselib.utils.StringUtil
import com.guider.gps.R
import kotlinx.android.synthetic.main.activity_health_care_msg_detail.*

class HealthCareMsgDetailActivity : BaseActivity() {

    override val contentViewResId: Int
        get() = R.layout.activity_health_care_msg_detail

    override fun openEventBus(): Boolean {
        return false
    }

    private var content = ""

    override fun initImmersion() {
        setTitle(resources.getString(R.string.app_watch_detail))
        showBackButton(R.drawable.ic_back_white)
        if (intent != null) {
            if (StringUtil.isNotBlankAndEmpty(intent.getStringExtra("content"))) {
                content = intent.getStringExtra("content")!!
            }
        }
    }

    override fun initView() {
    }

    override fun initLogic() {
        careMsgContent.text = content
    }

    override fun showToolBar(): Boolean {
        return true
    }
}