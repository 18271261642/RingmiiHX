package com.guider.health.apilib.enums

/**
 * @Package: com.guider.health.apilib.enums
 * @ClassName: SystemMsgType
 * @Description: 系统消息类型
 * @Author: hjr
 * @CreateDate: 2020/10/14 15:32
 * Copyright (C), 1998-2020, GuiderTechnology
 */
enum class SystemMsgType {
    //手环电量 //关机 // 擦除 //sos报警 //电子围栏报警
    OPELECTRICITY,
    CLOSE,
    TAKEOFF,
    SOS,
    FENCE
}