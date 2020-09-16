package com.guider.healthring.b30.b30homefragment;


import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.guider.healthring.Commont;
import com.guider.healthring.MyApp;
import com.guider.healthring.R;
import com.guider.healthring.b30.B30BloadDetailActivity;
import com.guider.healthring.b30.B30DeviceActivity;
import com.guider.healthring.b30.B30HeartDetailActivity;
import com.guider.healthring.b30.B30HomeActivity;
import com.guider.healthring.b30.B30SleepDetailActivity;
import com.guider.healthring.b30.B30StepDetailActivity;
import com.guider.healthring.b30.ManualMeaureBloadActivity;
import com.guider.healthring.b30.ManualMeaureHeartActivity;
import com.guider.healthring.b30.b30view.B30CusBloadView;
import com.guider.healthring.b30.b30view.B30CusHeartView;
import com.guider.healthring.b30.b30view.B30CusSleepView;
import com.guider.healthring.b30.bean.B30HalfHourDB;
import com.guider.healthring.b30.bean.B30HalfHourDao;
import com.guider.healthring.b30.service.NewConnBleHelpService;
import com.guider.healthring.bleutil.MyCommandManager;
import com.guider.healthring.commdbserver.CommDBManager;
import com.guider.healthring.siswatch.LazyFragment;
import com.guider.healthring.siswatch.NewSearchActivity;
import com.guider.healthring.siswatch.utils.WatchConstants;
import com.guider.healthring.siswatch.utils.WatchUtils;
import com.guider.healthring.util.LocalizeTool;
import com.guider.healthring.util.SharedPreferencesUtils;
import com.guider.healthring.util.ToastUtil;
import com.guider.healthring.widget.WaveProgress;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.veepoo.protocol.model.datas.HalfHourBpData;
import com.veepoo.protocol.model.datas.HalfHourRateData;
import com.veepoo.protocol.model.datas.HalfHourSportData;
import com.veepoo.protocol.model.datas.SleepData;
import com.veepoo.protocol.model.datas.TimeData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2018/7/20.
 */

public class B30HomeFragment extends LazyFragment
        implements NewConnBleHelpService.ConnBleMsgDataListener,View.OnClickListener {

    View b30HomeFragment;
    WaveProgress b30ProgressBar;
    //日期
    TextView b30TopDateTv;
    //电量
    ImageView batteryTopImg;
    TextView batteryPowerTv;
    TextView b30ConnectStateTv;

    //目标步数
    TextView b30GoalStepTv;
    //运动步数的chart
    BarChart b30BarChart;
    //步数数据
    List<BarEntry> b30ChartList;
    //血压
    TextView bloadLastTimeTv;
    TextView b30BloadValueTv;
    /**
     * 血压图表
     */
    B30CusBloadView b30HomeBloadChart;
    //睡眠图表
    B30CusSleepView b30CusSleepView;
    TextView b30StartEndTimeTv;
    SmartRefreshLayout b30HomeSwipeRefreshLayout;
    TextView homeTodayTv;
    ImageView homeTodayImg;
    TextView homeYestTodayTv;
    ImageView iv_top;
    ImageView homeYestdayImg;
    TextView homeBeYestdayTv;
    ImageView homeBeYestdayImg;
    TextView homeFastStatusTv;
    /**
     * 日期的集合
     */
    private List<String> b30BloadList;
    /**
     * 一对高低血压集合
     */
    private List<Map<Integer, Integer>> bloadListMap;

    //血压的集合map，key : 时间；value : 血压值map
    private List<Map<String, Map<Integer, Integer>>> resultBpMapList;


    private List<BarEntry> tmpB30StepList;
    LinearLayout b30SportChartLin1;
    RelativeLayout b30ChartTopRel;

    //心率图标
    B30CusHeartView b30CusHeartView;
    //最后一次时间
    TextView lastTimeTv;
    //心率值
    TextView b30HeartValueTv;
    //心率图标数据
    List<Integer> heartList;
    //最大步数
    TextView b30SportMaxNumTv;
    //用于计算最大步数
    private List<Integer> tmpIntegerList;

    private NewConnBleHelpService connBleHelpService;
    /**
     * 上下文
     */
    private Context mContext;

    /**
     * 目标步数
     */
    int goalStep;
    //默认步数
    int defaultSteps = 0;

    private List<Integer> sleepList;
    /**
     * 当前显示哪天的数据(0_今天 1_昨天 2_前天)
     */
    private int currDay = 0;
    /**
     * Json帮助工具
     */
    private Gson gson;
    /**
     * 本地化工具类
     */
    private LocalizeTool mLocalTool;


    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (getActivity() != null && !getActivity().isFinishing() &&
                    b30HomeSwipeRefreshLayout != null) {
                b30HomeSwipeRefreshLayout.finishRefresh();// 停止下拉刷新
            }
            switch (msg.what) {
                case 1000:
                    mHandler.removeMessages(1001);// 正常关闭就移除延时口令

                    updatePageData();
                    B30HomeActivity activity = (B30HomeActivity) getActivity();
                    if (getActivity() != null) activity.startUploadDate();// 上传数据

                    //数据读取完后开始上传本地保存的数据
                    CommDBManager.getCommDBManager().startUploadDbService(MyApp.getContext());
                    break;
                case 1001:
//                    Log.d("bobo", "handleMessage: 请求超过默认秒数");
                    break;
                case 1002://步数
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        List<HalfHourSportData> sportData = (List<HalfHourSportData>) msg.obj;
                        b30SportMaxNumTv.setText("0" + getResources().getString(R.string.steps));
                        showSportStepData(sportData);//展示步数的图表
                    }
                    break;
                case 1003://心率
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        List<HalfHourRateData> rateData = (List<HalfHourRateData>) msg.obj;
                        showSportHeartData(rateData);//展示心率的图表
                    }
                    break;
                case 1004://血压
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        List<HalfHourBpData> bpData = (List<HalfHourBpData>) msg.obj;
                        showBloodData(bpData);//展示血压的图表
                    }
                    break;
                case 1005:
//                    if (getActivity() != null && !getActivity().isFinishing()) {
//                        SleepData sleepData = (SleepData) msg.obj;
//                        showSleepData(sleepData);//展示睡眠的图表
//                    }
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        if (b30StartEndTimeTv != null)
                            b30StartEndTimeTv.setText("");
//                            b30StartEndTimeTv.setText("--:--");
                        SleepData sleepData = (SleepData) msg.obj;
                        showSleepData(sleepData);//展示睡眠的图表
                    }
                    break;
            }
        }
    };


    @SuppressLint("WrongConstant")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WatchUtils.B30_CONNECTED_ACTION);
        intentFilter.addAction(WatchUtils.B30_DISCONNECTED_ACTION);
        mContext.registerReceiver(broadcastReceiver, intentFilter);
        if (connBleHelpService == null) {
            connBleHelpService = NewConnBleHelpService.getConnBleHelpService();
        }

        connBleHelpService.setConnBleMsgDataListener(this);
        //目标步数
        goalStep = (int) SharedPreferencesUtils.getParam(
                MyApp.getContext(), "b30Goal", 0);
        String saveDate = (String) SharedPreferencesUtils.getParam(
                mContext, "saveDate", "");
        if (WatchUtils.isEmpty(saveDate)) {
            SharedPreferencesUtils.setParam(mContext, "saveDate",
                    System.currentTimeMillis() / 1000 + "");
        }

        //保存的时间 用于防止切换过快
        String tmpSaveTime = (String) SharedPreferencesUtils.getParam(getmContext(),
                "save_curr_time", "");
        if (WatchUtils.isEmpty(tmpSaveTime))
            SharedPreferencesUtils.setParam(getmContext(), "save_curr_time",
                    System.currentTimeMillis() / 1000 + "");
//        MyLogUtil.e("---------------记录---onCreate");

        //setGuiderBemo();
    }


    private Context getmContext() {
        return mContext == null ? MyApp.getContext() : mContext;
    }

    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext
     * @param serviceName 是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        b30HomeFragment = inflater.inflate(R.layout.fragment_b30_home_layout,
                container, false);
        initViewIds();

        initViews();
        initData();
//        MyLogUtil.e("---------------记录---onCreateView");
        return b30HomeFragment;
    }

    private void initViewIds() {
        b30ProgressBar = b30HomeFragment.findViewById(R.id.b30ProgressBar);
        b30TopDateTv = b30HomeFragment.findViewById(R.id.b30_top_dateTv);
        batteryTopImg = b30HomeFragment.findViewById(R.id.batteryTopImg);
        batteryPowerTv = b30HomeFragment.findViewById(R.id.batteryPowerTv);
        b30ConnectStateTv = b30HomeFragment.findViewById(R.id.b30connectStateTv);
        b30GoalStepTv = b30HomeFragment.findViewById(R.id.b30GoalStepTv);
        b30BarChart = b30HomeFragment.findViewById(R.id.b30BarChart);
        bloadLastTimeTv = b30HomeFragment.findViewById(R.id.bloadLastTimeTv);
        b30BloadValueTv = b30HomeFragment.findViewById(R.id.b30BloadValueTv);
        b30HomeBloadChart = b30HomeFragment.findViewById(R.id.b30HomeBloadChart);
        b30CusSleepView = b30HomeFragment.findViewById(R.id.b30CusSleepView);
        b30StartEndTimeTv = b30HomeFragment.findViewById(R.id.b30StartEndTimeTv);
        b30HomeSwipeRefreshLayout = b30HomeFragment.findViewById(R.id.b30HomeSwipeRefreshLayout);
        homeTodayTv = b30HomeFragment.findViewById(R.id.homeTodayTv);
        homeTodayImg = b30HomeFragment.findViewById(R.id.homeTodayImg);
        homeYestTodayTv = b30HomeFragment.findViewById(R.id.homeYestTodayTv);
        iv_top = b30HomeFragment.findViewById(R.id.iv_top);
        homeYestdayImg = b30HomeFragment.findViewById(R.id.homeYestdayImg);
        homeBeYestdayTv = b30HomeFragment.findViewById(R.id.homeBeYestdayTv);
        homeBeYestdayImg = b30HomeFragment.findViewById(R.id.homeBeYestdayImg);
        homeFastStatusTv = b30HomeFragment.findViewById(R.id.homeFastStatusTv);
        b30SportChartLin1 = b30HomeFragment.findViewById(R.id.b30SportChartLin1);
        b30ChartTopRel = b30HomeFragment.findViewById(R.id.b30ChartTopRel);
        b30CusHeartView = b30HomeFragment.findViewById(R.id.b30HomeHeartChart);
        lastTimeTv = b30HomeFragment.findViewById(R.id.lastTimeTv);
        b30HeartValueTv = b30HomeFragment.findViewById(R.id.b30HeartValueTv);
        b30SportMaxNumTv = b30HomeFragment.findViewById(R.id.b30SportMaxNumTv);
        b30SportChartLin1.setOnClickListener(this);
        b30BarChart.setOnClickListener(this);
        b30HomeFragment.findViewById(R.id.b30CusHeartLin).setOnClickListener(this);
        b30HomeFragment.findViewById(R.id.b30CusBloadLin).setOnClickListener(this);
        b30HomeFragment.findViewById(R.id.b30MeaureHeartImg).setOnClickListener(this);
        b30HomeFragment.findViewById(R.id.b30MeaureBloadImg).setOnClickListener(this);
        b30HomeFragment.findViewById(R.id.b30SleepLin).setOnClickListener(this);
        b30HomeFragment.findViewById(R.id.homeFastLin).setOnClickListener(this);
        homeTodayTv.setOnClickListener(this);
        homeYestTodayTv.setOnClickListener(this);
        homeBeYestdayTv.setOnClickListener(this);
        b30HomeFragment.findViewById(R.id.battery_watchRecordShareImg).setOnClickListener(this);
    }

    private void initData() {
        if (b30GoalStepTv != null)
            b30GoalStepTv.setText(getResources().getString(R.string.goal_step) + goalStep + getResources().getString(R.string.steps));

        //运动图表
        b30ChartList = new ArrayList<>();
        tmpB30StepList = new ArrayList<>();
        tmpIntegerList = new ArrayList<>();
        //心率图表
        heartList = new ArrayList<>();
        //血压图表
        b30BloadList = new ArrayList<>();
        bloadListMap = new ArrayList<>();

        //血压图表
        resultBpMapList = new ArrayList<>();


        sleepList = new ArrayList<>();
        gson = new Gson();
        mLocalTool = new LocalizeTool(mContext);

        updatePageData();
    }


    private void initViews() {
        b30TopDateTv.setText(WatchUtils.getCurrentDate());
       // if (b30TopDateTv != null) b30TopDateTv.setText("    ");
        if (getActivity() != null && !getActivity().isFinishing() && b30ProgressBar != null) {
            b30ProgressBar.setMaxValue(goalStep);
            b30ProgressBar.setValue(defaultSteps);
        }
        if (b30SportChartLin1 != null)
            b30SportChartLin1.setBackgroundColor(getResources().getColor(R.color.b30_sport));
        if (iv_top != null) iv_top.setImageResource(R.mipmap.hx_home);
        if (b30HomeSwipeRefreshLayout != null) {
            if (MyCommandManager.DEVICENAME == null)
                b30HomeSwipeRefreshLayout.setEnableRefresh(false);
            b30HomeSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
                @Override
                public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                    getBleMsgData();
//                    Log.d("bobo", "onRefresh: getBleMsgData()");
                }
            });
        }

    }


    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {

        if (isVisible) {  //判断是否读取数据
            if (WatchUtils.isEmpty(WatchUtils.getSherpBleMac(MyApp.getContext())))
                return;
            int currCode = (int) SharedPreferencesUtils.getParam(MyApp.getContext(),
                    "code_status", 0);
            clearDataStyle(currCode);//设置每次回主界面，返回数据不清空的
            //updatePageData();
            if (connBleHelpService != null && MyCommandManager.DEVICENAME != null) {
                long currentTime = System.currentTimeMillis() / 1000;
                //保存的时间
                String tmpSaveTime = (String) SharedPreferencesUtils.getParam(MyApp.getContext(),
                        "saveDate", currentTime + "");
                long diffTime = 0;
                if (tmpSaveTime != null) {
                    diffTime = (currentTime - Long.parseLong(tmpSaveTime)) / 60;
                }
                if (WatchConstants.isScanConn) {  //是搜索进来的
                    WatchConstants.isScanConn = false;
                    //getBleMsgData();
                    if (b30HomeSwipeRefreshLayout != null) b30HomeSwipeRefreshLayout.autoRefresh();
                } else {  //不是搜索进来的
                    if (diffTime > 30) {// 大于十分钟没更新再取数据
                        getBleMsgData();
                        if (b30HomeSwipeRefreshLayout != null)
                            b30HomeSwipeRefreshLayout.autoRefresh();
                    }
                }
            }


        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    /* 这里会有断连的情况 */
    @Override
    public void onResume() {
        super.onResume();
        if (MyCommandManager.DEVICENAME != null && MyCommandManager.ADDRESS != null) {    //已连接
            if (b30ConnectStateTv != null)
                b30ConnectStateTv.setText(getResources().getString(R.string.connted));
            homeFastStatusTv.setText(getResources().getString(R.string.more_opera));
            int param = (int) SharedPreferencesUtils.getParam(MyApp.getContext(),
                    Commont.BATTERNUMBER, 0);
            if (param > 0) {
                showBatterStute(param);
//                int currCode = (int) SharedPreferencesUtils.getParam(getmContext(),"code_status",0);
//                clearDataStyle(currCode);//设置每次回主界面，返回数据不清空的
            }
        } else {  //未连接
            if (getActivity() != null && !getActivity().isFinishing()) {
                if (b30ConnectStateTv != null)
                    b30ConnectStateTv.setText(getResources().getString(R.string.disconnted));
                homeFastStatusTv.setText(getResources().getString(R.string.disconnted));
                B30HomeActivity activity = (B30HomeActivity) getActivity();
                if (activity != null) activity.reconnectDevice();// 重新连接
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (broadcastReceiver != null) {
                mContext.unregisterReceiver(broadcastReceiver);
            }
            if (b30ProgressBar != null) {
                b30ProgressBar.removeAnimator();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* 电量更新 */
    @Override
    public void getBleBatteryData(int batteryLevel) {
        SharedPreferencesUtils.setParam(getContext(), Commont.BATTERNUMBER, batteryLevel);//保存下电量
        showBatterStute(batteryLevel);
    }

    /**
     * 显示电量
     *
     * @param batteryLevel
     */
    @SuppressLint("SetTextI18n")
    void showBatterStute(int batteryLevel) {
        try {
            if (batteryTopImg == null) return;
            if (batteryLevel >= 0 && batteryLevel == 1) {
                batteryTopImg.setBackground(getResources().getDrawable(R.mipmap.image_battery_two));
            } else if (batteryLevel == 2) {
                batteryTopImg.setBackground(getResources().getDrawable(R.mipmap.image_battery_three));
            } else if (batteryLevel == 3) {
                batteryTopImg.setBackground(getResources().getDrawable(R.mipmap.image_battery_four));
            } else if (batteryLevel == 4) {
                batteryTopImg.setBackground(getResources().getDrawable(R.mipmap.image_battery_five));
            }
            if (batteryPowerTv != null) batteryPowerTv.setText("" + batteryLevel * 25 + "%");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* 手环实时步数有更新 */
    @Override
    public void getBleSportData(int step) {
        B30HalfHourDB db = new B30HalfHourDB();
        db.setAddress(MyApp.getInstance().getMacAddress());
        db.setDate(WatchUtils.obtainFormatDate(currDay));
        db.setType(B30HalfHourDao.TYPE_STEP);
        db.setOriginData("" + step);
        B30HalfHourDao.getInstance().saveOriginData(db);
        defaultSteps = step;
        if (getActivity() != null && !getActivity().isFinishing() && b30ProgressBar != null) {
            b30ProgressBar.setMaxValue(goalStep);
            b30ProgressBar.setValue(step);
        }
    }

    /* 原始数据有更新 */
    @Override
    public void onOriginData() {
        mHandler.sendEmptyMessage(1000);// 步数和健康数据都取到了,就关闭刷新条
//        updatePageData();
//        B30HomeActivity activity = (B30HomeActivity) getActivity();
//        if (getActivity() != null) activity.startUploadDate();// 上传数据
    }

    /**
     * 更新页面数据
     */
    private void updatePageData() {
        String mac = WatchUtils.getSherpBleMac(MyApp.getContext());
        String date = WatchUtils.obtainFormatDate(currDay);
        if (WatchUtils.isEmpty(mac))
            return;
        Log.e("TAG  updatePageData ", "-------mac=" + mac + "--date=" + date);
        updateStepData(mac, date);  //更新步数
        updateSportData(mac, date); //更新运动图表数据
        updateRateData(mac, date);  //更新心率数据
        updateSleepData(mac, WatchUtils.obtainFormatDate(currDay)); //睡眠数据
        //血压
        updateBpData(mac, date);//更新血压数据


//
//        if (runnable == null)
//            mHandler.removeCallbacks(runnable);
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                String mac = MyApp.getInstance().getMacAddress();
//                if (mac == null) return;
//                String date = WatchUtils.obtainFormatDate(currDay);
//                updateStepData(mac, date);
//                updateSportData(mac, date);
//                updateRateData(mac, date);
//                updateBpData(mac, date);
////                updateSleepData(mac, WatchUtils.obtainFormatDate(currDay + 1));
//                updateSleepData(mac, WatchUtils.obtainFormatDate(currDay)); //睡眠数据
//            }
//        };
//        mHandler.post(runnable);

    }

    /**
     * 取出本地步数数据,并显示
     */
    private void updateStepData(String mac, String date) {
//        Log.e("-------步数 数据时间----", date);
        String step = B30HalfHourDao.getInstance().findOriginData(mac, date, B30HalfHourDao
                .TYPE_STEP);
        if (WatchUtils.isEmpty(step))
            step = 0 + "";
        int stepLocal = 0;
        try {
            stepLocal = Integer.valueOf(step);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        defaultSteps = stepLocal;
        if (getActivity() != null && !getActivity().isFinishing() && b30ProgressBar != null) {
            b30ProgressBar.setMaxValue(goalStep);
            b30ProgressBar.setValue(stepLocal);
        }

    }

    /**
     * 取出本地运动数据
     */
    private void updateSportData(String mac, String date) {
//        Log.e("-------运动 数据时间----", date);
        String sport = B30HalfHourDao.getInstance().findOriginData(mac, date, B30HalfHourDao
                .TYPE_SPORT);
        List<HalfHourSportData> sportData = gson.fromJson(sport, new TypeToken<List<HalfHourSportData>>() {
        }.getType());

        Message message = new Message();
        message.what = 1002;
        message.obj = sportData;
        mHandler.sendMessage(message);//发送消息，展示步数图标

//        if (getActivity() != null && !getActivity().isFinishing() && b30SportMaxNumTv != null)
//            b30SportMaxNumTv.setText("0" + getResources().getString(R.string.steps));
//        showSportStepData(sportData);//展示步数的图表
    }

    /**
     * 取出本地心率数据
     */
    private void updateRateData(String mac, String date) {
//        Log.e("-------心率 数据时间----", date);
        String rate = B30HalfHourDao.getInstance().findOriginData(mac, date, B30HalfHourDao
                .TYPE_RATE);
        List<HalfHourRateData> rateData = gson.fromJson(rate, new TypeToken<List<HalfHourRateData>>() {
        }.getType());
        Message message = new Message();
        message.what = 1003;
        message.obj = rateData;
        mHandler.sendMessage(message);//发送消息，展示心率的图表
//        showSportHeartData(rateData);//展示心率的图表
    }

    /**
     * 取出本地血压数据
     */
    private void updateBpData(String mac, String date) {
//        Log.e("-------血压 数据时间----", date);
        String bp = B30HalfHourDao.getInstance().findOriginData(mac, date, B30HalfHourDao.TYPE_BP);
        List<HalfHourBpData> bpData = gson.fromJson(bp, new TypeToken<List<HalfHourBpData>>() {
        }.getType());
        Message message = new Message();
        message.what = 1004;
        message.obj = bpData;
        mHandler.sendMessage(message);//发送消息，展示血压的图表
//        showBloodData(bpData);//展示血压的图表
    }

    /**
     * 取出本地睡眠数据
     */
    private void updateSleepData(String mac, String date) {
//        Log.e("-------睡眠 数据时间----", date);
        String sleep = B30HalfHourDao.getInstance().findOriginData(mac, date, B30HalfHourDao
                .TYPE_SLEEP);
        SleepData sleepData = gson.fromJson(sleep, SleepData.class);
        Message message = mHandler.obtainMessage();
        message.what = 1005;
        message.obj = sleepData;
        mHandler.sendMessage(message);//发送消息，展示睡眠的图表
//        showSleepData(sleepData);//展示睡眠的图表
    }

/*//    /**
//     * 展示睡眠图表
//     */
//    private void showSleepData(SleepData sleepData) {
//        if (getActivity() == null || getActivity().isFinishing()) return;
//        sleepList.clear();
//        if (sleepData != null) {
//            if (b30StartEndTimeTv != null)
//                b30StartEndTimeTv.setText(sleepData.getSleepDown().getColck() + "-" + sleepData.getSleepUp().getColck());
//            String sleepLin = sleepData.getSleepLine();
//            for (int i = 0; i < sleepLin.length(); i++) {
//                if (i <= sleepLin.length() - 1) {
//                    int subStr = Integer.valueOf(sleepLin.substring(i, i + 1));
//                    sleepList.add(subStr);
//                }
//            }
//            sleepList.add(0, 2);
//            sleepList.add(sleepLin.length(), 2);
//        } else {
//            if (b30StartEndTimeTv != null) b30StartEndTimeTv.setText("");
//        }
//        if (sleepList != null && !sleepList.isEmpty()) {
//            if (b30CusSleepView != null) b30CusSleepView.setSleepList(sleepList);
//        } else {
//            if (b30CusSleepView != null) b30CusSleepView.setSleepList(new ArrayList<Integer>());
//        }
//    }*/


//    /**
//     * 展示睡眠图表
//     */
//    private void showSleepData(SleepData sleepData) {
//        if (getActivity() == null || getActivity().isFinishing()) return;
//        sleepList.clear();
//
//        if (sleepData != null) {
//            Log.e("睡眠", "-------sleepData=" + sleepData.toString());
//            if (b30StartEndTimeTv != null)
//                b30StartEndTimeTv.setText(sleepData.getSleepDown().getColck() + "-" + sleepData.getSleepUp().getColck());
//            String sleepLin = sleepData.getSleepLine();
//            //Log.e(TAG, "-----睡眠的长度=" + sleepLin + "---=length=" + sleepLin.length());
//            if (WatchUtils.isEmpty(sleepLin) || sleepLin.length() < 2) {
//                if (b30CusSleepView != null) b30CusSleepView.setSleepList(new ArrayList<Integer>());
//                return;
//            }
//            for (int i = 0; i < sleepLin.length(); i++) {
//                if (i <= sleepLin.length() - 1) {
//                    int subStr = Integer.valueOf(sleepLin.substring(i, i + 1));
//                    sleepList.add(subStr);
//                }
//            }
//            sleepList.add(0, 2);
//            sleepList.add(0);
//            sleepList.add(2);
//        } else {
////            if (b30StartEndTimeTv != null) b30StartEndTimeTv.setText("--:--");
//            if (b30StartEndTimeTv != null) b30StartEndTimeTv.setText("");
//        }
//        if (sleepList != null && !sleepList.isEmpty()) {
//            if (b30CusSleepView != null) {
//                b30CusSleepView.setShowSeekBar(false);
//                b30CusSleepView.setSleepList(sleepList);
//            }
//        } else {
//            if (b30CusSleepView != null) {
//                b30CusSleepView.setShowSeekBar(false);
//                b30CusSleepView.setSleepList(new ArrayList<Integer>());
//            }
//        }
//    }

    /**
     * 展示睡眠图表
     */
    private void showSleepData(SleepData sleepData) {
        if (getActivity() == null || getActivity().isFinishing()) return;
        sleepList.clear();
        if (sleepData != null) {
            if (b30StartEndTimeTv != null)
                b30StartEndTimeTv.setText(sleepData.getSleepDown().getColck() + "-" + sleepData.getSleepUp().getColck());
            String sleepLin = sleepData.getSleepLine();
//            Log.e(TAG, "-----睡眠的长度=" + sleepLin + "---=length=" + sleepLin.length());
            if (!WatchUtils.isEmpty(sleepLin) || sleepLin.length() > 2) {
                if (b30CusSleepView != null) b30CusSleepView.setSleepList(new ArrayList<Integer>());
                for (int i = 0; i < sleepLin.length(); i++) {
                    if (i <= sleepLin.length() - 1) {
                        int subStr = Integer.valueOf(sleepLin.substring(i, i + 1));
                        sleepList.add(subStr);
                    }
                }
                sleepList.add(0, 2);
                sleepList.add(0);
                sleepList.add(2);
            }
        } else {
            if (b30StartEndTimeTv != null) b30StartEndTimeTv.setText("");
        }

        if (b30CusSleepView != null) {
            if (sleepList != null && !sleepList.isEmpty()) {
                b30CusSleepView.setSeekBarShow(false);
                b30CusSleepView.setSleepList(sleepList);
            } else {
                b30CusSleepView.setSeekBarShow(false);
                b30CusSleepView.setSleepList(new ArrayList<Integer>());
            }
        }

    }

    /**
     * 展示血压图表
     */
    @SuppressLint("SetTextI18n")
    private void showBloodData(List<HalfHourBpData> bpData) {
//        if (getActivity() == null || getActivity().isFinishing()) return;
//        b30BloadList.clear();
//        bloadListMap.clear();
//        if (bpData != null && !bpData.isEmpty()) {
////            Log.e("-----血压数据-A",""+bpData.size());
////            LogTestUtil.e("-----血压数据-",""+JSON.toJSONString(bpData));
//            //获取日期
//            for (HalfHourBpData halfHourBpData : bpData) {
//                b30BloadList.add(halfHourBpData.getTime().getColck());// 时:分
//                Map<Integer, Integer> mp = new HashMap<>();
//                mp.put(halfHourBpData.getLowValue(), halfHourBpData.getHighValue());
//                bloadListMap.add(mp);
//            }
//            //最近一次的血压数据
//            HalfHourBpData lastHalfHourBpData = bpData.get(bpData.size() - 1);
//            if (lastHalfHourBpData != null) {
//                if (bloadLastTimeTv != null)
//                    bloadLastTimeTv.setText(getResources().getString(R.string.string_recent) + " " + lastHalfHourBpData.getTime().getColck());
//                //最近时间的血压高低值
//                if (b30BloadValueTv != null)
//                    b30BloadValueTv.setText(lastHalfHourBpData.getHighValue() + "/" + lastHalfHourBpData.getLowValue() + "mmhg");
//            }
//        }
////        b30HomeBloadChart.setTimeList(b30BloadList);
////        b30HomeBloadChart.setMapList(bloadListMap);
//        if (b30HomeBloadChart != null) b30HomeBloadChart.setDataMap(obtainBloodMap(bpData));
//        if (b30HomeBloadChart != null) b30HomeBloadChart.setScale(false);


        if (getActivity() == null || getActivity().isFinishing()) return;
        b30BloadList.clear();
        bloadListMap.clear();
        resultBpMapList.clear();
        if (bpData != null && !bpData.isEmpty()) {
            //获取日期
            for (HalfHourBpData halfHourBpData : bpData) {
                Map<String, Map<Integer, Integer>> mapMap = new HashMap<>();

                b30BloadList.add(halfHourBpData.getTime().getColck());// 时:分
                Map<Integer, Integer> mp = new HashMap<>();
                mp.put(halfHourBpData.getLowValue(), halfHourBpData.getHighValue());
                bloadListMap.add(mp);

                mapMap.put(halfHourBpData.getTime().getColck(), mp);
                resultBpMapList.add(mapMap);
            }
            //最近一次的血压数据
            HalfHourBpData lastHalfHourBpData = bpData.get(bpData.size() - 1);
            if (lastHalfHourBpData != null) {
                if (bloadLastTimeTv != null)
                    bloadLastTimeTv.setText(getResources().getString(R.string.string_recent) + " " + lastHalfHourBpData.getTime().getColck());
                //最近时间的血压高低值
                if (b30BloadValueTv != null)
                    b30BloadValueTv.setText(lastHalfHourBpData.getHighValue() + "/" + lastHalfHourBpData.getLowValue() + "mmhg");
            }
        }
        if (b30HomeBloadChart != null)
            b30HomeBloadChart.setBpVerticalMap(resultBpMapList);

    }

    /**
     * 统计血压数据源
     *
     * @param bpData 手环源数据
     * @return Map结果: String:日期 Point:x低压_y高压
     */
    private Map<String, Point> obtainBloodMap(List<HalfHourBpData> bpData) {
        if (bpData == null || bpData.isEmpty()) return null;
        Map<String, Point> dataMap = new HashMap<>();
        for (HalfHourBpData item : bpData) {
            String time = item.getTime().getColck();
            //Log.e("B30", "  =====  统计血压  " + item.getLowValue() + "---" + item.getHighValue());
            Point point = new Point(item.getLowValue(), item.getHighValue());
            dataMap.put(time, point);
        }
        return dataMap;
    }

    /**
     * 展示心率图表
     */
    private void showSportHeartData(List<HalfHourRateData> rateData) {
        if (getActivity() == null || getActivity().isFinishing()) return;
        heartList.clear();
        if (rateData != null && !rateData.isEmpty()) {
            List<Map<String, Integer>> listMap = new ArrayList<>();
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
            for (int i = 0; i < listMap.size(); i++) {
                Map<String, Integer> map = listMap.get(i);
                heartList.add(map.get("val"));
            }
            HalfHourRateData lastHalfHourRateData = rateData.get(rateData.size() - 1);
            if (lastHalfHourRateData != null) {
                if (lastTimeTv != null)
                    lastTimeTv.setText(getResources().getString(R.string.string_recent) + " " + lastHalfHourRateData.getTime().getColck());
                if (b30HeartValueTv != null)
                    b30HeartValueTv.setText(lastHalfHourRateData.getRateValue() + " bpm");
            }
//            b30CusHeartView.setPointRadio(5);//圆点的半径
            if (b30CusHeartView != null) b30CusHeartView.setRateDataList(heartList);
        } else {
//            b30CusHeartView.setPointRadio(5);//圆点的半径
            if (b30CusHeartView != null) b30CusHeartView.setRateDataList(heartList);
        }
    }

    /**
     * 展示步数的图表
     */
    private void showSportStepData(List<HalfHourSportData> sportData) {
        if (getActivity() == null || getActivity().isFinishing()) return;
        if (b30ChartList != null) b30ChartList.clear();
        if (tmpIntegerList != null) tmpIntegerList.clear();
        if (tmpB30StepList != null) tmpB30StepList.clear();

        if (sportData != null && !sportData.isEmpty()) {
            List<Map<String, Integer>> listMap = new ArrayList<>();
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

            for (int i = 0; i < listMap.size(); i++) {
                Map<String, Integer> tmpMap = listMap.get(i);
                if (tmpB30StepList != null) tmpB30StepList.add(new BarEntry(i, tmpMap.get("val")));
                if (tmpIntegerList != null) tmpIntegerList.add(tmpMap.get("val"));
            }
            if (b30ChartList != null) b30ChartList.addAll(tmpB30StepList);
            if (b30SportMaxNumTv != null)
                b30SportMaxNumTv.setText(Collections.max(tmpIntegerList) + getResources().getString(R.string.steps));
            initBarChart(b30ChartList);
            if (b30BarChart != null) b30BarChart.invalidate();
        } else {
            initBarChart(b30ChartList);
            if (b30BarChart != null) {
                b30BarChart.setNoDataTextColor(Color.WHITE);
                b30BarChart.invalidate();
            }
        }
    }

    //步数图表展示
    @SuppressWarnings("deprecation")
    private void initBarChart(List<BarEntry> pointbar) {
        BarDataSet barDataSet = new BarDataSet(pointbar, "");
        barDataSet.setDrawValues(false);//是否显示柱子上面的数值
        //barDataSet.setColor(Color.parseColor("#fa8072"));//设置第一组数据颜色
        barDataSet.setColor(Color.parseColor("#88d785"));//设置第一组数据颜色


        if (b30BarChart == null) return;
        Legend mLegend = b30BarChart.getLegend();
        mLegend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);//设置注解的位置在右
        mLegend.setForm(Legend.LegendForm.CIRCLE);// 样式
        mLegend.setFormSize(15.0f);// 字体
        mLegend.setTextColor(Color.BLUE);// 颜色
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
        b30BarChart.setPinchZoom(false);
        b30BarChart.setScaleEnabled(false);
        b30BarChart.setTouchEnabled(false);

        b30BarChart.getDescription().setEnabled(false);

        b30BarChart.getAxisRight().setEnabled(false);//右侧不显示Y轴
        b30BarChart.getAxisLeft().setAxisMinValue(0.8f);//设置Y轴显示最小值，不然0下面会有空隙
        b30BarChart.getAxisLeft().setDrawGridLines(false);//不设置Y轴网格
        b30BarChart.getAxisLeft().setEnabled(false);

        b30BarChart.getXAxis().setSpaceMax(0.5f);
        b30BarChart.animateXY(1000, 2000);//设置动画
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b30SportChartLin1: // 运动数据详情
            case R.id.b30BarChart: // 运动数据详情
                B30StepDetailActivity.startAndParams(getActivity(), WatchUtils.obtainFormatDate(currDay));
                break;
            case R.id.b30CusHeartLin:   //心率详情
                B30HeartDetailActivity.startAndParams(getActivity(), WatchUtils.obtainFormatDate(currDay));
                break;
            case R.id.b30CusBloadLin:   //血压详情
                B30BloadDetailActivity.startAndParams(getActivity(), WatchUtils.obtainFormatDate(currDay));
                break;
            case R.id.b30MeaureHeartImg:    //手动测量心率
                if (!Commont.DevicesSport) {
                    startActivity(new Intent(getActivity(), ManualMeaureHeartActivity.class));
                } else {
                    ToastUtil.showShort(getContext(), getResources().getString(R.string.string_out_devices_sport));
                }

                break;
            case R.id.b30MeaureBloadImg:    //手动测量血压
                if (!Commont.DevicesSport) {
                    startActivity(new Intent(getActivity(), ManualMeaureBloadActivity.class));
                } else {
                    ToastUtil.showShort(getContext(), getResources().getString(R.string.string_out_devices_sport));
                }

                break;
            case R.id.b30SleepLin:      //睡眠详情
//                B30SleepDetailActivity.startAndParams(getActivity(), WatchUtils.obtainFormatDate
//                        (currDay + 1));
                B30SleepDetailActivity.startAndParams(getActivity(), WatchUtils.obtainFormatDate(currDay));
                break;
            case R.id.homeTodayTv:  //今天
                clearDataStyle(0);
                break;
            case R.id.homeYestTodayTv:  //昨天
                clearDataStyle(1);
                break;
            case R.id.homeBeYestdayTv:  //前天
                clearDataStyle(2);
                break;
            case R.id.battery_watchRecordShareImg:  //分享
                WatchUtils.shareCommData(getActivity());
                break;
            case R.id.homeFastLin:
                if(MyCommandManager.DEVICENAME != null){
                    startActivity(new Intent(getmContext(),B30DeviceActivity.class));
                }else{
                    MyApp.getInstance().getB30ConnStateService().stopAutoConn();
                    startActivity(new Intent(getActivity(), NewSearchActivity.class));
                    if (getActivity() != null)
                        getActivity().finish();
                }
                break;
        }
    }

    /**
     * 获取手环的数据
     */
    Runnable runnable;

    private void getBleMsgData() {
        if (runnable == null)
            mHandler.removeCallbacks(runnable);
        runnable = new Runnable() {
            @Override
            public void run() {
                SharedPreferencesUtils.setParam(mContext, "saveDate", System.currentTimeMillis() / 1000 + "");
                connBleHelpService.getDeviceMsgData();
                String date = mLocalTool.getUpdateDate();// 最后更新总数据的日期
                long delayMillis = 20 * 1000;// 默认超时时间
                if (date.equals(WatchUtils.obtainFormatDate(0))) {
                    connBleHelpService.readAllHealthData(true);// 只刷新今天数据
                } else {
                    connBleHelpService.readAllHealthData(false);// 刷新三天数据
                    delayMillis = 40 * 1000;
                }
                mHandler.sendEmptyMessageDelayed(1001, delayMillis);
            }
        };
        mHandler.post(runnable);

    }


    private void clearDataStyle(final int code) {
        //Log.e(TAG,"--------code="+code+"---currDay="+currDay);
        long currentTime = System.currentTimeMillis() / 1000;   //当前时间
        //保存的时间
        String tmpSaveTime = (String) SharedPreferencesUtils.getParam(MyApp.getContext(), "save_curr_time", "");
        long diffTime = (currentTime - Long.valueOf(tmpSaveTime));
        if (diffTime < 2)
            return;
        SharedPreferencesUtils.setParam(MyApp.getContext(), "save_curr_time", System.currentTimeMillis() / 1000 + "");

        //if (code == currDay) return;// 防重复点击
        if (homeTodayImg != null) homeTodayImg.setVisibility(View.INVISIBLE);
        if (homeYestdayImg != null) homeYestdayImg.setVisibility(View.INVISIBLE);
        if (homeBeYestdayImg != null) homeBeYestdayImg.setVisibility(View.INVISIBLE);
        switch (code) {
            case 0: //今天
                if (homeTodayImg != null) homeTodayImg.setVisibility(View.VISIBLE);
                break;
            case 1: //昨天
                if (homeYestdayImg != null) homeYestdayImg.setVisibility(View.VISIBLE);
                break;
            case 2: //前天
                if (homeBeYestdayImg != null) homeBeYestdayImg.setVisibility(View.VISIBLE);
                break;
        }
        currDay = code;
        SharedPreferencesUtils.setParam(MyApp.getContext(), "code_status", code);
        updatePageData();

//        if (runnable == null)
//            mHandler.removeCallbacks(runnable);
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                if (code == currDay) return;// 防重复点击
//                if (homeTodayImg != null) homeTodayImg.setVisibility(View.INVISIBLE);
//                if (homeYestdayImg != null) homeYestdayImg.setVisibility(View.INVISIBLE);
//                if (homeBeYestdayImg != null) homeBeYestdayImg.setVisibility(View.INVISIBLE);
//                switch (code) {
//                    case 0: //今天
//                        if (homeTodayImg != null) homeTodayImg.setVisibility(View.VISIBLE);
//                        break;
//                    case 1: //昨天
//                        if (homeYestdayImg != null) homeYestdayImg.setVisibility(View.VISIBLE);
//                        break;
//                    case 2: //前天
//                        if (homeBeYestdayImg != null) homeBeYestdayImg.setVisibility(View.VISIBLE);
//                        break;
//                }
//                currDay = code;
//                updatePageData();
//            }
//        };
//        mHandler.post(runnable);

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @SuppressLint("WrongConstant")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(WatchUtils.B30_CONNECTED_ACTION)) { //连接
                    homeFastStatusTv.setText(getResources().getString(R.string.more_opera));
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        String textConn = getResources().getString(R.string.connted);
                        if (b30ConnectStateTv != null) b30ConnectStateTv.setText(textConn);
                        if (connBleHelpService != null && MyCommandManager.DEVICENAME != null) {
                            if (b30HomeSwipeRefreshLayout != null) {
                                b30HomeSwipeRefreshLayout.setEnableRefresh(true);
//                            getBleMsgData(true);// 连接上后读数据
                                b30HomeSwipeRefreshLayout.autoRefresh();
                                // setGuiderBemo();
                            }
                        }
                    }
                }
                if (action.equals(WatchUtils.B30_DISCONNECTED_ACTION)) {  //断开
                    MyCommandManager.ADDRESS = null;// 断开连接了就设置为null
                    homeFastStatusTv.setText(getResources().getString(R.string.disconnted));
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        String textDis = getResources().getString(R.string.disconnted);
                        if (b30HomeSwipeRefreshLayout != null)
                            b30HomeSwipeRefreshLayout.setEnableRefresh(false);
                        if (b30ConnectStateTv != null) b30ConnectStateTv.setText(textDis);

                        // SharedPreferences_status.save_IMEI(MyApplication.context, "");
                    }
                }
            }
        }
    };


    @SuppressLint("WrongConstant")
    private void setGuiderBemo() {
        if (MyCommandManager.DEVICENAME != null && MyCommandManager.ADDRESS != null) {
            if (!WatchUtils.isEmpty(MyCommandManager.ADDRESS)) {
                try {
                    boolean bleServices = isServiceWork(getActivity(), "hat.bemo.BlueTooth.blegatt.baseService.BLEScanService");
                    if (!bleServices) {
                        if (serviceConnection != null) {
                            // getActivity().bindService(new Intent(getActivity(), BLEScanService.class), serviceConnection, Service.BIND_AUTO_CREATE);
                        }
                    }
                    boolean myServices = isServiceWork(getActivity(), "hat.bemo.MyService");
                    if (!myServices) {
                        if (serviceConnection != null) {
                            //getActivity().bindService(new Intent(getActivity(), MyService.class), serviceConnection, Service.BIND_AUTO_CREATE);
                        }
                    }
                    //ToastUtil.showShort(getActivity(),"要传递过去的mac"+MyCommandManager.ADDRESS);
//                    Intent intents = new Intent(getActivity(), MainActivity.class);
//                    intents.setFlags(101);//傳送設備碼
//                    intents.putExtra("data", MyCommandManager.ADDRESS);
//                    getActivity().startActivity(intents); //REQUESTCODE--->1
                } catch (Error e) {
                    e.printStackTrace();
                }
            }
        }
    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };


}
