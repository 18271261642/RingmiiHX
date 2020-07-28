package com.guider.health.bp.view.yfbp;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.guider.health.bp.R;
import com.guider.health.bp.view.BPFragment;
import com.guider.health.bp.view.TipTitleView;
import com.guider.health.common.core.Config;
import com.guider.health.common.device.DeviceInit;
import com.guider.health.common.utils.SkipClick;

import ble.BleClient;

/**
 * 这是动脉硬化款的血压仪四川云峰的
 * Created by haix on 2019/6/25.
 */

public class BPOperaterReminder2 extends BPFragment implements Runnable{

    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bp_yf_reminder2_next, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null){
            return;
        }
        initView();

        startGetData();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showDialog("等待测量完成...");
            }
        }, 3000);
    }

    private void initView() {
        setHomeEvent(view.findViewById(R.id.home), Config.HOME_DEVICE);
        ((TextView) view.findViewById(R.id.title)).setText("操作指南");
        view.findViewById(R.id.skip).setVisibility(View.VISIBLE);
        view.findViewById(R.id.skip).setOnClickListener(new SkipClick(this , DeviceInit.DEV_BP_YF));
        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRun = false;
                pop();
            }
        });
        TipTitleView tips = view.findViewById(R.id.tips);
        tips.setTips("动脉硬化测量","开机提醒","蓝牙连接", "信息录入", "操作指南" , "测量结果");
        tips.toTip(4);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (hidden) {
            isRun = false;
            hideDialog();
        }

    }

    YfkjDataAdapter yfkjDataAdapter;
    private void startGetData() {
        synchronized (checkResultThread) {
            yfkjDataAdapter = new YfkjDataAdapter();
            checkResultThread.start();
        }
    }

    boolean isRun = true;
    Thread checkResultThread = new Thread(this);


    @Override
    public void run() {
        YfkjDataAdapter.YfkjMeasurementBean bean = null;
        while (bean == null && isRun) {
            // 每隔3秒获取一次数据
            SystemClock.sleep(3000);
            bean = yfkjDataAdapter.getReadData();
        }
        BleClient.instance().getClassicClient().close();
        BleClient.instance().disconnect(BleClient.instance().popStagingDevice());
        // 获取到数据之后跳转到结果展示页
        if (bean != null && isRun) {
            BPOperaterReminder2.this._mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isRun) {
                        start(new BPOperaterReminderResult());
                    }
                }
            });
        }
    }
}
