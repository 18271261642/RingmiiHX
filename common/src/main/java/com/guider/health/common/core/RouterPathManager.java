package com.guider.health.common.core;

import java.util.LinkedList;

/**
 * Created by haix on 2019/6/21.
 */

public class RouterPathManager {

    public final static LinkedList<String> Devices = new LinkedList<>();
    public final static String BP_PATH = "/bp/main";
    public final static String ECG_PATH = "/ecg/main";
    public final static String GLU_PATH = "/glu/main";
    public final static String L_PATH = "l/main";
    public final static String END_PATH = "/app/end";
    public final static String CHOOSE_DEVICE_PATH = "/app/ChooseDeviceFragment";
    public final static String CHOOSE_WEIXIN_PATH = "/weix/WeixinScan";
    public final static String IDCARD_PATH = "/infocard/CardScanIdCard";
}
