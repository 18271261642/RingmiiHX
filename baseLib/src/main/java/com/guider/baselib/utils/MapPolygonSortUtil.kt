package com.guider.baselib.utils

import com.google.android.gms.maps.model.LatLng
import java.util.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * @Package: com.guider.baselib.utils
 * @ClassName: MapPolygonSortUtil
 * @Description: 地图区域排序工具类
 * @Author: hjr
 * @CreateDate: 2020/10/17 11:09
 * Copyright (C), 1998-2020, GuiderTechnology
 */
object MapPolygonSortUtil {
    /**
     * 通过工具方法获得2个点坐标对应的在垂直方向上的角度
     *
     * @param lat_a 纬度1
     * @param lng_a 经度1
     * @param lat_b 纬度2
     * @param lng_b 经度2
     * @return
     */
    private fun getAngle1(lat_a: Double, lng_a: Double, lat_b: Double, lng_b: Double): Double {
        val y = sin(lng_b - lng_a) * cos(lat_b)
        val x = cos(lat_a) * sin(lat_b) - (sin(lat_a)
                * cos(lat_b) * cos(lng_b - lng_a))
        var brng = atan2(y, x)
        brng = Math.toDegrees(brng)
        if (brng < 0) brng += 360
        return brng
    }

    fun changeMapPoint(list: MutableList<LatLng>): List<LatLng> {
        //计算中心点坐标
        var plusX = 0.0
        var plusY = 0.0
        for (latLng in list) {
            plusX += latLng.latitude
            plusY += latLng.longitude
        }
        val center = LatLng(plusX / list.size, plusY / list.size)
        //将坐标数组转换为HashMap<Integer, ArrayList<Object>> 映射为下标->(角度,坐标)
        val mapAll = HashMap<Int, ArrayList<Any>>()
        for (i in list.indices) {
            //第一个放经纬度 第二个放角度
            val objList = ArrayList<Any>()
            objList.add(list[i])
            objList.add(getAngle1(center.latitude, center.longitude,
                    list[i].latitude, list[i].longitude))
            mapAll[i] = objList
        }
        //采用冒泡排序法对角度进行排序
        var temp: ArrayList<Any>
        val size = mapAll.size
        for (i in 0 until size - 1) {
            for (j in 0 until size - 1 - i) {
                if (mapAll[j]?.get(1).toString().toDouble() >
                        mapAll[j + 1]?.get(1).toString().toDouble()) //交换两数位置
                {
                    temp = mapAll[j]!!
                    mapAll[j] = mapAll[j + 1]!!
                    mapAll[j + 1] = temp
                }
            }
        }
        //生成新的顺时针的坐标点集合
        list.clear()
        for (integer in mapAll.keys) {
            if (mapAll[integer]?.get(0) is LatLng) {
                list.add(mapAll[integer]?.get(0) as LatLng)
            }
        }
        return list
    }
}