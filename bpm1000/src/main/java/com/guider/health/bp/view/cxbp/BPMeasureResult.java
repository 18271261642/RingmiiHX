package com.guider.health.bp.view.cxbp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.guider.health.bp.R;
import com.guider.health.bp.view.BPFragment;
import com.guider.health.common.cache.MeasureDataUploader;
import com.guider.health.common.core.Config;
import com.guider.health.common.core.HeartPressCx;
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
        view = inflater.inflate(R.layout.bp_cx_meassure_result, container, false);

        return view;
    }

    @SuppressLint("SetTextI18n")
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
                startWithPop(new BPMeasureIng());
            }
        });


        int dbp = Integer.parseInt(HeartPressCx.getInstance().getDbp());
        int sbp = Integer.parseInt(HeartPressCx.getInstance().getSbp());

        ((TextView) view.findViewById(R.id.bp_press1)).setText(sbp+"");
        ((TextView) view.findViewById(R.id.bp_press2)).setText(dbp+"");
        ((TextView) view.findViewById(R.id.bp_result)).setText(HeartPressCx.getInstance().getSbp()
                + "/" + HeartPressCx.getInstance().getDbp());


        HeartPressCx.getInstance().startStandardRun(new StandardCallback() {
            @Override
            public void onResult(boolean isFinish) {
                if (BPMeasureResult.this.isDetached()) {
                    return;
                }
                if (isFinish) {
                    if ("理想血压".equals(HeartPressCx.getInstance().getCardShowStr())){
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
                HeartPressCx.getInstance().getDeviceAddress(),
                HeartPressCx.getInstance().getDbp(),
                HeartPressCx.getInstance().getSbp(),
                HeartPressCx.getInstance().getHeart()
        );
        MeasureDataUploader.getInstance(_mActivity).uploadHeartBpm(
                Integer.parseInt(HeartPressCx.getInstance().getHeart())
        );
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

    private void setLocal() {
        List<ParamHealthRangeAnlysis> list = new ArrayList<>();

        ParamHealthRangeAnlysis paramHealthRangeAnlysis1 = new ParamHealthRangeAnlysis();
        paramHealthRangeAnlysis1.setType(ParamHealthRangeAnlysis.BLOODPRESSURE);
        paramHealthRangeAnlysis1.setValue1(HeartPressCx.getInstance().getSbp());
        paramHealthRangeAnlysis1.setValue2(HeartPressCx.getInstance().getDbp());
        String birth = UserManager.getInstance().getBirth();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(birth.substring(0, 4));
        paramHealthRangeAnlysis1.setYear(Integer.parseInt(stringBuffer.toString()));
        list.add(paramHealthRangeAnlysis1);


        List<String> results = Judgement.healthDataAnlysis(list);
        if (results != null){
            HeartPressCx.getInstance().setCardShowStr(results.get(0));
        }

        if ("理想血压".equals(HeartPressCx.getInstance().getCardShowStr())){
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
