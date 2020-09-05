package com.guider.gps.view.activity

import android.annotation.SuppressLint
import android.app.Activity
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
import com.guider.gps.R
import com.guider.gps.adapter.HomeLeftDrawMsgAdapter
import com.guider.gps.view.fragment.HealthFragment
import com.guider.gps.view.fragment.LocationFragment
import com.guider.gps.view.fragment.MedicineFragment
import com.guider.gps.view.fragment.MineFragment
import com.guider.health.apilib.ApiCallBack
import com.guider.health.apilib.ApiUtil
import com.guider.health.apilib.IGuiderApi
import com.guider.health.apilib.bean.CheckBindDeviceBean
import com.guider.health.apilib.bean.UserInfo
import kotlinx.android.synthetic.main.activity_home_draw_layout.*
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import retrofit2.Call
import retrofit2.Response


class MainActivity : BaseActivity() {

    override val contentViewResId: Int
        get() = R.layout.activity_main

    // Fragment 集合
    private val fragments = arrayListOf<Fragment>()
    private var exitTime: Long = 0
    private lateinit var drawAdapter: HomeLeftDrawMsgAdapter
    private var bindDeviceList = arrayListOf<UserInfo>()
    private var pageTitle = ""
    private var deviceName = ""
    private var bindListBean: CheckBindDeviceBean? = null

    override fun initImmersion() {
        showBackButton(R.drawable.icon_home_left_menu, this)
        setRightImage2(R.drawable.ic_home_msg, this)
        if (intent != null) {
            if (intent.getParcelableExtra<CheckBindDeviceBean?>("bindListBean") != null) {
                bindListBean = intent.getParcelableExtra("bindListBean")
            }
        }
    }

    override fun initView() {
        //按年月日输出当前日期
        val currentDayValue = DateUtil.localNowStringByPattern(TIME_FORMAT_PATTERN4)
        pageTitle = MMKVUtil.getString(CURRENT_DEVICE_NAME)
        if (StringUtil.isEmpty(pageTitle)) {
            pageTitle = currentDayValue
        }
        setTitle(pageTitle, CommonUtils.getColor(mContext!!, R.color.white))
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
        drawAdapter = HomeLeftDrawMsgAdapter(mContext!!, bindDeviceList)
        drawAdapter.setListener(object : AdapterOnItemClickListener {
            @SuppressLint("WrongConstant")
            override fun onClickItem(position: Int) {
                toastShort(resources.getString(R.string.app_main_bind_device))
                homeDrawLayout.closeDrawer(Gravity.START)
                deviceName = bindDeviceList[position].name!!
                MMKVUtil.saveString(CURRENT_DEVICE_NAME, deviceName)
                //我的页面 顶部标题固定为我的其他页面为设备名称
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
        getBindDeviceList()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshBindDeviceList(event: EventBusEvent<CheckBindDeviceBean>) {
        if (event.code == EventBusAction.REFRESH_DEVICE_MEMBER_LIST) {
            if (event.data != null) {
                bindListBean = event.data as CheckBindDeviceBean
                getBindDeviceList()
            }
        }
    }

    private fun getBindDeviceList() {
        if (bindListBean != null && !bindListBean?.userInfos.isNullOrEmpty()) {
            bindDeviceList.clear()
            bindDeviceList.addAll(bindListBean?.userInfos!!)
            bindDeviceList[0].isSelected = 1
            drawAdapter.setSourceList(bindDeviceList)
        } else {
            showDialog()
            val accountId = MMKVUtil.getInt(USER.USERID, 0)
            ApiUtil.createApi(IGuiderApi::class.java, false)
                    .getGroupBindMember(accountId = accountId)
                    .enqueue(object : ApiCallBack<CheckBindDeviceBean>(mContext) {
                        override fun onApiResponse(call: Call<CheckBindDeviceBean>?,
                                                   response: Response<CheckBindDeviceBean>?) {
                            if (response?.body() != null) {
                                bindListBean = response.body()
                                MMKVUtil.saveString(BIND_DEVICE_ACCOUNT_ID, accountId.toString())
                                MMKVUtil.saveString(CURRENT_DEVICE_NAME,
                                        mContext!!.resources.getString(R.string.app_own_string))
                                bindListBean?.userInfos?.forEach {
                                    if (it.accountId == accountId) {
                                        it.relationShip = mContext!!.resources.getString(
                                                R.string.app_own_string)
                                    }
                                }
                                bindDeviceList.clear()
                                bindDeviceList.addAll(bindListBean?.userInfos!!)
                                bindDeviceList[0].isSelected = 1
                                drawAdapter.setSourceList(bindDeviceList)
                            }
                        }

                        override fun onRequestFinish() {
                            dismissDialog()
                        }
                    })
        }
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
                        bindDeviceList[position].name)
                unBindContentTv.text = unbindValue
                val confirm = dialogView.findViewById<ConstraintLayout>(R.id.confirmLayout)
                confirm.setOnClickListener {
                    unbindDeviceEvent(position)
                    dialog?.dismiss()
                }
            }
        }
        dialog.initView()
        dialog.show(true)
    }

    fun unbindDeviceFromMineFragment(name: String) {
        var devicePosition = 0
        for (i in 0 until bindDeviceList.size) {
            if (bindDeviceList[i].name == name) {
                devicePosition = i
            }
        }
        when (devicePosition) {
            0 -> {
                deviceName = bindDeviceList[devicePosition + 1].name!!
                MMKVUtil.saveString(CURRENT_DEVICE_NAME, deviceName)
            }
            else -> {
                deviceName = bindDeviceList[devicePosition - 1].name!!
                MMKVUtil.saveString(CURRENT_DEVICE_NAME, deviceName)
            }
        }
        bindDeviceList.removeAt(devicePosition)
        drawAdapter.notifyDataSetChanged()
    }

    private fun unbindDeviceEvent(position: Int) {
        //如果是解绑的是当前账户绑定的设备则需要去重新绑定
        if (bindDeviceList[position].name == mContext!!.resources.getString(R.string.app_own_string)) {
            showDialog()
            val accountId = bindDeviceList[position].accountId
            ApiUtil.createApi(IGuiderApi::class.java, false)
                    .unBindDevice(accountId, "")
                    .enqueue(object : ApiCallBack<Any?>(mContext) {
                        override fun onApiResponse(call: Call<Any?>?,
                                                   response: Response<Any?>?) {
                            if (response?.body() != null) {
                                MMKVUtil.clearByKey(BIND_DEVICE_ACCOUNT_ID)
                                val intent = Intent(mContext, AddNewDeviceActivity::class.java)
                                intent.putExtra("type", "mine")
                                startActivity(intent)
                                unBindDeviceAdapterShow(position)
                            }
                        }

                        override fun onRequestFinish() {
                            dismissDialog()
                        }
                    })
        } else {
            unBindGroupMemberEvent(position)
        }
    }

    private fun unBindGroupMemberEvent(position: Int) {
        if (bindListBean != null && bindListBean!!.userGroupId != 0.0) {
            showDialog()
            val groupId = bindListBean!!.userGroupId.toInt()
            val accountId = bindDeviceList[position].accountId
            ApiUtil.createApi(IGuiderApi::class.java, false)
                    .unBindGroupMember(groupId, accountId)
                    .enqueue(object : ApiCallBack<List<UserInfo>?>(mContext) {
                        override fun onApiResponse(call: Call<List<UserInfo>?>?,
                                                   response: Response<List<UserInfo>?>?) {
                            if (response?.body() != null) {
                                bindListBean!!.userGroupId = groupId.toDouble()
                                bindListBean!!.userInfos = response.body()
                                unBindDeviceAdapterShow(position)
                            }
                        }

                        override fun onRequestFinish() {
                            dismissDialog()
                        }
                    })
        }
    }

    private fun unBindDeviceAdapterShow(position: Int) {
        if (bindDeviceList.size == 1) {
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
                    deviceName = bindDeviceList[position + 1].name!!
                    MMKVUtil.saveString(CURRENT_DEVICE_NAME, deviceName)
                    if (homeViewPager.currentItem != 3)
                        setTitle(deviceName)
                }
                else -> {
                    deviceName = bindDeviceList[position - 1].name!!
                    MMKVUtil.saveString(CURRENT_DEVICE_NAME, deviceName)
                    if (homeViewPager.currentItem != 3)
                        setTitle(deviceName)
                }
            }
        }
        bindDeviceList.removeAt(position)
        drawAdapter.notifyItemRemoved(position)
        toastShort(resources.getString(R.string.app_main_unbind_success))
    }

    @SuppressLint("WrongConstant")
    override fun onNoDoubleClick(v: View) {
        when (v) {
            iv_left -> {
                homeDrawLayout.openDrawer(Gravity.START)
            }
            addDeviceLayout -> {
                val intent = Intent(mContext, AddNewDeviceActivity::class.java)
                intent.putExtra("type", "family")
                intent.putExtra("userGroupId", bindListBean?.userGroupId.toString())
                startActivityForResult(intent, ADD_NEW_DEVICE)
            }
            iv_toolbar_right2 -> {
                startActivity(Intent(mContext, RingMsgListActivity::class.java))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                ADD_NEW_DEVICE -> {
                    //添加新设备成功，刷新设备列表
                }
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
                    pageTitle = if (StringUtil.isEmpty(deviceName)) {
                        DateUtil.localNowStringByPattern(TIME_FORMAT_PATTERN4)
                    } else deviceName
                    setTitle(pageTitle)
                    setShowRightPoint(true)
                }
                R.id.menu_item_location -> {
                    it.setIcon(R.drawable.icon_home_bottom_location_select)
                    homeViewPager.currentItem = 1
                    deviceName = MMKVUtil.getString(CURRENT_DEVICE_NAME)
                    pageTitle = if (StringUtil.isEmpty(deviceName)) {
                        DateUtil.localNowStringByPattern(TIME_FORMAT_PATTERN4)
                    } else deviceName
                    setTitle(pageTitle)
                    setShowRightPoint(true)
                }
                R.id.menu_item_medicine -> {
                    it.setIcon(R.drawable.icon_home_bottom_medcine_select)
                    homeViewPager.currentItem = 2
                    deviceName = MMKVUtil.getString(CURRENT_DEVICE_NAME)
                    pageTitle = if (StringUtil.isEmpty(deviceName)) {
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
            layoutParams.height = ScreenUtils.dip2px(mContext, 30f)
            layoutParams.width = ScreenUtils.dip2px(mContext, 46f)
            iconView.layoutParams = layoutParams
        }
    }

    override fun showToolBar(): Boolean {
        return true
    }

    override fun openEventBus(): Boolean {
        return true
    }

    abstract class MyProgramFragmentAdapter(fragmentActivity: FragmentActivity)
        : FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int {
            return 4
        }
    }
}