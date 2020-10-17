package com.guider.gps.view.activity

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.guider.baselib.base.BaseActivity
import com.guider.baselib.utils.*
import com.guider.baselib.widget.viewpageradapter.FragmentLazyStateAdapterViewPager2
import com.guider.gps.R
import com.guider.gps.view.fragment.RingMsgListFragment
import com.guider.health.apilib.GuiderApiUtil
import kotlinx.android.synthetic.main.activity_msg_list.*
import kotlinx.coroutines.launch
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgeAnchor
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgePagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgeRule

class RingMsgListActivity : BaseActivity() {

    private var tabTitles = arrayListOf<String>()
    private val mBadgeCountList = arrayListOf<Int>()
    private var abnormalMsgUndoNum = 0
    private var careMsgUndoNum = 0

    //进入页面需跳转到的指定列表项
    private var entryPageIndex = 0
    private var badgeTextView1: TextView? = null
    private var badgeTextView2: TextView? = null
    private var badgeTextView3: TextView? = null

    override val contentViewResId: Int
        get() = R.layout.activity_msg_list

    override fun initImmersion() {
        setTitle(resources.getString(R.string.app_msg))
        showBackButton(R.drawable.ic_back_white)
        if (intent != null) {
            if (intent.getIntExtra("abnormalMsgUndoNum", 0) != 0) {
                abnormalMsgUndoNum = intent.getIntExtra("abnormalMsgUndoNum", 0)
            }
            if (intent.getIntExtra("careMsgUndoNum", 0) != 0) {
                careMsgUndoNum = intent.getIntExtra("careMsgUndoNum", 0)
            }
            if (intent.getIntExtra("entryPageIndex", 0) != 0) {
                entryPageIndex = intent.getIntExtra("entryPageIndex", 0)
            }
        }
    }

    override fun initView() {
        tabTitles = arrayListOf(
                resources.getString(R.string.app_msg_error_notify),
                resources.getString(R.string.app_msg_care_info),
                resources.getString(R.string.app_msg_system_info)
        )
    }

    override fun initLogic() {
        msgViewpager.apply {
            adapter = FragmentLazyStateAdapterViewPager2(
                    this@RingMsgListActivity,
                    fragments = generateTextFragments(tabTitles))
        }
        initMagicIndicator1()
        if (entryPageIndex != 0) msgViewpager.currentItem = entryPageIndex
        mBadgeCountList.add(abnormalMsgUndoNum)
        mBadgeCountList.add(careMsgUndoNum)
        mBadgeCountList.add(0)
        updateCustomTab(0)
        updateCustomTab(1)
        updateCustomTab(2)
        if (mBadgeCountList[0] != 0)
            msgTabLayout.postDelayed({
                resetAbnormalMsgUnReadStatus()
            }, 3000)
    }

    fun resetCareNum() {
        if (mBadgeCountList[1] != 0)
            msgTabLayout.postDelayed({
                resetCareMsgUnReadStatus()
            }, 3000)
    }

    fun getCareMsgUndoNum() {
        val accountId = MMKVUtil.getInt(USER.USERID)
        lifecycleScope.launch {
            try {
                val resultBean = GuiderApiUtil.getHDApiService().getCareMsgUndo(accountId)
                if (resultBean != null) {
                    mBadgeCountList[1] = resultBean.toInt()
                    updateCustomTab(1)
                    if (mBadgeCountList[1] != 0)
                        msgTabLayout.postDelayed({
                            resetCareMsgUnReadStatus()
                        }, 3000)
                }
            } catch (e: Exception) {
                toastShort(e.message!!)
            }
        }
    }

    fun getAbnormalMsgUndoNum() {
        val accountId = MMKVUtil.getInt(USER.USERID)
        lifecycleScope.launch {
            try {
                val resultBean = GuiderApiUtil.getHDApiService().getAbnormalMsgUndo(accountId)
                if (resultBean != null) {
                    mBadgeCountList[0] = resultBean.toInt()
                    updateCustomTab(0)
                    if (mBadgeCountList[0] != 0)
                        msgTabLayout.postDelayed({
                            resetAbnormalMsgUnReadStatus()
                        }, 3000)
                }
            } catch (e: Exception) {
                toastShort(e.message!!)
            }
        }
    }

    private fun resetCareMsgUnReadStatus() {
        if (mBadgeCountList[1] <= 0) return
        val accountId = MMKVUtil.getInt(USER.USERID)
        lifecycleScope.launch {
            try {
                val resultBean = GuiderApiUtil.getHDApiService().resetCareMsgReadStatus(accountId)
                if (resultBean == "true") {
                    mBadgeCountList[1] = 0
                    updateCustomTab(1)
                }
            } catch (e: Exception) {
                toastShort(e.message!!)
            }
        }

    }

    private fun resetAbnormalMsgUnReadStatus() {
        if (mBadgeCountList[0] <= 0) return
        val accountId = MMKVUtil.getInt(USER.USERID)
        lifecycleScope.launch {
            try {
                val resultBean = GuiderApiUtil.getHDApiService()
                        .resetAbnormalMsgReadStatus(accountId)
                if (resultBean != null) {
                    mBadgeCountList[0] = 0
                    updateCustomTab(0)
                }
            } catch (e: Exception) {
                toastShort(e.message!!)
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initMagicIndicator1() {
        val commonNavigator = CommonNavigator(this)
        commonNavigator.isAdjustMode = true
        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getCount(): Int {
                return tabTitles.size
            }

            @SuppressLint("InflateParams")
            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val badgePagerTitleView = BadgePagerTitleView(context)
                val simplePagerTitleView: SimplePagerTitleView = ColorTransitionPagerTitleView(context)
                simplePagerTitleView.text = tabTitles[index]
                simplePagerTitleView.textSize = 15f
                simplePagerTitleView.normalColor = CommonUtils.getColor(
                        mContext!!, R.color.color999999)
                simplePagerTitleView.selectedColor = CommonUtils.getColor(
                        mContext!!, R.color.color333333)
                simplePagerTitleView.setOnClickListener {
                    msgViewpager.currentItem = index
                }
                badgePagerTitleView.innerPagerTitleView = simplePagerTitleView

                // setup badge
                when (index) {
                    1 -> {
                        badgeTextView2 = LayoutInflater.from(context).inflate(
                                R.layout.simple_count_badge_layout, null) as TextView
                        badgePagerTitleView.badgeView = badgeTextView2
                    }
                    2 -> {
                        badgeTextView3 = LayoutInflater.from(context).inflate(
                                R.layout.simple_count_badge_layout, null) as TextView
                        badgePagerTitleView.badgeView = badgeTextView3
                    }
                    else -> {
                        badgeTextView1 = LayoutInflater.from(context).inflate(
                                R.layout.simple_count_badge_layout, null) as TextView
                        badgePagerTitleView.badgeView = badgeTextView1
                    }
                }

                // set badge position
                badgePagerTitleView.xBadgeRule = BadgeRule(BadgeAnchor.CONTENT_RIGHT,
                        +UIUtil.dip2px(context, 2.0))
                badgePagerTitleView.yBadgeRule = BadgeRule(BadgeAnchor.CONTENT_TOP,
                        -UIUtil.dip2px(context, 6.0))

                // don't cancel badge when tab selected
                badgePagerTitleView.isAutoCancelBadge = false
                return badgePagerTitleView
            }

            override fun getIndicator(context: Context): IPagerIndicator {
                val indicator = LinePagerIndicator(context)
                indicator.setColors(CommonUtils.getColor(
                        mContext!!, R.color.colorF18937))
                return indicator
            }
        }
        msgTabLayout.navigator = commonNavigator
        val titleContainer = commonNavigator.titleContainer // must after setNavigator
        titleContainer.showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
        titleContainer.dividerPadding = UIUtil.dip2px(this, 6.0)
        titleContainer.dividerDrawable = CommonUtils.getDrawable(
                mContext!!, R.drawable.simple_splitter)
        msgViewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float,
                                        positionOffsetPixels: Int) {
                msgTabLayout.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                msgTabLayout.onPageSelected(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                msgTabLayout.onPageScrollStateChanged(state)
            }
        })
    }

    /**
     * 构建多个TextFragment
     */
    private fun generateTextFragments(tabTitleList: ArrayList<String>) =
            mutableListOf<Fragment>().apply {
                tabTitleList.forEach {
                    add(RingMsgListFragment.newInstance(it))
                }
            }

    private fun updateCustomTab(tabPosition: Int) {
        when (tabPosition) {
            0 -> {
                if (mBadgeCountList[tabPosition] == 0) {
                    badgeTextView1?.visibility = View.INVISIBLE
                } else {
                    badgeTextView1?.visibility = View.VISIBLE
                    badgeTextView1?.text = formatBadgeNumber(mBadgeCountList[tabPosition])
                }
            }
            1 -> {
                if (mBadgeCountList[tabPosition] == 0) {
                    badgeTextView2?.visibility = View.INVISIBLE
                } else {
                    badgeTextView2?.visibility = View.VISIBLE
                    badgeTextView2?.text = formatBadgeNumber(mBadgeCountList[tabPosition])
                }
            }
            2 -> {
                if (mBadgeCountList[tabPosition] == 0) {
                    badgeTextView3?.visibility = View.INVISIBLE
                } else {
                    badgeTextView3?.visibility = View.VISIBLE
                    badgeTextView3?.text = formatBadgeNumber(mBadgeCountList[tabPosition])
                }
            }
        }
        //刷新主页的消息红点
        EventBusUtils.sendEvent(EventBusEvent(
                EventBusAction.REFRESH_RIGHT_RED_POINT,
                "${mBadgeCountList[0]}&${mBadgeCountList[1]}"
        ))
    }

    private fun formatBadgeNumber(value: Int): String? {
        if (value <= 0) {
            return null
        }
        return if (value < 100) {
            value.toString()
        } else "99+"
    }

    override fun showToolBar(): Boolean {
        return true
    }

    override fun openEventBus(): Boolean {
        return false
    }
}