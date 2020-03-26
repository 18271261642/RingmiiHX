package com.guider.glu.presenter;

import com.guider.health.bluetooth.core.DeviceUUID;

import java.util.UUID;

/**
 * Created by haix on 2019/6/10.
 */

public class GLUDeviceUUID implements DeviceUUID {

    //总
    public static final UUID SERVICE_UUID = UUID.fromString("0000FFB0-0000-1000-8000-00805f9b34fb");
    //次
    public  static final UUID CHARACTER_UUID = UUID.fromString("0000FFB1-0000-1000-8000-00805f9b34fb");
    public  static final UUID DATA_LINE_UUID = UUID.fromString("0000FFB2-0000-1000-8000-00805f9b34fb");
    //0000ffb2-0000-1000-8000-00805f9b34fb
    //不变
    public  static final  UUID UUID_FINAL = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    @Override
    public UUID getSERVICE_UUID() {
        return SERVICE_UUID;
    }

    @Override
    public UUID getCHARACTER_UUID() {
        return CHARACTER_UUID;
    }

    @Override
    public UUID getDATA_LINE_UUID() {
        return DATA_LINE_UUID;
    }

    @Override
    public UUID getUUID_FINAL() {
        return UUID_FINAL;
    }
}
