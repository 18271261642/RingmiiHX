package com.guider.healthring.h9.settingactivity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.guider.healthring.R;
import com.guider.healthring.siswatch.WatchBaseActivity;

/**
 * @aboutContent: 校针说明
 * @author： 安
 * @crateTime: 2017/10/20 11:47
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class H9GuideActivity extends WatchBaseActivity {

    TextView barTitles;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.h9_guide_layout);
        barTitles =findViewById(R.id.bar_titles);
        findViewById(R.id.image_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        barTitles.setText(getResources().getString(R.string.guide));
    }
}
