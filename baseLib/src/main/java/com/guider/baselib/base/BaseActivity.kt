package com.guider.baselib.base

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewStub
import android.widget.*
import com.guider.baselib.R
import com.guider.baselib.utils.OnNoDoubleClickListener
import com.guider.baselib.utils.AppManager
import com.guider.baselib.utils.CommonUtils
import com.guider.baselib.utils.EventBusUtils
import com.guider.baselib.widget.dialog.DialogProgress
import com.gyf.immersionbar.ImmersionBar
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity
import me.jessyan.autosize.utils.ScreenUtils

abstract class BaseActivity : RxAppCompatActivity(), OnNoDoubleClickListener {

    /**
     * 在使用自定义toolbar时候的根布局 =toolBarView+childView
     */
    var baseView: View? = null

    /**
     * 获取contentView 资源id
     */
    abstract val contentViewResId: Int
    var mImmersionBar: ImmersionBar? = null

    //获取自定义toolbarview 资源id 默认为-1，showToolBar()方法必须返回true才有效
    val toolBarResId: Int = R.layout.layout_common_toolbar
    var toolbar_line: View? = null
    var mToolbar: Toolbar? = null
    var mContext: Context? = null
    private var mLoadingDialog: DialogProgress? = null
    var commonToolBarId: LinearLayout? = null
    var tv_title: TextView? = null
    var iv_left: ImageView? = null
    var tv_leftTitle: TextView? = null
    var iv_toolbar_right: ImageView? = null
    var rightRedPoint: View? = null
    var iv_toolbar_right2: ImageView? = null

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppManager.single_instance.addActivity(this)
        if (openEventBus()) {
            EventBusUtils.register(this)
        }
        beforeContentViewSet()
        setContentView(contentViewResId)
        if (mImmersionBar == null) {
            mImmersionBar = ImmersionBar.with(this)
        }
        if (showToolBar()) {
            mImmersionBar!!.titleBar(R.id.toolbar).init()
            mToolbar = findViewById<View>(R.id.toolbar) as Toolbar
            toolbar_line = findViewById(R.id.toolbar_line)
            if (null != mToolbar) {
                setSupportActionBar(mToolbar)
                supportActionBar!!.setDisplayShowTitleEnabled(false)
                initTitle()
            }
        }
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        mContext = this
        initImmersion()
        initView()
        initLogic()
    }

    open fun beforeContentViewSet() {}

    abstract fun openEventBus(): Boolean

    //状态栏白底黑字
    fun whiteStatusBarBlackFont() {
        //原理：如果当前设备支持状态栏字体变色，会设置状态栏字体为黑色，
        // 如果当前设备不支持状态栏字体变色，会使当前状态栏加上透明度，否则不执行透明度
        mImmersionBar!!.statusBarDarkFont(true, 0.2f).init()
    }

    /**
     * 给状态设置颜色
     * @param resId 颜色的id
     */
    fun setStatusBarColor(resId: Int) {
        mImmersionBar?.statusBarColor(resId)?.init()
    }

    //设置toolbar颜色
    private fun setToolBarColor(color: Int) {
        if (null != mToolbar) {
            mToolbar!!.setBackgroundColor(color)
        }
    }

    /**
     * @Description: 显示进度条
     */
    fun showDialog() {
        mLoadingDialog = DialogProgress(mContext, null)
        mLoadingDialog?.showDialog()
    }


    /**
     * @Description: 关闭进度条
     */
    fun dismissDialog() {
        if (mLoadingDialog == null) return
        mLoadingDialog?.hideDialog()
    }

    /**
     * 初始化沉浸式
     */
    abstract fun initImmersion()

    /**
     * 初始化view
     */
    abstract fun initView()

    /**
     * 初始化逻辑
     */
    protected abstract fun initLogic()

    private fun initTitle() {
        if (mToolbar == null) {
            return
        }
        commonToolBarId = findViewById(R.id.commonToolBarId)
        iv_left = mToolbar!!.findViewById(R.id.iv_left)
        tv_leftTitle = mToolbar!!.findViewById(R.id.toolbar_title_left)
        tv_title = mToolbar!!.findViewById(R.id.toolbar_title)
        iv_toolbar_right = mToolbar!!.findViewById(R.id.iv_toolbar_right)
        iv_toolbar_right2 = mToolbar!!.findViewById(R.id.iv_toolbar_right2)
        rightRedPoint = mToolbar!!.findViewById(R.id.rightRedPoint)
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        baseView = LayoutInflater.from(this).inflate(R.layout.ac_base, null, false)
        if (baseView != null) {
            if (showToolBar() && toolBarResId != -1) {
                //如果需要显示自定义toolbar,并且资源id存在的情况下，实例化baseView;
                val mVs_toolbar = baseView!!.findViewById<View>(R.id.vs_toolbar) as ViewStub//toolbar容器
                mVs_toolbar.layoutResource = toolBarResId//toolbar资源id
                mVs_toolbar.inflate()//填充toolbar
            }
            val fl_container = baseView!!.findViewById<View>(R.id.fl_container) as FrameLayout
            setContentView(baseView)
            LayoutInflater.from(this).inflate(layoutResID, fl_container, true)//子布局
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        // 获取到前台进程的Activity
        var mForegroundActivity: Activity? = null

        //获取前台activity
        fun getForegroundActivity(): Activity {
            return mForegroundActivity!!
        }
    }

    override fun onResume() {
        super.onResume()
        mForegroundActivity = this
    }

    fun setStatusBarBackground(resId: Int) {
        mToolbar?.setBackgroundResource(resId)
    }

    //设置标题
    open fun setTitle(string: String) {
        if (null != tv_title) {
            tv_title!!.text = string
            tv_title!!.visibility = View.VISIBLE
        }
    }

    fun setShowRightPoint(isShow: Boolean) {
        if (isShow) {
            rightRedPoint?.visibility = View.VISIBLE
        } else rightRedPoint?.visibility = View.GONE
    }

    //设置标题
    fun setTitle(string: String, color: Int) {
        if (null != tv_title) {
            tv_title!!.text = string
            tv_title!!.setTextColor(color)
            tv_title!!.visibility = View.VISIBLE
        }
    }

    //设置左标题
    fun setLeftTitle(string: String, color: Int = R.color.color999999) {
        if (null != tv_leftTitle) {
            tv_leftTitle!!.also {
                it.text = string
                it.setTextColor(CommonUtils.getColor(this, color))
                it.visibility = View.VISIBLE
                it.setOnClickListener {
                    finish()
                }
            }
        }
    }

    //设置左标题
    fun setLeftTitle(string: String, listener: View.OnClickListener) {
        if (null != tv_leftTitle) {
            tv_leftTitle!!.text = string
            tv_leftTitle!!.visibility = View.VISIBLE
            tv_leftTitle!!.setOnClickListener(listener)
        }
    }

    //设置右边顶部图标
    fun setRightImage(resId: Int, listener: View.OnClickListener) {
        if (null != iv_toolbar_right) {
            iv_toolbar_right!!.setImageResource(resId)
            iv_toolbar_right!!.visibility = View.VISIBLE
            iv_toolbar_right!!.setOnClickListener(listener)
        }
    }

    //设置右边顶部图标
    fun setRightImage2(resId: Int, listener: View.OnClickListener) {
        if (null != iv_toolbar_right2) {
            iv_toolbar_right2!!.setImageResource(resId)
            iv_toolbar_right2!!.visibility = View.VISIBLE
            iv_toolbar_right2!!.setOnClickListener(listener)
        }
    }

    //显示返回按钮
    fun showBackButton(@DrawableRes res: Int) {
        iv_left?.let {
            it.visibility = View.VISIBLE
            it.setImageResource(res)
            it.setOnClickListener { finish() }
        }
    }

    fun showBackButtonNoClick(@DrawableRes res: Int) {
        iv_left?.let {
            it.visibility = View.VISIBLE
            it.setImageResource(res)
            it.setOnClickListener(null)
        }
    }


    //显示返回按钮
    fun showBackButton(@DrawableRes res: Int, listener: View.OnClickListener?) {
        iv_left?.let {
            it.visibility = View.VISIBLE
            it.setImageResource(res)
            if (listener != null) {
                it.setOnClickListener(listener)
            } else {
                it.setOnClickListener { finish() }
            }
        }
    }


    fun hideBackButton() {
        if (iv_left != null) {
            iv_left?.visibility = View.GONE
        }
    }

    override fun onNoDoubleClick(v: View) {}

    /**
     * 是否显示通用toolBar
     */
    abstract fun showToolBar(): Boolean

    override fun onDestroy() {
        super.onDestroy()
        baseView = null
        if (mLoadingDialog != null) {
            mLoadingDialog?.hideDialog()
            mLoadingDialog = null
        }
        if (openEventBus()) {
            EventBusUtils.unregister(this)
        }
        AppManager.single_instance.finishActivity(this)
    }
}