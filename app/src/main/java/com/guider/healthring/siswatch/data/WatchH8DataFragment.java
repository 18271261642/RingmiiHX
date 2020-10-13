package com.guider.healthring.siswatch.data;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.github.mikephil.charting.formatter.ValueFormatter;
import com.guider.healthring.R;
import com.guider.healthring.activity.MyPersonalActivity;
import com.guider.healthring.net.OkHttpObservable;
import com.guider.healthring.rxandroid.CommonSubscriber;
import com.guider.healthring.rxandroid.SubscriberOnNextListener;
import com.guider.healthring.siswatch.H8ShareActivity;
import com.guider.healthring.siswatch.bean.WatchDataDatyBean;
import com.guider.healthring.siswatch.record.RecordHistoryActivity;
import com.guider.healthring.siswatch.utils.WatchUtils;
import com.guider.healthring.util.SharedPreferencesUtils;
import com.guider.healthring.util.URLs;
import com.guider.healthring.w30s.BaseFragment;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunjianhua on 2017/11/6.
 */

public class WatchH8DataFragment extends BaseFragment implements View.OnClickListener{

    private static final String TAG = "-------WatchH8DataFragment";

    View h8View;
    LineChart watchNewh8dataweekChar;
    List<WatchDataDatyBean> watchDataList;
    //身高
    TextView h8UserHeightTv;
    //体重
    TextView h8UserWeighttTv;
    //年龄
    TextView h8UserAgetTv;
    //步数显示
    TextView watchNewh8DataStepTv;
    //里程
    TextView watchNewh8DataDisTv;
    //卡路里
    TextView watchNewh8DataKcalTv;
    //周
    TextView weekNewtv1;
    //月
    TextView monthNewtv2;
    //年
    TextView yearNewtv3;
    View weekNewview1;
    View monthNewview2;
    View yearNewview3;
    TextView watchNewh8DatadatelTv;
    TextView xTv1;
    TextView xTv2;
    TextView xTv3;
    TextView xTv4;
    TextView xTv5;
    TextView xTv6;
    TextView xTv7;
    TextView h8UserSexTv;
    //x轴的textView布局
    LinearLayout h8XLin;
    RadioButton xRb1;
    RadioButton xRb2;
    RadioButton xRb3;
    RadioButton xRb4;
    RadioButton xRb5;
    RadioButton xRb6;
    RadioButton xRb7;
    LinearLayout h8TesetRadioLin;
    RadioGroup h8xRg;
    View newH8XView;

    //标题
    TextView h8DataTitleTv;

    private String weekTag = "week";
    Map<String, Integer> sumMap;
    //日期
    private List<String> dtLis;


    //折线图的数据
    List<Entry> entryList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        h8View = inflater.inflate(R.layout.fragment_new_h8_data, container, false);
        initViewIds();

        initViews();
        //showTodaySportData();   //显示当天的数据
        watchNewh8DatadatelTv.setText(WatchUtils.getCurrentDate().substring(5, WatchUtils.getCurrentDate().length()));
        //默认显示周的数据
        clearViewBack();
        weekNewtv1.setTextColor(ContextCompat.getColor(getActivity(), R.color.new_colorAccent));
        weekNewview1.setBackground(ResourcesCompat.getDrawable(getResources(), R.mipmap.h8_datareportperiodselected, null));
        return h8View;
    }

    private void initViewIds() {
        watchNewh8dataweekChar = h8View.findViewById(R.id.watch_newh8dataweekChar);
        h8UserHeightTv = h8View.findViewById(R.id.h8UserHeightTv);
        h8UserWeighttTv = h8View.findViewById(R.id.h8UserWeighttTv);
        h8UserAgetTv = h8View.findViewById(R.id.h8UserAgetTv);
        h8UserAgetTv = h8View.findViewById(R.id.h8UserAgetTv);
        watchNewh8DataStepTv = h8View.findViewById(R.id.watch_newh8_dataStepTv);
        watchNewh8DataDisTv = h8View.findViewById(R.id.watch_newh8_dataDisTv);
        watchNewh8DataKcalTv = h8View.findViewById(R.id.watch_newh8_dataKcalTv);
        weekNewtv1 = h8View.findViewById(R.id.week_newtv1);
        monthNewtv2 = h8View.findViewById(R.id.month_newtv2);
        yearNewtv3 = h8View.findViewById(R.id.year_newtv3);
        weekNewview1 = h8View.findViewById(R.id.week_newview1);
        monthNewview2 = h8View.findViewById(R.id.month_newview2);
        yearNewview3 = h8View.findViewById(R.id.year_newview3);
        watchNewh8DatadatelTv = h8View.findViewById(R.id.watch_newh8_datadatelTv);
        xTv1 = h8View.findViewById(R.id.xTv1);
        xTv2 = h8View.findViewById(R.id.xTv2);
        xTv3 = h8View.findViewById(R.id.xTv3);
        xTv4 = h8View.findViewById(R.id.xTv4);
        xTv5 = h8View.findViewById(R.id.xTv5);
        xTv6 = h8View.findViewById(R.id.xTv6);
        xTv7 = h8View.findViewById(R.id.xTv7);
        h8UserSexTv = h8View.findViewById(R.id.h8UserSexTv);
        h8XLin = h8View.findViewById(R.id.h8_x_lin);
        xRb1 = h8View.findViewById(R.id.xRb1);
        xRb1 = h8View.findViewById(R.id.xRb1);
        xRb2 = h8View.findViewById(R.id.xRb2);
        xRb3 = h8View.findViewById(R.id.xRb3);
        xRb4 = h8View.findViewById(R.id.xRb4);
        xRb5 = h8View.findViewById(R.id.xRb5);
        xRb6 = h8View.findViewById(R.id.xRb6);
        xRb7 = h8View.findViewById(R.id.xRb7);
        h8TesetRadioLin = h8View.findViewById(R.id.h8_teset_radio_lin);
        h8xRg = h8View.findViewById(R.id.h8xRg);
        newH8XView = h8View.findViewById(R.id.newH8XView);
        h8DataTitleTv = h8View.findViewById(R.id.h8_data_titleTv);
        weekNewtv1.setOnClickListener(this);
        monthNewtv2.setOnClickListener(this);
        yearNewtv3.setOnClickListener(this);
        h8View.findViewById(R.id.newh8dataUserLi).setOnClickListener(this);
        h8View.findViewById(R.id.h8_dataLinChartImg).setOnClickListener(this);
        h8View.findViewById(R.id.h8_dataShareImg).setOnClickListener(this);
        h8View.findViewById(R.id.h8_data_titleLinImg).setOnClickListener(this);
    }


    //视图显示不显示
    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        if(isVisible){
            getLindataFromServer("week");
        }
    }

    //显示当天的数据
    private void showTodaySportData() {
        //显示今天的步数
        String todayStep = ((String) SharedPreferencesUtils.getParam(getActivity(), "stepsnum", "")).trim();
        String kal = ((String) SharedPreferencesUtils.getParam(getActivity(), "watchkcal", "")).trim();
        String discan = (String) SharedPreferencesUtils.getParam(getActivity(), "watchstepsdistants", "");

        if (!WatchUtils.isEmpty(discan)) {
            watchNewh8DataDisTv.setText(discan + getResources().getString(R.string.km));
        }
        if (!WatchUtils.isEmpty(kal)) {
            watchNewh8DataKcalTv.setText(StringUtils.substringBefore(kal, ".") + getResources().getString(R.string.km_cal));
        }


        watchNewh8DatadatelTv.setText(WatchUtils.getCurrentDate().substring(5, WatchUtils.getCurrentDate().length()));
        //默认显示周的数据
        clearViewBack();
        weekNewtv1.setTextColor(ContextCompat.getColor(getActivity(), R.color.new_colorAccent));
        weekNewview1.setBackground(ResourcesCompat.getDrawable(getResources(), R.mipmap.h8_datareportperiodselected, null));

    }

    private void initViews() {
        dtLis = new ArrayList<>();
        watchDataList = new ArrayList<>();
        h8DataTitleTv.setText(getResources().getString(R.string.data));

        entryList = new ArrayList<>();

        h8XLin.setVisibility(View.GONE);
        newH8XView.setVisibility(View.VISIBLE);
        h8TesetRadioLin.setVisibility(View.VISIBLE);
        getUserInfoData();

    }

    //获取用户信息
    private void getUserInfoData() {
        h8UserHeightTv.setText(getResources().getString(R.string.height) + ":" + "-");
        h8UserWeighttTv.setText(getResources().getString(R.string.weight) + ":" + "-");
        h8UserAgetTv.setText(getResources().getString(R.string.age) + ":" + "-");
        h8UserSexTv.setText(getResources().getString(R.string.sex) + ":" + "-");


        String url = URLs.HTTPs + URLs.getUserInfo; //查询用户信息
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("userId", SharedPreferencesUtils.readObject(getActivity(), "userId"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SubscriberOnNextListener userSub = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {
                if (!WatchUtils.isEmpty(result)) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.getString("resultCode").equals("001")) {
                            JSONObject myInfoJsonObject = jsonObject.getJSONObject("userInfo");
                            if (myInfoJsonObject != null) {
                                h8UserHeightTv.setText(getResources().getString(R.string.height) + ":" + myInfoJsonObject.getString("height") + "");
                                h8UserWeighttTv.setText(getResources().getString(R.string.weight) + ":" + myInfoJsonObject.getString("weight"));
                                String birtday = myInfoJsonObject.getString("birthday"); //生日
                                if (!WatchUtils.isEmpty(birtday)) {
                                    h8UserAgetTv.setText(getResources().getString(R.string.age) + ":" + WatchUtils.getAgeFromBirthTime(birtday));
                                }
                                String sex = myInfoJsonObject.getString("sex");
                                if (sex.equals("M")) {
                                    h8UserSexTv.setText(getResources().getString(R.string.sex) + ":" + getResources().getString(R.string.sex_nan));
                                } else {
                                    h8UserSexTv.setText(getResources().getString(R.string.sex) + ":" + getResources().getString(R.string.sex_nv));
                                }

                            } else {
                                h8UserHeightTv.setText(getResources().getString(R.string.height) + ":" + "-");
                                h8UserWeighttTv.setText(getResources().getString(R.string.weight) + ":" + "-");
                                h8UserAgetTv.setText(getResources().getString(R.string.age) + ":" + "-");
                                h8UserSexTv.setText(getResources().getString(R.string.sex) + ":" + "-");
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    h8UserHeightTv.setText(getResources().getString(R.string.height) + ":" + "-");
                    h8UserWeighttTv.setText(getResources().getString(R.string.weight) + ":" + "-");

                    h8UserAgetTv.setText(getResources().getString(R.string.age) + ":" + "-");

                    h8UserSexTv.setText(getResources().getString(R.string.sex) + ":" + "-");

                }
            }

        };
        CommonSubscriber commonSubscriber2 = new CommonSubscriber(userSub, getActivity());
        OkHttpObservable.getInstance().getData(commonSubscriber2, url, jsonObj.toString());
    }


    private void getLindataFromServer(final String datatag) {
        showLoadingDialog("loading...");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String url = URLs.HTTPs + URLs.GET_WATCH_DATA_DATA;
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("userId", SharedPreferencesUtils.readObject(getActivity(), "userId"));
            jsonParams.put("deviceCode", SharedPreferencesUtils.readObject(getActivity(), "mylanmac"));

            if (datatag.equals("week")) { //周
                //开始时间
                jsonParams.put("startDate", sdf.format(WatchUtils.getDateBefore(sdf.parse(WatchUtils.getCurrentDate()), 7)));
                //结束时间
                jsonParams.put("endDate", sdf.format(WatchUtils.getDateBefore(sdf.parse(WatchUtils.getCurrentDate()), 1)));
            } else if (datatag.equals("month")) {
                //开始时间
                jsonParams.put("startDate", sdf.format(WatchUtils.getDateBefore(sdf.parse(WatchUtils.getCurrentDate()), 30)));
                //结束时间
                jsonParams.put("endDate", sdf.format(WatchUtils.getDateBefore(sdf.parse(WatchUtils.getCurrentDate()), 1)));
            } else if (datatag.equals("year")) {
                //开始时间
                jsonParams.put("startDate", sdf.format(WatchUtils.getDateBefore(sdf.parse(WatchUtils.getCurrentDate()), 364)));
                //结束时间
                jsonParams.put("endDate", WatchUtils.getCurrentDate());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<String>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onNext(String result) {
                closeLoadingDialog();
                Log.e(TAG, "-----result--11--" + result);
                if (!WatchUtils.isEmpty(result)) {
                    try {
                        JSONObject jso = new JSONObject(result);
                        String daydata = jso.getString("day");
                        watchDataList.clear();
                        watchDataList = new Gson().fromJson(daydata, new TypeToken<List<WatchDataDatyBean>>() {
                        }.getType());

                        Log.e(TAG, "-----watchDataList.seze----" + watchDataList.size() + weekTag);
                        if (datatag.equals("year")) {
                            sumMap = new HashMap<>();
                            int sum = 0;
                            for (int i = 0; i < watchDataList.size(); i++) {
                                String strDate = watchDataList.get(i).getRtc().substring(0, 7);
                                if (sumMap.get(strDate) != null) {
                                    sum += watchDataList.get(i).getStepNumber();
                                }else{
                                    sum = watchDataList.get(i).getStepNumber();
                                }
                                sumMap.put(strDate, sum);

                            }
                            dtLis.clear();
                            Log.e(TAG,"------map---"+sumMap.toString());
                            //遍历map
                            for (Map.Entry<String, Integer> entry : sumMap.entrySet()) {
                                dtLis.add(entry.getKey().trim());
                            }
                            //升序排列
                            Collections.sort(dtLis, new Comparator<String>() {
                                @Override
                                public int compare(String s, String t1) {
                                    return s.compareTo(t1);
                                }
                            });
                            LineChart(watchDataList,12,sumMap);
                        } else if (datatag.equals("week")) {
                            dtLis.clear();
                            Collections.sort(watchDataList, new Comparator<WatchDataDatyBean>() {
                                @Override
                                public int compare(WatchDataDatyBean o1, WatchDataDatyBean o2) {
                                    return o1.getRtc().compareTo(o2.getRtc());
                                }
                            });
                            xRb1.setText(watchDataList.get(0).getRtc().substring(8, watchDataList.get(0).getRtc().length()));
                            xRb2.setText(watchDataList.get(1).getRtc().substring(8, watchDataList.get(0).getRtc().length()));
                            xRb3.setText(watchDataList.get(2).getRtc().substring(8, watchDataList.get(0).getRtc().length()));
                            xRb4.setText(watchDataList.get(3).getRtc().substring(8, watchDataList.get(0).getRtc().length()));
                            xRb5.setText(watchDataList.get(4).getRtc().substring(8, watchDataList.get(0).getRtc().length()));
                            xRb6.setText(watchDataList.get(5).getRtc().substring(8, watchDataList.get(0).getRtc().length()));
                            xRb7.setText(watchDataList.get(6).getRtc().substring(8, watchDataList.get(0).getRtc().length()));
                            //设置昨天默认选中
                            xRb7.setChecked(true);
                            showTopData(6);
                            h8xRg.setOnCheckedChangeListener(new weekTvClickListener());

                            LineChart(watchDataList,watchDataList.size(),new HashMap<String, Integer>());

                        } else { //月
                            dtLis.clear();
                            LineChart(watchDataList,watchDataList.size(),new HashMap<String, Integer>());
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        };

        Log.e("新数据", "-----maps----" + jsonParams.toString() + "--" + url);
        CommonSubscriber commonSubscriber = new CommonSubscriber(subscriberOnNextListener, getActivity());
        OkHttpObservable.getInstance().getData(commonSubscriber, url, jsonParams.toString());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.week_newtv1:  //周
                h8XLin.setVisibility(View.GONE);
                newH8XView.setVisibility(View.VISIBLE);
                h8TesetRadioLin.setVisibility(View.VISIBLE);
                watchDataList.clear();
                entryList.clear();
                weekTag = "week";
                clearViewBack();
                weekNewtv1.setTextColor(ContextCompat.getColor(getActivity(), R.color.new_colorAccent));
                weekNewview1.setBackground(ResourcesCompat.getDrawable(getResources(), R.mipmap.h8_datareportperiodselected, null));
                getLindataFromServer("week");

                break;
            case R.id.month_newtv2: //月
                h8XLin.setVisibility(View.GONE);
                h8TesetRadioLin.setVisibility(View.GONE);
                newH8XView.setVisibility(View.GONE);
                watchDataList.clear();
                entryList.clear();
                weekTag = "month";
                clearViewBack();
                monthNewtv2.setTextColor(ContextCompat.getColor(getActivity(), R.color.new_colorAccent));
                monthNewview2.setBackground(ResourcesCompat.getDrawable(getResources(), R.mipmap.h8_datareportperiodselected, null));
                getLindataFromServer("month");
                break;
            case R.id.year_newtv3:  //年的点击
                h8XLin.setVisibility(View.GONE);
                h8TesetRadioLin.setVisibility(View.GONE);
                newH8XView.setVisibility(View.GONE);
                watchDataList.clear();
                entryList.clear();
                weekTag = "year";
                clearViewBack();
                yearNewtv3.setTextColor(ContextCompat.getColor(getActivity(), R.color.new_colorAccent));
                yearNewview3.setBackground(ResourcesCompat.getDrawable(getResources(), R.mipmap.h8_datareportperiodselected, null));
                getLindataFromServer("year");
                break;
            case R.id.newh8dataUserLi:  //至个人中心
                startActivity(new Intent(getActivity(), MyPersonalActivity.class));
                break;
            case R.id.h8_dataLinChartImg:   //至柱状图
                startActivity(new Intent(getActivity(), H8DataLinchartActivity.class));
                break;
            case R.id.h8_data_titleLinImg:  //至列表
                startActivity(new Intent(getActivity(), RecordHistoryActivity.class));
                break;
            case R.id.h8_dataShareImg:  //分享
                startActivity(new Intent(getActivity(),H8ShareActivity.class));
                break;

        }
    }

    private void clearViewBack() {
        weekNewview1.setBackground(null);
        monthNewview2.setBackground(null);
        yearNewview3.setBackground(null);
        weekNewview1.setBackgroundColor(getResources().getColor(R.color.black_c));
        monthNewview2.setBackgroundColor(getResources().getColor(R.color.black_c));
        yearNewview3.setBackgroundColor(getResources().getColor(R.color.black_c));
        weekNewtv1.setTextColor(Color.parseColor("#828282"));
        monthNewtv2.setTextColor(Color.parseColor("#828282"));
        yearNewtv3.setTextColor(Color.parseColor("#828282"));

    }


    private class weekTvClickListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
            switch (checkedId) {
                case R.id.xRb1:
                    showTopData(0);
                    xRb1.toggle();
                    break;
                case R.id.xRb2:
                    showTopData(1);
                    break;
                case R.id.xRb3:
                    showTopData(2);
                    break;
                case R.id.xRb4:
                    showTopData(3);
                    break;
                case R.id.xRb5:
                    showTopData(4);
                    break;
                case R.id.xRb6:
                    showTopData(5);
                    break;
                case R.id.xRb7:
                    showTopData(6);
                    break;
            }
        }
    }

    private void showTopData(int position) {
        watchNewh8DataStepTv.setText("" + watchDataList.get(position).getStepNumber() + "");
        watchNewh8DataDisTv.setText(watchDataList.get(position).getDistance() + getResources().getString(R.string.km));
        watchNewh8DataKcalTv.setText(watchDataList.get(position).getCalories() + getResources().getString(R.string.km_cal));
        watchNewh8DatadatelTv.setText(watchDataList.get(position).getRtc());
    }


    @SuppressLint("LongLogTag")
    public void LineChart(List<WatchDataDatyBean> wtList, int count, Map<String,Integer> mp){
        Log.e(TAG,"----count="+count);
        //数据解析
        entryList.clear();
        if(count == 12){    //年
            for(int k=0;k<=12;k++){
                entryList.add(new Entry(k,mp.get(dtLis.get(k))));
            }

        }else{
            for(int i = 0;i<count;i++){
                Log.e(TAG,"------解析="+wtList.get(i).getStepNumber());
                entryList.add(new Entry(i,wtList.get(i).getStepNumber()));
                dtLis.add(wtList.get(i).getRtc().substring(5, wtList.get(0).getRtc().length()));
            }
        }

        watchNewh8dataweekChar.getDescription().setEnabled(false);
        watchNewh8dataweekChar.setPinchZoom(false);
        watchNewh8dataweekChar.setScaleXEnabled(false);
        watchNewh8dataweekChar.setScaleEnabled(false);
        //是否拖动
        watchNewh8dataweekChar.setDragEnabled(false);
        //右侧Y轴
        YAxis leftAxis = watchNewh8dataweekChar.getAxisRight();  //得到图表的右侧Y轴实例
        leftAxis.enableGridDashedLine(10f, 20f, 0f); //设置横向表格为虚线
        leftAxis.setLabelCount(2,false);  //设置y轴显示的数量
        leftAxis.setGridLineWidth(0);
        leftAxis.setEnabled(false);

        //左侧y轴
        YAxis leftY = watchNewh8dataweekChar.getAxisLeft();
        leftY.setEnabled(false);

//        IAxisValueFormatter iAxisValueFormatter = new BarXFormartValue(watchNewh8dataweekChar,dtLis);

        //x轴
        XAxis xAxis = watchNewh8dataweekChar.getXAxis();  //设置X轴
        xAxis.setDrawGridLines(false);  //不显示网格线

        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return dtLis.get((int)value);
            }
        });
        if(count == 7){ //周
            xAxis.setEnabled(false);
        }else{
            xAxis.setEnabled(true);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        }

        Legend mLegend = watchNewh8dataweekChar.getLegend();
        mLegend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);//设置注解的位置在右
        mLegend.setForm(Legend.LegendForm.CIRCLE);// 样式
        mLegend.setFormSize(15.0f);// 字体
        mLegend.setTextColor(Color.BLUE);// 颜色
        mLegend.setEnabled(false);

        //设置数据
        LineDataSet lineDataSet = new LineDataSet(entryList, "");
        lineDataSet.setColor(Color.parseColor("#40e0d0")); //设置该线的颜色
        lineDataSet.setCircleColor(Color.parseColor("#40e0d0")); //设置节点圆圈颜色
        lineDataSet.setDrawValues(true); //设置是否显示点的坐标值
        lineDataSet.setValueTextColor(Color.parseColor("#5abdfe"));
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);// 设置平滑曲线

        //线的集合（可单条或多条线）
        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);
        //把要画的所有线(线的集合)添加到LineData里
        LineData lineData = new LineData(dataSets);

        watchNewh8dataweekChar.setDrawGridBackground(false);
        watchNewh8dataweekChar.setDrawGridBackground(false);
        //把最终的数据setData
        watchNewh8dataweekChar.setData(lineData);
        //动画
        watchNewh8dataweekChar.animateX(2000);
    }


}