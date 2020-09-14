package com.guider.gps.view.activity

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.guider.baselib.base.BaseActivity
import com.guider.baselib.utils.*
import com.guider.baselib.widget.BadgeView
import com.guider.baselib.widget.viewpageradapter.FragmentLazyStateAdapterViewPager2
import com.guider.gps.R
import com.guider.gps.view.fragment.RingMsgListFragment
import com.guider.health.apilib.ApiCallBack
import com.guider.health.apilib.ApiUtil
import com.guider.health.apilib.IUserHDApi
import kotlinx.android.synthetic.main.activity_msg_list.*
import retrofit2.Call
import retrofit2.Response

class RingMsgListActivity : BaseActivity() {

    private var tabTitles = arrayListOf<String>()
    private val mBadgeCountList = arrayListOf<Int>()
    private var abnormalMsgUndoNum = 0
    private var careMsgUndoNum = 0

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
        }
    }

    override fun initView() {
        tabTitles = arrayListOf(
                resources.getString(R.string.app_msg_error_notify),
                resources.getString(R.string.app_msg_care_info),
                resources.getString(R.string.app_msg_system_info)
        )
        msgTabLayout.tabMode = TabLayout.MODE_FIXED
        // 设置选中下划线颜色
        msgTabLayout.setSelectedTabIndicatorColor(
                CommonUtils.getColor(mContext!!, R.color.colorF18937))
        // 设置文本字体颜色[未选中颜色、选中颜色]
        msgTabLayout.setTabTextColors(CommonUtils.getColor(mContext!!, R.color.color999999),
                CommonUtils.getColor(mContext!!, R.color.color333333))
        // 设置下划线跟文本宽度一致
        msgTabLayout.isTabIndicatorFullWidth = true
    }

    override fun initLogic() {
        msgViewpager.apply {
            adapter = FragmentLazyStateAdapterViewPager2(
                    this@RingMsgListActivity,
                    fragments = generateTextFragments(tabTitles))
        }
        TabLayoutMediator(msgTabLayout, msgViewpager) { _, _ ->
        }.attach()
        mBadgeCountList.add(abnormalMsgUndoNum)
        mBadgeCountList.add(careMsgUndoNum)
        mBadgeCountList.add(0)
        setUpTabBadge()
    }

    fun getCareMsgUndoNum() {
        val accountId = MMKVUtil.getInt(USER.USERID)
        ApiUtil.createHDApi(IUserHDApi::class.java, false)
                .getCareMsgUndo(accountId)
                .enqueue(object : ApiCallBack<String>(mContext) {
                    override fun onApiResponse(call: Call<String>?, response: Response<String>?) {
                        if (response?.body() != null) {
                            mBadgeCountList[1] = response.body()!!.toInt()
                            updateCustomTab(1)
                        }
                    }
                })
    }

    fun getAbnormalMsgUndoNum() {
        val accountId = MMKVUtil.getInt(USER.USERID)
        ApiUtil.createHDApi(IUserHDApi::class.java, false)
                .getAbnormalMsgUndo(accountId)
                .enqueue(object : ApiCallBack<String>(mContext) {
                    override fun onApiResponse(call: Call<String>?, response: Response<String>?) {
                        if (response?.body() != null) {
                            mBadgeCountList[0] = response.body()!!.toInt()
                            updateCustomTab(0)
                            if (mBadgeCountList[0] != 0)
                                msgTabLayout.postDelayed({
                                    resetAbnormalMsgUnReadStatus()
                                }, 3000)

                        }
                    }
                })
    }

    fun resetCareMsgUnReadStatus(id: Int) {
        if (mBadgeCountList[1] <= 0) return
        val accountId = MMKVUtil.getInt(USER.USERID)
        ApiUtil.createHDApi(IUserHDApi::class.java, false)
                .resetCareMsgReadStatus(accountId, id)
                .enqueue(object : ApiCallBack<Any>(mContext) {
                    override fun onApiResponse(call: Call<Any>?, response: Response<Any>?) {
                        if (response?.body() != null) {
                            mBadgeCountList[1]--
                            updateCustomTab(1)
                        }
                    }
                })
    }

    private fun resetAbnormalMsgUnReadStatus() {
        val accountId = MMKVUtil.getInt(USER.USERID)
        ApiUtil.createHDApi(IUserHDApi::class.java, false)
                .resetAbnormalMsgReadStatus(accountId)
                .enqueue(object : ApiCallBack<Any>(mContext) {
                    override fun onApiResponse(call: Call<Any>?, response: Response<Any>?) {
                        if (response?.body() != null) {
                            mBadgeCountList[0] = 0
                            updateCustomTab(0)
                        }
                    }
                })
    }

    private fun setUpTabBadge() {
        for (i in generateTextFragments(tabTitles).indices) {
            updateCustomTab(i, false)
        }
        // 需加上以下代码,不然会出现更新Tab角标后,选中的Tab字体颜色不是选中状态的颜色
        msgTabLayout.getTabAt(msgTabLayout.selectedTabPosition)?.customView?.isSelected = true
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

    @SuppressLint("InflateParams")
    private fun getTabItemView(position: Int): View? {
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.tab_layout_item, null)
        val textView = view.findViewById<View>(R.id.textview) as TextView
        textView.text = tabTitles[position]
        val target = view.findViewById<View>(R.id.badgeview_target)
        val badgeView = BadgeView(mContext)
        badgeView.setTargetView(target)
        badgeView.setBadgeMargin(0, 6,
                10, 0)
        badgeView.text = formatBadgeNumber(mBadgeCountList[position])
        return view
    }

    private fun updateCustomTab(tabPosition: Int, isSetSelect: Boolean = true) {
        val tab = msgTabLayout.getTabAt(tabPosition)!!
        // 更新Badge前,先remove原来的customView,否则Badge无法更新
        val customView = tab.customView
        if (customView != null) {
            val parent = customView.parent
            if (parent != null) {
                (parent as ViewGroup).removeView(customView)
            }
        }
        // 更新CustomView
        tab.customView = getTabItemView(tabPosition)
        // 需加上以下代码,不然会出现更新Tab角标后,选中的Tab字体颜色不是选中状态的颜色
        if (isSetSelect) msgTabLayout.getTabAt(
                msgTabLayout.selectedTabPosition)?.customView?.isSelected = true
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
            // equivalent to String#valueOf(int);
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