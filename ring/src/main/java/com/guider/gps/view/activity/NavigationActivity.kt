package com.guider.gps.view.activity

import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import com.guider.baselib.base.BaseActivity
import com.guider.gps.R

/**
 * @Package: com.guider.gps.view.activity
 * @ClassName: NavigationActivity
 * @Description: 导航页面
 * @Author: hjr
 * @CreateDate: 2020/12/18 11:33
 * Copyright (C), 1998-2020, GuiderTechnology
 */
class NavigationActivity : BaseActivity() {

    private var navHostFragment: NavHostFragment? = null

    override val contentViewResId: Int
        get() = R.layout.activity_navigation

    override fun initImmersion() {
        setTitle("导航页面")
        showBackButton(R.drawable.ic_back_white)
    }

    override fun initView() {
        val fragmentManager = supportFragmentManager
        navHostFragment = NavHostFragment()
        val beginTransaction = fragmentManager.beginTransaction()
        beginTransaction.replace(R.id.fragment, navHostFragment!!)
                .setPrimaryNavigationFragment(navHostFragment)// 等价于 xml 中的 app:defaultNavHost="true"
                .commitAllowingStateLoss()
    }

    override fun onResume() {
        super.onResume()
        navHostFragment?.let {
            val navSimple: NavGraph =
                    it.navController.navInflater.inflate(
                            R.navigation.mobile_navigation)
            navHostFragment?.navController?.graph = navSimple// 等价于 xml 中的 app:navGraph 的设置
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        navHostFragment = null
    }

    override fun initLogic() {

    }

    override fun showToolBar(): Boolean {
        return true
    }
}