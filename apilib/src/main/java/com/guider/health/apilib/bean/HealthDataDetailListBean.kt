package com.guider.health.apilib.bean

/**
 * @Package: com.guider.health.apilib.bean
 * @ClassName: HealthDataListBean
 * @Description: 健康数据详细列表数据bean
 * @Author: hjr
 * @CreateDate: 2020/9/11 16:18
 * Copyright (C), 1998-2020, GuiderTechnology
 */
data class HealthDataDetailListBean (var time:String,var list:List<HealthDataSimpleBean>)