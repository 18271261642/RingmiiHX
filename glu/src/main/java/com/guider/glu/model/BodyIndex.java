package com.guider.glu.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by haix on 2019/7/5.
 */

public class BodyIndex implements Parcelable {



    public final static String Normal = "Normal";
    public final static String Abnormal = "Abnormal";
    private static BodyIndex bodyIndex = new BodyIndex();
    private BodyIndex(){

    }

    public static BodyIndex getInstance(){
        return bodyIndex;
    }

    public BodyIndex(Parcel in) {

        timeMeal = in.readString();
        weigh = in.readString();
        height = in.readString();
        glucose = in.readString();
        diabetesType = in.readString();
        sulphonylureasState = in.readString();
        biguanidesState = in.readString();
        glucosedesesSate = in.readString();
    }

    //反序列化
    public static final Creator<BodyIndex> CREATOR = new Creator<BodyIndex>() {

        @Override
        public BodyIndex createFromParcel(Parcel in) {
            return new BodyIndex(in);
        }

        @Override
        public BodyIndex[] newArray(int size) {
            return new BodyIndex[size];
        }
    };

    //描述
    @Override
    public int describeContents() {
        return 0;
    }


    //序列化方法
    @Override
    public void writeToParcel(Parcel out, int flags) {

        out.writeString(timeMeal);
        out.writeString(weigh);
        out.writeString(height);
        out.writeString(glucose);
        out.writeString(diabetesType);
        out.writeString(sulphonylureasState);
        out.writeString(biguanidesState);
        out.writeString(glucosedesesSate);

    }



    public String getWeigh() {
        return weigh;
    }

    public void setWeigh(String weigh) {
        this.weigh = weigh;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getGlucose() {
        return glucose;
    }

    public void setGlucose(String glucose) {
        this.glucose = glucose;
    }

    public String getDiabetesType() {
        return diabetesType;
    }

    public void setDiabetesType(String diabetesType) {
        this.diabetesType = diabetesType;
    }

    public String getSulphonylureasState() {
        return sulphonylureasState;
    }

    public void setSulphonylureasState(String sulphonylureasState) {
        this.sulphonylureasState = sulphonylureasState;
    }

    public String getBiguanidesState() {
        return biguanidesState;
    }

    public void setBiguanidesState(String biguanidesState) {
        this.biguanidesState = biguanidesState;
    }

    public String getGlucosedesesSate() {
        return glucosedesesSate;
    }

    public void setGlucosedesesSate(String glucosedesesSate) {
        this.glucosedesesSate = glucosedesesSate;
    }

    public String getTimeMeal() {
        return timeMeal;
    }

    public void setTimeMeal(String timeMeal) {
        this.timeMeal = timeMeal;
    }

    private String timeMeal;
    private String weigh;
    private String height;
    private String glucose;
    private String diabetesType = "Normal";
    private String sulphonylureasState = "0";//璜脲类
    private String biguanidesState = "0";//双胍类
    private String glucosedesesSate = "0";//肝酶抑制剂
    private String createTime;
    private String updateTime;
    private float value;//血糖值
    private int id;
    private boolean isEatmedicine;

    public boolean isEatmedicine() {
        return isEatmedicine;
    }

    public void setEatmedicine(boolean eatmedicine) {
        isEatmedicine = eatmedicine;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

}
