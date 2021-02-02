//package com.guider.health.ybglu
//
//import com.guider.health.bluetooth.core.DeviceUUID
//import java.util.*
//
///**
// * @Package: com.guider.health.ybglu
// * @ClassName: YBGluDeviceUUID
// * @Description:
// * @Author: hjr
// * @CreateDate: 2021/1/12 17:35
// * Copyright (C), 1998-2021, GuiderTechnology
// */
//class YBGluDeviceUUID : DeviceUUID {
//
//    private val SERVICE_UUID = "00001000-0000-1000-8000-00805f9b34fb"
//    private val READ_UUID = "00001002-0000-1000-8000-00805f9b34fb"
//    private val NOTIFICATION_UUID = "00002902-0000-1000-8000-00805f9b34fb"
//
//    override fun getSERVICE_UUID(): UUID? {
//        return UUID.fromString(SERVICE_UUID)
//    }
//
//    override fun getCHARACTER_UUID(): UUID? {
//        return UUID.fromString(READ_UUID)
//    }
//
//    override fun getDATA_LINE_UUID(): UUID? {
//        return null
//    }
//
//    override fun getUUID_FINAL(): UUID? {
//        return UUID.fromString(NOTIFICATION_UUID)
//    }
//}