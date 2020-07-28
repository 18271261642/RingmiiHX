package com.guider.healthring.b31.hrv;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.guider.healthring.R;
import com.guider.healthring.b31.bpoxy.Spo2SecondDialogView;
import com.guider.healthring.b31.bpoxy.markview.SPMarkerView;
import com.guider.healthring.b31.bpoxy.util.ChartViewUtil;
import com.guider.healthring.b31.bpoxy.util.HrvDescripterUtil;
import com.guider.healthring.b31.model.B31HRVBean;
import com.guider.healthring.siswatch.WatchBaseActivity;
import com.guider.healthring.siswatch.utils.WatchUtils;
import com.guider.healthring.util.Constant;
import com.github.mikephil.charting.charts.LineChart;
import com.google.gson.Gson;
import com.veepoo.protocol.model.datas.HRVOriginData;
import com.veepoo.protocol.util.HRVOriginUtil;
import com.veepoo.protocol.util.HrvScoreUtil;
import com.veepoo.protocol.view.LorenzChartView;
import com.vp.cso.hrvreport.JNIChange;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.guider.healthring.b31.bpoxy.enums.Constants.CHART_MAX_HRV;
import static com.guider.healthring.b31.bpoxy.enums.Constants.CHART_MIDDLE_HRV;
import static com.guider.healthring.b31.bpoxy.enums.Constants.CHART_MIN_HRV;
import static com.veepoo.protocol.model.enums.ESpo2hDataType.TYPE_HRV;

/**
 * Created by Admin
 * Date 2018/12/19
 */
public class B31HrvDetailActivity extends WatchBaseActivity implements View.OnClickListener{

    private static final String TAG = "B31HrvDetailActivity";
    ImageView hrvReferMarkviewImg;
    ListView lorenzListDescripe;
    //心脏健康指数
    TextView hrvDetailHeartSocreTv;
    ImageView hrvReferBackImg;
    TextView hrvNoLeroDescTv;

    LorenzChartView lorezChartView;
    ImageView commentB30BackImg;
    TextView commentB30TitleTv;
    //折线图
    LineChart b31HrvDetailTopChart;
    //markview
    SPMarkerView mMarkviewHrv;
    TextView commArrowDate;
    RecyclerView hrvDataRrecyclerView;
    LinearLayout hrvLerozenLin;
    ConstraintLayout hrvListDataConLy;
    TextView herLerzeoTv;
    TextView herDataTv;
    Toolbar relaLayoutTitle;

    private List<HRVOriginData> list;

    private Gson gson = new Gson();

    private String currDay;

    //适配器
    private HrvListDataAdapter hrvListDataAdapter;

    private HrvDescDialogView hrvDescDialogView;
    private List<Map<String, Float>> listMap;

    JNIChange mJNIChange;

    private Spo2SecondDialogView spo2SecondDialogView;
    HRVOriginUtil mHrvOriginUtil;


    public static void startAndParams(Context context, String date) {
        Intent intent = new Intent(context, B31HrvDetailActivity.class);
        intent.putExtra(Constant.DETAIL_DATE, date);
        context.startActivity(intent);
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.e(TAG,"-----msg="+msg.what);
            switch (msg.what) {
                case 1001:
                    List<HRVOriginData> resultHrv = (List<HRVOriginData>) msg.obj;
                    initLinChartData(resultHrv);
                    lorezChartView.updateData(resultHrv);
                    //分析报告界面
                    showResult(resultHrv);
                    break;
                case 1002:
                    initLinChartData(tmpHRVlist);
                    lorezChartView.updateData(tmpHRVlist);
                    listMap.clear();
                    hrvListDataAdapter.notifyDataSetChanged();
                    //分析报告界面
                    showResult(tmpHRVlist);
                    break;

            }

        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b31_hrv_detail);
        initViewIds();
        initViews();


        initAdapter();
        currDay = getIntent().getStringExtra(Constant.DETAIL_DATE);
        updateList(new int[]{0, 11, 32, 23, 14, 0});

        findDataFromDb(currDay);


    }

    private void initViewIds() {
        hrvReferMarkviewImg = findViewById(R.id.hrvReferMarkviewImg);
        lorenzListDescripe = findViewById(R.id.lorenz_list_descripe);
        hrvDetailHeartSocreTv = findViewById(R.id.hrvDetailHeartSocreTv);
        hrvReferBackImg = findViewById(R.id.hrvReferBackImg);
        hrvNoLeroDescTv = findViewById(R.id.hrvNoLeroDescTv);
        lorezChartView = findViewById(R.id.lorezChartView);
        commentB30BackImg = findViewById(R.id.commentB30BackImg);
        commentB30TitleTv = findViewById(R.id.commentB30TitleTv);
        b31HrvDetailTopChart = findViewById(R.id.b31HrvDetailTopChart);
        commArrowDate = findViewById(R.id.commArrowDate);
        hrvDataRrecyclerView = findViewById(R.id.hrvDataRrecyclerView);
        hrvLerozenLin = findViewById(R.id.hrvLerozenLin);
        hrvListDataConLy = findViewById(R.id.hrvListDataConLy);
        herLerzeoTv = findViewById(R.id.herLerzeoTv);
        herDataTv = findViewById(R.id.herDataTv);
        relaLayoutTitle = findViewById(R.id.commB31TitleLayout);

        findViewById(R.id.commArrowLeft).setOnClickListener(this);
        findViewById(R.id.commArrowRight).setOnClickListener(this);
        commentB30BackImg.setOnClickListener(this);
        herLerzeoTv.setOnClickListener(this);
        herDataTv.setOnClickListener(this);
        findViewById(R.id.hrvType1).setOnClickListener(this);
        findViewById(R.id.hrvType2).setOnClickListener(this);
        findViewById(R.id.hrvType3).setOnClickListener(this);
        findViewById(R.id.hrvType4).setOnClickListener(this);
        findViewById(R.id.hrvType5).setOnClickListener(this);
        findViewById(R.id.hrvType6).setOnClickListener(this);
        findViewById(R.id.hrvType7).setOnClickListener(this);
        findViewById(R.id.hrvType8).setOnClickListener(this);
        findViewById(R.id.hrvType9).setOnClickListener(this);
    }


    private void showResult(List<HRVOriginData> originHRVList) {
        if (originHRVList == null || originHRVList.isEmpty()) {
            lorenzListDescripe.setVisibility(View.GONE);
            hrvNoLeroDescTv.setVisibility(View.VISIBLE);
            return;
        }
        lorenzListDescripe.setVisibility(View.VISIBLE);
        hrvNoLeroDescTv.setVisibility(View.GONE);

        HrvScoreUtil hrvScoreUtil = new HrvScoreUtil();
        double[] lorenData = hrvScoreUtil.getLorenData(originHRVList);
        if (lorenData == null || lorenData.length < 1500) {
            return;
        }
        int[] bufferdata = new int[lorenData.length];
        for (int i = 0; i < bufferdata.length; i++) {
            bufferdata[i] = (int) lorenData[i];
        }
        int[] result = null;
        try {
            result = mJNIChange.hrvAnalysisReport(bufferdata, bufferdata.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (result == null) {
            return;
        }
        updateList(result);
    }


    private void updateList(int[] result) {
        getRepoListData(result).clear();
        setListViewHeightBasedOnChildren(lorenzListDescripe);
        List<Map<String, Object>> repoListData = getRepoListData(result);
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, repoListData,
                R.layout.vpspo2h_item_lorendes, new String[]{"title", "info", "level"},
                new int[]{R.id.loren_descripe_title, R.id.loren_descripe_info, R.id.loren_descripe_level});
        lorenzListDescripe.setAdapter(simpleAdapter);
        lorenzListDescripe.setOnItemClickListener(null);
        lorenzListDescripe.setFocusable(false);
    }

    private void transHrvMarkImg(int value) {
        int backImgWidth = hrvReferBackImg.getWidth();
        int currV = backImgWidth / 10;
        TranslateAnimation translateAnimation = new TranslateAnimation(0, value == 0 ? 0 : backImgWidth - 40 - (100 - value) / 10 * currV + (hrvReferMarkviewImg.getWidth() / 2),
                Animation.ABSOLUTE,
                Animation.ABSOLUTE);
        translateAnimation.setDuration(3 * 1000);
        translateAnimation.setFillAfter(true);
        hrvReferMarkviewImg.startAnimation(translateAnimation);


    }


    private void initAdapter() {
        mJNIChange = new JNIChange();
        list = new ArrayList<>();
        listMap = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        hrvDataRrecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        hrvDataRrecyclerView.setLayoutManager(linearLayoutManager);
        hrvDataRrecyclerView.setNestedScrollingEnabled(false);
        hrvListDataAdapter = new HrvListDataAdapter(listMap, B31HrvDetailActivity.this);
        hrvDataRrecyclerView.setAdapter(hrvListDataAdapter);
        hrvListDataAdapter.setHrvItemClickListener(hrvItemClickListener);

    }

    private void initLorezView() {

        lorezChartView.setTextSize(80);
        lorezChartView.setTextColor(Color.RED);
        lorezChartView.setDotColor(Color.RED);
        lorezChartView.setDotSize(5);
        lorezChartView.setLineWidth(8);
        lorezChartView.setLineColor(Color.RED);


    }

    private void initViews() {
        commArrowDate.setText(currDay);
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText("HRV");
        relaLayoutTitle.setBackgroundColor(Color.parseColor("#ECA83D"));

        mMarkviewHrv = new SPMarkerView(getApplicationContext(), R.layout.vpspo2h_markerview,
                true, CHART_MIDDLE_HRV, TYPE_HRV);
        clearHrvStyle(0);
        initLorezView();


    }

    List<HRVOriginData> tmpHRVlist = new ArrayList<>();

    private void findDataFromDb(final String currDay) {
        commArrowDate.setText(currDay);
        tmpHRVlist.clear();
        final String mac = WatchUtils.getSherpBleMac(B31HrvDetailActivity.this);
        if(WatchUtils.isEmpty(mac))
            return;
        showLoadingDialog("Loading...");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    String where = "bleMac = ? and dateStr = ?";
                    List<B31HRVBean> reList = LitePal.where(where, mac,currDay).find(B31HRVBean.class);
                    if (reList == null || reList.isEmpty()) {
                        Message message = handler.obtainMessage();
                        message.what = 1002;
                        message.obj = tmpHRVlist;
                        handler.sendMessage(message);
                        return;
                    }
                    for (B31HRVBean hrvBean : reList) {
                        // Log.e(TAG,"----------hrvBean="+hrvBean.toString());
                        tmpHRVlist.add(gson.fromJson(hrvBean.getHrvDataStr(), HRVOriginData.class));
                    }

                    Message message = handler.obtainMessage();
                    message.what = 1001;
                    message.obj = tmpHRVlist;
                    handler.sendMessage(message);
                }catch (Exception e){
                    e.printStackTrace();
                }


            }
        });
        thread.start();

    }

    private void initLinChartData(List<HRVOriginData> originHRVList) {

        listMap.clear();
        //心脏健康指数
        HrvScoreUtil hrvScoreUtil = new HrvScoreUtil();
        int heartSocre = hrvScoreUtil.getSocre(originHRVList);
        transHrvMarkImg(heartSocre);
        hrvDetailHeartSocreTv.setText(heartSocre + "");
        //折线图的
        List<HRVOriginData> data0to8 = getMoringData(originHRVList);
        mHrvOriginUtil = new HRVOriginUtil(data0to8);

        List<Map<String, Float>> tenMinuteData = mHrvOriginUtil.getTenMinuteData();

        ChartViewUtil chartViewUtil = new ChartViewUtil(b31HrvDetailTopChart, mMarkviewHrv, true,
                CHART_MAX_HRV, CHART_MIN_HRV, "No Data", TYPE_HRV);
        chartViewUtil.updateChartView(tenMinuteData);
        mMarkviewHrv.setData(tenMinuteData);
        closeLoadingDialog();
        listMap.addAll(tenMinuteData);
        hrvListDataAdapter.notifyDataSetChanged();

    }


    /**
     * 获取0点-8点之间的数据
     *
     * @param originSpo2hList
     * @return
     */
    @NonNull
    private List<HRVOriginData> getMoringData(List<HRVOriginData> originSpo2hList) {
        List<HRVOriginData> moringData = new ArrayList<>();
        for (com.veepoo.protocol.model.datas.HRVOriginData HRVOriginData : originSpo2hList) {
            if (HRVOriginData.getmTime().getHMValue() < 8 * 60) {
                moringData.add(HRVOriginData);
            }
        }
        return moringData;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:    //返回
                finish();
                break;
            case R.id.commArrowLeft:    //前一天
                changeCurrDay(true);
                break;
            case R.id.commArrowRight:   //后一天
                changeCurrDay(false);
                break;
            case R.id.herLerzeoTv:  //图表展示
                clearHrvStyle(0);
                break;
            case R.id.herDataTv:    //列表数据
                clearHrvStyle(1);
                break;
            case R.id.hrvType1:
                showHrvDescDialog(getResources().getString(R.string.vphrv_lorentz_chart_1),
                        getResources().getString(R.string.vphrv_lorentz_chart_des_1),R.drawable.hrv_gradivew_1_big);
                break;
            case R.id.hrvType2:
                showHrvDescDialog(getResources().getString(R.string.vphrv_lorentz_chart_2),
                        getResources().getString(R.string.vphrv_lorentz_chart_des_2),R.drawable.hrv_gradivew_2_big);
                break;
            case R.id.hrvType3:
                showHrvDescDialog(getResources().getString(R.string.vphrv_lorentz_chart_3),
                        getResources().getString(R.string.vphrv_lorentz_chart_des_3),R.drawable.hrv_gradivew_3_big);
                break;
            case R.id.hrvType4:
                showHrvDescDialog(getResources().getString(R.string.vphrv_lorentz_chart_4),
                        getResources().getString(R.string.vphrv_lorentz_chart_des_4),R.drawable.hrv_gradivew_4_big);
                break;
            case R.id.hrvType5:
                showHrvDescDialog(getResources().getString(R.string.vphrv_lorentz_chart_5),
                        getResources().getString(R.string.vphrv_lorentz_chart_des_5),R.drawable.hrv_gradivew_5_big);
                break;
            case R.id.hrvType6:
                showHrvDescDialog(getResources().getString(R.string.vphrv_lorentz_chart_6),
                        getResources().getString(R.string.vphrv_lorentz_chart_des_6),R.drawable.hrv_gradivew_6_big);
                break;
            case R.id.hrvType7:
                showHrvDescDialog(getResources().getString(R.string.vphrv_lorentz_chart_7),
                        getResources().getString(R.string.vphrv_lorentz_chart_des_7),R.drawable.hrv_gradivew_7_big);
                break;
            case R.id.hrvType8:
                showHrvDescDialog(getResources().getString(R.string.vphrv_lorentz_chart_8),
                        getResources().getString(R.string.vphrv_lorentz_chart_des_8),R.drawable.hrv_gradivew_8_big);
                break;
            case R.id.hrvType9:
                showHrvDescDialog(getResources().getString(R.string.vphrv_lorentz_chart_9),
                        getResources().getString(R.string.vphrv_lorentz_chart_des_9),R.drawable.hrv_gradivew_9_big);
                break;
        }
    }

    private void showHrvDescDialog(String titleId,String descTxt, int drawable) {
        if(hrvDescDialogView == null){
            hrvDescDialogView = new HrvDescDialogView(B31HrvDetailActivity.this);
        }else{
            hrvDescDialogView.cancel();
            hrvDescDialogView = new HrvDescDialogView(B31HrvDetailActivity.this);
        }

        hrvDescDialogView.show();
        hrvDescDialogView.setHrvDescTitleTxt(titleId);
        hrvDescDialogView.setHrvDescContent(descTxt);
        hrvDescDialogView.setHrvDescImg(getResources().getDrawable(drawable));
        hrvDescDialogView.setHrvDescDialogListener(new HrvDescDialogView.HrvDescDialogListener() {
            @Override
            public void cancleDialog() {
                hrvDescDialogView.dismiss();
            }
        });


    }

    private void clearHrvStyle(int code) {
        clearAll();
        switch (code) {
            case 0:
                herLerzeoTv.setBackgroundColor(Color.parseColor("#ECA83D"));
                herLerzeoTv.setTextColor(Color.WHITE);
                hrvLerozenLin.setVisibility(View.VISIBLE);
                hrvListDataConLy.setVisibility(View.GONE);
                break;
            case 1:
                herDataTv.setBackgroundColor(Color.parseColor("#ECA83D"));
                herDataTv.setTextColor(Color.WHITE);
                hrvLerozenLin.setVisibility(View.GONE);
                hrvListDataConLy.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void clearAll() {
        herLerzeoTv.setBackgroundColor(Color.WHITE);
        herLerzeoTv.setTextColor(getResources().getColor(R.color.contents_text));
        herDataTv.setBackgroundColor(Color.WHITE);
        herDataTv.setTextColor(getResources().getColor(R.color.contents_text));

    }


    private void changeCurrDay(boolean isDay) {
        String date = WatchUtils.obtainAroundDate(currDay, isDay);
        if (date.equals(currDay) || date.isEmpty()) {
            return;// 空数据,或者大于今天的数据就别切了
        }
        currDay = date;
        findDataFromDb(currDay);

    }

    private List<Map<String, Object>> getRepoListData(int[] data) {
        List<Map<String, Object>> list = new ArrayList<>();
        if (data == null || data.length == 0) {
            return list;
        }
        HrvDescripterUtil hrv = new HrvDescripterUtil(getApplicationContext());
        String[] repoTitle = hrv.getRepoTitle();
        for (int i = 1; i < data.length - 1; i++) {
            Map<String, Object> map = new HashMap<>();
            int mapInt = (i + 1) * 100 + +data[i];
            map.put("title", repoTitle[i]);
            map.put("info", hrv.getRepoInfo(mapInt));
            map.put("level", hrv.getLevel(mapInt));
            list.add(map);
        }
        return list;
    }


    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter;
        listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }


    private HrvListDataAdapter.HrvItemClickListener hrvItemClickListener = new HrvListDataAdapter.HrvItemClickListener() {
        @Override
        public void hrvItemClick(int position) {
            if(spo2SecondDialogView == null){
                spo2SecondDialogView = new Spo2SecondDialogView(B31HrvDetailActivity.this);
            }
            List<Map<String,Float>> lt = mHrvOriginUtil.getDetailList(listMap.size()-position-1);
            spo2SecondDialogView.show();
            spo2SecondDialogView.setSpo2Type(555);
            spo2SecondDialogView.setMapList(lt);
            //spo2SecondDialogView.setHRVUtils(mHrvOriginUtil,listMap.size()-position-1);
        }
    };

}
