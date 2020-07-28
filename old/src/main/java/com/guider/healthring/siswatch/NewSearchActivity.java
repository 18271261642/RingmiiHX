package com.guider.healthring.siswatch;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.guider.healthring.Commont;
import com.guider.healthring.MyApp;
import com.guider.healthring.R;
import com.guider.healthring.activity.GuiderDeviceActivity;
import com.guider.healthring.b30.B30HomeActivity;
import com.guider.healthring.b30.service.VerB30PwdListener;
import com.guider.healthring.b31.B31HomeActivity;
import com.guider.healthring.bleutil.MyCommandManager;
import com.guider.healthring.h9.H9HomeActivity;
import com.guider.healthring.siswatch.adapter.CustomBlueAdapter;
import com.guider.healthring.siswatch.bean.CustomBlueDevice;
import com.guider.healthring.siswatch.utils.BlueAdapterUtils;
import com.guider.healthring.siswatch.utils.HidUtil;
import com.guider.healthring.siswatch.utils.WatchUtils;
import com.guider.healthring.util.SharedPreferencesUtils;
import com.guider.healthring.util.ToastUtil;
import com.guider.healthring.view.CusInputDialogView;
import com.sdk.bluetooth.config.BluetoothConfig;
import com.sdk.bluetooth.manage.AppsBluetoothManager;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.guider.healthring.siswatch.utils.WatchConstants.isScanConn;
import static com.guider.healthring.siswatch.utils.WatchUtils.B31_NAME;

/**
 * Created by Administrator on 2017/10/31.
 */

/**
 * 新的搜索页面
 */
public class NewSearchActivity extends GetUserInfoActivity implements
        CustomBlueAdapter.OnSearchOnBindClickListener
        , SwipeRefreshLayout.OnRefreshListener {
//        BluetoothManagerScanListener
//        BluetoothManagerDeviceConnectListener {

    private static final String TAG = "NewSearchActivity";
    private static final int BLUE_VISIABLE_TIME_CODE = 10 * 1000;
    private static final int REQUEST_TURNON_BLUE_CODE = 1001;   //打开蓝牙请求码
    private static final int STOP_SCANNER_DEVICE_CODE = 1002;   //停止扫描
    private static final int GET_OPENBLUE_SUCCESS_CODE = 120;   //请求打开蓝牙 ，用户确认打开后返回
    public static final int H9_REQUEST_CONNECT_CODE = 1112;

    private String H9CONNECT_STATE_ACTION = "com.example.bozhilun.android.h9.connstate";

    //RecycleView
    @BindView(R.id.search_recycler)
    RecyclerView searchRecycler;
    //Swiper
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;
    //跑马灯textView
    @BindView(R.id.search_alertTv)
    TextView searchAlertTv;
    @BindView(R.id.newSearchTitleLeft)
    FrameLayout newSearchTitleLeft;
    @BindView(R.id.newSearchTitleTv)
    TextView newSearchTitleTv;
    @BindView(R.id.newSearchRightImg1)
    ImageView newSearchRightImg1;


    private List<CustomBlueDevice> customDeviceList;  //数据源集合
    private CustomBlueAdapter customBlueAdapter;    //适配器
    private BluetoothAdapter bluetoothAdapter;  //蓝牙适配器
    private List<String> repeatList;

    private CustomBlueDevice customBlueDevice = null;

    //B30系列设备验证密码提示框
    private CusInputDialogView cusInputDialogView;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressLint("MissingPermission")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case STOP_SCANNER_DEVICE_CODE:  //停止扫描
                    swipeRefresh.setRefreshing(false);
                    bluetoothAdapter.stopLeScan(leScanCallback);
                    break;
                case H9_REQUEST_CONNECT_CODE:  //H 9手表连接
                    closeLoadingDialog();
                    startActivity(new Intent(NewSearchActivity.this, H9HomeActivity.class));
                    finish();
                    break;
                case 777:   //连接失败后更新目录
                    refresh();
                    break;

            }

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "-------onCreate----");
        setContentView(R.layout.activity_search_device);
        ButterKnife.bind(this);
        //启动B30的服务
        MyApp.startB30Server();

        initViews();
        h9DataOper();
        initData();


    }

    private void h9DataOper() {
        String mylanya = (String) SharedPreferencesUtils.readObject(NewSearchActivity.this, Commont.BLENAME);
        String mylanmac = (String) SharedPreferencesUtils.readObject(NewSearchActivity.this, Commont.BLEMAC);
        String defaultMac = BluetoothConfig.getDefaultMac(NewSearchActivity.this);
        if (!TextUtils.isEmpty(mylanya) || !TextUtils.isEmpty(mylanmac) || !TextUtils.isEmpty(defaultMac)) {
            AppsBluetoothManager.getInstance(MyApp.getContext()).doUnbindDevice(mylanmac);
            AppsBluetoothManager.getInstance(NewSearchActivity.this).clearBluetoothManagerDeviceConnectListeners();
            SharedPreferencesUtils.saveObject(NewSearchActivity.this, Commont.BLENAME, "");
            SharedPreferencesUtils.saveObject(NewSearchActivity.this, Commont.BLEMAC, "");
            MyApp.getInstance().setMacAddress(null);// 清空全局
            BluetoothConfig.setDefaultMac(NewSearchActivity.this, "");
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        try {
            if (customDeviceList != null)
                customDeviceList.clear();
            if (customBlueAdapter != null)
                customBlueAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initData() {


        repeatList = new ArrayList<>();
        BluetoothManager bm = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bm.getAdapter();

        //判断是否有位置权限
        if (AndPermission.hasPermissions(NewSearchActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            operScan();
        } else {
            verticalPermission();
        }


    }

    private void verticalPermission() {
        AndPermission.with(NewSearchActivity.this)
                .runtime()
                .permission(Manifest.permission.ACCESS_FINE_LOCATION)
                .rationale(rational)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {

                    }
                }).onDenied(new Action<List<String>>() {
            @Override
            public void onAction(List<String> data) {
                ToastUtil.showToast(NewSearchActivity.this, "已拒绝权限,可能无法搜索到蓝牙设备！");
            }
        }).start();


    }


    //当权限被拒绝时提醒用户再次授权
    private Rationale rational = new Rationale() {
        @Override
        public void showRationale(Context context, Object data, RequestExecutor executor) {
            executor.execute();
            AndPermission.with(NewSearchActivity.this)
                    .runtime()
                    .permission(Manifest.permission.ACCESS_FINE_LOCATION)
                    .onGranted(new Action<List<String>>() {
                        @Override
                        public void onAction(List<String> data) {

                        }
                    })
                    .onDenied(new Action<List<String>>() {
                        @Override
                        public void onAction(List<String> data) {
                            //AndPermission.with(NewSearchActivity.this).runtime().setting().
                        }
                    })
                    .start();
        }
    };


    @SuppressLint("MissingPermission")
    private void operScan() {
        if (bluetoothAdapter != null) {
            if (!bluetoothAdapter.isEnabled()) {  //未打开蓝牙
                BlueAdapterUtils.getBlueAdapterUtils(NewSearchActivity.this).turnOnBlue(NewSearchActivity.this,
                        BLUE_VISIABLE_TIME_CODE, REQUEST_TURNON_BLUE_CODE);
            } else {
                scanBlueDevice(true);   //扫描设备
            }
        } else {
            //不支持蓝牙
            ToastUtil.showToast(NewSearchActivity.this, getResources().getString(R.string.bluetooth_not_supported));
            return;
        }
    }

    //扫描和停止扫描设备
    @SuppressLint("MissingPermission")
    private void scanBlueDevice(boolean b) {
        if (!AndPermission.hasPermissions(NewSearchActivity.this, Manifest.permission.ACCESS_FINE_LOCATION))
            verticalPermission();
        if (b) {

            //扫描设备，默认扫描时间为10秒
            swipeRefresh.setRefreshing(true);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                }
            }, 2000);
            getPhonePairDevice();   //获取手机配对的设备
            bluetoothAdapter.startLeScan(leScanCallback);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = STOP_SCANNER_DEVICE_CODE;
                    handler.sendMessage(message);
                }
            }, BLUE_VISIABLE_TIME_CODE);
        } else {
            if (swipeRefresh != null)
                swipeRefresh.setRefreshing(false);
            if (bluetoothAdapter != null)
                bluetoothAdapter.stopLeScan(leScanCallback);
        }
    }

    //获取配对的蓝牙设备
    @SuppressLint("MissingPermission")
    private void getPhonePairDevice() {
        //获取已配对设备
        @SuppressLint("MissingPermission") Object[] lstDevice = bluetoothAdapter.getBondedDevices().toArray();
        if(lstDevice == null)
            return;
        for (Object o : lstDevice) {
            BluetoothDevice bluetoothDevice = (BluetoothDevice) o;
            if (bluetoothDevice == null)
                return;
            if (bluetoothDevice.getName() == null)
                return;
            if (bluetoothDevice.getName().contains("H8")) {
                repeatList.add(bluetoothDevice.getAddress());
                customDeviceList.add(new CustomBlueDevice(bluetoothDevice, "", 0));
                customBlueAdapter.notifyDataSetChanged();
            }

        }

    }

    /**
     * 蓝牙扫描回调
     *
     *  bleName.contains("Ringmii") || bleName.length() == 3 && bleName.equals(WatchUtils.B30_NAME) || (bleName.length() == 3 && bleName.equals(B31_NAME)) || (bleName.length() == 7 && bleName.equals(WatchUtils.RINGMII_NAME))
     *                         || (bleName.length() == 4 && bleName.equals(WatchUtils.S500_NAME))
     */
    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice bluetoothDevice, int rssi, byte[] scanRecord) {
            @SuppressLint("MissingPermission") String bleName = bluetoothDevice.getName();
            String bleMac = bluetoothDevice.getAddress(); //bozlun
            if (!WatchUtils.isEmpty(bleName)) {
                    if (!repeatList.contains(bleMac)) {
                        if (customDeviceList.size() <= 50) {
                            repeatList.add(bleMac);
                            customDeviceList.add(new CustomBlueDevice(bluetoothDevice, Math.abs(rssi) + "", ((scanRecord[7] + scanRecord[8]))));
                            Comparator comparator = new Comparator<CustomBlueDevice>() {
                                @Override
                                public int compare(CustomBlueDevice o1, CustomBlueDevice o2) {
                                    return o1.getRssi().compareTo(o2.getRssi());
                                }
                            };
                            Collections.sort(customDeviceList, comparator);
                            customBlueAdapter.notifyDataSetChanged();
                        } else {
                            scanBlueDevice(false);
                        }
                    }
                }
            }
    };

    private void initViews() {
        //注册扫描蓝牙设备的广播
        registerReceiver(broadcastReceiver, BlueAdapterUtils.getBlueAdapterUtils(NewSearchActivity.this).scanIntFilter()); //注册广播
        //H8BleManagerInstance.getH8BleManagerInstance();
        //跑马灯效果
        searchAlertTv.setSelected(true);
        newSearchTitleTv.setText(getResources().getString(R.string.search_device));
        //设置RecyclerView相关
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        searchRecycler.setLayoutManager(linearLayoutManager);
        searchRecycler.addItemDecoration(new DividerItemDecoration(NewSearchActivity.this, DividerItemDecoration.VERTICAL));
        customDeviceList = new ArrayList<>();
        customBlueAdapter = new CustomBlueAdapter(customDeviceList, NewSearchActivity.this);
        searchRecycler.setAdapter(customBlueAdapter);
        customBlueAdapter.setOnBindClickListener(this);
        swipeRefresh.setOnRefreshListener(this);

        newSearchTitleTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startActivity(GuiderDeviceActivity.class);
                return true;
            }
        });

    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TURNON_BLUE_CODE) {    //打开蓝牙返回
            if (resultCode == GET_OPENBLUE_SUCCESS_CODE) {  //打开蓝牙返回
                scanBlueDevice(true);
            } else {  //点击取消 0    //取消后强制打开
                bluetoothAdapter.enable();
                scanBlueDevice(true);
            }
        }
    }


    @Override
    protected void onStop() {
        super.onStop();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (broadcastReceiver != null)
                unregisterReceiver(broadcastReceiver);
            if (cusInputDialogView != null) {
                cusInputDialogView.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //绑定按钮的点击事件
    @SuppressLint("MissingPermission")
    @Override
    public void doBindOperator(int position) {
        try {
            if (bluetoothAdapter != null && !bluetoothAdapter.isDiscovering()) {
                scanBlueDevice(false);
            }
            if (customDeviceList != null) {
                customBlueDevice = customDeviceList.get(position);
            }
            @SuppressLint("MissingPermission") String bleName = customBlueDevice.getBluetoothDevice().getName();
            if (customBlueDevice == null || WatchUtils.isEmpty(bleName))
                return;
            /**
             * B30，B36，Ringmiihx手表
             */
            if (bleName.equals(WatchUtils.B30_NAME)
                    || bleName.equals(WatchUtils.RINGMII_NAME)
                    || bleName.equals(B31_NAME)
                    || bleName.equals(WatchUtils.S500_NAME) || bleName.contains("Ringmii")) {
                //connectB30(customBlueDevice.getBluetoothDevice().getAddress().trim(), bleName);
                //B30 B31 500S Ringmii
                if (customBlueDevice != null && !WatchUtils.isEmpty(customBlueDevice.getBluetoothDevice().getAddress())) {
                    showLoadingDialog("connect...");
                    MyApp.getInstance().getB30ConnStateService().connB30ConnBle(customBlueDevice.getBluetoothDevice().getAddress(), bleName);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //下拉刷新
    @Override
    public void onRefresh() {
        refresh();
    }

    private void refresh() {
        customDeviceList.clear();
        repeatList.clear();
        customBlueAdapter.notifyDataSetChanged();
        scanBlueDevice(true);
    }

    //广播接收者接收H8手表配对连接的状态
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            closeLoadingDialog();
            Log.e(TAG, "--------action-------" + action);
            if (!WatchUtils.isEmpty(action)) {
                if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (blueState) {
                        case BluetoothAdapter.STATE_TURNING_ON://13

                            break;
                        case BluetoothAdapter.STATE_ON://13
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF://11
                            closeLoadingDialog();
                            if (customDeviceList != null)
                                customDeviceList.clear();
                            if (customBlueAdapter != null)
                                customBlueAdapter.notifyDataSetChanged();


                            break;
                        case BluetoothAdapter.STATE_OFF://10
                            if (customDeviceList != null) customDeviceList.clear();
                            if (customBlueAdapter != null)
                                customBlueAdapter.notifyDataSetChanged();

                            break;
                    }
                }
                if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {   //绑定状态的广播，配对
                    int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE);
                    BluetoothDevice bd = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (bd == null)
                        return;
                    if (bd.getName() == null)
                        return;
                    switch (bondState) {
                        case BluetoothDevice.BOND_BONDED:   //已绑定 12
//                            Log.e(TAG, "-----111-----");
                            if (customBlueDevice != null) {
                                if (bd.getName().equals(customBlueDevice.getBluetoothDevice().getName())) {
//                                    Log.e(TAG, "-----22-----");
                                    showLoadingDialog("connect...");
                                    HidUtil.getInstance(MyApp.getContext()).connect(bd);
                                }
                            }
                            break;
                        case BluetoothDevice.BOND_BONDING:  //绑定中   11
                            if (customBlueDevice != null) {
                                if (bd.getName().equals(customBlueDevice.getBluetoothDevice().getName())) {
//                                    Log.e(TAG, "-----22-----");
                                    showLoadingDialog(verLanguage());
                                }
                            }
                            break;
                        case BluetoothDevice.BOND_NONE: //绑定失败  10
                            if (customBlueDevice != null && customBlueDevice.getBluetoothDevice().getName() != null) {
                                if (bd.getName().equals(customBlueDevice.getBluetoothDevice().getName())) {
                                    closeLoadingDialog();
                                    ToastUtil.showToast(NewSearchActivity.this, getResources().getString(R.string.string_conn_fail));
                                    refresh();
                                }
                            }
                            break;
                        default:
                            break;
                    }
                }


                //B30手环连接成功
                if (action.equals(WatchUtils.B30_CONNECTED_ACTION)) { //B30连接成功
                    closeLoadingDialog();
                    isScanConn = true;
                    startActivity(B30HomeActivity.class);
                    NewSearchActivity.this.finish();
                }

                //B31、500S连接成功
                if (action.equals(WatchUtils.B31_CONNECTED_ACTION)) {
                    closeLoadingDialog();
                    isScanConn = true;
                    startActivity(B31HomeActivity.class);
                    NewSearchActivity.this.finish();
                }


                //B30，B31 连接失败
                if (action.equals(WatchUtils.B30_DISCONNECTED_ACTION)) {
                    closeLoadingDialog();
                    ToastUtil.showShort(NewSearchActivity.this, getResources().getString(R.string.string_conn_fail));
                    MyCommandManager.ADDRESS = null;// 断开连接了就设置为null
                }

                //B30，B31 提示输入密码
                if (action.equals(WatchUtils.CHANGEPASS)) {
                    String b30ID = intent.getStringExtra("b30ID");
                    Log.d("--------id-", b30ID);
                    Log.d("------zza---", "弹出输入提示");
                    //showLoadingDialog2(b30ID);
                    showB30InputPwd();
                }

            }
        }
    };

    //提示输入密码
    private void showB30InputPwd() {
        if (cusInputDialogView == null) {
            cusInputDialogView = new CusInputDialogView(NewSearchActivity.this);
        }
        cusInputDialogView.show();
        cusInputDialogView.setCancelable(false);
        cusInputDialogView.setCusInputDialogListener(new CusInputDialogView.CusInputDialogListener() {
            @Override
            public void cusDialogCancle() {
                cusInputDialogView.dismiss();
                //断开连接
                MyApp.getInstance().getVpOperateManager().disconnectWatch(new IBleWriteResponse() {
                    @Override
                    public void onResponse(int i) {

                    }
                });
                //刷新搜索界面
                handler.sendEmptyMessage(777);
            }

            @Override
            public void cusDialogSureData(String data) {
                MyApp.getInstance().getB30ConnStateService().continuteConn(data, new VerB30PwdListener() {
                    @Override
                    public void verPwdFailed() {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                ToastUtil.showCusToast(NewSearchActivity.this,getResources().getString(R.string.miamacuo));
//                            }
//                        });
                        ToastUtil.showShort(NewSearchActivity.this, getResources().getString(R.string.miamacuo));
                        // ToastUtil.showLong(NewSearchActivity.this, getResources().getString(R.string.miamacuo));
                    }

                    @Override
                    public void verPwdSucc() {
                        cusInputDialogView.dismiss();
                    }
                });
            }
        });

    }


    //返回按键
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            startActivity(B31HomeActivity.class);
            finish();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @SuppressLint("MissingPermission")
    @OnClick({R.id.newSearchTitleLeft, R.id.newSearchRightImg1})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.newSearchTitleLeft:   //返回
                startActivity(B31HomeActivity.class);
                finish();
                break;
            case R.id.newSearchRightImg1: //帮助
                if (bluetoothAdapter != null && !bluetoothAdapter.isDiscovering()) {
                    scanBlueDevice(false);
                }
                startActivity(SearchExplainActivity.class);
                break;
        }
    }


    //判断系统语言
    private String verLanguage() {
        //语言
        String locals = Locale.getDefault().getLanguage();
        if (!WatchUtils.isEmpty(locals)) {
            if (locals.equals("zh")) {
                return "配对中...";
            } else {
                return "Pairing..";
            }
        } else {
            return "Pairing..";
        }

    }

}
