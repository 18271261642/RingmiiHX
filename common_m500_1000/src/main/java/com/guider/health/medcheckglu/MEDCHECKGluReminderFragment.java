package com.guider.health.medcheckglu;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.getmedcheck.lib.MedCheck;
import com.getmedcheck.lib.constant.Constants;
import com.getmedcheck.lib.events.EventReadingProgress;
import com.getmedcheck.lib.model.BleDevice;
import com.getmedcheck.lib.model.BloodGlucoseData;
import com.getmedcheck.lib.model.IDeviceData;
import com.getmedcheck.lib.utils.StringUtils;
import com.guider.health.PermissionUtil;
import com.guider.health.all.R;
import com.guider.health.common.core.Config;
import com.guider.health.common.core.Glucose;
import com.guider.health.common.core.MEDCHECKGlucose;
import com.guider.health.common.device.DeviceInit;
import com.guider.health.common.utils.SkipClick;
import com.guider.health.common.utils.ToastUtil;
import com.guider.health.common.views.dialog.DialogProgressCountdown;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import static com.getmedcheck.lib.constant.Constants.BLOOD_GLUCOSE_DEVICE_ID_NEW;

//MEDCHECK血糖提示页面
public class MEDCHECKGluReminderFragment extends MedCheckFragment implements View.OnClickListener{

    private View view;
    private DialogProgressCountdown mDialogProgressCountdown;
    private boolean mAllPermissionsReady = false;
    private BleDevice mBleDevice = null;
    private Button nextButton;
    private TextView time1_food_selected;
    private TextView empty_food_selected;
    private int isEmptyFood = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_fragment_medcheck_glu_reminder,
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
        ImageView remindIv = view.findViewById(R.id.reminderIv);
        remindIv.setImageResource(R.mipmap.ic_medcheck_todo);
        ImageView empty_food = view.findViewById(R.id.empty_food);
        ImageView time1_food = view.findViewById(R.id.time1_food);
        time1_food_selected = view.findViewById(R.id.time1_food_selected);
        empty_food_selected = view.findViewById(R.id.empty_food_selected);
        Glucose.getInstance().setFoodTime(isEmptyFood);
        //开始测量先进行设备的连接
        nextButton = (Button) view.findViewById(R.id.btn_fora_glu_test);
        nextButton.setOnClickListener(this);
        empty_food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEmptyFood = 0;
                Glucose.getInstance().setFoodTime(isEmptyFood);
                time1_food_selected.setVisibility(View.GONE);
                empty_food_selected.setVisibility(View.VISIBLE);
            }
        });
        time1_food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEmptyFood = 2;
                Glucose.getInstance().setFoodTime(isEmptyFood);
                time1_food_selected.setVisibility(View.VISIBLE);
                empty_food_selected.setVisibility(View.GONE);
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
            if (scanResult.getDevice().getName().startsWith(BLOOD_GLUCOSE_DEVICE_ID_NEW)) {
                Log.e("medcheckglu", "设备在扫描,并且扫描到了指定的设备");
                //匹配到了medcheck的血糖仪，进行连接，连接之后进行数据的读取，最后进入下一页
                if (!mAllPermissionsReady || TextUtils.isEmpty(mBleDevice.getMacAddress())) {
                    return;
                } else {
                    _mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //找到了指定的设备就停止扫描进行连接
                            Log.e("medcheckglu", "停止扫描");
                            MedCheck.getInstance().stopScan(_mActivity);
                            MedCheck.getInstance().connect(_mActivity, mBleDevice.getMacAddress());
                        }
                    });
                }
            }
        }
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
            Log.e("medcheckglu", "读取数据完成");
        }
    }

    @Override
    protected void onDeviceDataReceive(BluetoothDevice device, ArrayList<IDeviceData> deviceData,
                                       String json, String deviceType) {
        super.onDeviceDataReceive(device, deviceData, json, deviceType);
        if (deviceData == null) {
            return;
        }
        Log.e("medcheckglu", "读取数据的json为" + json);
        if (deviceData.size() == 0) {
            //先暂时存放在提示处
            Log.e("medcheckglu", "读取数据为空或者读取失败");
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Type: ").append(deviceType).append("\n\n");
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ENGLISH);
        IDeviceData deviceDatum = deviceData.get(deviceData.size() - 1);
        if (deviceDatum.getType().equals(Constants.TYPE_BGM)) {
            BloodGlucoseData bloodGlucoseData = (BloodGlucoseData) deviceDatum;
            //判断是不是最新的数据
            Log.e("medcheckglu", "读取到的数据的时间为" + bloodGlucoseData.getDateTime());
            if (System.currentTimeMillis() - bloodGlucoseData.getDateTime() >= 10) {
                //时间超过10s说明不是最新的数据
                Log.e("medcheckglu", "不是最新的数据");
//                return;
            }
            DecimalFormat df = new DecimalFormat("0.0");
            float val = 0;
            if (StringUtils.isNumber(bloodGlucoseData.getHigh())) {
                val = Float.parseFloat(bloodGlucoseData.getHigh()) / 18f;
            }
            MEDCHECKGlucose.getMEDCHECKGluInstance().setGlucose(val);
            stringBuilder.append(df.format(val)).append(" mmol/L (").append(
                    bloodGlucoseData.getHigh()).append(" mg/dL)\n");
            stringBuilder.append(bloodGlucoseData.getAcPcStringValue()).append("\n");
            stringBuilder.append("DATE: ").append(sdf.format(bloodGlucoseData.getDateTime()));
        }
        Log.e("medcheckglu", "读取到的数据按需要格式为" + stringBuilder.toString());
        _mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDialogProgressCountdown.hideDialog();
            }
        });
        MedCheck.getInstance().disconnectDevice(_mActivity);
        start(new MEDCHECKGluResultFragment());
    }

    @Override
    protected void onPermissionGrantedAndBluetoothOn() {
        super.onPermissionGrantedAndBluetoothOn();
        mAllPermissionsReady = true;
    }

    private void initHeadView() {
        // 返回Home处理
        setHomeEvent(view.findViewById(R.id.home), Config.HOME_DEVICE);

        // title显式
        ((TextView) view.findViewById(R.id.title))
                .setText(getResources().getString(R.string.medcheck_glu_reminder_title));
        ((TextView) view.findViewById(R.id.tv_test_type)).setText(R.string.blood_sugar);
        TextView tv_test_reminder = ((TextView) view.findViewById(R.id.tv_test_reminder));
        tv_test_reminder.setText(R.string.medcheck_glu_tips);

        // 是否能跳过
        view.findViewById(R.id.skip).setVisibility(View.VISIBLE);
        view.findViewById(R.id.skip).setOnClickListener(new SkipClick(this,
                DeviceInit.DEV_MEDCHECK_GLU));

        // 返回上一页
        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop();
            }
        });
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_fora_glu_test){
            nextButton.setEnabled(false);
            mDialogProgressCountdown.showDialog(1000 * 60,
                    1000, new Runnable() {
                        @Override
                        public void run() {
                            Log.e("medcheckglu", "60s连接失败");
                            MedCheck.getInstance().stopScan(_mActivity);
                            nextButton.setText(_mActivity.getResources().getString(
                                    R.string.common_m_retest));
                            nextButton.setEnabled(true);
                        }
                    });
            if (!PermissionUtil.checkLocationPermission(_mActivity)) {
                PermissionUtil.requestLocationPerm(_mActivity);
            } else {
                mAllPermissionsReady = true;
                MedCheck.getInstance().startScan(_mActivity);
            }
        }
    }
}
