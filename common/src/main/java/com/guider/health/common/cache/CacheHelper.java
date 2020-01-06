package com.guider.health.common.cache;

import android.content.Context;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

public class CacheHelper {

    /**
     * 最大重试次数
     */
    private final int MAX_REUPLOAD_NUM = 10;

    /**
     * 最大缓存条数
     */
    private final int MAX_CACHE_NUM = 100;

    private CacheHelper(Context context) {
        FlowManager.init(context.getApplicationContext());
    }
    private volatile static CacheHelper instance;
    public static CacheHelper getInstance(Context context) {
        if (instance != null) {
            return instance;
        }
        if (instance == null) {
            synchronized (CacheHelper.class) {
                if (instance == null) {
                    instance = new CacheHelper(context);
                }
            }
        }
        return instance;
    }

    /**
     * 获取所有缓存数据
     * @return
     */
    public List<CacheData> getAllData() {
        List<CacheData> list = SQLite.select().from(CacheData.class).queryList();
        if (list.size() > MAX_CACHE_NUM) {
            // todo 缓存的太多了 , 直接清空数据库
            SQLite.delete().from(CacheData.class).execute();
        }
        return list;
    }

    /**
     * 添加一条缓存数据
     * @param cacheData
     */
    public void addCacheData(CacheData cacheData) {
        cacheData.insert();
    }

    /**
     * 更新重新上传次数
     * @param cacheData
     */
    public void updateReupload(CacheData cacheData) {
        cacheData.setReuploadNum(cacheData.getReuploadNum() + 1);
        if (cacheData.getReuploadNum() > MAX_REUPLOAD_NUM) {
            cacheData.delete();
        }
    }

    /**
     * 在URL没有匹配项的时候就删除这条缓存
     * @param cacheData
     */
    public void deleteCache(CacheData cacheData) {
        cacheData.delete();
    }
}
