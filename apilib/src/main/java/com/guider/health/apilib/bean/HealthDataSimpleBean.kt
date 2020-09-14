package com.guider.health.apilib.bean

/**
 * @Package: com.guider.health.apilib.bean
 * @ClassName: HealthDataSimpleBean
 * @Description: 健康数据整合类
 * (整合 血压，心率，体温，睡眠，运动，血糖)
 * @Author: hjr
 * @CreateDate: 2020/9/11 14:54
 * Copyright (C), 1998-2020, GuiderTechnology
 */
data class HealthDataSimpleBean(
        val id: Int,
        val testTime: String,
        var dbp: Int = 0,
        var sbp: Int = 0,
        var state: String = "",
        var state2: String = "",
        var bodyTemp: Double = 0.0,
        var hb: Int = 0,
        var minute: Int = 0,
        var step: Int = 0,
        var bs: Double = 0.0,
        var bo:Int = 0,
        var warningContent: String = ""
)