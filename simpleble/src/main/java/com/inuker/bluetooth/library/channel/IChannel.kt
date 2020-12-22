package com.inuker.bluetooth.library.channel

/**
 * Created by dingjikerbo on 17/4/19.
 */
interface IChannel {
    /**
     * 底层写数据
     */
    fun write(bytes: ByteArray?, callback: ChannelCallback?)

    /**
     * 通知底层读到数据
     */
    fun onRead(bytes: ByteArray?)

    /**
     * 通知上层收到数据
     */
    fun onRecv(bytes: ByteArray?)

    /**
     * 上层发数据
     */
    fun send(value: ByteArray?, callback: ChannelCallback?)
}