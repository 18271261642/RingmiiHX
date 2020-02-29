package com.guider.glu.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.guider.glu.R;
import com.guider.health.common.core.BaseFragment;

public class TempGlu extends BaseFragment {

    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.glu_operate_reminders, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        YCGluUtils ycGluUtils = new YCGluUtils(_mActivity);
        ycGluUtils.start();
    }
}
