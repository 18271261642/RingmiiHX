package com.guider.gps.view.activity

import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.guider.baselib.base.BaseActivity
import com.guider.baselib.utils.CommonUtils
import com.guider.baselib.widget.viewpageradapter.FragmentLazyStateAdapterViewPager2
import com.guider.gps.R
import com.guider.gps.view.fragment.InputCodeAddDeviceFragment
import com.guider.gps.view.fragment.ScanCodeAddDeviceFragment
import kotlinx.android.synthetic.main.activity_add_new_device.*

class AddNewDeviceActivity : BaseActivity() {

    override val contentViewResId: Int
        get() = R.layout.activity_add_new_device

    private var fragments = mutableListOf<Fragment>()
    private var tabTitleList = arrayListOf<String>()

    override fun initImmersion() {
        setTitle(resources.getString(R.string.app_add_device))
        showBackButton(R.drawable.ic_back_white)
    }

    override fun initView() {
        tabTitleList = arrayListOf(
                resources.getString(R.string.app_add_device_scan_code),
                resources.getString(R.string.app_add_device_input))
        newDeviceTabLayout.tabMode = TabLayout.MODE_FIXED
        // 设置选中下划线颜色
        newDeviceTabLayout.setSelectedTabIndicatorColor(
                CommonUtils.getColor(mContext!!, R.color.colorF18937))
        // 设置文本字体颜色[未选中颜色、选中颜色]
        newDeviceTabLayout.setTabTextColors(CommonUtils.getColor(mContext!!, R.color.color999999),
                CommonUtils.getColor(mContext!!, R.color.color333333))
        // 设置下划线跟文本宽度一致
        newDeviceTabLayout.isTabIndicatorFullWidth = true
        fragments.add(ScanCodeAddDeviceFragment.newInstance())
        fragments.add(InputCodeAddDeviceFragment.newInstance())
        newDeviceViewPager.apply {
            adapter = FragmentLazyStateAdapterViewPager2(
                    this@AddNewDeviceActivity, fragments = fragments)
        }
        TabLayoutMediator(newDeviceTabLayout, newDeviceViewPager) { tab, position ->
            tab.text = tabTitleList[position]
        }.attach()
    }

    override fun initLogic() {

    }

    override fun showToolBar(): Boolean {
        return true
    }

    override fun openEventBus(): Boolean {
        return false
    }
}