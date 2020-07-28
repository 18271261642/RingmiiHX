package com.guider.health.ecg;

import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.guider.health.bluetooth.core.BleBluetooth;
import com.guider.health.common.core.BaseActivity;
import com.guider.health.common.core.BaseFragment;
import com.guider.health.ecg.view.ECGDeviceOperate;


/**
 *
 *   蓝牙 -> 找到设备 -> 设备页面 -> 测量数据
 */
public class ECGMainActivity extends BaseActivity{


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.egc_activity_main);

        BleBluetooth.getInstance().init(this);

//        ProjectInit.init(this)
//                .withApiHost("http://192.168.100.41:80/")
//                .configure();


        hideBottomUIMenu();


        BaseFragment fragment = findFragment(ECGDeviceOperate.class);
        if (fragment == null) {
            loadRootFragment(R.id.main_content, new ECGDeviceOperate());
//

        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        hideBottomUIMenu();
    }

    //    MyH myH = new MyH(ECGMainActivity.this);
//
//    private static class MyH extends Handler{
//
//        public WeakReference<ECGMainActivity> weakReference;
//
//        public MyH(ECGMainActivity mainActivity){
//            weakReference = new WeakReference(mainActivity);
//        }
//        public void handleMessage(Message msg) {
//
//            if (weakReference.get() != null){
//                weakReference.get().measureFragment.showPageInformation();
//            }
//
//
//        }
//    }
//
//
//    ECGMeasureModel measureModel = new ECGMeasureModel();
//
//    @Override
//    public List getData() {
//        if(ECGTrueModel.getInstance().openBluetooth(this)){
//            return null;
//        }
//
//        measureModel.getTimeData(myH, mDevice);
//
//        return null;
//    }

//    /**
//     * 判断搜索的设备是新蓝牙设备，且不重复
//     *
//     * @param device
//     * @return
//     */
//    private boolean isNewDevice(BluetoothDevice device) {
//        boolean repeatFlag = false;
//        for (BluetoothDevice d :
//                deviceLists) {
//            if (d.getAddress().equals(device.getAddress())) {
//                repeatFlag = true;
//            }
//        }
//        //不是已绑定状态，且列表中不重复
//        return device.getBondState() != BluetoothDevice.BOND_BONDED && !repeatFlag;
//    }

    //全屏
    protected void hideBottomUIMenu() {

        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
}
