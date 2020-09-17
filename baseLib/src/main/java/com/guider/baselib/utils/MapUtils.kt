package com.guider.baselib.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import java.net.URISyntaxException


/**
 * @Package: com.guider.baselib.utils
 * @ClassName: MapUtils
 * @Description:
 * @Author: hjr
 * @CreateDate: 2020/9/17 9:05
 * Copyright (C), 1998-2020, GuiderTechnology
 */
object MapUtils {
    fun startGuide(context: Context, latitude: Double, longitude: Double) {
        when {
            isAvilible(context, "com.google.android.apps.maps") -> {
                startNaviGoogle(context, latitude, longitude)
            }
            isAvilible(context, "com.autonavi.minimap") -> {
                val tempLat = MapPositionUtil.gps84_To_Gcj02(latitude, longitude)
                startNaviGao(context, tempLat.lat, tempLat.lon)
            }
            isAvilible(context, "com.baidu.BaiduMap") -> {
                val tempLat = MapPositionUtil.gps84_To_Bd09(latitude, longitude)
                startNaviBaidu(context, tempLat.lat, tempLat.lon)
            }
        }
    }

    //谷歌地图,起点就是定位点
    private fun startNaviGoogle(context: Context, latitude: Double, longitude: Double) {
        val gmmIntentUri: Uri = Uri.parse("google.navigation:q=$latitude,$longitude&mode=w")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        context.startActivity(mapIntent)
    }

    //高德地图,起点就是定位点
    // 终点是LatLng ll = new LatLng("你的纬度latitude","你的经度longitude");
    private fun startNaviGao(context: Context, latitude: Double, longitude: Double) {
        try {
            //sourceApplication
            val intent = Intent.getIntent("androidamap://navi?sourceApplication" +
                    "=公司的名称&poiname=我的目的地&lat=$latitude&lon=$longitude&dev=0")
            context.startActivity(intent)
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    //百度地图
    private fun startNaviBaidu(context: Context, latitude: Double, longitude: Double) {
        try {
            val intent = Intent.getIntent("intent://map/direction?destination=" +
                    "latlng:" + latitude + "," + longitude +
                    "|name:&origin=" + "我的位置" + "&mode=driving?ion=" + "我的位置" + "&referer=" +
                    "Autohome|GasStation#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end")
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //验证各种导航地图是否安装
    private fun isAvilible(context: Context, packageName: String): Boolean {
        //获取packagemanager
        val packageManager: PackageManager = context.packageManager
        //获取所有已安装程序的包信息
        val packageInfos = packageManager.getInstalledPackages(0)
        //用于存储所有已安装程序的包名
        val packageNames: MutableList<String> = ArrayList()
        //从pinfo中将包名字逐一取出，压入pName list中
        if (packageInfos != null) {
            for (i in packageInfos.indices) {
                val packName = packageInfos[i].packageName
                packageNames.add(packName)
            }
        }
        //判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
        return packageNames.contains(packageName)
    }
}