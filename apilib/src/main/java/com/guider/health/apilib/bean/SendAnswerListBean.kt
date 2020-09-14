package com.guider.health.apilib.bean

data class SendAnswerListBean(
    val accountId: Int,
    val chatContentType: String,
    val chatId: Int,
    val checked: Boolean,
    val content: String,
    val createTime: String,
    val id: Int,
    val updateTime: String
)