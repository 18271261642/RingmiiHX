package cn.wuweikang.view;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.guider.health.common.core.BaseFragment;
import com.guider.health.common.core.Config;

import cn.wuweikang.R;

/**
 * Created by haix on 2019/7/29.
 */

public class Dao12Interface extends BaseFragment{

    protected View view;
    // private String[] mainNotf1 = {"开机操作" , "蓝牙连接" , "设备测量" , "测量结果"};
    private int indexFragment = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (this.getClass().getSimpleName().equals("VideoShow")){
            view = inflater.inflate(R.layout.video_show, container, false);
            indexFragment = 1;
        }else if (this.getClass().getSimpleName().equals("TurnOnOperator")){
            view = inflater.inflate(R.layout.turn_on_operator, container, false);
            indexFragment = 2;
        }else if (this.getClass().getSimpleName().equals("ConnectBluetooth")){
            view = inflater.inflate(R.layout.connect_bluetooth, container, false);
            indexFragment = 3;
        }else if (this.getClass().getSimpleName().equals("MeasureDevice")){
            view = inflater.inflate(R.layout.measure_device, container, false);
            indexFragment = 4;
        }else if (this.getClass().getSimpleName().equals("ResultMeasure")){
            view = inflater.inflate(R.layout.result_measure, container, false);
            indexFragment = 5;
        }
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null){
            return;
        }


        setHomeEvent(view.findViewById(R.id.home), Config.HOME_DEVICE);
        if (indexFragment == 1){
            ((TextView)view.findViewById(R.id.title)).setText(getTitle(0));
        }else if (indexFragment == 2){
            ((TextView)view.findViewById(R.id.title)).setText(getTitle(0));
        }else if (indexFragment == 3){
            ((TextView)view.findViewById(R.id.title)).setText(getTitle(1));
        }else if (indexFragment == 4){
            ((TextView)view.findViewById(R.id.title)).setText(getTitle(2));
        }else if (indexFragment == 5){
            ((TextView)view.findViewById(R.id.title)).setText(getTitle(3));
        }


        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pop();
            }
        });

        SpannableString spannableString = new SpannableString(getResources().getString(R.string.wwk_ecg) + " > "
                + getTitle(0) + " > "
                + getTitle(1) + " > "
                + getTitle(2) + " > "
                + getTitle(3));
        spannableString.setSpan(new AbsoluteSizeSpan(20, true), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (indexFragment == 1){
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#F18937")), 7, 13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new AbsoluteSizeSpan(23, true), 7, 13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }else if(indexFragment == 2){
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#F18937")), 7, 13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new AbsoluteSizeSpan(23, true), 7, 13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }else if(indexFragment == 3){
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#F18937")), 14, 20, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new AbsoluteSizeSpan(23, true), 14, 20, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }else if(indexFragment == 4){
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#F18937")), 22, 28, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new AbsoluteSizeSpan(23, true), 22, 28, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }else if(indexFragment == 5){
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#F18937")), 29, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new AbsoluteSizeSpan(23, true), 29, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        try {
            TextView tv = (TextView)view.findViewById(R.id.navigation_text);
            if (tv != null){
                tv.setText(spannableString);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String getTitle(int index) {
        int id = R.string.wwk_title1;
        switch (index) {
            case 0:
                id = R.string.wwk_title1;
                break;
            case 1:
                id = R.string.wwk_title2;
                break;
            case 2:
                id = R.string.wwk_title3;
                break;
            case 3:
                id = R.string.wwk_title4;
                break;
        }
        return getResources().getString(id);
    }
}
