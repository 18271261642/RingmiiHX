package com.guider.baselib.utils

import android.view.View
import java.util.*

/**
 * Description 防多次点击
 * Author HJR36
 */
interface OnNoDoubleClickListener : View.OnClickListener {

    override fun onClick(v: View) {
        val currentTime = Calendar.getInstance().timeInMillis
        val mId = v.id
        if (id != mId) {
            id = mId
            lastClickTime = currentTime
            onNoDoubleClick(v)
            return
        }
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime
            onNoDoubleClick(v)
        }
    }

    fun onNoDoubleClick(v: View)
}