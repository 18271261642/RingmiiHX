package com.lljjcoder.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @2Do:
 * @Author M2
 * @Version v ${VERSION}
 * @Date 2017/7/7 0007.
 */
public class DistrictBeanNew implements Parcelable {

    private String area_code; /*110101*/

    private String area_name; /*东城区*/

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

    private String sortLetters;//拼音首字母

    @Override
    public String toString() {
        return area_name;
    }

    public String getArea_code() {
        return area_code == null ? "" : area_code;
    }

    public void setArea_code(String area_code) {
        this.area_code = area_code;
    }

    public String getArea_name() {
        return area_name == null ? "" : area_name;
    }

    public void setArea_name(String area_name) {
        this.area_name = area_name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.area_code);
        dest.writeString(this.area_name);
        dest.writeString(this.sortLetters);
    }

    public DistrictBeanNew() {
    }

    protected DistrictBeanNew(Parcel in) {
        this.area_code = in.readString();
        this.area_name = in.readString();
        this.sortLetters = in.readString();
    }

    public static final Creator<DistrictBeanNew> CREATOR = new Creator<DistrictBeanNew>() {
        @Override
        public DistrictBeanNew createFromParcel(Parcel source) {
            return new DistrictBeanNew(source);
        }

        @Override
        public DistrictBeanNew[] newArray(int size) {
            return new DistrictBeanNew[size];
        }
    };
}
