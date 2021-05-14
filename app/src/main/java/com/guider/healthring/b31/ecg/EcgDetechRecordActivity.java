package com.guider.healthring.b31.ecg;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.guider.healthring.MyApp;
import com.guider.healthring.R;
import com.guider.healthring.b31.ecg.bean.EcgSourceBean;
import com.guider.healthring.siswatch.WatchBaseActivity;
import com.guider.healthring.siswatch.utils.WatchUtils;
import com.guider.healthring.w30s.adapters.CommonRecyclerAdapter;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 展示ecg测量的记录
 * * Created by Admin
 * Date 2021/4/21
 */
public class EcgDetechRecordActivity extends WatchBaseActivity implements View.OnClickListener, CommonRecyclerAdapter.OnItemListener {

    private ImageView commBackImg;
    private TextView titleTv;
    private ImageView leftImg,rightImg;
    private TextView dateTv;

    private String currDay = WatchUtils.getCurrentDate();

    private EcgDetectRecordAdapter ecgDetectRecordAdapter;
    private RecyclerView recyclerView;
    private List<EcgSourceBean> list;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecg_detech_record_layout);

        initViews();

        queryDbEcgData(currDay);
    }


    //查询数据库中的ecg
    private void queryDbEcgData(String dayStr){
        try {
            list.clear();
            dateTv.setText(dayStr);
            String bleMac = MyApp.getInstance().getMacAddress();
            if(WatchUtils.isEmpty(bleMac))
                return;
            List<EcgSourceBean> sourceBeanList = LitePal.where("bleMac = ? and detectDate = ?",bleMac,dayStr).find(EcgSourceBean.class);
            if(sourceBeanList == null){
                ecgDetectRecordAdapter.notifyDataSetChanged();
                return;
            }
            list.addAll(sourceBeanList);
            ecgDetectRecordAdapter.notifyDataSetChanged();
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    private void initViews() {
        leftImg = findViewById(R.id.commArrowLeft);
        rightImg = findViewById(R.id.commArrowRight);
        titleTv = findViewById(R.id.commentB30TitleTv);
        dateTv = findViewById(R.id.commArrowDate);
        commBackImg = findViewById(R.id.commentB30BackImg);

        recyclerView = findViewById(R.id.ecgDetectRecordRecyclerView);

        titleTv.setText("ECG记录");
        commBackImg.setVisibility(View.VISIBLE);

        leftImg.setOnClickListener(this);
        rightImg.setOnClickListener(this);
        commBackImg.setOnClickListener(this);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        list = new ArrayList<>();
        ecgDetectRecordAdapter = new EcgDetectRecordAdapter(this,list,R.layout.item_ecg_record_layout);
        recyclerView.setAdapter(ecgDetectRecordAdapter);
        ecgDetectRecordAdapter.setmOnItemListener(this);

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.commentB30BackImg:
                finish();
                break;
            case R.id.commArrowLeft:
                changeDayData(true);
                break;
            case R.id.commArrowRight:
                changeDayData(false);
                break;
        }
    }


    private void changeDayData(boolean left) {
        String date = WatchUtils.obtainAroundDate(currDay, left);
        if (date.equals(currDay) || date.isEmpty()) {
            return;// 空数据,或者大于今天的数据就别切了
        }
        currDay = date;
        queryDbEcgData(currDay);
    }

    @Override
    public void onItemClickListener(View view, int position) {
        if(list.isEmpty())
            return;
        EcgSourceBean ecgSourceBean = list.get(position);
        startActivity(ShowEcgDataActivity.class,new String[]{"ecg_time","ecg_desc","ecg_source"},new String[]{ecgSourceBean.getDetectDate()+" "+ecgSourceBean.getDetectTime(),ecgSourceBean.getEcgDetectStateBeanStr(),ecgSourceBean.getEcgListStr()});
    }

    @Override
    public void onLongClickListener(View view, int position) {

    }
}
