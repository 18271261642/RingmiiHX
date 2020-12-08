package com.guider.health.apilib.bean

data class BloodOxygenListBean(
    val accountId: Int,
    val bo: Int,
    val createTime: String,
    val deviceCode: String,
    val heartBeat: Int,
    val id: Int,
    val normal: Boolean,
    val state: String,
    val state2: String,
    val testTime: String,
        //详细状态判断的key值
    var stateKey: String = ""
)