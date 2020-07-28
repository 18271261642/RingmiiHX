package com.guider.healthring.w30s.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.guider.healthring.R;
import com.guider.healthring.siswatch.WatchBaseActivity;

/**
 * @aboutContent:
 * @author： An
 * @crateTime: 2018/3/9 18:07
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class NewFindFriendActivity extends WatchBaseActivity {

    TextView barTitles;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friend);
        barTitles =findViewById(R.id.bar_titles);
        findViewById(R.id.image_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        barTitles.setText(getResources().getString(R.string.string_new_friends));
    }
}
