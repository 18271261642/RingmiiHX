package com.guider.health.apilib.bean

data class SleepDataListBean(
    val accountId: Int,
    val createTime: String,
    val deviceCode: String,
    val endTime: String,
    val id: Int,
    val minute: Int,
    //1；深度睡眠，2；浅度睡眠，3；醒来时长
    val sleepType: Int,
    val startTime: String,
    val testTime: String
)