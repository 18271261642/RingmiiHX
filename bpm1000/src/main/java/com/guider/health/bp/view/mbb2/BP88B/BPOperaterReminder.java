package com.guider.health.bp.view.mbb2.BP88B;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.guider.health.arouter_annotation.Route;
import com.guider.health.bp.R;
import com.guider.health.bp.view.BPFragment;
import com.guider.health.common.core.Config;
import com.guider.health.common.core.RouterPathManager;
import com.guider.health.common.device.DeviceInit;
import com.guider.health.common.utils.SkipClick;

/**
 * Created by haix on 2019/6/24.
 */
@Route(path = RouterPathManager.BP_PATH)
public class BPOperaterReminder extends BPFragment {

    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bp_mbb88_operaer_reminder, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHomeEvent(view.findViewById(R.id.home), Config.HOME_DEVICE);
        ((TextView) view.findViewById(R.id.title)).setText("操作提醒");
        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop();
            }
        });

        view.findViewById(R.id.skip).setVisibility(View.VISIBLE);
        view.findViewById(R.id.skip).setOnClickListener(new SkipClick(this , DeviceInit.DEV_BP_MBB_88));

        view.findViewById(R.id.bp_button_n).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                start(new BPMeasureFragment());
            }
        });
    }
}

