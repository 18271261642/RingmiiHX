package com.guider.gps.view.activity

import android.app.Activity
import android.content.Intent
import android.text.InputType
import android.view.View
import com.guider.baselib.base.BaseActivity
import com.guider.baselib.utils.StringUtil
import com.guider.baselib.utils.toastShort
import com.guider.gps.R
import kotlinx.android.synthetic.main.activity_single_edit.*

/**
 * 公共的单行编辑输入框的页面
 */
class SingleLineEditActivity : BaseActivity() {

    private var type = ""
    private var inputValue = ""

    override val contentViewResId: Int
        get() = R.layout.activity_single_edit

    override fun openEventBus(): Boolean {
        return false
    }

    override fun initImmersion() {
        if (intent != null) {
            if (StringUtil.isNotBlankAndEmpty(intent.getStringExtra("type"))) {
                type = intent.getStringExtra("type")!!
                setTitle(type)
            }
            if (StringUtil.isNotBlankAndEmpty(intent.getStringExtra("inputValue"))) {
                inputValue = intent.getStringExtra("inputValue")!!
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
            else -> {
                editTitle.text = type
                editInput.hint = resources.getString(R.string.app_please_fill_in) + type
                editInput.inputType = InputType.TYPE_CLASS_TEXT
            }
        }
    }

    override fun initLogic() {
        if (StringUtil.isNotBlankAndEmpty(inputValue)) {
            editInput.setText(inputValue)
        }
    }

    override fun onNoDoubleClick(v: View) {
        when (v) {
            iv_toolbar_right -> {
                when (type) {
                    resources.getString(R.string.app_main_mine_sport_target) -> {
                        if (editInput.text.toString().toInt() <= 0) {
                            toastShort("指定的目标步数要大于0")
                            return
                        }
                    }
                    else -> {
                        if (StringUtil.isEmpty(editInput.text.toString())) {
                            toastShort("输入值不能为空")
                            return
                        }
                    }
                }
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