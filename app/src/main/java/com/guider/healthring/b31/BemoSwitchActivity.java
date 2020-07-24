package com.guider.healthring.b31;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.guider.healthring.R;
import com.guider.healthring.siswatch.WatchBaseActivity;
import com.guider.healthring.util.SharedPreferencesUtils;


/**
 * Created by Admin
 * Date 2019/10/24
 */
public class BemoSwitchActivity extends WatchBaseActivity implements CompoundButton.OnCheckedChangeListener {


    ImageView commentB30BackImg;
    TextView commentB30TitleTv;
    ToggleButton bemoSwitchToggleButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bemo_switch);
        initViewIds();

        initViews();


        boolean isBemo = (boolean) SharedPreferencesUtils.getParam(BemoSwitchActivity.this,"bemo_switch",false);
        bemoSwitchToggleButton.setChecked(isBemo);


    }

    private void initViewIds() {
        commentB30BackImg = findViewById(R.id.commentB30BackImg);
        commentB30TitleTv = findViewById(R.id.commentB30TitleTv);
        bemoSwitchToggleButton = findViewById(R.id.bemoSwitchToggleButton);
        commentB30BackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initViews() {
        commentB30TitleTv.setText(getResources().getString(R.string.bemo_switch));
        commentB30BackImg.setVisibility(View.VISIBLE);
        bemoSwitchToggleButton.setOnCheckedChangeListener(this);


    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!buttonView.isPressed())
            return;
        switch (buttonView.getId()){
            case R.id.bemoSwitchToggleButton:
                SharedPreferencesUtils.setParam(BemoSwitchActivity.this,"bemo_switch",isChecked);
                break;
        }
    }
}
