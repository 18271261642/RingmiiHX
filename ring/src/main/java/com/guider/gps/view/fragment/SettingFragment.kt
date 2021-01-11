package com.guider.gps.view.fragment

import android.view.View
import androidx.navigation.Navigation
import com.guider.baselib.base.BaseApplication
import com.guider.baselib.base.BaseFragment
import com.guider.feifeia3.utils.ToastUtil
import com.guider.gps.R
import kotlinx.android.synthetic.main.fragment_setting.*

/**
 * @Package: com.guider.gps.view.fragment
 * @ClassName: SettingFragment
 * @Description: 设置页面
 * @Author: hjr
 * @CreateDate: 2020/12/18 11:49
 * Copyright (C), 1998-2020, GuiderTechnology
 */
class SettingFragment : BaseFragment() {
    override val layoutRes: Int
        get() = R.layout.fragment_setting

    override fun initView(rootView: View) {
        val test: String = arguments?.getString("test") ?: ""
        val num: Int = arguments?.getInt("num") ?: 0
        ToastUtil.show(BaseApplication.guiderHealthContext, "接受的参数为${test} ---- $num")
    }

    override fun initLogic() {
        backButton.setOnClickListener {
            Navigation.findNavController(requireView()).navigateUp()
        }
    }
}