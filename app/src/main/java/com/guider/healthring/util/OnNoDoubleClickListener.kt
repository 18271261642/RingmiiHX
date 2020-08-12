package com.guider.healthring.util

import android.view.View
import java.util.*

/**
 * Description 防多次点击
 * Author HJR36
 */
interface OnNoDoubleClickListener : View.OnClickListener {

    override fun onClick(v: View) {
        //两次点击重复时间，300ms为宜
        val MIN_CLICK_DELAY_TIME = 300
        var lastClickTime = 0L
        var id: Int = -1
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