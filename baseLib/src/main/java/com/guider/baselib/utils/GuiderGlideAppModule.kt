package com.guider.baselib.utils

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.module.AppGlideModule
import java.io.InputStream

/**
 * Description TODO 自定义Glide缓存
 * Author HJR36
 * Date 2018/4/17 17:11
 */
@GlideModule
class GuiderGlideAppModule :AppGlideModule() {

    /**
     *  通过GlideBuilder设置默认的结构(Engine,BitmapPool ,ArrayPool,MemoryCache等等).
     * @param context
     * @param glideBuilder
     */
    override fun applyOptions(context: Context, glideBuilder: GlideBuilder) {
        // 内部磁盘私用缓存
        glideBuilder.setDiskCache(
                ExternalCacheDiskCacheFactory(context,
                        "glide_cache", 50 * 1024 * 1024))
        // 内存缓存策略
        glideBuilder.setMemoryCache(LruResourceCache(20 * 1024 * 1024))
        // 位图的池的大小
        glideBuilder.setBitmapPool(LruBitmapPool(20 * 1024 * 1024))
    }

    /**
     * 清单解析的开启
     *
     * 这里不开启，避免添加相同的modules两次
     * @return
     */
    override fun isManifestParsingEnabled(): Boolean {
        return false
    }

    /**
     * 为App注册一个自定义的String类型的BaseGlideUrlLoader
     * @param context
     * @param registry
     */
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.append(String::class.java, InputStream::class.java, CustomBaseGlideUrlLoader.Factory())
    }

}