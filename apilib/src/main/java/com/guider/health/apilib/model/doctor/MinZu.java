package com.guider.health.apilib.model.doctor;

import java.util.List;

public class MinZu {


    /**
     * id : 2
     * createTime : 2019-07-02T12:40:07Z
     * parentId : 1
     * name : 汉族
     * sortId : 1
     */

    private int id;
    private String createTime;
    private int parentId;
    private String name;
    private int sortId;

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

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSortId() {
        return sortId;
    }

    public void setSortId(int sortId) {
        this.sortId = sortId;
    }


    /**
     * 根据民族Code获取民族名称
     *
     * @param code
     * @return
     */
    public String getStrMinzu(List<MinZu> minZuList , int code) {
        if (minZuList == null) {
            return "";
        }
        for (MinZu minZu : minZuList) {
            if (minZu.getSortId() == code) {
                return minZu.getName();
            }
        }
        return "";
    }
}
