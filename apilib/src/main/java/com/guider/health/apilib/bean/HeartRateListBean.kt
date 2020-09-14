package com.guider.health.apilib.bean

data class HeartRateListBean(
    val accountId: Int,
    val createTime: String,
    val deviceCode: String,
    val hb: Int,
    val id: Int,
    val normal: Boolean,
    val state: String,
    val state2: String,
    val testTime: String
)