package com.guider.healthring.B18I.b18isystemic;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.guider.healthring.R;

/**
 * @aboutContent: 个人资料
 * @author： 安
 * @crateTime: 2017/9/19 14:39
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class MinePersonDataActivity extends AppCompatActivity {
    private static final String TAG = "--MinePersonDataActivity";
    TextView barTitles;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_person_data_layout);
        barTitles = findViewById(R.id.bar_titles);
        findViewById(R.id.image_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        barTitles.setText(getResources().getString(R.string.personal_str));
        whichDevice();//判断是B18i还是H9
    }

    private String is18i;

    //判断是B18i还是H9
    private void whichDevice() {
        is18i = getIntent().getStringExtra("is18i");
        if (TextUtils.isEmpty(is18i)) finish();
        //在这里分别请求数据
//        if (is18i.equals("B18i")) {
//            initGetB18iTar();
//        } else {
//            initGetH9Tar();
//        }
    }
}