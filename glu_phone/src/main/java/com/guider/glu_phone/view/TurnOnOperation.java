package com.guider.glu_phone.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.guider.glu.presenter.GLUServiceManager;
import com.guider.glu_phone.R;
import com.guider.health.common.core.MyUtils;

/**
 * Created by haix on 2019/7/18.
 */

public class TurnOnOperation extends GlocoseFragment{


    private View view;

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);


        if (!hidden) {

            System.gc();
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.operation, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        GLUServiceManager.getInstance().setIsPhone(true);

        ((TextView)view.findViewById(R.id.head_title)).setText(getResources().getText(R.string.measure_2_kaijicaozuo));
        view.findViewById(R.id.head_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pop();
            }
        });

        view.findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!MyUtils.isNormalClickTime()){
                    return;
                }
                start(new BluetoothConnect());

            }
        });
    }



}
