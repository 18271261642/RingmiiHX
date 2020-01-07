package com.guider.health.bp.view.avebp;

import android.os.Bundle;
import android.support.annotation.Nullable;
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

/**
 * 这是VAE-2000的血压仪
 * Created by haix on 2019/6/25.
 */

public class BPFirstOperaterReminder extends BPFragment {

    private View view;

    private long startTime;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bp_ave_reminder_first, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null){
            return;
        }
        startTime = System.currentTimeMillis() - 1000 * 60 * 5;
        initView();
    }

    private void initView() {
        setHomeEvent(view.findViewById(R.id.home), Config.HOME_DEVICE);
        view.findViewById(R.id.skip).setVisibility(View.VISIBLE);
        view.findViewById(R.id.skip).setOnClickListener(new SkipClick(this , DeviceInit.DEV_BP_AVE));
        ((TextView) view.findViewById(R.id.title)).setText("操作指南");
        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop();
            }
        });
        TipTitleView tips = view.findViewById(R.id.tips);
        tips.setTips("动脉硬化测量","操作指南", "蓝牙连接", "测量结果");
        tips.toTip(1);

        view.findViewById(R.id.bp_vae_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BPOperaterReminder1 bpOperaterReminder1 = new BPOperaterReminder1();
                Bundle bundle = new Bundle();
                bundle.putLong("startTime", startTime);
                bpOperaterReminder1.setArguments(bundle);
                start(bpOperaterReminder1);
            }
        });

    }

}
