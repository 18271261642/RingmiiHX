package com.guider.health.ecg.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.guider.health.common.core.Config;
import com.guider.health.common.core.HealthRange;
import com.guider.health.common.core.HearRate;
import com.guider.health.common.device.DeviceInit;
import com.guider.health.common.utils.SkipClick;
import com.guider.health.ecg.R;

import java.util.HashMap;

/**
 * Created by haix on 2019/6/12.
 * 6导测量提示页
 */


public class ECGDeviceOperateNest extends ECGFragment {


    private View view;
    private final static int REQUEST_CODE = 11;


    public static HashMap<String, String> map = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.ecg_device_operate_nest, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHomeEvent(view.findViewById(R.id.home), Config.HOME_DEVICE);
        ((TextView) view.findViewById(R.id.title)).setText(_mActivity.getResources().getString(R.string.ecg_test_op));
        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop();

            }
        });

        view.findViewById(R.id.skip).setVisibility(View.VISIBLE);
        view.findViewById(R.id.skip).setOnClickListener(new SkipClick(this , DeviceInit.DEV_ECG_6));

        map.put("xxx", "yyy");

        view.findViewById(R.id.ecg_bt_start_measure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(_mActivity, ECGConnectAndMeassureActivity.class);
                intent.putExtra("HealthRange", HealthRange.getInstance());
                startActivityForResult(intent, REQUEST_CODE);

            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("sssssssssrr", "result了  " + resultCode);
        if (requestCode == REQUEST_CODE && resultCode == TONEXT) {
            HearRate hearRate = data.getParcelableExtra("result");
            HearRate.getInstance().setHeartRate(hearRate.getHeartRate());
            HearRate.getInstance().setHeartRateLight(hearRate.getHeartRateLight());
            HearRate.getInstance().setHealthLight(hearRate.getHealthLight());
            HearRate.getInstance().setHealthLightOriginal(hearRate.getHealthLightOriginal());
            HearRate.getInstance().setDiaDescribe(hearRate.getDiaDescribe());
            HearRate.getInstance().setSDNN(hearRate.getSDNN());
            HearRate.getInstance().setLFHF(hearRate.getLFHF());
            HearRate.getInstance().setNN50(hearRate.getNN50());
            HearRate.getInstance().setPNN50(hearRate.getPNN50());
            HearRate.getInstance().setPredictedSymptoms(hearRate.getPredictedSymptoms());
            HearRate.getInstance().setPervousSystemBalanceLight(hearRate.getPervousSystemBalanceLight());
            HearRate.getInstance().setStressLight(hearRate.getStressLight());
            HearRate.getInstance().setDeviceAddress(hearRate.getDeviceAddress());

            start(new ECGDeviceMeasureResult0());
            hideDialog();
        }else if (requestCode == REQUEST_CODE && resultCode == TOHOME){
            //home返回
            try {
                popTo(Class.forName(Config.HOME_DEVICE), false);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }else if(requestCode == REQUEST_CODE && resultCode == TOBACK){
            Toast.makeText(_mActivity, _mActivity.getResources().getString(R.string.test_error), Toast.LENGTH_LONG).show();
            //点返回
        } else if (resultCode == SkipClick.CODE_SKIP) {
            Log.i("sssssssssrr", "关闭弹窗了");
            showDialog();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    hideDialog();
                    new SkipClick(ECGDeviceOperateNest.this , DeviceInit.DEV_ECG_6).skip();
                }
            }, 1000);
        }

    }

    @Override
    public Context getViewContext() {
        return _mActivity;
    }




}
