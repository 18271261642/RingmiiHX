package com.guider.health.bluetooth.core;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import java.util.List;
import java.util.Set;


/**
 * Created by haix on 2019/5/30.
 * <p>
 * 搜索设备 -> 连接 -> 绑定设备(不用重新绑定) -> 连接成功!
 * 如果是已经绑定过的设备: device.getBondState() == BluetoothDevice.BOND_BONDED
 * 通过bluetoothAdapter.getBondedDevices()获取已经绑定过的设备, 不能重新绑定
 */

public class Bluetooth {

    private final static String TAG = "Bluetooth";

    BluetoothAdapter bluetoothAdapter;

    private final String ACTIONFILTER = "android.bluetooth.device.action.PAIRING_REQUEST";
    String pin = "1234";  //此处为你要连接的蓝牙设备的初始密钥，一般为1234或0000


    private Bluetooth() {

    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case Params.MY_PERMISSION_REQUEST_CONSTANT: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 运行时权限已授权
                    Log.i("Bluetooth", "运行时权限已授权");
                }
                return;
            }
        }
    }

    private final static Bluetooth openBlueTooth = new Bluetooth();


    public static Bluetooth getInstance() {
        return openBlueTooth;
    }

    public BluetoothAdapter checkSupportBlueTooth() {
        if (bluetoothAdapter == null) {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null) {
                Log.i("haix", "--------------- 不支持蓝牙");
                return null;
            }
        }
        return bluetoothAdapter;
    }

    private MyBtReceiver myBtReceiver;
    public void initBluetooth(Context context) {
        checkSupportBlueTooth();

        if (myBtReceiver == null){
            myBtReceiver = new MyBtReceiver();
            checkBlueToothAndTurnOn();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
            intentFilter.addAction(ACTIONFILTER);
            intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            context.registerReceiver(myBtReceiver, intentFilter);
       }

    }

    public void unReceiver(Context context){
//        try {
//            if (context != null && myBtReceiver != null){
        //会报没有注册错误, 捕获不到
//                context.unregisterReceiver(myBtReceiver);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }


    //    if (!mBluetoothAdapter.isEnabled()) {
//        mBluetoothAdapter.enable();
//    }
    public boolean checkBlueToothAndTurnOn() {
        // 蓝牙未打开，询问打开
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
//            Intent turnOnBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            ((Activity) context).startActivityForResult(turnOnBtIntent, Params.REQUEST_ENABLE_BT);
            bluetoothAdapter.enable();

            return false;

        }
        return true;
    }

    public void setBluetoothEnableVisibility(Context context) {
        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        enableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 600);
        ((Activity) context).startActivityForResult(enableIntent, Params.REQUEST_ENABLE_VISIBILITY);
    }

    public void stopDiscoveringBluetooth(){
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }

    }


    private static final String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
    public void searchBluetoothDevices(Context context) {

        checkSupportBlueTooth();

        stopDiscoveringBluetooth();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int i = context.checkSelfPermission(permissions[0]);
            if (i != PackageManager.PERMISSION_GRANTED) {
                ((Activity) context).requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Params.MY_PERMISSION_REQUEST_CONSTANT);
            }
        }


        bluetoothAdapter.startDiscovery();
    }

    public void closeBluetooth() {
        bluetoothAdapter.disable();
    }

    public void cancelDiscovery() {
        if (bluetoothAdapter != null && bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
    }

    /**
     * 用户打开蓝牙后，显示已绑定的设备列表
     */
    public void getBondDevices(List deviceList) {

        checkBlueToothAndTurnOn();

        deviceList.clear();
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()){
            Set<BluetoothDevice> tmp = bluetoothAdapter.getBondedDevices();
            for (BluetoothDevice d : tmp) {
                deviceList.add(d);
            }
        }
    }

    public BluetoothAdapter getBluetoothAdapter(){
        return bluetoothAdapter;
    }


//    //得到配对的设备列表，清除已配对的设备
//    public void removePairDevice(){
//        if(bluetoothAdapter!=null){
//            Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
//            for(BluetoothDevice device : bondedDevices ){
//                unpairDevice(device);
//            }
//        }
//
//    }
//
//    //反射来调用BluetoothDevice.removeBond取消设备的配对
//    private void unpairDevice(BluetoothDevice device) {
//        try {
//            Method m = device.getClass()
//                    .getMethod("removeBond", (Class[]) null);
//            m.invoke(device, (Object[]) null);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    /**
     * 广播接受器
     */
    private class MyBtReceiver extends BroadcastReceiver {


        public void SetPairlistener() {
//            this.mMakePariListener = makePariBlueToothListener;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            Log.i(TAG, "action的值为: " + action);
            BluetoothDevice btDevice = null;  //创建一个蓝牙device对象
            // 从Intent中获取设备对象
            btDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (makePairBlueToothListener == null) {
                return;
            }
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Log.i("haix", "开始搜索 ...");

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.i("haix", "搜索结束");
                makePairBlueToothListener.scanFinish(btDevice);

            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (btDevice != null && btDevice.getName() != null && btDevice.getBondState() != BluetoothDevice.BOND_BONDED) {

                    //Log.i(TAG, "发现设备: " + btDevice.getName());
                    makePairBlueToothListener.foundDevice(btDevice);
                }


            } else if (ACTIONFILTER.equals(action)) {
                Log.e("action2=", action);
                Log.e("here", "btDevice.getName()");
                Log.i("haix", "配对请求: "+ action + " ---------------");
                try {
                    //1.确认配对
                    ClsUtils.setPairingConfirmation(btDevice.getClass(), btDevice, true);
                    //2.终止有序广播
                    Log.i("order...", "终止有序广播:" + isOrderedBroadcast() + ",isInitialStickyBroadcast:" + isInitialStickyBroadcast());
                    abortBroadcast();//如果没有将广播终止，则会出现一个一闪而过的配对框。
                    //3.调用setPin方法进行配对...
                    boolean ret = ClsUtils.setPin(btDevice.getClass(), btDevice, pin);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {

                switch (btDevice.getBondState()) {
                    case BluetoothDevice.BOND_BONDING:// 正在配对
                        makePairBlueToothListener.pairing(btDevice);
                        Log.i(TAG, " 正在配对: " + btDevice.getName());
                        break;
                    case BluetoothDevice.BOND_BONDED:// 配对结束
                        makePairBlueToothListener.pairingSuccess(btDevice);
                        Log.i(TAG, "配对完成: " + btDevice.getName());
                        break;
                    case BluetoothDevice.BOND_NONE:// 取消配对/未配对

                        Log.i(TAG, " 取消配对/未配对: " + btDevice.getName());
                    default:
                        break;
                }
            }
        }
    }


    MakePairBlueToothListener makePairBlueToothListener;

    public void setMakePairBlueToothListener(MakePairBlueToothListener makePairBlueToothListener) {
        this.makePairBlueToothListener = makePairBlueToothListener;
    }

    public interface MakePairBlueToothListener {
        void foundDevice(BluetoothDevice btDevice);

        void pairing(BluetoothDevice btDevice);

        void pairingSuccess(BluetoothDevice btDevice);

        void scanFinish(BluetoothDevice btDevice);
    }


}