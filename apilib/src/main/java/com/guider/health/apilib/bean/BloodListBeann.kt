package com.guider.health.apilib.bean

data class BloodListBeann(
    val accountId: Int,
    val createTime: String,
    val dbp: Int,
    val deviceCode: String,
    val heartBeat: Int,
    val id: Int,
    val normal: Boolean,
    val sbp: Int,
    val state: String,
    val state2: String,
    val testTime: String,
        //详细状态判断的key值
    var stateKey: String = ""
)