package com.guider.healthring.b30;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.guider.healthring.MyApp;
import com.guider.healthring.R;
import com.guider.healthring.b30.view.ScrollPickerView;
import com.guider.healthring.b30.view.StringScrollPicker;
import com.guider.healthring.bleutil.MyCommandManager;
import com.guider.healthring.siswatch.WatchBaseActivity;
import com.guider.healthring.util.ToastUtil;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IBPSettingDataListener;
import com.veepoo.protocol.model.datas.BpSettingData;
import com.veepoo.protocol.model.settings.BpSetting;

import java.util.ArrayList;
import java.util.List;

/**
 * 私人血压
 */
public class PrivateBloadActivity extends WatchBaseActivity
        implements ScrollPickerView.OnSelectedListener,View.OnClickListener {

    ImageView commentB30BackImg;
    TextView commentB30TitleTv;
    TextView tv_bload;
    StringScrollPicker hightBloadView;
    StringScrollPicker lowBloadView;


    //血压数据
    private List<String> hightBloadList;
    //低压
    private List<String> lowBloadList;
    /**
     * 是否是私人模式
     */
//    private boolean isOpenPrivateModel;

    private int highBload;
    private int lowBload;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_bload_b30);
        initViewIds();
        initViews();
        initData();
        readBloodState();
    }

    private void initViewIds() {
        commentB30BackImg = findViewById(R.id.commentB30BackImg);
        commentB30TitleTv = findViewById(R.id.commentB30TitleTv);
        tv_bload = findViewById(R.id.tv_bload);
        hightBloadView = findViewById(R.id.hightBloadView);
        lowBloadView = findViewById(R.id.lowBloadView);
        commentB30BackImg.setOnClickListener(this);
        findViewById(R.id.b30SetPrivateBloadBtn).setOnClickListener(this);
    }

    //读取私人血压
    private void readBloodState() {
        if (MyCommandManager.DEVICENAME != null) {
            MyApp.getInstance().getVpOperateManager().readDetectBP(iBleWriteResponse, new IBPSettingDataListener() {
                @Override
                public void onDataChange(BpSettingData bpSettingData) {
                    handlerBloodState(bpSettingData);
                }
            });
        }
    }

    /**
     * 处理手环血压状态
     */
    private void handlerBloodState(BpSettingData bpSettingData) {
//        isOpenPrivateModel = bpSettingData.getModel() == EBPDetectModel.DETECT_MODEL_PRIVATE;
        highBload = bpSettingData.getHighPressure();
        lowBload = bpSettingData.getLowPressure();
        String hint = highBload + "/" + lowBload;
        tv_bload.setText(hint);
        hightBloadView.setSelectedPosition(hightBloadList.indexOf(highBload + ""));
        lowBloadView.setSelectedPosition(lowBloadList.indexOf(lowBload + ""));
    }

    private void initData() {
        hightBloadList = new ArrayList<>();
        lowBloadList = new ArrayList<>();
        for (int i = 80; i <= 209; i++) {
            hightBloadList.add(i + 1 + "");
        }
        for (int k = 46; k <= 179; k++) {
            lowBloadList.add(k + 1 + "");
        }
        hightBloadView.setData(hightBloadList);
        lowBloadView.setData(lowBloadList);
        hightBloadView.setOnSelectedListener(this);
        lowBloadView.setOnSelectedListener(this);
    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(R.string.private_mode_bloodpressure);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:    //返回
                finish();
                break;
            case R.id.b30SetPrivateBloadBtn:    //保存
                if (MyCommandManager.DEVICENAME != null) {
//                    BpSetting bpSetting = new BpSetting(isOpenPrivateModel, highBload, lowBload);
                    BpSetting bpSetting = new BpSetting(true, highBload, lowBload);
                    MyApp.getInstance().getVpOperateManager().settingDetectBP(iBleWriteResponse, new IBPSettingDataListener() {
                        @Override
                        public void onDataChange(BpSettingData bpSettingData) {
                            handlerBloodState(bpSettingData);
                            ToastUtil.showShort(PrivateBloadActivity.this, getResources().getString(R.string.settings_success));
                            finish();
                        }
                    }, bpSetting);
                }
                break;
        }
    }

    @Override
    public void onSelected(ScrollPickerView scrollPickerView, int position) {
        switch (scrollPickerView.getId()) {
            case R.id.hightBloadView:   //高压
                highBload = Integer.valueOf(hightBloadList.get(position));
                break;
            case R.id.lowBloadView: //低压
                lowBload = Integer.valueOf(lowBloadList.get(position));
                break;
        }
    }

    private IBleWriteResponse iBleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int i) {

        }
    };

}
