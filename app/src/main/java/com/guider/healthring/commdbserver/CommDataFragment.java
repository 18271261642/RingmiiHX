package com.guider.healthring.commdbserver;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.formatter.ValueFormatter;
import com.guider.healthring.Commont;
import com.guider.healthring.MyApp;
import com.guider.healthring.R;
import com.guider.healthring.b30.b30view.B30BloadDataView;
import com.guider.healthring.b30.view.DataMarkView;
import com.guider.healthring.siswatch.LazyFragment;
import com.guider.healthring.siswatch.data.BarXFormartValue;
import com.guider.healthring.siswatch.utils.WatchUtils;
import com.guider.healthring.util.GetJsonDataUtil;
import com.guider.healthring.util.SharedPreferencesUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.gson.Gson;

import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Admin
 * Date 2019/3/14
 */
public class CommDataFragment extends LazyFragment implements View.OnClickListener {

    private static final String TAG = "CommDataFragment";

    View commView;
    ImageView commentB30BackImg;
    TextView commentB30TitleTv;
    TextView b30DataWeekTv;
    TextView b30DataMonthTv;
    TextView b30DataYearTv;
    TextView stepDataValueTv;
    BarChart stepDataChartView;
    TextView sleepDataValueTv;
    BarChart sleepDataChartView;
    TextView heartDataValueTv;
    BarChart heartDataChartView;
    TextView bloodDataValueTv;
    //血压的图表
    B30BloadDataView charBloadView;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA);
    RelativeLayout b30BloadChartLin;

    private Gson gson = new Gson();
    //步数相关
    private List<Integer> mValues;
    List<BarEntry> pointbar;
    private List<String> xStepList;

    //心率相关

    private List<Integer> heartValues;
    /**
     * 心率X轴数据
     */
    private List<String> heartXList;
    private List<BarEntry> heartBarEntryList;


    /**
     * 睡眠的数值
     */
    private List<Float> sleepVlaues;
    /**
     * 睡眠X轴
     */
    private List<String> sleepXList;
    private List<BarEntry> sleepBarEntryList;


    //血压的时间数据
    private ArrayList<String> bloodDateList = new ArrayList<>();
    //临时的血压数据源
    private ArrayList<String> tempBloodDateList = new ArrayList<>();


    //血压的数据源
    private List<SparseIntArray> bloodList = new ArrayList<>();

    String filtPath = Environment.getExternalStorageDirectory() + "/DCIM/";

    private SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    private SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd", Locale.CHINA);
    private SimpleDateFormat sdf3 = new SimpleDateFormat("yy/MM", Locale.CHINA); //yy/MM格式
    private int SectectCode = 7;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x01:      //步数
                    if (getActivity() != null && !getActivity().isFinishing())
                        showStepsChat(mValues, xStepList);
                    break;
                case 0x02:  //心率
                    if (getActivity() != null && !getActivity().isFinishing())
                        showHeartChart(heartValues, heartXList);
                    break;
                case 0x03:  //睡眠
                    if (getActivity() != null && !getActivity().isFinishing())
                        showSleepChart(sleepVlaues, sleepXList);
                    break;
                case 0x04:  //血压
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        charBloadView.updateView(bloodList, bloodDateList);
                        charBloadView.setScal(false);
                    }
                    break;
            }
        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        commView = inflater.inflate(R.layout.fragment_b30_data, container, false);
        initViewIds();
        initViews();
        initData();


        return commView;
    }

    private void initViewIds() {
        commentB30BackImg = commView.findViewById(R.id.commentB30BackImg);
        commentB30TitleTv = commView.findViewById(R.id.commentB30TitleTv);
        b30DataWeekTv = commView.findViewById(R.id.b30DataWeekTv);
        b30DataMonthTv = commView.findViewById(R.id.b30DataMonthTv);
        b30DataYearTv = commView.findViewById(R.id.b30DataYearTv);
        stepDataValueTv = commView.findViewById(R.id.stepDataValueTv);
        stepDataChartView = commView.findViewById(R.id.stepDataChartView);
        sleepDataValueTv = commView.findViewById(R.id.sleepDataValueTv);
        sleepDataChartView = commView.findViewById(R.id.sleepDataChartView);
        heartDataValueTv = commView.findViewById(R.id.heartDataValueTv);
        heartDataChartView = commView.findViewById(R.id.heartDataChartView);
        bloodDataValueTv = commView.findViewById(R.id.bloodDataValueTv);
        charBloadView = commView.findViewById(R.id.charBloadView);
        b30BloadChartLin = commView.findViewById(R.id.rel_blood);
        commentB30BackImg.setOnClickListener(this);
        b30DataWeekTv.setOnClickListener(this);
        b30DataMonthTv.setOnClickListener(this);
        b30DataYearTv.setOnClickListener(this);
    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(getResources().getString(R.string.data));
        String bleName = WatchUtils.getSherpBleName(getActivity());
        b30BloadChartLin.setVisibility(View.VISIBLE);

    }


    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        if (isVisible) {
            setClearStyle(0);
        }
    }

    private void initData() {
        mValues = new ArrayList<>();
        xStepList = new ArrayList<>();
        pointbar = new ArrayList<>();

        heartValues = new ArrayList<>();
        heartXList = new ArrayList<>();
        heartBarEntryList = new ArrayList<>();

        sleepVlaues = new ArrayList<>();
        sleepXList = new ArrayList<>();
        sleepBarEntryList = new ArrayList<>();

    }


    //根据开始和计算日期查询数据
    private void findDataForDB(int code) {
        mValues.clear();
        xStepList.clear();
        //String endDay = WatchUtils.obtainFormatDate(1); //默认昨天
        String endDay = WatchUtils.getCurrentDate();  //WatchUtils.obtainAroundDate(WatchUtils.getCurrentDate(), false);
        String startDay;

        switch (code) {
            case 0x01:
                startDay = WatchUtils.obtainFormatDate(29);
                break;
            case 0x02:
                startDay = WatchUtils.obtainFormatDate(364);
                break;
            default:
                startDay = WatchUtils.obtainFormatDate(6);
                break;
        }
        String bleMac = WatchUtils.getSherpBleMac(getContext());
        if (WatchUtils.isEmpty(bleMac)) {
            showSleepChart(new ArrayList<>(), new ArrayList<>());
            charBloadView.updateView(new ArrayList<>(), new ArrayList<>());
            charBloadView.setScal(false);
            return;
        }

        Log.e(TAG, "----startDay=" + startDay + "--=endDay=" + endDay + "-=bleMac=" + bleMac);
        List<CommStepCountDb> uploadStepList = CommDBManager.getCommDBManager().findCountStepForUpload(bleMac, startDay, endDay);
        if (uploadStepList != null && !uploadStepList.isEmpty()) {
            analysisStepData(uploadStepList, code);
        } else {
            showStepsChat(new ArrayList<>(), new ArrayList<>());
        }

        List<CommHeartDb> commHeartDbList = CommDBManager.getCommDBManager().findCommHeartForUpload(bleMac, startDay,
                endDay);

        if (commHeartDbList != null && !commHeartDbList.isEmpty()) {
            analysisHeartData(commHeartDbList, code);
        } else {
            showHeartChart(new ArrayList<>(), new ArrayList<>());
        }

        List<CommSleepDb> commSleepDbList = CommDBManager.getCommDBManager().findCommSleepForUpload(bleMac, startDay, endDay);


        if (commSleepDbList != null && !commSleepDbList.isEmpty()) {
            analysisSleepData(commSleepDbList, code);
        } else {
            showSleepChart(new ArrayList<>(), new ArrayList<>());
        }

        List<CommBloodDb> bloodDbList = CommDBManager.getCommDBManager().findCommBloodForUpload(bleMac, startDay, endDay);
        if (bloodDbList != null && !bloodDbList.isEmpty()) {
            analysisBloodData(bloodDbList, code);
        } else {
            charBloadView.updateView(new ArrayList<>(), new ArrayList<>());
            charBloadView.setScal(false);
        }

    }

    //解析血压数据
    private void analysisBloodData(final List<CommBloodDb> bloodDb, final int code) {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    bloodDateList.clear();
                    bloodList.clear();

                    Map<String, SparseIntArray> bloodMap = new HashMap<>();
                    String currDay = WatchUtils.getCurrentDate();
                    if (SectectCode == 364) { //年 ，年需将天合并为月并求取平均值
                        Map<String, SparseIntArray> tmpYearMap = new HashMap<>();
                        String currMonthStr = WatchUtils.getCurrentDateFormat("yy/MM");
                        for (int i = 0; i < 12; i++) {
                            SparseIntArray sparseIntArray = new SparseIntArray();
                            sparseIntArray.append(0, 0);
                            tmpYearMap.put(currMonthStr, sparseIntArray);
                            currMonthStr = getLastMonth(currMonthStr);
                        }
                        for (CommBloodDb cb : bloodDb) {
                            String dbStr = cb.getRtc();
                            int heightBp = cb.getAvgdiastolic();
                            int lowBp = cb.getAvgsystolic();
                            SparseIntArray sparseIntArray = new SparseIntArray();
                            sparseIntArray.append(heightBp, lowBp);
                            String tmpDbStr = dayFormat2(dbStr);

                            if (tmpYearMap.get(tmpDbStr) != null) {
                                tmpYearMap.put(tmpDbStr, sparseIntArray);
                            } else {
                                tmpYearMap.put(tmpDbStr, sparseIntArray);
                            }
                        }

                        for (Map.Entry<String, SparseIntArray> yMM : tmpYearMap.entrySet()) {
                            bloodDateList.add(yMM.getKey());
                        }

                        Collections.sort(bloodDateList, new Comparator<String>() {
                            @Override
                            public int compare(String s, String t1) {
                                return s.compareTo(t1);
                            }
                        });

                        for (int i = 0; i < bloodDateList.size(); i++) {
                            bloodList.add(tmpYearMap.get(bloodDateList.get(i)));
                        }
                        handler.sendEmptyMessage(0x04);
                        return;
                    }

                    //组合日期
                    for (int i = 0; i < SectectCode; i++) {
                        Log.e(TAG, "-----currDay=" + currDay);
                        SparseIntArray sparseIntArray = new SparseIntArray();
                        sparseIntArray.append(0, 0);
                        bloodMap.put(currDay, sparseIntArray);
                        currDay = WatchUtils.obtainAroundDate(currDay, true);
                    }

                    for (CommBloodDb cb : bloodDb) {
                        SparseIntArray sparseIntArray = new SparseIntArray();
                        sparseIntArray.append(cb.getAvgdiastolic(), cb.getAvgsystolic());

                        bloodMap.put(cb.getRtc(), sparseIntArray);
                    }

                    for (Map.Entry<String, SparseIntArray> mp : bloodMap.entrySet()) {
                        String dayStr = mp.getKey();
                        SparseIntArray bpArray = mp.getValue();
                        String tmpDbStr = dayFormat(dayStr);
                        bloodList.add(bpArray);
                        bloodDateList.add(tmpDbStr);
                    }
                    Collections.sort(bloodDateList, new Comparator<String>() {
                        @Override
                        public int compare(String s, String t1) {
                            return s.compareTo(t1);
                        }
                    });
                    handler.sendEmptyMessage(0x04);
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //计算睡眠
    private void analysisSleepData(final List<CommSleepDb> sleepDb, final int code) {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sleepVlaues.clear();
                    sleepXList.clear();
                    //排序，
                    Map<String, Integer> sleepMap = new HashMap<>();
                    String currDay = WatchUtils.getCurrentDate();
                    if (SectectCode == 364) { //年 ，年需将天合并为月并求取平均值
                        Map<String, Integer> tmpYearMap = new HashMap<>();
                        String currMonthStr = WatchUtils.getCurrentDateFormat("yy/MM");
                        for (int i = 0; i < 12; i++) {
                            tmpYearMap.put(currMonthStr, 0);
                            currMonthStr = getLastMonth(currMonthStr);
                        }
                        for (CommSleepDb cb : sleepDb) {
                            String dbStr = cb.getDateStr();
                            int stepNumber = cb.getSleeplen();
                            String tmpDbStr = dayFormat2Sleep(dbStr);

                            if (tmpYearMap.get(tmpDbStr) != null) {
                                tmpYearMap.put(tmpDbStr, stepNumber + tmpYearMap.get(tmpDbStr));
                            } else {
                                tmpYearMap.put(tmpDbStr, stepNumber);
                            }
                        }

                        for (Map.Entry<String, Integer> yMM : tmpYearMap.entrySet()) {
                            sleepXList.add(yMM.getKey());
                        }

                        Collections.sort(sleepXList, new Comparator<String>() {
                            @Override
                            public int compare(String s, String t1) {
                                return s.compareTo(t1);
                            }
                        });

                        for (int i = 0; i < sleepXList.size(); i++) {
                            int sleepL = tmpYearMap.get(sleepXList.get(i));
                            sleepVlaues.add(Float.valueOf(sleepL));
                        }
                        handler.sendEmptyMessage(0x03);
                        return;
                    }

                    //组合日期
                    for (int i = 0; i < SectectCode; i++) {
                        sleepMap.put(currDay, 0);
                        currDay = WatchUtils.obtainAroundDate(currDay, true);
                    }

                    for (CommSleepDb cb : sleepDb) {
                        sleepMap.put(cb.getDateStr(), cb.getSleeplen());
                    }

                    for (Map.Entry<String, Integer> mp : sleepMap.entrySet()) {
                        String dayStr = mp.getKey();
                        sleepXList.add(dayFormatSleep(dayStr)); //日期
                        sleepVlaues.add(Float.valueOf(mp.getValue()));
                    }
                    Collections.sort(sleepXList, new Comparator<String>() {
                        @Override
                        public int compare(String s, String t1) {
                            return s.compareTo(t1);
                        }
                    });

                    handler.sendEmptyMessage(0x03);
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //计算心率
    private void analysisHeartData(final List<CommHeartDb> heartDb, final int code) {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    heartValues.clear();
                    heartXList.clear();
                    //排序，
                    Map<String, Integer> heartMap = new HashMap<>();
                    String currDay = WatchUtils.getCurrentDate();
                    if (SectectCode == 364) { //年 ，年需将天合并为月并求取平均值
                        Map<String, Integer> tmpYearMap = new HashMap<>();
                        String currMonthStr = WatchUtils.getCurrentDateFormat("yy/MM");
                        for (int i = 0; i < 12; i++) {
                            tmpYearMap.put(currMonthStr, 0);
                            currMonthStr = getLastMonthHeart(currMonthStr);
                        }
                        for (CommHeartDb cb : heartDb) {
                            String dbStr = cb.getDateStr();
                            int stepNumber = cb.getAvgheartrate();
                            String tmpDbStr = dayFormat2Heart(dbStr);

                            if (tmpYearMap.get(tmpDbStr) != null) {
                                tmpYearMap.put(tmpDbStr, stepNumber + tmpYearMap.get(tmpDbStr));
                            } else {
                                tmpYearMap.put(tmpDbStr, stepNumber);
                            }
                        }

                        for (Map.Entry<String, Integer> yMM : tmpYearMap.entrySet()) {
                            heartXList.add(yMM.getKey());
                        }

                        Collections.sort(heartXList, new Comparator<String>() {
                            @Override
                            public int compare(String s, String t1) {
                                return s.compareTo(t1);
                            }
                        });

                        for (int i = 0; i < heartXList.size(); i++) {
                            heartValues.add(tmpYearMap.get(heartXList.get(i)));
                        }
                        handler.sendEmptyMessage(0x02);
                        return;
                    }

                    //组合日期
                    for (int i = 0; i < SectectCode; i++) {
                        heartMap.put(currDay, 0);
                        currDay = WatchUtils.obtainAroundDate(currDay, true);
                    }

                    for (CommHeartDb cb : heartDb) {
                        heartMap.put(cb.getDateStr(), cb.getAvgheartrate());
                    }

                    for (Map.Entry<String, Integer> mp : heartMap.entrySet()) {
                        String dayStr = mp.getKey();
                        heartXList.add(dayFormat(dayStr)); //日期
                        heartValues.add(mp.getValue());
                    }
                    Collections.sort(heartXList, new Comparator<String>() {
                        @Override
                        public int compare(String s, String t1) {
                            return s.compareTo(t1);
                        }
                    });
                    handler.sendEmptyMessage(0x02);
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private String dayFormat2(String day) {
        try {
            Date tmpDay = yearFormat.parse(day);
            assert tmpDay != null;
            return sdf3.format(tmpDay);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String dayFormat2Heart(String day) {
        try {
            Date tmpDay = yearFormat.parse(day);
            assert tmpDay != null;
            return sdf3.format(tmpDay);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String dayFormat2Sleep(String day) {
        try {
            Date tmpDay = yearFormat.parse(day);
            assert tmpDay != null;
            return sdf3.format(tmpDay);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private SimpleDateFormat sleepYearFm = new SimpleDateFormat("yyyy-MM-dd",Locale.CHINA);
    private SimpleDateFormat mmDDf = new SimpleDateFormat("MM/dd",Locale.CHINA);
    private String dayFormat(String day) {
        try {
            Date tmpDay = sleepYearFm.parse(day);
            assert tmpDay != null;
            return mmDDf.format(tmpDay);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private String dayFormatSleep(String day) {
        try {
            Date tmpDay = yearFormat.parse(day);
            assert tmpDay != null;
            return sdf2.format(tmpDay);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    //步数的计算
    private void analysisStepData(final List<CommStepCountDb> stepDb, final int code) {
        try {
            new Thread(() -> {
                Map<String, Integer> stepMap = new HashMap<>();
                String currDay = WatchUtils.getCurrentDate();
                if (SectectCode == 364) { //年 ，年需将天合并为月并求取平均值
                    Map<String, Integer> tmpYearMap = new HashMap<>();
                    String currMonthStr = WatchUtils.getCurrentDateFormat("yy/MM");
                    for (int i = 0; i < 12; i++) {
                        tmpYearMap.put(currMonthStr, 0);
                        currMonthStr = getLastMonth(currMonthStr);
                    }
                    for (CommStepCountDb cb : stepDb) {
                        String dbStr = cb.getDateStr();
                        int stepNumber = cb.getStepnumber();
                        String tmpDbStr = dayFormat2(dbStr);

                        if (tmpYearMap.get(tmpDbStr) != null) {
                            tmpYearMap.put(tmpDbStr, stepNumber + tmpYearMap.get(tmpDbStr));
                        } else {
                            tmpYearMap.put(tmpDbStr, stepNumber);
                        }
                    }

                    for (Map.Entry<String, Integer> yMM : tmpYearMap.entrySet()) {
                        xStepList.add(yMM.getKey());
                    }

                    Collections.sort(xStepList, new Comparator<String>() {
                        @Override
                        public int compare(String s, String t1) {
                            return s.compareTo(t1);
                        }
                    });

                    for (int i = 0; i < xStepList.size(); i++) {
                        mValues.add(tmpYearMap.get(xStepList.get(i)));
                    }
                    handler.sendEmptyMessage(0x01);
                    return;
                }

                //组合日期
                for (int i = 0; i < SectectCode; i++) {
                    Log.e(TAG, "-----currDay=" + currDay);
                    stepMap.put(currDay, 0);
                    currDay = WatchUtils.obtainAroundDate(currDay, true);
                }

                for (CommStepCountDb cb : stepDb) {
                    stepMap.put(cb.getDateStr(), cb.getStepnumber());
                }

                for (Map.Entry<String, Integer> mp : stepMap.entrySet()) {
                    String dayStr = mp.getKey();
                    Log.e(TAG, "------dayStr=" + dayStr);
                    xStepList.add(dayFormat(dayStr)); //日期
                    mValues.add(mp.getValue());
                }
                Collections.sort(xStepList, new Comparator<String>() {
                    @Override
                    public int compare(String s, String t1) {
                        return s.compareTo(t1);
                    }
                });
                handler.sendEmptyMessage(0x01);
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:    //返回
                getActivity().finish();
                break;
            case R.id.b30DataWeekTv:    //周
                setClearStyle(0);
                break;
            case R.id.b30DataMonthTv:   //月
                setClearStyle(1);
                break;
            case R.id.b30DataYearTv:    //年
                setClearStyle(2);
                break;
        }
    }


    //周，月，年切换
    private void setClearStyle(int code) {
        if (b30DataWeekTv != null) {
            b30DataWeekTv.setBackgroundResource(R.drawable.newh9data_selecte_text_shap);
            b30DataWeekTv.setTextColor(Color.parseColor("#333333"));
        }
        if (b30DataMonthTv != null) {
            b30DataMonthTv.setBackgroundResource(R.drawable.newh9data_selecte_text_shap);
            b30DataMonthTv.setTextColor(Color.parseColor("#333333"));
        }
        if (b30DataYearTv != null) {
            b30DataYearTv.setBackgroundResource(R.drawable.newh9data_selecte_text_shap);
            b30DataYearTv.setTextColor(Color.parseColor("#333333"));
        }

        if (getActivity() == null || getActivity().isFinishing())
            return;
        switch (code) {
            case 0:
                b30DataWeekTv.setTextColor(getResources().getColor(R.color.white));
                b30DataWeekTv.setBackgroundResource(R.drawable.newh9data_unselecte_text_shap_one);
                break;
            case 1:
                b30DataMonthTv.setTextColor(getResources().getColor(R.color.white));
                b30DataMonthTv.setBackgroundResource(R.drawable.newh9data_unselecte_text_shap);
                break;
            case 2:
                b30DataYearTv.setTextColor(getResources().getColor(R.color.white));
                b30DataYearTv.setBackgroundResource(R.drawable.newh9data_unselecte_text_shap_two);
                break;
        }
        SectectCode = ((code == 0) ? 7 : (code == 1) ? 30 : (code == 2) ? 364 : 7);
//        Log.e(TAG, "===查询的天数== " + SectectCode);
        findDataForDB(code);
    }


    // 展示步数图表
    private void showStepsChat(List<Integer> mValues, List<String> xList) {
        if (getActivity() == null || getActivity().isFinishing())
            return;
        pointbar.clear();
        if (xList.isEmpty()) {
            stepDataChartView.setNoDataText(getResources().getString(R.string.nodata));
            stepDataChartView.setNoDataTextColor(Color.WHITE);
            stepDataChartView.invalidate();
            return;
        }
        for (int i = 0; i < mValues.size(); i++) {
            pointbar.add(new BarEntry(i, mValues.get(i)));
        }
        BarDataSet barDataSet = new BarDataSet(pointbar, "");
        barDataSet.setDrawValues(false);//是否显示柱子上面的数值
        barDataSet.setValueTextColor(Color.WHITE);
        barDataSet.setColor(Color.parseColor("#ffffff"));//设置第一组数据颜色
        barDataSet.setHighLightColor(Color.parseColor("#ffffff"));
        Legend mLegend = stepDataChartView.getLegend();
        mLegend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);//设置注解的位置在右
        mLegend.setForm(Legend.LegendForm.CIRCLE);// 样式
        mLegend.setFormSize(1.0f);// 字体
        mLegend.setTextColor(Color.BLUE);// 颜色
        mLegend.setEnabled(false);

        ArrayList<IBarDataSet> threebardata = new ArrayList<>();//IBarDataSet 接口很关键，
        // 是添加多组数据的关键结构，LineChart也是可以采用对应的接口类，也可以添加多组数据
        threebardata.add(barDataSet);

        BarData bardata = new BarData(threebardata);
        if (mValues.size() >= 15) {
            bardata.setBarWidth(0.2f);  //设置柱子宽度
            barDataSet.setValueTextSize(6.0f);//柱子顶部字体大小
        } else {
            bardata.setBarWidth(0.1f);  //设置柱子宽度
        }
        stepDataChartView.setData(bardata);

        stepDataChartView.setDoubleTapToZoomEnabled(false);   //双击缩放
        stepDataChartView.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        stepDataChartView.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        //设置注解的位置在左上方
        stepDataChartView.getLegend().setForm(Legend.LegendForm.CIRCLE);//这是左边显示小图标的形状
        stepDataChartView.setPinchZoom(false);
        stepDataChartView.setTouchEnabled(true);
        stepDataChartView.setScaleEnabled(false);

//        BarXFormartValue xFormartValue = new BarXFormartValue(stepDataChartView, xList);
        XAxis xAxis = stepDataChartView.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置X轴的位置
        xAxis.setDrawGridLines(false);//不显示网格
        xAxis.setAxisLineColor(Color.WHITE);
        xAxis.setAxisLineWidth(2f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value < xList.size())
                    return xList.get((int) value);
                else return "";
            }
        });
        xAxis.setEnabled(true);
        stepDataChartView.getDescription().setEnabled(false);
        DataMarkView dataMarkView = new DataMarkView(getActivity(), R.layout.mark_view_layout, 1);
        dataMarkView.setChartView(stepDataChartView);
        stepDataChartView.setMarker(dataMarkView);
        stepDataChartView.getAxisRight().setEnabled(false);//右侧不显示Y轴
        stepDataChartView.getAxisLeft().setAxisMinValue(0.8f);//设置Y轴显示最小值，不然0下面会有空隙
        stepDataChartView.getAxisLeft().setDrawGridLines(false);//不设置Y轴网格
        stepDataChartView.getAxisLeft().setEnabled(false);
        stepDataChartView.getXAxis().setSpaceMax(0.5f);
        stepDataChartView.animateXY(1000, 2000);//设置动画
    }


    //展示心率图表
    private void showHeartChart(List<Integer> heartList, List<String> xlt) {
        if (getActivity() == null || getActivity().isFinishing())
            return;
        if (heartXList.isEmpty()) {
            heartDataChartView.setNoDataTextColor(Color.WHITE);
            heartDataChartView.setNoDataText(getResources().getString(R.string.nodata));
            heartDataChartView.invalidate();
            return;
        }
        heartBarEntryList.clear();
        for (int i = 0; i < heartList.size(); i++) {
            heartBarEntryList.add(new BarEntry(i, (int) heartList.get(i)));
        }

        BarDataSet barDataSet = new BarDataSet(heartBarEntryList, "");
        barDataSet.setDrawValues(false);//是否显示柱子上面的数值
        barDataSet.setValueTextColor(Color.WHITE);
        barDataSet.setColor(Color.parseColor("#ffffff"));//设置第一组数据颜色
        barDataSet.setHighLightColor(Color.parseColor("#ffffff"));
//        barDataSet.setHighLightColor(Color.GREEN);

        Legend mLegend = stepDataChartView.getLegend();
        mLegend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);//设置注解的位置在右
        mLegend.setForm(Legend.LegendForm.CIRCLE);// 样式
        mLegend.setFormSize(1.0f);// 字体
        mLegend.setTextColor(Color.BLUE);// 颜色
        mLegend.setEnabled(false);

        ArrayList<IBarDataSet> threebardata = new ArrayList<>();//IBarDataSet 接口很关键，
        // 是添加多组数据的关键结构，LineChart也是可以采用对应的接口类，也可以添加多组数据
        threebardata.add(barDataSet);

        BarData bardata = new BarData(threebardata);
        if (heartList.size() >= 15) {
            bardata.setBarWidth(0.2f);  //设置柱子宽度
            barDataSet.setValueTextSize(6.0f);//柱子顶部字体大小
        } else {
            bardata.setBarWidth(0.1f);  //设置柱子宽度
        }

        heartDataChartView.setData(bardata);
        heartDataChartView.setDoubleTapToZoomEnabled(false);   //双击缩放
        heartDataChartView.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        heartDataChartView.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        //设置注解的位置在左上方
        heartDataChartView.getLegend().setForm(Legend.LegendForm.CIRCLE);//这是左边显示小图标的形状
        heartDataChartView.getLegend().setEnabled(false);
        heartDataChartView.setTouchEnabled(true);
        heartDataChartView.setPinchZoom(false);
        heartDataChartView.setScaleEnabled(false);

//        BarXFormartValue xFormartValue = new BarXFormartValue(heartDataChartView, xlt);
        XAxis xAxis = heartDataChartView.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置X轴的位置
        xAxis.setDrawGridLines(false);//不显示网格
        xAxis.setAxisLineColor(Color.WHITE);
        xAxis.setAxisLineWidth(2f);//设置X轴的高度
        xAxis.setTextColor(Color.WHITE);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value < xlt.size())
                    return xlt.get((int) value);
                else return "";
            }
        });
        xAxis.setEnabled(true);


        DataMarkView dataMarkView = new DataMarkView(getActivity(), R.layout.mark_view_layout, 1);
        dataMarkView.setChartView(heartDataChartView);
        heartDataChartView.setMarker(dataMarkView);

        heartDataChartView.getDescription().setEnabled(false);

        heartDataChartView.getAxisRight().setEnabled(false);//右侧不显示Y轴
        heartDataChartView.getAxisLeft().setAxisMinValue(0.8f);//设置Y轴显示最小值，不然0下面会有空隙
        heartDataChartView.getAxisLeft().setDrawGridLines(false);//不设置Y轴网格
        heartDataChartView.getAxisLeft().setEnabled(false);
        heartDataChartView.getXAxis().setSpaceMax(0.5f);
        heartDataChartView.animateXY(1000, 2000);//设置动画
    }


    //展示睡眠图表
    private void showSleepChart(List<Float> sleepVlaues, List<String> sleepXList) {
        if (getActivity() == null || getActivity().isFinishing())
            return;
        if (sleepXList.size() == 0) {
            sleepDataChartView.setNoDataText(getResources().getString(R.string.nodata));
            sleepDataChartView.setNoDataTextColor(Color.WHITE);
            sleepDataChartView.invalidate();
            return;
        }
        sleepBarEntryList.clear();
        for (int i = 0; i < sleepVlaues.size(); i++) {
            sleepBarEntryList.add(new BarEntry(i, sleepVlaues.get(i)));
        }

        BarDataSet barDataSet = new BarDataSet(sleepBarEntryList, "");
        barDataSet.setDrawValues(false);//是否显示柱子上面的数值
        barDataSet.setValueTextColor(Color.WHITE);
        barDataSet.setColor(Color.parseColor("#ffffff"));//设置第一组数据颜色
        barDataSet.setHighLightColor(Color.parseColor("#ffffff"));
//        barDataSet.setHighLightColor(Color.GREEN);

        Legend mLegend = sleepDataChartView.getLegend();
        mLegend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);//设置注解的位置在右
        mLegend.setForm(Legend.LegendForm.CIRCLE);// 样式
        mLegend.setFormSize(1.0f);// 字体
        mLegend.setTextColor(Color.BLUE);// 颜色
        mLegend.setEnabled(false);

        ArrayList<IBarDataSet> threebardata = new ArrayList<>();//IBarDataSet 接口很关键，
        // 是添加多组数据的关键结构，LineChart也是可以采用对应的接口类，也可以添加多组数据
        threebardata.add(barDataSet);

        BarData bardata = new BarData(threebardata);
        if (sleepVlaues.size() >= 15) {
            bardata.setBarWidth(0.2f);  //设置柱子宽度
            barDataSet.setValueTextSize(6.0f);//柱子顶部字体大小
        } else {
            bardata.setBarWidth(0.1f);  //设置柱子宽度
        }


        sleepDataChartView.setData(bardata);
        sleepDataChartView.setDoubleTapToZoomEnabled(false);   //双击缩放
        sleepDataChartView.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        sleepDataChartView.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        //设置注解的位置在左上方
        sleepDataChartView.getLegend().setForm(Legend.LegendForm.CIRCLE);//这是左边显示小图标的形状
        sleepDataChartView.setTouchEnabled(true);
        sleepDataChartView.setPinchZoom(false);
        sleepDataChartView.setScaleEnabled(false);

//        BarXFormartValue xFormartValue = new BarXFormartValue(sleepDataChartView, sleepXList);
        XAxis xAxis = sleepDataChartView.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置X轴的位置
        xAxis.setDrawGridLines(false);//不显示网格
        xAxis.setAxisLineColor(Color.WHITE);
        xAxis.setAxisLineWidth(2f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                //增加下标和数组长度对比，防止数组越界
                if ((int) value <= sleepXList.size() - 1)
                    return sleepXList.get((int) value);
                else return "";
            }
        });
        xAxis.setEnabled(true);
        sleepDataChartView.getDescription().setEnabled(false);


        DataMarkView dataMarkView = new DataMarkView(getActivity(), R.layout.mark_view_layout, 0);
        dataMarkView.setChartView(sleepDataChartView);
        sleepDataChartView.setMarker(dataMarkView);


        sleepDataChartView.getAxisRight().setEnabled(false);//右侧不显示Y轴
        sleepDataChartView.getAxisLeft().setAxisMinValue(0.8f);//设置Y轴显示最小值，不然0下面会有空隙
        sleepDataChartView.getAxisLeft().setDrawGridLines(false);//不设置Y轴网格
        sleepDataChartView.getAxisLeft().setEnabled(false);
        sleepDataChartView.getXAxis().setSpaceMax(0.5f);
        sleepDataChartView.animateXY(1000, 2000);//设置动画
    }


    //获取当前月份的上一个月
    private String getLastMonth(String cuuMonth) {
        try {
            Date currDate = sdf3.parse(cuuMonth);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currDate);
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
            return sdf3.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    //获取当前月份的上一个月
    private String getLastMonthHeart(String cuuMonth) {
        try {
            Date currDate = sdf3.parse(cuuMonth);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currDate);
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
            return sdf3.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

}
