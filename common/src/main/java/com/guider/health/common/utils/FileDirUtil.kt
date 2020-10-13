package com.guider.health.common.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.guider.health.common.R
import java.io.File

/**
 * @Package: com.guider.health.common.utils
 * @ClassName: FileDirUtil
 * @Description: android10的文件目录
 * @Author: hjr
 * @CreateDate: 2020/10/12 14:44
 * Copyright (C), 1998-2020, GuiderTechnology
 */
object FileDirUtil {

    fun getExternalFileDir(context: Context): String? {
        return context.getExternalFilesDir("temp")?.path
    }

    fun getExternalFilePath(context: Context, fileName: String): String? {
        val dir = context.getExternalFilesDir("temp")
        return File("$dir/$fileName").path
    }

    fun addBitmapToAlbum(
            bitmap: Bitmap,
            displayName: String,
            mimeType: String,
            compressFormat: Bitmap.CompressFormat, context: Context
    ) {
        val values = ContentValues()
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
        values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
        } else {
            values.put(
                    MediaStore.MediaColumns.DATA,
                    "${Environment.getExternalStorageDirectory().path}/${Environment.DIRECTORY_DCIM}/$displayName"
            )
        }
        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        if (uri != null) {
            val outputStream = context.contentResolver.openOutputStream(uri)
            if (outputStream != null) {
                bitmap.compress(compressFormat, 100, outputStream)
                outputStream.close()
                ToastUtil.showToast(context, context.resources.getString(R.string.addPicToAlbum))
            }
        }
    }
}