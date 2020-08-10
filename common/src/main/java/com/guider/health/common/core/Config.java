package com.guider.health.common.core;

import android.util.ArrayMap;

import com.guider.health.apilib.model.Devices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by haix on 2019/6/22.
 */

public class Config {

    public  static String HOME_DEVICE = "com.guider.health.ChooseDeviceFragment";
    public  static String HOME_LOGIN = "com.guider.health.ChooseLoginFragment";
    public  static String DEVICES_FRAGMENT = "com.guider.health.ChooseDeviceFragment";
    public  static String BIND_PHONE = "com.guider.health.shenzhen.RegisterFragment";
    public  static String FOCUS_WECHAT = "com.guider.health.shenzhen.WeixinScanBind";

    //6导测量仪器
    public final static String ECG_FRAGMENT = "com.guider.health.ecg.view.ECGDeviceOperate";
    //红豆心电
    public final static String ECG_HD_FRAGMENT = "com.gaider.proton.view.ProtonEcgFirst";
    // 无创血糖测量
    public final static String GLU_FRAGMENT = "com.guider.glu.view.GLUChooseTime";
    //福尔血糖
    public final static String FORA_GLU_FRAGMENT = "com.guider.health.foraglu.ForaGluReminderFragment";
    // 福尔血氧
    public final static String FORA_BO_FRAGMENT = "com.guider.health.forabo.ForaBOReminderFragment";
    // 福尔耳温
    public final static String FORA_ET_FRAGMENT = "com.guider.health.foraet.ForaETReminderFragment";
    // TODO 添加新设备首页
    // 移动版  福尔臂式血壓測量
    public static String BP_FRAGMENT = "com.guider.health.bp.view.bp.BPFirstOperaterReminder";
    // 四川云峰的VAE-2000的血压仪
    public static String BP_AVE_FRAGMENT = "com.guider.health.bp.view.vaebp.BPFirstOperaterReminder";
    // 连线款的血压仪
    public static String BP_CX_FRAGMENT = "com.guider.health.bp.view.cxbp.BPFirstOperaterReminder";
    // 四川云峰的动脉硬化款的血压仪
    public static String BP_YF_FRAGMENT = "com.guider.health.bp.view.yfbp.BPFirstOperaterReminder";
    // MBB_88
    public static String BP_MBB88_FRAGMENT = "com.guider.health.bp.view.mbb2.BP88B.BPFirstOperaterReminder";
    // MBB_9804
    public static String BP_MBB9804_FRAGMENT = "com.guider.health.bp.view.mbb2.BP9804.BPFirstOperaterReminder";

    public final static String WX_FRAGMENT = "com.guider.health.weixinlogin.view.WeixinScan";
    public final static String IDCARD_FRAGMENT = "com.guider.health.infocard.view.CardScanIdCard";
    // 脏音听诊器
    public final static String ECG_ZANG_YIN = "com.guider.health.ecg.view.ZangYin";

    public final static String END_FRAGMENT = "com.guider.health.ShowAllDevicesMessureResult";
    public final static String RTC_ACTIVITY = "com.aliyun.rtcdemo.activity.AliRtcChatActivity";
    public final static String PRINT_FRAGMENT = "com.guider.printlib.PrintingResultFragment";
    //十二导心电测量
    public final static String DAO12_FRAGMENT = "cn.wuweikang.view.VideoShow";
    // MEDCHECK 血糖
    public final static String MEDCHECK_GLU_FRAGMENT =
            "com.guider.health.medcheckglu.MEDCHECKGluReminderFragment";
    // MEDCHECK 血压
    public final static String MEDCHECK_PRE_FRAGMENT
            = "com.guider.health.medcheckpre.MEDCheckPressureReminderFragment";

    public final static HashMap<String, Boolean> mapX = new HashMap<>();

    public static String IndexOxygen;

    public static final List<String> DEVICE_KEYS = new ArrayList<>();
    public static final ArrayMap<String, Devices> DEVICE_OBJ = new ArrayMap<>();
}
