package com.guider.healthring.b31;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.google.gson.Gson;
import com.guider.healthring.MyApp;
import com.guider.healthring.R;
import com.guider.healthring.b30.b30view.B30CusSleepView;
import com.guider.healthring.b30.bean.B30HalfHourDao;
import com.guider.healthring.b30.bean.CusVPSleepPrecisionData;
import com.guider.healthring.siswatch.WatchBaseActivity;
import com.guider.healthring.siswatch.utils.WatchUtils;
import com.guider.healthring.util.Constant;

import org.apache.commons.lang.StringUtils;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;


/**
 * B31s精准睡眠详细页面
 * Created by Admin
 * Date 2019/9/6
 */
public class B31sPrecisionSleepActivity extends WatchBaseActivity implements View.OnClickListener {

    private static final String TAG = "B31sPrecisionSleepActiv";



    public static void startAndParams(Context context, String date) {
        Intent intent = new Intent(context, B31sPrecisionSleepActivity.class);
        intent.putExtra(Constant.DETAIL_DATE, date);
        context.startActivity(intent);
    }

    ConstraintLayout sleepLengthConLin,sleepAwakeConLin,sleepInsomniaConLin,sleepEayConLin,sleepDeepConLin,sleepLowConLin;


    ImageView commentB30BackImg;
    TextView commentB30TitleTv;
    private ImageView commArrowLeft,commArrowRight;
    B30CusSleepView b31sDetailCusSleepView;
    SeekBar b31sSleepSeekBar;
    TextView sleepCurrDateTv;
    //睡眠开始时间
    TextView b31sStartSleepTimeTv;
    //睡眠结束时间
    TextView b31sEndSleepTimeTv;
    //睡眠时间长
    TextView b31sDetailAllSleepTv;
    //苏醒
    TextView b31sDetailAwakeTimesTv;
    //失眠
    TextView detailInsomniaSleepTv;
    //快速眼动
    TextView detailAwakeHeightTv;

    //深睡时长
    TextView b31sDetailDeepTv;
    TextView b31sSleepLengthResultTv;


    //浅睡时长
    TextView b31sDetailHightSleepTv;

    //苏醒次数
    TextView b31sAwakeNumbersTv;

    //入睡效率
    TextView b31sSleepInEfficiencyScoreTv;
    //睡眠效率
    TextView b31sSleepEffectivenessTv;
    Toolbar commB31TitleLayout;
    //睡眠质量表示星星
    AppCompatRatingBar b31sPercisionSleepQualityRatingBar;
    //苏醒百分比
    TextView b31sAwawkPercentTv;
    //苏醒状态
    TextView b31sSleepAwakeResultTv;
    //失眠百分比
    TextView b31sSleepInsomniaPercentTv;
    //失眠状态
    TextView b31sSleepInsomniaResultTv;
    //快速眼动百分比
    TextView b31sSleepEayPercentTv;
    //快速演的状态
    TextView b31sSleepEayResultTv;
    //深睡百分比
    TextView b31sSleepDeepPercentTv;
    //深睡状态
    TextView b31sSleepDeepResultTv;
    //浅睡百分比
    TextView b31sSleepLowPercentTv;
    //浅睡状态
    TextView b31sSleepLowResultTv;

    LinearLayout commDateLin;

    private List<Integer> listValue;

    String currDay;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b31s_precision_sleep);

        findViews();

        initViews();
        currDay = getIntent().getStringExtra(Constant.DETAIL_DATE);
        if (WatchUtils.isEmpty(currDay))
            currDay = WatchUtils.getCurrentDate();
        initData(currDay);


    }


    private void findViews(){
        commentB30BackImg = findViewById(R.id.commentB30BackImg);
        commentB30TitleTv = findViewById(R.id.commentB30TitleTv);
        b31sDetailCusSleepView = findViewById(R.id.b31sDetailCusSleepView);

        commArrowLeft = findViewById(R.id.commArrowLeft);
        commArrowRight = findViewById(R.id.commArrowRight);

        b31sSleepSeekBar = findViewById(R.id.b31sSleepSeekBar);
        sleepCurrDateTv = findViewById(R.id.commArrowDate);

        commDateLin = findViewById(R.id.commCheckDateLin);

        //睡眠开始时间
        b31sStartSleepTimeTv = findViewById(R.id.b31sStartSleepTimeTv);
        //睡眠结束时间
        b31sEndSleepTimeTv = findViewById(R.id.b31sEndSleepTimeTv);
        //睡眠时间长
        b31sDetailAllSleepTv = findViewById(R.id.b31sDetailAllSleepTv);
        //苏醒
        b31sDetailAwakeTimesTv = findViewById(R.id.b31sDetailAwakeTimesTv);
        //失眠
        detailInsomniaSleepTv = findViewById(R.id.detailInsomniaSleepTv);
        //快速眼动
        detailAwakeHeightTv = findViewById(R.id.detailAwakeHeightTv);

        //深睡时长
        b31sDetailDeepTv = findViewById(R.id.b31sDetailDeepTv);
        b31sSleepLengthResultTv = findViewById(R.id.b31sSleepLengthResultTv);
        //浅睡时长
        b31sDetailHightSleepTv = findViewById(R.id.b31sDetailHightSleepTv);
        //苏醒次数
        b31sAwakeNumbersTv = findViewById(R.id.b31sAwakeNumbersTv);

        //入睡效率
        b31sSleepInEfficiencyScoreTv = findViewById(R.id.b31sSleepInEfficiencyScoreTv);
        //睡眠效率
        b31sSleepEffectivenessTv = findViewById(R.id.b31sSleepEffectivenessTv);
        commB31TitleLayout = findViewById(R.id.commB31TitleLayout);
        //睡眠质量表示星星
        b31sPercisionSleepQualityRatingBar = findViewById(R.id.b31sPercisionSleepQualityRatingBar);
        //苏醒百分比
        b31sAwawkPercentTv = findViewById(R.id.b31sAwawkPercentTv);
        //苏醒状态
        b31sSleepAwakeResultTv = findViewById(R.id.b31sSleepAwakeResultTv);
        //失眠百分比
        b31sSleepInsomniaPercentTv = findViewById(R.id.b31sSleepInsomniaPercentTv);
        //失眠状态
        b31sSleepInsomniaResultTv = findViewById(R.id.b31sSleepInsomniaResultTv);
        //快速眼动百分比
        b31sSleepEayPercentTv = findViewById(R.id.b31sSleepEayPercentTv);
        //快速演的状态
        b31sSleepEayResultTv = findViewById(R.id.b31sSleepEayResultTv);
        //深睡百分比
        b31sSleepDeepPercentTv = findViewById(R.id.b31sSleepDeepPercentTv);
        //深睡状态
        b31sSleepDeepResultTv = findViewById(R.id.b31sSleepDeepResultTv);
        //浅睡百分比
        b31sSleepLowPercentTv = findViewById(R.id.b31sSleepLowPercentTv);
        //浅睡状态
        b31sSleepLowResultTv = findViewById(R.id.b31sSleepLowResultTv);


        sleepLengthConLin = findViewById(R.id.sleepLengthConLin);
        sleepAwakeConLin = findViewById(R.id.sleepAwakeConLin);
        sleepInsomniaConLin = findViewById(R.id.sleepInsomniaConLin);
        sleepEayConLin = findViewById(R.id.sleepEayConLin);
        sleepDeepConLin = findViewById(R.id.sleepDeepConLin);
        sleepLowConLin  = findViewById(R.id.sleepLowConLin);


        sleepLengthConLin.setOnClickListener(this);
        sleepAwakeConLin.setOnClickListener(this);
        sleepInsomniaConLin.setOnClickListener(this);
        sleepEayConLin.setOnClickListener(this);
        sleepDeepConLin.setOnClickListener(this);
        sleepLowConLin.setOnClickListener(this);
        commentB30BackImg.setOnClickListener(this);
        commArrowLeft.setOnClickListener(this);
        commArrowRight.setOnClickListener(this);


    }



    private void initData(String dayStr) {
        sleepCurrDateTv.setText(dayStr);
        listValue.clear();
        try {
            String mac = MyApp.getInstance().getMacAddress();
            if (WatchUtils.isEmpty(mac))
                return;
            String sleepStr = B30HalfHourDao.getInstance().findOriginData(mac, dayStr, B30HalfHourDao.TYPE_PRECISION_SLEEP);
//            Log.e(TAG, "------sleepStr=" + sleepStr);
            CusVPSleepPrecisionData cusVPSleepPrecisionData = new Gson().fromJson(sleepStr, CusVPSleepPrecisionData.class);
//             Log.e(TAG,"-------sl="+cusVPSleepPrecisionData.toString());
            showSleepChartView(cusVPSleepPrecisionData);
            showSleepDetail(cusVPSleepPrecisionData);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("SetTextI18n")
    private void showSleepDetail(CusVPSleepPrecisionData cusVPSleepPrecisionData) {
        //睡眠质量
        int sleepQuality = cusVPSleepPrecisionData == null ? 1 : cusVPSleepPrecisionData.getSleepQulity();
        b31sPercisionSleepQualityRatingBar.setMax(5);
        b31sPercisionSleepQualityRatingBar.setNumStars(sleepQuality);

        //入睡时间
        b31sStartSleepTimeTv.setText(cusVPSleepPrecisionData == null ? "0h0m" : cusVPSleepPrecisionData.getSleepDown().getColck());
        //起床时间
        b31sEndSleepTimeTv.setText(cusVPSleepPrecisionData == null ? "0h0m" : cusVPSleepPrecisionData.getSleepUp().getColck());
        //睡眠时长
        if (cusVPSleepPrecisionData == null) {
            b31sDetailAllSleepTv.setText("0h0m");
            b31sSleepLengthResultTv.setText("--");
        } else {
            int allTime = cusVPSleepPrecisionData.getAllSleepTime();
            b31sDetailAllSleepTv.setText(allTime / 60 + "h" + allTime % 60 + "m");

            //7-9小时之间为正常
            b31sSleepLengthResultTv.setText(allTime <420 ? "偏低" : ( allTime >540 ? "偏高" : "正常"));
        }


        //失眠时长
        detailInsomniaSleepTv.setText(cusVPSleepPrecisionData == null ? "0h0m" : cusVPSleepPrecisionData.getInsomniaLength() / 60 + "h" + cusVPSleepPrecisionData.getInsomniaLength() % 60 + "m");
        //快速眼动时长
        detailAwakeHeightTv.setText(cusVPSleepPrecisionData == null ? "0h0m" : cusVPSleepPrecisionData.getOtherDuration() / 60 + "h" + cusVPSleepPrecisionData.getOtherDuration() % 60 + "m");
        //深度睡眠时长
        b31sDetailDeepTv.setText(cusVPSleepPrecisionData == null ? "0h0m" : cusVPSleepPrecisionData.getDeepSleepTime() / 60 + "h" + cusVPSleepPrecisionData.getDeepSleepTime() % 60 + "m");
        //浅度睡眠时长
        b31sDetailHightSleepTv.setText(cusVPSleepPrecisionData == null ? "0h0m" : cusVPSleepPrecisionData.getLowSleepTime() / 60 + "h" + cusVPSleepPrecisionData.getLowSleepTime() % 60 + "m");
        //苏醒次数
        b31sAwakeNumbersTv.setText(cusVPSleepPrecisionData == null ? "--" : cusVPSleepPrecisionData.getWakeCount() + getResources().getString(R.string.cishu));
        //入睡效率
        b31sSleepInEfficiencyScoreTv.setText(cusVPSleepPrecisionData == null ? "--" : cusVPSleepPrecisionData.getFirstDeepDuration() + getResources().getString(R.string.signle_minute));
        //睡眠效率得分
        b31sSleepEffectivenessTv.setText(cusVPSleepPrecisionData == null ? "--" : cusVPSleepPrecisionData.getGetUpToDeepAve() + getResources().getString(R.string.signle_minute));
        if(cusVPSleepPrecisionData == null){
            return;
        }
        //总的睡眠时长
        float countSleepTime = cusVPSleepPrecisionData.getAllSleepTime();
        DecimalFormat decimalFormat = new DecimalFormat("#.##");


        //失眠
        float insomniaDurationTime = cusVPSleepPrecisionData.getInsomniaDuration();
        if(insomniaDurationTime == 0){
            b31sSleepInsomniaPercentTv.setText("0%");
            b31sSleepInsomniaResultTv.setText("正常");
        }else{
            float insomniaPercent = insomniaDurationTime / countSleepTime;
            String insomniPercentV = StringUtils.substringAfter(decimalFormat.format(insomniaPercent), ".");
            b31sSleepInsomniaPercentTv.setText(insomniPercentV + "%");
            int tmpInsomniV = Integer.valueOf(insomniPercentV);
            b31sSleepInsomniaResultTv.setText(tmpInsomniV == 0 ? "正常" : "严重");
        }



        //深睡
        float deepTime = cusVPSleepPrecisionData.getDeepSleepTime();
        float deepPercent = deepTime / countSleepTime;
        //Log.e(TAG,"--------深睡="+deepTime+"--="+deepPercent);
        String tmpPer = decimalFormat.format(deepPercent);
        if(tmpPer.equals("0")){
            b31sSleepDeepPercentTv.setText("0%");
            b31sSleepDeepResultTv.setText("偏低");
        }else{
            String deepPercentV = StringUtils.substringAfter(tmpPer,".");
            b31sSleepDeepPercentTv.setText(deepPercentV+"%");
            int tmpDeepV = Integer.valueOf(deepPercentV);
            b31sSleepDeepResultTv.setText(tmpDeepV>=21?"正常":"偏低");
        }


        //浅睡
        float lowTime = cusVPSleepPrecisionData.getLowSleepTime();
        float lowPercent = lowTime / countSleepTime;
        String lowPercentV = StringUtils.substringAfter(decimalFormat.format(lowPercent),".");
        b31sSleepLowPercentTv.setText(lowPercentV+"%");
        int tmpLowV = Integer.valueOf(lowPercentV);
        b31sSleepLowResultTv.setText((0<=tmpLowV && tmpLowV<=59)?"正常":"偏低");



        //快速眼动
        float otherTime = cusVPSleepPrecisionData.getOtherDuration();
        float otherPercent = otherTime / countSleepTime;
        float formV = Float.valueOf(decimalFormat.format(otherPercent));
        formV = formV * 100;
        String otherPercentV = StringUtils.substringBefore(decimalFormat.format(formV),".");
        b31sSleepEayPercentTv.setText(otherPercentV +"%");
        int tmpOhterV = Integer.valueOf(otherPercentV);
        b31sSleepEayResultTv.setText((tmpOhterV >=10 && tmpOhterV <=30)?"正常":"偏低");


        //苏醒百分比，
        //苏醒时长=总时长-深睡-浅睡-快速眼动
        float awakeTime = countSleepTime - deepTime - lowTime - otherTime;
        if(awakeTime == 0){
            b31sAwawkPercentTv.setText("0%");
            b31sSleepAwakeResultTv.setText( "正常");
            b31sDetailAwakeTimesTv.setText( "0h0m");
        }else{
            float awakePercent = awakeTime / countSleepTime;
            String formStr = StringUtils.substringAfter(decimalFormat.format(awakePercent),".");
            b31sAwawkPercentTv.setText(Integer.valueOf(formStr.trim())+"%");
            int tmpAwakeV = Integer.valueOf(formStr);
            b31sSleepAwakeResultTv.setText(tmpAwakeV <= 1 ? "正常" : "严重");
            //苏醒时长
            b31sDetailAwakeTimesTv.setText(((int)awakeTime) / 60 + "h" + ((int)awakeTime) % 60 + "m");
        }



    }

    //展示睡眠图表
    private void showSleepChartView(final CusVPSleepPrecisionData cusVPSleepPrecisionData) {
        if (cusVPSleepPrecisionData == null) {
            listValue.clear();
            b31sDetailCusSleepView.setPrecisionSleep(true);
            b31sDetailCusSleepView.setSleepList(listValue);
            return;
        }
        String sleepLin = cusVPSleepPrecisionData.getSleepLine();
        Log.e(TAG, "------精准睡眠字符串=" + sleepLin + "\n" + sleepLin.length());
        for (int i = 0; i < sleepLin.length(); i++) {
            if (i <= sleepLin.length() - 1) {
                int subStr = Integer.valueOf(sleepLin.substring(i, i + 1));
                listValue.add(subStr);
            }
        }

        Log.e(TAG, "---------size=" + listValue.size());
        b31sDetailCusSleepView.setPrecisionSleep(true);
        b31sDetailCusSleepView.setSleepList(listValue);

        b31sDetailCusSleepView.setPrecisionSleepLin(false);
        b31sSleepSeekBar.setMax(listValue.size());
        b31sSleepSeekBar.setProgress(-1);
        //开始时间
        final int startTime = cusVPSleepPrecisionData.getSleepDown().getHMValue();
        b31sSleepSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == listValue.size())
                    return;
                //Log.e(TAG,"-------progress="+progress);

                int seekTime = startTime + progress;
                //Log.e(TAG,"-----seekTime="+seekTime);

                int resultHour = seekTime / 60;
                int resultMine = seekTime % 60;

                //精准睡眠，睡眠曲线是一组由0,1,2，3,4组成的字符串，每一个字符代表时长为1分钟，
                // 其中0表示深睡，1表示浅睡，2表示快速眼动,3表示失眠,4表示苏醒。
                int sleepType = listValue.get(progress);
                String sleetTypeStr = null;
                switch (sleepType) {
                    case 0:     //深睡
                        sleetTypeStr = "深睡";
                        break;
                    case 1:     //浅睡
                        sleetTypeStr = "浅睡";
                        break;
                    case 2:     //快速眼动
                        sleetTypeStr = "快速眼动";
                        break;
                    case 3:     //失眠
                        sleetTypeStr = "失眠";
                        break;
                    case 4:     //苏醒
                        sleetTypeStr = "苏醒";
                        break;
                }
                String hourS = resultHour < 10 ? ("0" + resultHour) :resultHour>=24 ? ("0"+(resultHour-24)) : resultHour+"";

                b31sDetailCusSleepView.setSleepDateTxt(sleetTypeStr + (hourS + ":" + (resultMine < 10 ? ("0" + resultMine) : resultMine)));
                b31sDetailCusSleepView.setPreicisionLinSchdue(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                b31sDetailCusSleepView.setPrecisionSleepLin(true, 0);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(getResources().getString(R.string.sleep));
        commB31TitleLayout.setBackgroundColor(Color.parseColor("#20806F"));
        commDateLin.setBackgroundColor(Color.parseColor("#20806F"));
        listValue = new ArrayList<>();
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
        initData(currDay);
    }



    private void verticalSleepType(String sleepDesc,String valueStr ,String status,int code){
        startActivity(B31sPercisionSleepDescActivity.class,new String[]{"sleepDesc","valueStr","status","code"},new String[]{sleepDesc,valueStr,status,String.valueOf(code)});

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:
                finish();
                break;
            case R.id.commArrowRight:   //后一天
                changeDayData(false);
                break;
            case R.id.commArrowLeft:    //前一天
                changeDayData(true);
                break;
            case R.id.sleepLengthConLin:    //睡眠时长
                String sleepLengthV = b31sDetailAllSleepTv.getText().toString().trim();
                String sleepLengthStatus = b31sSleepLengthResultTv.getText().toString();
                verticalSleepType("睡眠时长",sleepLengthV,sleepLengthStatus,1);
                break;
            case R.id.sleepAwakeConLin:     //苏醒
                String sleepAwakeConLinV = b31sAwawkPercentTv.getText().toString();
                String b31sSleepAwakeResultTvV = b31sSleepAwakeResultTv.getText().toString();
                verticalSleepType("苏醒",sleepAwakeConLinV,b31sSleepAwakeResultTvV,2);
                break;
            case R.id.sleepInsomniaConLin:  //失眠
                String b31sSleepInsomniaPercentTvV = b31sSleepInsomniaPercentTv.getText().toString();
                String b31sSleepInsomniaStatus = b31sSleepInsomniaResultTv.getText().toString().trim();
                verticalSleepType("失眠",b31sSleepInsomniaPercentTvV,b31sSleepInsomniaStatus,3);
                break;
            case R.id.sleepEayConLin:       //快速眼动
                String b31sSleepEayPercentTvV = b31sSleepEayPercentTv.getText().toString();
                String b31sSleepEayResultTvV = b31sSleepEayResultTv.getText().toString();
                verticalSleepType("快速眼动",b31sSleepEayPercentTvV,b31sSleepEayResultTvV,4);
                break;
            case R.id.sleepDeepConLin:  //深睡
                String b31sSleepDeepPercentTvV = b31sSleepDeepPercentTv.getText().toString();
                String b31sSleepDeepResultTvV = b31sSleepDeepResultTv.getText().toString();

                verticalSleepType("深睡",b31sSleepDeepPercentTvV,b31sSleepDeepResultTvV,5);

                break;
            case R.id.sleepLowConLin:   //浅睡
                String b31sSleepLowPercentTvV = b31sSleepLowPercentTv.getText().toString();
                String b31sSleepLowResultTvV = b31sSleepLowResultTv.getText().toString();

                verticalSleepType("浅睡",b31sSleepLowPercentTvV,b31sSleepLowResultTvV,6);
                break;
        }
    }
}
