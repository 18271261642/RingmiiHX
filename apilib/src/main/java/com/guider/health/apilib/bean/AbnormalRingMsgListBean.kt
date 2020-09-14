package com.guider.health.apilib.bean

data class AbnormalRingMsgListBean(
        val accountId: Int,
        val createTime: Any,
        val dataType: String,
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