package com.guider.gps.view.activity

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.guider.baselib.base.BaseActivity
import com.guider.baselib.utils.*
import com.guider.feifeia3.utils.ToastUtil
import com.guider.gps.R
import com.guider.gps.view.fragment.InputCodeAddDeviceFragment
import com.guider.gps.view.fragment.ScanCodeAddDeviceFragment
import com.guider.health.apilib.ApiCallBack
import com.guider.health.apilib.ApiUtil
import com.guider.health.apilib.BuildConfig
import com.guider.health.apilib.IGuiderApi
import com.guider.health.apilib.bean.CheckBindDeviceBean
import com.guider.health.apilib.bean.UserInfo
import com.king.zxing.Intents
import kotlinx.android.synthetic.main.activity_add_new_device.*
import retrofit2.Call
import retrofit2.Response

@Suppress("DEPRECATION")
class AddNewDeviceActivity : BaseActivity() {

    override val contentViewResId: Int
        get() = R.layout.activity_add_new_device

    private var fragments = mutableListOf<Fragment>()
    private var tabTitleList = arrayListOf<String>()
    private var userGroupId = ""

    //主要用来判断是自己绑定设备还是绑定其他人的设备
    private var type = ""

    override fun initImmersion() {
        setTitle(resources.getString(R.string.app_add_device))
        showBackButton(R.drawable.ic_back_white)
        if (intent != null) {
            if (StringUtil.isNotBlankAndEmpty(intent.getStringExtra("type"))) {
                type = intent.getStringExtra("type")!!
            }
            if (StringUtil.isNotBlankAndEmpty(intent.getStringExtra("userGroupId"))) {
                userGroupId = intent.getStringExtra("userGroupId")!!
            }
        }
    }

    override fun initView() {
        tabTitleList = arrayListOf(
                resources.getString(R.string.app_add_device_scan_code),
                resources.getString(R.string.app_add_device_input))
        newDeviceTabLayout.tabMode = TabLayout.MODE_FIXED
        // 设置选中下划线颜色
        newDeviceTabLayout.setSelectedTabIndicatorColor(
                CommonUtils.getColor(mContext!!, R.color.colorF18937))
        // 设置文本字体颜色[未选中颜色、选中颜色]
        newDeviceTabLayout.setTabTextColors(CommonUtils.getColor(mContext!!, R.color.color999999),
                CommonUtils.getColor(mContext!!, R.color.color333333))
        // 设置下划线跟文本宽度一致
        newDeviceTabLayout.isTabIndicatorFullWidth = true
        fragments.add(ScanCodeAddDeviceFragment.newInstance())
        fragments.add(InputCodeAddDeviceFragment.newInstance(type))
        newDeviceViewPager.apply {
            adapter = DeviceViewPagerAdapter(
                    this@AddNewDeviceActivity.supportFragmentManager,
                    fragments = fragments, titles = tabTitleList)
        }
        newDeviceViewPager.offscreenPageLimit = fragments.size
        newDeviceTabLayout.setupWithViewPager(newDeviceViewPager)
    }

    inner class DeviceViewPagerAdapter(fragmentManager: FragmentManager,
                                       private val fragments: MutableList<Fragment>,
                                       private val titles: MutableList<String>) :
            FragmentPagerAdapter(fragmentManager) {

        override fun getItem(position: Int) = fragments[position]

        override fun getCount() = fragments.size

        override fun getPageTitle(position: Int) = titles[position]

    }

    /**
     * 绑定新设备是当前登录账户新绑定
     * @param code 要绑定的设备的设备码
     */
    fun bindNewDeviceWithAccount(code: String, nickName: String, header: String,
                                 isShowDialog: Boolean = true) {
        //判断是为当前账户绑定还是添加家人的设备
        //第一种type是登录之后或者打开app时绑定新设备
        //第二种type是在首页解绑设备后重新绑定新设备
        if (type == "mine" || type == "unBindAndBindNew") {
            showDialog()
            val accountId = MMKVUtil.getInt(USER.USERID, 0)
            ApiUtil.createApi(IGuiderApi::class.java, false)
                    .bindDeviceWithAccount(accountId, code)
                    .enqueue(object : ApiCallBack<Any?>(mContext) {
                        override fun onApiResponseNull(call: Call<Any?>?,
                                                       response: Response<Any?>?) {
                            //绑定失败返回null
                            if (response?.body() != null) {
                                toastShort(mContext!!.resources.getString(R.string.app_bind_fail))
                            }
                        }

                        override fun onApiResponse(call: Call<Any?>?,
                                                   response: Response<Any?>?) {
                            if (response?.body() != null) {
                                val bean = ParseJsonData.parseJsonAny<CheckBindDeviceBean>(
                                        response.body()!!)
                                MMKVUtil.saveInt(BIND_DEVICE_ACCOUNT_ID, accountId)
                                bean.userInfos?.forEach {
                                    if (it.accountId == accountId) {
                                        it.relationShip = it.name
                                        MMKVUtil.saveString(BIND_DEVICE_NAME, it.name!!)
                                        if (StringUtil.isNotBlankAndEmpty(it.deviceCode)) {
                                            if (type == "unBindAndBindNew") {
                                                MMKVUtil.saveString(BIND_DEVICE_CODE, it.deviceCode!!)
                                            } else {
                                                MMKVUtil.saveString(USER.OWN_BIND_DEVICE_CODE, it.deviceCode!!)
                                            }
                                        }
                                    }
                                }
                                if (type == "unBindAndBindNew") {
                                    val intent = Intent()
                                    intent.putExtra("bindListBean", bean)
                                    setResult(Activity.RESULT_OK, intent)
                                } else {
                                    val intent = Intent(mContext!!, MainActivity::class.java)
                                    intent.putExtra("bindListBean", bean)
                                    startActivity(intent)
                                }
                                finish()
                            }
                        }

                        override fun onRequestFinish() {
                            dismissDialog()
                        }
                    })
        } else {
            if (isShowDialog)
                showDialog()
            val map = hashMapOf<String, Any>()
            map["deviceCode"] = code
            map["relationShip"] = nickName
            map["url"] = header
            map["userGroupId"] = userGroupId.toInt()
            ApiUtil.createApi(IGuiderApi::class.java, false)
                    .memberJoinGroup(map)
                    .enqueue(object : ApiCallBack<Any>(mContext) {
                        override fun onApiResponse(call: Call<Any>?,
                                                   response: Response<Any>?) {
                            if (response?.body() != null) {
                                val list = ParseJsonData.parseJsonDataList<UserInfo>(response.body()!!,
                                        UserInfo::class.java)
                                if (!list.isNullOrEmpty()) {
                                    val bean = CheckBindDeviceBean()
                                    bean.userGroupId = userGroupId.toInt()
                                    bean.userInfos = list
                                    intent.putExtra("bindListBean", bean)
                                    setResult(Activity.RESULT_OK, intent)
                                    finish()
                                }
                            }
                        }

                        override fun onRequestFinish() {
                            dismissDialog()
                        }
                    })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                SCAN_CODE -> {
                    val result = data.getStringExtra(Intents.Scan.RESULT)
                    val tempCode = "863659040064551"
                    if (StringUtil.isNotBlankAndEmpty(result)) {
                        if (!BuildConfig.DEBUG && result?.length != tempCode.length) {
                            ToastUtil.showCenter(mContext!!,
                                    mContext!!.resources.getString(R.string.app_incorrect_format))
                        } else {
                            newDeviceViewPager.currentItem = 1
                            (fragments[1] as InputCodeAddDeviceFragment).refreshCodeShow(result!!)
                        }
                    }
                }
                IMAGE_CUT_CODE -> {
                    (fragments[1] as InputCodeAddDeviceFragment).selectPicCallBack(data)
                }
            }
        }
    }

    override fun initLogic() {

    }

    override fun showToolBar(): Boolean {
        return true
    }

    override fun openEventBus(): Boolean {
        return false
    }
}