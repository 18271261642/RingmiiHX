package com.guider.healthring.b30;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.guider.healthring.MyApp;
import com.guider.healthring.R;
import com.guider.healthring.b30.adapter.B30StepDetailAdapter;
import com.guider.healthring.b30.bean.B30HalfHourDao;
import com.guider.healthring.siswatch.WatchBaseActivity;
import com.guider.healthring.siswatch.utils.WatchUtils;
import com.guider.healthring.util.Constant;
import com.guider.healthring.util.LocalizeTool;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.veepoo.protocol.model.datas.HalfHourSportData;
import com.veepoo.protocol.model.datas.TimeData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/8/4.
 */

public class B30StepDetailActivity extends WatchBaseActivity implements View.OnClickListener{

    /**
     * 跳转到B30StepDetailActivity,并附带参数
     *
     * @param context 启动源上下文
     * @param date    附带的参数:日期
     */
    public static void startAndParams(Context context, String date) {
        Intent intent = new Intent(context, B30StepDetailActivity.class);
        intent.putExtra(Constant.DETAIL_DATE, date);
        context.startActivity(intent);
    }

    ImageView commentB30BackImg;
    TextView commentB30TitleTv;
    ImageView commentB30ShareImg;
    RelativeLayout b30ChartTopRel;
    BarChart b30BarChart;
    LinearLayout b30SportChartLin1;
    RecyclerView b30StepDetailRecyclerView;
    TextView countStepTv;
    TextView countDisTv;
    TextView countKcalTv;
    TextView stepCurrDateTv;

    /**
     * 列表数据源
     */
    private List<HalfHourSportData> dataList = new ArrayList<>();
    /**
     * 列表适配器
     */
    private B30StepDetailAdapter b30StepDetailAdapter;
    /**
     * 步数数据
     */
    List<BarEntry> b30ChartList = new ArrayList<>();
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
        setContentView(R.layout.activity_b30_step_detail_layout);
        initViewIds();
        initViews();
        initData();
    }

    private void initViewIds() {
        commentB30BackImg = findViewById(R.id.commentB30BackImg);
        commentB30TitleTv = findViewById(R.id.commentB30TitleTv);
        commentB30ShareImg = findViewById(R.id.commentB30ShareImg);
        b30ChartTopRel = findViewById(R.id.b30ChartTopRel);
        b30BarChart = findViewById(R.id.b30BarChart);
        b30SportChartLin1 = findViewById(R.id.b30SportChartLin1);
        b30StepDetailRecyclerView = findViewById(R.id.b30StepDetailRecyclerView);
        countStepTv = findViewById(R.id.countStepTv);
        countDisTv = findViewById(R.id.countDisTv);
        countKcalTv = findViewById(R.id.countKcalTv);
        stepCurrDateTv = findViewById(R.id.stepCurrDateTv);
        commentB30BackImg.setOnClickListener(this);
        commentB30ShareImg.setOnClickListener(this);
        findViewById(R.id.stepCurrDateLeft).setOnClickListener(this);
        findViewById(R.id.stepCurrDateRight).setOnClickListener(this);

    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(R.string.move_ment);
//        commentB30ShareImg.setVisibility(View.VISIBLE);
        b30ChartTopRel.setVisibility(View.GONE);
        b30SportChartLin1.setBackgroundColor(Color.parseColor("#2594EE"));
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager
                .VERTICAL, true);
        b30StepDetailRecyclerView.setLayoutManager(layoutManager);
        b30StepDetailRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        b30StepDetailAdapter = new B30StepDetailAdapter(this, dataList);
        b30StepDetailRecyclerView.setAdapter(b30StepDetailAdapter);
        gson = new Gson();
        currDay = getIntent().getStringExtra(Constant.DETAIL_DATE);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        stepCurrDateTv.setText(currDay);
        String mac = MyApp.getInstance().getMacAddress();
        String sport = B30HalfHourDao.getInstance().findOriginData(mac, currDay, B30HalfHourDao
                .TYPE_SPORT);
        Log.e("步数详细","-------sport-="+sport);
        List<HalfHourSportData> sportData = gson.fromJson(sport, new TypeToken<List<HalfHourSportData>>() {
        }.getType());
        if (sportData == null) sportData = new ArrayList<>();
        showBarChart(sportData);
        String step = B30HalfHourDao.getInstance().findOriginData(mac, currDay, B30HalfHourDao
                .TYPE_STEP);
        countStepTv.setText(step == null ? "0" : step);// 本地步数
        showListData(sportData);
    }

    /**
     * 显示图表
     */
    private void showBarChart(List<HalfHourSportData> sportData) {
        List<Map<String, Integer>> listMap = new ArrayList<>();
        if (sportData != null && !sportData.isEmpty()) {
            int k = 0;
            for (int i = 0; i < 48; i++) {
                Map<String, Integer> map = new HashMap<>();
                int time = i * 30;
                map.put("time", time);
                TimeData tmpDate = sportData.get(k).getTime();
                int tmpIntDate = tmpDate.getHMValue();
                if (tmpIntDate == time) {
                    map.put("val", sportData.get(k).getStepValue());
                    if (k < sportData.size() - 1) {
                        k++;
                    }
                } else {
                    map.put("val", 0);
                }
                listMap.add(map);
            }
        }
        b30ChartList.clear();
        for (int i = 0; i < listMap.size(); i++) {
            Map<String, Integer> tmpMap = listMap.get(i);
            b30ChartList.add(new BarEntry(i, tmpMap.get("val")));
        }
        initBarChart(b30ChartList);
        b30BarChart.invalidate();
    }

    /**
     * 步数图表展示
     */
    private void initBarChart(List<BarEntry> pointBar) {
        BarDataSet barDataSet = new BarDataSet(pointBar, "");
        barDataSet.setDrawValues(false);//是否显示柱子上面的数值
        barDataSet.setColor(Color.WHITE);//设置第一组数据颜色

        Legend mLegend = b30BarChart.getLegend();
        mLegend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);//设置注解的位置在右
        mLegend.setForm(Legend.LegendForm.CIRCLE);// 样式
        mLegend.setFormSize(15.0f);// 字体
        mLegend.setTextColor(Color.WHITE);// 颜色
        mLegend.setEnabled(false);

        ArrayList<IBarDataSet> threeBarData = new ArrayList<>();//IBarDataSet 接口很关键，是添加多组数据的关键结构，LineChart也是可以采用对应的接口类，也可以添加多组数据
        threeBarData.add(barDataSet);

        BarData bardata = new BarData(threeBarData);
        bardata.setBarWidth(0.5f);  //设置柱子宽度

        b30BarChart.setData(bardata);
        b30BarChart.setDoubleTapToZoomEnabled(false);   //双击缩放
        b30BarChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        b30BarChart.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        //设置注解的位置在左上方
        b30BarChart.getLegend().setForm(Legend.LegendForm.CIRCLE);//这是左边显示小图标的形状

        b30BarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);//设置X轴的位置
        b30BarChart.getXAxis().setDrawGridLines(false);//不显示网格
        b30BarChart.getXAxis().setEnabled(false);

        b30BarChart.getDescription().setEnabled(false);

        b30BarChart.getAxisRight().setEnabled(false);//右侧不显示Y轴
        b30BarChart.getAxisLeft().setAxisMinValue(0.8f);//设置Y轴显示最小值，不然0下面会有空隙
        b30BarChart.getAxisLeft().setDrawGridLines(false);//不设置Y轴网格
        b30BarChart.getAxisLeft().setEnabled(false);
        b30BarChart.getXAxis().setSpaceMax(0.5f);
        b30BarChart.animateXY(1000, 2000);//设置动画
    }

    /**
     * 显示列表数据
     */
    private void showListData(List<HalfHourSportData> sportData) {
        if (dataList != null) dataList.clear();
        else dataList = new ArrayList<>();
        if (sportData != null) dataList.addAll(sportData);
        if (!dataList.isEmpty()) {
            for (int i = dataList.size() - 1; i >= 0; i--) {
                if (dataList.get(i).getStepValue() <= 0) {
                    dataList.remove(i);
                }
            }
        }
        b30StepDetailAdapter.notifyDataSetChanged();
        double calValue = 0;
        double disValue = 0;
        if (sportData != null) {
            for (HalfHourSportData item : sportData) {
                calValue += item.getCalValue();
                disValue += item.getDisValue();
            }
        }
        calValue = (double) Math.round(calValue * 100) / 100;
        countKcalTv.setText("" + calValue + "kcl");
        // 算一下距离,按公制英制来算
        boolean isMetric = new LocalizeTool(this).getMetricSystem();
        if (!isMetric) disValue = disValue * Constant.METRIC_MILE;
        disValue = (double) Math.round(disValue * 100) / 100;
        Log.d("bobo", "calValue: " + calValue + ",disValue: " + disValue);
        String disStr = isMetric ? getString(R.string.mileage_setkm) : getString(R.string
                .mileage_setmi);
        countDisTv.setText(disValue + disStr);
    }

   @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:
                finish();
                break;
            case R.id.commentB30ShareImg:   //分享
                WatchUtils.shareCommData(B30StepDetailActivity.this);
                break;
            case R.id.stepCurrDateLeft:   //切换上一天数据
                changeDayData(true);
                break;
            case R.id.stepCurrDateRight:   //切换下一天数据
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
