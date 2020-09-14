package com.guider.health.apilib.bean

data class BodyTempListBean(
    val accountId: Int,
    val bodyTemp: Double,
    val createTime: String,
    val deviceCode: String,
    val id: Int,
    val normal: Boolean,
    val state: String,
    val state2: String,
    val testTime: String
)