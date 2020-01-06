package com.gaider.proton;

import android.app.Activity;

import com.proton.ecgcard.algorithm.bean.AlgorithmResult;
import com.proton.ecgcard.algorithm.bean.RealECGData;

public interface Protocol {

    int CODE_HAND_ON = 1;
    int CODE_HAND_GONE = 0;

    interface IMeasureView{

        void onStartSearch();

        void notFindDevice();

        void onConnectFail();

        void onTick(int time);

        void onWarning();

        void onDeviceMeasure(RealECGData currentData);

        void hasDeviceInfo(String heart , String power , String signal);

        void onStatusChange(int code);

        void onAnalysisResult(AlgorithmResult algorithmResult);

        Activity getMyContext();
    }

    interface IMeasurePresenter{

        void init(IMeasureView view);

        void start();

        void restart();

        void finish();
    }
}
