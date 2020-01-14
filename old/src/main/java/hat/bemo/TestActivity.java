package hat.bemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.guider.healthring.R;

public class TestActivity extends AppCompatActivity {
    String imei;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);



//        SharedPreferences_status.save_IMEI(MyApplication.context,imei);


        findViewById(R.id.test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imei = SystemUtil.getIMEI(TestActivity.this);
                if (TextUtils.isEmpty(imei)) return;
                Log.e("===IMEI ",imei);


                Intent intent = new Intent(TestActivity.this, MainActivity.class);
                intent.setFlags(101);//傳送設備碼
                intent.putExtra("data", imei);
                startActivity(intent);

                //要调用另一个APP的activity所在的包名
//        String packageName = "hat.bemo";
                //要调用另一个APP的activity名字
//        String activity = "hat.bemo.MainActivity”;
//        ComponentName component = new ComponentName(packageName, activity);

//        intent.setComponent(component);
                //傳送旗標
//                intent.setFlags(101);//傳送設備碼
//                intent.putExtra("data", imei);
//                startActivity(intent);
            }
        });
    }

}
