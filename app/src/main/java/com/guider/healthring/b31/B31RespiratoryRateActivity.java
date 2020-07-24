package com.guider.healthring.b31;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.guider.healthring.MyApp;
import com.guider.healthring.R;
import com.guider.healthring.b30.b30view.CustomCircleProgressBar;
import com.guider.healthring.bleutil.MyCommandManager;
import com.guider.healthring.siswatch.WatchBaseActivity;
import com.guider.healthring.siswatch.utils.WatchUtils;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IBreathDataListener;
import com.veepoo.protocol.model.datas.BreathData;

/**
 * 测量B31的呼吸率
 * Created by Admin
 * Date 2018/12/18
 */
public class B31RespiratoryRateActivity extends WatchBaseActivity implements View.OnClickListener{

    private static final String TAG = "B31RespiratoryRateActiv";


    ImageView commentB30BackImg;
    TextView commentB30TitleTv;
    ImageView commentB30ShareImg;
    CustomCircleProgressBar b31MeaureRateProgressView;
    TextView showB31RateStateTv;
    ImageView b31MeaureRateStartImg;

    //开始或者停止测量的标识
    private boolean isStart = false;


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1001){
                BreathData breathData = (BreathData) msg.obj;
                if(breathData == null)
                    return;
                if(breathData.getProgressValue() == 100){
                    stopMan();
//                    b31MeaureRateProgressView.setTmpTxt(breathData.getValue()+"次/分");string_count_time
                    b31MeaureRateProgressView.setTmpTxt(breathData.getValue()+""+getResources().getString(R.string.string_count_time));
                }
            }
        }
    };



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b31_respiratory_rate_layout);
        initViewIds();
        initViews();


    }

    private void initViewIds() {
        commentB30BackImg = findViewById(R.id.commentB30BackImg);
        commentB30TitleTv = findViewById(R.id.commentB30TitleTv);
        commentB30ShareImg = findViewById(R.id.commentB30ShareImg);
        b31MeaureRateProgressView = findViewById(R.id.b31MeaureRateProgressView);
        showB31RateStateTv = findViewById(R.id.showB31RateStateTv);
        b31MeaureRateStartImg = findViewById(R.id.b31MeaureRateStartImg);
        commentB30BackImg.setOnClickListener(this);
        commentB30ShareImg.setOnClickListener(this);
        b31MeaureRateStartImg.setOnClickListener(this);
    }

    private void initViews() {
        commentB30TitleTv.setText(getResources().getString(R.string.vpspo2h_toptitle_breath));
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30ShareImg.setVisibility(View.INVISIBLE);
        b31MeaureRateProgressView.setInsideColor(Color.parseColor("#EBEBEB"));
        b31MeaureRateProgressView.setOutsideColor(Color.WHITE);


    }

   @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:    //返回
                finish();
                break;
            case R.id.commentB30ShareImg:   //分享
                WatchUtils.shareCommData(B31RespiratoryRateActivity.this);
                break;
            case R.id.b31MeaureRateStartImg:    //开始或暂停测量
                if(MyCommandManager.DEVICENAME == null){
                    showB31RateStateTv.setText(getResources().getString(R.string.disconnted));
                    return;
                }
                startOrStopManRate();
                break;
        }
    }

    private void startOrStopManRate() {
        if(!isStart){
            isStart = true;
            b31MeaureRateStartImg.setImageResource(R.drawable.detect_breath_stop);
            b31MeaureRateProgressView.setTmpTxt(null);
            b31MeaureRateProgressView.setScheduleDuring(60 * 1000);
            b31MeaureRateProgressView.setProgress(100);
            MyApp.getInstance().getVpOperateManager().startDetectBreath(iBleWriteResponse, new IBreathDataListener() {
                @Override
                public void onDataChange(BreathData breathData) {
                    Log.e(TAG,"-----------breathData="+breathData.toString());
                    Message message = handler.obtainMessage();
                    message.what = 1001;
                    message.obj = breathData;
                    handler.sendMessage(message);
                }
            });

        }else{
            stopMan();
            b31MeaureRateProgressView.stopAnim();
            MyApp.getInstance().getVpOperateManager().stopDetectBreath(iBleWriteResponse, new IBreathDataListener() {
                @Override
                public void onDataChange(BreathData breathData) {

                }
            });
        }

    }

    //停止测量
    private void stopMan() {
        isStart = false;
        b31MeaureRateStartImg.setImageResource(R.drawable.detect_breath_start);
    }

    private IBleWriteResponse iBleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int i) {

        }
    };


    @Override
    protected void onStop() {
        super.onStop();
        if(isStart){    //还在测量中 停止测量
            stopMan();
            b31MeaureRateProgressView.stopAnim();
            MyApp.getInstance().getVpOperateManager().stopDetectBreath(iBleWriteResponse, new IBreathDataListener() {
                @Override
                public void onDataChange(BreathData breathData) {

                }
            });
        }
    }
}
