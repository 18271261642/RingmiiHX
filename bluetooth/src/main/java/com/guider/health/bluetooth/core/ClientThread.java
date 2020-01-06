package com.guider.health.bluetooth.core;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;

/**
 * Created by haix on 2019/5/14.
 */

public class ClientThread implements Runnable {

    private static final String TAG = "ClientThread";

    BluetoothDevice device;
    BluetoothSocket socket;
    OutputStream out;
    InputStream in;
    SocketConnectListener mConnectBack;

    public void setSocketConnectListener(SocketConnectListener ConnectBack) {
        this.mConnectBack = ConnectBack;
    }

    public static void toSocketConnectDevice(BluetoothDevice device, SocketConnectListener mConnectBack){

        if (device != null){

            try {
                Bluetooth.getInstance().checkBlueToothAndTurnOn();
                Bluetooth.getInstance().stopDiscoveringBluetooth();

                BluetoothSocket socket = device.createRfcommSocketToServiceRecord(UUID.fromString(Params.UUID));



                try {

                    if (socket != null){
                        socket.connect();
                    }

                } catch (IOException connectException) {
                    Log.i("haix",  " run3----异常了-------");
                    try {
                        socket.close();
                        mConnectBack.connectfaile(device);
                        return;
                    } catch (IOException closeException) {

                    }
                }
                if (socket != null && socket.isConnected()) {
                    OutputStream out = socket.getOutputStream();
                    InputStream in = socket.getInputStream();

                    Log.i("haix",  " run2----连接成功connectsuccess-------");
                    mConnectBack.connectsuccess(socket, device, out, in);

                } else {
                    Log.i("haix",  " socket有问题-------");
                    mConnectBack.connectfaile(device);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ClientThread(BluetoothDevice device) {

        this.device = device;


        try {
            if (device != null){

                socket = device.createRfcommSocketToServiceRecord(UUID.fromString(Params.UUID));
            }else{

            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void run() {

        Log.i("haix",  " run1-----------"+this);

        if (Bluetooth.getInstance().bluetoothAdapter.isDiscovering()){
            Bluetooth.getInstance().cancelDiscovery();
        }


        try {
            try {

                if (socket != null){
                    socket.connect();
                }

            } catch (IOException connectException) {
                Log.i("haix",  " run3----异常了-------"+this);
                try {
                    socket.close();
                    mConnectBack.connectfaile(device);
                    return;
                } catch (IOException closeException) {

                }
            }
            if (socket != null && socket.isConnected()) {
                out = socket.getOutputStream();
                in = socket.getInputStream();

                Log.i("haix",  " run2----connectsuccess-------"+this);
                mConnectBack.connectsuccess(socket, device, out, in);

            } else {
                Log.i("haix",  " socket有问题-------"+this);
                mConnectBack.connectfaile(device);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public interface SocketConnectListener {

        void connectsuccess(BluetoothSocket socket, BluetoothDevice device, OutputStream out, InputStream in);

        void connectfaile(BluetoothDevice device);

    }

}
