package com.guider.healthring.b31.ecg;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.guider.healthring.R;
import com.guider.healthring.activity.GuideActivity;
import com.guider.healthring.b31.ecg.bean.EcgDetectStateBean;
import com.guider.healthring.bean.BlueUser;
import com.guider.healthring.bean.UserInfoBean;
// import com.guider.healthring.commdbserver.ValueFormatter;
import com.guider.healthring.siswatch.WatchBaseActivity;
import com.guider.healthring.siswatch.utils.WatchUtils;
import com.guider.healthring.util.GetJsonDataUtil;
import com.guider.healthring.util.SharedPreferencesUtils;
import com.guider.libbase.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 展示已经测量保存的ecg数据
 * Created by Admin
 * Date 2021/4/14
 */
public class ShowEcgDataActivity extends WatchBaseActivity implements View.OnClickListener {

    private static final String TAG = "ShowEcgDataActivity";

    private RecyclerView ecgRecyclerView;
    private EcgShowAdapter adapter;

    private List<List<Integer>> resultList;

    ImageView commentB30BackImg;
    TextView commentB30TitleTv;

    private TextView ecgHeartDescTv;
    private TextView ecgDescTv;
    private TextView userNickTv;
    private TextView ecgDetectTimeTv;



    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            handler.removeMessages(0x00);
            String jsonStr = (String) msg.obj;
            if(jsonStr == null)
                return;
            //所有的原始数据
            List<Integer> lit = new Gson().fromJson(jsonStr,new TypeToken<List<Integer>>(){}.getType());

            analysisEcgData(lit);


        }
    };



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        }
        setContentView(R.layout.activity_show_ecg_data_layout);

        initViews();

        showEcgData();

    }



    private void showEcgData() {
        String userInfo = SharedPreferencesUtils.readObject(this, "userInfo").toString();
        if(userInfo != null){
            Gson gson = new Gson();
            UserInfoBean userInfos = gson.fromJson(userInfo, UserInfoBean.class);
            if(userInfo != null)
                userNickTv.setText("昵称: "+userInfos.getNickname()+"");
        }

        String ecg_desc = getIntent().getStringExtra("ecg_desc");
        String ecg_source = getIntent().getStringExtra("ecg_source");
        String ecg_tine = getIntent().getStringExtra("ecg_time");
        if(!WatchUtils.isEmpty(ecg_tine)){
            ecgDetectTimeTv.setText(ecg_tine+"");
        }
        if(!WatchUtils.isEmpty(ecg_desc)){
            EcgDetectStateBean ecgDetectStateBean = new Gson().fromJson(ecg_desc,EcgDetectStateBean.class);
            if(ecgDetectStateBean == null)
                return;
            ecgHeartDescTv.setText("心率: "+(ecgDetectStateBean.getHr2() == 0 ?"--":ecgDetectStateBean.getHr2())+" bpm"
                    + "QT间期: "+(ecgDetectStateBean.getQtc() == 0 ? "--" : ecgDetectStateBean.getQtc()) +" ms " + "hrv: "+(ecgDetectStateBean.getHrv() == 0 || ecgDetectStateBean.getHrv() == 255 ? "--" : ecgDetectStateBean.getHr2())+"ms");
            ecgDescTv.setText("导联: I 走速: 25mm/s 增益: 10mm/mv  频率: 250HZ");
        }


        if(ecg_source != null){
            List<int[]> lt = new Gson().fromJson(ecg_source,new TypeToken<List<int[]>>(){}.getType());
            List<Integer> countList = new ArrayList<>();
            for(int i = 0;i<lt.size();i++){
                int[] itemArray = lt.get(i);
               for(int k = 0;k<itemArray.length;k++)
                   countList.add(itemArray[k]);
            }

            analysisEcgData(countList);
        }



//
//        String jsonStr = new GetJsonDataUtil().getJson(this,"ecg.json");
//
//        if(jsonStr == null)
//            return;
//        Message message = handler.obtainMessage();
//        message.what = 0x00;
//        message.obj= jsonStr;
//        handler.sendMessage(message);
    }


    private void initViews() {
        ecgDetectTimeTv = findViewById(R.id.ecgDetectTimeTv);
        userNickTv = findViewById(R.id.userNickTv);
        commentB30BackImg = findViewById(R.id.commentB30BackImg);
        commentB30TitleTv = findViewById(R.id.commentB30TitleTv);
        ecgRecyclerView = findViewById(R.id.ecgRecyclerView);
        ecgHeartDescTv = findViewById(R.id.ecgHeartDescTv);
        ecgDescTv = findViewById(R.id.ecgHeartDescTv);
        commentB30BackImg.setOnClickListener(this);

        commentB30TitleTv.setText("ECG");
        commentB30BackImg.setVisibility(View.VISIBLE);

        resultList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ShowEcgDataActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ecgRecyclerView.setLayoutManager(linearLayoutManager);
        adapter = new EcgShowAdapter(resultList,ShowEcgDataActivity.this);
        ecgRecyclerView.setAdapter(adapter);

    }



    List<List<Integer>> rt = new ArrayList<>();
    private void analysisEcgData(List<Integer> lit) {
        int count = lit.size() / 2000;
        int overCount = lit.size() % 2000;

        for(int i = 0;i<count;i++){
            List<Integer> lt = new ArrayList<>();
            List<Integer> tmpList = lit.subList(i * 2000,(i +1) * 2000);
            lt.addAll(tmpList);
            rt.add(lt);
        }
        resultList.addAll(rt);
        if(overCount != 0){
            List<Integer> tmpList2 = lit.subList(count * 2000,lit.size()-1);
            resultList.add(tmpList2);
        }
        adapter.notifyDataSetChanged();
        Log.e(TAG,"----大小="+resultList.size());
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.commentB30BackImg:
                finish();
                break;
        }
    }
}
