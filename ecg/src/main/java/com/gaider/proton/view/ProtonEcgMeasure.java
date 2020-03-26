package com.gaider.proton.view;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.gaider.proton.Protocol;
import com.gaider.proton.presenter.MeasurePresnter;
import com.guider.health.common.core.Config;
import com.guider.health.common.device.DeviceInit;
import com.guider.health.common.utils.SkipClick;
import com.guider.health.common.views.TipTitleView;
import com.guider.health.ecg.R;
import com.guider.health.ecg.view.ECGCompletedView;
import com.guider.health.ecg.view.ECGFragment;
import com.proton.ecgcard.algorithm.bean.AlgorithmResult;
import com.proton.ecgcard.algorithm.bean.RealECGData;
import com.proton.view.EcgRealTimeView;

public class ProtonEcgMeasure extends ECGFragment implements Protocol.IMeasureView {

    private EcgRealTimeView ecgView;
    private View tipView , llStart , vWarning , llMeasure;
    private TextView tvHeart , tvPower , tvSignal , tvTimeStr;
    private TextView tip;
    private Button btn;
    private View view;
    private ECGCompletedView tvTime;
    private Protocol.IMeasurePresenter mPresenter;

    WarningDialog warningDialog;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.proton_measure, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHomeEvent(view.findViewById(R.id.home), Config.HOME_DEVICE);
        view.findViewById(R.id.skip).setVisibility(View.VISIBLE);
        view.findViewById(R.id.skip).setOnClickListener(new SkipClick(this , DeviceInit.DEV_ECG_HD));
        ((TextView) view.findViewById(R.id.title)).setText(getResources().getString(R.string.operation_guide));
        TipTitleView tips = view.findViewById(R.id.tip_title);
        tips.setTips(getResources().getString(R.string.ecg_measurement),
                getResources().getString(R.string.input_parameters),
                getResources().getString(R.string.start_measuring),
                getResources().getString(R.string.results_display));
        tips.toTip(2);
        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.finish();
            }
        });


        warningDialog = new WarningDialog(_mActivity);
        ecgView = view.findViewById(R.id.id_ecg_view);
        tvHeart = view.findViewById(R.id.heart);
        tvPower = view.findViewById(R.id.power);
        tvSignal = view.findViewById(R.id.signal);
        tvTime = view.findViewById(R.id.time_measure);
        tvTimeStr = view.findViewById(R.id.measure_time);
        tip = view.findViewById(R.id.tip);
        btn = view.findViewById(R.id.btn);
        tipView = view.findViewById(R.id.tip_view);
        llStart = view.findViewById(R.id.ll_start);
        llMeasure = view.findViewById(R.id.ll_measure);
        vWarning = view.findViewById(R.id.warning);
        mPresenter = new MeasurePresnter();
        mPresenter.init(this);
        // 2.5秒后开始搜索设备 , 主要是让用户看清文案
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mPresenter.start();
            }
        }, 2500);
    }

    private Button showTipView(String msg , String btnStr) {
        if (llStart.getVisibility() == View.VISIBLE) {
            llStart.setVisibility(View.GONE);
        }

        tip.setText(msg);
        if (TextUtils.isEmpty(btnStr)) {
            btn.setVisibility(View.GONE);
        } else {
            btn.setVisibility(View.VISIBLE);
            btn.setText(btnStr);
        }
        tipView.setVisibility(View.VISIBLE);
        llMeasure.setVisibility(View.GONE);
        return btn;
    }

    private void hideTipView() {
        tipView.setVisibility(View.GONE);
        llMeasure.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStartSearch() {
        if (llStart.getVisibility() != View.VISIBLE) {
            showTipView(getResources().getString(R.string.searching) , null);
        }
    }

    @Override
    public void notFindDevice() {
        showTipView(getResources().getString(R.string.no_devices_found) , getResources().getString(R.string.search_again)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.start();
            }
        });
    }

    @Override
    public void onConnectFail() {
        showTipView(getResources().getString(R.string.connection_failed) , getResources().getString(R.string.reconnect)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.start();
            }
        });
    }

    @Override
    public void onTick(int time) {
        if (llStart.getVisibility() == View.VISIBLE) {
            llStart.setVisibility(View.GONE);
        }
        tvTime.setProgress(time);
        tvTimeStr.setText(time + "");
    }

    @Override
    public void onWarning() {
        if (vWarning.getVisibility() == View.VISIBLE) {
            return;
        }
        vWarning.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                vWarning.setVisibility(View.INVISIBLE);
            }
        }, 2000);
    }

    @Override
    public void onDeviceMeasure(RealECGData currentData) {
        hideTipView();
        ecgView.addEcgData(currentData.ecgData);
        if (!ecgView.isRunning()) {
            ecgView.startDrawWave();
        }
    }

    @Override
    public void hasDeviceInfo(String heart, String power, String signal) {
        tvHeart.setText(heart + "bpm");
        tvPower.setText(power + "%");
        tvSignal.setText(signal);
    }

    @Override
    public void onStatusChange(int code) {
        if (code == Protocol.CODE_HAND_GONE) {
//            showTipView("请勿将手指移开", null);
            warningDialog.show();
        } else {
            warningDialog.dismiss();
//            hideTipView();
        }
    }

    @Override
    public void onAnalysisResult(AlgorithmResult algorithmResult) {
        showDialog(getResources().getString(R.string.saving_data));
        if (ecgView.isRunning()) {
            ecgView.stopDrawWave();
        }
        mPresenter.finish();
        hideDialog();
        toNext(algorithmResult);
    }

    private void toNext(AlgorithmResult algorithmResult) {
        ProtonEcgResult protonEcgResult = new ProtonEcgResult();
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", algorithmResult);
        protonEcgResult.setArguments(bundle);
        startWithPop(protonEcgResult);
    }

    @Override
    public Activity getMyContext() {
        return _mActivity;
    }
}
