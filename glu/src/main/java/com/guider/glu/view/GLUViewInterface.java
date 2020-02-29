package com.guider.glu.view;

/**
 * Created by haix on 2019/6/13.
 */

public interface GLUViewInterface {

    void connectFailed(int status);
    void startConnect();
    void connectSuccess();

    void startInitTick(String time);
    void stopInit();
    void initTimeOut();

    void showMeasureTime(String time);
    void showMeasureRemind(String remind);
    void measureFinish();

    void uploadPersonalInfoSucceed();
    void uploadPersonalInfoFailed();

    void measureErrorAndCloseBlueConnect();

    void goMeasure();


    //----glu_phone使用

    void look_error(String error);
    void showMeasureTime(int time);
    
    void insertFingerToMeasure();

    void time60Begin();

    void finger_time(int time);
    //-----

}
