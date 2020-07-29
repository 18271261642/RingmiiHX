package cn.wuweikang.view;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.guider.health.common.core.Config;
import com.guider.health.common.device.DeviceInit;
import com.guider.health.common.utils.SkipClick;

import ble.BleClient;
import ble.SimpleDevice;
import ble.callback.SimpleCallback;
import cn.wuweikang.BTConnectStreamThread;
import cn.wuweikang.R;
import cn.wuweikang.WuweikangData;

/**
 * Created by haix on 2019/7/29.
 */

public class ConnectBluetooth extends Dao12Interface{


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        BleClient.init(_mActivity);
        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                beforeLeaveThisFragment();
                pop();
            }
        });

        view.findViewById(R.id.home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                beforeLeaveThisFragment();
                try {
                    popTo(Class.forName(Config.HOME_DEVICE), false);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        view.findViewById(R.id.skip).setVisibility(View.VISIBLE);
        view.findViewById(R.id.skip).setOnClickListener(new SkipClick(this , DeviceInit.DEV_ECG_12));

        BleClient.instance().searchClassicDevice(callback);
    }

    SimpleCallback callback = new SimpleCallback() {
        @Override
        protected SimpleDevice onFindDevice(SimpleDevice device) {
            if ("ALX SPP".equals(device.getName()) || "WWKECG".equals(device.getName())) {
                BleClient.instance().stopSearching();
                BleClient.instance().recordDevice(device);
                WuweikangData.getInstance().connect(ConnectBluetooth.this._mActivity , device.deviceInfo.device , handler);
            }
            return null;
        }

        @Override
        protected void onSearchFinish() {
            connectFailed(getResources().getString(R.string.wwk_not_found_device));
        }

        @Override
        public void onConnectSuccess(SimpleDevice device) {
            super.onConnectSuccess(device);

        }

        @Override
        public void onConnectFail(String msg) {
            super.onConnectFail(msg);
            connectFailed(getResources().getString(R.string.wwk_confirm_device_open));
        }
    };

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BTConnectStreamThread.BT_CONNECT_FAILED: // 连接失败
                    connectFailed(getResources().getString(R.string.wwk_battery_tips));
                    break;

                case BTConnectStreamThread.DRAW_ECG_WAVE: // 可以开始测量
                    toNext();
                    break;


            }
        }
    };

    public void beforeLeaveThisFragment(){
//        BleClient.instance().stopSearching();
    }


    public void connectFailed(String msg){

        final Button connect_failed = view.findViewById(R.id.connect_failed);
        final TextView reminder = view.findViewById(R.id.reminder);
        reminder.setText(msg);
        connect_failed.setVisibility(View.VISIBLE);

        connect_failed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BleClient.instance().disconnect(BleClient.instance().popStagingDevice());
                WuweikangData.getInstance().disconnect();
                BleClient.instance().searchClassicDevice(callback);

                reminder.setText(getResources().getString(R.string.wwk_bt_conectting_));
                connect_failed.setVisibility(View.INVISIBLE);
            }
        });


    }

    public void toNext(){
        start(new MeasureDevice());
    }
}
