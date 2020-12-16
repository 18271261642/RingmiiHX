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
import com.guider.baselib.utils.USER.HEADER
import com.guider.baselib.utils.USER.NAME
import com.guider.baselib.widget.dialog.DialogHolder
import com.guider.baselib.widget.image.ImageLoaderUtils
import com.guider.feifeia3.utils.ToastUtil
import com.guider.gps.R
import com.guider.health.apilib.GuiderApiUtil
import com.guider.health.apilib.bean.UserInfo
import com.guider.health.apilib.utils.MMKVUtil
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import kotlinx.android.synthetic.main.activity_person_info.*
import kotlinx.coroutines.launch

/**
 * 个人信息页
 */
class PersonInfoActivity : BaseActivity() {


    override val contentViewResId: Int
        get() = R.layout.activity_person_info

    private var isChangeTag = false
    private var isChangePicTag = false
    private var isNameChange = false
    private var type = ""

    private var name = ""
    private var phone = ""
    private var height = ""
    private var weight = ""
    private var picPath = ""
    private var sex = ""
    private var birthday = ""
    private var personInfoBean: UserInfo? = null

    override fun openEventBus(): Boolean {
        return false
    }

    override fun initImmersion() {
        setTitle(resources.getString(R.string.app_person_info_title))
        showBackButton(R.drawable.ic_back_white)
        setRightImage(R.drawable.icon_top_right_confirm, this)
    }

    override fun initView() {
        refreshHeaderAndName()
    }

    override fun initLogic() {
        if (MMKVUtil.getInt(USER.USERID, 0) != 0) {
            getUserInfoData(MMKVUtil.getInt(USER.USERID, 0))
        }
        sexLayout.setOnClickListener(this)
        birthdayLayout.setOnClickListener(this)
        addressLayout.setOnClickListener(this)
        headerLayout.setOnClickListener(this)
        nameLayout.setOnClickListener(this)
        weightLayout.setOnClickListener(this)
        heightLayout.setOnClickListener(this)
    }

    private fun getUserInfoData(accountId: Int) {

        lifecycleScope.launch {
            ApiCoroutinesCallBack.resultParse(onStart = {
                showDialog()
            }, block = {
                val resultBean = GuiderApiUtil.getApiService()
                        .getUserInfo(accountId)
                if (resultBean != null) {
                    personInfoBean = resultBean
                    //拿到个人信息刷新页面
                    dealPersonInfoShow()
                }
            }, onRequestFinish = {
                dismissDialog()
            })
        }
    }

    private fun dealPersonInfoShow() {
        if (picPath != personInfoBean?.headUrl &&
                StringUtil.isNotBlankAndEmpty(personInfoBean?.headUrl)) {
            //把这个标志设置为true是因为防止其他设备修改了头像和昵称
            isNameChange = true
            picPath = personInfoBean?.headUrl!!
            ImageLoaderUtils.loadImage(mContext!!, headerIv, picPath)
            MMKVUtil.saveString(HEADER, picPath)
        }
        if (name != personInfoBean?.name &&
                StringUtil.isNotBlankAndEmpty(personInfoBean?.name)) {
            //把这个标志设置为true是因为防止其他设备修改了头像和昵称
            isNameChange = true
            name = personInfoBean?.name!!
            MMKVUtil.saveString(NAME, name)
            nameTv.text = name
        }
        phone = if (StringUtil.isEmpty(personInfoBean?.phone)) MMKVUtil.getString(USER.PHONE)
        else personInfoBean?.phone ?: ""
        phoneTv.text = phone
        if (StringUtil.isNotBlankAndEmpty(personInfoBean?.birthday))
            birthday = personInfoBean?.birthday!!.replace(
                    "T00:00:00Z", "")
        birthdayTv.text = birthday
        sex = if (personInfoBean?.gender == "MAN") {
            mContext!!.resources.getString(R.string.app_person_info_man)
        } else {
            mContext!!.resources.getString(R.string.app_person_info_woman)
        }
        sexTv.text = sex
        height = personInfoBean?.height.toString()
        heightTv.text = height
        weight = personInfoBean?.weight.toString()
        weightTv.text = weight
        if (StringUtil.isNotBlankAndEmpty(personInfoBean?.descDetail)) {
            addressTv.text = personInfoBean?.descDetail
        }
    }

    override fun onNoDoubleClick(v: View) {
        when (v) {
            iv_toolbar_right -> {
                changePersonHeaderInfo()
            }
            sexLayout -> {
                sexDialogShow()
            }
            birthdayLayout -> {
                selectBirthdayDialog()
            }
            addressLayout -> {
                val intent = Intent(mContext, AddressSelectActivity::class.java)
                intent.putExtra("bean", personInfoBean)
                startActivityForResult(intent, SELECT_ADDRESS)
            }
            headerLayout -> {
                headerDialogShow()
            }
            nameLayout -> {
                val intent = Intent(mContext, SingleLineEditActivity::class.java)
                intent.putExtra("type", resources.getString(R.string.app_person_info_name))
                intent.putExtra("inputValue", name)
                startActivityForResult(intent, PERSON_NAME)
            }
            weightLayout -> {
                val intent = Intent(mContext, SingleLineEditActivity::class.java)
                intent.putExtra("type", resources.getString(R.string.app_person_info_weight))
                intent.putExtra("inputValue", weight)
                startActivityForResult(intent, PERSON_WEIGHT)
            }
            heightLayout -> {
                val intent = Intent(mContext, SingleLineEditActivity::class.java)
                intent.putExtra("type", resources.getString(R.string.app_person_info_height))
                intent.putExtra("inputValue", height)
                startActivityForResult(intent, PERSON_HEIGHT)
            }
        }
    }

    private fun changePersonHeaderInfo() {
        showDialog()
        if (isChangeTag) {
            if (isChangePicTag) {
                lifecycleScope.launch {
                    ApiCoroutinesCallBack.resultParse(block = {
                        val resultBean = GuiderApiUtil.getApiService().uploadFile(
                                GuiderApiUtil.uploadFile(picPath))
                        if (resultBean != null) {
                            Log.e("上传头像", "成功")
                            Log.e("上传头像", "成功")
                            changePersonInfo(resultBean)
                        }
                    }, onError = {
                        Log.e("上传头像", "失败")
                        dismissDialog()
                    })
                }
            } else {
                changePersonInfo(picPath)
            }
        } else {
            endEdit()
        }
    }

    private fun changePersonInfo(header: String?) {
        personInfoBean?.name = name
        personInfoBean?.birthday = "${birthday}T00:00:00Z"
        personInfoBean?.cardId = ""
        personInfoBean?.gender =
                if (sex == mContext!!.resources.getString(R.string.app_person_info_man)) {
                    "MAN"
                } else "WOMAN"
        personInfoBean?.height = height.toInt()
        personInfoBean?.weight = weight.toInt()
        personInfoBean?.phone = phone
        personInfoBean?.accountId = MMKVUtil.getInt(USER.USERID)
        personInfoBean?.headUrl = header
        lifecycleScope.launch {
            ApiCoroutinesCallBack.resultParse(block = {
                val resultBean = GuiderApiUtil.getApiService()
                        .editUserInfo(personInfoBean)
                if (resultBean != null) {
                    //修改成功
                    if (StringUtil.isNotBlankAndEmpty(name))
                        MMKVUtil.saveString(NAME, name)
                    if (StringUtil.isNotBlankAndEmpty(header))
                        MMKVUtil.saveString(HEADER, header!!)
                    toastShort(mContext!!.resources.getString(
                            R.string.app_person_info_change_success))
                    endEdit()
                } else {
                    toastShort(mContext!!.resources.getString(
                            R.string.app_person_info_change_fail))
                }
            }, onRequestFinish = {
                dismissDialog()
            })
        }
    }

    private fun endEdit() {
        intent.putExtra("isChange", isChangePicTag || isNameChange)
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun refreshHeaderAndName() {
        if (MMKVUtil.containKey(HEADER) &&
                StringUtil.isNotBlankAndEmpty(MMKVUtil.getString(HEADER))) {
            ImageLoaderUtils.loadImage(mContext!!, headerIv, MMKVUtil.getString(HEADER))
        }
        if (MMKVUtil.containKey(NAME) &&
                StringUtil.isNotBlankAndEmpty(MMKVUtil.getString(NAME))) {
            nameTv.text = MMKVUtil.getString(NAME)
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
                        if (monthInt + 1 < 10) {
                            mMonthNew = "0$mMonthNew"
                        }
                    } else {
                        if (monthInt < 10) {
                            mMonthNew = "0$mMonthNew"
                        }
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
                mContext!!.resources.getString(R.string.app_camera_permission),
                mContext!!.resources.getString(
                        R.string.app_request_permission_camera), {
            doThings()
        }, {
            ToastUtil.show(mContext!!,
                    mContext!!.resources.getString(R.string.app_request_permission_camera))
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
                PERSON_NAME, PERSON_PHONE, PERSON_HEIGHT, PERSON_WEIGHT -> {
                    isChangeTag = true
                    if (StringUtil.isNotBlankAndEmpty(data.getStringExtra("inputResult"))) {
                        val result = data.getStringExtra("inputResult")!!
                        when (requestCode) {
                            PERSON_NAME -> {
                                isNameChange = true
                                name = result
                                nameTv.text = name
                            }
                            PERSON_PHONE -> {
                                phone = result
                                phoneTv.text = phone
                            }
                            PERSON_HEIGHT -> {
                                height = result
                                heightTv.text = height
                            }
                            PERSON_WEIGHT -> {
                                weight = result
                                weightTv.text = weight
                            }
                        }

                    }
                }
                SELECT_ADDRESS -> {
                    if (data.getParcelableExtra<UserInfo>("bean") != null) {
                        personInfoBean = data.getParcelableExtra("bean")
                        if (StringUtil.isNotBlankAndEmpty(personInfoBean?.descDetail)) {
                            addressTv.text = personInfoBean?.descDetail
                        }
                    }
                }
                IMAGE_CUT_CODE -> {
                    isChangePicTag = true
                    isChangeTag = true
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