package com.guider.baselib.widget.image

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.request.RequestOptions

/**
 * Description TODO Glide图片显示工具类
 * Author HJR36
 * Date 2018/4/12 19:55
 */
object ImageLoaderUtils : BaseImageLoaderStrategy {

    private var mImageLoaderStrategy: BaseImageLoaderStrategy

    init {
        //默认使用Glide_Image-Loader
        mImageLoaderStrategy = GlideLoaderStrategy()
    }
    /**
     * 设置图片框架策略
     * @param strategy  图片框架策略
     **/
    fun setImageLoaderStrategy(strategy:BaseImageLoaderStrategy) {
        mImageLoaderStrategy = strategy
    }


    override fun loadImage(context: Context, view: ImageView, imgUrl: Any) {
        mImageLoaderStrategy.loadImage(context,view,imgUrl)
    }

    /**
     * 加载图片的option
     */
    override fun showImgWithOption(context: Context, path: String?, imageView: ImageView?
                                   ,option:RequestOptions) {
        mImageLoaderStrategy.showImgWithOption(context,path,imageView,option)
    }

    /**
     * 加载弧度图片
     */
    override fun showRoundedImg(context: Context, path: String?, imageView: ImageView?) {
        mImageLoaderStrategy.showRoundedImg(context,path,imageView)
    }

    /**
     * 加载圆形图片
     */
    override fun showCircleImg(context: Context, path: String?, imageView: ImageView?) {
        mImageLoaderStrategy.showCircleImg(context,path,imageView)
    }

    /**
     * radius 弧度
     */
    override fun showBlurImg(context: Context, path: String?, imageView: ImageView?,radius:Int) {
        mImageLoaderStrategy.showBlurImg(context,path,imageView,radius)
    }

}