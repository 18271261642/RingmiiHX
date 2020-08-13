package com.guider.feifeia3.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog
import android.view.View
import android.view.WindowManager
import com.guider.baselib.R

/**
 * Description TODO
 * Author HJR36
 * Date 2018/6/20 17:48
 */
object BottomDialogUtil {

    fun showBottomDialog(mContext: Context, view: View, gravity: Int, outSide: Boolean = true,
                         alpha: Float = 0.6f): AlertDialog {
        val dialog: AlertDialog = AlertDialog.Builder(mContext, R.style.my_dialog).create()
        val window = dialog.window!!
        window.setGravity(gravity)
        window.setWindowAnimations(R.style.AnimBottom)
        dialog.setCanceledOnTouchOutside(outSide)
        dialog.show()
        window.setContentView(view)
        val params: WindowManager.LayoutParams = window.attributes
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = WindowManager.LayoutParams.WRAP_CONTENT
        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND//就是这个属性导致window后所有的东西都成暗淡
        params.dimAmount = alpha//设置对话框的透明程度背景(非布局的透明度)
        window.attributes = params
        return dialog
    }

    fun showBottomDialog2(mContext: Context, view: View, gravity: Int): AlertDialog {//父布局是全屏的
        val dialog: AlertDialog = AlertDialog.Builder(mContext, R.style.my_dialog).create()
        val window = dialog.window!!
        window.setGravity(gravity)
        window.setWindowAnimations(R.style.AnimBottom)
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()
        window.setContentView(view)
        val params: WindowManager.LayoutParams = window.attributes
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = WindowManager.LayoutParams.MATCH_PARENT
        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND//就是这个属性导致window后所有的东西都成暗淡
        params.dimAmount = 0.5f//设置对话框的透明程度背景(非布局的透明度)
        window.attributes = params
        return dialog
    }
}