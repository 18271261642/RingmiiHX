package com.guider.health.apilib.bean

import android.os.Parcel
import android.os.Parcelable

class WeChatInfo : Parcelable {
    /**
     * openid : oeUVb08mdhM1G8JnHS1px9x2VqtI
     * nickname : 张三
     * sex : 1
     * headimgurl : http://thirdwx.qlogo.cn/mmopen/DHDrCukOdm9RYP9XicfQYA4bZhLgcsKZtricmH0U2JrzvZDJBd5dAticOR19yJ5CZzpn3PrPNcwS1icbSicCibYPSMycCeGAyXox0t/132
     * unionid : oaVtW50oPgqcS96ev0KTaJrv9REM
     */
    var appId: String? = null
        private set
    var openid: String? = null
    var nickname: String? = null
    var sex = 0
    var headimgurl: String? = null
    var unionid: String? = null
    fun setAppId(appId: String?): WeChatInfo {
        this.appId = appId
        return this
    }

    constructor() {}

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(appId)
        dest.writeString(openid)
        dest.writeString(nickname)
        dest.writeInt(sex)
        dest.writeString(headimgurl)
        dest.writeString(unionid)
    }

    protected constructor(`in`: Parcel) {
        appId = `in`.readString()
        openid = `in`.readString()
        nickname = `in`.readString()
        sex = `in`.readInt()
        headimgurl = `in`.readString()
        unionid = `in`.readString()
    }

    companion object {
        val CREATOR: Parcelable.Creator<WeChatInfo?> = object : Parcelable.Creator<WeChatInfo?> {
            override fun createFromParcel(source: Parcel): WeChatInfo? {
                return WeChatInfo(source)
            }

            override fun newArray(size: Int): Array<WeChatInfo?> {
                return arrayOfNulls(size)
            }
        }
    }
}