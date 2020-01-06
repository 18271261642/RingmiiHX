package com.guider.health.ecg.view;

import android.content.Context;

/**
 * Created by haix on 2019/6/13.
 */

public interface ECGViewInterface {

    void connectFaile(int code);
    void connectSuccess();
    void scanFailed();

    void measureTime(int time);
    void measureWare(int ware);
    void measureComplete();


    void onAnalysisTime(int time);
    void onAnalysisEnd();
    void onAnalysisResult(String result);

    /**
     * 低电量提示
     * 目前来说只做提示 , 不妨碍测量
     * @param power
     */
    void powerLow(String power);

    void measureFailed(String msg);

    /**
     * 在最后提交结果到服务器失败的时候会调用这个方法
     * @param msg 失败原因
     * @param isNeedRemeasure 是否需要重新测量
     */
    void measureTimeOut(String msg , boolean isNeedRemeasure);


    Context getViewContext();


}
