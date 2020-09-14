package com.guider.baselib.utils

import android.app.Activity
import com.guider.baselib.R
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia


/**
 * Description TODO 照片裁剪
 * Author HJR36
 * Date 2018/5/15 11:01
 */
object PhotoCuttingUtil {

    /**
     * 不需要裁剪
     */
    fun takePhotoZoom(context: Activity, requestCode: Int) {
        PictureSelector.create(context)
                .openCamera(PictureConfig.TYPE_IMAGE)// 单独拍照，也可录像或也可音频 看你传入的类型是图片or视频
                .imageEngine(GlideEngine.createGlideEngine())// 外部传入图片加载引擎，必传项
                .theme(R.style.picture_default_style)// 主题样式设置 具体参考 values/styles
                //  用法：R.style.picture.white.style
                .isWeChatStyle(true)// 是否开启微信图片选择风格
                .maxSelectNum(1)// 最大图片选择数量
                .minSelectNum(1)// 最小选择数量
                .isPreviewImage(true)// 是否可预览图片
                .isEnableCrop(false)// 是否裁剪
                .isCompress(true)// 是否压缩
                .synOrAsy(true)//同步true或异步false 压缩 默认同步
                .minimumCompressSize(100)// 小于100kb的图片不压缩
                .forResult(requestCode)//结果回调onActivityResult code
    }

    /**
     * 需要裁剪
     */
    fun takePhotoZoom2(context: Activity, requestCode: Int,
                       aspectWidthRatio: Int = 1, aspectHeightRatio: Int = 1) {
        PictureSelector.create(context)
                .openCamera(PictureConfig.TYPE_IMAGE)// 单独拍照，也可录像或也可音频 看你传入的类型是图片or视频
                .imageEngine(GlideEngine.createGlideEngine())// 外部传入图片加载引擎，必传项
                .theme(R.style.picture_default_style)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
                .maxSelectNum(1)// 最大图片选择数量
                .minSelectNum(1)// 最小选择数量
                .isWeChatStyle(true)// 是否开启微信图片选择风格
                .isMaxSelectEnabledMask(true)// 选择数到了最大阀值列表是否启用蒙层效果
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选
                .isPreviewImage(true)// 是否可预览图片
                .isCamera(true)// 是否显示拍照按钮
                .isEnableCrop(true)// 是否裁剪
                .isCompress(true)// 是否压缩
                .synOrAsy(true)//同步true或异步false 压缩 默认同步
                .withAspectRatio(aspectWidthRatio, aspectHeightRatio)// int 裁剪比例
                .isGif(false)// 是否显示gif图片
                .freeStyleCropEnabled(false)// 裁剪框是否可拖拽
                .circleDimmedLayer(true)// 是否圆形裁剪
                .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                .minimumCompressSize(100)// 小于100kb的图片不压缩
                .rotateEnabled(false) // 裁剪是否可旋转图片
                .scaleEnabled(true)// 裁剪是否可放大缩小图片
                .forResult(requestCode)//结果回调onActivityResult code
    }

    /**
     * 不需要裁剪
     */
    fun selectPhotoZoom(context: Activity, requestCode: Int,
                        selectionMedia: MutableList<LocalMedia> = arrayListOf(),
                        maxSelectNum: Int = 9) {
        PictureSelector.create(context)
                .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .imageEngine(GlideEngine.createGlideEngine())// 外部传入图片加载引擎，必传项
                .theme(R.style.picture_default_style)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
                .maxSelectNum(maxSelectNum)// 最大图片选择数量
                .minSelectNum(1)// 最小选择数量
                .isWeChatStyle(true)// 是否开启微信图片选择风格
                .isMaxSelectEnabledMask(true)// 选择数到了最大阀值列表是否启用蒙层效果
                .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选
                .selectionData(selectionMedia)
                .isPreviewImage(true)// 是否可预览图片
                .isCamera(true)// 是否显示拍照按钮
                .isEnableCrop(false)// 是否裁剪
                .isCompress(true)// 是否压缩
                .synOrAsy(true)//同步true或异步false 压缩 默认同步
                .isGif(false)// 是否显示gif图片
                .minimumCompressSize(100)// 小于100kb的图片不压缩
                .forResult(requestCode)//结果回调onActivityResult code
    }

    /**
     * 需要裁剪
     */
    fun selectPhotoZoom2(context: Activity, requestCode: Int,
                         aspectWidthRatio: Int = 1, aspectHeightRatio: Int = 1) {
        PictureSelector.create(context)
                .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .imageEngine(GlideEngine.createGlideEngine())// 外部传入图片加载引擎，必传项
                .theme(R.style.picture_default_style)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
                .maxSelectNum(1)// 最大图片选择数量
                .minSelectNum(1)// 最小选择数量
                .isWeChatStyle(true)// 是否开启微信图片选择风格
                .isMaxSelectEnabledMask(true)// 选择数到了最大阀值列表是否启用蒙层效果
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选
                .isPreviewImage(true)// 是否可预览图片
                .isCamera(true)// 是否显示拍照按钮
                .isEnableCrop(true)// 是否裁剪
                .isCompress(true)// 是否压缩
                .synOrAsy(true)//同步true或异步false 压缩 默认同步
                .withAspectRatio(aspectWidthRatio, aspectHeightRatio)// int 裁剪比例
                .isGif(false)// 是否显示gif图片
                .freeStyleCropEnabled(false)// 裁剪框是否可拖拽
                .circleDimmedLayer(true)// 是否圆形裁剪
                .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                .minimumCompressSize(100)// 小于100kb的图片不压缩
                .rotateEnabled(false) // 裁剪是否可旋转图片
                .scaleEnabled(true)// 裁剪是否可放大缩小图片
                .forResult(requestCode)//结果回调onActivityResult code
    }

}