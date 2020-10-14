package com.guider.baselib.widget.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction
import com.guider.baselib.R
import java.lang.reflect.Field


/**
 * @className: MyDialogFragment
 * @desc: 弹窗fragment
 */
class BottomDialogFragment(private val gravity: Int = Gravity.CENTER,
                           private val layoutId: Int = R.layout.dialog_apr_base) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // 使用不带Theme的构造器, 获得的dialog边框距离屏幕仍有几毫米的缝隙。
        val dialog = Dialog(activity!!, R.style.BottomDialog)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // 设置Content前设定
        dialog.setContentView(layoutId)
        dialog.setCanceledOnTouchOutside(true) // 外部点击取消

        // 设置宽度为屏宽, 靠近屏幕底部。
        val window = dialog.window

        window?.setWindowAnimations(R.style.AnimBottom)

        val lp = window!!.attributes
        lp.gravity = gravity
        // 宽度持平
        lp.width = WindowManager.LayoutParams.MATCH_PARENT

        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        window.attributes = lp

        return dialog
    }
}

class BottomDialogFragment2(private val gravity: Int = Gravity.CENTER,
                            private val outSizeEnable: Boolean = true,
                            private val listener: DialogInterface.OnDismissListener? = null,
                            private val dialogView: View) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // 使用不带Theme的构造器, 获得的dialog边框距离屏幕仍有几毫米的缝隙。
        val dialog = Dialog(activity!!, R.style.BottomDialog)

        dialog.setOnDismissListener(listener)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // 设置Content前设定
        dialog.setContentView(dialogView)
        dialog.setCanceledOnTouchOutside(outSizeEnable) // 外部点击取消

        // 设置宽度为屏宽, 靠近屏幕底部。
        val window = dialog.window

        window?.setWindowAnimations(R.style.AnimBottom)

        val lp = window!!.attributes
        lp.gravity = gravity
        // 宽度持平
        lp.width = WindowManager.LayoutParams.MATCH_PARENT

        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        window.attributes = lp

        return dialog
    }
}

object MyDialogNew {
    fun showDialog(activity: AppCompatActivity, view: View, gravity: Int,
                   outSizeEnable: Boolean, onDismissListener: DialogInterface.OnDismissListener? = null,
                   tag: String = "center_dialog"): DialogFragment {
        val dialog = BottomDialogFragment2(gravity = gravity, dialogView = view,
                outSizeEnable = outSizeEnable, listener = onDismissListener)
        try {
            val mDismissed: Field = DialogFragment::class.java.getDeclaredField("mDismissed")
            mDismissed.isAccessible = true
            mDismissed.set(dialog, false)

            val mShownByMe: Field = DialogFragment::class.java.getDeclaredField("mShownByMe")
            mShownByMe.isAccessible = true
            mShownByMe.set(dialog, true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val ft: FragmentTransaction = activity.supportFragmentManager.beginTransaction()
        ft.add(dialog, tag)
        ft.commitAllowingStateLoss()
        return dialog
    }

    fun showCenterDialog(activity: AppCompatActivity, view: View,
                         tag: String = "center_dialog2"): DialogFragment {
        val dialog = BottomDialogFragment2(dialogView = view)
        dialog.show(activity.supportFragmentManager, tag)
        return dialog
    }

    fun showBottomDialog(activity: AppCompatActivity, tag: String = "bottom_dialog", id: Int)
            : DialogFragment {
        val dialog = BottomDialogFragment(Gravity.BOTTOM, id)
        dialog.show(activity.supportFragmentManager, tag)
        return dialog
    }
}