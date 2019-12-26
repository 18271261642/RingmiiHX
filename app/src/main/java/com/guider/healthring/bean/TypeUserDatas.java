package com.guider.healthring.bean;

/**
 * 将个人信息数据同步到盖得服务器，需要该对象整理数据
 */
public class TypeUserDatas {
    private String typeData;
    private String dataJson;

    public String getTypeData() {
        return typeData;
    }

    public void setTypeData(String typeData) {
        this.typeData = typeData;
    }

    public String getDataJson() {
        return dataJson;
    }

    public void setDataJson(String dataJson) {
        this.dataJson = dataJson;
    }

    @Override
    public String toString() {
        return "TypeUserDatas{" +
                "typeData='" + typeData + '\'' +
                ", dataJson='" + dataJson + '\'' +
                '}';
    }
}
