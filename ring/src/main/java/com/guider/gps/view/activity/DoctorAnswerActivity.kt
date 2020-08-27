package com.guider.gps.view.activity

import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.guider.baselib.base.BaseActivity
import com.guider.baselib.utils.ScreenUtils
import com.guider.gps.R
import com.guider.gps.adapter.AnswerListAdapter
import kotlinx.android.synthetic.main.activity_doctor_answer.*

/**
 * 医生咨询页面
 */
class DoctorAnswerActivity : BaseActivity(), ViewTreeObserver.OnGlobalLayoutListener {

    override val contentViewResId: Int
        get() = R.layout.activity_doctor_answer

    private var isOpenMoreTag = false
    private lateinit var msgAdapter: AnswerListAdapter

    override fun openEventBus(): Boolean {
        return false
    }

//    override fun beforeContentViewSet() {
//        super.beforeContentViewSet()
//        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
//    }

    override fun initImmersion() {
        setTitle(resources.getString(R.string.app_main_medicine_doctors_consultation))
        showBackButton(R.drawable.ic_back_white)
    }

    override fun initView() {
        setListenerToRootView()
        msgListRv.layoutManager = LinearLayoutManager(this)
    }

    override fun initLogic() {
        more.setOnClickListener(this)
        msgAdapter = AnswerListAdapter(mContext!!, arrayListOf("1"))
        msgListRv.adapter = msgAdapter
    }


    override fun onNoDoubleClick(v: View) {
        when (v) {
            more -> {
                if (!isOpenMoreTag) {
                    isOpenMoreTag = true
                    hideKeyboard(more)
                    moreLayout.visibility = View.VISIBLE
                } else {
                    isOpenMoreTag = false
                    showKeyboard(editInput)
                    moreLayout.visibility = View.GONE
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
            val imm: InputMethodManager = view.context
                    .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            view.requestFocus()
            imm.showSoftInput(view, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun hideKeyboard(view: View) {
        try {
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