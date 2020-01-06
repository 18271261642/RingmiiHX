package com.guider.health.ecg.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.guider.health.common.core.Config;
import com.guider.health.common.core.HearRate;
import com.guider.health.common.core.Judgement;
import com.guider.health.common.core.ParamHealthRangeAnlysis;
import com.guider.health.common.core.RouterPathManager;
import com.guider.health.common.device.Unit;
import com.guider.health.common.device.standard.StandardCallback;
import com.guider.health.ecg.R;

import java.util.ArrayList;
import java.util.List;

import me.yokeyword.fragmentation.ISupportFragment;

/**
 * Created by haix on 2019/6/12.
 */

public class ECGDeviceMeasureResult extends ECGFragment {

    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.ecg_device_measure_result, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            return;
        }

        setHomeEvent(view.findViewById(R.id.home), Config.HOME_DEVICE);
        ((TextView) view.findViewById(R.id.title)).setText("测量结果");
        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popTo(ECGDeviceOperate.class, false);

            }
        });

        double aDouble = Double.valueOf(HearRate.getInstance().getHeartRate());
        ((TextView) view.findViewById(R.id.heart)).setText("心率: " + (int)aDouble + " " + new Unit().heart);

        HearRate.getInstance().startStandardRun(new StandardCallback() {
            @Override
            public void onResult(boolean isFinish) {
                if (ECGDeviceMeasureResult.this.isDetached()) {
                    return;
                }
                if (isFinish) {
                    setServer();
                } else {
                    setLocal();
                }
            }
        });

        view.findViewById(R.id.ecg_result_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (RouterPathManager.Devices.size() > 0) {
                    String fragmentPath = RouterPathManager.Devices.remove();

                    try {
                        popTo(Class.forName(Config.HOME_DEVICE), false);

                        start((ISupportFragment) Class.forName(fragmentPath).newInstance());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {

                    try {

                        popTo(Class.forName(Config.HOME_DEVICE), false);
                        start((ISupportFragment) Class.forName(Config.END_FRAGMENT).newInstance());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });


    }

    private void setServer() {
        //心率健康
        ((TextView) view.findViewById(R.id.heart_rate)).setText("心率健康: " + HearRate.getInstance().getStr_PNN50());
        //压力指数
        ((TextView) view.findViewById(R.id.pressure_index)).setText("压力指数: " + HearRate.getInstance().getStr_SDNN());
        //疲劳指数
        ((TextView) view.findViewById(R.id.fatigue_index)).setText("疲劳指数: " + HearRate.getInstance().getStr_LFHF());
    }

    private void setLocal() {
        List<ParamHealthRangeAnlysis> list = new ArrayList<>();

        ParamHealthRangeAnlysis paramHealthRangeAnlysis1 = new ParamHealthRangeAnlysis();
        paramHealthRangeAnlysis1.setType(ParamHealthRangeAnlysis.HEARTBEAT);
        paramHealthRangeAnlysis1.setValue1(HearRate.getInstance().getHeartRate());

        list.add(paramHealthRangeAnlysis1);

        ParamHealthRangeAnlysis paramHealthRangeAnlysis2 = new ParamHealthRangeAnlysis();
        paramHealthRangeAnlysis2.setType(ParamHealthRangeAnlysis.JKZS);
        paramHealthRangeAnlysis2.setValue1(HearRate.getInstance().getPNN50());//0.04762

        list.add(paramHealthRangeAnlysis2);


        ParamHealthRangeAnlysis paramHealthRangeAnlysis3 = new ParamHealthRangeAnlysis();
        paramHealthRangeAnlysis3.setType(ParamHealthRangeAnlysis.YLZS);
        paramHealthRangeAnlysis3.setValue1(HearRate.getInstance().getSDNN());//32.756

        list.add(paramHealthRangeAnlysis3);


        ParamHealthRangeAnlysis paramHealthRangeAnlysis4 = new ParamHealthRangeAnlysis();
        paramHealthRangeAnlysis4.setType(ParamHealthRangeAnlysis.LLZS);
        paramHealthRangeAnlysis4.setValue1(HearRate.getInstance().getLFHF());//1.6960....

        list.add(paramHealthRangeAnlysis4);


        List<String> results = Judgement.healthDataAnlysis(list);


        if (results != null && results.size() == 4) {
            //心率健康
            HearRate.getInstance().setStr_PNN50(results.get(1));
            ((TextView) view.findViewById(R.id.heart_rate)).setText("心率健康: " + results.get(1));
            //压力指数
            HearRate.getInstance().setStr_SDNN(results.get(2));
            ((TextView) view.findViewById(R.id.pressure_index)).setText("压力指数: " + results.get(2));
            //疲劳指数
            HearRate.getInstance().setStr_LFHF(results.get(3));
            ((TextView) view.findViewById(R.id.fatigue_index)).setText("疲劳指数: " + results.get(3));
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 992) {
            try {

                popTo(Class.forName(Config.HOME_DEVICE), false);
                start((ISupportFragment) Class.forName(Config.END_FRAGMENT).newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


}
