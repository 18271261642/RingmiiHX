package com.guider.health.apilib.bean

import android.os.Parcel
import android.os.Parcelable

/**
 * @Package: com.guider.health.apilib.bean
 * @ClassName: CheckBindDeviceBeanNew
 * @Description: 绑定设备bean类
 * @Author: hjr
 * @CreateDate: 2020/9/4 16:03
 * Copyright (C), 1998-2020, GuiderTechnology
 */
class CheckBindDeviceBean : Parcelable {
    var userGroupId = 0
    var userInfos: List<UserInfo>? = null
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(userGroupId)
        dest.writeTypedList(userInfos)
    }

    constructor() {}
    protected constructor(`in`: Parcel) {
        userGroupId = `in`.readInt()
        userInfos = `in`.createTypedArrayList(UserInfo.CREATOR) as List<UserInfo>?
    }

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<CheckBindDeviceBean?> =
                object : Parcelable.Creator<CheckBindDeviceBean?> {
            override fun createFromParcel(source: Parcel): CheckBindDeviceBean? {
                return CheckBindDeviceBean(source)
            }

            override fun newArray(size: Int): Array<CheckBindDeviceBean?> {
                return arrayOfNulls(size)
            }
        }
    }
}