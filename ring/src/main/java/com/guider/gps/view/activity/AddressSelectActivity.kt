package com.guider.gps.view.activity

import android.app.Activity
import android.util.Log
import android.view.View
import com.guider.baselib.base.BaseActivity
import com.guider.baselib.utils.CommonUtils
import com.guider.baselib.utils.StringUtil
import com.guider.baselib.utils.toastShort
import com.guider.gps.R
import com.guider.health.apilib.ApiCallBack
import com.guider.health.apilib.ApiUtil
import com.guider.health.apilib.IGuiderApi
import com.guider.health.apilib.enums.AddressType
import com.guider.health.apilib.bean.AddressWithCodeBean
import com.guider.health.apilib.bean.UserInfo
import com.lljjcoder.Interface.OnCityItemClickListenerNew
import com.lljjcoder.bean.CityBeanNew
import com.lljjcoder.bean.DistrictBeanNew
import com.lljjcoder.bean.ProvinceBeanNew
import com.lljjcoder.style.cityjd.JDCityConfig
import com.lljjcoder.style.cityjd.JDCityPicker
import kotlinx.android.synthetic.main.activity_address_select.*
import retrofit2.Call
import retrofit2.Response

/**
 * 地址选择页
 */
class AddressSelectActivity : BaseActivity() {

    private var districtValue = ""
    private var detailAddress = ""
    private var provinceValue = ""
    private var cityValue = ""
    private var countieValue = ""
    private var provinceValueInt = 0
    private var cityValueInt = 0
    private var countieValueInt = 0
    private var bean: UserInfo? = null

    override val contentViewResId: Int
        get() = R.layout.activity_address_select

    override fun openEventBus(): Boolean {
        return false
    }

    override fun initImmersion() {
        setTitle(resources.getString(R.string.app_person_info_detail_address))
        showBackButton(R.drawable.ic_back_white)
        setRightImage(R.drawable.icon_top_right_confirm, this)
        if (intent != null) {
            if (intent.getParcelableExtra<UserInfo>("bean") != null) {
                bean = intent.getParcelableExtra("bean")
            }
        }
    }

    override fun initView() {
        if (bean != null) {
            if (bean!!.province != 0) {
                provinceValueInt = bean!!.province
                cityValueInt = bean!!.city
                countieValueInt = bean!!.countie
                getProvinceAddress()
            }
            if (StringUtil.isNotBlankAndEmpty(bean!!.descDetail)) {
                detailAddress = bean!!.descDetail!!
                detailAddressEdit.setText(detailAddress)
            }
        }
    }

    override fun initLogic() {
        countryLayout.setOnClickListener(this)
    }

    override fun onNoDoubleClick(v: View) {
        when (v) {
            iv_toolbar_right -> {
                if (StringUtil.isEmpty(districtValue)) {
                    return
                }
                detailAddress = detailAddressEdit.text.toString()
                if (StringUtil.isEmpty(detailAddress)) {
                    return
                }
                getAddressCode()
            }
            countryLayout -> {
                selectAddressDialog()
            }
        }
    }

    private fun getProvinceAddress() {
        showDialog()
        ApiUtil.createApi(IGuiderApi::class.java, false)
                .getAddressCode(AddressType.PROVINCE, 0)
                .enqueue(object : ApiCallBack<List<AddressWithCodeBean>>(mContext) {
                    override fun onApiResponse(call: Call<List<AddressWithCodeBean>>?,
                                               response: Response<List<AddressWithCodeBean>>?) {
                        if (!response?.body().isNullOrEmpty()) {
                            run breaking@{
                                response?.body()?.forEach {
                                    if (it.id == provinceValueInt) {
                                        provinceValue = getProvinceName(it.name)
                                        getCityAddress(provinceValueInt)
                                        return@breaking
                                    }
                                }
                            }
                        }
                    }

                    override fun onRequestFinish() {
                        dismissDialog()
                    }
                })
    }

    private fun getProvinceName(name: String): String {
        return when (name) {
            //4个直辖市
            "北京", "天津", "上海", "重庆" -> {
                "${name}市"
            }
            //2个自治区
            "内蒙古", "西藏" -> {
                "${name}自治区"
            }
            "新疆" -> {
                "新疆维吾尔自治区"
            }
            "宁夏" -> {
                "宁夏回族自治区"
            }
            "壮族自治区" -> {
                "广西壮族自治区"
            }
            else -> "${name}省"
        }
    }

    private fun getCityAddress(parentId: Int) {
        ApiUtil.createApi(IGuiderApi::class.java, false)
                .getAddressCode(AddressType.CITY, parentId)
                .enqueue(object : ApiCallBack<List<AddressWithCodeBean>>(mContext) {
                    override fun onApiResponse(call: Call<List<AddressWithCodeBean>>?,
                                               response: Response<List<AddressWithCodeBean>>?) {
                        if (!response?.body().isNullOrEmpty()) {
                            run breaking@{
                                response?.body()?.forEach {
                                    if (it.id == cityValueInt) {
                                        cityValue = getCityName(it.name)
                                        getCountieAddress(cityValueInt)
                                        return@breaking
                                    }
                                }
                            }
                        }
                    }

                    override fun onRequestFinish() {
                        dismissDialog()
                    }
                })
    }

    private fun getCityName(name: String): String {
        return when (name) {
            //4个直辖市
            "北京", "天津", "上海", "重庆" -> {
                "${name}市"
            }
            else -> name
        }
    }

    private fun getCountieAddress(parentId: Int) {
        ApiUtil.createApi(IGuiderApi::class.java, false)
                .getAddressCode(AddressType.COUNTIE, parentId)
                .enqueue(object : ApiCallBack<List<AddressWithCodeBean>>(mContext) {
                    override fun onApiResponse(call: Call<List<AddressWithCodeBean>>?,
                                               response: Response<List<AddressWithCodeBean>>?) {
                        if (!response?.body().isNullOrEmpty()) {
                            run breaking@{
                                response?.body()?.forEach {
                                    if (it.id == countieValueInt) {
                                        countieValue = it.name
                                        districtValue =
                                                "$provinceValue $cityValue $countieValue"
                                        countryTv.text = districtValue
                                        countryTv.setTextColor(
                                                CommonUtils.getColor(
                                                        mContext!!, R.color.color333333))
                                        return@breaking
                                    }
                                }
                            }
                        }
                    }

                    override fun onRequestFinish() {
                        dismissDialog()
                    }
                })
    }

    private fun getAddressCode() {
        showDialog()
        val province = when {
            provinceValue.contains("省") -> {
                provinceValue.replace("省", "")
            }
            provinceValue.contains("市") -> {
                provinceValue.replace("市", "")
            }
            provinceValue.contains("维吾尔自治区") -> {
                provinceValue.replace("维吾尔自治区", "")
            }
            provinceValue.contains("回族自治区") -> {
                provinceValue.replace("回族自治区", "")
            }
            provinceValue.contains("壮族自治区") -> {
                provinceValue.replace("壮族自治区", "")
            }
            provinceValue.contains("自治区") -> {
                provinceValue.replace("自治区", "")
            }
            else -> provinceValue
        }
        ApiUtil.createApi(IGuiderApi::class.java, false)
                .getAddressCode(AddressType.PROVINCE, 0)
                .enqueue(object : ApiCallBack<List<AddressWithCodeBean>>(mContext) {
                    override fun onApiResponse(call: Call<List<AddressWithCodeBean>>?,
                                               response: Response<List<AddressWithCodeBean>>?) {
                        if (!response?.body().isNullOrEmpty()) {
                            run breaking@{
                                response?.body()?.forEach {
                                    if (it.name == province) {
                                        provinceValueInt = it.id
                                        getCityCode(provinceValueInt)
                                        return@breaking
                                    }
                                }
                            }
                        }
                    }

                    override fun onRequestFinish() {
                        dismissDialog()
                    }
                })
    }

    private fun getCityCode(parentId: Int) {
        val city = when (cityValue) {
            //4个直辖市
            "北京市", "天津市", "上海市", "重庆市" -> {
                cityValue.replace("市", "")
            }
            else -> cityValue
        }
        ApiUtil.createApi(IGuiderApi::class.java, false)
                .getAddressCode(AddressType.CITY, parentId)
                .enqueue(object : ApiCallBack<List<AddressWithCodeBean>>(mContext) {
                    override fun onApiResponse(call: Call<List<AddressWithCodeBean>>?,
                                               response: Response<List<AddressWithCodeBean>>?) {
                        if (!response?.body().isNullOrEmpty()) {
                            run breaking@{
                                response?.body()?.forEach {
                                    if (it.name == city) {
                                        cityValueInt = it.id
                                        getCountieCode(cityValueInt)
                                        return@breaking
                                    }
                                }
                            }
                        }
                    }

                    override fun onRequestFinish() {
                        dismissDialog()
                    }
                })
    }

    private fun getCountieCode(parentId: Int) {
        val countie = countieValue
        ApiUtil.createApi(IGuiderApi::class.java, false)
                .getAddressCode(AddressType.COUNTIE, parentId)
                .enqueue(object : ApiCallBack<List<AddressWithCodeBean>>(mContext) {
                    override fun onApiResponse(call: Call<List<AddressWithCodeBean>>?,
                                               response: Response<List<AddressWithCodeBean>>?) {
                        if (!response?.body().isNullOrEmpty()) {
                            run breaking@{
                                response?.body()?.forEach {
                                    if (it.name == countie) {
                                        countieValueInt = it.id
                                        changeAddressInfo()
                                        return@breaking
                                    }
                                }
                            }
                        }
                    }

                    override fun onRequestFinish() {
                        dismissDialog()
                    }
                })
    }

    private fun changeAddressInfo() {
        showDialog()
        bean?.province = provinceValueInt
        bean?.city = cityValueInt
        bean?.countie = countieValueInt
        bean?.descDetail = detailAddress
        ApiUtil.createApi(IGuiderApi::class.java, false)
                .editUserInfo(bean)
                .enqueue(object : ApiCallBack<UserInfo>(mContext) {
                    override fun onApiResponse(call: Call<UserInfo>?,
                                               response: Response<UserInfo>?) {
                        if (response?.body() != null) {
                            //修改成功
                            toastShort(mContext!!.resources.getString(
                                    R.string.app_person_info_change_success))
                            intent.putExtra("bean", bean)
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        }
                    }

                    override fun onRequestFinish() {
                        dismissDialog()
                    }
                })
    }

    private fun selectAddressDialog() {
        val cityPicker = JDCityPicker()
        val jdCityConfig = JDCityConfig.Builder().build()
        jdCityConfig.showType = JDCityConfig.ShowType.PRO_CITY_DIS
        cityPicker.init(this)
        cityPicker.setConfig(jdCityConfig)
        cityPicker.setOnCityItemClickListener(object : OnCityItemClickListenerNew() {

            override fun onSelected(province: ProvinceBeanNew, city: CityBeanNew,
                                    district: DistrictBeanNew) {
                Log.e("", "城市选择结果：" + province.area_name + city.area_name + district.area_name)
                provinceValue = province.area_name
                cityValue = city.area_name
                countieValue = district.area_name
                districtValue = "${province.area_name} ${city.area_name} ${district.area_name}"
                countryTv.setTextColor(CommonUtils.getColor(mContext!!, R.color.color333333))
                countryTv.text = districtValue
            }

            override fun onCancel() {
                if (StringUtil.isEmpty(districtValue)) {
                    countryTv.text = mContext!!.resources.getString(
                            R.string.app_person_info_district_information)
                    countryTv.setTextColor(CommonUtils.getColor(mContext!!, R.color.colorCCCCCC))
                }
            }
        })
        cityPicker.showCityPicker()
    }

    override fun showToolBar(): Boolean {
        return true
    }
}