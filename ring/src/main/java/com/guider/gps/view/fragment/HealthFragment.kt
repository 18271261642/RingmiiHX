package com.guider.gps.view.fragment

import android.annotation.SuppressLint
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.tabs.TabLayout
import com.guider.baselib.base.BaseFragment
import com.guider.baselib.utils.CommonUtils
import com.guider.baselib.utils.MMKVUtil
import com.guider.baselib.utils.TARGET_STEP
import com.guider.gps.R
import com.guider.health.apilib.ApiCallBack
import com.guider.health.apilib.ApiUtil
import com.guider.health.apilib.IUserHDApi
import kotlinx.android.synthetic.main.fragment_home_health.*
import retrofit2.Call
import retrofit2.Response

class HealthFragment : BaseFragment() {

    override val layoutRes: Int
        get() = R.layout.fragment_home_health

    private var tabTitleList = arrayListOf<String>()

    private var startTimeValue = ""
    private var endTimeValue = ""

    override fun initView(rootView: View) {
    }

    @SuppressLint("SetTextI18n")
    override fun initLogic() {
        tabTitleList = arrayListOf(
                resources.getString(R.string.app_main_health_today),
                resources.getString(R.string.app_main_health_yesterday),
                resources.getString(R.string.app_main_health_before_yesterday))
        val targetSteps = if (MMKVUtil.getInt(TARGET_STEP, 0) != 0) {
            String.format(
                    resources.getString(R.string.app_main_health_target_step),
                    MMKVUtil.getInt(TARGET_STEP, 0))
        } else String.format(
                resources.getString(R.string.app_main_health_target_step),
                8000)
        targetStepTv.text = targetSteps
        healthTabLayout.tabMode = TabLayout.MODE_FIXED
        // 设置选中下划线颜色
        healthTabLayout.setSelectedTabIndicatorColor(
                CommonUtils.getColor(mActivity, R.color.colorF18937))
        // 设置文本字体颜色[未选中颜色、选中颜色]
        healthTabLayout.setTabTextColors(CommonUtils.getColor(mActivity, R.color.color999999),
                CommonUtils.getColor(mActivity, R.color.color333333))
        // 设置下划线跟文本宽度一致
        healthTabLayout.isTabIndicatorFullWidth = true
        val generateTextFragments = generateTextFragments(tabTitleList)
//        healthViewPager.apply {
//            adapter = FragmentLazyStateAdapterViewPager2(mActivity, generateTextFragments)
//        }
        val fragmentNew = HealthDataFragment.newInstance("Fragment")
        val fragmentManager: FragmentManager? = mActivity.supportFragmentManager
        val transaction: FragmentTransaction? = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.healthViewPager, fragmentNew)
        transaction?.commitAllowingStateLoss()
        if (healthTabLayout.tabCount == 0) {
            tabTitleList.forEach {
                healthTabLayout.addTab(healthTabLayout.newTab().setText(it))
            }
            healthTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

                override fun onTabSelected(tab: TabLayout.Tab?) {

                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {

                }

                override fun onTabReselected(tab: TabLayout.Tab?) {

                }

            })
        }
//        TabLayoutMediator(healthTabLayout, healthViewPager) { tab, position ->
//            tab.text = tabTitleList[position]
//        }.attach()
        getHealthData()
    }

    private fun getHealthData() {
        getStepData()
        getHealthChartData()
    }

    private fun getHealthChartData() {
        getBloodData()
        getTempData()
        getHeartData()
        getSleepData()
        getSportData()
    }

    private fun getSportData() {

    }

    private fun getSleepData() {

    }

    private fun getHeartData() {

    }

    private fun getTempData() {

    }

    private fun getBloodData() {

    }

    private fun getStepData() {
        mActivity.showDialog()
        ApiUtil.createHDApi(IUserHDApi::class.java)
                .getHomeStep()
                .enqueue(object : ApiCallBack<String>() {
                    override fun onApiResponse(call: Call<String>?, response: Response<String>?) {
                        if (response?.body() != null) {
                            stepNumTv.text = response.body()
                        }
                    }

                    override fun onRequestFinish() {
                        mActivity.dismissDialog()
                    }
                })
    }

    /**
     * 构建多个TextFragment
     */
    private fun generateTextFragments(tabTitleList: ArrayList<String>) =
            mutableListOf<Fragment>().apply {
                tabTitleList.forEach {
//                    add()
                }
            }
}