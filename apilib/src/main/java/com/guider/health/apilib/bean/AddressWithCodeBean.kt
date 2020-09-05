package com.guider.health.apilib.bean

data class AddressWithCodeBean(
    val areaType: String,
    val createTime: String,
    val id: Int,
    val lat: Double,
    val lng: Double,
    val name: String,
    val parentId: Int,
    val updateTime: String
)