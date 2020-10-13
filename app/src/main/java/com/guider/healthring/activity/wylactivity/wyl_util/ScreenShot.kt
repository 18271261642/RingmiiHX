package com.guider.healthring.activity.wylactivity.wyl_util

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import com.guider.health.common.utils.FileDirUtil.addBitmapToAlbum
import com.guider.health.common.utils.StringUtil
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

/**
 * Google 地图截屏
 */
object ScreenShot {
    fun shoot(a: Activity) {
        val bitmap = takeScreenShot(a)
        val displayName = "${System.currentTimeMillis()}.png"
        val mimeType = "image/png"
        val compressFormat = Bitmap.CompressFormat.JPEG
        addBitmapToAlbum(bitmap, displayName, mimeType, compressFormat, a)
    }

    private fun takeScreenShot(activity: Activity): Bitmap {
        val view = activity.window.decorView
        view.isDrawingCacheEnabled = true
        view.buildDrawingCache()
        val bitmap = view.drawingCache
        val frame = Rect()
        activity.window.decorView.getWindowVisibleDisplayFrame(frame)
        val statusBarHeight = frame.top
        val width = activity.windowManager.defaultDisplay.width
        val height = activity.windowManager.defaultDisplay
                .height
        // 去掉标题栏
        val b = Bitmap.createBitmap(bitmap, 0, statusBarHeight, width,
                height - statusBarHeight)
        view.destroyDrawingCache()
        return b
    }
}