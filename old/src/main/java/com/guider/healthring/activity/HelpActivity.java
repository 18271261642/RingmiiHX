package com.guider.healthring.activity;

import android.widget.TextView;

import com.guider.healthring.R;
import com.guider.healthring.base.BaseActivity;

import butterknife.BindView;

/**
 * Created by admin on 2017/5/5.
 * 帮助类
 */

public class HelpActivity extends BaseActivity{

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @Override
    protected void initViews() {
        tvTitle.setText(getResources().getString(R.string.help));
    }

    @Override
    protected int getContentViewId() {return R.layout.activity_help;}

    @Override
    protected int getStatusBarColor() {
        return super.getStatusBarColor();
    }
}
