package com.guider.healthring.b30.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.guider.healthring.Commont;
import com.guider.healthring.MyApp;
import com.guider.healthring.R;
import com.guider.healthring.bleutil.MyCommandManager;
import com.guider.healthring.siswatch.utils.WatchUtils;
import com.guider.healthring.util.AppUtils;
import com.guider.healthring.util.SharedPreferencesUtils;
import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.Code;
import com.inuker.bluetooth.library.Constants;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.veepoo.protocol.listener.base.IABleConnectStatusListener;

import java.io.IOException;

public class NewB30ConnStateService extends Service {

    private static final String TAG = "NewB30ConnStateService";
    private static final int SEARCH_REQUEST_CODE = 1001;    //扫描
    private static final int AUTO_CONN_REQUEST_CODE = 1002; //非手动断开
    static final int AUTO_CONNECT_CODE = 1003;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothClient bluetoothClient;
    private final IBinder mLocalBinder = new B30LoadBuilder();
    private NewConnBleHelpService connBleHelpService;
    public B30ConnStateListener b30ConnStateListener;

    //震动
    private Vibrator mVibrator;
    private MediaPlayer mMediaPlayer;


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case AUTO_CONN_REQUEST_CODE:  //非手动断开消息
                    handler.removeMessages(AUTO_CONN_REQUEST_CODE);
                    String bleMacs = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), Commont.BLEMAC);
//                    if(WatchUtils.isEmpty(bleMacs))
//                        return;
//                    Log.e(TAG, "-----读取的地址=" + bleMacs + "--=" + MyCommandManager.DEVICENAME);
                    //非手动断开
                    if (!WatchUtils.isEmpty(bleMacs)) {
//                        MyLogUtil.e(TAG, "----非手动断开----");
                        connectAutoConn(true);
                    } else {  //手动断开
//                        MyLogUtil.e(TAG, "----手动断开----");
                    }

                    break;
                case SEARCH_REQUEST_CODE:  //搜索返回
                    handler.removeMessages(SEARCH_REQUEST_CODE);
                    String bleMacss = (String) SharedPreferencesUtils.readObject(
                            MyApp.getContext(), Commont.BLEMAC);
                    if (!WatchUtils.isEmpty(bleMacss)) {
                        SearchResult searchResult = (SearchResult) msg.obj;
                        if (searchResult != null) {
                            if (!WatchUtils.isEmpty(searchResult.getAddress()) &&
                                    searchResult.getName() != null) {
                                //Log.e(TAG, "----hand-msg=" + searchResult.getAddress() + (searchResult.getAddress().equals(bleMacss.trim())));
                                if (!WatchUtils.isEmpty(searchResult.getAddress()) &&
                                        searchResult.getAddress().equals(bleMacss.trim())) {
                                    Log.e(TAG, "----相等了----");
                                    if (bluetoothClient != null) {
                                        bluetoothClient.stopSearch();
                                    }
                                    connB30ConnBle(bleMacss, searchResult.getName().trim());
                                }
                            }
                        }
                    }
                    break;

                case AUTO_CONNECT_CODE:
                    handler.removeMessages(AUTO_CONNECT_CODE);
                    initBlue();
                    connectAutoConn(true);
                    break;
            }

        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        registerConnState();
        initBlue();

        regeditBackService();
    }

    private void initBlue() {
        bluetoothClient = new BluetoothClient(MyApp.getContext());
        BluetoothManager bluetoothManager = (BluetoothManager) MyApp.getContext().getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager != null) {
            bluetoothAdapter = bluetoothManager.getAdapter();
        }
    }

    //注册广播
    private void registerConnState() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WatchUtils.B30_CONNECTED_ACTION);
        intentFilter.addAction(WatchUtils.B30_DISCONNECTED_ACTION);
        intentFilter.addAction(WatchUtils.B31_CONNECTED_ACTION);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(broadcastReceiver, intentFilter);
    }


    // TODO 连接
    public void connB30ConnBle(final String mac, final String nameStr) {
        MyApp.getInstance().getVpOperateManager().registerConnectStatusListener(mac,
                iaBleConnectStatusListener);
        MyApp.getInstance().getVpOperateManager().connectDevice(mac, (i, bleGattProfile, b) -> {
            Log.e(TAG, "----connectState=" + i);
            if (i == Code.REQUEST_SUCCESS) {  //连接成功过
                Log.d("----去自动链接-", "go go go7" + b + "----" + i);
                if (bluetoothClient != null) {
                    bluetoothClient.stopSearch();
                }
            }
        }, i -> {
            Log.e(TAG, "----notifyState=" + i);
            Log.d("----去自动链接-", "go go go8" + "----" + i);
            if (i == Code.REQUEST_SUCCESS) {
                if (connBleHelpService == null) {
                    connBleHelpService = NewConnBleHelpService.getConnBleHelpService();
                }
                // connBleHelpService = connBleHelpService.getConnBleHelpService();
                connBleHelpService.setConnBleHelpListener(() -> {
                    MyCommandManager.DEVICENAME = nameStr;
                    MyCommandManager.ADDRESS = mac;
                    MyApp.getInstance().setMacAddress(mac);
                    SharedPreferencesUtils.saveObject(MyApp.getContext(), Commont.BLENAME, nameStr);
                    SharedPreferencesUtils.saveObject(MyApp.getContext(), Commont.BLEMAC, mac);
                    //连接成就发送广播
                    Intent intent = new Intent();
                    intent.setAction(WatchUtils.B31_CONNECTED_ACTION);
                    //500s，600z
                    if (nameStr.equals("500S") || nameStr.equals("600Z") ||
                            nameStr.equals("B31") || nameStr.equals("B31S")) {
                        //B31的连接
                        intent.setAction(WatchUtils.B31_CONNECTED_ACTION);
                    } else {
                        //B30、B36、盖德
                        intent.setAction(WatchUtils.B30_CONNECTED_ACTION);
                    }
                    sendBroadcast(intent);
                });
                NewConnBleHelpService.getConnBleHelpService().doConnOperater(mac);
            }
        });
    }

    //停止自动搜索
    public void stopAutoConn() {
        if (bluetoothClient != null)
            bluetoothClient.stopSearch();
    }


    /**
     * 验证设备的密码，此方法必须在connB30ConnBle（）方法后调用
     *
     * @param pwd
     * @param //无用的参数
     */
    public void continuteConn(String pwd, VerB30PwdListener verB30PwdListener) {
        if (connBleHelpService != null)
            connBleHelpService.doConnOperater(pwd, verB30PwdListener);
    }


    //同步用户信息和设备目标步数
    public void setUserInfoToDevice() {
        if (connBleHelpService != null)
            connBleHelpService.setDeviceUserData();
    }

    //自动连接
    @SuppressLint("MissingPermission")
    public void connectAutoConn(boolean isScan) {
        if (isScan) {
            if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
                //bluetoothAdapter.startLeScan(leScanCallback);
                SearchRequest request = (new SearchRequest.Builder())
                        .searchBluetoothLeDevice(Integer.MAX_VALUE, 2)
                        .build();
                bluetoothClient.search(request, new SearchResponse() {
                    @Override
                    public void onSearchStarted() {
                        Log.e(TAG, "-----开始扫描----");
                    }

                    @Override
                    public void onDeviceFounded(SearchResult searchResult) {
                        if (searchResult != null) {
                            Log.e(TAG, "----onDeviceFound=" + searchResult.getName()
                                    + "-mac=" + searchResult.getAddress());
                            Message message = handler.obtainMessage();
                            message.what = SEARCH_REQUEST_CODE;
                            message.obj = searchResult;
                            handler.sendMessage(message);
                        }

                    }

                    @Override
                    public void onSearchStopped() {
                        Log.e(TAG, "----扫描停止----");
                    }

                    @Override
                    public void onSearchCanceled() {
                        Log.e(TAG, "----扫描关闭----");
                    }
                });
            }
        } else {
            if (bluetoothClient != null) {
                bluetoothClient.stopSearch();
            }
        }
    }


    //监听蓝牙连接或断开的状态
    private IABleConnectStatusListener iaBleConnectStatusListener = new IABleConnectStatusListener() {
        @Override
        public void onConnectStatusChanged(String s, int state) {
            switch (state) {
                case Constants.STATUS_CONNECTED:    //已连接
                    Log.e(TAG, "-----监听--conn");
                    findPhoneListenerData();

                    break;
                case Constants.STATUS_DISCONNECTED: //已断开
                    Log.e(TAG, "-----监听--disconn");
                    Intent intents = new Intent();
                    intents.setAction(WatchUtils.B30_DISCONNECTED_ACTION);
                    sendBroadcast(intents);
//                    if(b30ConnStateListener != null){
//                        b30ConnStateListener.onB30Disconn();
//                    }
                    MyCommandManager.ADDRESS = null;// 断开连接了就设置为null
                    MyCommandManager.DEVICENAME = null;
                    handler.sendEmptyMessage(AUTO_CONN_REQUEST_CODE);
                    break;
            }
        }
    };

    //监听查找手机
    private void findPhoneListenerData() {
        MyApp.getInstance().getVpOperateManager().settingFindPhoneListener(() -> {
            try {
                mVibrator = (Vibrator) MyApp.getContext().getSystemService(Service.VIBRATOR_SERVICE);
                mMediaPlayer = new MediaPlayer();
                AssetFileDescriptor file = MyApp.getContext().getResources().openRawResourceFd(R.raw.music);
                try {
                    mMediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(),
                            file.getLength());
                    mMediaPlayer.prepare();
                    file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mMediaPlayer.setVolume(0.5f, 0.5f);
                mMediaPlayer.setLooping(false);
                mMediaPlayer.start();
                if (mVibrator.hasVibrator()) {
                    //想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到
                    mVibrator.vibrate(new long[]{500, 1000, 500, 1000}, -1);//查找手机是调用系统震动
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    public void setB30ConnStateListener(B30ConnStateListener b30ConnStateListener) {
        this.b30ConnStateListener = b30ConnStateListener;
    }

    public interface B30ConnStateListener {
        void onB30Connect();

        void onB30Disconn();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (broadcastReceiver != null) {
                unregisterReceiver(broadcastReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mLocalBinder;
    }


    public class B30LoadBuilder extends Binder {
        public NewB30ConnStateService getB30Service() {
            return NewB30ConnStateService.this;
        }
    }

    /**
     * 监听蓝牙打开和关闭的广播
     */
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null)
                return;
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int bleState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                Log.e(TAG, "------bleState=" + bleState);
                String b30Name = (String) SharedPreferencesUtils.readObject(MyApp.getInstance().getApplicationContext(), Commont.BLENAME);
                String bMac = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), Commont.BLEMAC);
                if (WatchUtils.isEmpty(bMac))
                    return;
                switch (bleState) {
                    case BluetoothAdapter.STATE_TURNING_ON: //蓝牙打开 11
                        if (!WatchUtils.isEmpty(b30Name) && WatchUtils.isVPBleDevice(b30Name)) {
                            handler.sendEmptyMessage(AUTO_CONNECT_CODE);
                        }
                        break;
                    case BluetoothAdapter.STATE_ON:
                        if (!WatchUtils.isEmpty(b30Name) && WatchUtils.isVPBleDevice(b30Name)) {
                            handler.sendEmptyMessage(AUTO_CONNECT_CODE);
                        }
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:    //蓝牙关闭 13
                        if (!WatchUtils.isEmpty(b30Name) && WatchUtils.isVPBleDevice(b30Name)) {
                            //handler.sendEmptyMessage(H8BleConstances.END_AUTO_CONN_CODE);
                            handler.sendEmptyMessage(AUTO_CONN_REQUEST_CODE);
                            MyCommandManager.ADDRESS = null;// 断开连接了就设置为null
                            MyCommandManager.DEVICENAME = null;
                            MyCommandManager.DEVICENAME = null;
                        }

                        break;
                }
            }
        }

    };

    //启动前台服务
    private void regeditBackService() {
        try {
            String appName = AppUtils.getAppName(getApplicationContext());
            Bitmap appIcon = AppUtils.getBitmap(getApplicationContext());
            NotificationCompat.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String channelId = "1001";
                NotificationChannel channel = new NotificationChannel(channelId,
                        "guider_hx", NotificationManager.IMPORTANCE_LOW);
                NotificationManager manager = (NotificationManager) getSystemService(
                        Context.NOTIFICATION_SERVICE);
                if (manager == null)
                    return;
                manager.createNotificationChannel(channel);
                builder = new NotificationCompat.Builder(this,
                        channelId);
            } else {
                builder = new NotificationCompat.Builder(this);
            }
            Notification notification = builder
                    //禁止滑动删除
                    .setOngoing(true)
                    .setContentTitle(appName)
                    .setContentText(appName)
                    .setLargeIcon(appIcon)
                    .setAutoCancel(false)
                    .build();
            startForeground(101, notification);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "前台服务 ------" + e.getMessage());
        }

//        try {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                String channelID = "12";
//                String channelName = "channel_name";
//                NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);
//                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                manager.createNotificationChannel(channel);
//                Notification.Builder builder = new Notification.Builder(this);
//
//                //Notification builder = new Notification();
//
////            builder.setSmallIcon(R.drawable.ic_noti_s);
//                builder.setSmallIcon(R.drawable.ic_gaide_launch);
//                builder.setContentText("Guider HX");
//                builder.setContentTitle("Guider HX");
//                //创建通知时指定channelID
//                builder.setChannelId(channelID);
//                Notification notification = builder.build();
//
////            // 通知行为（点击后能进入应用界面）
////            Intent intent = new Intent(this, B30HomeActivity.class);
////            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
////            builder.setContentIntent(pendingIntent);
//
//
//                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
//                notificationManagerCompat.notify(12, notification);
//                startForeground(12, notification);
//            } else {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
//                    NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//                    //Notification.Builder builder = new Notification.Builder(this,11);
//                    builder.setSmallIcon(R.drawable.ic_gaide_launch);
//                    builder.setContentText("Guider HX");
//                    builder.setContentTitle("Guider HX");
//                    // 设置通知的点击行为：自动取消/跳转等
//                    builder.setAutoCancel(false);
//                    startForeground(12, builder.build());
//                }
//
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }

    }
}
