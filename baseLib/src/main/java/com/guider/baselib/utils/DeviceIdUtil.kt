@file:Suppress("DEPRECATION")

package com.guider.baselib.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import okhttp3.internal.and
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*

/**
 * @author hjr
 */
object DeviceIdUtil {
    /**
     * 获得设备硬件标识
     * @return 设备硬件标识
     */
    fun getDeviceId(context: Context): String {
        return getDeviceId(context, true)
    }

    /**
     * 获得设备硬件标识
     *
     * @param context 上下文
     * @return 设备硬件标识
     */
    fun getDeviceId(context: Context, isHavePermission: Boolean): String {
        val sbDeviceId = StringBuilder()
        //获得设备序列号（无需权限）
        val serial = sERIAL
        //获得硬件uuid（根据硬件相关属性，生成uuid）（无需权限）
        val uuid = deviceUUID.replace("-", "")
        if (Build.VERSION.SDK_INT < 29 && isHavePermission) {
            //获得设备默认IMEI（>=6.0 需要ReadPhoneState权限）
            val imei = getIMEI(context)
            //追加imei
            if (StringUtil.isNotBlankAndEmpty(imei)) {
                sbDeviceId.append(imei)
                sbDeviceId.append("|")
            }
        } else {
            //获得AndroidId（无需权限）
            val androidid = getAndroidId(context)
            //追加androidid
            if (StringUtil.isNotBlankAndEmpty(androidid)) {
                sbDeviceId.append(androidid)
                sbDeviceId.append("|")
            }
        }
        //追加serial
        if (StringUtil.isNotBlankAndEmpty(serial)) {
            sbDeviceId.append(serial)
            sbDeviceId.append("|")
        }
        //追加硬件uuid
        if (uuid.isNotEmpty()) {
            sbDeviceId.append(uuid)
        }

        //生成SHA1，统一DeviceId长度
        if (sbDeviceId.isNotEmpty()) {
            try {
                val hash = getHashByString(sbDeviceId.toString())
                val sha1 = bytesToHex(hash)
                if (sha1.isNotEmpty()) {
                    //返回最终的DeviceId
                    return sha1
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        //如果以上硬件标识数据均无法获得，
        //则DeviceId默认使用系统随机数，这样保证DeviceId不为空
        return UUID.randomUUID().toString().replace("-", "")
    }

    //需要获得READ_PHONE_STATE权限，>=6.0，默认返回null
    @SuppressLint("HardwareIds", "MissingPermission")
    private fun getIMEI(context: Context): String {
        try {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            return tm.deviceId
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return ""
    }

    /**
     * 获得设备的AndroidId
     *
     * @param context 上下文
     * @return 设备的AndroidId
     */
    @SuppressLint("HardwareIds")
    private fun getAndroidId(context: Context): String {
        try {
            return Settings.Secure.getString(context.contentResolver,
                    Settings.Secure.ANDROID_ID)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return ""
    }

    /**
     * 获得设备序列号（如：WTK7N16923005607）, 个别设备无法获取
     *
     * @return 设备序列号
     */
    @get:SuppressLint("HardwareIds")
    private val sERIAL: String
        get() {
            try {
                return Build.SERIAL
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            return ""
        }

    /**
     * 获得设备硬件uuid
     * 使用硬件信息，计算出一个随机数
     *
     * @return 设备硬件uuid
     */
    @get:SuppressLint("HardwareIds")
    private val deviceUUID: String
        get() = try {
            val dev = "3883756"
            +Build.BOARD.length % 10
            +Build.BRAND.length % 10
            +Build.DEVICE.length % 10
            +Build.HARDWARE.length % 10
            +Build.ID.length % 10
            +Build.MODEL.length % 10
            +Build.PRODUCT.length % 10
            +Build.SERIAL.length % 10
            UUID(dev.hashCode().toLong(),
                    Build.SERIAL.hashCode().toLong()).toString()
        } catch (ex: Exception) {
            ex.printStackTrace()
            ""
        }

    /**
     * 取SHA1
     *
     * @param data 数据
     * @return 对应的hash值
     */
    private fun getHashByString(data: String): ByteArray {
        return try {
            val messageDigest = MessageDigest.getInstance("SHA1")
            messageDigest.reset()
            messageDigest.update(data.toByteArray(StandardCharsets.UTF_8))
            messageDigest.digest()
        } catch (e: Exception) {
            "".toByteArray()
        }
    }

    /**
     * 转16进制字符串
     *
     * @param data 数据
     * @return 16进制字符串
     */
    private fun bytesToHex(data: ByteArray): String {
        val sb = StringBuilder()
        for (datum in data) {
            val stmp = Integer.toHexString(datum and 0xFF)
            if (stmp.length == 1) sb.append("0")
            sb.append(stmp)
        }
        return sb.toString().toUpperCase(Locale.CHINA)
    }
}