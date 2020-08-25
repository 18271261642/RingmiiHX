package com.guider.gps.view.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import cn.addapp.pickers.picker.DatePicker
import com.guider.baselib.base.BaseActivity
import com.guider.baselib.utils.*
import com.guider.baselib.widget.dialog.DialogHolder
import com.guider.gps.R
import kotlinx.android.synthetic.main.activity_health_data_list.*
import kotlinx.android.synthetic.main.activity_person_info.*

/**
 * 个人信息页
 */
class PersonInfoActivity : BaseActivity() {


    override val contentViewResId: Int
        get() = R.layout.activity_person_info

    private var isChangeTag = false

    override fun openEventBus(): Boolean {
        return false
    }

    override fun initImmersion() {
        setTitle(resources.getString(R.string.app_person_info_title))
        showBackButton(R.drawable.ic_back_white)
        setRightImage(R.drawable.icon_top_right_confirm, this)
    }

    override fun initView() {

    }

    override fun initLogic() {
        phoneLayout.setOnClickListener(this)
        sexLayout.setOnClickListener(this)
        birthdayLayout.setOnClickListener(this)
        addressLayout.setOnClickListener(this)
    }

    override fun onNoDoubleClick(v: View) {
        when (v) {
            iv_toolbar_right -> {
                toastShort(resources.getString(R.string.app_person_info_change_success))
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
            phoneLayout -> {
                val intent = Intent(mContext, SingleLineEditActivity::class.java)
                intent.putExtra("type", resources.getString(R.string.app_phone_number) )
                startActivityForResult(intent, PERSON_INFO)
            }
            sexLayout -> {
                sexDialogShow()
            }
            birthdayLayout -> {
                selectBirthdayDialog()
            }
            addressLayout ->{
                val intent = Intent(mContext,AddressSelectActivity::class.java)
                startActivityForResult(intent,ADDRESS_SELECT)
            }
        }
    }

    private fun selectBirthdayDialog() {
        val picker = DatePicker(this)
        picker.setGravity(Gravity.BOTTOM)
        picker.setTopPadding(15)
        picker.setRangeStart(1900, 1, 1)
        picker.setRangeEnd(2100, 12, 31)
        val timeValue = if (StringUtils.isNotBlankAndEmpty(birthdayTv.text.toString())) {
            birthdayTv.text.toString()
        } else {
            CommonUtils.getCurrentDate()
        }
        val dayInt = timeValue.substring(
                timeValue.lastIndexOf('-') + 1).toInt()
        val yearInt = timeValue.substring(0, timeValue.indexOf('-')).toInt()
        val monthInt = timeValue.substring(
                timeValue.indexOf('-') + 1,
                timeValue.lastIndexOf('-')).toInt()
        picker.setSelectedItem(yearInt, monthInt, dayInt)
        picker.setTitleText("$yearInt-$monthInt-$dayInt")
        picker.setWeightEnable(true)
        picker.setTitleText(resources.getString(R.string.app_person_info_select_birthday))
        picker.setSelectedTextColor(CommonUtils.getColor(mContext!!, R.color.colorF18937))
        picker.setUnSelectedTextColor(CommonUtils.getColor(mContext!!, R.color.colorDDDDDD))
        picker.setTopLineColor(CommonUtils.getColor(mContext!!, R.color.colorDDDDDD))
        picker.setLineColor(CommonUtils.getColor(mContext!!, R.color.colorDDDDDD))
        picker.setOnDatePickListener(DatePicker.OnYearMonthDayPickListener { year, month, day ->
            run {
                val selectDate = year.plus("-")
                        .plus(if (month.toInt() < 10) {
                            "0$month"
                        } else month)
                        .plus("-").plus(if (day.toInt() < 10) {
                            "0$day"
                        } else day)
                birthdayTv.text = selectDate
            }
        })
        picker.show()
    }

    private fun sexDialogShow() {
        val dialog = object : DialogHolder(this,
                R.layout.dialog_person_info_sex, Gravity.BOTTOM) {
            override fun bindView(dialogView: View) {
                val manTv = dialogView.findViewById<TextView>(R.id.manTv)
                manTv.isSelected = true
                val girlTv = dialogView.findViewById<TextView>(R.id.girlTv)
                manTv.setOnClickListener {
                    manTv.isSelected = true
                    girlTv.isSelected = false
                }
                girlTv.setOnClickListener {
                    girlTv.isSelected = true
                    manTv.isSelected = false
                }
                val sexConfirmIv = dialogView.findViewById<ImageView>(R.id.sexConfirmIv)
                sexConfirmIv.setOnClickListener {
                    dialog?.dismiss()
                    if (manTv.isSelected) {
                        sexTv.text = manTv.text
                    } else {
                        sexTv.text = girlTv.text
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