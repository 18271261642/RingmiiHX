package com.guider.health.apilib.bean

data class BloodSugarListBean(
    val accountId: Int,
    val bloodSpeed: Int,
    val bs: Double,
    val bsTime: String,
    val createTime: String,
    val deviceCode: String,
    val hemoglobin: Int,
    val id: Int,
    val normal: Boolean,
    val state: String,
    val state2: String,
    val testTime: String
)