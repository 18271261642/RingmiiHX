package com.guider.baselib.utils

import android.content.Context
import android.widget.Toast

/**
 * ClassName: ext
 * Description TODO 扩展函数
 */

fun Context.toastShort(msg: String ) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Context.toastLong(msg: String) {
    Toast.makeText(this,msg,Toast.LENGTH_LONG).show()
}

fun Context.isNotBlankAndEmpty(str:String?):Boolean{
    return str != null && str != "null" && str.isNotEmpty()
}

fun String.trimEmpty(str:String?):String?{
    return str?.replace(" ","")
}