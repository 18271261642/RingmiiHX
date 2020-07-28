package com.guider.healthring.b30;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.guider.healthring.MyApp;
import com.guider.healthring.R;
import com.guider.healthring.b30.adapter.B30HeartDetailAdapter;
import com.guider.healthring.b30.b30view.B30CusHeartView;
import com.guider.healthring.b30.bean.B30HalfHourDao;
import com.guider.healthring.siswatch.WatchBaseActivity;
import com.guider.healthring.siswatch.utils.WatchUtils;
import com.guider.healthring.util.Constant;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.veepoo.protocol.model.datas.HalfHourRateData;
import com.veepoo.protocol.model.datas.HalfHourSportData;
import com.veepoo.protocol.model.datas.TimeData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * B30心率详情界面
 */
public class B30HeartDetailActivity extends WatchBaseActivity implements View.OnClickListener{

    /**
     * 跳转到B30HeartDetailActivity,并附带参数
     *
     * @param context 启动源上下文
     * @param date    附带的参数:日期
     */
    public static void startAndParams(Context context, String date) {
        Intent intent = new Intent(context, B30HeartDetailActivity.class);
        intent.putExtra(Constant.DETAIL_DATE, date);
        context.startActivity(intent);
    }

    ImageView commentB30BackImg;
    TextView commentB30TitleTv;
    ImageView commentB30ShareImg;
    B30CusHeartView b30HeartDetailView;
    RecyclerView b30HeartDetailRecyclerView;
    TextView rateCurrdateTv;
    private List<HalfHourRateData> halfHourRateDatasList;
    private List<HalfHourSportData> halfHourSportDataList;
    private B30HeartDetailAdapter b30HeartDetailAdapter;

    /**
     * 心率图标数据
     */
    List<Integer> heartList;

    /**
     * 当前显示的日期(数据根据日期加载)
     */
    private String currDay;
    /**
     * Json工具类
     */
    private Gson gson;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b30_heart_detail_layout);
        initViewIds();
        initViews();
        initData();
    }

    private void initViewIds() {
        commentB30BackImg = findViewById(R.id.commentB30BackImg);
        commentB30TitleTv = findViewById(R.id.commentB30TitleTv);
        commentB30ShareImg = findViewById(R.id.commentB30ShareImg);
        b30HeartDetailView = findViewById(R.id.b30HeartDetailView);
        b30HeartDetailRecyclerView = findViewById(R.id.b30HeartDetailRecyclerView);
        rateCurrdateTv = findViewById(R.id.rateCurrdateTv);
        commentB30BackImg.setOnClickListener(this);
        commentB30ShareImg.setOnClickListener(this);
        findViewById(R.id.rateCurrDateLeft).setOnClickListener(this);
        findViewById(R.id.rateCurrDateRight).setOnClickListener(this);
    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(R.string.heart_rate);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        b30HeartDetailRecyclerView.setLayoutManager(layoutManager);
        b30HeartDetailRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        halfHourRateDatasList = new ArrayList<>();
        halfHourSportDataList = new ArrayList<>();
        b30HeartDetailAdapter = new B30HeartDetailAdapter(halfHourRateDatasList, halfHourSportDataList, this);
        b30HeartDetailRecyclerView.setAdapter(b30HeartDetailAdapter);

        heartList = new ArrayList<>();
        gson = new Gson();
        currDay = getIntent().getStringExtra(Constant.DETAIL_DATE);
    }

    private void initData() {
        rateCurrdateTv.setText(currDay);
        String mac = MyApp.getInstance().getMacAddress();
        String rate = B30HalfHourDao.getInstance().findOriginData(mac, currDay, B30HalfHourDao
                .TYPE_RATE);
        List<HalfHourRateData> rateData = gson.fromJson(rate, new TypeToken<List<HalfHourRateData>>() {
        }.getType());

        String sport = B30HalfHourDao.getInstance().findOriginData(mac, currDay, B30HalfHourDao
                .TYPE_SPORT);
        List<HalfHourSportData> sportData = gson.fromJson(sport, new TypeToken<List<HalfHourSportData>>() {
        }.getType());

        halfHourRateDatasList.clear();
        halfHourSportDataList.clear();
//        MyLogUtil.d("------------", rateData.size() + "========" + sportData.size());

        List<Map<String, Integer>> listMap = new ArrayList<>();
        if (rateData != null && !rateData.isEmpty()) {
            int k = 0;
            for (int i = 0; i < 48; i++) {
                Map<String, Integer> map = new HashMap<>();
                int time = i * 30;
                map.put("time", time);
                TimeData tmpDate = rateData.get(k).getTime();
                int tmpIntDate = tmpDate.getHMValue();

                if (tmpIntDate == time) {
                    map.put("val", rateData.get(k).getRateValue());
                    if (k < rateData.size() - 1) {
                        k++;
                    }
                } else {
                    map.put("val", 0);
                }
                listMap.add(map);
            }
            Collections.sort(rateData, new Comparator<HalfHourRateData>() {
                @Override
                public int compare(HalfHourRateData o1, HalfHourRateData o2) {
                    return o2.getTime().getColck().compareTo(o1.getTime().getColck());
                }
            });

            Collections.sort(sportData, new Comparator<HalfHourSportData>() {
                @Override
                public int compare(HalfHourSportData o1, HalfHourSportData o2) {
                    return o2.getTime().getColck().compareTo(o1.getTime().getColck());
                }
            });
            halfHourRateDatasList.addAll(rateData);
            halfHourSportDataList.addAll(sportData);
        }
        heartList.clear();
        for (int i = 0; i < listMap.size(); i++) {
            Map<String, Integer> map = listMap.get(i);
            heartList.add(map.get("val"));
        }
        //圆点的半径
//        b30HeartDetailView.setPointRadio(5);
        //绘制标线
        b30HeartDetailView.setCanvasBeanLin(true);
        b30HeartDetailView.setRateDataList(heartList);

        b30HeartDetailAdapter.notifyDataSetChanged();
    }

   @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg: //返回
                finish();
                break;
            case R.id.commentB30ShareImg:
                WatchUtils.shareCommData(B30HeartDetailActivity.this);
                break;
            case R.id.rateCurrDateLeft:   //切换上一天数据
                changeDayData(true);
                break;
            case R.id.rateCurrDateRight:   //切换下一天数据
                changeDayData(false);
                break;
        }
    }

    /**
     * 根据日期切换数据
     */
    private void changeDayData(boolean left) {
        String date = WatchUtils.obtainAroundDate(currDay, left);
        if (date.equals(currDay) || date.isEmpty()) {
            return;// 空数据,或者大于今天的数据就别切了
        }
        currDay = date;
        initData();
    }

}
