package com.guider.gps.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.guider.baselib.base.BaseActivity
import com.guider.baselib.utils.*
import com.guider.baselib.widget.dialog.DialogHolder
import com.guider.baselib.widget.dialog.DialogProgress
import com.guider.baselib.widget.image.ImageLoaderUtils
import com.guider.feifeia3.utils.ToastUtil
import com.guider.gps.R
import com.guider.gps.adapter.CountryCodeDialogAdapter
import com.guider.health.apilib.ApiCallBack
import com.guider.health.apilib.ApiUtil
import com.guider.health.apilib.GuiderApiUtil
import com.guider.health.apilib.JsonApi
import com.guider.health.apilib.bean.AreCodeBean
import com.guider.health.apilib.bean.TokenInfo
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import kotlinx.android.synthetic.main.activity_bind_phone.*
import kotlinx.android.synthetic.main.include_password_edit.*
import kotlinx.android.synthetic.main.include_phone_edit_layout.*
import kotlinx.android.synthetic.main.include_simple_complete_info.*
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response

/**
 * @Package: com.guider.gps.view.activity
 * @ClassName: BindPhoneActivity
 * @Description: 绑定手机号页面
 * @Author: hjr
 * @CreateDate: 2020/8/28 14:51
 * Copyright (C), 1998-2020, GuiderTechnology
 */
class BindPhoneActivity : BaseActivity() {

    private var openId = ""
    private var appId = ""
    private var header = ""
    private var name = ""
    private var phoneValue = ""
    private var sex = ""
    private var birthday = ""
    private var type = ""
    private var isHaveLineHead = true
    private var mDialog: DialogProgress? = null

    override val contentViewResId: Int
        get() = R.layout.activity_bind_phone

    override fun openEventBus(): Boolean {
        return false
    }

    override fun initImmersion() {
        showBackButton(R.drawable.ic_back_white)
        setTitle(mContext!!.resources.getString(R.string.app_bind_phone_number))
        if (intent != null) {
            if (StringUtil.isNotBlankAndEmpty(intent.getStringExtra("openId"))) {
                openId = intent.getStringExtra("openId")!!
            }
            if (StringUtil.isNotBlankAndEmpty(intent.getStringExtra("appId"))) {
                appId = intent.getStringExtra("appId")!!
            }
            if (StringUtil.isNotBlankAndEmpty(intent.getStringExtra("header"))) {
                header = intent.getStringExtra("header")!!
            }
            if (StringUtil.isNotBlankAndEmpty(intent.getStringExtra("name"))) {
                name = intent.getStringExtra("name")!!
            }
        }
    }

    override fun initView() {
        if (StringUtil.isEmpty(header)) {
            isHaveLineHead = false
            headerLayout.visibility = View.VISIBLE
        }
    }

    override fun initLogic() {
        passwordEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s?.length != 0) {
                    passwordShowIv.visibility = View.VISIBLE
                } else passwordShowIv.visibility = View.GONE
            }

        })
        passwordShowIv.setOnClickListener(this)
        countryCodeLayout.setOnClickListener(this)
        bindTv.setOnClickListener(this)
        sexLayout.setOnClickListener(this)
        birthdayLayout.setOnClickListener(this)
        headerLayout.setOnClickListener(this)
    }

    override fun showToolBar(): Boolean {
        return true
    }

    override fun onNoDoubleClick(v: View) {
        when (v) {
            countryCodeLayout -> {
                getCountryCode()
            }
            bindTv -> {
                bindEvent()
            }
            sexLayout -> {
                sexDialogShow()
            }
            passwordShowIv -> {
                if (passwordShowIv.isSelected) {
                    passwordShowIv.isSelected = false
                    passwordShowIv.setImageResource(R.drawable.icon_password_show_close)
                    passwordEdit.inputType =
                            InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
                    passwordEdit.setSelection(passwordEdit.text.length)
                } else {
                    passwordShowIv.isSelected = true
                    passwordShowIv.setImageResource(R.drawable.icon_password_show_open)
                    passwordEdit.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    passwordEdit.setSelection(passwordEdit.text.length)
                }
            }
            birthdayLayout -> {
                selectBirthdayDialog()
            }
            headerLayout -> {
                headerDialogShow()
            }
        }
    }

    private fun headerDialogShow() {
        val dialog = object : DialogHolder(this,
                R.layout.dialog_personicon_bottom_layout, Gravity.BOTTOM) {
            override fun bindView(dialogView: View) {
                val mTakePicture = dialogView.findViewById<TextView>(R.id.takePicture)
                val mSelectPicture = dialogView.findViewById<TextView>(R.id.selectPicture)
                val mDismiss = dialogView.findViewById<TextView>(R.id.dismiss)
                mTakePicture.setOnClickListener {
                    type = "takePhoto"
                    requestPhonePermission()
                    dialog?.dismiss()
                }
                mSelectPicture.setOnClickListener {
                    type = ""
                    requestPhonePermission()
                    dialog?.dismiss()
                }
                mDismiss.setOnClickListener {
                    dialog?.dismiss()
                }
            }
        }
        dialog.initView()
        dialog.show(true)
    }

    @SuppressLint("CheckResult")
    private fun requestPhonePermission() {
        val perms = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
        PermissionUtils.requestPermissionActivity(this, perms, "照相机权限", {
            doThings()
        }, {
            ToastUtil.show(mContext!!, mContext!!.resources.getString(
                    R.string.app_request_permission_camera))
        })
    }

    private fun doThings() {
        if (type == "takePhoto") takePhoto()
        else selectPhoto()
    }

    private fun selectPhoto() {
        PhotoCuttingUtil.selectPhotoZoom2(this, IMAGE_CUT_CODE)
    }

    private fun takePhoto() {
        PhotoCuttingUtil.takePhotoZoom2(this, IMAGE_CUT_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                IMAGE_CUT_CODE -> {
                    // 图片选择结果回调
                    val selectList = PictureSelector.obtainMultipleResult(data)
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，已取压缩路径为准，因为是先裁剪后压缩的
                    if (selectList.isNotEmpty()) {
                        val media: LocalMedia = selectList[0]
                        if (StringUtil.isEmpty(media.path)) {
                            return
                        }
                        val path: String
                        path = if (media.isCut && !media.isCompressed) {
                            // 裁剪过
                            media.cutPath
                        } else if (media.isCompressed || media.isCut && media.isCompressed) {
                            // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
                            media.compressPath
                        } else {
                            // 原图
                            media.path
                        }
                        if (!(PictureMimeType.isContent(path) &&
                                        !media.isCut && !media.isCompressed)) {
                            header = path
                        }
                        if (isNotBlankAndEmpty(header)) {
                            ImageLoaderUtils.loadImage(mContext!!, headerIv,
                                    if (PictureMimeType.isContent(path) &&
                                            !media.isCut && !media.isCompressed)
                                        Uri.parse(path) else path)
                        }
                    }
                }
            }
        }
    }

    private fun selectBirthdayDialog() {
        val timeValue = if (StringUtil.isNotBlankAndEmpty(birthday)) {
            birthday
        } else {
            CommonUtils.getCurrentDate()
        }
        var dayInt = timeValue.substring(
                timeValue.lastIndexOf('-') + 1).toInt()
        var yearInt = timeValue.substring(0, timeValue.indexOf('-')).toInt()
        var monthInt = timeValue.substring(
                timeValue.indexOf('-') + 1,
                timeValue.lastIndexOf('-')).toInt()
        val dialog = object : DialogHolder(this,
                R.layout.dialog_persondate_bottom_layout, Gravity.BOTTOM) {
            override fun bindView(dialogView: View) {
                val confirmIv = dialogView.findViewById<ImageView>(R.id.confirmIv)
                val mDatePicker = dialogView.findViewById<DatePicker>(R.id.datePicker)
                mDatePicker.init(yearInt, monthInt - 1, dayInt) { _, year, monthOfYear, dayOfMonth ->
                    yearInt = year
                    monthInt = monthOfYear
                    dayInt = dayOfMonth
                }
                //月份返回的是少1的数
                confirmIv.setOnClickListener {
                    var mMonthNew: String = (monthInt + 1).toString()
                    if (monthInt + 1 < 10) {
                        mMonthNew = "0$mMonthNew"
                    }
                    var mDayNew: String = (dayInt).toString()
                    if (dayInt < 10) {
                        mDayNew = "0$dayInt"
                    }
                    birthday = "$yearInt-$mMonthNew-$mDayNew"
                    birthdayTv.text = birthday
                    dialog?.dismiss()
                }
            }
        }
        dialog.initView()
        dialog.show(true)
    }

    private fun sexDialogShow() {
        val dialog = object : DialogHolder(this,
                R.layout.dialog_person_info_sex, Gravity.BOTTOM) {
            override fun bindView(dialogView: View) {
                val manTv = dialogView.findViewById<TextView>(R.id.manTv)
                val girlTv = dialogView.findViewById<TextView>(R.id.girlTv)
                if (StringUtil.isNotBlankAndEmpty(sex)) {
                    if (sex == manTv.text) {
                        manTv.isSelected = true
                    } else girlTv.isSelected = true
                } else manTv.isSelected = true
                manTv.setOnClickListener {
                    manTv.isSelected = true
                    girlTv.isSelected = false
                    dialog?.dismiss()
                    if (manTv.isSelected) {
                        sexTv.text = manTv.text
                    } else {
                        sexTv.text = girlTv.text
                    }
                    sex = sexTv.text.toString()
                }
                girlTv.setOnClickListener {
                    girlTv.isSelected = true
                    manTv.isSelected = false
                    dialog?.dismiss()
                    if (manTv.isSelected) {
                        sexTv.text = manTv.text
                    } else {
                        sexTv.text = girlTv.text
                    }
                    sex = sexTv.text.toString()
                }
            }
        }
        dialog.initView()
        dialog.show(true)
    }

    private fun bindEvent() {
        phoneValue = phoneEdit.text.toString()
        if (StringUtil.isEmpty(phoneValue)) {
            toastShort(mContext!!.resources.getString(R.string.app_login_phone_empty))
            return
        }
        val countryCode = countryTv.text.toString().replace("+", "")
        if (!StringUtil.isMobileNumber(countryCode, phoneValue)) {
            toastShort(mContext!!.resources.getString(R.string.app_phone_illegal))
            return
        }
        if (!StringUtil.isMobileNumber(countryCode, phoneValue)) {
            toastShort(mContext!!.resources.getString(R.string.app_phone_illegal))
            return
        }
        if (StringUtil.isEmpty(passwordEdit.text.toString())) {
            toastShort(mContext!!.resources.getString(R.string.app_login_password_empty))
            return
        }
        val passwordValue = MyUtils.md5(passwordEdit.text.toString())
        sex = sexTv.text.toString()
        if (StringUtil.isEmpty(sex)) {
            toastShort(mContext!!.resources.getString(R.string.app_complete_sex_empty))
            return
        }
        if (StringUtil.isEmpty(header)) {
            toastShort(mContext!!.resources.getString(R.string.app_complete_header_empty))
            return
        }
        birthday = birthdayTv.text.toString()
        if (StringUtil.isEmpty(birthday)) {
            toastShort(mContext!!.resources.getString(R.string.app_complete_birthday_empty))
            return
        }
        //从line来的头像或者上传成功但是绑定时的情况
        if (isHaveLineHead || header.startsWith("http")) {
            isHaveLineHead = true
            bindLineToPhone(header, countryCode, passwordValue)
        } else {
            uploadHeader(countryCode, passwordValue)
        }
    }

    private fun uploadHeader(countryCode: String, passwordValue: String) {
        // 上传头像
        showDialog()
        lifecycleScope.launch {
            try {
                val resultBean = GuiderApiUtil.getApiService().uploadFile(
                        GuiderApiUtil.uploadFile(header))
                dismissDialog()
                if (resultBean != null) {
                    Log.e("上传头像", "成功")
                    header = resultBean
                    bindLineToPhone(header, countryCode, passwordValue)
                }
            } catch (e: Exception) {
                dismissDialog()
                Log.e("上传头像", "失败")
                toastShort(e.message!!)
            }
        }
    }


    private fun bindLineToPhone(header: String, countryCode: String, passwordValue: String) {
        mDialog = DialogProgress(mContext!!, null)
        mDialog?.showDialog()
        val map = hashMapOf<String, Any?>()
        map["appId"] = appId
        map["areaCode"] = countryCode
        map["birthday"] = "${birthday}T00:00:00Z"
        map["password"] = passwordValue
        map["doctorAccountId"] = -1
        map["gender"] = if (sex == mContext!!.resources.getString(
                        R.string.app_person_info_man)) "MAN"
        else "WOMAN"
        map["groupId"] = -1
        map["headimgurl"] = header
        map["nickname"] = name
        map["openid"] = openId
        map["phone"] = phoneValue
        lifecycleScope.launch {
            try {
                val bean = GuiderApiUtil.getApiService()
                        .lineBindLogin(map)
                mDialog?.hideDialog()
                if (bean != null)
                    loginSuccessEvent(bean.TokenInfo)
            } catch (e: Exception) {
                mDialog?.hideDialog()
                toastShort(e.message!!)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mDialog != null) {
            mDialog?.hideDialog()
            mDialog = null
        }
    }

    private fun loginSuccessEvent(bean: TokenInfo) {
        MMKVUtil.saveString(USER.TOKEN, bean.token!!)
        MMKVUtil.saveInt(USER.USERID, bean.accountId)
        MMKVUtil.saveString(USER.COUNTRY_CODE, countryTv.text.toString())
        MMKVUtil.saveString(USER.PHONE, phoneValue)
        MMKVUtil.saveString(REFRESH_TOKEN, bean.refreshToken!!)
        MMKVUtil.saveInt(EXPIRED_TIME, bean.expired)
        toastShort("登录成功")
        //登录成功后还需要回到登录页校验账号是否绑定设备的逻辑
        enterMainPage()
    }

    private fun enterMainPage() {
        intent.putExtra("bind", "success")
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun getCountryCode() {
        showDialog()
        ApiUtil.createRingApi(JsonApi::class.java)
                .countryCode
                .enqueue(object : ApiCallBack<List<AreCodeBean>>(mContext) {
                    override fun onApiResponse(call: Call<List<AreCodeBean>>?,
                                               response: Response<List<AreCodeBean>>?) {
                        if (response != null) {
                            if (response.body() != null) {
                                getCountryCodeDialogShow(response.body())
                            }
                        }
                    }

                    override fun onRequestFinish() {
                        dismissDialog()
                    }
                })
    }

    private fun getCountryCodeDialogShow(areaList: List<AreCodeBean>?) {
        if (areaList.isNullOrEmpty()) {
            return
        }
        val newList = areaList as ArrayList
        val dialog = object : DialogHolder(this,
                R.layout.dialog_country_code, Gravity.CENTER) {
            override fun bindView(dialogView: View) {
                val adapter = CountryCodeDialogAdapter(mContext!!, newList)
                val countryRv = dialogView.findViewById<RecyclerView>(R.id.countryRv)
                adapter.setListener(object : AdapterOnItemClickListener {
                    @SuppressLint("SetTextI18n")
                    override fun onClickItem(position: Int) {
                        val code = newList[position].phoneCode.toString()
                        countryTv.text = "+$code"
                        dialog?.dismiss()
                    }

                })
                countryRv.layoutManager = LinearLayoutManager(mContext)
                countryRv.adapter = adapter
            }
        }
        dialog.initView()
        dialog.show(true)
    }
}