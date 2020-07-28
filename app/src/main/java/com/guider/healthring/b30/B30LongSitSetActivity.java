package com.guider.healthring.b30;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.aigestudio.wheelpicker.widgets.ProfessionPick;
import com.aigestudio.wheelpicker.widgets.ProvincePick;
import com.guider.healthring.MyApp;
import com.guider.healthring.R;
import com.guider.healthring.bleutil.MyCommandManager;
import com.guider.healthring.siswatch.WatchBaseActivity;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.ILongSeatDataListener;
import com.veepoo.protocol.model.datas.LongSeatData;
import com.veepoo.protocol.model.enums.ELongSeatStatus;
import com.veepoo.protocol.model.settings.LongSeatSetting;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * Created by Administrator on 2018/8/13.
 */

/**
 * 久坐提醒设置时间
 */
public class B30LongSitSetActivity extends WatchBaseActivity
        implements CompoundButton.OnCheckedChangeListener,View.OnClickListener{


    ImageView commentB30BackImg;
    TextView commentB30TitleTv;
    TextView showB30LongSitStartTv;
    TextView showB30LongSitEndTv;
    TextView showB30LongSitTv;
    ToggleButton longSitToggleBtn;


    private ArrayList<String> hourList;
    private ArrayList<String> minuteList;
    private HashMap<String, ArrayList<String>> minuteMapList;
    ArrayList<String> longTimeLit;

    boolean isToggChecked = false;
    int hour;
    int min;
    int startHour;
    int startMin;

    ArrayList<String> longSitHourList;//久坐结束时间
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b30_long_sitset);
        initViewIds();
        initViews();
        initData();
        //读取久坐提醒
        readLongSitData();

    }

    private void initViewIds() {
        commentB30BackImg = findViewById(R.id.commentB30BackImg);
        commentB30TitleTv = findViewById(R.id.commentB30TitleTv);
        showB30LongSitStartTv = findViewById(R.id.showB30LongSitStartTv);
        showB30LongSitEndTv = findViewById(R.id.showB30LongSitEndTv);
        showB30LongSitTv = findViewById(R.id.showB30LongSitTv);
        longSitToggleBtn = findViewById(R.id.longSitToggleBtn);
        commentB30BackImg.setOnClickListener(this);
        findViewById(R.id.b30LongSitStartRel).setOnClickListener(this);
        findViewById(R.id.b30LongSitEndRel).setOnClickListener(this);
        findViewById(R.id.b30LongSitTimeRel).setOnClickListener(this);
        findViewById(R.id.b30LongSitSaveBtn).setOnClickListener(this);
    }

    private void readLongSitData() {



        //读取久坐提醒
        MyApp.getInstance().getVpOperateManager().readLongSeat(iBleWriteResponse, new ILongSeatDataListener() {
            @Override
            public void onLongSeatDataChange(LongSeatData longSeatData) {
                isToggChecked = longSeatData.isOpen();
                longSitToggleBtn.setChecked(longSeatData.isOpen());

                hour = longSeatData.getEndHour();
                min = longSeatData.getEndMinute();

                startHour = longSeatData.getStartHour();
                startMin = longSeatData.getStartMinute();

                String startHourStr = "";
                String startMinStr = "";

                String minStr = "";
                String hourStr = "";

                if(startHour < 10){
                    startHourStr = "0"+ startHour;
                }else {
                    startHourStr = ""+startHour;
                }

                if(startMin < 10){
                    startMinStr = "0"+ startMin;
                }else {
                    startMinStr = ""+startMin;
                }

                if(min < 10){
                    minStr  = "0"+ min;
                }else {
                    minStr = ""+min;
                }

                if(hour < 10){
                    hourStr = "0"+hour;
                }else {
                    hourStr = ""+hour;
                }

                showB30LongSitStartTv.setText(startHourStr + ":" + startMinStr);
                showB30LongSitEndTv.setText(hourStr + ":" + minStr);
                showB30LongSitTv.setText(longSeatData.getThreshold() + "min");
            }
        });
    }

    private void initData() {
        hourList = new ArrayList<>();

        minuteList = new ArrayList<>();

        minuteMapList = new HashMap<>();


        longTimeLit = new ArrayList<>();

        longSitHourList = new ArrayList<>();

        for (int i = 8; i <= 18; i ++) {
            if(i < 10){
                longSitHourList.add("0"+i);
            }else {
                longSitHourList.add(i + "");
            }

        }

        for (int i = 30; i <= 240; i++) {
            longTimeLit.add(i + "");
        }

        for (int i = 0; i < 60; i++) {
            if (i == 0) {
                minuteList.add("00");
            } else if (i < 10) {
                minuteList.add("0" + i);
            } else {
                minuteList.add(i + "");
            }
        }
        for (int i = 8; i <= 18; i++) {
            if (i < 10) {
                hourList.add("0" + i + "");
                minuteMapList.put("0" + i + "", minuteList);

            } else {
                hourList.add(i + "");
                minuteMapList.put(i + "", minuteList);

            }
        }

    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(getResources().getString(R.string.string_sedentary_setting));//"久坐设置"
        longSitToggleBtn.setOnCheckedChangeListener(this);
    }

   @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:    //返回
                finish();
                break;
            case R.id.b30LongSitStartRel:   //开始时间
                chooseStartEndDate(0);
                break;
            case R.id.b30LongSitEndRel: //结束时间
                chooseStartEndDate(1);
                break;
            case R.id.b30LongSitTimeRel:    //时长
                chooseLongTime();
                break;
            case R.id.b30LongSitSaveBtn:    //保存
                saveLongSitData();
                break;
        }
    }

    private void saveLongSitData() {
        try {
            if (MyCommandManager.DEVICENAME != null) {
                String startD = showB30LongSitStartTv.getText().toString().trim();
                int startHour = Integer.valueOf(StringUtils.substringBefore(startD, ":").replace(" ","").trim());
                int startMine = Integer.valueOf(StringUtils.substringAfter(startD, ":").replace(" ","").trim());
                String endD = showB30LongSitEndTv.getText().toString().trim();
                int endHour = Integer.valueOf(StringUtils.substringBefore(endD, ":").replace(" ","").trim());
                int endMine = Integer.valueOf(StringUtils.substringAfter(endD, ":").replace(" ","").trim());
                //时长
                String longD = showB30LongSitTv.getText().toString().trim();
                int longTime = Integer.valueOf(StringUtils.substringBefore(longD, "min").replace(" ","").trim());
                MyApp.getInstance().getVpOperateManager().settingLongSeat(iBleWriteResponse, new LongSeatSetting(startHour, startMine, endHour, endMine, longTime, isToggChecked), new ILongSeatDataListener() {
                    @Override
                    public void onLongSeatDataChange(LongSeatData longSeatData) {
                        Log.e("久坐", "----longSeatData=" + longSeatData.toString());
//                        if (longSeatData.getStatus() == LongSeatOperater.LSStatus.OPEN_SUCCESS || longSeatData.getStatus() == LongSeatOperater.LSStatus.CLOSE_SUCCESS) {
//                            Toast.makeText(B30LongSitSetActivity.this, getResources().getString(R.string.settings_success), Toast.LENGTH_SHORT).show();
//                            finish();
//
//                        }
                        Log.e("久坐", "----longSeatData=" + longSeatData.toString());
                        if (longSeatData.getStatus() == ELongSeatStatus.OPEN_SUCCESS || longSeatData.getStatus() == ELongSeatStatus.CLOSE_SUCCESS) {
                            Toast.makeText(B30LongSitSetActivity.this,  getResources().getString(R.string.settings_success), Toast.LENGTH_SHORT).show();
                            finish();

                        }
                    }
                });
            }
        }catch (Error error){}

    }

    //设置时长
    private void chooseLongTime() {
        ProfessionPick dailyumberofstepsPopWin = new ProfessionPick.Builder(B30LongSitSetActivity.this, new ProfessionPick.OnProCityPickedListener() {
            @Override
            public void onProCityPickCompleted(String profession) {
                showB30LongSitTv.setText(profession + "min");
            }
        }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                .btnTextSize(16) // button text size
                .viewTextSize(25) // pick view text size
                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                .setProvinceList(longTimeLit) //min year in loop
                .dateChose("30") // date chose when init popwindow
                .build();
        dailyumberofstepsPopWin.showPopWin(B30LongSitSetActivity.this);
    }

    //TODO 设置久坐hour部分
    //设置久坐结束时间
    private void setLongSitHour() {
        ProfessionPick dailyumberofstepsPopWin = new ProfessionPick.Builder(B30LongSitSetActivity.this,
                new ProfessionPick.OnProCityPickedListener() {
                    @Override
                    public void onProCityPickCompleted(String profession) {
                        showB30LongSitEndTv.setText(profession+":00");
                       // SharedPreferencesUtils.setParam(getActivity(), "b30SleepGoal", profession.trim());
                    }
                }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                .btnTextSize(16) // button text size
                .viewTextSize(25) // pick view text size
                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                .setProvinceList(longSitHourList) //min year in loop
                .dateChose("8.0") // date chose when init popwindow
                .build();
        dailyumberofstepsPopWin.showPopWin(B30LongSitSetActivity.this);
    }



    //选择时间
    private void chooseStartEndDate(final int code) {
        ProvincePick starPopWin = new ProvincePick.Builder(B30LongSitSetActivity.this, new ProvincePick.OnProCityPickedListener() {
            @Override
            public void onProCityPickCompleted(String province, String city, String dateDesc) {
                if (code == 0) {  //开始时间
                    showB30LongSitStartTv.setText(province + ":" + city);
                }
                else if (code == 1) {    //结束时间
                    showB30LongSitEndTv.setText(province + ":" + city);

                }

            }
        }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                .btnTextSize(16) // button text size
                .viewTextSize(25) // pick view text size
                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                .setProvinceList(hourList) //min year in loop
                .setCityList(minuteMapList) // max year in loop
                .build();
        starPopWin.showPopWin(B30LongSitSetActivity.this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.longSitToggleBtn:
                isToggChecked = isChecked;
                break;
        }
    }

    private IBleWriteResponse iBleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int i) {

        }
    };
}
