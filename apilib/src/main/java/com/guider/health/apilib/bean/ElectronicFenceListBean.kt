package com.guider.health.apilib.bean

import java.io.Serializable

data class ElectronicFenceListBean(
        var accountId: Int,
        var deviceCode: String,
        var id: Int,
        var `open`: Boolean,
        var pointList: List<ElectronicFenceBean>,
        var title: String
):Serializable