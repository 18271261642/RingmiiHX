package com.guider.healthring.siswatch;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.guider.healthring.R;
import com.guider.healthring.bean.MessageEvent;
import com.guider.healthring.bleutil.Customdata;
import com.guider.healthring.siswatch.utils.WatchUtils;
import com.guider.healthring.siswatch.utils.test.TimeInterface;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by Administrator on 2017/10/26.
 */

public class GetWatchTimeActivity extends WatchBaseActivity implements TimeInterface {

    TextView tvTitle;
    Toolbar toolbar;
    TextView showWatchTimeTv;
    TextView showAnalysisTimeTv;
    TextView showSysTimeTv;
    Button searchWatchBtn;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1001:
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    showSysTimeTv.setText(sdf.format(new Date(System.currentTimeMillis())));
                    break;
            }
        }
    };



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_watchtime);
        initViewIds();
        EventBus.getDefault().register(this);
        EventBus.getDefault().post(new MessageEvent("getWatchTime"));

        initViews();
        new TimeThread().start();   //获取当前系统时间
//        MyApp.getWatchBluetoothService().setTimeInterface(this);


    }

    private void initViewIds(){
        tvTitle = findViewById(R.id.tv_title);
        toolbar = findViewById(R.id.toolbar);
        showWatchTimeTv = findViewById(R.id.showWatchTimeTv);
        showAnalysisTimeTv = findViewById(R.id.showAnalysisTimeTv);
        showSysTimeTv = findViewById(R.id.showSysTimeTv);
        searchWatchBtn = findViewById(R.id.searchWatchBtn);
        searchWatchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onViewClicked();
            }
        });

    }

    @Override
    public void getWatchTime(Object o) {
        Log.e("GETQW", "------jiekou------" + Arrays.toString((byte[]) o));
    }

    public void onViewClicked() {
        EventBus.getDefault().post(new MessageEvent("laidianphone"));
    }

    class TimeThread extends Thread {
        @Override
        public void run() {
            super.run();
            do {
                try {
                    Thread.sleep(1000);
                    Message message = new Message();
                    message.what = 1001;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (true);
        }
    }

    private void initViews() {
        tvTitle.setText("获取手表的时间");
        toolbar.setNavigationIcon(R.mipmap.backs);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event) {
        String result = event.getMessage();
        if (!WatchUtils.isEmpty(result)) {
            Log.e("GETWATCH", "-----result-----" + result);
            if (result.equals("rebackWatchTime")) {
                byte[] watchTimeData = (byte[]) event.getObject();
                showWatchTimeTv.setText(Customdata.bytes2HexString(watchTimeData) + "-" + Arrays.toString(watchTimeData));
                for (int i = 0; i < watchTimeData.length; i++) {

                }
                showAnalysisTimeTv.setText(String.valueOf(20) + watchTimeData[6] + "-" + watchTimeData[7] + "-" + watchTimeData[8] + " " + watchTimeData[9] + ":" + watchTimeData[10] + ":" + watchTimeData[11]);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
