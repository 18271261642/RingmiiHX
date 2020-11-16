package com.guider.health.medcheckglu;

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
import com.guider.health.common.core.ForaGlucose;
import com.guider.health.common.core.Glucose;
import com.guider.health.common.core.MEDCHECKGlucose;
import com.guider.health.common.core.MyUtils;
import com.guider.health.common.core.RouterPathManager;
import com.guider.health.common.device.DeviceInit;
import com.guider.health.common.device.IUnit;
import com.guider.health.common.utils.SkipClick;
import com.guider.health.common.utils.UnitUtil;

import me.yokeyword.fragmentation.ISupportFragment;

//MEDCHECK血糖结果显示页面
public class MEDCHECKGluResultFragment extends BaseFragment {

    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_fragment_fora_glu_result, container,
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
        TextView tvResult = view.findViewById(R.id.tv_fora_glu_reslut);
        IUnit iUnit = UnitUtil.INSTANCE.getIUnit();
        double value = iUnit.getGluShowValue(MEDCHECKGlucose.getMEDCHECKGluInstance().getGlucose(),
                2);
        tvResult.setText(String.format(getResources().getString(R.string.medcheck_glu_reult_format),
                value) + iUnit.getGluUnit());
        // 默认随机
        String bsTime = "RANDOM";
        if (Glucose.getInstance().getFoodTime() == 0) {
            //空腹
            bsTime = "FPG";
        }
        if (Glucose.getInstance().getFoodTime() >= 1) {
            bsTime = "TWOHPPG";
        }
        MeasureDataUploader.getInstance(_mActivity).uploadBloodSugar(
                MyUtils.getMacAddress(),
                0,
                (float) ForaGlucose.getForaGluInstance().getGlucose(),
                0,
                bsTime
        );
        // 进入结果统计页
        view.findViewById(R.id.btn_fora_glu_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!MyUtils.isNormalClickTime()){
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

    private void initHeadView() {
        // 返回Home处理
        setHomeEvent(view.findViewById(R.id.home), Config.HOME_DEVICE);

        // title显式
        ((TextView) view.findViewById(R.id.title)).setText(getResources()
                .getString(R.string.medcheck_glu_redult_title));
        ((TextView) view.findViewById(R.id.tv_test_type)).setText(R.string.blood_sugar);

        // 是否能跳过
        view.findViewById(R.id.skip).setVisibility(View.GONE);
        view.findViewById(R.id.skip).setOnClickListener(
                new SkipClick(this , DeviceInit.DEV_MEDCHECK_GLU));

        // 返回上一页
        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop();
            }
        });
    }
}
