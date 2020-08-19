package com.guider.health.medcheckpre;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.getmedcheck.lib.MedCheck;
import com.getmedcheck.lib.constant.Constants;
import com.getmedcheck.lib.events.EventReadingProgress;
import com.getmedcheck.lib.model.BleDevice;
import com.getmedcheck.lib.model.BloodPressureData;
import com.getmedcheck.lib.model.IDeviceData;
import com.guider.health.all.R;
import com.guider.health.common.core.Config;
import com.guider.health.common.core.MEDCHECKPressure;
import com.guider.health.common.device.DeviceInit;
import com.guider.health.common.utils.SkipClick;
import com.guider.health.common.utils.ToastUtil;
import com.guider.health.common.views.dialog.DialogProgressCountdown;
import com.guider.health.medcheckglu.MedCheckFragment;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import static com.getmedcheck.lib.constant.Constants.BLOOD_PRESSURE_DEVICE_ID_NEW;

//MEDCheck血压提示页面
public class MEDCheckPressureReminderFragment extends MedCheckFragment {
    private View view;
    private DialogProgressCountdown mDialogProgressCountdown;
    private Button nextButton;
    private BleDevice mBleDevice = null;
    private boolean mAllPermissionsReady = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_fragment_fora_glu_reminder,
                container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDialogProgressCountdown = new DialogProgressCountdown(_mActivity);
        initHeadView();
        initBodyView();
    }

    private void initBodyView() {
        registerCallback();
        requestLocationPermission();
        //开始测量先进行设备的连接
        nextButton = (Button) view.findViewById(R.id.btn_fora_glu_test);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextButton.setEnabled(false);
                mDialogProgressCountdown.showDialog(1000 * 60,
                        1000, new Runnable() {
                    @Override
                    public void run() {
                        Log.e("medcheckpre", "60s连接失败");
                        MedCheck.getInstance().stopScan(_mActivity);
                        ToastUtil.showShort(_mActivity,"连接失败");
                        nextButton.setEnabled(true);
                    }
                });
                checkAllConditions();
            }
        });
    }


    @Override
    protected void startScan() {
        super.startScan();
        MedCheck.getInstance().startScan(_mActivity);
    }

    @Override
    protected void onDeviceScanResult(ScanResult scanResult) {
        super.onDeviceScanResult(scanResult);
        if (scanResult.getDevice() != null && scanResult.getDevice().getName() != null) {
            mBleDevice = new BleDevice(scanResult.getDevice());
            // if device name start with med check device name then
            if (scanResult.getDevice().getName().startsWith(BLOOD_PRESSURE_DEVICE_ID_NEW)) {
                Log.e("medcheckpre", "设备在扫描,并且扫描到了指定的设备");
                //匹配到了medcheck的血压仪，进行连接，连接之后进行数据的读取，最后进入下一页
                if (!mAllPermissionsReady || TextUtils.isEmpty(mBleDevice.getMacAddress())) {
                    return;
                } else {
                    _mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //找到了指定的设备就停止扫描进行连接
                            Log.e("medcheckpre", "停止扫描");
                            MedCheck.getInstance().stopScan(_mActivity);
                            MedCheck.getInstance().connect(_mActivity, mBleDevice.getMacAddress());
                        }
                    });
                }
            }
        }
    }

    @Override
    protected void onPermissionGrantedAndBluetoothOn() {
        super.onPermissionGrantedAndBluetoothOn();
        mAllPermissionsReady = true;
    }

    @Override
    protected void onDeviceConnectionStateChange(BleDevice bleDevice, int status) {
        super.onDeviceConnectionStateChange(bleDevice, status);
        if (mBleDevice != null && bleDevice.getMacAddress().equals(mBleDevice.getMacAddress())
                && status == 1) {
            if (mBleDevice == null || !mAllPermissionsReady ||
                    TextUtils.isEmpty(mBleDevice.getMacAddress())) {
                return;
            }
            MedCheck.getInstance().writeCommand(_mActivity, mBleDevice.getMacAddress());
        }
    }

    @Override
    protected void onDeviceDataReadingStateChange(int state, String message) {
        super.onDeviceDataReadingStateChange(state, message);
        if (state == EventReadingProgress.COMPLETED) {
            //是否读取数据完成
            Log.e("medcheckpre", "读取数据完成");
        }
    }

    @Override
    protected void onDeviceDataReceive(BluetoothDevice device, ArrayList<IDeviceData> deviceData,
                                       String json, String deviceType) {
        super.onDeviceDataReceive(device, deviceData, json, deviceType);
        if (deviceData == null) {
            return;
        }
        Log.e("medcheckpre", "读取数据的json为" + json);
        if (deviceData.size() == 0) {
            //先暂时存放在提示处
            Log.e("medcheckpre", "读取数据为空或者读取失败");
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Type: ").append(deviceType).append("\n\n");
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ENGLISH);
        IDeviceData deviceDatum = deviceData.get(deviceData.size() - 1);
        if (deviceDatum.getType().equals(Constants.TYPE_BPM)) {
            BloodPressureData bloodPressureData = (BloodPressureData) deviceDatum;
            //高压
            stringBuilder.append("SYS: ").append(bloodPressureData.getSystolic()).append(" mmHg, ");
            //低压
            stringBuilder.append("DIA: ").append(bloodPressureData.getDiastolic()).append(" mmHg, ");
            //心率
            stringBuilder.append("PUL: ").append(bloodPressureData.getHeartRate()).append(" min\n");
            //值高 收缩压
            MEDCHECKPressure.getMEDCHECKPressureInstance().setSbp(bloodPressureData.getSystolic());
            //低 舒张
            MEDCHECKPressure.getMEDCHECKPressureInstance().setDbp(bloodPressureData.getDiastolic());
            //心率
            MEDCHECKPressure.getMEDCHECKPressureInstance().setHeart(bloodPressureData.getHeartRate());
            stringBuilder.append("IHB: ").append(bloodPressureData.getIHB()).append(", ");
            stringBuilder.append("DATE: ").append(sdf.format(bloodPressureData.getDateTime()));
            if (System.currentTimeMillis() - bloodPressureData.getDateTime() >= 10) {
                //时间超过10s说明不是最新的数据
                Log.e("medcheckpre", "不是最新的数据");
//                return;
            }
            Log.e("medcheckpre", "读取到的数据按需要格式为" + stringBuilder.toString());
            _mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mDialogProgressCountdown.hideDialog();
                }
            });
            MedCheck.getInstance().disconnectDevice(_mActivity);
            start(new MEDCheckPressureResultFragment());
        }
    }

    private void initHeadView() {
        // 返回Home处理
        setHomeEvent(view.findViewById(R.id.home), Config.HOME_DEVICE);

        // title显式
        ((TextView) view.findViewById(R.id.title))
                .setText(getResources().getString(R.string.medcheck_pre_reminder_title));
        ((TextView) view.findViewById(R.id.tv_test_type)).setText(R.string.blood_pressure);
        TextView tv_test_reminder = view.findViewById(R.id.tv_test_reminder);
        tv_test_reminder.setText(R.string.medcheck_pre_tips);
        // 是否能跳过
        view.findViewById(R.id.skip).setVisibility(View.VISIBLE);
        view.findViewById(R.id.skip).setOnClickListener(new SkipClick(this,
                DeviceInit.DEV_MEDCHECK_PRE));

        // 返回上一页
        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop();
            }
        });
    }

}
