package com.guider.baselib.cache;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = AppData.class , name = "HD_CACHE")
public class CacheData  extends BaseModel {


    @PrimaryKey
    private long time; // 创建时间
    @Column
    private String url;// 提交到哪个URL
    @Column
    private String gson;// 提交的json
    @Column
    private int reuploadNum; // 重新上传的次数

    public long getTime() {
        return time;
    }

    public CacheData setTime(long time) {
        this.time = time;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public CacheData setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getGson() {
        return gson;
    }

    public CacheData setGson(String gson) {
        this.gson = gson;
        return this;
    }

    public int getReuploadNum() {
        return reuploadNum;
    }

    public CacheData setReuploadNum(int reuploadNum) {
        this.reuploadNum = reuploadNum;
        return this;
    }
}
