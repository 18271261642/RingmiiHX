package com.guider.baselib.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.guider.baselib.utils.EventBusUtils
import com.guider.baselib.utils.OnNoDoubleClickListener
import com.guider.feifeia3.utils.ToastUtil
import com.orhanobut.logger.Logger
import com.trello.rxlifecycle3.components.support.RxFragment


/**
 * description: Fragmetn基类
 * autour: lisong
 * date: 2017/8/14 0014 下午 2:28
 * version:
 */
abstract class BaseFragment : RxFragment(), OnNoDoubleClickListener {

    protected var rootView: View? = null
    /**
     * 获取布局文件
     */
    protected abstract val layoutRes: Int
    lateinit var mActivity: BaseActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        if (openEventBus()) {
            EventBusUtils.register(this)
        }
        if (rootView == null) {
            rootView = inflater.inflate(layoutRes, container, false)
        }
        initView(rootView!!)
        initLogic()
        return rootView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as BaseActivity
    }

    /**
     * 是否开启eventBus
     */
    fun openEventBus(): Boolean{
        return false
    }

    /**
     * 初始化view
     */
    protected abstract fun initView(rootView: View)

    /**
     * 初始化逻辑
     */
    protected abstract fun initLogic()

    override fun onDestroyView() {
        if (openEventBus()) {
            EventBusUtils.unregister(this)
        }
        super.onDestroyView()
    }

    override fun onNoDoubleClick(v: View) {}

    fun showLog(msg: String) {
        Logger.d(msg)
    }

    fun showLog(msg: Any) {
        Logger.d(msg)
    }

    fun showToast(msg: String) {
        activity?.let { ToastUtil.show(it, msg) }
    }

    fun <T : ViewModel> getModel(modelClass: Class<T>): T {
        return ViewModelProvider(this).get(modelClass)
    }

}
