package com.guider.health.apilib.model

data class HeartMeasureResultListBean(
        val accountId: Int,
        val bloodSpeed: Int,//血流速度
        val bs: Double = 0.0,//血糖值
        val bsTime: String,
        val createTime: String?,
        val deviceCode: String?,
        val deviceTypeName: String?,
        val hemoglobin: Int,//血红蛋白
        val id: Int,
        val name: String,
        val normal: Boolean,
        val state: String,
        val state2: String,
        val testTime: String,
        val bo: Int,//血氧
        val heartBeat: Int,
        val hb: Int//心率
)
