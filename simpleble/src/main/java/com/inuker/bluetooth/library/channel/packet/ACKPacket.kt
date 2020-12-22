package com.inuker.bluetooth.library.channel.packet

import java.lang.reflect.Modifier
import java.nio.ByteBuffer

/**
 * Created by dingjikerbo on 17/4/16.
 */
class ACKPacket @JvmOverloads constructor(val status: Int,
                                          /**
                                           * 序号从1开始
                                           */
                                          val seq: Int = 0,
                                          override val name: String? = ACK) : Packet() {


    override fun toBytes(): ByteArray {
        val buffer = ByteBuffer.wrap(BUFFER)
        buffer.putShort(SN_CTR.toShort())
        buffer.put(TYPE_ACK.toByte())
        buffer.put(0.toByte()) // ack包command设为空
        buffer.putShort(status.toShort())
        buffer.putShort(seq.toShort())
        return buffer.array()
    }

    override fun toString(): String {
        return "ACKPacket{" +
                "status=" + getStatusDesc(status) +
                ", seq=" + seq +
                '}'
    }

    private fun getStatusDesc(status: Int): String {
        for (field in javaClass.declaredFields) {
            if (field.modifiers and (Modifier.STATIC or Modifier.FINAL) > 0) {
                try {
                    if (field[null] === Integer.valueOf(status)) {
                        return field.name
                    }
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }
            }
        }
        return status.toString() + ""
    }

    companion object {
        /**
         * 数据同步成功
         */
        const val SUCCESS = 0

        /**
         * 设备就绪
         */
        const val READY = 1

        /**
         * 设备繁忙
         */
        const val BUSY = 2

        /**
         * 同步超时
         */
        const val TIMEOUT = 3

        /**
         * 取消同步
         */
        const val CANCEL = 4

        /**
         * 同步丢包
         */
        const val SYNC = 5
    }
}