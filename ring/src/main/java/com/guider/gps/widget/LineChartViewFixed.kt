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

    private var startx = 0
    private var starty = 0


    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        parent.requestDisallowInterceptTouchEvent(true)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startx = event.x.toInt()
                starty = event.y.toInt()
            }
            MotionEvent.ACTION_MOVE -> {
                val endx = event.x.toInt()
                val endy = event.y.toInt()
                val dx: Int = endx - startx
                val dy: Int = endy - starty
                if (abs(dy) < abs(dx)) {
                    //左右滑动
                    parent.requestDisallowInterceptTouchEvent(true)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }


}