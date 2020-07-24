package com.guider.health.apilib.model;

/**
 * Created by haix on 2019/7/24.
 */

public class Devices {

    private int id;
    private String createTime;
    private String updateTime;
    private String typeName;
    private String btName;
    private String version;
    private String name;
    private String imgUrl;
    private String descript;
    private int typeMask;

    public Devices() {}

    public Devices(String btName, String version, String name, String imgUrl) {
        setBtName(btName);
        setVersion(version);
        setName(name);
        setImgUrl(imgUrl);
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

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getBtName() {
        return btName;
    }

    public void setBtName(String btName) {
        this.btName = btName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getDescript() {
        return descript;
    }

    public void setDescript(String descript) {
        this.descript = descript;
    }

    public int getTypeMask() {
        return typeMask;
    }

    public void setTypeMask(int typeMask) {
        this.typeMask = typeMask;
    }
}
