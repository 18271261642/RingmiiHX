package com.guider.health.apilib.bean

import java.io.Serializable

/**
 * @Package: com.guider.gps.bean
 * @ClassName: ElectronicFenceBean
 * @Description: 电子围栏坐标bean类
 * @Author: hjr
 * @CreateDate: 2020/9/7 14:42
 * Copyright (C), 1998-2020, GuiderTechnology
 */
//x经度、y纬度
data class ElectronicFenceBean(var lat: Double, var lng: Double):Serializable