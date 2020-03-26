package com.guider.health.ecg.view;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.guider.health.common.net.MainActivity;
import com.guider.health.ecg.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class SixLead extends AppCompatActivity {

    // loge
    private static final String TAG = MainActivity.class.getSimpleName();
    // 圖
    private LineChart chart;
    // 幾筆資料
    private int bytes;
    // 開一個大容器  126208上下
    private byte[] buffer = new byte[150000];
    // 波1 值
    private int[] wv1;
    // 波1 專用
    private double[] mY = new double[5];
    private double[] mX = new double[5];
    private int[] mStreamBuf = new int[21];
    private double[] mACoef = {
            0.00001347408952448771,
            0.00005389635809795083,
            0.00008084453714692624,
            0.00005389635809795083,
            0.00001347408952448771
    };
    private double[] mBCoef = {
            1.00000000000000000000,
            -3.67172908916193470000,
            5.06799838673418980000,
            -3.11596692520174570000,
            0.71991032729187143000
    };

    // 波2 值
    private int[] wv2;
    // 波2 專用
    private double[] mY2 = new double[5];
    private double[] mX2 = new double[5];
    private int[] mStreamBuf2 = new int[21];
    private double[] mACoef2 = {
            0.00001347408952448771,
            0.00005389635809795083,
            0.00008084453714692624,
            0.00005389635809795083,
            0.00001347408952448771
    };
    private double[] mBCoef2 = {
            1.00000000000000000000,
            -3.67172908916193470000,
            5.06799838673418980000,
            -3.11596692520174570000,
            0.71991032729187143000
    };

    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sixlead);

        // 全屏
        this.getWindow().setFlags(WindowManager.LayoutParams.
                FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //將螢幕轉成横式
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // pete 大波形  "/data/user/0/com.example.apptext/files/J3QB2044.lp4"
        // 自 中間亂 "/data/user/0/com.example.apptext/files/J3QH2049.lp4"

//        path = "/data/user/0/com.example.apptext/files/J3QB2044.lp4";


        path = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator+"ECG"+File.separator + "temp.lp4";

        // 解壓縮此文件
//        decompNDK myDecompNDK = new decompNDK();
//        myDecompNDK.decpEcgFile(path);

        // 刪除檔案

        // 替換文字
        path = path.replace(".lp4",".cha");

        try {
            // 讀檔到fi
            FileInputStream fi = new FileInputStream(new File(path));
            // fi檔資料取到buffer，且bytes筆資料
            bytes = fi.read(buffer);
            // 126208 => 928包 * 32 => 29.696秒
            Log.e(TAG, "waveHandled: bytes: "+bytes);
            // 刪除檔案
//            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
//                + File.separator+"ECG");
//            file.delete();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            openFail(getResources().getString(R.string.ecg_file_open_error));
            Log.e(TAG, "waveHandled: bytes: "+ "error111" + e.toString());
            return;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "waveHandled: bytes: "+ "error222");
            openFail(getResources().getString(R.string.ecg_file_handle_error));
            return;
        }


        try{

            // (-100意思) => 100 * 4 = 少400ms
            wv1 = new int[((bytes / 136) << 3) - 100];
            wv2 = new int[((bytes / 136) << 3) - 100];

        }catch (NegativeArraySizeException e){
            e.printStackTrace();
            openFail(getResources().getString(R.string.ecg_file_handle_error));
            return;
        }

        int ans = 0;
        int ans2 = 0;
        int numI = 0;
        int wave1 = 0;
        int wave2 = 0;
        // 把檔案裡的波1、波2資料分別帶入陣列


        for (int i = 0 ; i < bytes ; i = i+8){
            numI = i % 136;
            // 波1
            if (-1 < numI && numI < 57){
                ans = ((int)buffer[i]&0xff) + (((int)buffer[(i+1)]&0xff) << 8);
                ans = getStreamLP(ans);

                // 去掉 前 100 * 4 亂數資料
                if (wave1 > 99){
                    wv1[wave1 - 100] = ans;
                }
                wave1++;
                // 波2
            }

            else if (63 < numI && numI < 121){
                ans2 = ((int)buffer[i]&0xff) + (((int)buffer[(i+1)]&0xff) << 8);
                ans2 = getStreamLP2(ans2);

                // 去掉 前 100 * 4 亂數資料
                if (wave2 > 99){
                    wv2[wave2 - 100] = ans2;
                }
                wave2++;
                // 跳過時間部分
                if (numI == 120){
                    i = i + 8;
                }
            }
        }


        go();

    }

    private void go() {
        // 背景圖設定
        chart = findViewById(R.id.chart);
        // 表格右下角 沒有描述文字
        chart.getDescription().setEnabled(false);
        // 點擊功能
        chart.setTouchEnabled(true);
        // 拖動
        chart.setDragEnabled(true);
        // 超出頁面
        chart.setScaleEnabled(true);
        // 兩指縮放
        chart.setPinchZoom(true);
        // 造成表格內灰色
        chart.setDrawGridBackground(true);
        // 設置替代背景顏色
        chart.setBackgroundColor(Color.WHITE);


        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();
        // 不要有左下角的線條資訊
        l.setEnabled(false);
        // 把左下的水藍方塊影藏
        l.setForm(Legend.LegendForm.NONE);


        // X軸設定
        XAxis xl = chart.getXAxis();
        // X軸值不顯示
        xl.setDrawLabels(false);
        xl.setTextColor(Color.GRAY);
        xl.setAxisMaximum(3000f);
        xl.setAxisMinimum(0f);
        // 不要繪製X軸網格線
        xl.setDrawGridLines(false);


        // 自設網格線
        LimitLine llx ;
        for (int i = 0 ; i < 1501 ; i++){
            llx = new LimitLine(i * 10,"");
            if (i%5 == 0){
                // 網格線粗細
                llx.setLineWidth(0.3f);
            }else{
                // 網格線粗細
                llx.setLineWidth(0.1f);
            }
            llx.enableDashedLine(10,0,0);
            xl.addLimitLine(llx);
        }

//        // 避免最後一次剪輯
//        xl.setAvoidFirstLastClipping(true);
//        xl.setEnabled(true);

        // Y軸設定
        YAxis leftAxis = chart.getAxisLeft();
        // Y值不顯示
        leftAxis.setDrawLabels(false);
        leftAxis.setTextColor(Color.GRAY);
        leftAxis.setAxisMaximum(300f);
        leftAxis.setAxisMinimum(0f);
        // 不要繪製Y軸網格線
        leftAxis.setDrawGridLines(false);
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        // 設置標籤
        LimitLine ll1 = new LimitLine(286, "I");
        ll1.setLineWidth(0);
        // 空白線
        ll1.enableDashedLine(0, 10, 0);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        ll1.setTextSize(14);
        leftAxis.addLimitLine(ll1);

        LimitLine ll2 = new LimitLine(236, "II");
        ll2.setLineWidth(0);
        // 空白線
        ll2.enableDashedLine(0, 10, 0);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        ll2.setTextSize(14);
        leftAxis.addLimitLine(ll2);

        LimitLine ll3 = new LimitLine(186, "III");
        ll3.setLineWidth(0);
        // 空白線
        ll3.enableDashedLine(0, 10, 0);
        ll3.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        ll3.setTextSize(14);
        leftAxis.addLimitLine(ll3);

        LimitLine llR = new LimitLine(136, "aVR");
        llR.setLineWidth(0);
        // 空白線
        llR.enableDashedLine(0, 10, 0);
        llR.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        llR.setTextSize(10);
        leftAxis.addLimitLine(llR);

        LimitLine llL = new LimitLine(86, "aVL");
        llL.setLineWidth(0);
        // 空白線
        llL.enableDashedLine(0, 10, 0);
        llL.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        llL.setTextSize(10);
        leftAxis.addLimitLine(llL);

        LimitLine llF = new LimitLine(36, "aVF");
        llF.setLineWidth(0);
        // 空白線
        llF.enableDashedLine(0, 10, 0);
        llF.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        llF.setTextSize(10);
        leftAxis.addLimitLine(llF);


        LimitLine lly;
        for (int i = 0 ; i < 151 ; i++){
            lly = new LimitLine(i << 1,"");
            if (i%5 == 0){
                // 網格線粗細
                lly.setLineWidth(0.3f);
            }else{
                // 網格線粗細
                lly.setLineWidth(0.1f);
            }
            leftAxis.addLimitLine(lly);
        }

        // 動畫
//        chart.animateX(10000);

        handled();


    }

    private void handled() {



        // 大N I
        ArrayList<Entry> valuesI = new ArrayList<>();
        valuesI.add(new Entry( 0 , 265 ));
        valuesI.add(new Entry( 20 , 265 ));
        valuesI.add(new Entry( 20 , 285 ));
        valuesI.add(new Entry( 70 , 285 ));
        valuesI.add(new Entry( 70 , ((wv1[0] ) / 11f) + 275f ));
//        valuesI.add(new Entry( 70 , 265 ));
//        valuesI.add(new Entry( 90 , 265 ));


        // 點跟點的連線設定
        LineDataSet setI = new LineDataSet(valuesI, "");
        // 點跟點之間的虛線設定
        setI.enableDashedLine(1, 0, 0);
        // 不要畫點上面的數字
        setI.setDrawValues(!setI.isDrawValuesEnabled());
        // 不要劃所有點
        setI.setDrawCircles(false);
        // 不出現點上的十字黃線
        setI.setHighlightEnabled(false);
        // 讓連線變圓曲線
//        set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        // 線寬度
        setI.setLineWidth(2);
        // 線顏色
        setI.setColor(Color.BLACK);

        int inI = wv1.length >> 1;
        // 波I
        ArrayList<Entry> values = new ArrayList<>();
        for (int i = 0 ; i < inI ; i++){
            values.add(new Entry( i + 70, ((wv1[i] ) / 11f) + 275f ));
//            values.add(new Entry( i + 0, ((wv1[i] ) / 11f) + 275f ));
        }
        // 點跟點的連線設定
        LineDataSet set1 = new LineDataSet(values, "");
        // 點跟點之間的虛線設定
        set1.enableDashedLine(1, 0, 0);
        // 不要畫點上面的數字
        set1.setDrawValues(!set1.isDrawValuesEnabled());
        // 不要劃所有點
        set1.setDrawCircles(false);
        // 不出現點上的十字黃線
        set1.setHighlightEnabled(false);
        // 讓連線變圓曲線
        set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        // 線寬度
        set1.setLineWidth(1f);
        // 線顏色
        set1.setColor(Color.BLUE);



        // 大N II
        ArrayList<Entry> valuesII = new ArrayList<>();
        valuesII.add(new Entry( 0 , 215 ));
        valuesII.add(new Entry( 20 , 215 ));
        valuesII.add(new Entry( 20 , 235 ));
        valuesII.add(new Entry( 70 , 235 ));
        valuesII.add(new Entry( 70 , ((wv2[0] ) / 11f) + 225f ));
//        valuesII.add(new Entry( 70 , 215 ));
//        valuesII.add(new Entry( 90 , 215 ));
        // 點跟點的連線設定
        LineDataSet setII = new LineDataSet(valuesII, "");
        // 點跟點之間的虛線設定
        setII.enableDashedLine(1, 0, 0);
        // 不要畫點上面的數字
        setII.setDrawValues(!setII.isDrawValuesEnabled());
        // 不要劃所有點
        setII.setDrawCircles(false);
        // 不出現點上的十字黃線
        setII.setHighlightEnabled(false);
        // 讓連線變圓曲線
//        set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        // 線寬度
        setII.setLineWidth(2);
        // 線顏色
        setII.setColor(Color.BLACK);

        // 波II
        ArrayList<Entry> values2 = new ArrayList<>();
        for (int i = 0 ; i < inI ; i++){
            values2.add(new Entry(i + 70 , ((wv2[i] ) / 11f) + 225f ));
//            values2.add(new Entry(i + 0 , ((wv2[i] ) / 11f) + 225f ));
        }
        // 點跟點的連線設定
        LineDataSet set2 = new LineDataSet(values2, "");
        // 點跟點之間的虛線設定
        set2.enableDashedLine(1f, 0f, 0);
        // 不要畫點上面的數字
        set2.setDrawValues(false);
        // 不要劃所有點
        set2.setDrawCircles(false);
        // 不出現點上的十字黃線
        set2.setHighlightEnabled(false);
        // 讓連線變圓曲線
        set2.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        // 線寬度
        set2.setLineWidth(1f);
        // 線顏色
        set2.setColor(Color.BLACK);



        // 大N III
        ArrayList<Entry> valuesIII = new ArrayList<>();
        valuesIII.add(new Entry( 0 , 165 ));
        valuesIII.add(new Entry( 20 , 165 ));
        valuesIII.add(new Entry( 20 , 185 ));
        valuesIII.add(new Entry( 70 , 185 ));
        valuesIII.add(new Entry( 70 , ((wv2[0] - wv1[0]) / 11f) + 175f  ));
//        valuesIII.add(new Entry( 70 , 165 ));
//        valuesIII.add(new Entry( 90 , 165 ));
//        // 點跟點的連線設定
        LineDataSet setIII = new LineDataSet(valuesIII, "");
        // 點跟點之間的虛線設定
        setIII.enableDashedLine(1, 0, 0);
        // 不要畫點上面的數字
        setIII.setDrawValues(!setIII.isDrawValuesEnabled());
        // 不要劃所有點
        setIII.setDrawCircles(false);
        // 不出現點上的十字黃線
        setIII.setHighlightEnabled(false);
        // 讓連線變圓曲線
//        set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        // 線寬度
        setIII.setLineWidth(2);
        // 線顏色
        setIII.setColor(Color.BLACK);

        // 波III  ^L2 - ^L1
        ArrayList<Entry> values3 = new ArrayList<>();
        for (int i = 0 ; i < inI ; i++){
            values3.add(new Entry(i + 70, ((wv2[i] - wv1[i]) / 11f) + 175f  ));
//            values3.add(new Entry(i + 0, ((wv2[i] - wv1[i]) / 11f) + 175f  ));
        }
        // 點跟點的連線設定
        LineDataSet set3 = new LineDataSet(values3, "");
        // 點跟點之間的虛線設定
        set3.enableDashedLine(1f, 0f, 0);
        // 不要畫點上面的數字
        set3.setDrawValues(false);
        // 不要劃所有點
        set3.setDrawCircles(false);
        // 不出現點上的十字黃線
        set3.setHighlightEnabled(false);
        // 讓連線變圓曲線
        set3.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        // 線寬度
        set3.setLineWidth(1f);
        // 線顏色
        set3.setColor(Color.BLUE);



        // 大N aVR
        ArrayList<Entry> valuesR = new ArrayList<>();
        valuesR.add(new Entry( 0 , 115 ));
        valuesR.add(new Entry( 20 , 115 ));
        valuesR.add(new Entry( 20 , 135 ));
        valuesR.add(new Entry( 70 , 135 ));
        valuesR.add(new Entry( 70 , ((- (wv1[0]+wv2[0]) >> 1) /11f) +125f  ));
//        valuesR.add(new Entry( 70 , 115 ));
//        valuesR.add(new Entry( 90 , 115 ));
        // 點跟點的連線設定
        LineDataSet setR = new LineDataSet(valuesR, "");
        // 點跟點之間的虛線設定
        setR.enableDashedLine(1, 0, 0);
        // 不要畫點上面的數字
        setR.setDrawValues(!setR.isDrawValuesEnabled());
        // 不要劃所有點
        setR.setDrawCircles(false);
        // 不出現點上的十字黃線
        setR.setHighlightEnabled(false);
        // 讓連線變圓曲線
//        set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        // 線寬度
        setR.setLineWidth(2);
        // 線顏色
        setR.setColor(Color.BLACK);

        // 波4  aVR： -0.5*(^L1+^L2)
        ArrayList<Entry> values4 = new ArrayList<>();
        for (int i = 0 ; i < inI ; i++){
            values4.add(new Entry(i + 70 ,  ((- (wv1[i]+wv2[i]) >> 1) /11f) +125f  ));
//            values4.add(new Entry(i + 0 ,  ((- (wv1[i]+wv2[i]) >> 1) /11f) +125f  ));
        }
        // 點跟點的連線設定
        LineDataSet set4 = new LineDataSet(values4, "");
        // 點跟點之間的虛線設定
        set4.enableDashedLine(1f, 0f, 0);
        // 不要畫點上面的數字
        set4.setDrawValues(false);
        // 不要劃所有點
        set4.setDrawCircles(false);
        // 不出現點上的十字黃線
        set4.setHighlightEnabled(false);
        // 讓連線變圓曲線
        set4.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        // 線寬度
        set4.setLineWidth(1f);
        // 線顏色
        set4.setColor(Color.BLACK);



        // 大N aVL
        ArrayList<Entry> valuesL = new ArrayList<>();
        valuesL.add(new Entry( 0 , 65 ));
        valuesL.add(new Entry( 20 , 65 ));
        valuesL.add(new Entry( 20 , 85 ));
        valuesL.add(new Entry( 70 , 85 ));
        valuesL.add(new Entry( 70 , ((wv1[0] + (- wv2[0] >> 1)) /11f) +75f  ));
//        valuesL.add(new Entry( 70 , 65 ));
//        valuesL.add(new Entry( 90 , 65 ));
        // 點跟點的連線設定
        LineDataSet setL = new LineDataSet(valuesL, "");
        // 點跟點之間的虛線設定
        setL.enableDashedLine(1, 0, 0);
        // 不要畫點上面的數字
        setL.setDrawValues(!setL.isDrawValuesEnabled());
        // 不要劃所有點
        setL.setDrawCircles(false);
        // 不出現點上的十字黃線
        setL.setHighlightEnabled(false);
        // 讓連線變圓曲線
//        set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        // 線寬度
        setL.setLineWidth(2);
        // 線顏色
        setL.setColor(Color.BLACK);

        // 波5  aVL： ^L1 + -0.5*^L2
        ArrayList<Entry> values5 = new ArrayList<>();
        for (int i = 0 ; i < inI ; i++){
            values5.add(new Entry(i + 70 ,  ((wv1[i] + (- wv2[i] >> 1)) /11f) +75f  ));
//            values5.add(new Entry(i + 0 ,  ((wv1[i] + (- wv2[i] >> 1)) /11f) +75f  ));
        }
        // 點跟點的連線設定
        LineDataSet set5 = new LineDataSet(values5, "");
        // 點跟點之間的虛線設定
        set5.enableDashedLine(1f, 0f, 0);
        // 不要畫點上面的數字
        set5.setDrawValues(false);
        // 不要劃所有點
        set5.setDrawCircles(false);
        // 不出現點上的十字黃線
        set5.setHighlightEnabled(false);
        // 讓連線變圓曲線
        set5.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        // 線寬度
        set5.setLineWidth(1f);
        // 線顏色
        set5.setColor(Color.BLUE);



        // 大N aVF
        ArrayList<Entry> valuesF = new ArrayList<>();
        valuesF.add(new Entry( 0 , 15 ));
        valuesF.add(new Entry( 20 , 15 ));
        valuesF.add(new Entry( 20 , 35 ));
        valuesF.add(new Entry( 70 , 35 ));
        valuesF.add(new Entry( 70 , ((wv2[0] + (- wv1[0] >> 1)) /11f) +25f ));
//        valuesF.add(new Entry( 70 , 15 ));
//        valuesF.add(new Entry( 90 , 15 ));
        // 點跟點的連線設定
        LineDataSet setF = new LineDataSet(valuesF, "");
        // 點跟點之間的虛線設定
        setF.enableDashedLine(1, 0, 0);
        // 不要畫點上面的數字
        setF.setDrawValues(!setF.isDrawValuesEnabled());
        // 不要劃所有點
        setF.setDrawCircles(false);
        // 不出現點上的十字黃線
        setF.setHighlightEnabled(false);
        // 讓連線變圓曲線
//        set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        // 線寬度
        setF.setLineWidth(2);
        // 線顏色
        setF.setColor(Color.BLACK);

        // 波6  aVF：  -0.5*^L1 + ^L2
        ArrayList<Entry> values6 = new ArrayList<>();
        for (int i = 0 ; i < inI ; i++){
            values6.add(new Entry(i + 70 ,  ((wv2[i] + (- wv1[i] >> 1)) /11f) +25f  ));
//            values6.add(new Entry(i + 0 ,  ((wv2[i] + (- wv1[i] >> 1)) /11f) +25f  ));
        }
        // 點跟點的連線設定
        LineDataSet set6 = new LineDataSet(values6, "");
        // 點跟點之間的虛線設定
        set6.enableDashedLine(1f, 0f, 0);
        // 不要畫點上面的數字
        set6.setDrawValues(false);
        // 不要劃所有點
        set6.setDrawCircles(false);
        // 不出現點上的十字黃線
        set6.setHighlightEnabled(false);
        // 讓連線變圓曲線
        set6.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        // 線寬度
        set6.setLineWidth(1f);
        // 線顏色
        set6.setColor(Color.BLACK);


        LineData d = new LineData(setI,setII,setIII,setR,setL,setF,
                set1,set2,set3,set4,set5,set6);
        chart.setData(d);

    }


    private int getStreamLP(int NewSample) {
        int n;
        int tmp;
        //shift the old samples
        for (n = 4; n > 0; n--) {
            mX[n] = mX[n - 1];
            mY[n] = mY[n - 1];
        }

        //Calculate the new output
        mX[0] = (double) NewSample;
        mY[0] = mACoef[0] * mX[0];
        for (n = 1; n <= 4; n++)
            mY[0] += mACoef[n] * mX[n] - mBCoef[n] * mY[n];

        for (n = 20; n >= 1; n--)
            mStreamBuf[n] = mStreamBuf[n - 1];

        mStreamBuf[0] = NewSample;

        tmp = mStreamBuf[20] + (( - (int) mY[0]));

        return tmp;
    }

    private int getStreamLP2(int NewSample) {
        int n;
        int tmp;
        //shift the old samples
        for (n = 4; n > 0; n--) {
            mX2[n] = mX2[n - 1];
            mY2[n] = mY2[n - 1];
        }

        //Calculate the new output
        mX2[0] = (double) NewSample;
        mY2[0] = mACoef2[0] * mX2[0];
        for (n = 1; n <= 4; n++)
            mY2[0] += mACoef2[n] * mX2[n] - mBCoef2[n] * mY2[n];

        for (n = 20; n >= 1; n--)
            mStreamBuf2[n] = mStreamBuf2[n - 1];

        mStreamBuf2[0] = NewSample;

        tmp = mStreamBuf2[20] + (( - (int) mY2[0]));

        return tmp;
    }

    private void openFail(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        finish();
    }
}
