package com.guider.health.ecg.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Toast;

import com.guider.health.common.core.BaseActivity;
import com.guider.health.common.core.BaseFragment;
import com.guider.health.common.core.HealthRange;
import com.guider.health.ecg.R;

/**
 * Created by haix on 2019/7/4.
 */

public class ECGConnectAndMeassureActivity extends BaseActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        HealthRange healthRange = getIntent().getParcelableExtra("HealthRange");
        HealthRange.getInstance().toHealthRange(healthRange);

        setContentView(R.layout.egc_activity_main);

        BaseFragment fragment = findFragment(ECGDeviceConnectAndMeasure.class);
        if (fragment == null) {
            loadRootFragment(R.id.main_content, new ECGDeviceConnectAndMeasure());
//

        }

    }

}
