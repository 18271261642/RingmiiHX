package com.guider.health.apilib.model;

import java.util.Date;

public class TEntityHealth {
    private long id;
    private Date createTime;

    /**
     * 数值是否正常
     */
    private boolean normal;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public boolean isNormal() {
        return normal;
    }

    public void setNormal(boolean normal) {
        this.normal = normal;
    }
}
