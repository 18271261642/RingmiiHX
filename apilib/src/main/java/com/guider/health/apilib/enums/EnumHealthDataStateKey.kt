package com.guider.health.apilib.enums

/**
 * @Package: com.guider.health.apilib.enums
 * @ClassName: EnumHealthDataStateKey
 * @Description: 健康数据详细状态判断的枚举类
 * @Author: hjr
 * @CreateDate: 2020/12/8 11:47
 * Copyright (C), 1998-2020, GuiderTechnology
 */
enum class EnumHealthDataStateKey(var key: String, var value: String) {
    Normal("common.Normal", "正常"),
    Low("common.Low", "偏低"),
    LowFever("common.LowFever", "低度发热"),
    MediumFever("common.MediumFever", "中度发热"),
    HighlyFever("common.HighlyFever", "高度发热"),
    Hyperthermia("common.Hyperthermia", "超高度发热"),
    FbsLow("common.FbsLow", "空腹偏低"),
    FbsHigh("common.FbsHigh", "空腹偏高"),
    FbsNormal("common.FbsNormal", "空腹正常"),
    PbsLow("common.PbsLow", "餐后偏低"),
    PbsHigh("common.PbsHigh", "餐后偏高"),
    PbsNormal("common.PbsNormal", "餐后正常"),
    BpLow("common.BpLow", "正常血压偏低"),
    BpHigh("common.BpHigh", "正常血压偏高"),
    BpHyp("common.BpHyp", "疑似高血压"),
    BpNormal("common.BpNormal", "理想血压"),
    High("common.High", "偏高");
}