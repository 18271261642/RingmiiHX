package com.guider.healthring.b31.ecg;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.guider.healthring.R;
import com.guider.healthring.siswatch.WatchBaseActivity;
import com.guider.healthring.util.GetJsonDataUtil;
import com.guider.libbase.util.Log;

import java.util.ArrayList;
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
public class ShowEcgDataActivity extends WatchBaseActivity {

    private static final String TAG = "ShowEcgDataActivity";

    private RecyclerView ecgRecyclerView;
    private EcgShowAdapter adapter;

    private List<int[]> resultList;


    private List<int[]> tmpList = new ArrayList<>();
//
//    private EcgHeartRealthView ecgView1;
//    private EcgHeartRealthView ecgView2;


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            handler.removeMessages(0x00);
            tmpList.clear();
            String jsonStr = (String) msg.obj;
            if(jsonStr == null)
                return;
            //所有的原始数据
            List<Integer> lit = new Gson().fromJson(jsonStr,new TypeToken<List<Integer>>(){}.getType());
            analysisEcg(lit);

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

        String jsonStr = new GetJsonDataUtil().getJson(this,"ecg.json");

        if(jsonStr == null)
            return;
        Message message = handler.obtainMessage();
        message.what = 0x00;
        message.obj= jsonStr;
        handler.sendMessage(message);
    }


    private void initViews() {
//        ecgView1 = findViewById(R.id.ecgView1);
//        ecgView2 = findViewById(R.id.ecgView2);

        ecgRecyclerView = findViewById(R.id.ecgRecyclerView);
        resultList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ShowEcgDataActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ecgRecyclerView.setLayoutManager(linearLayoutManager);
        adapter = new EcgShowAdapter(resultList,ShowEcgDataActivity.this);
        ecgRecyclerView.setAdapter(adapter);

    }



    private void analysisEcg(List<Integer> lt){

        resultList.clear();
        //这里只让显示一个图表
        List<Integer> tmpLt = lt.subList(0,2100);

        //大小
        int ecgArraySize = tmpLt.size();
        //数量大小
        int count = ecgArraySize / 2000;

        int[] sourceArray = new int[ecgArraySize];

        for(int i = 0;i<tmpLt.size();i++){
            sourceArray[i] = tmpLt.get(i);
        }

        for(int i = 0;i<count;i++){
            int[] tmpArray = new int[2000];
            System.arraycopy(sourceArray,i * 2000,tmpArray,0,tmpArray.length);
            resultList.add(tmpArray);
        }


        //单独设置两个是正常的
//        ecgView1.changeData(resultList.get(0),resultList.get(0).length);
//        ecgView2.changeData(resultList.get(1),resultList.get(1).length);

        adapter.notifyDataSetChanged();
    }


}
