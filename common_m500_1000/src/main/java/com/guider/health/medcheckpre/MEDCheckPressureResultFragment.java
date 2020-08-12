package com.guider.health.medcheckpre;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.guider.health.all.R;
import com.guider.health.common.cache.MeasureDataUploader;
import com.guider.health.common.core.BaseFragment;
import com.guider.health.common.core.Config;
import com.guider.health.common.core.Judgement;
import com.guider.health.common.core.MEDCHECKPressure;
import com.guider.health.common.core.MyUtils;
import com.guider.health.common.core.ParamHealthRangeAnlysis;
import com.guider.health.common.core.RouterPathManager;
import com.guider.health.common.core.UserManager;
import com.guider.health.common.device.standard.StandardCallback;

import java.util.ArrayList;
import java.util.List;

import me.yokeyword.fragmentation.ISupportFragment;

//MEDCheck 血压结果页
public class MEDCheckPressureResultFragment extends BaseFragment {

    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_fragment_medcheck_pre_result, container,
                false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initHeadView();
        initBodyView();
    }

    @SuppressLint("SetTextI18n")
    private void initBodyView() {
        initData();
        // 进入结果统计页
        view.findViewById(R.id.bp_result_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!MyUtils.isNormalClickTime()) {
                    return;
                }
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

    @SuppressLint("SetTextI18n")
    private void initData() {
        int dbp = Integer.parseInt(MEDCHECKPressure.getMEDCHECKPressureInstance().getDbp());
        int sbp = Integer.parseInt(MEDCHECKPressure.getMEDCHECKPressureInstance().getSbp());

        List<ParamHealthRangeAnlysis> list = new ArrayList<>();
        ParamHealthRangeAnlysis paramHealthRangeAnlysis1 = new ParamHealthRangeAnlysis();
        paramHealthRangeAnlysis1.setType(ParamHealthRangeAnlysis.BLOODPRESSURE);
        paramHealthRangeAnlysis1.setValue1(sbp);
        paramHealthRangeAnlysis1.setValue2(dbp);
        String birth = UserManager.getInstance().getBirth();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(birth.substring(0, 4));
        stringBuilder.append(birth.substring(5, 7));
        stringBuilder.append(birth.substring(8, 10));
        paramHealthRangeAnlysis1.setYear(Integer.parseInt(stringBuilder.toString()));
        list.add(paramHealthRangeAnlysis1);
        List<String> results = Judgement.healthDataAnlysis(list);
        if (results.size() != 0) {
            MEDCHECKPressure.getMEDCHECKPressureInstance().setCardShowStr(results.get(0));
        }
        ((TextView) view.findViewById(R.id.bp_press1)).setText(sbp + "");
        ((TextView) view.findViewById(R.id.bp_press2)).setText(dbp + "");
        ((TextView) view.findViewById(R.id.bp_result)).setText(sbp + "/" + dbp);
        MEDCHECKPressure.getMEDCHECKPressureInstance().startStandardRun(new StandardCallback() {
            @Override
            public void onResult(boolean isFinish) {
                if (MEDCheckPressureResultFragment.this.isDetached()) {
                    return;
                }
                if (isFinish) {
                    if ("理想血压".equals(
                            MEDCHECKPressure.getMEDCHECKPressureInstance().getCardShowStr())) {
                        view.findViewById(R.id.bp_heart5).setVisibility(View.VISIBLE);
                    } else {
                        view.findViewById(R.id.bp_heart6).setVisibility(View.VISIBLE);
                    }
                } else {
                    setLocal();
                }
            }
        });
        MeasureDataUploader.getInstance(_mActivity).uploadBP(
                MEDCHECKPressure.getMEDCHECKPressureInstance().getDeviceAddress(),
                MEDCHECKPressure.getMEDCHECKPressureInstance().getDbp(),
                MEDCHECKPressure.getMEDCHECKPressureInstance().getSbp(),
                MEDCHECKPressure.getMEDCHECKPressureInstance().getHeart()
        );
        MeasureDataUploader.getInstance(_mActivity).uploadHeartBpm(
                Integer.parseInt(MEDCHECKPressure.getMEDCHECKPressureInstance().getHeart())
        );
    }

    private void setLocal() {
        List<ParamHealthRangeAnlysis> list = new ArrayList<>();
        ParamHealthRangeAnlysis paramHealthRangeAnlysis1 = new ParamHealthRangeAnlysis();
        paramHealthRangeAnlysis1.setType(ParamHealthRangeAnlysis.BLOODPRESSURE);
        paramHealthRangeAnlysis1.setValue1(MEDCHECKPressure.getMEDCHECKPressureInstance().getSbp());
        paramHealthRangeAnlysis1.setValue2(MEDCHECKPressure.getMEDCHECKPressureInstance().getDbp());
        String birth = UserManager.getInstance().getBirth();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(birth.substring(0, 4));
        paramHealthRangeAnlysis1.setYear(Integer.parseInt(stringBuilder.toString()));
        list.add(paramHealthRangeAnlysis1);
        List<String> results = Judgement.healthDataAnlysis(list);
        if (results != null) {
            MEDCHECKPressure.getMEDCHECKPressureInstance().setCardShowStr(results.get(0));
        }
        if ("理想血压".equals(MEDCHECKPressure.getMEDCHECKPressureInstance().getCardShowStr())) {
            view.findViewById(R.id.bp_heart5).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.bp_heart6).setVisibility(View.VISIBLE);
        }

    }

    private void initHeadView() {
        // 返回Home处理
        setHomeEvent(view.findViewById(R.id.home), Config.HOME_DEVICE);

        // title显式
        ((TextView) view.findViewById(R.id.title)).setText(getResources()
                .getString(R.string.medcheck_pre_redult_title));

        // 返回上一页
        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop();
            }
        });
    }

}
