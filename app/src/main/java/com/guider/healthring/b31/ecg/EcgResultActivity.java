package com.guider.healthring.b31.ecg;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.guider.healthring.MyApp;
import com.guider.healthring.R;
import com.guider.healthring.b31.ecg.bean.EcgDetectResultBean;
import com.guider.healthring.b31.ecg.bean.EcgSourceBean;
import com.guider.healthring.siswatch.WatchBaseActivity;
import com.guider.healthring.view.CusScheduleView;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;


/**
 * 心电详情页面
 * Created by Admin
 * Date 2021/5/22
 */
public class EcgResultActivity extends WatchBaseActivity implements View.OnClickListener {


    ImageView commentB30BackImg;
    TextView commentB30TitleTv;
    //结果心率
    TextView ecgResultFrequencyTv;
    TextView ecgResultSpeedTv;
    TextView ecgResultGainTv;

    TextView ecgResultReportTv;

    TextView ecgResultReportHeartTv;
    TextView ecgResultReportQTTv;
    TextView ecgResultReportHRVTv;
    //疾病分析结果
    TextView ecgResultDescTv;
    ImageView ecgResultPlayImg;
    //心电图view
    EcgHeartRealthView ecgHeartResultView;
    CusScheduleView ecgResultScheduleView;


    ConstraintLayout ecgResultHeartLayout;
    ConstraintLayout ecgResultQTLayout;
    ConstraintLayout ecgResultHRVLayout;


    private EcgSourceBean ecgSourceBean;

    private boolean isPlayEcg = false;
    List<int[]> ecgADCList;

    EcgDetectResultBean ecgDetectResultBean;


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecg_detect_result_layout);


        initViews();

        ecgSourceBean = MyApp.getInstance().getEcgSourceBean();
        if(ecgSourceBean == null){
            showEmptyData();
            return;
        }
        showResultData();

    }

    private void initViews() {
        findViews();
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText("心电详情");
        ecgHeartResultView.setCoumlnQutoCount(13 * 5);

        ecgResultScheduleView.setAllScheduleValue(100f);

    }


    private void findViews(){

       commentB30BackImg = findViewById(R.id.commentB30BackImg);
        commentB30TitleTv =  findViewById(R.id.commentB30TitleTv);
        //结果心率
        ecgResultFrequencyTv = findViewById(R.id.ecgResultFrequencyTv);
        ecgResultSpeedTv = findViewById(R.id.ecgResultSpeedTv);
        ecgResultGainTv = findViewById(R.id.ecgResultGainTv);
        ecgResultReportHeartTv = findViewById(R.id.ecgResultReportHeartTv);
        ecgResultReportQTTv = findViewById(R.id.ecgResultReportQTTv);
        ecgResultReportHRVTv = findViewById(R.id.ecgResultReportHRVTv);
        //疾病分析结果
        ecgResultDescTv = findViewById(R.id.ecgResultDescTv);
        ecgResultPlayImg = findViewById(R.id.ecgResultPlayImg);
        //心电图view
        ecgHeartResultView = findViewById(R.id.ecgHeartResultView);
        ecgResultScheduleView = findViewById(R.id.ecgResultScheduleView);

        ecgResultHeartLayout = findViewById(R.id.ecgResultHeartLayout);
        ecgResultQTLayout = findViewById(R.id.ecgResultQTLayout);
        ecgResultHRVLayout = findViewById(R.id.ecgResultHRVLayout);

        ecgResultReportTv = findViewById(R.id.ecgResultReportTv);

        commentB30BackImg.setOnClickListener(this);
        ecgResultPlayImg.setOnClickListener(this);
        ecgResultHeartLayout.setOnClickListener(this);
        ecgResultQTLayout.setOnClickListener(this);
        ecgResultHRVLayout.setOnClickListener(this);
        ecgResultReportTv.setOnClickListener(this);

    }


    private void showResultData(){
        try {
            if(ecgSourceBean == null)
                return;
            String ecgResultBean = ecgSourceBean.getEcgDetectResult();
            if(ecgResultBean == null)
                return;
            Log.e("结果","----ecgResult="+ecgResultBean);
            ecgDetectResultBean = new Gson().fromJson(ecgResultBean,EcgDetectResultBean.class);
            if(ecgDetectResultBean == null)
                return;
            ecgResultFrequencyTv.setText("频率:"+ecgDetectResultBean.getFrequency()+"HZ");
            //ecgResultSpeedTv.setText("走速:"+ecgDetectResultBean.get);

            ecgResultReportHeartTv.setText(ecgDetectResultBean.getAveHrv()+"次/分");
            ecgResultReportQTTv.setText(ecgDetectResultBean.getAveQT()+"毫秒");
            ecgResultReportHRVTv.setText(ecgDetectResultBean.getAveHrv()+"毫秒");

            ecgResultDescTv.setText("窦性心率,此心电未显示异常现象");
            //结果



            //ecgADC原始数据
            String adcStr = ecgSourceBean.getEcgListStr();
            if(adcStr == null)
                return;
           ecgADCList = new Gson().fromJson(adcStr,new TypeToken<List<int[]>>(){}.getType());

        }catch (Exception e){
            e.printStackTrace();
        }

    }


    private void showEmptyData(){

        ecgResultFrequencyTv.setText("频率:--");
        ecgResultSpeedTv.setText("走速:--");
        ecgResultGainTv.setText("增益:--");

        ecgResultReportHeartTv.setText("--");
        ecgResultReportQTTv.setText("--");
        ecgResultReportHRVTv.setText("--");

        ecgResultDescTv.setText("--");
    }


    private void startActivtyData(int code,int value){
        Intent intent = new Intent(this,ECGResultDescActivity.class);
        intent.putExtra("ecg_code",code);
        intent.putExtra("ecg_value",value);
        startActivity(intent);
    }


    int position = 0;
    private void rePlayEcg(){
        try {
            if(ecgSourceBean ==null)
                return;
            if(ecgADCList ==null || ecgADCList.isEmpty())
                return;
            position = 0;
            if(!isPlayEcg){ //播放
                isPlayEcg = true;
                ecgResultPlayImg.setImageResource(R.drawable.ecg_test_playback_pause);
                handler.post(runnable);
            }else {     //暂停
                isPlayEcg = false;
                ecgResultPlayImg.setImageResource(R.drawable.ecg_test_playback_start);
                handler.removeCallbacks(runnable);
                ecgHeartResultView.clearData();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                if(ecgADCList ==null || ecgADCList.isEmpty())
                    return;
                if(position == ecgADCList.size()-1){
                    rePlayEcg();
                    return;
                }
                float currPercent = 100f / ecgADCList.size();
                ecgResultScheduleView.setCurrScheduleValue(position * currPercent);
                int[] currValue = ecgADCList.get(position);
                ecgHeartResultView.changeData(currValue,currValue.length);
                position++;
                handler.postDelayed(runnable,100);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    };


    @Override
    protected void onStop() {
        super.onStop();
        try {
            handler.removeCallbacks(runnable);
            ecgHeartResultView.clearData();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:
                finish();
                break;
            case R.id.ecgResultPlayImg: //播放/暂停
                rePlayEcg();
                break;
            case R.id.ecgResultReportTv:    //查看报告
                if(ecgDetectResultBean == null)
                    return;
                startActivity(ShowEcgDataActivity.class,new String[]{"ecg_time","ecg_desc","ecg_source"},new String[]{ecgSourceBean.getDetectDate()+" "+ecgSourceBean.getDetectTime(),ecgSourceBean.getEcgDetectResult(),ecgSourceBean.getEcgListStr()});
                break;
            case R.id.ecgResultHeartLayout: //查看心率详情
                if(ecgDetectResultBean == null)
                    return;
                startActivtyData(0,ecgDetectResultBean.getAveHeart());
                break;
            case R.id.ecgResultQTLayout:    //QT间期
                if(ecgDetectResultBean == null)
                    return;
                startActivtyData(1,ecgDetectResultBean.getAveQT());
                break;
            case R.id.ecgResultHRVLayout:   //hrv
                if(ecgDetectResultBean == null)
                    return;
                startActivtyData(2,ecgDetectResultBean.getAveHrv());
                break;
        }
    }
}
