package bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.inuker.bluetooth.library1.old.BluetoothClient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import ble.BleClient;
import ble.SimpleDevice;
import ble.callback.IConnectCallback;
import ble.callback.IDisConnectCallback;

public class ClassicBluetoothClient {

    // 蓝牙2.0通用UUID
    private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    // 连接工具类
    BluetoothClient client;
    // 断开连接监听
    private IDisConnectCallback disConnectCallback;

    public ClassicBluetoothClient(BluetoothClient client) {
        this.client = client;
    }

    private BluetoothSocket mmClientSocket = null;
    private DataInputStream is = null;
    private DataOutputStream os = null;

    boolean isConnecting = false;

    public synchronized void connect(final SimpleDevice device, final IConnectCallback callback) {
        if (isConnecting || mmClientSocket != null) {
            if (callback != null) {
                callback.onConnectFail("蓝牙正在连接或已存在的连接");
            }
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                isConnecting = true;
                BleClient.instance().recordDevice(device);
                if (callback != null) {
                    callback.onConnectStart(device);
                }

                try {
                    BluetoothDevice d = device.deviceInfo.device;
                    mmClientSocket = d.createRfcommSocketToServiceRecord(uuid);
                    mmClientSocket.connect();
                    if (callback != null) {
                        if (mmClientSocket.getOutputStream() != null && mmClientSocket.getInputStream() != null) {
                            callback.onConnectSuccess(device);
                        } else {
                            if (mmClientSocket != null) {
                                mmClientSocket.close();
                            }
                            callback.onConnectFail("蓝牙连接失败");
                        }
                    }
                } catch (IOException e) {
                    try {
                        mmClientSocket = (BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(device, 1);
                        mmClientSocket.connect();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        callback.onConnectFail("蓝牙连接失败");
                    }
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onConnectFail("蓝牙连接失败");
                }finally {
                    isConnecting = false;
                }
            }
        }).start();
    }

    public void write(byte[] bytes) {
        try {
            if (os == null && mmClientSocket != null) {
                os = new DataOutputStream(mmClientSocket.getOutputStream());
            }
            if (os != null) {
                os.write(bytes);
                os.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] read() {
        try {
            if (is == null && mmClientSocket != null) {
                is = new DataInputStream(mmClientSocket.getInputStream());
            }
            if (is != null) {
                int length = is.available();
                if (length <= 0)
                    return null;
                byte[] bytes = new byte[length];
                is.read(bytes);
                return bytes;
            }
            return new byte[0];
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public BluetoothSocket getSocket() {
        return mmClientSocket;
    }

    public void close() {
        try {
            if (mmClientSocket != null) {
                if (is != null) {
                    is.close();
                }
                if (os != null) {
                    os.close();
                }
                mmClientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            is = null;
            os = null;
            mmClientSocket = null;
            isConnecting = false;
        }
    }

    public static void boud(Context context, BluetoothDevice device) {
        try {
            ClsUtils.createBond(device.getClass(), device);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void boudOfPin(Context context , final BluetoothDevice device , final String pin) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        // 注册广播接收器，接收并处理搜索结果
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
               if(BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action)){
                   try {
                       //如果不结束广播接收，配对界面会闪出
                       abortBroadcast();
                       //顺序一定要这样，否则会出问题
                       ClsUtils.setPin(device.getClass(), device, pin);
                       //这行代码会在控制台报错
                       //ClsUtils.setPairingConfirmation(bluetoothDevice.getClass(), bluetoothDevice,true);
                   }catch (Exception e){
                       e.printStackTrace();
                   }
                }
            }
        }, intentFilter);
        try {
            ClsUtils.createBond(device.getClass(), device);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
