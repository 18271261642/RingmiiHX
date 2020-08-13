package com.guider.baselib.widget.image

import android.annotation.SuppressLint
import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.guider.baselib.R
import com.guider.baselib.utils.GlideApp
import com.guider.baselib.widget.image.BaseImageLoaderStrategy
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import java.lang.ref.WeakReference


/**
 * Glide的实现类
 **/
class GlideLoaderStrategy : BaseImageLoaderStrategy {

    private lateinit var option: RequestOptions

    /**
     * 初始化加载配置
     */
    @SuppressLint("CheckResult")
    fun getOptions(): RequestOptions {
        option = RequestOptions()
        option.error(R.drawable.bg_image_default)
                .placeholder(R.drawable.bg_image_default)
                //下载的优先级
                .priority(Priority.NORMAL)
                //缓存策略
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .dontAnimate()
        return option
    }

    override fun loadImage(context: Context, view: ImageView, imgUrl: Any) {
        val mContext: WeakReference<Context> = WeakReference(context)
        GlideApp.with(mContext.get()!!)
                .load(imgUrl)
                .apply(getOptions())
                .into(view)
    }

    /**
     * 加载图片的option
     */
    override fun showImgWithOption(context: Context, path: String?, imageView: ImageView?, option: RequestOptions) {
        val mContext: WeakReference<Context> = WeakReference(context)
        GlideApp.with(mContext.get()!!)
                .load(path)
                .apply(getOptions())
                .apply(option)
                .into(imageView!!)
    }

    /**
     * 加载弧度图片
     */
    override fun showRoundedImg(context: Context, path: String?, imageView: ImageView?) {
        val mContext: WeakReference<Context> = WeakReference(context)
        val rOption = RequestOptions().transform(RoundedCornersTransformation(10, 0))
        GlideApp.with(mContext.get()!!)
                .load(path)
                .apply(getOptions())
                .apply(rOption)
                .into(imageView!!)
    }

    /**
     * 加载圆形图片
     */
    override fun showCircleImg(context: Context, path: String?, imageView: ImageView?) {
        val mContext: WeakReference<Context> = WeakReference(context)
//        val cOption = RequestOptions()
//                .placeholder(R.mipmap.portrait)
//                .error(R.mipmap.portrait)
//                .circleCrop()
        val cOption = RequestOptions.circleCropTransform()
        GlideApp.with(mContext.get()!!)
                .load(path)
                .apply(getOptions())
                .apply(cOption)
                .into(imageView!!)
    }

    /**
     * radius 弧度
     */
    override fun showBlurImg(context: Context, path: String?, imageView: ImageView?, radius: Int) {
        val mContext: WeakReference<Context> = WeakReference(context)
        val blurOption = RequestOptions()
                .transform(BlurTransformation(radius))

        GlideApp.with(mContext.get()!!)
                .load(path)
                .apply(getOptions())
                .apply(blurOption)
                .into(imageView!!)
    }
}