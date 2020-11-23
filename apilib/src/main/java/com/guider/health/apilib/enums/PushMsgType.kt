package com.guider.health.apilib.enums

/**
 * @Package: com.guider.health.apilib.enums
 * @ClassName: PushMsgType
 * @Description: 谷歌推送消息的类型
 * @Author: hjr
 * @CreateDate: 2020/11/20 16:25
 * Copyright (C), 1998-2020, GuiderTechnology
 */
enum class PushMsgType {
    //("血压")
    BP,
    //("血氧")
    BO,
    //("血糖")
    BS,
    //("心率")
    HR,
    //("电量低")
    ELE,
    //("摘除")
    TAKEOFF,
    //("关机")
    CLOSE,
    //("电子围栏")
    FENCE,
    //("SOS")
    SOS,
}