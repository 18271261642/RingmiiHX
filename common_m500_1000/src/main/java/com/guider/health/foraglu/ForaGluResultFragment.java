package com.guider.health.foraglu;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.guider.health.all.R;
import com.guider.health.common.cache.MeasureDataUploader;
import com.guider.health.common.core.BaseFragment;
import com.guider.health.common.core.Config;
import com.guider.health.common.core.ForaGlucose;
import com.guider.health.common.core.Glucose;
import com.guider.health.common.core.MyUtils;
import com.guider.health.common.core.RouterPathManager;
import com.guider.health.common.device.DeviceInit;
import com.guider.health.common.device.IUnit;
import com.guider.health.common.utils.SkipClick;
import com.guider.health.common.utils.UnitUtil;

import me.yokeyword.fragmentation.ISupportFragment;

/**
 * 福尔血糖测量提示界面
 */
public class ForaGluResultFragment extends BaseFragment {
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_fragment_fora_glu_result, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initHeadView();
        initBodyView();
    }

    private void initHeadView() {
        // 返回Home处理
        setHomeEvent(view.findViewById(R.id.home), Config.HOME_DEVICE);

        // title显式
        ((TextView) view.findViewById(R.id.title)).setText(getResources().getString(R.string.fora_glu_redult_title));

        // 是否能跳过
        view.findViewById(R.id.skip).setVisibility(View.GONE);
        view.findViewById(R.id.skip).setOnClickListener(new SkipClick(this, DeviceInit.DEV_FORA_GLU));

        // 返回上一页
        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop();
            }
        });
    }

    private void initBodyView() {
        // 结果展示
        TextView tvResult = view.findViewById(R.id.tv_fora_glu_reslut);
        IUnit iUnit = UnitUtil.getIUnit(_mActivity);
        double value = iUnit.getGluShowValue(ForaGlucose.getForaGluInstance().getGlucose(), 2);
        tvResult.setText(String.format(getResources().getString(R.string.fora_glu_reult_format), value) + iUnit.getGluUnit());
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
                bsTime // 默认随机
        );
        // 开始测量
        view.findViewById(R.id.btn_fora_glu_next).setOnClickListener(new View.OnClickListener() {
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
}
