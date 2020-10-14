package com.guider.gps.view.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.guider.baselib.base.BaseFragment
import com.guider.baselib.utils.IMAGE_CUT_CODE
import com.guider.baselib.utils.PermissionUtils
import com.guider.baselib.utils.PhotoCuttingUtil
import com.guider.baselib.utils.StringUtil
import com.guider.baselib.widget.dialog.DialogHolder
import com.guider.baselib.widget.image.ImageLoaderUtils
import com.guider.feifeia3.utils.ToastUtil
import com.guider.gps.R
import com.guider.gps.view.activity.AddNewDeviceActivity
import com.guider.health.apilib.BuildConfig
import com.guider.health.apilib.GuiderApiUtil
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import kotlinx.android.synthetic.main.fragment_input_code_add_device.*
import kotlinx.coroutines.launch

class InputCodeAddDeviceFragment : BaseFragment() {

    private val fragmentType = "text"
    private var addDeviceType = ""

    companion object {
        fun newInstance(text: String) = InputCodeAddDeviceFragment().apply {
            arguments = Bundle().apply { putString(fragmentType, text) }
            TAG = text
        }
    }

    override val layoutRes: Int
        get() = R.layout.fragment_input_code_add_device

    private var header = ""
    private var type = ""

    override fun initView(rootView: View) {

    }

    override fun initLogic() {
        arguments?.takeIf { it.containsKey(fragmentType) }?.apply {
            val string = getString(fragmentType)
            addDeviceType = string!!
        }
        if (addDeviceType == "mine" || addDeviceType == "unBindAndBindNew") {
            headerLayout.visibility = View.GONE
            deviceNameLayout.visibility = View.GONE
        }
        inputEdit.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (StringUtil.isNotBlankAndEmpty(s.toString())) {
                    deleteEdit.visibility = View.VISIBLE
                } else deleteEdit.visibility = View.GONE
            }

        })
        deviceNameEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.length != 0) {
                    deviceNameDelete.visibility = View.VISIBLE
                } else deviceNameDelete.visibility = View.GONE
            }

        })
        headerLayout.setOnClickListener(this)
        deviceNameDelete.setOnClickListener(this)
        deleteEdit.setOnClickListener(this)
        enterTv.setOnClickListener(this)
    }

    override fun openIsLazy(): Boolean {
        return false
    }

    fun refreshCodeShow(code: String) {
        inputEdit.setText(code)
    }

    override fun onNoDoubleClick(v: View) {
        when (v) {
            deleteEdit -> {
                inputEdit.setText("")
            }
            enterTv -> {
                if (StringUtil.isEmpty(inputEdit.text.toString())) {
                    showToast(mActivity.resources.getString(R.string.app_device_code_empty))
                    return
                }
                val tempCode = "863659040064551"
                if (!BuildConfig.DEBUG && inputEdit.text?.length != tempCode.length) {
                    ToastUtil.showCenter(mActivity,
                            mActivity.resources.getString(R.string.app_incorrect_format))
                    return
                }
                val deviceCode = inputEdit.text.toString()
                //添加家人时需要昵称和头像，自己绑定设备不需要
                if (!(addDeviceType == "mine" || addDeviceType == "unBindAndBindNew")) {
                    if (StringUtil.isEmpty(deviceNameEdit.text.toString())) {
                        showToast(mActivity.resources.getString(R.string.app_device_nickName_empty))
                        return
                    }
                    if (StringUtil.isEmpty(header)) {
                        showToast(mActivity.resources.getString(R.string.app_complete_header_empty))
                        return
                    }
                    val nickName = deviceNameEdit.text.toString()
                    if (header.startsWith("http")) {
                        (mActivity as AddNewDeviceActivity).bindNewDeviceWithAccount(
                                deviceCode, nickName, header)
                    } else {
                        uploadHeader(deviceCode, nickName)
                    }
                } else (mActivity as AddNewDeviceActivity).bindNewDeviceWithAccount(
                        deviceCode, "", header)
            }
            headerLayout -> {
                headerDialogShow()
            }
        }
    }

    private fun uploadHeader(deviceCode: String, nickName: String) {
        // 上传头像
        mActivity.showDialog()
        lifecycleScope.launch {
            try {
                val resultBean = GuiderApiUtil.getApiService().uploadFile(
                        GuiderApiUtil.uploadFile(header))
                if (resultBean != null) {
                    Log.e("上传头像", "成功")
                    header = resultBean
                    (mActivity as AddNewDeviceActivity).bindNewDeviceWithAccount(
                            deviceCode, nickName, header, false)
                }
            } catch (e: Exception) {
                mActivity.dismissDialog()
                Log.e("上传头像", "失败")
                showToast(e.message!!)
            }
        }
    }

    private fun headerDialogShow() {
        val dialog = object : DialogHolder(mActivity,
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
        PermissionUtils.requestPermissionFragment(this, perms, "照相机权限", {
            doThings()
        }, {
            ToastUtil.show(mActivity, mActivity.resources.getString(
                    R.string.app_request_permission_camera))
        })
    }

    private fun doThings() {
        if (type == "takePhoto") takePhoto()
        else selectPhoto()
    }

    private fun selectPhoto() {
        PhotoCuttingUtil.selectPhotoZoom2(mActivity, IMAGE_CUT_CODE)
    }

    private fun takePhoto() {
        PhotoCuttingUtil.takePhotoZoom2(mActivity, IMAGE_CUT_CODE)
    }

    fun selectPicCallBack(data: Intent?) {
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
            if (StringUtil.isNotBlankAndEmpty(header)) {
                ImageLoaderUtils.loadImage(mActivity, headerIv,
                        if (PictureMimeType.isContent(path) &&
                                !media.isCut && !media.isCompressed)
                            Uri.parse(path) else path)
            }

        }
    }
}