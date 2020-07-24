package com.guider.healthring.B18I.b18isystemic;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.guider.healthring.R;
import com.guider.healthring.siswatch.WatchBaseActivity;

/**
 * @aboutContent: 添加好友
 * @author： 安
 * @crateTime: 2017/9/19 11:30
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class FindFriendActivity extends WatchBaseActivity implements  View.OnClickListener{
    private static final String TAG = "--FindFriendActivity";
    TextView barTitles;
    EditText numberEdit;
    ListView frendsList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_frends_layout);
        initViewIds();
        barTitles.setText(getResources().getString(R.string.add_frendes));
        whichDevice();//判断是B18i还是H9
    }

    private void initViewIds() {
        barTitles = findViewById(R.id.bar_titles);
        numberEdit = findViewById(R.id.number_edit);
        frendsList = findViewById(R.id.frends_list);
        findViewById(R.id.image_back).setOnClickListener(this);
        findViewById(R.id.scan_text).setOnClickListener(this);
    }

    private String is18i;

    //判断是B18i还是H9
    private void whichDevice() {
        is18i = getIntent().getStringExtra("is18i");
        if (TextUtils.isEmpty(is18i)) finish();
        switch (is18i){
            case "B18i":
                break;
            case "H9":
                break;
            case "B15P":
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.scan_text://搜索
                String content = numberEdit.getText().toString();
                break;
        }
    }


    /**
     * 内部Adapter
     */
    public class FrendsListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            return null;
        }
    }
}
