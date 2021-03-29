package com.guider.healthring.b31.ecg;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.guider.healthring.MyApp;
import com.guider.healthring.R;
import com.guider.healthring.bleutil.MyCommandManager;
import com.guider.healthring.siswatch.WatchBaseActivity;
import com.guider.healthring.view.CusScheduleView;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IECGDetectListener;
import com.veepoo.protocol.listener.data.IECGReadDataListener;
import com.veepoo.protocol.listener.data.IECGReadIdListener;
import com.veepoo.protocol.model.datas.EcgDetectInfo;
import com.veepoo.protocol.model.datas.EcgDetectResult;
import com.veepoo.protocol.model.datas.EcgDetectState;
import com.veepoo.protocol.model.datas.TimeData;
import com.veepoo.protocol.model.enums.EEcgDataType;
import java.util.Arrays;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * 手动测量心电页面
 * Created by Admin
 * Date 2021/1/19
 */
public class EcgDetectActivity extends WatchBaseActivity implements View.OnClickListener {

    private static final String TAG = "EcgDetectActivity";

    ImageView commentB30BackImg;
    TextView commentB30TitleTv;
    ImageView detectEcgImgView;
    CusScheduleView ecgDetectScheduleView;
    TextView showEcgDetectStatusTv;
    TextView detectEcgHeartTv;
    TextView detectEcgQtTv;
    TextView detectEcgHrvTv;

    //心电图
    private ECGView heartView;

    //是否正在测量
    private boolean isMeasure = false;


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x01) {
                EcgDetectState ecgDetectState = (EcgDetectState) msg.obj;
                if (ecgDetectState == null)
                    return;
                int progress = ecgDetectState.getProgress();
                ecgDetectScheduleView.setCurrScheduleValue(progress);
                showEcgDetectStatusTv.setText("测量中: " + progress+"%");

                detectEcgHeartTv.setText(ecgDetectState.getHr1() == 0 ? "--次/分":ecgDetectState.getHr1()+"次/分");
                detectEcgQtTv.setText(ecgDetectState.getQtc() == 0 ? "--毫秒":ecgDetectState.getQtc()+"毫秒");
                detectEcgHrvTv.setText((ecgDetectState.getHrv() == 0 || ecgDetectState.getHrv() == 255) ?"--毫秒":ecgDetectState.getHrv()+"毫秒");

                if(progress == 100){    //测量完了
                    showEcgDetectStatusTv.setText("测量完毕");
                    detectEcgImgView.setImageResource(R.drawable.detect_sp_start);
                    isMeasure = false;
                    stopDetectEcg();
                }
            }

            if(msg.what == 0x02){
                int[] ecgArray = (int[]) msg.obj;

//                int maxValue = Arrays.stream(ecgArray).max().getAsInt();
//                int mineValue = Arrays.stream(ecgArray).min().getAsInt();
//
//
//                Log.e(TAG,"------maxValue="+maxValue+"--mineValue="+mineValue);
//
//
//                if(mineValue >= maxValue){
//                    maxValue = 30000;
//                    mineValue = -30000;
//                }

//                List<Integer> arrayList = Arrays.asList(ecgArray);
//                heartView.setMax(maxValue == 0 ? 30000 : maxValue);
//                heartView.setMin(mineValue == 0? -30000 : mineValue);
                heartView.offer(ecgArray);


//                heartView.setData(ecgArray);



            }

            if(msg.what == 0x03){   //结果

            }

        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecg_detect_layout);

        initViews();

    }

    private void initViews() {
        findViews();

        commentB30TitleTv.setText("心电测试");
        commentB30BackImg.setVisibility(View.VISIBLE);

        ecgDetectScheduleView.setAllScheduleValue(100f);

        heartView = findViewById(R.id.detectEcgView);

        showEmptyData();
    }

    private void findViews() {
        commentB30BackImg = findViewById(R.id.commentB30BackImg);
        commentB30TitleTv = findViewById(R.id.commentB30TitleTv);
        detectEcgImgView = findViewById(R.id.detectEcgImgView);
        ecgDetectScheduleView = findViewById(R.id.ecgDetectScheduleView);
        showEcgDetectStatusTv = findViewById(R.id.showEcgDetectStatusTv);
        detectEcgHeartTv = findViewById(R.id.detectEcgHeartTv);
        detectEcgQtTv = findViewById(R.id.detectEcgQtTv);
        detectEcgHrvTv = findViewById(R.id.detectEcgHrvTv);

        commentB30BackImg.setOnClickListener(this);
        detectEcgImgView.setOnClickListener(this);
    }


    private void showEmptyData(){
        detectEcgHeartTv.setText("--次/分");
        detectEcgQtTv.setText("--毫秒");
        detectEcgHrvTv.setText("--毫秒");
    }

    //读取所有心电数据
    private void readAllEcgData() {
        if (MyCommandManager.DEVICENAME == null)
            return;
        TimeData timeData = new TimeData(2021, 1, 22);
        MyApp.getInstance().getVpOperateManager().readECGId(iBleWriteResponse, timeData, EEcgDataType.MANUALLY, new IECGReadIdListener() {
            @Override
            public void readIdFinish(int[] ints) {
                Log.e(TAG, "---readECGId=" + Arrays.toString(ints));
            }
        });
        MyApp.getInstance().getVpOperateManager().readECGData(iBleWriteResponse, timeData, EEcgDataType.MANUALLY, new IECGReadDataListener() {
            @Override
            public void readDataFinish(List<EcgDetectResult> list) {
                Log.e(TAG, "---readDataFinish=" + new Gson().toJson(list));
            }
        });
    }


    //停止测量
    private void stopDetectEcg() {
        if (MyCommandManager.DEVICENAME == null)
            return;
        MyApp.getInstance().getVpOperateManager().stopDetectECG(iBleWriteResponse, true, new IECGDetectListener() {
            @Override
            public void onEcgDetectInfoChange(EcgDetectInfo ecgDetectInfo) {
                Log.e(TAG, "---stop---onEcgDetectInfoChange=" + ecgDetectInfo.toString());
            }

            @Override
            public void onEcgDetectStateChange(EcgDetectState ecgDetectState) {
                Log.e(TAG, "---stop----onEcgDetectStateChange=" + ecgDetectState.toString());
            }

            @Override
            public void onEcgDetectResultChange(EcgDetectResult ecgDetectResult) {
                Log.e(TAG, "----stop-----onEcgDetectResultChange=" + ecgDetectResult.toString());
            }

            @Override
            public void onEcgADCChange(int[] ints) {

                Log.e(TAG, "--stop--onEcgADCChange=" + Arrays.toString(ints));
            }
        });
    }


    //开始测量心电数据
    private void detectEcgData() {
        if (MyCommandManager.DEVICENAME == null)
            return;
        MyApp.getInstance().getVpOperateManager().startDetectECG(iBleWriteResponse, true, new IECGDetectListener() {
            @Override
            public void onEcgDetectInfoChange(EcgDetectInfo ecgDetectInfo) {
                Log.e(TAG, "------onEcgDetectInfoChange=" + ecgDetectInfo.toString());
            }

            @Override
            public void onEcgDetectStateChange(EcgDetectState ecgDetectState) {
                Log.e(TAG, "-------onEcgDetectStateChange=" + ecgDetectState.toString());
                Message message = handler.obtainMessage();
                message.what = 0x01;
                message.obj = ecgDetectState;
                handler.sendMessage(message);
            }

            @Override
            public void onEcgDetectResultChange(EcgDetectResult ecgDetectResult) {
                Log.e(TAG, "---------onEcgDetectResultChange=" + ecgDetectResult.toString() + "\n" + ecgDetectResult.getAveHeart());
                Message message = handler.obtainMessage();
                message.what = 0x03;
                message.obj = ecgDetectResult;
                handler.sendMessage(message);
            }

            @Override
            public void onEcgADCChange(int[] ints) {
                Log.e(TAG, "----onEcgADCChange=" + Arrays.toString(ints));
                Message message = handler.obtainMessage();
                message.what = 0x02;
                message.obj = ints;
                handler.sendMessage(message);
            }
        });
    }


    private IBleWriteResponse iBleWriteResponse = i -> {

    };



    private void startAndStopDetect(){
        if (!isMeasure) {
            detectEcgImgView.setImageResource(R.drawable.detect_sp_stop);
            isMeasure = true;
            detectEcgData();
        } else {
            detectEcgImgView.setImageResource(R.drawable.detect_sp_start);
            isMeasure = false;
            stopDetectEcg();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.detectEcgImgView:
                startAndStopDetect();
                break;
            case R.id.commentB30BackImg:
                finish();
                break;
        }
    }
}
