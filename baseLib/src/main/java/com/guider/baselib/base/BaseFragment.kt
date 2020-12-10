package com.guider.baselib.base

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.guider.baselib.utils.EventBusUtils
import com.guider.baselib.utils.OnNoDoubleClickListener
import com.guider.feifeia3.utils.ToastUtil
import com.orhanobut.logger.Logger


/**
 * description: Fragmetn基类
 * autour: lisong
 * date: 2017/8/14 0014 下午 2:28
 * version:
 */
abstract class BaseFragment : Fragment(), OnNoDoubleClickListener {

    protected var rootView: View? = null

    protected var TAG = javaClass.simpleName

    /**
     * 获取布局文件
     */
    protected abstract val layoutRes: Int
    lateinit var mActivity: BaseActivity

    private var isLoaded = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        if (openEventBus()) {
            EventBusUtils.register(this)
        }
        if (rootView == null) {
            rootView = inflater.inflate(layoutRes, container, false)
        }
        initView(rootView!!)
        return rootView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as BaseActivity
    }

    override fun onResume() {
        super.onResume()
        if (openIsLazy()) {
            if (!isLoaded && !isHidden) {
                initLogic()
                Log.d(TAG, "lazyInit:!!!!!!!")
                isLoaded = true
            }
        }else {
            initLogic()
        }
    }

    /**
     * 是否开启eventBus
     */
    open fun openEventBus(): Boolean {
        return false
    }

    /**
     * 是否开启懒加载
     * @return 默认开启
     */
    open fun openIsLazy(): Boolean {
        return true
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
        isLoaded = false
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
