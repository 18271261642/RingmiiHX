package com.guider.healthring.b31;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.guider.healthring.Commont;
import com.guider.healthring.MyApp;
import com.guider.healthring.R;
import com.guider.healthring.commdbserver.CommDBManager;
import com.guider.healthring.commdbserver.CommStepCountDb;
import com.guider.healthring.commdbserver.SyncDbUrls;
import com.guider.healthring.siswatch.utils.WatchUtils;
import com.guider.healthring.util.OkHttpTool;
import com.guider.healthring.util.SharedPreferencesUtils;
import com.guider.healthring.util.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Admin
 * Date 2019/10/14
 */
public class InternalTestActivity extends Activity  {

    private static final String TAG = "InternalTestActivity";

    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.internalStartBtn)
    Button internalStartBtn;
    @BindView(R.id.stopLocalBtn)
    Button stopLocalBtn;






    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internal_layout);
        ButterKnife.bind(this);

        initViews();

    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.commentB30BackImg, R.id.internalStartBtn, R.id.stopLocalBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:
                finish();
                break;
            case R.id.internalStartBtn:
                ToastUtil.showToast(this,"111");
                findHeartData();
                break;
            case R.id.stopLocalBtn:
                findAllUploadData();
                break;
        }
    }


    //查询所有需要上传的数据
    private void findAllUploadData() {
        //先查询下步数数据
        final List<CommStepCountDb> uploadStepList = CommDBManager.getCommDBManager().findCountStepForUpload(MyApp.getApplication().getMacAddress(), "2019-11-17", WatchUtils.getCurrentDate());
        if (uploadStepList == null || uploadStepList.isEmpty()){
           // downloadDb();
            return;
        }


        //用户的身高
        String userHeight = (String) SharedPreferencesUtils.getParam(this, Commont.USER_HEIGHT, "170");
        if (WatchUtils.isEmpty(userHeight))
            userHeight = "170";
        int uHeight = Integer.valueOf(userHeight.trim());
        //目标步数
        int goalStep = (int) SharedPreferencesUtils.getParam(this, "b30Goal", 8000);
        List<Map<String, String>> parmListMap = new ArrayList<>();
        for (CommStepCountDb countDb : uploadStepList) {
            //计算里程
            String dis = WatchUtils.getDistants(countDb.getStepnumber(), uHeight) + "";
            //卡路里
            String kcal = WatchUtils.getKcal(countDb.getStepnumber(), uHeight) + "";
            //Log.e(TAG, "--------查询需要上传的步数=" + countDb.toString());

            if (countDb.getDateStr().equals(WatchUtils.getCurrentDate()) || countDb.getDateStr().equals(WatchUtils.obtainFormatDate(1))) {
                Map<String, String> mp = new HashMap<>();
                mp.put("userid", countDb.getUserid());
                mp.put("stepnumber", countDb.getStepnumber() + "");
                mp.put("date", countDb.getDateStr());
                mp.put("devicecode", countDb.getDevicecode());
                mp.put("count", countDb.getCount() + "");
                mp.put("distance", dis);
                mp.put("calorie", kcal);
                mp.put("reach", (goalStep <= countDb.getStepnumber() ? 1 : 0) + "");
                parmListMap.add(mp);

            }

        }
        if (parmListMap.isEmpty()) {
            return;
        }

        String stepUloadUrl = SyncDbUrls.uploadCountStepUrl();
        String stepParmas =  new Gson().toJson(parmListMap);
        //Log.e(TAG, "--------步数---参数=" + stepParmas);

        OkHttpTool.getInstance().doRequest(stepUloadUrl, stepParmas, "step", new OkHttpTool.HttpResult() {
            @Override
            public void onResult(String result) {
                Log.e(TAG, "----------步数上传result=" + result);
                if (WatchUtils.isEmpty(result))
                    return;
//                if (WatchUtils.isNetRequestSuccess(result))
//                    updateUploadCountSteps(uploadStepList);

            }
        });


    }

    private void findHeartData() {
        CommDBManager.getCommDBManager().startUploadDbService(InternalTestActivity.this);

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
