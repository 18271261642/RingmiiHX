package com.guider.gps.view.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import cn.addapp.pickers.picker.DatePicker
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
import com.guider.baselib.widget.LoadingView
import com.guider.baselib.widget.calendarList.CalendarList
import com.guider.baselib.widget.dialog.DialogHolder
import com.guider.baselib.widget.dialog.DialogProgress
import com.guider.feifeia3.utils.ToastUtil
import com.guider.gps.BuildConfig
import com.guider.gps.R
import com.guider.gps.adapter.LocationTrackEventTimeAdapter
import com.guider.gps.bean.WithSelectBaseBean
import com.guider.gps.googleMap.MapPositionUtil
import com.guider.gps.view.activity.HistoryRecordActivity
import com.guider.gps.view.activity.LocationFrequencySetActivity
import com.guider.health.apilib.ApiCallBack
import com.guider.health.apilib.ApiUtil
import com.guider.health.apilib.IGuiderApi
import com.guider.health.apilib.bean.ElectronicFenceBean
import kotlinx.android.synthetic.main.fragment_location.*
import retrofit2.Call
import retrofit2.Response
import java.util.*
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
        requestLocationPermission()
        locationFunctionExtend.setOnClickListener(this)
        electronicSetLayout.setOnClickListener(this)
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
                if (selectDateDialog != null) {
                    selectDateDialog?.closeDialog()
                }
                if (loadView != null) {
                    loadView?.dismiss()
                }
                tabPosition = 0
                locationInfoLayout.visibility = View.VISIBLE
                locationNumberSet.text =
                        resources.getString(
                                R.string.app_main_map_positioning_frequency_setting)
                locationNumberSet.setTextColor(
                        CommonUtils.getColor(mActivity, R.color.color333333))
                historyRecord.text =
                        resources.getString(R.string.app_main_map_history_record)
                //取消结束标记并重新定位添加开始标记
                isFirstLocation = true
                mGoogleMap?.clear()
                locationMyAddress()
            }
            tabTitleList[1] -> {
                tabPosition = 1
                if (loadView != null) {
                    loadView?.dismiss()
                }
                trackEventsLayout.visibility = View.VISIBLE
                searchFrontLayout.visibility = View.VISIBLE
                selectTimeBackLayout.visibility = View.GONE
                //重新定位并添加开始标记
                isFirstLocation = true
                dateSelectTag = false
                mGoogleMap?.clear()
                locationMyAddress()
                //获取年月日格式的当前日期
                trackEventsDateValueTv.text = CommonUtils.getCurrentDate(TIME_FORMAT_PATTERN6)
                startTimeTv.text = CommonUtils.calTimeFrontDate(
                        CommonUtils.getCurrentDate(), 7)
                endTimeTv.text = CommonUtils.getCurrentDate()
                val timeHour = trackEventTimeList[trackEventTimePosition].name.toInt()
                val currentDateString = CommonUtils.getCurrentDate(DEFAULT_TIME_FORMAT_PATTERN)
                val startTimeValue = CommonUtils.calTimeFrontHour(currentDateString, timeHour)
                val endTimeValue = CommonUtils.getCurrentDate(DEFAULT_TIME_FORMAT_PATTERN)
                getUserPointData(startTimeValue, endTimeValue)
            }
            tabTitleList[2] -> {
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
                //重新定位并添加开始标记
                mGoogleMap?.clear()
                locationMyAddress()
                getElectronicFenceData()
            }
            tabTitleList[3] -> {
                if (selectDateDialog != null) {
                    selectDateDialog?.closeDialog()
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

    /**
     * 得到用户行动轨迹
     */
    private fun getUserPointData(startTimeValue: String, endTimeValue: String) {
        val dialog = DialogProgress(mActivity, null)
        dialog.showDialog()
        val accountId = MMKVUtil.getInt(USER.USERID)
        Log.i("getUserPointDataTime", "start$startTimeValue-----end$endTimeValue")
        ApiUtil.createApi(IGuiderApi::class.java, false)
                .userPosition(accountId, -1, 20,
                        DateUtilKotlin.localToUTC(startTimeValue)!!,
                        DateUtilKotlin.localToUTC(endTimeValue)!!)
                .enqueue(object : ApiCallBack<List<Any>>(mActivity) {
                    override fun onApiResponse(call: Call<List<Any>>?,
                                               response: Response<List<Any>>?) {
                        if (response?.body() != null) {

                        }
                    }

                    override fun onRequestFinish() {
                        dialog.hideDialog()
                    }
                })
        addEndPerthAndDrawLine()
    }

    /**
     * 获取设备的电子围栏信息
     */
    private fun getElectronicFenceData() {
        val deviceCode = MMKVUtil.getString(BIND_DEVICE_CODE)
        if (StringUtil.isEmpty(deviceCode)) return
        mActivity.showDialog()
        ApiUtil.createApi(IGuiderApi::class.java, false)
                .getElectronicFence(deviceCode)
                .enqueue(object : ApiCallBack<List<ElectronicFenceBean>>(mActivity) {
                    override fun onApiResponse(call: Call<List<ElectronicFenceBean>>?,
                                               response: Response<List<ElectronicFenceBean>>?) {
                        if (!response?.body().isNullOrEmpty()) {
                            val latLngList = arrayListOf<LatLng>()
                            response?.body()?.forEach {
                                latLngList.add(LatLng(it.x, it.y))
                            }
                            polygon = PolygonOptions()
                                    .fillColor(CommonUtils.getColor(
                                            mActivity, R.color.color80F18937))
                                    .strokeWidth(1.0f).addAll(latLngList)
                                    .strokeWidth(1f)
                            mGoogleMap?.addPolygon(polygon)
                        }
                    }

                    override fun onRequestFinish() {
                        mActivity.dismissDialog()
                    }
                })
    }

    /**
     * 设置电子围栏信息
     */
    private fun setElectronicFenceData() {
        val deviceCode = MMKVUtil.getString(BIND_DEVICE_CODE)
        if (StringUtil.isEmpty(deviceCode)) return
        mActivity.showDialog()
        val data = hashMapOf<String, Any>()
        data["deviceCode"] = deviceCode
        data["points"] = arrayListOf(
                ElectronicFenceBean(customFirstLatLng!!.latitude, customFirstLatLng!!.longitude),
                ElectronicFenceBean(customTwoLatLng!!.latitude, customTwoLatLng!!.longitude),
                ElectronicFenceBean(customThirdLatLng!!.latitude, customThirdLatLng!!.longitude),
                ElectronicFenceBean(customFourLatLng!!.latitude, customFourLatLng!!.longitude))
        ApiUtil.createApi(IGuiderApi::class.java, false)
                .setElectronicFence(data)
                .enqueue(object : ApiCallBack<String>(mActivity) {
                    override fun onApiResponse(call: Call<String>?,
                                               response: Response<String>?) {
                        if (response?.body() == "true") {
                            showToast("设置成功")
                        }
                    }

                    override fun onRequestFinish() {
                        mActivity.dismissDialog()
                    }
                })
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
                    val startTimeValue = CommonUtils.calTimeFrontHour(currentDateString, timeHour)
                    val endTimeValue = CommonUtils.getCurrentDate(DEFAULT_TIME_FORMAT_PATTERN)
                    getUserPointData(startTimeValue,endTimeValue)
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
                    loadView = LoadingView(mActivity, false)
                    loadView?.show()
                    locationNumberSet.postDelayed({
                        loadView?.dismiss()
                        ToastUtil.showCustomToast(null, mActivity,
                                true,
                                resources.getString(R.string.app_main_map_send_success))
                    }, 500)
                } else {
                    val intent = Intent(mActivity, LocationFrequencySetActivity::class.java)
                    startActivityForResult(intent, LOCATION_FREQUENCY_SET)
                }
            }
            historyRecord -> {
                val intent = Intent(mActivity, HistoryRecordActivity::class.java)
                startActivity(intent)
            }
            trackEventsDateCalSelectIv -> {
                searchFrontLayout.visibility = View.GONE
                selectTimeBackLayout.visibility = View.VISIBLE
                dateSelectTag = true
            }
            startTimeTv, endTimeTv -> {
                initSelectDateDialogShow()
            }
            searchLayout -> {
                val startTimeValue = "${startTimeTv.text} 00:00:00"
                val endTimeValue = "${endTimeTv.text} 00:00:00"
                getUserPointData(startTimeValue, endTimeValue)
            }
            trackEventsDateLeft -> {
                dateSelectTag = true
                val currentDate = trackEventsDateValueTv.text.toString()
                val calTimeFrontDate = CommonUtils.calTimeFrontDate(
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
                val startTimeValue = "$dateString 00:00:00"
                val endTimeValue = "$dateString 24:00:00"
                getUserPointData(startTimeValue, endTimeValue)
            }
            trackEventsDateRight -> {
                dateSelectTag = true
                val currentDate = trackEventsDateValueTv.text.toString()
                val calTimeFrontDate = CommonUtils.calTimeFrontDate(
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
                val startTimeValue = "$dateString 00:00:00"
                val endTimeValue = "$dateString 24:00:00"
                getUserPointData(startTimeValue, endTimeValue)
            }
            trackEventCalIv -> {
                selectPositionDate()
            }
            mapLocationIv -> {
                if (isShowGpsMode) {
                    mGoogleMap?.mapType = GoogleMap.MAP_TYPE_NORMAL//设置为正常地图模式
                } else {
                    mGoogleMap?.mapType = GoogleMap.MAP_TYPE_SATELLITE//设置为卫星模式
                }
                isShowGpsMode = !isShowGpsMode
            }
            //设置电子围栏在地图上坐标的button
            electronicSetLayout -> {
                if (customElectronicFencePointNum == 0)
                    mGoogleMap?.clear()
                electronicSetHint.visibility = View.VISIBLE
                mGoogleMap?.setOnMapClickListener {
                    //手动去编辑电子围栏
                    mapClickEvent(it)
                }
            }
            //删除电子围栏在地图上坐标点
            electronicDeleteLayout -> {
                if (customElectronicFencePointNum in 1..4) {
                    when (customElectronicFencePointNum) {
                        1 -> {
                            electronicDeleteLayout.isSelected = false
                            electronicDeleteLayout.isSelected = false
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
                    showToast("请选择足够的电子围栏点")
                }
            }
        }
    }

    /**
     * 选择指定某一天查询运动轨迹
     */
    private fun selectPositionDate() {
        val picker = DatePicker(mActivity)
        picker.setGravity(Gravity.BOTTOM)
        picker.setTopPadding(15)
        picker.setRangeStart(1900, 1, 1)
        picker.setRangeEnd(2100, 12, 31)
        val timeValue = trackEventsDateValueTv.text.toString().replace("日", "")
        val dayInt = timeValue.substring(
                timeValue.lastIndexOf('月') + 1).toInt()
        val yearInt = timeValue.substring(0, timeValue.indexOf('年')).toInt()
        val monthInt = timeValue.substring(
                timeValue.indexOf('年') + 1,
                timeValue.lastIndexOf('月')).toInt()
        picker.setSelectedItem(yearInt, monthInt, dayInt)
        picker.setTitleText("$yearInt-$monthInt-$dayInt")
        picker.setWeightEnable(true)
        picker.setTitleText(resources.getString(R.string.app_person_info_select_birthday))
        picker.setSelectedTextColor(CommonUtils.getColor(mActivity, R.color.colorF18937))
        picker.setUnSelectedTextColor(CommonUtils.getColor(mActivity, R.color.colorDDDDDD))
        picker.setTopLineColor(CommonUtils.getColor(mActivity, R.color.colorDDDDDD))
        picker.setLineColor(CommonUtils.getColor(mActivity, R.color.colorDDDDDD))
        picker.setOnDatePickListener(DatePicker.OnYearMonthDayPickListener { year, month, day ->
            run {
                val monthIntValue = month.toInt()
                val dayIntValue = day.toInt()
                val selectDate = year.plus("年")
                        .plus(if (monthIntValue < 10) {
                            "0$monthIntValue"
                        } else monthIntValue)
                        .plus("月").plus(if (dayIntValue < 10) {
                            "0$dayIntValue"
                        } else dayIntValue).plus("日")
                trackEventsDateValueTv.text = selectDate
                selectTimeBackLayout.visibility = View.GONE
                searchFrontLayout.visibility = View.VISIBLE
                val startTimeValue = "$selectDate 00:00:00"
                val endTimeValue = "$selectDate 24:00:00"
                dateSelectTag = false
                getUserPointData(startTimeValue, endTimeValue)
            }
        })
        picker.show()
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
        val uiSettings = mGoogleMap!!.uiSettings
        uiSettings.isMyLocationButtonEnabled = false //显示定位按钮
        uiSettings.isCompassEnabled = false //设置是否显示指南针
        uiSettings.isZoomControlsEnabled = true//缩放控件
        mGoogleMap!!.isTrafficEnabled = true
        mGoogleMap!!.setInfoWindowAdapter(CustomInfoWindowAdapter())
        mGoogleMap!!.setOnMarkerClickListener(this)
        mGoogleMap!!.setOnInfoWindowClickListener(this)
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
                } else {
                    if (mGoogleMap == null) return
                    when (customElectronicFencePointNum) {
                        1 -> {
                            customFirstLatLng = latLng
                            drawCustomFirstPoint(customFirstLatLng!!)
                            electronicSetLayout.isSelected = true
                            electronicSetIv.isSelected = true
                            electronicDeleteLayout.isSelected = true
                            electronicDeleteIv.isSelected = true
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
                .strokeWidth(1f)
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
        // 移动到定位点中心，并且缩放级别为18
        initCamera(LatLng(mLocationLat, mLocationLon))
        // 添加mark标记
        startDisplayPerth(LatLng(mLocationLat, mLocationLon))
    }

    private fun startDisplayPerth(latLng: LatLng) {
        val transJC02LatLng =
                if (BuildConfig.DEBUG) {
                    val gps84ToGcj02 =
                            MapPositionUtil.gps84_To_Gcj02(latLng.latitude, latLng.longitude)
                    LatLng(gps84ToGcj02.lat, gps84ToGcj02.lon)
                } else latLng
        firstLocationLat = transJC02LatLng.latitude
        firstLocationLng = transJC02LatLng.longitude
        // 每一次打点第一个的时候就是定位开始的时候\
        if (tabPosition == 0 || tabPosition == 1) {
            var bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_map_mark_bg_with_head)
            if (tabPosition == 1) {
                bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_mark_start_bg)
            }
            if (mGoogleMap == null) return
            starPerth = mGoogleMap?.addMarker(MarkerOptions()
                    .draggable(false).icon(bitmap).position(
                            LatLng(firstLocationLat, firstLocationLng)))
            if (tabPosition == 0) {
                starPerth?.tag = martTag
            }

            starPerth?.isDraggable = false //设置不可移动
        }
    }


    private fun endDisplayPerth(latLng: LatLng) {
        // 每一次打点第一个的时候就是定位开始的时候
        val bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_mark_end_bg)
        endPerth = mGoogleMap!!.addMarker(MarkerOptions()
                .draggable(false).icon(bitmap).position(latLng))
        endPerth?.isDraggable = false //设置不可移动
    }

    private fun addEndPerthAndDrawLine() {
        val endLng = firstLocationLng + 0.005
        endDisplayPerth(LatLng(firstLocationLat, endLng))
        val calculateLineDistance = calculateLineDistance(
                LatLng(firstLocationLat, firstLocationLng),
                LatLng(firstLocationLat, endLng))
        addPointLine(LatLng(firstLocationLat, firstLocationLng),
                LatLng(firstLocationLat, endLng))
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
            mGoogleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(transJC02LatLng, 16.0f))
        }
    }


    override fun onConnected(bundle: Bundle?) {
        locationMyAddress()
    }

    @SuppressLint("MissingPermission")
    private fun locationMyAddress() {
        val perms = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION)
        val checkPermissions = PermissionUtils.checkPermissions(mActivity, perms)
        if (!checkPermissions) return
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)
        if (mLastLocation != null) {
            isFirstLocation = false
            initCamera(LatLng(mLastLocation!!.latitude, mLastLocation!!.longitude))
            //            // 添加mark标记
            startDisplayPerth(LatLng(mLastLocation!!.latitude, mLastLocation!!.longitude))
            getAddress(mActivity, mLastLocation!!.latitude, mLastLocation!!.longitude)
            //将地图视角切换到定位的位置
            if (!Geocoder.isPresent()) {
                // Toast.makeText(this, "No geocoder available", Toast.LENGTH_LONG).show();
                return
            }
        } else {
            // 启动位置更新
            isFirstLocation = false
            startLocationUpdates()
        }
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

    override fun onConnectionSuspended(p0: Int) {

    }

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
     * 绘制线 两点一线
     *
     * @param oldPointLatLng
     * @param newPointLatLng
     */
    private fun addPointLine(oldPointLatLng: LatLng?, newPointLatLng: LatLng?) {
        line = PolylineOptions()
                .color(CommonUtils.getColor(mActivity, R.color.colorF18937))
                .width(4f)
                .geodesic(true)
                .add(oldPointLatLng, newPointLatLng)
        mGoogleMap?.addPolyline(line)
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
            return true
        }
        return false
    }

    override fun onInfoWindowClick(marker: Marker?) {
        if (marker?.tag == martTag) {
            showToast("开始导航")
            marker.hideInfoWindow()
        }
    }

    internal inner class CustomInfoWindowAdapter : GoogleMap.InfoWindowAdapter {

        private val window: View = mActivity.layoutInflater.inflate(
                R.layout.custom_map_mark_info, null)

        override fun getInfoWindow(marker: Marker): View {
            render(marker, window)
            return window
        }

        private fun render(marker: Any, window: View) {
            val locationMethodTv = window.findViewById<TextView>(R.id.locationMethodTv)
            locationMethodTv.text = String.format(
                    this@LocationFragment.mActivity.resources.getString(
                            R.string.app_map_location_method_content), "WIFI"
            )
        }

        override fun getInfoContents(p0: Marker?): View? {
            return null
        }

    }

    override fun onConnectionFailed(failResult: ConnectionResult) {
        showToast("地图连接失败${failResult.errorMessage}")
    }

    override fun onResume() {
        super.onResume()
        if (mGoogleApiClient != null) mGoogleApiClient?.connect()
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
        isStart = false
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient!!.isConnected) {
                LocationServices.FusedLocationApi.removeLocationUpdates(
                        mGoogleApiClient, locationListener)
            }
        }
    }

}