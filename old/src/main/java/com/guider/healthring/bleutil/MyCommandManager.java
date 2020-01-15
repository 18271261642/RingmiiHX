package com.guider.healthring.bleutil;


import android.bluetooth.BluetoothDevice;
import java.util.UUID;


/**
 * Created by thinkpad on 2017/3/19.
 */

public class MyCommandManager {


    /** 设备连接状态 */
    public static boolean deviceConnctState = false;
    /** 是否手动断开连接若意外断开连接重连 true为正常断开，flase为非正常断开 */
    public static boolean deviceDisconnState = false;
    public static BluetoothDevice timeDevice = null;
    public static String deviceAddress = "";


    public static UUID UUID_SERVICE;
    public static UUID UUID_WRITE;
    public static UUID UUID_READ;
    public static String DESC;

    public static String DEVICENAME = null;     //判断蓝牙是否连接
    public static String ADDRESS = null;
    public static int CONNECTIONSTATE = 0;


    //是否是ota升级
    public static boolean isOta = false;


}
