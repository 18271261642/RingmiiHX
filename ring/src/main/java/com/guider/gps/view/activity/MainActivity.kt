package com.guider.gps.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.guider.baselib.base.BaseActivity
import com.guider.baselib.utils.*
import com.guider.baselib.widget.dialog.DialogHolder
import com.guider.feifeia3.utils.MMKVUtil
import com.guider.gps.R
import com.guider.gps.adapter.HomeLeftDrawMsgAdapter
import com.guider.gps.bean.WithSelectBaseBean
import com.guider.gps.view.fragment.HealthFragment
import com.guider.gps.view.fragment.LocationFragment
import com.guider.gps.view.fragment.MedicineFragment
import com.guider.gps.view.fragment.MineFragment
import kotlinx.android.synthetic.main.activity_home_draw_layout.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() {

    override val contentViewResId: Int
        get() = R.layout.activity_main

    // Fragment 集合
    private val fragments = arrayListOf<Fragment>()
    private var exitTime: Long = 0
    private lateinit var drawAdapter: HomeLeftDrawMsgAdapter
    private var homeMsgList = arrayListOf<WithSelectBaseBean>()
    private var pageTitle = ""
    private var deviceName = ""

    override fun initImmersion() {
        showBackButton(R.drawable.icon_home_left_menu, this)
        setRightImage2(R.drawable.ic_home_msg, this)
    }

    override fun initView() {
        //按年月日输出当前日期
        val currentDayValue = DateUtil.localNowStringByPattern(TIME_FORMAT_PATTERN4)
        pageTitle = MMKVUtil.getString(CURRENT_DEVICE_NAME)
        if (StringUtils.isEmpty(pageTitle)) {
            pageTitle = currentDayValue
        }
        setTitle(currentDayValue, CommonUtils.getColor(mContext!!, R.color.white))
        homeViewPager.isUserInputEnabled = false //true:滑动，false：禁止滑动
        adjustNavigationIcoSize(bottomNavigationView)
        bottomNavigationView.itemIconTintList = null
        msgListRv.layoutManager = LinearLayoutManager(this)
    }

    override fun initLogic() {
        fragments.add(HealthFragment())
        fragments.add(LocationFragment())
        fragments.add(MedicineFragment())
        fragments.add(MineFragment())
        setNavigationListener()
        homeViewPager.apply {
            adapter = object : MyProgramFragmentAdapter(this@MainActivity) {
                override fun createFragment(position: Int): Fragment {
                    return fragments[position]
                }

            }
        }
        setViewPagerListener()
        addDeviceLayout.setOnClickListener(this)
        homeMsgList.add(WithSelectBaseBean(
                resources.getString(R.string.app_main_left_msg_device_name1), true))
        homeMsgList.add(WithSelectBaseBean(
                resources.getString(R.string.app_main_left_msg_device_name2)))
        homeMsgList.add(WithSelectBaseBean(
                resources.getString(R.string.app_main_left_msg_device_name3)))
        drawAdapter = HomeLeftDrawMsgAdapter(mContext!!, homeMsgList)
        MMKVUtil.saveString(CURRENT_DEVICE_NAME,
                resources.getString(R.string.app_main_left_msg_device_name1))
        drawAdapter.setListener(object : AdapterOnItemClickListener {
            @SuppressLint("WrongConstant")
            override fun onClickItem(position: Int) {
                toastShort(resources.getString(R.string.app_main_bind_device))
                homeDrawLayout.closeDrawer(Gravity.START)
                deviceName = homeMsgList[position].name
                MMKVUtil.saveString(CURRENT_DEVICE_NAME, deviceName)
                if (homeViewPager.currentItem != 3)
                    setTitle(deviceName)
            }

        })
        drawAdapter.setLongClickListener(object :
                HomeLeftDrawMsgAdapter.AdapterOnItemLongClickListener {
            override fun onClickItem(position: Int) {
                unBindDialogShow(position)
            }

        })
        msgListRv.adapter = drawAdapter
    }

    private fun unBindDialogShow(position: Int) {
        val dialog = object : DialogHolder(this,
                R.layout.dialog_mine_unbind, Gravity.CENTER) {
            @SuppressLint("SetTextI18n")
            override fun bindView(dialogView: View) {
                val cancel = dialogView.findViewById<ConstraintLayout>(R.id.cancelLayout)
                cancel.setOnClickListener {
                    dialog?.dismiss()
                }
                val unBindContentTv = dialogView.findViewById<TextView>(R.id.unBindContentTv)
                val unbindValue = String.format(
                        resources.getString(R.string.app_main_unbind_device),
                        homeMsgList[position].name)
                unBindContentTv.text = unbindValue
                val confirm = dialogView.findViewById<ConstraintLayout>(R.id.confirmLayout)
                confirm.setOnClickListener {
                    unbindDeviceEvent(position)
                    dialog?.dismiss()
                    toastShort(resources.getString(R.string.app_main_unbind_success))
                }
            }
        }
        dialog.initView()
        dialog.show(true)
    }

    fun unbindDeviceFromMineFragment(name: String) {
        var devicePosition = 0
        for (i in 0 until homeMsgList.size) {
            if (homeMsgList[i].name == name) {
                devicePosition = i
            }
        }
        when (devicePosition) {
            0 -> {
                deviceName = homeMsgList[devicePosition + 1].name
                MMKVUtil.saveString(CURRENT_DEVICE_NAME, deviceName)
            }
            else -> {
                deviceName = homeMsgList[devicePosition - 1].name
                MMKVUtil.saveString(CURRENT_DEVICE_NAME, deviceName)
            }
        }
        homeMsgList.removeAt(devicePosition)
        drawAdapter.notifyDataSetChanged()
    }

    private fun unbindDeviceEvent(position: Int) {
        if (homeMsgList.size == 1) {
            deviceName = ""
            MMKVUtil.clearByKey(CURRENT_DEVICE_NAME)
            if (homeViewPager.currentItem != 3) {
                val currentDayValue =
                        DateUtil.localNowStringByPattern(TIME_FORMAT_PATTERN4)
                setTitle(currentDayValue)
            }
        } else {
            when (position) {
                0 -> {
                    deviceName = homeMsgList[position + 1].name
                    MMKVUtil.saveString(CURRENT_DEVICE_NAME, deviceName)
                    if (homeViewPager.currentItem != 3)
                        setTitle(deviceName)
                }
                else -> {
                    deviceName = homeMsgList[position - 1].name
                    MMKVUtil.saveString(CURRENT_DEVICE_NAME, deviceName)
                    if (homeViewPager.currentItem != 3)
                        setTitle(deviceName)
                }
            }
        }
        homeMsgList.removeAt(position)
        drawAdapter.notifyItemRemoved(position)
    }

    @SuppressLint("WrongConstant")
    override fun onNoDoubleClick(v: View) {
        when (v) {
            iv_left -> {
                homeDrawLayout.openDrawer(Gravity.START)
            }
            addDeviceLayout -> {
                startActivity(Intent(mContext, AddNewDeviceActivity::class.java))
            }
            iv_toolbar_right2 -> {
                startActivity(Intent(mContext, RingMsgListActivity::class.java))
            }
        }
    }

    @SuppressLint("WrongConstant")
    override fun onBackPressed() {
        super.onBackPressed()
        if (homeDrawLayout.isDrawerOpen(Gravity.START)) {
            homeDrawLayout.closeDrawer(Gravity.START)
        }
        if (System.currentTimeMillis() - exitTime > 2000) {
            Toast.makeText(this, resources.getString(R.string.app_exit_hint),
                    Toast.LENGTH_SHORT).show()
            exitTime = System.currentTimeMillis()
        } else {
            val launcherIntent = Intent(Intent.ACTION_MAIN)
            launcherIntent.addCategory(Intent.CATEGORY_HOME)
            startActivity(launcherIntent)
        }
    }

    private fun setViewPagerListener() {
        homeViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                bottomNavigationView.menu.getItem(position).isChecked = true
            }
        })
    }

    private fun setNavigationListener() {
        bottomNavigationView.setOnNavigationItemSelectedListener {
            resetToDefaultIcon()//重置到默认不选中图片
            when (it.itemId) {
                R.id.menu_item_health -> {
                    it.setIcon(R.drawable.icon_home_bottom_health_select)
                    homeViewPager.currentItem = 0
                    deviceName = MMKVUtil.getString(CURRENT_DEVICE_NAME)
                    pageTitle = if (StringUtils.isEmpty(deviceName)) {
                        DateUtil.localNowStringByPattern(TIME_FORMAT_PATTERN4)
                    } else deviceName
                    setTitle(pageTitle)
                    setShowRightPoint(true)
                }
                R.id.menu_item_location -> {
                    it.setIcon(R.drawable.icon_home_bottom_location_select)
                    homeViewPager.currentItem = 1
                    deviceName = MMKVUtil.getString(CURRENT_DEVICE_NAME)
                    pageTitle = if (StringUtils.isEmpty(deviceName)) {
                        DateUtil.localNowStringByPattern(TIME_FORMAT_PATTERN4)
                    } else deviceName
                    setTitle(pageTitle)
                    setShowRightPoint(true)
                }
                R.id.menu_item_medicine -> {
                    it.setIcon(R.drawable.icon_home_bottom_medcine_select)
                    homeViewPager.currentItem = 2
                    deviceName = MMKVUtil.getString(CURRENT_DEVICE_NAME)
                    pageTitle = if (StringUtils.isEmpty(deviceName)) {
                        DateUtil.localNowStringByPattern(TIME_FORMAT_PATTERN4)
                    } else deviceName
                    setTitle(pageTitle)
                    setShowRightPoint(false)
                }
                R.id.menu_item_mine -> {
                    it.setIcon(R.drawable.icon_home_bottom_mine_select)
                    homeViewPager.currentItem = 3
                    setTitle("我的")
                    setShowRightPoint(true)
                }
            }
            true
        }
    }

    private fun resetToDefaultIcon() {
        val health: MenuItem = bottomNavigationView.menu.findItem(R.id.menu_item_health)
        health.setIcon(R.drawable.icon_home_bottom_health)
        val location: MenuItem = bottomNavigationView.menu.findItem(R.id.menu_item_location)
        location.setIcon(R.drawable.icon_home_bottom_location)
        val medcine: MenuItem = bottomNavigationView.menu.findItem(R.id.menu_item_medicine)
        medcine.setIcon(R.drawable.icon_home_bottom_medcine)
        val mine: MenuItem = bottomNavigationView.menu.findItem(R.id.menu_item_mine)
        mine.setIcon(R.drawable.icon_home_bottom_mine)
    }


    private fun adjustNavigationIcoSize(navigation: BottomNavigationView) {
        val menuView = navigation.getChildAt(0) as BottomNavigationMenuView
        for (i in 0 until menuView.childCount) {
            val iconView: View = menuView.getChildAt(i).findViewById(R.id.icon)
            val layoutParams: ViewGroup.LayoutParams = iconView.layoutParams
            val displayMetrics = resources.displayMetrics
            layoutParams.height = ScreenUtils.dip2px(mContext, 30f)
            layoutParams.width = ScreenUtils.dip2px(mContext, 46f)
            iconView.layoutParams = layoutParams
        }
    }

    override fun showToolBar(): Boolean {
        return true
    }

    override fun openEventBus(): Boolean {
        return false
    }

    abstract class MyProgramFragmentAdapter(fragmentActivity: FragmentActivity)
        : FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int {
            return 4
        }
    }
}