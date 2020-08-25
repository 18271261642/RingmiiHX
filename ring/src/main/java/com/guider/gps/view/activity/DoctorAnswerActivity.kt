package com.guider.gps.view.activity

import android.content.Context
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.guider.baselib.base.BaseActivity
import com.guider.gps.R
import com.guider.gps.adapter.AnswerListAdapter
import kotlinx.android.synthetic.main.activity_doctor_answer.*

/**
 * 医生咨询页面
 */
class DoctorAnswerActivity : BaseActivity() {

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

    override fun showToolBar(): Boolean {
        return true
    }
}