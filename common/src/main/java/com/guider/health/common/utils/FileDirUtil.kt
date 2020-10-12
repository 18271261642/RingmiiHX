package com.guider.health.common.utils

import android.content.Context
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
        return context.getExternalFilesDir("temp")?.absolutePath
    }

    fun getExternalFilePath(context: Context,fileName:String): String? {
        val dir = context.getExternalFilesDir("temp")
        return File("$dir/$fileName").absolutePath
    }
}