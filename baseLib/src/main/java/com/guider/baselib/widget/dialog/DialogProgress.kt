package com.guider.baselib.widget.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.TextView
import com.guider.baselib.R

@SuppressLint("InflateParams")
class DialogProgress(mContext: Context, private val mIDialogProgress: IDialogProgress?,
                     isCanceledOnTouchOutside: Boolean = true) {
    private val dialog: Dialog
    private val tvPic: TextView
    private val tvText: TextView
    private var myAlphaAnimation: RotateAnimation? = null
    fun showDialog() {
        if (dialog.isShowing) return
        mIDialogProgress?.onStart()
        dialog.show()
        myAlphaAnimation!!.startNow()
    }

    fun hideDialog() {
        if (!dialog.isShowing) return
        mIDialogProgress?.onEnd()
        dialog.dismiss()
    }

    fun setTvText(text: String?) {
        tvText.text = text
    }

    // 初始化动画
    private fun initAnimation() {
        myAlphaAnimation = RotateAnimation(0f, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f) // 设置图片动画属性，各参数说明可参照api
        myAlphaAnimation!!.repeatCount = -1 // 设置旋转重复次数，即转几圈
        myAlphaAnimation!!.duration = 500 // 设置持续时间，注意这里是每一圈的持续时间，
        // 如果上面设置的圈数为3，持续时间设置1000，则图片一共旋转3秒钟
        myAlphaAnimation!!.interpolator = LinearInterpolator() // 设置动画匀速改变。
        // 相应的还有AccelerateInterpolator、DecelerateInterpolator、CycleInterpolator等
        tvPic.animation = myAlphaAnimation // 设置imageview的动画，
        // 也可以myImageView.startAnimation(myAlphaAnimation)
        myAlphaAnimation!!.setAnimationListener(object : Animation.AnimationListener {
            // 设置动画监听事件
            override fun onAnimationStart(arg0: Animation) {
                // TODO Auto-generated method stub
                Log.i(TAG, "onAnimationStart")
            }

            override fun onAnimationRepeat(arg0: Animation) {
                // Log.i(TAG, "onAnimationRepeat");
            }

            // 图片旋转结束后触发事件，这里启动新的activity
            override fun onAnimationEnd(arg0: Animation) {
                // TODO Auto-generated method stub
                Log.i(TAG, "onAnimationEnd")
            }
        })
    }

    interface IDialogProgress {
        fun onStart()
        fun onEnd()
    }

    companion object {
        private val TAG = DialogProgress::class.java.simpleName
    }

    init {
        val view = LayoutInflater.from(mContext).inflate(R.layout.dialog_progress, null)
        tvPic = view.findViewById(R.id.ecg_loading_pic)
        tvText = view.findViewById(R.id.ecg_loading_text)
        dialog = Dialog(mContext)
        dialog.setCanceledOnTouchOutside(isCanceledOnTouchOutside)
        dialog.setCancelable(false)
        // 去掉标题线
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(view)
        dialog.setCanceledOnTouchOutside(false)

        // 背景透明
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val window = dialog.window
        val lp = window!!.attributes
        lp.gravity = Gravity.CENTER // 居中位置
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        window.attributes = lp
        window.setWindowAnimations(R.style.mystyle) // 添加动画
        initAnimation()
    }
}