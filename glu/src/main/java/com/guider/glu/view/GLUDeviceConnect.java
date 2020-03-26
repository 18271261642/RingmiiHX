package com.guider.glu.view;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.guider.glu.R;
import com.guider.glu.presenter.GLUServiceManager;
import com.guider.health.common.device.DeviceInit;
import com.guider.health.common.utils.SkipClick;

import ble.BleClient;

/**
 * 蓝牙连接
 * -> GLUStartMeasureAndShowResult
 * Created by haix on 2019/6/12.
 */

public class GLUDeviceConnect extends GLUFragment {

    private View view;


    //请等待提示再伸入手指\n建议的环境温度 20℃—27℃\n合适的手指温度29℃—35℃
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.glu_device_connect, container, false);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        GLUServiceManager.getInstance().stopDeviceConnect();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        //setHomeEvent(view.findViewById(R.id.home), Config.HOME_DEVICE);

        Log.i("haix", "dddd");

        BleClient.init(_mActivity);

        view.findViewById(R.id.home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                _mActivity.setResult(112);

                _mActivity.finish();
                System.exit(0);
            }
        });

        ((TextView) view.findViewById(R.id.title)).setText("设备连接");
        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GLUServiceManager.getInstance().stopDeviceConnect();
//                pop();

                _mActivity.setResult(113);

                _mActivity.finish();
                System.exit(0);
            }
        });

        view.findViewById(R.id.skip).setVisibility(View.VISIBLE);
        view.findViewById(R.id.skip).setOnClickListener(new SkipClick(this , DeviceInit.DEV_GLU , true));


        GLUServiceManager.getInstance().startDeviceConnect(_mActivity);
    }

    Handler handler = new Handler();

    @Override
    public void connectFailed(final int status) {

        handler.removeMessages(0);
        _mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //final Button buttonConnect = view.findViewById(R.id.button_cancel);
                final Button buttonFailed = view.findViewById(R.id.glu_button_failed);
                final TextView operatReminders = view.findViewById(R.id.glu_operat_reminders);
                final ImageView iconImage = view.findViewById(R.id.glu_connect_icon);

                buttonFailed.setVisibility(View.VISIBLE);
                buttonFailed.setText("已经解决 重新连接");
                iconImage.setImageResource(R.mipmap.icon_ble);
                if (status == -101) {
                    operatReminders.setText(
                            "1.请确认设备已正常开启\n" +
                            "2.请确认系统【蓝牙】已开启或重启系统蓝牙\n" +
                            "3.请确认系统【位置信息】功能已开启");
                } else {
                    operatReminders.setText("连接失败, 请确保设备开关已经打开");
                }
                buttonFailed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        operatReminders.setText("蓝牙连接中...");
                        //buttonConnect.setVisibility(View.VISIBLE);
                        buttonFailed.setVisibility(View.GONE);
                        GLUServiceManager.getInstance().startDeviceConnect(_mActivity);

                    }
                });
            }
        });

    }

    @Override
    public void connectSuccess() {

        handler.removeMessages(0);
        start(new GLUStartMeasureAndShowResult());

    }

    @Override
    public void startConnect() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                GLUServiceManager.getInstance().stopDeviceConnect();
                GLUServiceManager.getInstance().startDeviceConnect(_mActivity);
            }
        }, 1000 * 4);
    }
}
