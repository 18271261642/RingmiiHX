package com.guider.baselib.utils

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat

object CommonUtils {
    /**
     * 得到drawable
     */
    fun getDrawable(context: Context, id: Int): Drawable {
        return ContextCompat.getDrawable(context, id)!!
    }

    /**
     * 得到color
     */
    fun getColor(context: Context, id: Int): Int {
        return ContextCompat.getColor(context, id)
    }
}