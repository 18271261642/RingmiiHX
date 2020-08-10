package com.guider.glu_phone.view;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.guider.glu_phone.R;
import com.guider.glu_phone.net.NetRequest;
import com.guider.health.common.core.MyUtils;
import com.guider.health.common.core.UserManager;
import com.guider.health.common.utils.ToastUtil;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by haix on 2019/7/18.
 */

public class AttentionInfo extends GlocoseFragment{
    private GluSetFragment mGluSetFragment = new GluSetFragment();
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.attention_info, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        NetRequest.getInstance().getNonbsSet(new WeakReference<>(_mActivity),
                (code, result) -> {
            if (code == 0){
                //成功
                mGluSetFragment.setFlagSetting(true);
            }
        });

        NetRequest.getInstance().getUserInfo(new WeakReference<>(_mActivity),
                (code, result) -> {
            if (code == 0){
                // 成功
                mGluSetFragment.setFlagInfo(true);

                // 身高体重非法直接跳转到
                if (UserManager.getInstance().getHeight() <= 0 ||
                    UserManager.getInstance().getWeight() <= 0) {
                    start(mGluSetFragment);
                }
            }
        });

        ((TextView)view.findViewById(R.id.head_title)).setText(R.string.measure_1_zhuyishixiang);
        view.findViewById(R.id.head_back).setOnClickListener(view -> {
            FragmentManager fm = AttentionInfo.this.getFragmentManager();
            List<Fragment> fs = fm.getFragments();
            if (fs == null || fs.size() <= 1)
                _mActivity.finish();
            else
                pop();
        });

        view.findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!MyUtils.isNormalClickTime()){
                    return;
                }
                if (UserManager.getInstance().getHeight() <= 0 ||
                        UserManager.getInstance().getWeight() <= 0) {
                    ToastUtil.showLong(_mActivity, getResources().getString(R.string.tips_setting));
                    return;
                }
                start(new ChooseTime());
            }
        });

        Button btnGluSet = view.findViewById(R.id.btn_glu_set);
        btnGluSet.setVisibility(View.VISIBLE);
        // 进入设置页面
        btnGluSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!MyUtils.isNormalClickTime()){
                    return;
                }
                start(mGluSetFragment);
            }
        });
    }
}
