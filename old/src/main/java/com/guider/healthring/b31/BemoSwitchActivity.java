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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Admin
 * Date 2019/10/24
 */
public class BemoSwitchActivity extends WatchBaseActivity implements CompoundButton.OnCheckedChangeListener {


    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.bemoSwitchToggleButton)
    ToggleButton bemoSwitchToggleButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bemo_switch);
        ButterKnife.bind(this);

        initViews();


        boolean isBemo = (boolean) SharedPreferencesUtils.getParam(BemoSwitchActivity.this,"bemo_switch",false);
        bemoSwitchToggleButton.setChecked(isBemo);


    }

    private void initViews() {
        commentB30TitleTv.setText(getResources().getString(R.string.bemo_switch));
        commentB30BackImg.setVisibility(View.VISIBLE);
        bemoSwitchToggleButton.setOnCheckedChangeListener(this);


    }

    @OnClick(R.id.commentB30BackImg)
    public void onClick() {
        finish();
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
