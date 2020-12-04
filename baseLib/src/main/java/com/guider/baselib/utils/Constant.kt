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
const val TIME_FORMAT_PATTERN6 = "yyyy年MM月dd日"
const val TIME_FORMAT_PATTERN7 = "yyyy-MM-dd-HH"
const val TIME_FORMAT_PATTERN8 = "HH:mm"
const val TIME_FORMAT_PATTERN9 = "MM-dd HH:mm"
const val TIME_FORMAT_PATTERN10 = "MM-dd"

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
 * 默认国家区号
 */
val DEFAULT_COUNTRY_CODE = "TW"
/**
 * 常用MMKV的key常量
 * */
const val BIND_DEVICE_NAME = "bind_device_name"
const val BIND_DEVICE_ACCOUNT_ID = "bind_device_account_id"
const val BIND_DEVICE_ACCOUNT_HEADER = "bind_device_account_header"
const val BIND_DEVICE_ACCOUNT_POSITION_SEARCH_PERSON = "bind_device_account_position_search_person"
const val BIND_DEVICE_CODE = "bind_device_code"
const val BIND_DEVICE_LAST_LOCATION = "bind_device_account_last_position"
const val TARGET_STEP = "target_step"
const val IS_FIRST_START = "is_first_start"
const val REFRESH_TOKEN = "refresh_token"
const val EXPIRED_TIME = "expired_time"
const val HR_CHECK = "hrOpen"
const val HR_INTERVAL = "hrInterval"
const val BT_CHECK = "btOpen"
const val BT_INTERVAL = "btInterval"
const val NOTIFICATION_CLICK_ENTER = "notification_click_enter"
const val TOURISTS_MODE = "Tourists_mode"
const val PHONE_IMEI_CODE = "Phone_imei_code"

object USER {
    //个人信息页缓存相关
    const val USERID = "userId"//用户id
    const val HEADER = "userHeader"//用户头像
    const val NAME = "userName"//用户头像
    const val TOKEN = "token"//用户token
    const val TOKEN_CACHE = "token_cache"
    const val COUNTRY_CODE = "country_code"//国家区号
    const val AREA_CODE = "area_code"//国家区号
    const val PHONE = "phone"//手机号
    const val BIRTHDAY = "birthday"//生日
    const val OWN_BIND_DEVICE_CODE = "own_bind_device_code"
}

//裁剪照片路径
const val CROP_PATH_NEW = "/guiderGps/crop/image/"

/**
 * 下面均为跳转路径保存处
 */
const val msgList: String = "/ring/RingMsgListActivity"

//activity的resultCode 常量记录
const val SPORT_STEP_INFO = 0x000001
const val PERSON_PHONE = 0x000002
const val ADDRESS_SELECT = 0x000003
const val LOCATION_FREQUENCY_SET = 0x000004
const val REGISTER = 0x000005

// 图片裁剪
const val IMAGE_CUT_CODE = 0x000006
const val PERSON_NAME = 0x000007
const val PERSON_HEIGHT = 0x000008
const val PERSON_WEIGHT = 0x000009
const val PERSON_INFO = 0x000010
const val COMPLETE_INFO = 0x000011
const val BIND_PHONE = 0x000012
const val SCAN_CODE = 0x000013
const val ADD_NEW_DEVICE = 0x000014
const val ADD_NEW_MEMBER = 0x000015
const val SELECT_ADDRESS = 0x000016
const val ALARM_SET = 0x000017
const val HISTORY_RECORD_LOCATION = 0x000018
const val ELECTRONIC_FENCE_LIST = 0x000019
const val RESET_PASSWORD = 0x000020
const val ACCOUNT_BIND = 0x000021