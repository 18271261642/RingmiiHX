package com.guider.gps.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.constraintlayout.widget.ConstraintLayout


/**
 * @Package: com.guider.gps.widget
 * @ClassName: ChildClickableConstrainLayout
 * @Description: 子view不可点击的自定义父布局
 * @Author: hjr
 * @CreateDate: 2020/10/10 16:55
 * Copyright (C), 1998-2020, GuiderTechnology
 */
class ChildClickableConstrainLayout : ConstraintLayout {

    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) :
            super(context!!, attrs, defStyle)

    // 子控件是否可以接受点击事件
    private var childClickable = true

    /**
     * 重写onInterceptTouchEvent（）
     */
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        // 返回true则拦截子控件所有点击事件，如果childclickable为true，则需返回false
        return !childClickable
    }

    /**
     * 然后就像正常LinearLayout一样使用这个控件就可以了。在需要的时候调用一下setChildClickable，
     * 参数为true则所有子控件可以点击，false则不可点击。
     *
     * @param clickable
     */
    fun setChildClickable(clickable: Boolean) {
        childClickable = clickable
    }

}