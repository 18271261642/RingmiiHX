package com.guider.gps.view.activity

import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.guider.baselib.base.BaseActivity
import com.guider.baselib.utils.CommonUtils.logOutClearMMKV
import com.guider.baselib.utils.IS_FIRST_START
import com.guider.baselib.utils.MMKVUtil
import com.guider.baselib.utils.USER
import com.guider.baselib.widget.viewpageradapter.FragmentLazyStateAdapterViewPager2
import com.guider.gps.R
import com.guider.gps.view.fragment.GuideFragment
import kotlinx.android.synthetic.main.activity_guide.*

/**
 * @Package:        com.guider.gps.view.activity
 * @ClassName:      GuideActivity
 * @Description:    引导页
 * @Author:         hjr
 * @CreateDate:     2020/8/27 16:34
 * Copyright (C), 1998-2020, GuiderTechnology
 */
class GuideActivity : BaseActivity() {

    override val contentViewResId: Int
        get() = R.layout.activity_guide

    private val tabList = arrayListOf("page1", "page2", "page3")
    var flag = false

    override fun openEventBus(): Boolean {
        return false
    }

    override fun initImmersion() {
        whiteStatusBarBlackFont()
    }

    override fun initView() {
        val generateTextFragments = generateFragments(tabList)
        guideViewPager2.apply {
            adapter = FragmentLazyStateAdapterViewPager2(
                    this@GuideActivity, generateTextFragments)
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrollStateChanged(state: Int) {
                    when (state) {
                        ViewPager2.SCROLL_STATE_DRAGGING -> {
                            flag = false
                        }
                        ViewPager2.SCROLL_STATE_SETTLING -> {
                            flag = true
                        }
                        ViewPager2.SCROLL_STATE_IDLE -> {
                            if (guideViewPager2.currentItem == generateTextFragments.size - 1
                                    && !flag) {
                                fromGuideEntryApp()
                            }
                        }
                    }
                }
            })
        }
    }

    fun fromGuideEntryApp() {
        MMKVUtil.saveBoolean(IS_FIRST_START, true)
        dealEnterPageEvent()
    }

    override fun initLogic() {
        if (MMKVUtil.containKey(IS_FIRST_START) && MMKVUtil.getBoolean(IS_FIRST_START)) {
            dealEnterPageEvent()
        }
    }

    private fun dealEnterPageEvent() {
        if (MMKVUtil.getInt(USER.USERID, 0) != 0) {
            //已经登录过
            //判断是否有绑定的设备
            if (!MMKVUtil.containKey(USER.OWN_BIND_DEVICE_CODE)) {
//                val intent = Intent(mContext!!, AddNewDeviceActivity::class.java)
//                intent.putExtra("type", "mine")
//                startActivity(intent)
                logOutClearMMKV()
                startActivity(Intent(this, LoginActivity::class.java))
            } else startActivity(Intent(this, MainActivity::class.java))
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish()
    }

    override fun showToolBar(): Boolean {
        return false
    }

    /**
     * 构建多个TextFragment
     */
    private fun generateFragments(tabTitleList: ArrayList<String>) =
            mutableListOf<Fragment>().apply {
                tabTitleList.forEach {
                    add(GuideFragment.newInstance(it))
                }
            }
}