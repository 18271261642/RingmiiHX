package com.guider.health.apilib.bean

import android.os.Parcel
import android.os.Parcelable

class UserInfo : Parcelable {
    /**
     * accountId : 0
     * addr : string
     * birthday : 2019-08-07T14:59:46.422Z
     * cardId : string
     * city : 0
     * countie : 0
     * createTime : 2019-08-07T14:59:46.422Z
     * descDetail : string
     * emergencyContact : string
     * emergencyContactPhone : string
     * gender : MAN
     * headUrl : string
     * height : 0
     * id : 0
     * mail : string
     * name : string
     * nation : 0
     * phone : string
     * province : 0
     * remark : string
     * updateTime : 2019-08-07T14:59:46.422Z
     * userState : ACTIVE
     * weight : 0
     */
    var accountId = 0
    var addr: String? = null
    var birthday: String? = null
    var cardId: String? = null
    var city = 0
    var countie = 0
    var createTime: String? = null
    var descDetail: String? = null
    var emergencyContact: String? = null
    var emergencyContactPhone: String? = null
    var gender: String? = null
    var headUrl: String? = null
    var height = 0
    var id = 0
    var mail: String? = null
    var name: String? = null
    var nation = 0
    var phone: String? = null
    var province = 0
    var remark: String? = null
    var updateTime: String? = null
    var userState: String? = null
    var weight = 0.0
    var isSelected = 0
    var relationShip: String? = null
    var deviceCode: String? = null
    var electricity: Int = 0
    var deviceState: String? = ""
    var deviceMode:String? = "COMMON"
    //手环信号强度
    var rssi:Int = 0
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(accountId)
        dest.writeString(addr)
        dest.writeString(birthday)
        dest.writeString(cardId)
        dest.writeInt(city)
        dest.writeInt(countie)
        dest.writeString(createTime)
        dest.writeString(descDetail)
        dest.writeString(emergencyContact)
        dest.writeString(emergencyContactPhone)
        dest.writeString(gender)
        dest.writeString(headUrl)
        dest.writeInt(height)
        dest.writeInt(id)
        dest.writeString(mail)
        dest.writeString(name)
        dest.writeInt(nation)
        dest.writeString(phone)
        dest.writeInt(province)
        dest.writeString(remark)
        dest.writeString(updateTime)
        dest.writeString(userState)
        dest.writeDouble(weight)
        dest.writeInt(isSelected)
        dest.writeString(relationShip)
        dest.writeString(deviceCode)
        dest.writeInt(electricity)
        dest.writeString(deviceState)
        dest.writeString(deviceMode)
        dest.writeInt(rssi)
    }

    constructor()
    protected constructor(`in`: Parcel) {
        accountId = `in`.readInt()
        addr = `in`.readString()
        birthday = `in`.readString()
        cardId = `in`.readString()
        city = `in`.readInt()
        countie = `in`.readInt()
        createTime = `in`.readString()
        descDetail = `in`.readString()
        emergencyContact = `in`.readString()
        emergencyContactPhone = `in`.readString()
        gender = `in`.readString()
        headUrl = `in`.readString()
        height = `in`.readInt()
        id = `in`.readInt()
        mail = `in`.readString()
        name = `in`.readString()
        nation = `in`.readInt()
        phone = `in`.readString()
        province = `in`.readInt()
        remark = `in`.readString()
        updateTime = `in`.readString()
        userState = `in`.readString()
        weight = `in`.readDouble()
        isSelected = `in`.readInt()
        relationShip = `in`.readString()
        deviceCode = `in`.readString()
        electricity = `in`.readInt()
        deviceState = `in`.readString()
        deviceMode = `in`.readString()
        rssi = `in`.readInt()
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<UserInfo?> = object : Parcelable.Creator<UserInfo?> {
            override fun createFromParcel(source: Parcel): UserInfo? {
                return UserInfo(source)
            }

            override fun newArray(size: Int): Array<UserInfo?> {
                return arrayOfNulls(size)
            }
        }
    }
}