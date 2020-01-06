package com.guider.health.apilib.model.doctor;

import java.util.ArrayList;
import java.util.List;

public class City {

    /**
     * id : 3
     * createTime : null
     * updateTime : 2019-06-25T02:48:10Z
     * parentId : 59
     * areaType : COUNTIE
     * lng : 0
     * lat : 0
     * name : 河西区
     */

    private int id;
    private Object createTime;
    private String updateTime;
    private int parentId;
    private String areaType;
    private double lng;
    private double lat;
    private String name;
    private List<City> childs;

    public void putChild(City city) {
        if (childs == null) {
            childs = new ArrayList<>();
        }
        childs.add(city);
    }

    public List<City> getChilds() {
        if (childs == null || childs.size() <=0) {
            childs = new ArrayList<>();
            childs.add(this);
        }
        return childs;
    }

    public City setChilds(List<City> childs) {
        this.childs = childs;
        return this;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Object getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Object createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getAreaType() {
        return areaType;
    }

    public void setAreaType(String areaType) {
        this.areaType = areaType;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(int lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(int lat) {
        this.lat = lat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
