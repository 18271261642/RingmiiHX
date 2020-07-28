package com.guider.health.bp.view.mbb2.BP9804;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.guider.health.bp.R;
import com.guider.health.bp.presenter.BPServiceManager;
import com.guider.health.bp.presenter.mbb2.MbbMeasurePresenter;
import com.guider.health.bp.presenter.mbb2.Protocol;
import com.guider.health.bp.view.BPFragment;
import com.guider.health.bp.view.mbb2.iknetbluetoothlibrary.MeasurementResult;
import com.guider.health.common.core.Config;
import com.guider.health.common.core.HeartPressMbb_9804;
import com.guider.health.common.device.DeviceInit;
import com.guider.health.common.utils.SkipClick;

import java.util.List;

public class BPMeasureFragment extends BPFragment implements Protocol.IView {

    View view;
    private TextView bpReminder;
    Protocol.IPresenter mPresenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bp_mbb_connect_and_meassure, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        _mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setHomeEvent(view.findViewById(R.id.home), Config.HOME_DEVICE);
        ((TextView) view.findViewById(R.id.title)).setText("设备测量");
        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BPServiceManager.getInstance().stopDeviceConnect();
                pop();
            }
        });
        view.findViewById(R.id.skip).setVisibility(View.VISIBLE);
        view.findViewById(R.id.skip).setOnClickListener(new SkipClick(this , DeviceInit.DEV_BP_MBB_9804));


        initView();
        mPresenter = new MbbMeasurePresenter(this);
        mPresenter.start();
    }

    @Override
    public void onRunning(String running) {
        //测量过程中的压力值
        bpReminder.setText("正在测量中\n mmHg:" + running);
    }

    @Override
    public void onPower(String power) {
        //测量前获取的电量值
//        Toast.makeText(_mActivity, power, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMeasureResult(MeasurementResult result) {
        //测量结果
        HeartPressMbb_9804.getInstance().setSbp(result.getCheckShrink() + "");
        HeartPressMbb_9804.getInstance().setDbp(result.getCheckDiastole() + "");
        HeartPressMbb_9804.getInstance().setHeart(result.getCheckHeartRate() + "");
        startWithPop(new BPMeasureResult());
        mPresenter.finish();
    }

    @Override
    public void onMeasureError() {
        //测量错误
        changeUi2Fail("测量数据异常 \n 请重新测试");
    }

    @Override
    public void onFoundFinish(List<BluetoothDevice> deviceList) {
        //搜索结束，deviceList.size()如果为0，则没有搜索到设备
        if (deviceList.size() == 0) {
            changeUi2Fail("未搜索到设备");
        }
    }

    @Override
    public void onDisconnected(BluetoothDevice device) {
        //断开连接
        changeUi2Fail("设备已断开\n请确认开机后重新测试");
    }

    @Override
    public void onConnected(boolean isConnected, BluetoothDevice device) {
        //是否连接成功
        if (isConnected) {
        } else {
        }
    }

    @Override
    public Activity getMyActivity() {
        return _mActivity;
    }


    private void initView() {
        bpReminder = view.findViewById(R.id.bp_reminder);
    }


    private void changeUi2Fail(String msg) {
        bpReminder.setText(msg);
        //view.findViewById(R.id.bp_cancel).setVisibility(View.GONE);
        final Button bt = view.findViewById(R.id.bp_re);
        bt.setVisibility(View.VISIBLE);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bpReminder.setText("正在测量中\n  请稍后...");
                bt.setVisibility(View.INVISIBLE);
                mPresenter.start();
            }
        });
    }
}
