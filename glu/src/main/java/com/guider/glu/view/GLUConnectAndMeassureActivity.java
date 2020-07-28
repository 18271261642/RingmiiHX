package com.guider.glu.view;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.WindowManager;

import com.guider.glu.R;
import com.guider.glu.model.BodyIndex;
import com.guider.health.common.core.BaseActivity;
import com.guider.health.common.core.BaseFragment;
import com.guider.health.common.core.Glucose;
import com.guider.health.common.core.HealthRange;

/**
 * Created by haix on 2019/7/5.
 */

public class GLUConnectAndMeassureActivity extends BaseActivity{


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.glu_connect_measure_activity);

        BodyIndex bodyIndex = getIntent().getParcelableExtra("body");
        int timeTime = getIntent().getIntExtra("footTime", 0);
        Glucose.getInstance().setFoodTime(timeTime);
        HealthRange healthRange = getIntent().getParcelableExtra("HealthRange");
        HealthRange.getInstance().toHealthRange(healthRange);

        if (bodyIndex == null){
            return;
        }
        BodyIndex.getInstance().setBiguanidesState(bodyIndex.getBiguanidesState());
        BodyIndex.getInstance().setDiabetesType(bodyIndex.getDiabetesType());
        BodyIndex.getInstance().setGlucose(bodyIndex.getGlucose());
        BodyIndex.getInstance().setTimeMeal(bodyIndex.getTimeMeal());
        BodyIndex.getInstance().setWeigh(bodyIndex.getWeigh());
        BodyIndex.getInstance().setHeight(bodyIndex.getHeight());
        BodyIndex.getInstance().setSulphonylureasState(bodyIndex.getSulphonylureasState());
        BodyIndex.getInstance().setGlucosedesesSate(bodyIndex.getGlucosedesesSate());

        BaseFragment fragment = findFragment(GLUDeviceConnect.class);
        if (fragment == null) {
            if (bodyIndex != null){

                loadRootFragment(R.id.main_content, new GLUDeviceConnect());
            }

        }
    }


    long firstTime = 0;// 上一次按回退键的时间

}
