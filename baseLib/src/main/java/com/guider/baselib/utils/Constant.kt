package com.guider.baselib.utils

var MMKV_ROOT = ""
//两次点击重复时间，300ms为宜
const val MIN_CLICK_DELAY_TIME = 300
var lastClickTime: Long = 0
var id: Int = -1