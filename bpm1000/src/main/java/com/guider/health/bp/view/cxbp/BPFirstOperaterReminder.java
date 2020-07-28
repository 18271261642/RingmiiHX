package com.guider.health.bp.view.cxbp;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.guider.health.bp.R;
import com.guider.health.bp.view.BPFragment;
import com.guider.health.common.core.Config;
import com.guider.health.common.device.DeviceInit;
import com.guider.health.common.utils.SkipClick;

/**
 * 这是连线款的血压仪
 * Created by haix on 2019/6/25.
 */

public class BPFirstOperaterReminder extends BPFragment {

    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bp_cx_operater_next, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            return;
        }
        setHomeEvent(view.findViewById(R.id.home), Config.HOME_DEVICE);
        ((TextView) view.findViewById(R.id.title)).setText("操作指南");
        view.findViewById(R.id.skip).setVisibility(View.VISIBLE);
        view.findViewById(R.id.skip).setOnClickListener(new SkipClick(this , DeviceInit.DEV_BP_CX));
        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop();
            }
        });
        view.findViewById(R.id.bp_result_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startWithPop(new BPMeasureIng());
            }
        });
    }

}
