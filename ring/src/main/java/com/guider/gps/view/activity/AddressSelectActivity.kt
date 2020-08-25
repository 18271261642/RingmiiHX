package com.guider.gps.view.activity

import android.app.Activity
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.guider.baselib.base.BaseActivity
import com.guider.baselib.utils.toastShort
import com.guider.baselib.widget.dialog.DialogHolder
import com.guider.gps.R
import kotlinx.android.synthetic.main.activity_address_select.*
import kotlinx.android.synthetic.main.activity_person_info.*

/**
 * 地址选择页
 */
class AddressSelectActivity : BaseActivity() {

    override val contentViewResId: Int
        get() = R.layout.activity_address_select

    override fun openEventBus(): Boolean {
        return false
    }

    override fun initImmersion() {
        setTitle(resources.getString(R.string.app_person_info_detail_address))
        showBackButton(R.drawable.ic_back_white)
        setRightImage(R.drawable.icon_top_right_confirm, this)
    }

    override fun initView() {

    }

    override fun initLogic() {
        countryLayout.setOnClickListener(this)
    }

    override fun onNoDoubleClick(v: View) {
        when (v) {
            iv_toolbar_right -> {
                toastShort(resources.getString(R.string.app_person_info_change_success))
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
            countryLayout -> {
                selectAddressDialog()
            }
        }
    }

    private fun selectAddressDialog() {
        val dialog = object : DialogHolder(this,
                R.layout.dialog_select_address, Gravity.BOTTOM) {
            override fun bindView(dialogView: View) {
                var tag = 0
                val addressIv = dialogView.findViewById<ImageView>(R.id.addressIv)
                addressIv.setOnClickListener {
                    if (tag == 0) {
                        tag = 1
                        addressIv.setImageResource(R.drawable.icon_city)
                    } else {
                        addressIv.setImageResource(R.drawable.icon_district)
                        addressIv.postDelayed({
                            dialog?.dismiss()
                            countryTv.text = "北京市 北京市 朝阳区"
                        }, 1000)
                    }
                }
            }
        }
        dialog.initView()
        dialog.show(true)
    }

    override fun showToolBar(): Boolean {
        return true
    }
}