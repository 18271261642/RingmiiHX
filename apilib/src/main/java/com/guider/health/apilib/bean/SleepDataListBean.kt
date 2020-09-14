package com.guider.health.apilib.bean

data class SleepDataListBean(
    val accountId: Int,
    val createTime: String,
    val deviceCode: String,
    val endTime: String,
    val id: Int,
    val minute: Int,
    val sleepType: Int,
    val startTime: String,
    val testTime: String
)