package com.guider.health.apilib.bean

import com.guider.health.apilib.enums.AnswerMsgType

data class AnswerListBean(
    var accountId: Int,
    var content: String,
    var contentId: Int,
    var createTime: String,
    var type: AnswerMsgType
)