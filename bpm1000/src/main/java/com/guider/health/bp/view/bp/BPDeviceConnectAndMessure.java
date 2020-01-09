package com.guider.health.bp.view.bp;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.guider.health.bluetooth.core.BleBluetooth;
import com.guider.health.bp.R;
import com.guider.health.bp.presenter.BPServiceManager;
import com.guider.health.bp.view.BPFragment;
import com.guider.health.common.core.Config;
import com.guider.health.common.device.DeviceInit;
import com.guider.health.common.utils.SkipClick;

import ble.BleClient;

/**
 * Created by haix on 2019/6/10.
 */

public class BPDeviceConnectAndMessure extends BPFragment {


    private View view;

    Handler handler = new Handler();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bp_connect_and_meassure, container, false);


        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        _mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        BleBluetooth.getInstance().openBluetooth();

        setHomeEvent(view.findViewById(R.id.home), Config.HOME_DEVICE);
        ((TextView) view.findViewById(R.id.title)).setText(R.string.shebeiceliang);
        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BPServiceManager.getInstance().stopDeviceConnect();
                pop();
            }
        });

        view.findViewById(R.id.skip).setVisibility(View.VISIBLE);
        view.findViewById(R.id.skip).setOnClickListener(new SkipClick(this , DeviceInit.DEV_BP));

        //开始测量
        BleClient.init(_mActivity);
        BPServiceManager.getInstance().startMeasure();
        handler = new Handler();
        startFailTime();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeMessages(0);
    }

    @Override
    public void startUploadData() {

    }

    @Override
    public void connectAndMessureIsOK() {
        handler.removeMessages(0);
        _mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                showDialog(R.layout.bp_dialog);
                baseHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hideDialog();

                        startWithPop(new BPMeasureResult());
                    }
                }, 2000);


            }
        });

    }

    @Override
    public void connectNotSuccess() {
        handler.removeMessages(0);
        _mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                changeUi2Fail();
            }
        });

    }

    private void changeUi2Fail() {
        handler.removeMessages(0);
        ((TextView) view.findViewById(R.id.bp_reminder)).setText(R.string.lianjieshibai);

        //view.findViewById(R.id.bp_cancel).setVisibility(View.GONE);
        final Button bt = view.findViewById(R.id.bp_re);
        bt.setVisibility(View.VISIBLE);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView) view.findViewById(R.id.bp_reminder)).setText(R.string.celiangzhong);
                //view.findViewById(R.id.bp_cancel).setVisibility(View.VISIBLE);
                bt.setVisibility(View.INVISIBLE);
                BPServiceManager.getInstance().startMeasure();
                startFailTime();
            }
        });
    }

    void startFailTime() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 到这里就说明55秒都没有连接成功 ,要重新连接
                changeUi2Fail();
            }
        }, 1000 * 90);
    }
}
