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
import com.guider.health.common.device.DeviceInit;
import com.guider.health.common.utils.SkipClick;

/**
 * 这是动脉硬化款的血压仪四川云峰的
 * Created by haix on 2019/6/25.
 */

public class BPFirstOperaterReminder extends BPFragment {

    private View view;
    private Button research;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bp_yf_device_reminder, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            return;
        }
        initView();

    }

    private void initView() {
        setHomeEvent(view.findViewById(R.id.home), Config.HOME_DEVICE);
        view.findViewById(R.id.skip).setVisibility(View.VISIBLE);
        view.findViewById(R.id.skip).setOnClickListener(new SkipClick(this , DeviceInit.DEV_BP_YF));
        ((TextView) view.findViewById(R.id.title)).setText("开机提醒");
        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop();
            }
        });
        TipTitleView tips = view.findViewById(R.id.tips);
        tips.setTips("动脉硬化测量","开机提醒","蓝牙连接", "信息录入", "操作指南" , "测量结果");
        tips.toTip(1);

        research = view.findViewById(R.id.bp_button_n);
        research.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start(new BPOperaterReminder0());
            }
        });
    }

}