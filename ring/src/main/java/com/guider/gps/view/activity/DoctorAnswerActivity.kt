package com.guider.gps.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.guider.baselib.base.BaseActivity
import com.guider.baselib.utils.*
import com.guider.baselib.widget.dialog.DialogProgress
import com.guider.feifeia3.utils.ToastUtil
import com.guider.gps.R
import com.guider.gps.adapter.AnswerListAdapter
import com.guider.health.apilib.ApiCallBack
import com.guider.health.apilib.ApiUtil
import com.guider.health.apilib.IGuiderApi
import com.guider.health.apilib.bean.AnswerListBean
import com.guider.health.apilib.bean.SendAnswerListBean
import com.guider.health.apilib.enums.AnswerMsgType
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.scwang.smart.refresh.header.ClassicsHeader
import kotlinx.android.synthetic.main.activity_doctor_answer.*
import kotlinx.android.synthetic.main.activity_doctor_answer.msgListRv
import kotlinx.android.synthetic.main.fragment_ring_msg_list.*
import retrofit2.Call
import retrofit2.Response

/**
 * 医生咨询页面
 */
class DoctorAnswerActivity : BaseActivity(), ViewTreeObserver.OnGlobalLayoutListener {

    override val contentViewResId: Int
        get() = R.layout.activity_doctor_answer

    companion object {
        init {
            ClassicsHeader.REFRESH_HEADER_PULLING = "查看更多历史消息"
            ClassicsHeader.REFRESH_HEADER_REFRESHING = "正在加载..."
            ClassicsHeader.REFRESH_HEADER_LOADING = ""
            ClassicsHeader.REFRESH_HEADER_RELEASE = ""
            ClassicsHeader.REFRESH_HEADER_FINISH = ""
            ClassicsHeader.REFRESH_HEADER_FAILED = "没有更多历史消息"
            ClassicsHeader.REFRESH_HEADER_SECONDARY = ""
            ClassicsHeader.REFRESH_HEADER_UPDATE = ""
        }
    }

    private var isOpenMoreTag = false
    private lateinit var msgAdapter: AnswerListAdapter
    private var type = ""
    private var selectPicList = arrayListOf<String>()
    private var searchTime = ""
    private var msgList = arrayListOf<AnswerListBean>()

    //true：上一页(刷新)，false：下一页(加载更多)
    private var page = false
    private var isLoadMore = false

    override fun openEventBus(): Boolean {
        return false
    }

    override fun initImmersion() {
        setTitle(resources.getString(R.string.app_main_medicine_doctors_consultation))
        showBackButton(R.drawable.ic_back_white)
    }

    override fun initView() {
        setListenerToRootView()
        msgListRv.layoutManager = LinearLayoutManager(this)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initLogic() {
        more.setOnClickListener(this)
        msgAdapter = AnswerListAdapter(mContext!!, msgList)
        msgListRv.adapter = msgAdapter
        photoLayout.setOnClickListener(this)
        editInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                if (isOpenMoreTag) hideMoreLayout()
                if (msgList.size > 1)
                    msgListRv.postDelayed({
                        msgListRv.scrollToPosition(msgAdapter.itemCount - 1)
                    }, 300)
            }
        }
        editInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null && s.toString().trim().isNotEmpty() && s.length <= 300) {
                    send.visibility = View.VISIBLE
                    more.visibility = View.INVISIBLE
                } else {
                    send.visibility = View.INVISIBLE
                    more.visibility = View.VISIBLE
                }
                if (s != null && s.length >= 300) {
                    ToastUtil.show(mContext!!,
                            mContext!!.resources.getString(R.string.app_edit_text_limit_300))
                }
            }

        })
        searchTime = DateUtilKotlin.localToUTC(
                CommonUtils.getCurrentDate(DEFAULT_TIME_FORMAT_PATTERN))!!
        refresh_immsg.setOnRefreshListener {
            isLoadMore = true
            page = false
            getAnswerListData()
        }
        msgListRv.setOnTouchListener { _, _ ->
            hideKeyboard(more)
            false
        }
        refresh_immsg.setEnableAutoLoadMore(false)
        getAnswerListData()
        send.setOnClickListener(this)
    }

    private fun getAnswerListData() {
        if (!isLoadMore)
            showDialog()
        ApiUtil.createApi(IGuiderApi::class.java, false)
                .getAnswerMsgList(1, MMKVUtil.getInt(USER.USERID), page,
                        20, searchTime)
                .enqueue(object : ApiCallBack<Map<String, List<AnswerListBean>>>(mContext) {
                    override fun onApiResponse(call: Call<Map<String, List<AnswerListBean>>>?,
                                               response: Response<Map<String,
                                                       List<AnswerListBean>>>?) {
                        if (response?.body() != null) {
                            val body = response.body()!!
                            if (body.isNullOrEmpty()) {
                                loadDataEmpty()
                                return
                            }
                            if (isLoadMore) refresh_immsg.finishRefresh()
                            val tempList = arrayListOf<AnswerListBean>()
                            for (key in body.keys) {
                                //遍历取出key，再遍历map取出value。
                                body[key]?.let { tempList.addAll(it) }
                            }
                            searchTime = tempList[0].createTime
                            if (isLoadMore) {
                                msgList.addAll(0, tempList)
                            } else {
                                msgList = tempList
                            }
                            msgAdapter.setSourceList(msgList)
                            if (!isLoadMore) {
                                msgListRv.scrollToPosition(msgAdapter.itemCount - 1)
                            }
                        } else {
                            loadDataEmpty()
                        }
                    }

                    override fun onFailure(call: Call<Map<String, List<AnswerListBean>>>,
                                           t: Throwable) {
                        if (isLoadMore) refreshLayout.finishRefresh()
                    }

                    override fun onRequestFinish() {
                        if (!isLoadMore) dismissDialog()
                        isLoadMore = false
                    }
                })
    }

    private fun loadDataEmpty() {
        if (isLoadMore) {
            refresh_immsg.finishRefresh()
        }
        if (!isLoadMore) {
            msgList.clear()
            msgAdapter.setSourceList(msgList)
        }
    }

    override fun onNoDoubleClick(v: View) {
        when (v) {
            more -> {
                if (!isOpenMoreTag) {
                    hideKeyboard(more)
                    moreLayout.visibility = View.VISIBLE
                    isOpenMoreTag = true
                } else {
                    showKeyboard(editInput)
                    moreLayout.visibility = View.GONE
                    isOpenMoreTag = false
                }
                if (msgList.size > 1)
                    msgListRv.postDelayed({
                        msgListRv.scrollToPosition(msgAdapter.itemCount - 1)
                    }, 300)
            }
            cameraLayout -> {
                type = "takePhoto"
                requestPhonePermission()
            }
            photoLayout -> {
                type = "selectPic"
                requestPhonePermission()
            }
            send -> {
                v.isFocusable = true
                v.requestFocusFromTouch()
                //发送消息
                val content = editInput.text.toString()
                sendAnswerMsg(AnswerMsgType.STRING, content)
            }
        }
    }

    private fun uploadPic(pic: String, onSuccess: (url: String) -> Unit, onFail: () -> Unit) {
        showDialog()
        ApiUtil.uploadFile(null, pic, object : ApiCallBack<String>(mContext) {
            override fun onApiResponse(call: Call<String>?, response: Response<String>?) {
                Log.e("上传单张图片", "成功")
                if (response?.body() != null) {
                    onSuccess.invoke(response.body()!!)
                }

            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("上传单张图片", "失败")
                onFail.invoke()
            }

            override fun onRequestFinish() {
                super.onRequestFinish()
                dismissDialog()
            }
        })
    }

    private fun sendAnswerMsg(type: AnswerMsgType, content: String) {
        val mDialog = DialogProgress(mContext!!, null)
        mDialog.showDialog()
        val map = hashMapOf<String, Any>()
        map["content"] = content
        map["fromAccount"] = MMKVUtil.getInt(USER.USERID)
        map["toAccount"] = 1
        map["type"] = type
        ApiUtil.createApi(IGuiderApi::class.java, false)
                .sendAnswerMsg(map)
                .enqueue(object : ApiCallBack<SendAnswerListBean>(mContext) {
                    override fun onApiResponse(call: Call<SendAnswerListBean>?,
                                               response: Response<SendAnswerListBean>?) {
                        if (response?.body() != null) {
                            toastShort(mContext!!.resources.getString(R.string.app_send_success))
                            val body = response.body()!!
                            val bean = AnswerListBean(body.accountId, body.content,
                                    body.id, body.createTime, if (body.chatContentType == "STRING") {
                                AnswerMsgType.STRING
                            } else AnswerMsgType.IMAGE
                            )
                            msgList.add(bean)
                            if (type == AnswerMsgType.STRING) {
                                editInput.setText("")
                            }
                            msgAdapter.setSourceList(msgList)
                            msgListRv.scrollToPosition(msgAdapter.itemCount - 1)
                        }
                    }

                    override fun onRequestFinish() {
                        mDialog.hideDialog()
                    }
                })
    }

    @SuppressLint("CheckResult")
    private fun requestPhonePermission() {
        val perms = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
        PermissionUtils.requestPermissionActivity(this, perms, "照相机权限", {
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
        PhotoCuttingUtil.selectPhotoZoom(this, IMAGE_CUT_CODE)
    }

    private fun takePhoto() {
        PhotoCuttingUtil.takePhotoZoom(this, IMAGE_CUT_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                IMAGE_CUT_CODE -> {
                    if (isOpenMoreTag) {
                        moreLayout.visibility = View.GONE
                        isOpenMoreTag = false
                    } else {
                        hideKeyboard(more)
                    }
                    // 图片选择结果回调
                    val selectList = PictureSelector.obtainMultipleResult(data)
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，已取压缩路径为准，因为是先裁剪后压缩的
                    if (!selectList.isNullOrEmpty()) {
                        selectPicList.clear()
                        selectList.forEach {
                            val media: LocalMedia = it
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
                                selectPicList.add(path)
                            }
                        }
                        selectPicList.forEach {
                            uploadPic(it, { url ->
                                sendAnswerMsg(AnswerMsgType.IMAGE, url)
                            }, {
                                toastShort(mContext!!.resources.getString(R.string.app_fail_upload))
                            })
                        }
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        if (isOpenMoreTag) {
            moreLayout.visibility = View.GONE
        } else hideKeyboard(more)
        super.onBackPressed()
    }

    private fun showKeyboard(view: View) {
        try {
            view.requestFocus()
            val imm: InputMethodManager = view.context
                    .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun hideMoreLayout() {
        showKeyboard(editInput)
        moreLayout.visibility = View.GONE
        isOpenMoreTag = false
    }

    private fun hideKeyboard(view: View) {
        try {
            editInput.clearFocus()
            val imm: InputMethodManager = view.context
                    .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private var isAnim = false
    private lateinit var rootView: View

    private var isUp = false //键盘弹起后不去二次改变输入框的高度
    override fun onGlobalLayout() {
        val softKeyboardHeight = 100
        val r = Rect()
        rootView.getWindowVisibleDisplayFrame(r)
        val dm = rootView.resources.displayMetrics
        var heightDiff = rootView.bottom - r.bottom
        Log.e("diff", heightDiff.toString())
        val mKeyboardUp = heightDiff > softKeyboardHeight * dm.density
        //判断是否有虚拟键,6.0以上或全面屏无需
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            val point = Point()
            windowManager.defaultDisplay.getSize(point)
            if (getDpi() > point.y) {
                val resourceId = resources.getIdentifier(
                        "navigation_bar_height", "dimen", "android")
                heightDiff -= resources.getDimensionPixelSize(resourceId)
            }
        }

        if (mKeyboardUp) {
            //键盘弹出
            isOpenMoreTag = false
            moreLayout.visibility = View.GONE
            if (!isUp) {
                if (parent_immsg.height - inputLayout.height - msgListRv.height <
                        heightDiff && inputLayout.height + msgListRv.height + 30 >=
                        parent_immsg.height) {
                    parent_immsg.translationY = -heightDiff.toFloat()
                } else {
                    inputLayout.translationY = -heightDiff.toFloat()
                    val slop = parent_immsg.height - inputLayout.height -
                            heightDiff - msgListRv.height - 12
                    if (slop < 0)
                        refresh_immsg.translationY = slop.toFloat()
                }
                isAnim = true
                isUp = true
            }
        } else {
            //键盘收起
            if (isAnim) {
                isUp = false
                inputLayout.translationY = 0f
                parent_immsg.translationY = 0f
                refresh_immsg.translationY = 0f
                isAnim = false
            }
        }
    }

    private fun getDpi(): Int {
        var dpi = 0
        val dm = DisplayMetrics()
        try {
            val c = Class.forName("android.view.Display")
            val method = c.getMethod("getRealMetrics", DisplayMetrics::class.java)
            method.invoke(windowManager.defaultDisplay, dm)
            dpi = dm.heightPixels
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return dpi
    }

    private fun setListenerToRootView() {
        rootView = window.decorView.findViewById(android.R.id.content)
        rootView.viewTreeObserver.addOnGlobalLayoutListener(this)
    }

    override fun showToolBar(): Boolean {
        return true
    }
}