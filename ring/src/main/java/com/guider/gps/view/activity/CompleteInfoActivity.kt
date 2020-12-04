package com.guider.gps.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.guider.baselib.base.BaseActivity
import com.guider.baselib.utils.*
import com.guider.baselib.widget.dialog.DialogHolder
import com.guider.baselib.widget.image.ImageLoaderUtils
import com.guider.feifeia3.utils.ToastUtil
import com.guider.gps.R
import com.guider.health.apilib.GuiderApiUtil
import com.guider.health.apilib.utils.MMKVUtil
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import kotlinx.android.synthetic.main.complete_info_activity.*
import kotlinx.android.synthetic.main.include_simple_complete_info.*
import kotlinx.coroutines.launch

/**
 * @Package: com.guider.gps.view.activity
 * @ClassName: CompleteInfoActivity
 * @Description: 完善信息页
 * @Author: hjr
 * @CreateDate: 2020/9/3 14:10
 * Copyright (C), 1998-2020, GuiderTechnology
 */
class CompleteInfoActivity : BaseActivity() {

    private var picPath = ""
    private var type = ""
    private var name = ""
    private var sex = ""
    private var birthday = ""
    private var passwordValue = ""
    private var countryCode = ""
    private var areaCode = ""
    private var phoneValue = ""

    //判断是否是从注册页进入还是从绑定设备添加新账号
    private var pageEnterType = ""

    override val contentViewResId: Int
        get() = R.layout.complete_info_activity

    override fun openEventBus(): Boolean {
        return false
    }

    override fun initImmersion() {
        showBackButton(R.drawable.ic_back_white)
        setTitle(mContext!!.resources.getString(R.string.app_complete_info))
        if (intent != null) {
            if (StringUtil.isNotBlankAndEmpty(intent.getStringExtra("passwd"))) {
                passwordValue = intent.getStringExtra("passwd")!!
            }
            if (StringUtil.isNotBlankAndEmpty(intent.getStringExtra("phone"))) {
                phoneValue = intent.getStringExtra("phone")!!
            }
            if (StringUtil.isNotBlankAndEmpty(intent.getStringExtra("telAreaCode"))) {
                countryCode = intent.getStringExtra("telAreaCode")!!
            }
            if (StringUtil.isNotBlankAndEmpty(intent.getStringExtra("areaCode"))) {
                areaCode = intent.getStringExtra("areaCode")!!
            }
            if (StringUtil.isNotBlankAndEmpty(intent.getStringExtra("pageEnterType"))) {
                pageEnterType = intent.getStringExtra("pageEnterType")!!
            }
        }
    }

    override fun initView() {

    }

    override fun initLogic() {
        sexLayout.setOnClickListener(this)
        birthdayLayout.setOnClickListener(this)
        headerLayout.setOnClickListener(this)
        nameLayout.setOnClickListener(this)
        completeTv.setOnClickListener(this)
    }

    override fun onNoDoubleClick(v: View) {
        when (v) {
            sexLayout -> {
                sexDialogShow()
            }
            nameLayout -> {
                val intent = Intent(mContext, SingleLineEditActivity::class.java)
                intent.putExtra("type", resources.getString(R.string.app_person_info_name))
                if (StringUtil.isNotBlankAndEmpty(nameTv.text.toString()))
                    intent.putExtra("inputValue", nameTv.text.toString())
                startActivityForResult(intent, PERSON_NAME)
            }
            birthdayLayout -> {
                selectBirthdayDialog()
            }
            headerLayout -> {
                headerDialogShow()
            }
            completeTv -> {
                name = nameTv.text.toString()
                if (StringUtil.isEmpty(name)) {
                    toastShort(mContext!!.resources.getString(R.string.app_complete_name_empty))
                    return
                }
                sex = sexTv.text.toString()
                if (StringUtil.isEmpty(sex)) {
                    toastShort(mContext!!.resources.getString(R.string.app_complete_sex_empty))
                    return
                }
                if (StringUtil.isEmpty(picPath)) {
                    toastShort(mContext!!.resources.getString(R.string.app_complete_header_empty))
                    return
                }
                birthday = birthdayTv.text.toString()
                if (StringUtil.isEmpty(birthday)) {
                    toastShort(mContext!!.resources.getString(R.string.app_complete_birthday_empty))
                    return
                }
                uploadHeader()
            }
        }
    }

    private fun uploadHeader() {
        // 上传头像
        showDialog()
        lifecycleScope.launch {
            ApiCoroutinesCallBack.resultParse(mContext!!, block = {
                val resultBean = GuiderApiUtil.getApiService().uploadFile(
                        GuiderApiUtil.uploadFile(picPath))
                if (resultBean != null) {
                    Log.e("上传头像", "成功")
                    registerEvent(resultBean)
                }
            }, onError = {
                Log.e("上传头像", "失败")
                dismissDialog()
            })
        }
    }

    private fun registerEvent(headerUrl: String) {
        val map = hashMapOf<String, Any>()
        map["passwd"] = MyUtils.md5(passwordValue)
        map["phone"] = phoneValue
        map["telAreaCode"] = countryCode
        map["headUrl"] = headerUrl
        map["name"] = name
        //MAN, WOMAN
        map["gender"] =
                if (sex == mContext!!.resources.getString(R.string.app_person_info_man)) "MAN"
                else "WOMAN"
        map["birthday"] = "${birthday}T00:00:00Z"
        lifecycleScope.launch {
            ApiCoroutinesCallBack.resultParse(mContext!!, block = {
                val bean = GuiderApiUtil.getApiService()
                        .register(map)
                if (bean != null) {
                    if (StringUtil.isNotBlankAndEmpty(pageEnterType)) {
                        //为设备添加新用户成功
                        intent.putExtra("completeInfo", true)
                        setResult(Activity.RESULT_OK, intent)
                        EventBusUtils.sendEvent(EventBusEvent(
                                EventBusAction.DEVICE_ADD_MEMBER_INFO,
                                bean.accountId))
                        finish()
                    } else {
                        MMKVUtil.saveString(USER.TOKEN, bean.token!!)
                        MMKVUtil.saveInt(USER.USERID, bean.accountId)
                        MMKVUtil.saveString(USER.COUNTRY_CODE, countryCode)
                        MMKVUtil.saveString(USER.AREA_CODE, areaCode)
                        MMKVUtil.saveString(USER.PHONE, phoneValue)
                        MMKVUtil.saveString(REFRESH_TOKEN, bean.refreshToken!!)
                        MMKVUtil.saveInt(EXPIRED_TIME, bean.expired)
                        MMKVUtil.saveString(USER.HEADER, headerUrl)
                        MMKVUtil.saveString(USER.NAME, name)
                        toastShort("注册成功")
                        //登录成功后还需要回到登录页校验账号是否绑定设备的逻辑
                        intent.putExtra("completeInfo", true)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                }
            }, onRequestFinish = {
                dismissDialog()
            })
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
        var isChange = false
        val dialog = object : DialogHolder(this,
                R.layout.dialog_persondate_bottom_layout, Gravity.BOTTOM) {
            override fun bindView(dialogView: View) {
                val confirmIv = dialogView.findViewById<ImageView>(R.id.confirmIv)
                val mDatePicker = dialogView.findViewById<android.widget.DatePicker>(R.id.datePicker)
                mDatePicker.init(yearInt, monthInt - 1, dayInt) { _, year, monthOfYear, dayOfMonth ->
                    isChange = true
                    yearInt = year
                    monthInt = monthOfYear
                    dayInt = dayOfMonth
                }
                //月份返回的是少1的数
                confirmIv.setOnClickListener {
                    var mMonthNew: String = monthInt.toString()
                    if (isChange && StringUtil.isNotBlankAndEmpty(birthday)) {
                        mMonthNew = (monthInt + 1).toString()
                    }
                    if (monthInt + 1 < 10) {
                        mMonthNew = "0$mMonthNew"
                    }
                    var mDayNew: String = dayInt.toString()
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
                    if (manTv.isSelected) {
                        sexTv.text = manTv.text
                    } else {
                        sexTv.text = girlTv.text
                    }
                    sex = sexTv.text.toString()
                    dialog?.dismiss()
                }
                girlTv.setOnClickListener {
                    girlTv.isSelected = true
                    manTv.isSelected = false
                    if (manTv.isSelected) {
                        sexTv.text = manTv.text
                    } else {
                        sexTv.text = girlTv.text
                    }
                    sex = sexTv.text.toString()
                    dialog?.dismiss()
                }
            }
        }
        dialog.initView()
        dialog.show(true)
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
        PermissionUtils.requestPermissionActivity(this, perms,
                mContext!!.resources.getString(R.string.app_main_map_location_permission_hint), mContext!!.resources.getString(
                R.string.app_request_permission_camera), {
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
                PERSON_NAME -> {
                    if (StringUtil.isNotBlankAndEmpty(data.getStringExtra("inputResult"))) {
                        val result = data.getStringExtra("inputResult")!!
                        when (requestCode) {
                            PERSON_NAME -> {
                                name = result
                                nameTv.text = name
                            }
                        }

                    }
                }
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
                            picPath = path
                        }
                        if (isNotBlankAndEmpty(picPath)) {
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


    override fun showToolBar(): Boolean {
        return true
    }
}