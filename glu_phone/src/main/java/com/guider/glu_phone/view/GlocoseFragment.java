package com.guider.glu_phone.view;


import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.Nullable;

import com.guider.glu.presenter.GLUServiceManager;
import com.guider.glu.view.GLUViewInterface;
import com.guider.health.common.core.BaseFragment;

/**
 * Created by haix on 2019/6/23.
 */

public class GlocoseFragment extends BaseFragment implements GLUViewInterface{

    protected final static int TONEXT = 111;
    protected final static int TOHOME = 112;
    protected final static int TOBACK = 113;

    protected PowerManager.WakeLock mWakeLock;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        GLUServiceManager.getInstance().setViewObject(this);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);


        if (!hidden) {

            GLUServiceManager.getInstance().setViewObject(this);

        }

    }


    @Override
    public void connectFailed(int status) {

    }

    @Override
    public void startConnect() {

    }

    @Override
    public void connectSuccess() {

    }

    @Override
    public void startInitTick(String time) {

    }

    @Override
    public void stopInit() {

    }

    @Override
    public void initTimeOut() {

    }

    @Override
    public void showMeasureTime(String time) {

    }

    @Override
    public void showMeasureTime(int time) {

    }

    @Override
    public void showMeasureRemind(String remind) {

    }

    @Override
    public void measureFinish() {

    }

    @Override
    public void uploadPersonalInfoSucceed() {

    }

    @Override
    public void uploadPersonalInfoFailed() {

    }

    @Override
    public void measureErrorAndCloseBlueConnect() {

    }

    @Override
    public void goMeasure() {

    }

    @Override
    public void look_error(String error) {

    }

    @Override
    public void insertFingerToMeasure() {

    }

    @Override
    public void time60Begin() {

    }

    @Override
    public void finger_time(int time) {

    }


}
