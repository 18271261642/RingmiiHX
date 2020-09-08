package com.lljjcoder.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * @2Do:
 * @Author M2
 * @Version v ${VERSION}
 * @Date 2017/7/7 0007.
 */
public class CityBeanNew implements Parcelable {


    private String area_code; /*110101*/

    private String area_name; /*东城区*/

    private String sortLetters;  //显示数据拼音的首字母

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

    private ArrayList<DistrictBeanNew> sub;

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

    public ArrayList<DistrictBeanNew> getSub() {
        return sub;
    }

    public void setSub(ArrayList<DistrictBeanNew> sub) {
        this.sub = sub;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.area_code);
        dest.writeString(this.area_name);
        dest.writeTypedList(this.sub);
        dest.writeString(this.sortLetters);
    }

    public CityBeanNew() {
    }

    protected CityBeanNew(Parcel in) {
        this.area_code = in.readString();
        this.area_name = in.readString();
        this.sub = in.createTypedArrayList(DistrictBeanNew.CREATOR);
        this.sortLetters = in.readString();
    }

    public static final Creator<CityBeanNew> CREATOR = new Creator<CityBeanNew>() {
        @Override
        public CityBeanNew createFromParcel(Parcel source) {
            return new CityBeanNew(source);
        }

        @Override
        public CityBeanNew[] newArray(int size) {
            return new CityBeanNew[size];
        }
    };
}
