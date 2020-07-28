package com.guider.glu_phone.view;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.guider.glu.presenter.GLUServiceManager;
import com.guider.glu_phone.R;


/**
 * Created by haix on 2019/7/18.
 */

public class BluetoothConnect extends GlocoseFragment{



    private View view;

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);


        if (!hidden) {

            GLUServiceManager.getInstance().setViewObject(this);

        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bluetooth_connect, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((TextView) view.findViewById(R.id.head_title)).setText(getResources().getText(R.string.ble_connect));
        view.findViewById(R.id.head_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GLUServiceManager.getInstance().stopDeviceConnect();
                popTo(TurnOnOperation.class, false);

            }
        });


        GLUServiceManager.getInstance().startDeviceConnect(_mActivity);

    }

    @Override
    public void connectFailed(int status) {
        _mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Log.i("haix", "连接失败2");
                final Button bt = view.findViewById(R.id.button_failed);
                final TextView tv = view.findViewById(R.id.scan_time);
                tv.setVisibility(View.GONE);
                bt.setVisibility(View.VISIBLE);
                bt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bt.setVisibility(View.GONE);
                        //tv.setVisibility(View.VISIBLE);
                        GLUServiceManager.getInstance().startDeviceConnect(_mActivity);
                    }
                });
            }
        });


    }

    @Override
    public void connectSuccess() {
        //需要等待插入手指
    }

//    @Override
//    public void insertFingerToMeasure() {
//        startWithPop(new InsertFinger());
//    }

    @Override
    public void goMeasure() {
        Log.i("haix", "插入手指3");
        startWithPop(new InsertFinger());
    }
}
