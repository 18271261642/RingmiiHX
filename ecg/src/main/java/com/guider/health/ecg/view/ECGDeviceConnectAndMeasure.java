package com.guider.health.ecg.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.guider.health.common.core.HearRate;
import com.guider.health.common.device.DeviceInit;
import com.guider.health.common.utils.SkipClick;
import com.guider.health.ecg.R;
import com.guider.health.ecg.presenter.ECGServiceManager;

import java.util.ArrayList;

/**
 * Created by haix on 2019/5/30.
 */

public class ECGDeviceConnectAndMeasure extends ECGFragment {

    private ECGCompletedView time_measure;

    private View view;
    private LineChart lineChart;
    private LineDataSet chartSet = new LineDataSet(null, "");
    ArrayList<Entry> chartSet1Entries = new ArrayList<Entry>();
    private RotateAnimation myAlphaAnimation;
    private View loading_pic;
    private TextView loading_text;
    private TextView loading_tip;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.ecg_device_messure, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        view.findViewById(R.id.home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                _mActivity.setResult(112);

                _mActivity.finish();
                System.exit(0);
            }
        });
        ((TextView) view.findViewById(R.id.title)).setText("设备测量");
        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                _mActivity.setResult(113);

                _mActivity.finish();
                System.exit(0);
            }
        });

        view.findViewById(R.id.skip).setVisibility(View.VISIBLE);
        view.findViewById(R.id.skip).setOnClickListener(new SkipClick(this , DeviceInit.DEV_ECG_6 , true));


        lineChart = view.findViewById(R.id.wave);
        time_measure = view.findViewById(R.id.time_measure);
        initChart();


        ECGServiceManager.getInstance().startScanBlueToothDevice(false);
        //Toast.makeText(_mActivity, "正在连接蓝牙中....", Toast.LENGTH_SHORT).show();


    }


    @Override
    public void scanFailed() {

        ECGServiceManager.getInstance().stopDeviceConnect();
        _mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //view.findViewById(R.id.bp_cancel).setVisibility(View.GONE);
                final Button bt = view.findViewById(R.id.ecg_reconnect);
                bt.setVisibility(View.VISIBLE);
                final TextView tip = (TextView) view.findViewById(R.id.ecg_reminder);
                tip.setText("未找到设备\n请确认设备已经打开");
                bt.setText("已经解决 重新连接");
                bt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tip.setText("正在连接中\n 请稍后...");
                        ((LinearLayout) view.findViewById(R.id.wave_info)).setVisibility(View.INVISIBLE);
                        bt.setVisibility(View.INVISIBLE);
                        ECGServiceManager.getInstance().startScanBlueToothDevice(false);
                    }
                });
            }
        });

    }

    @Override
    public void connectFaile(int code) {

        ECGServiceManager.getInstance().stopDeviceConnect();
        _mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //view.findViewById(R.id.bp_cancel).setVisibility(View.GONE);
                final Button bt = view.findViewById(R.id.ecg_reconnect);
                bt.setVisibility(View.VISIBLE);
                final TextView tip = (TextView) view.findViewById(R.id.ecg_reminder);
                tip.setText("连接失败\n请确认设备已经打开");
                bt.setText("已经解决 重新连接");
                bt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //((TextView) view.findViewById(R.id.bp_reminder)).setText("正在测量中\n  请稍后...");
                        //view.findViewById(R.id.bp_cancel).setVisibility(View.VISIBLE);
                        tip.setText("正在连接中\n 请稍后...");
                        ((LinearLayout) view.findViewById(R.id.wave_info)).setVisibility(View.INVISIBLE);
                        bt.setVisibility(View.INVISIBLE);
                        ECGServiceManager.getInstance().startScanBlueToothDevice(false);
                    }
                });
            }
        });
    }

    @Override
    public void connectSuccess() {
        _mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((TextView) view.findViewById(R.id.ecg_reminder)).setVisibility(View.INVISIBLE);
                ((LinearLayout) view.findViewById(R.id.wave_info)).setVisibility(View.VISIBLE);
                ECGServiceManager.getInstance().startDeviceMessure();
            }
        });

    }


    @Override
    public void measureTime(final int time) {

        _mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                time_measure.setProgress(time);

            }
        });

    }

    @Override
    public void measureWare(final int ware) {

        _mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chartSet.setColor(Color.BLACK);

                chartSet.setDrawCircles(false);

                chartSet.setDrawFilled(false);

                chartSet.setFillAlpha(0);

                chartSet.setCircleRadius(0);

                chartSet.setLineWidth((float) 1.5);

                chartSet.setDrawValues(false);

                chartSet.setDrawFilled(true);


                if (chartSet1Entries.size() >= 300)
                    chartSet1Entries.clear();

                Entry chartSet1Entrie = new Entry(chartSet1Entries.size(), ware);

                chartSet1Entries.add(chartSet1Entrie);

                chartSet.setValues(chartSet1Entries);


                lineChart.setData(new LineData(chartSet));

                lineChart.setVisibleXRangeMinimum(300);

                lineChart.invalidate();

            }
        });

    }

    @Override
    public void measureComplete() {

        baseHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ECGServiceManager.getInstance().startResultAnalysis();

                View view = LayoutInflater.from(_mActivity).inflate(R.layout.ecg_dialog, null);
                loading_pic = view.findViewById(R.id.ecg_loading_pic);
                loading_text = view.findViewById(R.id.ecg_loading_text);
                loading_tip = view.findViewById(R.id.ecg_loading_tip);
                loading_tip.setText("请勿操作设备，数据上传中...");
                showDialog(view);

                animation();
                myAlphaAnimation.startNow();
            }
        }, 100);


    }

    @Override
    public void onAnalysisTime(final int time) {


        _mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loading_text.setText(time + "%");


            }
        });


    }

    @Override
    public void measureTimeOut(final String msg , final boolean isNeedRemeasure) {

        _mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(_mActivity, msg, Toast.LENGTH_LONG).show();
                final Button recommit = view.findViewById(R.id.ecg_reconnect);
                recommit.setVisibility(View.VISIBLE);
                if (myAlphaAnimation != null) {
                    myAlphaAnimation.cancel();
                }
                hideDialog();
                if (!isNeedRemeasure) {
                    recommit.setText("重新提交");
                    recommit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            recommit.setVisibility(View.INVISIBLE);
                            reupload();
                        }
                    });
                } else {
                    recommit.setText("重新测量");
                    recommit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            _mActivity.setResult(113);
                            _mActivity.finish();
                            System.exit(0);
                        }
                    });
                }
            }
        });


    }

    void reupload() {
        baseHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ECGServiceManager.getInstance().startGetToken();

                View view = LayoutInflater.from(_mActivity).inflate(R.layout.ecg_dialog, null);
                loading_pic = view.findViewById(R.id.ecg_loading_pic);
                loading_text = view.findViewById(R.id.ecg_loading_text);
                loading_tip = view.findViewById(R.id.ecg_loading_tip);
                loading_tip.setText("请勿操作设备，数据上传中...");
                showDialog(view);

                animation();
                myAlphaAnimation.startNow();
            }
        }, 100);
    }


    @Override
    public void onAnalysisEnd() {

        Log.i("haix", "读取文档数据完成");

    }

    @Override
    public void powerLow(String power) {
        super.powerLow(power);
        Toast.makeText(_mActivity, "电量低 , 请及时充电", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void measureFailed(String msg) {

        _mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                ECGServiceManager.getInstance().stopDeviceConnect();
                if (myAlphaAnimation != null) {
                    myAlphaAnimation.cancel();
                }
                hideDialog();
                //pop();

                _mActivity.setResult(113);

                _mActivity.finish();
                System.exit(0);
            }
        });

    }


    @Override
    public void onAnalysisResult(String result) {
        _mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (myAlphaAnimation != null) {
                    myAlphaAnimation.cancel();
                }
                hideDialog();

                //start(new ECGDeviceMeasureResult());

                Intent i = new Intent();
                i.putExtra("result", HearRate.getInstance());

                _mActivity.setResult(111, i);
                _mActivity.finish();
                System.exit(0);


            }
        });


    }


    public void animation() {
        myAlphaAnimation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);//设置图片动画属性，各参数说明可参照api
        myAlphaAnimation.setRepeatCount(-1);//设置旋转重复次数，即转几圈
        myAlphaAnimation.setDuration(500);//设置持续时间，注意这里是每一圈的持续时间，如果上面设置的圈数为3，持续时间设置1000，则图片一共旋转3秒钟
        myAlphaAnimation.setInterpolator(new LinearInterpolator());//设置动画匀速改变。相应的还有AccelerateInterpolator、DecelerateInterpolator、CycleInterpolator等
        loading_pic.setAnimation(myAlphaAnimation);//设置imageview的动画，也可以myImageView.startAnimation(myAlphaAnimation)
        myAlphaAnimation.setAnimationListener(new Animation.AnimationListener() { //设置动画监听事件
            @Override
            public void onAnimationStart(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                // TODO Auto-generated method stub

            }

            //图片旋转结束后触发事件，这里启动新的activity
            @Override
            public void onAnimationEnd(Animation arg0) {
                // TODO Auto-generated method stub
//                Intent i2 = new Intent(StartActivity.this, ECGMainActivity.class);
//                startActivity(i2);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        ECGServiceManager.getInstance().stopDeviceConnect();
        if (myAlphaAnimation != null) {
            myAlphaAnimation.cancel();
        }

        ECGServiceManager.getInstance().closeRecerver(_mActivity);

    }


    @Override
    public Context getViewContext() {
        return getActivity();
    }


    public void initChart() {
        chartSet.setColor(Color.BLACK);

        chartSet.setDrawCircles(false);

        chartSet.setDrawFilled(false);

        chartSet.setFillAlpha(0);

        chartSet.setCircleRadius(0);

        chartSet.setLineWidth((float) 1.5);

        chartSet.setDrawValues(false);

        chartSet.setDrawFilled(true);

        //圖表設定2（開始頁）
//        linechart.data = LineChartData(dataSets: [])
        lineChart.setData(new LineData());

//        linechart.xAxis.valueFormatter = nil
        lineChart.getXAxis().setValueFormatter(null);

//        linechart.xAxis.drawLabelsEnabled = false
        lineChart.getXAxis().setDrawLabels(false);


//        linechart.xAxis.drawGridLinesEnabled = false //顯示網格線
        lineChart.getXAxis().setDrawGridLines(false);

//        linechart.leftAxis.drawGridLinesEnabled = false //改
        lineChart.getAxisLeft().setDrawGridLines(false);

//        linechart.leftAxis.drawAxisLineEnabled = false //改
        lineChart.getAxisLeft().setDrawAxisLine(false);

//        linechart.leftAxis.granularityEnabled = false
        lineChart.getAxisLeft().setGranularityEnabled(false);


//        linechart.xAxis.drawAxisLineEnabled = false //改
        lineChart.getXAxis().setDrawAxisLine(false);

//        linechart.leftAxis.drawLabelsEnabled = false //LABE數字
        lineChart.getAxisLeft().setDrawLabels(false);


//        linechart.leftAxis.axisMinimum = 1500
        lineChart.getAxisLeft().setAxisMinimum(1500);

//        linechart.leftAxis.axisMaximum = 2500
        lineChart.getAxisLeft().setAxisMaximum(2500);
//
//        linechart.setScaleEnabled(false)
        lineChart.setScaleEnabled(false);

//        linechart.scaleXEnabled = true
        lineChart.setScaleXEnabled(true);

//        linechart.scaleYEnabled = true
//          linechart.setScaleYEnabled(true);

//        linechart.xAxis.axisMinimum = 0
        lineChart.getXAxis().setAxisMinimum(0);

//        linechart.xAxis.axisMaximum = 300
        lineChart.getXAxis().setAxisMaximum(300);

//        linechart.xAxis.granularity = 30
        lineChart.getXAxis().setGranularity(30);

//        linechart.leftAxis.granularity = 250
        lineChart.getAxisLeft().setGranularity(250);


//        linechart.rightAxis.drawLabelsEnabled = false
        lineChart.getAxisRight().setDrawLabels(false);

//        linechart.rightAxis.drawGridLinesEnabled = false
        lineChart.getAxisRight().setDrawGridLines(false);

//        linechart.rightAxis.drawAxisLineEnabled = false
        lineChart.getAxisRight().setDrawAxisLine(false);

//        linechart.chartDescription?.text = "" // must set or you'll see "Description Label", lol
        lineChart.getDescription().setText("");

//        linechart.legend.enabled = false // disable legend (default = true)
        lineChart.getLegend().setEnabled(false);

    }
}
