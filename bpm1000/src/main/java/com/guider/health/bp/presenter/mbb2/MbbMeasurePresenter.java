package com.guider.health.bp.presenter.mbb2;

import com.guider.health.bp.view.mbb2.iknetbluetoothlibrary.BluetoothManager;

public class MbbMeasurePresenter implements Protocol.IPresenter {

    Protocol.IView mView;
    private BluetoothManager bluetoothManager;

    public MbbMeasurePresenter(Protocol.IView view) {
        mView = view;
        bluetoothManager = BluetoothManager.getInstance(mView.getMyActivity());
        bluetoothManager.initSDK();
    }

    @Override
    public void start() {
        bluetoothManager.startBTAffair(mView);
    }

    @Override
    public void finish() {
        bluetoothManager.stopBTAffair();
    }

}
