package com.guider.health.apilib.bean

data class SleepDataListBean(
    val accountId: Int ,
    val createTime: String = "",
    val deviceCode: String ="",
    val endTime: String ="",
    val id: Int =0,
    val minute: Int =0,
    //1；深度睡眠，2；浅度睡眠，3；醒来时长
    val sleepType: Int =3,
    val startTime: String = "",
    val testTime: String =""
)