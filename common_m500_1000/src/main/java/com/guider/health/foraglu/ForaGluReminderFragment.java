package com.guider.health.foraglu;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.guider.health.all.R;
import com.guider.health.common.core.BaseFragment;
import com.guider.health.common.core.Config;
import com.guider.health.common.core.Glucose;
import com.guider.health.common.core.MyUtils;
import com.guider.health.common.device.DeviceInit;
import com.guider.health.common.utils.SkipClick;
import com.guider.health.common.utils.ToastUtil;
import com.guider.health.common.views.dialog.DialogProgressCountdown;

import ble.BleClient;

/**
 * 福尔血糖测量提示界面
 */
public class ForaGluReminderFragment extends BaseFragment implements BleVIewInterface {
    private View view;
    private DialogProgressCountdown mDialogProgressCountdown;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_fragment_fora_glu_reminder, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDialogProgressCountdown = new DialogProgressCountdown(_mActivity);
        initHeadView();
        initBodyView();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            GluServiceManager.getInstance().setViewObject(this);
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        GluServiceManager.getInstance().stopDeviceConnect();
    }

    @Override
    public void connectAndMessureIsOK() {
        GluServiceManager.getInstance().stopDeviceConnect();
        _mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDialogProgressCountdown.hideDialog();
            }
        });
        start(new ForaGluResultFragment());
    }

    @Override
    public void startUploadData() {

    }

    @Override
    public void connectNotSuccess() {
        _mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDialogProgressCountdown.hideDialog();
                changeUi2Fail();
            }
        });
    }

    private void initHeadView() {
        // 返回Home处理
        setHomeEvent(view.findViewById(R.id.home), Config.HOME_DEVICE);

        // title显式
        ((TextView) view.findViewById(R.id.title)).setText(getResources().getString(R.string.fora_glu_reminder_title));

        // 是否能跳过
        view.findViewById(R.id.skip).setVisibility(View.VISIBLE);
        view.findViewById(R.id.skip).setOnClickListener(new SkipClick(this , DeviceInit.DEV_FORA_GLU));

        // 返回上一页
        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop();
            }
        });
    }

    private void initBodyView() {
        GluServiceManager.getInstance().setViewObject(ForaGluReminderFragment.this);

        // 开始测量
        final Button btnTest = view.findViewById(R.id.btn_fora_glu_test);
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!MyUtils.isNormalClickTime()){
                    return;
                }
                btnTest.setEnabled(false);
                // 开始测量
                BleClient.init(_mActivity);

                startMeasure();
            }
        });
    }

    private void changeUi2Fail() {
        final Button bt = view.findViewById(R.id.btn_fora_glu_test);
        bt.setEnabled(true);
        bt.setVisibility(View.VISIBLE);
        bt.setText(getResources().getString(R.string.restart_test));
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt.setEnabled(false);

                startMeasure();
            }
        });
    }

    private void startMeasure() {
        GluServiceManager.getInstance().startMeasure();
        mDialogProgressCountdown.showDialog(1000 * 60, 1000, new Runnable() {
            @Override
            public void run() {
                changeUi2Fail();
                GluServiceManager.getInstance().stopDeviceConnect();
            }
        });
    }
}
