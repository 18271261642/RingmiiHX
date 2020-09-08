package com.guider.health.apilib.bean

data class SportListBean(
    val accountId: Int,
    val cal: Double,
    val createTime: String,
    val deviceCode: String,
    val dis: Double,
    val id: Int,
    val step: Int,
    val testTime: String
)