package com.guider.gps.view.fragment

import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.guider.baselib.base.BaseFragment
import com.guider.baselib.utils.CommonUtils
import com.guider.baselib.widget.viewpageradapter.FragmentLazyStateAdapterViewPager2
import com.guider.gps.R
import kotlinx.android.synthetic.main.fragment_medicine.*

class MedicineFragment : BaseFragment() {

    override val layoutRes: Int
        get() = R.layout.fragment_medicine

    private var tabTitleList = arrayListOf<String>()

    override fun initView(rootView: View) {

    }

    override fun initLogic() {
        tabTitleList = arrayListOf(
                resources.getString(R.string.app_main_health_blood_sugar),
                resources.getString(R.string.app_main_health_blood_pressure),
                resources.getString(R.string.app_main_health_blood_oxygen))
        medicineTabLayout.tabMode = TabLayout.MODE_FIXED
        // 设置选中下划线颜色
        medicineTabLayout.setSelectedTabIndicatorColor(
                CommonUtils.getColor(mActivity, R.color.colorF18937))
        // 设置文本字体颜色[未选中颜色、选中颜色]
        medicineTabLayout.setTabTextColors(CommonUtils.getColor(mActivity, R.color.color999999),
                CommonUtils.getColor(mActivity, R.color.color333333))
        // 设置下划线跟文本宽度一致
        medicineTabLayout.isTabIndicatorFullWidth = true
        val generateTextFragments = generateTextFragments(tabTitleList)
        medicineViewPager.apply {
            adapter = FragmentLazyStateAdapterViewPager2(mActivity, generateTextFragments)
        }
        TabLayoutMediator(medicineTabLayout, medicineViewPager) { tab, position ->
            tab.text = tabTitleList[position]
        }.attach()
    }

    /**
     * 构建多个TextFragment
     */
    private fun generateTextFragments(tabTitleList: ArrayList<String>) =
            mutableListOf<Fragment>().apply {
                tabTitleList.forEach {
                    add(MedicineDataFragment.newInstance(it))
                }
            }
}