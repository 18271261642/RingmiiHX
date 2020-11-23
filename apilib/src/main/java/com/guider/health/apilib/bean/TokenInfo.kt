package com.guider.health.apilib.bean

import android.os.Parcel
import android.os.Parcelable

open class TokenInfo protected constructor(`in`: Parcel) : Parcelable {
    /**
     * token : eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI1MzciLCJwYXNzd2QiOiJlMTBhZGMzOTQ5YmE1OWFiYmU1NmUwNTdmMjBmODgzZSIsImlkIjo1MzcsImV4cCI6MTU2NTUzMTcwNywiaWF0IjoxNTY1NTI0NTA3LCJqdGkiOiJmODI0MTRmYi1kYzczLTQ5MWUtOTE2OS04ZDA2ZDliM2EyMjcifQ.xUOAn8-wHqes8dwQzm992xcpH9v37_BPdm3w3CJL8xM
     * refreshToken : AS0YjOekBcPvlKPq4ou6zJ1j8XnEagT8
     * accountId : 537
     * expired : 7200
     */
    var token: String? = null
    var refreshToken: String? = null
    var accountId = 0
    var expired = 0
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(token)
        dest.writeString(refreshToken)
        dest.writeInt(accountId)
        dest.writeInt(expired)
    }

    init {
        token = `in`.readString()
        refreshToken = `in`.readString()
        accountId = `in`.readInt()
        expired = `in`.readInt()
    }

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<TokenInfo?> = object : Parcelable.Creator<TokenInfo?> {
            override fun createFromParcel(source: Parcel): TokenInfo? {
                return TokenInfo(source)
            }

            override fun newArray(size: Int): Array<TokenInfo?> {
                return arrayOfNulls(size)
            }
        }
    }
}