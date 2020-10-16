package com.guider.health.apilib.bean

data class CareMsgListBean(
    val adviceContent: String,
    val adviceType: String,
    val createTime: String?,
    val doctorAccountId: Int,
    val id: Int,
    val names: String,
    val sendTime: String,
    val state: String,
    val title: String,
    val unreadCount: Int,
    val updateTime: String,
    val userAccountIds: List<Int>
)