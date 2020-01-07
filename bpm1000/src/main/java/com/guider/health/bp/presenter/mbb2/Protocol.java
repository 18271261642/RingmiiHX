package com.guider.health.bp.presenter.mbb2;

import android.app.Activity;

import com.guider.health.bp.view.mbb2.iknetbluetoothlibrary.BluetoothManager;

public interface Protocol {

    interface IView extends BluetoothManager.OnBTMeasureListener{
        Activity getMyActivity();
    }

    interface IPresenter{
        void start();

        void finish();
    }
}
