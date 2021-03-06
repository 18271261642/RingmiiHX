package com.guider.health.bp.view.mbb2.BP88B;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.guider.health.bp.R;
import com.guider.health.bp.view.BPFragment;
import com.guider.health.common.cache.MeasureDataUploader;
import com.guider.health.common.core.Config;
import com.guider.health.common.core.HeartPressMbb_88;
import com.guider.health.common.core.Judgement;
import com.guider.health.common.core.ParamHealthRangeAnlysis;
import com.guider.health.common.core.RouterPathManager;
import com.guider.health.common.core.UserManager;
import com.guider.health.common.device.standard.StandardCallback;

import java.util.ArrayList;
import java.util.List;

import me.yokeyword.fragmentation.ISupportFragment;


/**
 * Created by haix on 2019/6/18.
 */

public class BPMeasureResult extends BPFragment {

    private View view;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bp_mbb88_meassure_result, container, false);

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
                try {
                    popTo(Class.forName(Config.BP_FRAGMENT), false);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        int dbp = Integer.parseInt(HeartPressMbb_88.getInstance().getDbp());
        int sbp = Integer.parseInt(HeartPressMbb_88.getInstance().getSbp());

        setStandard();

        ((TextView) view.findViewById(R.id.bp_press1)).setText(sbp+"");
        ((TextView) view.findViewById(R.id.bp_press2)).setText(dbp+"");
        ((TextView) view.findViewById(R.id.bp_result)).setText(HeartPressMbb_88.getInstance().getSbp() + "/" + HeartPressMbb_88.getInstance().getDbp());


        view.findViewById(R.id.bp_result_next).setOnClickListener(new View.OnClickListener() {
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

    private void setStandard() {
        HeartPressMbb_88.getInstance().startStandardRun(new StandardCallback() {
            @Override
            public void onResult(boolean isFinish) {
                if (BPMeasureResult.this.isDetached()) {
                    return;
                }
                if (isFinish) {
                    if ("理想血压".equals(HeartPressMbb_88.getInstance().getCardShowStr())){
                        view.findViewById(R.id.bp_heart5).setVisibility(View.VISIBLE);
                    }else{
                        view.findViewById(R.id.bp_heart6).setVisibility(View.VISIBLE);
                    }
                } else {
                    setLocal();
                }
            }
        });
        MeasureDataUploader.getInstance(_mActivity).uploadBP(
                HeartPressMbb_88.getInstance().getDeviceAddress(),
                HeartPressMbb_88.getInstance().getDbp(),
                HeartPressMbb_88.getInstance().getSbp(),
                HeartPressMbb_88.getInstance().getHeart()
        );
        MeasureDataUploader.getInstance(_mActivity).uploadHeartBpm(
                Integer.valueOf(HeartPressMbb_88.getInstance().getHeart())
        );
    }

    private void setLocal() {
        List<ParamHealthRangeAnlysis> list = new ArrayList<>();

        ParamHealthRangeAnlysis paramHealthRangeAnlysis1 = new ParamHealthRangeAnlysis();
        paramHealthRangeAnlysis1.setType(ParamHealthRangeAnlysis.BLOODPRESSURE);
        paramHealthRangeAnlysis1.setValue1(HeartPressMbb_88.getInstance().getSbp());
        paramHealthRangeAnlysis1.setValue2(HeartPressMbb_88.getInstance().getDbp());
        String birth = UserManager.getInstance().getBirth();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(birth.substring(0, 4));
        paramHealthRangeAnlysis1.setYear(Integer.parseInt(stringBuffer.toString()));
        list.add(paramHealthRangeAnlysis1);


        List<String> results = Judgement.healthDataAnlysis(list);
        if (results != null){
            HeartPressMbb_88.getInstance().setCardShowStr(results.get(0));
            HeartPressMbb_88.getInstance().setCardShowStr(results.get(0));
        }

        if ("理想血压".equals(HeartPressMbb_88.getInstance().getCardShowStr())){
            view.findViewById(R.id.bp_heart5).setVisibility(View.VISIBLE);
        }else{
            view.findViewById(R.id.bp_heart6).setVisibility(View.VISIBLE);
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
