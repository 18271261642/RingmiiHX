package com.guider.gps.view.activity

import com.guider.baselib.base.BaseActivity
import com.guider.gps.R
import kotlinx.android.synthetic.main.activity_health_care_msg_detail.*

class HealthCareMsgDetailActivity : BaseActivity() {

    override val contentViewResId: Int
        get() = R.layout.activity_health_care_msg_detail

    override fun openEventBus(): Boolean {
        return false
    }

    override fun initImmersion() {
        setTitle(resources.getString(R.string.app_watch_detail))
        showBackButton(R.drawable.ic_back_white)
    }

    override fun initView() {
    }

    override fun initLogic() {
        val msgContent = "人们每天摄入的热量大部分都来自碳水化合物，如" +
                "面包，面条，大米，谷物和土豆。对于简单碳水化" +
                "合物，饮用牛奶和果汁，食用适量的水果是十分重" +
                "要的。但食用糖和其他甜味剂会提供大量体内不需" +
                "合物，饮用牛奶和果汁，食用适量的水果是十分重" +
                "要的。但食用糖和其他甜味剂会提供大量体内不需" +
                "要的热量对健康有害。对于复杂碳水化合物，…"
        careMsgContent.text = msgContent
    }

    override fun showToolBar(): Boolean {
        return true
    }
}