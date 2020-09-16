package com.guider.health.apilib.bean

data class RingSetBean(
    val accountId: Int,
    val btInterval: Int,
    val btOpen: Boolean,
    val createTime: String,
    val deviceCode: String,
    val deviceState: String,
    val electricity: String,
    val fence: String,
    val hrInterval: Int,
    val hrOpen: Boolean,
    val id: Int,
    val optionRate: String,
    val updateTime: String,
    val walkTarget: Int
)