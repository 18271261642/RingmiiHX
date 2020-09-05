package com.guider.health.apilib.bean

import android.os.Parcel
import android.os.Parcelable

class BeanOfWeChat : Parcelable {
    /**
     * flag : false
     * WechatInfo : null
     * UserInfo : null
     */
    var isFlag = false
    var isDevFlag = false
        private set
    var wechatInfo: WeChatInfo? = null
    var userInfo: UserInfo? = null
    var tokenInfo: TokenInfo? = null
        private set

    fun setDevFlag(devFlag: Boolean): BeanOfWeChat {
        isDevFlag = devFlag
        return this
    }

    fun setTokenInfo(tokenInfo: TokenInfo?): BeanOfWeChat {
        this.tokenInfo = tokenInfo
        return this
    }

    constructor() {}

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeByte(if (isFlag) 1.toByte() else 0.toByte())
        dest.writeByte(if (isDevFlag) 1.toByte() else 0.toByte())
        dest.writeParcelable(wechatInfo, flags)
        dest.writeParcelable(userInfo, flags)
        dest.writeParcelable(tokenInfo, flags)
    }

    protected constructor(`in`: Parcel) {
        isFlag = `in`.readByte().toInt() != 0
        isDevFlag = `in`.readByte().toInt() != 0
        wechatInfo = `in`.readParcelable(WeChatInfo::class.java.classLoader)
        userInfo = `in`.readParcelable(UserInfo::class.java.classLoader)
        tokenInfo = `in`.readParcelable(TokenInfo::class.java.classLoader)
    }

    companion object {
        val CREATOR: Parcelable.Creator<BeanOfWeChat?> = object : Parcelable.Creator<BeanOfWeChat?> {
            override fun createFromParcel(source: Parcel): BeanOfWeChat? {
                return BeanOfWeChat(source)
            }

            override fun newArray(size: Int): Array<BeanOfWeChat?> {
                return arrayOfNulls(size)
            }
        }
    }
}