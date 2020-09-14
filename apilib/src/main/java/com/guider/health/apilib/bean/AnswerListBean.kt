package com.guider.health.apilib.bean

import com.guider.health.apilib.enums.AnswerMsgType

data class AnswerListBean(
    val accountId: Int,
    val content: String,
    val contentId: Int,
    val createTime: String,
    val type: AnswerMsgType
)