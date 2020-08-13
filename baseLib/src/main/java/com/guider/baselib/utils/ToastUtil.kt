package com.guider.feifeia3.utils

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.StringRes
import android.widget.Toast

/**
 * description:
 * autour: lisong
 * date: 2017/8/11 0011 上午 10:34
 * version: v1.0
 */

object ToastUtil {

    private var toast: Toast? = null
    private var longToast: Toast? = null

    @SuppressLint("ShowToast")
    fun show(context: Context, text: CharSequence) {
        if (toast == null) {
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT)
        }
        toast!!.setText(text)
        toast!!.show()
    }

    @SuppressLint("ShowToast")
    fun show(context: Context, @StringRes textRes: Int) {
        if (toast == null) {
            toast = Toast.makeText(context, textRes, Toast.LENGTH_SHORT)
        }
        toast!!.setText(textRes)
        toast!!.show()
    }

    @SuppressLint("ShowToast")
    fun showLong(context: Context, text: CharSequence) {
        if (longToast == null) {
            longToast = Toast.makeText(context, text, Toast.LENGTH_LONG)
        }
        longToast!!.setText(text)
        longToast!!.show()
    }

    @SuppressLint("ShowToast")
    fun showLong(context: Context, @StringRes textRes: Int) {
        if (longToast == null) {
            longToast = Toast.makeText(context, textRes, Toast.LENGTH_LONG)
        }
        longToast!!.setText(textRes)
        longToast!!.show()
    }
}
