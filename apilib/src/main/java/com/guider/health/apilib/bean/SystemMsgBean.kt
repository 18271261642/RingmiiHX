package com.guider.health.apilib.bean

import com.guider.health.apilib.enums.SystemMsgType

data class SystemMsgBean(
        val accountId: Int,
        val content: String,
        val createTime: String,
        val deviceCode: String,
        val id: Int,
        val name: String,
        val testTime: String,
        val type: SystemMsgType,
        val updateTime: String
)