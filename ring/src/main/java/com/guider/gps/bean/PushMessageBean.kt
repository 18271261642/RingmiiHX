package com.guider.gps.bean

import com.guider.health.apilib.enums.PushMsgType

/**
 * 推送消息附件信息的bean类
 */
data class PushMessageBean(
    val accountId: Int,
    val key:String,
    val dataId:Int,
    val type: PushMsgType
)