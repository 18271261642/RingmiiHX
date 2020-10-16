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
import com.guider.baselib.widget.CircleImageView
import com.guider.baselib.widget.LoadingView
import com.guider.baselib.widget.calendarList.CalendarList
import com.guider.baselib.widget.dialog.DialogHolder
import com.guider.baselib.widget.dialog.DialogProgress
import com.guider.feifeia3.utils.ToastUtil
import com.guider.gps.BuildConfig
import com.guider.gps.R
import com.guider.gps.adapter.LocationTrackEventTimeAdapter
import com.guider.gps.bean.WithSelectBaseBean
import com.guider.gps.view.activity.HistoryRecordActivity
import com.guider.gps.view.activity.LocationFrequencySetNewActivity
import com.guider.health.apilib.GuiderApiUtil
import com.guider.health.apilib.bean.ElectronicFenceBean
import com.guider.health.apilib.bean.UserPositionListBean
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_location.*
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


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

    private var trackEventTimePosition = 0

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

    private var martTag = "locationInfo"

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
    private var subscribe: Disposable? = null
    private var proactivelyAddressingNum = 0

    override fun initView(rootView: View) {
    }

    override fun initLogic() {
        tabTitleList = arrayListOf(
                resources.getString(R.string.app_main_map_location_information),
                resources.getString(R.string.app_main_map_track_events),
                resources.getString(R.string.app_main_map_electronic_fence),
                resources.getString(R.string.app_main_map_proactively_addressing))
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
        locationInfoLayout.visibility = View.VISIBLE
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
        initTrackEventTimeAdapter()
        trackEventsDateCalSelectIv.setOnClickListener(this)
        trackEventCalIv.setOnClickListener(this)
        startTimeTv.setOnClickListener(this)
        endTimeTv.setOnClickListener(this)
        mapLocationIv.setOnClickListener(this)
        searchLayout.setOnClickListener(this)
        trackEventsDateLeft.setOnClickListener(this)
        trackEventsDateRight.setOnClickListener(this)
    }

    /**
     * 处理tab的切换事件
     */
    private fun dealTabSelectEvent(tab: TabLayout.Tab) {
        resetFunctionLayout()
        when (tab.text.toString()) {
            tabTitleList[0] -> {
                mGoogleMap?.setInfoWindowAdapter(CustomInfoWindowAdapter())
                if (selectDateDialog != null) {
                    selectDateDialog?.closeDialog()
                }
                if (loadView != null) {
                    loadView?.dismiss()
                }
                initElectronicPoint()
                tabPosition = 0
                locationInfoLayout.visibility = View.VISIBLE
                locationNumberSet.text =
                        resources.getString(
                                R.string.app_main_map_positioning_frequency_setting)
                locationNumberSet.setTextColor(
                        CommonUtils.getColor(mActivity, R.color.color333333))
                historyRecord.text =
                        resources.getString(R.string.app_main_map_history_record)
                mGoogleMap?.clear()
                if (MMKVUtil.containKey(LAST_LOCATION_POINT_LAT)) {
                    val latLng = LatLng(
                            MMKVUtil.getDouble(LAST_LOCATION_POINT_LAT, 0.0),
                            MMKVUtil.getDouble(LAST_LOCATION_POINT_LNG, 0.0))
                    mGoogleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f))
                    startDisplayPerth(latLng)
                } else {
                    getUserLocationPointData()
                }
            }
            tabTitleList[1] -> {
                mGoogleMap?.setInfoWindowAdapter(null)
                if (selectDateDialog != null) {
                    selectDateDialog?.closeDialog()
                }
                initElectronicPoint()
                if (loadView != null) {
                    loadView?.dismiss()
                }
                tabPosition = 1
                trackEventsLayout.visibility = View.VISIBLE
                searchFrontLayout.visibility = View.VISIBLE
                selectTimeBackLayout.visibility = View.GONE
                dateSelectTag = false
                mGoogleMap?.clear()
                //获取年月日格式的当前日期
                trackEventsDateValueTv.text = CommonUtils.getCurrentDate(TIME_FORMAT_PATTERN6)
                startTimeTv.text = CommonUtils.calTimeFrontDay(
                        CommonUtils.getCurrentDate(), 7)
                endTimeTv.text = CommonUtils.getCurrentDate()
                val currentDateString = CommonUtils.getCurrentDate(DEFAULT_TIME_FORMAT_PATTERN)
                startTimeValue = CommonUtils.calTimeFrontHour(currentDateString, 24)
                endTimeValue = CommonUtils.getCurrentDate(DEFAULT_TIME_FORMAT_PATTERN)
                getUserPointLineData(startTimeValue, endTimeValue)
            }
            tabTitleList[2] -> {
                mGoogleMap?.setInfoWindowAdapter(null)
                mGoogleMap?.setOnMapClickListener {
                    //手动去编辑电子围栏
                    mapClickEvent(it)
                }
                initElectronicPoint()
                if (selectDateDialog != null) {
                    selectDateDialog?.closeDialog()
                }
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
            tabTitleList[3] -> {
                mGoogleMap!!.setInfoWindowAdapter(null)
                if (selectDateDialog != null) {
                    selectDateDialog?.closeDialog()
                }
                initElectronicPoint()
                if (loadView != null) {
                    loadView?.dismiss()
                }
                tabPosition = 3
                locationInfoLayout.visibility = View.VISIBLE
                locationNumberSet.text =
                        resources.getString(R.string.app_main_map_send_instructions)
                locationNumberSet.setTextColor(
                        CommonUtils.getColor(mActivity, R.color.colorF18937))
                historyRecord.text =
                        resources.getString(R.string.app_main_map_address_history)
                mGoogleMap?.clear()
            }
        }
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
        electronicSetIv.isSelected = false
        electronicSetTv.isSelected = false
        electronicAddLayout.isSelected = false
        electronicSetHint.visibility = View.GONE
    }


    override fun openEventBus(): Boolean {
        return true
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshHealthData(event: EventBusEvent<String>) {
        if (event.code == EventBusAction.REFRESH_HEALTH_DATA) {
            mGoogleMap?.clear()
            when (tabPosition) {
                0 -> {
                    mGoogleMap?.setInfoWindowAdapter(CustomInfoWindowAdapter())
                    getUserLocationPointData()
                }
                1 -> {
                    mGoogleMap?.setInfoWindowAdapter(null)
                    val currentDateString = CommonUtils.getCurrentDate(DEFAULT_TIME_FORMAT_PATTERN)
                    startTimeValue = CommonUtils.calTimeFrontHour(currentDateString, 24)
                    endTimeValue = CommonUtils.getCurrentDate(DEFAULT_TIME_FORMAT_PATTERN)
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

    /**
     * 得到用户最近的定位点
     */
    private fun getUserLocationPointData() {
        mDialog1 = DialogProgress(mActivity, null)
        mDialog1?.showDialog()
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
            try {
                val resultBean = GuiderApiUtil.getApiService()
                        .userPosition(accountId, 1, 1, "", "")
                mDialog1?.hideDialog()
                if (!resultBean.isNullOrEmpty() && resultBean.size == 1) {
                    val firstPosition = resultBean[0]
                    mGoogleMap?.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                    LatLng(firstPosition.lat, firstPosition.lng), 16.0f))
                    startDisplayPerth(LatLng(firstPosition.lat, firstPosition.lng))
                    if (StringUtil.isNotBlankAndEmpty(firstPosition.addr))
                        MMKVUtil.saveString(LAST_LOCATION_POINT_ADDRESS, firstPosition.addr)
                    if (StringUtil.isNotBlankAndEmpty(firstPosition.testTime))
                        MMKVUtil.saveString(LAST_LOCATION_POINT_TIME, firstPosition.testTime)
                    if (StringUtil.isNotBlankAndEmpty(firstPosition.signalType))
                        MMKVUtil.saveString(LAST_LOCATION_POINT_METHOD, firstPosition.signalType)
                    //判断是否是相同的定位点
                    if (!MMKVUtil.containKey(LAST_LOCATION_POINT_LAT) ||
                            (!(MMKVUtil.getDouble(LAST_LOCATION_POINT_LAT, 0.0)
                                    == firstPosition.lat &&
                                    MMKVUtil.getDouble(
                                            LAST_LOCATION_POINT_LNG, 0.0)
                                    == firstPosition.lng))) {
                        MMKVUtil.saveDouble(LAST_LOCATION_POINT_LAT, firstPosition.lat)
                        MMKVUtil.saveDouble(LAST_LOCATION_POINT_LNG, firstPosition.lng)
                    }
                } else {
                    MMKVUtil.clearByKey(LAST_LOCATION_POINT_LAT)
                    MMKVUtil.clearByKey(LAST_LOCATION_POINT_LNG)
                    MMKVUtil.clearByKey(LAST_LOCATION_POINT_ADDRESS)
                    MMKVUtil.clearByKey(LAST_LOCATION_POINT_TIME)
                    MMKVUtil.clearByKey(LAST_LOCATION_POINT_METHOD)
                    //如果没有位置的话就先跳转到台北坐标
                    mGoogleMap?.clear()
                    val latLng = LatLng(25.0422452, 121.5802603)
                    mGoogleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 9.5f))
                }
            } catch (e: Exception) {
                mDialog1?.hideDialog()
                showToast(e.message!!)
            }
        }
    }

    /**
     * 得到用户行动轨迹
     */
    private fun getUserPointLineData(startTimeValue: String, endTimeValue: String) {
        mDialog2 = DialogProgress(mActivity, null)
        mDialog2?.showDialog()
        val accountId = MMKVUtil.getInt(BIND_DEVICE_ACCOUNT_ID)
        if (accountId == 0) {
            mDialog2?.hideDialog()
            return
        }
        Log.i("getUserPointDataTime", "start$startTimeValue-----end$endTimeValue")
        lifecycleScope.launch {
            try {
                val resultBean = GuiderApiUtil.getApiService()
                        .userPosition(accountId, 1, 1, "", "")
                mDialog2?.hideDialog()
                if (!resultBean.isNullOrEmpty() && resultBean.size > 1) {
                    val firstPosition = resultBean[0]
                    val endPosition = resultBean[resultBean.size - 1]
                    startDisplayPerth(LatLng(firstPosition.lat, firstPosition.lng))
                    endDisplayPerth(LatLng(endPosition.lat, endPosition.lng))
                    val latLngList = arrayListOf<LatLng>()
                    resultBean.forEach {
                        latLngList.add(LatLng(it.lat, it.lng))
                    }
                    line = PolylineOptions()
                            .color(CommonUtils.getColor(mActivity, R.color.colorF18937))
                            .width(4f)
                            .geodesic(true)
                            .addAll(latLngList)
                    mGoogleMap?.addPolyline(line)
                    mGoogleMap?.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(latLngList[0], 16.0f))
                }
            } catch (e: Exception) {
                mDialog2?.hideDialog()
                showToast(e.message!!)
            }
        }
    }

    /**
     * 获取设备的电子围栏信息
     */
    private fun getElectronicFenceData() {
        val deviceCode = MMKVUtil.getString(BIND_DEVICE_CODE)
        val accountId = MMKVUtil.getInt(BIND_DEVICE_ACCOUNT_ID)
        if (StringUtil.isEmpty(deviceCode)) {
            return
        }
        mActivity.showDialog()
        lifecycleScope.launch {
            try {
                val resultBean = GuiderApiUtil.getApiService()
                        .getElectronicFence(deviceCode, accountId)
                mActivity.dismissDialog()
                if (resultBean is String && resultBean == "null") {
                    electronicAddLayout.post {
                        showGuideView()
                    }
                } else {
                    val asList = ParseJsonData.parseJsonDataList<ElectronicFenceBean>(
                            resultBean, ElectronicFenceBean::class.java)
                    if (asList.size < 4) {
                        electronicAddLayout.post {
                            showGuideView()
                        }
                    } else {
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
                        electronicCommitTv.isSelected = true
                        electronicDeleteIv.isSelected = true
                        electronicDeleteTv.isSelected = true
                        electronicDeleteLayout.isSelected = true
                        customElectronicFencePointNum = 4
                        mGoogleMap?.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(customFirstLatLng, 16.0f))
                    }
                }
            } catch (e: Exception) {
                mActivity.dismissDialog()
                showToast(e.message!!)
            }
        }
    }

    /**
     * 显示电子围栏高亮提示
     */
    private fun showGuideView() {
        electronicAddLayout.isSelected = true
        electronicSetIv.isSelected = true
        electronicSetTv.isSelected = true
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
    private fun setElectronicFenceData() {
        val deviceCode = MMKVUtil.getString(BIND_DEVICE_CODE)
        val accountId = MMKVUtil.getInt(BIND_DEVICE_ACCOUNT_ID)
        if (StringUtil.isEmpty(deviceCode)) return
        mActivity.showDialog()
        val data = hashMapOf<String, Any>()
        data["deviceCode"] = deviceCode
        data["accountId"] = accountId
        data["points"] = arrayListOf(
                ElectronicFenceBean(customFirstLatLng!!.latitude, customFirstLatLng!!.longitude),
                ElectronicFenceBean(customTwoLatLng!!.latitude, customTwoLatLng!!.longitude),
                ElectronicFenceBean(customThirdLatLng!!.latitude, customThirdLatLng!!.longitude),
                ElectronicFenceBean(customFourLatLng!!.latitude, customFourLatLng!!.longitude))
        lifecycleScope.launch {
            try {
                val resultBean = GuiderApiUtil.getApiService().setElectronicFence(data)
                mActivity.dismissDialog()
                if (resultBean == "true") {
                    showToast(mActivity.resources.getString(R.string.app_set_success))
                }
            } catch (e: Exception) {
                mActivity.dismissDialog()
                showToast(e.message!!)
            }
        }
    }

    /**
     * 确认电子围栏的显示或者取消已设置的信息
     */
    private fun confirmElectronicShow() {
        electronicAddLayout.isSelected = false
        electronicSetIv.isSelected = false
        electronicSetTv.isSelected = false
        customFirstPoint?.remove()
        customTwoPoint?.remove()
        customThirdPoint?.remove()
        customFourPoint?.remove()
        electronicDeleteLayout.isSelected = false
        electronicSetHint.visibility = View.GONE
        electronicDeleteIv.isSelected = false
        electronicDeleteTv.isSelected = false
        electronicCommitLayout.isSelected = false
        electronicCommitIv.isSelected = false
        electronicCommitTv.isSelected = true
        customElectronicFencePointNum = 0
        mGoogleMap?.setOnMapClickListener {}
    }

    /**
     * 初始化活动轨迹的时间列表的adapter
     */
    private fun initTrackEventTimeAdapter() {
        trackEventsTimeRv.layoutManager = LinearLayoutManager(
                mActivity, LinearLayoutManager.HORIZONTAL, false)
        arrayListOf("1", "2", "6", "12", "24").forEach {
            trackEventTimeList.add(WithSelectBaseBean(it, false))
        }
        trackEventTimeList[0].isSelect = true
        trackEventAdapter = LocationTrackEventTimeAdapter(mActivity, trackEventTimeList)
        trackEventAdapter.setListener(object : AdapterOnItemClickListener {
            override fun onClickItem(position: Int) {
                if (trackEventTimePosition != position && !dateSelectTag) {
                    trackEventTimeList[trackEventTimePosition].isSelect = false
                    trackEventTimeList[position].isSelect = true
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
                if (tabPosition == 3) {
                    val accountId = MMKVUtil.getInt(BIND_DEVICE_ACCOUNT_ID)
                    if (accountId == 0) {
                        return
                    }
                    //发起主动寻址
                    initiateActiveAddressing(accountId)
                } else {
                    val intent = Intent(mActivity, LocationFrequencySetNewActivity::class.java)
                    startActivityForResult(intent, LOCATION_FREQUENCY_SET)
                }
            }
            historyRecord -> {
                val intent = Intent(mActivity, HistoryRecordActivity::class.java)
                if (tabPosition == 3) {
                    intent.putExtra("entryType", "proactivelyAddressHistory")
                } else {
                    intent.putExtra("entryType", "locationHistory")
                }
                startActivity(intent)
            }
            trackEventsDateCalSelectIv -> {
                searchFrontLayout.visibility = View.GONE
                selectTimeBackLayout.visibility = View.VISIBLE
                dateSelectTag = trackEventsDateValueTv.text.toString() !=
                        CommonUtils.getCurrentDate(TIME_FORMAT_PATTERN6)
            }
            startTimeTv, endTimeTv -> {
                initSelectDateDialogShow()
            }
            searchLayout -> {
                startTimeValue = "${startTimeTv.text} 00:00:00"
                endTimeValue = "${endTimeTv.text} 00:00:00"
                getUserPointLineData(startTimeValue, endTimeValue)
            }
            trackEventsDateLeft -> {
                dateSelectTag = true
                val currentDate = trackEventsDateValueTv.text.toString()
                val calTimeFrontDate = CommonUtils.calTimeFrontDay(
                        currentDate, 1, TIME_FORMAT_PATTERN6)
                //切到了当前日期
                if (calTimeFrontDate == CommonUtils.getCurrentDate(TIME_FORMAT_PATTERN6)) {
                    dateSelectTag = false
                }
                trackEventsDateValueTv.text = calTimeFrontDate
                val dateString = calTimeFrontDate
                        .replace("年", "-")
                        .replace("月", "-")
                        .replace("日", "")
                startTimeValue = "$dateString 00:00:00"
                endTimeValue = "$dateString 24:00:00"
                getUserPointLineData(startTimeValue, endTimeValue)
            }
            trackEventsDateRight -> {
                dateSelectTag = true
                val currentDate = trackEventsDateValueTv.text.toString()
                val calTimeFrontDate = CommonUtils.calTimeFrontDay(
                        currentDate, -1, TIME_FORMAT_PATTERN6)
                trackEventsDateValueTv.text = calTimeFrontDate
                //切到了当前日期
                if (calTimeFrontDate == CommonUtils.getCurrentDate(TIME_FORMAT_PATTERN6)) {
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
                trackEventsDateValueTv.text = CommonUtils.getCurrentDate(TIME_FORMAT_PATTERN6)
                selectTimeBackLayout.visibility = View.GONE
                searchFrontLayout.visibility = View.VISIBLE
                val currentDateString = CommonUtils.getCurrentDate(DEFAULT_TIME_FORMAT_PATTERN)
                startTimeValue = CommonUtils.calTimeFrontHour(currentDateString, 24)
                endTimeValue = CommonUtils.getCurrentDate(DEFAULT_TIME_FORMAT_PATTERN)
                dateSelectTag = false
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
                            electronicSetIv.isSelected = true
                            electronicSetTv.isSelected = true
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
                if (customElectronicFencePointNum == 4) {
                    setElectronicFenceData()
                } else {
                    showToast(mActivity.resources.getString(R.string.app_electronic_hint))
                }
            }
        }
    }

    /**
     * 发起主动寻址
     */
    private fun initiateActiveAddressing(accountId: Int) {
        mActivity.showDialog()
        lifecycleScope.launch {
            try {
                val resultBean = GuiderApiUtil.getApiService().initiateActiveAddressing(accountId)
                mActivity.dismissDialog()
                if (resultBean == "true") {
                    proactivelyAddressingLoad(accountId)
                }
                ToastUtil.showCustomToast(null, mActivity,
                        true, if (resultBean == "true")
                    mActivity.resources.getString(R.string.app_main_send_success)
                else mActivity.resources.getString(R.string.app_main_send_fail))
            } catch (e: Exception) {
                mActivity.dismissDialog()
                Log.e(TAG, if (StringUtil.isNotBlankAndEmpty(e.message)) e.message!! else "fail")
                ToastUtil.showCustomToast(null, mActivity,
                        true,
                        mActivity.resources.getString(R.string.app_main_send_fail))
            }
        }
    }

    /**
     * 轮询主动寻址
     */
    private fun proactivelyAddressingLoad(accountId: Int) {
        if (subscribe != null && proactivelyAddressingNum < 24) {
            return
        }
        loadView = LoadingView(mActivity, false,
                mActivity.resources.getString(R.string.app_addressing_in_progress),
                false)
        loadView?.show()
        val time = DateUtilKotlin.localToUTC(
                DateUtil.localNowStringByPattern(DEFAULT_TIME_FORMAT_PATTERN))!!
        subscribe = Flowable.interval(0, 5, TimeUnit.SECONDS)
                .onBackpressureDrop()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    proactivelyAddressing(accountId, time)
                }
    }

    /**
     * 主动寻址
     */
    private fun proactivelyAddressing(accountId: Int, time: String) {
        ++proactivelyAddressingNum
        //如果2分钟还没有寻址成功，则结束寻址
        if (proactivelyAddressingNum == 24) {
            proactivelyAddressingFinish("fail")
            return
        }
        lifecycleScope.launch {
            try {
                val resultBean = GuiderApiUtil.getApiService().proactivelyAddressingData(
                        accountId, time)
                if (!(resultBean is String && resultBean == "null") && resultBean != null) {
                    val bean = ParseJsonData.parseJsonAny<UserPositionListBean>(resultBean)
                    startDisplayPerth(LatLng(bean.lat, bean.lng))
                    mGoogleMap?.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(LatLng(bean.lat, bean.lng), 16.0f))
                    proactivelyAddressingFinish("success")
                }
            } catch (e: Exception) {
                showToast(e.message!!)
            }
        }
    }

    private fun proactivelyAddressingFinish(result: String) {
        subscribe?.dispose()
        loadView?.dismiss()
        ToastUtil.showCustomToast(null, mActivity,
                true, if (result == "success")
            mActivity.resources.getString(R.string.app_main_map_addressing_success)
        else mActivity.resources.getString(R.string.app_main_map_addressing_fail))
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
                calendarListView.setOnDateSelected { startDate, endDate ->
                    calendarListView.postDelayed({
                        dialog?.dismiss()
                        startTimeTv.text = startDate
                        endTimeTv.text = endDate
                    }, 500)
                }
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
        if (MMKVUtil.containKey(LAST_LOCATION_POINT_LAT)) {
            val latLng = LatLng(
                    MMKVUtil.getDouble(LAST_LOCATION_POINT_LAT, 0.0),
                    MMKVUtil.getDouble(LAST_LOCATION_POINT_LNG, 0.0))
            startDisplayPerth(latLng)
            mGoogleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f))
        } else {
            getUserLocationPointData()
        }
        val uiSettings = mGoogleMap!!.uiSettings
        uiSettings.isMyLocationButtonEnabled = false //显示定位按钮
        uiSettings.isCompassEnabled = false //设置是否显示指南针
        uiSettings.isZoomControlsEnabled = true//缩放控件
        mGoogleMap?.isTrafficEnabled = true
        mGoogleMap?.setInfoWindowAdapter(CustomInfoWindowAdapter())
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
                    electronicSetIv.isSelected = false
                    electronicSetTv.isSelected = false
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
        polygon = PolygonOptions()
                .fillColor(CommonUtils.getColor(mActivity, R.color.color80F18937))
                .strokeWidth(1.0f).add(
                        LatLng(customFirstLatLng?.latitude!!,
                                customFirstLatLng?.longitude!!),
                        LatLng(customTwoLatLng?.latitude!!,
                                customTwoLatLng?.longitude!!),
                        LatLng(customThirdLatLng?.latitude!!,
                                customThirdLatLng?.longitude!!),
                        LatLng(customFourLatLng?.latitude!!,
                                customFourLatLng?.longitude!!))
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
                resources.getString(R.string.app_main_map_location_permission_hint), {
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

    /**
     * 设置定位点（开始点）的mark标志
     */
    private fun startDisplayPerth(latLng: LatLng) {
//        val transJC02LatLng =
//                if (BuildConfig.DEBUG && tabPosition == 0) {
//                    val gps84ToGcj02 =
//                            MapPositionUtil.gps84_To_Gcj02(latLng.latitude, latLng.longitude)
//                    LatLng(gps84ToGcj02.lat, gps84ToGcj02.lon)
//                } else latLng
        firstLocationLat = latLng.latitude
        firstLocationLng = latLng.longitude
        if (tabPosition == 0 || tabPosition == 3) {
            createCustomMarkIcon()
        }
        if (tabPosition == 1) {
            val bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_mark_start_bg)
            setMarkCustomShow(bitmap)
        }
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

    private fun createCustomMarkIcon() {
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
                            setMarkCustomShow(bitmap)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {}

                    })

        } else {
            setMarkCustomShow(bitmap)
        }
    }

    /**
     * 设置结束点的mark标志
     */
    private fun endDisplayPerth(latLng: LatLng) {
        // 每一次打点第一个的时候就是定位开始的时候
        val bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_mark_end_bg)
        endPerth = mGoogleMap?.addMarker(MarkerOptions()
                .draggable(false).icon(bitmap).position(latLng))
        endPerth?.isDraggable = false //设置不可移动
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
     * 地图两点之间的距离的算法
     */
    private fun calculateLineDistance(var0: LatLng?, var1: LatLng?): Float {
        return if (var0 != null && var1 != null) {
            try {
                val var2 = 0.01745329251994329
                var var4 = var0.longitude
                var var6 = var0.latitude
                var var8 = var1.longitude
                var var10 = var1.latitude
                var4 *= 0.01745329251994329
                var6 *= 0.01745329251994329
                var8 *= 0.01745329251994329
                var10 *= 0.01745329251994329
                val var12 = sin(var4)
                val var14 = sin(var6)
                val var16 = cos(var4)
                val var18 = cos(var6)
                val var20 = sin(var8)
                val var22 = sin(var10)
                val var24 = cos(var8)
                val var26 = cos(var10)
                val var28 = DoubleArray(3)
                val var29 = DoubleArray(3)
                var28[0] = var18 * var16
                var28[1] = var18 * var12
                var28[2] = var14
                var29[0] = var26 * var24
                var29[1] = var26 * var20
                var29[2] = var22
                val var30 = sqrt((var28[0] - var29[0]) * (var28[0] - var29[0]) +
                        (var28[1] - var29[1]) * (var28[1] - var29[1]) +
                        (var28[2] - var29[2]) * (var28[2] - var29[2]))
                (asin(var30 / 2.0) * 1.27420015798544E7).toFloat()
            } catch (var32: Throwable) {
                var32.printStackTrace()
                0.0f
            }
        } else {
            try {
                throw Exception(mActivity.resources.getString(R.string.illegal_coordinate_value))
            } catch (var33: java.lang.Exception) {
                var33.printStackTrace()
                0.0f
            }
        }
    }

    /**
     * 逆地理编码 得到地址
     * @param context
     * @param latitude
     * @param longitude
     * @return
     */
    private fun getAddress(context: Context, latitude: Double, longitude: Double): String {
        val geoCoder = Geocoder(context, Locale.getDefault());
        try {
            val address = geoCoder.getFromLocation(latitude, longitude, 1);
            Log.i("位置", "得到位置当前" + address + "'\n"
                    + "经度：" + address[0].longitude + "\n"
                    + "纬度：" + address[0].latitude + "\n"
                    + "纬度：" + "国家：" + address[0].countryName + "\n"
                    + "城市：" + address[0].locality + "\n"
                    + "名称：" + address[0].getAddressLine(1) + "\n"
                    + "街道：" + address[0].getAddressLine(0)
            );
            return address[0].getAddressLine(0) + "  " +
                    address[0].locality + " " + address[0].countryName;
        } catch (e: Exception) {
            e.printStackTrace();
            return "未知";
        }
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        if (marker?.tag == martTag) {
            marker.showInfoWindow()
        }
        return true
    }

    override fun onInfoWindowClick(marker: Marker?) {
        if (marker?.tag == martTag) {
            showToast("开始导航")
            marker.hideInfoWindow()
        }
    }

    /**
     * 自定义的地图信息弹窗
     */
    internal inner class CustomInfoWindowAdapter : GoogleMap.InfoWindowAdapter {

        private val window: View = mActivity.layoutInflater.inflate(
                R.layout.custom_map_mark_info, null)

        override fun getInfoWindow(marker: Marker): View {
            render(marker, window)
            return window
        }

        private fun render(marker: Any, window: View) {
            val locationMethodTv = window.findViewById<TextView>(R.id.locationMethodTv)
            val timeTv = window.findViewById<TextView>(R.id.timeTv)
            if (StringUtil.isNotBlankAndEmpty(MMKVUtil.getString(LAST_LOCATION_POINT_TIME))) {
                val localTime = DateUtilKotlin.uTCToLocal(
                        MMKVUtil.getString(LAST_LOCATION_POINT_TIME),
                        TIME_FORMAT_PATTERN1)
                timeTv.text = localTime
            }
            val martLocationTv = window.findViewById<TextView>(R.id.martLocationTv)
            if (StringUtil.isNotBlankAndEmpty(MMKVUtil.getString(LAST_LOCATION_POINT_ADDRESS))) {
                martLocationTv.visibility = View.VISIBLE
                val address = MMKVUtil.getString(LAST_LOCATION_POINT_ADDRESS)
                martLocationTv.text = address
            } else {
                martLocationTv.visibility = View.GONE
            }
            if (StringUtil.isNotBlankAndEmpty(MMKVUtil.getString(LAST_LOCATION_POINT_METHOD))) {
                val method = MMKVUtil.getString(LAST_LOCATION_POINT_METHOD)
                locationMethodTv.text = String.format(
                        this@LocationFragment.mActivity.resources.getString(
                                R.string.app_map_location_method_content), method
                )
            } else locationMethodTv.text = String.format(
                    this@LocationFragment.mActivity.resources.getString(
                            R.string.app_map_location_method_content), "WIFI"
            )
            val locationLayout = window.findViewById<ConstraintLayout>(R.id.locationLayout)
            if (MMKVUtil.containKey(LAST_LOCATION_POINT_LAT)) {
                val latLng = LatLng(
                        MMKVUtil.getDouble(LAST_LOCATION_POINT_LAT, 0.0),
                        MMKVUtil.getDouble(LAST_LOCATION_POINT_LNG, 0.0))
                locationLayout.setOnClickListener(object : OnNoDoubleClickListener {
                    override fun onNoDoubleClick(v: View) {
                        MapUtils.startGuide(mActivity, latLng.latitude, latLng.longitude)
                    }

                })
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

    override fun onDestroy() {
        super.onDestroy()
        if (selectDateDialog != null) {
            selectDateDialog?.closeDialog()
        }
        if (loadView != null) {
            loadView?.dismiss()
        }
        if (subscribe != null) {
            subscribe?.dispose()
            subscribe = null
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

}