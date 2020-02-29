package com.guider.glu;

import android.os.Bundle;
import android.util.Log;

import com.guider.glu.view.GLUChooseTime;
import com.guider.health.bluetooth.core.BleBluetooth;
import com.guider.health.common.core.BaseActivity;
import com.guider.health.common.core.BaseFragment;

public class MainActivity extends BaseActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        BleBluetooth.getInstance().init(this);


        BaseFragment fragment = findFragment(GLUChooseTime.class);
        Log.i("haix", "fragment: "+fragment);
        if (fragment == null) {
            loadRootFragment(R.id.main_content, new GLUChooseTime());
        }


    }

}
