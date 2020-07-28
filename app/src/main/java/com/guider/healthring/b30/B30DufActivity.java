package com.guider.healthring.b30;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.guider.healthring.R;
import com.guider.healthring.siswatch.WatchBaseActivity;
import com.guider.healthring.util.ToastUtil;

public class B30DufActivity extends WatchBaseActivity implements View.OnClickListener{

    ImageView commentB30BackImg;
    TextView commentB30TitleTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b30_dfu);
        initViewIds();
        initViews();

    }

    private void initViewIds() {
        commentB30BackImg = findViewById(R.id.commentB30BackImg);
        commentB30TitleTv = findViewById(R.id.commentB30TitleTv);
        commentB30BackImg.setOnClickListener(this);
        findViewById(R.id.b30DufBtn).setOnClickListener(this);
    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(getResources().getString(R.string.firmware_upgrade));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:
                finish();
                break;
            case R.id.b30DufBtn:
                ToastUtil.showShort(B30DufActivity.this,getResources().getString(R.string.latest_version));
                break;
        }
    }
}
