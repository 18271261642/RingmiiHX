package com.guider.healthring.B18I.b18isystemic;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.guider.healthring.R;

import cn.appscomm.bluetooth.app.BluetoothSDK;
import cn.appscomm.bluetooth.interfaces.ResultCallBack;

/**
 * @aboutContent: 关于
 * @author： 安
 * @crateTime: 2017/9/26 08:57
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class SettingAboutActivity extends AppCompatActivity implements View.OnClickListener {

    TextView barTitles;
    TextView versionTv;
    ImageView image_back;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_about_layout);
//        setContentView(R.layout.b18i_app_firmware_update_layout);
        barTitles = findViewById(R.id.bar_titles);
        versionTv = findViewById(R.id.version_tv);
        image_back = findViewById(R.id.image_back);
        image_back.setOnClickListener(this);
        barTitles.setText(getResources().getString(R.string.abour));
        BluetoothSDK.getDeviceVersion(resultCallBack);
    }

    @Override
    protected void onStart() {
        super.onStart();
        BluetoothSDK.getDeviceVersion(resultCallBack);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                finish();
                break;
        }
    }

    ResultCallBack resultCallBack = new ResultCallBack() {
        @Override
        public void onSuccess(int i, Object[] objects) {
            switch (i) {
                case ResultCallBack.TYPE_GET_DEVICE_VERSION:
                    versionTv.setText(String.valueOf(objects[0]));
                    break;
            }
        }

        @Override
        public void onFail(int i) {

        }
    };
}
