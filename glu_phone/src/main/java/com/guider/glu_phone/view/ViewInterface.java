package com.guider.glu_phone.view;

/**
 * Created by haix on 2019/7/18.
 */

public interface ViewInterface {

    void connectFailed(int status);
    void connectSuccess();
    void showMeasureTime(int time);
    void showMeasureRemind(String remind);
    void measureFinish();
    void measureErrorAndCloseBlueConnect();
    void insertFingerToMeasure();

    void time60Begin();

}
