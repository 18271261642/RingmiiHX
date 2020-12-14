package com.guider.healthring.b31.record;

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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.guider.health.common.utils.StringUtil;
import com.guider.healthring.Commont;
import com.guider.healthring.MyApp;
import com.guider.healthring.R;
import com.guider.healthring.b30.B30BloadDetailActivity;
import com.guider.healthring.b30.B30HeartDetailActivity;
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
import com.guider.healthring.b31.B31DeviceActivity;
import com.guider.healthring.b31.B31HomeActivity;
import com.guider.healthring.b31.B31ManFatigueActivity;
import com.guider.healthring.b31.B31ManSpO2Activity;
import com.guider.healthring.b31.B31RespiratoryRateActivity;
import com.guider.healthring.b31.InternalTestActivity;
import com.guider.healthring.b31.bpoxy.B31BpOxyAnysisActivity;
import com.guider.healthring.b31.bpoxy.ReadHRVSoDataForDevices;
import com.guider.healthring.b31.bpoxy.ShowSpo2DetailActivity;
import com.guider.healthring.b31.bpoxy.util.ChartViewUtil;
import com.guider.healthring.b31.bpoxy.util.VpSpo2hUtil;
import com.guider.healthring.b31.hrv.B31HrvDetailActivity;
import com.guider.healthring.b31.model.B31HRVBean;
import com.guider.healthring.b31.model.B31Spo2hBean;
import com.guider.healthring.bleutil.MyCommandManager;
import com.guider.healthring.commdbserver.CommDBManager;
import com.guider.healthring.siswatch.LazyFragment;
import com.guider.healthring.siswatch.NewSearchActivity;
import com.guider.healthring.siswatch.utils.WatchConstants;
import com.guider.healthring.siswatch.utils.WatchUtils;
import com.guider.healthring.util.Constant;
import com.guider.healthring.util.LocalizeTool;
import com.guider.healthring.util.SharedPreferencesUtils;
import com.guider.healthring.widget.WaveProgress;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.model.datas.HRVOriginData;
import com.veepoo.protocol.model.datas.HalfHourBpData;
import com.veepoo.protocol.model.datas.HalfHourRateData;
import com.veepoo.protocol.model.datas.HalfHourSportData;
import com.veepoo.protocol.model.datas.SleepData;
import com.veepoo.protocol.model.datas.Spo2hOriginData;
import com.veepoo.protocol.model.datas.TimeData;
import com.veepoo.protocol.model.enums.ESpo2hDataType;
import com.veepoo.protocol.util.HRVOriginUtil;
import com.veepoo.protocol.util.HrvScoreUtil;
import com.veepoo.protocol.util.Spo2hOriginUtil;
import org.litepal.LitePal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.guider.healthring.b31.bpoxy.enums.Constants.CHART_MAX_HRV;
import static com.guider.healthring.b31.bpoxy.enums.Constants.CHART_MAX_SPO2H;
import static com.guider.healthring.b31.bpoxy.enums.Constants.CHART_MIN_HRV;
import static com.guider.healthring.b31.bpoxy.enums.Constants.CHART_MIN_SPO2H;
import static com.veepoo.protocol.model.enums.ESpo2hDataType.TYPE_BEATH_BREAK;
import static com.veepoo.protocol.model.enums.ESpo2hDataType.TYPE_HRV;
import static com.veepoo.protocol.model.enums.ESpo2hDataType.TYPE_SPO2H;

/**
 * B31的记录页面
 * Created by Admin
 * Date 2018/12/17
 */
public class B31RecordFragment extends LazyFragment
        implements NewConnBleHelpService.ConnBleMsgDataListener, View.OnClickListener {

    private static final String TAG = "B31RecordFragment";
    ImageView ivTop;

    //HRV的图表
    LineChart b31HomeHrvChart;
    //血氧的图表
    LineChart homeSpo2LinChartView;
    TextView b31Spo2AveTv;
    //心脏健康指数
    TextView hrvHeartSocreTv;
    //快捷方式
    TextView homeFastStatusTv;

    private View b31View;

    TextView b30connectStateTv;
    ImageView batteryTopImg;
    //电池电量显示
    TextView batteryPowerTv;
    //日期
    TextView b30TopDateTv;
    //电量图片
    ImageView batteryWatchRecordShareImg;
    LinearLayout watchRecordTitleLin;
    //波浪形进度条
    WaveProgress b31ProgressBar;
    //目标步数显示
    TextView b31GoalStepTv;
    //今天
    TextView b31HomeTodayTv;
    ImageView b31HomeTodayImg;
    //昨天
    TextView b31HomeYestTodayTv;
    ImageView b31HomeYestdayImg;
    //前天
    TextView b31HomeBeYestdayTv;
    ImageView b31HomeBeYestdayImg;
    //运动图表最大步数
    TextView b30SportMaxNumTv;
    RelativeLayout b30ChartTopRel;
    //运动图表
    BarChart b30BarChart;
    TextView b30StartEndTimeTv;
    //睡眠图表
    B30CusSleepView b30CusSleepView;
    TextView lastTimeTv;
    TextView b30HeartValueTv;
    //心率图表
    B30CusHeartView b30HomeHeartChart;
    SmartRefreshLayout b31HomeSwipeRefreshLayout;
    //同步的状态
    TextView syncStatusTv;
    //血压测量的按钮
    LinearLayout item_blood;
    //血压
    LinearLayout b30CusBloadLin;
    //图表的布局
    ConstraintLayout spo2ChartListLayout;
    //B31S和500S血压测量的布局
    LinearLayout homeBpManLin;

    /**
     * 当前显示哪天的数据(0_今天 1_昨天 2_前天)
     */
    private int currDay = 0;
    //默认步数
    int defaultSteps = 0;
    /**
     * 目标步数
     */
    int goalStep;

    /**
     * 本地化工具类
     */
    private LocalizeTool mLocalTool;
    private Gson gson = new Gson();

    //展示睡眠数据的集合
    private List<Integer> sleepList;

    //展示心率数据的集合
    List<Integer> heartList;

    //步数数据
    List<BarEntry> b30ChartList;
    //用于计算最大步数
    private List<Integer> tmpIntegerList;
    //设置步数图表的临时数据
    private List<BarEntry> tmpB30StepList;


    private NewConnBleHelpService connBleHelpService;
    private Context mContext;

    private List<Spo2hOriginData> spo2hOriginDataList = new ArrayList<>();


    VpSpo2hUtil vpSpo2hUtil;
    LineChart mChartViewSpo2h;  //呼吸暂停图表
    LineChart mChartViewHeart;  //心脏负荷图表
    LineChart mChartViewSleep;  //睡眠活动图表
    LineChart mChartViewBreath; //呼吸率图表
    LineChart mChartViewLowspo2h;   //低氧时间图表

    private boolean iSNllSleep = false;

    //血压
    TextView bloadLastTimeTv;
    TextView b30BloadValueTv;
    /**
     * 血压图表
     */
    B30CusBloadView b30HomeBloadChart;

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


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1000:
                    if (getActivity() != null && !getActivity().isFinishing()) {

                        handler.removeMessages(1001);// 正常关闭就移除延时口令
                        if (syncStatusTv != null) syncStatusTv.setVisibility(View.VISIBLE);

                        //开始读取HRV
                        startReadDeviceService();

                        //页面数据更新
                        updatePageData();

                        startUploadDBService();

                        if (b31HomeSwipeRefreshLayout != null)
                            b31HomeSwipeRefreshLayout.finishRefresh();// 停止下拉刷新
                    }

                    break;
                case 1001:
//                    Log.d("bobo", "handleMessage: 请求超过默认秒数");
                    if (b31HomeSwipeRefreshLayout != null)
                        b31HomeSwipeRefreshLayout.finishRefresh();// 停止下拉刷新
                    break;
                case 1002:  //显示步数的图表
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        List<HalfHourSportData> sportData = (List<HalfHourSportData>) msg.obj;
                        b30SportMaxNumTv.setText("0" + getResources().getString(R.string.steps));
                        showSportStepData(sportData);//展示步数的图表
                    }
                    break;
                case 1003:  //心率图表显示
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        if (lastTimeTv != null)
                            lastTimeTv.setText(getResources().getString(R.string.string_recent) + " " + "--:--");
                        if (b30HeartValueTv != null)
                            b30HeartValueTv.setText("0 bpm");
                        List<HalfHourRateData> rateData = (List<HalfHourRateData>) msg.obj;
                        showSportHeartData(rateData);//展示心率的图表
                    }

                    break;
                case 1004:  //睡眠数据
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        if (b30StartEndTimeTv != null)
                            b30StartEndTimeTv.setText("--:--");
                        SleepData sleepData = (SleepData) msg.obj;
                        showSleepData(sleepData);//展示睡眠的图表
                    }
                    break;
                case 1112:  //血氧
                    if (getActivity() != null && !getActivity().isFinishing()) {
//                       if (!iSNllSleep){
//                           List<Spo2hOriginData> tmpLt = (List<Spo2hOriginData>) msg.obj;
//                           updateSpo2View(tmpLt);
//                       }

                        List<Spo2hOriginData> tmpLt = (List<Spo2hOriginData>) msg.obj;
                        updateSpo2View(tmpLt);

                    }
                    break;
                case 1113: //HRV
                    if (getActivity() != null && !getActivity().isFinishing()) {
//                        if (!iSNllSleep){
//                            List<HRVOriginData> tmpHrvList = (List<HRVOriginData>) msg.obj;
//                            showHrvData(tmpHrvList);
//                        }
                        List<HRVOriginData> tmpHrvList = (List<HRVOriginData>) msg.obj;
                        showHrvData(tmpHrvList);
                    }
                    break;
                case 1444://血压
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        if (bloadLastTimeTv != null)
                            bloadLastTimeTv.setText(getResources().getString(R.string.string_recent) + " --:--");
                        //最近时间的血压高低值
                        if (b30BloadValueTv != null)
                            b30BloadValueTv.setText("--:--");
                        List<HalfHourBpData> bpData = (List<HalfHourBpData>) msg.obj;
                        showMineBloodData(bpData);//展示血压的图表
                    }

                    break;
                case 555:
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        if (syncStatusTv != null) syncStatusTv.setVisibility(View.GONE);
                        B31HomeActivity activity = (B31HomeActivity) getActivity();
                        if (activity != null) activity.startUploadDate();// 上传数据
                    }
                    break;

            }
        }
    };


    //开始上传本地缓存的数据
    private void startUploadDBService() {
        CommDBManager.getCommDBManager().startUploadDbService(MyApp.getContext());
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getmContext().registerReceiver(broadcastReceiver, addB31IntentFilter());
        if (connBleHelpService == null) {
            connBleHelpService = NewConnBleHelpService.getConnBleHelpService();
        }
        connBleHelpService.setConnBleMsgDataListener(this);
        mLocalTool = new LocalizeTool(getmContext());
        //目标步数
        goalStep = (int) SharedPreferencesUtils.getParam(getmContext(),
                "b30Goal", 0);
        String saveDate = (String) SharedPreferencesUtils.getParam(getmContext(),
                "saveDate", "");
        if (WatchUtils.isEmpty(saveDate)) {
            SharedPreferencesUtils.setParam(getmContext(),
                    "saveDate", System.currentTimeMillis() / 1000 + "");
        }

        //保存的时间
        String tmpSaveTime = (String) SharedPreferencesUtils.getParam(getmContext(),
                "save_curr_time", "");
        if (WatchUtils.isEmpty(tmpSaveTime))
            SharedPreferencesUtils.setParam(getmContext(),
                    "save_curr_time", System.currentTimeMillis() / 1000 + "");

        //setGuiderBemo();
    }


    @SuppressLint("WrongConstant")
    private void setGuiderBemo() {
        if (MyCommandManager.DEVICENAME != null && MyCommandManager.ADDRESS != null && getActivity() != null) {
            if (!WatchUtils.isEmpty(MyCommandManager.ADDRESS)) {
                try {
                    boolean bleServices = isServiceWork(getActivity(),
                            "hat.bemo.BlueTooth.blegatt.baseService.BLEScanService");
                    if (!bleServices) {
                        if (serviceConnection != null) {
                            // getActivity().bindService(new Intent(getActivity(), BLEScanService.class), serviceConnection, Service.BIND_AUTO_CREATE);
                        }
                    }
                    boolean myServices = isServiceWork(getActivity(),
                            "hat.bemo.MyService");
                    if (!myServices) {
                        if (serviceConnection != null) {
                            // getActivity().bindService(new Intent(getActivity(), MyService.class), serviceConnection, Service.BIND_AUTO_CREATE);
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
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        b31View = inflater.inflate(R.layout.fragment_b31_record_layout, container, false);
        initViewIds();
        initViews();

        initData();


        return b31View;
    }

    private void initViewIds() {
        ivTop = b31View.findViewById(R.id.iv_top);
        b31HomeHrvChart = b31View.findViewById(R.id.b31HomeHrvChart);
        homeSpo2LinChartView = b31View.findViewById(R.id.homeSpo2LinChartView);
        b31Spo2AveTv = b31View.findViewById(R.id.b31Spo2AveTv);
        hrvHeartSocreTv = b31View.findViewById(R.id.hrvHeartSocreTv);
        homeFastStatusTv = b31View.findViewById(R.id.homeFastStatusTv);
        b30connectStateTv = b31View.findViewById(R.id.b30connectStateTv);
        batteryTopImg = b31View.findViewById(R.id.batteryTopImg);
        batteryPowerTv = b31View.findViewById(R.id.batteryPowerTv);
        b30TopDateTv = b31View.findViewById(R.id.b30_top_dateTv);
        batteryWatchRecordShareImg = b31View.findViewById(R.id.battery_watchRecordShareImg);
        watchRecordTitleLin = b31View.findViewById(R.id.watch_record_titleLin);
        b31ProgressBar = b31View.findViewById(R.id.b31ProgressBar);
        b31GoalStepTv = b31View.findViewById(R.id.b31GoalStepTv);
        b31HomeTodayTv = b31View.findViewById(R.id.b31HomeTodayTv);
        b31HomeTodayImg = b31View.findViewById(R.id.b31HomeTodayImg);
        b31HomeYestTodayTv = b31View.findViewById(R.id.b31HomeYestTodayTv);
        b31HomeYestdayImg = b31View.findViewById(R.id.b31HomeYestdayImg);
        b31HomeBeYestdayTv = b31View.findViewById(R.id.b31HomeBeYestdayTv);
        b31HomeBeYestdayImg = b31View.findViewById(R.id.b31HomeBeYestdayImg);
        b30SportMaxNumTv = b31View.findViewById(R.id.b30SportMaxNumTv);
        b30ChartTopRel = b31View.findViewById(R.id.b30ChartTopRel);
        b30BarChart = b31View.findViewById(R.id.b30BarChart);
        b30StartEndTimeTv = b31View.findViewById(R.id.b30StartEndTimeTv);
        b30CusSleepView = b31View.findViewById(R.id.b30CusSleepView);
        lastTimeTv = b31View.findViewById(R.id.lastTimeTv);
        b30HeartValueTv = b31View.findViewById(R.id.b30HeartValueTv);
        b30HomeHeartChart = b31View.findViewById(R.id.b30HomeHeartChart);
        b31HomeSwipeRefreshLayout = b31View.findViewById(R.id.b31HomeSwipeRefreshLayout);
        syncStatusTv = b31View.findViewById(R.id.syncStatusTv);
        item_blood = b31View.findViewById(R.id.item_blood);
        b30CusBloadLin = b31View.findViewById(R.id.b30CusBloadLin);
        spo2ChartListLayout = b31View.findViewById(R.id.spo2ChartListLayout);
        homeBpManLin = b31View.findViewById(R.id.homeBpManLin);
        mChartViewSpo2h = b31View.findViewById(R.id.block_chartview_spo2h);
        mChartViewHeart = b31View.findViewById(R.id.block_chartview_heart);
        mChartViewSleep = b31View.findViewById(R.id.block_chartview_sleep);
        mChartViewBreath = b31View.findViewById(R.id.block_chartview_breath);
        mChartViewLowspo2h = b31View.findViewById(R.id.block_chartview_lowspo2h);
        bloadLastTimeTv = b31View.findViewById(R.id.bloadLastTimeTv);
        b30BloadValueTv = b31View.findViewById(R.id.b30BloadValueTv);
        b30HomeBloadChart = b31View.findViewById(R.id.b30HomeBloadChart);
        b31HomeTodayTv.setOnClickListener(this);
        b31HomeYestTodayTv.setOnClickListener(this);
        b31HomeBeYestdayTv.setOnClickListener(this);
        b31View.findViewById(R.id.b30SportChartLin1).setOnClickListener(this);
        b31View.findViewById(R.id.b30SleepLin).setOnClickListener(this);
        b31View.findViewById(R.id.b30CusHeartLin).setOnClickListener(this);
        b31View.findViewById(R.id.homeB31ManHeartImg).setOnClickListener(this);
        b31View.findViewById(R.id.homeB31ManBloadOxImg).setOnClickListener(this);
        b31View.findViewById(R.id.homeB31ManFatigueImg).setOnClickListener(this);
        b31View.findViewById(R.id.homeB31ManBloadImg).setOnClickListener(this);
        b31View.findViewById(R.id.homeB31ManRespRateImg).setOnClickListener(this);
        b31View.findViewById(R.id.battery_watchRecordShareImg).setOnClickListener(this);
        b31View.findViewById(R.id.b31BpOxyLin).setOnClickListener(this);
        b31View.findViewById(R.id.b31HrvView).setOnClickListener(this);
        b31View.findViewById(R.id.block_spo2h).setOnClickListener(this);
        b31View.findViewById(R.id.block_heart).setOnClickListener(this);
        b31View.findViewById(R.id.block_sleep).setOnClickListener(this);
        b31View.findViewById(R.id.block_breath).setOnClickListener(this);
        b31View.findViewById(R.id.block_lowspo2h).setOnClickListener(this);
        b31View.findViewById(R.id.b30CusBloadLin).setOnClickListener(this);
        b31View.findViewById(R.id.homeFastLin).setOnClickListener(this);
    }


    private void initData() {
        sleepList = new ArrayList<>();
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

        initSpo2hUtil();
        //updatePageData();
    }

    @SuppressLint("SetTextI18n")
    private void initViews() {
        spo2ChartListLayout.setVisibility(View.VISIBLE);

        initTipTv();
        b30TopDateTv.setText(WatchUtils.getCurrentDate());
        if (b31GoalStepTv != null)
            b31GoalStepTv.setText(getResources().getString(R.string.goal_step)
                    + goalStep + getResources().getString(R.string.steps));
//        ivTop.setImageResource(R.mipmap.ic_home_top_b31);
        showDeviceIcon();

        //暂时屏蔽分享
        batteryWatchRecordShareImg.setVisibility(View.GONE);


        //进度圆显示默认的步数
        if (getActivity() != null && !getActivity().isFinishing() && b31ProgressBar != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    b31ProgressBar.setMaxValue(goalStep);
                    b31ProgressBar.setValue(defaultSteps);
                }
            });
        }


        if (b31HomeSwipeRefreshLayout != null) {
            if (WatchUtils.isEmpty(WatchUtils.getSherpBleMac(getmContext()))) {
                b31HomeSwipeRefreshLayout.setEnableRefresh(false);
            }

            if (MyCommandManager.DEVICENAME == null)
                b31HomeSwipeRefreshLayout.setEnableRefresh(false);


            b31HomeSwipeRefreshLayout.setOnRefreshListener(refreshLayout -> getBleMsgData());
        }

        b30TopDateTv.setOnLongClickListener(v -> {
            startActivity(new Intent(getmContext(), InternalTestActivity.class));
            return true;
        });

    }


    //判断显示的设备图标
    private void showDeviceIcon() {
        if (ivTop != null) {
            if (!WatchUtils.isEmpty(MyCommandManager.DEVICENAME)) {
                if (MyCommandManager.DEVICENAME.equals(WatchUtils.B31_NAME)) {
                    ivTop.setImageResource(R.mipmap.ic_home_top_b31);
                } else if (MyCommandManager.DEVICENAME.equals(WatchUtils.S500_NAME)) {
                    ivTop.setImageResource(R.mipmap.ic_500s);
                } else {
                    ivTop.setImageResource(R.mipmap.ic_home_top_b31);//b31s
                }
            }
        }


        boolean isB31HasBp = (boolean) SharedPreferencesUtils.getParam(getmContext(),
                Commont.IS_B31_HAS_BP_KEY, false);
        Log.e(TAG, "----------isB31HasBp=" + isB31HasBp);
        b30CusBloadLin.setVisibility(isB31HasBp ? View.VISIBLE : View.GONE);
        homeBpManLin.setVisibility(isB31HasBp ? View.VISIBLE : View.GONE);


    }


    //血氧的几个图表
    private void initTipTv() {
        TextView tipSpo2h = (TextView) b31View.findViewById(R.id.block_bottom_tip_spo2h);
        TextView tipHeart = (TextView) b31View.findViewById(R.id.block_bottom_tip_heart);
        TextView tipSleep = (TextView) b31View.findViewById(R.id.block_bottom_tip_sleep);
        TextView tipBeath = (TextView) b31View.findViewById(R.id.block_bottom_tip_beath);
        TextView tipLowsp = (TextView) b31View.findViewById(R.id.block_bottom_tip_lowspo2h);

        String stateNormal = getResources().getString(R.string.vpspo2h_state_normal);
        String stateLittle = getResources().getString(R.string.vpspo2h_state_little);
        String stateCalm = getResources().getString(R.string.vpspo2h_state_calm);
        String stateError = getResources().getString(R.string.vpspo2h_state_error);
        String stateMulSport = getResources().getString(R.string.vpspo2h_state_mulsport);
        String stateMulMulSport = getResources().getString(R.string.vpspo2h_state_mulmulsport);

        tipSpo2h.setText("[95-99]" + stateNormal);
        tipHeart.setText("[0-20]" + stateLittle + "\t\t[21-40]" + stateNormal + "\t\t[≥41]" + stateError);
        tipSleep.setText("[0-20]" + stateCalm + "\t\t[21-50]" + stateMulSport + "\t\t[51-80]" + stateMulMulSport);
        tipBeath.setText("[0-26]" + stateNormal + "\t\t[27-50]" + stateError);
        tipLowsp.setText("[0-20]" + stateNormal + "\t\t[21-300]" + stateError);
    }


    /**
     * 设置相关属性
     */
    private void initSpo2hUtil() {
        vpSpo2hUtil = new VpSpo2hUtil();
        vpSpo2hUtil.setLinearChart(mChartViewSpo2h, mChartViewHeart,
                mChartViewSleep, mChartViewBreath, mChartViewLowspo2h);
        vpSpo2hUtil.setMarkView(getmContext(), R.layout.vpspo2h_markerview);
        vpSpo2hUtil.setModelIs24(false);
    }


    public Context getmContext() {
        return mContext == null ? MyApp.getContext() : mContext;
    }


    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        if (isVisible) {  //判断是否读取数据
            String bleMac = WatchUtils.getSherpBleMac(getmContext());
            int curCode = (int) SharedPreferencesUtils.getParam(getmContext(),
                    "curr_code", 0);
            clearDataStyle(curCode);//设置每次回主界面，返回数据不清空的
            if (connBleHelpService != null && !WatchUtils.isEmpty(MyCommandManager.DEVICENAME)) {
                long currentTime = System.currentTimeMillis() / 1000;
                //保存的时间
                String tmpSaveTime = (String) SharedPreferencesUtils.getParam(getmContext(),
                        "saveDate", currentTime + "");
                long diffTime = 0;
                if (tmpSaveTime != null) {
                    diffTime = (currentTime - Long.parseLong(tmpSaveTime)) / 60;
                }
                if (WatchConstants.isScanConn) {  //是搜索进来的
                    WatchConstants.isScanConn = false;
                    //getBleMsgData();
                    if (b31HomeSwipeRefreshLayout != null) b31HomeSwipeRefreshLayout.autoRefresh();
                } else {  //不是搜索进来的
                    if (diffTime > 30) {// 大于十分钟没更新再取数据
                        getBleMsgData();
                        if (b31HomeSwipeRefreshLayout != null)
                            b31HomeSwipeRefreshLayout.autoRefresh();
                    }
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!WatchUtils.isEmpty(MyCommandManager.DEVICENAME) &&
                !WatchUtils.isEmpty(MyCommandManager.ADDRESS)) {    //已连接
            if (b30connectStateTv != null)
                b30connectStateTv.setText(getResources().getString(R.string.connted));
            homeFastStatusTv.setText(getResources().getString(R.string.more_opera));
            int param = (int) SharedPreferencesUtils.getParam(getmContext(),
                    Commont.BATTERNUMBER, 0);
            if (param > 0) {
                showBatterStute(param);
            }
        } else {  //未连接
            if (getActivity() != null && !getActivity().isFinishing()) {
                if (b30connectStateTv != null)
                    b30connectStateTv.setText(getResources().getString(R.string.disconnted));
                homeFastStatusTv.setText(getResources().getString(R.string.disconnted));
                B31HomeActivity activity = (B31HomeActivity) getActivity();
                if (activity != null) activity.reconnectDevice();// 重新连接
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null)
            mContext.unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.battery_watchRecordShareImg:  //分享
                if (getActivity() == null || getActivity().isFinishing())
                    return;
                break;
            case R.id.b31HomeTodayTv:   //当天的数据
                clearDataStyle(0);
                break;
            case R.id.b31HomeYestTodayTv:   //昨天的数据
                clearDataStyle(1);
                break;
            case R.id.b31HomeBeYestdayTv:   //前天的数据
                clearDataStyle(2);
                break;
            case R.id.b30SportChartLin1:    //运动图表的点击
                B30StepDetailActivity.startAndParams(getActivity(),
                        WatchUtils.obtainFormatDate(currDay));
                break;
            case R.id.b30SleepLin:  //睡眠图表的点击
                B30SleepDetailActivity.startAndParams(getActivity(),
                        WatchUtils.obtainFormatDate(currDay));
                break;
            case R.id.b30CusBloadLin://血压图表的点击
                B30BloadDetailActivity.startAndParams(getActivity(),
                        WatchUtils.obtainFormatDate(currDay));
                break;
            case R.id.b30CusHeartLin:   //心率图表的点击
                B30HeartDetailActivity.startAndParams(getActivity(),
                        WatchUtils.obtainFormatDate(currDay));
                break;
            case R.id.homeB31ManHeartImg:   //手动心率测量
                startActivity(new Intent(getmContext(), ManualMeaureHeartActivity.class));
                break;
            case R.id.homeB31ManBloadOxImg:     //手动血氧测量
                startActivity(new Intent(getmContext(), B31ManSpO2Activity.class));
                break;
            case R.id.homeB31ManFatigueImg:     //疲劳度测量
                startActivity(new Intent(getmContext(), B31ManFatigueActivity.class));
                break;
            case R.id.homeB31ManRespRateImg:    //呼吸率测量
                startActivity(new Intent(getmContext(), B31RespiratoryRateActivity.class));
                break;
            case R.id.homeB31ManBloadImg://血压手动测量
                startActivity(new Intent(getActivity(), ManualMeaureBloadActivity.class));
                break;
            case R.id.b31BpOxyLin:  //血氧分析
                B31BpOxyAnysisActivity.startAndParams(getmContext(),
                        WatchUtils.obtainFormatDate(currDay));
                break;
            case R.id.b31HrvView:    //HRV
                B31HrvDetailActivity.startAndParams(getmContext(),
                        WatchUtils.obtainFormatDate(currDay));
                break;
            case R.id.block_spo2h:  //血氧
                startToSpo2Detail("0", getResources().getString(R.string.vpspo2h_spo2h));
                break;
            case R.id.block_heart:  //心脏负荷
                startToSpo2Detail("1", getResources().getString(R.string.vpspo2h_toptitle_heart));
                break;
            case R.id.block_sleep:      //睡眠活动
                startToSpo2Detail("2", getResources().getString(R.string.vpspo2h_toptitle_sleep));
                break;
            case R.id.block_breath:     //呼吸率
                startToSpo2Detail("3", getResources().getString(R.string.vpspo2h_toptitle_breath));
                break;
            case R.id.block_lowspo2h:   //低氧时间
                startToSpo2Detail("4", getResources().getString(R.string.vpspo2h_toptitle_lowspo2h));
                break;
            case R.id.homeFastLin://设置页面
                if (MyCommandManager.DEVICENAME != null) {
                    startActivity(new Intent(getmContext(), B31DeviceActivity.class));
                } else {
                    MyApp.getInstance().getB30ConnStateService().stopAutoConn();

                    startActivity(new Intent(getActivity(), NewSearchActivity.class));
                    if (getActivity() != null)
                        getActivity().finish();
                }
                break;
        }
    }


    //跳转至详情页面
    private void startToSpo2Detail(String tag, String titleTxt) {
        Intent intent = new Intent(getmContext(), ShowSpo2DetailActivity.class);
        intent.putExtra("spo2_tag", tag);
        intent.putExtra("title", titleTxt);
        intent.putExtra(Constant.DETAIL_DATE, WatchUtils.obtainFormatDate(currDay));
        getmContext().startActivity(intent);

    }


    //读取手环的数据
    private void getBleMsgData() {
        if (WatchUtils.isEmpty(MyCommandManager.DEVICENAME))
            return;
        clearDataStyle(0);//设置每次回主界面
        SharedPreferencesUtils.setParam(getmContext(), "saveDate",
                System.currentTimeMillis() / 1000 + "");
        connBleHelpService.getDeviceMsgData();
        /**
         * 连接的成功时只读取昨天一天的数据，刷新时再读取前3天的数据
         */
        String date = mLocalTool.getUpdateDate();// 最后更新总数据的日期
        Log.e(TAG, "-----最后更新总数据的日期--date=" + date);
        if (WatchUtils.isEmpty(date))
            date = WatchUtils.obtainFormatDate(1);  //如果是空的话表示第一次读取
        long delayMillis = 60 * 1000;// 默认超时时间
        if (date.equals(WatchUtils.obtainFormatDate(0))) {
            connBleHelpService.readAllHealthData(true);// 刷新当天天数据 -----刷新3天数据
        } else {
            connBleHelpService.readAllHealthData(false);// 刷新3天数据 ------刷新昨天天数据
        }
        handler.sendEmptyMessageDelayed(1001, delayMillis);

    }

    /**
     * 获取HRV和血氧的异步
     */
    private ReadHRVSoDataForDevices upDataToGDServices;

    private void startReadDeviceService() {
        Log.e(TAG, "------startReadDeviceService------");
        //通过异步读取HRV和血氧
        try {
//            upDataToGDServices = new ReadHRVSoDataForDevices();
//            upDataToGDServices.execute();

            if (upDataToGDServices != null && upDataToGDServices.isGETDATAS()) {
                return;
            }
            if (upDataToGDServices != null && upDataToGDServices.getStatus() != null &&
                    upDataToGDServices.getStatus() == AsyncTask.Status.RUNNING) {
                upDataToGDServices.cancel(true); // 如果Task还在运行，则先取消它
//                Log.e("-------AAA--", "先取消异步，在去重新开始");
            } else {
                upDataToGDServices = new ReadHRVSoDataForDevices();
            }
            upDataToGDServices.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 更新页面数据
     */
    private void updatePageData() {

        String mac = WatchUtils.getSherpBleMac(getmContext());
        String date = WatchUtils.obtainFormatDate(currDay);
        if (WatchUtils.isEmpty(mac))
            return;
        Log.e(TAG, "-------mac=" + mac + "--date=" + date);
        updateStepData(mac, date);  //更新步数
        updateSportData(mac, date); //更新运动图表数据
        updateRateData(mac, date);  //更新心率数据

        updateSleepData(mac, WatchUtils.obtainFormatDate(currDay)); //睡眠数据
        //血压
        updateBpData(mac, date);//更新血压数据
        //HRV
        updateHRVData(mac, date);
        //血氧
        updateSpo2Data(mac, date);
    }


    //三天数据切换
    private void clearDataStyle(final int code) {
        long currentTime = System.currentTimeMillis() / 1000;   //当前时间
        //保存的时间
        String tmpSaveTime = (String) SharedPreferencesUtils.getParam(getmContext(),
                "save_curr_time", "");
        long diffTime = (currentTime - Long.parseLong(tmpSaveTime));
        if (diffTime < 2)
            return;
        SharedPreferencesUtils.setParam(getmContext(), "save_curr_time",
                System.currentTimeMillis() / 1000 + "");
        //if (code == currDay) return;// 防重复点击
        if (b31HomeTodayImg != null) b31HomeTodayImg.setVisibility(View.INVISIBLE);
        if (b31HomeYestdayImg != null) b31HomeYestdayImg.setVisibility(View.INVISIBLE);
        if (b31HomeBeYestdayImg != null) b31HomeBeYestdayImg.setVisibility(View.INVISIBLE);
        switch (code) {
            case 0: //今天
                if (b31HomeTodayImg != null) b31HomeTodayImg.setVisibility(View.VISIBLE);
                break;
            case 1: //昨天
                if (b31HomeYestdayImg != null) b31HomeYestdayImg.setVisibility(View.VISIBLE);
                break;
            case 2: //前天
                if (b31HomeBeYestdayImg != null) b31HomeBeYestdayImg.setVisibility(View.VISIBLE);
                break;
        }
        currDay = code;
        SharedPreferencesUtils.setParam(getmContext(), "curr_code", code);
        updatePageData();

    }


    private IBleWriteResponse iBleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int i) {

        }
    };

    //电量返回
    @Override
    public void getBleBatteryData(int batteryLevel) {
        SharedPreferencesUtils.setParam(getmContext(), Commont.BATTERNUMBER, batteryLevel);//保存下电量
        showBatterStute(batteryLevel);
    }

    //当天步数
    @Override
    public void getBleSportData(final int step) {
        B30HalfHourDB db = new B30HalfHourDB();
        db.setAddress(MyApp.getInstance().getMacAddress());
        db.setDate(WatchUtils.obtainFormatDate(currDay));
        db.setType(B30HalfHourDao.TYPE_STEP);
        db.setOriginData("" + step);
        B30HalfHourDao.getInstance().saveOriginData(db);
        defaultSteps = step;
        if (getActivity() != null && !getActivity().isFinishing() && b31ProgressBar != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    b31ProgressBar.setMaxValue(goalStep);
                    b31ProgressBar.setValue(step);
                }
            });

        }
    }

    @Override
    public void onOriginData() {
        handler.sendEmptyMessage(1000);// 步数和健康数据都取到了,就关闭刷新条
    }


    /**
     * 显示电量
     *
     * @param batteryLevel
     */
    private void showBatterStute(int batteryLevel) {
        if (getActivity() == null || getActivity().isFinishing())
            return;
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

    /**
     * 取出本地步数数据,并显示
     */
    private void updateStepData(String mac, String date) {
        String step = B30HalfHourDao.getInstance().findOriginData(mac, date, B30HalfHourDao
                .TYPE_STEP);
        if (WatchUtils.isEmpty(step))
            step = 0 + "";
        int stepLocal = 0;
        try {
            stepLocal = Integer.parseInt(step);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        defaultSteps = stepLocal;
        if (getActivity() != null && !getActivity().isFinishing() && b31ProgressBar != null) {
            final int finalStepLocal = stepLocal;
            getActivity().runOnUiThread(() -> {
                b31ProgressBar.setMaxValue(goalStep);
                b31ProgressBar.setValue(finalStepLocal);
            });

        }

    }


    /**
     * 取出本地运动数据
     */
    private void updateSportData(String mac, String date) {
        String sport = B30HalfHourDao.getInstance().findOriginData(mac, date, B30HalfHourDao
                .TYPE_SPORT);
        List<HalfHourSportData> sportData = gson.fromJson(sport,
                new TypeToken<List<HalfHourSportData>>() {
                }.getType());

        Message message = handler.obtainMessage();
        message.what = 1002;
        message.obj = sportData;
        handler.sendMessage(message);//发送消息，展示步数图标

    }

    /**
     * 取出本地心率数据
     */
    private void updateRateData(String mac, String date) {
        String rate = B30HalfHourDao.getInstance().findOriginData(mac, date, B30HalfHourDao
                .TYPE_RATE);
        List<HalfHourRateData> rateData = gson.fromJson(rate,
                new TypeToken<List<HalfHourRateData>>() {
                }.getType());
        Message message = handler.obtainMessage();
        message.what = 1003;
        message.obj = rateData;
        handler.sendMessage(message);//发送消息，展示心率的图表
//        showSportHeartData(rateData);//展示心率的图表
    }

    /**
     * 取出本地血压数据
     */
    private void updateBpData(String mac, String date) {
//        Log.e("-------血压 数据时间----", date);
        String bp = B30HalfHourDao.getInstance().findOriginData(mac, date, B30HalfHourDao.TYPE_BP);

//        Log.e(TAG, "----------血压数据库中数据=" + bp);

        List<HalfHourBpData> bpData = gson.fromJson(bp, new TypeToken<List<HalfHourBpData>>() {
        }.getType());

        Message message = new Message();
        message.what = 1444;
        message.obj = bpData;
        handler.sendMessage(message);//发送消息，展示血压的图表
//        showBloodData(bpData);//展示血压的图表
    }

    /**
     * 取出本地睡眠数据
     */
    private void updateSleepData(String mac, String date) {
//        Log.d("-------数据时间----", date);
        String sleep = B30HalfHourDao.getInstance().findOriginData(mac, date, B30HalfHourDao
                .TYPE_SLEEP);
//        Log.d("-------睡眠数据----", date);
        SleepData sleepData = gson.fromJson(sleep, SleepData.class);
        Message message = handler.obtainMessage();
        message.what = 1004;
        message.obj = sleepData;
        handler.sendMessage(message);//发送消息，展示睡眠的图表
    }

    List<HRVOriginData> tmpHRVlist = new ArrayList<>();

    //取出本地的HRV数据
    private void updateHRVData(final String mac, final String day) {
        //如果HRV还在读取中就不要显示一半
        if (ReadHRVSoDataForDevices.isUpdataHRV()) {
//            Log.e("-------AAA--", "如果HRV还在读取中就不要显示一半 "
//            + ReadHRVSoDataForDevices.isUpdataHRV());
            return;
        }
        tmpHRVlist.clear();
        try {
            Thread thread = new Thread(() -> {//bleMac = ? and
                try {
                    String where = "bleMac = ? and dateStr = ?";
                    List<B31HRVBean> reList = LitePal.where(where, mac, day).find(B31HRVBean.class);
                    if (reList == null || reList.isEmpty()) {
                        Message message = handler.obtainMessage();
                        message.what = 1113;
                        message.obj = tmpHRVlist;
                        handler.sendMessage(message);
                        return;
                    }
                    for (B31HRVBean hrvBean : reList) {
//                        Log.e(TAG,"----------hrvBean="+hrvBean.toString());
                        tmpHRVlist.add(gson.fromJson(hrvBean.getHrvDataStr(), HRVOriginData.class));
                    }

                    Message message = handler.obtainMessage();
                    message.what = 1113;
                    message.obj = tmpHRVlist;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    //更新血氧的数据
    private void updateSpo2Data(final String mac, final String date) {
        //如果血氧还在读取中就不要显示一半
        if (ReadHRVSoDataForDevices.isUpdataO2O()) {
//            Log.e("-------AAA--", "如果血氧还在读取中就不要显示一半 "
//            + ReadHRVSoDataForDevices.isUpdataO2O());
            return;
        }
//        Log.e(TAG, "--------血氧=" + date);
        spo2hOriginDataList.clear();

        new Thread(() -> {
            String where = "bleMac = ? and dateStr = ?";
            List<B31Spo2hBean> spo2hBeanList = LitePal.where(where, mac, date)
                    .find(B31Spo2hBean.class);
            if (spo2hBeanList == null || spo2hBeanList.isEmpty()) {
                Message message = handler.obtainMessage();
                message.what = 1112;
                message.obj = spo2hOriginDataList;
                handler.sendMessage(message);
                return;
            }
//                Log.e(TAG, "---血氧------查询数据=" + currDay + spo2hBeanList.size());
            for (B31Spo2hBean hBean : spo2hBeanList) {
//                    if (hBean != null) {
//                        if (Commont.isDebug) LogTestUtil.e(TAG, "-------SPO2 " + hBean.toString());
//                    }
//                    Log.e(TAG,"---------走到这里来了="+hBean.toString());
                spo2hOriginDataList.add(gson.fromJson(hBean.getSpo2hOriginData(),
                        Spo2hOriginData.class));
            }

            Message message = handler.obtainMessage();
            message.what = 1112;
            message.obj = spo2hOriginDataList;
            handler.sendMessage(message);

        }).start();


    }


    //显示血氧的图
    @SuppressLint("SetTextI18n")
    private void updateSpo2View(List<Spo2hOriginData> dataList) {

//        Log.e(TAG, "----------血氧展示=" + dataList.size());
        try {
            List<Spo2hOriginData> data0To8 = getSpo2MoringData(dataList);
            Spo2hOriginUtil spo2hOriginUtil = new Spo2hOriginUtil(data0To8);
            //获取处理完的血氧数据
            final List<Map<String, Float>> tenMinuteDataBreathBreak = spo2hOriginUtil.
                    getTenMinuteData(TYPE_BEATH_BREAK);
            final List<Map<String, Float>> tenMinuteDataSpo2h = spo2hOriginUtil.
                    getTenMinuteData(TYPE_SPO2H);
            //平均值
            int onedayDataArr[] = spo2hOriginUtil.getOnedayDataArr(ESpo2hDataType.TYPE_SPO2H);
            b31Spo2AveTv.setText(getResources().getString(R.string.ave_value)
                    + "\n" + onedayDataArr[2]);

            initSpo2hUtil();
            vpSpo2hUtil.setData(dataList);
            vpSpo2hUtil.showAllChartView();

            if (getActivity() == null)
                return;
            ChartViewUtil spo2ChartViewUtilHomes = new ChartViewUtil(homeSpo2LinChartView,
                    null, true,
                    CHART_MAX_SPO2H, CHART_MIN_SPO2H, "No Data", TYPE_SPO2H);
            spo2ChartViewUtilHomes.setxColor(R.color.head_text);
            spo2ChartViewUtilHomes.setNoDataColor(R.color.head_text);
            //更新血氧数据的图表
            spo2ChartViewUtilHomes.setBeathBreakData(tenMinuteDataBreathBreak);
            spo2ChartViewUtilHomes.updateChartView(tenMinuteDataSpo2h);
            spo2ChartViewUtilHomes.setBeathBreakData(tenMinuteDataBreathBreak);

            homeSpo2LinChartView.getAxisLeft().removeAllLimitLines();
            homeSpo2LinChartView.getAxisLeft().setDrawLabels(false);

            LineData data = homeSpo2LinChartView.getData();
            if (data == null)
                return;
            LineDataSet dataSetByIndex = (LineDataSet) data.getDataSetByIndex(0);
            if (dataSetByIndex != null) {
                dataSetByIndex.setDrawFilled(false);
                dataSetByIndex.setColor(Color.parseColor("#17AAE2"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (!StringUtil.isEmpty(e.getMessage()))
                Log.e(TAG, "查询报错----" + e.getMessage());
        }
    }


    /**
     * 展示睡眠图表
     */
    private void showSleepData(SleepData sleepData) {
        if (getActivity() == null || getActivity().isFinishing()) return;
        sleepList.clear();
        if (sleepData != null) {
            iSNllSleep = false;
            if (b30StartEndTimeTv != null)
                b30StartEndTimeTv.setText(sleepData.getSleepDown().getColck() +
                        "-" + sleepData.getSleepUp().getColck());
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
            iSNllSleep = true;
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
            if (b30HomeHeartChart != null) b30HomeHeartChart.setRateDataList(heartList);
        } else {
            if (b30HomeHeartChart != null) b30HomeHeartChart.setRateDataList(heartList);
        }
    }


    /**
     * 展示步数的图表，计算数据
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

    /**
     * 展示血压图表
     */
    @SuppressLint("SetTextI18n")
    private void showMineBloodData(List<HalfHourBpData> bpData) {
//        if (WatchUtils.isEmpty(MyCommandManager.DEVICENAME) && MyCommandManager.DEVICENAME.equals("B31")){}
        if (getActivity() == null || getActivity().isFinishing()) return;

        b30BloadList.clear();
        bloadListMap.clear();
        resultBpMapList.clear();
        if (bpData != null && !bpData.isEmpty()) {
            //获取日期
            for (HalfHourBpData halfHourBpData : bpData) {
                Map<String, Map<Integer, Integer>> mapMap = new HashMap<>();
                if (halfHourBpData == null) return;
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
        b30HomeBloadChart.setScale(false);
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
            //Log.e(TAG, "  =====  统计血压  " + item.getLowValue() + "---" + item.getHighValue());
            Point point = new Point(item.getLowValue(), item.getHighValue());
            dataMap.put(time, point);
        }
        return dataMap;
    }


    //监听连接状态的广播
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @SuppressLint("WrongConstant")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
//            Log.e(TAG, "-----------action-=" + action);
            if (WatchUtils.isEmpty(action))
                return;
            switch (action) {
                case WatchUtils.B31_CONNECTED_ACTION: {//连接
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        String textConn = getResources().getString(R.string.connted);
                        if (b30connectStateTv != null) b30connectStateTv.setText(textConn);
                        homeFastStatusTv.setText(getResources().getString(R.string.more_opera));
                        if (connBleHelpService != null && !WatchUtils.
                                isEmpty(MyCommandManager.DEVICENAME)) {
                            if (b31HomeSwipeRefreshLayout != null) {
                                b31HomeSwipeRefreshLayout.setEnableRefresh(true);
                                b31HomeSwipeRefreshLayout.autoRefresh();
//                            showDeviceIcon();
//                            if (!WatchUtils.isEmpty(MyCommandManager.DEVICENAME)
//                            && MyCommandManager.DEVICENAME.equals("B31")) {
//                                if (item_blood != null) item_blood.setVisibility(View.GONE);
//                            } else {
//                                if (item_blood != null) item_blood.setVisibility(View.VISIBLE);
//                            }
                            }
                            /**
                             * 盖得 bemo 加载
                             */
                            //setGuiderBemo();
                        }
                    }
                    break;
                }
                case WatchUtils.B30_DISCONNECTED_ACTION: {//断开
                    MyCommandManager.ADDRESS = null;// 断开连接了就设置为null
                    homeFastStatusTv.setText(getResources().getString(R.string.disconnted));
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        String textDis = getResources().getString(R.string.disconnted);

                        if (b31HomeSwipeRefreshLayout != null)
                            b31HomeSwipeRefreshLayout.setEnableRefresh(false);
                        if (b30connectStateTv != null) b30connectStateTv.setText(textDis);
                    }
                    //SharedPreferences_status.save_IMEI(MyApplication.context, "");
                    break;
                }
                case WatchUtils.B31_HRV_COMPLETE: {//Hrv的数据更新完了
                    String mac = WatchUtils.getSherpBleMac(getmContext());
                    String date = WatchUtils.obtainFormatDate(currDay);
//                Log.e("-------AAA--", "Hrv的数据更新完了 ");
                    updateHRVData(mac, date);
                    break;
                }
                case WatchUtils.B31_SPO2_COMPLETE: {//血氧的数据更新完了
                    String mac = WatchUtils.getSherpBleMac(getmContext());
                    String date = WatchUtils.obtainFormatDate(currDay);
                    handler.sendEmptyMessage(555);
//                Log.e("-------AAA--", "血氧的数据更新完了 ");
                    updateSpo2Data(mac, date);
                    break;
                }
            }
        }

    };


    //显示HRV的数据
    private void showHrvData(List<HRVOriginData> dataList) {
        try {
//            Log.e(TAG, "----显示HRV=" + dataList.size());
            if (dataList.size() == 0 || dataList.size() > 420)
                return;
            List<HRVOriginData> data0to8 = getMoringData(dataList);
            HRVOriginUtil mHrvOriginUtil = new HRVOriginUtil(data0to8);
            HrvScoreUtil hrvScoreUtil = new HrvScoreUtil();
            int heartSocre = hrvScoreUtil.getSocre(dataList);
            hrvHeartSocreTv.setText(getResources().getString(R.string.heart_health_sorce) + " " + heartSocre);
            final List<Map<String, Float>> tenMinuteData = mHrvOriginUtil.getTenMinuteData();
            if (getActivity() == null)
                return;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //主界面
                    showHomeView(tenMinuteData);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //显示HRV的数据
    private void showHomeView(List<Map<String, Float>> tenMinuteData) {
        try {
            ChartViewUtil chartViewUtilHome = new ChartViewUtil(b31HomeHrvChart, null, true,
                    CHART_MAX_HRV, CHART_MIN_HRV, "No Data", TYPE_HRV);
            b31HomeHrvChart.getAxisLeft().removeAllLimitLines();
            b31HomeHrvChart.getAxisLeft().setDrawLabels(false);
            chartViewUtilHome.setxColor(R.color.head_text);
            chartViewUtilHome.setNoDataColor(R.color.head_text);
            chartViewUtilHome.drawYLable(false, 1);
            chartViewUtilHome.updateChartView(tenMinuteData);
            LineData data = b31HomeHrvChart.getData();
            if (data == null)
                return;
            LineDataSet dataSetByIndex = (LineDataSet) data.getDataSetByIndex(0);
            if (dataSetByIndex != null) {
                dataSetByIndex.setDrawFilled(false);
                dataSetByIndex.setColor(Color.parseColor("#EC1A3B"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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
        try {
            if (originSpo2hList == null || originSpo2hList.isEmpty())
                return moringData;
            for (HRVOriginData hRVOriginData : originSpo2hList) {
                if (hRVOriginData.getmTime().getHMValue() < 8 * 60) {
                    moringData.add(hRVOriginData);
                }
            }
            return moringData;
        } catch (Exception e) {
            e.printStackTrace();
            moringData.clear();
            return moringData;
        }
    }

    /**
     * 获取0点-8点之间的数据
     *
     * @param originSpo2hList
     * @return
     */
    @NonNull
    private List<Spo2hOriginData> getSpo2MoringData(List<Spo2hOriginData> originSpo2hList) {
        List<Spo2hOriginData> spo2Data = new ArrayList<>();
        try {
            if (originSpo2hList == null || originSpo2hList.isEmpty())
                return spo2Data;
            for (Spo2hOriginData spo2hOriginData : originSpo2hList) {
                if (spo2hOriginData.getmTime().getHMValue() < 8 * 60) {
                    spo2Data.add(spo2hOriginData);
                }
            }
            return spo2Data;
        } catch (Exception e) {
            e.printStackTrace();
            return spo2Data;
        }

    }


    private IntentFilter addB31IntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WatchUtils.B31_CONNECTED_ACTION);
        intentFilter.addAction(WatchUtils.B30_DISCONNECTED_ACTION);
        intentFilter.addAction(WatchUtils.B31_HRV_COMPLETE);
        intentFilter.addAction(WatchUtils.B31_SPO2_COMPLETE);
        return intentFilter;
    }
}
