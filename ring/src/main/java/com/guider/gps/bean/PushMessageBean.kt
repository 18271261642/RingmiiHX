package com.guider.gps.bean

/**
 * 推送消息附件信息的bean类
 */
data class PushMessageBean(
    val accountId: Int,
    val key:String,
    val dataId:Int,
    val type: String
)