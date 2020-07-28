package com.guider.glu_phone.view;

import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.guider.glu.presenter.GLUServiceManager;
import com.guider.glu_phone.R;


/**
 * Created by haix on 2019/7/18.
 */

public class InsertFinger extends GlocoseFragment{

    private View view;
    private TextView daojishi;
    private TextView reminder_finger;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.insert_finger, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((TextView) view.findViewById(R.id.head_title)).setText(getResources().getText(R.string.measure));
        view.findViewById(R.id.head_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GLUServiceManager.getInstance().stopDeviceConnect();
                popTo(TurnOnOperation.class, false);
            }
        });

        daojishi = view.findViewById(R.id.daojishi);
        reminder_finger = view.findViewById(R.id.reminder_finger);



    }

    @Override
    public void finger_time(final int time) {
        if (time == 0){
            daojishi.setText("");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    pop();
                }
            }, 1500);
            return;
        }
        if (daojishi != null){
            daojishi.setText(time+"");
        }
    }



    @Override
    public void time60Begin() {

        startWithPop(new Measure());
    }

    @Override
    public void measureErrorAndCloseBlueConnect() {
        Log.i("haix", "=====测量错误");
        GLUServiceManager.getInstance().stopDeviceConnect();
        pop();
    }
}
