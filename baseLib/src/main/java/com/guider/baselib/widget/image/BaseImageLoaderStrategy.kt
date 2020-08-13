package com.guider.baselib.widget.image

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.request.RequestOptions

/**
 * 策略接口
 **/
interface BaseImageLoaderStrategy {
    /**
     * 默认方式加载图片
     * @param context 上下文
     * @param view View 控件
     * @param imgUrl 图片URL
     */
    fun loadImage(context: Context, view: ImageView, imgUrl: Any)

    fun showImgWithOption(context: Context, path: String?, imageView: ImageView?
                          , option: RequestOptions) {

    }

    /**
     * 加载弧度图片
     */
    fun showRoundedImg(context: Context, path: String?, imageView: ImageView?) {
    }

    /**
     * 加载圆形图片
     */
    fun showCircleImg(context: Context, path: String?, imageView: ImageView?) {
    }

    /**
     * radius 弧度
     */
    fun showBlurImg(context: Context, path: String?, imageView: ImageView?, radius: Int) {
    }
}