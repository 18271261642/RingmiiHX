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


    public final static String ECG_FRAGMENT = "com.guider.health.ecg.view.ECGDeviceOperate";
    public final static String ECG_HD_FRAGMENT = "com.gaider.proton.view.ProtonEcgFirst";
    public final static String GLU_FRAGMENT = "com.guider.glu.view.GLUChooseTime";
    // 移动版
    public static String BP_FRAGMENT = "com.guider.health.bp.view.bp.BPFirstOperaterReminder";
    // AVE
    public static String BP_AVE_FRAGMENT = "com.guider.health.bp.view.avebp.BPFirstOperaterReminder";
    // 插线版
    public static String BP_CX_FRAGMENT = "com.guider.health.bp.view.cxbp.BPFirstOperaterReminder";
    // 云峰
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
    public final static String DAO12_FRAGMENT = "cn.wuweikang.view.VideoShow";





    public final static HashMap<String, Boolean> mapX = new HashMap<>();


    public static String IndexOxygen;

//    public static String IndexBPress;




    public static final List<String> DEVICE_KEYS = new ArrayList<>();
    public static final ArrayMap<String, Devices> DEVICE_OBJ = new ArrayMap<>();




}
