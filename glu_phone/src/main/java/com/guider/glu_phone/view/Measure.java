package com.guider.glu_phone.view;

import android.os.Bundle;
import android.os.PowerManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.guider.glu.presenter.GLUServiceManager;
import com.guider.glu_phone.R;

import static android.content.Context.POWER_SERVICE;


/**
 * Created by haix on 2019/7/18.
 */

public class Measure extends GlocoseFragment{


    private View view;
    private ECGCompletedView completedView;
    private TextView reminder;
    private TextView measure_time;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.measure, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((TextView) view.findViewById(R.id.head_title)).setText(getResources().getText(R.string.measure_blu));
        view.findViewById(R.id.head_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GLUServiceManager.getInstance().stopDeviceConnect();
                popTo(TurnOnOperation.class, false);
            }
        });

        completedView = view.findViewById(R.id.time_measure);
        measure_time = view.findViewById(R.id.measure_time);
        reminder = view.findViewById(R.id.reminder);

        PowerManager powerManager = (PowerManager)_mActivity.getSystemService(POWER_SERVICE);
        if (powerManager != null) {
            mWakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "WakeLock");
            mWakeLock.setReferenceCounted(false);
            mWakeLock.acquire();
        }



    }





    @Override
    public void look_error(final String error) {
        _mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(_mActivity, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void showMeasureTime(String time) {

        if (completedView != null) {
            completedView.setProgress(Integer.valueOf(time));
        }

        if (measure_time != null){
            measure_time.setText(time);
        }
    }


    @Override
    public void showMeasureRemind(String remind) {

        if (reminder != null && remind != null){
            reminder.setText(remind);
        }
    }

    @Override
    public void measureFinish() {

        startWithPop(new MeasureResult());
    }

    @Override
    public void measureErrorAndCloseBlueConnect() {
        Log.i("haix", "=====测量错误");
        GLUServiceManager.getInstance().stopDeviceConnect();
        pop();
    }
}
