package com.guider.health.common.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.ArrayList;



/**
        *网络改变监听管理类
        *该类去注册BroadReceiver来监听网络的改变,不需要再在所有需要监听网络变化的类去亲自注册broadreceiver.
        *只需要实现NetStateListener接口,然后调用该类的addOnNetworkStateListener();即可在重写的onNetworkState方法中获取到最新的网络状态
        *如果不需要再继续监听 调用removeOnNetworkStateListener()即可.
        *
        *
        */

public class NetStateController {

    private final static String NET_STATE = "";

    private final static String TAG = NetStateController.class.getSimpleName();
    private final static boolean DEBUG = true;
    private static ArrayList<NetworkType> mNetworkTypes = new ArrayList<NetworkType>();


    //判断是不是有网路连接   有网时返回true，没网时返回false
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 在网络状态发生变化的地方调用 {@link OnNetworkStateListener#onNetworkState(NetworkType)}
     * 方法
     */
    /**
     * 观察者list
     */
    private ArrayList<NetStateListener> mNetworkStateListeners = new ArrayList<NetStateListener>();

    private static NetStateController manager;
    private static Context mContext;

    private NetStateController() {
    }

    ;

    /**
     * 获取{@link NetStateController}唯一实例
     *
     * @return 返回{@link NetStateController}唯一实例
     */
    public static NetStateController getInstance(Context context) {
        if (manager == null) {
            synchronized (NetStateController.class) {
                if (manager == null) {
                    manager = new NetStateController();
                    if (context != null)
                        mContext = context.getApplicationContext();
                }
                return manager;
            }
        }
        return manager;

    }

    /**
     * 启动网络类型监听,startService
     */
    private void startNetworkTypeService() {
        if (DEBUG) {
            Log.i(TAG, "startNetworkTypeService()");
        }
        if (mContext == null)
            throw new NullPointerException("mContext is null");
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mContext.registerReceiver(mReceiver, mFilter);

    }

    private void stopNetworkTypeService() {
        if (DEBUG) {
            Log.i(TAG, "stopNetworkTypeService");
        }
        if (mContext == null)
            throw new NullPointerException("mContext is null");
        if (mReceiver != null) {
            try {
                mContext.unregisterReceiver(mReceiver);
            } catch (Exception e) {
                Log.w(TAG, "Receiver not registered ");
            }
        }
    }

    /**
     * 往观察者队列中添加观察者
     */
    public void addOnNetworkStateListener(NetStateListener mListener) {
        if (mNetworkStateListeners != null) {
            // 如果为0 则调用启动系统上下文 注册全局监听。
            if (mNetworkStateListeners.size() == 0)
                startNetworkTypeService();
            if (mNetworkStateListeners.indexOf(mListener) == -1) {
                mNetworkStateListeners.add(mListener);
            }

        }
    }

    /**
     * 移除一个观察者
     */
    public void removeOnNetworkStateListener(NetStateListener mListener) {
        if (mNetworkStateListeners != null) {
            int index = mNetworkStateListeners.indexOf(mListener);
            if (index >= 0) {
                mNetworkStateListeners.remove(index);
                if (mNetworkStateListeners.size() == 0) {
                    stopNetworkTypeService();
                    mNetworkTypes.clear();
                }
            }

        }
    }

    /**
     * 移除全部观察者
     */
    public void removeAllListener() {
        if (mNetworkStateListeners != null) {
            mNetworkStateListeners.clear();
            stopNetworkTypeService();
        }
    }

    /**
     * 介乎
     */
    /**
     * 枚举网络类型</br> NET_NO：没有网络</br> NET_2G：2g网络</br> NET_3G：3g网络</br>
     * NET_4G：4g网络</br> NET_WIFI：wifi</br> NET_UNKNOWN：未知网络
     */
    public enum NetworkType {
        NET_NO, NET_WIFI, NET_2G, NET_3G, NET_4G, NET_UNKNOWN;
    }

    /**
     * 网络类型 主要用来区分 外网(包括普通wifi和移动网络),i-Liaoning,以及CMMB. 新增了本地类型
     */
    public enum ChannelType {
        Public_Type, ILiaoning_Type, CMMB_TYPE, NO_TYPE, UNKNOWN_TYPE, LOCAL_TYPE;
    }

    /**
     * 判断当前是否网络连接
     *
     * @return 状态码
     */
    public static NetworkType isConnected(Context context) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = mConnectivityManager.getActiveNetworkInfo();

        NetworkType stateCode = NetworkType.NET_NO;
        if (ni != null && ni.isConnectedOrConnecting() && ni.isAvailable()) {
            switch (ni.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    stateCode = NetworkType.NET_WIFI;
                    break;
                case ConnectivityManager.TYPE_MOBILE:
                    switch (ni.getSubtype()) {
                        case TelephonyManager.NETWORK_TYPE_GPRS: // 联通2g
                        case TelephonyManager.NETWORK_TYPE_CDMA: // 电信2g
                        case TelephonyManager.NETWORK_TYPE_EDGE: // 移动2g
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            stateCode = NetworkType.NET_2G;
                            break;
                        case TelephonyManager.NETWORK_TYPE_EVDO_A: // 电信3g
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                            //NETWORK_TYPE_EHRPD
                        case 14:
                            //NETWORK_TYPE_HSPAP
                        case 15:
                            stateCode = NetworkType.NET_3G;
                            break;
                        //NETWORK_TYPE_LTE
                        case 13:
                            stateCode = NetworkType.NET_4G;
                            break;
                        default:
                            stateCode = NetworkType.NET_UNKNOWN;
                    }
                    break;
                default:
                    stateCode = NetworkType.NET_UNKNOWN;
            }
        }
        return stateCode;
    }

    /**
     * 获得当前的类型
     */
    public static String getTheChannelType(Context context) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = mConnectivityManager.getActiveNetworkInfo();
        ChannelType mChannelType = ChannelType.UNKNOWN_TYPE;
        if (ni != null && ni.isConnectedOrConnecting() && ni.isAvailable()) {
            switch (ni.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    WifiInfo mWifiInfo = null;
                    if (mWifiManager != null) {
                        mWifiInfo = mWifiManager.getConnectionInfo();
                        String wifiName = mWifiInfo.getSSID();
                        if (wifiName.contains("i-LiaoNing")) {
                            mChannelType = ChannelType.ILiaoning_Type;
                        } else if (wifiName.contains("CMMB")) {
                            mChannelType = ChannelType.CMMB_TYPE;
                        } else {
                            mChannelType = ChannelType.Public_Type;
                        }
                    }
                    break;
                case ConnectivityManager.TYPE_MOBILE:
                    switch (ni.getSubtype()) {
                        case TelephonyManager.NETWORK_TYPE_GPRS: // 联通2g
                        case TelephonyManager.NETWORK_TYPE_CDMA: // 电信2g
                        case TelephonyManager.NETWORK_TYPE_EDGE: // 移动2g
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            mChannelType = ChannelType.Public_Type;
                            break;
                        case TelephonyManager.NETWORK_TYPE_EVDO_A: // 电信3g
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                            //NETWORK_TYPE_EHRPD
                        case 14:
                            //NETWORK_TYPE_HSPAP
                        case 15:
                            mChannelType = ChannelType.Public_Type;
                            break;
                        //NETWORK_TYPE_LTE
                        case 13:
                            mChannelType = ChannelType.Public_Type;
                            break;
                        default:
                            mChannelType = ChannelType.UNKNOWN_TYPE;
                            break;
                    }

            }
            return mChannelType.name();
        } else {
            mChannelType = ChannelType.NO_TYPE;
            return mChannelType.name();
        }
    }

    /**
     * 监听网络类型改变的广播
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

                NetworkType netState = isConnected(context);
                synchronized (NetStateController.class) {
                    mNetworkTypes.add(netState);
                    if (mNetworkStateListeners != null && whetherOrNotoprocess()) {
                        for (NetStateListener listener : mNetworkStateListeners) {
                            listener.onNetworkState(netState);
                        }
                    }
                }

                if (DEBUG) {
                    Log.i(TAG,
                            "Network status changes,the current network is : "
                                    + netState);
                }
            }
        }
    };

    private static boolean whetherOrNotoprocess() {
        int mTypesSize = mNetworkTypes.size();
        switch (mTypesSize) {
            case 0:
                return false;
            case 1:
                return true;
            default:
                if (mNetworkTypes.get(mTypesSize - 1).toString().equals(mNetworkTypes.get(mTypesSize - 2).toString())) {
                    return false;
                } else {
                    return true;
                }

        }
    }

}

