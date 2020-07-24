package com.guider.healthring.B18I.b18isystemic;


import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aigestudio.wheelpicker.widgets.ProfessionPick;
import com.guider.healthring.B18I.B18iCommon;
import com.guider.healthring.B18I.evententity.B18iEventBus;
import com.guider.healthring.MyApp;
import com.guider.healthring.R;
import com.guider.healthring.siswatch.NewSearchActivity;
import com.guider.healthring.siswatch.WatchBaseActivity;
import com.guider.healthring.siswatch.utils.WatchUtils;
import com.guider.healthring.util.SharedPreferencesUtils;
import com.sdk.bluetooth.manage.AppsBluetoothManager;
import com.sdk.bluetooth.manage.GlobalVarManager;
import com.sdk.bluetooth.protocol.command.base.BaseCommand;
import com.sdk.bluetooth.protocol.command.base.CommandConstant;
import com.sdk.bluetooth.protocol.command.setting.GoalsSetting;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferenceUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;

import cn.appscomm.bluetooth.app.BluetoothSDK;
import cn.appscomm.bluetooth.interfaces.ResultCallBack;


/**
 * @aboutContent: 目标设置
 * @author： 安
 * @crateTime: 2017/9/5 08:58
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class B18ITargetSettingActivity extends WatchBaseActivity implements  View.OnClickListener{
    public final String TAG = "----->>>" + this.getClass();
    TextView stepNumbers;
    TextView caloriesBurns;
    TextView sleepTime;
    TextView targetDistances;
    TextView ischangeNo;
    TextView ischangeOk;
    LinearLayout stepsNumber;
    LinearLayout caloriesBurn;
    LinearLayout sleepingTime;
    LinearLayout targetDistance;
    View yitiaoxian;
    ImageView imageFanhui;


    private int TSTEP = 1000;
    private int TKCAL = 50;
    private int TDIS = 10;
    private int TSLEEP = 10;
    private String is18i;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b18i_target_setting_layout);
        initViewIds();
        whichDevice();//判断是B18i还是H9
//        whichDevice();//判断是B18i还是H9
    }

    private void initViewIds() {
        stepNumbers = findViewById(R.id.step_Number);
        caloriesBurns = findViewById(R.id.calories_Burn);
        sleepTime = findViewById(R.id.sleep_Time);
        targetDistances = findViewById(R.id.target_Distance);
        ischangeNo = findViewById(R.id.ischange_no);
        ischangeOk = findViewById(R.id.ischange_ok);
        stepsNumber = findViewById(R.id.stepsNumber);
        caloriesBurn = findViewById(R.id.caloriesBurn);
        sleepingTime = findViewById(R.id.sleepingTime);
        targetDistance = findViewById(R.id.targetDistance);
        yitiaoxian = findViewById(R.id.yitiaoxian);
        imageFanhui = findViewById(R.id.image_fanhui);
        stepsNumber.setOnClickListener(this);
        caloriesBurn.setOnClickListener(this);
        sleepingTime.setOnClickListener(this);
        targetDistance.setOnClickListener(this);
        ischangeNo.setOnClickListener(this);
        ischangeOk.setOnClickListener(this);
        imageFanhui.setOnClickListener(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onB18iEventBus(B18iEventBus event) {
        switch (event.getName()) {
            case "STATE_ON":
                startActivity(NewSearchActivity.class);
                finish();
                break;
            case "STATE_TURNING_ON":
                break;
            case "STATE_OFF":
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                enableBtIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(enableBtIntent);
                break;
            case "STATE_TURNING_OFF":
                Toast.makeText(this, getResources().getString(R.string.bluetooth_disconnected), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        initStepList();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    ///判断是B18i还是H9
    private void whichDevice() {
        is18i = getIntent().getStringExtra("is18i");
        if (TextUtils.isEmpty(is18i)) finish();
        switch (is18i) {
            case "B18i":
                initGetB18iTar();
                break;
            case "H9":
                initGetH9Tar();
                break;
//            case "B15P":
//                ischangeOk.setVisibility(View.GONE);
//                ischangeNo.setVisibility(View.GONE);
//                stepsNumber.setEnabled(false);
//                caloriesBurn.setEnabled(false);
//                yitiaoxian.setVisibility(View.GONE);
//                sleepingTime.setVisibility(View.GONE);
//                targetDistance.setEnabled(false);
//                imageFanhui.setVisibility(View.VISIBLE);
//                initGetB15pTar();
//                break;
            case "W30S":
                caloriesBurn.setVisibility(View.GONE);
                targetDistance.setVisibility(View.GONE);
                //获取目标步数
                String w30stag = (String) SharedPreferenceUtil.get(this, "w30stag", "10000");
                //获取目标睡眠
                String w30ssleep = (String) SharedPreferenceUtil.get(this, "w30ssleep", "8");
                stepNumbers.setText(w30stag);
                sleepTime.setText(w30ssleep);
                break;
        }
    }

//    ESex sex = ESex.MAN;
//    int weight = 60;
//    int height = 170;
//
//    private void initGetB15pTar() {
//        String aimTip = "根据性别、身高、体重算出来的目标值,是否采用新的计算方法=" + false;
//        String userheight = (String) SharedPreferencesUtils.getParam(this, "userheight", "170");
//        String userweight = (String) SharedPreferencesUtils.getParam(this, "userweight", "60");
//        String param = (String) SharedPreferencesUtils.getParam(this, "usersex", "M");
//        if (param.equals("M")) {
//            sex = ESex.MAN;
//        } else {
//            sex = ESex.WOMEN;
//        }
//        weight = Integer.parseInt(userweight);
//        height = Integer.parseInt(userheight);
//        if (sex == null) {
//            sex = ESex.MAN;
//        }
//        if (weight < 0) {
//            weight = 60;
//        }
//        if (height < 0) {
//            height = 170;
//        }
//        String aimSportCount = "" + SportUtil.getAimSportCount(sex, weight, height);
//        String aimDistance = "" + SportUtil.getAimDistance(sex, weight, height);
//        String aimKcalNew = "" + SportUtil.getAimKcal(sex, weight, height, false);
//        stepNumbers.setText(aimSportCount);
//        caloriesBurns.setText(aimKcalNew);
//        targetDistances.setText(aimDistance);
//    }

    //获取H9的目标设置
    private void initGetH9Tar() {
        showLoadingDialog(getResources().getString(R.string.dlog));
        AppsBluetoothManager.getInstance(MyApp.getContext())
                .sendCommand(new GoalsSetting(new BaseCommand.CommandResultCallback() {
                    @Override
                    public void onSuccess(BaseCommand command) {
                        Log.d(TAG, "步数目标:" + GlobalVarManager.getInstance().getStepGoalsValue() + "\n" +
                                "卡路里目标:" + GlobalVarManager.getInstance().getCalorieGoalsValue() + "\n" +
                                "距离目标:" + GlobalVarManager.getInstance().getDistanceGoalsValue() + "\n" +
                                "睡眠时间目标:" + GlobalVarManager.getInstance().getSleepGoalsValue());

                        TSTEP = (int) GlobalVarManager.getInstance().getStepGoalsValue();
                        TKCAL = (int) GlobalVarManager.getInstance().getCalorieGoalsValue();
                        TDIS = (int) GlobalVarManager.getInstance().getDistanceGoalsValue();
                        TSLEEP = (int) GlobalVarManager.getInstance().getSleepGoalsValue();
                        stepNumbers.setText(String.valueOf(TSTEP));
                        caloriesBurns.setText(String.valueOf(TKCAL));
                        sleepTime.setText(String.valueOf(TSLEEP));
                        targetDistances.setText(String.valueOf(TDIS));
                        SharedPreferencesUtils.setParam(B18ITargetSettingActivity.this, "StepNumber", String.valueOf(TSTEP));
                        SharedPreferencesUtils.setParam(B18ITargetSettingActivity.this, "caloriesData", String.valueOf(TKCAL));
                        SharedPreferencesUtils.setParam(B18ITargetSettingActivity.this, "sleepData", String.valueOf(TSLEEP));
                        SharedPreferencesUtils.setParam(B18ITargetSettingActivity.this, "distanionData", String.valueOf(TDIS));
                        closeLoadingDialog();
                    }

                    @Override
                    public void onFail(BaseCommand baseCommand) {
                        Log.d(TAG, "获取目标设置失败");
                        closeLoadingDialog();
                        if (baseCommand.getAction() == CommandConstant.ACTION_CHECK) {
                            Toast.makeText(B18ITargetSettingActivity.this, getResources().getString(R.string.get_fail), Toast.LENGTH_SHORT).show();
                            finish();
                        } else if (baseCommand.getAction() == CommandConstant.ACTION_SET) {
                            Toast.makeText(B18ITargetSettingActivity.this, getResources().getString(R.string.settings_fail), Toast.LENGTH_SHORT).show();
                        }
                    }
                }));
    }

    private void initGetB18iTar() {
        BluetoothSDK.getGoalSetting(resultCallBack);//获取目标
    }

    ArrayList<String> daily_numberofstepsList;
    ArrayList<String> sleepData;
    ArrayList<String> caloliesData;
    ArrayList<String> disData;

    private void initStepList() {
        daily_numberofstepsList = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            daily_numberofstepsList.add(String.valueOf(i * 1000));
        }
        disData = new ArrayList<>();
        for (int i = 0; i <= 100; i++) {
            disData.add(String.valueOf(i));
        }
        caloliesData = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            caloliesData.add(String.valueOf(i * 5));
        }
        sleepData = new ArrayList<>();
        for (int i = 0; i <= 24; i++) {
            sleepData.add(String.valueOf(i));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.stepsNumber:
                setStepNumber();
                break;
            case R.id.caloriesBurn:
                setCalories();
                break;
            case R.id.sleepingTime:
                setSleepTime();
                break;
            case R.id.targetDistance:
                setDistancion();
                break;
            case R.id.ischange_no:
                finish();
                break;
            case R.id.ischange_ok:
                B18iCommon.ISCHECKTARGET = true;
                String step = (String) SharedPreferencesUtils.getParam(B18ITargetSettingActivity.this, "StepNumber", "7000");
                String calories = (String) SharedPreferencesUtils.getParam(B18ITargetSettingActivity.this, "caloriesData", "");
                String sleep = (String) SharedPreferencesUtils.getParam(B18ITargetSettingActivity.this, "sleepData", "8");
                String distation = (String) SharedPreferencesUtils.getParam(B18ITargetSettingActivity.this, "distanionData", "");
                if (is18i.equals("B18i")) {
                    if (!String.valueOf(TSTEP).equals(step)) {
                        BluetoothSDK.setStepGoal(resultCallBack, Integer.valueOf(step));//目标步数
                    }
                    if (!String.valueOf(TKCAL).equals(calories)) {
                        BluetoothSDK.setCaloriesGoal(resultCallBack, Integer.valueOf(calories));//消耗
                    }
                    if (!String.valueOf(TDIS).equals(distation)) {
                        BluetoothSDK.setDistanceGoal(resultCallBack, Integer.valueOf(distation));//距离
                    }
                    if (!String.valueOf(TSLEEP).equals(sleep)) {
                        BluetoothSDK.setSleepGoal(resultCallBack, Integer.valueOf(sleep));//睡眠时间
                    }
                } else if (is18i.equals("W30S")) {
                    //showLoadingDialog(getResources().getString(R.string.dlog));


                    if (String.valueOf(TSTEP).equals(step) && String.valueOf(TSLEEP).equals(sleep)) {
                        finish();
                        return;
                    } else {
                        if (!String.valueOf(TSTEP).equals(step)) {
                            if (!WatchUtils.isEmpty(step)) {
                                //目标步数
                                SharedPreferenceUtil.put(this, "w30stag", step);
                                MyApp.getmW30SBLEManage().setTargetStep(Integer.valueOf(step));
                            }
                        }
                        if (!String.valueOf(TSLEEP).equals(sleep)) {
                            if (!WatchUtils.isEmpty(sleep)) {
                                //目标睡眠
                                SharedPreferenceUtil.put(this, "w30ssleep", sleep);
                            }
                        }
                        finish();
                    }
                    SharedPreferencesUtils.setParam(B18ITargetSettingActivity.this, "StepNumber", "");
                    SharedPreferencesUtils.setParam(B18ITargetSettingActivity.this, "caloriesData", "");
                    SharedPreferencesUtils.setParam(B18ITargetSettingActivity.this, "sleepData", "");
                    SharedPreferencesUtils.setParam(B18ITargetSettingActivity.this, "distanionData", "");
                } else {

                    /*
                      String step = (String) SharedPreferencesUtils.getParam(B18ITargetSettingActivity.this, "StepNumber", "");
                String calories = (String) SharedPreferencesUtils.getParam(B18ITargetSettingActivity.this, "caloriesData", "");
                String sleep = (String) SharedPreferencesUtils.getParam(B18ITargetSettingActivity.this, "sleepData", "");
                String distation = (String) SharedPreferencesUtils.getParam(B18ITargetSettingActivity.this, "distanionData", "");
                    * */
                    //设置H9目标设置的在这里
                    if (String.valueOf(TSTEP).equals(step) && String.valueOf(TKCAL).equals(calories)
                            && String.valueOf(TDIS).equals(distation) && String.valueOf(TSLEEP).equals(sleep)) {
                        finish();
                    } else {
                        showLoadingDialog(getResources().getString(R.string.dlog));
                        if (!String.valueOf(TSTEP).equals(step)) {
                            int st = Integer.valueOf(step) / 100;
                            // 50*100
                            AppsBluetoothManager.getInstance(MyApp.getContext())
                                    .sendCommand(new GoalsSetting(commandResultCallback, (byte) 0, st, (byte) 0));//目标步数
                        }
                        if (!String.valueOf(TKCAL).equals(calories)) {
                            AppsBluetoothManager.getInstance(MyApp.getContext())
                                    .sendCommand(new GoalsSetting(commandResultCallback, (byte) 1, Integer.valueOf(calories), (byte) 0));//卡路里
                        }
                        if (!String.valueOf(TDIS).equals(distation)) {
                            AppsBluetoothManager.getInstance(MyApp.getContext())
                                    .sendCommand(new GoalsSetting(commandResultCallback, (byte) 2, Integer.valueOf(distation), (byte) 0));//距离
                        }
                        if (!String.valueOf(TSLEEP).equals(sleep)) {
                            AppsBluetoothManager.getInstance(MyApp.getContext())
                                    .sendCommand(new GoalsSetting(commandResultCallback, (byte) 3, Integer.valueOf(sleep), (byte) 0));//睡眠时间
                        }
                        SharedPreferencesUtils.setParam(B18ITargetSettingActivity.this, "StepNumber", "");
                        SharedPreferencesUtils.setParam(B18ITargetSettingActivity.this, "caloriesData", "");
                        SharedPreferencesUtils.setParam(B18ITargetSettingActivity.this, "sleepData", "");
                        SharedPreferencesUtils.setParam(B18ITargetSettingActivity.this, "distanionData", "");
                    }
                }
                break;
            case R.id.image_fanhui:
                finish();
                break;
        }
    }

    /**
     * H9目标设置回调
     */
    private BaseCommand.CommandResultCallback commandResultCallback = new BaseCommand.CommandResultCallback() {
        @Override
        public void onSuccess(BaseCommand baseCommand) {
            if (baseCommand instanceof GoalsSetting) {
                Log.d(TAG, "目标设置成功");
                closeLoadingDialog();
                finish();
            }
        }

        @Override
        public void onFail(BaseCommand baseCommand) {
            Log.d(TAG, "目标设置失败");
            Toast.makeText(B18ITargetSettingActivity.this, "目标设置失败", Toast.LENGTH_SHORT).show();
            closeLoadingDialog();
            finish();
        }
    };

    /**
     * 设置距离
     */
    private void setDistancion() {
        ProfessionPick caloriesburn = new ProfessionPick.Builder(B18ITargetSettingActivity.this, new ProfessionPick.OnProCityPickedListener() {
            @Override
            public void onProCityPickCompleted(String profession) {
                targetDistances.setText(profession);
                SharedPreferencesUtils.setParam(B18ITargetSettingActivity.this, "distanionData", profession);
            }
        }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                .btnTextSize(16) // button text size
                .viewTextSize(25) // pick view text size
                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                .setProvinceList(disData) //min year in loop
                .dateChose(String.valueOf(TDIS)) // date chose when init popwindow
                .build();
        caloriesburn.showPopWin(B18ITargetSettingActivity.this);
    }

    /**
     * 设置睡眠时间
     */
    private void setSleepTime() {
        String mSleep = "0";
        if (is18i.equals("W30S")) {
            //获取目标步数
            mSleep = (String) SharedPreferenceUtil.get(this, "w30ssleep", "8");
        } else {
            mSleep = String.valueOf(TSLEEP);
        }
        ProfessionPick sleepingtime = new ProfessionPick.Builder(B18ITargetSettingActivity.this, new ProfessionPick.OnProCityPickedListener() {
            @Override
            public void onProCityPickCompleted(String profession) {
                SharedPreferencesUtils.setParam(B18ITargetSettingActivity.this, "sleepData", profession);
                sleepTime.setText(profession);
            }
        }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                .btnTextSize(16) // button text size
                .viewTextSize(25) // pick view text size
                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                .setProvinceList(sleepData) //min year in loop
                .dateChose(mSleep) // date chose when init popwindow
                .build();
        sleepingtime.showPopWin(B18ITargetSettingActivity.this);
    }

    /**
     * 设置卡路里
     */
    private void setCalories() {
        ProfessionPick caloriesburn = new ProfessionPick.Builder(B18ITargetSettingActivity.this, new ProfessionPick.OnProCityPickedListener() {
            @Override
            public void onProCityPickCompleted(String profession) {
                SharedPreferencesUtils.setParam(B18ITargetSettingActivity.this, "caloriesData", profession);
                caloriesBurns.setText(profession);
            }
        }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                .btnTextSize(16) // button text size
                .viewTextSize(25) // pick view text size
                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                .setProvinceList(caloliesData) //min year in loop
                .dateChose(String.valueOf(TKCAL)) // date chose when init popwindow
                .build();
        caloriesburn.showPopWin(B18ITargetSettingActivity.this);
    }

    /**
     * 设置步数
     */
    private void setStepNumber() {
        String mStep = "1000";
        if (is18i.equals("W30S")) {
            //获取目标步数
            mStep = (String) SharedPreferenceUtil.get(this, "w30stag", "10000");
        } else {
            mStep = String.valueOf(TSTEP);
        }
        ProfessionPick stepsnumber = new ProfessionPick.Builder(B18ITargetSettingActivity.this, new ProfessionPick.OnProCityPickedListener() {
            @Override
            public void onProCityPickCompleted(String profession) {
                //设置步数
                SharedPreferencesUtils.setParam(B18ITargetSettingActivity.this, "StepNumber", profession);
                stepNumbers.setText(profession);
            }
        }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                .btnTextSize(16) // button text size
                .viewTextSize(25) // pick view text size
                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                .setProvinceList(daily_numberofstepsList) //min year in loop
                .dateChose(mStep) // date chose when init popwindow
                .build();
        stepsnumber.showPopWin(B18ITargetSettingActivity.this);
    }

    /**
     * 38i目标设置回调
     */
    ResultCallBack resultCallBack = new ResultCallBack() {
        @Override
        public void onSuccess(int i, Object[] objects) {
            switch (i) {
                case ResultCallBack.TYPE_SET_STEP_GOAL:
                    Log.i(TAG, "步数目标设置成");
                    break;
                case ResultCallBack.TYPE_SET_CALORIES_GOAL:
                    Log.i(TAG, "卡路里目标设置成");
                    break;
                case ResultCallBack.TYPE_SET_SLEEP_GOAL:
                    Log.i(TAG, "睡眠目标设置成");
                    break;
                case ResultCallBack.TYPE_SET_DISTANCE_GOAL:
                    Log.i(TAG, "距离目标设置成");
                    break;
                case ResultCallBack.TYPE_GET_GOAL_SETTING:
                    Log.i(TAG, "goal : " + Arrays.toString(objects));
                    TSTEP = (int) objects[0];
                    TKCAL = (int) objects[1];
                    TDIS = (int) objects[2];
                    TSLEEP = (int) objects[3];
                    stepNumbers.setText(String.valueOf(TSTEP));
                    caloriesBurns.setText(String.valueOf(TKCAL));
                    sleepTime.setText(String.valueOf(TDIS));
                    targetDistances.setText(String.valueOf(TSLEEP));
                    SharedPreferencesUtils.setParam(B18ITargetSettingActivity.this, "StepNumber", String.valueOf(TSTEP));
                    SharedPreferencesUtils.setParam(B18ITargetSettingActivity.this, "caloriesData", String.valueOf(TKCAL));
                    SharedPreferencesUtils.setParam(B18ITargetSettingActivity.this, "sleepData", String.valueOf(TDIS));
                    SharedPreferencesUtils.setParam(B18ITargetSettingActivity.this, "distanionData", String.valueOf(TSLEEP));
                    break;
            }
        }

        @Override
        public void onFail(int i) {
        }
    };

}
