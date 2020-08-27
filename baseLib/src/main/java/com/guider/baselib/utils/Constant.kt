package com.guider.baselib.utils

var MMKV_ROOT = ""

//两次点击重复时间，300ms为宜
const val MIN_CLICK_DELAY_TIME = 300
var lastClickTime: Long = 0
var id: Int = -1

/**时间格式化模型*/
const val DEFAULT_TIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss"
const val TIME_FORMAT_PATTERN1 = "yyyy-MM-dd HH:mm"
const val TIME_FORMAT_PATTERN2 = "yyyy/MM/dd HH:mm"
const val TIME_FORMAT_PATTERN3 = "HH:mm:ss"
const val TIME_FORMAT_PATTERN4 = "yyyy-MM-dd"
const val TIME_FORMAT_PATTERN5 = "yyyy-MM"

/**
 * app_id是从微信官网申请到的合法APPid
 */
val APP_ID_WX = "wxf55e2c3e1dcd35fc"

/**
 * 微信AppSecret值
 */
val APP_SECRET_WX = "5e94ec8b5939cbad04fae4139b9bdf1d"

/**
 * Line获取的产品ID
 */
val APP_ID_LINE = "1654871917"

/**
 * 常用MMKV的key常量
 * */
const val CURRENT_DEVICE_NAME = "current_device_name"
const val TARGET_STEP = "target_step"

object USER {
    //个人信息页缓存相关
    const val USERID = "userId"//用户id
}

//activity的resultCode 常量记录
const val SINGLE_LINE_EDIT = 0x000001
const val PERSON_INFO = 0x000002
const val ADDRESS_SELECT = 0x000003
const val LOCATION_FREQUENCY_SET = 0x000004
const val REGISTER = 0x000005