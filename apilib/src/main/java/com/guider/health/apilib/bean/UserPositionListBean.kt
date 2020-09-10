package com.guider.health.apilib.bean

data class UserPositionListBean(
    val accountId: Int,
    val addr: String,
    val createTime: String,
    val deviceCode: String,
    val id: Int,
    val lat: Double,
    val lng: Double,
    val testTime: String,
    val type: String,
    val updateTime: String
)