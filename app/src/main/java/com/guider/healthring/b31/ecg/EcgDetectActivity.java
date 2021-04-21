package com.guider.healthring.b31.ecg;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.guider.health.apilib.BuildConfig;
import com.guider.healthring.Commont;
import com.guider.healthring.MyApp;
import com.guider.healthring.R;
import com.guider.healthring.b30.bean.B30HalfHourDB;
import com.guider.healthring.b30.bean.B30HalfHourDao;
import com.guider.healthring.b31.ecg.bean.EcgDetectStateBean;
import com.guider.healthring.b31.ecg.bean.EcgSourceBean;
import com.guider.healthring.bleutil.MyCommandManager;
import com.guider.healthring.siswatch.WatchBaseActivity;
import com.guider.healthring.siswatch.utils.DateTimeUtils;
import com.guider.healthring.siswatch.utils.WatchUtils;
import com.guider.healthring.util.OkHttpTool;
import com.guider.healthring.util.SharedPreferencesUtils;
import com.guider.healthring.view.CusScheduleView;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IECGDetectListener;
import com.veepoo.protocol.listener.data.IECGReadDataListener;
import com.veepoo.protocol.listener.data.IECGReadIdListener;
import com.veepoo.protocol.model.datas.EcgDetectInfo;
import com.veepoo.protocol.model.datas.EcgDetectResult;
import com.veepoo.protocol.model.datas.EcgDetectState;
import com.veepoo.protocol.model.datas.TimeData;
import com.veepoo.protocol.model.enums.EEcgDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.guider.healthring.BuildConfig.APIHDURL;


/**
 * 手动测量心电页面
 * Created by Admin
 * Date 2021/1/19
 */
public class EcgDetectActivity extends WatchBaseActivity implements View.OnClickListener {

    private static final String TAG = "EcgDetectActivity";

    ImageView commentB30BackImg;
    TextView commentB30TitleTv;
    ImageView detectEcgImgView;
    CusScheduleView ecgDetectScheduleView;
    TextView showEcgDetectStatusTv;
    TextView detectEcgHeartTv;
    TextView detectEcgQtTv;
    TextView detectEcgHrvTv;

    //心电图
    private EcgHeartRealthView heartView;

    //是否正在测量
    private boolean isMeasure = false;

    private List<int[]> ecgList = new ArrayList<int[]>();

    private List<int[]> ecgSourceList = new ArrayList<>();

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x01) {
                EcgDetectState ecgDetectState = (EcgDetectState) msg.obj;
                if (ecgDetectState == null)
                    return;
                if(ecgDetectState.getWear() == 1){
                    showEcgDetectStatusTv.setText(getString(R.string.ecg_error1)); //"导联脱落,请正常佩戴!");
                    return;
                }

                int progress = ecgDetectState.getProgress();
                ecgDetectScheduleView.setCurrScheduleValue(progress);
                showEcgDetectStatusTv.setText(getString(R.string.ecg_measuring) + progress+"%");

                // 次/分
                detectEcgHeartTv.setText(ecgDetectState.getHr1() == 0 ? getString(R.string.ecg_cnt_m_):ecgDetectState.getHr1()+getString(R.string.ecg_cnt_m));
                detectEcgQtTv.setText(ecgDetectState.getQtc() == 0 ? getString(R.string.ecg_ms_):ecgDetectState.getQtc()+getString(R.string.ecg_ms));
                detectEcgHrvTv.setText((ecgDetectState.getHrv() == 0 || ecgDetectState.getHrv() == 255) ? getString(R.string.ecg_ms_) :ecgDetectState.getHrv()+getString(R.string.ecg_ms));


                if(progress == 100){    //测量完了
                    showEcgDetectStatusTv.setText(getString(R.string.ecg_tested));
                    detectEcgImgView.setImageResource(R.drawable.detect_sp_start);
                    isMeasure = false;
                    stopDetectEcg();

                    saveEcgToDb(ecgDetectState);

                    //进度100%后上传
                    uploadEcgData(ecgDetectState);


                }
            }
            if(msg.what == 0x02){   //测量返回，绘制图表
                int[] ecgArray = (int[]) msg.obj;
                heartView.changeData(ecgArray,25);
                ecgSourceList.add(ecgArray);
                for (int i = 0; i < ecgArray.length; i++) {
                    if (ecgArray[i] == 0 && ecgList.isEmpty()) {
                        continue;
                    }
                    int [] tmp = {ecgArray[i]};
                    ecgList.add(tmp);
                }
            }
            if(msg.what == 0x03){   //结果

            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecg_detect_layout);
        initViews();
    }

    private void initViews() {
        findViews();

        commentB30TitleTv.setText(getString(R.string.ecg_test));
        commentB30BackImg.setVisibility(View.VISIBLE);

        ecgDetectScheduleView.setAllScheduleValue(100f);

        heartView = findViewById(R.id.detectEcgView);
        heartView.setCoumlnQutoCount(16 * 5);

        showEmptyData();
    }

    private void findViews() {
        commentB30BackImg = findViewById(R.id.commentB30BackImg);
        commentB30TitleTv = findViewById(R.id.commentB30TitleTv);
        detectEcgImgView = findViewById(R.id.detectEcgImgView);
        ecgDetectScheduleView = findViewById(R.id.ecgDetectScheduleView);
        showEcgDetectStatusTv = findViewById(R.id.showEcgDetectStatusTv);
        detectEcgHeartTv = findViewById(R.id.detectEcgHeartTv);
        detectEcgQtTv = findViewById(R.id.detectEcgQtTv);
        detectEcgHrvTv = findViewById(R.id.detectEcgHrvTv);

        commentB30BackImg.setOnClickListener(this);
        detectEcgImgView.setOnClickListener(this);

        commentB30TitleTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                readAllEcgData();
                return true;
            }
        });

        detectEcgImgView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                startActivity(ShowEcgDataActivity.class);
                return true;
            }
        });
    }


    private void showEmptyData(){
        detectEcgHeartTv.setText(getString(R.string.ecg_cnt_m_));
        detectEcgQtTv.setText(getString(R.string.ecg_ms_));
        detectEcgHrvTv.setText(getString(R.string.ecg_ms_));
    }

    //读取所有心电数据
    private void readAllEcgData() {
        if (MyCommandManager.DEVICENAME == null)
            return;
        int currYear = DateTimeUtils.getCurrYear();
        int currMonth = DateTimeUtils.getCurrMonth();
        int currDay = DateTimeUtils.getCurrDay();
        TimeData timeData = new TimeData(currYear, currMonth, currDay);
        MyApp.getInstance().getVpOperateManager().readECGId(iBleWriteResponse, timeData, EEcgDataType.MANUALLY, new IECGReadIdListener() {
            @Override
            public void readIdFinish(int[] ints) {
                Log.e(TAG, "---readECGId=" + Arrays.toString(ints));
            }
        });
        MyApp.getInstance().getVpOperateManager().readECGData(iBleWriteResponse, timeData, EEcgDataType.MANUALLY, new IECGReadDataListener() {
            @Override
            public void readDataFinish(List<EcgDetectResult> list) {
                Log.e(TAG, "---readDataFinish=" + new Gson().toJson(list));
            }
        });
    }


    //停止测量
    private void stopDetectEcg() {
        if (MyCommandManager.DEVICENAME == null)
            return;
        MyApp.getInstance().getVpOperateManager().stopDetectECG(iBleWriteResponse, true, new IECGDetectListener() {
            @Override
            public void onEcgDetectInfoChange(EcgDetectInfo ecgDetectInfo) {
                Log.e(TAG, "---stop---onEcgDetectInfoChange=" + ecgDetectInfo.toString());
            }

            @Override
            public void onEcgDetectStateChange(EcgDetectState ecgDetectState) {
                Log.e(TAG, "---stop----onEcgDetectStateChange=" + ecgDetectState.toString());
            }

            @Override
            public void onEcgDetectResultChange(EcgDetectResult ecgDetectResult) {
                Log.e(TAG, "----stop-----onEcgDetectResultChange=" + ecgDetectResult.toString());
            }

            @Override
            public void onEcgADCChange(int[] ints) {
                Log.e(TAG, "--stop--onEcgADCChange=" + Arrays.toString(ints));
            }
        });
    }

    //开始测量心电数据
    private void detectEcgData() {
        if (MyCommandManager.DEVICENAME == null)
            return;
        ecgList.clear();
        ecgSourceList.clear();
        MyApp.getInstance().getVpOperateManager().startDetectECG(iBleWriteResponse, true, new IECGDetectListener() {
            @Override
            public void onEcgDetectInfoChange(EcgDetectInfo ecgDetectInfo) {
                Log.e(TAG, "------onEcgDetectInfoChange=" + ecgDetectInfo.toString());
            }

            @Override
            public void onEcgDetectStateChange(EcgDetectState ecgDetectState) {
                Log.e(TAG, "-------onEcgDetectStateChange=" + ecgDetectState.toString());
                Message message = handler.obtainMessage();
                message.what = 0x01;
                message.obj = ecgDetectState;
                handler.sendMessage(message);
            }

            @Override
            public void onEcgDetectResultChange(EcgDetectResult ecgDetectResult) {
                Log.e(TAG, "---------onEcgDetectResultChange=" + ecgDetectResult.toString() + "\n" + ecgDetectResult.getAveHeart());
                Message message = handler.obtainMessage();
                message.what = 0x03;
                message.obj = ecgDetectResult;
                handler.sendMessage(message);
            }


            //心电测量ecg数据回调
            @Override
            public void onEcgADCChange(int[] ints) {
                Log.e(TAG, "----onEcgADCChange=" + Arrays.toString(ints));
                Message message = handler.obtainMessage();
                message.what = 0x02;
                message.obj = ints;
                handler.sendMessage(message);
            }
        });
    }



    private IBleWriteResponse iBleWriteResponse = i -> {

    };



    private void startAndStopDetect(){
        if (!isMeasure) {
            detectEcgImgView.setImageResource(R.drawable.detect_sp_stop);
            isMeasure = true;
            detectEcgData();
        } else {
            detectEcgImgView.setImageResource(R.drawable.detect_sp_start);
            isMeasure = false;
            stopDetectEcg();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.detectEcgImgView:
                startAndStopDetect();
                break;
            case R.id.commentB30BackImg:
                finish();
                break;
        }
    }


    /**
     *  userId 用户id 不能为空
     *  analysisList 结论 不能为空 主要结论，不清楚和result1~result8如何对应
     *  analysisResults 分析结果 不能为空 详细结论，不清楚和result1~result8如何对应
     *  avm 幅值单位 不能为空 需要确认
     *  baseLineValue 基准值 不能为空 需要确认
     *  breathRate 呼吸率 可为0 对应aveResRate
     *  curveDescription 导联名称描述 不能为空，多导联已半角逗号为间隔 单导联默认为 I
     *  customAnalysis  自定义结论 可为空
     *  customType  自定义类型 可为空
     *  deviceCode 设备编码 可为空 对应mac
     *  ecgData 心电图数据，二维int数组 不能为空。 波形数据，应该对应filterSignals
     *  gain 增益 不能为空 为1
     *  heartRate 心率 可为0 对应aveHeart
     *  leadNumber 导联数 不可为空 单导联为1
     *  mask 心电图单点纵坐标掩码（默认值：Ox0FFF） 不可为空 默认为0xFFFFFFFF
     *  paxis P轴 可为0
     *  prInterval PR间期 可为0
     *  qrsAxis QRS轴 可为0
     *  qrsDuration QRS间期 可为0
     *  qtc QTC间期 可为0
     *  qtd QT间期 可为0 对应aveQT
     *  rrInterval RR间期 可为0
     *  rv5 RV5幅值 可为0
     *  samplingFrequency 采样频率 不可为空 对应uploadFrequency
     *  sv1 SV1幅值 可为0
     *  taxis t轴 可为0
     *  testTime 测试时间 不可为空 对应 date+testTime两个字段
     *
     */

    private void uploadEcgData(EcgDetectState detectState) {
        if (ecgList.isEmpty())
            return;
        Log.e(TAG, "-------ecg大小=" + ecgList.size());
        // ecgList.clear();
        List<Map<String, Object>> listMap = new ArrayList<>();
        long guiderId = (long) SharedPreferencesUtils.getParam(MyApp.getContext(), "accountIdGD", 0L);
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("accountId", guiderId);
        paramsMap.put("analysisList", ""); //结论 不能为空 主要结论，不清楚和result1~result8如何对应
        paramsMap.put("analysisResults", ""); //分析结果 不能为空 详细结论，不清楚和result1~result8如何对应
        paramsMap.put("avm", 0); //幅值单位 不能为空 需要确认
        paramsMap.put("baseLineValue", 0); //基准值
        paramsMap.put("breathRate", detectState.getBr2()); //呼吸率
        paramsMap.put("curveDescription", "I"); //导联名称描述 不能为空，多导联已半角逗号为间隔 单导联默认为 I
        paramsMap.put("customAnalysis", "{}");  //自定义结论
        paramsMap.put("customType", detectState.getDataType());  //自定义类型
        paramsMap.put("deviceCode", MyApp.getInstance().getMacAddress()); //设备编码
        paramsMap.put("ecgData", ecgList);  //心电图数据
        paramsMap.put("gain", 1); //增益 不能为空 为1
        paramsMap.put("heartRate", detectState.getHr2());   //心率 可为0
        paramsMap.put("leadNumber", 1);  //导联数 不可为空
        paramsMap.put("mask", 0x0FFFF);  //心电图单点纵坐标掩码（默认值：Ox0FFF） 不可为空 默认为0xFFFFFFFF
        paramsMap.put("paxis", 0); //P轴 可为0
        paramsMap.put("prInterval", 0); //PR间期 可为0
        paramsMap.put("qrsAxis", 0);  //QRS轴 可为0
        paramsMap.put("qrsDuration", 0);  //QRS间期 可为0
        paramsMap.put("qtc", detectState.getQtc());  //QTC间期 可为0
        paramsMap.put("qtd", 0);  //QT间期 可为0 对应aveQT
        paramsMap.put("rrInterval", 0);   //RR间期 可为0
        paramsMap.put("rv5", 0);    //RV5幅值 可为0
        paramsMap.put("samplingFrequency", 138);  //采样频率 不可为空 对应uploadFrequency
        paramsMap.put("sv1", 0);   //SV1幅值 可为0
        paramsMap.put("taxis", 0);   //t轴 可为0
        paramsMap.put("testTime", WatchUtils.getISO8601Timestamp(new Date()));  //测试时间 不可为空 对应 date+testTime两个字段

        listMap.add(paramsMap);
        String params = new Gson().toJson(listMap);

//        Log.e(TAG, "-----params=" + params);

        String url = APIHDURL + "api/v1/ecg";
        Log.i(TAG, url);
        OkHttpTool.getInstance().doRequest(url, params, null, new OkHttpTool.HttpResult() {
            @Override
            public void onResult(String result) {
                Log.e(TAG, "-------上传心电结果=" + result);
                ecgList.clear();
                listMap.clear();
            }
        });
    }


    //保存测量的数据至数据库中
    private void saveEcgToDb(EcgDetectState ecgDetectState){

        if(ecgSourceList.isEmpty())
            return;
        //mac地址
        String bleMac = MyApp.getInstance().getMacAddress();
        if(bleMac == null)
            return;

        EcgDetectStateBean ecgDetectStateBean = new EcgDetectStateBean(ecgDetectState.getEcgType(),ecgDetectState.getCon(),ecgDetectState.getDataType(),
                ecgDetectState.getDeviceState(),ecgDetectState.getHr1(),ecgDetectState.getHr2(),ecgDetectState.getHrv(),ecgDetectState.getRr1(),ecgDetectState.getRr2(),
                ecgDetectState.getBr1(),ecgDetectState.getBr2(),ecgDetectState.getWear(),ecgDetectState.getMid(),ecgDetectState.getQtc(),ecgDetectState.getProgress());
        EcgSourceBean ecgSourceBean = new EcgSourceBean();
        ecgSourceBean.setBleMac(bleMac);
        ecgSourceBean.setDetectDate(WatchUtils.getCurrentDate());
        ecgSourceBean.setDetectTime(WatchUtils.getCurrentDateFormat("HH:mm"));
        ecgSourceBean.setEcgDetectStateBeanStr(new Gson().toJson(ecgDetectStateBean));
        ecgSourceBean.setEcgListStr(new Gson().toJson(ecgSourceList));

        Log.e(TAG,"-----ecgSourceBean="+new Gson().toJson(ecgSourceBean));
        ecgSourceBean.save();

    }
}
