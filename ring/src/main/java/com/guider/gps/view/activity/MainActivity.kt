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
import com.guider.health.apilib.IUserHDApi
import com.guider.health.apilib.bean.CheckBindDeviceBean
import com.guider.health.apilib.bean.RingSetBean
import com.guider.health.apilib.bean.UserInfo
import com.guider.health.apilib.bean.UserPositionListBean
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
    private var abnormalMsgUndoNum = 0
    private var careMsgUndoNum = 0
    private var bindPosition = 0

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
        pageTitle = MMKVUtil.getString(BIND_DEVICE_NAME)
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
//                toastShort(resources.getString(R.string.app_main_bind_device))
                //如果是相同的位置，则不需要重新加载数据
                if (bindPosition == position) return
                bindDeviceList[bindPosition].isSelected = 0
                drawAdapter.notifyItemChanged(bindPosition)
                bindPosition = position
                bindDeviceList[bindPosition].isSelected = 1
                drawAdapter.notifyItemChanged(bindPosition)
                homeDrawLayout.closeDrawer(Gravity.START)
                deviceName = bindDeviceList[bindPosition].relationShip!!
                MMKVUtil.saveString(BIND_DEVICE_NAME, deviceName)
                MMKVUtil.saveInt(BIND_DEVICE_ACCOUNT_ID, bindDeviceList[bindPosition].accountId)
                if (StringUtil.isNotBlankAndEmpty(bindDeviceList[bindPosition].deviceCode))
                    MMKVUtil.saveString(BIND_DEVICE_CODE, bindDeviceList[bindPosition].deviceCode!!)
                //我的页面 顶部标题固定为我的其他页面为设备名称
                if (homeViewPager.currentItem != 3) {
                    pageTitle = deviceName
                    setTitle(pageTitle)
                }
                EventBusUtils.sendEvent(EventBusEvent(EventBusAction.REFRESH_HEALTH_DATA,
                        "refresh"))
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
        getWalkTargetData()
        getRingUndoNumData()
    }

    private fun getWalkTargetData() {
        val accountId = MMKVUtil.getInt(USER.USERID)
        ApiUtil.createApi(IGuiderApi::class.java, false)
                .getUserRingSet(accountId)
                .enqueue(object : ApiCallBack<RingSetBean>(mContext) {
                    override fun onApiResponse(call: Call<RingSetBean>?,
                                               response: Response<RingSetBean>?) {
                        if (response?.body() != null) {
                            MMKVUtil.saveInt(TARGET_STEP, response.body()!!.walkTarget)
                            MMKVUtil.saveBoolean(BT_CHECK, response.body()!!.btOpen)
                            MMKVUtil.saveInt(BT_INTERVAL, response.body()!!.btInterval)
                            MMKVUtil.saveBoolean(HR_CHECK, response.body()!!.hrOpen)
                            MMKVUtil.saveInt(HR_INTERVAL, response.body()!!.hrInterval)
                        }
                    }
                })
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

    private fun getRingUndoNumData() {
        val accountId = MMKVUtil.getInt(USER.USERID)
        getAbnormalMsgUndoNum(accountId)
        getCareMsgUndoNum(accountId)
    }

    private fun getCareMsgUndoNum(accountId: Int) {
        ApiUtil.createHDApi(IUserHDApi::class.java, false)
                .getCareMsgUndo(accountId)
                .enqueue(object : ApiCallBack<String>(mContext) {
                    override fun onApiResponse(call: Call<String>?, response: Response<String>?) {
                        if (response?.body() != null) {
                            careMsgUndoNum = response.body()!!.toInt()
                            if (careMsgUndoNum != 0) setShowRightPoint(true)
                        }
                    }
                })
    }

    private fun getAbnormalMsgUndoNum(accountId: Int) {
        ApiUtil.createHDApi(IUserHDApi::class.java, false)
                .getAbnormalMsgUndo(accountId)
                .enqueue(object : ApiCallBack<String>(mContext) {
                    override fun onApiResponse(call: Call<String>?, response: Response<String>?) {
                        if (response?.body() != null) {
                            abnormalMsgUndoNum = response.body()!!.toInt()
                            if (abnormalMsgUndoNum != 0) setShowRightPoint(true)
                        }
                    }
                })
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshRightRedPoint(event: EventBusEvent<String>) {
        if (event.code == EventBusAction.REFRESH_RIGHT_RED_POINT) {
            if (event.data != null) {
                abnormalMsgUndoNum = event.data!!.substring(0,
                        event.data!!.indexOf("&")).toInt()
                careMsgUndoNum = event.data!!.substring(
                        event.data!!.indexOf("&") + 1).toInt()
                if (abnormalMsgUndoNum != 0 || careMsgUndoNum != 0)
                    setShowRightPoint(true)
                else setShowRightPoint(false)
            }
        }
    }

    private fun getBindDeviceList() {
        getUserLocationPointData()
        val accountId = MMKVUtil.getInt(USER.USERID, 0)
        val bindAccountId = MMKVUtil.getInt(BIND_DEVICE_ACCOUNT_ID)
        val bingAccountName = MMKVUtil.getString(BIND_DEVICE_NAME)
        if (bindListBean != null && !bindListBean?.userInfos.isNullOrEmpty()) {
            bindDeviceList.clear()
            bindDeviceList.addAll(bindListBean?.userInfos!!)
            run breaking@{
                bindListBean?.userInfos?.forEach {
                    if (it.accountId == accountId) {
                        it.relationShip = it.name
                        return@breaking
                    }
                }
            }
            breaking@ for (i in bindDeviceList.indices) {
                if (bindDeviceList[i].accountId == bindAccountId) {
                    bindDeviceList[i].isSelected = 1
                    bindPosition = i
                    pageTitle = bindDeviceList[i].relationShip!!
                    setTitle(pageTitle,
                            CommonUtils.getColor(mContext!!, R.color.white))
                    break@breaking
                }
            }
            drawAdapter.setSourceList(bindDeviceList)
        } else {
            getLatestGroupData(accountId, bindAccountId, bingAccountName)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshLatestGroupData(event: EventBusEvent<Boolean>) {
        if (event.code == EventBusAction.REFRESH_LATEST_GROUP_DATA) {
            if (event.data!!) {
                val accountId = MMKVUtil.getInt(USER.USERID, 0)
                val bindAccountId = MMKVUtil.getInt(BIND_DEVICE_ACCOUNT_ID)
                val bingAccountName = MMKVUtil.getString(BIND_DEVICE_NAME)
                getLatestGroupData(accountId, bindAccountId, bingAccountName)
            }
        }
    }

    private fun getLatestGroupData(accountId: Int, bindAccountId: Int, bingAccountName: String) {
        showDialog()
        ApiUtil.createApi(IGuiderApi::class.java, false)
                .getGroupBindMember(accountId = accountId)
                .enqueue(object : ApiCallBack<CheckBindDeviceBean>(mContext) {
                    override fun onApiResponse(call: Call<CheckBindDeviceBean>?,
                                               response: Response<CheckBindDeviceBean>?) {
                        if (response?.body() != null) {
                            bindListBean = response.body()
                            run breaking@{
                                bindListBean?.userInfos?.forEach {
                                    if (it.accountId == accountId) {
                                        it.relationShip = it.name
                                        return@breaking
                                    }
                                }
                            }
                            breaking@ for (i in bindListBean?.userInfos!!.indices) {
                                if (bindListBean?.userInfos!![i].accountId == bindAccountId) {
                                    bindListBean?.userInfos!![i].isSelected = 1
                                    bindPosition = i
                                    if (bingAccountName !=
                                            bindListBean?.userInfos!![i].relationShip) {
                                        pageTitle = bindListBean?.userInfos!![i].relationShip!!
                                        MMKVUtil.saveString(BIND_DEVICE_NAME,
                                                bindListBean?.userInfos!![i].relationShip!!)
                                        setTitle(pageTitle,
                                                CommonUtils.getColor(mContext!!, R.color.white))
                                    }
                                    break@breaking
                                }
                            }
                            bindDeviceList.clear()
                            bindDeviceList.addAll(bindListBean?.userInfos!!)
                            drawAdapter.setSourceList(bindDeviceList)
                        }
                    }

                    override fun onRequestFinish() {
                        dismissDialog()
                    }
                })
    }

    /**
     * 得到用户最近的定位点
     */
    private fun getUserLocationPointData() {
        val accountId = MMKVUtil.getInt(BIND_DEVICE_ACCOUNT_ID)
        if (accountId == 0) return
        ApiUtil.createApi(IGuiderApi::class.java, false)
                .userPosition(accountId, 1, 1, "", "")
                .enqueue(object : ApiCallBack<List<UserPositionListBean>>(mContext) {
                    override fun onApiResponse(call: Call<List<UserPositionListBean>>?,
                                               response: Response<List<UserPositionListBean>>?) {
                        if (!response?.body().isNullOrEmpty() && response?.body()!!.size == 1) {
                            val pointList = response.body()!!
                            val firstPosition = pointList[0]
                            MMKVUtil.saveDouble(LAST_LOCATION_POINT_LAT, firstPosition.lat)
                            MMKVUtil.saveDouble(LAST_LOCATION_POINT_LNG, firstPosition.lng)
                            if (StringUtil.isNotBlankAndEmpty(firstPosition.addr))
                                MMKVUtil.saveString(LAST_LOCATION_POINT_ADDRESS, firstPosition.addr)
                            if (StringUtil.isNotBlankAndEmpty(firstPosition.testTime))
                                MMKVUtil.saveString(LAST_LOCATION_POINT_TIME, firstPosition.testTime)
                        } else {
                            MMKVUtil.clearByKey(LAST_LOCATION_POINT_LAT)
                            MMKVUtil.clearByKey(LAST_LOCATION_POINT_LNG)
                            MMKVUtil.clearByKey(LAST_LOCATION_POINT_ADDRESS)
                            MMKVUtil.clearByKey(LAST_LOCATION_POINT_TIME)
                        }
                    }
                })
    }

    private fun unBindDialogShow(position: Int) {
        val accountId = MMKVUtil.getInt(USER.USERID)
        if (bindDeviceList[position].accountId == accountId) {
            return
        }
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
                        bindDeviceList[position].relationShip)
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

    fun unbindDeviceFromMineFragment(accountId: Int) {
        showDialog()
        var deviceCode = ""
        bindDeviceList.forEach {
            if (it.accountId == accountId) {
                if (StringUtil.isNotBlankAndEmpty(it.deviceCode))
                    deviceCode = it.deviceCode!!
            }
        }
        ApiUtil.createApi(IGuiderApi::class.java, false)
                .unBindDeviceWithAccount(accountId, deviceCode)
                .enqueue(object : ApiCallBack<Any?>(mContext) {
                    override fun onApiResponse(call: Call<Any?>?,
                                               response: Response<Any?>?) {
                        if (response?.body() != null) {
                            toastShort(resources.getString(R.string.app_main_unbind_success))
                            //当前账户必须要有一个设备绑定，所以解绑后要重新到绑定页面
                            MMKVUtil.clearByKey(USER.OWN_BIND_DEVICE_CODE)
                            unbindDeviceFromMineFragmentBackEvent(accountId)
                            EventBusUtils.sendEvent(EventBusEvent(
                                    EventBusAction.REFRESH_MINE_FRAGMENT_UNBIND_SHOW,
                                    false))
                            unBindAndEnterAddDevice()
                        }
                    }

                    override fun onRequestFinish() {
                        dismissDialog()
                    }
                })
    }

    private fun unbindDeviceFromMineFragmentBackEvent(accountId: Int) {
        var devicePosition = 0
        breaking@ for (i in 0 until bindDeviceList.size) {
            if (bindDeviceList[i].accountId == accountId) {
                devicePosition = i
                break@breaking
            }
        }
        val bindAccountId = MMKVUtil.getInt(BIND_DEVICE_ACCOUNT_ID, 0)
        if (bindDeviceList.size > 1) {
            when (devicePosition) {
                0 -> {
                    deviceName = if (StringUtil.isNotBlankAndEmpty(
                                    bindDeviceList[devicePosition + 1].relationShip))
                        bindDeviceList[devicePosition + 1].relationShip!!
                    else bindDeviceList[devicePosition + 1].name!!
                    //删除了绑定的设备
                    if ((bindAccountId != 0 && bindAccountId == bindDeviceList[devicePosition].accountId)
                            || (bindAccountId == bindDeviceList[devicePosition + 1].accountId)
                    ) {
                        bindDeviceList[devicePosition + 1].isSelected = 1
                        MMKVUtil.saveString(BIND_DEVICE_NAME, deviceName)
                        MMKVUtil.saveInt(BIND_DEVICE_ACCOUNT_ID,
                                bindDeviceList[devicePosition + 1].accountId)
                        if (StringUtil.isNotBlankAndEmpty(bindDeviceList[devicePosition + 1].deviceCode))
                            MMKVUtil.saveString(BIND_DEVICE_CODE,
                                    bindDeviceList[devicePosition + 1].deviceCode!!)
                        if (homeViewPager.currentItem != 3)
                            setTitle(deviceName)
                        drawAdapter.notifyItemChanged(devicePosition + 1)
                    }
                }
                else -> {
                    deviceName = if (StringUtil.isNotBlankAndEmpty(
                                    bindDeviceList[devicePosition - 1].relationShip))
                        bindDeviceList[devicePosition - 1].relationShip!!
                    else bindDeviceList[devicePosition - 1].name!!
                    if ((bindAccountId != 0 && bindAccountId == bindDeviceList[devicePosition].accountId)
                            || (bindAccountId == bindDeviceList[devicePosition - 1].accountId)
                    ) {
                        bindDeviceList[devicePosition - 1].isSelected = 1
                        MMKVUtil.saveString(BIND_DEVICE_NAME, deviceName)
                        MMKVUtil.saveInt(BIND_DEVICE_ACCOUNT_ID,
                                bindDeviceList[devicePosition - 1].accountId)
                        if (StringUtil.isNotBlankAndEmpty(bindDeviceList[devicePosition - 1].deviceCode))
                            MMKVUtil.saveString(BIND_DEVICE_CODE,
                                    bindDeviceList[devicePosition - 1].deviceCode!!)
                        if (homeViewPager.currentItem != 3)
                            setTitle(deviceName)
                        drawAdapter.notifyItemChanged(devicePosition - 1)
                    }
                }
            }
        } else {
            deviceName = ""
            MMKVUtil.clearByKey(BIND_DEVICE_NAME)
            MMKVUtil.clearByKey(BIND_DEVICE_ACCOUNT_ID)
            MMKVUtil.clearByKey(BIND_DEVICE_CODE)
        }
        bindDeviceList.removeAt(devicePosition)
        drawAdapter.notifyDataSetChanged()
    }

    private fun unbindDeviceEvent(position: Int) {
        //如果是解绑的是当前账户绑定的设备则需要去重新绑定
//        val accountId = MMKVUtil.getInt(USER.USERID)
//        if (bindDeviceList[position].accountId == accountId) {
//            showDialog()
//            val deviceCode = if (StringUtil.isNotBlankAndEmpty(bindDeviceList[position].deviceCode)) {
//                bindDeviceList[position].deviceCode!!
//            } else "0"
//            ApiUtil.createApi(IGuiderApi::class.java, false)
//                    .unBindDeviceWithAccount(accountId, deviceCode)
//                    .enqueue(object : ApiCallBack<Any?>(mContext) {
//                        override fun onApiResponse(call: Call<Any?>?,
//                                                   response: Response<Any?>?) {
//                            if (response?.body() != null) {
//                                MMKVUtil.clearByKey(USER.OWN_BIND_DEVICE_CODE)
//                                unBindDeviceAdapterShow(position)
//                                EventBusUtils.sendEvent(EventBusEvent(
//                                        EventBusAction.REFRESH_MINE_FRAGMENT_UNBIND_SHOW,
//                                        false))
//                                unBindAndEnterAddDevice()
//                            }
//                        }
//
//                        override fun onRequestFinish() {
//                            dismissDialog()
//                        }
//                    })
//        } else {
        unBindGroupMemberEvent(position)
//        }
    }

    fun unBindAndEnterAddDevice() {
        val intent = Intent(mContext, AddNewDeviceActivity::class.java)
        intent.putExtra("type", "unBindAndBindNew")
        startActivityForResult(intent, ADD_NEW_DEVICE)
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
        val bindAccountId = MMKVUtil.getInt(BIND_DEVICE_ACCOUNT_ID, 0)
        if (bindDeviceList.size == 1) {
            deviceName = ""
            MMKVUtil.clearByKey(BIND_DEVICE_NAME)
            MMKVUtil.clearByKey(BIND_DEVICE_ACCOUNT_ID)
            MMKVUtil.clearByKey(BIND_DEVICE_CODE)
            if (homeViewPager.currentItem != 3) {
                val currentDayValue =
                        DateUtil.localNowStringByPattern(TIME_FORMAT_PATTERN4)
                setTitle(currentDayValue)
            }
        } else {
            when (position) {
                0 -> {
                    deviceName = if (StringUtil.isNotBlankAndEmpty(
                                    bindDeviceList[position + 1].relationShip))
                        bindDeviceList[position + 1].relationShip!!
                    else bindDeviceList[position + 1].name!!
                    //删除了绑定的设备
                    if ((bindAccountId != 0 && bindAccountId == bindDeviceList[position].accountId)
                            || (bindAccountId == bindDeviceList[position + 1].accountId)
                    ) {
                        bindDeviceList[position + 1].isSelected = 1
                        MMKVUtil.saveString(BIND_DEVICE_NAME, deviceName)
                        MMKVUtil.saveInt(BIND_DEVICE_ACCOUNT_ID,
                                bindDeviceList[position + 1].accountId)
                        if (StringUtil.isNotBlankAndEmpty(bindDeviceList[position + 1].deviceCode))
                            MMKVUtil.saveString(BIND_DEVICE_CODE,
                                    bindDeviceList[position + 1].deviceCode!!)
                        if (homeViewPager.currentItem != 3)
                            setTitle(deviceName)
                        drawAdapter.notifyItemChanged(position + 1)
                    }
                }
                else -> {
                    deviceName = if (StringUtil.isNotBlankAndEmpty(
                                    bindDeviceList[position - 1].relationShip))
                        bindDeviceList[position - 1].relationShip!!
                    else bindDeviceList[position - 1].name!!
                    if ((bindAccountId != 0 && bindAccountId == bindDeviceList[position].accountId)
                            || (bindAccountId == bindDeviceList[position - 1].accountId)
                    ) {
                        bindDeviceList[position - 1].isSelected = 1
                        MMKVUtil.saveString(BIND_DEVICE_NAME, deviceName)
                        MMKVUtil.saveInt(BIND_DEVICE_ACCOUNT_ID,
                                bindDeviceList[position - 1].accountId)
                        if (StringUtil.isNotBlankAndEmpty(bindDeviceList[position - 1].deviceCode))
                            MMKVUtil.saveString(BIND_DEVICE_CODE,
                                    bindDeviceList[position - 1].deviceCode!!)
                        if (homeViewPager.currentItem != 3)
                            setTitle(deviceName)
                        drawAdapter.notifyItemChanged(position - 1)
                    }
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
                startActivity(intent)
            }
            iv_toolbar_right2 -> {
                val intent = Intent(mContext, RingMsgListActivity::class.java)
                intent.putExtra("abnormalMsgUndoNum", abnormalMsgUndoNum)
                intent.putExtra("careMsgUndoNum", careMsgUndoNum)
                startActivity(intent)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                ADD_NEW_DEVICE -> {
                    //绑定了新设备，刷新设备列表
                    if (data.getParcelableExtra<CheckBindDeviceBean?>(
                                    "bindListBean") != null) {
                        bindListBean = data.getParcelableExtra("bindListBean")
                    }
                    EventBusUtils.sendEvent(EventBusEvent(
                            EventBusAction.REFRESH_MINE_FRAGMENT_UNBIND_SHOW,
                            true))
                    getBindDeviceList()
                    EventBusUtils.sendEvent(EventBusEvent(EventBusAction.REFRESH_HEALTH_DATA,
                            "refresh"))
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
                    deviceName = MMKVUtil.getString(BIND_DEVICE_NAME)
                    pageTitle = if (StringUtil.isEmpty(deviceName)) {
                        DateUtil.localNowStringByPattern(TIME_FORMAT_PATTERN4)
                    } else deviceName
                    setTitle(pageTitle)
                }
                R.id.menu_item_location -> {
                    it.setIcon(R.drawable.icon_home_bottom_location_select)
                    homeViewPager.currentItem = 1
                    deviceName = MMKVUtil.getString(BIND_DEVICE_NAME)
                    pageTitle = if (StringUtil.isEmpty(deviceName)) {
                        DateUtil.localNowStringByPattern(TIME_FORMAT_PATTERN4)
                    } else deviceName
                    setTitle(pageTitle)
                }
                R.id.menu_item_medicine -> {
                    it.setIcon(R.drawable.icon_home_bottom_medcine_select)
                    homeViewPager.currentItem = 2
                    deviceName = MMKVUtil.getString(BIND_DEVICE_NAME)
                    pageTitle = if (StringUtil.isEmpty(deviceName)) {
                        DateUtil.localNowStringByPattern(TIME_FORMAT_PATTERN4)
                    } else deviceName
                    setTitle(pageTitle)
                }
                R.id.menu_item_mine -> {
                    it.setIcon(R.drawable.icon_home_bottom_mine_select)
                    homeViewPager.currentItem = 3
                    setTitle("我的")
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