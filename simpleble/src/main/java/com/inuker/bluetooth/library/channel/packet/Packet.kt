package com.inuker.bluetooth.library.channel.packet

import java.nio.ByteBuffer

/**
 * Created by dingjikerbo on 17/4/14.
 */
/**
 * 包分流控包和数据包，流控包又包括指令包和ACK包
 */
abstract class Packet {
    private class Header {
        var sn = 0
        var type = 0
        var command = 0
        var parameter = 0
        lateinit var value: ByteArray
    }

    class Bytes @JvmOverloads constructor(var value: ByteArray, // [start, end)
                                                   var start: Int, var end: Int = value.size) {
        val dataLength: Int
            get() = end - start
    }

    abstract val name: String?
    abstract fun toBytes(): ByteArray?

    companion object {
        const val BUFFER_SIZE = 20
        @JvmField
		val BUFFER = ByteArray(BUFFER_SIZE)

        /**
         * 流控包，非零都表示数据包
         */
        const val SN_CTR = 0

        /**
         * 指令包
         */
        const val TYPE_CMD = 0x00

        /**
         * ACK包
         */
        const val TYPE_ACK = 0x01
        const val ACK = "ack"
        const val DATA = "gattProfile"
        const val CTR = "ctr"
        private fun parse(bytes: ByteArray): Header {
            val header = Header()
            val buffer = ByteBuffer.wrap(bytes)
            header.sn = buffer.short.toInt()
            header.value = bytes
            if (header.sn == SN_CTR) {
                header.type = buffer.get().toInt()
                header.command = buffer.get().toInt()
                header.parameter = buffer.int
            }
            return header
        }

        fun getPacket(bytes: ByteArray): Packet {
            val header = parse(bytes)
            return when (header.sn) {
                0 -> getFlowPacket(header)
                else -> getDataPacket(header)
            }
        }

        /**
         * 流控包分两种，流控和ACK
         */
        private fun getFlowPacket(header: Header): Packet {
            val parameter = header.parameter
            return when (header.type) {
                0 -> {
                    val frames = parameter shr 16
                    CTRPacket(frames)
                }
                1 -> {
                    val status = parameter shr 16
                    val seq = parameter and 0xffff
                    ACKPacket(status, seq)
                }
                else -> InvalidPacket()
            }
        }

        private fun getDataPacket(header: Header): Packet {
            return DataPacket(header.sn, Bytes(header.value, 2))
        }
    }
}