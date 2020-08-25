package com.guider.gps.view.activity

import android.app.Activity
import android.content.Intent
import android.text.InputType
import android.view.View
import android.widget.EditText
import com.guider.baselib.base.BaseActivity
import com.guider.baselib.utils.StringUtils
import com.guider.gps.R
import kotlinx.android.synthetic.main.activity_single_edit.*

/**
 * 公共的单行编辑输入框的页面
 */
class SingleLineEditActivity : BaseActivity() {

    private var type = ""

    override val contentViewResId: Int
        get() = R.layout.activity_single_edit

    override fun openEventBus(): Boolean {
        return false
    }

    override fun initImmersion() {
        if (intent != null) {
            if (StringUtils.isNotBlankAndEmpty(intent.getStringExtra("type"))) {
                type = intent.getStringExtra("type")!!
                setTitle(type)
            }
        }
        showBackButton(R.drawable.ic_back_white)
        setRightImage(R.drawable.icon_top_right_confirm, this)
    }

    override fun initView() {
        when (type) {
            resources.getString(R.string.app_main_mine_sport_target) -> {
                editTitle.text = resources.getString(R.string.app_step_number)
                editInput.hint = resources.getString(R.string.app_step_input_hint)
                editInput.inputType = InputType.TYPE_CLASS_NUMBER
            }
            resources.getString(R.string.app_phone_number) -> {
                editTitle.text = resources.getString(R.string.app_phone_number)
                editInput.hint = resources.getString(R.string.app_phone_input_hint)
                editInput.inputType = InputType.TYPE_CLASS_PHONE
            }
        }
    }

    override fun initLogic() {

    }

    override fun onNoDoubleClick(v: View) {
        when (v) {
            iv_toolbar_right -> {
                val intent = Intent()
                intent.putExtra("inputResult", editInput.text.toString())
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    override fun showToolBar(): Boolean {
        return true
    }
}