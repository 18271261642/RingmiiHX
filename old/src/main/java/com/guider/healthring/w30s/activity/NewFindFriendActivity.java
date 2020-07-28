package com.guider.healthring.w30s.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.widget.ListView;
import android.widget.TextView;

import com.guider.healthring.R;
import com.guider.healthring.siswatch.WatchBaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @aboutContent:
 * @author： An
 * @crateTime: 2018/3/9 18:07
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class NewFindFriendActivity extends WatchBaseActivity {


    @BindView(R.id.bar_titles)
    TextView barTitles;
    @BindView(R.id.w30s_listView)
    ListView w30sListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friend);
        ButterKnife.bind(this);
        barTitles.setText(getResources().getString(R.string.string_new_friends));
    }

    @OnClick(R.id.image_back)
    public void onViewClicked() {
        finish();
    }
}
