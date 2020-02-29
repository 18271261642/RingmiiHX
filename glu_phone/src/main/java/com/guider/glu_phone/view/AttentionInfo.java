package com.guider.glu_phone.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.guider.glu_phone.R;
import com.guider.glu_phone.net.NetRequest;
import com.guider.health.common.core.MyUtils;

import java.lang.ref.WeakReference;

/**
 * Created by haix on 2019/7/18.
 */

public class AttentionInfo extends GlocoseFragment{


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

        NetRequest.getInstance().getNonbsSet(new WeakReference<Activity>(_mActivity), new NetRequest.NetCallBack() {

            @Override
            public void result(int code, String result) {

                if (code == 0){
                    //成功
                }
            }
        });

        NetRequest.getInstance().getUserInfo(new WeakReference<Activity>(_mActivity), new NetRequest.NetCallBack() {

            @Override
            public void result(int code, String result) {

                if (code == 0){
                    //成功
                }
            }
        });



        ((TextView)view.findViewById(R.id.head_title)).setText(R.string.measure_1_zhuyishixiang);
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
                start(new ChooseTime());
            }
        });
    }
}
