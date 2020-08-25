package com.guider.gps.view.activity

import android.app.Activity
import android.view.View
import com.guider.baselib.base.BaseActivity
import com.guider.gps.R
import kotlinx.android.synthetic.main.activity_location_frequency_set.*

/**
 * 定位频率设置
 */
class LocationFrequencySetActivity : BaseActivity() {

    override val contentViewResId: Int
        get() = R.layout.activity_location_frequency_set

    override fun openEventBus(): Boolean {
        return false
    }

    override fun initImmersion() {
        setTitle(resources.getString(R.string.app_main_map_positioning_frequency_setting))
        showBackButton(R.drawable.ic_back_white)
        setRightImage(R.drawable.icon_top_right_confirm, this)
    }

    override fun initView() {
        switchSet1.isChecked = false
        switchSet2.isChecked = true
        switchSet3.isChecked = false
    }

    override fun onNoDoubleClick(v: View) {
        when (v) {
            iv_toolbar_right -> {
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    override fun initLogic() {

    }

    override fun showToolBar(): Boolean {
        return true
    }
}