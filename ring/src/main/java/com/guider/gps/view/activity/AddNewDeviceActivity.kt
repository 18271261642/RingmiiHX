package com.guider.gps.view.activity

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.guider.baselib.base.BaseActivity
import com.guider.baselib.utils.*
import com.guider.baselib.widget.viewpageradapter.FragmentLazyStateAdapterViewPager2
import com.guider.gps.R
import com.guider.gps.view.fragment.InputCodeAddDeviceFragment
import com.guider.gps.view.fragment.ScanCodeAddDeviceFragment
import com.guider.health.apilib.ApiCallBack
import com.guider.health.apilib.ApiUtil
import com.guider.health.apilib.IGuiderApi
import com.guider.health.apilib.bean.CheckBindDeviceBean
import com.king.zxing.Intents
import kotlinx.android.synthetic.main.activity_add_new_device.*
import retrofit2.Call
import retrofit2.Response

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
        fragments.add(InputCodeAddDeviceFragment.newInstance())
        newDeviceViewPager.apply {
            adapter = FragmentLazyStateAdapterViewPager2(
                    this@AddNewDeviceActivity, fragments = fragments)
        }
        TabLayoutMediator(newDeviceTabLayout, newDeviceViewPager) { tab, position ->
            tab.text = tabTitleList[position]
        }.attach()
    }

    /**
     * 绑定新设备是当前登录账户新绑定
     * @param code 要绑定的设备的设备码
     */
    fun bindNewDeviceWithAccount(code: String) {
        //判断是为当前账户绑定还是添加家人的设备
        if (type == "mine") {
            showDialog()
            val accountId = MMKVUtil.getInt(USER.USERID, 0)
            val codeValue = "12345678android"
            ApiUtil.createApi(IGuiderApi::class.java, false)
                    .bindDeviceWithAccount(accountId, codeValue)
                    .enqueue(object : ApiCallBack<Any?>(mContext) {
                        override fun onApiResponseNull(call: Call<Any?>?,
                                                       response: Response<Any?>?) {
                            //绑定失败返回null
                            if (response?.body() != null) {
                                toastShort("绑定失败")
                            }
                        }

                        override fun onApiResponse(call: Call<Any?>?,
                                                   response: Response<Any?>?) {
                            if (response?.body() != null) {
                                val bean = ParseJsonData.parseJsonAny<CheckBindDeviceBean>(
                                        response.body()!!)
                                val intent = Intent(mContext!!, MainActivity::class.java)
                                MMKVUtil.saveString(BIND_DEVICE_ACCOUNT_ID, accountId.toString())
                                MMKVUtil.saveString(CURRENT_DEVICE_NAME,
                                        mContext!!.resources.getString(R.string.app_own_string))
                                bean.userInfos?.forEach {
                                    if (it.accountId == accountId) {
                                        it.relationShip = mContext!!.resources.getString(
                                                R.string.app_own_string)
                                    }
                                }
                                intent.putExtra("bindListBean", bean)
                                startActivity(intent)
                                finish()
                            }
                        }

                        override fun onRequestFinish() {
                            dismissDialog()
                        }
                    })
        } else {
            showDialog()
            val codeValue = "87654321android"
            ApiUtil.createApi(IGuiderApi::class.java, false)
                    .verifyDeviceBind(codeValue)
                    .enqueue(object : ApiCallBack<String>(mContext) {
                        override fun onApiResponse(call: Call<String>?,
                                                   response: Response<String>?) {
                            if (response?.body() != null) {
                                val intent = Intent(mContext,
                                        DeviceBindAddMemberActivity::class.java)
                                intent.putExtra("userGroupId", userGroupId)
                                if (response.body()!!.toInt() < 0) {
                                    //返回帐号id，小于0则表示设备未绑定帐号
                                    intent.putExtra("codeValue", codeValue)
                                } else {
                                    //已有绑定的用户
                                    val accountId = response.body()
                                    toastShort("该设备已被绑定")
                                    intent.putExtra("accountId", accountId)
                                }
                                startActivityForResult(intent, DEVICE_BIND_ADD_MEMBER)
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
            if (requestCode == SCAN_CODE) {
                val result = data.getStringExtra(Intents.Scan.RESULT)
                if (StringUtil.isNotBlankAndEmpty(result)) {
                    bindNewDeviceWithAccount(result!!)
                }
            } else if (requestCode == DEVICE_BIND_ADD_MEMBER) {
                finish()
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