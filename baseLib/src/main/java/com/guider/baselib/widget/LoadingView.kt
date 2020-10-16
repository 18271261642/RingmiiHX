package com.guider.baselib.widget

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import com.guider.baselib.R

class LoadingView(context: Context, outSideCancelable: Boolean, loadingText: String,
                  var isShowTransparency: Boolean = true) : Dialog(context,
        if (isShowTransparency) R.style.my_dialog else R.style.my_dialog_no_transparency) {
    private var ivLoading: ImageView? = null

    @SuppressLint("InflateParams")
    private fun initView(context: Context, outSideCancelable: Boolean, loadingText: String) {
        setCanceledOnTouchOutside(outSideCancelable)
        val contentView = if (isShowTransparency) {
            LayoutInflater.from(context).inflate(
                    R.layout.view_loading, null, false)
        } else {
            LayoutInflater.from(context).inflate(
                    R.layout.view_custom_loading, null, false)
        }
        this.setContentView(contentView)
        ivLoading = findViewById(R.id.iv_loading)
        val anim = AnimationUtils.loadAnimation(context, R.anim.loading_anim)
        anim.interpolator = LinearInterpolator()
        anim.repeatCount = Animation.INFINITE
        ivLoading?.animation = anim
        ivLoading?.startAnimation(anim)
        val tvLoading = findViewById<TextView>(R.id.tv_loading)
        tvLoading.text = loadingText
    }

    override fun cancel() {
        super.cancel()
        if (ivLoading != null) ivLoading!!.clearAnimation()
    }

    init {
        initView(context, outSideCancelable, loadingText)
    }
}