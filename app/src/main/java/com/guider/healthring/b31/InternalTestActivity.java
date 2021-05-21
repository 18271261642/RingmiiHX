package com.guider.healthring.b31;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.guider.healthring.Commont;
import com.guider.healthring.MyApp;
import com.guider.healthring.R;
import com.guider.healthring.bleutil.MyCommandManager;
import com.guider.healthring.commdbserver.CommDBManager;
import com.guider.healthring.commdbserver.CommDownloadDb;
import com.guider.healthring.commdbserver.CommSleepDb;
import com.guider.healthring.commdbserver.CommStepCountDb;
import com.guider.healthring.commdbserver.SyncDbUrls;
import com.guider.healthring.siswatch.utils.WatchUtils;
import com.guider.healthring.util.OkHttpTool;
import com.guider.healthring.util.SharedPreferencesUtils;
import com.guider.healthring.util.ToastUtil;
import com.veepoo.protocol.listener.data.IAllHealthDataListener;
import com.veepoo.protocol.model.datas.OriginData;
import com.veepoo.protocol.model.datas.OriginHalfHourData;
import com.veepoo.protocol.model.datas.SleepData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Admin
 * Date 2019/10/14
 */
public class InternalTestActivity extends Activity implements View.OnClickListener{

    private static final String TAG = "InternalTestActivity";

    ImageView commentB30BackImg;
    TextView commentB30TitleTv;
    Button internalStartBtn;
    Button stopLocalBtn;
    Button saveDataBtn;
    private Button readDeviceDataBtn;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internal_layout);
        initViewIds();

        initViews();

    }

    private void initViewIds() {
        readDeviceDataBtn = findViewById(R.id.readDeviceDataBtn);
        commentB30BackImg = findViewById(R.id.commentB30BackImg);
        commentB30TitleTv = findViewById(R.id.commentB30TitleTv);
        internalStartBtn = findViewById(R.id.internalStartBtn);
        stopLocalBtn = findViewById(R.id.stopLocalBtn);
        saveDataBtn = findViewById(R.id.saveDataBtn);
        commentB30BackImg.setOnClickListener(this);
        internalStartBtn.setOnClickListener(this);
        stopLocalBtn.setOnClickListener(this);
        readDeviceDataBtn.setOnClickListener(this);
        saveDataBtn.setOnClickListener(this);
    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:
                finish();
                break;
            case R.id.internalStartBtn:
                ToastUtil.showToast(this, "111");
                findHeartData();
                break;
            case R.id.stopLocalBtn:
                findAllUploadData();
                break;
            case R.id.readDeviceDataBtn:
                readAllDeviceData();
                break;
            case R.id.saveDataBtn:
                saveStepData();
                break;
        }
    }


    private void saveStepData(){
        CommDBManager.getCommDBManager().saveCommCountStepDate("B31",MyApp.getInstance().getMacAddress(),WatchUtils.getCurrentDate(),8000);


        CommDBManager.getCommDBManager().saveCommCountStepDate("B31",MyApp.getInstance().getMacAddress(),WatchUtils.obtainFormatDate(1),6000);

        CommDBManager.getCommDBManager().saveCommCountStepDate("B31",MyApp.getInstance().getMacAddress(),WatchUtils.obtainFormatDate(4),5000);
    }




    private void readAllDeviceData(){
        if(MyCommandManager.DEVICENAME == null)
            return;
        MyApp.getInstance().getVpOperateManager().readAllHealthData(new IAllHealthDataListener() {
            @Override
            public void onProgress(float v) {
                Log.e(TAG,"-----onProgress="+v);
            }

            @Override
            public void onSleepDataChange(SleepData sleepData) {
                Log.e(TAG,"-----onSleepDataChange="+sleepData.toString());
            }

            @Override
            public void onReadSleepComplete() {
                Log.e(TAG,"-----onReadSleepComplete=");
            }

            @Override
            public void onOringinFiveMinuteDataChange(OriginData originData) {
                Log.e(TAG,"-----onOringinFiveMinuteDataChange=");
            }

            @Override
            public void onOringinHalfHourDataChange(OriginHalfHourData originHalfHourData) {
                Log.e(TAG,"-----onOringinHalfHourDataChange="+originHalfHourData.toString());
            }

            @Override
            public void onReadOriginComplete() {
                Log.e(TAG,"-----onReadOriginComplete=");
            }
        }, 2);
    }





    //查询所有需要上传的数据
    private void findAllUploadData() {

        CommDBManager.getCommDBManager().saveCommSleepDbData("B31", WatchUtils.getSherpBleMac(MyApp.getContext()), WatchUtils.getCurrentDate(),
                100, 200, 300, 400,
                "2021-05-19 23:50", "2021-05-20 07:50",
                1);

    }

    private void findHeartData() {
       // CommDBManager.getCommDBManager().startUploadDbService(InternalTestActivity.this);
        //查询血压
        List<CommSleepDb> bloodDb = CommDBManager.getCommDBManager().findCommSleepForUpload(MyApp.getInstance().getMacAddress(),
               "2021-05-10",WatchUtils.getCurrentDate());
        Log.e(TAG,"----blooddb="+(bloodDb == null)+new Gson().toJson(bloodDb));

        List<CommSleepDb> cb = CommDBManager.getCommDBManager().findCommSleepForUpload(MyApp.getInstance().getMacAddress(),WatchUtils.getCurrentDate());

        Log.e(TAG,"-----cb="+new Gson().toJson(cb));
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}
