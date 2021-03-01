package com.guider.gps.view.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.tabs.TabLayout
import com.guider.baselib.base.BaseFragment
import com.guider.baselib.utils.*
import com.guider.baselib.utils.CommonUtils.convertViewToBitmap
import com.guider.baselib.utils.DateUtilKotlin.getDateShowWithLanguage
import com.guider.baselib.widget.CircleImageView
import com.guider.baselib.widget.LoadingView
import com.guider.baselib.widget.calendarList.CalendarList
import com.guider.baselib.widget.dialog.DialogHolder
import com.guider.baselib.widget.dialog.DialogProgress
import com.guider.baselib.utils.ToastUtil
import com.guider.gps.BuildConfig
import com.guider.gps.R
import com.guider.gps.adapter.LocationTrackEventTimeAdapter
import com.guider.gps.bean.LocationPathShowMethodEnum
import com.guider.gps.bean.WithSelectBaseBean
import com.guider.gps.view.activity.ElectronicFenceListActivity
import com.guider.gps.view.activity.HistoryRecordActivity
import com.guider.health.apilib.GuiderApiUtil
import com.guider.health.apilib.bean.ElectronicFenceBean
import com.guider.health.apilib.bean.ElectronicFenceListBean
import com.guider.health.apilib.bean.UserPositionListBean
import com.guider.health.apilib.enums.PositionType
import com.guider.health.apilib.utils.GsonUtil
import com.guider.health.apilib.utils.MMKVUtil
import com.kyleduo.switchbutton.SwitchButton
import kotlinx.android.synthetic.main.include_loaction_bottom_layout.*
import kotlinx.android.synthetic.main.include_location_electronic_fence_layout.*
import kotlinx.android.synthetic.main.include_location_fragment_deal_layout.*
import kotlinx.android.synthetic.main.include_location_loading_proactively_address.*
import kotlinx.android.synthetic.main.include_location_position_layout.*
import kotlinx.android.synthetic.main.include_location_proactively_addressing.*
import kotlinx.android.synthetic.main.include_location_track_event_layout.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.ref.WeakReference
import java.util.*
import java.util.Locale


class LocationFragment : BaseFragment(),
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener {

    override val layoutRes: Int
        get() = R.layout.fragment_location

    private var tabTitleList = arrayListOf<String>()

    private var mGoogleMap: GoogleMap? = null

    private var locationExtendIsOpen = false

    private var tabPosition = 0

    private var trackEventTimePosition = -1

    private var loadView: LoadingView? = null

    private lateinit var trackEventAdapter: LocationTrackEventTimeAdapter

    private var trackEventTimeList = arrayListOf<WithSelectBaseBean>()

    private var selectDateDialog: DialogHolder? = null

    private var mLastLocation: Location? = null

    private var mGoogleApiClient: GoogleApiClient? = null

    private var mLocationRequest: LocationRequest? = null

    private var isStart = false

    // 是否是第一次定位
    private var isFirstLocation = true
    private var mLocationLat = 0.0
    private var mLocationLon = 0.0

    // 开始结束标志
    private var starPerth: Marker? = null
    private var endPerth: Marker? = null

    //一个临时点的mark标志
    private var tempMark: Marker? = null
    private var tempLocationBean: UserPositionListBean? = null

    //区分mark的tag标志
    private var martTag = "locationInfo"
    private var tempMarkTag = "tempLocationInfo"
    private var pathPointMarkTag = "pathPointMarkTag"

    private var firstLocationLat = 0.0

    private var firstLocationLng = 0.0

    private var line: PolylineOptions? = null

    private var polygon: PolygonOptions? = null

    private var isShowGpsMode = false
    private var customElectronicFencePointNum = 0

    //电子围栏临时点
    private var customFirstPoint: Marker? = null
    private var customFirstMartTag = "customFirstMartTag"
    private var customFirstLatLng: LatLng? = null
    private var customTwoPoint: Marker? = null
    private var customTwoMartTag = "customTwoMartTag"
    private var customTwoLatLng: LatLng? = null
    private var customThirdPoint: Marker? = null
    private var customThirdMartTag = "customThirdMartTag"
    private var customThirdLatLng: LatLng? = null
    private var customFourPoint: Marker? = null
    private var customFourMartTag = "customFourMartTag"
    private var customFourLatLng: LatLng? = null
    private var dateSelectTag = false
    private var startTimeValue = ""
    private var endTimeValue = ""
    private var mDialog1: DialogProgress? = null
    private var mDialog2: DialogProgress? = null
    private var proactivelyAddressingNum = 0

    //路径的显示方式(默认是画线)
    private var pathMethod = LocationPathShowMethodEnum.LINEWITHPOINT
    private var pathTimeWindow: Boolean = true
    private var pathSetDialog: DialogHolder? = null
    private var latLngList: ArrayList<LatLng> = arrayListOf()
    private var testTimeList = arrayListOf<String>()
    private var electronicSaveDialog: DialogHolder? = null
    private var fenceBean: ElectronicFenceListBean? = null
    private var searchPersonFlag = false
    private var latestUserPositionListBean: UserPositionListBean? = null
    private var personModeConfirmDialog: DialogHolder? = null
    private var cancelTag = false

    override fun initView(rootView: View) {
    }

    override fun initLogic() {
        tabTitleList = arrayListOf(
                resources.getString(R.string.app_main_map_location_information),
                resources.getString(R.string.app_main_map_track_events),
                resources.getString(R.string.app_main_map_electronic_fence),
        )
//                resources.getString(R.string.app_main_map_proactively_addressing))
        locationTabLayout.tabMode = TabLayout.MODE_FIXED
        // 设置选中下划线颜色
        locationTabLayout.setSelectedTabIndicatorColor(
                CommonUtils.getColor(mActivity, R.color.colorF18937))
        // 设置文本字体颜色[未选中颜色、选中颜色]
        locationTabLayout.setTabTextColors(CommonUtils.getColor(mActivity, R.color.color999999),
                CommonUtils.getColor(mActivity, R.color.color333333))
        // 设置下划线跟文本宽度一致
        locationTabLayout.isTabIndicatorFullWidth = true
        tabTitleList.forEach {
            locationTabLayout.addTab(locationTabLayout.newTab().setText(it))
        }
        locationPositionLayout.visibility = View.VISIBLE
        locationTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                dealTabSelectEvent(tab)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        initMap()
        locationFunctionExtend.setOnClickListener(this)
        electronicDeleteLayout.setOnClickListener(this)
        electronicCommitLayout.setOnClickListener(this)
        locationNumberSet.setOnClickListener(this)
        historyRecord.setOnClickListener(this)
        initTrackTimeSelect()
        trackEventsDateCalSelectIv.setOnClickListener(this)
        trackEventCalIv.setOnClickListener(this)
        startTimeTv.setOnClickListener(this)
        endTimeTv.setOnClickListener(this)
        mapLocationIv.setOnClickListener(this)
        searchLayout.setOnClickListener(this)
        trackEventsDateLeft.setOnClickListener(this)
        trackEventsDateRight.setOnClickListener(this)
        electronicFenceListLayout.setOnClickListener(this)
        positionHistoryLayout.setOnClickListener(this)
        searchPersonLayout.setOnClickListener(this)
        normalLayout.setOnClickListener(this)
        getPositionMethodSet()
    }

    /**
     * 显示定位位置的频率设置方式
     */
    private fun showPositionMethod() {
        if (searchPersonFlag) {
            searchPersonLayout.isSelected = true
            searchPersonTitle.isSelected = true
            searchPersonContentTv.isSelected = true
            normalLayout.isSelected = false
            normalTitle.isSelected = false
            normalContentTv.isSelected = false
        } else {
            searchPersonLayout.isSelected = false
            searchPersonTitle.isSelected = false
            searchPersonContentTv.isSelected = false
            normalLayout.isSelected = true
            normalTitle.isSelected = true
            normalContentTv.isSelected = true
        }
    }

    /**
     * 处理tab的切换事件
     */
    private fun dealTabSelectEvent(tab: TabLayout.Tab) {
        resetFunctionLayout()
        when (tab.text.toString()) {
            tabTitleList[0] -> {
                initFunctionShowAndDealEvent()
                locationPositionLayout.visibility = View.VISIBLE
                mGoogleMap?.setInfoWindowAdapter(
                        CustomInfoWindowAdapter(WeakReference(this)))
                closeShowDialog()
                if (loadView != null) {
                    loadView?.dismiss()
                }
                initElectronicPoint()
                tabPosition = 0
                locationNumberSet.text =
                        resources.getString(
                                R.string.app_main_map_positioning_frequency_setting)
                locationNumberSet.setTextColor(
                        CommonUtils.getColor(mActivity, R.color.color333333))
                historyRecord.text =
                        resources.getString(R.string.app_main_map_history_record)
                mGoogleMap?.clear()
                getUserLocationPointData()
            }
            tabTitleList[1] -> {
                initFunctionShowAndDealEvent()
                mapPathSetLayout.visibility = View.VISIBLE
                mapPathSetLayout.setOnClickListener(this)
                mGoogleMap?.setInfoWindowAdapter(
                        CustomInfoWindowAdapter(WeakReference(this)))
                mGoogleMap?.clear()
                closeShowDialog()
                initElectronicPoint()
                if (loadView != null) {
                    loadView?.dismiss()
                }
                tabPosition = 1
                trackEventsLayout.visibility = View.VISIBLE
                initTrackTimeSelect()
                getUserPointLineData(startTimeValue, endTimeValue)
            }
            tabTitleList[2] -> {
                initFunctionShowAndDealEvent()
                mGoogleMap?.setInfoWindowAdapter(null)
                mGoogleMap?.setOnMapClickListener {
                    //手动去编辑电子围栏
                    mapClickEvent(it)
                }
                initElectronicPoint()
                closeShowDialog()
                if (loadView != null) {
                    loadView?.dismiss()
                }
                tabPosition = 2
                electronicFenceLayout.visibility = View.VISIBLE
                starPerth = null
                endPerth = null
                line = null
                mGoogleMap?.clear()
                getElectronicFenceData()
            }
//            tabTitleList[3] -> {
//                initFunctionShowAndDealEvent()
//                mGoogleMap?.setInfoWindowAdapter(CustomInfoWindowAdapter(WeakReference(this)))
//                mGoogleMap?.clear()
//                closeShowDialog()
//                initElectronicPoint()
//                if (loadView != null) {
//                    loadView?.dismiss()
//                }
//                tabPosition = 3
//                locationInfoLayout.visibility = View.VISIBLE
//                locationNumberSet.text =
//                        resources.getString(R.string.app_main_map_send_instructions)
//                locationNumberSet.setTextColor(
//                        CommonUtils.getColor(mActivity, R.color.colorF18937))
//                historyRecord.text =
//                        resources.getString(R.string.app_main_map_address_history)
//            }
        }
    }

    private fun initFunctionShowAndDealEvent() {
        tempMark = null
        tempLocationBean = null
        closeLoadAnimation()
        mapPathSetLayout.visibility = View.GONE
        locationInfoLayout.visibility = View.GONE
        trackEventsLayout.visibility = View.GONE
        electronicFenceLayout.visibility = View.GONE
        locationPositionLayout.visibility = View.GONE
    }

    private fun startLoadAnimation() {
        closeLoadAnimation()
        loadingLayout.visibility = View.VISIBLE
        val anim = AnimationUtils.loadAnimation(context, R.anim.loading_anim)
        anim.interpolator = LinearInterpolator()
        anim.repeatCount = Animation.INFINITE
        iv_loading?.animation = anim
        iv_loading?.startAnimation(anim)
        tv_loading.text = mActivity.resources.getString(R.string.app_addressing_in_progress)
    }

    private fun closeLoadAnimation() {
        loadingLayout.visibility = View.GONE
        iv_loading?.clearAnimation()
        proactivelyAddressingNum = 0
    }

    private fun initElectronicPoint() {
        customFirstPoint = null
        customFirstLatLng = null
        customTwoPoint = null
        customTwoLatLng = null
        customThirdPoint = null
        customThirdLatLng = null
        customFourPoint = null
        customFourLatLng = null
        electronicCommitLayout.isSelected = false
        electronicCommitIv.isSelected = false
        electronicCommitTv.isSelected = false
        electronicCommitTv.isSelected = false
        electronicDeleteIv.isSelected = false
        electronicDeleteTv.isSelected = false
        electronicDeleteLayout.isSelected = false
        electronicAddIv.isSelected = false
        electronicAddTv.isSelected = false
        electronicAddLayout.isSelected = false
        electronicSetHint.visibility = View.GONE
    }


    override fun openEventBus(): Boolean {
        return true
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshLocationData(event: EventBusEvent<String>) {
        if (event.code == EventBusAction.REFRESH_USER_DATA) {
            mGoogleMap?.clear()
            when (tabPosition) {
                0 -> {
                    mGoogleMap?.setInfoWindowAdapter(
                            CustomInfoWindowAdapter(WeakReference(this)))
                    getUserLocationPointData()
                    getPositionMethodSet()
                }
                1 -> {
                    mGoogleMap?.setInfoWindowAdapter(
                            CustomInfoWindowAdapter(WeakReference(this)))
                    initTrackTimeSelect()
                    getUserPointLineData(startTimeValue, endTimeValue)
                }
                2 -> {
                    mGoogleMap?.setInfoWindowAdapter(null)
                    mGoogleMap?.clear()
                    getElectronicFenceData()
                }
            }
        }
    }

    private fun getPositionMethodSet() {
        if (MMKVUtil.containKey(BIND_DEVICE_ACCOUNT_POSITION_SEARCH_PERSON)) {
            val json = MMKVUtil.getString(BIND_DEVICE_ACCOUNT_POSITION_SEARCH_PERSON)
            val hashMap = GsonUtil.fromJson<HashMap<String, Boolean>>(json)
            val accountId = MMKVUtil.getInt(BIND_DEVICE_ACCOUNT_ID)
            if (hashMap.containsKey(accountId.toString()) && hashMap[accountId.toString()]!!) {
                searchPersonFlag = true
                showPositionMethod()
            } else {
                searchPersonFlag = false
                showPositionMethod()
            }
        } else {
            searchPersonFlag = false
            showPositionMethod()
        }
    }

    /**
     * 得到用户最近的定位点
     */
    private fun getUserLocationPointData() {
        val accountId = MMKVUtil.getInt(BIND_DEVICE_ACCOUNT_ID)
        if (accountId == 0) {
            mDialog1?.hideDialog()
            //如果没有位置的话就先跳转到台北坐标
            mGoogleMap?.clear()
            val latLng = LatLng(25.0422452, 121.5802603)
            mGoogleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 9.5f))
            return
        }
        lifecycleScope.launch {
            ApiCoroutinesCallBack.resultParse(onStart = {
                mDialog1 = DialogProgress(mActivity, null)
                mDialog1?.showDialog()
            }, block = {
                val resultBean = GuiderApiUtil.getApiService()
                        .userPosition(accountId, 1, 1, "", "")
                var dataMap = hashMapOf<String, Any>()
                if (MMKVUtil.containKey(BIND_DEVICE_LAST_LOCATION)) {
                    val json = MMKVUtil.getString(BIND_DEVICE_LAST_LOCATION)
                    dataMap = GsonUtil.fromJson(json)
                }
                if (!resultBean.isNullOrEmpty() && resultBean.size == 1) {
                    val firstPosition = resultBean[0]
                    mGoogleMap?.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                    LatLng(firstPosition.lat, firstPosition.lng), 16.0f))
                    startDisplayPerth(LatLng(firstPosition.lat, firstPosition.lng))
                    latestUserPositionListBean = firstPosition
                    if (dataMap.containsKey(accountId.toString())) {
                        val latestUserPositionListBean =
                                ParseJsonData.parseJsonAny<UserPositionListBean>(
                                        dataMap[accountId.toString()]!!)
                        //判断是否是相同的定位点
                        if (!(latestUserPositionListBean.lat == firstPosition.lat &&
                                        latestUserPositionListBean.lng
                                        == firstPosition.lng)) {
                            dataMap[accountId.toString()] = firstPosition
                        }
                    } else {
                        dataMap[accountId.toString()] = firstPosition
                    }
                } else {
                    latestUserPositionListBean = null
                    //如果没有位置的话就先跳转到台北坐标
                    mGoogleMap?.clear()
                    val latLng = LatLng(25.0422452, 121.5802603)
                    mGoogleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 9.5f))
                    if (dataMap.containsKey(accountId.toString())) {
                        dataMap.remove(accountId.toString())
                    }
                }
                val toJson = GsonUtil.toJson(dataMap)
                MMKVUtil.saveString(BIND_DEVICE_LAST_LOCATION, toJson)
            }, onRequestFinish = {
                mDialog1?.hideDialog()
            })
        }
    }

    /**
     * 得到用户行动轨迹
     */
    private fun getUserPointLineData(startTimeValue: String, endTimeValue: String) {
        val accountId = MMKVUtil.getInt(BIND_DEVICE_ACCOUNT_ID)
        if (accountId == 0) {
            return
        }
        Log.i("getUserPointDataTime", "start$startTimeValue-----end$endTimeValue")
        lifecycleScope.launch {
            ApiCoroutinesCallBack.resultParse(onStart = {
                mDialog2 = DialogProgress(mActivity, null)
                mDialog2?.showDialog()
            }, block = {
                var resultBean = GuiderApiUtil.getApiService()
                        .userPosition(accountId, -1, 20,
                                DateUtilKotlin.localToUTC(startTimeValue)!!,
                                DateUtilKotlin.localToUTC(endTimeValue)!!)
                latLngList = arrayListOf()
                testTimeList = arrayListOf()
                mGoogleMap?.clear()
                if (!resultBean.isNullOrEmpty() && resultBean.size > 1) {
                    resultBean = resultBean.reversed()
                    resultBean.forEach {
                        latLngList.add(LatLng(it.lat, it.lng))
                    }
                    testTimeList = resultBean.map {
                        DateUtilKotlin.uTCToLocal(
                                it.testTime, DEFAULT_TIME_FORMAT_PATTERN)!!
                    } as ArrayList<String>
                    Log.i(TAG, "定位轨迹的数量${resultBean.size}")
                    showPathWithMethod()
                } else if (!resultBean.isNullOrEmpty() && resultBean.size == 1) {
                    //一个时候不划线是因为要做轨迹方式的判断使用
                    latLngList.add(LatLng(resultBean[0].lat, resultBean[0].lng))
                    testTimeList.add(DateUtilKotlin.uTCToLocal(
                            resultBean[0].testTime, DEFAULT_TIME_FORMAT_PATTERN)!!)
                    showPathWithMethod()
                }
            }, onRequestFinish = {
                mDialog2?.hideDialog()
            })
        }
    }

    /**
     * 获取设备的电子围栏信息
     */
    private fun getElectronicFenceData() {
        val accountId = MMKVUtil.getInt(BIND_DEVICE_ACCOUNT_ID)
        if (accountId == 0) {
            return
        }
        lifecycleScope.launch {
            ApiCoroutinesCallBack.resultParse(onStart = {
                mActivity.showDialog()
            }, block = {
                val resultBean = GuiderApiUtil.getApiService()
                        .getElectronicFence(accountId, 1, true)
                if (resultBean is String && resultBean == "null") {
                    electronicAddLayout.post {
                        showGuideView()
                    }
                } else {
                    val fenceList = ParseJsonData.parseJsonDataList<ElectronicFenceListBean>(
                            resultBean, ElectronicFenceListBean::class.java)
                    fenceBean = fenceList[0]
                    val asList = fenceList[0].pointList
                    if (asList.size < 4) {
                        electronicAddLayout.post {
                            showGuideView()
                        }
                    } else {
                        drawHavedElectronicFence(asList)
                    }
                }
            }, onRequestFinish = {
                mActivity.dismissDialog()
            })
        }
    }

    /**
     * 画已有电子围栏
     */
    private fun drawHavedElectronicFence(asList: List<ElectronicFenceBean>) {
        electronicSetHint.visibility = View.GONE
        customFirstLatLng = LatLng(
                asList[0].lat, asList[0].lng)
        customTwoLatLng = LatLng(
                asList[1].lat, asList[1].lng)
        customThirdLatLng = LatLng(
                asList[2].lat, asList[2].lng)
        customFourLatLng = LatLng(
                asList[3].lat, asList[3].lng)
        drawCustomFirstPoint(customFirstLatLng!!)
        drawCustomTwoPoint(customTwoLatLng!!)
        drawCustomThirdPoint(customThirdLatLng!!)
        drawFourPointAndPolygon(customFourLatLng!!)
        electronicCommitLayout.isSelected = true
        electronicCommitIv.isSelected = true
        electronicCommitTv.isSelected = true
        electronicDeleteIv.isSelected = true
        electronicDeleteTv.isSelected = true
        electronicDeleteLayout.isSelected = true
        customElectronicFencePointNum = 4
        mGoogleMap?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(customFirstLatLng, 16.0f))
    }

    /**
     * 显示电子围栏高亮提示
     */
    private fun showGuideView() {
        electronicAddLayout.isSelected = true
        electronicAddIv.isSelected = true
        electronicAddTv.isSelected = true
        electronicSetClickEvent()
//        val builder = GuideBuilder()
//        builder.setTargetView(electronicAddLayout)
//                .setAlpha(150)
//                .setHighTargetCorner(20)
//                .setHighTargetPadding(10)
//        builder.setOnVisibilityChangedListener(object : GuideBuilder.OnVisibilityChangedListener {
//            override fun onShown() {}
//            override fun onDismiss() {
//                electronicAddLayout.isSelected = true
//                electronicSetIv.isSelected = true
//                electronicSetClickEvent()
//            }
//        })
//        builder.addComponent(SimpleComponent())
//        val guide = builder.createGuide()
//        guide.show(mActivity)
    }

    /**
     * 设置电子围栏信息
     */
    private fun setElectronicFenceData(electronicName: String) {
        if (fenceBean == null) {
            val deviceCode = MMKVUtil.getString(BIND_DEVICE_CODE)
            val accountId = MMKVUtil.getInt(BIND_DEVICE_ACCOUNT_ID)
            if (StringUtil.isEmpty(deviceCode)) return
            fenceBean = ElectronicFenceListBean(accountId, deviceCode, 0, true,
                    arrayListOf(), "")
        }
        fenceBean!!.title = electronicName
        fenceBean!!.pointList = arrayListOf(
                ElectronicFenceBean(customFirstLatLng!!.latitude, customFirstLatLng!!.longitude),
                ElectronicFenceBean(customTwoLatLng!!.latitude, customTwoLatLng!!.longitude),
                ElectronicFenceBean(customThirdLatLng!!.latitude, customThirdLatLng!!.longitude),
                ElectronicFenceBean(customFourLatLng!!.latitude, customFourLatLng!!.longitude))
        lifecycleScope.launch {
            ApiCoroutinesCallBack.resultParse(onStart = {
                mActivity.showDialog()
            }, block = {
                val resultBean = GuiderApiUtil.getApiService().setElectronicFence(fenceBean!!)
                if (resultBean == "true") {
                    ToastUtil.showCenterLong(mActivity,
                            mActivity.resources.getString(R.string.app_set_success))
                }
            }, onError = {
                fenceBean = null
            }) {
                mActivity.dismissDialog()
                electronicSaveDialog?.closeDialog()
                electronicSaveDialog = null
            }
        }
    }

    /**
     * 初始化活动轨迹的时间列表的adapter
     */
    private fun initTrackEventTimeAdapter() {
        trackEventTimeList.clear()
        trackEventTimePosition = -1
        trackEventsTimeRv.layoutManager = LinearLayoutManager(
                mActivity, LinearLayoutManager.HORIZONTAL, false)
        arrayListOf("1", "2", "6", "12", "24").forEach {
            trackEventTimeList.add(WithSelectBaseBean(it, false))
        }
        trackEventAdapter = LocationTrackEventTimeAdapter(mActivity, trackEventTimeList)
        trackEventAdapter.setListener(object : AdapterOnItemClickListener {
            override fun onClickItem(position: Int) {
                if (trackEventTimePosition != position && !dateSelectTag) {
                    if (trackEventTimePosition != -1)
                        trackEventTimeList[trackEventTimePosition].isSelect = false
                    trackEventTimeList[position].isSelect = true
                    if (trackEventTimePosition != -1)
                        trackEventAdapter.notifyItemChanged(trackEventTimePosition)
                    trackEventAdapter.notifyItemChanged(position)
                    trackEventTimePosition = position
                    val timeHour = trackEventTimeList[trackEventTimePosition].name.toInt()
                    val currentDateString = CommonUtils.getCurrentDate(DEFAULT_TIME_FORMAT_PATTERN)
                    startTimeValue = CommonUtils.calTimeFrontHour(currentDateString, timeHour)
                    endTimeValue = CommonUtils.getCurrentDate(DEFAULT_TIME_FORMAT_PATTERN)
                    getUserPointLineData(startTimeValue, endTimeValue)
                }
            }

        })
        trackEventsTimeRv.adapter = trackEventAdapter
    }

    /**
     * 重置功能区布局
     */
    private fun resetFunctionLayout() {
        locationInfoLayout.visibility = View.GONE
        trackEventsLayout.visibility = View.GONE
        electronicFenceLayout.visibility = View.GONE
    }

    override fun onNoDoubleClick(v: View) {
        when (v) {
            locationFunctionExtend -> {
                if (locationExtendIsOpen) {
                    extendIv.setImageResource(R.drawable.icon_below_arrow_location)
                    extendFunctionLayout.visibility = View.GONE
                } else {
                    extendIv.setImageResource(R.drawable.icon_up_arrow_location)
                    extendFunctionLayout.visibility = View.VISIBLE
                }
                locationExtendIsOpen = !locationExtendIsOpen
            }
            locationNumberSet -> {
                val accountId = MMKVUtil.getInt(BIND_DEVICE_ACCOUNT_ID)
                if (accountId == 0) {
                    return
                }
                //正在寻址过程中
                if (locationNumberSet.text == mActivity.resources.getString(
                                R.string.app_cancel_the_addressing)) {
                    proactivelyAddressingFinish("cancelSuccess")
                    locationNumberSet.text =
                            mActivity.resources.getString(
                                    R.string.app_main_map_send_instructions)
                    return
                }
                //发起主动寻址
                initiateActiveAddressing(accountId)
            }
            searchPersonLayout -> {
                //已经是寻人模式，就不需要再去设置了
                if (searchPersonFlag) return
                confirmSelectPersonDialog()
            }
            normalLayout -> {
                //已经是一般模式，就不需要再去设置了
                if (!searchPersonFlag) return
                commitPositionMethodSet()
            }
            historyRecord -> {
                val intent = Intent(mActivity, HistoryRecordActivity::class.java)
                intent.putExtra("entryType", "proactivelyAddressHistory")
                startActivityForResult(intent, HISTORY_RECORD_LOCATION)
            }
            positionHistoryLayout -> {
                val intent = Intent(mActivity, HistoryRecordActivity::class.java)
                intent.putExtra("entryType", "locationHistory")
                startActivityForResult(intent, HISTORY_RECORD_LOCATION)
            }
            trackEventsDateCalSelectIv -> {
                if (trackEventsDateValueTv.tag !is String) return
                searchFrontLayout.visibility = View.GONE
                selectTimeBackLayout.visibility = View.VISIBLE
                dateSelectTag = trackEventsDateValueTv.tag !=
                        CommonUtils.getCurrentDate(TIME_FORMAT_PATTERN6)
            }
            startTimeTv, endTimeTv -> {
                initSelectDateDialogShow()
            }
            searchLayout -> {
                startTimeValue = "${startTimeTv.text} 00:00:00"
                endTimeValue = "${endTimeTv.text} 24:00:00"
                getUserPointLineData(startTimeValue, endTimeValue)
            }
            trackEventsDateLeft -> {
                if (trackEventsDateValueTv.tag !is String) return
                dateSelectTag = true
                val currentDate = trackEventsDateValueTv.tag as String
                val calTimeFrontDate = CommonUtils.calTimeFrontDay(
                        currentDate, 1, TIME_FORMAT_PATTERN6)
                //切到了当前日期
                if (StringUtil.isNotBlankAndEmpty(calTimeFrontDate)
                        && calTimeFrontDate == CommonUtils.getCurrentDate(TIME_FORMAT_PATTERN6)) {
                    dateSelectTag = false
                }
                trackEventsDateValueTv.text = getDateShowWithLanguage(
                        DateUtil.stringToDate(calTimeFrontDate, TIME_FORMAT_PATTERN6))
                trackEventsDateValueTv.tag = DateUtil.stringToDate(calTimeFrontDate,
                        TIME_FORMAT_PATTERN6)
                val dateString = calTimeFrontDate
                        .replace("年", "-")
                        .replace("月", "-")
                        .replace("日", "")
                startTimeValue = "$dateString 00:00:00"
                endTimeValue = "$dateString 24:00:00"
                getUserPointLineData(startTimeValue, endTimeValue)
            }
            trackEventsDateRight -> {
                if (trackEventsDateValueTv.tag !is String) return
                dateSelectTag = true
                val currentDate = trackEventsDateValueTv.tag as String
                val calTimeFrontDate = CommonUtils.calTimeFrontDay(
                        currentDate, -1, TIME_FORMAT_PATTERN6)
                trackEventsDateValueTv.text = getDateShowWithLanguage(
                        DateUtil.stringToDate(calTimeFrontDate, TIME_FORMAT_PATTERN6))
                trackEventsDateValueTv.tag = DateUtil.stringToDate(calTimeFrontDate,
                        TIME_FORMAT_PATTERN6)
                //切到了当前日期
                if (StringUtil.isNotBlankAndEmpty(calTimeFrontDate)
                        && calTimeFrontDate == CommonUtils.getCurrentDate(TIME_FORMAT_PATTERN6)) {
                    dateSelectTag = false
                }
                val dateString = calTimeFrontDate
                        .replace("年", "-")
                        .replace("月", "-")
                        .replace("日", "")
                startTimeValue = "$dateString 00:00:00"
                endTimeValue = "$dateString 24:00:00"
                getUserPointLineData(startTimeValue, endTimeValue)
            }
            trackEventCalIv -> {
                initTrackTimeSelect()
                getUserPointLineData(startTimeValue, endTimeValue)
            }
            mapLocationIv -> {
                if (isShowGpsMode) {
                    mGoogleMap?.mapType = GoogleMap.MAP_TYPE_NORMAL//设置为正常地图模式
                } else {
                    mGoogleMap?.mapType = GoogleMap.MAP_TYPE_SATELLITE//设置为卫星模式
                }
                isShowGpsMode = !isShowGpsMode
            }
            //删除电子围栏在地图上坐标点
            electronicDeleteLayout -> {
                if (customElectronicFencePointNum in 1..4) {
                    when (customElectronicFencePointNum) {
                        1 -> {
                            electronicDeleteLayout.isSelected = false
                            electronicDeleteIv.isSelected = false
                            electronicDeleteTv.isSelected = false
                            customFirstPoint?.remove()
                        }
                        2 -> {
                            customTwoPoint?.remove()
                        }
                        3 -> {
                            customThirdPoint?.remove()
                        }
                        4 -> {
                            if (mGoogleMap == null) return
                            electronicCommitLayout.isSelected = false
                            electronicCommitIv.isSelected = false
                            electronicCommitTv.isSelected = false
                            electronicCommitTv.isSelected = false
                            electronicSetHint.visibility = View.VISIBLE
                            electronicAddLayout.isSelected = true
                            electronicAddIv.isSelected = true
                            electronicAddTv.isSelected = true
                            customFourPoint?.remove()
                            mGoogleMap?.clear()
                            drawCustomFirstPoint(customFirstLatLng!!)
                            drawCustomTwoPoint(customTwoLatLng!!)
                            drawCustomThirdPoint(customThirdLatLng!!)
                        }
                    }
                    customElectronicFencePointNum--
                }
            }
            //提交电子围栏信息
            electronicCommitLayout -> {
                if (customElectronicFencePointNum == 4 && electronicSaveDialog == null) {
                    saveElectronicFenceDialog()
                } else {
                    showToast(mActivity.resources.getString(R.string.app_electronic_hint))
                }
            }
            //行动轨迹显示方式的设置
            mapPathSetLayout -> {
                //保证有2个点以上设置才有意义
                if (pathSetDialog == null && latLngList.size > 1) {
                    pathMethodSetDialogShow()
                }
            }
            //电子围栏列表
            electronicFenceListLayout -> {
                val intent = Intent(mActivity, ElectronicFenceListActivity::class.java)
                startActivityForResult(intent, ELECTRONIC_FENCE_LIST)
            }
        }
    }

    private fun confirmSelectPersonDialog() {
        personModeConfirmDialog = object : DialogHolder(mActivity,
                R.layout.dialog_search_person_mode_confirm, Gravity.CENTER), View.OnClickListener {
            override fun bindView(dialogView: View) {
                val cancelTv = dialogView.findViewById<TextView>(R.id.cancelLayout)
                val confirmTv = dialogView.findViewById<TextView>(R.id.confirmLayout)
                cancelTv.setOnClickListener(this)
                confirmTv.setOnClickListener(this)
            }

            override fun onClick(v: View) {
                when (v.id) {
                    R.id.cancelLayout -> {
                        dialog?.dismiss()
                        personModeConfirmDialog = null
                    }
                    R.id.confirmLayout -> {
                        dialog?.dismiss()
                        personModeConfirmDialog = null
                        commitPositionMethodSet()
                    }
                }
            }
        }
        personModeConfirmDialog?.initView()
        personModeConfirmDialog?.show(true)
    }

    /**
     * 恢复初始的轨迹时间选择
     */
    private fun initTrackTimeSelect() {
        trackEventsDateValueTv.text = getDateShowWithLanguage()
        trackEventsDateValueTv.tag = CommonUtils.getCurrentDate(TIME_FORMAT_PATTERN6)
        startTimeTv.text = CommonUtils.calTimeFrontDay(
                CommonUtils.getCurrentDate(), 7)
        endTimeTv.text = CommonUtils.getCurrentDate()
        selectTimeBackLayout.visibility = View.GONE
        searchFrontLayout.visibility = View.VISIBLE
        val currentDateString = CommonUtils.getCurrentDate()
        startTimeValue = "$currentDateString 00:00:00"
        endTimeValue = CommonUtils.getCurrentDate(DEFAULT_TIME_FORMAT_PATTERN)
        dateSelectTag = false
        initTrackEventTimeAdapter()
    }


    private fun commitPositionMethodSet() {
        searchPersonFlag = !searchPersonFlag
        val accountId = MMKVUtil.getInt(BIND_DEVICE_ACCOUNT_ID)
        if (accountId == 0) {
            return
        }
        val positionType = if (searchPersonFlag) {
            PositionType.FINDER
        } else {
            PositionType.COMMON
        }
        lifecycleScope.launch {
            ApiCoroutinesCallBack.resultParse(onStart = {
                mActivity.showDialog()
            }, block = {
                GuiderApiUtil.getApiService().setLocationFrequency(
                        accountId, positionType)
                showPositionMethod()
                if (searchPersonFlag) {
                    ToastUtil.showCustomToast(null, mActivity, false,
                            mActivity.resources.getString(
                                    R.string.app_set_location_search_person_success),
                            R.drawable.rounded_99000000_5_bg, Gravity.BOTTOM,
                            ScreenUtils.dip2px(mActivity, 83f)
                    )
                } else {
                    showToast(mActivity.resources.getString(R.string.app_set_success))
                }
                var dataMap = hashMapOf<String, Boolean>()
                if (MMKVUtil.containKey(BIND_DEVICE_ACCOUNT_POSITION_SEARCH_PERSON)) {
                    val json = MMKVUtil.getString(BIND_DEVICE_ACCOUNT_POSITION_SEARCH_PERSON)
                    dataMap = GsonUtil.fromJson(json)
                }
                dataMap[accountId.toString()] = searchPersonFlag
                val toJson = GsonUtil.toJson(dataMap)
                MMKVUtil.saveString(BIND_DEVICE_ACCOUNT_POSITION_SEARCH_PERSON, toJson)
                //刷新群组列表中的寻人模式标识
                EventBusUtils.sendEvent(
                        EventBusEvent(EventBusAction.REFRESH_LATEST_GROUP_DATA, true))
            }, onError = {
                searchPersonFlag = !searchPersonFlag
            }) {
                mActivity.dismissDialog()
            }
        }
    }

    private fun saveElectronicFenceDialog() {
        electronicSaveDialog = object : DialogHolder(mActivity,
                R.layout.dialog_electronic_set_save, Gravity.CENTER), View.OnClickListener {
            lateinit var electronicNameEdit: EditText
            override fun bindView(dialogView: View) {
                val cancelIv = dialogView.findViewById<ConstraintLayout>(R.id.cancelLayout)
                val confirmLayout = dialogView.findViewById<ConstraintLayout>(R.id.confirmLayout)
                electronicNameEdit = dialogView.findViewById(R.id.electronicNameEdit)
                if (StringUtil.isNotBlankAndEmpty(fenceBean?.title)) {
                    electronicNameEdit.setText(fenceBean?.title)
                }
                cancelIv.setOnClickListener(this)
                confirmLayout.setOnClickListener(this)
            }

            override fun onClick(v: View) {
                when (v.id) {
                    R.id.cancelLayout -> {
                        dialog?.dismiss()
                        electronicSaveDialog = null
                    }
                    R.id.confirmLayout -> {
                        val electronicName = electronicNameEdit.text.toString()
                        if (StringUtil.isEmpty(electronicName)) return
                        setElectronicFenceData(electronicName)
                    }
                }
            }
        }
        electronicSaveDialog?.initView()
        electronicSaveDialog?.show(true)
    }

    @SuppressLint("StaticFieldLeak")
    private fun pathMethodSetDialogShow() {
        pathSetDialog = object : DialogHolder(mActivity,
                R.layout.dialog_path_method_set, Gravity.CENTER), View.OnClickListener {
            lateinit var lineSelectIv: ImageView
            lateinit var pointSelectIv: ImageView
            lateinit var pointWithLineSelectIv: ImageView
            lateinit var lineSelectTv: TextView
            lateinit var pointSelectTv: TextView
            lateinit var pointWithLineSelectTv: TextView
            lateinit var timeWindowSwitch: SwitchButton
            override fun bindView(dialogView: View) {
                val cancelIv = dialogView.findViewById<ConstraintLayout>(R.id.cancelLayout)
                val confirmLayout = dialogView.findViewById<ConstraintLayout>(R.id.confirmLayout)
                lineSelectIv = dialogView.findViewById(R.id.lineSelectIv)
                pointSelectIv = dialogView.findViewById(R.id.pointSelectIv)
                pointWithLineSelectIv = dialogView.findViewById(
                        R.id.pointWithLineSelectIv)
                lineSelectTv = dialogView.findViewById(R.id.lineSelectTv)
                pointSelectTv = dialogView.findViewById(R.id.pointSelectTv)
                pointWithLineSelectTv = dialogView.findViewById(
                        R.id.pointWithLineSelectTv)
                timeWindowSwitch = dialogView.findViewById(
                        R.id.timeWindowSwitch)
                when (pathMethod) {
                    LocationPathShowMethodEnum.LINE -> {
                        initMethodSetStatus(lineSelectIv, pointSelectIv, pointWithLineSelectIv,
                                lineSelectIv)
                    }
                    LocationPathShowMethodEnum.POINT -> {
                        initMethodSetStatus(lineSelectIv, pointSelectIv, pointWithLineSelectIv,
                                pointSelectIv)
                    }
                    LocationPathShowMethodEnum.LINEWITHPOINT -> {
                        initMethodSetStatus(lineSelectIv, pointSelectIv, pointWithLineSelectIv,
                                pointWithLineSelectIv)
                    }
                }
                cancelIv.setOnClickListener(this)
                lineSelectIv.setOnClickListener(this)
                pointSelectIv.setOnClickListener(this)
                pointWithLineSelectIv.setOnClickListener(this)
                lineSelectTv.setOnClickListener(this)
                pointSelectTv.setOnClickListener(this)
                pointWithLineSelectTv.setOnClickListener(this)
                confirmLayout.setOnClickListener(this)
                timeWindowSwitch.setCheckedNoEvent(pathTimeWindow)
            }

            private fun initMethodSetStatus(lineSelectIv: ImageView,
                                            pointSelectIv: ImageView,
                                            pointWithLineSelectIv: ImageView,
                                            selectView: ImageView) {
                lineSelectIv.isSelected = false
                pointSelectIv.isSelected = false
                pointWithLineSelectIv.isSelected = false
                selectView.isSelected = true
            }

            override fun onClick(v: View) {
                when (v.id) {
                    R.id.cancelLayout -> {
                        dialog?.dismiss()
                        pathSetDialog = null
                    }
                    R.id.confirmLayout -> {
                        when {
                            lineSelectIv.isSelected -> {
                                pathMethod = LocationPathShowMethodEnum.LINE
                            }
                            pointSelectIv.isSelected -> {
                                pathMethod = LocationPathShowMethodEnum.POINT
                            }
                            pointWithLineSelectIv.isSelected -> {
                                pathMethod = LocationPathShowMethodEnum.LINEWITHPOINT
                            }
                        }
                        pathTimeWindow = timeWindowSwitch.isChecked
                        showToast(mActivity.resources.getString(R.string.app_set_success))
                        dialog?.dismiss()
                        pathSetDialog = null
                        showPathWithMethod()
                    }
                    R.id.lineSelectIv, R.id.lineSelectTv -> {
                        initMethodSetStatus(lineSelectIv, pointSelectIv,
                                pointWithLineSelectIv, lineSelectIv)
                    }
                    R.id.pointSelectIv, R.id.pointSelectTv -> {
                        initMethodSetStatus(lineSelectIv, pointSelectIv,
                                pointWithLineSelectIv, pointSelectIv)
                    }
                    R.id.pointWithLineSelectIv, R.id.pointWithLineSelectTv -> {
                        initMethodSetStatus(lineSelectIv, pointSelectIv,
                                pointWithLineSelectIv, pointWithLineSelectIv)
                    }
                }
            }
        }
        pathSetDialog?.initView()
        pathSetDialog?.show(true)
    }

    private fun showPathWithMethod() {
        if (latLngList.isNullOrEmpty()) return
        if (latLngList.size == 1) {
            drawLocationPathPoints(0)
        } else {
            when (pathMethod) {
                LocationPathShowMethodEnum.LINE -> {
                    drawLocationPathPoints(0)
                    drawLocationPathPoints(latLngList.size - 1)
                    drawLocationPathLine()
                }
                LocationPathShowMethodEnum.POINT -> {
                    for (i in latLngList.indices) {
                        drawLocationPathPoints(i)
                    }
                }
                LocationPathShowMethodEnum.LINEWITHPOINT -> {
                    for (i in latLngList.indices) {
                        drawLocationPathPoints(i)
                    }
                    drawLocationPathLine()
                }
            }
        }
        mGoogleMap?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(latLngList[latLngList.size - 1], 14.0f))
    }

    /**
     * 发起主动寻址
     */
    private fun initiateActiveAddressing(accountId: Int) {
        lifecycleScope.launch {
            ApiCoroutinesCallBack.resultParse(onStart = {
                mActivity.showDialog()
            }, block = {
                val resultBean = GuiderApiUtil.getApiService().initiateActiveAddressing(accountId)
                if (resultBean == "true") {
                    proactivelyAddressingLoad(accountId)
                }
                ToastUtil.showCustomToast(null, mActivity,
                        true, if (resultBean == "true")
                    mActivity.resources.getString(R.string.app_main_send_success)
                else mActivity.resources.getString(R.string.app_main_send_fail))
            }, onError = {
                ToastUtil.showCustomToast(null, mActivity,
                        true,
                        mActivity.resources.getString(R.string.app_main_location_send_fail))
            }) {
                mActivity.dismissDialog()
            }
        }
    }

    /**
     * 轮询主动寻址
     */
    private fun proactivelyAddressingLoad(accountId: Int) {
        startLoadAnimation()
        cancelTag = false
        locationNumberSet.text = mActivity.resources.getString(R.string.app_cancel_the_addressing)
        val time = DateUtilKotlin.localToUTC(
                DateUtil.localNowStringByPattern(DEFAULT_TIME_FORMAT_PATTERN))!!
        lifecycleScope.launch {
            flow {
                for (i in 1..24) {
                    delay(1_000 * 5)
                    if (!cancelTag) {
                        emit(i)
                    } else return@flow
                }
            }.map {
                proactivelyAddressingNum = it
                Log.i(TAG, "轮询主动寻址执行了${proactivelyAddressingNum}次")
                GuiderApiUtil.getApiService().proactivelyAddressingData(
                        accountId, time)
            }.catch {
                proactivelyAddressingFinish("fail")
                cancelTag = true
            }.onCompletion {
                Log.i(TAG, "flowOnCompletion")
                if (cancelTag) return@onCompletion
                proactivelyAddressingFinish("fail")
                cancelTag = true
            }.flowOn(Dispatchers.IO)
                    .collectLatest {
                        if (!(it is String && it == "null") && it != null) {
                            tempLocationBean = ParseJsonData.parseJsonAny<UserPositionListBean>(it)
                            if (tempLocationBean == null) return@collectLatest
                            mGoogleMap?.animateCamera(
                                    CameraUpdateFactory.newLatLngZoom(LatLng(tempLocationBean!!.lat,
                                            tempLocationBean!!.lng), 16.0f))
                            startDisplayPerth(LatLng(tempLocationBean!!.lat, tempLocationBean!!.lng))
                            proactivelyAddressingFinish("success")
                            cancelTag = true
                        }
                    }
        }
    }

    private fun proactivelyAddressingFinish(result: String) {
        closeLoadAnimation()
        if (result == "cancelSuccess") {
            ToastUtil.showCustomToast(null, mActivity,
                    true,
                    mActivity.resources.getString(R.string.app_cancel_the_addressing_success)
            )
        } else {
            ToastUtil.showCustomToast(null, mActivity,
                    true, if (result == "success")
                mActivity.resources.getString(R.string.app_main_map_addressing_success)
            else mActivity.resources.getString(R.string.app_main_map_addressing_fail))
        }
    }

    override fun onPause() {
        super.onPause()
        //防止主动寻址进行中未终止
        closeLoadAnimation()
        if (locationNumberSet.text == mActivity.resources.getString(
                        R.string.app_cancel_the_addressing)) {
            locationNumberSet.text =
                    mActivity.resources.getString(
                            R.string.app_main_map_send_instructions)
        }
    }

    /**
     * 电子围栏设置点击事件
     */
    private fun electronicSetClickEvent() {
        if (customElectronicFencePointNum == 0)
            mGoogleMap?.clear()
        electronicSetHint.visibility = View.VISIBLE
    }

    /**
     * 初始化选择日期对话框
     */
    private fun initSelectDateDialogShow() {
        selectDateDialog = object : DialogHolder(mActivity,
                R.layout.dialog_location_select_date, Gravity.BOTTOM) {
            override fun bindView(dialogView: View) {
                val cancelIv = dialogView.findViewById<ImageView>(R.id.cancelIv)
                cancelIv.setOnClickListener {
                    dialog?.dismiss()
                }
                val calendarListView = dialogView.findViewById<CalendarList>(R.id.calendarListView)
                calendarListView.setOnDateSelectedListener(object : CalendarList.OnDateSelected {
                    override fun selected(startDate: String?, endDate: String?) {
                        calendarListView.postDelayed({
                            dialog?.dismiss()
                            startTimeTv.text = startDate
                            endTimeTv.text = endDate
                        }, 500)
                    }

                })
            }
        }
        selectDateDialog?.initView()
        selectDateDialog?.show(true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                LOCATION_FREQUENCY_SET -> {
                    showToast(mActivity.resources.getString(R.string.app_set_success))
                }
                HISTORY_RECORD_LOCATION -> {
                    tempLocationBean =
                            data.getSerializableExtra("clickLatLng") as UserPositionListBean?
                    if (tempLocationBean == null) return
                    mGoogleMap?.clear()
                    val latLng = LatLng(tempLocationBean!!.lat, tempLocationBean!!.lng)
                    mGoogleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f))
                    startDisplayPerth(latLng, true)
                }
                ELECTRONIC_FENCE_LIST -> {
                    if (data.getSerializableExtra("info") == null) return
                    fenceBean = data.getSerializableExtra("info") as ElectronicFenceListBean?
                    if (fenceBean?.pointList.isNullOrEmpty()) {
                        fenceBean = null
                        //初始化电子围栏
                        initElectronicPoint()
                        customElectronicFencePointNum = 0
                        showGuideView()
                    } else {
                        customElectronicFencePointNum = 0
                        electronicSetClickEvent()
                        drawHavedElectronicFence(fenceBean!!.pointList)
                    }
                }
            }
        }
    }

    /**
     * 初始化地图
     */
    private fun initMap() {
        mGoogleApiClient = GoogleApiClient.Builder(mActivity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
        var mapFragment = childFragmentManager.findFragmentById(R.id.mMapView)
        if (mapFragment != null) {
            mapFragment = mapFragment as SupportMapFragment
            mapFragment.getMapAsync(this)
        }
    }

    /**
     * 地图完成绘制监听
     */
    override fun onMapReady(it: GoogleMap?) {
        mGoogleMap = it
        mGoogleMap?.mapType = GoogleMap.MAP_TYPE_NORMAL
        //隐藏地图上的商家poi图标和公共交通标识
        mGoogleMap?.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        mActivity, R.raw.style_json))
        var dataMap = hashMapOf<String, Any>()
        if (MMKVUtil.containKey(BIND_DEVICE_LAST_LOCATION)) {
            val json = MMKVUtil.getString(BIND_DEVICE_LAST_LOCATION)
            dataMap = GsonUtil.fromJson(json)
        }
        val accountId = MMKVUtil.getInt(BIND_DEVICE_ACCOUNT_ID)
        if (accountId != 0) {
            if (dataMap.containsKey(accountId.toString())) {
                latestUserPositionListBean =
                        ParseJsonData.parseJsonAny<UserPositionListBean>(
                                dataMap[accountId.toString()]!!)
                val latLng = LatLng(latestUserPositionListBean!!.lat,
                        latestUserPositionListBean!!.lng)
                startDisplayPerth(latLng)
                mGoogleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f))
            } else {
                getUserLocationPointData()
            }
        }
        val uiSettings = mGoogleMap!!.uiSettings
        uiSettings.isMyLocationButtonEnabled = false //显示定位按钮
        uiSettings.isCompassEnabled = false //设置是否显示指南针
        uiSettings.isZoomControlsEnabled = true//缩放控件
        mGoogleMap?.setInfoWindowAdapter(
                CustomInfoWindowAdapter(WeakReference(this)))
        mGoogleMap?.setOnMarkerClickListener(this)
        mGoogleMap?.setOnInfoWindowClickListener(this)
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = 1000
        mLocationRequest!!.fastestInterval = 1000
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    /**
     * map点击事件处理
     * 当前仅做了电子围栏相关点击处理逻辑
     */
    private fun mapClickEvent(latLng: LatLng) {
        if (tabPosition == 2) {
            if (customElectronicFencePointNum < 4) {
                customElectronicFencePointNum++
                if (customElectronicFencePointNum == 4) {
                    if (mGoogleMap == null) return
                    customFourLatLng = latLng
                    drawFourPointAndPolygon(customFourLatLng!!)
                    electronicCommitLayout.isSelected = true
                    electronicCommitIv.isSelected = true
                    electronicCommitTv.isSelected = true
                    electronicCommitTv.isSelected = true
                    electronicSetHint.visibility = View.GONE
                    electronicAddLayout.isSelected = false
                    electronicAddIv.isSelected = false
                    electronicAddTv.isSelected = false
                } else {
                    if (mGoogleMap == null) return
                    when (customElectronicFencePointNum) {
                        1 -> {
                            customFirstLatLng = latLng
                            drawCustomFirstPoint(customFirstLatLng!!)
                            electronicDeleteLayout.isSelected = true
                            electronicDeleteIv.isSelected = true
                            electronicDeleteTv.isSelected = true
                        }
                        2 -> {
                            customTwoLatLng = latLng
                            drawCustomTwoPoint(customTwoLatLng!!)
                        }
                        3 -> {
                            customThirdLatLng = latLng
                            drawCustomThirdPoint(customThirdLatLng!!)
                        }
                    }
                }
            } else return
        }
    }

    //绘制自定义的电子围栏点 开始
    /**
     * 绘制第四点和区域
     */
    private fun drawFourPointAndPolygon(latLng: LatLng) {
        val bitmap = BitmapDescriptorFactory.fromResource(
                R.drawable.icon_custom_four_point)
        customFourPoint = mGoogleMap?.addMarker(MarkerOptions()
                .draggable(false).icon(bitmap).position(
                        LatLng(latLng.latitude, latLng.longitude)))
        customFourPoint?.tag = customFourMartTag
        customFourPoint?.isDraggable = false //设置不可移动
        val tempList = arrayListOf<LatLng>()
        tempList.add(LatLng(customFirstLatLng?.latitude!!,
                customFirstLatLng?.longitude!!))
        tempList.add(LatLng(customTwoLatLng?.latitude!!,
                customTwoLatLng?.longitude!!))
        tempList.add(LatLng(customThirdLatLng?.latitude!!,
                customThirdLatLng?.longitude!!))
        tempList.add(LatLng(customFourLatLng?.latitude!!,
                customFourLatLng?.longitude!!))
        val changeMapPointList = MapPolygonSortUtil.changeMapPoint(tempList)
        polygon = PolygonOptions()
                .fillColor(CommonUtils.getColor(mActivity, R.color.color80F18937))
                .strokeWidth(1.0f).addAll(changeMapPointList)
        mGoogleMap?.addPolygon(polygon)
    }

    private fun drawCustomThirdPoint(latLng: LatLng) {
        val bitmap = BitmapDescriptorFactory.fromResource(
                R.drawable.icon_custom_third_point)
        customThirdPoint = mGoogleMap?.addMarker(MarkerOptions()
                .draggable(false).icon(bitmap).position(
                        LatLng(latLng.latitude, latLng.longitude)))
        customThirdPoint?.tag = customThirdMartTag
        customThirdPoint?.isDraggable = false //设置不可移动
    }

    private fun drawCustomTwoPoint(latLng: LatLng) {
        val bitmap = BitmapDescriptorFactory.fromResource(
                R.drawable.icon_custom_two_point)
        customTwoPoint = mGoogleMap?.addMarker(MarkerOptions()
                .draggable(false).icon(bitmap).position(
                        LatLng(latLng.latitude, latLng.longitude)))
        customThirdPoint?.tag = customTwoMartTag
        customTwoPoint?.isDraggable = false //设置不可移动
    }

    private fun drawCustomFirstPoint(latLng: LatLng) {
        val bitmap = BitmapDescriptorFactory.fromResource(
                R.drawable.icon_custom_first_point)
        customFirstPoint = mGoogleMap?.addMarker(MarkerOptions()
                .draggable(false).icon(bitmap).position(
                        LatLng(latLng.latitude, latLng.longitude)))
        customFirstPoint?.tag = customFirstMartTag
        customFirstPoint?.isDraggable = false //设置不可移动
    }

    //绘制自定义的电子围栏点 结束


    /**
     * 位置权限
     */
    @SuppressLint("MissingPermission")
    private fun requestLocationPermission() {
        val perms = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION)
        PermissionUtils.requestPermissionFragment(this, perms,
                mActivity.resources.getString(R.string.app_location_permission),
                mActivity.resources.getString(R.string.app_main_map_location_permission_hint), {
            //初始化地图的定位服务
            updateLocationUI()
        }, {
            showToast(resources.getString(R.string.app_main_map_permission_error))
            mGoogleMap?.isMyLocationEnabled = false
            mGoogleMap?.uiSettings?.isMyLocationButtonEnabled = false
            mLastLocation = null
        })
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationUI() {
        mGoogleMap?.isMyLocationEnabled = true
        mGoogleMap?.uiSettings?.isMyLocationButtonEnabled = true
        startLocationUpdates()
    }

    /**
     * 位置监听
     */
    private val locationListener = LocationListener { location ->
        try {
            // 精确度越小越准，单位：米
            if (location.accuracy > 100) {
                return@LocationListener
            }
            mLastLocation = location
            if (isFirstLocation) {
                updateUI() //更新UI
                isFirstLocation = false
            }
        } catch (error: Error) {
        }
    }

    /**
     * 更新UI
     */
    @SuppressLint("SetTextI18n")
    private fun updateUI() {
        if (mLastLocation == null) return
        mLocationLat = mLastLocation!!.latitude
        mLocationLon = mLastLocation!!.longitude
        // 移动到定位点中心，并且缩放级别为16
        initCamera(LatLng(mLocationLat, mLocationLon))
        // 添加mark标记
        startDisplayPerth(LatLng(mLocationLat, mLocationLon))
    }

    //绘制定位轨迹的相关点
    private fun drawLocationPathPoints(index: Int) {
        val bitmap: BitmapDescriptor
        when (index) {
            0 -> {
                bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_mark_start_bg)
            }
            latLngList.size - 1 -> {
                bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_mark_end_bg)
            }
            else -> {
                val view = View.inflate(mActivity, R.layout.fragment_google_map_mark_path_point,
                        null)
                val pointIndexTv = view.findViewById<TextView>(R.id.pointIndexTv)
                pointIndexTv.text = (index + 1).toString()
                bitmap = BitmapDescriptorFactory.fromBitmap(convertViewToBitmap(view))
            }
        }
        setPathPointMarkCustomShow(bitmap, index)
    }

    //添加定位轨迹的点的mark标志
    private fun setPathPointMarkCustomShow(bitmap: BitmapDescriptor?, index: Int) {
        if (mGoogleMap == null) return
        if (bitmap == null) return
        val pointMark = mGoogleMap?.addMarker(MarkerOptions()
                .draggable(false).icon(bitmap).position(latLngList[index]))
        if (pathTimeWindow) {
            pointMark?.tag = "${pathPointMarkTag}${testTimeList[index]}"
            if (index == latLngList.size - 1)
                locationFunctionExtend.postDelayed({
                    pointMark?.showInfoWindow()
                }, 1000)
        }
        pointMark?.isDraggable = false //设置不可移动
    }

    //绘制定位轨迹的线
    private fun drawLocationPathLine() {
        line = null
        line = PolylineOptions()
                .color(CommonUtils.getColor(mActivity, R.color.colorF18937))
                .width(ScreenUtils.dip2px(mActivity, 4.0f).toFloat())
                .geodesic(true)
                .addAll(latLngList)
        mGoogleMap?.addPolyline(line)
    }

    /**
     * 设置定位点（开始点）的mark标志
     */
    private fun startDisplayPerth(latLng: LatLng, isTempLocation: Boolean = false) {
//        val transJC02LatLng =
//                if (BuildConfig.DEBUG && tabPosition == 0) {
//                    val gps84ToGcj02 =
//                            MapPositionUtil.gps84_To_Gcj02(latLng.latitude, latLng.longitude)
//                    LatLng(gps84ToGcj02.lat, gps84ToGcj02.lon)
//                } else latLng
        if (tabPosition == 3 || isTempLocation) {
            createCustomMarkIcon(true)
        } else {
            firstLocationLat = latLng.latitude
            firstLocationLng = latLng.longitude
            if (tabPosition == 0) {
                createCustomMarkIcon()
            }
        }
    }

    private fun setTempMarkCustomShow(bitmap: BitmapDescriptor?) {
        if (mGoogleMap == null) return
        if (tempLocationBean == null) return
        tempMark = mGoogleMap?.addMarker(MarkerOptions()
                .draggable(false).icon(bitmap).position(
                        LatLng(tempLocationBean!!.lat, tempLocationBean!!.lng)))
        tempMark?.tag = tempMarkTag
        locationFunctionExtend.postDelayed({
            tempMark?.showInfoWindow()
        }, 1000)
        tempMark?.isDraggable = false //设置不可移动
    }

    private fun setMarkCustomShow(bitmap: BitmapDescriptor?) {
        if (mGoogleMap == null) return
        starPerth = mGoogleMap?.addMarker(MarkerOptions()
                .draggable(false).icon(bitmap).position(
                        LatLng(firstLocationLat, firstLocationLng)))
        if (tabPosition == 0) {
            starPerth?.tag = martTag
            locationFunctionExtend.postDelayed({
                starPerth?.showInfoWindow()
            }, 1000)
        }
        starPerth?.isDraggable = false //设置不可移动
    }

    private fun createCustomMarkIcon(isTempLocation: Boolean = false) {
        var bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_map_mark_bg_with_head)
        if (StringUtil.isNotBlankAndEmpty(MMKVUtil.getString(BIND_DEVICE_ACCOUNT_HEADER))) {
            val view = View.inflate(mActivity, R.layout.fragment_google_map_mark_custom, null)
            val userHeaderIv = view.findViewById<CircleImageView>(R.id.userHeader)
            val picture = MMKVUtil.getString(BIND_DEVICE_ACCOUNT_HEADER)
            GlideApp.with(mActivity).asBitmap().load(picture)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap,
                                                     transition: Transition<in Bitmap>?) {
                            userHeaderIv.setImageBitmap(resource)
                            bitmap = BitmapDescriptorFactory.fromBitmap(convertViewToBitmap(view))
                            if (isTempLocation) {
                                setTempMarkCustomShow(bitmap)
                            } else setMarkCustomShow(bitmap)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {}

                    })

        } else {
            if (isTempLocation) {
                setTempMarkCustomShow(bitmap)
            } else setMarkCustomShow(bitmap)
        }
    }

    /**
     * 将地图视角切换到定位的位置
     */
    private fun initCamera(sydney: LatLng) {
        if (mGoogleMap != null) {
            val transJC02LatLng =
                    if (BuildConfig.DEBUG) {
                        val gps84ToGcj02 =
                                MapPositionUtil.gps84_To_Gcj02(sydney.latitude, sydney.longitude)
                        LatLng(gps84ToGcj02.lat, gps84ToGcj02.lon)
                    } else sydney
            mGoogleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(transJC02LatLng, 16.0f))
        }
    }


    override fun onConnected(bundle: Bundle?) {

    }

    /**
     * 定位我的位置
     */
    @SuppressLint("MissingPermission")
    private fun locationMyAddress() {
//        val perms = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
//                Manifest.permission.ACCESS_FINE_LOCATION)
//        val checkPermissions = PermissionUtils.checkPermissions(mActivity, perms)
//        if (!checkPermissions) return
//        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)
//        if (mLastLocation != null) {
//            isFirstLocation = false
//            initCamera(LatLng(mLastLocation!!.latitude, mLastLocation!!.longitude))
//            //            // 添加mark标记
//            startDisplayPerth(LatLng(mLastLocation!!.latitude, mLastLocation!!.longitude))
//            getAddress(mActivity, mLastLocation!!.latitude, mLastLocation!!.longitude)
//            //将地图视角切换到定位的位置
//            if (!Geocoder.isPresent()) {
//                // Toast.makeText(this, "No geocoder available", Toast.LENGTH_LONG).show();
//                return
//            }
//        } else {
//            // 启动位置更新
//            isFirstLocation = false
//            startLocationUpdates()
//        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val perms = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION)
        val checkPermissions = PermissionUtils.checkPermissions(mActivity, perms)
        if (!checkPermissions) return
        if (mGoogleApiClient!!.isConnected && mLocationRequest != null)
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, locationListener)
    }

    override fun onConnectionSuspended(p0: Int) {}

    /**
     * 逆地理编码 得到地址
     * @param context
     * @param latitude
     * @param longitude
     * @return
     */
    private fun getAddress(context: Context, latitude: Double, longitude: Double): String {
        val geoCoder = Geocoder(context, Locale.getDefault())
        try {
            val address = geoCoder.getFromLocation(latitude, longitude, 1)
            Log.i("位置", "得到位置当前" + address + "'\n"
                    + "经度：" + address[0].longitude + "\n"
                    + "纬度：" + address[0].latitude + "\n"
                    + "纬度：" + "国家：" + address[0].countryName + "\n"
                    + "城市：" + address[0].locality + "\n"
                    + "名称：" + address[0].getAddressLine(1) + "\n"
                    + "街道：" + address[0].getAddressLine(0)
            )
            return address[0].getAddressLine(0) + "  " +
                    address[0].locality + " " + address[0].countryName
        } catch (e: Exception) {
            e.printStackTrace()
            return "未知"
        }
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        if (marker?.tag == martTag || marker?.tag == tempMarkTag
                || (marker?.tag is String && (marker.tag as String).contains(pathPointMarkTag))) {
            marker.showInfoWindow()
        }
        return true
    }

    override fun onInfoWindowClick(marker: Marker?) {
        if (marker?.tag == martTag || marker?.tag == tempMarkTag) {
            marker.hideInfoWindow()
            var latLng: LatLng? = null
            if (marker.tag == martTag) {
                if (latestUserPositionListBean != null) {
                    latLng = LatLng(
                            latestUserPositionListBean!!.lat, latestUserPositionListBean!!.lng)
                }
            } else {
                if (tempLocationBean == null) return
                latLng = LatLng(tempLocationBean!!.lat, tempLocationBean!!.lng)
            }
            if (latLng == null) return
            MapUtils.startGuide(mActivity, latLng.latitude, latLng.longitude)
        } else if (marker?.tag is String && (marker.tag as String).contains(pathPointMarkTag)) {
            marker.hideInfoWindow()
        }
    }

    /**
     * 自定义的地图信息弹窗
     */
    @SuppressLint("InflateParams")
    class CustomInfoWindowAdapter(ref: WeakReference<LocationFragment>)
        : GoogleMap.InfoWindowAdapter {

        private var fragment: LocationFragment? = ref.get()

        private val guiderInfoWindow: View? = fragment?.mActivity?.layoutInflater?.inflate(
                R.layout.custom_map_mark_info, null)

        private val pathPointWindow: View? = fragment?.mActivity?.layoutInflater?.inflate(
                R.layout.path_point_map_mark_info, null)

        override fun getInfoWindow(marker: Marker): View? {
            if (fragment == null) return null
            return if (marker.tag == fragment?.martTag || marker.tag == fragment?.tempMarkTag) {
                render(marker, guiderInfoWindow)
                guiderInfoWindow
            } else if (marker.tag is String &&
                    (marker.tag as String).contains(fragment!!.pathPointMarkTag)) {
                render(marker, pathPointWindow)
                pathPointWindow
            } else {
                render(marker, guiderInfoWindow)
                guiderInfoWindow
            }
        }

        private fun render(marker: Marker, window: View?) {
            if (window == null) return
            if (window == guiderInfoWindow) {
                guiderInfoShowEvent(window, marker)
            } else if (window == pathPointWindow) {
                pathPointShowEvent(window, marker)
            }
        }

        private fun pathPointShowEvent(window: View, marker: Marker) {
            val pointTime = (marker.tag as String).replace(fragment!!.pathPointMarkTag, "")
            val pointTimeTv = window.findViewById<TextView>(R.id.pointTimeTv)
            pointTimeTv.text = pointTime
        }

        private fun guiderInfoShowEvent(window: View, marker: Marker) {
            val locationMethodTv = window.findViewById<TextView>(R.id.locationMethodTv)
            val timeTv = window.findViewById<TextView>(R.id.timeTv)
            val martLocationTv = window.findViewById<TextView>(R.id.martLocationTv)
            if (marker.tag == fragment?.martTag) {
                if (fragment?.latestUserPositionListBean == null) return
                if (StringUtil.isNotBlankAndEmpty(fragment?.latestUserPositionListBean!!.testTime)) {
                    val localTime = DateUtilKotlin.uTCToLocal(
                            fragment?.latestUserPositionListBean!!.testTime,
                            TIME_FORMAT_PATTERN1)
                    timeTv.text = localTime
                }

                if (StringUtil.isNotBlankAndEmpty(fragment?.latestUserPositionListBean!!.addr)) {
                    martLocationTv.visibility = View.VISIBLE
                    martLocationTv.text = fragment?.latestUserPositionListBean!!.addr
                } else {
                    martLocationTv.visibility = View.GONE
                }
                if (StringUtil.isNotBlankAndEmpty(fragment?.latestUserPositionListBean?.signalType)) {
                    locationMethodTv.text = String.format(
                            fragment!!.mActivity.resources.getString(
                                    R.string.app_map_location_method_content),
                            fragment?.latestUserPositionListBean?.signalType
                    )
                } else locationMethodTv.text = String.format(
                        fragment!!.mActivity.resources.getString(
                                R.string.app_map_location_method_content), "WIFI"
                )
            } else if (marker.tag == fragment?.tempMarkTag) {
                val localTime = DateUtilKotlin.uTCToLocal(fragment?.tempLocationBean?.createTime,
                        TIME_FORMAT_PATTERN1)
                timeTv.text = localTime
                val address = fragment?.tempLocationBean?.addr
                if (StringUtil.isNotBlankAndEmpty(address)) {
                    martLocationTv.visibility = View.VISIBLE
                    martLocationTv.text = address
                } else {
                    martLocationTv.visibility = View.GONE
                }
                val method = fragment?.tempLocationBean?.signalType
                if (StringUtil.isNotBlankAndEmpty(method)) {
                    locationMethodTv.text = String.format(
                            fragment!!.mActivity.resources.getString(
                                    R.string.app_map_location_method_content), method
                    )
                } else locationMethodTv.text = String.format(
                        fragment!!.mActivity.resources.getString(
                                R.string.app_map_location_method_content), "WIFI"
                )
            }
        }

        override fun getInfoContents(p0: Marker?): View? {
            return null
        }

    }

    override fun onConnectionFailed(failResult: ConnectionResult) {
        showToast("地图连接失败" +
                "${
                    if (StringUtil.isNotBlankAndEmpty(failResult.errorMessage))
                        failResult.errorMessage else ""
                }")
        eventDealLayout.setChildClickable(false)
    }

    override fun onResume() {
        super.onResume()
        if (mGoogleApiClient != null) mGoogleApiClient?.connect()
        //处理系统消息中需要跳转到地图指定页面的逻辑因为存在可能页面未加载出来的情况，
        // 所以使用的MMKV和EventBus结合的方式来判断
        if (MMKVUtil.containKey(EventBusAction.ENTRY_LOCATION_WITH_TYPE)) {
            Log.i(TAG, "系统消息进来需要跳转的页面为${
                MMKVUtil.getString(
                        EventBusAction.ENTRY_LOCATION_WITH_TYPE)
            }")
            EventBusUtils.sendStickyEvent(EventBusEvent(
                    EventBusAction.ENTRY_LOCATION_WITH_TYPE, MMKVUtil.getString(
                    EventBusAction.ENTRY_LOCATION_WITH_TYPE)))
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun entryDifferentLocationPageWithType(event: EventBusEvent<String>) {
        if (event.code == EventBusAction.ENTRY_LOCATION_WITH_TYPE) {
            when (event.data) {
                "0" -> {
                    if (locationTabLayout.getTabAt(0) != null) {
                        locationTabLayout.selectTab(locationTabLayout.getTabAt(0))
                        MMKVUtil.clearByKey(EventBusAction.ENTRY_LOCATION_WITH_TYPE)
                    }
                }
                "1" -> {
                    if (locationTabLayout.getTabAt(1) != null) {
                        locationTabLayout.selectTab(locationTabLayout.getTabAt(1))
                        MMKVUtil.clearByKey(EventBusAction.ENTRY_LOCATION_WITH_TYPE)
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (mGoogleApiClient != null) mGoogleApiClient?.disconnect()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        proactivelyAddressingNum = 0
        closeShowDialog()
        if (loadView != null) {
            loadView?.dismiss()
        }
        isStart = false
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient!!.isConnected) {
                LocationServices.FusedLocationApi.removeLocationUpdates(
                        mGoogleApiClient, locationListener)
            }
        }
        if (mDialog1 != null) {
            mDialog1?.hideDialog()
            mDialog1 = null
        }
        if (mDialog2 != null) {
            mDialog2?.hideDialog()
            mDialog2 = null
        }
    }

    private fun closeShowDialog() {
        if (selectDateDialog != null) {
            selectDateDialog?.closeDialog()
            selectDateDialog = null
        }
        if (pathSetDialog != null) {
            pathSetDialog?.closeDialog()
            pathSetDialog = null
        }
        if (electronicSaveDialog != null) {
            electronicSaveDialog?.closeDialog()
            electronicSaveDialog = null
        }
        if (personModeConfirmDialog != null) {
            personModeConfirmDialog?.closeDialog()
            personModeConfirmDialog = null
        }
    }

}