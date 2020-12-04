package com.guider.healthring.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import cn.wuweikang.StringUtil
import com.guider.healthring.BuildConfig
import com.guider.healthring.Commont
import com.guider.healthring.R
import com.guider.healthring.base.BaseActivity
import com.guider.healthring.siswatch.utils.WatchUtils
import com.guider.healthring.util.SharedPreferencesUtils
import com.guider.healthring.util.ToastUtil
import com.guider.libbase.util.Log
import java.util.*

/**
 * Created by thinkpad on 2017/3/9.
 * 意见反馈
 */
class FeedbackActivity : BaseActivity() {
    var tvTitle: TextView? = null
    var questionDetailTv: TextView? = null
    var password: EditText? = null
    var contactInfoTv: TextView? = null
    var leavePhoneEt: EditText? = null
    override fun initViews() {
        initViewIds()
        tvTitle!!.text = baseActivity.resources.getString(R.string.feedback)
    }

    private fun initViewIds() {
        tvTitle = findViewById(R.id.tv_title)
        questionDetailTv = findViewById(R.id.question_detail_tv)
        password = findViewById(R.id.password_feed)
        contactInfoTv = findViewById(R.id.contact_info_tv)
        leavePhoneEt = findViewById(R.id.leave_phone_et)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_complete, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val describe_problem = password!!.text.toString()
        val confrimPass = leavePhoneEt!!.text.toString()
        if (!WatchUtils.isEmpty(describe_problem) && !WatchUtils.isEmpty(confrimPass)) {
            submitFeedback(describe_problem, confrimPass)
        } else {
            ToastUtil.showShort(this, getString(R.string.leave_contact))
        }
        return super.onOptionsItemSelected(item)
    }

    private val NORMAL_PHONE = arrayOf("com.android.email",
            "com.android.email.activity.MessageCompose")
    private val MIUI_PHONE = arrayOf("com.android.email",
            "com.kingsoft.mail.compose.ComposeActivity")
    private val SAMSUNG_PHONE = arrayOf("com.samsung.android.email.provider",
            "com.samsung.android.email.composer.activity.MessageCompose")

    @SuppressLint("IntentReset")
    private fun submitFeedback(content: String, contact: String) {
        val appName = when (BuildConfig.HEALTH) {
            0, 1 -> {
                if (BuildConfig.APPLICATION_ID == "com.mcu.healthringx") {
                    "MCU手環"
                } else baseActivity.resources.getString(R.string.the_app_name)
            }
            2 -> {
                "Vigori Mate"
            }
            else -> baseActivity.resources.getString(R.string.the_app_name)
        }
        val emailHint = String.format(
                baseActivity.resources.getString(R.string.app_send_email_hint), appName)
        val contentValue = "${baseActivity.resources.getString(R.string.question_detail)}\n" +
                "${content}\n" +
                "${baseActivity.resources.getString(R.string.contact_info)}\n" +
                "${contact}\n" +
                emailHint
        try {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("Steven@guidertech.com"))
            intent.type = "text/plain"
            val deviceModel = Build.BRAND.trim().toUpperCase(Locale.ROOT)
            if (deviceModel.contains("HONOR") ||
                    deviceModel.contains("HUAWEI")
                    || deviceModel.contains("NUBIA")) {
                intent.setClassName(NORMAL_PHONE[0], NORMAL_PHONE[1])
            } else if (deviceModel.contains("XIAOMI")
                    || deviceModel.contains("Redmi")) {
                intent.setClassName(MIUI_PHONE[0], MIUI_PHONE[1])
            } else if (deviceModel.contains("SAMSUNG")) {
                intent.setClassName(SAMSUNG_PHONE[0], SAMSUNG_PHONE[1])
            } else {
                intent.setClassName(NORMAL_PHONE[0], NORMAL_PHONE[1])
            }
            intent.putExtra(Intent.EXTRA_TEXT, contentValue)
            intent.putExtra(Intent.EXTRA_SUBJECT,
                    baseActivity.resources.getString(R.string.app_user_feedback))
            startActivity(Intent.createChooser(intent, "Select email application."))
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("FeedbackActivity","发送邮件失败"+e.message)
        }
    }

    override fun getContentViewId(): Int {
        return R.layout.activity_feedback
    }

    override fun onPause() {
        super.onPause()
        closeKeyboard()
    }

    /**
     * 关闭软键盘
     */
    fun closeKeyboard() {
        val view = window.peekDecorView()
        if (view != null) {
            val inputmanger = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputmanger.hideSoftInputFromWindow(view.windowToken, 0)
        }
        hideKeyboard()
        if (currentFocus != null) {
            (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(currentFocus!!.windowToken,
                            InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    private fun hideKeyboard() {
        val imm = this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        if (imm.isActive && this.currentFocus != null) {
            if (this.currentFocus!!.windowToken != null) {
                imm.hideSoftInputFromWindow(this.currentFocus!!.windowToken,
                        InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }
    }
}