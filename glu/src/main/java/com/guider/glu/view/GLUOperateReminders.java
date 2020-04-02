package com.guider.glu.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.guider.glu.R;
import com.guider.glu.model.BodyIndex;
import com.guider.glu.presenter.GLUServiceManager;
import com.guider.health.common.cache.MeasureDataUploader;
import com.guider.health.common.core.Config;
import com.guider.health.common.core.Glucose;
import com.guider.health.common.core.HealthRange;
import com.guider.health.common.core.RouterPathManager;
import com.guider.health.common.device.DeviceInit;
import com.guider.health.common.utils.SkipClick;

import me.yokeyword.fragmentation.ISupportFragment;

/**
 * 开机提醒
 *  -> GLUDeviceConnect
 * Created by haix on 2019/6/21.
 */

public class GLUOperateReminders extends GLUFragment {

    private View view;
    private final static int REQUEST_CODE = 12;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.glu_operate_reminders, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        GLUServiceManager.getInstance().setViewObject(this);

        setHomeEvent(view.findViewById(R.id.home), Config.HOME_DEVICE);
        ((TextView) view.findViewById(R.id.title)).setText(getResources().getString(R.string.op_tips));
        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop();
            }
        });
        view.findViewById(R.id.skip).setVisibility(View.VISIBLE);
        view.findViewById(R.id.skip).setOnClickListener(new SkipClick(this , DeviceInit.DEV_GLU));

        view.findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(_mActivity, GLUConnectAndMeassureActivity.class);
                intent.putExtra("body", BodyIndex.getInstance());
                intent.putExtra("HealthRange", HealthRange.getInstance());
                intent.putExtra("footTime", Glucose.getInstance().getFoodTime());
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == TONEXT) {

            Glucose heart = data.getParcelableExtra("result");
            Glucose.getInstance().setGlucose(heart.getGlucose());
            Glucose.getInstance().setOxygenSaturation(heart.getOxygenSaturation());
            Glucose.getInstance().setHemoglobin(heart.getHemoglobin());
            Glucose.getInstance().setSpeed(heart.getSpeed());
            Glucose.getInstance().setPulse(heart.getPulse());
            Glucose.getInstance().setBatteryLevel(heart.getBatteryLevel());
            Glucose.getInstance().setDeviceAddress(heart.getDeviceAddress());
            Glucose.getInstance().setIndexOxygen(heart.getIndexOxygen());
            Glucose.getInstance().setIndexGlucose(heart.getIndexGlucose());

            Glucose.getInstance().set_glucose(heart.get_glucose());
            Glucose.getInstance().set_hemoglobin(heart.get_glucose());
            Glucose.getInstance().set_oxygenSaturation(heart.get_oxygenSaturation());
            Glucose.getInstance().set_pulse(heart.get_pulse());
            Glucose.getInstance().set_speed(heart.get_speed());

            Glucose.getInstance().setStr_glucose(heart.getStr_glucose());
            Glucose.getInstance().setStr_hemoglobin(heart.getStr_hemoglobin());
            Glucose.getInstance().setStr_oxygenSaturation(heart.getStr_oxygenSaturation());
            Glucose.getInstance().setStr_pulse(heart.getStr_pulse());
            Glucose.getInstance().setStr_speed(heart.getStr_speed());

            String bsTime = "RANDOM";
            if (Glucose.getInstance().getFoodTime() == 0) {
                //空腹
                bsTime = "FPG";
            }
            if (Glucose.getInstance().getFoodTime() >= 1) {
                bsTime = "TWOHPPG";
            }
            MeasureDataUploader.getInstance(_mActivity).uploadBloodSugar(
                    Glucose.getInstance().getDeviceAddress(),
                    Float.valueOf(Glucose.getInstance().getSpeed()),
                    (float) Glucose.getInstance().getGlucose(),
                    Float.valueOf(Glucose.getInstance().getHemoglobin()),
                    bsTime
            );
            MeasureDataUploader.getInstance(_mActivity).uploadBloodOxygen(
                    Glucose.getInstance().getDeviceAddress(),
                    Integer.parseInt(Glucose.getInstance().getOxygenSaturation()),
                    Integer.parseInt(Glucose.getInstance().getPulse())
            );
            MeasureDataUploader.getInstance(_mActivity).uploadHeartBpm(
                    Integer.valueOf(Glucose.getInstance().getPulse())
            );

            if (Config.mapX.get("glu")) {


                Config.mapX.put("glu", false);

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
//            Log.i("BaseDataSave On Server", "----测量完毕-----");
//            showDialog("正在同步数据...");
//            Glucose.getInstance().startStandardRun(new StandardCallback() {
//                @Override
//                public void onResult(boolean isFinish) {
//
//                }
//            });

        } else if (requestCode == REQUEST_CODE && resultCode == TOHOME) {
            //home返回
            try {
                popTo(Class.forName(Config.HOME_DEVICE), false);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_CODE && resultCode == TOBACK) {
            //点返回
        } else if (requestCode == 992) {

            try {
                popTo(Class.forName(Config.HOME_DEVICE), false);
                start((ISupportFragment) Class.forName(Config.END_FRAGMENT).newInstance());
            } catch (Exception e) {

            }
        } else if (resultCode == SkipClick.CODE_SKIP) {
            new SkipClick(this , DeviceInit.DEV_GLU).skip();
        }

    }


}
