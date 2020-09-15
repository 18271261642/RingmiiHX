package com.guider.gps.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import lecho.lib.hellocharts.view.LineChartView
import kotlin.math.abs


/**
 * @Package: com.guider.gps.widget
 * @ClassName: LineChartViewFixed
 * @Description: 解决横向滑动冲突的lineChart
 * @Author: hjr
 * @CreateDate: 2020/9/14 15:34
 * Copyright (C), 1998-2020, GuiderTechnology
 */
class LineChartViewFixed : LineChartView {

    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) :
            super(context!!, attrs, defStyle)

    private var startx = 0f
    private var starty = 0f


    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        var ratio = 1.8f //水平和竖直方向滑动的灵敏度,偏大是水平方向灵敏
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startx = event.x
                starty = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = abs(event.x - startx)
                val dy = abs(event.y - starty)
                startx = event.x
                starty = event.y
                parent.requestDisallowInterceptTouchEvent(
                        dx * ratio > dy)
            }
        }
        return super.dispatchTouchEvent(event)
    }


}