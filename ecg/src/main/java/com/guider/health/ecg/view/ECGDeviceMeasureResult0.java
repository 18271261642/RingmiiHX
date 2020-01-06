package com.guider.health.ecg.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.guider.health.common.cache.MeasureDataUploader;
import com.guider.health.common.core.Config;
import com.guider.health.common.core.HearRate;
import com.guider.health.common.core.Judgement;
import com.guider.health.common.core.ParamHealthRangeAnlysis;
import com.guider.health.common.device.standard.Constant;
import com.guider.health.common.device.standard.StandardCallback;
import com.guider.health.ecg.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by haix on 2019/6/12.
 */

public class ECGDeviceMeasureResult0 extends ECGFragment {

    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.ecg_device_measure_result0, container, false);
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
                pop();

            }
        });

        // 查看心电图
        view.findViewById(R.id.loog_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(_mActivity , SixLead.class));
            }
        });

        view.findViewById(R.id.ecg_result_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startWithPop(new ECGDeviceMeasureResult());
            }
        });
        loadLocalStandard();
        setStandardUi();
        HearRate.getInstance().startStandardRun(new StandardCallback() {
            @Override
            public void onResult(boolean isFinish) {
                if (isFinish) {
                    setStandardUi();
                }
            }
        });

        MeasureDataUploader.getInstance(_mActivity).uploadHeartBpm(
                Integer.valueOf(HearRate.getInstance().getHeartRate())
        );

        showDialog();
        final ECGImageView imageView = view.findViewById(R.id.ecg_img);
        imageView.setOnSaveFinishCall(new ECGImageView.OnSaveFinishCall() {
            @Override
            public void onSaveFinish() {
                String path = imageView.getFilePath();
                MeasureDataUploader.getInstance(_mActivity).uploadHeartState(
                        HearRate.getInstance().getDeviceAddress(),
                        HearRate.getInstance().getDiaDescribe(),
                        HearRate.getInstance().getHealthLight(),
                        HearRate.getInstance().getHealthLightOriginal(),
                        HearRate.getInstance().getHeartRate(),
                        HearRate.getInstance().getHeartRateLight(),
                        HearRate.getInstance().getLFHF(),
                        HearRate.getInstance().getStr_SDNN(),
                        HearRate.getInstance().getPervousSystemBalanceLight(),
                        HearRate.getInstance().getPNN50(),
                        HearRate.getInstance().getSDNN(),
                        HearRate.getInstance().getNN50(),
                        HearRate.getInstance().getPredictedSymptoms(),
                        HearRate.getInstance().getStressLight(),
                        path
                );
                hideDialog();
            }
        });
        imageView.saveImage();

    }

    private void setStandardUi() {
        if (this.isDetached()) {
            return;
        }
        TextView heart = view.findViewById(R.id.heart);
        TextView xinlvjiankang = view.findViewById(R.id.xinlvjiankang);
        TextView yalizhishu = view.findViewById(R.id.yalizhishu);
        TextView pilaozhishu = view.findViewById(R.id.pilaozhishu);
        View p_xinlvjiankang = view.findViewById(R.id.tag_xinlvjiankang_p);
        View p_yalizhishu = view.findViewById(R.id.tag_yalizhishu_p);
        View p_pilaozhishu = view.findViewById(R.id.tag_pilaozhishu_p);

        View tag_xinlvjiankang = view.findViewById(R.id.tag_xinglvjiankang);
        View tag_yalizhishu = view.findViewById(R.id.tag_yalizhishu);
        View tag_pilaozhishu = view.findViewById(R.id.tag_pilaozhishu);

        View tag_heart = view.findViewById(R.id.pointer);

        heart.setText(HearRate.getInstance().getHeartRate());
        HearRate instance = HearRate.getInstance();

        String str_sdnn = instance.getStr_SDNN();
        String str_lfhf = instance.getStr_LFHF();
        String str_pnn50 = instance.getStr_PNN50();

        // 设置文字
        heart.setText(instance.getHeartRate());
        xinlvjiankang.setText("心率健康:" + str_pnn50);
        yalizhishu.setText("压力指数:" + str_sdnn);
        pilaozhishu.setText("疲劳指数:" + str_lfhf);

        // 设置心率指针
        Float myHeart = Float.valueOf(instance.getHeartRate());
        float v = myHeart / 110f;
        v = v > 1.0f ? 1 : v;
        tag_heart.setRotation(v * 270);

        FrameLayout.LayoutParams params = null;
        // 设置压力指数
        switch (str_sdnn) {
            case Constant.HEALTHRANGE_SDNN_NORMAL:
                params = setOffset(true, (FrameLayout.LayoutParams) tag_yalizhishu.getLayoutParams());
                break;
            case Constant.HEALTHRANGE_SDNN_MILD:
                break;
            case Constant.HEALTHRANGE_SDNN_MEDIUM:
                params = setOffset(false , (FrameLayout.LayoutParams) tag_yalizhishu.getLayoutParams());
                break;
        }
        if (params != null) {
            tag_yalizhishu.setLayoutParams(params);
        }
        params = null;
        // 设置疲劳指数
        switch (str_lfhf) {
            case Constant.HEALTHRANGE_LFHF_NORMAL:
                params = setOffset(true , (FrameLayout.LayoutParams) tag_pilaozhishu.getLayoutParams());
                break;
            case Constant.HEALTHRANGE_LFHF_MILD:
                params = setOffset(true , (FrameLayout.LayoutParams) tag_pilaozhishu.getLayoutParams());
                break;
            case Constant.HEALTHRANGE_LFHF_MEDIUM:
                params = setOffset(false , (FrameLayout.LayoutParams) tag_pilaozhishu.getLayoutParams());
                break;
        }
        if (params != null) {
            tag_pilaozhishu.setLayoutParams(params);
        }
        params = null;
        // 设置心率健康
        switch (str_pnn50) {
            case Constant.HEALTHRANGE_PNN50_NORMAL:
                params = setOffset(true , (FrameLayout.LayoutParams) tag_xinlvjiankang.getLayoutParams());
                break;
            case Constant.HEALTHRANGE_PNN50_MILD:
                break;
            case Constant.HEALTHRANGE_PNN50_MEDIUM:
                params = setOffset(false , (FrameLayout.LayoutParams) tag_xinlvjiankang.getLayoutParams());
                break;
        }
        if (params != null) {
            tag_xinlvjiankang.setLayoutParams(params);
        }
    }

    private FrameLayout.LayoutParams setOffset(boolean isLeft , FrameLayout.LayoutParams layoutParams) {
        if (isLeft) {
            layoutParams.setMarginEnd((int) _mActivity.getResources().getDimension(R.dimen.dp_130));
        } else {
            layoutParams.setMargins((int) _mActivity.getResources().getDimension(R.dimen.dp_130) , 0 , 0 , 0);
        }
        return layoutParams;
    }

    private void loadLocalStandard() {
        if (ECGDeviceMeasureResult0.this.isDetached()) {
            return;
        }
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
            //压力指数
            HearRate.getInstance().setStr_SDNN(results.get(2));
            //疲劳指数
            HearRate.getInstance().setStr_LFHF(results.get(3));
        }
    }

}
