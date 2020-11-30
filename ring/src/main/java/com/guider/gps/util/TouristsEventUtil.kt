package com.guider.gps.util

import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.guider.baselib.base.BaseActivity
import com.guider.baselib.widget.dialog.DialogHolder
import com.guider.gps.R

/**
 * @Package: com.guider.gps.util
 * @ClassName: TouristsEventUtil
 * @Description: 游客登录相关事件工具类
 * @Author: hjr
 * @CreateDate: 2020/11/30 15:40
 * Copyright (C), 1998-2020, GuiderTechnology
 */
object TouristsEventUtil {

    fun generateTouristsDialog(activity: BaseActivity,
                               viewId: Int, onConfirm: () -> Unit): DialogHolder {
        return object : DialogHolder(activity,
                viewId, Gravity.CENTER) {
            override fun bindView(dialogView: View) {
                val cancelTv = dialogView.findViewById<TextView>(R.id.cancelLayout)
                cancelTv.setOnClickListener {
                    dialog?.dismiss()
                    dialog = null
                }
                val registerTv = dialogView.findViewById<TextView>(R.id.confirmLayout)
                registerTv.setOnClickListener {
                    dialog?.dismiss()
                    dialog = null
                    onConfirm.invoke()
                }
            }
        }
    }
}