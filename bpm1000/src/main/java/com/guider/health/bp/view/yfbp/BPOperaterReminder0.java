package com.guider.health.bp.view.yfbp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.guider.health.bp.R;
import com.guider.health.bp.view.BPFragment;
import com.guider.health.bp.view.TipTitleView;
import com.guider.health.common.core.Config;
import com.guider.health.common.core.HeartPressBp;
import com.guider.health.common.core.MyUtils;
import com.guider.health.common.device.DeviceInit;
import com.guider.health.common.utils.SkipClick;

import ble.BleClient;
import ble.SimpleDevice;
import ble.callback.SimpleCallback;

/**
 * 这是动脉硬化款的血压仪四川云峰的
 * Created by haix on 2019/6/25.
 */

public class BPOperaterReminder0 extends BPFragment {

    private View view;
    private Button research;
    private TextView textView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bp_yf_device_connect, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            return;
        }
        initView();

        initBle();

    }

    private void initView() {
        setHomeEvent(view.findViewById(R.id.home), Config.HOME_DEVICE);
        ((TextView) view.findViewById(R.id.title)).setText("蓝牙连接");
        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop();
            }
        });
        view.findViewById(R.id.skip).setVisibility(View.VISIBLE);
        view.findViewById(R.id.skip).setOnClickListener(new SkipClick(this , DeviceInit.DEV_BP_YF));
        TipTitleView tips = view.findViewById(R.id.tips);
        tips.setTips("动脉硬化测量","开机提醒","蓝牙连接", "信息录入", "操作指南" , "测量结果");
        tips.toTip(2);

        textView = view.findViewById(R.id.glu_operat_reminders);
        research = view.findViewById(R.id.glu_button_failed);
        research.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 连接失败后的按钮
                BleClient.instance().disconnect(BleClient.instance().popStagingDevice());
                BleClient.instance().getClassicClient().close();
                BleClient.instance().searchClassicDevice(bleCallback);
            }
        });
    }

    private void initBle() {
        BleClient.init(_mActivity);
        BleClient.instance().disconnect(BleClient.instance().popStagingDevice());
        BleClient.instance().searchClassicDevice( bleCallback);
    }

    SimpleCallback bleCallback = new SimpleCallback() {

        @Override
        public void onSearchStarted() {
            super.onSearchStarted();
            textView.setText("正在连接蓝牙...");
            research.setVisibility(View.GONE);
        }

        @Override
        protected SimpleDevice onFindDevice(SimpleDevice device) {
            if ("YFKJ".equals(device.getName())) {
                device.isBLE = false;
                return device;
            }
            return null;
        }

        @Override
        protected void onSearchFinish() {
            textView.setText("请确认设备已开机.");
            research.setVisibility(View.VISIBLE);
        }

        @Override
        public void onConnectSuccess(SimpleDevice device) {
            super.onConnectSuccess(device);
            HeartPressBp.getInstance().setDeviceAddress(MyUtils.getMacAddress());
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // 连接成功
                    start(new BPOperaterReminder1());
                }
            });
        }

        @Override
        public void onConnectFail(String msg) {
            super.onConnectFail(msg);

            _mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // 连接失败
                    textView.setText("请确认设备已开机.");
                    research.setVisibility(View.VISIBLE);
                }
            });
        }
    };

}

