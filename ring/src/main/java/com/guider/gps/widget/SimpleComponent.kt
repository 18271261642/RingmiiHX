package com.guider.gps.widget

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.binioter.guideview.Component
import com.guider.gps.R

/**
 * Created by binIoter on 16/6/17.
 */
class SimpleComponent : Component {

    @SuppressLint("InflateParams")
    override fun getView(inflater: LayoutInflater): View {
        return inflater.inflate(R.layout.layer_simple_component, null) as LinearLayout
    }

    override fun getAnchor(): Int {
        return Component.ANCHOR_BOTTOM
    }

    override fun getFitPosition(): Int {
        return Component.FIT_END
    }

    override fun getXOffset(): Int {
        return 0
    }

    override fun getYOffset(): Int {
        return 10
    }
}