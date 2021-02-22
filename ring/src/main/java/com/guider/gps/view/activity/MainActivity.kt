package com.guider.gps.view.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Intent
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.launcher.ARouter
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
import com.guider.baselib.base.BaseActivity
import com.guider.baselib.utils.*
import com.guider.baselib.utils.EventBusAction.REFRESH_CURRENT_LOGIN_ACCOUNT_INFO
import com.guider.baselib.widget.dialog.DialogHolder
import com.guider.feifeia3.utils.ToastUtil
import com.guider.gps.R
import com.guider.gps.adapter.HomeLeftDrawMsgAdapter
import com.guider.gps.util.TouristsEventUtil
import com.guider.gps.view.fragment.HealthFragment
import com.guider.gps.view.fragment.LocationFragment
import com.guider.gps.view.fragment.MedicineFragment
import com.guider.gps.view.fragment.MineFragment
import com.guider.health.apilib.GuiderApiUtil
import com.guider.health.apilib.bean.CheckBindDeviceBean
import com.guider.health.apilib.bean.UserInfo
import com.guider.health.apilib.enums.PositionType
import com.guider.health.apilib.enums.PushNationType
import com.guider.health.apilib.utils.GsonUtil
import com.guider.health.apilib.utils.MMKVUtil
import kotlinx.android.synthetic.main.activity_home_draw_layout.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_msg_list.*
import kotlinx.android.synthetic.main.include_location_fragment_deal_layout.*
import kotlinx.android.synthetic.main.include_phone_edit_layout.*
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*


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
    private var latestSystemMsgTime = ""
    private var dialogTagList = arrayListOf<String>()
    private var editDialog: DialogHolder? = null
    private var touristsModeRegisterDialog: DialogHolder? = null
    private var isHaveNewMsg = false

    override fun initImmersion() {
        showBackButton(R.drawable.icon_home_left_menu, this)
        setRightImage2(R.drawable.ic_home_msg, this)
        intentEvent()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        intentEvent()
    }

    private fun intentEvent() {
        if (intent != null) {
            if (intent.getParcelableExtra<CheckBindDeviceBean?>("bindListBean") != null) {
                //防止程序中途进入的话，修改值
                if (bindListBean == null)
                    bindListBean = intent.getParcelableExtra("bindListBean")
            }
            if (intent.getBooleanExtra("news", false)) {
                isHaveNewMsg = true
            }
            //由推送的消息进入
            if (isHaveNewMsg) {
                ARouter.getInstance().build(msgList)
                        //进入页面需跳转到的指定列表项
                        .withInt("entryPageIndex", 2)
                        .navigation()
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
        latestSystemMsgTime = DateUtilKotlin.localToUTC(
                DateUtil.localNowStringByPattern(DEFAULT_TIME_FORMAT_PATTERN))!!
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
                if (StringUtil.isEmpty(bindDeviceList[position].deviceCode)) return
                if (bindPosition == position) return
                bindDeviceList[bindPosition].isSelected = 0
                drawAdapter.notifyItemChanged(bindPosition)
                bindPosition = position
                bindDeviceList[bindPosition].isSelected = 1
                drawAdapter.notifyItemChanged(bindPosition)
                homeDrawLayout.closeDrawer(Gravity.START)
                deviceName = bindDeviceList[bindPosition].relationShip
                        ?: bindDeviceList[bindPosition].name ?: ""
                MMKVUtil.saveString(BIND_DEVICE_NAME, deviceName)
                MMKVUtil.saveInt(BIND_DEVICE_ACCOUNT_ID, bindDeviceList[bindPosition].accountId)
                if (StringUtil.isNotBlankAndEmpty(bindDeviceList[bindPosition].headUrl))
                    MMKVUtil.saveString(BIND_DEVICE_ACCOUNT_HEADER,
                            bindDeviceList[bindPosition].headUrl!!)
                if (StringUtil.isNotBlankAndEmpty(bindDeviceList[bindPosition].deviceCode))
                    MMKVUtil.saveString(BIND_DEVICE_CODE, bindDeviceList[bindPosition].deviceCode!!)
                //我的页面 顶部标题固定为我的其他页面为设备名称
                if (homeViewPager.currentItem != 3) {
                    pageTitle = deviceName
                    setTitle(pageTitle)
                }
                EventBusUtils.sendEvent(EventBusEvent(EventBusAction.REFRESH_USER_DATA,
                        "refresh"))
            }

        })
        drawAdapter.setLongClickListener(object :
                HomeLeftDrawMsgAdapter.AdapterOnItemLongClickListener {
            override fun onClickItem(position: Int) {
                unBindDialogShow(position)
            }

        })
        drawAdapter.setEditClickListener(object : HomeLeftDrawMsgAdapter.AdapterEditClickListener {
            override fun onEditItem(position: Int) {
//                if (StringUtil.isEmpty(drawAdapter.mData[position].relationShip)) return
                editNameDialog(position)
            }

        })
        msgListRv.adapter = drawAdapter
        getBindDeviceList()
        getRingUndoNumData()
        initGoogleFirebaseMessage()
    }

    /**
     * 初始化谷歌服务
     */
    private fun initGoogleFirebaseMessage() {
        val activityManager: ActivityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Log.i(TAG,
                    "onCreate: activityManager.isBackgroundRestricted() = " +
                            "${activityManager.isBackgroundRestricted}")
        }
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result
            Log.i(TAG, "谷歌推送的token为$token")
            uploadToken(token)
        })
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshPushToken(event: EventBusEvent<String>) {
        if (event.code == EventBusAction.REFRESH_NEW_PUSH_TOKEN) {
            if (StringUtil.isNotBlankAndEmpty(event.data)) {
                uploadToken(event.data)
            }
        }
    }

    private fun uploadToken(token: String?) {
        if (StringUtil.isEmpty(token)) return
        if (MMKVUtil.getInt(USER.USERID) == 0) return
        lifecycleScope.launch {
            ApiCoroutinesCallBack.resultParse(block = {
                val resultBean = GuiderApiUtil.getApiService().uploadPushToken(
                        MMKVUtil.getInt(USER.USERID), PushNationType.ZH_TW,
                        "ANDROID", token!!)
                if (resultBean != null) {
                    Log.i(TAG, "设置pushToken成功")
                }
            })
        }
    }

    @SuppressLint("StaticFieldLeak")
    private fun editNameDialog(position: Int) {
        if (editDialog == null) {
            editDialog = object : DialogHolder(this,
                    R.layout.dialog_device_account_name_edit, Gravity.CENTER), View.OnClickListener {
                lateinit var deviceNameEdit: EditText
                lateinit var deviceNameDeleteIv: ImageView
                override fun bindView(dialogView: View) {
                    val cancelIv = dialogView.findViewById<ConstraintLayout>(R.id.cancelLayout)
                    val confirmLayout = dialogView.findViewById<ConstraintLayout>(R.id.confirmLayout)
                    deviceNameEdit = dialogView.findViewById(R.id.deviceNameEdit)
                    deviceNameDeleteIv = dialogView.findViewById(R.id.deleteIv)
                    if (StringUtil.isNotBlankAndEmpty(drawAdapter.mData[position].relationShip)) {
                        deviceNameDeleteIv.visibility = View.VISIBLE
                        deviceNameEdit.setText(drawAdapter.mData[position].relationShip)
                    }
                    deviceNameEdit.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, start: Int,
                                                       count: Int, after: Int) {
                        }

                        override fun onTextChanged(s: CharSequence?, start: Int,
                                                   before: Int, count: Int) {
                        }

                        override fun afterTextChanged(s: Editable?) {
                            if (!s.isNullOrEmpty()) {
                                deviceNameDeleteIv.visibility = View.VISIBLE
                            } else {
                                deviceNameDeleteIv.visibility = View.GONE
                            }
                        }

                    })
                    deviceNameDeleteIv.setOnClickListener(this)
                    cancelIv.setOnClickListener(this)
                    confirmLayout.setOnClickListener(this)
                }

                override fun onClick(v: View) {
                    when (v.id) {
                        R.id.cancelLayout -> {
                            dialog?.dismiss()
                            editDialog = null
                        }
                        R.id.confirmLayout -> {
                            val electronicName = deviceNameEdit.text.toString()
                            if (StringUtil.isEmpty(electronicName)) return
                            updateDeviceRelationShip(position, electronicName)
                        }
                        R.id.deleteIv -> {
                            deviceNameEdit.setText("")

                        }
                    }
                }
            }
            editDialog?.initView()
            editDialog?.show(true)
        }
    }

    private fun updateDeviceRelationShip(position: Int, electronicName: String) {
        if (bindListBean == null) return
        val accountId = bindDeviceList[position].accountId
        lifecycleScope.launch {
            ApiCoroutinesCallBack.resultParse(onStart = {
                showDialog()
            }, block = {
                val resultBean = GuiderApiUtil.getApiService().updateRelationShipData(
                        bindListBean!!.userGroupId, accountId, electronicName)
                if (resultBean == "true") {
                    editDialog?.closeDialog()
                    editDialog = null
                    ToastUtil.showCustomToast(null, mContext!!, true,
                            mContext!!.resources.getString(
                                    R.string.app_person_info_change_success),
                            R.drawable.rounded_99000000_5_bg)
                    bindDeviceList[position].relationShip = electronicName
                    drawAdapter.notifyDataSetChanged()
                    //如果当前修改的用户是当前切换的绑定设备的用户的话改存储设备昵称和title
                    if (bindDeviceList[bindPosition].accountId == accountId) {
                        MMKVUtil.saveString(BIND_DEVICE_NAME, electronicName)
                        //我的页面 顶部标题固定为我的其他页面为设备名称
                        if (homeViewPager.currentItem != 3) {
                            pageTitle = electronicName
                            setTitle(pageTitle)
                        }
                    }
                }
            }, onRequestFinish = {
                dismissDialog()
            })
        }
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun sosRefreshLocation(event: EventBusEvent<String>) {
        if (event.code == EventBusAction.ENTRY_LOCATION_WITH_TYPE) {
            homeViewPager.currentItem = 1
        }
    }

    private fun getRingUndoNumData() {
        val accountId = MMKVUtil.getInt(USER.USERID)
        getAbnormalMsgUndoNum(accountId)
        getCareMsgUndoNum(accountId)
    }

    private fun getCareMsgUndoNum(accountId: Int) {
        lifecycleScope.launch {
            ApiCoroutinesCallBack.resultParse(block = {
                val resultBean = GuiderApiUtil.getHDApiService().getCareMsgUndo(accountId)
                if (resultBean != null) {
                    careMsgUndoNum = resultBean.toInt()
                    if (careMsgUndoNum != 0) setShowRightPoint(true)
                }
            })
        }
    }

    private fun getAbnormalMsgUndoNum(accountId: Int) {
        lifecycleScope.launch {
            ApiCoroutinesCallBack.resultParse(block = {
                val resultBean = GuiderApiUtil.getHDApiService().getAbnormalMsgUndo(accountId)
                if (resultBean != null) {
                    abnormalMsgUndoNum = resultBean.toInt()
                    if (abnormalMsgUndoNum != 0) setShowRightPoint(true)
                }
            })
        }
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
        if (bindListBean != null && !bindListBean?.userInfos.isNullOrEmpty()) {
            bindDeviceList.clear()
            bindDeviceList.addAll(bindListBean?.userInfos!!)
            run breaking@{
                bindListBean?.userInfos?.forEach {
                    if (it.accountId == accountId) {
                        it.relationShip = it.name
                        if (StringUtil.isNotBlankAndEmpty(it.headUrl))
                            MMKVUtil.saveString(USER.HEADER, it.headUrl!!)
                        if (StringUtil.isNotBlankAndEmpty(it.relationShip))
                            MMKVUtil.saveString(USER.NAME, it.relationShip ?: "")
                        return@breaking
                    }
                }
            }
            bindDeviceList = bindDeviceList.filter {
                StringUtil.isNotBlankAndEmpty(it.deviceCode)
            } as ArrayList<UserInfo>
            breaking@ for (i in bindDeviceList.indices) {
                if (bindDeviceList[i].accountId == bindAccountId) {
                    bindDeviceList[i].isSelected = 1
                    bindPosition = i
                    pageTitle = bindDeviceList[i].relationShip ?: bindDeviceList[i].name ?: ""
                    setTitle(pageTitle,
                            CommonUtils.getColor(mContext!!, R.color.white))
                    break@breaking
                }
            }
            drawAdapter.setSourceList(bindDeviceList)
            storageAccountPositionType()
        } else {
            getLatestGroupData(true)
        }
    }

    private fun storageAccountPositionType() {
        //存储用户的定位模式
        val dataMap = hashMapOf<String, Boolean>()
        bindDeviceList.map {
            dataMap[it.accountId.toString()] = it.deviceMode == PositionType.FINDER.name
        }
        val toJson = GsonUtil.toJson(dataMap)
        MMKVUtil.saveString(BIND_DEVICE_ACCOUNT_POSITION_SEARCH_PERSON, toJson)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshLatestGroupData(event: EventBusEvent<Boolean>) {
        if (event.code == EventBusAction.REFRESH_LATEST_GROUP_DATA) {
            if (event.data!!) {
                getLatestGroupData(false)
            }
        }
    }

    fun getLatestGroupData(isShowDialog: Boolean) {
        val accountId = MMKVUtil.getInt(USER.USERID, 0)
        val bindAccountId = MMKVUtil.getInt(BIND_DEVICE_ACCOUNT_ID)
        val bingAccountName = MMKVUtil.getString(BIND_DEVICE_NAME)
        lifecycleScope.launch {
            ApiCoroutinesCallBack.resultParse(onStart = {
                if (isShowDialog) showDialog()
            }, block = {
                val resultBean = GuiderApiUtil.getApiService()
                        .getGroupBindMember(accountId = accountId)
                if (resultBean != null) {
                    bindListBean = resultBean
                    run breaking@{
                        bindListBean?.userInfos?.forEach {
                            if (it.accountId == accountId) {
                                it.relationShip = it.name
                                if (StringUtil.isNotBlankAndEmpty(it.headUrl))
                                    MMKVUtil.saveString(USER.HEADER, it.headUrl!!)
                                if (StringUtil.isNotBlankAndEmpty(it.relationShip))
                                    MMKVUtil.saveString(USER.NAME, it.relationShip ?: "")
                                if (StringUtil.isNotBlankAndEmpty(it.phone)
                                        && !it.phone!!.contains("vis")) {
                                    //绑定当前的手机号因为没有点击登录按钮所以没有切换新的手机号避免出错
                                    MMKVUtil.saveString(USER.PHONE, it.phone ?: "")
                                }
                                EventBusUtils.sendEvent(EventBusEvent(
                                        REFRESH_CURRENT_LOGIN_ACCOUNT_INFO, true))
                                //当前登录账号的设备号为空
                                if (StringUtil.isEmpty(it.deviceCode)) {
                                    CommonUtils.logOutClearMMKV()
                                    val intent = Intent(mContext, LoginActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                            Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                    finish()
                                    return@resultParse
                                }
                                //多加一层验证是否是游客模式
                                if (StringUtil.isNotBlankAndEmpty(it.phone)
                                        && !it.phone!!.contains("vis")) {
                                    //说明已经是非游客，已经注册了账号
                                    MMKVUtil.saveBoolean(TOURISTS_MODE, false)
                                }
                                return@breaking
                            }
                        }
                    }
                    bindDeviceList.clear()
                    bindDeviceList.addAll(bindListBean?.userInfos!!)
                    bindDeviceList = bindDeviceList.filter {
                        StringUtil.isNotBlankAndEmpty(it.deviceCode)
                    } as ArrayList<UserInfo>
                    breaking@ for (i in bindDeviceList.indices) {
                        if (bindDeviceList[i].accountId == bindAccountId) {
                            bindDeviceList[i].isSelected = 1
                            bindPosition = i
                            if (bingAccountName !=
                                    bindDeviceList[i].relationShip) {
                                pageTitle = bindDeviceList[i].relationShip
                                        ?: bindDeviceList[i].name ?: ""
                                MMKVUtil.saveString(BIND_DEVICE_NAME, pageTitle)
                                if (homeViewPager.currentItem != 3)
                                    setTitle(pageTitle,
                                            CommonUtils.getColor(mContext!!, R.color.white))
                            }
                            break@breaking
                        }
                    }
                    drawAdapter.setSourceList(bindDeviceList)
                    storageAccountPositionType()
                }
            }, onRequestFinish = {
                if (isShowDialog) dismissDialog()
            })
        }
    }

    /**
     * 得到用户最近的定位点
     */
    private fun getUserLocationPointData() {
        val accountId = MMKVUtil.getInt(BIND_DEVICE_ACCOUNT_ID)
        if (accountId == 0) return
        lifecycleScope.launch {
            ApiCoroutinesCallBack.resultParse(block = {
                val resultBean = GuiderApiUtil.getApiService()
                        .userPosition(accountId, 1, 1, "", "")
                var dataMap = hashMapOf<String, Any>()
                if (MMKVUtil.containKey(BIND_DEVICE_LAST_LOCATION)) {
                    val json = MMKVUtil.getString(BIND_DEVICE_LAST_LOCATION)
                    dataMap = GsonUtil.fromJson(json)
                }
                if (!resultBean.isNullOrEmpty() && resultBean.size == 1) {
                    val firstPosition = resultBean[0]
                    dataMap[accountId.toString()] = firstPosition
                } else {
                    if (dataMap.containsKey(accountId.toString())) {
                        dataMap.remove(accountId.toString())
                    }
                }
                val toJson = GsonUtil.toJson(dataMap)
                MMKVUtil.saveString(BIND_DEVICE_LAST_LOCATION, toJson)
            })
        }
    }

    private fun unBindDialogShow(position: Int) {
        val accountId = MMKVUtil.getInt(USER.USERID)
        if (bindDeviceList[position].accountId == accountId) {
            return
        }
        val dialog = object : DialogHolder(this,
                R.layout.dialog_common_with_title, Gravity.CENTER) {
            @SuppressLint("SetTextI18n")
            override fun bindView(dialogView: View) {
                val cancel = dialogView.findViewById<ConstraintLayout>(R.id.cancelLayout)
                cancel.setOnClickListener {
                    dialog?.dismiss()
                }

                val unBindContentTv = dialogView.findViewById<TextView>(R.id.unBindContentTv)
                val unbindValue = String.format(
                        resources.getString(R.string.app_main_unbind_device),
                        bindDeviceList[position].relationShip
                                ?: bindDeviceList[position].name ?: "")
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
        var deviceCode = ""
        bindDeviceList.forEach {
            if (it.accountId == accountId) {
                if (StringUtil.isNotBlankAndEmpty(it.deviceCode))
                    deviceCode = it.deviceCode!!
            }
        }
        lifecycleScope.launch {
            ApiCoroutinesCallBack.resultParse(onStart = {
                showDialog()
            }, block = {
                val resultBean = GuiderApiUtil.getApiService()
                        .unBindDeviceWithAccount(accountId, deviceCode)
                dismissDialog()
                if (resultBean == null) return@resultParse
                toastShort(resources.getString(R.string.app_main_unbind_success))
                CommonUtils.logOutClearMMKV()
                val intent = Intent(mContext, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or
                        Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
                //当前账户必须要有一个设备绑定，所以解绑后要重新到绑定页面
//                MMKVUtil.clearByKey(USER.OWN_BIND_DEVICE_CODE)
//                unbindDeviceFromMineFragmentBackEvent(accountId)
//                EventBusUtils.sendEvent(EventBusEvent(
//                        EventBusAction.REFRESH_MINE_FRAGMENT_UNBIND_SHOW,
//                        false))
//                unBindAndEnterAddDevice()
            }, onRequestFinish = {
                dismissDialog()
            })
        }
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
                        bindDeviceList[devicePosition + 1].relationShip ?: ""
                    else bindDeviceList[devicePosition + 1].name!!
                    //删除了绑定的设备
                    if ((bindAccountId != 0 && bindAccountId == bindDeviceList[devicePosition].accountId)
                            || (bindAccountId == bindDeviceList[devicePosition + 1].accountId)
                    ) {
                        bindDeviceList[devicePosition + 1].isSelected = 1
                        MMKVUtil.saveString(BIND_DEVICE_NAME, deviceName)
                        MMKVUtil.saveInt(BIND_DEVICE_ACCOUNT_ID,
                                bindDeviceList[devicePosition + 1].accountId)
                        if (StringUtil.isNotBlankAndEmpty(bindDeviceList[devicePosition + 1].headUrl))
                            MMKVUtil.saveString(BIND_DEVICE_ACCOUNT_HEADER,
                                    bindDeviceList[devicePosition + 1].headUrl!!)
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
                        bindDeviceList[devicePosition - 1].relationShip ?: ""
                    else bindDeviceList[devicePosition - 1].name!!
                    if ((bindAccountId != 0 && bindAccountId == bindDeviceList[devicePosition].accountId)
                            || (bindAccountId == bindDeviceList[devicePosition - 1].accountId)
                    ) {
                        bindDeviceList[devicePosition - 1].isSelected = 1
                        MMKVUtil.saveString(BIND_DEVICE_NAME, deviceName)
                        MMKVUtil.saveInt(BIND_DEVICE_ACCOUNT_ID,
                                bindDeviceList[devicePosition - 1].accountId)
                        if (StringUtil.isNotBlankAndEmpty(bindDeviceList[devicePosition - 1].headUrl))
                            MMKVUtil.saveString(BIND_DEVICE_ACCOUNT_HEADER,
                                    bindDeviceList[devicePosition - 1].headUrl!!)
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
        if (bindListBean != null && bindListBean!!.userGroupId != 0) {
            val groupId = bindListBean!!.userGroupId
            val accountId = bindDeviceList[position].accountId
            lifecycleScope.launch {
                ApiCoroutinesCallBack.resultParse(onStart = {
                    showDialog()
                }, block = {
                    val resultBean = GuiderApiUtil.getApiService()
                            .unBindGroupMember(groupId, accountId)

                    if (resultBean != null) {
                        bindListBean!!.userGroupId = groupId
                        bindListBean!!.userInfos = resultBean
                        unBindDeviceAdapterShow(position)
                    }
                }, onRequestFinish = {
                    dismissDialog()
                })
            }
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
                        bindDeviceList[position + 1].relationShip ?: ""
                    else bindDeviceList[position + 1].name!!
                    //删除了绑定的设备
                    if ((bindAccountId != 0 && bindAccountId == bindDeviceList[position].accountId)
                            || (bindAccountId == bindDeviceList[position + 1].accountId)
                    ) {
                        bindDeviceList[position + 1].isSelected = 1
                        MMKVUtil.saveString(BIND_DEVICE_NAME, deviceName)
                        MMKVUtil.saveInt(BIND_DEVICE_ACCOUNT_ID,
                                bindDeviceList[position + 1].accountId)
                        if (StringUtil.isNotBlankAndEmpty(bindDeviceList[position + 1].headUrl))
                            MMKVUtil.saveString(BIND_DEVICE_ACCOUNT_HEADER,
                                    bindDeviceList[position + 1].headUrl!!)
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
                        bindDeviceList[position - 1].relationShip ?: ""
                    else bindDeviceList[position - 1].name!!
                    if ((bindAccountId != 0 && bindAccountId == bindDeviceList[position].accountId)
                            || (bindAccountId == bindDeviceList[position - 1].accountId)
                    ) {
                        bindDeviceList[position - 1].isSelected = 1
                        MMKVUtil.saveString(BIND_DEVICE_NAME, deviceName)
                        MMKVUtil.saveInt(BIND_DEVICE_ACCOUNT_ID,
                                bindDeviceList[position - 1].accountId)
                        if (StringUtil.isNotBlankAndEmpty(bindDeviceList[position - 1].headUrl))
                            MMKVUtil.saveString(BIND_DEVICE_ACCOUNT_HEADER,
                                    bindDeviceList[position - 1].headUrl!!)
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
                //区分是否是游客登录
                if (MMKVUtil.containKey(TOURISTS_MODE) && MMKVUtil.getBoolean(TOURISTS_MODE)) {
                    touristsEvent()
                } else {
                    val intent = Intent(mContext, AddNewDeviceActivity::class.java)
                    intent.putExtra("type", "family")
                    intent.putExtra("userGroupId", bindListBean?.userGroupId.toString())
                    startActivityForResult(intent, ADD_NEW_MEMBER)
                }
            }
            iv_toolbar_right2 -> {
                val intent = Intent(mContext, RingMsgListActivity::class.java)
                intent.putExtra("abnormalMsgUndoNum", abnormalMsgUndoNum)
                intent.putExtra("careMsgUndoNum", careMsgUndoNum)
                startActivity(intent)
            }
        }
    }

    /**
     * 游客登录的操作
     */
    fun touristsEvent() {
        touristsModeRegisterDialog = TouristsEventUtil.generateTouristsDialog(
                this, R.layout.dialog_tourists_mode) {
            //进入账户绑定页面
            val intent = Intent(mContext, AccountBindActivity::class.java)
            startActivityForResult(intent, ACCOUNT_BIND)
        }
        touristsModeRegisterDialog?.initView()
        touristsModeRegisterDialog?.show(true)
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
                    EventBusUtils.sendEvent(EventBusEvent(EventBusAction.REFRESH_USER_DATA,
                            "refresh"))
                }
                ADD_NEW_MEMBER -> {
                    //添加了家人
                    if (data.getParcelableExtra<CheckBindDeviceBean?>(
                                    "bindListBean") != null) {
                        bindListBean = data.getParcelableExtra("bindListBean")
                    }
                    getBindDeviceList()
                }
                ACCOUNT_BIND -> {
                    Log.i(TAG, "游客绑定手机账号成功，重新刷新数据")
                    getLatestGroupData(true)
                }
            }
        }
    }

    @SuppressLint("WrongConstant")
    override fun onBackPressed() {
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

    override fun onDestroy() {
        super.onDestroy()
        if (editDialog != null) {
            editDialog?.closeDialog()
            editDialog = null
        }
        if (touristsModeRegisterDialog != null) {
            touristsModeRegisterDialog?.closeDialog()
            touristsModeRegisterDialog = null
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