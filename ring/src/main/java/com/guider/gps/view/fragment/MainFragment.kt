package com.guider.gps.view.fragment

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import com.guider.baselib.base.BaseFragment
import com.guider.gps.R
import kotlinx.android.synthetic.main.fragment_nav_host.*

/**
 * @Package: com.guider.gps.view.fragment
 * @ClassName: NavHostFragment
 * @Description: 导航页的fragment
 * @Author: hjr
 * @CreateDate: 2020/12/18 11:38
 * Copyright (C), 1998-2020, GuiderTechnology
 */
class MainFragment : BaseFragment() {
    override val layoutRes: Int
        get() = R.layout.fragment_nav_host

    override fun initView(rootView: View) {

    }

    override fun initLogic() {
        mainPage.setOnClickListener {
            val bundle: Bundle = bundleOf(
                    "test" to "ha",
                    "num" to 9
            )
            Navigation.findNavController(requireView()).navigate(
                    R.id.action_navHostFragment_to_settingFragment, bundle)
        }
    }
}