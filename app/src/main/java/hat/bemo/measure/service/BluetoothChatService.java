package hat.bemo.measure.service;
/**
 * 描述：藍牙服務核心類
 */
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
public class BluetoothChatService {
    // 測試數據
    private static final String TAG = "BluetoothChatService";
    private static final boolean D = true;

    private static final String NAME = "BluetoothChat";

    // 聲明一個唯一的UUID
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");	//change by chongqing jinou	

    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;
    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int mState;

    // 常量,顯示當前的連接狀態
    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2; 
    public static final int STATE_CONNECTED = 3;
    
    public BluetoothChatService(Context context, Handler handler) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mHandler = handler;
    }
 
    /**
     * 設置當前的連接狀態
     * @param state  連接狀態
     */
    private synchronized void setState(int state) {
        if (D) Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;

        // 通知Activity更新UI
        mHandler.obtainMessage(BluetoothBaseActivity.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    /**
     * 返回當前連接狀態 
     * 
     */
    public synchronized int getState() {
        return mState;
    }

    /**
     *開始聊天服務
     *
     */
    public synchronized void start() {
        if (D) Log.d(TAG, "start");

        if (mConnectThread != null) {
        	mConnectThread.cancel(); 
        	mConnectThread = null;
        }

        if (mConnectedThread != null) {
        	mConnectedThread.cancel(); 
        	mConnectedThread = null;
        }

        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
        }
        setState(STATE_LISTEN);
    }

    /**
     * 連接遠程設備
     * @param device  連接
     */
    public synchronized void connect(BluetoothDevice device) {
        if (D) Log.d(TAG, "連接到:" + device);

        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
            	mConnectThread.cancel(); 
            	mConnectThread = null;
            }
        }

        if (mConnectedThread != null) {
        	mConnectedThread.cancel(); 
        	mConnectedThread = null;
        }

        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    /**
     * 啓動ConnectedThread開始管理一個藍牙連接
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        if (D) Log.d(TAG, "連接");

        if (mConnectThread != null) {
        	mConnectThread.cancel(); 
        	mConnectThread = null;
        }

        if (mConnectedThread != null) {
        	mConnectedThread.cancel(); 
        	mConnectedThread = null;
        }

        if (mAcceptThread != null) {
        	mAcceptThread.cancel(); 
        	mAcceptThread = null;
        }

        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        Message msg = mHandler.obtainMessage(BluetoothBaseActivity.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(BluetoothBaseActivity.DEVICE_NAME, device.getName());
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        setState(STATE_CONNECTED);
    }

    /**
     * 停止所有線程
     */
    public synchronized void stop() {
        if (D) Log.d(TAG, "stop");
        setState(STATE_NONE);
        if (mConnectThread != null) {
        	mConnectThread.cancel(); 
        	mConnectThread = null;
        }
        
        if (mConnectedThread != null) {
        	mConnectedThread.cancel(); 
        	mConnectedThread = null;
        }
        
        if (mAcceptThread != null) {
        	mAcceptThread.cancel(); 
        	mAcceptThread = null;
        }
    }

    /**
     * 以非同步方式寫入ConnectedThread
     * @param out 
     */
    public void write(byte[] out) {
        ConnectedThread r;
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }
        r.write(out);
    }

    /**
     * 無法連接，通知Activity
     */
    private void connectionFailed() {
        setState(STATE_LISTEN);
        
        Message msg = mHandler.obtainMessage(BluetoothBaseActivity.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(BluetoothBaseActivity.TOAST, "無法連接設備");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    /**
     * 設備斷開連接，通知Activity
     */
    private void connectionLost() {
 
        Message msg = mHandler.obtainMessage(BluetoothBaseActivity.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(BluetoothBaseActivity.TOAST, "設備斷開連接");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    /**
     * 監聽傳入的連接
     */
    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;

            try {
                tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (Exception e) {
                Log.e(TAG, "listen() failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            if (D) Log.d(TAG, "BEGIN mAcceptThread" + this);
            setName("AcceptThread");
            BluetoothSocket socket = null;

            while (mState != STATE_CONNECTED) {
                try {
                    socket = mmServerSocket.accept();
                } catch (Exception e) {
                    Log.e(TAG, "accept() 失敗", e);
                    break;
                }

                // 如果連接被接受
                if (socket != null) {
                    synchronized (BluetoothChatService.this) {
                        switch (mState) {
                        case STATE_LISTEN:
                        case STATE_CONNECTING:
                            //開始連接線程
                            connected(socket, socket.getRemoteDevice());
                            break;
                        case STATE_NONE:
                        case STATE_CONNECTED:
                            // 沒有準備好或已經連接
                            try {
                                socket.close();
                            } catch (IOException e) {
                                Log.e(TAG, "不能關閉這些連接", e);
                            }
                            break;
                        }
                    }
                }
            }
            if (D) Log.i(TAG, "結束mAcceptThread");
        }

        public void cancel() {
            if (D) Log.d(TAG, "取消 " + this);
            try {
                mmServerSocket.close();
            } catch (Exception e) {
                Log.e(TAG, "關閉失敗", e);
            }
        }
    }


    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;

            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "create() 失敗", e);
            }
            mmSocket = tmp;
        }

        @Override
        public void run() {
            Log.i(TAG, "開始mConnectThread");
            setName("ConnectThread");
            
            mAdapter.cancelDiscovery();

            try {
                mmSocket.connect();
            } catch (Exception e) {
                connectionFailed();
                try {
                    mmSocket.close();
                } catch (Exception e2) {
                    Log.e(TAG, "關閉連接失敗", e2);
                }
                BluetoothChatService.this.start();
                return;
            }

            synchronized (BluetoothChatService.this) {
                mConnectThread = null;
            }
            connected(mmSocket, mmDevice);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "關閉連接失敗", e);
            }
        }
    }

    /**
     * 處理所有傳入和傳出的傳輸
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "創建 ConnectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // 得到BluetoothSocket輸入和輸出流
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            } 
            mmInStream = tmpIn;
            mmOutStream = tmpOut;  
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            int bytes = 0;          
            //循環監聽消息
            while (true) {
                try {
                	byte[] buffer = new byte[58];             	
                    bytes = mmInStream.read(buffer);
//                  for(byte b: buffer){                   		                		              			 
//                			Log.e("meaDataList", b+"");                		
//                	} 
                    if(bytes > 0){                 	 
                    	mHandler.obtainMessage(BluetoothBaseActivity.MESSAGE_READ, buffer.length, -1, buffer).sendToTarget();           		       
                    }
                    else{
                        Log.e(TAG, "disconnected");
                        connectionLost();
                        
                        if(mState != STATE_NONE){
                            Log.e(TAG, "disconnected");
                        	BluetoothChatService.this.start();
                        }
                        break;
                    }
                } catch (Exception e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    
                    if(mState != STATE_NONE){
                    	// 在重新啓動監聽模式啓動該服務
                    	BluetoothChatService.this.start();
                    }
                    break;
                }
            }
        }

        /**
         * 寫入OutStream連接
         * @param buffer  要寫的字節
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);
                // 把消息傳給UI
                mHandler.obtainMessage(BluetoothBaseActivity.MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
}