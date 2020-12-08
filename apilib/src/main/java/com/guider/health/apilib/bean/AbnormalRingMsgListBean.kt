package com.guider.health.apilib.bean

import com.guider.health.apilib.enums.HealthWarnDataType

data class AbnormalRingMsgListBean(
        val accountId: Int,
        val createTime: String?,
        val dataType: HealthWarnDataType,
        val deviceTypeName: Any,
        val healthDataId: Int,
        val id: Int,
        val name: Any,
        val number: Int,
        val tableName: String,
        val testTime: String,
        val warningContent: String,
        var dataBean: HealthDataSimpleBean? = null,
        val relationShip:String
)