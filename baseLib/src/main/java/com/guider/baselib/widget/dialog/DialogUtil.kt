package com.guider.baselib.widget.dialog

import android.content.DialogInterface
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.guider.baselib.R

/**
 * @className: DialogUtil
 * @desc: 分类弹窗工具类
 */
open class CenterDialogHelper(activity: AppCompatActivity,
                              layoutId: Int = R.layout.dialog_apr_base,
                              protected val onConfirm: () -> Unit,
                              protected val onCancel: () -> Unit)
    : DialogHolder(activity, layoutId) {

    lateinit var title: TextView
    lateinit var content: TextView
    lateinit var hint: TextView
    lateinit var cancel: TextView
    lateinit var confirm: TextView
    lateinit var lineH: View
    lateinit var lineV: View

    override fun bindView(dialogView: View) {
        title = dialogView.findViewById(R.id.tv_title)
        content = dialogView.findViewById(R.id.tv_content)
        hint = dialogView.findViewById(R.id.tv_hint)
        cancel = dialogView.findViewById(R.id.cancel)
        confirm = dialogView.findViewById(R.id.confirm)
        lineH = dialogView.findViewById(R.id.line_ho)
        lineV = dialogView.findViewById(R.id.line_v)

        cancel.setOnClickListener {
            dialog?.dismiss()
            onCancel.invoke()
        }
        confirm.setOnClickListener {
            dialog?.dismiss()
            onConfirm.invoke()
        }
    }

    fun onConfig(config: DialogConfig) {
        checkConfig(config.title, title, config.useDefault)
        checkConfig(config.content, content, config.useDefault)
        checkConfig(config.hint, hint, config.useDefault)
        checkConfig(config.cancel, cancel, config.useDefault)
        checkConfig(config.confirm, confirm, config.useDefault)
        lineH.visibility = if (config.showHLine) View.VISIBLE else View.GONE
        lineV.visibility = if (config.showLine) View.VISIBLE else View.GONE
    }

    private fun checkConfig(value: String?, target: TextView, useDefault: Boolean) {
        if (!useDefault) {
            if (value != null && value.isNotBlank()) {
                target.text = value
                target.visibility = View.VISIBLE
            } else {
                target.visibility = View.GONE
            }
        }
    }
}


class BaseCenterDialogHelper(activity: AppCompatActivity,
                             layoutId: Int = R.layout.dialog_apr_base,
                             private val onBindView: (view: View) -> Unit,
                             private val onConfirm: () -> Unit,
                             private val onCancel: () -> Unit)
    : DialogHolder(activity, layoutId) {
    lateinit var cancel: TextView
    lateinit var confirm: TextView

    override fun bindView(dialogView: View) {
        onBindView.invoke(dialogView)
        cancel = dialogView.findViewById(R.id.cancel)
        confirm = dialogView.findViewById(R.id.confirm)
        cancel.setOnClickListener {
            dialog?.dismiss()
            onCancel.invoke()
        }
        confirm.setOnClickListener {
            dialog?.dismiss()
            onConfirm.invoke()
        }
    }
}

class BottomDialogHelper(activity: AppCompatActivity,
                         layoutId: Int,
                         private val onBindView: (view: View) -> Unit,
                         private val onConfirm: () -> Unit,
                         private val onCancel: () -> Unit) :
        DialogHolder(activity, layoutId, Gravity.BOTTOM) {
    override fun bindView(dialogView: View) {
        onBindView.invoke(dialogView)
        dialogView.findViewById<View>(R.id.confirm)?.setOnClickListener {
            dialog?.dismiss()
            onConfirm.invoke()
        }
        dialogView.findViewById<View>(R.id.cancel)?.setOnClickListener {
            dialog?.dismiss()
            onCancel.invoke()
        }
    }
}

class DialogConfig(val title: String? = null,
                   val content: String? = null,
                   val hint: String? = null,
                   val cancel: String? = null,
                   val confirm: String? = null,
                   val showLine: Boolean = true,
                   val showHLine: Boolean = true,
                   val useDefault: Boolean = true)

abstract class DialogHolder(protected val activity: AppCompatActivity,
                            private val layoutId: Int,
                            var gravity: Int = Gravity.CENTER, val tag: String = "dialog") {

    var dialog: DialogFragment? = null
    lateinit var dialogView: View

    fun initView() {
        dialogView = View.inflate(activity, layoutId, null)
        bindView(dialogView)
    }

    abstract fun bindView(dialogView: View)

    fun show(outSizeEnable: Boolean = true,
             onDismissListener: DialogInterface.OnDismissListener? = null) {
//        dialog = BottomDialogUtil.showBottomDialog(context, dialogView, gravity)
        //预防activity消失弹出dialog
        if (activity.isFinishing) return
        dialog = MyDialogNew.showDialog(activity, view = dialogView,
                gravity = gravity, outSizeEnable = outSizeEnable,
                onDismissListener = onDismissListener, tag = tag)
//        return dialog!!
    }

    fun closeDialog() {
        dialog?.dismissAllowingStateLoss()
    }
}