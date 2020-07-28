package com.guider.healthring.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.guider.healthring.R;
import com.guider.healthring.siswatch.WatchBaseActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Admin
 * Date 2019/10/21
 */
public class GuiderDeviceActivity extends WatchBaseActivity {

    private static final String TAG = "GuiderDeviceActivity";


    ImageView commentB30BackImg;
    TextView commentB30TitleTv;
    RecyclerView guiderDeviceRecyclerView;

    private List<Map<String,String>> guiderList ;
    private GuiderDeviceAdapter guiderDeviceAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guider_device);
        initViewIds();

        initViews();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("hat.bemo.BlueTooth.blegatt.device");
        registerReceiver(broadcastReceiver,intentFilter);

    }

    private void initViewIds() {
        commentB30BackImg = findViewById(R.id.commentB30BackImg);
        commentB30BackImg.setOnClickListener(v -> finish());
        commentB30TitleTv = findViewById(R.id.commentB30TitleTv);
        guiderDeviceRecyclerView = findViewById(R.id.guiderDeviceRecyclerView);
    }

    private void initViews() {
        commentB30TitleTv.setText("Bemo搜索的设备");
        commentB30BackImg.setVisibility(View.VISIBLE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        guiderDeviceRecyclerView.setLayoutManager(linearLayoutManager);
        guiderList = new ArrayList<>();
        guiderDeviceAdapter = new GuiderDeviceAdapter(guiderList);
        guiderDeviceRecyclerView.setAdapter(guiderDeviceAdapter);


    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action == null)
                return;
            if(action.equals("hat.bemo.BlueTooth.blegatt.device")){
                String bleName = intent.getStringExtra("guiderName");
                String bmeMac = intent.getStringExtra("guiderMac");
                Map<String,String> maps = new HashMap<>();
                maps.put("guider_ble_name",bleName);
                maps.put("guider_ble_mac",bmeMac);
                guiderList.add(maps);
                guiderDeviceAdapter.notifyDataSetChanged();
            }
        }
    };












    private class GuiderDeviceAdapter extends RecyclerView.Adapter<GuiderDeviceAdapter.GuiderHolder> {

        private List<Map<String,String>> lit;

        public GuiderDeviceAdapter(List<Map<String, String>> lit) {
            this.lit = lit;
        }

        @NonNull
        @Override
        public GuiderHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(GuiderDeviceActivity.this).inflate(R.layout.recyclerview_bluedevice, viewGroup,false);
            return new GuiderHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull GuiderHolder guiderHolder, int i) {
            guiderHolder.circularProgressButton.setVisibility(View.INVISIBLE);
            guiderHolder.bleNameTv.setText(lit.get(i).get("guider_ble_name"));
            guiderHolder.bleMacTv.setText(lit.get(i).get("guider_ble_mac"));

        }

        @Override
        public int getItemCount() {
            return lit.size();
        }

        class GuiderHolder extends RecyclerView.ViewHolder{

            TextView bleNameTv, bleMacTv;
            Button circularProgressButton;

            public GuiderHolder(@NonNull View itemView) {
                super(itemView);
                bleNameTv = (TextView) itemView.findViewById(R.id.blue_name_tv);
                bleMacTv = (TextView) itemView.findViewById(R.id.snmac_tv);
                circularProgressButton = itemView.findViewById(R.id.bind_btn);

            }
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver != null)
            unregisterReceiver(broadcastReceiver);
    }
}
