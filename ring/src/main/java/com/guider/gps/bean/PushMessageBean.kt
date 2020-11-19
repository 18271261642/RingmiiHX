package com.guider.gps.bean

/**
 * 推送消息附件信息的bean类
 */
data class PushMessageBean(
    val accountId: Int,
    val content: String,
    val dbp: Int,
    val imei: String,
    val name: String,
    val sbp: Int,
    val type: Int
)