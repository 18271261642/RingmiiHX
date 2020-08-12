package com.guider.healthring.util;

/**
 * 常量
 * @author XuBo
 */
public class Constant {

    /**
     * 公制转英制的倍率
     */
    public static final float METRIC_MILE = 0.621f;
    /**
     * 跳转页面附带的参数: 日期
     */
    public static final String DETAIL_DATE = "detail_date";
    /**
     * 页面跳转附带返回码,成功
     */
    public static final int RESULT_CODE = 200;

    public static final boolean ANTI_ALIAS = true;

    public static final int DEFAULT_SIZE = 150;
    public static final int DEFAULT_START_ANGLE = 270;
    public static final int DEFAULT_SWEEP_ANGLE = 360;

    public static final int DEFAULT_ANIM_TIME = 10 * 1000;

    public static final int DEFAULT_MAX_VALUE = 100;
    public static final int DEFAULT_VALUE = 0;

    public static final int DEFAULT_HINT_SIZE = 15;
    public static final int DEFAULT_UNIT_SIZE = 30;
    public static final int DEFAULT_VALUE_SIZE = 15;

    public static final int DEFAULT_ARC_WIDTH = 8;

    public static final int DEFAULT_WAVE_HEIGHT = 40;



    //根据秒转时分秒
    public static String secToTime(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = "00" + ":" + unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }
}
