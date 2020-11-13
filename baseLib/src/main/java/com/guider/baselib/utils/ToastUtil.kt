package com.guider.feifeia3.utils

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import com.guider.baselib.R

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
    fun showCenter(context: Context, text: CharSequence) {
        if (toast == null) {
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT)
        }
        toast!!.setGravity(Gravity.CENTER, 0, 0)
        toast!!.setText(text)
        toast!!.show()
    }

    @SuppressLint("ShowToast")
    fun showCenterLong(context: Context, text: CharSequence) {
        if (toast == null) {
            toast = Toast.makeText(context, text, Toast.LENGTH_LONG)
        }
        toast!!.setGravity(Gravity.CENTER, 0, 0)
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

    fun showCustomToast(view: View?, context: Context, isShort: Boolean, hintText: String?,
                        viewBackground: Int? = null,
                        gravity: Int = Gravity.CENTER, yOff: Int = 0) {
        val toast = Toast(context)
        var contentView = view
        if (contentView == null) {
            contentView = View.inflate(context, R.layout.toast_layout, null)
            if (viewBackground != null) {
                contentView!!.findViewById<LinearLayout>(R.id.rootLayout)
                        .setBackgroundResource(viewBackground)
            }
            contentView!!.findViewById<TextView>(R.id.hintText).text = hintText
        }
        toast.setGravity(gravity, 0, yOff)
        if (isShort) toast.duration = Toast.LENGTH_SHORT
        else toast.duration = Toast.LENGTH_LONG
        toast.view = contentView
        toast.show()
    }
}
